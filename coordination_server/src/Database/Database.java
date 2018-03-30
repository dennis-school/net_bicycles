package Database;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.ArrayList;

public class Database {

	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/database_test";

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
			//stmt.execute("SET FOREIGN_KEY_CHECKS=0;");
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
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
			if( rs.next() ) {
				locker_id = rs.getInt("id");	
			}
			rs.close();
		} catch (SQLException e) {
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
			if(rs.next())
				coordinator_id = rs.getInt("id");
			rs.close();
		} catch (SQLException e) {
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
		return getLockers( getCoordiantorId(coordinator_address) );
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
	 * return two closest coordinators around the coordinator with chosen id
	 * @param id
	 * @return
	 * @throws SQLException
	 * @throws UnknownHostException 
	 */
	public ArrayList<SocketAddress> getNeighbours(int id) {
		ArrayList<SocketAddress> coordinators = new ArrayList<SocketAddress>();
		
		try {
			sql = "SELECT\r\n" + 
					"  c1.*\r\n" + 
					"FROM\r\n" + 
					"  (SELECT\r\n" + 
					"     c.id AS id,\r\n" + 
					"     c.ip AS ip,\r\n" + 
					"     c.port AS port,\r\n" + 
					"     AVG(l.location_latitude) AS location_latitude,\r\n" + 
					"     AVG(l.location_longitude) AS location_longitude\r\n" + 
					"   FROM\r\n" + 
					"     coordinator AS c\r\n" + 
					"     JOIN locker_set AS l ON (l.coordinator_id = c.id)\r\n" + 
					"   GROUP BY c.id) AS c1,\r\n" + 
					"  coordinator AS c2\r\n" + 
					"  JOIN locker_set AS l2 ON (l2.coordinator_id = c2.id)\r\n" + 
					"WHERE\r\n" + 
					"  c2.id = " + id + " AND\r\n" + 
					"  c2.id <> c1.id\r\n" + 
					"GROUP BY\r\n" + 
					"  c1.id, c2.id\r\n" + 
					"ORDER BY\r\n" + 
					"  POW(c1.location_longitude-AVG(l2.location_longitude),2)+POW(c1.location_latitude-AVG(l2.location_latitude),2)\r\n" + 
					"  DESC\r\n" + 
					"LIMIT 2;";
			ResultSet rs = stmt.executeQuery(sql);
			
			while ( rs.next() ) {
				InetAddress inetAddress = InetAddress.getByName( rs.getString("ip") );
				int port = rs.getInt("port");
				coordinators.add( new InetSocketAddress( inetAddress, port) );
			}
			
		} catch (SQLException | UnknownHostException e) {
			e.printStackTrace();
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
	public InetAddress getCoordinatorAddress(int id) {
		sql = "SELECT ip FROM coordinator WHERE id = " + id + ";";
		ResultSet rs;
		InetAddress address = null;
		try {
			rs = stmt.executeQuery(sql);
			if( rs.next() ) {
				address = InetAddress.getByName( rs.getString("ip") );
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return address;
	}
	
	/**
	 * Return Port of Coordinator with id from Database
	 * @param id
	 * @return
	 * @throws SQLException
	 * @throws UnknownHostException
	 */
	public int getCoordiantorPort(int id) {
		sql = "SELECT port FROM coordinator WHERE id = " + id + ";";
		ResultSet rs;
		int port = 0;
		try {
			rs = stmt.executeQuery(sql);
			if( rs.next() ) {
				port = rs.getInt("port") ;	
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return port;
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
			sql = "insert into transaction (bicycle_id, user_id, taken_locker, taken_timestamp)\r\n" + 
					"values \r\n" + 
					"(\"" + bicycle_id + "\", " + user_id +", " + locker_id + ", Now() );";
		}else {
			sql = "update transaction set returned_locker = " + locker_id + ", returned_timestamp = NOW()\r\n" + 
					"where bicycle_id = \"" + bicycle_id + "\" AND ISNULL(returned_timestamp);";
		}
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void closeDatabase() {
		try {
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * check whether the Locker with specified socketAddress is free
	 * @param socketAddress
	 * @return
	 */
	public boolean isFreeLocker(SocketAddress socketAddress) {
		int port = ((InetSocketAddress)socketAddress).getPort();
		sql = "SELECT * FROM locker_set WHERE port = " + port + ";";
		ResultSet rs;
		Integer coordinator_id = null;
		try {
			rs = stmt.executeQuery(sql);
			if( rs.next() )
				coordinator_id = rs.getInt("coordinator_id");
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return coordinator_id == null;
	}

	/**
	 * Change the coordinator_id in Locker with specified socketAddress
	 * @param socketAddress
	 * @param coordinator_id
	 */
	public void takeLocker(SocketAddress socketAddress, int coordinator_id) {
		int port = ((InetSocketAddress)socketAddress).getPort();
		sql = "UPDATE locker_set SET coordinator_id = " + coordinator_id + " WHERE port = " + port + ";";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
