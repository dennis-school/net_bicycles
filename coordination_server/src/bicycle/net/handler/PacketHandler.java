package bicycle.net.handler;

import java.io.IOException;
import java.net.SocketAddress;

import bicycle.io.BEInputStream;

public interface PacketHandler {
	
	public void handlePacket( BEInputStream in, SocketAddress address, int pakcet_id ) throws IOException;

}
