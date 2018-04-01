package bicycle.net.packet;

public class PacketConnectionReject extends Packet {

	public PacketConnectionReject() {
		this.type = PacketType.CONNECTION_REJECT;
	}
}
