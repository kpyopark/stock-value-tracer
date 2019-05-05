package post;


public class Stock extends BaseStructure {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6545279609045392980L;

	/**
	 * �ش� ��
	 */
	private Company company = null;
	
	/**
	 * ����
	 */
	private String standardDate = null;  
	
	/**
	 * �ð�
	 */
	private String standardTime = null;
	
	/**
	 * �׸鰡
	 */
	private float parValue = 0;
	
	/**
	 * ���簡
	 */
	private int value = 0;
	
	/**
	 * �ŷ���
	 */
	private long volume = 0;
	
	private int todayHigh = 0;
	
	private int todayLow = 0;
	
	private long ordinaryShares = 0;
	
	private long marketCapitalization = 0;
	
	private int openPrice = 0;
	
	public int getOpenPrice() {
		return openPrice;
	}

	public void setOpenPrice(int openPrice) {
		this.openPrice = openPrice;
	}

	/**
	 * 
	 * @return
	 */
	public float getParValue() {
		return parValue;
	}
	
	/**
	 * 
	 * @param faceValue
	 */
	public void setParValue(float faceValue) {
		this.parValue = faceValue;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * 
	 * @param value
	 */
	public void setValue(int value) {
		this.value = value;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getStandardDate() {
		return standardDate;
	}

	public void setStandardDate(String standardDate) {
		this.standardDate = standardDate;
	}

	public String getStandardTime() {
		return standardTime;
	}

	public void setStandardTime(String standardTime) {
		this.standardTime = standardTime;
	}

	public long getVolume() {
		return volume;
	}

	public void setVolume(long volume) {
		this.volume = volume;
	}

	public int getTodayHigh() {
		return todayHigh;
	}

	public void setTodayHigh(int todayHigh) {
		this.todayHigh = todayHigh;
	}

	public int getTodayLow() {
		return todayLow;
	}

	public void setTodayLow(int todayLow) {
		this.todayLow = todayLow;
	}

	public long getOrdinaryShares() {
		return ordinaryShares;
	}

	public void setOrdinaryShares(long ordinaryShares) {
		this.ordinaryShares = ordinaryShares;
	}

	public long getMarketCapitalization() {
		return marketCapitalization;
	}

	public void setMarketCapitalization(long marketCapitalization) {
		this.marketCapitalization = marketCapitalization;
	}
	
	
	
}
