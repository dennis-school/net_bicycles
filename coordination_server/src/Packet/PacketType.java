package Packet;

public enum PacketType {
	PACKET_COORDINATOR_LIFE( 0 ),
	Packet_COORDINATOR_RESPONSE( 1 ),
	Packet_LOCKER_TRANSECTION( 2 );
	
	public final int id;
	
	private PacketType( int id ) {
		this.id = id;
	}
}
