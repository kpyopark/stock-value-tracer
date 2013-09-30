package robot;

import java.sql.SQLException;
import java.util.ArrayList;

import post.Company;
import post.CompanyFinancialStatus;
import post.CompanyFinancialStatusEstimated;
import dao.CompanyDao;
import dao.CompanyFinancialEstimStatusDao;
import estimator.FinancialStatusEstimator;

/**
 * CompanyListUpdator 정보에서 취합한 분기정보를 토대로 일년간의 기초자료를 생성한다.
 * 
 * @author Administrator
 *
 */
public class AnnualEstimationUpdator extends DataUpdator {
	
	ArrayList<Company> companyList = null;
	
	public AnnualEstimationUpdator() throws SQLException {
		this.getCompanyList();
	}
	
	private void getCompanyList() throws SQLException {
		CompanyDao dao = new CompanyDao();
		companyList = dao.selectAllList();
	}
	
	private CompanyFinancialStatusEstimated estimate(Company company) throws SQLException {
		FinancialStatusEstimator estimator = new FinancialStatusEstimator();
		ArrayList<CompanyFinancialStatus> cfsList = estimator.getStandardFinancialStatusList(company);
		CompanyFinancialStatusEstimated estimatedCfs = estimator.getEstimatedCompanyFinancialStatus(cfsList);
		return estimatedCfs;
	}
	
	private void updateEstimatedCfs(CompanyFinancialStatusEstimated estimatedCfs) throws SQLException {
		CompanyFinancialEstimStatusDao dao = new CompanyFinancialEstimStatusDao();
		if ( dao.select(estimatedCfs.getCompany(), estimatedCfs.getStandardDate(), estimatedCfs.getEstimKind()) != null ) {
			dao.delete(estimatedCfs);
		}
		dao.insert(estimatedCfs);
	}
	
	/**
	 * 전체 기업정보 목록을 기초로
	 * 추산치를 계산한다음
	 * 추산치 테이블을 update한다.
	 * 
	 * @throws SQLException
	 */
	public void updateCompanyFinancialStatusEstimated(Company company) throws SQLException {
		CompanyFinancialStatusEstimated estimatedCfs = estimate(company);
		Throwable err = null;
		if ( estimatedCfs == null ) {
			err = new Throwable("추산치 측정 불가. 직접 확인");
		} else {
			updateEstimatedCfs(estimatedCfs);
		}
		fireCompanyFinancialStatusEstimatedChanged(estimatedCfs, err);
	}
	
	public static void main(String[] args) {
		try {
			AnnualEstimationUpdator updator = new AnnualEstimationUpdator();
			updator.addUpdateListener(new robot.conc.ExamUpdateListener());
			Company company = new Company();
			company.setId("A025890");
			updator.updateCompanyFinancialStatusEstimated(company);
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

}
