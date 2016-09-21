package post;

public class StockEstimated extends BaseStructure {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5537462004157319506L;
	/**
	 * 회사정보
	 */
	private Company company = null;
	
	/**
	 * 재무 상태표
	 */
	private CompanyFinancialStatus cfs = null;
	
	public CompanyFinancialStatus getCfs() {
		return cfs;
	}

	public void setCfs(CompanyFinancialStatus cfs) {
		this.cfs = cfs;
	}

	/**
	 * 기준일자
	 */
	private String standardDate = null;
	/**
	 * 추산치 정보의 출처. O = 자채계산,  F = fnguide , Y = Yahoo , D = datacom
	 */
	private String estimKind = "O";
	/**
	 * 평균 per
	 */
	private float avePer = (float) 0.0;
	/**
	 * 평균 roe
	 */
	private float aveRoe = (float) 0.0;
	
	private float aveRoa = (float) 0.0;
	
	private float aveBps = (float) 0.0;
	/**
	 * Book / Price Ratio
	 */
	private float aveBpr = (float) 0.0;
	
	private float earningYield = (float) 0.0;
	
	private float debtRatio = (float) 0.0;
	
	public float getDebtRatio() {
		return debtRatio;
	}

	public void setDebtRatio(float debtRatio) {
		this.debtRatio = debtRatio;
	}

	public float getEarningYield() {
		return earningYield;
	}

	public void setEarningYield(float earningYield) {
		this.earningYield = earningYield;
	}

	public float getAveBps() {
		return aveBps;
	}

	public void setAveBps(float aveBps) {
		this.aveBps = aveBps;
	}

	/**
	 * devidendRatio : 배당율 
	 */
	private float aveDividendRatio = (float) 0.0;
	/**
	 * recentEps : 최신 eps  
	 */
	private float recentEps = (float) 0.0;
	/**
	 * 최신 주가
	 */
	private long recentStockValue = 0;
	/**
	 * 최종 eps
	 */
	private float lastEps = (float) 0.0;
	/**
	 * 평균 이익율
	 */
	private float expectationRation = (float)0.0;
	
	private String relatedDateList = "";
	
	public StockEstimated() {
		super();
	}
	
	public StockEstimated(BaseStructure cfs) throws Exception {
		super(cfs);
	}

	public float getAveDividendRatio() {
		return aveDividendRatio;
	}

	public void setAveDividendRatio(float aveDividendRatio) {
		this.aveDividendRatio = aveDividendRatio;
	}

	public float getRecentEps() {
		return recentEps;
	}

	public void setRecentEps(float aveEps) {
		this.recentEps = aveEps;
	}

	public float getAvePer() {
		return avePer;
	}

	public void setAvePer(float avePer) {
		this.avePer = avePer;
	}

	public float getAveRoe() {
		return aveRoe;
	}

	public void setAveRoe(float aveRoe) {
		this.aveRoe = aveRoe;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public float getExpectationRation() {
		return expectationRation;
	}

	public void setExpectationRation(float expectationRation) {
		this.expectationRation = expectationRation;
	}

	public float getLastEps() {
		return lastEps;
	}

	public void setLastEps(float lastEps) {
		this.lastEps = lastEps;
	}

	public long getRecentStockValue() {
		return recentStockValue;
	}

	public void setRecentStockValue(long recentStockValue) {
		this.recentStockValue = recentStockValue;
	}

	public String getStandardDate() {
		return standardDate;
	}

	public void setStandardDate(String standardDate) {
		this.standardDate = standardDate;
	}

	public String getEstimKind() {
		return estimKind;
	}

	public void setEstimKind(String estimKind) {
		this.estimKind = estimKind;
	}

	public float getAveRoa() {
		return aveRoa;
	}

	public void setAveRoa(float aveRoa) {
		this.aveRoa = aveRoa;
	}
	
	public void setAveBpr(float aveBpr) {
		this.aveBpr = aveBpr;
	}
	
	public float getAveBpr() {
		return this.aveBpr;
	}

	public String getRelatedDateList() {
		return relatedDateList;
	}

	public void setRelatedDateList(String relatedDateList) {
		this.relatedDateList = relatedDateList;
	}
}
