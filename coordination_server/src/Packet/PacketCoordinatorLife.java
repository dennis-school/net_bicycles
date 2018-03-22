package Packet;


public class PacketCoordinatorLife extends Packet {

	public PacketCoordinatorLife( ) {
		this.type = PacketType.PACKET_COORDINATOR_LIFE;
	}

	@Override
	public byte[] toBinary() {
		baos.write( this.type.id );
		return baos.toByteArray();
	}

}
