package net_bicycles_coordination_server;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import javax.swing.Timer;

import Packet.PacketLifeCheck;

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
			System.out.println( "Find dead coordinator " + socketAddress );
			this.aliveTimer.stop();
			//this.coordinator.removeCoordinator( port );
			takeOverLockers( );
		} );
		this.aliveTimer = new Timer( 5000, aliveTimerListener );
		this.aliveTimer.start();
	}
	
	public void takeOverLockers( ) {
		this.coordinator.takeLockers( socketAddress );
	}
	


	private DatagramPacket buildPacket( ) {
		PacketLifeCheck packet = new PacketLifeCheck();
		byte[] fullPacket = packet.toBinary( coordinator.getId() );
		return new DatagramPacket(fullPacket, fullPacket.length, this.socketAddress );
	}
	
	// Notify that it is alive
	public void alive() {
		this.aliveTimer.restart( );
	}

	@Override
	public void run() {
		DatagramPacket packet = buildPacket( );
		while( this.aliveTimer.isRunning( )  ) {
			
			try {
				socket.send( packet );
				//System.out.println( socket.getLocalPort() + ": check " + packet.getSocketAddress() );
				Thread.sleep( 1000 );
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
	}

}
