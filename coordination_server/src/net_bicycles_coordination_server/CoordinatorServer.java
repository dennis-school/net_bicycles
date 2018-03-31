package net_bicycles_coordination_server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.HashMap;

import Handler.*;
import Packet.*;

/**
 * handle all kinds of packet from lockers and coordinators
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
		this.packetHandlers.put( PacketType.PACKET_LIFE_CHECK.id, new PacketLifeCheckHandler(this.coordinator) );
		this.packetHandlers.put( PacketType.Packet_RESPONSE.id, new PacketResponseHandler(this.coordinator) );
		this.packetHandlers.put( PacketType.Packet_BICYCLE_TRANSECTION.id, new PacketLockerTransHandler(this.coordinator) );
		this.packetHandlers.put( PacketType.Packet_CONNECTION_REQUEST.id, new PacketConnectionRequestHandler(this.coordinator) );
		this.packetHandlers.put( PacketType.Packet_CONNECTION_ACCEPT.id, new PacketConnectionAcceptHandler(this.coordinator) );
		this.packetHandlers.put( PacketType.Packet_CONNECTION_REJICT.id, new PacketConnectionRejectHandler(this.coordinator) );
		this.packetHandlers.put( PacketType.Packet_Restart.id, new PacketRestartHandler(this.coordinator) );
	}

	// handle a unknown type package
	private void handlePacket() throws IOException, ClassNotFoundException {
		byte[] buf = new byte[1000];
		
		DatagramPacket packetDatagram = new DatagramPacket(buf, buf.length);
		socket.receive(packetDatagram);
		
		System.out.println( " receive!" );

        SocketAddress address = packetDatagram.getSocketAddress();
        
        ByteArrayInputStream bais = new ByteArrayInputStream(buf);
        
        // 2 bytes int
        int type = (bais.read()&0xFF) | ((bais.read()&0xFF)<<8);
        int packet_id = (bais.read()&0xFF) | ((bais.read()&0xFF)<<8);
        
        PacketHandler packetHandler = packetHandlers.get( type );
        packetHandler.handlePacket( bais, address, packet_id );
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
