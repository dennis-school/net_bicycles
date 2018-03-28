package net_bicycles_coordination_server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import Database.Database;
import Packet.PacketReplaceConnectionRequest;

/**
 * Handle locker's and coordinator's package
 * 
 * @author Luigi
 *
 */

public class Coordinator {

	// id of Coordinator in Database
	private int coordinator_id;
	
	private DatagramSocket socket;
	private SocketAddress address;
	
	private ArrayList<SocketAddress> listeningLockers;
	private ArrayList<SocketAddress> waitingLockers;
	
	// two closest coordinators around it
	private ArrayList<SocketAddress> neighbours;
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
		this.coordinator_id = id;
		this.database = new Database();
		this.neighbours = database.getNeighbours( id );
		
		createUDPSocket( this.coordinator_id );
		
		// create and start server to handle incoming packets
		this.server = new CoordinatorServer( this );
		Thread t = new Thread( server );
		t.start();

		// threads for each neighbour to check their status
		this.lifeCheckers = new HashMap<SocketAddress, LifeChecker>();
		createLifeChecker();
	}

	// check life status for surrounding coordinators
	private void createLifeChecker() {
		for( SocketAddress address:neighbours ) {
			LifeChecker checker = new LifeChecker( address, this );
			lifeCheckers.put( address, checker);
			Thread t = new Thread( checker );
			t.start();
		}
	}

	public synchronized void removeCoordinator( SocketAddress coordinator ) {
		this.neighbours.remove( coordinator );
	}
	
	public synchronized void addListeningLockers( SocketAddress locker ) {
		this.listeningLockers.add( locker );
	}
	
	public synchronized void addWaitingLockers( SocketAddress locker ) {
		this.waitingLockers.add( locker );
	}
	
	public synchronized void removeWaitingLockers( SocketAddress locker ) {
		this.waitingLockers.remove( locker );
	}
	
	/**
	 * get ip and port from Database using coordinator's id
	 * @param id
	 * @throws UnknownHostException
	 * @throws SQLException
	 */
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
	
	public int getId() {
		return this.coordinator_id;
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
	
	public boolean takeFreeLocker( SocketAddress locker_address ) {
		boolean isFree = database.isFreeLocker( locker_address );
		if( isFree ) {
			database.takeLocker( locker_address, coordinator_id );
			return true;
		}
		return false;
	}
	
	public void insertBicycleTransection(int isRemoved, String bicycle_id, int user_id, SocketAddress locker_address){
		int locker_id = this.database.getLockerId(locker_address);
		this.database.insertBicycleTransection( isRemoved, bicycle_id, user_id, locker_id);
	}
	
	public void takeLockers(SocketAddress coordinator_address) {
		ArrayList <SocketAddress> lockers = database.getLockers( coordinator_address );
		int deadCoordiantor_id = database.getCoordiantorId( coordinator_address );
		
		LockerRequester requester = new LockerRequester( this, lockers, deadCoordiantor_id);
		
		Thread t = new Thread( requester );
		t.start();
		
	}
	
}

