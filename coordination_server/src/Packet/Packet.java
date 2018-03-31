package Packet;

import java.io.ByteArrayInputStream;
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
	
	public void writeString( String s ) {
		try {
			baos.write( s.getBytes() );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void write1ByteInt( int i ) {
		baos.write( i );
	}
	
	public void write2ByteInt( int i ) {
		byte[] buff = new byte[2];
		buff[1] = (byte) ((i>>>8)&0xFF);
		buff[0] = (byte) (i&0xFF);
		baos.write( buff, 0, 2 );
	}
	
	public void write4ByteInt( int i ) {
		byte[] buff = new byte[4];
		buff[4] = (byte) ((i>>>24)&0xFF);
		buff[3] = (byte) ((i>>>16)&0xFF);
		buff[2] = (byte) ((i>>>8)&0xFF);
		buff[1] = (byte) (i&0xFF);
		baos.write( buff, 0, 4 );
	}
	
	
	/**
	 * Creates a binary representation of the payload of the packet
	 * Does not include any packet metadata.
	 * 
	 * @return
	 */
	public byte[] toBinary( int packet_id ) {
		write2ByteInt( this.type.id );
		write2ByteInt( packet_id );
		return baos.toByteArray();
	}
	
}
