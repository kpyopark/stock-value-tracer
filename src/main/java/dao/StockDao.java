package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import post.Company;
import post.Stock;

/**
<pre>
CREATE TABLE `tb_company_stock` (
  `STOCK_ID` varchar(10) CHARACTER SET euckr COLLATE euckr_bin NOT NULL DEFAULT '',
  `STANDARD_DATE` varchar(8) CHARACTER SET euckr COLLATE euckr_bin NOT NULL DEFAULT '',
  `STANDARD_TIME` varchar(6) CHARACTER SET euckr COLLATE euckr_bin NOT NULL DEFAULT '',
  `STOCK_PRICE` bigint(20) DEFAULT NULL,
  `STOCK_VOLUME` bigint(20) unsigned DEFAULT NULL,
  `CURRENT_ROA` float DEFAULT NULL,
  `CURRENT_ROE` float DEFAULT NULL,
  `CURRENT_ROI` float DEFAULT NULL,
  `ROA_RANK` int(10) unsigned DEFAULT NULL,
  `ROE_RANK` decimal(10,0) DEFAULT NULL,
  `ROI_RANK` decimal(10,0) DEFAULT NULL,
  `CURRENT_PER` float DEFAULT NULL,
  `CURRENT_PBR` float DEFAULT NULL,
  `PER_RANK` decimal(10,0) DEFAULT NULL,
  `PBR_RANK` decimal(10,0) DEFAULT NULL,
  `CURRENT_EPS` decimal(10,0) DEFAULT NULL,
  `MODIFIED_DATE` datetime DEFAULT NULL,
  `TODAY_HIGH` bigint(20) DEFAULT NULL,
  `TODAY_LOW` bigint(20) DEFAULT NULL,
  `ORDINARY_SHARES` bigint(20) DEFAULT NULL,
  `MARKET_CAPITALIZATION` bigint(20) DEFAULT NULL,
  `PAR_VALUE` float DEFAULT NULL,
  `OPEN_PRICE` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`STOCK_ID`,`STANDARD_DATE`,`STANDARD_TIME`),
  KEY `ix_company_stock_stock_id_standard_date_ordinary_share` (`STOCK_ID`,`STANDARD_DATE`,`ORDINARY_SHARES`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

</pre>
 * @author user
 *
 */
public class StockDao extends BaseDao {
	
	public boolean insert(Stock companyStock) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		boolean rtn = false;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("INSERT INTO tb_company_stock ( \n" +
					"STOCK_ID,\n" +
					"STANDARD_DATE,\n" +
					"STANDARD_TIME,\n" +
					"STOCK_PRICE,\n" +
					"STOCK_VOLUME,\n" +
					"CURRENT_ROA,\n" +
					"CURRENT_ROE,\n" +
					"CURRENT_ROI,\n" +
					"ROA_RANK,\n" +
					"ROE_RANK,\n" +
					"ROI_RANK,\n" +
					"CURRENT_PER,\n" +
					"CURRENT_PBR,\n" +
					"PER_RANK,\n" +
					"PBR_RANK,\n" +
					"CURRENT_EPS,\n" +
					"MODIFIED_DATE,\n" +
					"TODAY_HIGH,\n" +
					"TODAY_LOW,\n" +
					"ORDINARY_SHARES,\n" +
					"MARKET_CAPITALIZATION,\n" +
					"PAR_VALUE, \n" +
					"OPEN_PRICE\n" +
					") VALUES (\n" +
					"?, \n" +
					"?, \n" +
					"?, \n" +
					"?, \n" +
					"?, \n" +
					"?, \n" +
					"?, \n" +
					"?, \n" +
					"?, \n" +
					"?, \n" +
					"?, \n" +
					"?, \n" +
					"?, \n" +
					"?, \n" +
					"?, \n" +
					"?, \n" +
					"CURRENT_DATE, \n" +
					"?, \n" +
					"?, \n" +
					"?, \n" +
					"?, \n" +
					"?, \n" +
					"? " +
					")");
			ps.setString(1, companyStock.getCompany().getId() );
			ps.setString(2, companyStock.getStandardDate());
			ps.setString(3, companyStock.getStandardTime());
			ps.setLong(4, companyStock.getValue());
			ps.setLong(5, companyStock.getVolume());
			ps.setFloat(6, 0.0f);
			ps.setFloat(7, 0.0f);
			ps.setFloat(8, 0.0f);
			ps.setInt(9, 1);
			ps.setInt(10, 1);
			ps.setInt(11, 1);
			ps.setFloat(12, 0.0f);
			ps.setFloat(13, 0.0f);
			ps.setInt(14, 1);
			ps.setInt(15, 1);
			ps.setInt(16, 1);
			ps.setLong(17, companyStock.getTodayHigh());
			ps.setLong(18, companyStock.getTodayLow());
			ps.setLong(19, companyStock.getOrdinaryShares());
			ps.setLong(20, companyStock.getMarketCapitalization());
			ps.setFloat(21, companyStock.getParValue());
			ps.setInt(22, companyStock.getOpenPrice());
			
			rtn = ps.execute();
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( ps != null ) try { ps.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
			if ( conn != null ) try { conn.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
		}
		return rtn;
	}
	
	/**
	 * 
	 * 주식의 종류, 날짜, 시간에 맞추어 주가정보를 가지고 온다.
	 * 
	 * 시간이 없다면, 최종 시간의 정보를
	 * 날짜가 없다면, 최종 날짜의 정보를 가지고 온다.
	 * 
	 * @param id
	 * @param standardDate
	 * @param standardTime
	 * @return
	 * @throws SQLException
	 */
	public Stock select(Company company, String standardDate, String standardTime) throws SQLException {
		Stock rtn = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			if ( standardDate != null && standardTime != null ) {
				ps = conn.prepareStatement(SELECT_STATEMENT_03);
				ps.setString(1, company.getId() );
				ps.setString(2, standardDate);
				ps.setString(3, standardTime);
			} else if ( standardDate != null ) {
				ps = conn.prepareStatement(SELECT_STATEMENT_02);
				ps.setString(1, company.getId() );
				ps.setString(2, standardDate);
			} else {
				ps = conn.prepareStatement(SELECT_STATEMENT_01);
				ps.setString(1, company.getId() );
			}
			rs = ps.executeQuery();
			
			if( rs.next() ) {
				rtn = new Stock();
				rtn.setCompany(company);
				rtn.setValue(rs.getInt("STOCK_PRICE"));
				rtn.setVolume(rs.getInt("STOCK_VOLUME"));
				rtn.setStandardDate(rs.getString("STANDARD_DATE"));
				rtn.setStandardTime(rs.getString("STANDARD_TIME"));
				rtn.setTodayHigh(rs.getInt("TODAY_HIGH"));
				rtn.setTodayLow(rs.getInt("TODAY_LOW"));
				rtn.setOrdinaryShares(rs.getLong("ORDINARY_SHARES"));
				rtn.setMarketCapitalization(rs.getLong("MARKET_CAPITALIZATION"));
				rtn.setParValue(rs.getFloat("PAR_VALUE"));
				rtn.setOpenPrice(rs.getInt("OPEN_PRICE"));
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
	
	private static final String SELECT_STATEMENT_01 = "SELECT * FROM TB_COMPANY_STOCK WHERE STOCK_ID = ? ORDER BY STANDARD_DATE DESC, STANDARD_TIME DESC";
	private static final String SELECT_STATEMENT_02 = "SELECT * FROM TB_COMPANY_STOCK WHERE STOCK_ID = ? AND STANDARD_DATE = ? ORDER BY STANDARD_TIME DESC";
	private static final String SELECT_STATEMENT_03 = "SELECT * FROM TB_COMPANY_STOCK WHERE STOCK_ID = ? AND STANDARD_DATE = ? AND STANDARD_TIME =?";
	
	private static final String SELECT_LATEST_STATEMENT_01 = "SELECT * FROM TB_COMPANY_STOCK WHERE STOCK_ID = ? ORDER BY STANDARD_DATE DESC, STANDARD_TIME DESC";
	private static final String SELECT_LATEST_STATEMENT_02 = "SELECT * FROM TB_COMPANY_STOCK WHERE STOCK_ID = ? AND STANDARD_DATE <= ? ORDER BY STANDARD_DATE DESC, STANDARD_TIME DESC";
	private static final String SELECT_LATEST_STATEMENT_03 = "SELECT * FROM TB_COMPANY_STOCK WHERE STOCK_ID = ? AND STANDARD_DATE <= ? AND STANDARD_TIME <= ? ORDER BY STANDARD_DATE DESC, STANDARD_TIME DESC";
	
	public Stock getLatestStockValue(Company company, String standardDate, String standardTime) throws SQLException {
		Stock rtn = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			if ( standardDate != null && standardTime != null ) {
				ps = conn.prepareStatement(SELECT_LATEST_STATEMENT_03);
				ps.setString(1, company.getId() );
				ps.setString(2, standardDate);
				ps.setString(3, standardTime);
			} else if ( standardDate != null ) {
				ps = conn.prepareStatement(SELECT_LATEST_STATEMENT_02);
				ps.setString(1, company.getId() );
				ps.setString(2, standardDate);
			} else {
				ps = conn.prepareStatement(SELECT_LATEST_STATEMENT_01);
				ps.setString(1, company.getId() );
			}
			rs = ps.executeQuery();
			
			if( rs.next() ) {
				rtn = new Stock();
				rtn.setCompany(company);
				rtn.setValue(rs.getInt("STOCK_PRICE"));
				rtn.setVolume(rs.getInt("STOCK_VOLUME"));
				rtn.setStandardDate(rs.getString("STANDARD_DATE"));
				rtn.setStandardTime(rs.getString("STANDARD_TIME"));
				rtn.setTodayHigh(rs.getInt("TODAY_HIGH"));
				rtn.setTodayLow(rs.getInt("TODAY_LOW"));
				rtn.setOrdinaryShares(rs.getLong("ORDINARY_SHARES"));
				rtn.setMarketCapitalization(rs.getLong("MARKET_CAPITALIZATION"));
				rtn.setParValue(rs.getFloat("PAR_VALUE"));
				rtn.setOpenPrice(rs.getInt("OPEN_PRICE"));
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
	
	public boolean delete(Stock stock) {
		boolean rtn = false;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("DELETE FROM TB_COMPANY_STOCK WHERE STOCK_ID = ? AND STANDARD_DATE = ? AND STANDARD_TIME = ?");
			ps.setString(1, stock.getCompany().getId() );
			ps.setString(2, stock.getStandardDate() );
			ps.setString(3, stock.getStandardTime() );
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
