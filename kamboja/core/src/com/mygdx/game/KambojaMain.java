package com.mygdx.game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Json;
import com.codedisaster.steamworks.SteamAPI;
import com.codedisaster.steamworks.SteamAuth.AuthSessionResponse;
import com.codedisaster.steamworks.SteamException;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamUser;
import com.codedisaster.steamworks.SteamUserCallback;
import com.mygdx.game.analytics.GoogleAnalytics;
import com.mygdx.game.analytics.HitData;
import com.mygdx.game.objects.GameMusic;
import com.mygdx.game.objects.Player;
import com.mygdx.game.objects.PlayerController;
import com.mygdx.game.objects.SaveObject;
import com.mygdx.game.objects.Util;
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
import com.mygdx.game.states.Assets;
import com.mygdx.game.states.GameState;

public class KambojaMain extends ApplicationAdapter {
	
	//Analytics
	
	static GoogleAnalytics analytics;
	static String userID;
	//Resto
	
	private SpriteBatch sb;
	private Manager manager; //the list of states
	
	Texture loading;
	
	static KambojaMain instance;
	
	private static ArrayList<PlayerController> controllers; //all controllers connectes
	private static int deathsNumber = 5;
	private static int gameTime = 60 * 5;
	private static ArrayList<Player> postGamePlayers;
	private static String mapName;
	private static int weaponSize = 8;
	private static int playersize = 5;
	private static boolean items = true; 
	public static final double SENSITIVITY = 1.5f;
	public static int randomIndex = 4;
	public static boolean[] mapUnlocked = new boolean[]{true, true, true, false, false, false, false, true};
	public static boolean[] weaponUnlocked = new boolean[] {true, true, true, true, true, true, true, true};
	public static int level = 1;
	public static int experience = 0;
	public static int maxExperience = 10000;
	public static boolean vaporAmount = false;
	static SteamUser steamUser;
	public static boolean gameAlive;
	
	AssetManager assets;
	
	ShapeRenderer sr;

	
	public static KambojaMain getInstance() {
		return instance;
	}
	
	public KambojaMain() {
		assets = new AssetManager();
	}
	
	public AssetManager getAssets() {
		return assets;
	}
	
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

	public static String encrypt(String key, String initVector, String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());

            return Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String decrypt(String key, String initVector, String encrypted) throws RuntimeException{
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

            return new String(original);
        } catch (Exception ex) {
           throw new RuntimeException();
        }
    }
    
    public static String clampLength(String str, int size) {
    	return str.substring(0, size);
    }
    
    public static void loadGame() {
    	String encrypted;
    	
    	try {
			BufferedReader fw = new BufferedReader(new FileReader(new File("SavesDir/save.sav")));
			encrypted = fw.readLine();
			fw.close();
			
	    	String save = decrypt(clampLength("" + steamUser.getSteamID() + steamUser.getSteamID(), 16), "RandomInitVector", encrypted);

	    	System.out.println(save);
	    	
	    	Json json = new Json();
	    	SaveObject so = json.fromJson(SaveObject.class, save);
	    	
	    	mapUnlocked = so.getMaps();
	    	weaponUnlocked = so.getWeapons();
	    	level = so.getLevel();
	    	experience = so.getExperience();
	    	
		}
    	catch(FileNotFoundException e) {
    		System.out.println("Save file not found, creating new one");
    		File file = new File("SavesDir/save.sav");
    		if(!file.exists()) {
    			try {
					file.createNewFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
    		}
    	}
    	catch (IOException e) {
			e.printStackTrace();
		}
    	catch(RuntimeException e) {
    		File file = new File("SavesDir/save.sav");
    		if(file.exists()) {
    			System.out.println("Save corrupted, deleting and creating new one");
    			file.delete();
    			try {
					file.createNewFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
    		}
    	}
    	
    }
    
    public static void saveGame() {
    	SaveObject so = new SaveObject();
		so.setMaps(mapUnlocked);
		so.setWeapons(weaponUnlocked);
		so.setLevel(level);
		so.setExperience(experience);
		
		Json json = new Json();

		String encrypted = encrypt(clampLength("" + steamUser.getSteamID() + steamUser.getSteamID(), 16), "RandomInitVector", json.toJson(so));
		//System.out.println(encrypted);
		
		try {
			FileWriter fw = new FileWriter(new File("SavesDir/save.sav"));
			fw.write(encrypted);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
	public void create () {
		

		MultiPrintStream logTxt;
		try {
			logTxt = new MultiPrintStream(System.err, new PrintStream(new File ("log.txt")));
			System.setErr(logTxt);
			//System.setOut(logTxt);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		GameMusic.initialize();
		instance = this;
		sr = new ShapeRenderer();
		Assets.LOADSHIT(assets);

		loading = new Texture("imgs/loading.png");

		sb = new SpriteBatch();

		try {
			//SteamAPI.restartAppIfNecessary(747110);
		    if (!SteamAPI.init()) {
		    	System.err.println("Steam not connected");
		       System.exit(1);
		    }
		} catch (SteamException e) {
		    e.printStackTrace();
		}
		
		steamUser = new SteamUser(new SteamUserCallback() {
			public void onValidateAuthTicket(SteamID steamID, AuthSessionResponse authSessionResponse,
					SteamID ownerSteamID) {				
			}
			public void onMicroTxnAuthorization(int appID, long orderID, boolean authorized) {				
			}
		});
		
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest((""+steamUser.getSteamID().getAccountID()).getBytes(StandardCharsets.UTF_8));
			userID = bytesToHex(hash).substring(0, 9);
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		
		gameAlive = true;
				
		analytics = new GoogleAnalytics(userID, "UA-63640521-2");
		analytics.sessionStart();
		
		loadGame();
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
			GameMusic.MUSIC_VOLUME = Float.parseFloat(configs.get("MusicVolume"));
			
			vaporAmount = Boolean.parseBoolean(configs.get("Vaporwave"));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		capsule = new Texture("menu/postgame/lvl_capsule.png");
		loading_bar = new Texture("menu/postgame/bar_front.png");
		loading_case = new Texture("menu/postgame/bar_back.png");

	}
	
	private static String bytesToHex(byte[] hash) {
	    StringBuffer hexString = new StringBuffer();
	    for (int i = 0; i < hash.length; i++) {
	    String hex = Integer.toHexString(0xff & hash[i]);
	    if(hex.length() == 1) hexString.append('0');
	        hexString.append(hex);
	    }
	    return hexString.toString();
	}
	
	Texture capsule, loading_bar, loading_case;
	
	public static Texture getTexture(String name) {
		return getInstance().assets.get(name, Texture.class);
	}
	
	public static BitmapFont getFont(String name) {
		return getInstance().assets.get(name, BitmapFont.class);
	}
	
	public void resize(int width, int height){
		//manager.resize(width, height);
	}
	
	boolean managerLoaded = false;
	
	float gcTimer = 0;
	float smoothProgress = 0;
	float alpha = 0;
	public void render () {
		if (SteamAPI.isSteamRunning()) {
			SteamAPI.runCallbacks();
		}
		
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
		
		
		if(alpha == 1) {
			if(!managerLoaded) {
				manager = new Manager();
				manager.create();
				managerLoaded = true;
			}
			manager.update(Gdx.graphics.getDeltaTime());
			manager.render(sb);
		}
		else {
			
			sb.setProjectionMatrix(Util.getNormalProjection());
			smoothProgress += (assets.getProgress() - smoothProgress)/5.0f;
			sb.begin();
			
			sb.draw(loading_case,
					(1920 - (capsule.getWidth() + loading_bar.getWidth()))/2f + capsule.getWidth() - 50,
					(1080 - loading_case.getHeight())/2f,
					0, 0, 
					loading_case.getWidth(), loading_case.getHeight(),
					1, 1, 0,
					0, 0,
					loading_case.getWidth(),
					loading_case.getHeight(),
					false, false);
			sb.draw(loading_bar,
					(1920 - (capsule.getWidth() + loading_bar.getWidth()))/2f + capsule.getWidth() - 50,
					(1080 - loading_bar.getHeight())/2f,
					0, 0, 
					smoothProgress * loading_bar.getWidth(), loading_bar.getHeight(),
					1, 1, 0,
					0, 0,
					(int)(smoothProgress * loading_bar.getWidth()),
					loading_bar.getHeight(),
					false, false);
			sb.draw(capsule,
					(1920 - (capsule.getWidth() + loading_bar.getWidth()))/2f,
					(1080 - capsule.getHeight())/2f,
					capsule.getWidth()/2f, capsule.getHeight()/2f, 
					capsule.getWidth(), capsule.getHeight(),
					1, 1, smoothProgress * 1080,
					0, 0,
					capsule.getWidth(),
					capsule.getHeight(),
					false, false);
			sb.end();
			
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			
			sr.begin(ShapeType.Filled);
			sr.setColor(0, 0, 0, alpha);
			sr.rect(0, 0, 1920, 1080);
			sr.end();
			
			if(assets.update()) {
				alpha += Gdx.graphics.getDeltaTime();
				if(alpha > 1) alpha = 1;
			}
			
			Gdx.gl.glDisable(GL20.GL_BLEND);
		}

	}
	
	@Override
	public void dispose(){
		super.dispose();
		saveGame();
		SteamAPI.shutdown();
		analytics.sessionEnd();
		gameAlive = false;
	}

	public static void event(String category, String action, String label, HashMap<String, Object> cds) {
		
		//HitData data = analytics.constructBaseHit();
		
		
	}
//	
//	private static String generateUnique128ID() {
//		
//		String id = "";
//		
//		for(int i = 0; i < 128; i ++) {
//			int rnd = (int) (Math.random() * 122);
//			while(
//					(rnd < 48) ||
//					(rnd > 57 && rnd < 65) ||
//					(rnd > 90 && rnd < 97) ||
//					(rnd > 122)
//					) {
//				rnd = (int) (Math.random() * 122);
//			}
//			id += new String(new char[] {(char) rnd});
//			
//		}
//		return id;
//		
//	}
}
