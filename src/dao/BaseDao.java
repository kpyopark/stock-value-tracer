package dao;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;

public class BaseDao {

	static BasicDataSource ds = null;
	
	static {
		try {
			ds = new BasicDataSource();
			ds.setDriverClassName("org.postgresql.Driver");
	        ds.setUsername("postgres");
	        ds.setPassword("aaaa1111");
	        ds.setUrl("jdbc:postgresql://127.0.01:5432/stock");
	        ds.setMaxActive(120);
	        ds.setDefaultAutoCommit(true);
	        ds.setInitialSize(120);
		} catch ( Exception e ) { e.printStackTrace(); }
	}
	
	public static Connection getConnection() throws SQLException {
		Connection rtn = null;
		try {
			rtn = ds.getConnection();
		} catch ( Exception e ) {
			// 만약 에러가 일어나면 한번더 하게 해줌.
			rtn = ds.getConnection();
		}
		return rtn; 
	}
	
	public static void main(String[] args) {
		new BaseDao();
	}
}
