package internetResource.companyItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import common.StringUtil;
import post.Company;
import post.KrxItem;
import post.KrxSecurityType;

public class EtfListResource2016FromKrx {
	
	static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	static String ITEM_LIST_URL = null;
	static String OTP_URL = null;
	static String OTP_TARGET_PARAM = null;
	static String[][] ITEM_LIST_PARAMS = { 
		{"schdate","20160224"},											// 0
		{"pagePath","/contents/MKD/02/0206/02060400/MKD02060400.jsp"}, 	// 1 
		{"code", ""},											// 2
	};
	
	private static String[][] getParams(String standardDate, String otp) {
		String[][] params = ITEM_LIST_PARAMS.clone();
		params[0][1] = standardDate;
		params[2][1] = otp;
		return params;
	}
	
	static private enum ColumnList {
		VOLUME("accTrdvol"),
		STOCK_NAME("isuKorAbbrv"),
		STOCK_ID("isuSrtCd"),
		ORDINARY_SHARES("listShrs"),
		NAV("lstNav"),
		PREV_DIFF("prevddClsprc"),
		CURRENT_PRICE("tddClsprc"), // --  
		HIGH_PRICE("tddHgprc"), 
		LOW_PRICE("tddLwprc"), 
		START_PRICE("tddOpnprc"),
		TRACE_ERROR_RATE("traceErrRt"),
		PREV_DIFF_SIGN("updnTyp")
		// PREV_DIFF_RATIO("updn_rate"), 
		// SELL_PRICE("ofr_fst_qot_pr"), 
		// ASK_PRICE("bid_fst_qot_pr"), 
		// PAR_VALUE("par_pr"), 
		// CURRENCY_DIV("curr_iso"),  
		// MARKET_CAPITALIZATION("lst_stk_amt")
		;
		
		private String value;
		
		private ColumnList(String insDemandColumn) {
			value = insDemandColumn;
		}
	}
	
	static {
		ITEM_LIST_URL = "http://marketdata.krx.co.kr/contents/MKD/99/MKD99000001.jspx";
		OTP_URL = "http://marketdata.krx.co.kr/contents/COM/GenerateOTP.jspx?bld=MKD%2F02%2F0206%2F02060400%2Fmkd02060400&name=form";
		OTP_TARGET_PARAM = "MKD/02/0206/02060400/mkd02060400";	
	}
	
	private String getStockPriceInJson(String standardDate) {
		String[][] params = getParams(standardDate, KrxItemRetriever.getOTP(OTP_URL, OTP_TARGET_PARAM));
		return KrxItemRetriever.getJsonFromKrxUrl(ITEM_LIST_URL, params);
	}

	private static KrxItem getKrxItem(JSONObject item) throws Exception {
		KrxItem oneItem = new KrxItem();
		try {
			oneItem.setId((String)item.get(ColumnList.STOCK_ID.value));
			oneItem.setName((String)item.get(ColumnList.STOCK_NAME.value));
			String prevDiffSign = (String)item.get(ColumnList.PREV_DIFF_SIGN.value);
			int sign = prevDiffSign.equals("+") ? 1 : -1;
			oneItem.setStockPrice(StringUtil.getLongValue((String)item.get(ColumnList.CURRENT_PRICE.value)));
			oneItem.setNetChange(StringUtil.getLongValue((String)item.get(ColumnList.PREV_DIFF.value)) * sign);
			oneItem.setNetChangeRatio(
					1.0f * oneItem.getStockPrice() / 
					(oneItem.getStockPrice() - oneItem.getNetChange())
					);
			oneItem.setBid(StringUtil.getLongValue((String)item.get(ColumnList.CURRENT_PRICE.value)));
			oneItem.setAsk(StringUtil.getLongValue((String)item.get(ColumnList.CURRENT_PRICE.value)));
			oneItem.setVolume(StringUtil.getLongValue((String)item.get(ColumnList.VOLUME.value)));
			oneItem.setVolumnAmount(StringUtil.getLongValue((String)item.get(ColumnList.VOLUME.value)));
			oneItem.setOpenPrice(StringUtil.getLongValue((String)item.get(ColumnList.START_PRICE.value)));
			oneItem.setTodayHigh(StringUtil.getLongValue((String)item.get(ColumnList.HIGH_PRICE.value)));
			oneItem.setTodayLow(StringUtil.getLongValue((String)item.get(ColumnList.LOW_PRICE.value)));
			oneItem.setParValue(StringUtil.getFloatValue((String)item.get(ColumnList.NAV.value)));
			oneItem.setCurrency("WON");
			oneItem.setOrdinaryShare(StringUtil.getLongValue((String)item.get(ColumnList.ORDINARY_SHARES.value)));
			oneItem.setMaketCapitalization(
					StringUtil.getLongValue((String)item.get(ColumnList.ORDINARY_SHARES.value))
					* StringUtil.getLongValue((String)item.get(ColumnList.CURRENT_PRICE.value))
					);
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
	public ArrayList<KrxItem> getItemList(String standardDate) throws Exception {
		ArrayList<KrxItem> list = new ArrayList<KrxItem>();
		try {
			String jsonStr = getStockPriceInJson(standardDate);
			JSONParser parser = new JSONParser();
			JSONObject loadedItems = (JSONObject)parser.parse(jsonStr);
			JSONArray krxItems = (JSONArray)loadedItems.get("block1");
			for ( int pos = 0 ; pos < krxItems.size() ; pos++ ) {
				KrxItem item = getKrxItem((JSONObject)krxItems.get(pos));
				item.setStandardDate(standardDate);
				item.setSecurityType(KrxSecurityType.ETF);
				list.add(item);
			}
		} catch ( Exception e ) {
			System.out.println("Security Type: ETF : Date :" + standardDate);
			throw e;
		}
		return list;
	}
	
	public static void main(String[] args) {
		EtfListResource2016FromKrx webResource = new EtfListResource2016FromKrx();
		Company company = new Company();
		company.setId("A005930");
		company.setName("삼성전자");
		try {
			ArrayList<KrxItem> items = webResource.getItemList("20160224");
			System.out.println(items);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
	}

}
