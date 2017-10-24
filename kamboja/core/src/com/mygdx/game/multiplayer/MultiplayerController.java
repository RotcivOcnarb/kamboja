package com.mygdx.game.multiplayer;

import com.mygdx.game.objects.PlayerController;

public class MultiplayerController extends PlayerController{

	public MultiplayerController(int weapon, int player, String name) {
		super(weapon, null, player, name);
	}

}
