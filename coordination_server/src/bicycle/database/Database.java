package bicycle.database;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.ArrayList;
/**
 * Database that connect to the real database
 * Read and write from/into the real database
 * @author Luigi
 *
 */
public class Database {

	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/bicycle";

	// Database credentials
	static final String USER = "root";
	static final String PASS = null;

	Connection conn = null;

	public static class Credentials {
		public final String host;
		public final int port;
		public final String database;
		public final String username;
		public final String password;
		
		public Credentials( String host, int port, String database, String username, String password ) {
			this.host = host;
			this.port = port;
			this.database = database;
			this.username = username;
			this.password = password;
		}
	}
	
	public Database( Credentials credentials ) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = String.format( "jdbc:mysql://%s:%d/%s", credentials.host, credentials.port, credentials.database );
			conn = DriverManager.getConnection( url, credentials.username, credentials.password );
			//stmt.execute("SET FOREIGN_KEY_CHECKS=0;");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * return the locker id with the specified socket address
	 * @param locker_address
	 * @return
	 */
	public int getLockerId(SocketAddress locker_address) {
		int port = ((InetSocketAddress)locker_address).getPort();
		String sql = "SELECT * FROM locker_set WHERE port = ?;";
		ResultSet rs;
		int locker_id = 0;
		try {
			PreparedStatement stmt = conn.prepareStatement( sql );
			stmt.setInt( 1, port );
			rs = stmt.executeQuery();
			if( rs.next() ) {
				locker_id = rs.getInt("id");	
			}
			rs.close();
			stmt.close( );
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return locker_id;
	}
	
	/**
	 * return the coordinator id with specified socket address
	 * @param coordinator_address
	 * @return
	 */
	public int getCoordiantorId(SocketAddress coordinator_address) {
		int port = ((InetSocketAddress)coordinator_address).getPort();
		String sql = "SELECT * FROM coordinator WHERE port = ?;";
		ResultSet rs;
		int coordinator_id = 0;
		try {
			PreparedStatement stmt = conn.prepareStatement( sql );
			stmt.setInt(1, port);
			rs = stmt.executeQuery();
			if(rs.next())
				coordinator_id = rs.getInt("id");
			rs.close();
			stmt.close( );
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return coordinator_id;
	}
	
	/**
	 * return a array list if coordinator's id
	 * @return
	 */
	public ArrayList<Integer> getCoordinators(){
		ArrayList<Integer> coordinators = new ArrayList<Integer>();
		String sql = "SELECT * FROM coordinator;";
		
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while( rs.next() ) {
				coordinators.add( rs.getInt("id"));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return coordinators;
	}
	
	/**
	 * return a array list of socketAddress for lockers that belonging to a coordinator with specified socket address
	 * @param coordinator_address
	 * @return
	 */
	public ArrayList<SocketAddress> getLockers(SocketAddress coordinator_address) {
		return getLockers( getCoordiantorId(coordinator_address) );
	}
	
	/**
	 * return a array list of socketAddress for lockers that belonging to a coordinator with specified id
	 * @param id
	 * @return
	 * @throws SQLException
	 * @throws UnknownHostException
	 */
	public ArrayList<SocketAddress> getLockers(int id){
		String sql = "SELECT * FROM locker_set WHERE Coordinator_id = ?;";
		
		ResultSet rs;
		InetAddress inetAddress;
		int port;
		InetSocketAddress socketAddress;
		ArrayList<SocketAddress> lockers = new ArrayList<SocketAddress>();
		
		try {
			PreparedStatement stmt = conn.prepareStatement( sql );
			stmt.setInt( 1, id );
			rs = stmt.executeQuery();
			while( rs.next() ) {
				inetAddress = InetAddress.getByName(rs.getString("ip"));
				port = rs.getInt("port");
				socketAddress = new InetSocketAddress( inetAddress, port );
				lockers.add( socketAddress );
			}
			rs.close( );
			stmt.close( );
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
	 * return a array list of two closest coordinators around the coordinator with specified id
	 * @param id
	 * @return
	 * @throws SQLException
	 * @throws UnknownHostException 
	 */
	public ArrayList<SocketAddress> getNeighbours(int id) {
		ArrayList<SocketAddress> coordinators = new ArrayList<SocketAddress>();
		
		try {
			String sql = "SELECT\r\n" + 
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
					"  c2.id = ? AND\r\n" + 
					"  c2.id <> c1.id\r\n" + 
					"GROUP BY\r\n" + 
					"  c1.id, c2.id\r\n" + 
					"ORDER BY\r\n" + 
					"  POW(c1.location_longitude-AVG(l2.location_longitude),2)+POW(c1.location_latitude-AVG(l2.location_latitude),2)\r\n" + 
					"  DESC\r\n" + 
					"LIMIT 2;";
			PreparedStatement stmt = conn.prepareStatement( sql );
			stmt.setInt( 1, id );
			ResultSet rs = stmt.executeQuery( );
			
			while ( rs.next() ) {
				InetAddress inetAddress = InetAddress.getByName( rs.getString("ip") );
				int port = rs.getInt("port");
				coordinators.add( new InetSocketAddress( inetAddress, port) );
			}
			rs.close( );
			stmt.close( );
		} catch (SQLException | UnknownHostException e) {
			e.printStackTrace();
		}
		return coordinators;
	}

	/**
	 * return InetAddress of Coordinator with specified id
	 * @param id
	 * @return
	 * @throws SQLException
	 * @throws UnknownHostException
	 */
	public InetAddress getCoordinatorAddress(int id) {
		String sql = "SELECT ip FROM coordinator WHERE id = ?;";
		ResultSet rs;
		InetAddress address = null;
		try {
			PreparedStatement stmt = conn.prepareStatement( sql );
			stmt.setInt( 1, id );
			rs = stmt.executeQuery( );
			if( rs.next() ) {
				address = InetAddress.getByName( rs.getString("ip") );
			}
			rs.close();
			stmt.close( );
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
	 * return Port of Coordinator with specified id
	 * @param id
	 * @return
	 * @throws SQLException
	 * @throws UnknownHostException
	 */
	public int getCoordiantorPort(int id) {
		String sql = "SELECT port FROM coordinator WHERE id = ?;";
		ResultSet rs;
		int port = 0;
		try {
			PreparedStatement stmt = conn.prepareStatement( sql );
			stmt.setInt( 1, id );
			rs = stmt.executeQuery( );
			if( rs.next() ) {
				port = rs.getInt("port") ;	
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return port;
	}

	/**
	 * get a array list of socket address for both coordinators and lockers
	 * @return
	 */
	public ArrayList<SocketAddress> getAllAddress() {
		ArrayList<SocketAddress> addresses = new ArrayList<SocketAddress>();
		ResultSet rs;
		InetAddress inetAddress;
		int port;
		InetSocketAddress socketAddress;
		
		String sql = "SELECT ip, port\r\n" + 
				"FROM locker_set \r\n" + 
				"UNION\r\n" + 
				"SELECT ip, port \r\n" + 
				"FROM coordinator;";
		try {
			Statement stmt = conn.createStatement( );
			rs = stmt.executeQuery(sql);
			while( rs.next() ) {
				inetAddress = InetAddress.getByName(rs.getString("ip"));
				port = rs.getInt("port");
				socketAddress = new InetSocketAddress( inetAddress, port );
				addresses.add( socketAddress );
			}
			rs.close();
			stmt.close();
		} catch (SQLException | UnknownHostException e) {
			e.printStackTrace();
		}
		
		return addresses;
	}
	
	/**
	 * check whether the Locker with specified socketAddress is free (its coordinator_id is null)
	 * @param socketAddress
	 * @return
	 */
	public boolean isFreeLocker(SocketAddress socketAddress) {
		int port = ((InetSocketAddress)socketAddress).getPort();
		String sql = "SELECT * FROM locker_set WHERE port = ?;";
		ResultSet rs;
		Integer coordinator_id = null;
		try {
			PreparedStatement stmt = conn.prepareStatement( sql );
			stmt.setInt(1, port);
			rs = stmt.executeQuery( );
			if( rs.next() )
				coordinator_id = rs.getInt("coordinator_id");
			rs.close();
			stmt.close( );
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return coordinator_id == null;
	}
	
	/**
	 * check whether there is a free locker in database
	 * @return
	 */
	public boolean noFreeLocker() {
		String sql = "SELECT * FROM locker_set WHERE ISNULL(coordinator_id);";
		ResultSet rs;
		try {
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			if( rs.next() ) {
				rs.close();
				return false;
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * change the coordinator_id in Locker with specified socketAddress
	 * @param socketAddress
	 * @param coordinator_id
	 */
	public void takeLocker(SocketAddress socketAddress, int coordinator_id) {
		int port = ((InetSocketAddress)socketAddress).getPort();
		String sql = "UPDATE locker_set SET coordinator_id = ? WHERE port = ?;";
		try {
			PreparedStatement stmt = conn.prepareStatement( sql );
			stmt.setInt( 1, coordinator_id );
			stmt.setInt( 2, port );
			stmt.executeUpdate( );
			stmt.close( );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * set coordinator_id to null for all lockers in database
	 */
	public void restartLocker() {
		String sql ="UPDATE locker_set SET coordinator_id = null;";
		try {
			Statement stmt = conn.createStatement( );
			stmt.executeUpdate( sql );
			stmt.close( );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Insert one bike transaction into Database
	 * Transaction includes: taken bike, return bike, add new bike
	 * @param locker_id 
	 * @param user_id 
	 * @param bicycle_id 
	 * @param isRemoved 
	 * @param id
	 * @return
	 * @throws SQLException
	 * @throws UnknownHostException
	 */
	public void insertBicycleTransection( boolean isTaken, String bicycle_id, int user_id, int locker_id) {
		try {
			if( isTaken ) { // A bike is taken
				//bike is taken
				String sql = "INSERT INTO transaction (bicycle_id, user_id, taken_locker, taken_timestamp)" + 
						" values " + 
						"(?, ?, ?, NOW() );";
				PreparedStatement stmt = conn.prepareStatement( sql );
				stmt.setString( 1, bicycle_id );
				stmt.setInt( 2, user_id );
				stmt.setInt( 3, locker_id );
				stmt.executeUpdate( );
				stmt.close( );

			    // insert locker bike
			    sql = "UPDATE bicycle SET current_locker = NULL WHERE id = ?;";
			    stmt = conn.prepareStatement( sql );
			    stmt.setString(1, bicycle_id);
			    stmt.executeUpdate( );
			    stmt.close( );
			}else { // A bike is returned
				String sql = "UPDATE transaction SET returned_locker = ?, returned_timestamp = NOW() " + 
						     "WHERE bicycle_id = ? AND ISNULL(returned_timestamp);";
				PreparedStatement stmt = conn.prepareStatement( sql );
				stmt.setInt( 1, locker_id );
				stmt.setString( 2, bicycle_id );
				stmt.executeUpdate( );
				stmt.close( );
			
			    // insert locker bike
			    sql = "UPDATE bicycle SET current_locker = ? WHERE id = ?;";
			    stmt = conn.prepareStatement( sql );
			    stmt.setInt(1, locker_id);
			    stmt.setString(2, bicycle_id);
			    stmt.executeUpdate( );
			    stmt.close( );
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * close the database
	 */
	public void closeDatabase() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
