package com.mygdx.game.multiplayer;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.mygdx.game.objects.PlayerController;

public class MultiplayerController extends PlayerController{

	InetAddress host;
	
	public MultiplayerController(int weapon, int player, String name, InetAddress host) {
		super(weapon, null, player, name);
		this.host = host;
	}
	
	
	public InetAddress getAddress(){
		return host;
	}

}
