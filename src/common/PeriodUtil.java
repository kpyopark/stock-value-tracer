package common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class PeriodUtil {
	public static ArrayList<String> getQuarterPeriodsListFromAnnualPeriodsList(ArrayList<String> annualPeriodList) {
		ArrayList<String> quarterList = new ArrayList<String>();
		Collections.sort(annualPeriodList);
		String prevPeriod = null;
		for(String standardDate: annualPeriodList) {
			if ( "20000101".compareTo(standardDate) < 0 ) {
				if ( prevPeriod != null ) {
					// when the financial period got changed such like 2,5,8,11 -> 3,6,9,12
					//
					// 20131130 --> next period expected is 20140228
					// but the period changed to 3,6,9,12.
					// so there is no financial report on 20140228.
					String nextQuarter = prevPeriod;
					while ( (nextQuarter = StringUtil.getLastDayOfQuarter(nextQuarter,3)).compareTo(standardDate) < 0 ) {
						quarterList.add(nextQuarter);
					}
				}
				quarterList.add(standardDate);
				prevPeriod = standardDate;
			}
		}
		return quarterList;
	}
	
	public static ArrayList<String> getMonthlyPeriodList(int fromYear, int toYear) {
		ArrayList<String> periodList = new ArrayList<String>();
		int year;
		int month;
		for( year= fromYear ; year <= toYear; year++ ) {
			for ( month = 1; month <= 12 ; month++ )
				if ( month <= 9) 
					periodList.add(year + "0" + month + "15");
				else
					periodList.add(year + "" + month + "15");
		}
		return periodList;
	}
	
	public static ArrayList<String> getQuarterListFrom2000ToNow() {
		ArrayList<String> annualPeriodList = new ArrayList<String>();
		for ( int year = 2000; year <= Calendar.getInstance().get(Calendar.YEAR) ; year++ ) {
			annualPeriodList.add(year + "1231");
		}
		return getQuarterPeriodsListFromAnnualPeriodsList(annualPeriodList);
	}
	
	public static void main(String[] args) {
		//testGetPeriodList();
		System.out.println(
				getWorkDays(2017,Calendar.DECEMBER,12, 2017, Calendar.DECEMBER, 13));
	}
	
	public static void testGetPeriodList() {
		String[] testSet = {
			"20050831",
			"20080831",
			"20111130",
			"20120831",
			"20121231",
		};
		String[] expectedSet = {
				"20111130",
				"20120229",
				"20120531",
				"20120831",
				"20121130",
				"20121231"
		};
		ArrayList<String> results = getQuarterPeriodsListFromAnnualPeriodsList(new ArrayList<String>(Arrays.asList(testSet)));
		for( String period:results) {
			System.out.println(period);
		}
		System.out.println(results.containsAll(Arrays.asList(expectedSet)));
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
			if ( calendar.get(Calendar.YEAR) == toYear && calendar.get(Calendar.DAY_OF_YEAR) > lastJulianDate )
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

}
