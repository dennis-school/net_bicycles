package Handler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

import Packet.*;
import net_bicycles_coordination_server.Coordinator;

/** 
 * handle connection packet from locker
 * byte[] 2 byte type + 2 byte packet_id
 * @author Luigi
 *
 */

public class PacketConnectionRequestHandler implements PacketHandler {

	private Coordinator coordinator;
	private DatagramSocket socket;
	
	public PacketConnectionRequestHandler( Coordinator coordinator ) {
		this.coordinator = coordinator;
		this.socket = coordinator.getDatagramSocket();
	}
	
	@Override
	public void handlePacket(ByteArrayInputStream bais, SocketAddress locker_address, int packet_id) {
		byte[] fullPacket = null;
		
		if( coordinator.isFreeLocker( locker_address ) ) {
			
			coordinator.addListeningLockers( locker_address );
			PacketConnectionAccept packet = new PacketConnectionAccept();
			fullPacket = packet.toBinary( packet_id );
			
		}else {
			if( this.coordinator.inWaitingList( locker_address ) ){
				// handle lockers of dead coordinator
				this.coordinator.removeWaitingLockers( locker_address );
				this.coordinator.addListeningLockers( locker_address );
				PacketConnectionAccept packet = new PacketConnectionAccept();
				fullPacket = packet.toBinary( packet_id );
			}else {
				PacketConnectionReject packet = new PacketConnectionReject();
				fullPacket = packet.toBinary( packet_id );
			}
		}
		DatagramPacket datapacket = new DatagramPacket( fullPacket, fullPacket.length, locker_address);
		
		try {
			socket.send( datapacket );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
