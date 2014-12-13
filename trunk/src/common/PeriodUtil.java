package common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class PeriodUtil {
	public static ArrayList<String> getPeriodList(ArrayList<String> periods) {
		ArrayList<String> quarterList = new ArrayList<String>();
		Collections.sort(periods);
		String prevPeriod = null;
		for(String standardDate: periods) {
			if ( "20080101".compareTo(standardDate) < 0 ) {
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
	
	public static void main(String[] args) {
		testGetPeriodList();
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
		ArrayList<String> results = getPeriodList(new ArrayList<String>(Arrays.asList(testSet)));
		for( String period:results) {
			System.out.println(period);
		}
		System.out.println(results.containsAll(Arrays.asList(expectedSet)));
	}
}
