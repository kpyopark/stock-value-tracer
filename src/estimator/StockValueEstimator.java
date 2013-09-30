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
	String currentDate = null;
	
	public StockValueEstimator() {
		init();
	}
	
	private void init() {
		currentDate = new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
	}
	
	/**
	 * ȸ�� �ְ��� �̷� �߻�ġ�� ���Ѵ�.
	 * 
	 * @param company
	 * @return
	 */
	public StockEstimated caculateCompanyStockEstimation(Company company) {
		StockEstimated estimation = null;
		StockDao stockDao = new StockDao();
		CompanyFinancialEstimStatusDao estimStatusDao = new CompanyFinancialEstimStatusDao();
		try {
			CompanyFinancialStatusEstimated estimStatus = estimStatusDao.select(company,null, null);
			Stock stock = stockDao.select(company, null, null);
			if ( estimStatus != null && stock != null ) {
				estimation = caculateStockEstimation(estimStatus, stock);
			} else {
				if ( estimStatus == null )
					System.out.println("�ش� �߻�ġ�� �������� �ʽ��ϴ�. ["+company.getName() + ":" + company.getId() + "]");
				if ( stock == null )
					System.out.println("�ش� �ְ������� �������� �ʽ��ϴ�. ["+company.getName() + ":" + company.getId() + "]");
			}
		} catch ( Exception e ) {
			e.printStackTrace();
			System.out.println("�ش� ������ ������ ���� ���� ������ �߻��Ͽ����ϴ�.["+company.getName() + ":" + company.getId() + "]");
		}
		return estimation;
	}
	
	/**
	 * 
	 * �繫��ǥ�� �ְ� ������ �ְ��� �̷��߻�ġ�� ���Ѵ�.
	 * 
	 * @param cfs
	 * @param stock
	 * @return
	 */
	private StockEstimated caculateStockEstimation(CompanyFinancialStatus cfs, Stock stock) {
		Company company = cfs.getCompany();
		StockEstimated cse = new StockEstimated();
		cse.setCompany(company);
		
		cse.setAveDividendRatio(cfs.getDividendRatio());
		if ( cfs.getNetProfit() > 0 ) {
			if ( cfs.getOrdinarySharesSize() > 0 ) {
				cse.setRecentEps(cfs.getNetProfit() / cfs.getOrdinarySharesSize());
				if ( stock.getStandardDate().compareTo(currentDate) == 0 && stock.getValue() > 0 ) {
					cse.setAvePer(stock.getValue()/cse.getRecentEps());
				} else {
					cse.setRecentEps(0);
					cse.setAvePer(10000);
					System.out.println("�ְ��� ��ϵǾ� ���� �ʽ��ϴ�. Ȯ�� �ʿ�.[" + stock + "]");
				}
			} else {
				cse.setRecentEps(0);
				cse.setAvePer(10000);
				System.out.println("�����ְ� ��ϵǾ� ���� �ʽ��ϴ�. Ȯ�� �ʿ�.[" + cfs.getCompany().getName() + ":" + cfs.getCompany().getId() + "]");
			}
			if( cfs.getGrossCapital() > 0 ) {
				cse.setAveRoe(((float)cfs.getNetProfit())/cfs.getGrossCapital());
			} else {
				cse.setAveRoe(0);
				System.out.println("���ں����� ��ϵǾ� ���� �ʽ��ϴ�.[" + cfs.getCompany().getName() + ":" + cfs.getCompany().getId() + "]");
			}
			if( cfs.getAssets() > 0 ) {
				cse.setAveRoa(((float)cfs.getNetProfit())/cfs.getAssets());
			} else {
				cse.setAveRoa(0);
				System.out.println("�ڻ��� ��ϵǾ� ���� �ʽ��ϴ�.[" + cfs.getCompany().getName() + ":" + cfs.getCompany().getId() + "]");
			}
			cse.setLastEps((float)(cse.getRecentEps()*Math.pow(1+cse.getAveRoe(),10)));
		} else {
			cse.setRecentEps(-1);
			cse.setAvePer(10000);
			cse.setAveRoe(-1);
			cse.setAveRoa(-1);
		}
		cse.setExpectationRation((float)0.15);
		cse.setRecentStockValue(stock.getValue());
		cse.setStandardDate(cfs.getStandardDate());
		if ( cfs instanceof CompanyFinancialStatusEstimated ) {
			cse.setRelatedDateList(((CompanyFinancialStatusEstimated)cfs).getRelatedDateList());
		}
		
		return cse;
	}
	
	public static void main(String[] args) throws Exception {
		CompanyFinancialEstimStatusDao estimStatusDao = new CompanyFinancialEstimStatusDao();
		StockDao stockDao = new StockDao();
		Company company = new Company();
		company.setId("A025890");
		CompanyFinancialStatusEstimated estimStatus = estimStatusDao.select(company,null, null);
		Stock stock = stockDao.select(company, null, null);
		System.out.println(estimStatus);
		StockValueEstimator stockEstimator = new StockValueEstimator();
		StockEstimated estimation = stockEstimator.caculateStockEstimation(estimStatus,stock);
		System.out.println( estimation);
	}
}
