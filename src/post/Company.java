package post;

public class Company extends BaseStructure implements Comparable<Company> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -126697417460984305L;

	public String getFicsSector() {
		return ficsSector;
	}

	public void setFicsSector(String ficsSector) {
		this.ficsSector = ficsSector;
	}

	public String getFicsIndustryGroup() {
		return ficsIndustryGroup;
	}

	public void setFicsIndustryGroup(String ficsIndustryGroup) {
		this.ficsIndustryGroup = ficsIndustryGroup;
	}

	public String getFicsIndustry() {
		return ficsIndustry;
	}

	public void setFicsIndustry(String ficsIndustry) {
		this.ficsIndustry = ficsIndustry;
	}

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
	
	private boolean isClosed = false;
	
	private String ficsSector = "";
	
	private String ficsIndustryGroup = "";
	
	private String ficsIndustry = "";
	
	public boolean isClosed() {
		return isClosed;
	}

	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}

	
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

	@Override
	public int compareTo(Company o) {
		if (o == null)
			return 1;
		if (this.id == null)
			return -1;
		return this.id.compareTo(o.id) ;
	}
}
