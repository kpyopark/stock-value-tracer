package internetResource.companyItem;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import common.StringUtil;
import dao.CompanyExDao;
import dao.FutureAndOptionDao;
import post.CompanyEx;
import post.EnumFutureOptionCategory;
import post.FutureAndOption;

public class FutureResourceFromKrx {
	
	static String ITEM_LIST_URL = null;
	static String OTP_URL = null;
	static String OTP_TARGET_PARAM = null;

	static String[][] ITEM_LIST_PARAMS = {
		{"isu_cd","KRDRVOPMKI"},			// 0
		{"fromdate","20160924"},				// 1
		{"todate","20160924"},				// 2
		{"pagePath","/contents/MKD/06/0601/06010100/MKD06010100.jsp"},			// 3
		{"code",""},					// 4
		{"pageFirstCall", "Y"},
	};
	
	static private enum ColumnList {
		STOCK_NAME("kor_isu_nm"), CURRENT_PRICE("cur_pr"), FLUNC_TYPE_CD("fluc_tp_cd"),
		PREV_DAY_COMPARE("prv_dd_cmpr"), OPEN_PRICE("opn_pr"), HIGH_PRICE("hg_pr"),
		LOW_PRICE("lw_pr"), TX_VOLUME("tr_vl"), OPEN_VOLUME("opn_int_ctr_vl"), STOCK_ID("isu_cd");
		
		private String value;
		
		private ColumnList(String insDemandColumn) {
			value = insDemandColumn;
		}
	}
	
	private static String[][] getParams(EnumFutureOptionCategory category, String fromDate, String toDate, String otp) {
		String[][] params = ITEM_LIST_PARAMS.clone();
		params[0][1] = category.getCode();
		params[1][1] = fromDate;
		params[2][1] = toDate;
		params[4][1] = otp;
		return params;
	}
	
	
	static {
		ITEM_LIST_URL = "http://marketdata.krx.co.kr/contents/MKD/99/MKD99000001.jspx";
		OTP_URL = "http://marketdata.krx.co.kr/contents/COM/GenerateOTP.jspx?bld=MKD%2F06%2F0601%2F06010100%2Fmkd06010100_01&name=form";
		OTP_TARGET_PARAM = "MKD/06/0601/06010100/mkd06010100_01";
	}
	
	private String getFutureCode(EnumFutureOptionCategory category, String fromDate, String toDate) {
		String[][] params = getParams(category, fromDate, toDate, KrxItemRetriever.getOTP(OTP_URL, OTP_TARGET_PARAM));
		return KrxItemRetriever.getJsonFromKrxUrl(ITEM_LIST_URL, params);
	}
	
	private static FutureAndOption getFutures(JSONObject item) throws Exception {
		FutureAndOption oneItem = new FutureAndOption();
		try {
			String krxStockId = (String)item.get(ColumnList.STOCK_ID.value);
			String stockId = krxStockId.substring(3, 11);
			oneItem.setStockId(stockId);
			oneItem.setStockName((String)item.get(ColumnList.STOCK_NAME.value));
			oneItem.setClosedYn(false);
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
	public ArrayList<FutureAndOption> getItemList(EnumFutureOptionCategory category, String fromDate, String toDate) throws Exception {
		ArrayList<FutureAndOption> list = new ArrayList<FutureAndOption>();
		try {
			String jsonStr = getFutureCode(category, fromDate, toDate);
			JSONParser parser = new JSONParser();
			JSONObject loadedItems = (JSONObject)parser.parse(jsonStr);
			JSONArray krxItems = (JSONArray)loadedItems.get("result");
			for ( int pos = 0 ; pos < krxItems.size() ; pos++ ) {
				list.add(getFutures((JSONObject)krxItems.get(pos)));
			}
		} catch ( Exception e ) {
			throw e;
		}
		return list;
	}
	
	public static void updateFutures() {
		
		try {
			FutureResourceFromKrx krx = new FutureResourceFromKrx();
			FutureAndOptionDao dao = new FutureAndOptionDao();
			String standardDate = StringUtil.convertToStandardDate(new Date());
			ArrayList<FutureAndOption> list = krx.getItemList(EnumFutureOptionCategory.FOTYPE_FU_ALL, 
					standardDate, standardDate);
			List<FutureAndOption> listInDb = dao.getActiveFutures();
			CompanyExDao companyDao = new CompanyExDao();
			List<CompanyEx> futureUnderlyingStocks = companyDao.getSingleStockFutureUnderlyingStocks(); 
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
					System.out.println("Disable the future ->" + item.getStockId() + ":" + item.getStockName());
				}
			}
			// 새롭게 생긴 것들은, 추가한다.
			for(FutureAndOption item: newItems) {
				Optional<CompanyEx> targetBaseStock = futureUnderlyingStocks.stream().filter(x -> 
				item.getFutureBaseCode().equals(x.getFutureBaseCode())).findFirst();
				if(targetBaseStock.isPresent()) {
					item.setBaseStockId(targetBaseStock.get().getId());
				}
				dao.insert(item);
				System.out.println("Add new option ->" + item.getStockId() + ":" + item.getStockName());
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		updateFutures();
	}

}
