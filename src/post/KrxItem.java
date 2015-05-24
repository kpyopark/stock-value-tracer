package post;

public class KrxItem extends BaseStructure {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2892275854624581192L;

	private String id = "";
	
	private String name = "";
	
	private String standardDate = "";
	
	public String getStandardDate() {
		return standardDate;
	}

	public void setStandardDate(String standardDate) {
		this.standardDate = standardDate;
	}

	private long stockPrice = 0;
	
	private long netChange = 0;
	
	private float netChangeRatio = 0;
	
	private long bid = 0;
	
	private long ask = 0;
	
	private long volume = 0;
	
	private long volumnAmount = 0;
	
	private long openPrice = 0;
	
	//private long closePrice = 0;    // closePrice = stopPrice in this web API. ( last value of stock is closed value )
	
	private long todayHigh = 0;
	
	private long todayLow = 0;
	
	private float parValue = 0;
	
	private String currency = "";
	
	private long ordinaryShare = 0;
	
	private long maketCapitalization = 0;
	
	private String expireDate = "";
	
	private KrxSecurityType securityType = KrxSecurityType.STOCK;
	
	public KrxSecurityType getSecurityType() {
		return securityType;
	}

	public void setSecurityType(KrxSecurityType securityType) {
		this.securityType = securityType;
	}

	public String getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(String expireDate) {
		this.expireDate = expireDate;
	}

	public long getStockPrice() {
		return stockPrice;
	}

	public void setStockPrice(long stockPrice) {
		this.stockPrice = stockPrice;
	}

	public long getNetChange() {
		return netChange;
	}

	public void setNetChange(long netChange) {
		this.netChange = netChange;
	}

	public float getNetChangeRatio() {
		return netChangeRatio;
	}

	public void setNetChangeRatio(float netChangeRatio) {
		this.netChangeRatio = netChangeRatio;
	}

	public long getBid() {
		return bid;
	}

	public void setBid(long bid) {
		this.bid = bid;
	}

	public long getAsk() {
		return ask;
	}

	public void setAsk(long ask) {
		this.ask = ask;
	}

	public long getVolume() {
		return volume;
	}

	public void setVolume(long volume) {
		this.volume = volume;
	}

	public long getVolumnAmount() {
		return volumnAmount;
	}

	public void setVolumnAmount(long volumnAmount) {
		this.volumnAmount = volumnAmount;
	}

	public long getOpenPrice() {
		return openPrice;
	}

	public void setOpenPrice(long openPrice) {
		this.openPrice = openPrice;
	}

	//public long getClosePrice() {
	//	return closePrice;
	//}

	//public void setClosePrice(long closePrice) {
	//	this.closePrice = closePrice;
	//}

	public long getTodayHigh() {
		return todayHigh;
	}

	public void setTodayHigh(long todayHigh) {
		this.todayHigh = todayHigh;
	}

	public long getTodayLow() {
		return todayLow;
	}

	public void setTodayLow(long todayLow) {
		this.todayLow = todayLow;
	}

	public float getParValue() {
		return parValue;
	}

	public void setParValue(float parValue) {
		this.parValue = parValue;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public long getOrdinaryShare() {
		return ordinaryShare;
	}

	public void setOrdinaryShare(long ordinaryShare) {
		this.ordinaryShare = ordinaryShare;
	}

	public long getMaketCapitalization() {
		return maketCapitalization;
	}

	public void setMaketCapitalization(long maketCapitalization) {
		this.maketCapitalization = maketCapitalization;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		if ( id.charAt(0) != 'A' )
			this.id = "A" + id;
		else
			this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public CompanyEx getCompany() {
		CompanyEx company = new CompanyEx();
		company.setId(this.id);
		company.setName(this.name);
		return company;
	}
	
}
