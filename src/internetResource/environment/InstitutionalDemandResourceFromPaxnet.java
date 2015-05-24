package internetResource.environment;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import post.Company;
import post.InstitutionalDamand;

import common.StringUtil;
import common.TimeWatch;

import dao.CompanyDao;
import dao.InstitutionalDemandDao;

public class InstitutionalDemandResourceFromPaxnet {
	public static String ID_URL;
	
	static String ID_URL(String companyId, int page) {
		//return "http://paxnet.moneta.co.kr/stock/stockIntro/stockDimAnalysis/supDmdAnalysis01.jsp?code=" + companyId + "&wlog_pip=T_supDmdAnalysis01";
		return "http://paxnet.moneta.co.kr/stock/stockIntro/stockDimAnalysis/supDmdAnalysis01_reload.jsp?code=" + companyId + "&page=" + page;
	}
	
	static private enum InsDemandColumnType {
		STANDARD_DATE(1), CLOSING_PRICE(2), UPDOWN_RATIO(3), UPDOWN_VALUE(4), FOREIGNER_NET_DEMAND(5), FOREIGNER_OWNERSHIP_RATIO(6), COMPANY_NET_DEMAND(7), INDIVIDUAL_NET_DEMAND(8);
		
		private int column;
		
		private InsDemandColumnType(int insDemandColumn) {
			column = insDemandColumn;
		}
		
		public int getColumn() {
			return column;
		}
	}
	
	private static String XPATH_ID = "//*[@id=\"analysis\"]/tbody/tr";
	private static String XPATH_CONTENTS_LINE(int line) {
		return "//*[@id=\"analysis\"]/tbody/tr[" + line + "]/td";
	}

	private static boolean isValidLine(Object[] dataline ) {
		return ( dataline != null )
				&& ( dataline.length > 0 )
				&& ( ((TagNode)dataline[0]).getText().toString().indexOf("/") > 0 )
				&& dataline.length >= 8;
	}
	
	private static TagNode node(Object org) {
		return (TagNode)org;
	}
	
	static ArrayList<InstitutionalDamand> getInstitutionalDemandList(Company company, TagNode companyXML) {
		ArrayList<InstitutionalDamand> list = new ArrayList<InstitutionalDamand>();
		try {
			Object[] tags = companyXML.evaluateXPath(XPATH_ID);
			for ( int line = 1 ; line <= tags.length ; line++ ) {
				Object[] dataLine = (Object[])companyXML.evaluateXPath(XPATH_CONTENTS_LINE(line));
				if ( !isValidLine(dataLine) ) {
					continue;
				}
				InstitutionalDamand insDemand = new InstitutionalDamand();
				insDemand.setCompany(company);
				insDemand.setStandardDate(node(dataLine[0]).getText().toString().replaceAll("/",""));
				insDemand.setStandardTime("150000");
				insDemand.setStockClosingPrice(StringUtil.getLongValue(node(dataLine[1]).getText().toString()));
				insDemand.setStockUpdownRatioOfDay(StringUtil.getFloatValue(node(dataLine[2]).getText().toString().replaceAll("%", "")));
				insDemand.setStockUpdownPriceOfDay(StringUtil.getLongValue(node(dataLine[3]).getChildTags()[0].getText().toString().replaceAll("¡å", "-").replaceAll("¡ã","")));
				insDemand.setForeignerNetDemand(StringUtil.getLongValue(node(dataLine[4]).getText().toString()));
				insDemand.setForeignerOwnershipRatio(StringUtil.getFloatValue(node(dataLine[5]).getText().toString().replaceAll("%", "")));
				insDemand.setCompanyNetDemand(StringUtil.getLongValue(node(dataLine[6]).getText().toString()));
				insDemand.setIndividualNetDemand(StringUtil.getLongValue(node(dataLine[7]).getText().toString()));
				list.add(insDemand);
			}
			//System.out.println(list);
		} catch ( Exception e ) {
			e.printStackTrace();
			System.out.println("XML parsing error.");
		}
		return list;
	}
	
	static class InsdemandThread implements Runnable {
		Company company;
		InstitutionalDemandDao insDao;
		HttpURLConnection conn;
		int cnt;
		HtmlCleaner cleaner;
		InsdemandThread(int cnt, Company company, InstitutionalDemandDao insDao) {
			InsdemandThread.this.cnt = cnt;
			InsdemandThread.this.company = company;
			InsdemandThread.this.insDao = insDao;
			cleaner = new HtmlCleaner();
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				for ( int pagecnt = 1 ; pagecnt <= 1 ; pagecnt++ ) {
					System.out.println("[" + cnt + "][" + company.getId() + "][" + company.getName() + "] start... ");
					conn = (HttpURLConnection)new URL(ID_URL(company.getId().substring(1), pagecnt)).openConnection();
					TagNode xml = cleaner.clean(conn.getInputStream(), "euc-kr");
					ArrayList<InstitutionalDamand> insDemandList = getInstitutionalDemandList(company, xml);
					System.out.println("[" + cnt + "][" + company.getId() + "][" + company.getName() + "] start to delete and insert " + insDemandList.size());
					for ( int line = 0 ; line < Math.min(insDemandList.size(), 10) ; line++ ) { //insDemandList.size() ; line++ ) {
						insDao.replace(insDemandList.get(line));
					}
				}
			} catch ( Exception innere ) {
				innere.printStackTrace();
			}
			if ( conn != null ) {
				conn.disconnect();
			}
			
		}
	}
	
	public static void main(String[] args) {
		TimeWatch timewatch = new TimeWatch();
		ExecutorService threadPool = Executors.newFixedThreadPool(1);
		try {
			timewatch.start();
			final CompanyDao dao = new CompanyDao();
			final InstitutionalDemandDao insdemandDao = new InstitutionalDemandDao();
			final ArrayList<Company> companyList = dao.selectAllList();
			//final ArrayList<Company> companyList = new ArrayList<Company>();
			//Company testCompany = new Company();
			//testCompany.setId("A009070");
			//testCompany.setName("KCTC");
			//companyList.add(testCompany);
			timewatch.reset();
			// 1. Company List
			for ( int cnt = 0 ; cnt < companyList.size() ; cnt++ ) { //companyList.size() ; cnt++ ) {
				System.out.println("start....." + cnt + ":" + companyList.size());
				threadPool.execute(new InsdemandThread(cnt, companyList.get(cnt), insdemandDao));
			}
			timewatch.stopAndStart();
		} catch ( Exception e ) {
			e.printStackTrace();
			//System.out.println(e.getLocalizedMessage());
		}
		
	}
	
	
}
