package estimator;

import java.util.ArrayList;

import post.Company;
import post.CompanyFinancialStatus;
import post.CompanyFinancialStatusEstimated;
import post.Stock;
import post.StockEstimated;
import dao.CompanyFinancialEstimStatusDao;
import dao.StockDao;

public class StockValueEstimator {
	
	ArrayList<Company> companyList = null;
	ArrayList<Stock> stockList = null;
	String standardDate = null;
	
	public StockValueEstimator() {
		this(new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date()));
	}
	
	public StockValueEstimator(String standardDate) {
		this.standardDate = standardDate;
	}
	
	/**
	 * 회사 주가의 미래 추산치를 구한다.
	 * 
	 * @param company
	 * @return
	 */
	public StockEstimated caculateCompanyStockEstimation(Company company) {
		StockEstimated estimation = null;
		StockDao stockDao = new StockDao();
		CompanyFinancialEstimStatusDao estimStatusDao = new CompanyFinancialEstimStatusDao();
		try {
			CompanyFinancialStatusEstimated estimStatus = estimStatusDao.select(company,standardDate, null);
			Stock stock = stockDao.getLatestStockValue(company, standardDate, null);
			if ( estimStatus != null && stock != null ) {
				estimation = caculateStockEstimation(estimStatus, stock);
				estimation.setStandardDate(standardDate);
			} else {
				if ( estimStatus == null )
					System.out.println("해당 추산치가 존재하지 않습니다. ["+company.getName() + ":" + company.getId() + ":" + standardDate + "]");
				if ( stock == null )
					System.out.println("해당 주가정보가 존재하지 않습니다. ["+company.getName() + ":" + company.getId() + ":" + standardDate + "]");
			}
		} catch ( Exception e ) {
			e.printStackTrace();
			System.out.println("해당 정보를 가지고 오는 도중 에러가 발생하였습니다.["+company.getName() + ":" + company.getId() + ":" + standardDate + "]");
		}
		return estimation;
	}
	
	/**
	 * 
	 * 재무제표와 주가 정보로 주가의 미래추산치를 구한다.
	 * 
	 * @param cfs
	 * @param stock
	 * @return
	 */
	private StockEstimated caculateStockEstimation(CompanyFinancialStatus cfs, Stock stock) {
		Company company = cfs.getCompany();
		StockEstimated cse = new StockEstimated();
		cse.setCompany(company);
		cse.setCfs(cfs);
		
		cse.setAveDividendRatio(cfs.getDividendRatio());
		cse.setAveRoa(((float)cfs.getNetProfit())/cfs.getAssets());
		cse.setDebtRatio((float)cfs.getDebt()/cfs.getAssets());
		if ( cfs.getOrdinarySharesSize() > 0 ) {
			cse.setAveBps(((float)cfs.getGrossCapital()/cfs.getOrdinarySharesSize()));
			cse.setRecentEps(cfs.getNetProfit() / cfs.getOrdinarySharesSize());
			if ( stock.getValue() > 0 ) {
				cse.setAvePer(stock.getValue()/cse.getRecentEps());
				cse.setEarningYield(cse.getRecentEps()/stock.getValue());
				cse.setAveBpr(cse.getAveBps()/stock.getValue());
			} else {
				cse.setRecentEps(0);
				cse.setAvePer(10000);
				cse.setEarningYield(-100.0f);
				cse.setAveBpr(-100.0f);
				System.out.println("주가가 등록되어 있지 않습니다. 확인 필요.[" + stock + "]");
			}
		} else {
			cse.setAveBps(-100);
			cse.setRecentEps(0);
			cse.setAvePer(10000);
			cse.setEarningYield((float)-100.0);
			cse.setAveBpr(-100.0f);
			System.out.println("보통주가 등록되어 있지 않습니다. 확인 필요.[" + cfs.getCompany().getName() + ":" + cfs.getCompany().getId() + "]");
		}
		if ( cfs.getNetProfit() > 0 ) {
			if( cfs.getGrossCapital() > 0 ) {
				cse.setAveRoe(((float)cfs.getNetProfit())/cfs.getGrossCapital());
			} else {
				cse.setAveRoe(0);
				System.out.println("총자본금이 등록되어 있지 않습니다.[" + cfs.getCompany().getName() + ":" + cfs.getCompany().getId() + "]");
			}
			cse.setLastEps((float)(cse.getRecentEps()*Math.pow(1+cse.getAveRoe(),10)));
		} else {
			cse.setAveRoe(-1);
			cse.setAvePer(10000);
		}
		cse.setExpectationRation((float)0.15);
		cse.setRecentStockValue(stock.getValue());
		cse.setStandardDate(stock.getStandardDate());
		if ( cfs instanceof CompanyFinancialStatusEstimated ) {
			cse.setRelatedDateList(((CompanyFinancialStatusEstimated)cfs).getRelatedDateList());
		} else {
			cse.setRelatedDateList(cfs.getStandardDate());
		}
		
		return cse;
	}
	
	public static void main(String[] args) throws Exception {
		CompanyFinancialEstimStatusDao estimStatusDao = new CompanyFinancialEstimStatusDao();
		StockDao stockDao = new StockDao();
		Company company = new Company();
		company.setId("A008560");
		CompanyFinancialStatusEstimated estimStatus = estimStatusDao.select(company,null, null);
		Stock stock = stockDao.select(company, null, null);
		System.out.println(estimStatus);
		StockValueEstimator stockEstimator = new StockValueEstimator();
		StockEstimated estimation = stockEstimator.caculateStockEstimation(estimStatus,stock);
		System.out.println( estimation);
	}
}
