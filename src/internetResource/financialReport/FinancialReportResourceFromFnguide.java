package internetResource.financialReport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.http.client.HttpClient;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import common.NotNumericContentException;
import common.StringUtil;

import post.Company;
import post.CompanyFinancialStatus;
import dao.CompanyDao;

public class FinancialReportResourceFromFnguide {
	
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
	
	static String XPATH_FINANCIAL_STATUS_CATEGORY_CONSOLIDATED = "//*[@id=\"fhTheadD\"]/tr/th";
	static String XPATH_FINANCIAL_STATUS_ITEM_CONSOLIDATED = "//*[@id=\"fhTbodyD\"]/tr";
	static String XPATH_FINANCIAL_STATUS_CATEGORY_STANDALONE = "//*[@id=\"fhTheadB\"]/tr/th";
	static String XPATH_FINANCIAL_STATUS_ITEM_STANDALONE = "//*[@id=\"fhTbodyB\"]/tr";
	static String XPATH_FINANCIAL_STATUS_ITEM_VALUES(int row) {
		// in java. index base is 0
		// but in xpath, index base is 1.
		return "//*[@id=\"fhTbodyD\"]/tr[" + (row + 1) + "]/th";
	}

	static String XPATH_FICS_SECTOR = "//*[@id=\"compinfo\"]/table/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[3]/td/table/tbody/tr[2]/td[1]/span[2]";
	
	static String[][] GENERAL_REPORT_HEADERS = { 
		{ "매출액(억원)" ,"보험료수익(억원)", "순영업수익(억원)", "이자수익(억원)", "SALES" }, // "SALES", "" }, 
		{ "영업이익(억원)","영업손익(억원)", "OPERATION_PROFIT" },
		//{ "영업손익(억원)","OPERATION_PROFIT" }, // FOR ASSUARANCE INDUSTRY
		//{ "조정영업이익(억원)", "" } ,
		{ "당기순이익(억원)", "NET_PROFIT" },
		//{"지배주주귀속(억원)", "" },
		//{"비지배주주귀속(억원)", ""},
		{"자산총계(억원)", "ASSET_TOTAL" },
		{"부채총계(억원)" ,"DEBT_TOTAL" },
		{"자본총계(억원)" ,"CAPITAL_TOTAL" },
		{"자본금(억원)" ,"CAPITAL" },
		{"발행주식수(천주)", "GENERAL_STOCK_SIZE" }
	};
	
	public boolean checkSpecialGeneralFinancialReport(Company company) throws Exception {
		boolean hasGeneralReport = true;
		HttpURLConnection conn = null;
		boolean[] existHeaders = new boolean[GENERAL_REPORT_HEADERS.length];
		ArrayList<String> headers = new ArrayList<String>();
		for ( int tempCnt = 0 ; tempCnt < existHeaders.length; tempCnt++ ) existHeaders[tempCnt] = false;
		try {
			conn = (HttpURLConnection)new URL(ITEM_ID_URL(company.getId())).openConnection();
			TagNode financeReport = cleaner.clean(conn.getInputStream(), "euc-kr");
			Object[] items = financeReport.evaluateXPath(XPATH_FINANCIAL_STATUS_ITEM_CONSOLIDATED);
			for(int itemCount = 0; itemCount < items.length ; itemCount++ ) {
				TagNode[] childNodes = node(items[itemCount]).getChildTags();
				String header = StringUtil.removeHtmlSpaceTag(node(childNodes[0]).getText().toString());
				headers.add(header);
				for ( int headerPos = 0 ; headerPos < GENERAL_REPORT_HEADERS.length ; headerPos++ ) {
					for ( int subPos = 0 ; !existHeaders[headerPos] && subPos < GENERAL_REPORT_HEADERS[headerPos].length ; subPos++ ) {
						if (GENERAL_REPORT_HEADERS[headerPos][subPos].equals(header)) {
							existHeaders[headerPos] = true;
							break;
						}
					}
				}
			}
		} finally {
			if ( conn != null ) try { conn.disconnect(); } catch ( Exception e1 ) { e1.printStackTrace(); }
		}
		for( int checkCnt = 0 ; checkCnt < existHeaders.length; checkCnt++ ) hasGeneralReport = hasGeneralReport && existHeaders[checkCnt]; 
		if ( !hasGeneralReport )
			for( int printCnt = 0 ; printCnt < headers.size() ; printCnt++ )
				System.out.println(headers.get(printCnt));
		return !hasGeneralReport;
	}
	
	public ArrayList<CompanyFinancialStatus> getFinancialStatus(Company company) throws Exception {

		ArrayList<CompanyFinancialStatus> list = new ArrayList<CompanyFinancialStatus>();
		HttpURLConnection conn = null;
		ArrayList<Integer> columns = new ArrayList<Integer>();

		try {
			conn = (HttpURLConnection)new URL(ITEM_ID_URL(company.getId())).openConnection();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte buffer[] = new byte[4096];
			int length = -1;
			while( (length = conn.getInputStream().read(buffer, 0, 4096)) != -1 ) {
				baos.write(buffer, 0, length);
			}
			if ( ( baos.size() < 400 ) && ( new String( baos.toByteArray(), "euc-kr" ).indexOf("해당 종목이 없거나 종목에 대한 정보가 없습니다") > 0 ) ) {
				//company.set
				company.setClosed(true);
				return list;
			}
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			String htmlText = new String(baos.toByteArray(), "euc-kr");

			boolean isConsolidated = htmlText.indexOf("input[value=\"B\"]") < 0;
			
			TagNode financeReport = cleaner.clean(bais, "euc-kr");
			{
				Object[] ficsInfoObjects = financeReport.evaluateXPath(XPATH_FICS_SECTOR);
				if ( ficsInfoObjects.length > 0 ) {
					String ficsInfos = node(financeReport.evaluateXPath(XPATH_FICS_SECTOR)[0]).getText().toString();
					StringTokenizer st = new StringTokenizer(StringUtil.removeHtmlSpaceTag(ficsInfos), ">");
					if ( st.countTokens() == 3 ) {
						String ficsSector = st.nextToken().trim();
						String ficsIndustryGroup = st.nextToken().trim();
						String ficsIndustry = st.nextToken().trim();
						company.setFicsSector(ficsSector);
						company.setFicsIndustryGroup(ficsIndustryGroup);
						company.setFicsIndustry(ficsIndustry);
					}
				}
			}
			
			Object[] standardDates = financeReport.evaluateXPath(isConsolidated ? XPATH_FINANCIAL_STATUS_CATEGORY_CONSOLIDATED : XPATH_FINANCIAL_STATUS_CATEGORY_STANDALONE);
			for(int position=1; position < standardDates.length; position++) {
				if ( node(standardDates[position]).getText().toString().indexOf("Annual") >= 0 ||
						node(standardDates[position]).getText().toString().indexOf("Net Quarter") >= 0 ) {
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
					standardDate = standardDate.substring(0,4)+standardDate.substring(5,7)+ "01";
					standardDate = StringUtil.getLastDayOfMonth(standardDate);

					CompanyFinancialStatus financeStatus = new CompanyFinancialStatus();
					financeStatus.setCompany(company);
					financeStatus.setStandardDate(standardDate);
					financeStatus.setQuarter(!isAnnual);
					financeStatus.setFixed(!isPrediction);
					list.add(financeStatus);
					columns.add(position);
				}
			}
			Object[] items = financeReport.evaluateXPath(isConsolidated ? XPATH_FINANCIAL_STATUS_ITEM_CONSOLIDATED : XPATH_FINANCIAL_STATUS_ITEM_STANDALONE);
			for(int itemCount = 0; itemCount < items.length ; itemCount++ ) {
				TagNode[] childNodes = node(items[itemCount]).getChildTags();
				String header = StringUtil.removeHtmlSpaceTag(node(childNodes[0]).getText().toString());
				if ( header.equals("매출액(억원)") || header.equals("보험료수익(억원)") || header.equals("순영업수익(억원)") || header.equals("이자수익(억원)") || header.equals("영업수익(억원)") ) {
					for(int position = 0 ; position < list.size() ; position++ ) {
						list.get(position).setSales(StringUtil.getLongValue(node(childNodes[columns.get(position)]).getText().toString()) * 100000000);
					}
				} else if ( header.equals("영업이익(억원)") || header.equals("영업손익(억원)") ) {
					for(int position = 0 ; position < list.size() ; position++ ) {
						list.get(position).setOperatingProfit(StringUtil.getLongValue(node(childNodes[columns.get(position)]).getText().toString()) * 100000000);
					}
				} else if ( header.equals("당기순이익(억원)") ) {
					for(int position = 0 ; position < list.size() ; position++ ) {
						list.get(position).setNetProfit(StringUtil.getLongValue(node(childNodes[columns.get(position)]).getText().toString()) * 100000000);
					}
				} else if ( header.equals("자산총계(억원)") ) {
					for(int position = 0 ; position < list.size() ; position++ ) {
						list.get(position).setAssets(StringUtil.getLongValue(node(childNodes[columns.get(position)]).getText().toString()) * 100000000);
					}
				} else if ( header.equals("부채총계(억원)") ) {
					for(int position = 0 ; position < list.size() ; position++ ) {
						list.get(position).setDebt(StringUtil.getLongValue(node(childNodes[columns.get(position)]).getText().toString()) * 100000000);
					}
				} else if ( header.equals("자본총계(억원)") ) {
					for(int position = 0 ; position < list.size() ; position++ ) {
						list.get(position).setGrossCapital(StringUtil.getLongValue(node(childNodes[columns.get(position)]).getText().toString()) * 100000000);
					}
				} else if ( header.equals("자본금(억원)") ) {
					for(int position = 0 ; position < list.size() ; position++ ) {
						list.get(position).setCapital(StringUtil.getLongValue(node(childNodes[columns.get(position)]).getText().toString()) * 100000000);
					}
				} else if ( header.equals("부채비율(%)") ) {
					//
				} else if ( header.equals("유보율(%)") ) {
					//
				} else if ( header.equals("발행주식수(천주)") ) {
					for(int position = 0 ; position < list.size() ; position++ ) {
						list.get(position).setOrdinarySharesSize(StringUtil.getLongValue(node(childNodes[columns.get(position)]).getText().toString())* 1000);
					}
				} else if ( header.equals("ROA(%)") ) {
					for(int position = 0 ; position < list.size() ; position++ ) {
						list.get(position).setRoa(StringUtil.getFloatValue(node(childNodes[columns.get(position)]).getText().toString())/100);
					}
				} else if ( header.equals("ROE(%)") ) {
					for(int position = 0 ; position < list.size() ; position++ ) {
						list.get(position).setRoe(StringUtil.getFloatValue(node(childNodes[columns.get(position)]).getText().toString())/100);
					}
				} else if ( header.equals("배당수익률(%)") ) {
					for(int position = 0 ; position < list.size() ; position++ ) {
						list.get(position).setDividendRatio(StringUtil.getFloatValue(node(childNodes[columns.get(position)]).getText().toString())/100);
					}
				}
			}
		} catch ( Exception e ) {
			System.out.println("GETTING FINANCIAL STATUS IS FAILED:" + company.getName() + "[" + company.getId() + "]" );
			//throw e;
			e.printStackTrace();
		} finally {
			//
			conn.disconnect();
		}
		return list;
	}
	
	public static void main(String[] args) throws Exception {
		testFinancialStatusAPI();
	}
	
	public static void testCheckSpecialGeneralFinancialReport() {
		CompanyDao dao = null;
		FinancialReportResourceFromFnguide ir = new FinancialReportResourceFromFnguide();
		try {
			dao = new CompanyDao();
			ArrayList<Company> companies = dao.selectAllList();
			for( int cnt = 258 ; cnt < companies.size() ; cnt++) {
				System.out.println("--------- COMPANY[" + companies.get(cnt).getName() + "][" + companies.get(cnt).getId() + "][" + cnt + "/" + companies.size() + "]-------------");
				ir.checkSpecialGeneralFinancialReport(companies.get(cnt));
			}
		} catch ( Exception e1 ) {
			e1.printStackTrace();
		} finally {
		}
	}

	public static void testFinancialStatusAPI() {
		FinancialReportResourceFromFnguide ir = new FinancialReportResourceFromFnguide();
		CompanyDao dao = new CompanyDao();
		try {
			Company company = dao.select("A017940", null);
			ArrayList<CompanyFinancialStatus> financialReports = ir.getFinancialStatus(company);
			for ( int cnt = 0 ; cnt < financialReports.size(); cnt++ ) {
				System.out.println( financialReports.get(cnt) );
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

}
