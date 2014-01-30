package common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


import post.Company;
import post.StockEstimated;

public class StockEstimationResourceWithYahoo {
	
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
	
	static StockEstimationResourceWithYahoo resource = null;
	
	HttpClient client = null;
	
	private StockEstimationResourceWithYahoo() {
		//System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
		//System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
		//System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.header", "debug");
		//System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "debug");
		client = new DefaultHttpClient();
	}
	
	public static StockEstimationResourceWithYahoo getStockResource() {
		if ( resource == null )
			resource = new StockEstimationResourceWithYahoo();
		return resource; 
	}
	
	public StockEstimated getCompanyStockEstimation(Company company) throws Exception {
		StockEstimated stockEstim = new StockEstimated();
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
			
			HttpPost method = new HttpPost("http://datamall.koscom.co.kr/servlet/cyberir/AjaxCyberIrCurrentServlet");
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("screenId", "301001"));
			pairs.add(new BasicNameValuePair("simplecode", "005930"));
			pairs.add(new BasicNameValuePair("style", ""));
			pairs.add(new BasicNameValuePair("lang", "kor"));
			method.setEntity(new UrlEncodedFormEntity(pairs));
			HttpResponse response = client.execute(method);
			
			if ( response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
		        System.err.println("Method failed: " + response.getStatusLine().toString() );
		    }
			is = response.getEntity().getContent();
			br = new BufferedReader( new InputStreamReader( is ) );
			try {
				for ( String aLine = br.readLine() ; aLine != null ; aLine = br.readLine() ) {
					System.out.println(aLine);
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
	            stockEstim.setCompany(company);
	            stockEstim.setStandardDate(STANDARD_DATE.format(currentDate));
	            
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
		return stockEstim;
	}
	
	public static void main(String[] args) {
		StockEstimationResourceWithYahoo sr = new StockEstimationResourceWithYahoo();
		try {
			Company company = new Company();
			company.setId("A003600");
			company.setName("SK");
			// KT 030200
			// KTF 032390
			// LG필립스LCD 034220
			System.out.println(sr.getCompanyStockEstimation(company));
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
}

