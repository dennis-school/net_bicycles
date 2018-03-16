package net_bicycles_coordination_server;

import java.io.Serializable;

import net_bicycles_coordination_server.PacketTypes.PacketType;

public abstract class Packet implements Serializable {
	protected PacketType type;
	protected int id;
	
	public Packet( int id ) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	public PacketType getPacketType() {
		return this.type;
	}
}
