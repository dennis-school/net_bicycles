package Handler;

import java.io.ByteArrayInputStream;
import java.net.SocketAddress;

import net_bicycles_coordination_server.*;

public class PacketCoordinatorResponseHandler implements PacketHandler {

	private Coordinator coordinator;
	
	public PacketCoordinatorResponseHandler( Coordinator coordinator ) {
		this.coordinator = coordinator;
	}
	
	@Override
	public void handlePacket(ByteArrayInputStream bais, SocketAddress address) {
		LifeChecker checker = this.coordinator.getLifeChecker( address );
		checker.alive();
	}

}
