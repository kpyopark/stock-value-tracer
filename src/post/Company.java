package post;

public class Company extends BaseStructure {
	
	/**
	 * SECTOR_UNDEFINCED : ���͸� �������� ����
	 */
	public static final int SECTOR_UNDEFINED = 0;
	//public static final int SECTOR_
	
	/**
	 * id : ���� ���� ��ȣ
	 */
	private String id = "";
	
	/**
	 * name : ȸ�� �̸�
	 */
	private String name = "";
	
	/**
	 * standardDate : �����Ͻ� 
	 */
	private String standardDate = null;
	
	/**
	 * sector : �Ҽӵ� ���� 
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
