package bicycle;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.HashMap;

import bicycle.io.BEInputStream;
import bicycle.net.handler.*;
import bicycle.net.packet.*;

/**
 * handle all kinds of packet from lockers and coordinators
 * 
 * @author Luigi
 *
 */

public class CoordinatorServer implements Runnable {
	
	private Coordinator coordinator;
	private DatagramSocket socket;
	
	// hash map with packet's type and packet's handler
	private HashMap<Integer, PacketHandler> packetHandlers;
	
	public CoordinatorServer(Coordinator coordinator) {
		this.coordinator = coordinator;
		this.socket = coordinator.getDatagramSocket();
		this.packetHandlers = new HashMap<Integer, PacketHandler> ();
		
		buildPacketHandlers();
	}

	private void buildPacketHandlers() {
		this.packetHandlers.put( PacketType.LIFE_CHECK.id, new PacketLifeCheckHandler(this.coordinator) );
		this.packetHandlers.put( PacketType.RESPONSE.id, new PacketResponseHandler(this.coordinator) );
		this.packetHandlers.put( PacketType.BICYCLE_TRANSECTION.id, new PacketLockerTransHandler(this.coordinator) );
		this.packetHandlers.put( PacketType.CONNECTION_REQUEST.id, new PacketConnectionRequestHandler(this.coordinator) );
		this.packetHandlers.put( PacketType.CONNECTION_ACCEPT.id, new PacketConnectionAcceptHandler(this.coordinator) );
		this.packetHandlers.put( PacketType.CONNECTION_REJECT.id, new PacketConnectionRejectHandler(this.coordinator) );
		this.packetHandlers.put( PacketType.RESTART.id, new PacketRestartHandler(this.coordinator) );
	}

	/**
	 *  receive a unknown type package and give it to correct packet handler depending on its packet type
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void handlePacket() throws IOException, ClassNotFoundException {
		byte[] buf = new byte[1000];
		
		DatagramPacket packetDatagram = new DatagramPacket(buf, buf.length);
		socket.receive(packetDatagram);
		
		System.out.println( "Received a packet!" );

        SocketAddress address = packetDatagram.getSocketAddress();
        
        ByteArrayInputStream bais = new ByteArrayInputStream(buf);
        BEInputStream beIn = new BEInputStream( bais );
        
        // read 2 bytes int
        int type = beIn.readUint16( );
        int packet_id = beIn.readUint16( );
        
        if ( !packetHandlers.containsKey( type ) ) {
        	System.out.printf( "Packet handler for packet type %d not found\n", type );
        } else {
            PacketHandler packetHandler = packetHandlers.get( type );
            packetHandler.handlePacket( beIn, address, packet_id );
        }
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		while( true ) {

			try {
				handlePacket();
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}

}
