package Handler;

import java.io.ByteArrayInputStream;
import java.net.SocketAddress;

import net_bicycles_coordination_server.Coordinator;

public class PacketRestartHandler implements PacketHandler {

	private Coordinator coordinator;
	
	public PacketRestartHandler( Coordinator coordinator ) {
		this.coordinator = coordinator;
	}
	
	@Override
	public void handlePacket(ByteArrayInputStream bais, SocketAddress address, int pakcet_id) {
		this.coordinator.restart();
	}

}
