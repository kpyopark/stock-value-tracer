package post;


public class Stock extends BaseStructure {
	
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
	private int faceValue = 0;
	
	/**
	 * ���簡
	 */
	private int value = 0;
	
	/**
	 * �ŷ���
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
