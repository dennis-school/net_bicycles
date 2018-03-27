package Handler;

import java.io.ByteArrayInputStream;
import java.net.SocketAddress;

import net_bicycles_coordination_server.*;

public class PacketResponseHandler implements PacketHandler {

	private Coordinator coordinator;
	
	public PacketResponseHandler( Coordinator coordinator ) {
		this.coordinator = coordinator;
	}
	
	@Override
	public void handlePacket(ByteArrayInputStream bais, SocketAddress address, int packet_id) {
		LifeChecker checker = this.coordinator.getLifeChecker( address );
		checker.alive();
	}

}
