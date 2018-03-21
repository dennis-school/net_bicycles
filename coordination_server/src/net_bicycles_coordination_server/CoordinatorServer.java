package net_bicycles_coordination_server;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.HashMap;

import Handler.PacketCoordinatorLifeHandler;
import Handler.PacketCoordinatorResponseHandler;
import Handler.PacketHandler;
import Handler.PacketLockerTransHandler;
import Package.Packet;
import Package.PacketTypes.PacketType;

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
	
	private HashMap<PacketType, PacketHandler> packetHandlers;
	
	public CoordinatorServer(Coordinator coordinator) {
		this.coordinator = coordinator;
		this.socket = coordinator.getDatagramSocket();
		this.packetHandlers = new HashMap<PacketType, PacketHandler> ();
		
		buildPacketHandlers();
	}

	private void buildPacketHandlers() {
		this.packetHandlers.put( PacketType.Packet_Coordinator_Life, new PacketCoordinatorLifeHandler(this.socket) );
		this.packetHandlers.put(PacketType.Packet_Coordinator_Response, new PacketCoordinatorResponseHandler(this.coordinator) );
		this.packetHandlers.put( PacketType.Packet_Locker_Transection, new PacketLockerTransHandler(this.coordinator) );
	}

	// handle a unknown type package
	private void handlePacket() throws IOException, ClassNotFoundException {
		byte[] buf = new byte[1000];
		
		DatagramPacket packetDatagram = new DatagramPacket(buf, buf.length);
		socket.receive(packetDatagram);

        SocketAddress address = packetDatagram.getSocketAddress();
        
        ByteArrayInputStream byteStream = new ByteArrayInputStream(buf);
        ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(byteStream));
        Packet packet = (Packet)ois.readObject();
        
        PacketType packetType = packet.getPacketType();
        
        PacketHandler packetHandler = packetHandlers.get( packetType );
        packetHandler.handlePacket( packet, address );
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		while( true ) {

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

}
