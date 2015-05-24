package post;

public class StatementOfFinancialPosition extends BaseStructure {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6961386931449967473L;
	private Company company;
	private String standardDate;
	private boolean isAnnual;
	private boolean kospi;
	private boolean consolidatedStatement;
	private String ifrsGaap;
	private long totalAssets;
	private long noncurrentAssets;
	private long noncurrentPropertyPlantAndEquipment;
	private long intangibleAssets;
	private long investmentProperty;
	private long biologicalAssets;
	private long longTermInvestmentAssets;
	private long noncurrentTradeAndOtherReceivables;
	private long deferredTaxAssets;
	private long otherFinancialAssets;
	private long investmentAssets;
	private long ventureCapital;
	private long allowanceForVentureCapital;
	private long otherNoncurrentAssets;
	private long currentAssets;
	private long inventories;
	private long currentTradeAndOtherReceivables;
	private long shorttermInvestmentAssets;
	private long currentTaxAssets;
	private long otherNonfinancialAssets;
	private long cashAndCashEquivalents;
	private long quickAssets;
	private long cashAndDueFromFinancialInstitutions;
	private long loans;
	private long allowance;
	private long propertyPlantAndEquipment;
	private long accumulatedDepreciation;
	private long otherAssets;
	private long allowanceForOtherAssets;
	private long separateAccountLiablitiesAssets;
	private long cardAssets;
	private long totalAllowance;
	private long installmentCreditAssets;
	private long leaseAssets;
	private long continuingInvolvementAssets;
	private long newTechnologyAssets;
	private long noncurrentAssetsHeldForSaleAndDiscontinued;
	private long cmaAssets;
	private long totalEquity;
	private long paidinCapital;
	private long issuedCapital;
	private long earningsAndLosses;
	private long otherComponentsOfEquity;
	private long accumulatedOtherComprehensiveIncome;
	private long amountRelatedToCapitalAssetsHeldForSale;
	private long totalLiabilities;
	private long noncurrentLiabilities;
	private long noncurrentTradeAndOtherNoncurrentPayables;
	private long deferredTaxLiabilities;
	private long noncurretOtherNonfinancialLiabilities;
	private long retirementBenefitObligations;
	private long noncurrentBorrowings;
	private long currentLiabilities;
	private long currentTradeAndOtherNoncurrentPayables;
	private long currentBorrowings;
	private long noncurrentOtherNonfinancialLiabilities;
	private long shorttermAllowanceLiabilities;
	private long longtermAllowanceLiabilities;
	private long currentTaxLiabilities;
	private long financialLiabilitiesAtFairValueThroughProfitOrLoss;
	private long deckungsfonds;
	private long borrowingsAndDebentures;
	private long otherLiabilities;
	private long separateAccountLiablitiesLiabilities;
	private long shorttermFinancialLiabilities;
	private long policyholdersEquityAdjustment;
	private long deferredLiability;
	private long deposits;
	private long continuingInvolvementAssetRelatedDebt;
	private long liabilitiesHeldForSale;
	private long otherFinancialLiabilities;
	
	
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
		if(standardDate.contains(".")) {
			StringBuffer sb = new StringBuffer();
			for ( int pos = 0 ; pos < standardDate.length() ; pos++ )
				if ( standardDate.charAt(pos) >= '0' && standardDate.charAt(pos) <= '9' )
					sb.append(standardDate.charAt(pos));
			this.standardDate = sb.toString();
		} else
			this.standardDate = standardDate;
	}
	public boolean isAnnual() {
		return isAnnual;
	}
	public void setAnnual(boolean isAnnual) {
		this.isAnnual = isAnnual;
	}
	public boolean isKospi() {
		return kospi;
	}
	public void setKospi(boolean kospi) {
		this.kospi = kospi;
	}
	public boolean isConsolidatedStatement() {
		return consolidatedStatement;
	}
	public void setConsolidatedStatement(boolean consolidatedStatement) {
		this.consolidatedStatement = consolidatedStatement;
	}
	public String getIfrsGaap() {
		return ifrsGaap;
	}
	public void setIfrsGaap(String ifrsGaap) {
		this.ifrsGaap = ifrsGaap;
	}
	public long getTotalAssets() {
		return totalAssets;
	}
	public void setTotalAssets(long totalAssets) {
		this.totalAssets = totalAssets;
	}
	public long getNoncurrentAssets() {
		return noncurrentAssets;
	}
	public void setNoncurrentAssets(long noncurrentAssets) {
		this.noncurrentAssets = noncurrentAssets;
	}
	public long getNoncurrentPropertyPlantAndEquipment() {
		return noncurrentPropertyPlantAndEquipment;
	}
	public void setNoncurrentPropertyPlantAndEquipment(
			long noncurrentPropertyPlantAndEquipment) {
		this.noncurrentPropertyPlantAndEquipment = noncurrentPropertyPlantAndEquipment;
	}
	public long getIntangibleAssets() {
		return intangibleAssets;
	}
	public void setIntangibleAssets(long intangibleAssets) {
		this.intangibleAssets = intangibleAssets;
	}
	public long getInvestmentProperty() {
		return investmentProperty;
	}
	public void setInvestmentProperty(long investmentProperty) {
		this.investmentProperty = investmentProperty;
	}
	public long getBiologicalAssets() {
		return biologicalAssets;
	}
	public void setBiologicalAssets(long biologicalAssets) {
		this.biologicalAssets = biologicalAssets;
	}
	public long getLongTermInvestmentAssets() {
		return longTermInvestmentAssets;
	}
	public void setLongTermInvestmentAssets(long longTermInvestmentAssets) {
		this.longTermInvestmentAssets = longTermInvestmentAssets;
	}
	public long getNoncurrentTradeAndOtherReceivables() {
		return noncurrentTradeAndOtherReceivables;
	}
	public void setNoncurrentTradeAndOtherReceivables(
			long noncurrentTradeAndOtherReceivables) {
		this.noncurrentTradeAndOtherReceivables = noncurrentTradeAndOtherReceivables;
	}
	public long getDeferredTaxAssets() {
		return deferredTaxAssets;
	}
	public void setDeferredTaxAssets(long deferredTaxAssets) {
		this.deferredTaxAssets = deferredTaxAssets;
	}
	public long getOtherFinancialAssets() {
		return otherFinancialAssets;
	}
	public void setOtherFinancialAssets(long otherFinancialAssets) {
		this.otherFinancialAssets = otherFinancialAssets;
	}
	public long getInvestmentAssets() {
		return investmentAssets;
	}
	public void setInvestmentAssets(long investmentAssets) {
		this.investmentAssets = investmentAssets;
	}
	public long getVentureCapital() {
		return ventureCapital;
	}
	public void setVentureCapital(long ventureCapital) {
		this.ventureCapital = ventureCapital;
	}
	public long getAllowanceForVentureCapital() {
		return allowanceForVentureCapital;
	}
	public void setAllowanceForVentureCapital(long allowanceForVentureCapital) {
		this.allowanceForVentureCapital = allowanceForVentureCapital;
	}
	public long getOtherNoncurrentAssets() {
		return otherNoncurrentAssets;
	}
	public void setOtherNoncurrentAssets(long otherNoncurrentAssets) {
		this.otherNoncurrentAssets = otherNoncurrentAssets;
	}
	public long getCurrentAssets() {
		return currentAssets;
	}
	public void setCurrentAssets(long currentAssets) {
		this.currentAssets = currentAssets;
	}
	public long getInventories() {
		return inventories;
	}
	public void setInventories(long inventories) {
		this.inventories = inventories;
	}
	public long getCurrentTradeAndOtherReceivables() {
		return currentTradeAndOtherReceivables;
	}
	public void setCurrentTradeAndOtherReceivables(
			long currentTradeAndOtherReceivables) {
		this.currentTradeAndOtherReceivables = currentTradeAndOtherReceivables;
	}
	public long getShorttermInvestmentAssets() {
		return shorttermInvestmentAssets;
	}
	public void setShorttermInvestmentAssets(long shorttermInvestmentAssets) {
		this.shorttermInvestmentAssets = shorttermInvestmentAssets;
	}
	public long getCurrentTaxAssets() {
		return currentTaxAssets;
	}
	public void setCurrentTaxAssets(long currentTaxAssets) {
		this.currentTaxAssets = currentTaxAssets;
	}
	public long getOtherNonfinancialAssets() {
		return otherNonfinancialAssets;
	}
	public void setOtherNonfinancialAssets(long otherNonfinancialAssets) {
		this.otherNonfinancialAssets = otherNonfinancialAssets;
	}
	public long getCashAndCashEquivalents() {
		return cashAndCashEquivalents;
	}
	public void setCashAndCashEquivalents(long cashAndCashEquivalents) {
		this.cashAndCashEquivalents = cashAndCashEquivalents;
	}
	public long getQuickAssets() {
		return quickAssets;
	}
	public void setQuickAssets(long quickAssets) {
		this.quickAssets = quickAssets;
	}
	public long getCashAndDueFromFinancialInstitutions() {
		return cashAndDueFromFinancialInstitutions;
	}
	public void setCashAndDueFromFinancialInstitutions(
			long cashAndDueFromFinancialInstitutions) {
		this.cashAndDueFromFinancialInstitutions = cashAndDueFromFinancialInstitutions;
	}
	public long getLoans() {
		return loans;
	}
	public void setLoans(long loans) {
		this.loans = loans;
	}
	public long getAllowance() {
		return allowance;
	}
	public void setAllowance(long allowance) {
		this.allowance = allowance;
	}
	public long getPropertyPlantAndEquipment() {
		return propertyPlantAndEquipment;
	}
	public void setPropertyPlantAndEquipment(long propertyPlantAndEquipment) {
		this.propertyPlantAndEquipment = propertyPlantAndEquipment;
	}
	public long getAccumulatedDepreciation() {
		return accumulatedDepreciation;
	}
	public void setAccumulatedDepreciation(long accumulatedDepreciation) {
		this.accumulatedDepreciation = accumulatedDepreciation;
	}
	public long getOtherAssets() {
		return otherAssets;
	}
	public void setOtherAssets(long otherAssets) {
		this.otherAssets = otherAssets;
	}
	public long getAllowanceForOtherAssets() {
		return allowanceForOtherAssets;
	}
	public void setAllowanceForOtherAssets(long allowanceForOtherAssets) {
		this.allowanceForOtherAssets = allowanceForOtherAssets;
	}
	public long getSeparateAccountLiablitiesAssets() {
		return separateAccountLiablitiesAssets;
	}
	public void setSeparateAccountLiablitiesAssets(
			long separateAccountLiablitiesAssets) {
		this.separateAccountLiablitiesAssets = separateAccountLiablitiesAssets;
	}
	public long getCardAssets() {
		return cardAssets;
	}
	public void setCardAssets(long cardAssets) {
		this.cardAssets = cardAssets;
	}
	public long getTotalAllowance() {
		return totalAllowance;
	}
	public void setTotalAllowance(long totalAllowance) {
		this.totalAllowance = totalAllowance;
	}
	public long getInstallmentCreditAssets() {
		return installmentCreditAssets;
	}
	public void setInstallmentCreditAssets(long installmentCreditAssets) {
		this.installmentCreditAssets = installmentCreditAssets;
	}
	public long getLeaseAssets() {
		return leaseAssets;
	}
	public void setLeaseAssets(long leaseAssets) {
		this.leaseAssets = leaseAssets;
	}
	public long getContinuingInvolvementAssets() {
		return continuingInvolvementAssets;
	}
	public void setContinuingInvolvementAssets(long continuingInvolvementAssets) {
		this.continuingInvolvementAssets = continuingInvolvementAssets;
	}
	public long getNewTechnologyAssets() {
		return newTechnologyAssets;
	}
	public void setNewTechnologyAssets(long newTechnologyAssets) {
		this.newTechnologyAssets = newTechnologyAssets;
	}
	public long getNoncurrentAssetsHeldForSaleAndDiscontinued() {
		return noncurrentAssetsHeldForSaleAndDiscontinued;
	}
	public void setNoncurrentAssetsHeldForSaleAndDiscontinued(
			long noncurrentAssetsHeldForSaleAndDiscontinued) {
		this.noncurrentAssetsHeldForSaleAndDiscontinued = noncurrentAssetsHeldForSaleAndDiscontinued;
	}
	public long getCmaAssets() {
		return cmaAssets;
	}
	public void setCmaAssets(long cmaAssets) {
		this.cmaAssets = cmaAssets;
	}
	public long getTotalEquity() {
		return totalEquity;
	}
	public void setTotalEquity(long totalEquity) {
		this.totalEquity = totalEquity;
	}
	public long getPaidinCapital() {
		return paidinCapital;
	}
	public void setPaidinCapital(long paidinCapital) {
		this.paidinCapital = paidinCapital;
	}
	public long getIssuedCapital() {
		return issuedCapital;
	}
	public void setIssuedCapital(long issuedCapital) {
		this.issuedCapital = issuedCapital;
	}
	public long getEarningsAndLosses() {
		return earningsAndLosses;
	}
	public void setEarningsAndLosses(long earningsAndLosses) {
		this.earningsAndLosses = earningsAndLosses;
	}
	public long getOtherComponentsOfEquity() {
		return otherComponentsOfEquity;
	}
	public void setOtherComponentsOfEquity(long otherComponentsOfEquity) {
		this.otherComponentsOfEquity = otherComponentsOfEquity;
	}
	public long getAccumulatedOtherComprehensiveIncome() {
		return accumulatedOtherComprehensiveIncome;
	}
	public void setAccumulatedOtherComprehensiveIncome(
			long accumulatedOtherComprehensiveIncome) {
		this.accumulatedOtherComprehensiveIncome = accumulatedOtherComprehensiveIncome;
	}
	public long getAmountRelatedToCapitalAssetsHeldForSale() {
		return amountRelatedToCapitalAssetsHeldForSale;
	}
	public void setAmountRelatedToCapitalAssetsHeldForSale(
			long amountRelatedToCapitalAssetsHeldForSale) {
		this.amountRelatedToCapitalAssetsHeldForSale = amountRelatedToCapitalAssetsHeldForSale;
	}
	public long getTotalLiabilities() {
		return totalLiabilities;
	}
	public void setTotalLiabilities(long totalLiabilities) {
		this.totalLiabilities = totalLiabilities;
	}
	public long getNoncurrentLiabilities() {
		return noncurrentLiabilities;
	}
	public void setNoncurrentLiabilities(long noncurrentLiabilities) {
		this.noncurrentLiabilities = noncurrentLiabilities;
	}
	public long getNoncurrentTradeAndOtherNoncurrentPayables() {
		return noncurrentTradeAndOtherNoncurrentPayables;
	}
	public void setNoncurrentTradeAndOtherNoncurrentPayables(
			long noncurrentTradeAndOtherNoncurrentPayables) {
		this.noncurrentTradeAndOtherNoncurrentPayables = noncurrentTradeAndOtherNoncurrentPayables;
	}
	public long getDeferredTaxLiabilities() {
		return deferredTaxLiabilities;
	}
	public void setDeferredTaxLiabilities(long deferredTaxLiabilities) {
		this.deferredTaxLiabilities = deferredTaxLiabilities;
	}
	public long getNoncurretOtherNonfinancialLiabilities() {
		return noncurretOtherNonfinancialLiabilities;
	}
	public void setNoncurretOtherNonfinancialLiabilities(
			long noncurretOtherNonfinancialLiabilities) {
		this.noncurretOtherNonfinancialLiabilities = noncurretOtherNonfinancialLiabilities;
	}
	public long getRetirementBenefitObligations() {
		return retirementBenefitObligations;
	}
	public void setRetirementBenefitObligations(long retirementBenefitObligations) {
		this.retirementBenefitObligations = retirementBenefitObligations;
	}
	public long getNoncurrentBorrowings() {
		return noncurrentBorrowings;
	}
	public void setNoncurrentBorrowings(long noncurrentBorrowings) {
		this.noncurrentBorrowings = noncurrentBorrowings;
	}
	public long getCurrentLiabilities() {
		return currentLiabilities;
	}
	public void setCurrentLiabilities(long currentLiabilities) {
		this.currentLiabilities = currentLiabilities;
	}
	public long getCurrentTradeAndOtherNoncurrentPayables() {
		return currentTradeAndOtherNoncurrentPayables;
	}
	public void setCurrentTradeAndOtherNoncurrentPayables(
			long currentTradeAndOtherNoncurrentPayables) {
		this.currentTradeAndOtherNoncurrentPayables = currentTradeAndOtherNoncurrentPayables;
	}
	public long getCurrentBorrowings() {
		return currentBorrowings;
	}
	public void setCurrentBorrowings(long currentBorrowings) {
		this.currentBorrowings = currentBorrowings;
	}
	public long getNoncurrentOtherNonfinancialLiabilities() {
		return noncurrentOtherNonfinancialLiabilities;
	}
	public void setNoncurrentOtherNonfinancialLiabilities(
			long noncurrentOtherNonfinancialLiabilities) {
		this.noncurrentOtherNonfinancialLiabilities = noncurrentOtherNonfinancialLiabilities;
	}
	public long getShorttermAllowanceLiabilities() {
		return shorttermAllowanceLiabilities;
	}
	public void setShorttermAllowanceLiabilities(long shorttermAllowanceLiabilities) {
		this.shorttermAllowanceLiabilities = shorttermAllowanceLiabilities;
	}
	public long getLongtermAllowanceLiabilities() {
		return longtermAllowanceLiabilities;
	}
	public void setLongtermAllowanceLiabilities(long longtermAllowanceLiabilities) {
		this.longtermAllowanceLiabilities = longtermAllowanceLiabilities;
	}
	public long getCurrentTaxLiabilities() {
		return currentTaxLiabilities;
	}
	public void setCurrentTaxLiabilities(long currentTaxLiabilities) {
		this.currentTaxLiabilities = currentTaxLiabilities;
	}
	public long getFinancialLiabilitiesAtFairValueThroughProfitOrLoss() {
		return financialLiabilitiesAtFairValueThroughProfitOrLoss;
	}
	public void setFinancialLiabilitiesAtFairValueThroughProfitOrLoss(
			long financialLiabilitiesAtFairValueThroughProfitOrLoss) {
		this.financialLiabilitiesAtFairValueThroughProfitOrLoss = financialLiabilitiesAtFairValueThroughProfitOrLoss;
	}
	public long getDeckungsfonds() {
		return deckungsfonds;
	}
	public void setDeckungsfonds(long deckungsfonds) {
		this.deckungsfonds = deckungsfonds;
	}
	public long getBorrowingsAndDebentures() {
		return borrowingsAndDebentures;
	}
	public void setBorrowingsAndDebentures(long borrowingsAndDebentures) {
		this.borrowingsAndDebentures = borrowingsAndDebentures;
	}
	public long getOtherLiabilities() {
		return otherLiabilities;
	}
	public void setOtherLiabilities(long otherLiabilities) {
		this.otherLiabilities = otherLiabilities;
	}
	public long getSeparateAccountLiablitiesLiabilities() {
		return separateAccountLiablitiesLiabilities;
	}
	public void setSeparateAccountLiablitiesLiabilities(
			long separateAccountLiablitiesLiabilities) {
		this.separateAccountLiablitiesLiabilities = separateAccountLiablitiesLiabilities;
	}
	public long getShorttermFinancialLiabilities() {
		return shorttermFinancialLiabilities;
	}
	public void setShorttermFinancialLiabilities(long shorttermFinancialLiabilities) {
		this.shorttermFinancialLiabilities = shorttermFinancialLiabilities;
	}
	public long getPolicyholdersEquityAdjustment() {
		return policyholdersEquityAdjustment;
	}
	public void setPolicyholdersEquityAdjustment(long policyholdersEquityAdjustment) {
		this.policyholdersEquityAdjustment = policyholdersEquityAdjustment;
	}
	public long getDeferredLiability() {
		return deferredLiability;
	}
	public void setDeferredLiability(long deferredLiability) {
		this.deferredLiability = deferredLiability;
	}
	public long getDeposits() {
		return deposits;
	}
	public void setDeposits(long deposits) {
		this.deposits = deposits;
	}
	public long getContinuingInvolvementAssetRelatedDebt() {
		return continuingInvolvementAssetRelatedDebt;
	}
	public void setContinuingInvolvementAssetRelatedDebt(
			long continuingInvolvementAssetRelatedDebt) {
		this.continuingInvolvementAssetRelatedDebt = continuingInvolvementAssetRelatedDebt;
	}
	public long getLiabilitiesHeldForSale() {
		return liabilitiesHeldForSale;
	}
	public void setLiabilitiesHeldForSale(long liabilitiesHeldForSale) {
		this.liabilitiesHeldForSale = liabilitiesHeldForSale;
	}
	public long getOtherFinancialLiabilities() {
		return otherFinancialLiabilities;
	}
	public void setOtherFinancialLiabilities(long otherFinancialLiabilities) {
		this.otherFinancialLiabilities = otherFinancialLiabilities;
	}
}
