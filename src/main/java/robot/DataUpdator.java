package robot;

import java.util.Iterator;

import post.CompanyEx;
import post.CompanyFinancialStatus;
import post.CompanyFinancialStatusEstimated;
import post.Stock;
import post.StockEstimated;

public class DataUpdator {
	
	java.util.ArrayList<IUpdateListener> listenerList = new java.util.ArrayList<IUpdateListener>();
	
	public void addUpdateListener(IUpdateListener listener) {
		listenerList.add(listener);
	}
	
	public void removeUpdateListener(IUpdateListener listener) {
		listenerList.remove(listener);
	}
	
	/**
	 * 주가가 변경되었을 경우 알려줌. 
	 * 
	 * @param stock
	 * @param err
	 */
	public void fireStockValueChanged(final Stock stock, final Throwable err) {
		for (Iterator<IUpdateListener> eachOne = listenerList.iterator(); eachOne != null && eachOne.hasNext() ; ) {
			final IUpdateListener listener = eachOne.next();
			new Thread(new Runnable() {
				public void run() {
					if ( listener != null ) listener.stockValueChanged(stock, err);
				}
			}).start();
		}
	}
	
	/**
	 * 주가에 대한 미래추산치가 변경되었을 경우 알려줌.
	 * 
	 * @param cse
	 * @param err
	 */
	public void fireStockEstimationChanged(final StockEstimated cse, final Throwable err) {
		for (Iterator<IUpdateListener> eachOne = listenerList.iterator(); eachOne != null && eachOne.hasNext() ; ) {
			final IUpdateListener listener = eachOne.next();
			new Thread(new Runnable() {
				public void run() {
					if ( listener != null ) listener.stockEstimationChanged(cse, err);
				}
			}).start();
		}
	}
	
	/**
	 * 회사 자체에 대한 정보가 변경되었을 경우(이름이 변경되었거나, 삭제, 또는 수정되었을 경우)
	 * @param company
	 * @param err
	 */
	public void fireCompanyChanged(final CompanyEx company, final Throwable err) {
		for (Iterator<IUpdateListener> eachOne = listenerList.iterator(); eachOne != null && eachOne.hasNext() ; ) {
			final IUpdateListener listener = eachOne.next();
			new Thread(new Runnable() {
				public void run() {
					if ( listener != null ) listener.companyChanged(company, err);
				}
			}).start();
		}
	}
	
	/**
	 * 회사의 재무재표가 변경되었을 경우 알려줌.
	 * @param cfs
	 * @param err
	 */
	public void fireCompanyFinancialStatusChanged(final CompanyFinancialStatus cfs, final Throwable err) {
		for (Iterator<IUpdateListener> eachOne = listenerList.iterator(); eachOne != null && eachOne.hasNext() ; ) {
			final IUpdateListener listener = eachOne.next();
			new Thread(new Runnable() {
				public void run() {
					if ( listener != null ) listener.companyFinancialStatusChanged(cfs, err);
				}
			}).start();
		}
	}
	
	/**
	 * 회사의 재무재표 추산치가 변경되었을 경우 알려줌.
	 * @param cfe
	 * @param err
	 */
	public void fireCompanyFinancialStatusEstimatedChanged(final CompanyFinancialStatusEstimated cfe, final Throwable err) {
		for (Iterator<IUpdateListener> eachOne = listenerList.iterator(); eachOne != null && eachOne.hasNext() ; ) {
			final IUpdateListener listener = eachOne.next();
			new Thread(new Runnable() {
				public void run() {
					if ( listener != null ) listener.companyFinancialStatusEstimatedChanged(cfe, err);
				}
			}).start();
		}
	}

}
