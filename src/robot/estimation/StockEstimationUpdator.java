package robot.estimation;

import java.util.ArrayList;

import post.Company;
import post.StockEstimated;
import robot.DataUpdator;
import dao.CompanyDao;
import dao.CompanyStockEstimationDao;

/**
 * 저장되어 있는 주식정보를 활용하여 기초 자료를 생성한다.
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
		estimator.StockValueEstimator estimator = new estimator.StockValueEstimator();
		StockEstimated cse = estimator.caculateCompanyStockEstimation(company);
		return cse;
	}

	/**
	 * 
	 * 주가 추산치 정보를 수정한다.
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
			//StockEstimationUpdator updator = new StockEstimationUpdator();
			//updator.updateAllStockEstimation();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
}
