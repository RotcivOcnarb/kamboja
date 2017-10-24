package com.mygdx.game.objects;

import com.mygdx.game.KambojaMain;

public class BotController extends PlayerController{
	
	static String names[] = new String[]{"Damodara", "Ferid", "Hildraed", "Pip", "Orion", "Lucjan", "Owen", "Martynas", " Maalik", "Berko", "Kay", "Henrik",
			"Alexius", "Haidar", "Nyoman", "Stefan", "Zephyros"};

	public BotController(int player, byte[] identifier) {
		super(0, null, player, "", identifier);
		
		weapon = (int)(Math.random() * KambojaMain.getWeaponSize());
		name = names[(int)(Math.random() * names.length)];
	}

}
