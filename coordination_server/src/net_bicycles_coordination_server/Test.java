package net_bicycles_coordination_server;

import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.Timer;

import Packet.PacketType;

// test

public class Test {
	
	private static DatagramSocket socket1;
	private static DatagramSocket socket2;
	private static ActionListener aliveTimerListener;
	private static Timer aliveTimer;
	
	public static void main( String... args ) throws IOException {
		init();
		//testSend();
		//testReceive();
		
		//System.out.println( InetAddress.getByName("localhost") );
		
	}
	
	private static void init() throws SocketException, UnknownHostException {
		socket1 = new DatagramSocket();
		socket2 = new DatagramSocket();
	}
	
	private static void testTimer() throws IOException {
		
		Coordinator coordinator = new Coordinator();
		InetSocketAddress address = new InetSocketAddress( InetAddress.getLocalHost(), socket2.getLocalPort() );
		LifeChecker checker = new LifeChecker( address, coordinator );
		Thread t = new Thread( checker );
		t.start();
		System.out.println( "Timer start" );
		
		testReceive();
		
	}
	
	private static void testPacketType() {
		if( PacketType.PACKET_COORDINATOR_LIFE.id == 0 )
			System.out.println("PacketType is int");
	}
	
	private static void showInetInfo() throws UnknownHostException {
		System.out.println( InetAddress.getLocalHost() );
		System.out.println( socket1.getLocalAddress() + " " + socket1.getLocalPort() );
		System.out.println( socket2.getLocalAddress() + " " + socket2.getLocalPort() );
	}
	
	private static void testSend() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream( );
		baos.write( PacketType.PACKET_COORDINATOR_LIFE.id );
		baos.write( "test".getBytes() );
		byte[] p = baos.toByteArray();
		
		InetSocketAddress address = new InetSocketAddress( InetAddress.getLocalHost(), socket2.getLocalPort() );
		
		DatagramPacket packet = new DatagramPacket( p, p.length, address );
		//DatagramPacket packet = new DatagramPacket( p, p.length, socket2.getLocalSocketAddress() );
		socket1.send( packet );
		System.out.println("Socket1 send packet");
	}
	
	private static void testReceive() throws IOException {
		byte[] buf = new byte[10];
		DatagramPacket p = new DatagramPacket(buf, buf.length);
		socket2.receive( p );
		System.out.println("Socket2 receive the packet");
		ByteArrayInputStream bais = new ByteArrayInputStream( buf );
		System.out.println( bais.read() );
		//System.out.print( (char) bais.read() );
		//System.out.print( (char) bais.read() );
		//System.out.print( (char) bais.read() );
		//System.out.println( (char) bais.read() );
		
	}
}
