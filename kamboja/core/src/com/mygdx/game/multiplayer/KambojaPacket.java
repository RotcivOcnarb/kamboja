package com.mygdx.game.multiplayer;

import java.io.Serializable;

public class KambojaPacket implements Serializable{

	public enum PacketType{
		CONNECT_TO_SERVER, //client requesting connection to server -- Client -> Server
		CONNECTION_CONFIRM, //Server has confirmed connection -- Server -> Client
	}
	
	public PacketType type;
	
	public KambojaPacket(PacketType type) {
		this.type = type;
	}
	
}
