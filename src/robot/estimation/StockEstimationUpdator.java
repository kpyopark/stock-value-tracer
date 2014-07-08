package robot.estimation;

import java.util.ArrayList;

import post.Company;
import post.StockEstimated;
import robot.DataUpdator;
import dao.CompanyDao;
import dao.CompanyStockEstimationDao;
import estimator.StockValueEstimator;

/**
 * ����Ǿ� �ִ� �ֽ������� Ȱ���Ͽ� ���� �ڷḦ �����Ѵ�.
 * 
 * @author Administrator
 *
 */
public class StockEstimationUpdator extends DataUpdator {
	
	ArrayList<Company> companyList = null;
	
	public StockEstimationUpdator() {
		init();
	}
	
	public void init() {
		try {
			CompanyDao dao = new CompanyDao();
			companyList = dao.selectAllList(); 
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public StockEstimated estimate(Company company) {
		StockValueEstimator estimator = new StockValueEstimator();
		StockEstimated cse = estimator.caculateCompanyStockEstimation(company);
		return cse;
	}
	
	public void updateAllStockEstimation() {
		for(Company company:companyList) {
			updateStockEstimated(company);
		}
	}

	/**
	 * 
	 * �ְ� �߻�ġ ������ �����Ѵ�.
	 * 
	 * @param cse
	 * @return
	 */
	public int updateStockEstimated(Company company) {
		CompanyStockEstimationDao stockEstimDao = new CompanyStockEstimationDao();
		StockEstimated cse = estimate(company);
		Throwable err = null;
		int totCnt = 0;
		try {
			if ( stockEstimDao.select(cse.getCompany(), cse.getStandardDate() ) != null ) {
				stockEstimDao.delete(cse);
			}
			totCnt = stockEstimDao.insert(cse) ? 1 : 0;
		} catch ( Exception e ) {
			err = e;
		}
		fireStockEstimationChanged(cse, err);
		return totCnt;
	}

	public static void main(String[] args) {
		try {
			StockEstimationUpdator updator = new StockEstimationUpdator();
			updator.updateAllStockEstimation();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
}
