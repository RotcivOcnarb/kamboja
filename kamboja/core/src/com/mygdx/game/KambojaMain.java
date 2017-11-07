package com.mygdx.game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.codedisaster.steamworks.SteamAPI;
import com.codedisaster.steamworks.SteamException;
import com.mygdx.game.objects.Player;
import com.mygdx.game.objects.PlayerController;
import com.mygdx.game.objects.shift.Barrier;
import com.mygdx.game.objects.shift.Turret;
import com.mygdx.game.objects.weapon.Bazooka;
import com.mygdx.game.objects.weapon.DoublePistol;
import com.mygdx.game.objects.weapon.Flamethrower;
import com.mygdx.game.objects.weapon.Laser;
import com.mygdx.game.objects.weapon.Minigun;
import com.mygdx.game.objects.weapon.Mp5;
import com.mygdx.game.objects.weapon.Pistol;
import com.mygdx.game.objects.weapon.Shotgun;
import com.mygdx.game.states.GameState;

public class KambojaMain extends ApplicationAdapter {
	
	
	private SpriteBatch sb;
	private Manager manager; //the list of states
	
	private static ArrayList<PlayerController> controllers; //all controllers connectes
	private static int deathsNumber = 5;
	private static int gameTime = 60 * 5;
	private static ArrayList<Player> postGamePlayers;
	private static String mapName;
	private static int weaponSize = 8;
	private static int playersize = 5;
	private static boolean items = true; 
	public static final double SENSITIVITY = 1.5f;
	public static boolean[] mapUnlocked = new boolean[]{true, false, true, false, true, true, true, true, true, true, true, true, true, true, true, true};
	
	/**Lists all the controllers connected currently (of people that pressed "start", keyboard and bots
	 * included)
	 * 
	 * @return an array list of Controllers connected currently
	 */
	public static ArrayList<PlayerController> getControllers() {
		return controllers;
	}
	/**Resets or creates the controllers list
	 * @see getControllers()
	 */
	public static void initializeControllers(){
		controllers = new ArrayList<PlayerController>();
	}

	/**The number of deaths to end the game
	 * 
	 * @return the number of deaths (-1 means infinity)
	 */
	public static int getDeathsNumber() {
		return deathsNumber;
	}
	/**Sets the number of deaths to end the game
	 * 
	 * @param deaths the number of deaths (-1 means infinity)
	 */
	public static void setDeathsNumber(int deaths) {
		KambojaMain.deathsNumber = deaths;
	}
	/** The time it takes to end one play of the game
	 * 
	 * @return the time in seconds (-1 means infinity)
	 */
	public static int getGameTime() {
		return gameTime;
	}
	/**Sets the time it takes to end one play of the game
	 * 
	 * @param time the time in seconds (-1 means infinity)
	 */
	public static void setGameTime(int time) {
		KambojaMain.gameTime = time;
	}

	/**Gets a list of the players that have played the game on the last game, with its scores
	 * 
	 * @return the list of players
	 */
	public static ArrayList<Player> getPostGamePlayers() {
		return postGamePlayers;
	}
	/**Resets or creates the players post game list
	 * @see getPostGamePlayers()
	 */
	public static void initializePostGamePlayers(){
		postGamePlayers = new ArrayList<Player>();
	}

	/**The path of the .tmx file of that map
	 * 
	 * @return a string of the map file
	 */
	public static String getMapName() {
		return mapName;
	}

	/**Sets the path of the .tmx file of the map
	 * 
	 * @param map the .tmx path
	 */
	public static void setMapName(String map) {
		KambojaMain.mapName = map;
	}

	/**Returns the number of current weapons available in the Main menu selection
	 * 
	 * @return the number of weapons
	 */
	public static int getWeaponSize() {
		return weaponSize;
	}

	/**Returns the number of available skins for player selection in the Main menu
	 * 
	 * @return the number of skins
	 */
	public static int getPlayerSkinsSize() {
		return playersize;
	}


	/**If the item spawning at game is enabled
	 * 
	 * @return true if is enabled, false if it isn't
	 */
	public static boolean hasItems() {
		return items;
	}

	/**Sets the spawning items on or off
	 * 
	 * @param items true means enabled, false means disabled
	 */
	public static void setItems(boolean items) {
		KambojaMain.items = items;
	}

	public void create () {
		sb = new SpriteBatch();
		manager = new Manager();
		manager.create();
	
		//Loads the config.ini file and sets all the parameters
		try {
			File conf = new File("config.ini");
			
			BufferedReader br = new BufferedReader(new FileReader(conf));
			
			HashMap<String, String> configs = new HashMap<String, String>();
			String s;
			while((s = br.readLine()) != null){
				try{
					configs.put(s.split("=")[0], s.split("=")[1]);
				}
				catch(ArrayIndexOutOfBoundsException e){
					
				}
				//System.out.println(s);
			}
			
			br.close();
			
			Bazooka.DAMAGE = Float.parseFloat(configs.get("BazookaDamage"));
			Bazooka.PRECISION = Float.parseFloat(configs.get("BazookaPrecision"));
			Bazooka.WEIGHT = Float.parseFloat(configs.get("BazookaWeight"));
			
			DoublePistol.DAMAGE = Float.parseFloat(configs.get("DoublePistolDamage"));
			DoublePistol.PRECISION = Float.parseFloat(configs.get("DoublePistolPrecision"));
			DoublePistol.WEIGHT = Float.parseFloat(configs.get("DoublePistolWeight"));
			
			Flamethrower.DAMAGE = Float.parseFloat(configs.get("FlamethrowerDamage"));
			Flamethrower.PRECISION = Float.parseFloat(configs.get("FlamethrowerPrecision"));
			Flamethrower.WEIGHT = Float.parseFloat(configs.get("FlamethrowerWeight"));
			
			Laser.DAMAGE = Float.parseFloat(configs.get("LaserDamage"));
			Laser.PRECISION = Float.parseFloat(configs.get("LaserPrecision"));
			Laser.WEIGHT = Float.parseFloat(configs.get("LaserWeight"));
			
			Minigun.DAMAGE = Float.parseFloat(configs.get("MinigunDamage"));
			Minigun.PRECISION = Float.parseFloat(configs.get("MinigunPrecision"));
			Minigun.WEIGHT = Float.parseFloat(configs.get("MinigunWeight"));
			
			Mp5.DAMAGE = Float.parseFloat(configs.get("Mp5Damage"));
			Mp5.PRECISION = Float.parseFloat(configs.get("Mp5Precision"));
			Mp5.WEIGHT = Float.parseFloat(configs.get("Mp5Weight"));
			
			Pistol.DAMAGE = Float.parseFloat(configs.get("PistolDamage"));
			Pistol.PRECISION = Float.parseFloat(configs.get("PistolPrecision"));
			Pistol.WEIGHT = Float.parseFloat(configs.get("PistolWeight"));
			
			Shotgun.DAMAGE = Float.parseFloat(configs.get("ShotgunDamage"));
			Shotgun.PRECISION = Float.parseFloat(configs.get("ShotgunPrecision"));
			Shotgun.WEIGHT = Float.parseFloat(configs.get("ShotgunWeight"));
			
			Turret.LIFE = Float.parseFloat(configs.get("TurretLife"));
			Turret.RANGE = Float.parseFloat(configs.get("TurretRange"));
			Turret.DAMAGE = Float.parseFloat(configs.get("TurretDamage"));
			
			Barrier.LIFE = Float.parseFloat(configs.get("BarrierLife"));
			
			GameState.SFX = configs.get("SFX").toLowerCase().equals("true");
			GameState.BETA_ITEMS = configs.get("BetaItems").toLowerCase().equals("true");
			GameState.LIGHTS = configs.get("Lights").toLowerCase().equals("true");
			GameState.DEBUG = configs.get("DebugMode").toLowerCase().equals("true");
			GameState.DIFFICULTY = Integer.parseInt(configs.get("BotDifficulty"));
			GameState.VOLUME = Float.parseFloat(configs.get("SFXVolume"));
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	public void resize(int width, int height){
		manager.resize(width, height);
	}
	
	float gcTimer = 0;
	public void render () {
		//SteamAPI.runCallbacks();
		//summons the garbage collector every 30 seconds
		//not really sure if it makes any difference but nah, whatever
		gcTimer += Gdx.graphics.getDeltaTime();
		if(gcTimer >= 30){
			gcTimer = 0;
			System.gc();
		}
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);//clear screen
		Gdx.gl.glEnable(GL20.GL_BLEND); //enables transparency
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.input.setCursorCatched(true); //hides the cursor
		manager.update(Gdx.graphics.getDeltaTime());
		manager.render(sb);

	}
	
	@Override
	public void dispose(){
		super.dispose();
	}
}
