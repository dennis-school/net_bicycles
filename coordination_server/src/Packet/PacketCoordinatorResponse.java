package Packet;

public class PacketCoordinatorResponse extends Packet {

	public PacketCoordinatorResponse( ) {
		this.type = PacketType.Packet_COORDINATOR_RESPONSE;
	}

	@Override
	public byte[] toBinary( ) {
		baos.write( this.type.id );
		return baos.toByteArray( );
	}
}
