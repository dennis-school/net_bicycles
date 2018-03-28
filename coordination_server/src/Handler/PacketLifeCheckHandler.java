package Handler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

import Packet.PacketResponse;
import net_bicycles_coordination_server.Coordinator;

public class PacketLifeCheckHandler implements PacketHandler {
	private DatagramSocket socket;
	
	public PacketLifeCheckHandler( Coordinator coordinator ) {
		this.socket = coordinator.getDatagramSocket();
	}

	private DatagramPacket buildPacket(SocketAddress address, int packet_id) {
		PacketResponse packet = new PacketResponse( );
		byte[] fullPacket = packet.toBinary( packet_id );
		return new DatagramPacket( fullPacket, fullPacket.length, address );
	}
	
	@Override
	public void handlePacket(ByteArrayInputStream bais, SocketAddress address, int packet_id) {
		DatagramPacket packet = buildPacket( address, packet_id );
		try {
			socket.send( packet );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}