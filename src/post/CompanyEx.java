package post;

public class CompanyEx extends Company {
	
	private int securitySector = 0;		// 0 - ordinary stock, 1 - etf deffered, 2 - elw deffered

	public int getSecuritySector() {
		return securitySector;
	}

	public void setSecuritySector(int securitySector) {
		this.securitySector = securitySector;
	}

}
