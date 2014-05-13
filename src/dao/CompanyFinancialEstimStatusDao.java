package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import post.Company;
import post.CompanyFinancialStatusEstimated;

/**
<pre>
CREATE TABLE `tb_company_estim_stat` (
  `STOCK_ID` varchar(10) NOT NULL DEFAULT '',
  `STANDARD_DATE` varchar(8) NOT NULL DEFAULT '',
  `IS_ANNUAL` varchar(1) NOT NULL DEFAULT '',
  `ESTIM_KIND` varchar(1) NOT NULL DEFAULT 'O',
  `ASSET_TOTAL` bigint(20) DEFAULT NULL,
  `DEBT_TOTAL` bigint(20) DEFAULT NULL,
  `CAPITAL` bigint(20) DEFAULT NULL,
  `CAPITAL_TOTAL` bigint(20) DEFAULT NULL,
  `SALES` bigint(20) DEFAULT NULL,
  `OPERATION_PROFIT` bigint(20) DEFAULT NULL,
  `ORDINARY_PROFIT` bigint(20) DEFAULT NULL,
  `NET_PROFIT` bigint(20) DEFAULT NULL,
  `INVESTED_CAPITAL` bigint(20) DEFAULT NULL,
  `PREFFERED_STOCK_SIZE` bigint(20) DEFAULT NULL,
  `GENERAL_STOCK_SIZE` bigint(20) DEFAULT NULL,
  `DIVIDENED_RATIO` float(8,3) DEFAULT NULL,
  `ROE` float(8,3) DEFAULT NULL,
  `ROA` float(8,3) DEFAULT NULL,
  `ROI` float(8,3) DEFAULT NULL,
  `KOSPI_YN` varchar(1) DEFAULT NULL,
  `FIXED_YN` varchar(1) DEFAULT NULL,
  `MODIFIED_DATE` date DEFAULT NULL,
  `ESTIMATED_YN` varchar(1) NOT NULL DEFAULT 'N',
  `RELATED_DATE_LIST` varchar(45) DEFAULT NULL,
  `REGISTERED_DATE` varchar(8) DEFAULT NULL,
  PRIMARY KEY (`STOCK_ID`,`STANDARD_DATE`,`IS_ANNUAL`,`ESTIM_KIND`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

</pre>
 * @author user
 *
 */
public class CompanyFinancialEstimStatusDao extends BaseDao {
	
	final static long PRECISION_THRESHOLD = 1000000;
	
	public CompanyFinancialStatusEstimated updateFinancialReportEstimated(Company company, String registeredDate) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		CompanyFinancialStatusEstimated rtn = null;
		try {
			conn = getConnection();
			//System.out.println(financialStat.getRelatedDateList());
			ps = conn.prepareStatement("CALL proc_estimate_financial_report(?, ?)");
			int cnt = 1;
			ps.setString(cnt++, company.getId());
			ps.setString(cnt++, registeredDate);

			ps.execute();

			ps = conn.prepareStatement("select * from TB_COMPANY_ESTIM_STAT WHERE  STOCK_ID = ? and is_annual = 'Y' and standard_date <= ? ORDER BY 1,2 desc,3 limit 1");
			cnt = 1;
			ps.setString(cnt++, company.getId());
			ps.setString(cnt++, registeredDate);
			
			rs = ps.executeQuery();
			
			rtn = getCompanyFinancialStatusEstimatedFromResultSet(company, rs);
			
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( ps != null ) try { ps.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
			if ( conn != null ) try { conn.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
		}
		return rtn;
	}
	
	private static CompanyFinancialStatusEstimated getCompanyFinancialStatusEstimatedFromResultSet(Company company, ResultSet rs) throws SQLException {
		CompanyFinancialStatusEstimated rtn = new CompanyFinancialStatusEstimated();
		rtn.setCompany(company);
		rtn.setStandardDate(rs.getString("STANDARD_DATE"));
		rtn.setEstimKind(rs.getString("ESTIM_KIND"));
		rtn.setQuarter(rs.getString("IS_ANNUAL").equals("N"));
		rtn.setAssets(rs.getLong("ASSET_TOTAL"));
		rtn.setDebt(rs.getLong("DEBT_TOTAL"));
		rtn.setCapital(rs.getLong("CAPITAL"));
		rtn.setGrossCapital(rs.getLong("CAPITAL_TOTAL"));
		rtn.setSales(rs.getLong("SALES"));
		rtn.setOperatingProfit(rs.getLong("OPERATION_PROFIT"));
		rtn.setOrdinaryProfit(rs.getLong("ORDINARY_PROFIT"));
		rtn.setNetProfit(rs.getLong("NET_PROFIT"));
		rtn.setInvestedCapital(rs.getLong("INVESTED_CAPITAL"));
		rtn.setPrefferedSharesSize(rs.getLong("PREFFERED_STOCK_SIZE"));
		rtn.setOrdinarySharesSize(rs.getLong("GENERAL_STOCK_SIZE"));
		rtn.setDividendRatio(rs.getFloat("DIVIDENED_RATIO"));
		rtn.setRoe(rs.getFloat("ROE"));
		rtn.setRoa(rs.getFloat("ROA"));
		rtn.setRoi(rs.getFloat("ROI"));
		rtn.setKOSPI("Y".equals(rs.getString("KOSPI_YN")));
		rtn.setFixed("Y".equals(rs.getString("FIXED_YN")));
		rtn.setEstimatedYn(rs.getString("ESTIMATED_YN"));
		rtn.setRelatedDateList(rs.getString("RELATED_DATE_LIST"));
		return rtn;
	}
	
	public boolean insert(CompanyFinancialStatusEstimated financialStat) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		boolean rtn = false;
		try {
			conn = getConnection();
			//System.out.println(financialStat.getRelatedDateList());
			ps = conn.prepareStatement("INSERT INTO TB_COMPANY_ESTIM_STAT ( STOCK_ID,STANDARD_DATE,IS_ANNUAL,ESTIM_KIND,ASSET_TOTAL,DEBT_TOTAL,CAPITAL,CAPITAL_TOTAL,SALES,OPERATION_PROFIT,ORDINARY_PROFIT,NET_PROFIT,INVESTED_CAPITAL,PREFFERED_STOCK_SIZE,GENERAL_STOCK_SIZE,DIVIDENED_RATIO,ROE,ROA,ROI,KOSPI_YN,FIXED_YN,ESTIMATED_YN,RELATED_DATE_LIST ) VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )");
			int cnt = 1;
			ps.setString(cnt++, financialStat.getCompany().getId() );
			ps.setString(cnt++, financialStat.getStandardDate() );
			ps.setString(cnt++, financialStat.isQuarter() ? "N" : "Y" );
			ps.setString(cnt++, financialStat.getEstimKind() );
			ps.setLong(cnt++, financialStat.getAssets() );
			ps.setLong(cnt++, financialStat.getDebt() );
			ps.setLong(cnt++, financialStat.getCapital() );
			ps.setLong(cnt++, financialStat.getGrossCapital() );
			ps.setLong(cnt++, financialStat.getSales() );
			ps.setLong(cnt++, financialStat.getOperatingProfit() );
			ps.setLong(cnt++, financialStat.getOrdinaryProfit() );
			ps.setLong(cnt++, financialStat.getNetProfit() );
			ps.setLong(cnt++, 0 );
			ps.setLong(cnt++, financialStat.getPrefferedSharesSize() );
			ps.setLong(cnt++, financialStat.getOrdinarySharesSize() );
			ps.setFloat(cnt++, financialStat.getDividendRatio());
			ps.setFloat(cnt++, financialStat.getRoe());
			ps.setFloat(cnt++, financialStat.getRoa());
			ps.setFloat(cnt++, financialStat.getRoi());
			ps.setString(cnt++, financialStat.isKOSPI() ? "Y" : "N" );
			ps.setString(cnt++, financialStat.isFixed() ? "Y" : "N" );
			ps.setString(cnt++, financialStat.getEstimatedYn() );
			ps.setString(cnt++, financialStat.getRelatedDateList());

			rtn = ps.execute();
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( ps != null ) try { ps.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
			if ( conn != null ) try { conn.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
		}
		return rtn;
	}
	
	public CompanyFinancialStatusEstimated select(Company company, String standardDate, String estimKind) throws SQLException {
		CompanyFinancialStatusEstimated rtn = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			if ( estimKind == null )
				estimKind = "O";
			if ( standardDate != null ) {
				ps = conn.prepareStatement(SELECT_STATEMENT_01);
				ps.setString(1, company.getId() );
				ps.setString(2, standardDate );
				ps.setString(3, estimKind );
			} else {
				ps = conn.prepareStatement(SELECT_STATEMENT_02);
				ps.setString(1, company.getId() );
				ps.setString(2, estimKind );
			}
			rs = ps.executeQuery();
			
			if( rs.next() ) {
				rtn = getCompanyFinancialStatusEstimatedFromResultSet(company, rs);
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( rs != null ) try { rs.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
			if ( ps != null ) try { ps.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
			if ( conn != null ) try { conn.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
		}
		return rtn;
	}
	
	private final static String SELECT_STATEMENT_01 = "SELECT * FROM TB_COMPANY_ESTIM_STAT WHERE STOCK_ID = ? AND STANDARD_DATE = ? AND ESTIM_KIND = ?";
	private final static String SELECT_STATEMENT_02 = "SELECT * FROM TB_COMPANY_ESTIM_STAT WHERE STOCK_ID = ? AND ESTIM_KIND = ? ORDER BY STANDARD_DATE DESC";
	
	public boolean delete(CompanyFinancialStatusEstimated cfs) {
		boolean rtn = false;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("DELETE FROM TB_COMPANY_ESTIM_STAT WHERE STOCK_ID = ? AND STANDARD_DATE = ? AND IS_ANNUAL = ? AND ESTIM_KIND = ?");
			ps.setString(1, cfs.getCompany().getId() );
			ps.setString(2, cfs.getStandardDate() );
			ps.setString(3, cfs.isQuarter() ? "N" : "Y");
			ps.setString(4, cfs.getEstimKind() );
			rtn = ps.executeUpdate() > 0;
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( ps != null ) try { ps.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
			if ( conn != null ) try { conn.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
		}
		return rtn;
		
	}
	
}
