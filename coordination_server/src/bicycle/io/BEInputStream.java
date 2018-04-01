package bicycle.io;

import java.io.IOException;
import java.io.InputStream;

public class BEInputStream {
	private final InputStream in;
	
	public BEInputStream( InputStream in ) {
		this.in = in;
	}
	
	public int readUint8( ) throws IOException {
		byte[] data = new byte[1];
		ensureRead( data, 0, 1 );
		return data[0] & 0xFF;
	}

	public int readUint16( ) throws IOException {
		byte[] data = new byte[2];
		ensureRead( data, 0, 2 );
		return ( (data[0]&0xFF) << 8 ) | (data[1]&0xFF);
	}
	
	public int readUint32( ) throws IOException {
		byte[] data = new byte[4];
		ensureRead( data, 0, 4 );
		return ( (data[0]&0xFF) << 24 ) | ( (data[1]&0xFF) << 16 ) | ( (data[2]&0xFF) << 8 ) | (data[3]&0xFF);
	}
	
	public int read( byte[] b ) throws IOException {
		return in.read( b );
	}
	
	public int read( byte[] b, int offset, int length ) throws IOException {
		return in.read( b, offset, length );
	}
	
	public void ensureRead( byte[] b, int offset, int length ) throws IOException {
		int totalRead = 0;
		while ( totalRead < length ) {
			int r = in.read( b, offset + totalRead, length - totalRead );
			if ( r < 0 )
				throw new IOException( "Failed to ensure read" );
			totalRead += r;
		}
	}
}
