package net_bicycles_coordination_server;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import Database.Database;

/**
 * Handle locker's and coordinator's package
 * 
 * @author Luigi
 *
 */

public class Coordinator {

	// id of Coordinator in Database
	private int id;
	
	private DatagramSocket socket;
	private SocketAddress address;
	
	private ArrayList<SocketAddress> lockers;
	private ArrayList<SocketAddress> coordinators;
	private HashMap<SocketAddress, LifeChecker> lifeCheckers;
	
	CoordinatorServer server;
	
	// use for store and extract info from net_bicycle Database
	private Database database;
	
	public Coordinator() {
		try {
			this.socket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Coordinator(int id) throws UnknownHostException, SQLException {
		this.id = id;
		this.database = new Database();
		this.lockers = database.getLockers( id );
		this.coordinators = database.getCoordinators( id );
		this.lifeCheckers = new HashMap<SocketAddress, LifeChecker>();
		createUDPSocket( id );
		this.server = new CoordinatorServer( this );
		Thread t = new Thread( server );
		t.start();
		createLifeChecker();
	}

	// check life status for surrounding coordinators
	private void createLifeChecker() {
		for( SocketAddress address:coordinators ) {
			LifeChecker checker = new LifeChecker( address, this );
			lifeCheckers.put( address, checker);
			Thread t = new Thread( checker );
			t.start();
		}
	}

	public synchronized void removeCoordinator( SocketAddress address ) {
		this.coordinators.remove( address );
	}
	
	public synchronized void addLockers( SocketAddress address ) {
		this.lockers.add( address );
	}
	
	private void createUDPSocket(int id) throws UnknownHostException, SQLException {
		this.address = new InetSocketAddress( database.getCoordinatorAddress(id), database.getCoordiantorPort(id) );
		try {
			this.socket = new DatagramSocket( address );
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		try {
			this.socket = new DatagramSocket();
			this.localAddress = socket.getLocalAddress();
			this.localPort = socket.getLocalPort();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
	
	public DatagramSocket getDatagramSocket() {
		return this.socket;
	}
	
	public SocketAddress getSocketAddress() {
		return this.address;
	}
	
	public LifeChecker getLifeChecker( SocketAddress address ) {
		return this.lifeCheckers.get(address);
	}
	
	public void insertBicycleTransection() throws SQLException {
		this.database.insertBicycleTransection();
	}
	
}

