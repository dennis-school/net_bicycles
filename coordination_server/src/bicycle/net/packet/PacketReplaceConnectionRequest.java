package bicycle.net.packet;

public class PacketReplaceConnectionRequest extends Packet {

	public PacketReplaceConnectionRequest() {
		this.type = PacketType.REPLACE_CONNECTION_REQUEST;
	}
}
