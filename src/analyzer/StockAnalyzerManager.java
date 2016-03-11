package analyzer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import post.Company;
import post.CompanyEx;
import post.KrxSecurityType;
import robot.IUpdateListener;
import robot.company.CompanyListUpdatorFromKrx;
import robot.estimation.AnnualEstimationUpdator;
import robot.estimation.StockEstimationUpdator;
import robot.financialReport.FinancialReportListUpdatorFromFnguide;
import robot.financialReport.FinancialReportRefiner;
import robot.listenter.ExamUpdateListener;
import robot.stock.StockValueUpdator;

import common.StringUtil;

import dao.CompanyExDao;

class ThreadPool {

	java.util.Vector<Thread> runThreads = new java.util.Vector<Thread>();
	
	static int MAX_SIZE = 4;
	
	public ThreadPool() {
		
	}
	
	public void run(final Runnable runnable) {
		int position;
		while ( ( position = getAvailiable() ) < 0 ) {
			try {
				runThreads.wait(10000);
			} catch ( Exception e ) {
			}
		}
		if ( position < runThreads.size() )
			runThreads.remove(position);
		runThreads.add(position, new Thread(){
			public void run() {
				runnable.run();
				synchronized(runThreads) {
					runThreads.notifyAll();
				}
			}
		});
		runThreads.get(position).start();
		printStatus();
	}
	
	public void notifyAllThreads() {
		synchronized(runThreads) {
			runThreads.notifyAll();
		}
	}
	
	private synchronized int getAvailiable() {
		int position = -1;
		
		if ( runThreads.size() < MAX_SIZE ) {
			position = runThreads.size();
		} else {
			for(int cnt=0; cnt<runThreads.size() ;cnt++ ) {
				if ( !runThreads.get(cnt).isAlive() ) {
					position = cnt;
					System.out.println("select position:"+position);
					break;
				}
			}
		}
		return position; 
	}
	
	private synchronized void printStatus() {
		System.out.println("current size:" + runThreads.size() );
	}
}

public class StockAnalyzerManager {

	ArrayList<CompanyEx> companyList = null;
	CompanyExDao dao = null;
	IUpdateListener listener = null;
	
	ThreadPool threadPool = new ThreadPool();
	
	public StockAnalyzerManager() {
		init();
	}
	
	protected void init() {
		try {
			dao = new CompanyExDao();
			companyList = dao.selectAllList(StringUtil.convertToStandardDate(new java.util.Date())); 
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<CompanyEx> getCompanyList() {
		return companyList;
	}
	
	public void setUpdateListener(IUpdateListener listener) {
		this.listener = listener;
	}
	
	/**
	 * 회사 목록을 수정한다.
	 * 현재 이 부분은 손질이 약간 필요함.
	 */
	public void startCompanyListUpdator() {
		try {
			CompanyListUpdatorFromKrx updator = new CompanyListUpdatorFromKrx();
			if ( listener != null ) updator.addUpdateListener(listener);
			updator.updateLatestCompanyList();
			if ( listener != null ) updator.removeUpdateListener(listener);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 재무재표 정보를 Web에서 가지고 온다.
	 */
	public void startCompanyFinancialStatusUpdator() {
		try {
			final ArrayList<Company> failedCompany = new ArrayList<Company>();
			final FinancialReportListUpdatorFromFnguide updator = new FinancialReportListUpdatorFromFnguide();
			updator.addUpdateListener(listener);
			for (int cnt = 0 ;cnt < companyList.size(); cnt++ ) {
				final CompanyEx company = companyList.get(cnt);
				if ( company.getSecuritySector() == KrxSecurityType.STOCK.getType() )
					threadPool.run(new Runnable() {
						public void run() {
							try {
								CompanyEx orgCompany = new CompanyEx();
								orgCompany.copyStructure(company);
								if (orgCompany.getSecuritySector() == CompanyEx.SECURITY_ORDINARY_STOCK) {
									System.out.println("Ordinary Stock:" + company);
									updator.updateFinancialStatus(company);
									if ( ( orgCompany.isClosed() != company.isClosed() ) ||
											( orgCompany.getFicsSector() != null && orgCompany.getFicsSector().equals(company.getFicsSector() ) ) ||
											( orgCompany.getFicsIndustryGroup() != null && orgCompany.getFicsIndustryGroup().equals(company.getFicsIndustryGroup() ) ) ||
											( orgCompany.getFicsIndustry() != null && orgCompany.getFicsIndustry().equals(company.getFicsIndustry() ) ) ||
											( orgCompany.isClosed() != company.isClosed() ) ) {
										dao.update(company);
									}
								} else {
									System.out.println("Deffered Stock:" + company);
								}
							} catch ( Exception e ) {
								e.printStackTrace();
								failedCompany.add(company);
							}
						}
					});
			}
			threadPool.notifyAllThreads();
			updator.removeUpdateListener(listener);
			for (Company failed:failedCompany)
				System.out.println("Failed:" + failed);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 오류가 발생한 재무 정보를 보정한다.
	 */
	public void startFinancialReportRefiner() {
		try {
			FinancialReportRefiner.testCheckAllCompanyFinancialStatement();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 회사 주가정보를 Web에서 가지고 온다.
	 */
	public void startStockValueUpdator() {
		try {
			StockValueUpdator updator = new StockValueUpdator();
			updator.addUpdateListener(listener);
			for (int cnt = 0 ;cnt < companyList.size(); cnt++ ) {
				if (companyList.get(cnt).getSecuritySector() == 0 ) 
					updator.updateStockInfoFromWeb(companyList.get(cnt));
			}
			updator.removeUpdateListener(listener);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 미래의 회사 재무제표를 추산한다.
	 */
	public void startAnnualEstimationUpdator() {
		try {
			AnnualEstimationUpdator updator = new AnnualEstimationUpdator();
			updator.addUpdateListener(listener);
			String standardDate = STANDARD_DATE_FORMAT.format(new Date());
			for (int cnt = 0 ;cnt < companyList.size(); cnt++ ) {
				if ( companyList.get(cnt).getSecuritySector() == CompanyEx.SECURITY_ORDINARY_STOCK )
					updator.updateCompanyFinancialStatusEstimated(companyList.get(cnt), standardDate);
				else
				{
					System.out.println("Deffered Stock.");
				}
			}
			updator.removeUpdateListener(listener);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 주가 예상치를 추산한다.
	 */ 
	public void startStockValueEstimationUpdator() {
		try {
			StockEstimationUpdator updator = new StockEstimationUpdator();
			updator.addUpdateListener(listener);
			String registeredDate = StringUtil.convertToStandardDate(new java.util.Date());
			for (int cnt = 0 ;cnt < companyList.size(); cnt++ ) {
				if ( companyList.get(cnt).getSecuritySector() == KrxSecurityType.STOCK.getType() ) {
					updator.updateStockEstimated(companyList.get(cnt), registeredDate);
				}
			}
			updator.removeUpdateListener(listener);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	static SimpleDateFormat STANDARD_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	
	/**
	 * 주가를 평가한다.
	 */
	public void startStockAnalyzer() {
		try {
			String standardDate = STANDARD_DATE_FORMAT.format(new Date());
			StockAnalyzer stockAnal = new StockAnalyzer(standardDate);
			stockAnal.getBestStockList(500, standardDate);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		StockAnalyzerManager manager = new StockAnalyzerManager();
		manager.setUpdateListener(new ExamUpdateListener());
		manager.startCompanyListUpdator();
		//manager.startCompanyFinancialStatusUpdator();
		//manager.startFinancialReportRefiner();
		manager.startAnnualEstimationUpdator();
		manager.startStockValueEstimationUpdator();
		manager.startStockAnalyzer();
	}
}
