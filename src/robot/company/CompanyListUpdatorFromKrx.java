package robot.company;

import internetResource.companyItem.CompanyAndItemListResourceFromKrx;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import post.CompanyEx;
import post.KrxItem;
import post.Stock;
import robot.DataUpdator;

import common.StringUtil;

import dao.CompanyExDao;
import dao.StockDao;

public class CompanyListUpdatorFromKrx extends DataUpdator {
	
	CompanyExDao dao = null;
	StockDao stockDao = null;

	public CompanyListUpdatorFromKrx() {
		dao = new CompanyExDao();
		stockDao = new StockDao();		
	}

	public void insertETFstockFrom2002Year() {
		CompanyAndItemListResourceFromKrx ir = new CompanyAndItemListResourceFromKrx();
		List<String> workDays = new ArrayList<String>();
		// To last year.
		for( int year = 2002 ; year < 2014 ; year++ ) {
			workDays.addAll(getWorkDaysForOneYear(year, Calendar.DECEMBER, 31));
		}
		Calendar calendar = Calendar.getInstance();
		workDays.addAll(getWorkDaysForOneYear(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
		for( int dayCnt = 0 ; dayCnt < workDays.size(); dayCnt++ ) {
			String standardDate = workDays.get(dayCnt);
			System.out.println("Start for the date - " + standardDate + " -");
			try {
				ArrayList<KrxItem> companyAndStock = ir.getItemList(1 /* ETF */, standardDate, null);
				System.out.println("    # of etf :" + companyAndStock.size());
				for ( int cnt = 0 ; cnt < companyAndStock.size(); cnt++ ) {
					CompanyEx company = companyAndStock.get(cnt).getCompany();
					company.setSecuritySector(1);
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
		stock.setValue((int)krxStockInfo.getClosePrice());
		stock.setVolume(krxStockInfo.getVolume());
		return stock;
	}
	
	public void insertCompanyAndStockFromKrxItem(String standardDate) {
		CompanyAndItemListResourceFromKrx ir = new CompanyAndItemListResourceFromKrx();
		try {
			ArrayList<CompanyEx> companiesFromDB = dao.selectAllList(standardDate);
			for ( int securityType = 0 ; securityType < 2 ;  securityType++ ) {
				ArrayList<KrxItem> krxItemList = ir.getItemList(securityType, standardDate, null);
				for ( int cnt = 0 ; cnt < krxItemList.size(); cnt++ ) {
					// CompanyEx
					CompanyEx companyFromKrx = krxItemList.get(cnt).getCompany();
					companyFromKrx.setStandardDate(standardDate);
					int currentDBPosition = -1;
					if ( ( currentDBPosition = companiesFromDB.indexOf(companyFromKrx) ) != -1 ) {
						CompanyEx companyEx = companiesFromDB.get(currentDBPosition);
						if ( !companyEx.getName().equals(companyFromKrx.getName()) ) {
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
				}
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public void updateLatestCompanyList() {
		try {
			String latestStandardDate = dao.getLatestStandardDate();
			Date latestDate = StringUtil.convertToDate(latestStandardDate);
			Date currentDate = new java.util.Date();
			List<String> workDays = getWorkDays(latestDate.getDate(), latestDate.getMonth(), latestDate.getDay(), currentDate.getYear(), currentDate.getMonth(), currentDate.getDay());
			insertCompanyCodeListAndStockValueForPeriods(workDays);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		CompanyListUpdatorFromKrx updator = new CompanyListUpdatorFromKrx();
		List<String> workDays = CompanyListUpdatorFromKrx.getWorkDays(2014, Calendar.APRIL, 28, 2014, Calendar.MAY, 1);
		updator.insertCompanyCodeListAndStockValueForPeriods(workDays);
	}
	
}