package bicycle.net.handler;

import java.net.SocketAddress;

import bicycle.Coordinator;
import bicycle.io.BEInputStream;

public class PacketConnectionRejectHandler implements PacketHandler {

	private Coordinator coordinator;
	
	public PacketConnectionRejectHandler( Coordinator coordinator ) {
		this.coordinator = coordinator;
	}
	
	@Override
	public void handlePacket( BEInputStream in, SocketAddress address, int packet_id) {
		this.coordinator.removeWaitingLockers( address );
	}

}
