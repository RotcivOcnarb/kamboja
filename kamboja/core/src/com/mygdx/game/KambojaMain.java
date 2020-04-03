package com.mygdx.game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
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
import com.codedisaster.steamworks.SteamResult;
import com.codedisaster.steamworks.SteamUser;
import com.codedisaster.steamworks.SteamUserCallback;
import com.mygdx.game.analytics.GoogleAnalytics;
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
	
	public static boolean STEAM_ACTIVATION = false;
	
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
	
	
	//Instance variables
	
	AssetManager assets;	
	ShapeRenderer sr;
	BitmapFont font;

	
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

	/** Encrypts a string using a key and an initVector using AES encryption 
	 * 
	 * @param key The key used to encrypt
	 * @param initVector have no idea what this is for, just use a random string
	 * @param value the string you want to encrypt
	 * @return the encrypted string
	 */
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
	/** Decrypts a AES encrypted string, assuming you have the key and the initVector used
	 * 
	 * @param key the key used to encrypt
	 * @param initVector the initVector used to Encrypt
	 * @param encrypted the encrypted string
	 * @return the decrypted string (original)
	 * @throws RuntimeException
	 */
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
    
    /** Clamps a string to a maximum size
     * 
     * @param str the string you want to clamp
     * @param size the maximum size
     * @return
     */
    public static String clampLength(String str, int size) {
    	return str.substring(0, size);
    }
    /** Loads a game data from folder "SavesDir/save.sav" into the game
     * 
     */
    public static void loadGame() {
    	String encrypted;
    	
    	try {
			BufferedReader fw = new BufferedReader(new FileReader(new File("SavesDir/save.sav")));
			encrypted = fw.readLine();
			fw.close();
			
			String accountKey = userID;
			
	    	String save = decrypt(clampLength("" + accountKey + accountKey, 16), "RandomInitVector", encrypted);

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
    
    /**
     * Saves a game data of current data into "SavesDir/save.sav" file
     */
    
    public static void saveGame() {
    	SaveObject so = new SaveObject();
		so.setMaps(mapUnlocked);
		so.setWeapons(weaponUnlocked);
		so.setLevel(level);
		so.setExperience(experience);
		
		Json json = new Json();

		String accountKey = userID;
		
		String encrypted = encrypt(clampLength("" + accountKey + accountKey, 16), "RandomInitVector", json.toJson(so));
		//System.out.println(encrypted);
		
		try {
			FileWriter fw = new FileWriter(new File("SavesDir/save.sav"));
			fw.write(encrypted);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    /** LibGDX default create method
     * 
     */
	public void create () {
		
		
		try {
			DatagramSocket ds = new DatagramSocket();
			InetAddress ip = InetAddress.getLocalHost();
			
			byte[] bts = "batatinha".getBytes();
			
			DatagramPacket dp = new DatagramPacket(bts, bts.length, ip, 3224);
			ds.send(dp);
			
			ds.close();
			
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
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

		if(STEAM_ACTIVATION) {
			try {
				SteamAPI.loadLibraries();
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
				public void onEncryptedAppTicket(SteamResult result) {
					
				}
			});
		}
		
		

		
		gameAlive = true;
				

		
		loadGame();
		//Loads the config.ini file and sets all the parameters
		HashMap<String, String> configs = new HashMap<String, String>();
		try {
			File conf = new File("config.ini");
			
			BufferedReader br = new BufferedReader(new FileReader(conf));
			
			
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
			
			if(!configs.containsKey("identifier")) {
				
				if(STEAM_ACTIVATION) {
					configs.put("identifier", steamUser.getSteamID().toString());
					configs.put("acc_identifier", steamUser.getSteamID().getAccountID() + "");
				}
				else {
					configs.put("identifier", randomKey(10));
					configs.put("acc_identifier", randomKey(10));
				}
				
			}
			
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
		
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest((""+configs.get("acc_identifier")).getBytes(StandardCharsets.UTF_8));
			userID = bytesToHex(hash).substring(0, 9);
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		analytics = new GoogleAnalytics(userID, "UA-63640521-2");
		
		capsule = new Texture("menu/postgame/lvl_capsule.png");
		loading_bar = new Texture("menu/postgame/bar_front.png");
		loading_case = new Texture("menu/postgame/bar_back.png");

	}
	
	
	/** Convert a byte array to its text representation in Hexadecimal
	 * 
	 * @param hash the byte array
	 * @return A string that represents this byte array in hexadecimal
	 */
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
	
	/** Gets a Asset loaded texture. The texture needs to be loaded to this method to function correctly
	 *  @see Assets
	 * 
	 * @param name the texture file path
	 * @return the Texture object
	 */
	public static Texture getTexture(String name) {
		return getInstance().assets.get(name, Texture.class);
	}
	/** Gets a Asset loaded font. The font needs to be loaded to this method to function correctly
	 *  @see Assets
	 * 
	 * @param name the font file path
	 * @return the Font object
	 */
	public static BitmapFont getFont(String name) {
		return getInstance().assets.get(name, BitmapFont.class);
	}
	
	boolean managerLoaded = false;
	
	float gcTimer = 0;
	float smoothProgress = 0;
	float alpha = 0;
	
	/** LibGDX default render method
	 * 
	 */
	public void render () {
		//Steam execution loop
		if(STEAM_ACTIVATION) {
			if (SteamAPI.isSteamRunning()) {
				SteamAPI.runCallbacks();
			}
		}
		
		//Resets screen for next frame draw
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);//clear screen
		Gdx.gl.glEnable(GL20.GL_BLEND); //enables transparency
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.input.setCursorCatched(true); //hides the cursor
		
		if(alpha == 1) {
			
			//Creates the State Manager, if it isn't already created
			if(!managerLoaded) {
				manager = new Manager(); //If you are studying, this is the next class you may want to check
				manager.create();
				managerLoaded = true;
			}
			//Updates and renders the scenes
			manager.update(Gdx.graphics.getDeltaTime());
			manager.render(sb);
						
			// Draws FPS at the top of the screen, regardless of scene
			if(GameState.DEBUG) {			
				if(assets.isLoaded("fonts/olivers barney.ttf")) {
					if(font == null)
						font = (BitmapFont) assets.get("fonts/olivers barney.ttf");
					sb.setProjectionMatrix(Util.getNormalProjection());
					
					sb.begin();
					font.draw(sb, (int)Math.floor(1/Gdx.graphics.getDeltaTime()) + "", 10, Gdx.graphics.getHeight() - 20);
					sb.end();
				}
			}
		}
		else {
			//Draw load screen, if the assets hasn't been loaded yet
			
			sb.setProjectionMatrix(Util.getNormalProjection());
			smoothProgress += (assets.getProgress() - smoothProgress)/5.0f; //percentage of loading progression
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
			
			//Blackscreen fade in / out
			sr.begin(ShapeType.Filled);
			sr.setColor(0, 0, 0, alpha);
			sr.rect(0, 0, 1920, 1080);
			sr.end();
			
			if(assets.update()) { //Actually LOADS things (if true, means load is compelte)
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
		if(STEAM_ACTIVATION)
			SteamAPI.shutdown();
		gameAlive = false;
	}
	
	public String randomKey(int size) {
		
		String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		
		String key = "";
		
		for(int i = 0; i < size; i ++) {
			int rnd = (int) (Math.random() * characters.length());
			key += characters.substring(rnd, rnd+1);
		}
		
		return key;
		
	}

	/** Sends an event to Google Analytics
	 * 
	 * @param category
	 * @param action
	 * @param label
	 * @param cds Custom Dimensions list
	 */
	public static void event(String category, String action, String label, HashMap<String, String> cds) {
		analytics.event(category, action, label, cds);
	}
	/** Sends an event to Google Analytics
	 * 
	 * @param category
	 * @param action
	 * @param label
	 */
	public static void event(String category, String action, String label) {
		analytics.event(category, action, label);
	}
	/** Sends an screenview to Google Analytics
	 * 
	 * @param screenName the screenName
	 */
	public static void screenview(String screenName) {
		analytics.screenview(screenName);
	}

}
