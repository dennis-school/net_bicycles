package Package;

import Package.PacketTypes.PacketType;

public class PacketCoordinatorResponse extends Packet {

	public PacketCoordinatorResponse( ) {
		this.type = PacketType.Packet_Coordinator_Response;
	}

}
