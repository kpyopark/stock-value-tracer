package internetResource.companyItem;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.htmlcleaner.TagNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import post.Company;
import post.KrxItem;
import post.KrxSecurityType;
import common.StringUtil;

public class CompanyAndItemListResourceFromKrx2 {
	
	static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	static String ITEM_LIST_URL = null;
	static String OTP_URL = null;
	static String[][] ITEM_LIST_PARAMS = { 
		{"market_gubun","ALL"},										// 0
		{"indx_ind_cd",""},											// 1
		{"sect_tp_cd","ALL"},										// 2
		{"isu_cdnm", ""},											// 3
		{"isu_cd",""},												// 4
		{"isu_nm",""},			                                    // 5
		{"isu_srt_cd",""},                                          // 6 
		{"secugrp","ST"},                                           // 7
		{"stock_gubun","on"},                                       // 8
		{"schdate","20160224"},                                     // 9 
		{"pagePath","/contents/MKD/04/0406/04060200/MKD04060200.jsp"}, 
																	// 10 
		{"code","gaYduSMAuz5QLSMiaSCd4elyQyh4VAKkipOqhrKJLGpUbY6kXsaf8rwqLEueOmMgoYD4OQpAKa4jmcopr+H5Ddt5pXCTeOTH7KH5u3SuR/RvOF38l0pss68CkzJVdjoU"},
																	// 11
		{"pageSize", "3000"},
	};
	
	private static String[][] getParams(KrxSecurityType securityType, String standardDate, Company company, String otp) {
		String[][] params = ITEM_LIST_PARAMS.clone();
		//params[3][1] = company.getId() + "/" + company.getName();
		//params[4][1] = "KR7005930003";
		params[5][1] = company.getName();
		params[6][1] = company.getId();
		params[7][1] = securityType.getNewSecurityType();
		params[9][1] = standardDate;
		params[11][1] = otp;
		return params;
	}
	
	static private enum ColumnList {
		STOCK_ID("isu_cd"), STOCK_NAME("kor_shrt_isu_nm"), CURRENT_PRICE("isu_cur_pr"), PREV_DIFF_SIGN("fluc_tp_cd"), PREV_DIFF("prv_dd_cmpr"),
		PREV_DIFF_RATIO("updn_rate"), SELL_PRICE("ofr_fst_qot_pr"), ASK_PRICE("bid_fst_qot_pr"), VOLUME("isu_tr_vl"), 
		VOLUME_AMOUNT("isu_tr_amt"), START_PRICE("isu_opn_pr"),
		HIGH_PRICE("isu_hg_pr"), LOW_PRICE("isu_lw_pr"), 
		PAR_VALUE("par_pr"), CURRENCY_DIV("curr_iso"), ORDINARY_SHARES("lst_stk_vl"), MARKET_CAPITALIZATION("lst_stk_amt");
		
		private String value;
		
		private ColumnList(String insDemandColumn) {
			value = insDemandColumn;
		}
	}
	
	static {
		ITEM_LIST_URL = "http://marketdata.krx.co.kr/contents/MKD/99/MKD99000001.jspx";
		OTP_URL = "http://marketdata.krx.co.kr/contents/COM/GenerateOTP.jspx?bld=MKD%2F04%2F0406%2F04060200%2Fmkd04060200&name=form";
	}
	
	static String SAMPLE_RETURN_VALUE;
	static {
		SAMPLE_RETURN_VALUE = "{\"block1\":[{\"trd_dd\":\"2016/02/23\",\"tdd_clsprc\":\"1,181,000\",\"fluc_tp\":\"1\",\"tdd_cmpr\":\"6,000\",\"acc_trdvol\":\"147,674\",\"acc_trdval\":\"174,272,083,000\",\"tdd_opnprc\":\"1,179,000\",\"tdd_hgprc\":\"1,189,000\",\"tdd_lwprc\":\"1,173,000\",\"mktcap\":\"171,326,887\",\"list_shrs\":\"145,069,337\"},{\"trd_dd\":\"2016/02/22\",\"tdd_clsprc\":\"1,175,000\",\"fluc_tp\":\"2\",\"tdd_cmpr\":\"15,000\",\"acc_trdvol\":\"174,899\",\"acc_trdval\":\"206,059,224,890\",\"tdd_opnprc\":\"1,190,000\",\"tdd_hgprc\":\"1,192,000\",\"tdd_lwprc\":\"1,166,000\",\"mktcap\":\"170,456,471\",\"list_shrs\":\"145,069,337\"},{\"trd_dd\":\"2016/02/19\",\"tdd_clsprc\":\"1,190,000\",\"fluc_tp\":\"1\",\"tdd_cmpr\":\"3,000\",\"acc_trdvol\":\"176,852\",\"acc_trdval\":\"209,455,620,271\",\"tdd_opnprc\":\"1,187,000\",\"tdd_hgprc\":\"1,195,000\",\"tdd_lwprc\":\"1,174,000\",\"mktcap\":\"172,632,511\",\"list_shrs\":\"145,069,337\"},{\"trd_dd\":\"2016/02/18\",\"tdd_clsprc\":\"1,187,000\",\"fluc_tp\":\"1\",\"tdd_cmpr\":\"2,000\",\"acc_trdvol\":\"222,481\",\"acc_trdval\":\"264,666,470,521\",\"tdd_opnprc\":\"1,203,000\",\"tdd_hgprc\":\"1,203,000\",\"tdd_lwprc\":\"1,178,000\",\"mktcap\":\"172,197,303\",\"list_shrs\":\"145,069,337\"},{\"trd_dd\":\"2016/02/17\",\"tdd_clsprc\":\"1,185,000\",\"fluc_tp\":\"1\",\"tdd_cmpr\":\"17,000\",\"acc_trdvol\":\"246,062\",\"acc_trdval\":\"291,221,290,000\",\"tdd_opnprc\":\"1,179,000\",\"tdd_hgprc\":\"1,201,000\",\"tdd_lwprc\":\"1,169,000\",\"mktcap\":\"171,907,164\",\"list_shrs\":\"145,069,337\"},{\"trd_dd\":\"2016/02/16\",\"tdd_clsprc\":\"1,168,000\",\"fluc_tp\":\"1\",\"tdd_cmpr\":\"14,000\",\"acc_trdvol\":\"180,401\",\"acc_trdval\":\"211,309,041,000\",\"tdd_opnprc\":\"1,158,000\",\"tdd_hgprc\":\"1,179,000\",\"tdd_lwprc\":\"1,157,000\",\"mktcap\":\"169,440,986\",\"list_shrs\":\"145,069,337\"}]}";
	}
	
	static String XPATH_ONE_ITEM_RECORD = "//tr";

	static TagNode node(Object org) {
		return (TagNode)org;
	}	
	
	private static String getOTP() {
		HttpURLConnection conn = null;
		OutputStream os = null;
		BufferedReader br = null;
		String[][] params = {
				{"bld", "MKD/04/0406/04060200/mkd04060200"},
				{"name", "form"}
		};
		String otp = null;
		try {
			conn = (HttpURLConnection)new URL(OTP_URL).openConnection();
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
			br = new BufferedReader(new InputStreamReader(is));
			otp = br.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if ( conn != null ) try { conn.disconnect(); } catch ( Exception e1 ) {e1.printStackTrace();}
			if ( br != null ) try { br.close(); } catch ( Exception e1 ) {e1.printStackTrace();}
		}
		return otp;
	}
	
	private String getStockPriceInJson(KrxSecurityType securityType, String standardDate, Company company) {
		HttpURLConnection conn = null;
		OutputStream os = null;
		InputStream is = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		String[][] params = getParams(securityType, standardDate, company, getOTP());
		String stockPriceList = null;
		try {
			conn = (HttpURLConnection)new URL(ITEM_LIST_URL).openConnection();
			conn.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			conn.setRequestProperty("Cookie", "__utma=139639017.608487098.1438889613.1438889613.1456263472.2; __utmc=139639017; __utmz=139639017.1456263472.2.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); JSESSIONID=F989631BDF62F5A6B124AC647D64009D.58tomcat1; G_ENABLED_IDPS=google; __utma=70557324.770790152.1456263490.1456263490.1456263490.1; __utmc=70557324; __utmz=70557324.1456263490.1.1.utmcsr=krx.co.kr|utmccn=(referral)|utmcmd=referral|utmcct=/contents/COM/SiteSearch.jsp");
			conn.setRequestProperty("Host", "marketdata.krx.co.kr");
			conn.setRequestProperty("Origin", "marketdata.krx.co.kr");
			conn.setRequestProperty("Referer", "http://marketdata.krx.co.kr/mdi");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.109 Safari/537.36");
			conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			StringBuffer postParameter = new StringBuffer();
			for( int paramCount = 0 ; paramCount < params.length ; paramCount++ ) {
				postParameter.append("&").append(params[paramCount][0]).append("=").append(URLEncoder.encode(params[paramCount][1], "utf-8"));
			}
			os = conn.getOutputStream();
			os.write(postParameter.substring(1).getBytes());
			System.out.println(postParameter.substring(1));
			os.flush();
			os.close();
			is = conn.getInputStream();
			byte[] buffer = new byte[1024];
			int length = 0;
			while((length = is.read(buffer,0,1024)) != -1 ) {
				baos.write(buffer, 0, length);
			}
			stockPriceList = new String(baos.toByteArray(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if ( is != null ) try { is.close(); } catch ( Exception e1 ) {e1.printStackTrace();}
			if ( baos != null ) try { baos.close(); } catch ( Exception e1 ) {e1.printStackTrace();}
			if ( conn != null ) try { conn.disconnect(); } catch ( Exception e1 ) {e1.printStackTrace();}
		}
		return stockPriceList;
	}

	private static KrxItem getKrxItem(JSONObject item) throws Exception {
		KrxItem oneItem = new KrxItem();
		try {
			oneItem.setId((String)item.get(ColumnList.STOCK_ID.value));
			oneItem.setName((String)item.get(ColumnList.STOCK_NAME.value));
			String prevDiffSign = (String)item.get(ColumnList.PREV_DIFF_SIGN.value);
			int sign = prevDiffSign.equals("1") ? 1 : -1;
			oneItem.setStockPrice(StringUtil.getLongValue((String)item.get(ColumnList.CURRENT_PRICE.value)));
			oneItem.setNetChange(StringUtil.getLongValue((String)item.get(ColumnList.PREV_DIFF.value)) * sign);
			oneItem.setNetChangeRatio(StringUtil.getFloatValue((String)item.get(ColumnList.PREV_DIFF_RATIO.value)) * sign);
			oneItem.setBid(StringUtil.getLongValue((String)item.get(ColumnList.SELL_PRICE.value)));
			oneItem.setAsk(StringUtil.getLongValue((String)item.get(ColumnList.ASK_PRICE.value)));
			oneItem.setVolume(StringUtil.getLongValue((String)item.get(ColumnList.VOLUME.value)));
			oneItem.setVolumnAmount(StringUtil.getLongValue((String)item.get(ColumnList.VOLUME_AMOUNT.value)));
			oneItem.setOpenPrice(StringUtil.getLongValue((String)item.get(ColumnList.START_PRICE.value)));
			oneItem.setTodayHigh(StringUtil.getLongValue((String)item.get(ColumnList.HIGH_PRICE.value)));
			oneItem.setTodayLow(StringUtil.getLongValue((String)item.get(ColumnList.LOW_PRICE.value)));
			oneItem.setParValue(StringUtil.getFloatValue((String)item.get(ColumnList.PAR_VALUE.value)));
			oneItem.setCurrency((String)item.get(ColumnList.CURRENCY_DIV.value));
			oneItem.setOrdinaryShare(StringUtil.getLongValue((String)item.get(ColumnList.ORDINARY_SHARES.value)));
			oneItem.setMaketCapitalization(StringUtil.getLongValue((String)item.get(ColumnList.MARKET_CAPITALIZATION.value)));
		} catch ( Exception e ) {
			e.printStackTrace();
			System.out.println("There is invalid items:" + oneItem.getId() + ":" + oneItem.getName() );
			throw e;
		}
		return oneItem;
	}
	
	/**
	 * @return
	 * @throws Exception
	 */
	public ArrayList<KrxItem> getItemList(KrxSecurityType securityType, String standardDate, Company company) throws Exception {
		ArrayList<KrxItem> list = new ArrayList<KrxItem>();
		try {
			String jsonStr = getStockPriceInJson(securityType, standardDate, company);
			JSONParser parser = new JSONParser();
			JSONObject loadedItems = (JSONObject)parser.parse(jsonStr);
			JSONArray krxItems = (JSONArray)loadedItems.get("상장종목검색");
			for ( int pos = 0 ; pos < krxItems.size() ; pos++ ) {
				KrxItem item = getKrxItem((JSONObject)krxItems.get(pos));
				item.setStandardDate(standardDate);
				item.setSecurityType(securityType);
				list.add(item);
			}
		} catch ( Exception e ) {
			System.out.println("Security Type:" + securityType + ": Date :" + standardDate + ": ID :" + company.getId());
			throw e;
		}
		return list;
	}
	
	public void updateSecuritySectorForEtf(String standardDate) throws Exception {
		ArrayList<KrxItem> list = new ArrayList<KrxItem>();
		try {
			/*
			Object[] tags = getRootTagNode(KrxSecurityType.ETF, standardDate, null).evaluateXPath(XPATH_ONE_ITEM_RECORD);
			for ( int line = 0 ; line < tags.length ; line++ ) {
				KrxItem item = getKrxItem((TagNode)tags[line]);
				list.add(item);
			}
			*/
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	static Company DAY_CHECK_DEFAULT_STOCK;
	
	static {
		DAY_CHECK_DEFAULT_STOCK = new Company();
		DAY_CHECK_DEFAULT_STOCK.setId("A005930");
		DAY_CHECK_DEFAULT_STOCK.setName("삼성전자");
	}
	
	private static boolean isKrxWorkDay(String workDay) {
		boolean contains = false;
		HttpURLConnection conn = null;
		OutputStream os = null;
		try {
			String[][] params = getParams(KrxSecurityType.STOCK, workDay, DAY_CHECK_DEFAULT_STOCK, getOTP());
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
		CompanyAndItemListResourceFromKrx2 webResource = new CompanyAndItemListResourceFromKrx2();
		Company company = new Company();
		company.setId("A005930");
		company.setName("삼성전자");
		try {
			ArrayList<KrxItem> items = webResource.getItemList(KrxSecurityType.STOCK, "20160224", company);
			System.out.println(items);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
	}

}
