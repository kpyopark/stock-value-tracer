package robot.financialReport;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import common.StringUtil;
import dao.CompanyExDao;
import dao.CompanyFinancialRefinedStatusDao;
import dao.CompanyFinancialStatusDao;
import post.CompanyEx;
import post.CompanyFinancialStatus;
import post.KrxSecurityType;
import robot.DataUpdator;

public class FinancialReportRefiner extends DataUpdator {
	
	final static Logger logger = Logger.getLogger(FinancialReportRefiner.class);
			
	public static HashMap<String, CompanyFinancialStatus> makePeriodListFromAnnual(HashMap<String, CompanyFinancialStatus> matchedAnnualStatus) {
		HashMap<String, CompanyFinancialStatus> rtn = new HashMap<String, CompanyFinancialStatus>();
		ArrayList<String> periods = new ArrayList<String>(matchedAnnualStatus.keySet());
		Collections.sort(periods);
		Collections.reverse(periods);
		String baseDateToFuture = StringUtil.getLastDayOfQuarter(StringUtil.convertToStandardDate(new java.util.Date()), 4);
		for(String standardDate: periods) {
			CompanyFinancialStatus cfs = matchedAnnualStatus.get(standardDate);
			//if ( "20120101".compareTo(standardDate) < 0 && baseDateToFuture.compareTo(standardDate) > 0) {
			if ( "20000101".compareTo(standardDate) < 0 ) {
				String prevQuarter = standardDate;
				if( baseDateToFuture.compareTo(prevQuarter) > 0) rtn.put(prevQuarter, cfs);
				prevQuarter = StringUtil.getLastDayOfQuarter(prevQuarter, -3);
				if( baseDateToFuture.compareTo(prevQuarter) > 0) rtn.put(prevQuarter, cfs);
				prevQuarter = StringUtil.getLastDayOfQuarter(prevQuarter, -3);
				if( baseDateToFuture.compareTo(prevQuarter) > 0) rtn.put(prevQuarter, cfs);
				prevQuarter = StringUtil.getLastDayOfQuarter(prevQuarter, -3);
				if( baseDateToFuture.compareTo(prevQuarter) > 0) rtn.put(prevQuarter, cfs);
			}
		}
		return rtn;
	}

	public static List<CompanyFinancialStatus> retrieveValidQuarterReports(CompanyEx company,List<CompanyFinancialStatus> cfsList) {
		HashMap<String, CompanyFinancialStatus> matchedQuarterReports = new HashMap<String, CompanyFinancialStatus>();
		HashMap<String, CompanyFinancialStatus> matchedAnnualStatus = new HashMap<String, CompanyFinancialStatus>();
		List<CompanyFinancialStatus> validQuarterReports = new ArrayList<CompanyFinancialStatus>();
		//
		// We are going to separate financial statements into two parts - one is annual reports, the other is quarter report.
		// 
		// There is no standard to set base date of financial reports.
		// so. we resolve this problem with base date of annual reports.
		//
		// At first. we extract the annual report from all lists of financial reports.
		// and, on the base of these base date, the quarters date could be calculated.
		//
		// Next, check where there is VALID quarter report on the calculated base date.
		//
		// If no, the quarter report will be calculated from annual report or other quarter reports.
		//
		// ---------- The above Algorithm should be modified --------
		// 
		// because of new feature that we calculates annual reports from quarter report at some points.
		//
		// So. we will add a new annual period.
		// 
		// for example, we want to calculate annual report at the time '2013-04-23'.
		//
		// we would retrieve the financial reports which registered before '2013-04-23'.
		//
		// There must be a annual report '2012-12-31' in the retrieved list.
		//
		// So, we would add a next annual report '2013-12-31' in the list.
		// 
		// and recalculate it.
		logger.debug(String.format("COMPANY:[%s]:",company.getName()));
		
		String latestAnnualReportStandardDate = null;
		
		for(CompanyFinancialStatus cfs : cfsList) {
			if ( cfs.isQuarter() ) {
				matchedQuarterReports.put(cfs.getStandardDate(), cfs);
			} else {
				if ( latestAnnualReportStandardDate == null )
					latestAnnualReportStandardDate = cfs.getStandardDate();
				matchedAnnualStatus.put(cfs.getStandardDate(),cfs);
			}
		}
		
		if ( latestAnnualReportStandardDate != null ) {
			// If there is no annual financial statement in this year. this routine put the this year to the standard date list.
			String previousYearStandardDate = StringUtil.getPreviousAnnualStandardDate(latestAnnualReportStandardDate);
			if(!matchedAnnualStatus.containsKey(previousYearStandardDate))
				matchedAnnualStatus.put(previousYearStandardDate, null);
			matchedAnnualStatus.put(StringUtil.getNextAnnualStandardDate(latestAnnualReportStandardDate),null);
		}
		HashMap<String, CompanyFinancialStatus> validListFromAnnual = makePeriodListFromAnnual(matchedAnnualStatus);

		ArrayList<String> datelistFromAnnualReports = new ArrayList<String>(validListFromAnnual.keySet());
		Collections.sort(datelistFromAnnualReports);

		logger.debug(String.format("ANNUAL REPORT LIST:[%s]:",datelistFromAnnualReports));
		
		for (String validDate: datelistFromAnnualReports ) {
			CompanyFinancialStatus cfs = null;
			if ( isValidReport( cfs = matchedQuarterReports.get(validDate) ) ) {
				validQuarterReports.add(cfs);
			} else {
				if ( cfs != null && !cfs.isFixed() ) {
					// Don't use estimated report.
					continue;
				}
				if ( cfs == null ) {
					cfs = new CompanyFinancialStatus();
					cfs.setCompany(company);
					cfs.setStandardDate(validDate);
					cfs.setQuarter(true);
				}
				CompanyFinancialStatus annualCfs = validListFromAnnual.get(validDate);
				if ( annualCfs == null ) {
					logger.warn(String.format("[%s][%s] Matched annual report[%s] missing.", company.getId(), company.getName(), validDate));
					continue;
				}
				if ( ! hasAssetReport(cfs) ) {
					if ( hasAssetReport(annualCfs)) {
						// System.out.println("	--> asset info: derived report can be calculated.");
						calculateAssetFromOtherReport(cfs, annualCfs);
					}
					else if ( hasAssetReport(matchedQuarterReports.get(StringUtil.getLastDayOfQuarter(validDate, -3))) ) {
						//System.out.println("	--> asset info: previous report can be referenced.");
						calculateAssetFromOtherReport(cfs, matchedQuarterReports.get(StringUtil.getLastDayOfQuarter(validDate, -3)));
					} else { 
						logger.warn(String.format("[%s][%s] has no valid ASSET report for [%s].", company.getId(), company.getName(), validDate));
					}
				}
				if ( ! hasSalesReport(cfs) ) {
					if ( hasSalesReport(annualCfs) ) {
						//System.out.println("	--> sales info: derived report can be calculated.");
						calculateQuarterSalesFromAnnualReport(cfs, annualCfs);
					} else {
						logger.warn(String.format("[%s][%s] has no valid SALES report for [%s].", company.getId(), company.getName(), validDate));
					}
				}
				if ( cfs.getRegisteredDate() == null || cfs.getRegisteredDate().length() != 8 ) {
					cfs.setRegisteredDate(StringUtil.addDate(cfs.getStandardDate(),50));
				}
				if ( isValidReport( cfs ) ) {
					validQuarterReports.add(cfs);
				} else {
					if ( hasAssetReport(cfs) ) {
						validQuarterReports.add(cfs);
					} else {
						logger.warn(String.format("[%s][%s]-Report [%s] has excluded.", company.getId(), company.getName(), validDate));
					}
				}
			}
		}
		return validQuarterReports;
	}
	
	public static boolean isValidReport(CompanyFinancialStatus cfs) {
		return cfs != null && hasAssetReport(cfs) && hasSalesReport(cfs);
	}
	
	public static boolean hasAssetReport(CompanyFinancialStatus cfs) {
		return cfs != null && cfs.getAssets() != 0 && cfs.getCapital() != 0 && cfs.getDebt() != 0 && cfs.getGrossCapital() != 0;
	}
	
	public static boolean hasSalesReport(CompanyFinancialStatus cfs) {
		return cfs != null && cfs.getSales() != 0 && cfs.getOperatingProfit() != 0 && cfs.getNetProfit() != 0;
	}
	
	public static void calculateQuarterSalesFromAnnualReport(CompanyFinancialStatus quarter, CompanyFinancialStatus annual) {
		quarter.setCalculated(true);
		quarter.setNetProfit(annual.getNetProfit()/4);
		quarter.setOperatingProfit(annual.getOperatingProfit()/4);
		quarter.setOrdinaryProfit(annual.getOrdinaryProfit()/4);
		quarter.setSales(annual.getSales()/4);
	}
	
	public static void calculateAssetFromOtherReport(CompanyFinancialStatus quarter, CompanyFinancialStatus annual) {
		quarter.setCalculated(true);
		quarter.setAssets(annual.getAssets());
		quarter.setCapital(annual.getCapital());
		quarter.setDebt(annual.getDebt());
		quarter.setGrossCapital(annual.getGrossCapital());
		quarter.setInvestedCapital(annual.getInvestedCapital());
		quarter.setOrdinarySharesSize(annual.getOrdinarySharesSize());
		quarter.setPrefferedSharesSize(annual.getPrefferedSharesSize());
		quarter.setFixed(true);
		quarter.setKOSPI(annual.isKOSPI());
	}
	
	public static void main(String[] args) throws SQLException {
		//testCheckAllCompanyFinancialStatement();
		testCheckCfs();
	}
	
	public static void testCheckAllCompanyFinancialStatement() throws SQLException {
		ArrayList<CompanyEx> companyList = null;
		CompanyExDao dao = new CompanyExDao();
		CompanyFinancialStatusDao financialDao = new CompanyFinancialStatusDao();
		CompanyFinancialRefinedStatusDao cfsRefinedDao = new CompanyFinancialRefinedStatusDao();
		String registeredDate = StringUtil.convertToStandardDate(new java.util.Date());
		companyList = dao.selectAllList(registeredDate, KrxSecurityType.STOCK);
		for ( CompanyEx company : companyList ) {
			if ( company.getSecuritySector() == KrxSecurityType.STOCK.getType() ) {
				ArrayList<CompanyFinancialStatus> financialStatusList = null;
				financialStatusList = financialDao.getFinancialStatus(company, registeredDate);
				List<CompanyFinancialStatus> cfsList = retrieveValidQuarterReports(company, financialStatusList);
				for ( CompanyFinancialStatus cfs : cfsList) {
					CompanyFinancialStatus oldcfs = cfsRefinedDao.select(company, cfs.getStandardDate(), cfs.isQuarter());
					if ( oldcfs != null ) {
						cfsRefinedDao.delete(oldcfs);
						cfsRefinedDao.insert(cfs);
					} else {
						cfsRefinedDao.insert(cfs);
					}
				}
			}
		}
	}
	
	public static void testCheckCfs() throws SQLException {
		CompanyEx company = new CompanyEx();
		CompanyFinancialStatusDao financialDao = new CompanyFinancialStatusDao();
		String registeredDate = StringUtil.convertToStandardDate(new java.util.Date());
		company.setId("A032280");
		ArrayList<CompanyFinancialStatus> financialStatusList = null;
		financialStatusList = financialDao.getFinancialStatus(company, registeredDate);
		List<CompanyFinancialStatus> cfsList = retrieveValidQuarterReports(company, financialStatusList);
		for( CompanyFinancialStatus cfs : cfsList ) {
			System.out.println( cfs );
		}
	}
	
}
