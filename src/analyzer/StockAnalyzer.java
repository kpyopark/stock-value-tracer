package analyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import post.Company;
import post.CompanyFinancialStatus;
import post.StockEstimated;
import post.StockRank;
import dao.CompanyDao;
import dao.CompanyFinancialStatusDao;
import dao.CompanyStockEstimationDao;
import estimator.FinancialStatusEstimator;

public class StockAnalyzer {
	
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
			StockEstimated stockEstim = stockEstimDao.select(companyList.get(cnt));
			if ( stockEstim != null )
				stockEstimList.add(stockEstim);
		}
	}
	
	private void printStockListToConsole(int rank) {
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
					FinancialStatusEstimator.getLatestOrdinarySharesSize(stockRankList.get(cnt).getCompany()) + ";" +
					"http://stock.naver.com/sise/ksc_summary.nhn?code=" + stockRankList.get(cnt).getCompany().getId().substring(1)
					);
		}
	}
	
	private void printStockListToExcel(int rank) {
		
	}
	
	public void getBestStockList(int rank) throws java.sql.SQLException {
		calculateRankByRoe();
		calculateRankByRoa();
		calculateRankByPer();
		calculateTotRank();
		printStockListToConsole(rank);
		printStockListToExcel(rank);
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
		    HSSFRow row = sheet1.createRow((short)0);
			printData(wb,row,stockRankList.get(cnt));
		}

	    FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream("workbook.xls");
		    wb.write(fileOut);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if ( fileOut != null ) try { fileOut.close(); } catch ( Exception e ) {}
		}

	}
	
	final static String[] HEADERS = { "NAME","ID","PER","ROA","ROE",
		"TOT","EST","AVEPER","AVEROE","AVEROA",
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
		cell.setCellValue("http://stock.naver.com/sise/ksc_summary.nhn?code=" + rankInfo.getCompany().getId().substring(1));
	}
	
	public static void main(String[] args) throws Exception {
		StockAnalyzer stockAnal = new StockAnalyzer();
		stockAnal.getBestStockList(50);
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


