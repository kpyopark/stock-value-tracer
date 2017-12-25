package post;

public class CompanyEx extends Company {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2856177873627462114L;
	public static int SECURITY_ORDINARY_STOCK = 0;
	public static int SECURITY_ETF_DEFFERED = 1;
	public static int SECURITY_ELW_DEFFERED = 2;
	
	public String getKrxIndustryCode() {
		return krxIndustryCode;
	}

	public void setKrxIndustryCode(String krxIndustryCode) {
		this.krxIndustryCode = krxIndustryCode;
	}

	public String getKrxIndustrySector() {
		return krxIndustrySector;
	}

	public void setKrxIndustrySector(String krxIndustrySector) {
		this.krxIndustrySector = krxIndustrySector;
	}

	public String getKrxIndustryCategory() {
		return krxIndustryCategory;
	}

	public void setKrxIndustryCategory(String krxIndustryCategory) {
		this.krxIndustryCategory = krxIndustryCategory;
	}
	
	public String getTelNo() {
		return telNo;
	}

	public void setTelNo(String telNo) {
		this.telNo = telNo;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}



	private String krxIndustryCode;
	private String krxIndustrySector;
	private String krxIndustryCategory;
	
	private String telNo;
	private String address;
	private String futureYn;
	private String futureBaseCode;
	
	public String getFutureBaseCode() {
		return futureBaseCode;
	}

	public void setFutureBaseCode(String futureBaseCode) {
		this.futureBaseCode = futureBaseCode;
	}

	public String getFutureYn() {
		return futureYn;
	}

	public void setFutureYn(String futureYn) {
		this.futureYn = futureYn;
	}



	private int securitySector = 0;		// 0 - ordinary stock, 1 - etf deffered, 2 - elw deffered

	public int getSecuritySector() {
		return securitySector;
	}

	public void setSecuritySector(int securitySector) {
		this.securitySector = securitySector;
	}

}
