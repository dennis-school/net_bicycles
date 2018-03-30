package Database;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
//STEP 1. Import required packages
import java.sql.*;
import java.util.ArrayList;

public class Database_test {
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException, UnknownHostException {
		Database database = new Database();
		
		InetSocketAddress address = new InetSocketAddress( InetAddress.getLocalHost(), 8030 );
		//int locker_id = database.getCoordiantorId( address );
		
		/*
		ArrayList<SocketAddress> coordinators = database.getNeighbours( 1 );
		
		for( SocketAddress coordinator:coordinators ) {
			System.out.println( coordinator );
		}
		*/
		
		//int address = database.getCoordiantorPort( 1 );
		//System.out.println( address );
		//System.out.println( locker_id );
		
		//database.insertBicycleTransection(0, "AAITBACZ89", 10101010, 34);
		//database.insertBicycleTransection(1, "AAITBACZ89", 10101010, 34);
		
		//System.out.println( database.isFreeLocker( address ));
		
		//database.takeLocker( address, 2);
		
		return ;
	}

}