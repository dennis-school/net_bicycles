package Handler;

import java.net.SocketAddress;

import Package.Packet;

public interface PacketHandler {
	
	public void handlePacket( Packet p, SocketAddress address );

}
