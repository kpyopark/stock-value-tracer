package analyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.commons.collections4.list.TreeList;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFHyperlink;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import post.Company;
import post.CompanyEx;
import post.StockEstimated;
import post.StockRank;
import dao.CompanyDao;
import dao.CompanyExDao;
import dao.CompanyFinancialStatusDao;
import dao.CompanyStockEstimationDao;
import estimator.FinancialStatusEstimator;

public class StockAnalyzer {

	static SimpleDateFormat FILE_DATE_FORMAT = null;
	
	static {
		FILE_DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd.HHmmss");
	}
	
	String standardDate;

	ArrayList<CompanyEx> companyList = null;
	ArrayList<StockEstimated> stockEstimList = new ArrayList<StockEstimated>();
	TreeList<StockRank> stockRankList = new TreeList<StockRank>();
	//ArrayList<StockRank> stockRankList = new ArrayList<StockRank>();
	CompanyStockEstimationDao stockEstimDao = new CompanyStockEstimationDao();
	CompanyExDao companyDao = new CompanyExDao();
	CompanyFinancialStatusDao financialStatusDao = new CompanyFinancialStatusDao();
	
	public StockAnalyzer(String standardDate) throws Exception {
		this.standardDate = standardDate;
		getCompanyList();
		getCompanyStockEstimationList();
	}
	
	private void getCompanyList() throws java.sql.SQLException {
		companyList = companyDao.selectAllList(this.standardDate);
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
		stockEstimList.clear();
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
		System.out.println("Name;Id;Per;Roa;Roe;Bpp;E.Y;Tot;Est;AvePer;AveRoe;AveRoa;AveBpp;EarningYeild;DebtRatio:AveDividendRatio;RecentEps;RecentStockValue;LastEps;Date;size;URL");
		for ( int cnt = 0 ; cnt < rank ; cnt++ ) {
			//System.out.println( stockRankList.get(cnt));
			System.out.println(stockRankList.get(cnt).getCompany().getName() + ";" +
					stockRankList.get(cnt).getCompany().getId() + ";" +
					stockRankList.get(cnt).getPerRank() + ";" + 
					stockRankList.get(cnt).getRoaRank() + ";" +
					stockRankList.get(cnt).getRoeRank() + ";" +
					stockRankList.get(cnt).getBppRank() + ";" +
					stockRankList.get(cnt).getEarningYieldRank() + ";" +
					stockRankList.get(cnt).getTotRank() + ";" +
					stockRankList.get(cnt).getStockEstimation().getEstimKind() + ";" +
					stockRankList.get(cnt).getStockEstimation().getAvePer() + ";" +
					stockRankList.get(cnt).getStockEstimation().getAveRoe() + ";" +
					stockRankList.get(cnt).getStockEstimation().getAveRoa() + ";" +
					stockRankList.get(cnt).getStockEstimation().getAveBpp() + ";" +
					stockRankList.get(cnt).getStockEstimation().getEarningYield() + ";" +
					stockRankList.get(cnt).getStockEstimation().getDebtRatio() + ";" +
					stockRankList.get(cnt).getStockEstimation().getAveDividendRatio() + ";" +
					stockRankList.get(cnt).getStockEstimation().getRecentEps() + ";" +
					stockRankList.get(cnt).getStockEstimation().getRecentStockValue() + ";" +
					stockRankList.get(cnt).getStockEstimation().getLastEps() + ";" +
					stockRankList.get(cnt).getStockEstimation().getRelatedDateList() + ";" +
					FinancialStatusEstimator.getLatestOrdinarySharesSize(stockRankList.get(cnt).getCompany(), registeredDate) + ";" +
					"http://finance.naver.com/item/main.nhn?code=" + stockRankList.get(cnt).getCompany().getId().substring(1)
					);
		}
	}
	
	void printStockListToExcel(int rank) {
		File newExcel = null;
		newExcel = new File("F:\\Document\\00.순매수-순매도\\beststock_" + FILE_DATE_FORMAT.format(new Date()) + ".xls" );
		createExcelFile(newExcel, rank);
	}
	
	void printStockListToXML(int rank, String registeredDate) {
		File newXML = null;
		newXML = new File("F:\\Document\\00.순매수-순매도\\beststock_" + FILE_DATE_FORMAT.format(new Date()) + ".xml" );
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
		//calculateRankByAdjustRoe();
		calculateRankByEarningYield();
		calculateRankByBpp();
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
			} else {
				stockRank.setStockEstimation(stockEstimList.get(cnt));
				stockRankList.add(stockRank);
			}
			stockRank.setRoeRank(cnt+1);
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
			} else {
				stockRank.setStockEstimation(stockEstimList.get(cnt));
				stockRankList.add(stockRank);
			}
			stockRank.setRoaRank(cnt+1);
		}
	}

	@Deprecated
	private void calculateRankByAdjustRoe() {
		java.util.Collections.sort(stockEstimList,new AdjustRoeComparator());
		int rank = 1;
		for ( int cnt = 0 ; cnt < stockEstimList.size() ; cnt++ ) {
			StockRank stockRank = new StockRank();
			stockRank.setCompany(stockEstimList.get(cnt).getCompany());
			if ( stockRankList.contains(stockRank) ) {
				stockRank = stockRankList.get(stockRankList.indexOf(stockRank));
			} else {
				stockRank.setStockEstimation(stockEstimList.get(cnt));
				stockRankList.add(stockRank);
			}
			if ( stockEstimList.get(cnt).getAveRoe() >= 0 ) {
				rank = cnt+1;
			}
			stockRank.setRoeRank(rank);
		}
	}

	private void calculateRankByBpp() {
		java.util.Collections.sort(stockEstimList,new BppComparator());
		int rank = 1;
		for ( int cnt = 0 ; cnt < stockEstimList.size() ; cnt++ ) {
			StockRank stockRank = new StockRank();
			stockRank.setCompany(stockEstimList.get(cnt).getCompany());
			if ( stockRankList.contains(stockRank) ) {
				stockRank = stockRankList.get(stockRankList.indexOf(stockRank));
			} else {
				stockRank.setStockEstimation(stockEstimList.get(cnt));
				stockRankList.add(stockRank);
			}
			if ( stockEstimList.get(cnt).getAveBpp() >= 0 ) {
				rank = cnt+1;
			}
			stockRank.setBppRank(rank);
		}
	}

	private void calculateRankByEarningYield() {
		java.util.Collections.sort(stockEstimList,new EarningYieldComparator());
		for ( int cnt = 0 ; cnt < stockEstimList.size() ; cnt++ ) {
			StockRank stockRank = new StockRank();
			stockRank.setCompany(stockEstimList.get(cnt).getCompany());
			if ( stockRankList.contains(stockRank) ) {
				stockRank = stockRankList.get(stockRankList.indexOf(stockRank));
			} else {
				stockRank.setStockEstimation(stockEstimList.get(cnt));
				stockRankList.add(stockRank);
			}
			stockRank.setEarningYieldRank(cnt+1);
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
			//stockRank.setTotRank(stockRank.getPerRank()+stockRank.getRoaRank());
			stockRank.setTotRank((int)(stockRank.getRoaRank() * 0.3 + stockRank.getRoeRank() * 0.5 + stockRank.getEarningYieldRank() * 0.8 + stockRank.getBppRank() * 0.2));
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
			sb.append("<linkurl>").append("http://finance.naver.com/item/main.nhn?code=" + stockRankList.get(cnt).getCompany().getId().substring(1)).append("</linkurl>");
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
	
	final static String[] HEADERS = { "NAME","ID","PER","ROA","ROE","BPP","E.Y",
		"TOT","EST","SECTOR","GROUP","INDUSTRY","AVEPER","AVEROE","AVEROA",
		"AVEBPP", "EARNING_RATIO", "DEBT_RATIO",
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
		row.createCell(column++).setCellValue(rankInfo.getBppRank());
		row.createCell(column++).setCellValue(rankInfo.getEarningYieldRank());
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
		cell.setCellValue(rankInfo.getStockEstimation().getAveBpp());
		cell.setCellStyle(percentStyle);
		cell = row.createCell(column++);
		cell.setCellValue(rankInfo.getStockEstimation().getEarningYield());
		cell.setCellStyle(percentStyle);
		cell = row.createCell(column++);
		cell.setCellValue(rankInfo.getStockEstimation().getDebtRatio());
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
		// 1. All registered date from the estim stat.
		// 2. In each registered date, retreive all company list.
		// 3. Analyze all.
		String standardDate = STANDARD_DATE_FORMAT.format(new Date());
		StockAnalyzer stockAnal = new StockAnalyzer(standardDate);
		stockAnal.getBestStockList(500, standardDate);
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

class AdjustRoeComparator implements java.util.Comparator<StockEstimated> {

	/**
	 * 
	 * @param debtRatio
	 * @param roe
	 * @return
	 * 
	 * When Debt/Asset ratio is between 0 and 0.5, the the value of roe is exactly same to it's value
	 * When debt/asset ratio is between 0.5 and 0.9, the value of roe is decreased. at the point 0.9. the value of roe is 0. it's meaningless.
	 *      y = -2.5x + 2.25 ( adjusted factor )
	 * 
	 * But. The debt ratio also effects the earning of the company. so. this methods the enlarge the effect of debt too much.
	 */
	private static float calculateAdjustRoe(float debtRatio, float roe) {
		if ( roe < 0 )
			return roe;
		if ( debtRatio >= 0 && debtRatio <= 0.5 )
			return roe;
		else if ( debtRatio > 0.5 && debtRatio < 0.9 )
			return ( debtRatio * (float)-2.5 + (float)2.25 ) * roe;
		else
			return 0;
	}
	
	public int compare(StockEstimated src, StockEstimated tgt) {
		return calculateAdjustRoe( src.getDebtRatio(), src.getAveRoe() ) > calculateAdjustRoe(tgt.getDebtRatio(), src.getAveRoe() ) ? 1 : -1;
	}
}

class BppComparator implements java.util.Comparator<StockEstimated> {
	public int compare(StockEstimated src, StockEstimated tgt) {
		return src.getAveBpp() < tgt.getAveBpp() ? 1 :
			src.getAveBpp() == tgt.getAveBpp() ? 0 : -1;
	}
}

class PerComparator implements java.util.Comparator<StockEstimated> {
	public int compare(StockEstimated src, StockEstimated tgt) {
		//if ( src.getAvePer() == 0 ) return 1;
		return src.getAvePer() > tgt.getAvePer() ? 1 :
			src.getAvePer() == tgt.getAvePer() ? 0 : -1;
	}
}

class EarningYieldComparator implements java.util.Comparator<StockEstimated> {
	public int compare(StockEstimated src, StockEstimated tgt) {
		return ( src.getEarningYield() < tgt.getEarningYield() ? 1 : -1 );
	}
}

class TotComparator implements java.util.Comparator<StockRank> {
	public int compare(StockRank src, StockRank tgt) {
		return src.getTotRank() > tgt.getTotRank() ? 1 :
			src.getTotRank() == tgt.getTotRank() ? 0 : -1;
	}
}


