package bicycle.net.handler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

import bicycle.Coordinator;
import bicycle.io.BEInputStream;
import bicycle.net.packet.PacketResponse;

/**
 * send a Response packet to the socket address indicates the life
 * @author Luigi
 *
 */
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
	public void handlePacket( BEInputStream in, SocketAddress coordinatorAddress, int packet_id) {
		DatagramPacket packet = buildPacket( coordinatorAddress, packet_id );
		try {
			socket.send( packet );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}