package net_bicycles_coordination_server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.HashMap;

import Handler.PacketCoordinatorLifeHandler;
import Handler.PacketCoordinatorResponseHandler;
import Handler.PacketHandler;
import Handler.PacketLockerTransHandler;
import Packet.PacketType;

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
	
	private HashMap<Integer, PacketHandler> packetHandlers;
	
	public CoordinatorServer(Coordinator coordinator) {
		this.coordinator = coordinator;
		this.socket = coordinator.getDatagramSocket();
		this.packetHandlers = new HashMap<Integer, PacketHandler> ();
		
		buildPacketHandlers();
	}

	private void buildPacketHandlers() {
		this.packetHandlers.put( PacketType.PACKET_COORDINATOR_LIFE.id, new PacketCoordinatorLifeHandler(this.socket) );
		this.packetHandlers.put(PacketType.Packet_COORDINATOR_RESPONSE.id, new PacketCoordinatorResponseHandler(this.coordinator) );
		this.packetHandlers.put( PacketType.Packet_LOCKER_TRANSECTION.id, new PacketLockerTransHandler(this.coordinator) );
	}

	// handle a unknown type package
	private void handlePacket() throws IOException, ClassNotFoundException {
		byte[] buf = new byte[1000];
		
		DatagramPacket packetDatagram = new DatagramPacket(buf, buf.length);
		socket.receive(packetDatagram);

        SocketAddress address = packetDatagram.getSocketAddress();
        
        ByteArrayInputStream bais = new ByteArrayInputStream(buf);
        
        int type = bais.read();
        
        PacketHandler packetHandler = packetHandlers.get( type );
        packetHandler.handlePacket( bais, address );
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
