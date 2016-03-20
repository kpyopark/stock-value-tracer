package internetResource.stock;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;

import post.Company;
import post.Stock;

import common.StringUtil;


public class StockResource {
	
	static SimpleDateFormat STANDARD_DATE = null;
	static SimpleDateFormat STANDARD_TIME = null;
	
	static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	static DocumentBuilder builder = null;
	static char[] charbuffer = null;
	static java.nio.CharBuffer buffer = null;
    
	
	static {
		try {
			STANDARD_DATE = new SimpleDateFormat("yyyyMMdd");
			STANDARD_TIME = new SimpleDateFormat("HHmmss");
			//factory.setValidating(false);
			//factory.setCoalescing(true);
			builder = factory.newDocumentBuilder();
			charbuffer = new char[1024];
			buffer = java.nio.CharBuffer.wrap(charbuffer);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	static StockResource resource = null;
	
	HttpClient client = null;
	
	private StockResource() {
		//System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
		//System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
		//System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.header", "debug");
		//System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "debug");
		client = new DefaultHttpClient();
	}
	
	public static StockResource getStockResource() {
		if ( resource == null )
			resource = new StockResource();
		return resource; 
	}
	
	public Stock getStock(Company company) throws Exception {
		Stock stock = new Stock();
		String value = null;
		//String volume = null;
		String stockId = null;

		Date currentDate = new java.util.Date();

		BufferedReader br = null;
		InputStream is = null;
		try {
			if ( company.getId().length() == 7 ) stockId = company.getId().substring(1);
			else {
				stockId = company.getId();
			}
			
			HttpGet method = new HttpGet("http://datamall.koscom.co.kr/servlet/cyberir/AjaxCyberIrCurrentServlet?screenId=301003&simplecode=" + stockId);

			HttpResponse response = client.execute(method);

			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
		        System.err.println("Method failed: " + response.getStatusLine().toString());
		    }
			is = response.getEntity().getContent();
			br = new BufferedReader( new InputStreamReader( is ) );
			int readCnt = 0;
			java.io.CharArrayWriter caw = new java.io.CharArrayWriter(5000);
			while( ( readCnt = br.read(buffer) ) != -1 ) {
				for ( int cnt = 0 ; cnt < readCnt ; cnt++ ) {
					char aChar = buffer.get(cnt);
					if ( aChar >= 0x20 ||  aChar == 0x9 || aChar == 0xA || aChar == 0xD || aChar == 0x13 ) //|| ( aChar >= 0x20 && aChar <= 0xD7FF ) || ( aChar >= 0xE000 && aChar <= 0xFFFD ) || ( aChar >= 0x10000 && aChar <= 0x10FFFF) )
						caw.write(aChar);
				}
				buffer.clear();
			}
			//java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(baos.toByteArray());
            Document document = builder.parse(new java.io.ByteArrayInputStream(caw.toString().getBytes()));
			try {
				stock.setCompany(company);
	            stock.setValue(Integer.parseInt(StringUtil.getNumericValue(document.getElementsByTagName("current").item(0).getTextContent())));
	            stock.setVolume(Integer.parseInt(StringUtil.getNumericValue(document.getElementsByTagName("volume").item(0).getTextContent())));
	            stock.setStandardDate(STANDARD_DATE.format(currentDate));
	            stock.setStandardTime(STANDARD_TIME.format(currentDate));
			} catch ( Exception e1 ) {
				System.out.println("해당 종목은 정보를 웹 상에서 확인해주세요.["+value+"]["+company+"]");
            	//e1.printStackTrace();
            	return null;
			} finally {
				method.releaseConnection();
			}
		} catch ( Exception e ) {
			e.printStackTrace();
			System.out.println("다음 회사정보 주가를 읽는 중 에러발생:"+company);
			throw e;
		}
		return stock;
	}
	
	public static void main(String[] args) {
		StockResource sr = new StockResource();
		try {
			Company company = new Company();
			company.setId("A004540");
			//company.setName("아모레퍼시픽");
			// KT 030200
			// KTF 032390
			// LG필립스LCD 034220
			System.out.println(sr.getStock(company));
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
}

