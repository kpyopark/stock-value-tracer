package analyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFHyperlink;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import post.Company;
import post.StockEstimated;
import post.StockRank;
import dao.CompanyDao;
import dao.CompanyFinancialStatusDao;
import dao.CompanyStockEstimationDao;
import estimator.FinancialStatusEstimator;

public class StockAnalyzer {

	static SimpleDateFormat DATE_FORMAT = null;
	
	static {
		DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd.HHmmss");
	}

	ArrayList<Company> companyList = new ArrayList<Company>();
	ArrayList<StockEstimated> stockEstimList = new ArrayList<StockEstimated>();
	ArrayList<StockRank> stockRankList = new ArrayList<StockRank>();
	CompanyStockEstimationDao stockEstimDao = new CompanyStockEstimationDao();
	CompanyDao companyDao = new CompanyDao();
	CompanyFinancialStatusDao financialStatusDao = new CompanyFinancialStatusDao();
	
	public StockAnalyzer() throws Exception {
		getCompanyList();
		getCompanyStockEstimationList();
	}
	
	private void getCompanyList() throws java.sql.SQLException {
		companyList = companyDao.selectAllList();
	}
	
	/**
	 * 최신 자료를 토대로 추산된 주가와, 재무제표 정보를 활용한 StockEstimation정보를
	 * 모든 회사를 대상으로 가지고 온다.
	 * 
	 * 단. 해당 회사의 주가 정보가 부정확할 경우 누락된다.
	 * 
	 * @throws java.sql.SQLException
	 */
	private void getCompanyStockEstimationList() throws java.sql.SQLException {
		for( int cnt = 0 ; cnt < companyList.size() ; cnt++ ) {
			if ( !companyList.get(cnt).isClosed() ) {
				StockEstimated stockEstim = stockEstimDao.select(companyList.get(cnt));
				if ( stockEstim != null )
					stockEstimList.add(stockEstim);
			} else {
				System.out.println("Company[" + companyList.get(cnt).getId() + ":" + companyList.get(cnt).getName() + "] should be ignored.");
			}
		}
	}
	
	private void printStockListToConsole(int rank, String registeredDate) {
		System.out.println("Name;Id;Per;Roa;Roe;Tot;Est;AvePer;AveRoe;AveRoa;AveDividendRatio;RecentEps;RecentStockValue;LastEps;Date;size;URL");
		for ( int cnt = 0 ; cnt < rank ; cnt++ ) {
			//System.out.println( stockRankList.get(cnt));
			System.out.println(stockRankList.get(cnt).getCompany().getName() + ";" +
					stockRankList.get(cnt).getCompany().getId() + ";" +
					stockRankList.get(cnt).getPerRank() + ";" + 
					stockRankList.get(cnt).getRoaRank() + ";" +
					stockRankList.get(cnt).getRoeRank() + ";" +
					stockRankList.get(cnt).getTotRank() + ";" +
					stockRankList.get(cnt).getStockEstimation().getEstimKind() + ";" +
					stockRankList.get(cnt).getStockEstimation().getAvePer() + ";" +
					stockRankList.get(cnt).getStockEstimation().getAveRoe() + ";" +
					stockRankList.get(cnt).getStockEstimation().getAveRoa() + ";" +
					stockRankList.get(cnt).getStockEstimation().getAveDividendRatio() + ";" +
					stockRankList.get(cnt).getStockEstimation().getRecentEps() + ";" +
					stockRankList.get(cnt).getStockEstimation().getRecentStockValue() + ";" +
					stockRankList.get(cnt).getStockEstimation().getLastEps() + ";" +
					stockRankList.get(cnt).getStockEstimation().getRelatedDateList() + ";" +
					FinancialStatusEstimator.getLatestOrdinarySharesSize(stockRankList.get(cnt).getCompany(), registeredDate) + ";" +
					"http://stock.naver.com/sise/ksc_summary.nhn?code=" + stockRankList.get(cnt).getCompany().getId().substring(1)
					);
		}
	}
	
	void printStockListToExcel(int rank) {
		File newExcel = null;
		newExcel = new File("C:\\Users\\user\\Documents\\00.순매수-순매도\\beststock_" + DATE_FORMAT.format(new Date()) + ".xls" );
		createExcelFile(newExcel, rank);
	}
	
	void printStockListToXML(int rank, String registeredDate) {
		File newXML = null;
		newXML = new File("C:\\Users\\user\\Documents\\00.순매수-순매도\\beststock_" + DATE_FORMAT.format(new Date()) + ".xml" );
		createXMLFile(newXML, rank, registeredDate);
	}

	
	/*
	static String HEADER_LIST[] = { 
		"Name",						// 0 
		"Id", 						// 1
		"Per",						// 2
		"Roa",						// 3
		"Roe",						// 4
		"Tot",						// 5
		"Est",						// 6
		"AvePer",					// 7
		"AveRoe",					// 8
		"AveRoa",					// 9
		"AveDividendRatio",			// 10
		"RecentEps",				// 11
		"RecentStockValue",			// 12
		"LastEps",					// 13
		"Date",						// 14
		"size",						// 15
		"URL"};						// 16
	
	private void printStockListToExcel(int rank) {
		File newExcel = null;
		try {
			newExcel = new File("C:\\Users\\user\\Documents\\00.순매수-순매도\\beststock_" + DATE_FORMAT.format(new Date()) + ".xls" );
			POIFSFileSystem fs=new POIFSFileSystem(new FileInputStream(newExcel));
			HSSFWorkbook workbook=new HSSFWorkbook(fs);
			HSSFSheet sheet=workbook.createSheet("Shee1");
			HSSFFont font = workbook.createFont();
			font.setFontName(HSSFFont.FONT_ARIAL);
			HSSFCellStyle titlestyle = workbook.createCellStyle();
			titlestyle.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
			titlestyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			titlestyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			titlestyle.setFont(font);
			HSSFRow row = sheet.createRow((short)0);
			for ( int column = 0 ; column < HEADER_LIST.length ; column++ ) {
				HSSFCell cell1 = row.createCell(column);
				cell1.setCellValue(HEADER_LIST[column]);
				cell1.setCellStyle(titlestyle);
			}
			HSSFCellStyle style = workbook.createCellStyle();
			style.setFont(font);
			for ( int cnt = 0 ; cnt < rank ; cnt++ ) {
				//System.out.println( stockRankList.get(cnt));
				int column = 0;
				HSSFRow contentRow = sheet.createRow(cnt+1);
				contentRow.createCell(column++).setCellValue(stockRankList.get(cnt).getCompany().getName());	// 0
				contentRow.createCell(column++).setCellValue(stockRankList.get(cnt).getCompany().getId());
				contentRow.createCell(column++).setCellValue(stockRankList.get(cnt).getPerRank());
				contentRow.createCell(column++).setCellValue(stockRankList.get(cnt).getRoaRank());
				contentRow.createCell(column++).setCellValue(stockRankList.get(cnt).getRoeRank());
				contentRow.createCell(column++).setCellValue(stockRankList.get(cnt).getTotRank());				// 5
				contentRow.createCell(column++).setCellValue(stockRankList.get(cnt).getStockEstimation().getEstimKind());
				contentRow.createCell(column++).setCellValue(stockRankList.get(cnt).getStockEstimation().getAvePer());
				contentRow.createCell(column++).setCellValue(stockRankList.get(cnt).getStockEstimation().getAveRoe());
				contentRow.createCell(column++).setCellValue(stockRankList.get(cnt).getStockEstimation().getAveRoa());
				contentRow.createCell(column++).setCellValue(stockRankList.get(cnt).getStockEstimation().getAveDividendRatio());
				contentRow.createCell(column++).setCellValue(stockRankList.get(cnt).getStockEstimation().getRecentEps());
				contentRow.createCell(column++).setCellValue(stockRankList.get(cnt).getStockEstimation().getRecentStockValue());
				contentRow.createCell(column++).setCellValue(stockRankList.get(cnt).getStockEstimation().getLastEps());
				contentRow.createCell(column++).setCellValue(stockRankList.get(cnt).getStockEstimation().getRelatedDateList());
				contentRow.createCell(column++).setCellValue(FinancialStatusEstimator.getLatestOrdinarySharesSize(stockRankList.get(cnt).getCompany()));
				contentRow.createCell(column++).setCellValue("http://stock.naver.com/sise/ksc_summary.nhn?code=" + stockRankList.get(cnt).getCompany().getId().substring(1));
			}
			
		} catch( Exception e ) {
			e.printStackTrace();
		} finally {
		}
	}
	*/
	public void getBestStockList(int rank, String registeredDate) throws java.sql.SQLException {
		calculateRankByRoe();
		calculateRankByRoa();
		calculateRankByPer();
		calculateTotRank();
		printStockListToConsole(rank, registeredDate);
		printStockListToExcel(rank);
		printStockListToXML(rank, registeredDate);
	}
	
	private void calculateRankByRoe() {
		java.util.Collections.sort(stockEstimList,new RoeComparator());
		for ( int cnt = 0 ; cnt < stockEstimList.size() ; cnt++ ) {
			StockRank stockRank = new StockRank();
			stockRank.setCompany(stockEstimList.get(cnt).getCompany());
			stockRank.setStockEstimation(stockEstimList.get(cnt));
			if ( stockRankList.contains(stockRank) ) {
				stockRank = stockRankList.get(stockRankList.indexOf(stockRank));
				stockRank.setRoeRank(cnt+1);
			} else {
				stockRank.setRoeRank(cnt+1);
				stockRankList.add(stockRank);
			}
		}
	}

	private void calculateRankByRoa() {
		java.util.Collections.sort(stockEstimList,new RoaComparator());
		for ( int cnt = 0 ; cnt < stockEstimList.size() ; cnt++ ) {
			StockRank stockRank = new StockRank();
			stockRank.setCompany(stockEstimList.get(cnt).getCompany());
			stockRank.setStockEstimation(stockEstimList.get(cnt));
			if ( stockRankList.contains(stockRank) ) {
				stockRank = stockRankList.get(stockRankList.indexOf(stockRank));
				stockRank.setRoaRank(cnt+1);
			} else {
				stockRank.setRoaRank(cnt+1);
				stockRankList.add(stockRank);
			}
		}
	}

	private void calculateRankByPer() {
		java.util.Collections.sort(stockEstimList,new PerComparator());
		for ( int cnt = 0 ; cnt < stockEstimList.size() ; cnt++ ) {
			StockRank stockRank = new StockRank();
			stockRank.setCompany(stockEstimList.get(cnt).getCompany());
			stockRank.setStockEstimation(stockEstimList.get(cnt));
			if ( stockRankList.contains(stockRank) ) {
				stockRank = stockRankList.get(stockRankList.indexOf(stockRank));
				stockRank.setPerRank(cnt+1);
			} else {
				stockRank.setPerRank(cnt+1);
				stockRankList.add(stockRank);
			}
		}
	}
	
	private void calculateTotRank() {
		Iterator iter = stockRankList.iterator();
		while( iter.hasNext() ) {
			StockRank stockRank = (StockRank)iter.next();
			stockRank.setTotRank(stockRank.getPerRank()+stockRank.getRoaRank());
		}
		java.util.Collections.sort(stockRankList,new TotComparator());
	}
	
	private void createExcelFile(File targetFile, int rank) {
		
		HSSFWorkbook wb = new HSSFWorkbook();
		
	    HSSFSheet sheet1 = wb.createSheet("data_sheet");
	    
	    HSSFRow headerRow = sheet1.createRow((short)0);
	    printHeader(headerRow);

		for ( int cnt = 0 ; cnt < rank ; cnt++ ) {
			//System.out.println( stockRankList.get(cnt));
		    HSSFRow row = sheet1.createRow((short)cnt+1);
			printData(wb,row,stockRankList.get(cnt));
		}

	    FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream(targetFile);
		    wb.write(fileOut);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if ( fileOut != null ) try { fileOut.close(); } catch ( Exception e ) {}
		}

	}
	
	public static String getUtf8String(String eucKr) {
		String utf8 = null;
		try {
		if ( eucKr != null )
			utf8 = new String(eucKr.getBytes("utf-8"), "utf-8");
		} catch ( Exception e ) { e.printStackTrace(); }
		return utf8;
	}

	/*
	 * Schema
	 * name
	 * id
	 * 
	 */
	private void createXMLFile(File targetFile, int rank, String registeredDate) {
		StringBuffer sb = new StringBuffer();
		sb.append("<root>");
		for ( int cnt = 0 ; cnt < rank ; cnt++ ) {
			sb.append("<item>");
			sb.append("<name>").append(getUtf8String(stockRankList.get(cnt).getCompany().getName())).append("</name>");
			sb.append("<id>").append(stockRankList.get(cnt).getCompany().getId()).append("</id>");
			sb.append("<per>").append(stockRankList.get(cnt).getPerRank()).append("</per>");
			sb.append("<roa>").append(stockRankList.get(cnt).getRoaRank()).append("</roa>");
			sb.append("<roe>").append(stockRankList.get(cnt).getRoeRank()).append("</roe>");
			sb.append("<tot>").append(stockRankList.get(cnt).getTotRank()).append("</tot>");
			sb.append("<aveper>").append(stockRankList.get(cnt).getStockEstimation().getAvePer()).append("</aveper>");
			sb.append("<averoe>").append(stockRankList.get(cnt).getStockEstimation().getAveRoe()).append("</averoe>");
			sb.append("<averoa>").append(stockRankList.get(cnt).getStockEstimation().getAveRoa()).append("</averoa>");
			sb.append("<avedividened>").append(stockRankList.get(cnt).getStockEstimation().getAveDividendRatio()).append("</avedividened>");
			sb.append("<eps>").append(stockRankList.get(cnt).getStockEstimation().getRecentEps()).append("</eps>");
			sb.append("<stockvalue>").append(stockRankList.get(cnt).getStockEstimation().getRecentStockValue()).append("</stockvalue>");
			sb.append("<date>").append(stockRankList.get(cnt).getStockEstimation().getStandardDate()).append("</date>");
			sb.append("<size>").append(FinancialStatusEstimator.getLatestOrdinarySharesSize(stockRankList.get(cnt).getCompany(), registeredDate)).append("</size>");
			sb.append("<linkurl>").append("http://stock.naver.com/sise/ksc_summary.nhn?code=" + stockRankList.get(cnt).getCompany().getId().substring(1)).append("</linkurl>");
			sb.append("</item>\n");
		}
		sb.append("</root>");
	    FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream(targetFile);
			fileOut.write(sb.toString().getBytes());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if ( fileOut != null ) try { fileOut.close(); } catch ( Exception e ) {}
		}

	}
	
	final static String[] HEADERS = { "NAME","ID","PER","ROA","ROE",
		"TOT","EST","SECTOR","GROUP","INDUSTRY","AVEPER","AVEROE","AVEROA",
		"AVEDIV","REPS","RSTOCK","LEPS","DATE","URL"
	};
	
	private void printHeader(HSSFRow row) {
		for ( short column = 0; column < HEADERS.length ; column++ ) {
			row.createCell(column).setCellValue(HEADERS[column]);
		}
	}
	
	private void printData(HSSFWorkbook wb, HSSFRow row, StockRank rankInfo) {
		short column = 0;
	    HSSFCellStyle textStyle = wb.createCellStyle();
	    HSSFCellStyle perStyle = wb.createCellStyle();
	    HSSFCellStyle percentStyle = wb.createCellStyle();
	    HSSFCellStyle numStyle = wb.createCellStyle();
	    textStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));
	    perStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
	    percentStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));
	    numStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));
	    
	    //HSSFClientAnchor anchor = null;
	    
	    HSSFCell cell = null;
		row.createCell(column++).setCellValue(rankInfo.getCompany().getName());
		row.createCell(column++).setCellValue(rankInfo.getCompany().getId());
		row.createCell(column++).setCellValue(rankInfo.getPerRank());
		row.createCell(column++).setCellValue(rankInfo.getRoaRank());
		row.createCell(column++).setCellValue(rankInfo.getRoeRank());
		row.createCell(column++).setCellValue(rankInfo.getTotRank());
		row.createCell(column++).setCellValue(rankInfo.getStockEstimation().getEstimKind());
		row.createCell(column++).setCellValue(rankInfo.getCompany().getFicsSector());
		row.createCell(column++).setCellValue(rankInfo.getCompany().getFicsIndustryGroup());
		row.createCell(column++).setCellValue(rankInfo.getCompany().getFicsIndustry());
		cell = row.createCell(column++);
		cell.setCellValue(rankInfo.getStockEstimation().getAvePer());
		cell.setCellStyle(perStyle);
		cell = row.createCell(column++);
		cell.setCellValue(rankInfo.getStockEstimation().getAveRoe());
		cell.setCellStyle(percentStyle);
		cell = row.createCell(column++);
		cell.setCellValue(rankInfo.getStockEstimation().getAveRoa());
		cell.setCellStyle(percentStyle);
		cell = row.createCell(column++);
		cell.setCellValue(rankInfo.getStockEstimation().getAveDividendRatio());
		cell.setCellStyle(percentStyle);
		cell = row.createCell(column++);
		cell.setCellValue(rankInfo.getStockEstimation().getRecentEps());
		cell.setCellStyle(numStyle);
		cell = row.createCell(column++);
		cell.setCellValue(rankInfo.getStockEstimation().getRecentStockValue());
		cell.setCellStyle(numStyle);
		cell = row.createCell(column++);
		cell.setCellValue(rankInfo.getStockEstimation().getLastEps());
		cell.setCellStyle(numStyle);
		cell = row.createCell(column++);
		cell.setCellValue(rankInfo.getStockEstimation().getRelatedDateList());
		cell.setCellStyle(textStyle);
		cell = row.createCell(column++);
		cell.setCellValue("http://finance.naver.com/item/main.nhn?code=" + rankInfo.getCompany().getId().substring(1));
		HSSFHyperlink link = new HSSFHyperlink(HSSFHyperlink.LINK_URL);
		link.setAddress("http://finance.naver.com/item/main.nhn?code=" + rankInfo.getCompany().getId().substring(1));
		cell.setHyperlink(link);
	}
	
	static SimpleDateFormat STANDARD_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	
	public static void main(String[] args) throws Exception {
		StockAnalyzer stockAnal = new StockAnalyzer();
		String currentDate = STANDARD_DATE_FORMAT.format(new Date());
		stockAnal.getBestStockList(100, currentDate);
	}
	
}

class RoeComparator implements java.util.Comparator<StockEstimated> {
	public int compare(StockEstimated src, StockEstimated tgt) {
		return (src.getAveRoe() > tgt.getAveRoe() ? 1 :
			src.getAveRoe() == tgt.getAveRoe() ? 0 : -1) * -1;
	}
}

class RoaComparator implements java.util.Comparator<StockEstimated> {
	public int compare(StockEstimated src, StockEstimated tgt) {
		return (src.getAveRoa() > tgt.getAveRoa() ? 1 :
			src.getAveRoa() == tgt.getAveRoa() ? 0 : -1) * -1;
	}
}

class PerComparator implements java.util.Comparator<StockEstimated> {
	public int compare(StockEstimated src, StockEstimated tgt) {
		//if ( src.getAvePer() == 0 ) return 1;
		return src.getAvePer() > tgt.getAvePer() ? 1 :
			src.getAvePer() == tgt.getAvePer() ? 0 : -1;
	}
}
 
class TotComparator implements java.util.Comparator<StockRank> {
	public int compare(StockRank src, StockRank tgt) {
		return src.getTotRank() > tgt.getTotRank() ? 1 :
			src.getTotRank() == tgt.getTotRank() ? 0 : -1;
	}
}


