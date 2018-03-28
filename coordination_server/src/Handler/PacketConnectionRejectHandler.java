package Handler;

import java.io.ByteArrayInputStream;
import java.net.SocketAddress;

import net_bicycles_coordination_server.Coordinator;

public class PacketConnectionRejectHandler implements PacketHandler {

	private Coordinator coordinator;
	
	public PacketConnectionRejectHandler( Coordinator coordinator ) {
		this.coordinator = coordinator;
	}
	
	@Override
	public void handlePacket(ByteArrayInputStream bais, SocketAddress address, int pakcet_id) {
		this.coordinator.removeWaitingLockers( address );
	}

}
