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
import java.util.HashMap;

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
		//testTimer();
		
		//System.out.println( InetAddress.getByName("localhost") );
		
		testSend();

		//testReceive();
		
		//Coordinator c = new Coordinator();
		
		
		
	}
	
	private static void init() throws SocketException, UnknownHostException {
		socket1 = new DatagramSocket();
		socket2 = new DatagramSocket();
	}
	

	private static void testTimer() throws IOException {
		aliveTimerListener = ( ev -> {
			System.out.println( "time up" );
			aliveTimer.stop();
		} );
		aliveTimer = new Timer( 5000, aliveTimerListener );
		aliveTimer.start();
		System.out.println( "start" );
		while( aliveTimer.isRunning( )  ) {
			
		}
		
	}
	
	private static void testLifeChecker() throws IOException {
		
		Coordinator coordinator = new Coordinator();
		InetSocketAddress address = new InetSocketAddress( InetAddress.getLocalHost(), socket2.getLocalPort() );
		LifeChecker checker = new LifeChecker( address, coordinator );
		Thread t = new Thread( checker );
		t.start();
		System.out.println( "Timer start" );
		
		testLifeReceive();
		
	}

	private static void testLifeReceive() throws IOException {
		byte[] buf = new byte[100];
		DatagramPacket p = new DatagramPacket(buf, buf.length);
		socket2.receive( p );
		System.out.println("Socket2 receive the packet");
	}	


	private static void testPacketType() {
		if( PacketType.PACKET_LIFE_CHECK.id == 0 )
			System.out.println("PacketType is int");
	}
	
	private static void showInetInfo() throws UnknownHostException {
		System.out.println( InetAddress.getLocalHost() );
		System.out.println( socket1.getLocalAddress() + " " + socket1.getLocalPort() );
		System.out.println( socket2.getLocalAddress() + " " + socket2.getLocalPort() );
	}
	
	private static void testSend( ) throws IOException {
		byte[] buff = new byte[100];
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream( );
		// type 2 byte
		buff[1] = (byte) ((PacketType.Packet_BICYCLE_TRANSECTION.id>>>8)&0xFF);
		buff[0] = (byte) (PacketType.Packet_BICYCLE_TRANSECTION.id&0xFF);
		baos.write( buff, 0, 2 );
		// packet_id 2 bytes
		buff[1] = (byte) ((4>>>8)&0xFF);
		buff[0] = (byte) (4&0xFF);
		baos.write( buff, 0, 2 );
		// 1 byte
		baos.write('1');
		// 10 byte
		baos.write( "TEIFUCVO85".getBytes() );
		// 4 byte int
		int i = 11001100;
		buff[3] = (byte) ((i>>>24)&0xFF);
		buff[2] = (byte) ((i>>>16)&0xFF);
		buff[1] = (byte) ((i>>>8)&0xFF);
		buff[0] = (byte) (i&0xFF);
		baos.write( buff, 0, 4 );
		byte[] p = baos.toByteArray();

		InetSocketAddress address = new InetSocketAddress( InetAddress.getLocalHost(), socket2.getLocalPort() );
		
		DatagramPacket packet = new DatagramPacket( p, p.length, new InetSocketAddress( InetAddress.getByName("localhost"), 8100 ) );
		//DatagramPacket packet = new DatagramPacket( p, p.length, address );
		socket1.send( packet );
		System.out.println("Socket1 send packet");
	}
	
	private static void testReceive( ) throws IOException {
		byte[] buf = new byte[100];
		DatagramPacket p = new DatagramPacket(buf, buf.length);
		socket2.receive( p );
		System.out.println("Socket2 receive the packet");
		ByteArrayInputStream bais = new ByteArrayInputStream( buf );

		
		int type = (bais.read()&0xFF) | ((bais.read()&0xFF)<<8);
		//int type = ((bais.read() << 8) & 0x0000ff00) | (bais.read() & 0x000000ff);
		System.out.println( type );
		
		int packet_id = (bais.read()&0xFF) | ((bais.read()&0xFF)<<8);
		System.out.println( packet_id );
		
		int isRemoved = bais.read() - '0';
		System.out.println( isRemoved );
		
		int count = 0;
		String bicycle_id = "";
		while( count < 10 ) {
			bicycle_id += (char)bais.read();
			count++;
		}
		System.out.println( bicycle_id );

		//int user_id = ((bais.read()&0xFF)<<24) | ((bais.read()&0xFF)<<16) | ((bais.read()&0xFF)<<8) | (bais.read()&0xFF);
		int user_id = (bais.read()&0xFF) | ((bais.read()&0xFF)<<8) | ((bais.read()&0xFF)<<16) | ((bais.read()&0xFF)<<24);
		System.out.println( user_id );
		
		
		
	}
	
	private int read2ByteInt( ByteArrayInputStream bais ) {
		return ((bais.read()&0xFF)<<8) | (bais.read()&0xFF);
	}
	
	private int read4ByteInt( ByteArrayInputStream bais) {
		return ((bais.read()&0xFF)<<24) | ((bais.read()&0xFF)<<16) | ((bais.read()&0xFF)<<8) | (bais.read()&0xFF);
	}
	
}
