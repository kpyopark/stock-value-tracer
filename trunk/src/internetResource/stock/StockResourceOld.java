package internetResource.stock;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


import post.Company;
import post.Stock;

/**
 * @deprecated
 * @author user
 *
 */
public class StockResourceOld {
	
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
	
	static StockResourceOld resource = null;
	
	HttpClient client = null;
	
	private StockResourceOld() {
		//System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
		//System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
		//System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.header", "debug");
		//System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "debug");
		client = new DefaultHttpClient();
	}
	
	public static StockResourceOld getStockResource() {
		if ( resource == null )
			resource = new StockResourceOld();
		return resource; 
	}
	
	public Stock getStock(Company company) throws Exception {
		Stock stock = new Stock();
		String value = null;
		String volume = null;
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
		        System.err.println("Method failed: " + response.getStatusLine());
		    }
			is = response.getEntity().getContent();
			br = new BufferedReader( new InputStreamReader( is ) );
			try {
				for ( String aLine = br.readLine() ; aLine != null ; aLine = br.readLine() ) {
					if ( aLine.contains("<td width=\"107\" class=\"td_color\">현재가</td>") || aLine.contains("<td width=\"25%\" class=\"td_color\">현재가</td>") ) {
						aLine = br.readLine();
						value = aLine.trim().
							replaceAll("<td class=\"tb_data\" align=\"right\">", "").
							replaceAll("<td width=\"25%\" class=\"tb_data\" align=\"right\">", "").
							replaceAll("<font class=price_up_color>","").
							replaceAll("<font class=price_dw_color>","").
							replaceAll(",", "").
							replaceAll("</font></td>","").
							replaceAll("</td>","").trim(); 
					}
					if ( aLine.contains("\">거래량(주)</td>") ) {
						aLine = br.readLine();
						volume = aLine.trim().replaceAll("<td class=\"tb_data\" align=\"right\">","").replaceAll("</td>","").replaceAll(",","");
						break;
					}
				}
	            stock.setCompany(company);
	            stock.setValue(Integer.parseInt(value));
	            stock.setVolume(Integer.parseInt(volume));
	            stock.setStandardDate(STANDARD_DATE.format(currentDate));
	            stock.setStandardTime(STANDARD_TIME.format(currentDate));
			} catch ( Exception e1 ) {
				System.out.println("해당 종목은 정보를 웹 상에서 확인해주세요.["+value+"]["+company+"]");
            	e1.printStackTrace();
            	return null;
			} finally {
				method.releaseConnection();
			}
		} catch ( Exception e ) {
			e.printStackTrace();
			throw e;
		}
		return stock;
	}
	
	public static void main(String[] args) {
		StockResourceOld sr = new StockResourceOld();
		try {
			Company company = new Company();
			company.setId("A005930");
			company.setName("삼성전자");
			// KT 030200
			// KTF 032390
			// LG필립스LCD 034220
			System.out.println(sr.getStock(company));
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
}

