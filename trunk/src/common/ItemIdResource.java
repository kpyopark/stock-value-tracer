package common;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;

//import org.w3c.dom.*;

public class ItemIdResource {
	
	static String ITEM_ID_URL = null;
	
	static {
		ITEM_ID_URL = "http://comp.fnguide.com/svo/HandBook_new/SVD_list.asp?pGB=1&mkt_gb=&gicode=&market_gb=&keyword=";
	}
	
	public String getId(String name) throws Exception {
		HttpURLConnection conn = null;
		InputStream is = null;
		BufferedReader br = null;
		String id = null;
		try {
			conn = (HttpURLConnection)new URL(ITEM_ID_URL+name).openConnection();
			is = conn.getInputStream();
			br = new BufferedReader( new InputStreamReader(is) );
			
			//print all the html
			Pattern pattern = Pattern.compile("javascript:Go[(].........[)]");
	        Pattern pattern2 = Pattern.compile("[A-Z]\\p{Digit}{6}");
			for ( CharSequence aLine = br.readLine() ; aLine != null ; aLine = br.readLine() ) {
				System.out.println(aLine);
				Matcher matcher = pattern.matcher(aLine);
		        if ( matcher.find() ) {
		        	Matcher matcher2 = pattern2.matcher(matcher.group());
		        	if ( matcher2.find() ) {
				        id = matcher2.group();
			        	break;
		        	}
		        }
			}
			System.out.println( "id:" + id );
		} catch ( Exception e ) {
			throw e;
		} finally {
			if ( conn == null ) try { conn.disconnect(); } catch ( Exception e1 ) {e1.printStackTrace();}
			if ( br != null ) try { br.close(); } catch ( Exception e1 ) {e1.printStackTrace();}
			else if ( is != null ) try { is.close(); } catch ( Exception e1 ) {e1.printStackTrace();}
		}
		return id;
	}
	
    public CharSequence fromFile(String filename) throws IOException {
        FileInputStream fis = new FileInputStream(filename);
        FileChannel fc = fis.getChannel();
    
        // Create a read-only CharBuffer on the file
        ByteBuffer bbuf = fc.map(FileChannel.MapMode.READ_ONLY, 0, (int)fc.size());
        CharBuffer cbuf = Charset.forName("8859_1").newDecoder().decode(bbuf);
        return cbuf;
    }

	public static void main(String[] args) {
		ItemIdResource iir = new ItemIdResource();
		try {
			iir.getId("»ï¼ºÀüÀÚ");
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
}
