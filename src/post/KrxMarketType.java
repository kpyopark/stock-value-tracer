package post;

public enum KrxMarketType {
	ALL("전체"), KOSPI("STK"), KOSDAQ("KSQ"), KONEX("KNX");
	private String marketType = "전체";
	private KrxMarketType(String type) {
		this.marketType = type;
	}
	public String getMarketType() {
		return marketType;
	}
	
}
