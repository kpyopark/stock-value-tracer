package post;

public enum KrxSecurityType {
	STOCK(0), ETF(1), ELW(2), ETN(3), ETC(4);
	private int secuityType = 0;
	private KrxSecurityType(int type) {
		this.secuityType = type;
	}
	public static KrxSecurityType getKrxSecurityTypeFromInt(int type) {
		switch (type) {
		case 0 :
			return STOCK;
		case 1 : 
			return ETF;
		case 2 :
			return ELW;
		case 3 :
			return ETN;
		case 4 :
			return ETC;
		}
		return STOCK;
	}
	public int getType() {
		return secuityType;
	}
	
	public String getNewSecurityType() {
		switch (secuityType) {
		case 0 :
			return "ST";
		case 1 : 
			return "EF";
		case 2 :
			return "EW";
		case 3 :
			return "EN";
		case 4 :
			return "Y";
		}
		return "ST";
	}
	
}
