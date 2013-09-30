package dao;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;

public class BaseDao {

	static BasicDataSource ds = null;
	
	static {
		try {
			ds = new BasicDataSource();
			ds.setDriverClassName("com.mysql.jdbc.Driver");
	        ds.setUsername("root");
	        ds.setPassword("aaaa1111");
	        ds.setUrl("jdbc:mysql://127.0.0.1:3306/stock");
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
