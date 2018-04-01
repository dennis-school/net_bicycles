package bicycle.net.packet;


public class PacketLifeCheck extends Packet {

	public PacketLifeCheck( ) {
		this.type = PacketType.LIFE_CHECK;
	}

}
