package Packet;

public enum PacketType {
	// packet from coordinator/locker to coordinator to check life
	PACKET_LIFE_CHECK( 0 ),
	
	// packet from coordinator to coordinator/locker to response
	Packet_RESPONSE( 1 ),
	
	// two packets only from locker
	Packet_BICYCLE_TRANSECTION( 2 ),
	Packet_CONNECTION_REQUEST( 3 ),
	
	// packet only form coordinator to locker to accept/reject connection
	Packet_CONNECTION_ACCEPT( 4 ),
	Packet_CONNECTION_REJICT( 5 ),
	
	// packet from coordinator to locker to replace coordinator
	Packet_REPLACE_CONNECTION_REQUEST( 6 ),
	
	// packet from coordinator to locker to approve a transaction
	Packet_Transaction_Approved( 7 ),
	
	// pakcet form coordinator to locker/coordinator to restart
	Packet_Restart( 8 );
	
	public final int id;
	
	private PacketType( int id ) {
		this.id = id;
	}
}
