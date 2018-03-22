package Handler;

import java.io.ByteArrayInputStream;
import java.net.SocketAddress;

public interface PacketHandler {
	
	public void handlePacket( ByteArrayInputStream bais, SocketAddress address );

}
