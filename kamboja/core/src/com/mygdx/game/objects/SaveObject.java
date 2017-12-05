package com.mygdx.game.objects;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;

public class SaveObject implements Serializable{

	boolean maps[] = new boolean[16];
	boolean weapons[] = new boolean[8];
	int level;
	int experience;
	
	public void write(Json json) {
		json.writeValue("maps", maps);
		json.writeValue("weapons", weapons);
		json.writeValue("level", level);
		json.writeValue("experience", experience);
	}

	public void read(Json json, JsonValue jsonData) {
		maps = jsonData.get("maps").asBooleanArray();
		weapons = jsonData.get("weapons").asBooleanArray();
		level = jsonData.get("level").asInt();
		experience = jsonData.get("experience").asInt();
	}

	public boolean[] getMaps() {
		return maps;
	}

	public void setMaps(boolean[] maps) {
		this.maps = maps;
	}

	public boolean[] getWeapons() {
		return weapons;
	}

	public void setWeapons(boolean[] weapons) {
		this.weapons = weapons;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getExperience() {
		return experience;
	}

	public void setExperience(int experience) {
		this.experience = experience;
	}

}
