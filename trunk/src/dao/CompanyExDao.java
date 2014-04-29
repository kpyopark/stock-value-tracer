package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import post.CompanyEx;

public class CompanyExDao extends BaseDao {
	
	public boolean insert(CompanyEx company) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		boolean rtn = false;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("INSERT INTO tb_company_and_deffered " +
					"(" +
					"STOCK_ID , STANDARD_DATE , COMPANY_NAME, SECURITY_SECTOR, " +
					"FICS_SECTOR, FICS_INDUSTRY_GROUP, FICS_INDUSTRY, CLOSED_YN, MODIFIED_DATE ) " +
					"VALUES ( " +
					"?, ?, ?, ?, " +
					"?, ?, ?, ?, date_format(curdate(), '%Y%m%d') )"
					);
			ps.setString(1, company.getId() );
			ps.setString(2, company.getStandardDate() );
			ps.setString(3, company.getName() );
			ps.setInt(4, company.getSecuritySector() );
			ps.setString(5, company.getFicsSector() );
			ps.setString(6, company.getFicsIndustryGroup() );
			ps.setString(7, company.getFicsIndustry() );
			ps.setString(8, company.isClosed() ? "Y" : "N" );
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
	
	public boolean update(CompanyEx company) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		boolean rtn = false;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("UPDATE tb_company_and_deffered SET COMPANY_NAME = ?, MODIFIED_DATE = date_format(curdate(), '%Y%m%d'), SECURITY_SECTOR = ?, FICS_SECTOR = ? , FICS_INDUSTRY_GROUP = ?, FICS_INDUSTRY = ?, CLOSED_YN = ? WHERE STOCK_ID = ? AND STANDARD_DATE = ?");
			ps.setString(1, company.getName() );
			ps.setInt(2, company.getSecuritySector() );
			ps.setString(3, company.getFicsSector());
			ps.setString(4, company.getFicsIndustryGroup());
			ps.setString(5, company.getFicsIndustry());
			ps.setString(6, company.isClosed() ? "Y": "N");
			ps.setString(7, company.getId() );
			ps.setString(8, company.getStandardDate());
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
	
	public boolean updateSecuritySector(CompanyEx company) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		boolean rtn = false;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("UPDATE tb_company_and_deffered SET MODIFIED_DATE = date_format(curdate(), '%Y%m%d'), SECURITY_SECTOR = ? WHERE STOCK_ID = ?");
			ps.setInt(1, company.getSecuritySector() );
			ps.setString(2, company.getId() );
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
	public CompanyEx select(String id,String standardDate) throws SQLException {
		CompanyEx rtn = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			if ( standardDate == null ) {
				ps = conn.prepareStatement("SELECT * FROM tb_company_and_deffered WHERE STOCK_ID = ? ORDER BY STANDARD_DATE DESC");
				ps.setString(1, id );
			} else {
				ps = conn.prepareStatement("SELECT * FROM tb_company_and_deffered WHERE STOCK_ID = ? AND STANDARD_DATE = ?");
				ps.setString(1, id );
				ps.setString(2, standardDate );
			}
			rs = ps.executeQuery();
			
			if( rs.next() ) {
				rtn = new CompanyEx();
				rtn.setId(id);
				rtn.setName(rs.getString("COMPANY_NAME"));
				rtn.setStandardDate(rs.getString("STANDARD_DATE"));
				rtn.setSecuritySector(rs.getInt("SECURITY_SECTOR"));
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
	
	public java.util.ArrayList<CompanyEx> selectAllList(String standardDate) throws SQLException {
		java.util.ArrayList<CompanyEx> list = new java.util.ArrayList<CompanyEx>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("SELECT * FROM tb_company_and_deffered A JOIN ( " +
					"SELECT STOCK_ID, MAX(STANDARD_DATE) STANDARD_DATE FROM tb_company_and_deffered  " +
					"WHERE  STANDARD_DATE <= ?  " +
					"GROUP BY STOCK_ID ) B USING ( STOCK_ID, STANDARD_DATE) " +
					"WHERE CLOSED_YN <> 'Y' ");
			ps.setString(1, standardDate);
			rs = ps.executeQuery();
			
			while ( rs.next() ) {
				CompanyEx company = new CompanyEx();
				company.setId(rs.getString("STOCK_ID"));
				company.setName(rs.getString("COMPANY_NAME"));
				company.setStandardDate(rs.getString("STANDARD_DATE"));
				company.setFicsSector(rs.getString("FICS_SECTOR"));
				company.setFicsIndustryGroup(rs.getString("FICS_INDUSTRY_GROUP"));
				company.setFicsIndustry(rs.getString("FICS_INDUSTRY"));
				company.setClosed("Y".equals(rs.getString("CLOSED_YN")));
				company.setSecuritySector(rs.getInt("SECURITY_SECTOR"));
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
	
	public void insertCompanyTableFromDefferedTable() {
		Connection conn = null;
		PreparedStatement ps = null;
		int updateCount = 0;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("insert into tb_company " + 
					"select stock_id, company_name, standard_date, modified_date, fics_sector, fics_industry_group, fics_industry, closed_yn from " + 
					"( select stock_id, company_name, standard_date, modified_date, fics_sector, fics_industry_group, fics_industry, closed_yn  " +
					"from   tb_company_and_deffered where security_sector = 0 ) a left outer join ( select stock_id, standard_date, 'true' isExist from tb_company ) b " +
					"using ( stock_id, standard_date ) " +
					"where isExist is null"
					);
			updateCount = ps.executeUpdate();
			System.out.println("Net Company Inserted :" + updateCount);
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( ps != null ) try { ps.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
			if ( conn != null ) try { conn.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
		}
		
	}
	
}
