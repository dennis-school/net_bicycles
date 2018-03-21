package Handler;

import java.net.SocketAddress;

import Package.Packet;
import net_bicycles_coordination_server.Coordinator;

public class PacketLockerTransHandler implements PacketHandler {

	private Coordinator coordinator;
	
	public PacketLockerTransHandler( Coordinator coordinator ) {
		this.coordinator = coordinator;
	}

	@Override
	public void handlePacket(Packet p, SocketAddress address) {
		// TODO Auto-generated method stub
		
	}

}
