package robot.stock;

import internetResource.stock.StockResource;

import java.util.ArrayList;

import post.Company;
import post.CompanyEx;
import post.KrxSecurityType;
import post.Stock;
import robot.DataUpdator;
import common.StringUtil;
import dao.CompanyExDao;
import dao.StockDao;

/**
 * 웹에서 최신 주가 정보를 가지고 온다. 
 * 
 * @author Administrator
 *
 */
public class StockValueUpdator extends DataUpdator {
	
	/**
	 * List에서 가지고 오는 정확도 떨어지는 정보의 모음. 작년말월자의 정보가 최선임.
	 */
	
	public StockValueUpdator() {
		init();
	}
	
	public void init() {

	}
	
	/**
	 * 웹에서 주식정보를 가져와서 활용
	 */
	public int updateStockInfoFromWeb(Company company) throws Exception {
		StockDao dao = new StockDao();
		StockResource sr = StockResource.getStockResource();
		Throwable err = null;
		int totCount = 0;
		try {
			Stock stock = sr.getStock(company);
			if ( stock != null ) {
				System.out.println( stock );
				if ( dao.select(stock.getCompany(),
						stock.getStandardDate(),stock.getStandardTime()) != null ) {
					dao.delete(stock);
				}
				if ( dao.insert(stock) ) 
					totCount++;
			} else {
				//System.out.println("해당 회사정보는 web에서 가지고 올 수 없었습니다.[" + company.getName() + "]");
				err = new Throwable("WEB 에서 주가 획득 불가");
			}
			fireStockValueChanged(stock, err);
		} catch ( Exception e ) {
			e.printStackTrace();
			System.out.println("해당 회사정보는 web에서 가지고 올 수 없었습니다.[" + company.getName() + "]");
		}
		return totCount;
	}
	
	public static void main(String[] args) {
		try {
			StockValueUpdator updator = new StockValueUpdator();
			ArrayList<CompanyEx> companyList = null;
			CompanyExDao dao = new CompanyExDao();
			companyList = dao.selectAllList(StringUtil.convertToStandardDate(new java.util.Date()), KrxSecurityType.STOCK); 
			for ( int cnt = 0 ; cnt < companyList.size() ; cnt++ ) {
				updator.updateStockInfoFromWeb(companyList.get(cnt));
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
}
