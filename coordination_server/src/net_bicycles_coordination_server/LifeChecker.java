package net_bicycles_coordination_server;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

import Package.PacketCoordinatorLife;

public class LifeChecker implements Runnable {

	private Coordinator coordinator;
	private SocketAddress address;
	private DatagramSocket socket;
	
	private boolean isAlive;
	private boolean active;
	
	public LifeChecker(SocketAddress address, Coordinator coordinator) {
		this.coordinator = coordinator;
		this.address = address;
		this.socket = coordinator.getDatagramSocket();
		this.isAlive = false;
		this.active = true;
	}

	private DatagramPacket buildPacket(SocketAddress address) {
		// maybe should add more information in packet?
		PacketCoordinatorLife packet = new PacketCoordinatorLife( );
		
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream(4000);
        ObjectOutputStream outputStream;
		try {
			outputStream = new ObjectOutputStream(new BufferedOutputStream(byteStream));
	        outputStream.flush();
	        outputStream.writeObject(packet);
	        outputStream.flush();
	        outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        byte[] bytes =  byteStream.toByteArray();
        
		return new DatagramPacket(bytes, bytes.length, address);
	}
	
	public void takeOverLockers( ) {
		// first use address to find coordinator in database
		// + tell database the death of coordinator
		// second find lockers of that coordinator
		// third send message to lockers to change coordinator
	}
	
	public void alive() {
		this.isAlive = true;
	}
	
	@Override
	public void run() {
		
		while( this.active ) {
			this.isAlive = false;
			DatagramPacket packet = buildPacket( this.address );
			try {
				socket.send( packet );
				// server will handle response packet
				
				Thread.sleep( 5000 );
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// if server didn't get response packet within 5s, we assume that coordinator die
			if( !isAlive ) {
				this.coordinator.removeCoordinator( this.address );
				takeOverLockers( );
				this.active = false;
			}
		}
		
	}

}
