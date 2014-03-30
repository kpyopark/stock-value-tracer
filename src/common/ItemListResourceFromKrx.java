package common;

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

import dao.CompanyExDao;
import dao.StockDao;

import post.Company;
import post.CompanyEx;
import post.KrxItem;
import post.Stock;

public class ItemListResourceFromKrx {
	
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
	
	static void setParams(int securityType, String standardDate, String id) {
		switch (securityType) {
		case 0 :
			ITEM_LIST_PARAMS[8][0] = "secugrp1";
			break;
		case 1 :
			ITEM_LIST_PARAMS[8][0] = "secugrp2";
			break;
		case 2 :
			ITEM_LIST_PARAMS[8][0] = "secugrp3";
			break;
		case 3 :
			ITEM_LIST_PARAMS[8][0] = "secugrp4";
			break;
		default :
			ITEM_LIST_PARAMS[8][0] = "secugrp1";
			break;
		}
		ITEM_LIST_PARAMS[10][1] = standardDate;
		if ( id != null ) {
			ITEM_LIST_PARAMS[11][0] = "isu_cd";
			ITEM_LIST_PARAMS[11][1] = id;
		} else {
			ITEM_LIST_PARAMS[11][0] = "temp";
		}
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
	
	private static KrxItem getKrxItem(TagNode item) {
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
		}
		return oneItem;
	}
	
	private TagNode getRootTagNode(int securityType, String standardDate, String id) {
		TagNode itemListXML = null;
		HttpURLConnection conn = null;
		OutputStream os = null;
		BufferedReader br = null;
		try {
			setParams(securityType, standardDate, id);
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
	public ArrayList<KrxItem> getItemList(int securityType, String standardDate, String id) throws Exception {
		ArrayList<KrxItem> list = new ArrayList<KrxItem>();
		try {
			Object[] tags = getRootTagNode(securityType, standardDate, id).evaluateXPath(XPATH_ONE_ITEM_RECORD);
			for ( int line = 0 ; line < tags.length ; line++ ) {
				KrxItem item = getKrxItem((TagNode)tags[line]);
				list.add(item);
			}
		} catch ( Exception e ) {
			throw e;
		}
		return list;
	}
	
	private static String getTodayDate() {
		return new SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
	}
	
	public void updateSecuritySectorForEtf(String standardDate) throws Exception {
		ArrayList<KrxItem> list = new ArrayList<KrxItem>();
		try {
			Object[] tags = getRootTagNode(1, standardDate, null).evaluateXPath(XPATH_ONE_ITEM_RECORD);
			for ( int line = 0 ; line < tags.length ; line++ ) {
				KrxItem item = getKrxItem((TagNode)tags[line]);
				list.add(item);
			}
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public static void main(String[] args) {
		ItemListResourceFromKrx ir = new ItemListResourceFromKrx();
		CompanyExDao dao = new CompanyExDao();
		List<String> workDays = new ArrayList<String>();
		// To last year.
		for( int year = 2002 ; year < 2014 ; year++ ) {
			workDays.addAll(getWorkDaysForOneYear(year, Calendar.DECEMBER, 31));
		}
		Calendar calendar = Calendar.getInstance();
		workDays.addAll(getWorkDaysForOneYear(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
		for( int dayCnt = 0 ; dayCnt < workDays.size(); dayCnt++ ) {
			String standardDate = workDays.get(dayCnt);
			System.out.println("Start for the date - " + standardDate + " -");
			try {
				ArrayList<KrxItem> companyAndStock = ir.getItemList(1 /* ETF */, standardDate, null);
				System.out.println("    # of etf :" + companyAndStock.size());
				for ( int cnt = 0 ; cnt < companyAndStock.size(); cnt++ ) {
					CompanyEx company = companyAndStock.get(cnt).getCompany();
					company.setSecuritySector(1);
					company.setStandardDate(standardDate);
					dao.update(company);
				}
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
	}
	
	public static void insertETFstockFrom2002Year() {
		ItemListResourceFromKrx ir = new ItemListResourceFromKrx();
		CompanyExDao dao = new CompanyExDao();
		List<String> workDays = new ArrayList<String>();
		// To last year.
		for( int year = 2002 ; year < 2014 ; year++ ) {
			workDays.addAll(getWorkDaysForOneYear(year, Calendar.DECEMBER, 31));
		}
		Calendar calendar = Calendar.getInstance();
		workDays.addAll(getWorkDaysForOneYear(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
		for( int dayCnt = 0 ; dayCnt < workDays.size(); dayCnt++ ) {
			String standardDate = workDays.get(dayCnt);
			System.out.println("Start for the date - " + standardDate + " -");
			try {
				ArrayList<KrxItem> companyAndStock = ir.getItemList(1 /* ETF */, standardDate, null);
				System.out.println("    # of etf :" + companyAndStock.size());
				for ( int cnt = 0 ; cnt < companyAndStock.size(); cnt++ ) {
					CompanyEx company = companyAndStock.get(cnt).getCompany();
					company.setSecuritySector(1);
					company.setStandardDate(standardDate);
					dao.update(company);
				}
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
	}
	
	public static void insertCompanyCodeListAndStockValueFrom1999Year() {
		ItemListResourceFromKrx ir = new ItemListResourceFromKrx();
		CompanyExDao dao = new CompanyExDao();
		StockDao stockDao = new StockDao();

		List<String> workDays = new ArrayList<String>();
		// To last year.
		for( int year = 1999 ; year < 2014 ; year++ ) {
			workDays.addAll(getWorkDaysForOneYear(year, Calendar.DECEMBER, 31));
		}
		Calendar calendar = Calendar.getInstance();
		workDays.addAll(getWorkDaysForOneYear(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));

		for( int dayCnt = 0 ; dayCnt < workDays.size(); dayCnt++ ) {
			String standardDate = workDays.get(dayCnt);
			System.out.println("Start for the date - " + standardDate + " -");
			try {
				ArrayList<CompanyEx> companies = dao.selectAllList(standardDate);
				System.out.println("  # of companies : " + companies.size());
				for ( int securitySector = 0 ; securitySector < 2 ;  securitySector++ ) {
					ArrayList<KrxItem> companyList = ir.getItemList(securitySector, standardDate, null);
					System.out.println("  securitySector : " + securitySector);
					System.out.println("  # of companies from krx : " + companyList.size());
					for ( int cnt = 0 ; cnt < companyList.size(); cnt++ ) {
						// CompanyEx
						CompanyEx company = companyList.get(cnt).getCompany();
						company.setStandardDate(standardDate);
						company.setSecuritySector(securitySector);
						int index = -1;
						if ( ( index = companies.indexOf(company) ) != -1 ) {
							CompanyEx companyEx = companies.get(index);
							if ( !companyEx.getName().equals(company.getName()) )
								dao.insert(company);
						} else
							dao.insert(company);
	
						// Stock Info
						Stock stock = new Stock();
						stock.setCompany(company);
						stock.setMarketCapitalization(companyList.get(cnt).getMaketCapitalization());
						stock.setOrdinaryShares(companyList.get(cnt).getOrdinaryShare());
						stock.setParValue(companyList.get(cnt).getParValue());
						stock.setStandardDate(standardDate);
						stock.setStandardTime("150000");
						stock.setTodayHigh((int)companyList.get(cnt).getTodayHigh());
						stock.setTodayLow((int)companyList.get(cnt).getTodayLow());
						stock.setValue((int)companyList.get(cnt).getStockPrice());
						stock.setOpenPrice((int)companyList.get(cnt).getOpenPrice());
						stock.setVolume(companyList.get(cnt).getVolume());
						
						stockDao.insert(stock);
					}
				}
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
	}
	
	private static boolean isKrxWorkDay(String workDay) {
		boolean contains = false;
		HttpURLConnection conn = null;
		OutputStream os = null;
		try {
			setParams(0, workDay, "A005930" /* Samsung Electronics */);
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
			contains = ( is.read() != -1 );
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( conn != null ) try { conn.disconnect(); } catch ( Exception e1 ) {e1.printStackTrace();}
		}
		return contains;
	}
	
	private static List<String> getWorkDaysForOneW(int year, int month /* from 0 - January */, int day /* from 1 base. */) {
		List<String> rtn = new ArrayList<String>();
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(year, Calendar.JANUARY, 1);
		int maxDayOfYear = calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
		SimpleDateFormat standardFormat = new SimpleDateFormat("yyyyMMdd");
		for( int cnt = 0 ; cnt < maxDayOfYear ; cnt++ ) {
			if ( ( calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY ) && ( calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY ) ) {
				rtn.add(standardFormat.format(calendar.getTime()));
			}
			if ( calendar.get(Calendar.MONTH) == month && calendar.get(Calendar.DAY_OF_MONTH) == day )
				break;
			calendar.add(Calendar.DAY_OF_YEAR, 1);
		}
		return rtn;
	}
	
	private static List<String> getWorkDaysForOneYear(int year, int month /* from 0 - January */, int day /* from 1 base. */) {
		List<String> rtn = new ArrayList<String>();
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(year, Calendar.JANUARY, 1);
		int maxDayOfYear = calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
		SimpleDateFormat standardFormat = new SimpleDateFormat("yyyyMMdd");
		for( int cnt = 0 ; cnt < maxDayOfYear ; cnt++ ) {
			if ( ( calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY ) && ( calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY ) ) {
				rtn.add(standardFormat.format(calendar.getTime()));
			}
			if ( calendar.get(Calendar.MONTH) == month && calendar.get(Calendar.DAY_OF_MONTH) == day )
				break;
			calendar.add(Calendar.DAY_OF_YEAR, 1);
		}
		return rtn;
	}
	
	public static void main2(String[] args) {
		ItemListResourceFromKrx ir = new ItemListResourceFromKrx();
		CompanyExDao dao = new CompanyExDao();
		StockDao stockDao = new StockDao();
		String standardDate = getTodayDate();
		try {
			ArrayList<CompanyEx> companies = dao.selectAllList(standardDate);
			for ( int securityType = 0 ; securityType < 2 ;  securityType++ ) {
				ArrayList<KrxItem> companyList = ir.getItemList(securityType, standardDate, null);
				for ( int cnt = 0 ; cnt < companyList.size(); cnt++ ) {
					// CompanyEx
					CompanyEx company = companyList.get(cnt).getCompany();
					company.setStandardDate(standardDate);
					int index = -1;
					if ( ( index = companies.indexOf(company) ) != -1 ) {
						CompanyEx companyEx = companies.get(index);
						if ( !companyEx.getName().equals(company.getName()) )
							dao.insert(company);
					} else
						dao.insert(company);

					// Stock Info
					Stock stock = new Stock();
					stock.setCompany(company);
					stock.setMarketCapitalization(companyList.get(cnt).getMaketCapitalization());
					stock.setOrdinaryShares(companyList.get(cnt).getOrdinaryShare());
					stock.setParValue(companyList.get(cnt).getParValue());
					stock.setStandardDate(standardDate);
					stock.setStandardTime("150000");
					stock.setTodayHigh((int)companyList.get(cnt).getTodayHigh());
					stock.setTodayLow((int)companyList.get(cnt).getTodayLow());
					stock.setValue((int)companyList.get(cnt).getClosePrice());
					stock.setVolume(companyList.get(cnt).getVolume());
					
					stockDao.insert(stock);
				}
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

}
