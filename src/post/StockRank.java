package post;

public class StockRank extends BaseStructure {
	
	/**
	 * �ְ� ��� ȸ�� 
	 */
	private Company company = null;
	
	private StockEstimated stockEstimation = null;
	
	/**
	 * ROE�� �������� Rank
	 */
	private int roeRank = Integer.MAX_VALUE;
	
	/**
	 * PER�� �������� Rank
	 */
	private int perRank = Integer.MAX_VALUE;
	
	/**
	 * �ֻ��� Rank
	 */
	private int totRank = Integer.MAX_VALUE;
	
	private int roaRank = Integer.MAX_VALUE;
	
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