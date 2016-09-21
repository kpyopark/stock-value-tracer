package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import common.StringUtil;
import post.Company;
import post.CompanyEx;
import post.CompanyFinancialStatusEstimated;

/**
<pre>
CREATE TABLE `tb_company_estim_stat` (
  `STOCK_ID` varchar(10) NOT NULL DEFAULT '',
  `STANDARD_DATE` varchar(8) NOT NULL DEFAULT '',
  `IS_ANNUAL` varchar(1) NOT NULL DEFAULT '',
  `ESTIM_KIND` varchar(1) NOT NULL DEFAULT 'O',
  `ASSET_TOTAL` bigint(20) DEFAULT NULL,
  `DEBT_TOTAL` bigint(20) DEFAULT NULL,
  `CAPITAL` bigint(20) DEFAULT NULL,
  `CAPITAL_TOTAL` bigint(20) DEFAULT NULL,
  `SALES` bigint(20) DEFAULT NULL,
  `OPERATION_PROFIT` bigint(20) DEFAULT NULL,
  `ORDINARY_PROFIT` bigint(20) DEFAULT NULL,
  `NET_PROFIT` bigint(20) DEFAULT NULL,
  `INVESTED_CAPITAL` bigint(20) DEFAULT NULL,
  `PREFFERED_STOCK_SIZE` bigint(20) DEFAULT NULL,
  `GENERAL_STOCK_SIZE` bigint(20) DEFAULT NULL,
  `DIVIDENED_RATIO` float(8,3) DEFAULT NULL,
  `ROE` float(8,3) DEFAULT NULL,
  `ROA` float(8,3) DEFAULT NULL,
  `ROI` float(8,3) DEFAULT NULL,
  `KOSPI_YN` varchar(1) DEFAULT NULL,
  `FIXED_YN` varchar(1) DEFAULT NULL,
  `MODIFIED_DATE` date DEFAULT NULL,
  `ESTIMATED_YN` varchar(1) NOT NULL DEFAULT 'N',
  `RELATED_DATE_LIST` varchar(45) DEFAULT NULL,
  `REGISTERED_DATE` varchar(8) DEFAULT NULL,
  PRIMARY KEY (`STOCK_ID`,`STANDARD_DATE`,`IS_ANNUAL`,`ESTIM_KIND`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;


-- Procedure
DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `proc_estimate_financial_report`(in in_stock_id varchar(10),in in_registered_date varchar(8))
BEGIN
	set @num=0,@stock_id='',@sales=0,@operation_profit=0,@ordinary_profit=0,@net_profit=0;

	delete from tb_company_estim_stat
    where  stock_id = in_stock_id and standard_date = ( 
			select max(standard_date) 
            from tb_company_stat_refined
			where stock_id = in_stock_id 
            and standard_date <= in_registered_date 
            and is_annual = 'N' 
            and fixed_yn = 'Y' 
		)
		and is_annual = 'Y' and estim_kind = 'O';

    insert into tb_company_estim_stat
 	select stock_id, 
		max(standard_date) as standard_date, 
		'Y' as IS_ANNUAL, 
        'O' as estim_kind,
        avg(if(asset_total = 0, null, asset_total)) as asset_total, 
        avg(if(debt_total = 0, null, debt_total)) as debt_total,
        avg(if(capital = 0, null, capital)) as capital,
        avg(if(capital_total = 0, null, capital_total)) as capital_total,
        sum(sales_) as sales,
        sum(operation_profit_) as operation_profit,
        sum(ordinary_profit_) as ordinary_profit,
        sum(net_profit_) as net_profit,
        avg(if(invested_capital = 0, null, invested_capital)) as invested_capital,
        avg(if(preffered_stock_size = 0, null, preffered_stock_size)) as preffered_stock_size,
        avg(if(general_stock_size = 0, null, general_stock_size)) as general_stock_size,
        max(DIVIDENED_RATIO) as dividened_ratio,
        avg(roe) as roe,
        avg(roa) as roa,
        avg(roi) as roi,
        KOSPI_YN,
        FIXED_YN,
        MODIFIED_DATE,
        CALCULATED_YN AS ESTIMATED_YN,
        group_concat(standard_date) AS RELATED_DATE_LIST,
        REGISTERED_DATE
	from ( 
		select *,
			@num := IF(@stock_id = stock_id, @num + 1, 1) as rownum,
            @stock_id := stock_id as stock_id_group,
            @sales := IF(sales <> 0, sales, @sales) as sales_,
            @operation_profit := IF(operation_profit <> 0, operation_profit, @operation_profit) as operation_profit_,
            @ordinary_profit := IF(ordinary_profit <> 0, ordinary_profit, @ordinary_profit) as ordinary_profit_,
            @net_profit := IF(net_profit <> 0, net_profit, @net_profit) as net_profit_
        from tb_company_stat_refined 
		where stock_id = in_stock_id and standard_date <= in_registered_date
		and is_annual = 'N'
		and fixed_yn = 'Y'
		order by standard_date desc
        ) b
	where rownum <= 4;
    
END$$
DELIMITER ;


</pre>
 * @author user
 *
 */
public class CompanyFinancialEstimStatusDao extends BaseDao {
	
	final static long PRECISION_THRESHOLD = 1000000;
	
	final static String INSERT_QUERY = 
			"    insert into tb_company_estim_stat" +
			" 	select stock_id, " +
			"		max(standard_date) as standard_date, " +
			"		'Y' as IS_ANNUAL, " +
			"        'O' as estim_kind," +
			"        avg(case when asset_total = 0 then null else asset_total end) as asset_total, " +
			"        avg(case when debt_total = 0 then null else debt_total end) as debt_total," +
			"        avg(case when capital = 0 then null else capital end) as capital," +
			"        avg(case when capital_total = 0 then null else capital_total end) as capital_total," +
			"        sum(sales_) as sales," +
			"        sum(operation_profit_) as operation_profit," +
			"        sum(ordinary_profit_) as ordinary_profit," +
			"        sum(net_profit_) as net_profit," +
			"        avg(case when invested_capital = 0 then null else invested_capital end) as invested_capital," +
			"        avg(case when preffered_stock_size = 0 then null else preffered_stock_size end) as preffered_stock_size," +
			"        avg(case when general_stock_size = 0 then null else general_stock_size end) as general_stock_size," +
			"        max(DIVIDENED_RATIO) as dividened_ratio," +
			"        avg(roe) as roe," +
			"        avg(roa) as roa," +
			"        avg(roi) as roi," +
			"        KOSPI_YN," +
			"        FIXED_YN," +
			"        max(MODIFIED_DATE) as modified_date," +
			"        'Y' AS ESTIMATED_YN," +
			"        array_to_string(array_agg(standard_date),',') AS RELATED_DATE_LIST," +
			"        max(REGISTERED_DATE) as registered_date" +
			"	from " +
			"	( " +
			"	select *," +
			"	       case when sales <> 0 then sales else avg(case when sales = 0 then null else sales end) over (partition by stock_id) end sales_," +
			"	       case when operation_profit <> 0 then operation_profit else avg(case when operation_profit = 0 then null else operation_profit end) over (partition by stock_id) end operation_profit_," +
			"	       case when ordinary_profit <> 0 then ordinary_profit else avg(case when ordinary_profit = 0 then null else ordinary_profit end) over (partition by stock_id) end ordinary_profit_," +
			"	       case when net_profit <> 0 then net_profit else avg(case when net_profit = 0 then null else net_profit end) over (partition by stock_id) end net_profit_" +
			"        from tb_company_stat_refined " +
			"	where 1=1" +
			"	        and stock_id = ? and standard_date <= ?" +
			"		and is_annual = 'N'" +
			"		and fixed_yn = 'Y'" +
			"	order by standard_date desc" +
			"	limit 4" +
			"        ) b" +
			"        group by stock_id, b.kospi_yn, b.fixed_yn";
	
	final static String DELETE_QUERY = 
			"delete from tb_company_estim_stat" +
					"    where  stock_id = ? and standard_date = ( " +
					"			select max(standard_date) " +
					"            from tb_company_stat_refined" +
					"			where stock_id = ? " +
					"            and standard_date <= ? " +
					"            and is_annual = 'N' " +
					"            and fixed_yn = 'Y' " +
					"		)" +
					"		and is_annual = 'Y' and estim_kind = 'O'";
	
	final static String DELETE_OVERESTIMATED_ROWS = 
			"delete from tb_company_estim_stat " +
					"where  (stock_id, standard_date) in ( " +
					"select a.stock_id, a.standard_date from tb_company_estim_stat a, " +
					"        ( " +
					"         select b1.stock_id, max(b1.standard_date) as standard_date " +
					"         from tb_company_stat_refined b1  " +
					"         where b1.stock_id = ? and b1.standard_date between to_char(to_timestamp(?, 'YYYYMMDD') - '1 year'::interval, 'YYYYMMDD') and to_char(to_timestamp(?, 'YYYYMMDD'), 'YYYYMMDD') " +
					"	 and b1.is_annual = 'N'  " +
					"	 and b1.fixed_yn = 'Y'  " +
					"         group by b1.stock_id " +
					"        ) b " +
					"where   a.stock_id = b.stock_id  " +
					"        and a.standard_date > b.standard_date " +
					"	and a.is_annual = 'Y' and a.estim_kind = 'O' " +
					")";
	
	private void deleteLastestFinancialStatementForUpdate(Connection conn, Company company, String registeredDate) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(DELETE_QUERY);
			int cnt = 1;
			ps.setString(cnt++, company.getId());
			ps.setString(cnt++, company.getId());
			ps.setString(cnt++, registeredDate);

			ps.execute();
		} finally {
			if(ps!=null)try{ps.close();}catch(Exception e){}
		}
	}
	
	private void deleteFalseEstimatedItems(Connection conn, Company company, String registeredDate) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(DELETE_OVERESTIMATED_ROWS);
			int cnt = 1;
			ps.setString(cnt++, company.getId());
			ps.setString(cnt++, registeredDate);
			ps.setString(cnt++, registeredDate);

			ps.execute();
		} finally {
			if(ps!=null)try{ps.close();}catch(Exception e){}
		}
	}
	
	private void insertLastestFinancialStatementForUpdate(Connection conn, Company company, String registeredDate) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(INSERT_QUERY);
			int cnt = 1;
			ps.setString(cnt++, company.getId());
			ps.setString(cnt++, registeredDate);
			ps.execute();
		} finally {
			if(ps!=null)try{ps.close();}catch(Exception e){}
		}
	}
	
	public CompanyFinancialStatusEstimated updateFinancialReportEstimated(Company company, String registeredDate) {
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		CompanyFinancialStatusEstimated rtn = null;
		try {
			conn = getConnection();
			deleteLastestFinancialStatementForUpdate(conn, company, registeredDate);
			deleteFalseEstimatedItems(conn, company, registeredDate);
			insertLastestFinancialStatementForUpdate(conn, company, registeredDate);
			
			ps2 = conn.prepareStatement("select * from TB_COMPANY_ESTIM_STAT WHERE  STOCK_ID = ? and is_annual = 'Y' and standard_date <= ? ORDER BY 1,2 desc,3 limit 1");
			int cnt = 1;
			ps2.setString(cnt++, company.getId());
			ps2.setString(cnt++, registeredDate);
			
			rs = ps2.executeQuery();
			
			rs.next();
			
			rtn = getCompanyFinancialStatusEstimatedFromResultSet(company, rs);
			
		} catch ( Exception e ) {
			System.err.println("update estimation:" + company.getId() + "[" + company.getName() + "]:" + registeredDate + " failed.");
			//e.printStackTrace();
		} finally {
			if ( ps != null ) try { ps.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
			if ( ps2 != null ) try { ps2.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
			if ( conn != null ) try { conn.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
		}
		return rtn;
	}
	
	private static CompanyFinancialStatusEstimated getCompanyFinancialStatusEstimatedFromResultSet(Company company, ResultSet rs) throws SQLException {
		CompanyFinancialStatusEstimated rtn = new CompanyFinancialStatusEstimated();
		rtn.setCompany(company);
		rtn.setStandardDate(rs.getString("STANDARD_DATE"));
		rtn.setEstimKind(rs.getString("ESTIM_KIND"));
		rtn.setQuarter(rs.getString("IS_ANNUAL").equals("N"));
		rtn.setAssets(rs.getLong("ASSET_TOTAL"));
		rtn.setDebt(rs.getLong("DEBT_TOTAL"));
		rtn.setCapital(rs.getLong("CAPITAL"));
		rtn.setGrossCapital(rs.getLong("CAPITAL_TOTAL"));
		rtn.setSales(rs.getLong("SALES"));
		rtn.setOperatingProfit(rs.getLong("OPERATION_PROFIT"));
		rtn.setOrdinaryProfit(rs.getLong("ORDINARY_PROFIT"));
		rtn.setNetProfit(rs.getLong("NET_PROFIT"));
		rtn.setInvestedCapital(rs.getLong("INVESTED_CAPITAL"));
		rtn.setPrefferedSharesSize(rs.getLong("PREFFERED_STOCK_SIZE"));
		rtn.setOrdinarySharesSize(rs.getLong("GENERAL_STOCK_SIZE"));
		rtn.setDividendRatio(rs.getFloat("DIVIDENED_RATIO"));
		rtn.setRoe(rs.getFloat("ROE"));
		rtn.setRoa(rs.getFloat("ROA"));
		rtn.setRoi(rs.getFloat("ROI"));
		rtn.setKOSPI("Y".equals(rs.getString("KOSPI_YN")));
		rtn.setFixed("Y".equals(rs.getString("FIXED_YN")));
		rtn.setEstimatedYn(rs.getString("ESTIMATED_YN"));
		rtn.setRelatedDateList(rs.getString("RELATED_DATE_LIST"));
		rtn.setRegisteredDate(rs.getString("REGISTERED_DATE"));
		return rtn;
	}
	
	public boolean insert(CompanyFinancialStatusEstimated financialStat) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		boolean rtn = false;
		try {
			conn = getConnection();
			//System.out.println(financialStat.getRelatedDateList());
			ps = conn.prepareStatement("INSERT INTO TB_COMPANY_ESTIM_STAT ( STOCK_ID,STANDARD_DATE,IS_ANNUAL,ESTIM_KIND,ASSET_TOTAL,DEBT_TOTAL,CAPITAL,CAPITAL_TOTAL,SALES,OPERATION_PROFIT,ORDINARY_PROFIT,NET_PROFIT,INVESTED_CAPITAL,PREFFERED_STOCK_SIZE,GENERAL_STOCK_SIZE,DIVIDENED_RATIO,ROE,ROA,ROI,KOSPI_YN,FIXED_YN,ESTIMATED_YN,RELATED_DATE_LIST,REGISTERED_DATE ) VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )");
			int cnt = 1;
			ps.setString(cnt++, financialStat.getCompany().getId() );
			ps.setString(cnt++, financialStat.getStandardDate() );
			ps.setString(cnt++, financialStat.isQuarter() ? "N" : "Y" );
			ps.setString(cnt++, financialStat.getEstimKind() );
			ps.setLong(cnt++, financialStat.getAssets() );
			ps.setLong(cnt++, financialStat.getDebt() );
			ps.setLong(cnt++, financialStat.getCapital() );
			ps.setLong(cnt++, financialStat.getGrossCapital() );
			ps.setLong(cnt++, financialStat.getSales() );
			ps.setLong(cnt++, financialStat.getOperatingProfit() );
			ps.setLong(cnt++, financialStat.getOrdinaryProfit() );
			ps.setLong(cnt++, financialStat.getNetProfit() );
			ps.setLong(cnt++, 0 );
			ps.setLong(cnt++, financialStat.getPrefferedSharesSize() );
			ps.setLong(cnt++, financialStat.getOrdinarySharesSize() );
			ps.setFloat(cnt++, financialStat.getDividendRatio());
			ps.setFloat(cnt++, financialStat.getRoe());
			ps.setFloat(cnt++, financialStat.getRoa());
			ps.setFloat(cnt++, financialStat.getRoi());
			ps.setString(cnt++, financialStat.isKOSPI() ? "Y" : "N" );
			ps.setString(cnt++, financialStat.isFixed() ? "Y" : "N" );
			ps.setString(cnt++, financialStat.getEstimatedYn() );
			ps.setString(cnt++, financialStat.getRelatedDateList());
			ps.setString(cnt++, financialStat.getRegisteredDate());
			rtn = ps.execute();
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( ps != null ) try { ps.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
			if ( conn != null ) try { conn.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
		}
		return rtn;
	}
	
	public CompanyFinancialStatusEstimated select(Company company, String standardDate, String estimKind) throws SQLException {
		CompanyFinancialStatusEstimated rtn = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			if ( estimKind == null )
				estimKind = "O";
			if ( standardDate != null ) {
				ps = conn.prepareStatement(SELECT_STATEMENT_01);
				ps.setString(1, company.getId() );
				ps.setString(2, standardDate );
				ps.setString(3, estimKind );
			} else {
				ps = conn.prepareStatement(SELECT_STATEMENT_02);
				ps.setString(1, company.getId() );
				ps.setString(2, estimKind );
			}
			rs = ps.executeQuery();
			
			if( rs.next() ) {
				rtn = getCompanyFinancialStatusEstimatedFromResultSet(company, rs);
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
	
	private final static String SELECT_STATEMENT_01 = "SELECT * FROM TB_COMPANY_ESTIM_STAT WHERE STOCK_ID = ? AND STANDARD_DATE <= ? AND ESTIM_KIND = ? ORDER BY STANDARD_DATE DESC";
	private final static String SELECT_STATEMENT_02 = "SELECT * FROM TB_COMPANY_ESTIM_STAT WHERE STOCK_ID = ? AND ESTIM_KIND = ? ORDER BY STANDARD_DATE DESC";
	
	public boolean delete(CompanyFinancialStatusEstimated cfs) {
		boolean rtn = false;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("DELETE FROM TB_COMPANY_ESTIM_STAT WHERE STOCK_ID = ? AND STANDARD_DATE = ? AND IS_ANNUAL = ? AND ESTIM_KIND = ?");
			ps.setString(1, cfs.getCompany().getId() );
			ps.setString(2, cfs.getStandardDate() );
			ps.setString(3, cfs.isQuarter() ? "N" : "Y");
			ps.setString(4, cfs.getEstimKind() );
			rtn = ps.executeUpdate() > 0;
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( ps != null ) try { ps.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
			if ( conn != null ) try { conn.close(); } catch ( Exception e1 ) { e1.printStackTrace(); }
		}
		return rtn;
		
	}
	
	public static void main(String[] args) {
		testUpdateFinancialReportEstimated();
	}
	
	public static void testUpdateFinancialReportEstimated() {
		CompanyFinancialEstimStatusDao dao = new CompanyFinancialEstimStatusDao();
		CompanyEx company = new CompanyEx();
		company.setId("A036580");
		dao.updateFinancialReportEstimated(company, StringUtil.convertToStandardDate(new Date()));
	}
	
}
