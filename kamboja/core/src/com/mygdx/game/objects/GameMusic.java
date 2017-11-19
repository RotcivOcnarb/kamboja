package com.mygdx.game.objects;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class GameMusic{
	
	 static HashMap<String, Music> inGameMusics;
	 
	 public static float MUSIC_VOLUME = 1;
	 
	 public static final String MAIN_MENU = "Guitar Mayhem 5";
	
	public static void initialize() {
		inGameMusics = new HashMap<String, Music>();
		
		
		/*
		 * music = Gdx.audio.newMusic(Gdx.files.internal("music/the_league_of_mice.ogg"));
			music.setLooping(true);
		 */
	}
	
	public static void loadMusic(String music) {
		if(!inGameMusics.containsKey(music)) {
			inGameMusics.put(music, Gdx.audio.newMusic(Gdx.files.internal("music/" + music + ".ogg")));
		}
		
	}
	
	public static Music getMusic(String musicName) {
		return inGameMusics.get(musicName);
	}
	
	public static void setVolume(String musicName, float volume) {
		Music music = inGameMusics.get(musicName);
		music.setVolume(volume * MUSIC_VOLUME);
	}
	
	public static void loop(String musicName, float volume) {
		Music music = inGameMusics.get(musicName);
		music.setLooping(true);
		if(!music.isPlaying()) {
			music.setVolume(MUSIC_VOLUME * volume);
			music.play();
		}
	}
	
	public static void playOnce(String musicName) {
		Music music = inGameMusics.get(musicName);
		music.setLooping(false);
		if(!music.isPlaying()) {
			music.setVolume(MUSIC_VOLUME);
			music.play();
		}
	}
	
	public static void stopAll() {
		for(String s : inGameMusics.keySet()) {
			inGameMusics.get(s).stop();
		}
	}
	
	public static void fadeIn(String musicName) {
		Music music = inGameMusics.get(musicName);
		if(music.getVolume() < MUSIC_VOLUME)
			music.setVolume(music.getVolume() + (0.01f*MUSIC_VOLUME));
		else {
			music.setVolume(MUSIC_VOLUME);
		}
	}
	
	public static void fadeOut(String musicName) {
		Music music = inGameMusics.get(musicName);
		if(music.getVolume() > 0.01f*MUSIC_VOLUME)
			music.setVolume(music.getVolume() - 0.01f*MUSIC_VOLUME);
		else
			music.stop();
		
	}


}
