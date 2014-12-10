package post;

public class CompanyEx extends Company {
	
	public static int SECURITY_ORDINARY_STOCK = 0;
	public static int SECURITY_ETF_DEFFERED = 1;
	public static int SECURITY_ELW_DEFFERED = 2;
	
	private int securitySector = 0;		// 0 - ordinary stock, 1 - etf deffered, 2 - elw deffered

	public int getSecuritySector() {
		return securitySector;
	}

	public void setSecuritySector(int securitySector) {
		this.securitySector = securitySector;
	}

}
