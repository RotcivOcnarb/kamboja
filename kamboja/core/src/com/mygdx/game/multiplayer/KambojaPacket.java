package com.mygdx.game.multiplayer;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class KambojaPacket implements Serializable{

	public enum PacketType{
		PLAYER_INPUT,
		PLAYER_ENTER,
		PLAYER_POSITION
	}
	
	public InetAddress ipOrigin;
	public PacketType type;
	public Object data;
	
	public KambojaPacket(PacketType type, InetAddress ipOrigin) {
		this.type = type;
		this.ipOrigin = ipOrigin;
	}
	
	public KambojaPacket(PacketType type) {
		this.type = type;
		try {
			ipOrigin = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
}
