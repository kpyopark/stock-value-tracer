package common;

public class StringUtil {

		public static String getNumericValue(String org) {
			int tag_start = -1;
			int tag_end = -1;
			while ( ( tag_start = org.indexOf("<") ) >= 0 ) {
				tag_end = org.indexOf(">");
				org = org.substring(0,tag_start) + org.substring(tag_end+1);
				System.out.println(org);
			}
			org = org.replace(",","").trim();
			return org;
		}
	
}
