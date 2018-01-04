package com.mygdx.game.objects;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.mygdx.game.states.GameState;

public class GameMusic{
	
	static boolean started = false;
	
	 static HashMap<String, Music> inGameMusics;
	 
	 public static float MUSIC_VOLUME = 0;
	 
	 public static final String MAIN_MENU = "main_menu";
	 
	 static String currentMusic = MAIN_MENU;
	static  String nextMusic = MAIN_MENU;
	
	public static void initialize() {
		inGameMusics = new HashMap<String, Music>();
		
		inGameMusics.put(MAIN_MENU, Gdx.audio.newMusic(Gdx.files.internal("music/menu inicial.ogg")));
		inGameMusics.get(MAIN_MENU).setLooping(true);
		
		inGameMusics.put("garden", Gdx.audio.newMusic(Gdx.files.internal("music/garden.ogg")));
		inGameMusics.get("garden").setLooping(true);

		inGameMusics.put("library", Gdx.audio.newMusic(Gdx.files.internal("music/library.ogg")));
		inGameMusics.get("library").setLooping(true);

		inGameMusics.put("forest", Gdx.audio.newMusic(Gdx.files.internal("music/forest.ogg")));
		inGameMusics.get("forest").setLooping(true);

		inGameMusics.put("island", Gdx.audio.newMusic(Gdx.files.internal("music/ilha flutuante.ogg")));
		inGameMusics.get("island").setLooping(true);

	}
	
	public static void startMenu() {
		if(!started) {
			currentMusic = MAIN_MENU;
			nextMusic = MAIN_MENU;
			MUSIC_VOLUME = 0;
			fadeIn = true;
			inGameMusics.get(MAIN_MENU).play();
			started = true;
		}
		else {
			playMenuSong();
		}
		
	}
	
	public static void playMenuSong() {
		playSong(MAIN_MENU);
	}
	
	public static void playSong(String songName) {
		nextMusic = songName;
		
		if(!nextMusic.equals(currentMusic))
		fadeOut = true;
		
	}
	
	static boolean fadeIn = true;
	static boolean fadeOut = false;
	
	public static void update() {
		
			if(fadeOut) {
				inGameMusics.get(currentMusic).setVolume(MUSIC_VOLUME * GameState.VOLUME);
				MUSIC_VOLUME -= 0.01f;
				if(MUSIC_VOLUME < 0) {
					MUSIC_VOLUME = 0;
					inGameMusics.get(currentMusic).stop();
					currentMusic = nextMusic;
					inGameMusics.get(currentMusic).play();
					fadeOut = false;
					fadeIn = true;
				}
			}
			if(fadeIn) {
				inGameMusics.get(currentMusic).setVolume(MUSIC_VOLUME * GameState.VOLUME);
				MUSIC_VOLUME += 0.01f;
				if(MUSIC_VOLUME > 1) {
					MUSIC_VOLUME = 1;
					fadeIn = false;
				}
			}
		
		
	}

}
