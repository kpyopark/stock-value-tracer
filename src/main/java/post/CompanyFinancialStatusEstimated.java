package post;

public class CompanyFinancialStatusEstimated extends CompanyFinancialStatus {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4960950488390560930L;
	private boolean isEstimated = true;
	/**
	 * 추산치가 나온 출처. O = 스스로한 계산치, F = fnguide , Y = yahoo , D = datacom
	 */
	private String estimKind = "O";
	private String relatedDateList = "";
	private String estimatedYn = "N";
	
	public String getEstimatedYn() {
		return estimatedYn;
	}

	public void setEstimatedYn(String estimatedYn) {
		this.estimatedYn = estimatedYn;
	}

	public boolean isEstimated() {
		return isEstimated;
	}

	public void setEstimated(boolean isEstimated) {
		this.isEstimated = isEstimated;
	}
	
	public CompanyFinancialStatusEstimated() {
		super();
	}
	
	public CompanyFinancialStatusEstimated(BaseStructure bs) throws Exception {
		super(bs);
	}
	
	public static void main(String[] args) throws Exception {
		CompanyFinancialStatus cfs = new CompanyFinancialStatus();
		cfs.setAssets(1000);
		cfs.setCapital(2000);
		CompanyFinancialStatusEstimated cfse = new CompanyFinancialStatusEstimated();
		cfse.copyStructure(cfs);
		
		System.out.println(cfse);
	}

	public String getEstimKind() {
		return estimKind;
	}

	public void setEstimKind(String estimKind) {
		this.estimKind = estimKind;
	}

	public String getRelatedDateList() {
		return relatedDateList;
	}

	public void setRelatedDateList(String relatedDateList) {
		this.relatedDateList = relatedDateList;
	}

}
