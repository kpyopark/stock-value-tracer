package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import post.Company;
import post.CompanyFinancialStatus;

/**
<pre>
CREATE TABLE `tb_company_stat_refined` (
  `STOCK_ID` varchar(10) NOT NULL DEFAULT '',
  `STANDARD_DATE` varchar(8) NOT NULL DEFAULT '',
  `IS_ANNUAL` varchar(1) NOT NULL DEFAULT '',
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
  `CALCULATED_YN` varchar(1) DEFAULT 'N',
  `REGISTERED_DATE` varchar(8) DEFAULT NULL,
  PRIMARY KEY (`STOCK_ID`,`STANDARD_DATE`,`IS_ANNUAL`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;


</pre>
 * @author user
 * 
 * 
 *
 */
public class CompanyFinancialRefinedStatusDao extends BaseDao {
	
	final static long PRECISION_THRESHOLD = 1000000;
	
	public boolean insert(CompanyFinancialStatus financialStat) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		boolean rtn = false;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("INSERT INTO TB_COMPANY_STAT_REFINED ( STOCK_ID,STANDARD_DATE,IS_ANNUAL,ASSET_TOTAL,DEBT_TOTAL,CAPITAL,CAPITAL_TOTAL,SALES,OPERATION_PROFIT,ORDINARY_PROFIT,NET_PROFIT,INVESTED_CAPITAL,PREFFERED_STOCK_SIZE,GENERAL_STOCK_SIZE,DIVIDENED_RATIO,ROE,ROA,ROI,KOSPI_YN,FIXED_YN, MODIFIED_DATE, CALCULATED_YN, REGISTERED_DATE ) VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, now(),?,? )");
			int cnt = 1;
			ps.setString(cnt++, financialStat.getCompany().getId() );
			ps.setString(cnt++, financialStat.getStandardDate() );
			ps.setString(cnt++, financialStat.isQuarter() ? "N" : "Y" );
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
			ps.setString(cnt++,  financialStat.isCalculated() ? "Y": "N");
			ps.setString(cnt++,  financialStat.getRegisteredDate());
			rtn = ps.execute();
		} catch ( SQLException sqle ) {
			throw sqle;
		} catch ( Exception e ) {
			System.out.println("=============FINALCIAL DATE:" + financialStat.getStandardDate() );
			e.printStackTrace();
		} finally {
			if ( ps != null ) try { ps.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
			if ( conn != null ) try { conn.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
		}
		return rtn;
	}
	
	public CompanyFinancialStatus select(Company company, String standardDate, boolean isQuarter) throws SQLException {
		CompanyFinancialStatus rtn = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("SELECT * FROM TB_COMPANY_STAT_REFINED WHERE STOCK_ID = ? AND STANDARD_DATE = ? AND IS_ANNUAL = ?");
			ps.setString(1, company.getId() );
			ps.setString(2, standardDate );
			ps.setString(3, isQuarter ? "N" : "Y");
			rs = ps.executeQuery();
			
			if( rs.next() ) {
				rtn = getCfsFromResultSet(rs);
				rtn.setCompany(company);
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
	
	/*
	public CompanyFinancialStatus update(CompanyFinancialStatus oldStatus, CompanyFinancialStatus newStatus) throws SQLException {
		
		if ( oldStatus.getCompany() == null || newStatus.getCompany() == null ) return null;
		if ( !oldStatus.getCompany().getId().equals(newStatus.getCompany().getId()) ) return null;
		
		CompanyFinancialStatus mergeStatus = new CompanyFinancialStatus();
		mergeStatus.setCompany(oldStatus.getCompany());
		
		boolean needUpdated = false;
		
		if ( Math.abs(oldStatus.getAssets()-newStatus.getAssets()) > PRECISION_THRESHOLD ) {
			needUpdated = true;
			mergeStatus.setAssets(Math.max(oldStatus.getAssets(),newStatus.getAssets()));
		}
		return mergeStatus;
	}
	*/
	
	public boolean delete(CompanyFinancialStatus cfs) {
		boolean rtn = false;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("DELETE FROM TB_COMPANY_STAT_REFINED WHERE STOCK_ID = ? AND STANDARD_DATE = ? AND IS_ANNUAL = ?");
			ps.setString(1, cfs.getCompany().getId() );
			ps.setString(2, cfs.getStandardDate() );
			ps.setString(3, cfs.isQuarter() ? "N" : "Y");
			rtn = ps.executeUpdate() > 0;
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( ps != null ) try { ps.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
			if ( conn != null ) try { conn.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
		}
		return rtn;
	}
	
	private static CompanyFinancialStatus getCfsFromResultSet(ResultSet rs) throws SQLException {
		CompanyFinancialStatus rtn = new CompanyFinancialStatus();
		rtn.setStandardDate(rs.getString("STANDARD_DATE"));
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
		rtn.setCalculated("Y".equals(rs.getString("CALCULATED_YN")));
		rtn.setRegisteredDate(rs.getString("REGISTERED_DATE"));
		return rtn;
	}
	
	/**
	 * 해당 종목에 맞는 재무정보를 가지고 온다.
	 * @param company
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<CompanyFinancialStatus> getFinancialStatus(Company company, String registeredDate) throws SQLException {
		ArrayList<CompanyFinancialStatus> list = new ArrayList<CompanyFinancialStatus>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("SELECT * FROM TB_COMPANY_STAT_REFINED WHERE STOCK_ID = ? and registered_date <= ? ORDER BY 1,2 desc,3");
			ps.setString(1, company.getId() );
			ps.setString(2, registeredDate );
			rs = ps.executeQuery();
			
			while ( rs.next() ) {
				CompanyFinancialStatus cfs = getCfsFromResultSet(rs);
				cfs.setCompany(company);
				list.add(cfs);
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( rs != null ) try { rs.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
			if ( ps != null ) try { ps.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
			if ( conn != null ) try { conn.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
		}
		return list;
	}
	
	
}
