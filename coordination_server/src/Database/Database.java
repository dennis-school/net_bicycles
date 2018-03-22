package Database;

import java.net.InetAddress;
import java.net.SocketAddress;
import java.sql.*;
import java.util.ArrayList;

public class Database {

	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/";

	// Database credentials
	static final String USER = "root";
	static final String PASS = "12345678";

	Connection conn = null;
	Statement stmt = null;
	String sql;

	public Database() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<SocketAddress> getLockers(int id) throws SQLException {
		sql = "SELECT * FROM locker_set WHERE Coordinator = " + id + ";";
		ResultSet rs = stmt.executeQuery(sql);
		ArrayList<SocketAddress> lockers = new ArrayList<SocketAddress>();
		while( rs.next() ) {
			lockers.add( new SocketAddress( rs.get  ) );
		}
		
		return null;
	}

	public ArrayList<SocketAddress> getCoordinators(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	public InetAddress getCoordinatorAddress(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getCoordiantorPort(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void insertBicycleTransection() {
		// TODO Auto-generated method stub

	}
	
	public void closeDatabase() throws SQLException {
		stmt.close();
		conn.close();
	}
	
}
