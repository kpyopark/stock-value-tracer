package common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtil {

	static SimpleDateFormat STANDARD_DATE = null;
	static SimpleDateFormat STANDARD_TIME = null;
	
	static {
		try {
			STANDARD_DATE = new SimpleDateFormat("yyyyMMdd");
			STANDARD_TIME = new SimpleDateFormat("HHmmss");
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public static Date convertToDate(String standardDate) {
		java.util.Date rtn = null;
		try {
			rtn = STANDARD_DATE.parse(standardDate);
		} catch ( ParseException pe ) {
			pe.printStackTrace();
		}
		return rtn;
	}
	
	public static String convertToStandardDate(Date date) {
		String rtn = null;
		rtn = STANDARD_DATE.format(date);
		return rtn;
	}
	
	public static String getNumericValue(String org) {
		int tag_start = -1;
		int tag_end = -1;
		org = org.trim();
		while ( ( tag_start = org.indexOf("<") ) >= 0 ) {
			tag_end = org.indexOf(">");
			org = org.substring(0,tag_start) + org.substring(tag_end+1);
			System.out.println(org);
		}
		org = org.replace(",","").trim();
		return org;
	}

	public static String removeHtmlSpaceTag(String content) {
		return (content != null) ? content.trim().replaceAll("&nbsp;", "") : null;
	}
	
	public static long getLongValue(String content) throws NotNumericContentException {
		long rtn = 0;
		if ( content == null || content.trim().length() == 0 || content.trim().equals("&nbsp;") || content.equals("-"))
			return 0;
		content = content.trim();
		try {
			rtn = Long.parseLong(content.replaceAll(",", ""));
		} catch ( Exception e ) {
			throw new NotNumericContentException(content + ":" + e.getMessage() );
		}
		return rtn;
	}
	
	public static float getFloatValue(String content) throws NotNumericContentException {
		float rtn = 0;
		if ( content == null || content.trim().length() == 0 || content.trim().equals("&nbsp;") || content.equals("-"))
			return 0;
		if ( content == null || content.trim().length() == 0 || content.trim().equals("&nbsp;") )
			return 0;
		if ( content.equals("N/A(IFRS)") )
			return 0;
		content = content.trim();
		try {
			rtn = Float.parseFloat(content.replaceAll("%", "").replaceAll(",",""));
		} catch ( Exception e ) {
			throw new NotNumericContentException(content + ":" + e.getMessage() );
		}
		return rtn;
	}

}
