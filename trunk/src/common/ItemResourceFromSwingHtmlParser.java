package common;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import org.htmlparser.Parser;

import post.Company;


public class ItemResourceFromSwingHtmlParser {
	
	static String ITEM_ID_URL = null;
	
	static {
		ITEM_ID_URL = "http://www.fnguide.com/SVO/Handbook_New/html/SVD_Main_XXX.htm?pGB=4";
	}
	
	public Company getItem(String id) throws Exception {
		Company companyInfo = new Company();
		try {
			Parser parser = new Parser("http://www.fnguide.com/SVO/Handbook_New/html/SVD_Main_A005930.htm?pGB=4");
			parser.setEncoding("EUC-KR");
			Filter1 filter1 = new Filter1();
			System.out.println( filter1.getContent(parser,"발행주식수(보통)") );
		} catch ( Exception e ) {
			throw e;
		} finally {
			//if ( conn == null ) try { conn.disconnect(); } catch ( Exception e1 ) {e1.printStackTrace();}
			//if ( br != null ) try { br.close(); } catch ( Exception e1 ) {e1.printStackTrace();}
			//else if ( is != null ) try { is.close(); } catch ( Exception e1 ) {e1.printStackTrace();}
		}
		return companyInfo;
	}
	
	/*
	private Company parseHtmlDocument(HTMLDocument doc) {
		Company companyInfo = new Company();
		// Parsing Rull
		Element root = doc.getDefaultRootElement();
		printElementFull(root,0);
		return companyInfo;
	}
	
	private void printElementFull(Element parent, int depth) {
		String pandding = "";
		for( int tmp = 0; tmp < depth ; tmp++ ) {
			pandding += " ";
		}
		for( int cnt = 0; cnt < parent.getElementCount() ; cnt++ ) {
			Element child = parent.getElement(cnt);
			System.out.print(pandding + child);
			if ( child.getElementCount() > 0 ) {
				printElementFull(child, depth+1);
			}
		}
	}
	*/
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
		ItemResourceFromSwingHtmlParser ir = new ItemResourceFromSwingHtmlParser();
		try {
			ir.getItem(iir.getId("삼성전자"));
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	// Parsing Test.
	/*
	String aLine = null;
	while( (aLine = br.readLine() ) != null ) {
	//	System.out.println(aLine);
	}
	*/
	/*
	StringBean sb = new StringBean();
	Parser parser = new Parser("http://www.fnguide.com/SVO/Handbook_New/html/SVD_Main_A005930.htm?pGB=4");
	HtmlPage page = new HtmlPage(parser);
	parser.setEncoding("EUC-KR");
	parser.visitAllNodesWith(page);
	TableTag[] tables = page.getTables();
	for ( int cnt = 0 ; tables != null && cnt < tables.length ; cnt++ ) {
		System.out.println( "TABLE CONTENT[" + cnt + "]:" + tables[cnt].getStringText() );
	}
	*/
	/*
	parser.visitAllNodesWith(sb);
	String s = sb.getStrings();
	sb.setLinks(true);
	parser.reset();
	parser.visitAllNodesWith(sb);
	String sl = sb.getStrings();
	System.out.println("s:" + s);
	System.out.println("sl:" + sl);
	*/
	/*
	EditorKit kit = new HTMLEditorKit();
	HTMLDocument doc = (HTMLDocument)kit.createDefaultDocument();
	doc.putProperty("IgnoreCharsetDirective", new Boolean(true));
	System.out.println( doc.getReader(0));
	kit.read(br,doc,0);
	companyInfo = parseHtmlDocument(doc);
	*/

	
}
