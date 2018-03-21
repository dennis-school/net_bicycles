package Package;

import java.io.Serializable;

import Package.PacketTypes.PacketType;

public abstract class Packet implements Serializable {
	protected PacketType type;
	
	public Packet( ) {
		
	}
	
	public PacketType getPacketType() {
		return this.type;
	}
}
