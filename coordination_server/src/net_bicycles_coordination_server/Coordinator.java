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

	private DatagramSocket socket;
	private InetAddress localAddress;
	private int localPort;
	
	private ArrayList<SocketAddress> lockersAddress; //why we need this?

	private HashMap<SocketAddress, Coordinator > coordinatorsStatus;
	
	// use for store and extract info from net_bicycle Database
	private Database database;
	
	public static void main( String... args ) throws UnknownHostException {
		Coordinator coordinator = new Coordinator();
		coordinator.createUDPSocket();
		coordinator.start();
	}
	
	public Coordinator() {
		
	}
	
	private void createUDPSocket() {
		try {
			this.socket = new DatagramSocket();
			this.localAddress = socket.getLocalAddress();
			this.localPort = socket.getLocalPort();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void start() {
		StatusNotifier notifier = new StatusNotifier( this );
		CoordinatorServer server = new CoordinatorServer( this );
		
		Thread t1 = new Thread( notifier );
		Thread t2 = new Thread( server );
		
		t1.start();
		t2.start();
	}
	
	public DatagramSocket getDatagramSocket() {
		return this.socket;
	}
	
	public InetAddress getInetAddress() {
		return this.localAddress;
	}
	
	public int getPort() {
		return this.localPort;
	}
	
	public void lockerJoin( SocketAddress newLocker ) {
		lockersAddress.add( newLocker );
	}
	
	public void insertBicycleTransection() {
		
	}
	
	public void takeOverLockers( SocketAddress address ) {
		// first use address to find coordinator in database
		// + tell database the death of coordinator
		// second find lockers of that coordinator
		// third send message to lockers to change coordinator
	}

	public void checkLifeStatus(SocketAddress address, int id) {
		// check whether the coordinator is work
		if( coordinatorsStatus.containsValue(address) ) {
			takeOverLockers( address );
		}
		
	}
}

