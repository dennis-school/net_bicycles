package bicycle.net.packet;

public class PacketConnectionAccept extends Packet {

	public PacketConnectionAccept() {
		this.type = PacketType.CONNECTION_ACCEPT;
	}
}
