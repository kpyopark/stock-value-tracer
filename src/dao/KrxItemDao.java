package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import post.KrxItem;
import post.KrxSecurityType;

/**
<pre>
CREATE TABLE `tb_company_stock_daily` (
  `STOCK_ID` varchar(10) COLLATE utf8_bin NOT NULL DEFAULT '',
  `STANDARD_DATE` varchar(8) COLLATE utf8_bin NOT NULL DEFAULT '',
  `STOCK_PRICE` bigint(20) DEFAULT NULL,
  `NET_CHANGE` bigint(20) DEFAULT NULL,
  `NET_CHANGE_RATIO` float DEFAULT NULL,
  `TRADING_VOLUME` bigint(20) unsigned DEFAULT NULL,
  `ASK_PRICE` bigint(20) DEFAULT NULL,
  `BID_PRICE` bigint(20) DEFAULT NULL,
  `TODAY_HIGH` bigint(20) DEFAULT NULL,
  `TODAY_LOW` bigint(20) DEFAULT NULL,
  `VOLUME` bigint(20) DEFAULT NULL,
  `VOLUME_AMOUNT` bigint(20) DEFAULT NULL,
  `OPEN_PRICE` bigint(20) DEFAULT NULL,
  `PAR_VALUE` bigint(20) DEFAULT NULL,
  `CURRENCY` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `ORDINARY_SHARE` bigint(20) DEFAULT NULL,
  `MARKET_CAPITAL` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`STOCK_ID`,`STANDARD_DATE`),
  KEY `ix_company_stock_daily_stock_id_standard_date_ordinary_share` (`STOCK_ID`,`STANDARD_DATE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

</pre>
 * @author user
 *
 */
public class KrxItemDao extends BaseDao {
	
	static String INSERT_STRING = "INSERT INTO tb_company_stock_daily ( " +
			"STOCK_ID,  " +
			"COMPANY_NAME, " +
			"STANDARD_DATE,  " +
			"SECURITY_TYPE,  " +
			"STOCK_PRICE,  " +
			"NET_CHANGE, " +
			"NET_CHANGE_RATIO, " +
			"ASK_PRICE,  " +
			"BID_PRICE,  " +
			"TODAY_HIGH, " +
			"TODAY_LOW,  " +
			"VOLUME, " +
			"VOLUME_AMOUNT,  " +
			"OPEN_PRICE, " +
			"PAR_VALUE,  " +
			"CURRENCY, " +
			"ORDINARY_SHARE, " +
			"MARKET_CAPITAL " +
			") VALUES ( " +
			"?, " +
			"?, " +
			"?, " +
			"?, " +
			"?, " +
			"?, " +
			"?, " +
			"?, " +
			"?, " +
			"?, " +
			"?, " +
			"?, " +
			"?, " +
			"?, " +
			"?, " +
			"?, " +
			"?, " +
			"?  " +
			") " +
			"ON CONFLICT (STOCK_ID, STANDARD_DATE) " +
			"DO UPDATE SET " +
			"COMPANY_NAME = EXCLUDED.COMPANY_NAME , " +
			"SECURITY_TYPE= EXCLUDED.SECURITY_TYPE, " +
			"STOCK_PRICE= EXCLUDED.STOCK_PRICE, " +
			"NET_CHANGE = EXCLUDED.NET_CHANGE , " +
			"NET_CHANGE_RATIO = EXCLUDED.NET_CHANGE_RATIO , " +
			"ASK_PRICE= EXCLUDED.ASK_PRICE, " +
			"BID_PRICE= EXCLUDED.BID_PRICE, " +
			"TODAY_HIGH = EXCLUDED.TODAY_HIGH , " +
			"TODAY_LOW= EXCLUDED.TODAY_LOW, " +
			"VOLUME = EXCLUDED.VOLUME , " +
			"VOLUME_AMOUNT= EXCLUDED.VOLUME_AMOUNT, " +
			"OPEN_PRICE = EXCLUDED.OPEN_PRICE , " +
			"PAR_VALUE= EXCLUDED.PAR_VALUE, " +
			"CURRENCY = EXCLUDED.CURRENCY , " +
			"ORDINARY_SHARE = EXCLUDED.ORDINARY_SHARE , " +
			"MARKET_CAPITAL = EXCLUDED.MARKET_CAPITAL ";
	
	private boolean insertOneRow(PreparedStatement ps, KrxItem krxItem) throws SQLException {
		int inxCnt = 1;
		ps.setString(inxCnt++, krxItem.getId() );
		ps.setString(inxCnt++, krxItem.getName() );
		ps.setString(inxCnt++, krxItem.getStandardDate() );
		ps.setString(inxCnt++, krxItem.getSecurityType().getType()+"" );
		ps.setLong(inxCnt++, krxItem.getStockPrice());
		ps.setLong(inxCnt++, krxItem.getNetChange());
		ps.setFloat(inxCnt++, krxItem.getNetChangeRatio());
		ps.setLong(inxCnt++, krxItem.getAsk());
		ps.setLong(inxCnt++, krxItem.getBid());
		ps.setLong(inxCnt++, krxItem.getTodayHigh());
		ps.setLong(inxCnt++, krxItem.getTodayLow());
		ps.setLong(inxCnt++, krxItem.getVolume());
		ps.setLong(inxCnt++, krxItem.getVolumnAmount());
		ps.setLong(inxCnt++, krxItem.getOpenPrice());
		ps.setFloat(inxCnt++, krxItem.getParValue());
		ps.setString(inxCnt++, krxItem.getCurrency());
		ps.setLong(inxCnt++, krxItem.getOrdinaryShare());
		ps.setLong(inxCnt++, krxItem.getMaketCapitalization());
		
		return ps.execute();
	}
	
	public boolean insert(KrxItem krxItem) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		boolean rtn = false;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(INSERT_STRING);
			insertOneRow(ps, krxItem);
			conn.commit();
		} catch ( Exception e ) {
			e.printStackTrace();
			conn.rollback();
		} finally {
			conn.setAutoCommit(true);
			if ( ps != null ) try { ps.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
			if ( conn != null ) try { conn.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
		}
		return rtn;
		
	}
	
	public boolean insert(List<KrxItem> krxItems) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		boolean rtn = false;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(INSERT_STRING);
			for ( KrxItem krxItem : krxItems) {
				insertOneRow(ps, krxItem);
			}
			conn.commit();
		} catch ( Exception e ) {
			e.printStackTrace();
			conn.rollback();
		} finally {
			conn.setAutoCommit(true);
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
	public KrxItem select(KrxItem krxItem, String standardDate) throws SQLException {
		KrxItem rtn = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			if ( standardDate != null ) {
				ps = conn.prepareStatement(SELECT_STATEMENT_02);
				ps.setString(1, krxItem.getId() );
				ps.setString(2, standardDate);
			} else {
				ps = conn.prepareStatement(SELECT_STATEMENT_01);
				ps.setString(1, krxItem.getId() );
			}
			rs = ps.executeQuery();
			
			if( rs.next() ) {
				rtn = getKrxItemFromResultSet(rs);
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
	
	private static final String SELECT_STATEMENT_01 = "SELECT * FROM TB_COMPANY_STOCK_DAILY WHERE STOCK_ID = ? ORDER BY STANDARD_DATE DESC";
	private static final String SELECT_STATEMENT_02 = "SELECT * FROM TB_COMPANY_STOCK_DAILY WHERE STOCK_ID = ? AND STANDARD_DATE = ?";
	private static final String SELECT_NEARLEST_STATEMENT_01 = "SELECT * " +
			"FROM TB_COMPANY_STOCK_DAILY " +
			"WHERE  STOCK_ID = ? " +
			"       AND STANDARD_DATE = ( SELECT MAX(STANDARD_DATE) FROM TB_COMPANY_STOCK_DAILY WHERE STOCK_ID = ? AND STANDARD_DATE < ? )";
	
	
	public static KrxItem getKrxItemFromResultSet(ResultSet rs) throws SQLException {
		KrxItem rtn = new KrxItem();
		rtn.setId(rs.getString("STOCK_ID"));
		rtn.setName(rs.getString("COMPANY_NAME"));
		rtn.setStandardDate(rs.getString("STANDARD_DATE")) ;
		rtn.setSecurityType(KrxSecurityType.getKrxSecurityTypeFromInt(rs.getInt("SECURITY_TYPE"))) ;
		rtn.setStockPrice(rs.getLong("STOCK_PRICE"));
		rtn.setNetChange(rs.getLong("NET_CHANGE"));
		rtn.setNetChangeRatio(rs.getFloat("NET_CHANGE_RATIO"));
		rtn.setAsk(rs.getLong("ASK_PRICE"));
		rtn.setBid(rs.getLong("BID_PRICE"));
		rtn.setTodayHigh(rs.getLong("TODAY_HIGH"));
		rtn.setTodayLow(rs.getLong("TODAY_LOW"));
		rtn.setVolume(rs.getLong("VOLUME"));
		rtn.setVolumnAmount(rs.getLong("VOLUME_AMOUNT"));
		rtn.setOpenPrice(rs.getLong("OPEN_PRICE"));
		rtn.setParValue(rs.getFloat("PAR_VALUE"));
		rtn.setCurrency(rs.getString("CURRENCY"));
		rtn.setOrdinaryShare(rs.getLong("ORDINARY_SHARE"));
		rtn.setMaketCapitalization(rs.getLong("MARKET_CAPITAL"));
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
	public KrxItem selectNearlestValue(KrxItem krxItem, String standardDate) throws SQLException {
		KrxItem rtn = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement(SELECT_NEARLEST_STATEMENT_01);
			ps.setString(1, krxItem.getId() );
			ps.setString(2, krxItem.getId() );
			ps.setString(3, standardDate);
				
			rs = ps.executeQuery();
			
			if( rs.next() ) {
				rtn = getKrxItemFromResultSet(rs);
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

	public boolean delete(KrxItem krxItem) {
		boolean rtn = false;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("DELETE FROM TB_COMPANY_STOCK_DAILY WHERE STOCK_ID = ? AND STANDARD_DATE = ?");
			ps.setString(1, krxItem.getId() );
			ps.setString(2, krxItem.getStandardDate() );
			rtn = ps.executeUpdate() > 0;
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( ps != null ) try { ps.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
			if ( conn != null ) try { conn.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
		}
		return rtn;
	}
	
	public String getLatestStandardDate() {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String rtn = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("SELECT MAX(STANDARD_DATE) FROM TB_COMPANY_STOCK_DAILY WHERE STOCK_ID = 'A005930'");
			rs = ps.executeQuery();
			if(rs.next())
				rtn = rs.getString(1);
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( rs != null ) try { rs.close(); } catch (Exception e1) { e1.printStackTrace(); }
			if ( ps != null ) try { ps.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
			if ( conn != null ) try { conn.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
		}
		return rtn;
	}
	
}
