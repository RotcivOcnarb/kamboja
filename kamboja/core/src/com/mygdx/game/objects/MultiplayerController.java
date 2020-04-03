package com.mygdx.game.objects;

import com.badlogic.gdx.controllers.Controller;

public class MultiplayerController extends PlayerController{

	public MultiplayerController(int weapon, int player, String name, String controllerName) {
		super(weapon, null, player, name, controllerName);
	}

}
