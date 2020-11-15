package internetResource.stock;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

/**
 * 
 * Don't use this class until all standard stock code are gathered into database.
 * 
 */
public class StockResourceFromKrx {
	
	static HtmlCleaner cleaner;
	static {
		cleaner = new HtmlCleaner();
	}
	
	private static String SECRET_KEY = "//*[@id=\"se_key\"]";
	
	final static String STOCK_RESOURCE_URL1 = "http://www.krx.co.kr/m2/m2_1/m2_1_4/JHPKOR02001_04.jsp";
	final static String STOCK_RESOURCE_URL2 = "http://www.krx.co.kr/por_kor/corelogic/process/m2/m2_1/m2_1_4/hpkor02001_04.xhtml?data-only=true";
	final static String STOCK_RESOURCE_URL_TEST = "http://localhost:8080/index.html?data-only=true";
	
	// The required fields of real-time stock value xml are 'se_key' and 'isu_cd'
	// 'isu_cd' is the standard code of target item.
	// 'isu_cd' can be acquired from another page.
	
	String[][] stockResourceParams = {
		{ "se_key", "h2vlHsaQb4ibg4pgAqqvzA==" },
		{ "isu_cd", "KR7005930003" },
		{ "isu_nm", "삼성전자 [005930]" },
		{ "mthd" , "" },
		{ "fr_work_dt", "20140301" },
		{ "to_work_dt", "20140330" },
		{ "searchBtn", "" },
		{ "searchBtn2", "조회" },
		{ "_", "" },
	};
	
	private StockResourceFromKrx() {
	}
	
	private String getSecretKey() {
		String secretKey = "";
		HttpURLConnection conn = null;
		BufferedReader br = null;
		TagNode secKeyXML = null;
		try {
			conn = (HttpURLConnection)new URL(STOCK_RESOURCE_URL1).openConnection();
			conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
			conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
			conn.setRequestProperty("Accept-Encoding","gzip,deflate,sdch");
			conn.setRequestProperty("Accept-Language","ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4");
			conn.setRequestProperty("Referer", "http://www.krx.co.kr/m2/m2_1/m2_1_4/JHPKOR02001_04.jsp");
			conn.setRequestProperty("Cookie", "JSESSIONID=8C473BDFA55CF1CD08A1C818E61906D2.node_tomcat102_8109; JSESSIONID=DDE959247AD49AECC732B49D8653EF04.node_tomcat102_8109; _EXEN=1; __utma=139639017.174687372.1395400314.1398942705.1398956062.27; __utmb=139639017.1.10.1398956062; __utmc=139639017; __utmz=139639017.1395484050.4.3.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided)");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.131 Safari/537.36");
			conn.setRequestProperty("X-Prototype-Version","1.6.1");
			conn.setRequestProperty("Origin","http://www.krx.co.kr");
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			secKeyXML = cleaner.clean(is);
			secretKey = ((TagNode)secKeyXML.evaluateXPath(SECRET_KEY)[0]).getAttributeByName("value");
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( conn != null ) try { conn.disconnect(); } catch ( Exception e1 ) {e1.printStackTrace();}
			if ( br != null ) try { br.close(); } catch ( Exception e1 ) {e1.printStackTrace();}
		}
		return secretKey;
	}
	
	/**
	 * 
	 * @param standardDate
	 * @param id
	 */
	// TODO :
	private void setParams(String standardDate, String id) {
		stockResourceParams[0][1] = getSecretKey();
	}
	
	private HttpURLConnection getURLConnection(String url) throws Exception {
		HttpURLConnection conn = null;
		conn = (HttpURLConnection)new URL(url).openConnection();
		conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
		conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
		conn.setRequestProperty("Accept-Encoding","gzip,deflate,sdch");
		conn.setRequestProperty("Accept-Language","ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4");
		conn.setRequestProperty("Cookie", "JSESSIONID=8C473BDFA55CF1CD08A1C818E61906D2.node_tomcat102_8109; JSESSIONID=DDE959247AD49AECC732B49D8653EF04.node_tomcat102_8109; _EXEN=1; __utma=139639017.174687372.1395400314.1398942705.1398956062.27; __utmb=139639017.1.10.1398956062; __utmc=139639017; __utmz=139639017.1395484050.4.3.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided)");
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.131 Safari/537.36");
		conn.setRequestProperty("X-Prototype-Version","1.6.1");
		conn.setRequestProperty("Origin","http://www.krx.co.kr");
		conn.setRequestMethod("POST");
		conn.setDoInput(true);
		conn.setDoOutput(true);
		return conn;
	}
	
	private void setRequestFormData(HttpURLConnection conn) throws Exception {
		OutputStream os = null;
		try {
			StringBuffer postParameter = new StringBuffer();
			for( int paramCount = 0 ; paramCount < stockResourceParams.length ; paramCount++ ) {
				postParameter.append("&").append(stockResourceParams[paramCount][0]).append("=").append(URLEncoder.encode(stockResourceParams[paramCount][1]));
			}
			os = conn.getOutputStream();
			os.write(postParameter.substring(1).getBytes());
			os.write('\n');
			os.flush();
			os.close();
		} finally {
			if ( os != null ) try { os.close(); } catch ( Exception e ) {
				e.printStackTrace();
			}
		}
	}
	
	private TagNode getRootTagNode(String standardDate, String id) {
		TagNode itemListXML = null;
		HttpURLConnection conn = null;
		BufferedReader br = null;
		try {
			setParams(standardDate, id);
			conn = getURLConnection(STOCK_RESOURCE_URL2);
			conn.setRequestProperty("Referer", "http://www.krx.co.kr/m2/m2_1/m2_1_4/JHPKOR02001_04.jsp");
			setRequestFormData(conn);
			InputStream is = conn.getInputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int length = -1;
			byte[] buffer = new byte[4096];
			baos.write("<table>".getBytes());
			while( (length = is.read(buffer, 0, 4096)) != -1 ) {
				baos.write(buffer, 0, length);
			}
			baos.write("</table>".getBytes());
			System.out.println(new String(baos.toByteArray()));
			itemListXML = cleaner.clean(new String(baos.toByteArray(), "utf-8"));
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( conn != null ) try { conn.disconnect(); } catch ( Exception e1 ) {e1.printStackTrace();}
			if ( br != null ) try { br.close(); } catch ( Exception e1 ) {e1.printStackTrace();}
		}
		return itemListXML;
	}
	
	public static void main(String[] args) {
		StockResourceFromKrx sr = new StockResourceFromKrx();
		try {
			//startEchoServer();
			System.out.println(sr.getRootTagNode("20140430", "A005930"));
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public static void startEchoServer() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				ServerSocket serverSocket = null;
				InputStream is = null;
				try {
					serverSocket = new ServerSocket(8080);
					Socket socket = serverSocket.accept();
					is = socket.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(is));
					String aLine = null;
					while( (aLine = br.readLine()) != null ) {
						System.out.println("#" + aLine);
					}
				} catch ( Exception e ) {
					e.printStackTrace();
				} finally {
					if ( serverSocket != null ) try { serverSocket.close(); } catch ( Exception e ) { e.printStackTrace(); }
				}
			}
		}).start();
	}
	
}

