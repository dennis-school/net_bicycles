package net_bicycles_coordination_server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import Database.Database;

/**
 * Main for running one Coordinator with input id
 * run cmd at directory \net_bicycles\coordination_server\bin
 * cmd: java -cp .; first jar ; second jar net_bicycles_coordinator_server.Main coordinator_id
 * Two jar file is in folder coordinator_server
 * coordinator_id is a integer which in database, currently we only have 1,2,3,4,5
 * 
 * @author Luigi
 *
 */

public class Main {
	public static void main( String... args ) {
		/*
		Database database = new Database();
		ArrayList<Integer> coordinators = database.getCoordinators();
		for( int coordinator: coordinators) {
			Coordinator c = new Coordinator(coordinator);
			System.out.println("Coordinator" + coordinator + " start work");
		}
		*/
		int id = Integer.parseInt(args[0]);
		new Coordinator( id );
		
	}
}
