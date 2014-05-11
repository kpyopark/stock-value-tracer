package estimator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import post.Company;
import post.CompanyFinancialStatus;
import post.CompanyFinancialStatusEstimated;
import dao.CompanyDao;
import dao.CompanyFinancialStatusDao;

public class FinancialStatusEstimator {
	
	ArrayList<Company> companyList = null;
	
	public FinancialStatusEstimator() {
		init();
	}
	
	private void init() {
		CompanyDao dao = new CompanyDao();
		try {
			companyList = dao.selectAllList();
		} catch ( Exception e ) {
			e.printStackTrace();
			companyList = new ArrayList<Company>();
		}
	}
	
	static String FIRST_QUARTER = "0331";
	static String SECOND_QUARTER = "0630";
	static String THIRD_QUARTER = "0930";
	static String FORTH_QUARTER = "1231";
	
	static String[] STANDARD_QUARTER_LIST = {FIRST_QUARTER,SECOND_QUARTER,THIRD_QUARTER,FORTH_QUARTER};

	/**
	 * 	분기 수익중 가장 최신의 것(미래예상치 제외) 4가지를 가지고 온다.
	 *  연간 수익증 가장 최신의 것(미래예상치 제외, fixed) 을 가지고 온다.
	 *  분기에서 구할 수 있는 기간과 연간 수익에서  구할 수 있는 기간을 비교하여 가능한 큰쪽을 선택한다.
	 *  
	 *  구해진 재무정보 리스트는 가장 최신의 자료들인 것이다.
	 *  이를 통해 재무정보를 추산한다.
	 * 
	 * @param company
	 * @return
	 */
	public ArrayList<CompanyFinancialStatus> getStandardFinancialStatusList(Company company, String registeredDate) {
		CompanyFinancialStatusDao orgDao = new CompanyFinancialStatusDao();
		ArrayList<CompanyFinancialStatus> cfsList = null;
		ArrayList<CompanyFinancialStatus> rtn = null;
		
		try {
			cfsList = getAvailiableCfs( orgDao.getFinancialStatus(company, registeredDate) );
			ArrayList<CompanyFinancialStatus> quarterList = getQuarterList(cfsList);
			ArrayList<CompanyFinancialStatus> annualList = getAnnualList(cfsList);
			ArrayList<CompanyFinancialStatus> continuousQuarterList = getContinuousQuarterList(quarterList);
			
			if ( continuousQuarterList.size() == 0 ) {
				rtn = annualList;
			} else if ( annualList.size() == 0 ) {
				// 이런 일이 발생하면 안되겠지요.^^
				rtn = continuousQuarterList;
			} else {
				rtn = ( continuousQuarterList.get(0).getStandardDate().compareTo(
						annualList.get(0).getStandardDate()) > 0 ) ?
						continuousQuarterList : annualList;
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return rtn;
	}
	
	public CompanyFinancialStatusEstimated getEstimatedCompanyFinancialStatus(ArrayList<CompanyFinancialStatus> cfsList, String registeredDate) {
		CompanyFinancialStatusEstimated estimated = new CompanyFinancialStatusEstimated();
		if ( cfsList.size() == 0 ) {
			System.out.println("기초 재무정보를 획득하지 못했습니다. 위에 프로그램을 다시 한번 손좀 보세요.");
			return null;
		}
		if ( cfsList.get(0).isQuarter() ) {
			if ( cfsList.size() < 4 ) {
				System.out.println("분기정보가 정확하게 들어오지 않았습니다. 이 매쏘드 바로 전을 확인하세요.");
			} else {
				CompanyFinancialStatus first = cfsList.get(0);
				CompanyFinancialStatus second = cfsList.get(1);
				CompanyFinancialStatus third = cfsList.get(2);
				CompanyFinancialStatus forth = cfsList.get(3);
				
				//System.out.println("--분기정보로 산출--[" + first.getCompany().getId() + ":" + first.getCompany().getName() +"]");
				
				estimated.setCompany(first.getCompany());
				estimated.setRelatedDateList(first.getStandardDate()+","+second.getStandardDate()+"," +third.getStandardDate() + "," + forth.getStandardDate());
				
				int fixedCfsCount = ( first.isFixed() ? 1 : 0 ) + ( second.isFixed() ? 1 : 0 ) +
						( third.isFixed() ? 1 : 0 ) + ( forth.isFixed() ? 1 : 0 );
				long estimatedAssets = (( first.isFixed() ? first.getAssets() : 0 ) + ( second.isFixed() ? second.getAssets() : 0 ) +
						( third.isFixed() ? third.getAssets() : 0 ) + ( forth.isFixed() ? forth.getAssets() : 0 )) / fixedCfsCount;
				long estimatedCapital = (( first.isFixed() ? first.getCapital() : 0 ) + ( second.isFixed() ? second.getCapital() : 0 ) +
						( third.isFixed() ? third.getCapital() : 0 ) + ( forth.isFixed() ? forth.getCapital() : 0 )) / fixedCfsCount;
				long estimatedDebt = (( first.isFixed() ? first.getDebt() : 0 ) + ( second.isFixed() ? second.getDebt() : 0 ) +
						( third.isFixed() ? third.getDebt() : 0 ) + ( forth.isFixed() ? forth.getDebt() : 0 )) / fixedCfsCount;
				long estimatedGrossCapital = (( first.isFixed() ? first.getGrossCapital() : 0 ) + ( second.isFixed() ? second.getGrossCapital() : 0 ) +
						( third.isFixed() ? third.getGrossCapital() : 0 ) + ( forth.isFixed() ? forth.getGrossCapital() : 0 )) / fixedCfsCount;
				long estimatedInvestedCapital = (( first.isFixed() ? first.getInvestedCapital() : 0 ) + ( second.isFixed() ? second.getInvestedCapital() : 0 ) +
						( third.isFixed() ? third.getInvestedCapital() : 0 ) + ( forth.isFixed() ? forth.getInvestedCapital() : 0 )) / fixedCfsCount;
				long estimatedNetProfit = first.getNetProfit() + second.getNetProfit() + third.getNetProfit() + forth.getNetProfit();
				if ( first.getNetProfit() == 0 || second.getNetProfit() == 0 || third.getNetProfit() == 0 || forth.getNetProfit() == 0 ) {
					// 분기별 순수익이 정확하게 산출되지 않은경우. 정확하지 않은 0인 항목은 제외하고 수익을 구할 수 있도록 수정한다.
					int realValueCount = 0;
					realValueCount += first.getNetProfit() != 0 ? 1 : 0;
					realValueCount += second.getNetProfit() != 0 ? 1 : 0;
					realValueCount += third.getNetProfit() != 0 ? 1 : 0;
					realValueCount += forth.getNetProfit() != 0 ? 1 : 0;
					estimatedNetProfit = realValueCount == 0 ? 0 : estimatedNetProfit * 4 / realValueCount; 
				}
				//TODO: 배당수익율과 같은 것들은 여러해 동안의 자료를 통한 추산치로 추후 계산하는 것으로 한다.
				long estimatedOperatingProfit = first.getOperatingProfit() + second.getOperatingProfit() + third.getOperatingProfit() + forth.getOperatingProfit();
				if ( first.getOperatingProfit() == 0 || second.getOperatingProfit() == 0 || third.getOperatingProfit() == 0 || forth.getOperatingProfit() == 0 ) {
					// 분기별 순수익이 정확하게 산출되지 않은경우. 정확하지 않은 0인 항목은 제외하고 수익을 구할 수 있도록 수정한다.
					int realValueCount = 0;
					realValueCount += first.getOperatingProfit() != 0 ? 1 : 0;
					realValueCount += second.getOperatingProfit() != 0 ? 1 : 0;
					realValueCount += third.getOperatingProfit() != 0 ? 1 : 0;
					realValueCount += forth.getOperatingProfit() != 0 ? 1 : 0;
					estimatedOperatingProfit = realValueCount == 0 ? 0 : estimatedOperatingProfit * 4 / realValueCount; 
				}
				long estimatedOrdinaryProfit = first.getOrdinaryProfit() + second.getOrdinaryProfit() + third.getOrdinaryProfit() + forth.getOrdinaryProfit();
				if ( first.getOrdinaryProfit() == 0 || second.getOrdinaryProfit() == 0 || third.getOrdinaryProfit() == 0 || forth.getOrdinaryProfit() == 0 ) {
					// 분기별 순수익이 정확하게 산출되지 않은경우. 정확하지 않은 0인 항목은 제외하고 수익을 구할 수 있도록 수정한다.
					int realValueCount = 0;
					realValueCount += first.getOrdinaryProfit() != 0 ? 1 : 0;
					realValueCount += second.getOrdinaryProfit() != 0 ? 1 : 0;
					realValueCount += third.getOrdinaryProfit() != 0 ? 1 : 0;
					realValueCount += forth.getOrdinaryProfit() != 0 ? 1 : 0;
					estimatedOrdinaryProfit = realValueCount == 0 ? 0 : estimatedOrdinaryProfit * 4 / realValueCount; 
				}
				// 주식수는 가장 최신의 주식수를 가지고 온다. modifid 2007.02.27
				long estimatedOrdinarySharesSize = getLatestOrdinarySharesSize(first.getCompany(), registeredDate);
					//(( first.isFixed() ? first.getOrdinarySharesSize() : 0 ) + ( second.isFixed() ? second.getOrdinarySharesSize() : 0 ) +
					//	( third.isFixed() ? third.getOrdinarySharesSize() : 0 ) + ( forth.isFixed() ? forth.getOrdinarySharesSize() : 0 )) / fixedCfsCount;
				// 주식수는 가장 최신의 주식수를 가지고 온다. modifid 2007.02.27
				long estimatedPrefferedSharesSize = getLatestPrefferedSharesSize(first.getCompany(), registeredDate);
					//(( first.isFixed() ? first.getPrefferedSharesSize() : 0 ) + ( second.isFixed() ? second.getPrefferedSharesSize() : 0 ) +
					//	( third.isFixed() ? third.getPrefferedSharesSize() : 0 ) + ( forth.isFixed() ? forth.getPrefferedSharesSize() : 0 )) / fixedCfsCount;
				long estimatedSales = (( first.isFixed() ? first.getSales() : 0 ) + ( second.isFixed() ? second.getSales() : 0 ) +
						( third.isFixed() ? third.getSales() : 0 ) + ( forth.isFixed() ? forth.getSales() : 0 )) / fixedCfsCount;
				
				estimated.setAssets(estimatedAssets);
				estimated.setCapital(estimatedCapital);
				estimated.setDebt(estimatedDebt);
				estimated.setGrossCapital(estimatedGrossCapital);
				estimated.setInvestedCapital(estimatedInvestedCapital);
				estimated.setNetProfit(estimatedNetProfit);
				estimated.setOperatingProfit(estimatedOperatingProfit);
				estimated.setOrdinaryProfit(estimatedOrdinaryProfit);
				estimated.setOrdinarySharesSize(estimatedOrdinarySharesSize);
				estimated.setPrefferedSharesSize(estimatedPrefferedSharesSize);
				estimated.setSales(estimatedSales);
				
				estimated.setFixed(false);
				estimated.setStandardDate(first.getStandardDate());
				estimated.setQuarter(false);
				
			}
		} else {
			java.util.Collections.sort(cfsList,new StandardDateReverseComparator());
			try {
				//System.out.println("--년 정보로 산출--[" + cfsList.get(0).getCompany().getId() + ":" + cfsList.get(0).getCompany().getName() +"]");
				estimated.setRelatedDateList(cfsList.get(0).getStandardDate());
				estimated.copyStructure(cfsList.get(0));
				estimated.setOrdinarySharesSize(getLatestOrdinarySharesSize(cfsList.get(0).getCompany(), registeredDate));
				estimated.setPrefferedSharesSize(getLatestPrefferedSharesSize(cfsList.get(0).getCompany(), registeredDate));
			} catch ( Exception e ) {
				System.out.println("년간 재무정보를 정확하게 얻지 못했습니다. 확인이 필요합니다.[" + cfsList.get(0).getCompany().getId() + ":" + cfsList.get(0).getCompany().getName() +"]");
			}
		}
		return estimated;
	}
	
	static CompanyFinancialStatusDao orgDaoUsedBySharesSize = new CompanyFinancialStatusDao();
	
	public static long getLatestOrdinarySharesSize(Company company, String registeredDate) {
		long rtn = Integer.MAX_VALUE;
		try {
			ArrayList<CompanyFinancialStatus> cfsList = orgDaoUsedBySharesSize.getFinancialStatus(company, registeredDate);
			java.util.Collections.sort(cfsList,new StandardDateReverseComparator());
			for ( int cnt = 0 ; cnt < cfsList.size() ; cnt++ ) {
				if ( cfsList.get(cnt).getOrdinarySharesSize() != 0 ) {
					rtn = cfsList.get(cnt).getOrdinarySharesSize();
					break;
				}
			}
		} catch ( Exception e ) { e.printStackTrace(); }
		return rtn;
	}
	
	private static long getLatestPrefferedSharesSize(Company company, String registeredDate) {
		long rtn = Integer.MAX_VALUE;
		try {
			ArrayList<CompanyFinancialStatus> cfsList = orgDaoUsedBySharesSize.getFinancialStatus(company, registeredDate);
			java.util.Collections.sort(cfsList,new StandardDateReverseComparator());
			for ( int cnt = 0 ; cnt < cfsList.size() ; cnt++ ) {
				if ( cfsList.get(cnt).getOrdinarySharesSize() != 0 ) {
					rtn = cfsList.get(cnt).getPrefferedSharesSize();
					break;
				}
			}
		} catch ( Exception e ) { e.printStackTrace(); }
		return rtn;
	}
	
	private static ArrayList<CompanyFinancialStatus> getContinuousQuarterList(ArrayList<CompanyFinancialStatus> cfsList) {
		ArrayList<CompanyFinancialStatus> contiCfsList = new ArrayList<CompanyFinancialStatus>();
		if( cfsList.size() < 4 ) return contiCfsList;
		java.util.Collections.sort(cfsList,new StandardDateReverseComparator());
		
		for ( int cnt = 0 ; cnt < cfsList.size() - 3 ; cnt++ ) {
			if ( isNextQuarter( cfsList.get(cnt).getStandardDate() , cfsList.get(cnt+1).getStandardDate() ) &&
				 isNextQuarter( cfsList.get(cnt+1).getStandardDate() , cfsList.get(cnt+2).getStandardDate() ) &&
				 isNextQuarter( cfsList.get(cnt+2).getStandardDate() , cfsList.get(cnt+3).getStandardDate() ) ) {
				contiCfsList.add(cfsList.get(cnt));
				contiCfsList.add(cfsList.get(cnt+1));
				contiCfsList.add(cfsList.get(cnt+2));
				contiCfsList.add(cfsList.get(cnt+3));
			}
		}
		return contiCfsList;
	}
	
	private static boolean isNextQuarter(String  firstQuarter, String secondQuarter) {
		boolean isNextQuarter = false;
		if ( firstQuarter.length() == 8 && secondQuarter.length() == 8 ) {
			try {
				int firstYear, secondYear;
				int firstMonth, secondMonth;
				firstYear = Integer.parseInt(firstQuarter.substring(0,4));
				secondYear = Integer.parseInt(secondQuarter.substring(0,4));
				firstMonth = Integer.parseInt(firstQuarter.substring(4,6));
				secondMonth = Integer.parseInt(secondQuarter.substring(4,6));
				return (firstYear - secondYear) * 12 + ( firstMonth - secondMonth ) == 3;
			} catch ( Exception e ) {
				// 에러가 나면 분기정보가 틀린 것임.
			}
		}
		return isNextQuarter;
	}
	
	
	
	/**
	 * 분기실절정보를 가지고 온다.
	 * @param cfsList
	 * @return
	 */
	private static ArrayList<CompanyFinancialStatus> getQuarterList(ArrayList<CompanyFinancialStatus> cfsList) {
		ArrayList<CompanyFinancialStatus> quarterCfsList = new ArrayList<CompanyFinancialStatus>();
		for ( int cnt = 0 ; cnt < cfsList.size() ; cnt++ ) {
			if ( cfsList.get(cnt).isQuarter() )
				quarterCfsList.add(cfsList.get(cnt));
		}
		java.util.Collections.sort(quarterCfsList,new StandardDateReverseComparator());
		return quarterCfsList;
	}
	
	/**
	 * 년간실적정보를 가지고 온다.
	 * @param cfsList
	 * @return
	 */
	private static ArrayList<CompanyFinancialStatus> getAnnualList(ArrayList<CompanyFinancialStatus> cfsList) {
		ArrayList<CompanyFinancialStatus> annualCfsList = new ArrayList<CompanyFinancialStatus>();
		for ( int cnt = 0 ; cnt < cfsList.size() ; cnt++ ) {
			if ( !cfsList.get(cnt).isQuarter() )
				annualCfsList.add(cfsList.get(cnt));
		}
		java.util.Collections.sort(annualCfsList,new StandardDateReverseComparator());
		return annualCfsList;
	}
	
	private static ArrayList<CompanyFinancialStatus> getAvailiableCfs(ArrayList<CompanyFinancialStatus> cfsList) {
		ArrayList<CompanyFinancialStatus> newList = new ArrayList<CompanyFinancialStatus>();
		String currentQuarter = getCurrentQuarter();
		for ( int cnt = 0 ; cnt < cfsList.size() ; cnt++ ) {
			if ( cfsList.get(cnt).getStandardDate().compareTo(currentQuarter) <= 0 ) {
				if ( cfsList.get(cnt).isFixed() ) {
					newList.add(cfsList.get(cnt));
				} else {
					//System.out.println("이 자료는 미래자료(추산치)이므로 제외해야함.[" + cfsList.get(cnt).getCompany().getId() + ":" + cfsList.get(cnt).getCompany().getName() + ":" + cfsList.get(cnt).getStandardDate() + "]" );
				}
			} else {
				//System.out.println("이 자료는 미래자료(추산치)이므로 제외해야함.[" + cfsList.get(cnt).getCompany().getId() + ":" + cfsList.get(cnt).getCompany().getName() + ":" + cfsList.get(cnt).getStandardDate() + "]" );
			}
		}
		return newList;
	}
	
	private static String getCurrentQuarter() {
		java.util.GregorianCalendar calendar = new java.util.GregorianCalendar();
		int currentMonth = calendar.get(java.util.GregorianCalendar.MONTH);
		int currentYear = calendar.get(java.util.GregorianCalendar.YEAR);
		int currentQuarter = (int)(currentMonth/4);
		String currentQuarterString = currentYear + STANDARD_QUARTER_LIST[currentQuarter];
		return currentQuarterString;
	}
	
	static SimpleDateFormat STANDARD_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	
	public static void main(String[] args) {
		FinancialStatusEstimator estim = new FinancialStatusEstimator();
		Company company = new Company();
		String currentDate = STANDARD_DATE_FORMAT.format(new Date());
		company.setId("A031980");
		company.setName("피에스케이");
		System.out.println(estim.getEstimatedCompanyFinancialStatus(estim.getStandardFinancialStatusList(company, currentDate), currentDate));
		System.out.println(FinancialStatusEstimator.getLatestOrdinarySharesSize(company, currentDate));
	}
	
}

/**
 * 재무상태에서 기준날짜순으로 sorting할 때 사용하는 Comparator
 * @author Administrator
 *
 */
class StandardDateComparator implements java.util.Comparator<CompanyFinancialStatus> {
	public int compare(CompanyFinancialStatus src, CompanyFinancialStatus tgt) {
		return src.getStandardDate().compareTo(tgt.getStandardDate());
	}
}

class StandardDateReverseComparator implements java.util.Comparator<CompanyFinancialStatus> {
	public int compare(CompanyFinancialStatus src, CompanyFinancialStatus tgt) {
		return src.getStandardDate().compareTo(tgt.getStandardDate()) * -1;
	}
}
