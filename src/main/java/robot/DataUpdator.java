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
	 * �ְ��� ����Ǿ��� ��� �˷���. 
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
	 * �ְ��� ���� �̷��߻�ġ�� ����Ǿ��� ��� �˷���.
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
	 * ȸ�� ��ü�� ���� ������ ����Ǿ��� ���(�̸��� ����Ǿ��ų�, ����, �Ǵ� �����Ǿ��� ���)
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
	 * ȸ���� �繫��ǥ�� ����Ǿ��� ��� �˷���.
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
	 * ȸ���� �繫��ǥ �߻�ġ�� ����Ǿ��� ��� �˷���.
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
