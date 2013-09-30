package common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import post.Company;
import post.CompanyFinancialStatus;

public class ItemListResource {
	
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
			while( ( aLine = br.readLine() ) != null ) {
				if ( aLine.contains("<tr height='18'><td width='5%' class='td_gray_center' align='center'>") ) {
					Company company = new Company();
					CompanyFinancialStatus financialStatus = new CompanyFinancialStatus();
					System.out.println(aLine.replaceAll("<tr height='18'><td width='5%' class='td_gray_center' align='center'>", "").replaceAll("</td>", ""));
					br.readLine();
					company.setId(br.readLine().replaceAll("<a href=.javascript.GoMenuto..4.,.", "").replaceAll("... class=.tahoma_Blue. onclick=.GoMenuto.>", ""));
					company.setName(br.readLine().replaceAll("</a></td>",""));
					financialStatus.setCompany(company);
					String date = br.readLine().replaceAll("<td width='9%' class='td_white_center' align='center'>", "").replaceAll("</td>", "");
					date += "01";
					financialStatus.setStandardDate(date);
					company.setStandardDate(date);
					financialStatus.setKOSPI(br.readLine().replaceAll("<td width='5%' class='td_white_center' align='center'><font color='FF0030'>", "").replaceAll("</font>&nbsp;&nbsp;</td>", "").trim().replaceAll("&nbsp;","").equals("°Å"));
					financialStatus.setAssets(1000000 * getLongValue(br.readLine().replaceAll("<td width='9%' class='td_white_center' align='right'>","").replaceAll("&nbsp;&nbsp;</td>","")));
					financialStatus.setCapital(1000000 * getLongValue(br.readLine().replaceAll("<td width='9%' class='td_white_center' align='right'>","").replaceAll("&nbsp;&nbsp;</td>","")));
					financialStatus.setGrossCapital(1000000 * getLongValue(br.readLine().replaceAll("<td width='9%' class='td_white_center' align='right'>","").replaceAll("&nbsp;&nbsp;</td>","")));
					financialStatus.setDebt(financialStatus.getAssets()-financialStatus.getGrossCapital());
					financialStatus.setSales(1000000 * getLongValue(br.readLine().replaceAll("<td width='9%' class='td_white_center' align='right'>","").replaceAll("&nbsp;&nbsp;</td>","")));
					financialStatus.setOperatingProfit(1000000 * getLongValue(br.readLine().replaceAll("<td width='9%' class='td_white_center' align='right'>","").replaceAll("&nbsp;&nbsp;</td>","")));
					//financialStatus.setOrdinaryProfit(1000000 * getLongValue(br.readLine().replaceAll("<td width='9%' class='td_white_center' align='right'>","").replaceAll("&nbsp;&nbsp;</td>","")));
					financialStatus.setNetProfit(1000000 * getLongValue(br.readLine().replaceAll("<td width='9%' class='td_white_center' align='right'>","").replaceAll("&nbsp;&nbsp;</td>","")));
					financialStatus.setFixed(true);
					financialStatus.setQuarter(false);
					list.add(financialStatus);
				}
			}
		} catch ( Exception e ) {
			throw e;
		} finally {
			if ( conn == null ) try { conn.disconnect(); } catch ( Exception e1 ) {e1.printStackTrace();}
			if ( br != null ) try { br.close(); } catch ( Exception e1 ) {e1.printStackTrace();}
			//else if ( is != null ) try { is.close(); } catch ( Exception e1 ) {e1.printStackTrace();}
		}
		return list;
	}
	
	public static void main(String[] args) {
		//ItemIdResource iir = new ItemIdResource();
		ItemListResource ir = new ItemListResource();
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
