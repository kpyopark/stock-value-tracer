package post;


public class Stock extends BaseStructure {
	
	/**
	 * 해당 주
	 */
	private Company company = null;
	
	/**
	 * 일자
	 */
	private String standardDate = null;  
	
	/**
	 * 시간
	 */
	private String standardTime = null;
	
	/**
	 * 액면가
	 */
	private int faceValue = 0;
	
	/**
	 * 현재가
	 */
	private int value = 0;
	
	/**
	 * 거래량
	 */
	private int volume = 0;
	
	/**
	 * 
	 * @return
	 */
	public int getFaceValue() {
		return faceValue;
	}
	
	/**
	 * 
	 * @param faceValue
	 */
	public void setFaceValue(int faceValue) {
		this.faceValue = faceValue;
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

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}
	
}
