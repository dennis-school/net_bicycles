package Handler;

import java.io.ByteArrayInputStream;
import java.net.SocketAddress;
import java.sql.SQLException;

import net_bicycles_coordination_server.Coordinator;

public class PacketLockerTransHandler implements PacketHandler {

	private Coordinator coordinator;
	
	public PacketLockerTransHandler( Coordinator coordinator ) {
		this.coordinator = coordinator;
	}

	@Override
	public void handlePacket(ByteArrayInputStream bais, SocketAddress address, int packet_id) {
		bais.read();
		int isRemoved = bais.read();
		int count = 0;
		String bicycle_id = null;
		while( count < 10 ) {
			bicycle_id += (char)bais.read();
		}
		int user_id = bais.read();
		this.coordinator.insertBicycleTransection(isRemoved, bicycle_id, user_id, address );
		
	}

}
