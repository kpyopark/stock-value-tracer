package robot;

import internetResource.companyItem.CompanyListResourceFromFnguide;
import internetResource.financialReport.FinancialReportResourceFromFnguide;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;

import post.Company;
import post.CompanyFinancialStatus;


import dao.CompanyDao;
import dao.CompanyFinancialStatusDao;

/**
 * ȸ���� �繫������ web�󿡼� ������ �´�. (�б������� �����Ѵ�.)
 * @author Administrator
 *
 */
public class CompanyListUpdator extends DataUpdator {
	
	public final static long PRECISION_THRESHOLD = 199999999;
	
	ArrayList<Company> companyList = null;
	
	public void getCompanyList() throws SQLException {
		CompanyDao dao = new CompanyDao();
		companyList = dao.selectAllList();
	}
	
	/**
	 * List���� ������ ���� ��Ȯ�� �������� ������ ����. �۳⸻������ ������ �ּ���.
	 */
	ArrayList<CompanyFinancialStatus> financialStatusList = null;
	
	/**
	 * List�� web page���� ������ ��.
	 * @throws Exception
	 * @deprecated
	 */
	public void getAnnualStatusList() throws Exception {
		CompanyListResourceFromFnguide ilr = new CompanyListResourceFromFnguide();
		financialStatusList = ilr.getItemList();
	}
	
	/**
	 * ������ ������ �� ���������� Ȱ���Ͽ� ��� ���� update
	 * @deprecated
	 */
	public int updateCompany() throws Exception {
		CompanyDao dao = new CompanyDao();
		int totCount = 0;
		for ( int cnt = 0 ; cnt < financialStatusList.size() ; cnt++ ) {
			Company comp = dao.select(financialStatusList.get(cnt).getCompany().getId(),financialStatusList.get(cnt).getStandardDate());
			if ( comp == null ) {
				if ( dao.insert(financialStatusList.get(cnt).getCompany()) )
					totCount++;
			} else {
				// ȸ����� �ٲ���� ��� �ٲ� ȸ����� �ִ´�.
				if ( !comp.getName().equals(financialStatusList.get(cnt).getCompany().getName()) ) {
					comp.setName(financialStatusList.get(cnt).getCompany().getName());
					comp.setStandardDate(financialStatusList.get(cnt).getStandardDate());
					System.out.println(comp);
					dao.update(comp);
				}
			}
		}
		return totCount;
	}
	
	/**
	 * ������ ������ �� �ڷḦ Ȱ���Ͽ� ��� �������� update
	 * @deprecated
	 * 
	 * @return
	 * @throws Exception
	 */
	public int updateFinancialStatus() throws Exception {
		CompanyFinancialStatusDao dao = new CompanyFinancialStatusDao();
		int totCount = 0;
		for ( int cnt = 0 ; cnt < financialStatusList.size() ; cnt++ ) {
			CompanyFinancialStatus stat = financialStatusList.get(cnt);
			CompanyFinancialStatus oldStat = dao.select(stat.getCompany(),stat.getStandardDate(),stat.isQuarter());
			if ( oldStat == null ) {
				if ( dao.insert(stat) )
					totCount++;
			} else {
				updateMergedData(oldStat, stat);
				totCount++;
			}
		}
		return totCount;
	}
	
	/**
	 * �ش� ����� ���� ����(�б⺰ ������)�� ������ �ͼ� update
	 * 
	 * @param company
	 * @return
	 * @throws Exception
	 */
	public int updateFinancialStatus(Company company) throws Exception {
		CompanyFinancialStatusDao dao = new CompanyFinancialStatusDao();
		int totCount = 0;
		FinancialReportResourceFromFnguide ir = new FinancialReportResourceFromFnguide();
		ArrayList<CompanyFinancialStatus> financialStatus = ir.getFinancialStatus(company);
		for ( int cnt = 0 ; cnt < financialStatus.size() ; cnt++ ) {
			System.out.println("start update for[" + company.getId() + ":" + company.getName() + "]");
			CompanyFinancialStatus stat = financialStatus.get(cnt);
			CompanyFinancialStatus oldStat = dao.select(stat.getCompany(),stat.getStandardDate(),stat.isQuarter());
			if ( oldStat == null ) {
				if ( dao.insert(stat) )
					totCount++;
			} else {
				// ���� ��� �������� kospi ���ΰ� �������� �ƴ���.
				stat.setKOSPI(oldStat.isKOSPI());
				stat = updateMergedData(oldStat, stat);
			}
			if ( stat != null ) fireCompanyFinancialStatusChanged(stat, null);
		}
		return totCount;
	}
	
	/**
	 * ���� �ڷ�� �ֽ� �ڷ��� ���� �ֽ��� �ڷḦ ���Ͽ� update�Ѵ�.
	 * 
	 * @param oldStatus
	 * @param newStatus
	 * @return
	 * @throws SQLException
	 */
	private CompanyFinancialStatus updateMergedData(CompanyFinancialStatus oldStatus, CompanyFinancialStatus newStatus) throws Exception {
		
		if ( !oldStatus.getCompany().getId().equals(newStatus.getCompany().getId()) ) throw new Exception("ID�� �ٸ�["+oldStatus.getCompany().getId()+"]["+newStatus.getCompany().getId()+"]");
		if ( oldStatus.isFixed() && oldStatus.getOrdinarySharesSize() != 0 ) {
			return null; 
		}

		CompanyFinancialStatus mergeStatus = new CompanyFinancialStatus();
		mergeStatus.setCompany(oldStatus.getCompany());
		mergeStatus.setStandardDate(oldStatus.getStandardDate());
		mergeStatus.setQuarter(oldStatus.isQuarter());
		
		boolean needUpdated = false;

		needUpdated |= mergeCompanyFinancialStatus(mergeStatus, oldStatus, newStatus, "assets" , isUpdateNeeded(oldStatus , newStatus , "assets" ) );
		needUpdated |= mergeCompanyFinancialStatus(mergeStatus, oldStatus, newStatus, "capital" , isUpdateNeeded(oldStatus , newStatus , "capital" ) );
		needUpdated |= mergeCompanyFinancialStatus(mergeStatus, oldStatus, newStatus, "debt" , isUpdateNeeded(oldStatus , newStatus , "debt" ) );
		needUpdated |= mergeCompanyFinancialStatus(mergeStatus, oldStatus, newStatus, "dividendRatio" , isUpdateNeeded(oldStatus , newStatus , "dividendRatio" ) );
		needUpdated |= mergeCompanyFinancialStatus(mergeStatus, oldStatus, newStatus, "grossCapital" , isUpdateNeeded(oldStatus , newStatus , "grossCapital" ) );
		needUpdated |= mergeCompanyFinancialStatus(mergeStatus, oldStatus, newStatus, "Fixed" , isUpdateNeeded(oldStatus , newStatus , "Fixed" ) );
		needUpdated |= mergeCompanyFinancialStatus(mergeStatus, oldStatus, newStatus, "KOSPI" , isUpdateNeeded(oldStatus , newStatus , "KOSPI" ) );
		needUpdated |= mergeCompanyFinancialStatus(mergeStatus, oldStatus, newStatus, "Quarter" , isUpdateNeeded(oldStatus , newStatus , "Quarter" ) );
		needUpdated |= mergeCompanyFinancialStatus(mergeStatus, oldStatus, newStatus, "netProfit" , isUpdateNeeded(oldStatus , newStatus , "netProfit" ) );
		needUpdated |= mergeCompanyFinancialStatus(mergeStatus, oldStatus, newStatus, "operatingProfit" , isUpdateNeeded(oldStatus , newStatus , "operatingProfit" ) );
		needUpdated |= mergeCompanyFinancialStatus(mergeStatus, oldStatus, newStatus, "ordinaryProfit" , isUpdateNeeded(oldStatus , newStatus , "ordinaryProfit" ) );
		needUpdated |= mergeCompanyFinancialStatus(mergeStatus, oldStatus, newStatus, "roa" , isUpdateNeeded(oldStatus , newStatus , "roa" ) );
		needUpdated |= mergeCompanyFinancialStatus(mergeStatus, oldStatus, newStatus, "roe" , isUpdateNeeded(oldStatus , newStatus , "roe" ) );
		needUpdated |= mergeCompanyFinancialStatus(mergeStatus, oldStatus, newStatus, "roi" , isUpdateNeeded(oldStatus , newStatus , "roi" ) );
		needUpdated |= mergeCompanyFinancialStatus(mergeStatus, oldStatus, newStatus, "sales" , isUpdateNeeded(oldStatus , newStatus , "sales" ) );
		needUpdated |= mergeCompanyFinancialStatus(mergeStatus, oldStatus, newStatus, "ordinarySharesSize" , isUpdateNeeded(oldStatus , newStatus , "ordinarySharesSize" ) );
		needUpdated |= mergeCompanyFinancialStatus(mergeStatus, oldStatus, newStatus, "prefferedSharesSize" , isUpdateNeeded(oldStatus , newStatus , "prefferedSharesSize" ) );
		needUpdated |= mergeCompanyFinancialStatus(mergeStatus, oldStatus, newStatus, "investedCapital" , isUpdateNeeded(oldStatus , newStatus , "investedCapital" ) );
		
		if ( needUpdated ) {
			CompanyFinancialStatusDao dao = new CompanyFinancialStatusDao();
			dao.delete(oldStatus);
			try {
				dao.insert(mergeStatus);
			} catch ( Exception e ) {
				dao.insert(oldStatus);
			}
		}
		
		return mergeStatus;
	}

	private boolean mergeCompanyFinancialStatus(CompanyFinancialStatus target, CompanyFinancialStatus oldStatus , CompanyFinancialStatus newStatus , String field, boolean mergedWithNewStatus ) throws Exception {
		
		Method getMethod = null;
		Method setMethod = null;
		
		try {
			getMethod = CompanyFinancialStatus.class.getMethod("get" + field.substring(0,1).toUpperCase() + field.substring(1) , (Class[])null);
		} catch ( NoSuchMethodException nme ) {
			getMethod = CompanyFinancialStatus.class.getMethod("is" + field.substring(0,1).toUpperCase() + field.substring(1) , (Class[])null);
		}
		Class[] parameterType = new Class[1];
		parameterType[0] = getMethod.getReturnType();
		setMethod = CompanyFinancialStatus.class.getMethod("set" + field.substring(0,1).toUpperCase() + field.substring(1) , parameterType );
		
		Object[] parameterValue = new Object[1];
		parameterValue[0] = getMethod.invoke(mergedWithNewStatus ? newStatus : oldStatus , (Object[])null);
		setMethod.invoke(target,parameterValue[0]);
		
		return mergedWithNewStatus;
	}
	
	private boolean isUpdateNeeded(CompanyFinancialStatus oldStatus, CompanyFinancialStatus newStatus, String field) throws Exception {
		boolean rtn = false;
		Method targetMethod = null; 
		try {
			targetMethod = CompanyFinancialStatus.class.getMethod("get" + field.substring(0,1).toUpperCase() + field.substring(1) , (Class[])null);
		} catch ( NoSuchMethodException nme ) {
			targetMethod = CompanyFinancialStatus.class.getMethod("is" + field.substring(0,1).toUpperCase() + field.substring(1) , (Class[])null);
		}
		Object oldValue = (Object)targetMethod.invoke(oldStatus, (Object[])null);
		Object newValue = (Object)targetMethod.invoke(newStatus, (Object[])null);
		if ( oldValue instanceof Long ) {
			rtn = (((Long)newValue).longValue() != 0 ) && ( Math.abs( ((Long)oldValue).longValue() - ((Long)newValue).longValue() ) > PRECISION_THRESHOLD );
		} else if ( oldValue instanceof Float ){
			rtn = (((Float)newValue).floatValue() != (float)0.0 ) && !oldValue.equals(newValue);
		} else {
			rtn = !oldValue.equals(newValue);
		}
		return rtn;
	}
	
	private static void testIsDifferent() throws Exception {
		CompanyFinancialStatus cfs1 = new CompanyFinancialStatus();
		CompanyFinancialStatus cfs2 = new CompanyFinancialStatus();
		Company company = new Company();
		company.setName("a");
		company.setId("id");
		cfs1.setCompany(company);
		cfs2.setCompany(company);
		cfs1.setKOSPI(false);
		cfs2.setKOSPI(true);
		CompanyListUpdator updator = new CompanyListUpdator();
		System.out.println( "true is right. result[" + updator.isUpdateNeeded(cfs1, cfs2, "KOSPI") + "]" );
		cfs1.setRoa((float)0.11);
		cfs2.setRoa((float)0.11);
		System.out.println( "false is right. result[" + updator.isUpdateNeeded(cfs1, cfs2, "roa") + "]" );
	}
	
	public static void main(String[] args) throws Exception {
		testUpdateFinancialStatus();
	}
	
	public static void testUpdateFinancialStatus() {
		CompanyDao dao = null;
		CompanyListUpdator updator = new CompanyListUpdator();
		try {
			dao = new CompanyDao();
			Company company = null;
			company = dao.select("A000140", null);
			updator.updateFinancialStatus(company);
		} catch ( Exception e1 ) { 
			e1.printStackTrace();
		} finally {
		}
	}
	
	public static void testUpdateAllCompany() {
		try {
			CompanyListUpdator updator = new CompanyListUpdator();
			if ( false ) { // Wanna to update all company list? Do this code block.
				updator.getAnnualStatusList();
				updator.updateCompany();
			}
			//updator.updateFinancialStatus();
			//updator.getCompanyList();
			//updator.updateAllFinancialStatus();
			Company comp = new Company();
			CompanyDao dao = new CompanyDao();
			comp = dao.select("A000040", null);
			updator.updateFinancialStatus(comp);
			//testIsDifferent();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
}
