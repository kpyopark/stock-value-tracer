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
		{"�ڻ��Ѱ�",null,"totalAssets"},
		{"�������ڻ�",null,"noncurrentAssets"},
		{"�������ڻ�(��)",null,"noncurrentAssets"},
		{"�����ڻ�(����)","�������ڻ�","noncurrentPropertyPlantAndEquipment"},
		{"�����ڻ�(��)","�������ڻ�","noncurrentPropertyPlantAndEquipment"},
		{"�����ڻ�",null,"intangibleAssets"},
		{"�����ڻ�(��)",null,"intangibleAssets"},
		{"���ںε���",null,"investmentProperty"},
		{"�����ڻ�",null,"biologicalAssets"},
		{"��������ڻ�",null,"longTermInvestmentAssets"},
		{"����ä�ǹױ�Ÿä��","�������ڻ�","noncurrentTradeAndOtherReceivables"},
		{"�̿����μ��ڻ�",null,"deferredTaxAssets"},
		{"��Ÿ�����ڻ�",null,"otherFinancialAssets"},
		{"�����ڻ�",null,"investmentAssets"},
		{"â�������ڻ�",null,"ventureCapital"},
		{"(â�������ڻ�������ä)",null,"allowanceForVentureCapital"},
		{"��Ÿ�������ڻ�",null,"otherNoncurrentAssets"},
		{"�����ڻ�",null,"currentAssets"},
		{"�����ڻ�(��)",null,"currentAssets"},
		{"����ڻ�(��)",null,"inventories"},
		{"����ä�ǹױ�Ÿä��","�����ڻ�","currentTradeAndOtherReceivables"},
		{"�ܱ������ڻ�",null,"shorttermInvestmentAssets"},
		{"�����μ��ڻ�",null,"currentTaxAssets"},
		{"��Ÿ������ڻ�",null,"otherNonfinancialAssets"},
		{"���ݹ����ݼ��ڻ�",null,"cashAndCashEquivalents"},
		{"�����ڻ�",null,"quickAssets"},
		{"���ݹ׿�ġ��",null,"cashAndDueFromFinancialInstitutions"},
		{"����ä��",null,"loans"},
		{"(�������(-))",null,"allowance"},
		{"(�������)",null,"allowance"},
		{"�����ڻ�",null,"propertyPlantAndEquipment"},
		{"(�����󰢴���װ�(-))",null,"accumulatedDepreciation"},
		{"(�����󰢴����(-))",null,"accumulatedDepreciation"},
		{"��Ÿ�ڻ�",null,"otherAssets"},
		{"(��Ÿ�ڻ�������)",null,"allowanceForOtherAssets"},
		{"Ư�������ڻ�",null,"separateAccountLiablitiesAssets"},
		{"ī���ڻ�",null,"cardAssets"},
		{"(��������հ�(-))",null,"totalAllowance"},
		{"�Һα����ڻ�",null,"installmentCreditAssets"},
		{"�����ڻ�",null,"leaseAssets"},
		{"�����������ڻ�",null,"continuingInvolvementAssets"},
		{"�ű�������ڻ�",null,"newTechnologyAssets"},
		{"�Ű������ڻ��ó���ڻ�����",null,"noncurrentAssetsHeldForSaleAndDiscontinued"},
		{"�������������ڻ�",null,"cmaAssets"},
		{"�ں��Ѱ�",null,"totalEquity"},
		{"�����ں�",null,"paidinCapital"},
		{"�ں���",null,"issuedCapital"},
		{"�����׿���",null,"earningsAndLosses"},
		{"�����׿���(��ձ�)",null,"earningsAndLosses"},
		{"��Ÿ�ں��������",null,"otherComponentsOfEquity"},
		{"��Ÿ�������ʹ����",null,"accumulatedOtherComprehensiveIncome"},
		{"�Ű������ڻ�����ں���",null,"amountRelatedToCapitalAssetsHeldForSale"},
		{"��ä�Ѱ�",null,"totalLiabilities"},
		{"��������ä",null,"noncurrentLiabilities"},
		{"��������ä(��)",null,"noncurrentLiabilities"},
		{"����ä���ױ�Ÿä��","��������ä","noncurrentTradeAndOtherNoncurrentPayables"},
		{"�̿����μ���ä",null,"deferredTaxLiabilities"},
		{"��Ÿ�������ä","��������ä","noncurretOtherNonfinancialLiabilities"},
		{"�����޿�ä��",null,"retirementBenefitObligations"},
		{"���������Ժ�ä",null,"noncurrentBorrowings"},
		{"������ä",null,"currentLiabilities"},
		{"������ä(��)",null,"currentLiabilities"},
		{"����ä���ױ�Ÿä��","������ä","currentTradeAndOtherNoncurrentPayables"},
		{"�������Ժ�ä",null,"currentBorrowings"},
		{"��Ÿ�������ä","������ä","noncurrentOtherNonfinancialLiabilities"},
		{"�ܱ�����ä",null,"shorttermAllowanceLiabilities"},
		{"�������ä(��)",null,"longtermAllowanceLiabilities"},
		{"�����μ���ä",null,"currentTaxLiabilities"},
		{"�������νı�����ä",null,"financialLiabilitiesAtFairValueThroughProfitOrLoss"},
		{"å���غ��",null,"deckungsfonds"},
		{"���Ժ�ä",null,"borrowingsAndDebentures"},
		{"��Ÿ��ä",null,"otherLiabilities"},
		{"Ư��������ä",null,"separateAccountLiablitiesLiabilities"},
		{"�ܱ�Ÿű�����ä",null,"shorttermFinancialLiabilities"},
		{"�������������",null,"policyholdersEquityAdjustment"},
		{"�̿���ä",null,"deferredLiability"},
		{"������ä",null,"deposits"},
		{"�����������ڻ���ú�ä",null,"continuingInvolvementAssetRelatedDebt"},
		{"�Ű�������ä",null,"liabilitiesHeldForSale"},
		{"��Ÿ������ä",null,"otherFinancialLiabilities"},
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
									if ( name.indexOf("�������ڻ�") == 0 ) {
										context = "�������ڻ�";
									} else if ( name.indexOf("�����ڻ�") == 0 ) {
										context = "�����ڻ�";
									} else if ( name.indexOf("��������ä") == 0 ) {
										context = "��������ä";
									} else if ( name.indexOf("������ä") == 0 ) {
										context = "������ä";
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
