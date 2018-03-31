package Handler;

import java.io.ByteArrayInputStream;
import java.net.SocketAddress;
import java.sql.SQLException;

import net_bicycles_coordination_server.Coordinator;

/**
 * packet from Locker to Coordinator
 * packet: byte[] 
 * 	2 byte int for type,
 * 	2 byte int for packet_id,
 * 	1 byte char for taken/removed
 * 	10 byte char for bicycle_id
 * 	4 byte int for user_id
 * @author Luigi
 *
 */
public class PacketLockerTransHandler implements PacketHandler {

	private Coordinator coordinator;
	
	public PacketLockerTransHandler( Coordinator coordinator ) {
		this.coordinator = coordinator;
	}

	@Override
	public void handlePacket(ByteArrayInputStream bais, SocketAddress address, int packetID) {
		
		
		int type = ((bais.read()&0xFF)<<8) | (bais.read()&0xFF);
		int packet_id = ((bais.read()&0xFF)<<8) | (bais.read()&0xFF);
		int isRemoved = bais.read() - '0';
		int count = 0;
		String bicycle_id = null;
		while( count < 10 ) {
			bicycle_id += (char)bais.read();
			count++;
		}
		int user_id = ((bais.read()&0xFF)<<24) | ((bais.read()&0xFF)<<16) | ((bais.read()&0xFF)<<8) | (bais.read()&0xFF);
		
		System.out.println( "Coordiantor" + coordinator.getId() + " receive a transaction: bike " + bicycle_id + (isRemoved == 0 ? " remove":" return") + " by " + user_id );
		
		this.coordinator.insertBicycleTransection(isRemoved, bicycle_id, user_id, address );
		
	}

}
