package net_bicycles_coordination_server;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

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
	private InetAddress address;
	private int port;
	
	private ArrayList<SocketAddress> lockers;
	private ArrayList<SocketAddress> coordinators;
	private HashMap<SocketAddress, LifeChecker> lifeCheckers;
	
	// use for store and extract info from net_bicycle Database
	private Database database;
	
	public static void main( String... args ) throws UnknownHostException {
		Coordinator coordinator = new Coordinator(1);
		coordinator.start();
	}
	
	public Coordinator(int id) {
		this.id = id;
		this.database = new Database();
		this.lockers = database.getLockers( id );
		this.coordinators = database.getCoordinators( id );
		this.lifeCheckers = new HashMap<SocketAddress, LifeChecker>();

		createUDPSocket( id );
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
	
	private void createUDPSocket(int id) {
		this.port = 5555;
		try {
			this.socket = new DatagramSocket( port );
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
	
	private void start() {
		CoordinatorServer server = new CoordinatorServer( this );
		Thread t = new Thread( server );
		t.start();
	}
	
	public DatagramSocket getDatagramSocket() {
		return this.socket;
	}
	
	public InetAddress getInetAddress() {
		return this.address;
	}
	
	public int getPort() {
		return this.port;
	}
	
	public LifeChecker getLifeChecker( SocketAddress address ) {
		return this.lifeCheckers.get(address);
	}
	
	public void insertBicycleTransection() {
		
	}
	
}

