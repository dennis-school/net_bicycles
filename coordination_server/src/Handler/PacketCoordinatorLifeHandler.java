package Handler;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

import Package.Packet;
import Package.PacketCoordinatorResponse;

public class PacketCoordinatorLifeHandler implements PacketHandler {

	
	private DatagramSocket socket;
	
	public PacketCoordinatorLifeHandler( DatagramSocket socket ) {
		this.socket = socket;
	}

	private DatagramPacket buildPacket(SocketAddress address) {
		// maybe should add more information in packet?
		PacketCoordinatorResponse packet = new PacketCoordinatorResponse( );
		
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream(4000);
        ObjectOutputStream outputStream;
		try {
			outputStream = new ObjectOutputStream(new BufferedOutputStream(byteStream));
	        outputStream.flush();
	        outputStream.writeObject(packet);
	        outputStream.flush();
	        outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        byte[] bytes =  byteStream.toByteArray();
        
		return new DatagramPacket(bytes, bytes.length, address);
	}
	
	@Override
	public void handlePacket(Packet p, SocketAddress address) {
		DatagramPacket packet = buildPacket( address );
		try {
			socket.send( packet );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}