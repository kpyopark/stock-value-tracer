package common;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlparser.Parser;

import post.Company;
import post.CompanyFinancialStatus;
import post.InstitutionalDamand;
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
	
	static String XPATH_ID = "//*[@id=\"analysis\"]/tbody/tr";
	
	static String XPATH_CONTENTS_LINE(int line) {
		return "//*[@id=\"analysis\"]/tbody/tr[" + line + "]/td";
	}

	private static long getLongValue(String content) throws NotNumericContentException {
		long rtn = 0;
		if ( content == null || content.trim().length() == 0 || content.trim().equals("&nbsp;") )
			return 0;
		if ( content.equals("-") )
			return 0;
		try {
			rtn = Long.parseLong(content.replaceAll(",", ""));
		} catch ( Exception e ) {
			throw new NotNumericContentException(content + ":" + e.getMessage() );
		}
		return rtn;
	}
	
	private static float getFloatValue(String content) throws NotNumericContentException {
		float rtn = 0;
		if ( content == null || content.trim().length() == 0 || content.trim().equals("&nbsp;") )
			return 0;
		if ( content.equals("-") )
			return 0;
		try {
			rtn = Float.parseFloat(content.replaceAll("%", ""));
		} catch ( Exception e ) {
			throw new NotNumericContentException(content + ":" + e.getMessage() );
		}
		return rtn;
	}
	
	public ArrayList<CompanyFinancialStatus> getFinancialStatus(String name, String id) throws Exception {
		ArrayList<CompanyFinancialStatus> list = new ArrayList<CompanyFinancialStatus>();
		Parser parser = null;
		boolean needAnotherConnection = true;

		return list;
	}
	static HtmlCleaner cleaner;
	
	static {
		cleaner = new HtmlCleaner();
	}
	
	static boolean isValidLine(Object[] dataline ) {
		return ( dataline != null )
				&& ( dataline.length > 0 )
				&& ( ((TagNode)dataline[0]).getText().toString().indexOf("/") > 0 )
				&& dataline.length >= 8;
	}
	
	static TagNode node(Object org) {
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
				insDemand.setStockClosingPrice(getLongValue(node(dataLine[1]).getText().toString()));
				insDemand.setStockUpdownRatioOfDay(getFloatValue(node(dataLine[2]).getText().toString().replaceAll("%", "")));
				insDemand.setStockUpdownPriceOfDay(getLongValue(node(dataLine[3]).getChildTags()[0].getText().toString().replaceAll("¡å", "-").replaceAll("¡ã","")));
				insDemand.setForeignerNetDemand(getLongValue(node(dataLine[4]).getText().toString()));
				insDemand.setForeignerOwnershipRatio(getFloatValue(node(dataLine[5]).getText().toString().replaceAll("%", "")));
				insDemand.setCompanyNetDemand(getLongValue(node(dataLine[6]).getText().toString()));
				insDemand.setIndividualNetDemand(getLongValue(node(dataLine[7]).getText().toString()));
				list.add(insDemand);
			}
			//System.out.println(list);
		} catch ( Exception e ) {
			e.printStackTrace();
			System.out.println("XML parsing error.");
		}
		return list;
	}
	
	public static long getLongValueFromString(String value) {
		long rtn = 0;
		try {
			value = value.replaceAll(",", "");
			if ( "-".equals(value) )
				return 0;
			rtn = Long.parseLong(value);
		} catch ( Exception e ) {
			System.out.println("Invalid number..:" + e.getMessage() );
			rtn = 0;
		}
		return rtn;
	}

	public static void main(String[] args) {
		TimeWatch timewatch = new TimeWatch();
		HttpURLConnection conn = null;
		try {
			timewatch.start();
			CompanyDao dao = new CompanyDao();
			InstitutionalDemandDao insdemandDao = new InstitutionalDemandDao();
			ArrayList<Company> companyList = dao.selectAllList();
			timewatch.reset();
			// 1. Company List
			for ( int cnt = 0 ; cnt < companyList.size() ; cnt++ ) { //companyList.size() ; cnt++ ) {
				System.out.println("start....." + cnt + ":" + companyList.size());
				for ( int pagecnt = 1 ; pagecnt <= 5 ; pagecnt++ ) {
					conn = (HttpURLConnection)new URL(ID_URL(companyList.get(cnt).getId().substring(1), pagecnt)).openConnection();
					TagNode xml = cleaner.clean(conn.getInputStream(), "euc-kr");
					ArrayList<InstitutionalDamand> insDemandList = getInstitutionalDemandList(companyList.get(cnt), xml);
					for ( int line = 0 ; line < insDemandList.size() ; line++ ) {
						insdemandDao.delete(insDemandList.get(line));
						insdemandDao.insert(insDemandList.get(line));
					}
				}
				timewatch.stopAndStart();
			}
			timewatch.stopAndStart();
		} catch ( Exception e ) {
			e.printStackTrace();
			//System.out.println(e.getLocalizedMessage());
		}
		if ( conn != null ) {
			conn.disconnect();
		}
		
	}
	
	
}
