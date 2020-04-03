package com.mygdx.game.objects;

import com.mygdx.game.KambojaMain;

public class BotController extends PlayerController{
	
	static String names[] = new String[]{"Damodara", "Ferid", "Hildraed", "Pip", "Orion", "Lucjan", "Owen", "Martynas", " Maalik", "Berko", "Kay", "Henrik",
			"Alexius", "Haidar", "Nyoman", "Stefan", "Zephyros"};

	public BotController(int player) {
		super(0, null, player, "", "BOT");
		
		weapon = (int)(Math.random() * KambojaMain.getWeaponSize());
		
		while(!KambojaMain.weaponUnlocked[weapon]) {
			weapon = (int)(Math.random() * KambojaMain.getWeaponSize());
		}
		
		name = names[(int)(Math.random() * names.length)];
	}

}
