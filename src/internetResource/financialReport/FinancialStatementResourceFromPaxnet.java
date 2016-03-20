package internetResource.financialReport;

import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import post.Company;
import post.CompanyEx;
import post.KrxSecurityType;
import post.StatementOfFinancialPosition;
import common.NotNumericContentException;
import common.StringUtil;
import common.TimeWatch;
import dao.CompanyDao;
import dao.CompanyExDao;
import dao.CompanyStatementOfFinancialPosition;

public class FinancialStatementResourceFromPaxnet {
	public static String ID_URL;
	
	static {
		ID_URL = "http://media.kisline.com/fininfo/mainFininfo.nice?paper_stock=066570&nav=4&header=N";
	}
	static String ID_URL(String companyId) {
		return "http://media.kisline.com/fininfo/mainFininfo.nice?paper_stock=" + companyId + "&nav=4&header=N";
	}
	
	static private enum StatementType { IFRS_SEPARATE("Sin0"), IFRS_CONSOLIDATED("Sin1"); 
		//,GAAP_SEPARATE("Sin2"), GAAP_CONSOLIDATED("Sin4");
		private String divType;
		StatementType(String divType) {
			this.divType = divType;
		}
		
		public String getDivType() {
			return divType;
		}
		
		public String getTypeNumber() {
			return divType.substring(3);
		}
	}
	
	static private enum StatementPeriodType { PERIOD_YEAR("yt"), PERIOD_QUATER("pt");
		private String periodType;
		StatementPeriodType(String periodType) {
			this.periodType = periodType;
		}
		public String getPeriodType() {
			return periodType;
		}
	}
	
	static String XPATH_ID = "//*[@id=\"container\"]/div[1]/div/p[1]/em[text()]";
	static String XPATH_STANDARD_DATE(StatementType type, StatementPeriodType periodType) {
		return "//*[@id=\"" + type.getDivType() + "\"]/div[@name=\"" + periodType.getPeriodType() + type.getTypeNumber() + "\"]/table/thead/tr/th";
	}
	static String XPATH_CONTENTS(StatementType type, StatementPeriodType periodType) {
		return "//*[@id=\"" + type.getDivType() + "\"]/div[@name=\"" + periodType.getPeriodType() + type.getTypeNumber() + "\"]/table/tbody/tr";
	}
	// ---> HEAD measn line count
	static String XPATH_CONTENTS_HEAD(StatementType type, StatementPeriodType periodType, int line) {
		return "//*[@id=\"" + type.getDivType() + "\"]/div[@name=\"" + periodType.getPeriodType() + type.getTypeNumber() + "\"]/table/tbody/tr[" + line + "]/th";
	}
	// It means matrix
	static String XPATH_CONTENTS_DATA(StatementType type, StatementPeriodType periodType, int line, int column) {
		return "//*[@id=\"" + type.getDivType() + "\"]/div[@name=\"" + periodType.getPeriodType() + type.getTypeNumber() + "\"]/table/tbody/tr[" + line + "]/td[" + column +  "]";
	}
	//*[@id="Sin0"]/div[1]/table/thead/tr/th[1]
	//*[@id="Sin0"]/div[@id="yt0"]/table/thead/tr/th
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
			rtn = Float.parseFloat(content.replaceAll(",", ""));
		} catch ( Exception e ) {
			throw new NotNumericContentException(content + ":" + e.getMessage() );
		}
		return rtn;
	}
	
	static HtmlCleaner cleaner;
	
	static {
		cleaner = new HtmlCleaner();
	}
	
	static ArrayList<String> getStandardDateList(StatementType type, StatementPeriodType period, TagNode companyXML) {
		ArrayList<String> list = new ArrayList<String>();
		try {
			Object[] tags = companyXML.evaluateXPath(XPATH_STANDARD_DATE(type, period));
			//System.out.println("XPATH_STANDARD_DATE:" + XPATH_STANDARD_DATE(type, period) );
			for ( int position = 1 ; position < tags.length ; position++ ) {
				list.add(((TagNode)tags[position]).getText().toString());
			}
			System.out.print(list);
		} catch ( Exception e ) {
			e.printStackTrace();
			System.out.println("XML parsing error.");
		}
		return list;
	}
	
	static ArrayList<String> getAllItemList(StatementType type, StatementPeriodType period, TagNode companyXML) {
		ArrayList<String> list = new ArrayList<String>();
		try {
			Object[] tags = companyXML.evaluateXPath(XPATH_CONTENTS(type, period));
			for ( int position = 0 ; position < tags.length ; position++ ) {
				Object[] subtags = companyXML.evaluateXPath(XPATH_CONTENTS_HEAD(type, period, position));
				if ( subtags.length > 0 )
					list.add(((TagNode)subtags[0]).getText().toString());
			}
			//System.out.print(list);
		} catch ( Exception e ) {
			e.printStackTrace();
			System.out.println("XML parsing error.");
		}
		return list;
	}
	
	static String getDataValue(StatementType type, StatementPeriodType period, TagNode companyXML, int line, int column) {
		String value = "";
		try {
			//System.out.print("XPATH_CONTENTS_DATA:" + XPATH_CONTENTS_DATA(type, period, line + 1, column +1));
			Object[]tags = companyXML.evaluateXPath(XPATH_CONTENTS_DATA(type, period, line + 1, column + 1));
			value = ((TagNode)tags[0]).getText().toString();
			//System.out.print(value);
		} catch ( Exception e ) {
			e.printStackTrace();
			System.out.println("XML parsing error.");
		}
		return value;
	}
	
	static class FinancialStatementItemStat {
		public String itemName;
		public int count;
		public ArrayList<Company> relatedComp;
		
		public FinancialStatementItemStat(String itemName) {
			this.itemName = itemName;
			relatedComp = new ArrayList<Company>();
		}
		
		public boolean equals(Object obj) {
			if ( obj instanceof FinancialStatementItemStat ) {
				return ((FinancialStatementItemStat) obj).itemName.equals(this.itemName);
			}
			return super.equals(obj);
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append(itemName).append(":").append(count).append(":");
			for (int cnt = 0 ; cnt < Math.min( relatedComp.size(), 10) ; cnt++ )
				sb.append(":").append(relatedComp.get(cnt).getName()).append("-").append(relatedComp.get(cnt).getId());
			return sb.toString();
		}
	}
	
	static String[][] PAXNET_FINANCE_ITEM_MAPPER = {
		{"자산총계",null,"totalAssets"},
		{"비유동자산",null,"noncurrentAssets"},
		{"비유동자산(계)",null,"noncurrentAssets"},
		{"유형자산(순액)","비유동자산","noncurrentPropertyPlantAndEquipment"},
		{"유형자산(계)","비유동자산","noncurrentPropertyPlantAndEquipment"},
		{"무형자산",null,"intangibleAssets"},
		{"무형자산(계)",null,"intangibleAssets"},
		{"투자부동산",null,"investmentProperty"},
		{"생물자산",null,"biologicalAssets"},
		{"장기투자자산",null,"longTermInvestmentAssets"},
		{"매출채권및기타채권","비유동자산","noncurrentTradeAndOtherReceivables"},
		{"이연법인세자산",null,"deferredTaxAssets"},
		{"기타금융자산",null,"otherFinancialAssets"},
		{"투자자산",null,"investmentAssets"},
		{"창업투자자산",null,"ventureCapital"},
		{"(창업투자자산대손충당부채)",null,"allowanceForVentureCapital"},
		{"기타비유동자산",null,"otherNoncurrentAssets"},
		{"유동자산",null,"currentAssets"},
		{"유동자산(계)",null,"currentAssets"},
		{"재고자산(계)",null,"inventories"},
		{"매출채권및기타채권","유동자산","currentTradeAndOtherReceivables"},
		{"단기투자자산",null,"shorttermInvestmentAssets"},
		{"당기법인세자산",null,"currentTaxAssets"},
		{"기타비금융자산",null,"otherNonfinancialAssets"},
		{"현금및현금성자산",null,"cashAndCashEquivalents"},
		{"당좌자산",null,"quickAssets"},
		{"현금및예치금",null,"cashAndDueFromFinancialInstitutions"},
		{"대출채권",null,"loans"},
		{"(대손충당금(-))",null,"allowance"},
		{"(대손충당금)",null,"allowance"},
		{"유형자산",null,"propertyPlantAndEquipment"},
		{"(감가상각누계액계(-))",null,"accumulatedDepreciation"},
		{"(감가상각누계액(-))",null,"accumulatedDepreciation"},
		{"기타자산",null,"otherAssets"},
		{"(기타자산대손충당금)",null,"allowanceForOtherAssets"},
		{"특별계정자산",null,"separateAccountLiablitiesAssets"},
		{"카드자산",null,"cardAssets"},
		{"(대손충당금합계(-))",null,"totalAllowance"},
		{"할부금융자산",null,"installmentCreditAssets"},
		{"리스자산",null,"leaseAssets"},
		{"지속적관여자산",null,"continuingInvolvementAssets"},
		{"신기술금융자산",null,"newTechnologyAssets"},
		{"매각예정자산및처분자산집단",null,"noncurrentAssetsHeldForSaleAndDiscontinued"},
		{"어음관리계좌자산",null,"cmaAssets"},
		{"자본총계",null,"totalEquity"},
		{"납입자본",null,"paidinCapital"},
		{"자본금",null,"issuedCapital"},
		{"이익잉여금",null,"earningsAndLosses"},
		{"이익잉여금(결손금)",null,"earningsAndLosses"},
		{"기타자본구성요소",null,"otherComponentsOfEquity"},
		{"기타포괄손익누계액",null,"accumulatedOtherComprehensiveIncome"},
		{"매각예정자산관련자본액",null,"amountRelatedToCapitalAssetsHeldForSale"},
		{"부채총계",null,"totalLiabilities"},
		{"비유동부채",null,"noncurrentLiabilities"},
		{"비유동부채(계)",null,"noncurrentLiabilities"},
		{"매입채무및기타채무","비유동부채","noncurrentTradeAndOtherNoncurrentPayables"},
		{"이연법인세부채",null,"deferredTaxLiabilities"},
		{"기타비금융부채","비유동부채","noncurretOtherNonfinancialLiabilities"},
		{"퇴직급여채무",null,"retirementBenefitObligations"},
		{"비유동차입부채",null,"noncurrentBorrowings"},
		{"유동부채",null,"currentLiabilities"},
		{"유동부채(계)",null,"currentLiabilities"},
		{"매입채무및기타채무","유동부채","currentTradeAndOtherNoncurrentPayables"},
		{"유동차입부채",null,"currentBorrowings"},
		{"기타비금융부채","유동부채","noncurrentOtherNonfinancialLiabilities"},
		{"단기충당부채",null,"shorttermAllowanceLiabilities"},
		{"장기충당부채(계)",null,"longtermAllowanceLiabilities"},
		{"당기법인세부채",null,"currentTaxLiabilities"},
		{"당기손익인식금융부채",null,"financialLiabilitiesAtFairValueThroughProfitOrLoss"},
		{"책임준비금",null,"deckungsfonds"},
		{"차입부채",null,"borrowingsAndDebentures"},
		{"기타부채",null,"otherLiabilities"},
		{"특별계정부채",null,"separateAccountLiablitiesLiabilities"},
		{"단기매매금융부채",null,"shorttermFinancialLiabilities"},
		{"계약자지분조정",null,"policyholdersEquityAdjustment"},
		{"이연부채",null,"deferredLiability"},
		{"예수부채",null,"deposits"},
		{"지속적관여자산관련부채",null,"continuingInvolvementAssetRelatedDebt"},
		{"매각예정부채",null,"liabilitiesHeldForSale"},
		{"기타금융부채",null,"otherFinancialLiabilities"},
	};
	
	public static String getMappedColumn(String context, String currentItem) {
		for( int line = 0 ; line < PAXNET_FINANCE_ITEM_MAPPER.length ; line++ ) {
			if ( currentItem.equals(PAXNET_FINANCE_ITEM_MAPPER[line][0]) ) {
				if (PAXNET_FINANCE_ITEM_MAPPER[line][1] == null )
					return PAXNET_FINANCE_ITEM_MAPPER[line][2];
				else if ( context != null && context.indexOf(PAXNET_FINANCE_ITEM_MAPPER[line][1]) >= 0 ) {
					return PAXNET_FINANCE_ITEM_MAPPER[line][2];
				} else {
					System.out.println("invalud context & item : contenxt:[" + context + "] currentItem:[" + currentItem + "]");
				}
			}
		}
		return null;
	}
	
	public static long getLongValueFromString(String value) {
		long rtn = 0;
		try {
			value = value.replaceAll(",", "");
			if ( "-".equals(value) )
				return 0;
			rtn = Long.parseLong(value);
		} catch ( Exception e ) {
			System.out.println("Invalid number..:" + e.getMessage() );
			rtn = 0;
		}
		return rtn;
	}

	public static void main(String[] args) {
		TimeWatch timewatch = new TimeWatch();
		HttpURLConnection conn = null;
		ArrayList<CompanyEx> badCompanyList = new ArrayList<CompanyEx>();
		try {
			timewatch.start();
			CompanyExDao dao = new CompanyExDao();
			CompanyStatementOfFinancialPosition sofDao = new CompanyStatementOfFinancialPosition();
			ArrayList<CompanyEx> companyList = dao.selectAllList(StringUtil.convertToStandardDate(new java.util.Date()), KrxSecurityType.STOCK);
			timewatch.reset();
			// 1. Company List
			for ( int cnt = 0 ; cnt < companyList.size() ; cnt++ ) { //companyList.size() ; cnt++ ) {
				System.out.println("start....." + cnt + ":" + companyList.size());
				conn = (HttpURLConnection)new URL(ID_URL(companyList.get(cnt).getId().substring(1))).openConnection();
				TagNode xml = cleaner.clean(conn.getInputStream());
				// 2. IFRS / GAAP
				for (StatementType type : StatementType.values()) {
					// 3. YEAR / QUARTER
					for(StatementPeriodType period : StatementPeriodType.values()) {
						ArrayList<String> standardDateList = getStandardDateList(type,period, xml);
						System.out.println("standardDateList:" + standardDateList);
						ArrayList<String> itemList = getAllItemList(type, period, xml);
						System.out.println("itemList:" + itemList);
						String context = null;
						if ( itemList.size() > 0 ) {
							// 5. STANDARD DATE
							for ( int column = 0 ; column < standardDateList.size() ; column++ ) {
								StatementOfFinancialPosition sofp = new StatementOfFinancialPosition();
								sofp.setCompany(companyList.get(cnt));
								sofp.setStandardDate(standardDateList.get(column));
								sofp.setAnnual(period == StatementPeriodType.PERIOD_YEAR);
								sofp.setKospi(true);
								sofp.setConsolidatedStatement(/*type == StatementType.GAAP_CONSOLIDATED || */type == StatementType.IFRS_CONSOLIDATED);
								sofp.setIfrsGaap( /*( type == StatementType.GAAP_CONSOLIDATED || type == StatementType.GAAP_SEPARATE ) ? "GAAP" : */ "IFRS");
								// 4. ITEM LIST
								for ( int line = 0 ; line < itemList.size() ; line++ ) {
									String name = itemList.get(line).trim();
									if ( name.indexOf("비유동자산") == 0 ) {
										context = "비유동자산";
									} else if ( name.indexOf("유동자산") == 0 ) {
										context = "유동자산";
									} else if ( name.indexOf("비유동부채") == 0 ) {
										context = "비유동부채";
									} else if ( name.indexOf("유동부채") == 0 ) {
										context = "유동부채";
									}
									String value = getDataValue(type, period, xml, line, column);
									String mappedAttribute = getMappedColumn(context, name);
									if ( mappedAttribute != null ) {
										Class[] param = { Long.TYPE };
										Method method = sofp.getClass().getDeclaredMethod("set" + mappedAttribute.substring(0,1).toUpperCase() + mappedAttribute.substring(1), param);
										Object[] para = new Object[1];
										
										para[0] = new Long(getLongValueFromString(value));
										method.invoke(sofp, para);
									} else {
										System.out.println("bad attribute:[" + name + "]" );
									}
								}
								sofDao.delete(sofp);
								sofDao.insert(sofp);
								System.out.println("----------- statement of financial position [" + cnt + "/" + companyList.size() + "][" + type.getDivType() + "][" + period + "][" + standardDateList.get(column) + "]-------" );
								System.out.println(sofp);
							}
						} else {
							// If there is no statement for separate corporation, we will assume that this company has abnormal state - bankruptcy. disappered...
							if ( type == StatementType.IFRS_SEPARATE ) {
								System.out.println("bad company:[" + companyList.get(cnt) + "]" );
								badCompanyList.add(companyList.get(cnt));
							}
						}
						timewatch.stopAndStart();
					}
				}
			}
			timewatch.stop();
			System.out.println("----------------------------------------------------");
			for ( int badcount = 0 ; badcount < badCompanyList.size() ; badcount++ ) {
				System.out.println(badCompanyList.get(badcount));
			}
			System.out.println("----------------------------------------------------");
		} catch ( Exception e ) {
			e.printStackTrace();
			//System.out.println(e.getLocalizedMessage());
		}
		if ( conn != null ) {
			conn.disconnect();
		}
		
	}
	
	
}
