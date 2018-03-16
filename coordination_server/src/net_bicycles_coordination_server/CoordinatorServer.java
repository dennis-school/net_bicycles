package net_bicycles_coordination_server;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

import net_bicycles_coordination_server.PacketTypes.PacketType;

/**
 * handle all kinds of packet from lockers and coordinators
 * send change-coordinator-packet to notify locker ?
 * 
 * @author Luigi
 *
 */

public class CoordinatorServer implements Runnable {
	
	private Coordinator coordinator;
	private DatagramSocket socket;
	
	public CoordinatorServer(Coordinator coordinator) {
		this.coordinator = coordinator;
		this.socket = coordinator.getDatagramSocket();
	}

	// handle a unknown type package
	private void handlePacket() throws IOException, ClassNotFoundException {
		byte[] buf = new byte[1000];
		
		DatagramPacket packetDatagram = new DatagramPacket(buf, buf.length);
		socket.receive(packetDatagram);
		
        
        ByteArrayInputStream byteStream = new ByteArrayInputStream(buf);
        ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(byteStream));
        Packet packet = (Packet)ois.readObject();
        
        PacketType packetType = packet.getPacketType();
        SocketAddress address = packetDatagram.getSocketAddress();
        
        if( packetType == PacketType.Packet_CoordinatorStatus ) {
        	handleLifeSignal( (PacketCoordinatorStatus) packet, address );
        }else if( packetType == PacketType.Packet_LockerStatus ) {
        	handleLockerInfo();
        }
        
	}
	
	
	// handle the package from other coordinators
	private void handleLifeSignal( PacketCoordinatorStatus packet, SocketAddress address ) {
		int id = packet.getId();
		coordinator.checkLifeStatus( address, id );
		
	}
	
	// handle the information package form lockers
	private void handleLockerInfo() {
        // find id of locker in database by using socketAddress ?
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try {
			handlePacket();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
