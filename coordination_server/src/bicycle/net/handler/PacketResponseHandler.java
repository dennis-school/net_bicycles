package bicycle.net.handler;

import java.net.SocketAddress;

import bicycle.Coordinator;
import bicycle.LifeChecker;
import bicycle.io.BEInputStream;

/**
 * reset the timer for life checker of coordinator with specified socket address
 * @author Luigi
 *
 */
public class PacketResponseHandler implements PacketHandler {

	private Coordinator coordinator;
	
	public PacketResponseHandler( Coordinator coordinator ) {
		this.coordinator = coordinator;
	}
	
	@Override
	public void handlePacket(BEInputStream in, SocketAddress address, int packet_id) {
		LifeChecker checker = this.coordinator.getLifeChecker( address );
		checker.alive();
	}

}
