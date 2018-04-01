package bicycle.io;

import java.io.IOException;

import java.io.OutputStream;

public class BEOutputStream {
	private final OutputStream out;
	
	public BEOutputStream( OutputStream out ) {
		this.out = out;
	}
	
	public void writeUint32( int v ) throws IOException {
		byte[] data = new byte[4];
		data[0] = (byte) ( ( v >>> 24 ) & 0xFF );
		data[1] = (byte) ( ( v >>> 16 ) & 0xFF );
		data[2] = (byte) ( ( v >>> 8 ) & 0xFF );
		data[3] = (byte) ( v & 0xFF );
		write( data );
	}
	
	public void writeUint16( int v ) throws IOException {
		byte[] data = new byte[2];
		data[0] = (byte) ( ( v >>> 8 ) & 0xFF );
		data[1] = (byte) ( v & 0xFF );
		write( data );
	}
	
	public void write( byte[] b ) throws IOException {
		out.write( b );
	}
	
	public void write( byte[] b, int offset, int length ) throws IOException {
		out.write( b, offset, length );
	}
}
