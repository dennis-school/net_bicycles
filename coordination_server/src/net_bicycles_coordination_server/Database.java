package net_bicycles_coordination_server;

import java.sql.*;

// install and run MySQL
// download java JDBC file

public class Database {

	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost/";

	//  Database credentials
	static final String USER = "root";
	static final String PASS = "12345678";
	
	Connection conn;
	
	public Database() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addCoordinator( Coordinator coordinator ) throws SQLException {
		
		String sql = null;
		PreparedStatement ptmt = conn.prepareStatement(sql);
		
		ptmt.setString(2, coordinator.getDatagramSocket());
		
	}
	
	public void deleteCoordinator( Coordinator coordinator ) {
		
	}
	
	public void addTransection( ) {
		
	}
	
}
