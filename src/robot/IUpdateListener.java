package robot;

import post.CompanyEx;
import post.CompanyFinancialStatus;
import post.CompanyFinancialStatusEstimated;
import post.Stock;
import post.StockEstimated;

public interface IUpdateListener {

	/**
	 * 주가가 변경되었을 경우 알려줌. 
	 * 
	 * @param stock
	 * @param err
	 */
	public void stockValueChanged(Stock stock, Throwable err);
	
	/**
	 * 주가에 대한 미래추산치가 변경되었을 경우 알려줌.
	 * 
	 * @param cse
	 * @param err
	 */
	public void stockEstimationChanged(StockEstimated cse, Throwable err);
	
	/**
	 * 회사 자체에 대한 정보가 변경되었을 경우(이름이 변경되었거나, 삭제, 또는 수정되었을 경우)
	 * @param company
	 * @param err
	 */
	public void companyChanged(CompanyEx company, Throwable err);
	
	/**
	 * 회사의 재무재표가 변경되었을 경우 알려줌.
	 * @param cfs
	 * @param err
	 */
	public void companyFinancialStatusChanged(CompanyFinancialStatus cfs, Throwable err);
	
	/**
	 * 회사의 재무재표 추산치가 변경되었을 경우 알려줌.
	 * @param cfe
	 * @param err
	 */
	public void companyFinancialStatusEstimatedChanged(CompanyFinancialStatusEstimated cfe, Throwable err);
	
}
