package net_bicycles_coordination_server;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import javax.swing.Timer;

import Packet.PacketCoordinatorLife;

public class LifeChecker implements Runnable {
	private Coordinator coordinator;
	private SocketAddress socketAddress;
	private DatagramSocket socket;
	
	private ActionListener aliveTimerListener;
	private Timer aliveTimer;
	
	public LifeChecker(SocketAddress socketAddress, Coordinator coordinator) {
		this.coordinator = coordinator;
		this.socketAddress = socketAddress;
		this.socket = coordinator.getDatagramSocket();
		this.aliveTimerListener = ( ev -> {
			System.out.println( "No! I'm dead!" );
			this.aliveTimer.stop();
			//this.coordinator.removeCoordinator( port );
			takeOverLockers( );
		} );
		this.aliveTimer = new Timer( 5000, aliveTimerListener );
		this.aliveTimer.start();
	}
	
	public void takeOverLockers( ) {
		// first use address to find coordinator in database
		// + tell database the death of coordinator
		// second find lockers of that coordinator
		// third send message to lockers to change coordinator
	}
	


	private DatagramPacket buildPacket( ) {
		PacketCoordinatorLife packet = new PacketCoordinatorLife();
		byte[] fullPacket = packet.toBinary();
		return new DatagramPacket(fullPacket, fullPacket.length, this.socketAddress );
	}
	
	// Notify that it is alive
	public void alive() {
		this.aliveTimer.restart( );
	}

	@Override
	public void run() {
		while( this.aliveTimer.isRunning( )  ) {
			
			DatagramPacket packet = buildPacket( );
			try {
				socket.send( packet );
				System.out.println( socket.getLocalPort() + ": Hi " + packet.getSocketAddress() + ", are you alive?" );
				Thread.sleep( 1000 );
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
	}

}
