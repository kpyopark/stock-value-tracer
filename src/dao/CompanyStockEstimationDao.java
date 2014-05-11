package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import post.Company;
import post.StockEstimated;

public class CompanyStockEstimationDao extends BaseDao {
	
	final static long PRECISION_THRESHOLD = 1000000;
	
	public boolean insert(StockEstimated estimatedCfs) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		boolean rtn = false;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("INSERT INTO TB_DECADE_ESTIM ( STOCK_ID,STANDARD_DATE,ESTIM_KIND,AVE_PER,AVE_ROE,AVE_ROA,AVE_DIVIDEND_RATIO,RECENT_EPS,RECENT_STOCK_VALUE,LAST_EPS,EXPECTATION_RATIO,RELATED_DATE_LIST ) VALUES ( ?,?,?,?,?,?,?,?,?,?,?,? )");
			int cnt = 1;
			ps.setString(cnt++, estimatedCfs.getCompany().getId() );
			ps.setString(cnt++, estimatedCfs.getStandardDate() );
			ps.setString(cnt++, estimatedCfs.getEstimKind() );
			ps.setFloat(cnt++, estimatedCfs.getAvePer() > Integer.MAX_VALUE ? Integer.MAX_VALUE : estimatedCfs.getAvePer() );
			ps.setFloat(cnt++, estimatedCfs.getAveRoe() );
			ps.setFloat(cnt++, estimatedCfs.getAveRoa() );
			ps.setFloat(cnt++, estimatedCfs.getAveDividendRatio() );
			ps.setFloat(cnt++, estimatedCfs.getRecentEps() );
			ps.setLong(cnt++, estimatedCfs.getRecentStockValue() );
			ps.setFloat(cnt++, estimatedCfs.getLastEps() );
			ps.setFloat(cnt++, estimatedCfs.getLastEps() );
			ps.setString(cnt++, estimatedCfs.getRelatedDateList() );
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
	 * 검색하고자 하는 정확한 회사정보와 기준일자를 알 경우 이용.
	 * 
	 * @param company
	 * @param standardDate
	 * @return
	 * @throws SQLException
	 */
	public StockEstimated select(Company company, String standardDate) throws SQLException {
		StockEstimated rtn = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("SELECT * FROM TB_DECADE_ESTIM WHERE STOCK_ID = ? AND STANDARD_DATE = ?");
			ps.setString(1, company.getId() );
			ps.setString(2, standardDate );
			rs = ps.executeQuery();
			
			if( rs.next() ) {
				rtn = getCseFromResultSet(rs);
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
	
	public StockEstimated select(Company company) throws SQLException {
		StockEstimated rtn = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("SELECT * FROM TB_DECADE_ESTIM WHERE STOCK_ID = ? ORDER BY STANDARD_DATE DESC");
			ps.setString(1, company.getId() );
			rs = ps.executeQuery();
			
			if( rs.next() ) {
				rtn = getCseFromResultSet(rs);
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
	
	public boolean delete(StockEstimated cse) {
		boolean rtn = false;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("DELETE FROM TB_DECADE_ESTIM WHERE STOCK_ID = ? AND STANDARD_DATE = ? AND ESTIM_KIND = ?");
			ps.setString(1, cse.getCompany().getId() );
			ps.setString(2, cse.getStandardDate() );
			ps.setString(3, cse.getEstimKind() );
			rtn = ps.executeUpdate() > 0;
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( ps != null ) try { ps.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
			if ( conn != null ) try { conn.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
		}
		return rtn;
	}
	
	private static StockEstimated getCseFromResultSet(ResultSet rs) throws SQLException {
		StockEstimated rtn = new StockEstimated();
		rtn.setStandardDate(rs.getString("STANDARD_DATE"));
		rtn.setEstimKind(rs.getString("ESTIM_KIND"));
		rtn.setAvePer(rs.getFloat("AVE_PER"));
		rtn.setAveRoe(rs.getFloat("AVE_ROE"));
		rtn.setAveRoa(rs.getFloat("AVE_ROA"));
		rtn.setAveDividendRatio(rs.getFloat("AVE_DIVIDEND_RATIO"));
		rtn.setRecentEps(rs.getFloat("RECENT_EPS"));
		rtn.setRecentStockValue(rs.getLong("RECENT_STOCK_VALUE"));
		rtn.setLastEps(rs.getFloat("LAST_EPS"));
		rtn.setExpectationRation(rs.getFloat("EXPECTATION_RATIO"));
		rtn.setRelatedDateList(rs.getString("RELATED_DATE_LIST"));
		return rtn;
	}
	
}
