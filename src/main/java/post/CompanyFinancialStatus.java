package post;

public class CompanyFinancialStatus extends BaseStructure {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1397366767529694042L;
	/**
	 * 회사정보
	 */
	private Company company = null;
	/**
	 * assets : 자산
	 */
	private long assets = 0;
	/**
	 * capital : 자본
	 */
	private long capital = 0;
	/**
	 * debt : 부채
	 */
	private long debt = 0;
	/**
	 * devidendRatio : 배당율 
	 */
	private float dividendRatio = (float) 0.0;
	/**
	 * 자본총계
	 */
	private long grossCapital = 0;
	/**
	 * 현재 정보가 고정된 정보인지 여부
	 */
	private boolean isFixed = true;
	/**
	 * KOSPI 등재여부
	 */
	private boolean isKOSPI = true;
	/**
	 * 현재 정보가 net quarter인지 여부
	 */
	private boolean isQuarter = false;
	/**
	 * netProfit : 순이익 
	 */
	private long netProfit = 0;
	/**
	 * operatingProfit : 영업이익 
	 */
	private long operatingProfit = 0;
	/**
	 * 경상이익
	 */
	private long ordinaryProfit = 0;
	private float roa = (float) 0.0;
	private float roe = (float) 0.0;
	private float roi = (float) 0.0;
	/**
	 * saled : 총매출
	 */
	private long sales = 0;
	
	/**
	 * 기준일자
	 */
	private String standardDate = null;
	/**
	 * 보통주 발행 주식수
	 */
	private long ordinarySharesSize = 0;
	
	/**
	 * 우선주 발행 주식수
	 */
	private long prefferedSharesSize = 0;
	
	/**
	 * 투하자본
	 */
	private long investedCapital = 0;
	
	
	private boolean isCalculated = false;
	
	
	private String registeredDate = "";
	
	//
	
	public String getRegisteredDate() {
		return registeredDate;
	}
	public void setRegisteredDate(String registeredDate) {
		this.registeredDate = registeredDate;
	}
	public long getAssets() {
		return assets;
	}
	public void setAssets(long assets) {
		this.assets = assets;
	}
	public long getCapital() {
		return capital;
	}
	public void setCapital(long capital) {
		this.capital = capital;
	}
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}
	public long getDebt() {
		return debt;
	}
	public void setDebt(long debt) {
		this.debt = debt;
	}
	public float getDividendRatio() {
		return dividendRatio;
	}
	public void setDividendRatio(float devidendRatio) {
		this.dividendRatio = devidendRatio;
	}
	public long getGrossCapital() {
		return grossCapital;
	}
	public void setGrossCapital(long grossCapital) {
		this.grossCapital = grossCapital;
	}
	public boolean isFixed() {
		return isFixed;
	}
	public void setFixed(boolean isFixed) {
		this.isFixed = isFixed;
	}
	public boolean isKOSPI() {
		return isKOSPI;
	}
	public void setKOSPI(boolean isKOSPI) {
		this.isKOSPI = isKOSPI;
	}
	public boolean isQuarter() {
		return isQuarter;
	}
	public void setQuarter(boolean isNetQuarter) {
		this.isQuarter = isNetQuarter;
	}
	public long getNetProfit() {
		return netProfit;
	}
	public void setNetProfit(long netProfit) {
		this.netProfit = netProfit;
	}
	public long getOperatingProfit() {
		return operatingProfit;
	}
	public void setOperatingProfit(long operatingProfit) {
		this.operatingProfit = operatingProfit;
	}
	public long getOrdinaryProfit() {
		return ordinaryProfit;
	}
	public void setOrdinaryProfit(long ordinaryProfit) {
		this.ordinaryProfit = ordinaryProfit;
	}
	public float getRoa() {
		return roa;
	}
	public void setRoa(float roa) {
		this.roa = roa;
	}
	public float getRoe() {
		return roe;
	}
	public void setRoe(float roe) {
		this.roe = roe;
	}
	public long getSales() {
		return sales;
	}
	public void setSales(long sales) {
		this.sales = sales;
	}
	public String getStandardDate() {
		return standardDate;
	}
	public void setStandardDate(String standardDate) {
		this.standardDate = standardDate;
	}
	public long getOrdinarySharesSize() {
		return ordinarySharesSize;
	}
	public void setOrdinarySharesSize(long ordinarySharesSize) {
		this.ordinarySharesSize = ordinarySharesSize;
	}
	public long getPrefferedSharesSize() {
		return prefferedSharesSize;
	}
	public void setPrefferedSharesSize(long prefferedSharesSize) {
		this.prefferedSharesSize = prefferedSharesSize;
	}
	public float getRoi() {
		return roi;
	}
	public void setRoi(float roi) {
		this.roi = roi;
	}
	public long getInvestedCapital() {
		return investedCapital;
	}
	public void setInvestedCapital(long investedCapital) {
		this.investedCapital = investedCapital;
	}
	
	
	public CompanyFinancialStatus() {
		super();
	}
	
	public CompanyFinancialStatus(BaseStructure cfs) throws Exception {
		super(cfs);
	}
	public boolean isCalculated() {
		return isCalculated;
	}
	public void setCalculated(boolean isCalculated) {
		this.isCalculated = isCalculated;
	}
}
