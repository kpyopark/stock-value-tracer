package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import post.Company;

/**
<pre>
CREATE TABLE `tb_company` (
  `STOCK_ID` varchar(10) COLLATE euckr_bin NOT NULL,
  `COMPANY_NAME` varchar(100) COLLATE euckr_bin NOT NULL,
  `STANDARD_DATE` varchar(8) COLLATE euckr_bin NOT NULL DEFAULT '',
  `MODIFIED_DATE` date DEFAULT NULL,
  `FICS_SECTOR` varchar(45) COLLATE euckr_bin DEFAULT NULL,
  `FICS_INDUSTRY_GROUP` varchar(45) COLLATE euckr_bin DEFAULT NULL,
  `FICS_INDUSTRY` varchar(45) COLLATE euckr_bin DEFAULT NULL,
  `CLOSED_YN` varchar(45) COLLATE euckr_bin DEFAULT NULL,
  `STANDARD_ID` varchar(12) COLLATE euckr_bin DEFAULT NULL,
  PRIMARY KEY (`STOCK_ID`,`STANDARD_DATE`)
) ENGINE=MyISAM DEFAULT CHARSET=euckr COLLATE=euckr_bin;

</pre>
 * @author user
 *
 */

@Deprecated
public class CompanyDao extends BaseDao {
	
	public boolean insert(Company company) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		boolean rtn = false;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("INSERT INTO TB_COMPANY ( STOCK_ID , COMPANY_NAME , STANDARD_DATE, MODIFIED_DATE, FICS_SECTOR, FICS_INDUSTRY_GROUP, FICS_INDUSTRY, CLOSED_YN ) VALUES ( ? , ? , ?, CURRENT_DATE, ?, ?, ?, ? )");
			ps.setString(1, company.getId() );
			ps.setString(2, company.getName() );
			ps.setString(3, company.getStandardDate());
			ps.setString(4, company.getFicsSector());
			ps.setString(5, company.getFicsIndustryGroup());
			ps.setString(6, company.getFicsIndustry());
			ps.setString(7, company.isClosed() ? "Y" : "N" );
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
	
	public boolean update(Company company) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		boolean rtn = false;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("UPDATE TB_COMPANY SET COMPANY_NAME = ?, MODIFIED_DATE = CURRENT_DATE, FICS_SECTOR = ? , FICS_INDUSTRY_GROUP = ?, FICS_INDUSTRY = ?, CLOSED_YN = ? WHERE STOCK_ID = ? AND STANDARD_DATE = ?");
			ps.setString(1, company.getName() );
			ps.setString(2, company.getFicsSector());
			ps.setString(3, company.getFicsIndustryGroup());
			ps.setString(4, company.getFicsIndustry());
			ps.setString(5, company.isClosed() ? "Y": "N");
			ps.setString(6, company.getId() );
			ps.setString(7, company.getStandardDate());
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
	
	/**
	 * Stock id를 이용하여 회사의 존재 여부를 확인하며
	 * 있을 경우 회사 정보를 가지고 온다.
	 * 없을 경우 null값을 돌려준다.
	 * 
	 * 중요!! 실은 이 메쏘드는 변경이 필요하다.
	 * PK 가 id와 standard_date 두개임에도 불구하고 id로만 검색하기 때문이다.
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	//TODO: 검색시, id,standard_date 두개로 검색할 수 있도록 변경이 필요함.
	public Company select(String id,String standardDate) throws SQLException {
		Company rtn = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			if ( standardDate == null ) {
				ps = conn.prepareStatement("SELECT * FROM TB_COMPANY WHERE STOCK_ID = ? ORDER BY STANDARD_DATE DESC");
				ps.setString(1, id );
			} else {
				ps = conn.prepareStatement("SELECT * FROM TB_COMPANY WHERE STOCK_ID = ? AND STANDARD_DATE = ?");
				ps.setString(1, id );
				ps.setString(2, standardDate );
			}
			rs = ps.executeQuery();
			
			if( rs.next() ) {
				rtn = new Company();
				rtn.setId(id);
				rtn.setName(rs.getString("COMPANY_NAME"));
				rtn.setStandardDate(rs.getString("STANDARD_DATE"));
				rtn.setFicsSector(rs.getString("FICS_SECTOR"));
				rtn.setFicsIndustryGroup(rs.getString("FICS_INDUSTRY_GROUP"));
				rtn.setFicsIndustry(rs.getString("FICS_INDUSTRY"));
				rtn.setClosed("Y".equals(rs.getString("CLOSED_YN")));
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
	
	public java.util.ArrayList<Company> selectAllList() throws SQLException {
		java.util.ArrayList<Company> list = new java.util.ArrayList<Company>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("SELECT A.STOCK_ID, A.COMPANY_NAME, A.STANDARD_DATE, A.FICS_SECTOR, A.FICS_INDUSTRY_GROUP, A.FICS_INDUSTRY, A.CLOSED_YN FROM TB_COMPANY A JOIN ( SELECT STOCK_ID, MAX(STANDARD_DATE) AS STANDARD_DATE FROM TB_COMPANY GROUP BY STOCK_ID ) B ON ( A.STOCK_ID = B.STOCK_ID AND A.STANDARD_DATE = B.STANDARD_DATE )");
			rs = ps.executeQuery();
			
			while ( rs.next() ) {
				Company company = new Company();
				company.setId(rs.getString("STOCK_ID"));
				company.setName(rs.getString("COMPANY_NAME"));
				company.setStandardDate(rs.getString("STANDARD_DATE"));
				company.setFicsSector(rs.getString("FICS_SECTOR"));
				company.setFicsIndustryGroup(rs.getString("FICS_INDUSTRY_GROUP"));
				company.setFicsIndustry(rs.getString("FICS_INDUSTRY"));
				company.setClosed("Y".equals(rs.getString("CLOSED_YN")));
				list.add(company);
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
	
	public java.util.ArrayList<Company> selectAllList(String standardDate) throws SQLException {
		java.util.ArrayList<Company> list = new java.util.ArrayList<Company>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("SELECT * FROM tb_company A JOIN ( " +
					"SELECT STOCK_ID, MAX(STANDARD_DATE) STANDARD_DATE FROM tb_company  " +
					"WHERE  STANDARD_DATE <= ?  " +
					"GROUP BY STOCK_ID ) B USING ( STOCK_ID, STANDARD_DATE) " +
					"WHERE CLOSED_YN <> 'Y' ");
			ps.setString(1, standardDate);
			rs = ps.executeQuery();
			
			while ( rs.next() ) {
				Company company = new Company();
				company.setId(rs.getString("STOCK_ID"));
				company.setName(rs.getString("COMPANY_NAME"));
				company.setStandardDate(rs.getString("STANDARD_DATE"));
				company.setFicsSector(rs.getString("FICS_SECTOR"));
				company.setFicsIndustryGroup(rs.getString("FICS_INDUSTRY_GROUP"));
				company.setFicsIndustry(rs.getString("FICS_INDUSTRY"));
				company.setClosed("Y".equals(rs.getString("CLOSED_YN")));
				list.add(company);
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
