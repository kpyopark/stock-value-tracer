package internetResource.environment;

import internetResource.companyItem.KrxItemRetriever;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import common.StringUtil;

import dao.ClosedDayDao;

public class ClosedDayRetriever {
	static String ITEM_LIST_URL = null;
	static String OTP_URL = null;
	static String OTP_TARGET_PARAM = null;

	static String[][] ITEM_LIST_PARAMS = {
		{"search_bas_yy","2017"},										// 0
		{"gridTp","KRX"},												// 1
		{"pagePath","/contents/MKD/01/0110/01100305/MKD01100305.jsp"},	// 2
		{"code",""},													// 3
		{"pageFirstCall", "Y"},											// 4
	};
	
	static private enum ColumnList {
		CALENDAR_DD("calnd_dd"), DAY_TYPE_CODE("dy_tp_cd"), HOLIDAY_NAME("holdy_nm"),
		KOREAN_DAY_TYPE("kr_dy_tp");
		
		private String value;
		
		private ColumnList(String insDemandColumn) {
			value = insDemandColumn;
		}
	}
	
	private static String[][] getParams(String targetYear, String otp) {
		String[][] params = ITEM_LIST_PARAMS.clone();
		params[0][1] = targetYear;
		params[3][1] = otp;
		return params;
	}
	
	
	static {
		ITEM_LIST_URL = "http://marketdata.krx.co.kr/contents/MKD/99/MKD99000001.jspx";
		OTP_URL = "http://marketdata.krx.co.kr/contents/COM/GenerateOTP.jspx?bld=MKD%2F01%2F0110%2F01100305%2Fmkd01100305_01&name=form";
		OTP_TARGET_PARAM = "MKD/01/0110/01100305/mkd01100305_01";
	}
	
	private String getClosedDayCode(String targetYear) {
		String[][] params = getParams(targetYear, KrxItemRetriever.getOTP(OTP_URL, OTP_TARGET_PARAM));
		return KrxItemRetriever.getJsonFromKrxUrl(ITEM_LIST_URL, params);
	}
	
	private static String getClosedDay(JSONObject item) throws Exception {
		String closedDay = null;
		try {
			closedDay = (String)item.get(ColumnList.CALENDAR_DD.value);
			closedDay = closedDay.replaceAll(java.util.regex.Pattern.quote("-"), "");
		} catch ( Exception e ) {
			e.printStackTrace();
			System.out.println("There is invalid items:" + closedDay );
			throw e;
		}
		return closedDay;
	}
	
	/**
	 * @return
	 * @throws Exception
	 */
	public ArrayList<String> getItemList(String targetYear) throws Exception {
		ArrayList<String> list = new ArrayList<String>();
		try {
			String jsonStr = getClosedDayCode(targetYear);
			JSONParser parser = new JSONParser();
			JSONObject loadedItems = (JSONObject)parser.parse(jsonStr);
			JSONArray krxItems = (JSONArray)loadedItems.get("block1");
			for ( int pos = 0 ; pos < krxItems.size() ; pos++ ) {
				list.add(getClosedDay((JSONObject)krxItems.get(pos)));
			}
		} catch ( Exception e ) {
			throw e;
		}
		return list;
	}
	
	public static void updateClosedDays() {
		
		try {
			ClosedDayRetriever krx = new ClosedDayRetriever();
			ClosedDayDao dao = new ClosedDayDao();
			String standardDate = StringUtil.convertToStandardDate(new Date());
			String currentYear = standardDate.substring(0, 4);
			String nextYear = StringUtil.getNextAnnualStandardDate(standardDate).substring(0, 4);
			List<String> currentlist = krx.getItemList(currentYear);
			List<String> nextlist = krx.getItemList(nextYear);
			dao.deleteAnnualDayAndInsertAll(currentlist);
			dao.deleteAnnualDayAndInsertAll(nextlist);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		updateClosedDays();
	}
}
