package com.mygdx.game.multiplayer.packagetypes;

import java.io.Serializable;

public class PlayerInput implements Serializable{

	public enum InputAction{
		KEY_UP,
		KEY_DOWN,
		BUTTON_DOWN,
		BUTTON_UP,
		AXIS_MOVED,
		CONTROLLER_CONNECTED,
		CONTROLLER_DISCONNECTED
	}
	
	public InputAction action;
	public float value;
	public int code, controllerID;
	public String controllerName;
	
}
