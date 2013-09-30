package post;

public class Company extends BaseStructure {
	
	/**
	 * SECTOR_UNDEFINCED : 섹터를 정의하지 않음
	 */
	public static final int SECTOR_UNDEFINED = 0;
	//public static final int SECTOR_
	
	/**
	 * id : 증시 고유 번호
	 */
	private String id = "";
	
	/**
	 * name : 회사 이름
	 */
	private String name = "";
	
	/**
	 * standardDate : 기준일시 
	 */
	private String standardDate = null;
	
	/**
	 * sector : 소속된 섹터 
	 */
	private int sector = SECTOR_UNDEFINED;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSector() {
		return sector;
	}

	public void setSector(int sector) {
		this.sector = sector;
	}

	public String getStandardDate() {
		return standardDate;
	}

	public void setStandardDate(String standardDate) {
		this.standardDate = standardDate;
	}
	
	public boolean equals(Object tgt) {
		if ( tgt instanceof Company ) {
			Company tgtCompany = (Company)tgt;
			return tgtCompany.getId().equals(this.getId());
		}
		return super.equals(tgt);
	}
}
