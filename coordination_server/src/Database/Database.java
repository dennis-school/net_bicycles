package Database;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.ArrayList;

public class Database {

	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/Net_Bicycle";

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



	/**
	 * return locker_id by using its socket address
	 * @param locker_address
	 * @return
	 */
	public int getLockerId(SocketAddress locker_address) {
		int port = ((InetSocketAddress)locker_address).getPort();
		sql = "SELECT * FROM locker_set WHERE port = " + port + ";";
		ResultSet rs;
		int locker_id = 0;
		try {
			rs = stmt.executeQuery(sql);
			locker_id = rs.getInt("id");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return locker_id;
	}
	
	/**
	 * return coordinator_id by using its socket address
	 * @param coordinator_address
	 * @return
	 */
	public int getCoordiantorId(SocketAddress coordinator_address) {
		int port = ((InetSocketAddress)coordinator_address).getPort();
		sql = "SELECT * FROM coordinator WHERE port = " + port + ";";
		ResultSet rs;
		int coordinator_id = 0;
		try {
			rs = stmt.executeQuery(sql);
			coordinator_id = rs.getInt("id");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return coordinator_id;
	}
	
	/**
	 * return a set of socketAddress of lockers of a coordinator with its socket address
	 * @param coordinator_address
	 * @return
	 */
	public ArrayList<SocketAddress> getLockers(SocketAddress coordinator_address) {
		int port = ((InetSocketAddress)coordinator_address).getPort();
		int coordinator_id = 0;
		sql = "SELECT * FROM coordinator WHERE port = " + port + ";";
		ResultSet rs;
		try {
			rs = stmt.executeQuery(sql);
			coordinator_id = rs.getInt("coordinator_id");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getLockers( coordinator_id );
	}
	
	/**
	 * return a set of socketAddress of lockers of a coordinator with id
	 * @param id
	 * @return
	 * @throws SQLException
	 * @throws UnknownHostException
	 */
	public ArrayList<SocketAddress> getLockers(int id){
		sql = "SELECT * FROM locker_set WHERE Coordinator_id = " + id + ";";
		
		ResultSet rs;
		InetAddress inetAddress;
		int port;
		InetSocketAddress socketAddress;
		ArrayList<SocketAddress> lockers = new ArrayList<SocketAddress>();
		
		try {
			rs = stmt.executeQuery(sql);
			while( rs.next() ) {
				inetAddress = InetAddress.getByName(rs.getString("ip"));
				port = rs.getInt("port");
				socketAddress = new InetSocketAddress( inetAddress, port );
				lockers.add( socketAddress );
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return lockers;
	}

	/**
	 * return five closest coordinators around the coordinator with chosen id
	 * @param id
	 * @return
	 * @throws SQLException
	 * @throws UnknownHostException 
	 */
	@SuppressWarnings("null")
	public ArrayList<SocketAddress> getNeighbours(int id) throws SQLException, UnknownHostException {
		ArrayList<SocketAddress> coordinators = null;
		int count = 0;
		
		sql = "DROP TABLE IF EXISTS v1;\r\n" + 
				"DROP TABLE IF EXISTS v2;\r\n" + 
				"\r\n" + 
				"create table v1 as\r\n" + 
				"select AVG(locker_set.location_longitude) as x, AVG(locker_set.location_latitude) as y, coordinator.ip as ip, coordinator.port as port, coordinator.id as id\r\n" + 
				"from locker_set\r\n" + 
				"LEFT JOIN coordinator ON locker_set.coordinator_id = coordinator.id\r\n" + 
				"group by locker_set.coordinator_id;\r\n" + 
				"\r\n" + 
				"create table v2 as\r\n" + 
				"select sqrt( power( x - (select v1.x from v1 where v1.id = " + id + "), 2) + power( y - (select v1.y from v1 where v1.id = " + id + "), 2) ) as diff, ip, port\r\n" + 
				"from v1\r\n" + 
				"order by diff;";
		
		stmt.executeUpdate(sql);
		
		sql = "select * from v2;";
		ResultSet rs = stmt.executeQuery(sql);
		
		while (rs.next() && count < 2) {
			count++;
			InetAddress inetAddress = InetAddress.getByName( rs.getString("ip") );
			int port = rs.getInt("port");

			coordinators.add( new InetSocketAddress( inetAddress, port) );
		}
		
		
		
		return coordinators;
	}

	/**
	 * Return InetAddress of Coordinator with id from Database
	 * @param id
	 * @return
	 * @throws SQLException
	 * @throws UnknownHostException
	 */
	public InetAddress getCoordinatorAddress(int id) throws SQLException, UnknownHostException {
		sql = "SELECT ip FROM coordinator WHERE id = " + id + ";";
		ResultSet rs = stmt.executeQuery(sql);
		return InetAddress.getByName( rs.getString("ip") );
	}
	
	/**
	 * Return Port of Coordinator with id from Database
	 * @param id
	 * @return
	 * @throws SQLException
	 * @throws UnknownHostException
	 */
	public int getCoordiantorPort(int id) throws SQLException {
		sql = "SELECT port FROM coordinator WHERE id = " + id + ";";
		ResultSet rs = stmt.executeQuery(sql);
		return rs.getInt("port") ;
	}

	/**
	 * Insert one bike transaction into Database
	 * @param locker_id 
	 * @param user_id 
	 * @param bicycle_id 
	 * @param isRemoved 
	 * @param id
	 * @return
	 * @throws SQLException
	 * @throws UnknownHostException
	 */
	public void insertBicycleTransection(int isRemoved, String bicycle_id, int user_id, int locker_id) {
		String sql = null;
		if( isRemoved == 0 ) {
			//bike is taken
			sql = "INSERT INTO transaction (bicycle_id, user_id, taken_locker) VALUES ( " + bicycle_id + ", " + user_id + ", " + locker_id + ");";
		}else {
			
		}
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void closeDatabase() throws SQLException {
		stmt.close();
		conn.close();
	}

	public boolean isFreeLocker(SocketAddress socketAddress) {
		int port = ((InetSocketAddress)socketAddress).getPort();
		sql = "SELECT * FROM locker_set WHERE port = " + port + ";";
		ResultSet rs;
		Integer coordinator_id = null;
		try {
			rs = stmt.executeQuery(sql);
			coordinator_id = rs.getInt("coordinator_id");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return coordinator_id == null;
	}

	public void takeLocker(SocketAddress socketAddress, int coordinator_id) {
		int port = ((InetSocketAddress)socketAddress).getPort();
		sql = "update locker_set set coordinator_id = " + coordinator_id + " where port = " + port + ";";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
