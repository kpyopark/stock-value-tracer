package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import post.FutureAndOption;

public class FutureAndOptionDao extends BaseDao {

	private static String INSERT_FUTURE_AND_OPTION = "INSERT INTO TB_FUTURE_AND_OPTION " + 
			"( STOCK_ID , STOCK_NAME , FUTURE_OPTION_TYPE, TARGET_YM, " + 
			"END_TARGET_YM, CLOSED_YN, BASE_STOCK_ID, START_FUTURE_ID, " + 
			"END_FUTURE_ID, MODIFIED_DATE, TARGET_YMD, ACTION_PRICE ) " + 
			"VALUES ( ?, ?, ?, ?, ?,  ?, ?, ?, ? , CURRENT_DATE,  ?,? ) " +
			"ON CONFLICT (STOCK_ID) DO NOTHING";
	
	public boolean insert(FutureAndOption futureAndOption) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		boolean rtn = false;
		try {
			conn = getConnection();
			ps = conn.prepareStatement(INSERT_FUTURE_AND_OPTION);
			ps.setString(1, futureAndOption.getStockId());
			ps.setString(2, futureAndOption.getStockName());
			ps.setString(3, futureAndOption.getFutureOptionType());
			ps.setString(4, futureAndOption.getTargetYm());
			ps.setString(5, futureAndOption.getEndTargetYm());
			ps.setString(6, futureAndOption.getClosedYn() ? "Y" : "N");
			ps.setString(7, futureAndOption.getBaseStockId());
			ps.setString(8, futureAndOption.getStartFutureId());
			ps.setString(9, futureAndOption.getEndFutureId());
			ps.setString(10, futureAndOption.getTargetYmd());
			ps.setFloat(11, futureAndOption.getActionPrice());
			rtn = ps.execute();
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( ps != null ) try { ps.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
			if ( conn != null ) try { conn.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
		}
		System.out.println( rtn );
		return rtn;
	}
	
	public List<FutureAndOption> getActiveFutures() {
		List<FutureAndOption> rtn = new ArrayList<FutureAndOption>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement(
					"SELECT stock_id, stock_name, future_option_type, target_ym, end_target_ym, closed_yn, "
					+ "base_stock_id, start_future_id, end_future_id, modified_date, target_ymd, action_price "
					+ "FROM TB_FUTURE_AND_OPTION WHERE CLOSED_YN <> 'Y' AND FUTURE_OPTION_TYPE IN ('F', 'SP')");
			rs = ps.executeQuery();
			FutureAndOption oneOption = null;
			while(rs.next()) {
				oneOption = new FutureAndOption();
				oneOption.setStockId(rs.getString(1));
				oneOption.setStockName(rs.getString(2));
				oneOption.setFutureOptionType(rs.getString(3));
				oneOption.setTargetYm(rs.getString(4));
				oneOption.setEndTargetYm(rs.getString(5));
				oneOption.setClosedYn("Y".equals(rs.getString(6)));
				oneOption.setBaseStockId(rs.getString(7));
				oneOption.setStartFutureId(rs.getString(8));
				oneOption.setEndFutureId(rs.getString(9));
				oneOption.setModifedDate(rs.getDate(10));
				oneOption.setTargetYmd(rs.getString(11));
				oneOption.setActionPrice(rs.getFloat(12));
				rtn.add(oneOption);
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( ps != null ) try { ps.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
			if ( conn != null ) try { conn.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
		}
		System.out.println( rtn );
		return rtn;
	}
	
	public List<FutureAndOption> getActiveOptions() {
		List<FutureAndOption> rtn = new ArrayList<FutureAndOption>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("SELECT stock_id, stock_name, future_option_type, target_ym, end_target_ym, closed_yn, "
					+ "base_stock_id, start_future_id, end_future_id, modified_date, target_ymd, action_price FROM TB_FUTURE_AND_OPTION WHERE CLOSED_YN <> 'Y' and FUTURE_OPTION_TYPE IN ('C', 'P')");
			rs = ps.executeQuery();
			FutureAndOption oneOption = null;
			while(rs.next()) {
				oneOption = new FutureAndOption();
				oneOption.setStockId(rs.getString(1));
				oneOption.setStockName(rs.getString(2));
				oneOption.setFutureOptionType(rs.getString(3));
				oneOption.setTargetYm(rs.getString(4));
				oneOption.setEndTargetYm(rs.getString(5));
				oneOption.setClosedYn("Y".equals(rs.getString(6)));
				oneOption.setBaseStockId(rs.getString(7));
				oneOption.setStartFutureId(rs.getString(8));
				oneOption.setEndFutureId(rs.getString(9));
				oneOption.setModifedDate(rs.getDate(10));
				oneOption.setTargetYmd(rs.getString(11));
				oneOption.setActionPrice(rs.getFloat(12));
				rtn.add(oneOption);
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( ps != null ) try { ps.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
			if ( conn != null ) try { conn.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
		}
		System.out.println( rtn );
		return rtn;
	}
	
	public boolean update(FutureAndOption futureAndOption) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		boolean rtn = false;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("UPDATE TB_FUTURE_AND_OPTION SET MODIFIED_DATE = CURRENT_DATE, CLOSED_YN = ?, TARGET_YMD = ? WHERE STOCK_ID = ?");
			ps.setString(1, futureAndOption.getClosedYn() ? "Y" : "N" );
			ps.setString(2, futureAndOption.getTargetYmd());
			ps.setString(3, futureAndOption.getStockId());
			rtn = ps.execute();
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( ps != null ) try { ps.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
			if ( conn != null ) try { conn.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
		}
		System.out.println( rtn );
		return rtn;
	}
	
}
