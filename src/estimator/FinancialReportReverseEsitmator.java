package estimator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import common.StringUtil;
import post.Company;
import post.CompanyEx;
import post.CompanyFinancialStatus;
import post.KrxSecurityType;
import dao.CompanyDao;
import dao.CompanyExDao;
import dao.CompanyFinancialStatusDao;

public class FinancialReportReverseEsitmator {

	ArrayList<CompanyEx> companyList = null;
	CompanyFinancialStatusDao orgDao = null;
	CompanyExDao companyExDao = null;
	
	public FinancialReportReverseEsitmator() {
		init();
	}
	
	private void init() {
		companyExDao = new CompanyExDao();
		orgDao = new CompanyFinancialStatusDao();
		try {
			companyList = companyExDao.selectAllList(StringUtil.convertToStandardDate(new java.util.Date()), KrxSecurityType.STOCK);
		} catch ( Exception e ) {
			e.printStackTrace();
			companyList = new ArrayList<CompanyEx>();
		}
	}
	
	static String FIRST_QUARTER = "0331";
	static String SECOND_QUARTER = "0630";
	static String THIRD_QUARTER = "0930";
	static String FORTH_QUARTER = "1231";
	
	static String[] STANDARD_QUARTER_LIST = {FIRST_QUARTER,SECOND_QUARTER,THIRD_QUARTER,FORTH_QUARTER};
	
	private static boolean isValidReportForProfit(CompanyFinancialStatus cfs) {
		return ( cfs.getSales() != 0 ) && ( cfs.getOperatingProfit() != 0 ) && ( cfs.getNetProfit() != 0 );
	}
	
	/**
	 * If asset, debt, capital info is 0, this cell would be filled with nearest one of report's value.
	 * 
	 * @param company
	 * @return
	 */
	public void fillBlankAssetInfos(Company company, String registeredDate) {
		ArrayList<CompanyFinancialStatus> cfs = null;
		CompanyFinancialStatus prevcfs = null;
		try {
			cfs = orgDao.getFinancialStatus(company, registeredDate);
			for( int cnt = 0 ; cnt < cfs.size() ; cnt++ ) {
				CompanyFinancialStatus target = cfs.get(cnt);
				if ( target.getAssets() == 0 || target.getDebt() == 0 || target.getCapital() == 0 || target.getGrossCapital() == 0 ) {
					target.setAssets(prevcfs.getAssets());
					target.setDebt(prevcfs.getDebt());
					target.setCapital(prevcfs.getCapital());
					target.setGrossCapital(prevcfs.getGrossCapital());
					target.setCalculated(true);
					orgDao.delete(target);
					orgDao.insert(target);
					System.out.println("updated  asset:" + target.getCompany().getId() + ":" + target.getCompany().getName() + ":" + target.getStandardDate() + ":" + target.isQuarter() );
				} else {
					prevcfs = target;
				}
				if ( target.getOperatingProfit() == 0 || target.getNetProfit() == 0 || target.getSales() == 0 ) {
					if ( target.getOperatingProfit() == 0 && target.getNetProfit() == 0 && target.getSales() == 0 ) {
						// There is no clue to calculate missing parameters.
						if ( target.isQuarter() ) {
							boolean isUpdated = false;
							for( int pos = 0 ; pos < cfs.size() ; pos++ ) {
								// If there is a annual report which was published in same year
								if ( target.getStandardDate().substring(0, 4).equals(cfs.get(pos).getStandardDate().substring(0,4)) && !cfs.get(pos).isQuarter() ) {
									//
									target.setSales(cfs.get(pos).getSales() / 4);
									target.setOperatingProfit(cfs.get(pos).getOperatingProfit() / 4);
									target.setNetProfit(cfs.get(pos).getNetProfit() / 4);
									target.setCalculated(true);
									orgDao.delete(target);
									orgDao.insert(target);
									isUpdated = true;
									System.out.println("updated sales, operating, net profit:" + target.getCompany().getId() + ":" + target.getCompany().getName() + ":" + target.getStandardDate() + ":" + target.isQuarter() );
									break;
								}
							}
							if ( isUpdated )
								System.out.println("updated sales, operating, net profit failed.:" + target.getCompany().getId() + ":" + target.getCompany().getName() + ":" + target.getStandardDate() + ":" + target.isQuarter() );
						} else {
							boolean isUpdated = false;
							CompanyFinancialStatus prevYear = null;
							CompanyFinancialStatus nextYear = null;
							for( int pos = 0 ; pos < cfs.size() ; pos++ ) {
								// If there is a annual report which was published in same year
								if ( ("" + (Integer.parseInt(target.getStandardDate().substring(0, 4))-1)).equals(cfs.get(pos).getStandardDate().substring(0,4)) && !cfs.get(pos).isQuarter() &&
										isValidReportForProfit(cfs.get(pos))) {
									prevYear = cfs.get(pos);
								}
								if ( ("" + (Integer.parseInt(target.getStandardDate().substring(0, 4))+1)).equals(cfs.get(pos).getStandardDate().substring(0,4)) && !cfs.get(pos).isQuarter() &&
										isValidReportForProfit(cfs.get(pos))) {
									nextYear = cfs.get(pos);
								}
							}
							if( prevYear != null && nextYear != null) {
								target.setSales((prevYear.getSales() + nextYear.getSales())/2);
								target.setOperatingProfit((prevYear.getOperatingProfit() + nextYear.getOperatingProfit()) / 2);
								target.setNetProfit((prevYear.getNetProfit()+nextYear.getNetProfit()) / 2);
								target.setCalculated(true);
								isUpdated = true;
								orgDao.delete(target);
								orgDao.insert(target);
								System.out.println("updated sales, operating, net profit:" + target.getCompany().getId() + ":" + target.getCompany().getName() + ":" + target.getStandardDate() + ":" + target.isQuarter() );
							}
							if ( isUpdated )
								System.out.println("updated sales, operating, net profit failed.:" + target.getCompany().getId() + ":" + target.getCompany().getName() + ":" + target.getStandardDate() + ":" + target.isQuarter() );
						}
					} else if ( target.getSales() == 0 && target.getOperatingProfit() == 0 ) {
						// There is only netProfit.
						target.setSales((long)((double)target.getNetProfit() / 0.0519));
						target.setOperatingProfit((long)((double)target.getNetProfit() / 0.7607));
						target.setCalculated(true);
						orgDao.delete(target);
						orgDao.insert(target);
						System.out.println("updated sales, operating profit:" + target.getCompany().getId() + ":" + target.getCompany().getName() + ":" + target.getStandardDate() + ":" + target.isQuarter() );
					} else if ( target.getSales() == 0 && target.getNetProfit() == 0 ) {
						// There is only operating profit.
						target.setSales((long)((double)target.getOperatingProfit() / 0.0682));
						target.setNetProfit((long)((double)target.getOperatingProfit() * 0.7607));
						target.setCalculated(true);
						orgDao.delete(target);
						orgDao.insert(target);
						System.out.println("updated sales, net profit:" + target.getCompany().getId() + ":" + target.getCompany().getName() + ":" + target.getStandardDate() + ":" + target.isQuarter() );
					} else if ( target.getOperatingProfit() == 0 && target.getNetProfit() == 0 ) {
						// There is only sales
						target.setOperatingProfit((long)((double)target.getSales() * 0.0682));
						target.setNetProfit((long)((double)target.getSales() * 0.0519));
						target.setCalculated(true);
						orgDao.delete(target);
						orgDao.insert(target);
						System.out.println("updated sales, net profit:" + target.getCompany().getId() + ":" + target.getCompany().getName() + ":" + target.getStandardDate() + ":" + target.isQuarter() );
					} else if ( target.getSales() == 0 ) {
						target.setOperatingProfit((long)((double)target.getSales() * 0.0682));
						target.setCalculated(true);
						orgDao.delete(target);
						orgDao.insert(target);
						System.out.println("updated sales:" + target.getCompany().getId() + ":" + target.getCompany().getName() + ":" + target.getStandardDate() + ":" + target.isQuarter() );
					} else if ( target.getOperatingProfit() == 0 ) {
						target.setOperatingProfit((long)((double)target.getSales() * 0.0682));
						target.setCalculated(true);
						orgDao.delete(target);
						orgDao.insert(target);
						System.out.println("updated operating profit:" + target.getCompany().getId() + ":" + target.getCompany().getName() + ":" + target.getStandardDate() + ":" + target.isQuarter() );
					} else if ( target.getNetProfit() == 0 ) {
						target.setNetProfit((long)((double)target.getOperatingProfit() * 0.7607));
						target.setCalculated(true);
						orgDao.delete(target);
						orgDao.insert(target);
						System.out.println("updated net profit:" + target.getCompany().getId() + ":" + target.getCompany().getName() + ":" + target.getStandardDate() + ":" + target.isQuarter() );
					}
				}
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	private static String[] PERIOD_01 = { "0131", "0430", "0731", "1031" };
	private static String[] PERIOD_02 = { "0228", "0531", "0831", "1130" };
	private static String[] PERIOD_03 = { "0331", "0630", "0930", "1231" };
	
	private static int PERIOD_TYPE(String standardDate) {
		int pos = 0;
		String monthday = standardDate.substring(4,8);
		for( pos = 0 ; pos < PERIOD_01.length ; pos++ ) {
			if ( PERIOD_01[pos].equals(monthday) ) {
				return 0;
			}
		}
		for( pos = 0 ; pos < PERIOD_02.length ; pos++ ) {
			if ( PERIOD_02[pos].equals(monthday) ) {
				return 1;
			}
		}
		for( pos = 0 ; pos < PERIOD_03.length ; pos++ ) {
			if ( PERIOD_03[pos].equals(monthday) ) {
				return 2;
			}
		}
		return 2;
	}
	
	private static String[] getPeriodList(int mode) {
		switch(mode) {
		case 0: 
			return PERIOD_01;
		case 1:
			return PERIOD_02;
		case 2:
		default:
			return PERIOD_03;
		}
	}
	
	private static int getQuarterPosition(String[] periods, String standardDate) {
		String monthday = standardDate.substring(4,8);
		return periods[0].equals(monthday) ? 0 :
			periods[1].equals(monthday) ? 1 :
				periods[2].equals(monthday) ? 2 : 3;
	}

	private static int getYear(String standardDate) {
		return Integer.parseInt(standardDate.substring(0,4));
	}
	
	private static int getMonth(String standardDate) {
		return Integer.parseInt(standardDate.substring(4,6));
	}
	
	private static int getMonthDay(String standardDate) {
		return Integer.parseInt(standardDate.substring(4,8));
	}
	
	private static String getNextYearString(String standardDate) {
		return ( getYear(standardDate) + 1 ) + standardDate.substring(4,8);
	}
	
	private static String getPrevQuarter(String standardDate) {
		String[] periods = getPeriodList(PERIOD_TYPE(standardDate));
		int pos = getQuarterPosition(periods, standardDate);
		if ( pos == 0 ) {
			int prevYear = getYear(standardDate) - 1;
			String prevQuarter = prevYear + periods[3];
			return prevQuarter;
		} else {
			return getYear(standardDate) + periods[pos - 1];
		}
	}
	
	private static String getNextQuarter(String standardDate) {
		String[] periods = getPeriodList(PERIOD_TYPE(standardDate));
		int pos = getQuarterPosition(periods, standardDate);
		if ( pos == 3 ) {
			int nextYear = getYear(standardDate) + 1;
			String nextQuarter = nextYear + periods[0];
			return nextQuarter;
		} else {
			return getYear(standardDate) + periods[pos + 1];
		}
	}
	
	public void fillYearReport(Company company, String registeredDate) {
		ArrayList<CompanyFinancialStatus> cfs = null;
		CompanyFinancialStatus prevYear = null;
		try {
			cfs = orgDao.getFinancialStatus(company, registeredDate);
			for( int cnt = 0 ; cnt < cfs.size() ; cnt++ ) {
				CompanyFinancialStatus target = cfs.get(cnt);
				if ( !target.isQuarter() ) {
					if(prevYear != null && ( getYear(prevYear.getStandardDate()) + 2) == getYear(target.getStandardDate()) ) {
						target.setStandardDate( getNextYearString(prevYear.getStandardDate()));
						target.setAssets((prevYear.getAssets()+target.getAssets())/2);
						target.setDebt((prevYear.getDebt()+target.getDebt())/2);
						target.setCapital((prevYear.getCapital()+target.getCapital())/2);
						target.setGrossCapital((prevYear.getGrossCapital()+target.getGrossCapital())/2);
						target.setSales((prevYear.getSales()+target.getSales())/2);
						target.setOperatingProfit((prevYear.getOperatingProfit()+target.getOperatingProfit())/2);
						target.setNetProfit((prevYear.getNetProfit()+target.getNetProfit())/2);
						target.setOrdinarySharesSize((prevYear.getOrdinarySharesSize()+target.getOrdinarySharesSize())/2);
						target.setDividendRatio((prevYear.getDividendRatio()+target.getDividendRatio())/2);
						target.setRoe((prevYear.getRoe()+target.getRoe())/2);
						target.setRoa((prevYear.getRoa()+target.getRoa())/2);
						target.setRoi((prevYear.getRoi()+target.getRoi())/2);
						target.setCalculated(true);
						System.out.println("insert new report:" + target.getCompany().getId() + ":" + target.getCompany().getName() + ":" + target.getStandardDate() + ":" + target.isQuarter() );
						orgDao.insert(target);
					}
					prevYear = target;
				}
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	static class AnnualQuarterRelatedCfs {
		public AnnualQuarterRelatedCfs(CompanyFinancialStatus quarter, CompanyFinancialStatus annual, int numberOfQuarters) {
			this.quarter = quarter;
			this.annual = annual;
			this.numberOfQuarters = numberOfQuarters;
		}
		CompanyFinancialStatus quarter = null;
		CompanyFinancialStatus annual = null;
		int numberOfQuarters = 4;
	}
	
	static boolean quarterReportAlreadyExist(ArrayList<CompanyFinancialStatus> quartercfs, int checkPosition, String standardDate ) {
		for(int cnt = checkPosition ; cnt < quartercfs.size() ; cnt++ ) {
			if ( quartercfs.get(cnt).getStandardDate().equals(standardDate) )
				return true;
		}
		return false;
	}

	public void fillQuaterReport(Company company, String registeredDate) {
		ArrayList<CompanyFinancialStatus> cfs = null;
		ArrayList<CompanyFinancialStatus> annualcfs = new ArrayList<CompanyFinancialStatus>();
		ArrayList<CompanyFinancialStatus> quartercfs = new ArrayList<CompanyFinancialStatus>();
		ArrayList<AnnualQuarterRelatedCfs> idealquartercfs = new ArrayList<AnnualQuarterRelatedCfs>();
		try {
			cfs = orgDao.getFinancialStatus(company, registeredDate);
			if ( cfs.size() == 0 )
				return;
			// 1. Select annual report 
			// 2. Calculate quarter periods.
			// 3. Check missing periods
			CompanyFinancialStatus prevAnnualReport = null;
			for( int cnt = 0 ; cnt < cfs.size() ; cnt++ ) {
				if ( cfs.get(cnt).isQuarter() ) {
					// Quarter reports.
					quartercfs.add(cfs.get(cnt));
				} else {
					annualcfs.add(cfs.get(cnt));
					if ( prevAnnualReport == null || 
							( getMonthDay(cfs.get(cnt).getStandardDate()) == getMonthDay(prevAnnualReport.getStandardDate()) ) ) {
						// Assume that current annual report consists of 4 quarter reports.
						CompanyFinancialStatus curquarter = new CompanyFinancialStatus();
						curquarter.setStandardDate(cfs.get(cnt).getStandardDate());
						CompanyFinancialStatus prev1quarter = new CompanyFinancialStatus();
						prev1quarter.setStandardDate(getPrevQuarter(curquarter.getStandardDate()));
						CompanyFinancialStatus prev2quarter = new CompanyFinancialStatus();
						prev2quarter.setStandardDate(getPrevQuarter(prev1quarter.getStandardDate()));
						CompanyFinancialStatus prev3quarter = new CompanyFinancialStatus();
						prev3quarter.setStandardDate(getPrevQuarter(prev2quarter.getStandardDate()));
						idealquartercfs.add(new AnnualQuarterRelatedCfs(prev3quarter, cfs.get(cnt), 4));
						idealquartercfs.add(new AnnualQuarterRelatedCfs(prev2quarter, cfs.get(cnt), 4));
						idealquartercfs.add(new AnnualQuarterRelatedCfs(prev1quarter, cfs.get(cnt), 4));
						idealquartercfs.add(new AnnualQuarterRelatedCfs(curquarter, cfs.get(cnt), 4));
					} else {
						// Example) When to change the end of annual report.
						// 200403 - 200306, 200309, 200312, 200403 : 200403 annual reports contains 4 quarter data.
						// 200412 - 200406, 200409, 200412         : 200412 annual reports contains only 3 quarter data.
						// The important factor which choose the number of quarter reports is the gap between previous and current annual end day.
						// This can be calculated by ( 2004 * 12 + 12 ) - ( 2004 * 12 + 3 ) = 9
						// 9 / 3 = 3 quarters
						int numberOfQuarters = (int)((getYear(cfs.get(cnt).getStandardDate()) * 12 + getMonth(cfs.get(cnt).getStandardDate()))
								-(getYear(prevAnnualReport.getStandardDate()) * 12 + getMonth(prevAnnualReport.getStandardDate()))) / 3;
						CompanyFinancialStatus curQuarter = new CompanyFinancialStatus();
						curQuarter.setStandardDate(prevAnnualReport.getStandardDate());
						CompanyFinancialStatus nextQuarter = null;
						//System.out.println("Cut due date changed. numberOfQuarters[" + numberOfQuarters + "] prev due date[" + prevAnnualReport.getStandardDate() + "] current due date[" + cfs.get(cnt).getStandardDate() + "]");
						for( int position = 0 ; position < numberOfQuarters ; position++ ) {
							nextQuarter = new CompanyFinancialStatus();
							nextQuarter.setStandardDate(getNextQuarter(curQuarter.getStandardDate()));
							idealquartercfs.add(new AnnualQuarterRelatedCfs(nextQuarter, cfs.get(cnt), numberOfQuarters));
							//System.out.println("standard date[" + nextQuarter.getStandardDate() + "]");
							curQuarter = nextQuarter;
						}
					}
					prevAnnualReport = cfs.get(cnt);
				}
			}
			// If ideal quarter reports exist.. (it means there are annual reports in the financial report list.
			int checkPosition = 0;
			if ( idealquartercfs.size() > 0 ) {
				for( int idealPosition = 0 ; idealPosition < idealquartercfs.size() ; idealPosition++ ) {
					if ( quarterReportAlreadyExist(quartercfs, checkPosition, idealquartercfs.get(idealPosition).quarter.getStandardDate() ) ) {
						checkPosition++;
					} else {
						// There is no original quarter report. 
						// You should calculate quarter report assumed.
						CompanyFinancialStatus _annualcfs = idealquartercfs.get(idealPosition).annual;
						CompanyFinancialStatus _quartercfs = idealquartercfs.get(idealPosition).quarter;
						int numberOfQuarters = idealquartercfs.get(idealPosition).numberOfQuarters;
						_quartercfs.setCompany(company);
						_quartercfs.setCalculated(true);
						_quartercfs.setAssets(_annualcfs.getAssets());
						_quartercfs.setCapital(_annualcfs.getCapital());
						_quartercfs.setDebt(_annualcfs.getDebt());
						_quartercfs.setDividendRatio(0);
						_quartercfs.setFixed(true);
						_quartercfs.setGrossCapital(_annualcfs.getGrossCapital());
						_quartercfs.setInvestedCapital(_annualcfs.getInvestedCapital());
						_quartercfs.setKOSPI(_annualcfs.isKOSPI());
						_quartercfs.setNetProfit(_annualcfs.getNetProfit() / numberOfQuarters);
						_quartercfs.setOperatingProfit(_annualcfs.getOperatingProfit() / numberOfQuarters);
						_quartercfs.setOrdinaryProfit(_annualcfs.getOrdinaryProfit() / numberOfQuarters);
						_quartercfs.setOrdinarySharesSize(_annualcfs.getOrdinarySharesSize());
						_quartercfs.setPrefferedSharesSize(_annualcfs.getPrefferedSharesSize());
						_quartercfs.setQuarter(true);
						_quartercfs.setRoa(0);
						_quartercfs.setRoe(0);
						_quartercfs.setRoi(0);
						_quartercfs.setSales(_annualcfs.getSales() / numberOfQuarters);
						orgDao.insert(_quartercfs);
					}
				}
			}
			/*
			if ( quartercfs.size() > 0 ) {
				CompanyFinancialStatus testcfs = quartercfs.get(0).quarter;
				for ( int cnt = 1 ; cnt < quartercfs.size() ; cnt++ ) {
					if ( !getNextQuarter(testcfs.getStandardDate()).equals(quartercfs.get(cnt).quarter.getStandardDate()) ) {
						System.out.println("this company has wrong reports.[" + company.getId() + "][" + company.getName() + "][" + testcfs.getStandardDate() + "]");
					}
					testcfs = quartercfs.get(cnt).quarter;
				}
			}
			*/
			/*
			for( int cnt = cfs.size() - 1 ; cnt == 0 ; cnt-- ) {
				CompanyFinancialStatus target = cfs.get(cnt);
				String[] periods = getPeriodList(PERIOD_TYPE(cfs.get(0).getStandardDate()));
				if ( target.isQuarter() 
						&& ( prevQuarter != null ) 
						&& ( !getPrevQuarter(prevQuarter.getStandardDate()).equals(target.getStandardDate()) ) ) {
					if ( latestYear != null ) {
						// If latest annual report exists....
						
					} else {
						// If there is no annual report. 
					}
					prevQuarter = target;
				} else {
					latestYear = target;
				}
			}
			*/
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		// 2.  
	}

	public static void main(String[] args) {
		//executeFillblankYearReport();
		executeFillblankQuarterReport();
	}
	
	static SimpleDateFormat STANDARD_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	
	public static void executeFillblankQuarterReport() {
		FinancialReportReverseEsitmator estimator = new FinancialReportReverseEsitmator();
		ArrayList<CompanyEx> companies = estimator.companyList;
		String currentDate = STANDARD_DATE_FORMAT.format(new Date());
		try {
			for ( int pos = 0 ; pos < companies.size() ; pos++ ) {
				System.out.println("-------> company[" + companies.get(pos).getId() + "][" + companies.get(pos).getName() + "][" + pos + "/" + companies.size() + "] <--------");
				//estimator.fillYearReport(companies.get(pos));
				estimator.fillQuaterReport(companies.get(pos), currentDate);
			}
		} catch ( Exception e1 ) {
			e1.printStackTrace();
		} 
	}
	
	public static void executeFillblankYearReport() {
		FinancialReportReverseEsitmator estimator = new FinancialReportReverseEsitmator();
		ArrayList<CompanyEx> companies = estimator.companyList;
		String currentDate = STANDARD_DATE_FORMAT.format(new Date());
		try {
			for ( int pos = 0 ; pos < companies.size() ; pos++ ) {
				System.out.println("-------> company[" + companies.get(pos).getId() + "][" + companies.get(pos).getName() + "][" + pos + "/" + companies.size() + "] <--------");
				estimator.fillYearReport(companies.get(pos), currentDate);
			}
		} catch ( Exception e1 ) {
			e1.printStackTrace();
		} 
	}
	
	public static void executeFillblankAssetInfo() {
		FinancialReportReverseEsitmator estimator = new FinancialReportReverseEsitmator();
		ArrayList<CompanyEx> companies = estimator.companyList;
		String currentDate = STANDARD_DATE_FORMAT.format(new Date());
		try {
			for ( int pos = 0 ; pos < companies.size() ; pos++ ) {
				System.out.println("-------> company[" + companies.get(pos).getId() + "][" + companies.get(pos).getName() + "][" + pos + "/" + companies.size() + "] <--------");
				estimator.fillBlankAssetInfos(companies.get(pos), currentDate);
			}
		} catch ( Exception e1 ) {
			e1.printStackTrace();
		} 
	}
}
