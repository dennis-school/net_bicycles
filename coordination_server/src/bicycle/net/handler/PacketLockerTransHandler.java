package bicycle.net.handler;

import java.io.IOException;
import java.net.SocketAddress;

import bicycle.Coordinator;
import bicycle.io.BEInputStream;

/**
 * packet from Locker to Coordinator
 * packet: byte[] 
 * 	2 byte int for type,
 * 	2 byte int for packet_id,
 * 	1 byte char for taken/removed
 * 	10 byte char for bicycle_id
 * 	4 byte int for user_id
 * 
 * extract information from input and save it as transaction into database
 * @author Luigi
 *
 */
public class PacketLockerTransHandler implements PacketHandler {

	private Coordinator coordinator;
	
	public PacketLockerTransHandler( Coordinator coordinator ) {
		this.coordinator = coordinator;
	}

	@Override
	public void handlePacket( BEInputStream in, SocketAddress address, int packet_id) throws IOException {
		int isRemoved = in.readUint8( ) - '0';
		byte[] strData = new byte[10];
		in.ensureRead( strData, 0, 10 );
		String bicycleId = new String( strData );
		int userId = in.readUint32( );
		
		System.out.println( "Coordinator" + coordinator.getId() + " receive a transaction: bike " + bicycleId + (isRemoved == 0 ? " remove":" return") + " by " + userId );
		
		this.coordinator.insertBicycleTransection(isRemoved, bicycleId, userId, address );
	}

}
