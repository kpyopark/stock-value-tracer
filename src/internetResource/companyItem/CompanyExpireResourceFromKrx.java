package internetResource.companyItem;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import common.StringUtil;
import dao.CompanyExDao;
import dao.StockDao;
import post.Company;
import post.CompanyEx;
import post.KrxItem;
import post.Stock;

public class CompanyExpireResourceFromKrx {
	
	static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	static HtmlCleaner cleaner;
	static {
		cleaner = new HtmlCleaner();
	}
	static String ITEM_LIST_URL = null;
	static String[][] ITEM_LIST_PARAMS = { 
		{"isu_cd",""},				// 0
		{"market_gubun",""},		// 1
		{"isu_nm",""},				// 2
		{"fr_work_dt",""},			// 3
		{"to_work_dt",""},			// 4
	};
	
	static void setParams(String fromDate, String toDate) {
		ITEM_LIST_PARAMS[3][1] = fromDate;
		ITEM_LIST_PARAMS[4][1] = toDate;
	}
	
	static private enum ColumnList {
		STOCK_ID(0), STOCK_NAME(1), EXPIRE_DATE(2), REASON(3);
		
		private int value;
		
		private ColumnList(int insDemandColumn) {
			value = insDemandColumn;
		}
	}
	
	static {
		ITEM_LIST_URL = "http://www.krx.co.kr/por_kor/corelogic/process/m6/m6_1/m6_1_5/hpkor06001_05.xhtml?data-only=true";
	}
	
	static String XPATH_ONE_ITEM_RECORD = "//tr";

	static TagNode node(Object org) {
		return (TagNode)org;
	}	
	
	private static KrxItem getKrxItem(TagNode item) {
		KrxItem oneItem = new KrxItem();
		try {
			TagNode[] children = item.getChildTags();
			oneItem.setId(children[ColumnList.STOCK_ID.value].getText().toString());
			oneItem.setName(children[ColumnList.STOCK_NAME.value].getText().toString());
			oneItem.setExpireDate(children[ColumnList.EXPIRE_DATE.value].getText().toString().replaceAll("/",""));
		} catch ( Exception e ) {
			e.printStackTrace();
			System.out.println("There is invalid items:" + oneItem.getId() + ":" + oneItem.getName() );
		}
		return oneItem;
	}
	
	private TagNode getRootTagNode(String standardDate) {
		TagNode itemListXML = null;
		HttpURLConnection conn = null;
		OutputStream os = null;
		BufferedReader br = null;
		try {
			setParams(StringUtil.getLastDayOfQuarter(standardDate, -180), StringUtil.getLastDayOfQuarter(standardDate,0));
			conn = (HttpURLConnection)new URL(ITEM_LIST_URL).openConnection();
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			StringBuffer postParameter = new StringBuffer();
			for( int paramCount = 0 ; paramCount < ITEM_LIST_PARAMS.length ; paramCount++ ) {
				postParameter.append("&").append(ITEM_LIST_PARAMS[paramCount][0]).append("=").append(ITEM_LIST_PARAMS[paramCount][1]);
			}
			os = conn.getOutputStream();
			os.write(postParameter.substring(1).getBytes());
			os.flush();
			os.close();
			InputStream is = conn.getInputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int length = -1;
			byte[] buffer = new byte[4096];
			baos.write("<table>".getBytes());
			while( (length = is.read(buffer, 0, 4096)) != -1 ) {
				baos.write(buffer, 0, length);
			}
			baos.write("</table>".getBytes());
			itemListXML = cleaner.clean(new String(baos.toByteArray(), "utf-8"));
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( conn != null ) try { conn.disconnect(); } catch ( Exception e1 ) {e1.printStackTrace();}
			if ( br != null ) try { br.close(); } catch ( Exception e1 ) {e1.printStackTrace();}
			//else if ( is != null ) try { is.close(); } catch ( Exception e1 ) {e1.printStackTrace();}
		}
		return itemListXML;
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public ArrayList<KrxItem> getItemList(String standardDate) throws Exception {
		ArrayList<KrxItem> list = new ArrayList<KrxItem>();
		try {
			Object[] tags = getRootTagNode(standardDate).evaluateXPath(XPATH_ONE_ITEM_RECORD);
			for ( int line = 0 ; line < tags.length ; line++ ) {
				KrxItem item = getKrxItem((TagNode)tags[line]);
				list.add(item);
			}
		} catch ( Exception e ) {
			throw e;
		}
		return list;
	}
	
	public void updateSecuritySectorForEtf(String standardDate) throws Exception {
		ArrayList<KrxItem> list = new ArrayList<KrxItem>();
		try {
			Object[] tags = getRootTagNode(standardDate).evaluateXPath(XPATH_ONE_ITEM_RECORD);
			for ( int line = 0 ; line < tags.length ; line++ ) {
				KrxItem item = getKrxItem((TagNode)tags[line]);
				list.add(item);
			}
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public static void main(String[] args) throws Exception {
		CompanyExpireResourceFromKrx krx = new CompanyExpireResourceFromKrx();
		ArrayList<KrxItem> list = krx.getItemList(StringUtil.convertToStandardDate(new java.util.Date()));
		for ( KrxItem item: list) {
			System.out.println(item);
		}
	}

}
