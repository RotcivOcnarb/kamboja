package com.mygdx.game.objects;

import com.badlogic.gdx.controllers.Controller;

public class MultiplayerController extends PlayerController{

	public String ip;
	
	public MultiplayerController(int weapon, int player, String name, String controllerName, String ip) {
		super(weapon, null, player, name, controllerName);
		this.ip = ip;
	}

}
