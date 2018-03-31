package net_bicycles_coordination_server;

import java.awt.event.ActionListener;
import java.io.IOException;
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
import Packet.PacketConnectionReject;
import Packet.PacketReplaceConnectionRequest;
import Packet.PacketRestart;

/**
 * Coordinator that handle message from Locker, modify Database, communicate with each other
 * 
 * @author Luigi
 *
 */

public class Coordinator {

	// id of Coordinator in Database
	private int coordinator_id;
	
	// socket
	private DatagramSocket socket;
	private SocketAddress address;
	private int port;
	
	// Locker that currently send packet
	private ArrayList<SocketAddress> listeningLockers;
	
	// Locker that may send packet in future
	private ArrayList<SocketAddress> waitingLockers;
	
	private ActionListener aliveTimerListener;
	private Timer aliveTimer;

	// two closest coordinators around it
	private ArrayList<SocketAddress> neighbours;
	
	// two thread that check neighbours' life
	private HashMap<SocketAddress, LifeChecker> lifeCheckers;
	
	// server handler packets
	CoordinatorServer server;
	
	// database that store info
	private Database database;
	
	public Coordinator() {
		try {
			this.socket = new DatagramSocket();
			this.address = socket.getLocalSocketAddress();
			this.port = socket.getLocalPort();
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

		// check whether the system is already started
		if( this.database.noFreeLocker() ) {
			// if the system is started, restart the system for new incoming coordiantor
			restartWholeSystem();
		}else {
			initChecker();
		}
		
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
			this.address = this.socket.getLocalSocketAddress();
			this.port = this.socket.getLocalPort();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * restart all coordinators and all lockers in the system
	 */
	private void restartWholeSystem() {
		ArrayList<SocketAddress> allAddress = this.database.getAllAddress();
		this.database.restartLocker();
		
		PacketRestart packet = new PacketRestart();
		byte[] fullPacket = packet.toBinary( coordinator_id );
		for( SocketAddress address : allAddress ) {
			DatagramPacket datapacket = new DatagramPacket( fullPacket, fullPacket.length, address);
			try {
				socket.send( datapacket );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		restart();
	}
	
	/**
	 *  create a timer, wait for locker to connect to coordinator
	 */
	private void initChecker() {
		this.aliveTimerListener = ( ev -> {
			this.aliveTimer.stop();
			this.neighbours = database.getNeighbours( this.coordinator_id );
			System.out.println( "Coordinator" + coordinator_id + " try to find neighbours");
			
			// threads for each neighbours to check their status
			this.lifeCheckers = new HashMap<SocketAddress, LifeChecker>();
			createLifeChecker();
		} );
		this.aliveTimer = new Timer( 10000, aliveTimerListener );
		this.aliveTimer.start();
	}
	
	/**
	 *  check life checker for all surrounding coordinators
	 */
	private void createLifeChecker() {
		for( SocketAddress address:neighbours ) {
			LifeChecker checker = new LifeChecker( address, this );
			lifeCheckers.put( address, checker);
			Thread t = new Thread( checker );
			t.start();
		}
	}
	
	/**
	 * restart coordinator itself
	 */
	public void restart() {
		this.listeningLockers.clear();
		this.waitingLockers.clear();
		initChecker();
	}

	public synchronized void removeCoordinator( SocketAddress coordinator ) {
		this.neighbours.remove( coordinator );
	}
	
	public synchronized void addListeningLockers( SocketAddress locker_address ) {
		this.database.takeLocker( locker_address, coordinator_id );
		this.listeningLockers.add( locker_address );
	}
	
	public synchronized void addWaitingLockers( SocketAddress locker ) {
		this.waitingLockers.add( locker );
	}
	
	public synchronized void removeWaitingLockers( SocketAddress locker ) {
		this.waitingLockers.remove( locker );
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
	
	public int getPort() {
		return this.port;
	}
	
	/**
	 * whether the locker with specified address is in the waiting list
	 * @param locker
	 * @return
	 */
	public boolean inWaitingList( SocketAddress locker ) {
		return this.waitingLockers.contains( locker );
	}
	
	/**
	 * get the life checker of a coordinator with specified socket address of that coordinator
	 * @param address
	 * @return
	 */
	public LifeChecker getLifeChecker( SocketAddress address ) {
		return this.lifeCheckers.get(address);
	}
	
	/**
	 * whether the locker with specified socket address is free
	 * @param locker_address
	 * @return
	 */
	public boolean isFreeLocker( SocketAddress locker_address ) {
		boolean isFree = database.isFreeLocker( locker_address );
		return isFree;
	}
	
	/**
	 * insert transaction of a bicycle into database
	 * @param isRemoved
	 * @param bicycle_id
	 * @param user_id
	 * @param locker_address
	 */
	public void insertBicycleTransection(int isRemoved, String bicycle_id, int user_id, SocketAddress locker_address){
		int locker_id = this.database.getLockerId(locker_address);
		this.database.insertBicycleTransection( isRemoved, bicycle_id, user_id, locker_id);
	}
	
	/**
	 * notified the death of coordinator to all lockers from that coordinator with specified socket address
	 * @param coordinator_address
	 */
	public void takeLockers(SocketAddress coordinator_address) {
		ArrayList <SocketAddress> lockers = database.getLockers( coordinator_address );
		int deadCoordiantor_id = database.getCoordiantorId( coordinator_address );
		
		LockerRequester requester = new LockerRequester( this, lockers, deadCoordiantor_id);
		
		Thread t = new Thread( requester );
		t.start();
		
	}
	
}

