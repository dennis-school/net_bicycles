package net_bicycles_coordination_server;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.ArrayList;


/**
 * a thread periodically send live signal to other coordinators
 * tell the coordinator is alive
 * 
 * @author Luigi
 *
 */

public class StatusNotifier implements Runnable{
	
	private Coordinator coordinator;
	private DatagramSocket socket;
	private int lifetime;
	
	public StatusNotifier(Coordinator coordinator) {
		this.coordinator = coordinator;
		this.socket = coordinator.getDatagramSocket();
		this.lifetime = 0;
	}
	
	private DatagramPacket buildPacket(SocketAddress address) {
		// maybe should add more information in packet?
		PacketCoordinatorStatus packet = new PacketCoordinatorStatus( lifetime );
		
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
	
	@Override
	public void run() {
		// first get a list of coordinators from Database
		// ex. socketAddress
		ArrayList <SocketAddress> addresses = null;
		
		// second sort them due to server's physical address and choose five
		

		// send signal to those coordinators
		for( SocketAddress address : addresses ) {
			lifetime = lifetime + 1;
			DatagramPacket packet = buildPacket( address );
			try {
				socket.send( packet );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		
		// sleep some times
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
