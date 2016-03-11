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
	static String OTP_TARGET_PARAM = null;
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
		if(company != null) {
			params[5][1] = company.getName();
			params[6][1] = company.getId();
		}
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
		OTP_TARGET_PARAM = "MKD/04/0406/04060200/mkd04060200";	
	}
	
	private String getStockPriceInJson(KrxSecurityType securityType, String standardDate, Company company) {
		String[][] params = getParams(securityType, standardDate, company, KrxItemRetriever.getOTP(OTP_URL, OTP_TARGET_PARAM));
		return KrxItemRetriever.getJsonFromKrxUrl(ITEM_LIST_URL, params);
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
