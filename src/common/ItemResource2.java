package common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;

import dao.CompanyDao;

import post.Company;
import post.CompanyFinancialStatus;
import post.InstitutionalDamand;

public class ItemResource2 {
	
	HttpClient client = null;
	static HtmlCleaner cleaner;
	
	static {
		cleaner = new HtmlCleaner();
	}
	
	static String ITEM_ID_URL(String id) {
		return "http://comp.fnguide.com/svo/handbook_New/xml/SVD_Main.asp?pGB=1&gicode=" + id;
	}
	
	static TagNode node(Object org) {
		return (TagNode)org;
	}
	
	private static long getLongValue(String content) throws NotNumericContentException {
		long rtn = 0;
		if ( content == null || content.trim().length() == 0 || content.trim().equals("&nbsp;") )
			return 0;
		try {
			rtn = Long.parseLong(content.replaceAll(",", ""));
		} catch ( Exception e ) {
			throw new NotNumericContentException(content + ":" + e.getMessage() );
		}
		return rtn;
	}
	
	private static float getFloatValue(String content) throws NotNumericContentException {
		float rtn = (float)0.0;
		if ( content == null || content.trim().length() == 0 || content.trim().equals("&nbsp;") )
			return 0;
		if ( content.equals("N/A(IFRS)") )
			return 0;
		try {
			rtn = Float.parseFloat(content.replaceAll(",", ""));
		} catch ( Exception e ) {
			throw new NotNumericContentException(content + ":" + e.getMessage() );
		}
		return rtn;
	}
	
	static String XPATH_FINANCIAL_STATUS_CATEGORY = "//*[@id=\"fhTheadD\"]/tr/th";
	static String XPATH_FINANCIAL_STATUS_ITEM = "//*[@id=\"fhTbodyD\"]/tr";
	static String XPATH_FINANCIAL_STATUS_ITEM_VALUES(int row) {
		// in java. index base is 0
		// but in xpath, index base is 1.
		return "//*[@id=\"fhTbodyD\"]/tr[" + (row + 1) + "]/th";
	}
	
	public ArrayList<CompanyFinancialStatus> getFinancialStatus(Company company) throws Exception {
		ArrayList<CompanyFinancialStatus> list = new ArrayList<CompanyFinancialStatus>();
		HttpURLConnection conn = null;

		try {
			conn = (HttpURLConnection)new URL(ITEM_ID_URL(company.getId())).openConnection();
			TagNode financeReport = cleaner.clean(conn.getInputStream(), "euc-kr");
			Object[] standardDates = financeReport.evaluateXPath(XPATH_FINANCIAL_STATUS_CATEGORY);
			for(int position=1; position < standardDates.length; position++) {
				boolean isAnnual = node(standardDates[position]).getText().toString().indexOf("Annual") >= 0;
				String standardDate = "";
				if ( isAnnual ) 
					standardDate = node(standardDates[position]).getText().toString().replace("Annual", "");
				else
					standardDate = node(standardDates[position]).getText().toString().replace("Net Quarter", "");
				boolean isIFRS = standardDate.indexOf("IFRS") >= 0;
				if ( isIFRS )
					standardDate = standardDate.substring(0,standardDate.indexOf("IFRS"));
				else
					standardDate = standardDate.substring(0,standardDate.indexOf("GAAP"));
				boolean isPrediction = standardDate.indexOf("(E)") >= 0;
				standardDate = standardDate.substring(0,4)+standardDate.substring(5,7)+( (standardDate.substring(5,7).equals("03") || standardDate.substring(5,7).equals("12") ) ? "31" : "30" );

				CompanyFinancialStatus financeStatus = new CompanyFinancialStatus();
				financeStatus.setCompany(company);
				financeStatus.setStandardDate(standardDate);
				financeStatus.setQuarter(!isAnnual);
				list.add(financeStatus);
			}
			Object[] items = financeReport.evaluateXPath(XPATH_FINANCIAL_STATUS_ITEM);
			for(int itemCount = 0; itemCount < items.length ; itemCount++ ) {
				TagNode[] childNodes = node(items[itemCount]).getChildTags();
				if ( node(childNodes[0]).getText().toString().indexOf("매출액") >= 0 ) {
					for(int count = 1; count< childNodes.length ; count++) {
						list.get(count-1).setSales(getLongValue(node(childNodes[count]).getText().toString()) * 100000000);
					}
				} else if ( node(childNodes[0]).getText().toString().indexOf("영업이익률") >= 0 ) {
					//
				} else if ( node(childNodes[0]).getText().toString().indexOf("영업이익") >= 0 ) {
					for(int count = 1; count< childNodes.length ; count++) {
						list.get(count-1).setOperatingProfit(getLongValue(node(childNodes[count]).getText().toString()) * 100000000);
					}
				} else if ( node(childNodes[0]).getText().toString().indexOf("조정영업이익") >= 0 ) {
					for(int count = 1; count< childNodes.length ; count++) {
						list.get(count-1).setOperatingProfit(getLongValue(node(childNodes[count]).getText().toString()) * 100000000);
					}
				} else if ( node(childNodes[0]).getText().toString().indexOf("당기순이익") >= 0 ) {
					for(int count = 1; count< childNodes.length ; count++) {
						list.get(count-1).setNetProfit(getLongValue(node(childNodes[count]).getText().toString()) * 100000000);
					}
				} else if ( node(childNodes[0]).getText().toString().indexOf("자산총계") >= 0 ) {
					for(int count = 1; count< childNodes.length ; count++) {
						list.get(count-1).setAssets(getLongValue(node(childNodes[count]).getText().toString()) * 100000000);
					}
				} else if ( node(childNodes[0]).getText().toString().indexOf("부채총계") >= 0 ) {
					for(int count = 1; count< childNodes.length ; count++) {
						list.get(count-1).setDebt(getLongValue(node(childNodes[count]).getText().toString()) * 100000000);
					}
				} else if ( node(childNodes[0]).getText().toString().indexOf("자본총계") >= 0 ) {
					for(int count = 1; count< childNodes.length ; count++) {
						list.get(count-1).setGrossCapital(getLongValue(node(childNodes[count]).getText().toString()) * 100000000);
					}
				} else if ( node(childNodes[0]).getText().toString().indexOf("자본금") >= 0 ) {
					for(int count = 1; count< childNodes.length ; count++) {
						list.get(count-1).setCapital(getLongValue(node(childNodes[count]).getText().toString()) * 100000000);
					}
				} else if ( node(childNodes[0]).getText().toString().indexOf("부채비율") >= 0 ) {
					//
				} else if ( node(childNodes[0]).getText().toString().indexOf("유보율") >= 0 ) {
					//
				} else if ( node(childNodes[0]).getText().toString().indexOf("발행주식수") >= 0 ) {
					for(int count = 1; count< childNodes.length ; count++) {
						list.get(count-1).setOrdinarySharesSize(getLongValue(node(childNodes[count]).getText().toString())* 1000);
					}
				} else if ( node(childNodes[0]).getText().toString().indexOf("ROA") >= 0 ) {
					for(int count = 1; count< childNodes.length ; count++) {
						list.get(count-1).setRoa(getFloatValue(node(childNodes[count]).getText().toString()));
					}
				} else if ( node(childNodes[0]).getText().toString().indexOf("ROE") >= 0 ) {
					for(int count = 1; count< childNodes.length ; count++) {
						list.get(count-1).setRoe(getFloatValue(node(childNodes[count]).getText().toString()));
					}
				} else if ( node(childNodes[0]).getText().toString().indexOf("배당수익률") >= 0 ) {
					for(int count = 1; count< childNodes.length ; count++) {
						list.get(count-1).setDividendRatio(getFloatValue(node(childNodes[count]).getText().toString()));
					}
				}
			}
		} catch ( Exception e ) {
			//System.out.println("GETTING FINANCIAL STATUS IS FAILED:" + name + "[" + id + "]" );
			//throw e;
			e.printStackTrace();
		} finally {
			//
		}
		return list;
	}
	
	public static void main(String[] args) {
		ItemResource2 ir = new ItemResource2();
		CompanyDao dao = new CompanyDao();
		try {
			Company company = dao.select("A105560", null);
			ArrayList<CompanyFinancialStatus> financialReports = ir.getFinancialStatus(company);
			for ( int cnt = 0 ; cnt < financialReports.size(); cnt++ ) {
				System.out.println( financialReports.get(cnt) );
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

}
