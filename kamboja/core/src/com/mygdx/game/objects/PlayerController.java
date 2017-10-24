package com.mygdx.game.objects;

import com.badlogic.gdx.controllers.Controller;
import com.mygdx.game.KambojaMain;

public class PlayerController {
	
	int weapon;
	Controller controller;
	int player;
	String name;
	byte[] identifier = new byte[5];
	int index;
	
	public PlayerController(int weapon, Controller controller, int player, String name, byte[] identifier, int index){
		this.weapon = weapon;
		this.identifier = identifier;
		this.controller = controller;
		this.player = player;
		this.name = name;
		this.index = index;
	}
	
	public int getIndex(){
		return index;
	}
	
	public static byte[] generateIdentifier(){
		byte[] identifier = new byte[5];
		for(int i = 0; i < 5; i ++){
			identifier[i] = (byte)(Math.random() * 127);
		}
		return identifier;
	}
	
	public byte[] getIdentifier(){
		return identifier;
	}

	public int getWeapon() {
		return weapon;
	}
	
	public void nextWeapon(){
		weapon ++;
		if(weapon == KambojaMain.getWeaponSize()) weapon = 0;
	}
	
	public void previousWeapon(){
		weapon --;
		if(weapon == -1) weapon = KambojaMain.getWeaponSize() -1 ;
	}

	public void setWeapon(int weapon) {
		this.weapon = weapon;
	}

	public int getPlayer() {
		return player;
	}
	public void setPlayer(int player){
		this.player = player;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}

}
