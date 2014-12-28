package robot.estimation;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import post.Company;
import post.CompanyFinancialStatus;
import post.CompanyFinancialStatusEstimated;
import robot.DataUpdator;
import dao.CompanyDao;
import dao.CompanyFinancialEstimStatusDao;
import estimator.FinancialStatusEstimator;

/**
 * CompanyListUpdator �������� ������ �б������� ���� �ϳⰣ�� �����ڷḦ �����Ѵ�.
 * 
 * @author Administrator
 *
 */
public class AnnualEstimationUpdator extends DataUpdator {
	
	ArrayList<Company> companyList = null;
	CompanyFinancialEstimStatusDao estimDao = new CompanyFinancialEstimStatusDao();
	
	public AnnualEstimationUpdator() throws SQLException {
		this.getCompanyList();
	}
	
	private void getCompanyList() throws SQLException {
		CompanyDao dao = new CompanyDao();
		companyList = dao.selectAllList();
	}
	
	/**
	 * It is replace with procedure.
	 * @param company
	 * @param registeredDate
	 * @return
	 * @throws SQLException
	 */
	@Deprecated
	private CompanyFinancialStatusEstimated estimate(Company company, String registeredDate) throws SQLException {
		FinancialStatusEstimator estimator = new FinancialStatusEstimator();
		ArrayList<CompanyFinancialStatus> cfsList = estimator.getStandardFinancialStatusList(company,registeredDate);
		CompanyFinancialStatusEstimated estimatedCfs = estimator.getEstimatedCompanyFinancialStatus(cfsList, registeredDate);
		return estimatedCfs;
	}
	
	@Deprecated
	private void updateEstimatedCfs(CompanyFinancialStatusEstimated estimatedCfs, String registeredDate) throws SQLException {
		CompanyFinancialEstimStatusDao dao = new CompanyFinancialEstimStatusDao();
		if ( dao.select(estimatedCfs.getCompany(), estimatedCfs.getStandardDate(), estimatedCfs.getEstimKind()) != null ) {
			dao.delete(estimatedCfs);
		}
		dao.insert(estimatedCfs);
	}
	
	/**
	 * ��ü ������� ����� ���ʷ�
	 * �߻�ġ�� ����Ѵ���
	 * �߻�ġ ���̺��� update�Ѵ�.
	 * 
	 * @throws SQLException
	 */
	public void updateCompanyFinancialStatusEstimated(Company company, String registeredDate) throws SQLException {
		CompanyFinancialStatusEstimated estimatedCfs = estimDao.updateFinancialReportEstimated(company, registeredDate); //estimate(company, registeredDate);
		fireCompanyFinancialStatusEstimatedChanged(estimatedCfs, null);
	}
	
	static SimpleDateFormat STANDARD_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	
	public static void main(String[] args) {
		try {
			AnnualEstimationUpdator updator = new AnnualEstimationUpdator();
			updator.addUpdateListener(new robot.listenter.ExamUpdateListener());
			Company company = new Company();
			company.setId("A025890");
			updator.updateCompanyFinancialStatusEstimated(company, STANDARD_DATE_FORMAT.format(new Date()));
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

}
