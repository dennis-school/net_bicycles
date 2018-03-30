package net_bicycles_coordination_server;

import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Timer;

import Database.Database;
import Packet.PacketReplaceConnectionRequest;

/**
 * Coordinator that handle message from Locker, modify Database, communicate with each other
 * 
 * @author Luigi
 *
 */

public class Coordinator {

	// id of Coordinator in Database
	private int coordinator_id;
	
	private DatagramSocket socket;
	private SocketAddress address;
	
	// Locker that send packet
	private ArrayList<SocketAddress> listeningLockers;
	
	// Locker that may send packet in furture
	private ArrayList<SocketAddress> waitingLockers;
	
	// two closest coordinators around it
	private ActionListener aliveTimerListener;
	private Timer aliveTimer;
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
	
	public Coordinator(int id) {
		this.coordinator_id = id;
		this.database = new Database();
		this.listeningLockers = new ArrayList<SocketAddress>();
		this.waitingLockers = new ArrayList<SocketAddress>();
		
		createUDPSocket( this.coordinator_id );
		
		// create and start server to handle incoming packets
		this.server = new CoordinatorServer( this );
		Thread t = new Thread( server );
		t.start();

		this.aliveTimerListener = ( ev -> {
			this.aliveTimer.stop();
			this.neighbours = database.getNeighbours( id );
			System.out.println( "Coordinator" + coordinator_id + " try to find neighbours");
			
			// threads for each neighbour to check their status
			this.lifeCheckers = new HashMap<SocketAddress, LifeChecker>();
			createLifeChecker();
		} );
		this.aliveTimer = new Timer( 10000, aliveTimerListener );
		this.aliveTimer.start();
		
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
	 * get ip and port from Database using coordinator's id and create socketAddress from them
	 * @param id
	 * @throws UnknownHostException
	 * @throws SQLException
	 */
	private void createUDPSocket(int id) {
		this.address = new InetSocketAddress( database.getCoordinatorAddress(id), database.getCoordiantorPort(id) );
		try {
			this.socket = new DatagramSocket( address );
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	/**
	 * Check whether the locker with locker_address is free, take it if it is free
	 * @param locker_address
	 * @return
	 */
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
	
	/**
	 * try to take over lockers from a Coordinator with coordinator_address
	 * @param coordinator_address
	 */
	public void takeLockers(SocketAddress coordinator_address) {
		ArrayList <SocketAddress> lockers = database.getLockers( coordinator_address );
		int deadCoordiantor_id = database.getCoordiantorId( coordinator_address );
		
		LockerRequester requester = new LockerRequester( this, lockers, deadCoordiantor_id);
		
		Thread t = new Thread( requester );
		t.start();
		
	}
	
	public boolean inWaitingList( SocketAddress locker ) {
		return this.waitingLockers.contains( locker );
	}
	
}

