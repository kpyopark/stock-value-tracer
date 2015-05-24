package analyzer;

import java.util.ArrayList;

import post.Company;
import post.InstitutionalDamand;
import dao.CompanyDao;
import dao.InstitutionalDemandDao;

public class InstitutionalDemandAnalyzer {
	
	private ArrayList<Company> companies;
	private InstitutionalDemandDao dao;
	
	public InstitutionalDemandAnalyzer() {
		try {
			CompanyDao compDao = new CompanyDao();
			companies = compDao.selectAllList();
			dao = new InstitutionalDemandDao();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	// Our purpose
	// 1. There is corelationship between accumulated demand of specific organization and closing price.
	// 2. If statement<1> is true, which stocks depend on this trend.
	// 3. Which period of statistics might show this trends well.
	
	public void analyzeCorelationships() {
		ArrayList<InstitutionalDamand> demands = null;
		for ( int position = 0 ; position < companies.size() ; position++ ) {
		}
	}
	
}
