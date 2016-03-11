package internetResource.companyItem;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class KrxItemRetriever {
	
	public static String getOTP(String url, String otpTarget) {
		HttpURLConnection conn = null;
		OutputStream os = null;
		BufferedReader br = null;
		String[][] params = {
				{"bld", otpTarget},
				{"name", "form"}
		};
		String otp = null;
		try {
			conn = (HttpURLConnection)new URL(url).openConnection();
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			StringBuffer postParameter = new StringBuffer();
			for( int paramCount = 0 ; paramCount < params.length ; paramCount++ ) {
				postParameter.append("&").append(params[paramCount][0]).append("=").append(params[paramCount][1]);
			}
			os = conn.getOutputStream();
			os.write(postParameter.substring(1).getBytes());
			os.flush();
			os.close();
			InputStream is = conn.getInputStream();
			br = new BufferedReader(new InputStreamReader(is));
			otp = br.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if ( conn != null ) try { conn.disconnect(); } catch ( Exception e1 ) {e1.printStackTrace();}
			if ( br != null ) try { br.close(); } catch ( Exception e1 ) {e1.printStackTrace();}
		}
		return otp;
	}
	
	public static String getJsonFromKrxUrl(String resourceURL, String[][] params) {
		HttpURLConnection conn = null;
		OutputStream os = null;
		InputStream is = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		String jsonArray = null;
		try {
			conn = (HttpURLConnection)new URL(resourceURL).openConnection();
			conn.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			conn.setRequestProperty("Cookie", "__utma=139639017.608487098.1438889613.1438889613.1456263472.2; __utmc=139639017; __utmz=139639017.1456263472.2.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); JSESSIONID=F989631BDF62F5A6B124AC647D64009D.58tomcat1; G_ENABLED_IDPS=google; __utma=70557324.770790152.1456263490.1456263490.1456263490.1; __utmc=70557324; __utmz=70557324.1456263490.1.1.utmcsr=krx.co.kr|utmccn=(referral)|utmcmd=referral|utmcct=/contents/COM/SiteSearch.jsp");
			conn.setRequestProperty("Host", "marketdata.krx.co.kr");
			conn.setRequestProperty("Origin", "marketdata.krx.co.kr");
			conn.setRequestProperty("Referer", "http://marketdata.krx.co.kr/mdi");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.109 Safari/537.36");
			conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			StringBuffer postParameter = new StringBuffer();
			for( int paramCount = 0 ; paramCount < params.length ; paramCount++ ) {
				postParameter.append("&").append(params[paramCount][0]).append("=").append(URLEncoder.encode(params[paramCount][1], "utf-8"));
			}
			os = conn.getOutputStream();
			os.write(postParameter.substring(1).getBytes());
			System.out.println(postParameter.substring(1));
			os.flush();
			os.close();
			is = conn.getInputStream();
			byte[] buffer = new byte[1024];
			int length = 0;
			while((length = is.read(buffer,0,1024)) != -1 ) {
				baos.write(buffer, 0, length);
			}
			jsonArray = new String(baos.toByteArray(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if ( is != null ) try { is.close(); } catch ( Exception e1 ) {e1.printStackTrace();}
			if ( baos != null ) try { baos.close(); } catch ( Exception e1 ) {e1.printStackTrace();}
			if ( conn != null ) try { conn.disconnect(); } catch ( Exception e1 ) {e1.printStackTrace();}
		}
		return jsonArray;
	}


}
