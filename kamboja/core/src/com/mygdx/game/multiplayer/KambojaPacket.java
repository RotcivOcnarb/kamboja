package com.mygdx.game.multiplayer;

import java.io.Serializable;
import java.net.InetAddress;

public class KambojaPacket implements Serializable{

	public enum PacketType{
		CONNECT_TO_SERVER, //client requesting connection to server -- Client -> Server
		CONNECTION_CONFIRM, //Server has confirmed connection -- Server -> Client
	}
	
	public InetAddress ipOrigin;
	public PacketType type;
	
	public KambojaPacket(PacketType type, InetAddress ipOrigin) {
		this.type = type;
		this.ipOrigin = ipOrigin;
	}
	
}
