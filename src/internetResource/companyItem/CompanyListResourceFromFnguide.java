package internetResource.companyItem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import post.Company;
import post.CompanyFinancialStatus;

import common.NotNumericContentException;

/**
 * @deprecated
 * @author user
 *
 */
public class CompanyListResourceFromFnguide {
	
	static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	
	static String ITEM_ID_URL = null;
	
	static {
		ITEM_ID_URL = "http://comp.fnguide.com/svo/handbook_New/xml/SVD_UJRank.asp?pGB=4&u_cd=&sort=7";
	}
	
	private static long getLongValue(String content) throws NotNumericContentException {
		long rtn = 0;
		if ( content == null || content.trim().length() == 0 || content.trim().equals("&nbsp;") )
			return 0;
		try {
			rtn = Long.parseLong(content.replaceAll(",", "").replaceAll("<font color='FF0030'>","").replaceAll("</font>",""));
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
			rtn = Float.parseFloat(content.replaceAll(",", "").replaceAll("<font color='FF0030'>","").replaceAll("</font>",""));
		} catch ( Exception e ) {
			throw new NotNumericContentException(content + ":" + e.getMessage() );
		}
		return rtn;
	}
	
	private static String getValueFieldFromLine(String aLine) {
		boolean start = false;
		boolean filterOn = false;
		int startPoint = 0;
		for ( int cnt = 0 ; cnt < aLine.length() ; cnt++ ) {
			if ( aLine.charAt(cnt) == '>' ) {
				filterOn = true;
			} else if ( filterOn & ( aLine.charAt(cnt) >= '0' && aLine.charAt(cnt) <= '9' ) ) {
				filterOn = false;
				start = true;
				startPoint = cnt;
			} else if ( start && aLine.charAt(cnt) == '<' ) {
				start = false;
				return aLine.substring(startPoint, cnt);
			}
		}
		return "0";
	}
	
	/**
	 * @return
	 * @throws Exception
	 */
	public ArrayList<CompanyFinancialStatus> getItemList() throws Exception {
		ArrayList<CompanyFinancialStatus> list = new ArrayList<CompanyFinancialStatus>();
		HttpURLConnection conn = null;
		BufferedReader br = null;
		try {
			conn = (HttpURLConnection)new URL(ITEM_ID_URL).openConnection();
			br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String aLine = null;
			int linecnt = 0;
			while( ( aLine = br.readLine() ) != null ) {
				System.out.println(linecnt++ + ":" + aLine);
				if ( aLine.contains("<td class='tdcon_lef_r b tdtit'>") ) {
					Company company = new Company();
					CompanyFinancialStatus financialStatus = new CompanyFinancialStatus();
					company.setId(br.readLine().trim().replaceAll("<a href=\"javascript:GoMenuto[(]'.','","").substring(0,7));
					System.out.println(company.getId());
					company.setName(br.readLine().trim().replaceAll("</a></td>",""));
					System.out.println(company.getName());
					financialStatus.setCompany(company);
					String date = br.readLine().trim().replaceAll("<td class=\"tdcon_cen_r\">", "").replaceAll("</td>", "");
					date += "01";
					financialStatus.setStandardDate(date);
					System.out.println(company.getStandardDate());
					company.setStandardDate(date);
					financialStatus.setKOSPI(br.readLine().indexOf("À¯") >= 0);
					System.out.println(financialStatus.isKOSPI());
					financialStatus.setAssets(1000000 * getLongValue(getValueFieldFromLine(br.readLine())));
					System.out.println(financialStatus.getAssets());
					financialStatus.setCapital(1000000 * getLongValue(getValueFieldFromLine(br.readLine())));
					System.out.println(financialStatus.getCapital());
					financialStatus.setGrossCapital(1000000 * getLongValue(getValueFieldFromLine(br.readLine())));
					System.out.println(financialStatus.getGrossCapital());
					financialStatus.setSales(1000000 * getLongValue(getValueFieldFromLine(br.readLine())));
					System.out.println(financialStatus.getSales());
					financialStatus.setOperatingProfit(1000000 * getLongValue(getValueFieldFromLine(br.readLine())));
					System.out.println(financialStatus.getOperatingProfit());
					financialStatus.setNetProfit(1000000 * getLongValue(getValueFieldFromLine(br.readLine())));
					System.out.println(financialStatus.getNetProfit());
					financialStatus.setFixed(true);
					financialStatus.setQuarter(false);
					list.add(financialStatus);
				}
			}
		} catch ( Exception e ) {
			throw e;
		} finally {
			if ( conn != null ) try { conn.disconnect(); } catch ( Exception e1 ) {e1.printStackTrace();}
			if ( br != null ) try { br.close(); } catch ( Exception e1 ) {e1.printStackTrace();}
			//else if ( is != null ) try { is.close(); } catch ( Exception e1 ) {e1.printStackTrace();}
		}
		return list;
	}
	
	public static void main2(String[] args) {
		System.out.println(getValueFieldFromLine("<td class=\"tdcon_rig_r\">10</td>"));
	}
	
	public static void main(String[] args) {
		//ItemIdResource iir = new ItemIdResource();
		CompanyListResourceFromFnguide ir = new CompanyListResourceFromFnguide();
		try {
			ArrayList<CompanyFinancialStatus> companyList = ir.getItemList();
			for ( int cnt = 0 ; cnt < companyList.size(); cnt++ ) {
				System.out.println( companyList.get(cnt) );
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

}
