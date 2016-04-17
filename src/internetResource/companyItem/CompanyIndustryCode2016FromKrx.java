package internetResource.companyItem;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import post.CompanyEx;
import post.KrxItem;
import post.KrxMarketType;

public class CompanyIndustryCode2016FromKrx {
	
	static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

	static String ITEM_LIST_URL = null;
	static String OTP_URL = null;
	static String OTP_TARGET_PARAM = null;

	static String[][] ITEM_LIST_PARAMS = {
		{"market_gubun","ALL"},			// 0
		{"isu_cdnm","ÀüÃ¼"},				// 1
		{"sort_type","A"},				// 2
		{"lst_stk_vl","1"},				// 3
		{"cpt","1"},					// 4
		{"pagePath","/contents/MKD/04/0406/04060100/MKD04060100.jsp"},			// 5
		{"code",""},					// 6
		{"pageSize", "3000"},
	};
	
	static private enum ColumnList {
		NUMBER("no"), STOCK_ID("isu_cd"), STOCK_NAME("kor_cor_nm"), INDUSTRY_CODE("std_ind_cd"),INDUSTRY_CATEGORY("std_ind_nm"), TELNO("tel_no"), ADDRESS("addr");
		
		private String value;
		
		private ColumnList(String insDemandColumn) {
			value = insDemandColumn;
		}
	}

	private static String[][] getParams(KrxMarketType marketType, String otp) {
		String[][] params = ITEM_LIST_PARAMS.clone();
		params[1][1] = marketType.getMarketType();
		params[6][1] = otp;
		return params;
	}
	
	
	static {
		ITEM_LIST_URL = "http://marketdata.krx.co.kr/contents/MKD/99/MKD99000001.jspx";
		OTP_URL = "http://marketdata.krx.co.kr/contents/COM/GenerateOTP.jspx?bld=MKD%2F04%2F0406%2F04060100%2Fmkd04060100_01&name=form";
		OTP_TARGET_PARAM = "MKD/04/0406/04060100/mkd04060100_01";
	}
	
	private String getIndustryCodeInJson(KrxMarketType marketType) {
		String[][] params = getParams(marketType, KrxItemRetriever.getOTP(OTP_URL, OTP_TARGET_PARAM));
		return KrxItemRetriever.getJsonFromKrxUrl(ITEM_LIST_URL, params);
	}

	private static CompanyEx getCompanyEx(JSONObject item) throws Exception {
		CompanyEx oneItem = new CompanyEx();
		try {
			oneItem.setId("A" + (String)item.get(ColumnList.STOCK_ID.value));
			oneItem.setName((String)item.get(ColumnList.STOCK_NAME.value));
			oneItem.setKrxIndustryCode((String)item.get(ColumnList.INDUSTRY_CODE.value));
			oneItem.setKrxIndustryCategory((String)item.get(ColumnList.INDUSTRY_CATEGORY.value));
			oneItem.setTelNo((String)item.get(ColumnList.TELNO.value));
			oneItem.setAddress(URLDecoder.decode((String)item.get(ColumnList.ADDRESS.value)));
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
	public ArrayList<CompanyEx> getItemList(KrxMarketType marketType) throws Exception {
		ArrayList<CompanyEx> list = new ArrayList<CompanyEx>();
		try {
			String jsonStr = getIndustryCodeInJson(marketType);
			JSONParser parser = new JSONParser();
			JSONObject loadedItems = (JSONObject)parser.parse(jsonStr);
			JSONArray krxItems = (JSONArray)loadedItems.get("block1");
			for ( int pos = 0 ; pos < krxItems.size() ; pos++ ) {
				CompanyEx item = getCompanyEx((JSONObject)krxItems.get(pos));
				list.add(item);
			}
		} catch ( Exception e ) {
			throw e;
		}
		return list;
	}
	
	public static void main(String[] args) throws Exception {
		CompanyIndustryCode2016FromKrx krx = new CompanyIndustryCode2016FromKrx();
		ArrayList<CompanyEx> list = krx.getItemList(KrxMarketType.ALL);
		for ( CompanyEx item: list) {
			System.out.println(item);
		}
	}

}
