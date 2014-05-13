package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import post.Company;
import post.StatementOfFinancialPosition;

/**
<pre>
CREATE TABLE `tb_statement_of_financial_position` (
  `STOCK_ID` varchar(10) NOT NULL DEFAULT '',
  `STANDARD_DATE` varchar(8) NOT NULL DEFAULT '',
  `IS_ANNUAL` varchar(1) NOT NULL DEFAULT '',
  `KOSPI_YN` varchar(1) DEFAULT NULL,
  `CONSOLIDATED_STATEMENT` varchar(1) NOT NULL DEFAULT 'Y',
  `IFRS_GAAP` varchar(4) NOT NULL DEFAULT 'IFRS',
  `TOTAL_ASSETS` bigint(20) DEFAULT NULL,
  `NONCURRENT_ASSETS` bigint(20) DEFAULT NULL,
  `NONCURRENT_PROPERTY_PLANT_AND_EQUIPMENT` bigint(20) DEFAULT NULL,
  `INTANGIBLE_ASSETS` bigint(20) DEFAULT NULL,
  `INVESTMENT_PROPERTY` bigint(20) DEFAULT NULL,
  `BIOLOGICAL_ASSETS` bigint(20) DEFAULT NULL,
  `LONG_TERM_INVESTMENT_ASSETS` bigint(20) DEFAULT NULL,
  `NONCURRENT_TRADE_AND_OTHER_RECEIVABLES` bigint(20) DEFAULT NULL,
  `DEFERRED_TAX_ASSETS` bigint(20) DEFAULT NULL,
  `OTHER_FINANCIAL_ASSETS` bigint(20) DEFAULT NULL,
  `INVESTMENT_ASSETS` bigint(20) DEFAULT NULL,
  `VENTURE_CAPITAL` bigint(20) DEFAULT NULL,
  `ALLOWANCE_FOR_VENTURE_CAPITAL` bigint(20) DEFAULT NULL,
  `OTHER_NONCURRENT_ASSETS` bigint(20) DEFAULT NULL,
  `CURRENT_ASSETS` bigint(20) DEFAULT NULL,
  `INVENTORIES` bigint(20) DEFAULT NULL,
  `CURRENT_TRADE_AND_OTHER_RECEIVABLES` bigint(20) DEFAULT NULL,
  `SHORTTERM_INVESTMENT_ASSETS` bigint(20) DEFAULT NULL,
  `CURRENT_TAX_ASSETS` bigint(20) DEFAULT NULL,
  `OTHER_NONFINANCIAL_ASSETS` bigint(20) DEFAULT NULL,
  `CASH_AND_CASH_EQUIVALENTS` bigint(20) DEFAULT NULL,
  `QUICK_ASSETS` bigint(20) DEFAULT NULL,
  `CASH_AND_DUE_FROM_FINANCIAL_INSTITUTIONS` bigint(20) DEFAULT NULL,
  `LOANS` bigint(20) DEFAULT NULL,
  `ALLOWANCE` bigint(20) DEFAULT NULL,
  `PROPERTY_PLANT_AND_EQUIPMENT` bigint(20) DEFAULT NULL,
  `ACCUMULATED_DEPRECIATION` bigint(20) DEFAULT NULL,
  `OTHER_ASSETS` bigint(20) DEFAULT NULL,
  `ALLOWANCE_FOR_OTHER_ASSETS` bigint(20) DEFAULT NULL,
  `SEPARATE_ACCOUNT_LIABLITIES_ASSETS` bigint(20) DEFAULT NULL,
  `CARD_ASSETS` bigint(20) DEFAULT NULL,
  `TOTAL_ALLOWANCE` bigint(20) DEFAULT NULL,
  `INSTALLMENT_CREDIT_ASSETS` bigint(20) DEFAULT NULL,
  `LEASE_ASSETS` bigint(20) DEFAULT NULL,
  `CONTINUING_INVOLVEMENT_ASSETS` bigint(20) DEFAULT NULL,
  `NEW_TECHNOLOGY_ASSETS` bigint(20) DEFAULT NULL,
  `NONCURRENT_ASSETS_HELD_FOR_SALE_AND_DISCONTINUED` bigint(20) DEFAULT NULL,
  `CMA_ASSETS` bigint(20) DEFAULT NULL,
  `TOTAL_EQUITY` bigint(20) DEFAULT NULL,
  `PAIDIN_CAPITAL` bigint(20) DEFAULT NULL,
  `ISSUED_CAPITAL` bigint(20) DEFAULT NULL,
  `EARNINGS_AND_LOSSES` bigint(20) DEFAULT NULL,
  `OTHER_COMPONENTS_OF_EQUITY` bigint(20) DEFAULT NULL,
  `ACCUMULATED_OTHER_COMPREHENSIVE_INCOME` bigint(20) DEFAULT NULL,
  `AMOUNT_RELATED_TO_CAPITAL_ASSETS_HELD_FOR_SALE` bigint(20) DEFAULT NULL,
  `TOTAL_LIABILITIES` bigint(20) DEFAULT NULL,
  `NONCURRENT_LIABILITIES` bigint(20) DEFAULT NULL,
  `NONCURRENT_TRADE_AND_OTHER_NONCURRENT_PAYABLES` bigint(20) DEFAULT NULL,
  `DEFERRED_TAX_LIABILITIES` bigint(20) DEFAULT NULL,
  `NONCURRET_OTHER_NONFINANCIAL_LIABILITIES` bigint(20) DEFAULT NULL,
  `RETIREMENT_BENEFIT_OBLIGATIONS` bigint(20) DEFAULT NULL,
  `NONCURRENT_BORROWINGS` bigint(20) DEFAULT NULL,
  `CURRENT_LIABILITIES` bigint(20) DEFAULT NULL,
  `CURRENT_TRADE_AND_OTHER_NONCURRENT_PAYABLES` bigint(20) DEFAULT NULL,
  `CURRENT_BORROWINGS` bigint(20) DEFAULT NULL,
  `NONCURRENT_OTHER_NONFINANCIAL_LIABILITIES` bigint(20) DEFAULT NULL,
  `SHORTTERM_ALLOWANCE_LIABILITIES` bigint(20) DEFAULT NULL,
  `LONGTERM_ALLOWANCE_LIABILITIES` bigint(20) DEFAULT NULL,
  `CURRENT_TAX_LIABILITIES` bigint(20) DEFAULT NULL,
  `FINANCIAL_LIABILITIES_AT_FAIR_VALUE_THROUGH_PROFIT_OR_LOSS` bigint(20) DEFAULT NULL,
  `DECKUNGSFONDS` bigint(20) DEFAULT NULL,
  `BORROWINGS_AND_DEBENTURES` bigint(20) DEFAULT NULL,
  `OTHER_LIABILITIES` bigint(20) DEFAULT NULL,
  `SEPARATE_ACCOUNT_LIABLITIES_LIABILITIES` bigint(20) DEFAULT NULL,
  `SHORTTERM_FINANCIAL_LIABILITIES` bigint(20) DEFAULT NULL,
  `POLICYHOLDERS_EQUITY_ADJUSTMENT` bigint(20) DEFAULT NULL,
  `DEFERRED_LIABILITY` bigint(20) DEFAULT NULL,
  `DEPOSITS` bigint(20) DEFAULT NULL,
  `CONTINUING_INVOLVEMENT_ASSET_RELATED_DEBT` bigint(20) DEFAULT NULL,
  `LIABILITIES_HELD_FOR_SALE` bigint(20) DEFAULT NULL,
  `OTHER_FINANCIAL_LIABILITIES` bigint(20) DEFAULT NULL,
  `REGISTERED_DATE` varchar(8) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

</pre>
 * @author user
 *
 */
public class CompanyStatementOfFinancialPosition extends BaseDao {

	private static String INSERT_QUERY = "INSERT INTO `stock`.`tb_statement_of_financial_position` "
			+ "(`STOCK_ID`, "
			+ "`STANDARD_DATE`, "
			+ "`IS_ANNUAL`, "
			+ "`KOSPI_YN`, "
			+ "`CONSOLIDATED_STATEMENT`, "
			+ "`IFRS_GAAP`, "
			+ "`TOTAL_ASSETS`, "
			+ "`NONCURRENT_ASSETS`, "
			+ "`NONCURRENT_PROPERTY_PLANT_AND_EQUIPMENT`, "
			+ "`INTANGIBLE_ASSETS`, "
			+ "`INVESTMENT_PROPERTY`, "
			+ "`BIOLOGICAL_ASSETS`, "
			+ "`LONG_TERM_INVESTMENT_ASSETS`, "
			+ "`NONCURRENT_TRADE_AND_OTHER_RECEIVABLES`, "
			+ "`DEFERRED_TAX_ASSETS`, "
			+ "`OTHER_FINANCIAL_ASSETS`, "
			+ "`INVESTMENT_ASSETS`, "
			+ "`VENTURE_CAPITAL`, "
			+ "`ALLOWANCE_FOR_VENTURE_CAPITAL`, "
			+ "`OTHER_NONCURRENT_ASSETS`, "
			+ "`CURRENT_ASSETS`, "
			+ "`INVENTORIES`, "
			+ "`CURRENT_TRADE_AND_OTHER_RECEIVABLES`, "
			+ "`SHORTTERM_INVESTMENT_ASSETS`, "
			+ "`CURRENT_TAX_ASSETS`, "
			+ "`OTHER_NONFINANCIAL_ASSETS`, "
			+ "`CASH_AND_CASH_EQUIVALENTS`, "
			+ "`QUICK_ASSETS`, "
			+ "`CASH_AND_DUE_FROM_FINANCIAL_INSTITUTIONS`, "
			+ "`LOANS`, "
			+ "`ALLOWANCE`, "
			+ "`PROPERTY_PLANT_AND_EQUIPMENT`, "
			+ "`ACCUMULATED_DEPRECIATION`, "
			+ "`OTHER_ASSETS`, "
			+ "`ALLOWANCE_FOR_OTHER_ASSETS`, "
			+ "`SEPARATE_ACCOUNT_LIABLITIES_ASSETS`, "
			+ "`CARD_ASSETS`, "
			+ "`TOTAL_ALLOWANCE`, "
			+ "`INSTALLMENT_CREDIT_ASSETS`, "
			+ "`LEASE_ASSETS`, "
			+ "`CONTINUING_INVOLVEMENT_ASSETS`, "
			+ "`NEW_TECHNOLOGY_ASSETS`, "
			+ "`NONCURRENT_ASSETS_HELD_FOR_SALE_AND_DISCONTINUED`, "
			+ "`CMA_ASSETS`, "
			+ "`TOTAL_EQUITY`, "
			+ "`PAIDIN_CAPITAL`, "
			+ "`ISSUED_CAPITAL`, "
			+ "`EARNINGS_AND_LOSSES`, "
			+ "`OTHER_COMPONENTS_OF_EQUITY`, "
			+ "`ACCUMULATED_OTHER_COMPREHENSIVE_INCOME`, "
			+ "`AMOUNT_RELATED_TO_CAPITAL_ASSETS_HELD_FOR_SALE`, "
			+ "`TOTAL_LIABILITIES`, "
			+ "`NONCURRENT_LIABILITIES`, "
			+ "`NONCURRENT_TRADE_AND_OTHER_NONCURRENT_PAYABLES`, "
			+ "`DEFERRED_TAX_LIABILITIES`, "
			+ "`NONCURRET_OTHER_NONFINANCIAL_LIABILITIES`, "
			+ "`RETIREMENT_BENEFIT_OBLIGATIONS`, "
			+ "`NONCURRENT_BORROWINGS`, "
			+ "`CURRENT_LIABILITIES`, "
			+ "`CURRENT_TRADE_AND_OTHER_NONCURRENT_PAYABLES`, "
			+ "`CURRENT_BORROWINGS`, "
			+ "`NONCURRENT_OTHER_NONFINANCIAL_LIABILITIES`, "
			+ "`SHORTTERM_ALLOWANCE_LIABILITIES`, "
			+ "`LONGTERM_ALLOWANCE_LIABILITIES`, "
			+ "`CURRENT_TAX_LIABILITIES`, "
			+ "`FINANCIAL_LIABILITIES_AT_FAIR_VALUE_THROUGH_PROFIT_OR_LOSS`, "
			+ "`DECKUNGSFONDS`, "
			+ "`BORROWINGS_AND_DEBENTURES`, "
			+ "`OTHER_LIABILITIES`, "
			+ "`SEPARATE_ACCOUNT_LIABLITIES_LIABILITIES`, "
			+ "`SHORTTERM_FINANCIAL_LIABILITIES`, "
			+ "`POLICYHOLDERS_EQUITY_ADJUSTMENT`, "
			+ "`DEFERRED_LIABILITY`, "
			+ "`DEPOSITS`, "
			+ "`CONTINUING_INVOLVEMENT_ASSET_RELATED_DEBT`, "
			+ "`LIABILITIES_HELD_FOR_SALE`, "
			+ "`OTHER_FINANCIAL_LIABILITIES`) "
			+ "VALUES "
			+ " (?, "		//	+ "(<{STOCK_ID: }>, "                         
			+ " ?, "		//	+ "<{STANDARD_DATE: }>, "
			+ " ?, "		//	+ "<{IS_ANNUAL: }>, "
			+ " ?, "		//	+ "<{KOSPI_YN: }>, "
			+ " ?, "		//	+ "<{CONSOLIDATED_STATEMENT: Y}>, "
			+ " ?, "		//	+ "<{IFRS_GAAP: IFRS}>, "
			+ " ?, "		//	+ "<{TOTAL_ASSETS: }>, "
			+ " ?, "		//	+ "<{NONCURRENT_ASSETS: }>, "
			+ " ?, "		//	+ "<{NONCURRENT_PROPERTY_PLANT_AND_EQUIPMENT: }>, "
			+ " ?, "		//	+ "<{INTANGIBLE_ASSETS: }>, "
			+ " ?, "		//	+ "<{INVESTMENT_PROPERTY: }>, "
			+ " ?, "		//	+ "<{BIOLOGICAL_ASSETS: }>, "
			+ " ?, "		//	+ "<{LONG_TERM_INVESTMENT_ASSETS: }>, "
			+ " ?, "		//	+ "<{NONCURRENT_TRADE_AND_OTHER_RECEIVABLES: }>, "
			+ " ?, "		//	+ "<{DEFERRED_TAX_ASSETS: }>, "
			+ " ?, "		//	+ "<{OTHER_FINANCIAL_ASSETS: }>, "
			+ " ?, "		//	+ "<{INVESTMENT_ASSETS: }>, "
			+ " ?, "		//	+ "<{VENTURE_CAPITAL: }>, "
			+ " ?, "		//	+ "<{ALLOWANCE_FOR_VENTURE_CAPITAL: }>, "
			+ " ?, "		//	+ "<{OTHER_NONCURRENT_ASSETS: }>, "
			+ " ?, "		//	+ "<{CURRENT_ASSETS: }>, "
			+ " ?, "		//	+ "<{INVENTORIES: }>, "
			+ " ?, "		//	+ "<{CURRENT_TRADE_AND_OTHER_RECEIVABLES: }>, "
			+ " ?, "		//	+ "<{SHORTTERM_INVESTMENT_ASSETS: }>, "
			+ " ?, "		//	+ "<{CURRENT_TAX_ASSETS: }>, "
			+ " ?, "		//	+ "<{OTHER_NONFINANCIAL_ASSETS: }>, "
			+ " ?, "		//	+ "<{CASH_AND_CASH_EQUIVALENTS: }>, "
			+ " ?, "		//	+ "<{QUICK_ASSETS: }>, "
			+ " ?, "		//	+ "<{CASH_AND_DUE_FROM_FINANCIAL_INSTITUTIONS: }>, "
			+ " ?, "		//	+ "<{LOANS: }>, "
			+ " ?, "		//	+ "<{ALLOWANCE: }>, "
			+ " ?, "		//	+ "<{PROPERTY_PLANT_AND_EQUIPMENT: }>, "
			+ " ?, "		//	+ "<{ACCUMULATED_DEPRECIATION: }>, "
			+ " ?, "		//	+ "<{OTHER_ASSETS: }>, "
			+ " ?, "		//	+ "<{ALLOWANCE_FOR_OTHER_ASSETS: }>, "
			+ " ?, "		//	+ "<{SEPARATE_ACCOUNT_LIABLITIES_ASSETS: }>, "
			+ " ?, "		//	+ "<{CARD_ASSETS: }>, "
			+ " ?, "		//	+ "<{TOTAL_ALLOWANCE: }>, "
			+ " ?, "		//	+ "<{INSTALLMENT_CREDIT_ASSETS: }>, "
			+ " ?, "		//	+ "<{LEASE_ASSETS: }>, "
			+ " ?, "		//	+ "<{CONTINUING_INVOLVEMENT_ASSETS: }>, "
			+ " ?, "		//	+ "<{NEW_TECHNOLOGY_ASSETS: }>, "
			+ " ?, "		//	+ "<{NONCURRENT_ASSETS_HELD_FOR_SALE_AND_DISCONTINUED: }>, "
			+ " ?, "		//	+ "<{CMA_ASSETS: }>, "
			+ " ?, "		//	+ "<{TOTAL_EQUITY: }>, "
			+ " ?, "		//	+ "<{PAIDIN_CAPITAL: }>, "
			+ " ?, "		//	+ "<{ISSUED_CAPITAL: }>, "
			+ " ?, "		//	+ "<{EARNINGS_AND_LOSSES: }>, "
			+ " ?, "		//	+ "<{OTHER_COMPONENTS_OF_EQUITY: }>, "
			+ " ?, "		//	+ "<{ACCUMULATED_OTHER_COMPREHENSIVE_INCOME: }>, "
			+ " ?, "		//	+ "<{AMOUNT_RELATED_TO_CAPITAL_ASSETS_HELD_FOR_SALE: }>, "
			+ " ?, "		//	+ "<{TOTAL_LIABILITIES: }>, "
			+ " ?, "		//	+ "<{NONCURRENT_LIABILITIES: }>, "
			+ " ?, "		//	+ "<{NONCURRENT_TRADE_AND_OTHER_NONCURRENT_PAYABLES: }>, "
			+ " ?, "		//	+ "<{DEFERRED_TAX_LIABILITIES: }>, "
			+ " ?, "		//	+ "<{NONCURRET_OTHER_NONFINANCIAL_LIABILITIES: }>, "
			+ " ?, "		//	+ "<{RETIREMENT_BENEFIT_OBLIGATIONS: }>, "
			+ " ?, "		//	+ "<{NONCURRENT_BORROWINGS: }>, "
			+ " ?, "		//	+ "<{CURRENT_LIABILITIES: }>, "
			+ " ?, "		//	+ "<{CURRENT_TRADE_AND_OTHER_NONCURRENT_PAYABLES: }>, "
			+ " ?, "		//	+ "<{CURRENT_BORROWINGS: }>, "
			+ " ?, "		//	+ "<{NONCURRENT_OTHER_NONFINANCIAL_LIABILITIES: }>, "
			+ " ?, "		//	+ "<{SHORTTERM_ALLOWANCE_LIABILITIES: }>, "
			+ " ?, "		//	+ "<{LONGTERM_ALLOWANCE_LIABILITIES: }>, "
			+ " ?, "		//	+ "<{CURRENT_TAX_LIABILITIES: }>, "
			+ " ?, "		//	+ "<{FINANCIAL_LIABILITIES_AT_FAIR_VALUE_THROUGH_PROFIT_OR_LOSS: }>, "
			+ " ?, "		//	+ "<{DECKUNGSFONDS: }>, "
			+ " ?, "		//	+ "<{BORROWINGS_AND_DEBENTURES: }>, "
			+ " ?, "		//	+ "<{OTHER_LIABILITIES: }>, "
			+ " ?, "		//	+ "<{SEPARATE_ACCOUNT_LIABLITIES_LIABILITIES: }>, "
			+ " ?, "		//	+ "<{SHORTTERM_FINANCIAL_LIABILITIES: }>, "
			+ " ?, "		//	+ "<{POLICYHOLDERS_EQUITY_ADJUSTMENT: }>, "
			+ " ?, "		//	+ "<{DEFERRED_LIABILITY: }>, "
			+ " ?, "		//	+ "<{DEPOSITS: }>, "
			+ " ?, "		//	+ "<{CONTINUING_INVOLVEMENT_ASSET_RELATED_DEBT: }>, "
			+ " ?, "		//	+ "<{LIABILITIES_HELD_FOR_SALE: }>, "
			+ " ?) ";		//	+ "<{OTHER_FINANCIAL_LIABILITIES: }>);";


	public boolean insert(StatementOfFinancialPosition finstat)
			throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		boolean rtn = false;
		try {
			conn = getConnection();
			ps = conn.prepareStatement(INSERT_QUERY);
			ps.setString(1, finstat.getCompany().getId());
			ps.setString(2, finstat.getStandardDate());
			ps.setString(3, finstat.isAnnual() ? "Y" : "N");
			ps.setString(4, finstat.isKospi() ? "Y" : "N");
			ps.setString(5, finstat.isConsolidatedStatement() ? "Y" : "N");
			ps.setString(6, finstat.getIfrsGaap());
			ps.setLong(7,finstat.getTotalAssets());
			ps.setLong(8,finstat.getNoncurrentAssets());
			ps.setLong(9,finstat.getNoncurrentPropertyPlantAndEquipment());
			ps.setLong(10,finstat.getIntangibleAssets());
			ps.setLong(11,finstat.getInvestmentProperty());
			ps.setLong(12,finstat.getBiologicalAssets());
			ps.setLong(13,finstat.getLongTermInvestmentAssets());
			ps.setLong(14,finstat.getNoncurrentTradeAndOtherReceivables());
			ps.setLong(15,finstat.getDeferredTaxAssets());
			ps.setLong(16,finstat.getOtherFinancialAssets());
			ps.setLong(17,finstat.getInvestmentAssets());
			ps.setLong(18,finstat.getVentureCapital());
			ps.setLong(19,finstat.getAllowanceForVentureCapital());
			ps.setLong(20,finstat.getOtherNoncurrentAssets());
			ps.setLong(21,finstat.getCurrentAssets());
			ps.setLong(22,finstat.getInventories());
			ps.setLong(23,finstat.getCurrentTradeAndOtherReceivables());
			ps.setLong(24,finstat.getShorttermInvestmentAssets());
			ps.setLong(25,finstat.getCurrentTaxAssets());
			ps.setLong(26,finstat.getOtherNonfinancialAssets());
			ps.setLong(27,finstat.getCashAndCashEquivalents());
			ps.setLong(28,finstat.getQuickAssets());
			ps.setLong(29,finstat.getCashAndDueFromFinancialInstitutions());
			ps.setLong(30,finstat.getLoans());
			ps.setLong(31,finstat.getAllowance());
			ps.setLong(32,finstat.getPropertyPlantAndEquipment());
			ps.setLong(33,finstat.getAccumulatedDepreciation());
			ps.setLong(34,finstat.getOtherAssets());
			ps.setLong(35,finstat.getAllowanceForOtherAssets());
			ps.setLong(36,finstat.getSeparateAccountLiablitiesAssets());
			ps.setLong(37,finstat.getCardAssets());
			ps.setLong(38,finstat.getTotalAllowance());
			ps.setLong(39,finstat.getInstallmentCreditAssets());
			ps.setLong(40,finstat.getLeaseAssets());
			ps.setLong(41,finstat.getContinuingInvolvementAssets());
			ps.setLong(42,finstat.getNewTechnologyAssets());
			ps.setLong(43,finstat.getNoncurrentAssetsHeldForSaleAndDiscontinued());
			ps.setLong(44,finstat.getCmaAssets());
			ps.setLong(45,finstat.getTotalEquity());
			ps.setLong(46,finstat.getPaidinCapital());
			ps.setLong(47,finstat.getIssuedCapital());
			ps.setLong(48,finstat.getEarningsAndLosses());
			ps.setLong(49,finstat.getOtherComponentsOfEquity());
			ps.setLong(50,finstat.getAccumulatedOtherComprehensiveIncome());
			ps.setLong(51,finstat.getAmountRelatedToCapitalAssetsHeldForSale());
			ps.setLong(52,finstat.getTotalLiabilities());
			ps.setLong(53,finstat.getNoncurrentLiabilities());
			ps.setLong(54,finstat.getNoncurrentTradeAndOtherNoncurrentPayables());
			ps.setLong(55,finstat.getDeferredTaxLiabilities());
			ps.setLong(56,finstat.getNoncurretOtherNonfinancialLiabilities());
			ps.setLong(57,finstat.getRetirementBenefitObligations());
			ps.setLong(58,finstat.getNoncurrentBorrowings());
			ps.setLong(59,finstat.getCurrentLiabilities());
			ps.setLong(60,finstat.getCurrentTradeAndOtherNoncurrentPayables());
			ps.setLong(61,finstat.getCurrentBorrowings());
			ps.setLong(62,finstat.getNoncurrentOtherNonfinancialLiabilities());
			ps.setLong(63,finstat.getShorttermAllowanceLiabilities());
			ps.setLong(64,finstat.getLongtermAllowanceLiabilities());
			ps.setLong(65,finstat.getCurrentTaxLiabilities());
			ps.setLong(66,finstat.getFinancialLiabilitiesAtFairValueThroughProfitOrLoss());
			ps.setLong(67,finstat.getDeckungsfonds());
			ps.setLong(68,finstat.getBorrowingsAndDebentures());
			ps.setLong(69,finstat.getOtherLiabilities());
			ps.setLong(70,finstat.getSeparateAccountLiablitiesLiabilities());
			ps.setLong(71,finstat.getShorttermFinancialLiabilities());
			ps.setLong(72,finstat.getPolicyholdersEquityAdjustment());
			ps.setLong(73,finstat.getDeferredLiability());
			ps.setLong(74,finstat.getDeposits());
			ps.setLong(75,finstat.getContinuingInvolvementAssetRelatedDebt());
			ps.setLong(76,finstat.getLiabilitiesHeldForSale());
			ps.setLong(77,finstat.getOtherFinancialLiabilities());

			rtn = ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ps != null)
				try {
					ps.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			if (conn != null)
				try {
					conn.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
		}
		System.out.println(rtn);
		return rtn;
	}

	public boolean delete(StatementOfFinancialPosition finstat)
			throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		boolean rtn = false;
		try {
			conn = getConnection();
			ps = conn
					.prepareStatement("DELETE FROM tb_statement_of_financial_position WHERE STOCK_ID = ? and standard_date = ? and is_annual = ? and consolidated_statement = ? and ifrs_gaap = ?");
			ps.setString(1, finstat.getCompany().getId());
			ps.setString(2, finstat.getStandardDate());
			ps.setString(3, finstat.isAnnual() ? "Y" : "N");
			ps.setString(4, finstat.isConsolidatedStatement() ? "Y" : "N");
			ps.setString(5, finstat.getIfrsGaap());
			rtn = ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ps != null)
				try {
					ps.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			if (conn != null)
				try {
					conn.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
		}
		System.out.println(rtn);
		return rtn;
	}

	public boolean update(StatementOfFinancialPosition finstat)
			throws SQLException {
		boolean rtn = false;
		rtn = delete(finstat);
		if (rtn)
			rtn = insert(finstat);
		System.out.println(rtn);
		return rtn;
	}

	static private void setValueFromResultSet(StatementOfFinancialPosition rtn,
			ResultSet rs) throws SQLException {
		rtn.setStandardDate(rs.getString("STANDARD_DATE"));
		rtn.setAnnual(rs.getString("IS_ANNUAL").equals("Y"));
		rtn.setKospi(rs.getString("KOSPI_YN").equals("Y"));
		rtn.setConsolidatedStatement(rs.getString("CONSOLIDATED_STATEMENT").equals("Y"));
		rtn.setIfrsGaap(rs.getString("IFRS_GAAP"));
		rtn.setTotalAssets(rs.getLong("TOTAL_ASSETS"));
		rtn.setNoncurrentAssets(rs.getLong("NONCURRENT_ASSETS"));
		rtn.setNoncurrentPropertyPlantAndEquipment(rs
				.getLong("NONCURRENT_PROPERTY_PLANT_AND_EQUIPMENT"));
		rtn.setIntangibleAssets(rs.getLong("INTANGIBLE_ASSETS"));
		rtn.setInvestmentProperty(rs.getLong("INVESTMENT_PROPERTY"));
		rtn.setBiologicalAssets(rs.getLong("BIOLOGICAL_ASSETS"));
		rtn.setLongTermInvestmentAssets(rs
				.getLong("LONG_TERM_INVESTMENT_ASSETS"));
		rtn.setNoncurrentTradeAndOtherReceivables(rs
				.getLong("NONCURRENT_TRADE_AND_OTHER_RECEIVABLES"));
		rtn.setDeferredTaxAssets(rs.getLong("DEFERRED_TAX_ASSETS"));
		rtn.setOtherFinancialAssets(rs.getLong("OTHER_FINANCIAL_ASSETS"));
		rtn.setInvestmentAssets(rs.getLong("INVESTMENT_ASSETS"));
		rtn.setVentureCapital(rs.getLong("VENTURE_CAPITAL"));
		rtn.setAllowanceForVentureCapital(rs
				.getLong("ALLOWANCE_FOR_VENTURE_CAPITAL"));
		rtn.setOtherNoncurrentAssets(rs.getLong("OTHER_NONCURRENT_ASSETS"));
		rtn.setCurrentAssets(rs.getLong("CURRENT_ASSETS"));
		rtn.setInventories(rs.getLong("INVENTORIES"));
		rtn.setCurrentTradeAndOtherReceivables(rs
				.getLong("CURRENT_TRADE_AND_OTHER_RECEIVABLES"));
		rtn.setShorttermInvestmentAssets(rs
				.getLong("SHORTTERM_INVESTMENT_ASSETS"));
		rtn.setCurrentTaxAssets(rs.getLong("CURRENT_TAX_ASSETS"));
		rtn.setOtherNonfinancialAssets(rs.getLong("OTHER_NONFINANCIAL_ASSETS"));
		rtn.setCashAndCashEquivalents(rs.getLong("CASH_AND_CASH_EQUIVALENTS"));
		rtn.setQuickAssets(rs.getLong("QUICK_ASSETS"));
		rtn.setCashAndDueFromFinancialInstitutions(rs
				.getLong("CASH_AND_DUE_FROM_FINANCIAL_INSTITUTIONS"));
		rtn.setLoans(rs.getLong("LOANS"));
		rtn.setAllowance(rs.getLong("ALLOWANCE"));
		rtn.setPropertyPlantAndEquipment(rs
				.getLong("PROPERTY_PLANT_AND_EQUIPMENT"));
		rtn.setAccumulatedDepreciation(rs.getLong("ACCUMULATED_DEPRECIATION"));
		rtn.setOtherAssets(rs.getLong("OTHER_ASSETS"));
		rtn.setAllowanceForOtherAssets(rs.getLong("ALLOWANCE_FOR_OTHER_ASSETS"));
		rtn.setSeparateAccountLiablitiesAssets(rs
				.getLong("SEPARATE_ACCOUNT_LIABLITIES_ASSETS"));
		rtn.setCardAssets(rs.getLong("CARD_ASSETS"));
		rtn.setTotalAllowance(rs.getLong("TOTAL_ALLOWANCE"));
		rtn.setInstallmentCreditAssets(rs.getLong("INSTALLMENT_CREDIT_ASSETS"));
		rtn.setLeaseAssets(rs.getLong("LEASE_ASSETS"));
		rtn.setContinuingInvolvementAssets(rs
				.getLong("CONTINUING_INVOLVEMENT_ASSETS"));
		rtn.setNewTechnologyAssets(rs.getLong("NEW_TECHNOLOGY_ASSETS"));
		rtn.setNoncurrentAssetsHeldForSaleAndDiscontinued(rs
				.getLong("NONCURRENT_ASSETS_HELD_FOR_SALE_AND_DISCONTINUED"));
		rtn.setCmaAssets(rs.getLong("CMA_ASSETS"));
		rtn.setTotalEquity(rs.getLong("TOTAL_EQUITY"));
		rtn.setPaidinCapital(rs.getLong("PAIDIN_CAPITAL"));
		rtn.setIssuedCapital(rs.getLong("ISSUED_CAPITAL"));
		rtn.setEarningsAndLosses(rs.getLong("EARNINGS_AND_LOSSES"));
		rtn.setOtherComponentsOfEquity(rs.getLong("OTHER_COMPONENTS_OF_EQUITY"));
		rtn.setAccumulatedOtherComprehensiveIncome(rs
				.getLong("ACCUMULATED_OTHER_COMPREHENSIVE_INCOME"));
		rtn.setAmountRelatedToCapitalAssetsHeldForSale(rs
				.getLong("AMOUNT_RELATED_TO_CAPITAL_ASSETS_HELD_FOR_SALE"));
		rtn.setTotalLiabilities(rs.getLong("TOTAL_LIABILITIES"));
		rtn.setNoncurrentLiabilities(rs.getLong("NONCURRENT_LIABILITIES"));
		rtn.setNoncurrentTradeAndOtherNoncurrentPayables(rs
				.getLong("NONCURRENT_TRADE_AND_OTHER_NONCURRENT_PAYABLES"));
		rtn.setDeferredTaxLiabilities(rs.getLong("DEFERRED_TAX_LIABILITIES"));
		rtn.setNoncurretOtherNonfinancialLiabilities(rs
				.getLong("NONCURRET_OTHER_NONFINANCIAL_LIABILITIES"));
		rtn.setRetirementBenefitObligations(rs
				.getLong("RETIREMENT_BENEFIT_OBLIGATIONS"));
		rtn.setNoncurrentBorrowings(rs.getLong("NONCURRENT_BORROWINGS"));
		rtn.setCurrentLiabilities(rs.getLong("CURRENT_LIABILITIES"));
		rtn.setCurrentTradeAndOtherNoncurrentPayables(rs
				.getLong("CURRENT_TRADE_AND_OTHER_NONCURRENT_PAYABLES"));
		rtn.setCurrentBorrowings(rs.getLong("CURRENT_BORROWINGS"));
		rtn.setNoncurrentOtherNonfinancialLiabilities(rs
				.getLong("NONCURRENT_OTHER_NONFINANCIAL_LIABILITIES"));
		rtn.setShorttermAllowanceLiabilities(rs
				.getLong("SHORTTERM_ALLOWANCE_LIABILITIES"));
		rtn.setLongtermAllowanceLiabilities(rs
				.getLong("LONGTERM_ALLOWANCE_LIABILITIES"));
		rtn.setCurrentTaxLiabilities(rs.getLong("CURRENT_TAX_LIABILITIES"));
		rtn.setFinancialLiabilitiesAtFairValueThroughProfitOrLoss(rs
				.getLong("FINANCIAL_LIABILITIES_AT_FAIR_VALUE_THROUGH_PROFIT_OR_LOSS"));
		rtn.setDeckungsfonds(rs.getLong("DECKUNGSFONDS"));
		rtn.setBorrowingsAndDebentures(rs.getLong("BORROWINGS_AND_DEBENTURES"));
		rtn.setOtherLiabilities(rs.getLong("OTHER_LIABILITIES"));
		rtn.setSeparateAccountLiablitiesLiabilities(rs
				.getLong("SEPARATE_ACCOUNT_LIABLITIES_LIABILITIES"));
		rtn.setShorttermFinancialLiabilities(rs
				.getLong("SHORTTERM_FINANCIAL_LIABILITIES"));
		rtn.setPolicyholdersEquityAdjustment(rs
				.getLong("POLICYHOLDERS_EQUITY_ADJUSTMENT"));
		rtn.setDeferredLiability(rs.getLong("DEFERRED_LIABILITY"));
		rtn.setDeposits(rs.getLong("DEPOSITS"));
		rtn.setContinuingInvolvementAssetRelatedDebt(rs
				.getLong("CONTINUING_INVOLVEMENT_ASSET_RELATED_DEBT"));
		rtn.setLiabilitiesHeldForSale(rs.getLong("LIABILITIES_HELD_FOR_SALE"));
		rtn.setOtherFinancialLiabilities(rs
				.getLong("OTHER_FINANCIAL_LIABILITIES"));
	}

	private String SELECT_STATEMENT_01 = "SELECT * FROM tb_statement_of_financial_position WHERE STOCK_ID = ? and standard_date = ? and is_annual = ? and consolidated_statement = ? and ifrs_gaap = ?";

	public StatementOfFinancialPosition select(Company company,
			String standardDate, boolean isAnnual, boolean isConsolidated,
			boolean isIfrs) throws SQLException {
		StatementOfFinancialPosition rtn = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			if (standardDate == null) {
				ps = conn.prepareStatement(SELECT_STATEMENT_01);
				ps.setString(1, company.getId());
				ps.setString(2, standardDate);
				ps.setString(3, isAnnual ? "Y" : "N");
				ps.setString(4, isConsolidated ? "Y" : "N");
				ps.setString(5, isIfrs ? "IFRS" : "GAAP");
			}
			rs = ps.executeQuery();

			if (rs.next()) {
				rtn = new StatementOfFinancialPosition();
				rtn.setCompany(company);
				setValueFromResultSet(rtn, rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			if (ps != null)
				try {
					ps.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			if (conn != null)
				try {
					conn.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
		}
		return rtn;
	}

	private String SELECT_STATEMENT_02 = "SELECT * FROM tb_statement_of_financial_position WHERE STOCK_ID = ?";

	public java.util.ArrayList<StatementOfFinancialPosition> selectAllList(
			Company company) throws SQLException {
		java.util.ArrayList<StatementOfFinancialPosition> list = new java.util.ArrayList<StatementOfFinancialPosition>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement(SELECT_STATEMENT_02);
			ps.setString(1, company.getId());
			rs = ps.executeQuery();
			StatementOfFinancialPosition rtn = null;
			if (rs.next()) {
				rtn = new StatementOfFinancialPosition();
				rtn.setCompany(company);
				setValueFromResultSet(rtn, rs);
				list.add(rtn);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			if (ps != null)
				try {
					ps.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			if (conn != null)
				try {
					conn.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
		}
		return list;
	}
}
