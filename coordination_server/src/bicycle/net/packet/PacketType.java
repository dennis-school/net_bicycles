package bicycle.net.packet;

public enum PacketType {
	// packet from coordinator/locker to coordinator to check life
	LIFE_CHECK( 0 ),
	
	// packet from coordinator to coordinator/locker to response
	RESPONSE( 1 ),
	
	// packets contain transaction from locker to coordinator
	BICYCLE_TRANSECTION( 2 ),
	
	// packet from locker to coordinator for connection request
	CONNECTION_REQUEST( 3 ),
	
	// packet form coordinator to locker to accept/reject connection
	CONNECTION_ACCEPT( 4 ),
	CONNECTION_REJECT( 5 ),
	
	// packet from coordinator to locker to replace coordinator
	REPLACE_CONNECTION_REQUEST( 6 ),
	
	// packet from coordinator to locker to approve a transaction
	TRANSACTION_APPROVED( 7 ),
	
	// packet form coordinator to locker/coordinator to restart
	RESTART( 8 );
	
	public final int id;
	
	private PacketType( int id ) {
		this.id = id;
	}
}
