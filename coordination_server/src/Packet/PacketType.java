package Packet;

public enum PacketType {
	// packet from coordinator/locker to coordinator to check life
	PACKET_LIFE_CHECK( 0 ),
	
	// packet from coordinator to coordinator/locker to response
	Packet_RESPONSE( 1 ),
	
	// two packets only from locker
	Packet_BICYCLE_TRANSECTION( 2 ),
	Packet_CONNECTION_REQUEST( 3 ),
	
	// packet form coordinator to locker to accept/reject connection
	// or packet form locker to coordinator to accept/reject replacement
	Packet_CONNECTION_ACCEPT( 4 ),
	Packet_CONNECTION_REJICT( 5 ),
	
	// packet from coordinator to locker to replace coordinator
	Packet_REPLACE_CONNECTION_REQUEST( 6 );
	
	public final int id;
	
	private PacketType( int id ) {
		this.id = id;
	}
}
