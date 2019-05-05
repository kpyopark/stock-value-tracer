package post;

public class InstitutionalDamand extends BaseStructure {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4316470156895192175L;
	Company company;
	String standardDate;
	String standardTime;
	long stockClosingPrice;
	float stockUpdownRatioOfDay;
	long stockUpdownPriceOfDay;
	long foreignerNetDemand;
	float foreignerOwnershipRatio;
	long companyNetDemand;
	long individualNetDemand;
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
	public long getStockClosingPrice() {
		return stockClosingPrice;
	}
	public void setStockClosingPrice(long stockClosingPrice) {
		this.stockClosingPrice = stockClosingPrice;
	}
	public float getStockUpdownRatioOfDay() {
		return stockUpdownRatioOfDay;
	}
	public void setStockUpdownRatioOfDay(float stockUpdownRatioOfDay) {
		this.stockUpdownRatioOfDay = stockUpdownRatioOfDay;
	}
	public long getStockUpdownPriceOfDay() {
		return stockUpdownPriceOfDay;
	}
	public void setStockUpdownPriceOfDay(long stockUpdownPriceOfDay) {
		this.stockUpdownPriceOfDay = stockUpdownPriceOfDay;
	}
	public long getForeignerNetDemand() {
		return foreignerNetDemand;
	}
	public void setForeignerNetDemand(long foreignerNetDemand) {
		this.foreignerNetDemand = foreignerNetDemand;
	}
	public float getForeignerOwnershipRatio() {
		return foreignerOwnershipRatio;
	}
	public void setForeignerOwnershipRatio(float foreignerOwnershipRatio) {
		this.foreignerOwnershipRatio = foreignerOwnershipRatio;
	}
	public long getCompanyNetDemand() {
		return companyNetDemand;
	}
	public void setCompanyNetDemand(long companyNetDemand) {
		this.companyNetDemand = companyNetDemand;
	}
	public long getIndividualNetDemand() {
		return individualNetDemand;
	}
	public void setIndividualNetDemand(long individualNetDemand) {
		this.individualNetDemand = individualNetDemand;
	}
	
}
