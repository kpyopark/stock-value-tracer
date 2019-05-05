package robot.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TestClient {
	String url = "http://datamall.koscom.co.kr/servlet/cyberir/CyberIrCurrentServlet?screenId=301003&simplecode=005930";
	public TestClient(){
		
	}
	
	public void transfer() throws Exception {
		Socket soc = null;
		PrintWriter pw = null;
		BufferedReader br = null;
		try {
			soc = new Socket("datamall.koscom.co.kr",80);
			pw = new PrintWriter(soc.getOutputStream());
			pw.println("GET /servlet/cyberir/CyberIrCurrentServlet?screenId=301003&simplecode=005930 HTTP/1.1");
			pw.println("User-Agent: Java/1.5.0_04");
			pw.println("Host: datamall.koscom.co.kr");
			pw.println("Accept: text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");
			pw.println("Content-type: application/x-www-form-urlencoded");
			pw.println("");
			pw.flush();

			br = new BufferedReader( new InputStreamReader(soc.getInputStream()) );
			String aLine = null;
			while( ( aLine = br.readLine()) != null ) {
				System.out.println( aLine );
			}
		} catch ( Exception e ) {
			throw e;
		} finally {
			if ( soc != null ) try { soc.close(); } catch ( Exception e1 ) {}
			if ( pw != null ) try { pw.close(); } catch ( Exception e2 ) {}
		}
		
		//HttpClient client = new HttpClient();
		
		//HttpMethod method = new GetMethod(url);
		//client.executeMethod(method);
		//method.releaseConnection();
	}
	
	public static void main(String[] args) throws Exception {
		TestClient client = new TestClient();
		//for ( int cnt = 0 ; )
		client.transfer();
	}
	
}
