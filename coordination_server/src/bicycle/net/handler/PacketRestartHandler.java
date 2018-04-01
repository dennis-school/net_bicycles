package bicycle.net.handler;

import java.net.SocketAddress;

import bicycle.Coordinator;
import bicycle.io.BEInputStream;
/**
 * restart the coordinator itself
 * @author Luigi
 *
 */
public class PacketRestartHandler implements PacketHandler {

	private Coordinator coordinator;
	
	public PacketRestartHandler( Coordinator coordinator ) {
		this.coordinator = coordinator;
	}
	
	@Override
	public void handlePacket(BEInputStream in, SocketAddress address, int packet_id) {
		System.out.println( "Coordiantor" + coordinator.getId() + " going to restart" );
		this.coordinator.restart();
	}

}
