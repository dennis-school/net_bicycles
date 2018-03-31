package Packet;

public enum PacketType {
	// packet from coordinator/locker to coordinator to check life
	PACKET_LIFE_CHECK( 0 ),
	
	// packet from coordinator to coordinator/locker to response
	Packet_RESPONSE( 1 ),
	
	// packets contain transaction from locker to coordinator
	Packet_BICYCLE_TRANSECTION( 2 ),
	
	// packet from locker to coordinator for connection request
	Packet_CONNECTION_REQUEST( 3 ),
	
	// packet form coordinator to locker to accept/reject connection
	Packet_CONNECTION_ACCEPT( 4 ),
	Packet_CONNECTION_REJICT( 5 ),
	
	// packet from coordinator to locker to replace coordinator
	Packet_REPLACE_CONNECTION_REQUEST( 6 ),
	
	// packet from coordinator to locker to approve a transaction
	Packet_Transaction_Approved( 7 ),
	
	// packet form coordinator to locker/coordinator to restart
	Packet_Restart( 8 );
	
	public final int id;
	
	private PacketType( int id ) {
		this.id = id;
	}
}
