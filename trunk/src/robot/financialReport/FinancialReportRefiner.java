package robot.financialReport;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import post.CompanyEx;
import post.CompanyFinancialStatus;
import robot.DataUpdator;
import common.PeriodUtil;
import common.StringUtil;
import dao.CompanyExDao;
import dao.CompanyFinancialStatusDao;

public class FinancialReportRefiner extends DataUpdator {

	public static void checkPeriod(CompanyEx company,List<CompanyFinancialStatus> cfsList) {
		ArrayList<String> periodList = new ArrayList<String>();
		ArrayList<String> invalidList = new ArrayList<String>();
		for(CompanyFinancialStatus cfs : cfsList) {
			if ( cfs.isQuarter() )
				if ( StringUtil.isValidDate(cfs.getStandardDate()))
					periodList.add(cfs.getStandardDate());
				else 
					invalidList.add(cfs.getStandardDate());
		}
		ArrayList<String> validList = PeriodUtil.getPeriodList(periodList);
		if ( periodList.containsAll(validList) ) {
			//
		} else {
			System.out.println("Stock which has invalid periods :" + company.getId() + ":" + company.getName());
			for (String validDate: validList ) {
				System.out.println( "\t\tValid Date:" + validDate + ":" + ( periodList.contains(validDate) ? "" : "---- need to be added ----" ));
			}
			for (String invalidDate: invalidList ) {
				System.out.println( "\t\tinvalid Date:" + invalidDate + ":" + "---- need to be deleted ----" );
			}
		}
	}
	
	public static void main(String[] args) throws SQLException {
		ArrayList<CompanyEx> companyList = null;
		CompanyExDao dao = new CompanyExDao();
		CompanyFinancialStatusDao financialDao = new CompanyFinancialStatusDao();
		String registeredDate = StringUtil.convertToStandardDate(new java.util.Date());
		companyList = dao.selectAllList(registeredDate);
		for ( CompanyEx company : companyList ) {
			ArrayList<CompanyFinancialStatus> financialStatusList = null;
			financialStatusList = financialDao.getFinancialStatus(company, registeredDate);
			checkPeriod(company, financialStatusList);
		}
	}
	
}
