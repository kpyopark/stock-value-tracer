import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;


public class TextViewer {
	
	public static void main(String[] args) throws Exception {
		
		URL url = new URL("http://blog.naver.com/NBlogMain.nhn?blogId=mokpojsk&Redirect=Log&logNo=130003827952&");
		// http://blog.naver.com/mokpojsk?Redirect=Log&logNo=130003827952
		// http://blog.naver.com/NBlogMain.nhn?blogId=mokpojsk&Redirect=Log&logNo=130003827952&
		// /NBlogHidden.nhn?blogId=mokpojsk&musicYN=1
		URLConnection con = url.openConnection();
		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String aLine = null;
		
		while( ( aLine = br.readLine() ) != null )
			System.out.println( aLine );
		
		br.close();
		
	}
}
