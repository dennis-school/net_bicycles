package net_bicycles_coordination_server;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;

// test

public class Test {
	public static void Main( String... args ) {
		
		System.out.println( "Hello world" );
		
		try {
			DatagramSocket socket = new DatagramSocket();
			SocketAddress socketAddress = socket.getLocalSocketAddress();
			InetAddress address = socket.getLocalAddress();
			int port = socket.getLocalPort();
			
			System.out.println( address + " " + port );
			System.out.println( socketAddress );
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
