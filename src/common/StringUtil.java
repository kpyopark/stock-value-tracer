package common;

public class StringUtil {

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
			content = content.trim();
			if ( content.equals("-") )
				return 0;
			try {
				rtn = Float.parseFloat(content.replaceAll("%", "").replaceAll(",",""));
			} catch ( Exception e ) {
				throw new NotNumericContentException(content + ":" + e.getMessage() );
			}
			return rtn;
		}

}
