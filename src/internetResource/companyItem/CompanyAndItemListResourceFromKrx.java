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

import common.PeriodUtil;
import common.StringUtil;
import dao.CompanyExDao;
import dao.StockDao;
import post.Company;
import post.CompanyEx;
import post.KrxItem;
import post.KrxSecurityType;
import post.Stock;

public class CompanyAndItemListResourceFromKrx {
	
	static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	static HtmlCleaner cleaner;
	static {
		cleaner = new HtmlCleaner();
	}
	static String ITEM_LIST_URL = null;
	static String[][] ITEM_LIST_PARAMS = { 
		{"market","allVal"},		// 0
		{"indxIndCd","0000"},		// 1
		{"cur_page","1"},			// 2
		{"pageSize","3000"},		// 3
		{"page_yn","N"},			// 4
		{"market_gubun","allVal"},	// 5
		{"indx_ind_cd","0000"},		// 6
		{"isu_nm",""},				// 7
		{"secugrp1","Y"},			// 8
		{"stock_gubun",""},			// 9
		{"work_dt","20140321"},		// 10
		{"isu_cd",""},				// 11
	};
	
	private static String[][] getParams(KrxSecurityType securityType, String standardDate, String id) {
		String[][] params = ITEM_LIST_PARAMS.clone();
		switch (securityType) {
		case STOCK :
			params[8][0] = "secugrp1";
			break;
		case ETF :
			params[8][0] = "secugrp2";
			break;
		case ELW :
			params[8][0] = "secugrp3";
			break;
		case ETN :
			params[8][0] = "secugrp4";
			break;
		case ETC :
			params[8][0] = "secugrp5";
			break;
		default :
			params[8][0] = "secugrp1";
			break;
		}
		params[10][1] = standardDate;
		if ( id != null ) {
			params[11][0] = "isu_cd";
			params[11][1] = id;
		} else {
			params[11][0] = "temp";
		}
		return params;
	}
	
	static private enum ColumnList {
		STOCK_ID(0), STOCK_NAME(1), CURRENT_PRICE(2), PREV_DIFF(3),
		PREV_DIFF_RATIO(4), SELL_PRICE(5), ASK_PRICE(6), VOLUME(7), VOLUME_AMOUNT(8), START_PRICE(9),
		HIGH_PRICE(10), LOW_PRICE(11), PAR_VALUE(12), CURRENCY_DIV(13), ORDINARY_SHARES(14), MARKET_CAPITALIZATION(15);
		
		private int value;
		
		private ColumnList(int insDemandColumn) {
			value = insDemandColumn;
		}
	}
	
	static {
		ITEM_LIST_URL = "http://www.krx.co.kr/por_kor/corelogic/process/m6/m6_1/m6_1_2/hpkor06001_02.xhtml?data-only=true";
	}
	
	static String XPATH_ONE_ITEM_RECORD = "//tr";

	static TagNode node(Object org) {
		return (TagNode)org;
	}	
	
	private static KrxItem getKrxItem(TagNode item) throws Exception {
		KrxItem oneItem = new KrxItem();
		try {
			TagNode[] children = item.getChildTags();
			oneItem.setId(children[ColumnList.STOCK_ID.value].getText().toString());
			oneItem.setName(children[ColumnList.STOCK_NAME.value].getText().toString());
			oneItem.setStockPrice(StringUtil.getLongValue(children[ColumnList.CURRENT_PRICE.value].getText().toString()));
			oneItem.setNetChange(StringUtil.getLongValue(children[ColumnList.PREV_DIFF.value].getText().toString()));
			oneItem.setNetChangeRatio(StringUtil.getFloatValue(children[ColumnList.PREV_DIFF_RATIO.value].getText().toString()));
			oneItem.setBid(StringUtil.getLongValue(children[ColumnList.SELL_PRICE.value].getText().toString()));
			oneItem.setAsk(StringUtil.getLongValue(children[ColumnList.ASK_PRICE.value].getText().toString()));
			oneItem.setVolume(StringUtil.getLongValue(children[ColumnList.VOLUME.value].getText().toString()));
			oneItem.setVolumnAmount(StringUtil.getLongValue(children[ColumnList.VOLUME_AMOUNT.value].getText().toString()));
			oneItem.setOpenPrice(StringUtil.getLongValue(children[ColumnList.START_PRICE.value].getText().toString()));
			oneItem.setTodayHigh(StringUtil.getLongValue(children[ColumnList.HIGH_PRICE.value].getText().toString()));
			oneItem.setTodayLow(StringUtil.getLongValue(children[ColumnList.LOW_PRICE.value].getText().toString()));
			oneItem.setParValue(StringUtil.getFloatValue(children[ColumnList.PAR_VALUE.value].getText().toString()));
			oneItem.setCurrency(children[ColumnList.CURRENCY_DIV.value].getText().toString());
			oneItem.setOrdinaryShare(StringUtil.getLongValue(children[ColumnList.ORDINARY_SHARES.value].getText().toString()));
			oneItem.setMaketCapitalization(StringUtil.getLongValue(children[ColumnList.MARKET_CAPITALIZATION.value].getText().toString()));
		} catch ( Exception e ) {
			e.printStackTrace();
			System.out.println("There is invalid items:" + oneItem.getId() + ":" + oneItem.getName() );
			throw e;
		}
		return oneItem;
	}
	
	private TagNode getRootTagNode(KrxSecurityType securityType, String standardDate, String id) {
		TagNode itemListXML = null;
		HttpURLConnection conn = null;
		OutputStream os = null;
		BufferedReader br = null;
		try {
			String[][] params = getParams(securityType, standardDate, id);
			conn = (HttpURLConnection)new URL(ITEM_LIST_URL).openConnection();
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			StringBuffer postParameter = new StringBuffer();
			for( int paramCount = 0 ; paramCount < params.length ; paramCount++ ) {
				postParameter.append("&").append(params[paramCount][0]).append("=").append(params[paramCount][1]);
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
	public ArrayList<KrxItem> getItemList(KrxSecurityType securityType, String standardDate, String id) throws Exception {
		ArrayList<KrxItem> list = new ArrayList<KrxItem>();
		try {
			Object[] tags = getRootTagNode(securityType, standardDate, id).evaluateXPath(XPATH_ONE_ITEM_RECORD);
			for ( int line = 0 ; line < tags.length ; line++ ) {
				KrxItem item = getKrxItem((TagNode)tags[line]);
				item.setStandardDate(standardDate);
				item.setSecurityType(securityType);
				list.add(item);
			}
		} catch ( Exception e ) {
			System.out.println("Security Type:" + securityType + ": Date :" + standardDate + ": ID :" + id);
			throw e;
		}
		return list;
	}
	
	public void updateSecuritySectorForEtf(String standardDate) throws Exception {
		ArrayList<KrxItem> list = new ArrayList<KrxItem>();
		try {
			Object[] tags = getRootTagNode(KrxSecurityType.ETF, standardDate, null).evaluateXPath(XPATH_ONE_ITEM_RECORD);
			for ( int line = 0 ; line < tags.length ; line++ ) {
				KrxItem item = getKrxItem((TagNode)tags[line]);
				list.add(item);
			}
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	private static boolean isKrxWorkDay(String workDay) {
		boolean contains = false;
		HttpURLConnection conn = null;
		OutputStream os = null;
		try {
			String[][] params = getParams(KrxSecurityType.STOCK, workDay, "A005930" /* Samsung Electronics */);
			conn = (HttpURLConnection)new URL(ITEM_LIST_URL).openConnection();
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			StringBuffer postParameter = new StringBuffer();
			for( int paramCount = 0 ; paramCount < params.length ; paramCount++ ) {
				postParameter.append("&").append(params[paramCount][0]).append("=").append(params[paramCount][1]);
			}
			os = conn.getOutputStream();
			os.write(postParameter.substring(1).getBytes());
			os.flush();
			os.close();
			InputStream is = conn.getInputStream();
			contains = ( is.read() != -1 );
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( conn != null ) try { conn.disconnect(); } catch ( Exception e1 ) {e1.printStackTrace();}
		}
		return contains;
	}
	
	public static void main(String[] args) {
		CompanyAndItemListResourceFromKrx webResource = new CompanyAndItemListResourceFromKrx();
		try {
			List<String> testDates = PeriodUtil.getWorkDaysForOneYear(2011,2, 8);
			for ( String standardDate : testDates ) {
				System.out.println("Test Date:" + standardDate);
				ArrayList<KrxItem> getItemList = webResource.getItemList(KrxSecurityType.STOCK, standardDate, null);
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
	}

}
