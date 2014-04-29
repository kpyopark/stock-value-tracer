package internetResource.companyItem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;

import common.NotNumericContentException;

import post.Company;
import post.CompanyFinancialStatus;

/**
 * @deprecated
 * @author user
 *
 */
public class ItemResource {
	
	static String ITEM_ID_URL = null;
	HttpClient client = null;
	
	static {
		ITEM_ID_URL = "http://comp.fnguide.com/SVO/Handbook_New/html/SVD_Main_XXX.htm?pGB=4";
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
		try {
			rtn = Float.parseFloat(content.replaceAll(",", ""));
		} catch ( Exception e ) {
			throw new NotNumericContentException(content + ":" + e.getMessage() );
		}
		return rtn;
	}
	
	public ArrayList<CompanyFinancialStatus> getFinancialStatus(String name, String id) throws Exception {
		ArrayList<CompanyFinancialStatus> list = new ArrayList<CompanyFinancialStatus>();
		Parser parser = null;
		boolean needAnotherConnection = true;

		try {
			//parser = new Parser("http://comp.fnguide.com/SVO/Handbook_New/xml/SVD_Main_" + id + ".htm?pGB=4");
			parser = new Parser("http://comp.fnguide.com/svo/handbook_New/xml/SVD_Main.asp?pGB=1&gicode=" + id );
			parser.setEncoding("EUC-KR");
			Filter1 filter1 = new Filter1();
			NodeList dateList = filter1.getContent(parser,"Recent Q");
			NodeList stockSizeList = filter1.getContent(parser,"�����ֽļ�(������,õ��)");
			NodeList devidenedRatioList = filter1.getContent(parser,"�����ͷ�(������,����,%)");
			NodeList theSalesList = filter1.getContent(parser,"�����(���)");
			NodeList operatingProfitList = filter1.getContent(parser,"��������(���)");
			NodeList netProfitList = filter1.getContent(parser,"��������(���)");
			NodeList roaList = filter1.getContent(parser,"ROA(%)");
			NodeList roeList = filter1.getContent(parser,"ROE(%)");
			NodeList grossCapitalList = filter1.getContent(parser,"�ں��Ѱ�(���)");
			NodeList debtList = filter1.getContent(parser,"��ä�Ѱ�(���)");
			NodeList capitalList = filter1.getContent(parser,"�ں���(���)");

			for(int cnt = 0 ; cnt < dateList.size(); cnt++ ) {
				Company companyInfo = new Company();
				companyInfo.setName(name);
				companyInfo.setId(id);
				CompanyFinancialStatus financialStatus = new CompanyFinancialStatus();
				financialStatus.setCompany(companyInfo);
				if ( cnt < 4 ) // ���� ȭ�鿡�� 4���� column���ʹ� net quarter�� ǥ�õ�
					financialStatus.setQuarter(false);
				else
					financialStatus.setQuarter(true);
				
				String strDate = dateList.elementAt(cnt).toPlainTextString();
				if ( strDate.contains("(E)") ) {// ���� �������� ��� 
					financialStatus.setFixed(false);
					strDate = strDate.replaceAll("[(]E[)]", "");
				} else if ( strDate.contains("(P)") ) {
					financialStatus.setFixed(false);
					strDate = strDate.replaceAll("[(]P[)]", "");
				} else {
					financialStatus.setFixed(true);
				}
				strDate = strDate.replaceAll("[.]","");
				strDate += "01";

				if ( !strDate.equals("01") ) { 
					financialStatus.setStandardDate(strDate);
					financialStatus.setGrossCapital(getLongValue(grossCapitalList.elementAt(cnt).toPlainTextString())*100000000);
					financialStatus.setDebt(getLongValue(debtList.elementAt(cnt).toPlainTextString())*100000000);
					financialStatus.setAssets(financialStatus.getGrossCapital() + financialStatus.getDebt());
					financialStatus.setCapital(getLongValue(capitalList.elementAt(cnt).toPlainTextString())*100000000);
					financialStatus.setDividendRatio(getFloatValue(devidenedRatioList.elementAt(cnt).toPlainTextString())/100);
					financialStatus.setNetProfit(getLongValue(netProfitList.elementAt(cnt).toPlainTextString())*100000000);
					financialStatus.setOperatingProfit(getLongValue(operatingProfitList.elementAt(cnt).toPlainTextString())*100000000);
					financialStatus.setSales(getLongValue(theSalesList.elementAt(cnt).toPlainTextString())*100000000);
					financialStatus.setOrdinarySharesSize(getLongValue(stockSizeList.elementAt(cnt).toPlainTextString())*1000);
					financialStatus.setRoa(getFloatValue(roaList.elementAt(cnt).toPlainTextString())/100);
					financialStatus.setRoe(getFloatValue(roeList.elementAt(cnt).toPlainTextString())/100);
					needAnotherConnection = financialStatus.getOrdinarySharesSize() == 0;
					list.add(financialStatus);
				}
			}
			
			if ( needAnotherConnection ) {
			// ���� �����ֿ� ���� ������ ���� ��쿡�� ������ ��ܿ��� ���ؼ� ������ �� �� �̸� ��� ������ setting�Ѵ�.
				if ( client == null )
					client = new DefaultHttpClient();
				HttpGet method = new HttpGet("http://comp.fnguide.com/SVO/Handbook_New/html/SVD_Main_" + id + ".htm?pGB=4");
				HttpResponse response = client.execute(method);
				BufferedReader br = null;
				try {
					br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
					for( String aLine = br.readLine() ; aLine != null ; aLine = br.readLine() ) {
						if ( aLine.contains("<td class=\"td_gray_left\" >�����ֽļ�(����)</td>") ) {
							aLine = br.readLine();
							int ordinaryShareSize = 0;
							try {
								ordinaryShareSize = Integer.parseInt(aLine.replaceAll("<td class=\"td_white_center\" align=\"right\" style=\"padding-right:15pt\" >","").replaceAll("</td>","").replaceAll(",","").trim());
							} catch ( Exception e3 ) {
								System.out.println("�����ֽļ� ������ ��Ȯ�ϰ� ������� �ʽ��ϴ�.");
							}
							if ( ordinaryShareSize > 0 ) {
								for ( int cnt = 0 ; cnt < list.size() ; cnt++ ) {
									list.get(cnt).setOrdinarySharesSize(ordinaryShareSize);
								}
							}
						}
					}
				} catch( Exception e1 ) {
					System.out.println("������ ������ ������ �� �� ������ �߻��Ͽ����ϴ�.");
				} finally {
					method.releaseConnection();
				}
			}
		} catch ( Exception e ) {
			System.out.println("GETTING FINANCIAL STATUS IS FAILED:" + name + "[" + id + "]" );
			//throw e;
		} finally {
			//
		}
		return list;
	}
	
	public static void main(String[] args) {
		ItemIdResource iir = new ItemIdResource();
		ItemResource ir = new ItemResource();
		try {
			String name = "��Ÿ����ũ��";
			String id = iir.getId(name);
			ArrayList<CompanyFinancialStatus> companyList = ir.getFinancialStatus(name, id);
			for ( int cnt = 0 ; cnt < companyList.size(); cnt++ ) {
				System.out.println( companyList.get(cnt) );
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

}
