package bicycle.net.handler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

import bicycle.Coordinator;
import bicycle.io.BEInputStream;
import bicycle.net.packet.PacketConnectionAccept;
import bicycle.net.packet.PacketConnectionReject;

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
	
	/**
	 * accept the connection if the locker is free in database
	 * accept the connection if the locker is in waiting list
	 * reject otherwise
	 */
	@Override
	public void handlePacket( BEInputStream in, SocketAddress lockerAddress, int packet_id) {
		byte[] fullPacket = null;
		
		System.out.println("Coordinator" + coordinator.getId() + " receive connection request from Locker at " + lockerAddress );
		
		if( coordinator.isFreeLocker( lockerAddress ) ) {
			
			coordinator.addListeningLockers( lockerAddress );
			PacketConnectionAccept packet = new PacketConnectionAccept();
			fullPacket = packet.toBinary( packet_id );
			
		}else {
			if( this.coordinator.inWaitingList( lockerAddress ) ){
				// handle lockers of dead coordinator
				this.coordinator.removeWaitingLockers( lockerAddress );
				this.coordinator.addListeningLockers( lockerAddress );
				PacketConnectionAccept packet = new PacketConnectionAccept();
				fullPacket = packet.toBinary( packet_id );
			}else {
				PacketConnectionReject packet = new PacketConnectionReject();
				fullPacket = packet.toBinary( packet_id );
			}
		}
		DatagramPacket datapacket = new DatagramPacket( fullPacket, fullPacket.length, lockerAddress);
		
		try {
			socket.send( datapacket );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
