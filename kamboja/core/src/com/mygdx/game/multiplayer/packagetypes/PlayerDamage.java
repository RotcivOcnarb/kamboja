package com.mygdx.game.multiplayer.packagetypes;

import java.io.Serializable;

public class PlayerDamage implements Serializable{

	public int owner, target;
	public float damage;
	public boolean showBlood;
	
}
