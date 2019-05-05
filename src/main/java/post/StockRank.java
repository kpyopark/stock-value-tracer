package post;

public class StockRank extends BaseStructure {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5067583453728158614L;

	/**
	 * 주가 대상 회사 
	 */
	private Company company = null;
	
	private StockEstimated stockEstimation = null;
	
	/**
	 * ROE를 기준으로 Rank
	 */
	private int roeRank = Integer.MAX_VALUE;
	
	/**
	 * PER를 기준으로 Rank
	 */
	private int perRank = Integer.MAX_VALUE;
	
	/**
	 * 최상의 Rank
	 */
	private int totRank = Integer.MAX_VALUE;
	
	private int roaRank = Integer.MAX_VALUE;
	
	private int bpsRank = Integer.MAX_VALUE;
	
	private int bprRank = Integer.MAX_VALUE;
	
	public int getBprRank() {
		return bprRank;
	}
	public void setBprRank(int bprRank) {
		this.bprRank = bprRank;
	}
	public int getBpsRank() {
		return bpsRank;
	}
	public void setBpsRank(int bpsRank) {
		this.bpsRank = bpsRank;
	}
	public int getEarningYieldRank() {
		return EarningYieldRank;
	}
	public void setEarningYieldRank(int earningYieldRank) {
		EarningYieldRank = earningYieldRank;
	}
	private int EarningYieldRank = Integer.MAX_VALUE;
	
	public int getRoaRank() {
		return roaRank;
	}
	public void setRoaRank(int roaRank) {
		this.roaRank = roaRank;
	}
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}
	public int getPerRank() {
		return perRank;
	}
	public void setPerRank(int perRank) {
		this.perRank = perRank;
	}
	public int getRoeRank() {
		return roeRank;
	}
	public void setRoeRank(int roeRank) {
		this.roeRank = roeRank;
	}
	public int getTotRank() {
		return totRank;
	}
	public void setTotRank(int totRank) {
		this.totRank = totRank;
	}
	
	public boolean equals(Object tgt) {
		if ( tgt instanceof StockRank ) {
			StockRank tgtStockRank = (StockRank)tgt;
			return tgtStockRank.getCompany().equals(this.getCompany());
		}
		return super.equals(tgt);
	}
	public StockEstimated getStockEstimation() {
		return stockEstimation;
	}
	public void setStockEstimation(StockEstimated stockEstimation) {
		this.stockEstimation = stockEstimation;
	}
	
}
