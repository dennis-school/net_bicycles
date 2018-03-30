package net_bicycles_coordination_server;

import java.util.ArrayList;

import Database.Database;

public class Main {
	public static void main( String... args ) {
		Database database = new Database();
		ArrayList<Integer> coordinators = database.getCoordinators();
		for( int coordinator: coordinators) {
			Coordinator c = new Coordinator(coordinator);
			System.out.println("Coordinator" + coordinator + " start work");
		}
		return;
	}
}
