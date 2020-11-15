package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import post.Company;
import post.InstitutionalDamand;

/**
<pre>
CREATE TABLE `tb_institutional_demand` (
  `stock_id` varchar(10) NOT NULL,
  `standard_date` varchar(8) NOT NULL DEFAULT '',
  `standard_time` varchar(6) NOT NULL DEFAULT '',
  `stock_closing_price` bigint(20) DEFAULT NULL,
  `stock_updown_ratio_of_day` float(20,5) DEFAULT NULL,
  `stock_updown_price_of_day` bigint(20) DEFAULT NULL,
  `foreigner_net_demand` bigint(20) DEFAULT NULL,
  `foreigner_ownership_ratio` float DEFAULT NULL,
  `company_net_demand` bigint(20) DEFAULT NULL,
  `individual_net_demand` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`stock_id`,`standard_date`,`standard_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

</pre>
 * @author user
 *
 */
public class InstitutionalDemandDao extends BaseDao {
	
	public InstitutionalDemandDao() {
		super();
	}
	
	static String INSERT_STATEMENT = "INSERT INTO tb_institutional_demand " +
			"(stock_id, " +
			"standard_date, " +
			"standard_time, " +
			"stock_closing_price, " +
			"stock_updown_ratio_of_day, " +
			"stocK_updown_price_of_day, " +
			"foreigner_net_demand, " +
			"foreigner_ownership_ratio, " +
			"company_net_demand, " +
			"individual_net_demand) " +
			"VALUES " +
			"(?,"+
			"?,"+
			"?,"+
			"?,"+
			"?,"+
			"?,"+
			"?,"+
			"?,"+
			"?,"+
			"?);";

	static String REPLACE_STATEMENT = "REPLACE INTO tb_institutional_demand " +
			"(stock_id, " +
			"standard_date, " +
			"standard_time, " +
			"stock_closing_price, " +
			"stock_updown_ratio_of_day, " +
			"stocK_updown_price_of_day, " +
			"foreigner_net_demand, " +
			"foreigner_ownership_ratio, " +
			"company_net_demand, " +
			"individual_net_demand) " +
			"VALUES " +
			"(?,"+
			"?,"+
			"?,"+
			"?,"+
			"?,"+
			"?,"+
			"?,"+
			"?,"+
			"?,"+
			"?);";
	
	public boolean insert(InstitutionalDamand insDemand) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		boolean rtn = false;
		try {
			conn = getConnection();
			ps = conn.prepareStatement(INSERT_STATEMENT);
			int cnt = 1;
			ps.setString(cnt++, insDemand.getCompany().getId() );
			ps.setString(cnt++, insDemand.getStandardDate() );
			ps.setString(cnt++, insDemand.getStandardTime());
			ps.setLong(cnt++, insDemand.getStockClosingPrice());
			ps.setFloat(cnt++, insDemand.getStockUpdownRatioOfDay());
			ps.setLong(cnt++, insDemand.getStockUpdownPriceOfDay());
			ps.setLong(cnt++, insDemand.getForeignerNetDemand());
			ps.setFloat(cnt++,  insDemand.getForeignerOwnershipRatio());
			ps.setLong(cnt++, insDemand.getCompanyNetDemand());
			ps.setLong(cnt++,  insDemand.getIndividualNetDemand());
			rtn = ps.execute();
		} catch ( Exception e ) {
			System.out.println("=============STANDARD DATE:" + insDemand.getStandardDate() );
			e.printStackTrace();
		} finally {
			if ( ps != null ) try { ps.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
			if ( conn != null ) try { conn.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
		}
		return rtn;
	}
	
	public boolean replace(InstitutionalDamand insDemand) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		boolean rtn = false;
		try {
			conn = getConnection();
			ps = conn.prepareStatement(REPLACE_STATEMENT);
			int cnt = 1;
			ps.setString(cnt++, insDemand.getCompany().getId() );
			ps.setString(cnt++, insDemand.getStandardDate() );
			ps.setString(cnt++, insDemand.getStandardTime());
			ps.setLong(cnt++, insDemand.getStockClosingPrice());
			ps.setFloat(cnt++, insDemand.getStockUpdownRatioOfDay());
			ps.setLong(cnt++, insDemand.getStockUpdownPriceOfDay());
			ps.setLong(cnt++, insDemand.getForeignerNetDemand());
			ps.setFloat(cnt++,  insDemand.getForeignerOwnershipRatio());
			ps.setLong(cnt++, insDemand.getCompanyNetDemand());
			ps.setLong(cnt++,  insDemand.getIndividualNetDemand());
			rtn = ps.execute();
		} catch ( Exception e ) {
			System.out.println("=============STANDARD DATE:" + insDemand.getStandardDate() );
			e.printStackTrace();
		} finally {
			if ( ps != null ) try { ps.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
			if ( conn != null ) try { conn.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
		}
		return rtn;
	}
	
	private static InstitutionalDamand getInstitutionalDemandFromResultSet(Company company, ResultSet rs) throws SQLException {
		InstitutionalDamand insDemand = new InstitutionalDamand();
		insDemand.setCompany(company);
		insDemand.setStandardDate(rs.getString("STANDARD_DATE"));
		insDemand.setStandardTime(rs.getString("STANDARD_TIME"));
		insDemand.setStockClosingPrice(rs.getLong("stock_closing_price"));
		insDemand.setStockUpdownRatioOfDay(rs.getFloat("stock_updown_ratio_of_day"));
		insDemand.setStockUpdownPriceOfDay(rs.getLong("stocK_updown_price_of_day"));
		insDemand.setForeignerNetDemand(rs.getLong("foreigner_net_demand"));
		insDemand.setForeignerOwnershipRatio(rs.getFloat("foreigner_ownership_ratio"));
		insDemand.setCompanyNetDemand(rs.getLong("company_net_demand"));
		insDemand.setIndividualNetDemand(rs.getLong("individual_net_demand"));
		return insDemand;
	}
	
	private static String SELECT_STATEMENT_01 = "SELECT * FROM tb_institutional_demand WHERE STOCK_ID = ? AND STANDARD_DATE = ? AND STANDARD_TIME = ?";
	
	public InstitutionalDamand select(Company company, String standardDate, String standardTime) throws SQLException {
		InstitutionalDamand rtn = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement(SELECT_STATEMENT_01);
			ps.setString(1, company.getId() );
			ps.setString(2, standardDate );
			ps.setString(3, standardTime);
			rs = ps.executeQuery();
			
			if( rs.next() ) {
				rtn = getInstitutionalDemandFromResultSet(company, rs);
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
	
	public boolean delete(InstitutionalDamand insDemand) {
		boolean rtn = false;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("DELETE FROM tb_institutional_demand WHERE STOCK_ID = ? AND STANDARD_DATE = ? AND STANDARD_TIME = ?");
			ps.setString(1, insDemand.getCompany().getId() );
			ps.setString(2, insDemand.getStandardDate() );
			ps.setString(3, insDemand.getStandardTime() );
			rtn = ps.executeUpdate() > 0;
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( ps != null ) try { ps.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
			if ( conn != null ) try { conn.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
		}
		return rtn;
	}
	
	/**
	 * 해당 종목에 맞는 재무정보를 가지고 온다.
	 * @param company
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<InstitutionalDamand> getInstitutionalDemandList(Company company) throws SQLException {
		ArrayList<InstitutionalDamand> list = new ArrayList<InstitutionalDamand>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("SELECT * FROM tb_institutional_demand WHERE STOCK_ID = ?");
			ps.setString(1, company.getId() );
			rs = ps.executeQuery();
			
			while ( rs.next() ) {
				InstitutionalDamand insDemand = getInstitutionalDemandFromResultSet(company, rs);
				list.add(insDemand);
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
