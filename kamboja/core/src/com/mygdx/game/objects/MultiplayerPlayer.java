package com.mygdx.game.objects;

import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.states.GameState;

public class MultiplayerPlayer extends Player{

	public String ip;
	
	public MultiplayerPlayer(Body body, int id, GameState state, String name, String ip) {
		super(body, id, state, name);
		this.ip = ip;
	}

}
