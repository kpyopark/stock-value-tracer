package internetResource.environment;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlparser.Parser;

import common.StringUtil;
import common.TimeWatch;

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
	
	private static String XPATH_ID = "//*[@id=\"analysis\"]/tbody/tr";
	
	private static String XPATH_CONTENTS_LINE(int line) {
		return "//*[@id=\"analysis\"]/tbody/tr[" + line + "]/td";
	}

	private static HtmlCleaner cleaner;
	
	static {
		cleaner = new HtmlCleaner();
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
				try {
					for ( int pagecnt = 1 ; pagecnt <= 1 ; pagecnt++ ) {
						conn = (HttpURLConnection)new URL(ID_URL(companyList.get(cnt).getId().substring(1), pagecnt)).openConnection();
						TagNode xml = cleaner.clean(conn.getInputStream(), "euc-kr");
						ArrayList<InstitutionalDamand> insDemandList = getInstitutionalDemandList(companyList.get(cnt), xml);
						for ( int line = 0 ; line < 20 ; line++ ) { //insDemandList.size() ; line++ ) {
							insdemandDao.delete(insDemandList.get(line));
							insdemandDao.insert(insDemandList.get(line));
						}
					}
				} catch ( Exception innere ) {
					innere.printStackTrace();
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
