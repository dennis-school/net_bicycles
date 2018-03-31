package net_bicycles_coordination_server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import Database.Database;

/**
 * Main for running one Coordinator with input id
 * 
 * cmd: java -cp .; first jar ; second jar net_bicycles_coordinator_server.Main coordinator_id]
 * Where jar file is in folder coordinator_server
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
