package com.mygdx.game.objects;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class GameMusic{
	
	 static HashMap<String, Music> inGameMusics;
	 
	 public static float MUSIC_VOLUME = 1;
	 
	 public static final String MAIN_MENU = "main_menu";
	
	public static void initialize() {
		inGameMusics = new HashMap<String, Music>();
		
		//inGameMusics.put(MAIN_MENU, Gdx.audio.newMusic(Gdx.files.internal("music")));
	}
	
	public static void turnOffMenuSong() {
		
	}
	
	public static void turnOnMenuSong() {
		
	}
	
	public static void playMapSong(String songName) {
		
	}
	
	public static void turnOffMapSong() {
		
	}

}
