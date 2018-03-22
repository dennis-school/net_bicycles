package Handler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

import Packet.PacketCoordinatorResponse;

public class PacketCoordinatorLifeHandler implements PacketHandler {
	private DatagramSocket socket;
	
	public PacketCoordinatorLifeHandler( DatagramSocket socket ) {
		this.socket = socket;
	}

	private DatagramPacket buildPacket(SocketAddress address) {
		PacketCoordinatorResponse packet = new PacketCoordinatorResponse( );
		byte[] fullPacket = packet.toBinary();
		return new DatagramPacket( fullPacket, fullPacket.length, address );
	}
	
	@Override
	public void handlePacket(ByteArrayInputStream bais, SocketAddress address) {
		DatagramPacket packet = buildPacket( address );
		try {
			socket.send( packet );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}