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
	
	private static String removeHtmlSpaceTag(String content) {
		return (content != null) ? content.trim().replaceAll("&nbsp;", "") : null;
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
	
	static String XPATH_FICS_SECTOR = "//*[@id=\"compinfo\"]/table/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[3]/td/table/tbody/tr[2]/td[1]/span[2]";
	
	static String[][] GENERAL_REPORT_HEADERS = { 
		{ "�����(���)" ,"��������(���)", "����������(���)", "���ڼ���(���)", "SALES" }, // "SALES", "" }, 
		{ "��������(���)","��������(���)", "OPERATION_PROFIT" },
		//{ "��������(���)","OPERATION_PROFIT" }, // FOR ASSUARANCE INDUSTRY
		//{ "������������(���)", "" } ,
		{ "��������(���)", "NET_PROFIT" },
		//{"�������ֱͼ�(���)", "" },
		//{"���������ֱͼ�(���)", ""},
		{"�ڻ��Ѱ�(���)", "ASSET_TOTAL" },
		{"��ä�Ѱ�(���)" ,"DEBT_TOTAL" },
		{"�ں��Ѱ�(���)" ,"CAPITAL_TOTAL" },
		{"�ں���(���)" ,"CAPITAL" },
		{"�����ֽļ�(õ��)", "GENERAL_STOCK_SIZE" }
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
			Object[] items = financeReport.evaluateXPath(XPATH_FINANCIAL_STATUS_ITEM);
			for(int itemCount = 0; itemCount < items.length ; itemCount++ ) {
				TagNode[] childNodes = node(items[itemCount]).getChildTags();
				String header = removeHtmlSpaceTag(node(childNodes[0]).getText().toString());
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
			if ( ( baos.size() < 400 ) && ( new String( baos.toByteArray(), "euc-kr" ).indexOf("�ش� ������ ���ų� ���� ���� ������ �����ϴ�") > 0 ) ) {
				//company.set
				company.setClosed(true);
				return list;
			}
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			//System.out.println(new String(baos.toByteArray(),"euc-kr"));
			TagNode financeReport = cleaner.clean(bais, "euc-kr");
			{
				Object[] ficsInfoObjects = financeReport.evaluateXPath(XPATH_FICS_SECTOR);
				if ( ficsInfoObjects.length > 0 ) {
					String ficsInfos = node(financeReport.evaluateXPath(XPATH_FICS_SECTOR)[0]).getText().toString();
					StringTokenizer st = new StringTokenizer(removeHtmlSpaceTag(ficsInfos), ">");
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
			Object[] standardDates = financeReport.evaluateXPath(XPATH_FINANCIAL_STATUS_CATEGORY);
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
					standardDate = standardDate.substring(0,4)+standardDate.substring(5,7)+( (standardDate.substring(5,7).equals("03") || standardDate.substring(5,7).equals("12") ) ? "31" : "30" );

					CompanyFinancialStatus financeStatus = new CompanyFinancialStatus();
					financeStatus.setCompany(company);
					financeStatus.setStandardDate(standardDate);
					financeStatus.setQuarter(!isAnnual);
					financeStatus.setFixed(!isPrediction);
					list.add(financeStatus);
					columns.add(position);
				}
			}
			Object[] items = financeReport.evaluateXPath(XPATH_FINANCIAL_STATUS_ITEM);
			for(int itemCount = 0; itemCount < items.length ; itemCount++ ) {
				TagNode[] childNodes = node(items[itemCount]).getChildTags();
				String header = removeHtmlSpaceTag(node(childNodes[0]).getText().toString());
				if ( header.equals("�����(���)") || header.equals("��������(���)") || header.equals("����������(���)") || header.equals("���ڼ���(���)") ) {
					for(int position = 0 ; position < list.size() ; position++ ) {
						list.get(position).setSales(getLongValue(node(childNodes[columns.get(position)]).getText().toString()) * 100000000);
					}
				} else if ( header.equals("��������(���)") || header.equals("��������(���)") ) {
					for(int position = 0 ; position < list.size() ; position++ ) {
						list.get(position).setOperatingProfit(getLongValue(node(childNodes[columns.get(position)]).getText().toString()) * 100000000);
					}
				} else if ( header.equals("��������(���)") ) {
					for(int position = 0 ; position < list.size() ; position++ ) {
						list.get(position).setNetProfit(getLongValue(node(childNodes[columns.get(position)]).getText().toString()) * 100000000);
					}
				} else if ( header.equals("�ڻ��Ѱ�(���)") ) {
					for(int position = 0 ; position < list.size() ; position++ ) {
						list.get(position).setAssets(getLongValue(node(childNodes[columns.get(position)]).getText().toString()) * 100000000);
					}
				} else if ( header.equals("��ä�Ѱ�(���)") ) {
					for(int position = 0 ; position < list.size() ; position++ ) {
						list.get(position).setDebt(getLongValue(node(childNodes[columns.get(position)]).getText().toString()) * 100000000);
					}
				} else if ( header.equals("�ں��Ѱ�(���)") ) {
					for(int position = 0 ; position < list.size() ; position++ ) {
						list.get(position).setGrossCapital(getLongValue(node(childNodes[columns.get(position)]).getText().toString()) * 100000000);
					}
				} else if ( header.equals("�ں���(���)") ) {
					for(int position = 0 ; position < list.size() ; position++ ) {
						list.get(position).setCapital(getLongValue(node(childNodes[columns.get(position)]).getText().toString()) * 100000000);
					}
				} else if ( header.equals("��ä����(%)") ) {
					//
				} else if ( header.equals("������(%)") ) {
					//
				} else if ( header.equals("�����ֽļ�(õ��)") ) {
					for(int position = 0 ; position < list.size() ; position++ ) {
						list.get(position).setOrdinarySharesSize(getLongValue(node(childNodes[columns.get(position)]).getText().toString())* 1000);
					}
				} else if ( header.equals("ROA(%)") ) {
					for(int position = 0 ; position < list.size() ; position++ ) {
						list.get(position).setRoa(getFloatValue(node(childNodes[columns.get(position)]).getText().toString())/100);
					}
				} else if ( header.equals("ROE(%)") ) {
					for(int position = 0 ; position < list.size() ; position++ ) {
						list.get(position).setRoe(getFloatValue(node(childNodes[columns.get(position)]).getText().toString())/100);
					}
				} else if ( header.equals("�����ͷ�(%)") ) {
					for(int position = 0 ; position < list.size() ; position++ ) {
						list.get(position).setDividendRatio(getFloatValue(node(childNodes[columns.get(position)]).getText().toString())/100);
					}
				}
			}
		} catch ( Exception e ) {
			//System.out.println("GETTING FINANCIAL STATUS IS FAILED:" + name + "[" + id + "]" );
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
			Company company = dao.select("A000140", null);
			ArrayList<CompanyFinancialStatus> financialReports = ir.getFinancialStatus(company);
			for ( int cnt = 0 ; cnt < financialReports.size(); cnt++ ) {
				System.out.println( financialReports.get(cnt) );
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

}