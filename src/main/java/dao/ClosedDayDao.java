package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 
<pre>
CREATE TABLE public.tb_closeday
(
  standard_date character varying(8) NOT NULL,
  CONSTRAINT tb_closeday_pkey PRIMARY KEY (standard_date)
)
;
</pre>
 * @author user
 *
 */

public class ClosedDayDao extends BaseDao {
	public boolean deleteAnnualDayAndInsertAll(List<String> closedDayList) throws SQLException {
		Connection conn = null;
		PreparedStatement ps1 = null, ps2 = null;
		boolean rtn = false;
		try {
			if (closedDayList.size() < 1 )
				throw new Exception("Closed Day list size should be greater than 0.");
			conn = getConnection();
			ps1 = conn.prepareStatement("DELETE FROM TB_CLOSEDAY WHERE STANDARD_DATE LIKE ?");
			ps1.setString(1, closedDayList.get(0).substring(0, 4) + "%" );
			ps1.executeUpdate();
			for(String standardDate : closedDayList) {
				ps2 = conn.prepareStatement("INSERT INTO TB_CLOSEDAY ( STANDARD_DATE ) VALUES ( ? )");
				ps2.setString(1, standardDate);
				ps2.executeUpdate();
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( ps1 != null ) try { ps1.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
			if ( ps2 != null ) try { ps2.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
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
			ps = conn.prepareStatement("SELECT STANDARD_DATE FROM TB_CLOSEDAY");
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
