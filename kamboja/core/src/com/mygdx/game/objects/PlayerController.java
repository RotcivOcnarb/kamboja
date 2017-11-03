package com.mygdx.game.objects;

import com.badlogic.gdx.controllers.Controller;
import com.mygdx.game.KambojaMain;

public class PlayerController {
	
	int weapon;
	Controller controller;
	int player;
	String name;
	
	public PlayerController(int weapon, Controller controller, int player, String name){
		this.weapon = weapon;
		this.controller = controller;
		this.player = player;
		this.name = name;
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

	public void addLetterToName(String letter) {
		if(name.length() < 8)
		name += letter;
	}
	
	public void removeLetterFromName() {
		if(name.length() > 0) {
			name = name.substring(0, name.length()-1);
		}
	}
	

}
