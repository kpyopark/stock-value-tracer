package robot;

import java.util.ArrayList;

import post.Company;
import post.Stock;

import common.StockResource;

import dao.CompanyDao;
import dao.StockDao;

/**
 * ������ �ֽ� �ְ� ������ ������ �´�. 
 * 
 * @author Administrator
 *
 */
public class StockValueUpdator extends DataUpdator {
	
	/**
	 * List���� ������ ���� ��Ȯ�� �������� ������ ����. �۳⸻������ ������ �ּ���.
	 */
	
	public StockValueUpdator() {
		init();
	}
	
	public void init() {

	}
	
	/**
	 * ������ �ֽ������� �����ͼ� Ȱ��
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
				//System.out.println("�ش� ȸ�������� web���� ������ �� �� �������ϴ�.[" + company.getName() + "]");
				err = new Throwable("WEB ���� �ְ� ȹ�� �Ұ�");
			}
			fireStockValueChanged(stock, err);
		} catch ( Exception e ) {
			e.printStackTrace();
			System.out.println("�ش� ȸ�������� web���� ������ �� �� �������ϴ�.[" + company.getName() + "]");
		}
		return totCount;
	}
	
	public static void main(String[] args) {
		try {
			StockValueUpdator updator = new StockValueUpdator();
			ArrayList<Company> companyList = null;
			CompanyDao dao = new CompanyDao();
			companyList = dao.selectAllList(); 
			for ( int cnt = 0 ; cnt < companyList.size() ; cnt++ ) {
				updator.updateStockInfoFromWeb(companyList.get(cnt));
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
}