package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import post.CompanyEx;
import post.KrxSecurityType;

/**
<pre>
CREATE TABLE tb_company_and_deffered
(
  stock_id character varying(10) NOT NULL,
  standard_date character varying(8) NOT NULL,
  company_name character varying(100),
  security_sector integer,
  fics_sector character varying(45),
  fics_industry_group character varying(45),
  fics_industry character varying(45),
  closed_yn character varying(45),
  modified_date timestamp without time zone,
  krx_industry_code character varying(6),
  krx_industry_sector character varying(100),
  krx_industry_category character varying(100),
  CONSTRAINT tb_company_and_deffered_pkey PRIMARY KEY (stock_id, standard_date)
)

</pre>
 * @author user
 *
 */
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
					"FICS_SECTOR, FICS_INDUSTRY_GROUP, FICS_INDUSTRY, CLOSED_YN, MODIFIED_DATE,krx_industry_code,krx_industry_sector, krx_industry_category, tel_no, address, FUTURE_YN, future_base_code) " +
					"VALUES ( " +
					"?, ?, ?, ?, " +
					"?, ?, ?, ?, CURRENT_DATE, ?, ?, ?,?, ?,?,? )"
					);
			ps.setString(1, company.getId() );
			ps.setString(2, company.getStandardDate() );
			ps.setString(3, company.getName() );
			ps.setInt(4, company.getSecuritySector() );
			ps.setString(5, company.getFicsSector() );
			ps.setString(6, company.getFicsIndustryGroup() );
			ps.setString(7, company.getFicsIndustry() );
			ps.setString(8, company.isClosed() ? "Y" : "N" );
			ps.setString(9, company.getKrxIndustryCode());
			ps.setString(10, company.getKrxIndustrySector());
			ps.setString(11, company.getKrxIndustryCategory());
			ps.setString(12, company.getTelNo());
			ps.setString(13, company.getAddress());
			ps.setString(14,  company.getFutureYn());
			ps.setString(15, company.getFutureBaseCode());
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
			ps = conn.prepareStatement("UPDATE tb_company_and_deffered SET COMPANY_NAME = ?, MODIFIED_DATE = CURRENT_DATE, SECURITY_SECTOR = ?, FICS_SECTOR = ? , FICS_INDUSTRY_GROUP = ?, FICS_INDUSTRY = ?, CLOSED_YN = ?, krx_industry_code = ?, krx_industry_sector = ?,krx_industry_category = ?, tel_no = ?, address = ? WHERE STOCK_ID = ? AND STANDARD_DATE = ?");
			ps.setString(1, company.getName() );
			ps.setInt(2, company.getSecuritySector() );
			ps.setString(3, company.getFicsSector());
			ps.setString(4, company.getFicsIndustryGroup());
			ps.setString(5, company.getFicsIndustry());
			ps.setString(6, company.isClosed() ? "Y": "N");
			ps.setString(7, company.getKrxIndustryCode());
			ps.setString(8, company.getKrxIndustrySector());
			ps.setString(9, company.getKrxIndustryCategory());
			ps.setString(10, company.getTelNo());
			ps.setString(11, company.getAddress());
			ps.setString(12, company.getId() );
			ps.setString(13, company.getStandardDate());
			rtn = ps.executeUpdate() > 0;
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( ps != null ) try { ps.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
			if ( conn != null ) try { conn.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
		}
		System.out.println( String.format("[%s]-[%s]Company information has changed. [%s]", company.getId(), company.getName(), rtn ? "Yes" : "No") );
		return rtn;
	}
	
	public boolean updateSecuritySector(CompanyEx company) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		boolean rtn = false;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("UPDATE tb_company_and_deffered SET MODIFIED_DATE = CURRENT_DATE, SECURITY_SECTOR = ? WHERE STOCK_ID = ?");
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
	
	
	private static CompanyEx getCompanyExFromResultset(ResultSet rs) throws SQLException {
		CompanyEx rtn = new CompanyEx();
		rtn.setId(rs.getString("STOCK_ID"));
		rtn.setName(rs.getString("COMPANY_NAME"));
		rtn.setStandardDate(rs.getString("STANDARD_DATE"));
		rtn.setSecuritySector(rs.getInt("SECURITY_SECTOR"));
		rtn.setFicsSector(rs.getString("FICS_SECTOR"));
		rtn.setFicsIndustryGroup(rs.getString("FICS_INDUSTRY_GROUP"));
		rtn.setFicsIndustry(rs.getString("FICS_INDUSTRY"));
		rtn.setClosed("Y".equals(rs.getString("CLOSED_YN")));
		rtn.setKrxIndustryCode(rs.getString("KRX_INDUSTRY_CODE"));
		rtn.setKrxIndustrySector(rs.getString("KRX_INDUSTRY_SECTOR"));
		rtn.setKrxIndustryCategory(rs.getString("KRX_INDUSTRY_CATEGORY"));
		rtn.setTelNo(rs.getString("TEL_NO"));
		rtn.setAddress(rs.getString("ADDRESS"));
		rtn.setFutureYn(rs.getString("FUTURE_YN"));
		rtn.setFutureBaseCode(rs.getString("FUTURE_BASE_CODE"));
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
				rtn = getCompanyExFromResultset(rs);
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
	
	public List<CompanyEx> getSingleStockFutureUnderlyingStocks() throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<CompanyEx> rtn = new ArrayList<CompanyEx>();
		try {
			conn = getConnection();
			ps = conn.prepareStatement("SELECT * FROM tb_company_and_deffered WHERE FUTURE_YN = 'Y' AND CLOSED_YN <> 'Y'");
			rs = ps.executeQuery();
			while( rs.next() ) {
				rtn.add(getCompanyExFromResultset(rs));
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

	public java.util.ArrayList<CompanyEx> selectAllList(String standardDate, KrxSecurityType securityType) throws SQLException {
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
				CompanyEx company = getCompanyExFromResultset(rs);
				if(securityType == null || securityType.getType() == company.getSecuritySector())
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

	@Deprecated
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
	
	public String getLatestStandardDate() {
		String standardDate = "";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("select max(standard_date) from tb_company_and_deffered");
			rs = ps.executeQuery();
			if (rs.next() )
				standardDate = rs.getString(1);
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( rs != null ) try { rs.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
			if ( ps != null ) try { ps.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
			if ( conn != null ) try { conn.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
		}
		return standardDate;
	}
	
}
