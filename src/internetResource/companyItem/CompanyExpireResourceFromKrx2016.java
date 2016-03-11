package internetResource.companyItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import post.KrxItem;
import post.KrxMarketType;

public class CompanyExpireResourceFromKrx2016 {
	
	static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

	static String ITEM_LIST_URL = null;
	static String OTP_URL = null;
	static String OTP_TARGET_PARAM = null;

	static String[][] ITEM_LIST_PARAMS = { 
		{"market_gubun",""},		// 0
		{"isu_cdnm",""},			// 1
		{"isu_cd",""},				// 2
		{"isu_nm",""},				// 3
		{"isu_srt_cd",""},			// 4
		{"fromdate",""},			// 5
		{"todate",""},				// 6
		{"pagePath","/contents/MKD/04/0406/04060600/MKD04060600.jsp"},			// 7
		{"code",""},				// 8
	};
	
	static private enum ColumnList {
		STOCK_ID("isu_cd"), STOCK_NAME("kor_cor_nm"), CHANGE_DATE("chg_dt"), TRAN_STOP_REASON("tr_stp_rsn");
		
		private String value;
		
		private ColumnList(String insDemandColumn) {
			value = insDemandColumn;
		}
	}

	private static String[][] getParams(KrxMarketType marketType, String standardDate, String otp) {
		String[][] params = ITEM_LIST_PARAMS.clone();
		params[0][1] = marketType.getMarketType();
		params[5][1] = standardDate;
		params[6][1] = standardDate;
		params[8][1] = otp;
		return params;
	}
	
	
	static {
		ITEM_LIST_URL = "http://marketdata.krx.co.kr/contents/MKD/99/MKD99000001.jspx";
		OTP_URL = "http://marketdata.krx.co.kr/contents/COM/GenerateOTP.jspx?bld=MKD%2F04%2F0406%2F04060600%2Fmkd04060600&name=form";
		OTP_TARGET_PARAM = "MKD/04/0406/04060600/mkd04060600";
	}
	
	private String getExpiredStockInJson(KrxMarketType marketType, String standardDate) {
		String[][] params = getParams(marketType, standardDate, KrxItemRetriever.getOTP(OTP_URL, OTP_TARGET_PARAM));
		return KrxItemRetriever.getJsonFromKrxUrl(ITEM_LIST_URL, params);
	}

	private static KrxItem getKrxItem(JSONObject item) throws Exception {
		KrxItem oneItem = new KrxItem();
		try {
			oneItem.setId((String)item.get(ColumnList.STOCK_ID.value));
			oneItem.setName((String)item.get(ColumnList.STOCK_NAME.value));
			oneItem.setExpireDate(((String)item.get(ColumnList.CHANGE_DATE.value)).replaceAll("/", ""));
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
	public ArrayList<KrxItem> getItemList(KrxMarketType marketType, String standardDate) throws Exception {
		ArrayList<KrxItem> list = new ArrayList<KrxItem>();
		try {
			String jsonStr = getExpiredStockInJson(marketType, standardDate);
			JSONParser parser = new JSONParser();
			JSONObject loadedItems = (JSONObject)parser.parse(jsonStr);
			JSONArray krxItems = (JSONArray)loadedItems.get("block1");
			for ( int pos = 0 ; pos < krxItems.size() ; pos++ ) {
				KrxItem item = getKrxItem((JSONObject)krxItems.get(pos));
				list.add(item);
			}
		} catch ( Exception e ) {
			throw e;
		}
		return list;
	}
	
	public static void main(String[] args) throws Exception {
		CompanyExpireResourceFromKrx2016 krx = new CompanyExpireResourceFromKrx2016();
		ArrayList<KrxItem> list = krx.getItemList(KrxMarketType.ALL, "20160223");
		for ( KrxItem item: list) {
			System.out.println(item);
		}
	}

}
