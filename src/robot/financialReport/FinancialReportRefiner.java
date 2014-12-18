package robot.financialReport;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import post.CompanyEx;
import post.CompanyFinancialStatus;
import robot.DataUpdator;
import common.PeriodUtil;
import common.StringUtil;
import dao.CompanyExDao;
import dao.CompanyFinancialRefinedStatusDao;
import dao.CompanyFinancialStatusDao;

public class FinancialReportRefiner extends DataUpdator {

	public static HashMap<String, CompanyFinancialStatus> makePeriodListFromAnnual(HashMap<String, CompanyFinancialStatus> matchedAnnualStatus) {
		HashMap<String, CompanyFinancialStatus> rtn = new HashMap<String, CompanyFinancialStatus>();
		ArrayList<String> periods = new ArrayList<String>(matchedAnnualStatus.keySet());
		Collections.sort(periods);
		Collections.reverse(periods);
		String baseDateToFuture = StringUtil.getLastDayOfQuarter(StringUtil.convertToStandardDate(new java.util.Date()), 3);
		for(String standardDate: periods) {
			CompanyFinancialStatus cfs = matchedAnnualStatus.get(standardDate);
			if ( "20120101".compareTo(standardDate) < 0 && baseDateToFuture.compareTo(standardDate) > 0) {
				String prevQuarter = standardDate;
				rtn.put(prevQuarter, cfs);
				prevQuarter = StringUtil.getLastDayOfQuarter(prevQuarter, -3);
				rtn.put(prevQuarter, cfs);
				prevQuarter = StringUtil.getLastDayOfQuarter(prevQuarter, -3);
				rtn.put(prevQuarter, cfs);
				prevQuarter = StringUtil.getLastDayOfQuarter(prevQuarter, -3);
				rtn.put(prevQuarter, cfs);
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
		for(CompanyFinancialStatus cfs : cfsList) {
			if ( cfs.isQuarter() ) {
				matchedQuarterReports.put(cfs.getStandardDate(), cfs);
			} else {
				matchedAnnualStatus.put(cfs.getStandardDate(),cfs);
			}
		}
		HashMap<String, CompanyFinancialStatus> validListFromAnnual = makePeriodListFromAnnual(matchedAnnualStatus);

		ArrayList<String> datelistFromAnnualReports = new ArrayList<String>(validListFromAnnual.keySet());
		Collections.sort(datelistFromAnnualReports);
		
		for (String validDate: datelistFromAnnualReports ) {
			CompanyFinancialStatus cfs = null;
			if ( isValidReport( cfs = matchedQuarterReports.get(validDate) ) ) {
				validQuarterReports.add(cfs);
			} else {
				if ( cfs != null && !cfs.isFixed() )
					continue;
				if ( cfs == null ) {
					cfs = new CompanyFinancialStatus();
					cfs.setCompany(company);
					cfs.setStandardDate(validDate);
					cfs.setQuarter(true);
				}
				CompanyFinancialStatus annualCfs = validListFromAnnual.get(validDate);
				if ( annualCfs == null )
					System.out.println("	--> there is no annual reports");
				if ( ! hasAssetReport(cfs) ) {
					if ( hasAssetReport(annualCfs)) {
						// System.out.println("	--> asset info: derived report can be calculated.");
						calculateAssetFromOtherReport(cfs, annualCfs);
					}
					else if ( hasAssetReport(matchedQuarterReports.get(StringUtil.getLastDayOfQuarter(validDate, -3))) ) {
						//System.out.println("	--> asset info: previous report can be referenced.");
						calculateAssetFromOtherReport(cfs, matchedQuarterReports.get(StringUtil.getLastDayOfQuarter(validDate, -3)));
					} else { 
						System.out.println("Stock info:" + cfs.getCompany().getId() + ":" + cfs.getStandardDate() + ":" + cfs.getCompany().getName());
						System.out.println("	--> asset info: THERE IS NO WAY TO SOLVE.");
					}
				}
				if ( ! hasSalesReport(cfs) ) {
					if ( hasSalesReport(annualCfs) ) {
						//System.out.println("	--> sales info: derived report can be calculated.");
						calculateQuarterSalesFromAnnualReport(cfs, annualCfs);
					} else {
						System.out.println("Stock info:" + cfs.getCompany().getId() + ":" + cfs.getStandardDate() + ":" + cfs.getCompany().getName());
						System.out.println("	--> sales info: THERE IS NO WAY TO SOLVE. BUT THIS REPORT WILL BE USED.");
						calculateQuarterSalesFromAnnualReport(cfs, annualCfs);
					}
				}
				if ( isValidReport( cfs ) ) {
					validQuarterReports.add(cfs);
				} else {
					System.out.println("Stock info:" + cfs.getCompany().getId() + ":" + validDate + ":" + cfs.getCompany().getName());
					if ( hasAssetReport(cfs) ) {
						validQuarterReports.add(cfs);
					} else {
						System.out.println("	--> EXCLUDED." );
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
		testCheckAllCompanyFinancialStatement();
		//testCheckCfs();
	}
	
	public static void testCheckAllCompanyFinancialStatement() throws SQLException {
		ArrayList<CompanyEx> companyList = null;
		CompanyExDao dao = new CompanyExDao();
		CompanyFinancialStatusDao financialDao = new CompanyFinancialStatusDao();
		CompanyFinancialRefinedStatusDao cfsRefinedDao = new CompanyFinancialRefinedStatusDao();
		String registeredDate = StringUtil.convertToStandardDate(new java.util.Date());
		companyList = dao.selectAllList(registeredDate);
		for ( CompanyEx company : companyList ) {
			System.out.println("company");
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
	
	public static void testCheckCfs() throws SQLException {
		CompanyEx company = new CompanyEx();
		CompanyFinancialStatusDao financialDao = new CompanyFinancialStatusDao();
		String registeredDate = StringUtil.convertToStandardDate(new java.util.Date());
		company.setId("A003350");
		ArrayList<CompanyFinancialStatus> financialStatusList = null;
		financialStatusList = financialDao.getFinancialStatus(company, registeredDate);
		List<CompanyFinancialStatus> cfsList = retrieveValidQuarterReports(company, financialStatusList);
		for( CompanyFinancialStatus cfs : cfsList ) {
			System.out.println( cfs );
		}
	}
	
}