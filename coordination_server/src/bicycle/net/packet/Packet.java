package bicycle.net.packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import bicycle.io.BEOutputStream;
/**
 * Packet use for communication between lockers/coordinators
 * @author Luigi
 *
 */
public abstract class Packet {
	protected PacketType type;
	
	public Packet( ) {
		
	}
	
	public PacketType getPacketType() {
		return this.type;
	}
	
	
	/**
	 * Creates a binary representation of the payload of the packet
	 * 
	 * @return
	 * @throws IOException 
	 */
	public byte[] toBinary( int packet_id ) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream( );
			BEOutputStream out = new BEOutputStream( baos );
			out.writeUint16( type.id );
			out.writeUint16( packet_id );
			return baos.toByteArray();
		} catch ( IOException ex ) {
			return new byte[0];
		}
	}
}
