package Handler;

import java.net.SocketAddress;

import Package.Packet;
import net_bicycles_coordination_server.*;

public class PacketCoordinatorResponseHandler implements PacketHandler {

	private Coordinator coordinator;
	
	public PacketCoordinatorResponseHandler( Coordinator coordinator ) {
		this.coordinator = coordinator;
	}
	
	@Override
	public void handlePacket(Packet p, SocketAddress address) {
		LifeChecker checker = this.coordinator.getLifeChecker( address );
		checker.alive();
	}

}
