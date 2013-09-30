package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import post.Company;
import post.Stock;

public class StockDao extends BaseDao {
	
	public boolean insert(Stock companyStock) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		boolean rtn = false;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("INSERT INTO TB_COMPANY_STOCK ( STOCK_ID , STANDARD_DATE , STANDARD_TIME , STOCK_PRICE , STOCK_VOLUME ) VALUES ( ? , ? , ? , ? , ? )");
			ps.setString(1, companyStock.getCompany().getId() );
			ps.setString(2, companyStock.getStandardDate());
			ps.setString(3, companyStock.getStandardTime());
			ps.setLong(4, companyStock.getValue());
			ps.setLong(5, companyStock.getVolume());
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
