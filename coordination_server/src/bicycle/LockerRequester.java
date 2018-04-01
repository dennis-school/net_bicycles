package bicycle;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.ArrayList;

import bicycle.net.packet.PacketReplaceConnectionRequest;

/**
 * Tell locker the death of coordinator
 * @author Luigi
 *
 */
public class LockerRequester implements Runnable {

	private Coordinator coordinator;
	private ArrayList<SocketAddress> lockers;
	private int deadCoordiantor_id;
	private DatagramSocket socket;
	
	public LockerRequester(Coordinator coordinator, ArrayList<SocketAddress> lockers, int deadCoordinator_id) {
		this.coordinator = coordinator;
		this.lockers = lockers;
		this.deadCoordiantor_id = deadCoordinator_id;
		this.socket = this.coordinator.getDatagramSocket();
	}

	@Override
	public void run() {
		for( SocketAddress locker:lockers ) {
			coordinator.addWaitingLockers( locker );
			
			PacketReplaceConnectionRequest p = new PacketReplaceConnectionRequest();
			byte[] fullPacket = p.toBinary( deadCoordiantor_id );
			DatagramPacket datapacket = new DatagramPacket( fullPacket, fullPacket.length, locker);
			
			try {
				socket.send( datapacket );
				Thread.sleep( 1000 );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}

}
