package robot.company;

import internetResource.companyItem.CompanyAndItemListResourceFromKrx;
import internetResource.companyItem.CompanyExpireResourceFromKrx;
import internetResource.financialReport.FinancialReportResourceFromFnguide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import post.CompanyEx;
import post.KrxItem;
import post.KrxSecurityType;
import post.Stock;
import robot.DataUpdator;
import streamProcess.krx.KrxStreamInserter;
import streamProcess.krx.KrxStreamWebResource;
import common.StringUtil;
import dao.CompanyExDao;
import dao.KrxItemDao;
import dao.StockDao;

public class CompanyListUpdatorFromKrx extends DataUpdator {
	
	CompanyExDao dao = null;
	StockDao stockDao = null;
	KrxItemDao krxDao = null;

	public CompanyListUpdatorFromKrx() {
		dao = new CompanyExDao();
		stockDao = new StockDao();
		krxDao = new KrxItemDao();
	}
	
	public void insertETFstockFrom2002Year() {
		CompanyAndItemListResourceFromKrx ir = new CompanyAndItemListResourceFromKrx();
		List<String> workDays = new ArrayList<String>();
		for( int year = 2002 ; year < 2014 ; year++ ) {
			workDays.addAll(getWorkDaysForOneYear(year, Calendar.DECEMBER, 31));
		}
		Calendar calendar = Calendar.getInstance();
		workDays.addAll(getWorkDaysForOneYear(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
		for( int dayCnt = 0 ; dayCnt < workDays.size(); dayCnt++ ) {
			String standardDate = workDays.get(dayCnt);
			System.out.println("Start for the date - " + standardDate + " -");
			try {
				ArrayList<KrxItem> companyAndStock = ir.getItemList(KrxSecurityType.ETF, standardDate, null);
				System.out.println("    # of etf :" + companyAndStock.size());
				for ( int cnt = 0 ; cnt < companyAndStock.size(); cnt++ ) {
					CompanyEx company = companyAndStock.get(cnt).getCompany();
					company.setSecuritySector(KrxSecurityType.ETF.getType());
					company.setStandardDate(standardDate);
					dao.update(company);
				}
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
	}
	
	public void insertCompanyCodeListAndStockValueFrom1999Year() {

		Calendar calendar = Calendar.getInstance();
		for( int year = 1999 ; year < calendar.get(Calendar.YEAR) ; year++ ) {
			insertCompanyCodeListAndStockValueForOneYear(year);
		}
	}
	
	public void insertCompanyCodeListAndStockValueForOneYear(int year) {
		List<String> workDays = new ArrayList<String>();
		Calendar calendar = Calendar.getInstance();
		if ( year == Calendar.getInstance().get(Calendar.YEAR) ) {
			workDays.addAll(getWorkDaysForOneYear(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
		} else {
			workDays.addAll(getWorkDaysForOneYear(year, Calendar.DECEMBER, 31));
		}
		insertCompanyCodeListAndStockValueForPeriods(workDays);
	}
	
	public void insertCompanyCodeListAndStockValueForPeriods(List<String> workDays) {
		for( int dayCnt = 0 ; dayCnt < workDays.size(); dayCnt++ ) {
			String standardDate = workDays.get(dayCnt);
			System.out.println("Start for the date - " + standardDate + " -");
			insertCompanyAndStockFromKrxItem(standardDate);
		}
	}
	

	public void insertKrxItemsForPeriods(List<String> workDays) {
		KrxStreamWebResource webResource = new KrxStreamWebResource();
		KrxStreamInserter inserter = new KrxStreamInserter(webResource.getDestinationQueue());
		webResource.startStream();
		inserter.startStream();
		try {
			for( int dayCnt = 0 ; dayCnt < workDays.size(); dayCnt++ ) {
				String standardDate = workDays.get(dayCnt);
				System.out.println("Start for the date - " + standardDate + " - ");
				for ( KrxSecurityType securityType : KrxSecurityType.values() ) {
					webResource.addWebResourceTask(standardDate, securityType);
				}
			}
		} catch ( InterruptedException ie ) {
			ie.printStackTrace();
		}
		inserter.stopStream();
		webResource.stopStream();
	}

	public static List<String> getWorkDays(int fromYear, int fromMonth, int fromDay, int toYear, int toMonth, int toDay) {
		List<String> rtn = new ArrayList<String>();
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(toYear,  toMonth, toDay);
		int lastJulianDate = calendar.get(Calendar.DAY_OF_YEAR);
		calendar.clear();
		calendar.set(fromYear, fromMonth, fromDay);
		SimpleDateFormat standardFormat = new SimpleDateFormat("yyyyMMdd");
		while(true) {
			if ( calendar.get(Calendar.YEAR) > toYear )
				break;
			if ( calendar.get(Calendar.YEAR) == toYear && calendar.get(Calendar.DAY_OF_YEAR) >= lastJulianDate )
				break;
			if ( ( calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY ) && ( calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY ) ) {
				rtn.add(standardFormat.format(calendar.getTime()));
			}
			calendar.add(Calendar.DAY_OF_YEAR, 1);
		}
		return rtn;
	}
	
	public static List<String> getWorkDaysForOneYear(int year, int month /* from 0 - January */, int day /* from 1 base. */) {
		return getWorkDays(year, Calendar.JANUARY, 1, year, month, day);
	}

	private static Stock getStockFromKrxItem(KrxItem krxStockInfo, CompanyEx company, String standardDate, String standardTime) {
		Stock stock = new Stock();
		stock.setCompany(company);
		stock.setMarketCapitalization(krxStockInfo.getMaketCapitalization());
		stock.setOrdinaryShares(krxStockInfo.getOrdinaryShare());
		stock.setParValue(krxStockInfo.getParValue());
		stock.setStandardDate(standardDate);
		stock.setStandardTime(standardTime);
		stock.setTodayHigh((int)krxStockInfo.getTodayHigh());
		stock.setTodayLow((int)krxStockInfo.getTodayLow());
		stock.setValue((int)krxStockInfo.getStockPrice());
		stock.setVolume(krxStockInfo.getVolume());
		return stock;
	}

	public void insertCompanyAndStockFromKrxItem(String standardDate) {
		CompanyAndItemListResourceFromKrx ir = new CompanyAndItemListResourceFromKrx();
		try {
			ArrayList<CompanyEx> companiesFromDB = dao.selectAllList(standardDate);
			for ( KrxSecurityType securityType : KrxSecurityType.values() ) {
				ArrayList<KrxItem> krxItemList = ir.getItemList(securityType, standardDate, null);
				for ( int cnt = 0 ; cnt < krxItemList.size(); cnt++ ) {
					// CompanyEx
					CompanyEx companyFromKrx = krxItemList.get(cnt).getCompany();
					companyFromKrx.setStandardDate(standardDate);
					companyFromKrx.setSecuritySector(securityType.getType());
					int currentDBPosition = -1;
					if ( ( currentDBPosition = companiesFromDB.indexOf(companyFromKrx) ) != -1 ) {
						CompanyEx companyEx = companiesFromDB.get(currentDBPosition);
						if ( !companyEx.getName().equals(companyFromKrx.getName()) 
								|| companyEx.getSecuritySector() != companyFromKrx.getSecuritySector() ) {
							dao.insert(companyFromKrx);
							fireCompanyChanged(companyFromKrx, null);
						}
					} else
						dao.insert(companyFromKrx);
					// Stock Info
					Stock stock = getStockFromKrxItem(krxItemList.get(cnt), companyFromKrx, standardDate, "150000");
					if ( stockDao.select(companyFromKrx, standardDate, "150000") != null ) {
						// skip
					} else {
						stockDao.insert(stock);
					}
					if ( krxDao.select(krxItemList.get(cnt), standardDate) != null ) {
						// skip
					} else {
						krxDao.insert(krxItemList.get(cnt));
					}
				}
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	static class KrxStockMatcher implements Comparator<KrxItem> {

		@Override
		public int compare(KrxItem o1, KrxItem o2) {
			return o1.getId().compareTo(o2.getId());
		}
		
	}
	
	public void insertCompanyExpirationFromKrxItem() {
		CompanyExpireResourceFromKrx ir = new CompanyExpireResourceFromKrx();
		KrxStockMatcher matcher = new KrxStockMatcher();
		try {
			String standardDate = StringUtil.convertToStandardDate(new java.util.Date());
			ArrayList<CompanyEx> companiesFromDB = dao.selectAllList(standardDate);
			ArrayList<KrxItem> krxItemList = ir.getItemList(standardDate);
			System.out.println(krxItemList.size());
			Collections.sort(krxItemList,matcher);
			for ( CompanyEx company : companiesFromDB ) {
				KrxItem krxCompany = new KrxItem();
				krxCompany.setId(company.getId());
				int position = Collections.binarySearch(krxItemList, krxCompany, matcher);
				//System.out.println(company.getId() + ":" + company.getName() + ":" + position);
				if ( position >= 0 ) {
					KrxItem matchedItem = krxItemList.get(position);
					company.setStandardDate(matchedItem.getExpireDate());
					company.setClosed(true);
					System.out.println("closed..:" + company);
					CompanyEx prevCompanyInfo = dao.select(company.getId(), company.getStandardDate());
					if ( prevCompanyInfo != null ) {
						if ( prevCompanyInfo.isClosed() ) {
							// it's correct information. do nothing.
						} else {
							// it's invalid information. delete and insert.
							prevCompanyInfo.setClosed(true);
							dao.update(prevCompanyInfo);
						}
					} else {
						dao.insert(company);
					}
				}
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public void updateLatestCompanyList() {
		try {
			String latestStandardDate = dao.getLatestStandardDate();
			int fromYear, fromMonth, fromDay, toYear, toMonth, toDay;
			Date latestDate = StringUtil.convertToDate(latestStandardDate);
			Calendar calendar = Calendar.getInstance();
			calendar.clear();
			calendar.setTimeInMillis(latestDate.getTime());
			fromYear = calendar.get(Calendar.YEAR);
			fromMonth = calendar.get(Calendar.MONTH);
			fromDay = calendar.get(Calendar.DAY_OF_MONTH);
			calendar.clear();
			calendar.setTimeInMillis(System.currentTimeMillis());
			toYear = calendar.get(Calendar.YEAR);
			toMonth = calendar.get(Calendar.MONTH);
			toDay = calendar.get(Calendar.DAY_OF_MONTH);
			List<String> workDays = getWorkDays(fromYear, fromMonth, fromDay,
					toYear, toMonth, toDay);
			insertCompanyCodeListAndStockValueForPeriods(workDays);
			insertCompanyExpirationFromKrxItem();
			updateFicsSectorInfo();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public void updateKrxItemsFrom1996() {
		try {
			int fromYear, fromMonth, fromDay, toYear, toMonth, toDay;
			Calendar calendar = Calendar.getInstance();
			fromYear = 1996;
			fromMonth = Calendar.JANUARY;
			fromDay = 1;
			calendar.clear();
			calendar.setTimeInMillis(System.currentTimeMillis());
			toYear = calendar.get(Calendar.YEAR);
			toMonth = calendar.get(Calendar.MONTH);
			toDay = calendar.get(Calendar.DAY_OF_MONTH);
			List<String> workDays = getWorkDays(fromYear, fromMonth, fromDay,
					toYear, toMonth, toDay);
			insertKrxItemsForPeriods(workDays);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public void updateFicsSectorInfo() {
		CompanyExDao dao = new CompanyExDao();
		try {
			ArrayList<CompanyEx> companyList = dao.selectAllList(dao.getLatestStandardDate());
			FinancialReportResourceFromFnguide ir = new FinancialReportResourceFromFnguide();
			boolean needUpdate;
			for( CompanyEx fromDB : companyList ) {
				CompanyEx fromWeb = new CompanyEx();
				needUpdate = false;
				fromWeb.copyStructure(fromDB);
				ir.getFinancialStatus(fromWeb);
				if ( fromWeb.getFicsIndustry() != null && !fromWeb.getFicsIndustry().equals(fromDB.getFicsIndustry())) {
					needUpdate = true;
				}
				if ( fromWeb.getFicsIndustryGroup() != null && !fromWeb.getFicsIndustryGroup().equals(fromDB.getFicsIndustryGroup())) {
					needUpdate = true;
				}
				if ( fromWeb.getFicsSector() != null && !fromWeb.getFicsSector().equals(fromDB.getFicsSector())) {
					needUpdate = true;
				}
				if ( needUpdate ) {
					dao.update(fromWeb);
				}
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		CompanyListUpdatorFromKrx updator = new CompanyListUpdatorFromKrx();
		updator.updateKrxItemsFrom1996();
		// After this class runs, execute procedure 'proc_import_companies_from_extend_table' 
	}
	
}
