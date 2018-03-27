package Packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public abstract class Packet {
	protected PacketType type;
	protected ByteArrayOutputStream baos;
	
	public Packet( ) {
		this.baos = new ByteArrayOutputStream();
	}
	
	public PacketType getPacketType() {
		return this.type;
	}
	
	public void writeInt( int i ) {
		baos.write( i );
	}
	
	public void writeString( String s ) {
		try {
			baos.write( s.getBytes() );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates a binary representation of the payload of the packet
	 * Does not include any packet metadata.
	 * 
	 * @return
	 */
	public byte[] toBinary( int packet_id ) {
		writeInt( this.type.id );
		writeInt( packet_id );
		return baos.toByteArray();
	}
	
}
