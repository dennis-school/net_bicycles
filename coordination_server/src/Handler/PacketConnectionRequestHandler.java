package Handler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

import Packet.*;
import net_bicycles_coordination_server.Coordinator;

public class PacketConnectionRequestHandler implements PacketHandler {

	private Coordinator coordinator;
	private DatagramSocket socket;
	
	public PacketConnectionRequestHandler( Coordinator coordinator ) {
		this.coordinator = coordinator;
		this.socket = coordinator.getDatagramSocket();
	}
	
	@Override
	public void handlePacket(ByteArrayInputStream bais, SocketAddress address, int packet_id) {
		byte[] fullPacket;
		
		if( coordinator.takeFreeLocker( address ) ) {
			coordinator.addListeningLockers( address );
			PacketConnectionAccept packet = new PacketConnectionAccept();
			fullPacket = packet.toBinary( packet_id );
		}else {
			PacketConnectionReject packet = new PacketConnectionReject();
			fullPacket = packet.toBinary( packet_id );
		}
		DatagramPacket datapacket = new DatagramPacket( fullPacket, fullPacket.length, address);
		
		try {
			socket.send( datapacket );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
