package internetResource.companyItem;


import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import dao.FutureAndOptionDao;
import post.CompanyEx;
import post.EnumFutureOptionCategory;
import post.FutureAndOption;
import post.KrxMarketType;

public class FutureAndOptionResourceFromKrx {
	
	static String ITEM_LIST_URL = null;
	static String OTP_URL = null;
	static String OTP_TARGET_PARAM = null;

	static String[][] ITEM_LIST_PARAMS = {
		{"isu_cd","KRDRVOPMKI"},			// 0
		{"cp_type","C"},				// 1
		{"sort_type","A"},				// 2
		{"type","1"},				// 3
		{"pagePath","/contents/MKD/06/0602/06020100/MKD06020100.jsp"},			// 4
		{"code",""},					// 5
		{"pageSize", "3000"},
	};
	
	static private enum ColumnList {
		STOCK_NAME("kor_isu_nm"), CURRENT_PRICE("cur_pr"), PREV_DAY_COMPARE("prv_dd_cmpr"), STOCK_ID("isu_cd");
		
		private String value;
		
		private ColumnList(String insDemandColumn) {
			value = insDemandColumn;
		}
	}

	private static String[][] getParams(EnumFutureOptionCategory category, String callPutType, String otp) {
		String[][] params = ITEM_LIST_PARAMS.clone();
		params[0][1] = category.getCode();
		params[1][1] = callPutType;
		params[5][1] = otp;
		return params;
	}
	
	
	static {
		ITEM_LIST_URL = "http://marketdata.krx.co.kr/contents/MKD/99/MKD99000001.jspx";
		OTP_URL = "https://marketdata.krx.co.kr/contents/COM/GenerateOTP.jspx?bld=MKD%2F06%2F0602%2F06020100%2Fmkd06020100&name=form";
		OTP_TARGET_PARAM = "MKD/06/0602/06020100/mkd06020100";
	}
	
	private String getOptionCode(EnumFutureOptionCategory category, String callPutType) {
		String[][] params = getParams(category, callPutType, KrxItemRetriever.getOTP(OTP_URL, OTP_TARGET_PARAM));
		return KrxItemRetriever.getJsonFromKrxUrl(ITEM_LIST_URL, params);
	}

	private static FutureAndOption getFutureAndOption(JSONObject item) throws Exception {
		FutureAndOption oneItem = new FutureAndOption();
		try {
			String krxStockId = (String)item.get(ColumnList.STOCK_ID.value);
			String stockId = krxStockId.substring(3, 11);
			oneItem.setStockId(stockId);
			oneItem.setStockName((String)item.get(ColumnList.STOCK_NAME.value));
			String optionName = oneItem.getStockName();
			StringTokenizer st = new StringTokenizer(optionName, " ");
			String baseName = st.nextToken();
			String callPutType = st.nextToken();
			String targetYm = st.nextToken();
			String actionPrice = st.nextToken();
			oneItem.setFutureOptionType(callPutType);
			oneItem.setTargetYm(targetYm);
			oneItem.setActionPrice(Float.parseFloat(actionPrice));
		} catch ( Exception e ) {
			e.printStackTrace();
			System.out.println("There is invalid items:" + oneItem.getStockId() + ":" + oneItem.getStockName() );
			throw e;
		}
		return oneItem;
	}
	
	/**
	 * @return
	 * @throws Exception
	 */
	public ArrayList<FutureAndOption> getItemList(EnumFutureOptionCategory category, String callPutType) throws Exception {
		ArrayList<FutureAndOption> list = new ArrayList<FutureAndOption>();
		try {
			String jsonStr = getOptionCode(category, callPutType);
			JSONParser parser = new JSONParser();
			JSONObject loadedItems = (JSONObject)parser.parse(jsonStr);
			JSONArray krxItems = (JSONArray)loadedItems.get("result");
			for ( int pos = 0 ; pos < krxItems.size() ; pos++ ) {
				list.add(getFutureAndOption((JSONObject)krxItems.get(pos)));
			}
		} catch ( Exception e ) {
			throw e;
		}
		return list;
	}
	
	public static void updateIndexOptionAndMiniIndexOption() {
		
		try {
			FutureAndOptionResourceFromKrx krx = new FutureAndOptionResourceFromKrx();
			FutureAndOptionDao dao = new FutureAndOptionDao();
			ArrayList<FutureAndOption> list = krx.getItemList(EnumFutureOptionCategory.FOTYPE_KOSPI200, "C");
			list.addAll(krx.getItemList(EnumFutureOptionCategory.FOTYPE_KOSPI200, "P"));
			list.addAll(krx.getItemList(EnumFutureOptionCategory.FOTYPE_MINISP200, "C"));
			list.addAll(krx.getItemList(EnumFutureOptionCategory.FOTYPE_MINISP200, "P"));
			List<FutureAndOption> listInDb = dao.getActionOptions();
			List<FutureAndOption> newItems = new ArrayList<FutureAndOption>();
			List<FutureAndOption> oldItems = new ArrayList<FutureAndOption>();
			for ( FutureAndOption item: list) {
				if(listInDb.stream().anyMatch(x -> x.getStockId().equals(item.getStockId())))
				{
					oldItems.add(item);
				} else {
					newItems.add(item);
				}
			}
			// Old 에도 없는 것들은, Closed Yn 을 "Y" 로 변경해서 저장해야 한다.
			for(FutureAndOption item: listInDb) {
				if(item.getBaseStockId() != null)
					continue;
				if(!oldItems.stream().anyMatch(x -> item.getStockId().equals(x.getStockId()))) {
					item.setClosedYn(true);
					dao.update(item);
					System.out.println("Disable the option ->" + item.getStockId() + ":" + item.getStockName());
				}
			}
			// 새롭게 생긴 것들은, 추가한다.
			for(FutureAndOption item: newItems) {
				dao.insert(item);
				System.out.println("Add new option ->" + item.getStockId() + ":" + item.getStockName());
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		updateIndexOptionAndMiniIndexOption();
	}

}
