package Packet;

import java.io.ByteArrayOutputStream;

public abstract class Packet {
	protected PacketType type;
	protected ByteArrayOutputStream baos;
	
	public Packet( ) {
		this.baos = new ByteArrayOutputStream();
	}
	
	public PacketType getPacketType() {
		return this.type;
	}
	
	/**
	 * Creates a binary representation of the payload of the packet
	 * Does not include any packet metadata.
	 * 
	 * @return
	 */
	public abstract byte[] toBinary( );
}
