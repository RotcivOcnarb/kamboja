package com.mygdx.game.multiplayer.packagetypes;

import java.io.Serializable;

import com.badlogic.gdx.math.Vector2;

public class PlayerPosition implements Serializable{

	public Vector2 position;
	public Vector2 angle;
	public int player;
	public float weaponAnalog;
	
}
