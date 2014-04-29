package internetResource.companyItem;

import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;

import common.NotNumericContentException;

import post.Company;
import post.CompanyFinancialStatus;

/**
 * @deprecated
 * @author user
 *
 */
public class RealTimeItemResource {
	
	static String ITEM_ID_URL = null;
	
	static {
		ITEM_ID_URL = "http://www.fnguide.com/SVO/Handbook_New/html/SVD_Main_XXX.htm?pGB=4";
	}
	
	private static long getLongValue(String content) throws NotNumericContentException {
		long rtn = 0;
		if ( content == null || content.trim().length() == 0 || content.trim().equals("&nbsp;") )
			return 0;
		try {
			rtn = Long.parseLong(content.replaceAll(",", ""));
		} catch ( Exception e ) {
			throw new NotNumericContentException(content + ":" + e.getMessage() );
		}
		return rtn;
	}
	
	private static float getFloatValue(String content) throws NotNumericContentException {
		float rtn = (float)0.0;
		if ( content == null || content.trim().length() == 0 || content.trim().equals("&nbsp;") )
			return 0;
		try {
			rtn = Float.parseFloat(content);
		} catch ( Exception e ) {
			throw new NotNumericContentException(content + ":" + e.getMessage() );
		}
		return rtn;
	}
	
	public CompanyFinancialStatus getRealTimeStockInfomation(Company company) throws Exception {
		CompanyFinancialStatus rtn = new CompanyFinancialStatus();
		try {
			Parser parser = new Parser("http://kr.stock.yahoo.com/sise/st01.html?code=" + company.getId() );
			parser.setEncoding("EUC-KR");
			Filter1 filter1 = new Filter1();
			NodeList per = filter1.getContent(parser,"주가수익비율(PER)");
			System.out.println( "1" );
			if ( per.size() > 0 ) {
				System.out.println( per.elementAt(0).toPlainTextString() );
			}
			/*
			for(int cnt = 0 ; cnt < dateList.size(); cnt++ ) {
				Company companyInfo = new Company();
				companyInfo.setName(name);
				companyInfo.setId(id);
				CompanyFinancialStatus financialStatus = new CompanyFinancialStatus();
				financialStatus.setCompany(companyInfo);
				if ( cnt < 4 ) // 현재 화면에서 4번재 column부터는 net quarter로 표시됨
					financialStatus.setQuarter(false);
				else
					financialStatus.setQuarter(true);
				
				String strDate = dateList.elementAt(cnt).toPlainTextString();
				if ( strDate.contains("(E)") ) {// 현재 변동중인 경우 
					financialStatus.setFixed(false);
					strDate = strDate.replaceAll("[(]E[)]", "");
				} else {
					financialStatus.setFixed(true);
				}
				strDate = strDate.replaceAll("[.]","");
				strDate += "01";
				financialStatus.setStandardDate(strDate);
				financialStatus.setAssets(getLongValue(assetList.elementAt(cnt).toPlainTextString())*100000000);
				financialStatus.setCapital(getLongValue(capitalList.elementAt(cnt).toPlainTextString())*100000000);
				financialStatus.setDebt(getLongValue(debtList.elementAt(cnt).toPlainTextString())*100000000);
				financialStatus.setDividendRatio(getFloatValue(devidenedRatioList.elementAt(cnt).toPlainTextString()));
				financialStatus.setNetProfit(getLongValue(netProfitList.elementAt(cnt).toPlainTextString())*100000000);
				financialStatus.setOperatingProfit(getLongValue(operatingProfitList.elementAt(cnt).toPlainTextString())*100000000);
				financialStatus.setSales(getLongValue(theSalesList.elementAt(cnt).toPlainTextString())*100000000);
				financialStatus.setOrdinarySharesSize(getLongValue(stockSizeList.elementAt(cnt).toPlainTextString())*1000);
				financialStatus.setRoa(getFloatValue(roaList.elementAt(cnt).toPlainTextString())/100);
				financialStatus.setRoe(getFloatValue(roeList.elementAt(cnt).toPlainTextString())/100);
				list.add(financialStatus);
			}
			*/
		} catch ( Exception e ) {
			System.out.println("GETTING FINANCIAL STATUS IS FAILED:" + company.getName() + "[" + company.getId() + "]" );
			throw e;
		} finally {
			//if ( conn == null ) try { conn.disconnect(); } catch ( Exception e1 ) {e1.printStackTrace();}
			//if ( br != null ) try { br.close(); } catch ( Exception e1 ) {e1.printStackTrace();}
			//else if ( is != null ) try { is.close(); } catch ( Exception e1 ) {e1.printStackTrace();}
		}
		return rtn;
	}
	
	public static void main(String[] args) {
		ItemIdResource iir = new ItemIdResource();
		//RealTimeItemResource ir = new RealTimeItemResource();
		try {
			String name = "삼성전자";
			String id = iir.getId(name);
			Company company = new Company();
			company.setName(name);
			company.setId(id);
			//CompanyFinancialStatus companyList = ir.getRealTimeStockInfomation(company);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

}
