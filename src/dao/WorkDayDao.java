package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
<pre>
CREATE TABLE `tb_workday` (
  `STANDARD_DATE` varchar(8) NOT NULL DEFAULT '',
  PRIMARY KEY (`STANDARD_DATE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

</pre>
 * 
 * @author user
 *
 */
public class WorkDayDao extends BaseDao {
	public boolean insert(String standardDate) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		boolean rtn = false;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("INSERT INTO TB_WORKDAY ( STANDARD_DATE ) VALUES ( ? )");
			ps.setString(1, standardDate );
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
	
	public java.util.ArrayList<String> selectAllList() throws SQLException {
		java.util.ArrayList<String> list = new java.util.ArrayList<String>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("SELECT STANDARD_DATE FROM TB_WORKDAY");
			rs = ps.executeQuery();
			
			while ( rs.next() ) {
				list.add(rs.getString("STANDARD_DATE"));
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
