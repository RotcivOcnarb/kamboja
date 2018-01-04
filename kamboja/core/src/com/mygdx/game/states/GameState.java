package com.mygdx.game.states;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.KambojaMain;
import com.mygdx.game.Manager;
import com.mygdx.game.State;
import com.mygdx.game.controllers.Gamecube;
import com.mygdx.game.controllers.GenericController;
import com.mygdx.game.controllers.Playstation3;
import com.mygdx.game.controllers.XBox;
import com.mygdx.game.objects.BetterBot;
import com.mygdx.game.objects.BotController;
import com.mygdx.game.objects.Bullet;
import com.mygdx.game.objects.GameMusic;
import com.mygdx.game.objects.GamePause;
import com.mygdx.game.objects.Item;
import com.mygdx.game.objects.MyContactListener;
import com.mygdx.game.objects.PersistentParticleEffect;
import com.mygdx.game.objects.Player;
import com.mygdx.game.objects.Util;
import com.mygdx.game.objects.map.Block;
import com.mygdx.game.objects.map.BreakableBlock;
import com.mygdx.game.objects.map.HoleBlock;
import com.mygdx.game.objects.map.IslandBackground;
import com.mygdx.game.objects.map.KambojaMap;
import com.mygdx.game.objects.map.UnbreakableBlock;
import com.mygdx.game.objects.map.WaterBlock;

import box2dLight.RayHandler;
import net.dermetfan.gdx.physics.box2d.Box2DMapObjectParser;

public class GameState extends State{
	
	public static final float UNIT_SCALE = 45;
	
	private GamePause gamepause;
	
	private OrthographicCamera camera;
	private TiledMap tiledMap;
	private World world;
	private Box2DDebugRenderer b2dr; //desenha outline dos Bodies no mundo
	private ArrayList<Block> blocks;
	private static int mapWidth;
	private static int mapHeight;
	private Rectangle2D bounds;
	
	int bitmap[][];
	
	private RayHandler handler;
	private BitmapFont font;
	private BitmapFont timeFont;
	
	private boolean inputBlocked;
	
	Box2DMapObjectParser parser;
	
	private int tilesize = 32;
	
	private ArrayList<Bullet> bullets;
	private ArrayList<Body> flameParticles;
	
	private ShapeRenderer sr;
	
	private boolean pause;
	
	private boolean intro;
	private boolean outro;
	private float opacity = 1;
	private float endTimer;
	private boolean end;
	private String musicName;
	
	private float menuPos = 0;
	private float targetMenuPos = 0;
	
	private GlyphLayout layout;
	
	private float timer;
	private float timeCount;

	private Sound ko[] = new Sound[3];
	private Sound gameover[] = new Sound[3];
	private Sound three, two, one, start;
	private Sound bottle[] = new Sound[2];
	
	private ArrayList<Player> players;
	
	private static ArrayList<Body> forRemoval;	
	
	PersistentParticleEffect bloodEffect;
	PersistentParticleEffect shellEffect;
	PersistentParticleEffect rockEffect;
	PersistentParticleEffect skullEffect;
	PersistentParticleEffect bloodSpill;

	private ParticleEffect explosion;
	private ParticleEffectPool explosionPool;
	private ArrayList<PooledEffect> explosionEffects;
	
	private ArrayList<Item> items;
	
	private float itemTimer = 0;
	
	public static boolean DEBUG;
	public static boolean LIGHTS;
	public static boolean BETA_ITEMS;
	public static boolean SFX = true;
	public static int DIFFICULTY = 0;
	public static float VOLUME = 1;

	private boolean said_three, said_two, said_one, said_start;
	private IslandBackground islandBackground;
	
	//shader stuff
	
	ShaderProgram overlay;
	FrameBuffer beforeBlood;
	FrameBuffer afterBlood;
	
	KambojaMap kambojaMap;
	

	
	Vector2 med = new Vector2();
	Array<Body> bodies = new Array<Body>();
	
	public static void removeBody(Body b){
		if(!forRemoval.contains(b)){
			forRemoval.add(b);
		}
	}
	
	public GameState(Manager manager) {
		super(manager);
	}

	public void addBullet(Bullet b){
		getBullets().add(b);
	}
	
	
	public void screenshake(float amount){
		getCamera().position.add((float)((Math.random()*2)-1)*amount, (float)((Math.random()*2)-1)*amount, 0);
		
	}
	
	public void dispose(){
		sr.dispose();
		getTiledMap().dispose();
		world.dispose();
		b2dr.dispose();
		explosion.dispose();
		if(islandBackground != null)
		islandBackground.dispose();
		if(handler != null)
		handler.dispose();
		font.dispose();
		timeFont.dispose();
		for(Item i : getItems()){
			i.dispose();
		}
		for(Player p : getPlayers()){
			p.dispose();
		}
		for(Block b : getBlocks()){
			b.dispose();
		}
		for(Bullet b : getBullets()){
			b.dispose();
		}
		ko[0].dispose();
		ko[1].dispose();
		ko[2].dispose();
		
		gameover[0].dispose();
		gameover[1].dispose();
		gameover[2].dispose();
		
		three.dispose();
		two.dispose();
		one.dispose();
		start.dispose();
		
		bottle[0].dispose();
		bottle[1].dispose();
		
		gamepause.dispose();
	}
	
	boolean exitMap = false;
	
	public void create() {
		
		//shader stuff
		

		parser = new Box2DMapObjectParser(1f/UNIT_SCALE);
		
		beforeBlood = new FrameBuffer(Format.RGBA8888, 1920, 1080, false);
		afterBlood = new FrameBuffer(Format.RGBA8888, 1920, 1080, false);

		overlay = new ShaderProgram(
				Gdx.files.internal("shaders/default.vs"),
				Gdx.files.internal("shaders/overlay.fs"));
		ShaderProgram.pedantic = false;
	
		if (overlay.getLog().length()!=0)
			System.out.println(overlay.getLog());
		
		setBullets(new ArrayList<Bullet>());
		setFlameParticles(new ArrayList<Body>());
		
		gamepause = new GamePause(this);
		
		ko[0] = Gdx.audio.newSound(Gdx.files.internal("audio/ko1.ogg"));
		ko[1] = Gdx.audio.newSound(Gdx.files.internal("audio/ko2.ogg"));
		ko[2] = Gdx.audio.newSound(Gdx.files.internal("audio/ko3.ogg"));
		
		gameover[0] = Gdx.audio.newSound(Gdx.files.internal("audio/gameover1.ogg"));
		gameover[1] = Gdx.audio.newSound(Gdx.files.internal("audio/gameover2.ogg"));
		gameover[2] = Gdx.audio.newSound(Gdx.files.internal("audio/gameover3.ogg"));
		
		three = Gdx.audio.newSound(Gdx.files.internal("audio/three.ogg"));
		two = Gdx.audio.newSound(Gdx.files.internal("audio/two.ogg"));
		one = Gdx.audio.newSound(Gdx.files.internal("audio/one.ogg"));
		start = Gdx.audio.newSound(Gdx.files.internal("audio/start.ogg"));
		
		bottle[0] = Gdx.audio.newSound(Gdx.files.internal("audio/weapon/bottle1.ogg"));
		bottle[1] = Gdx.audio.newSound(Gdx.files.internal("audio/weapon/bottle2.ogg"));
		
		said_three = false;
		said_two = false;
		said_one = false;
		said_start = false;
	
		if(getCamera() == null)
		setCamera(new OrthographicCamera());
		
		getCamera().setToOrtho(false, 1920, 1080);
		getCamera().zoom = 0.00001f;
		
		inputBlocked = true;
		
		timeCount = 0;
		
		opacity = 1;
		
		itemTimer = (float) (Math.random() * 10 + 10);
		
		menuPos = 0;
		targetMenuPos = 0;
		
		setIntro(true);
		setOutro(false);
		setEnd(false);
		
		if(getItems() == null)
		setItems(new ArrayList<Item>());
		
		getItems().clear();
		
		setPause(false);
		
		sr = new ShapeRenderer();
		
		if(bounds == null)
		bounds = new Rectangle2D.Double();
		
		System.out.println("Loading map (" + KambojaMain.getMapName() + ")");
		setTiledMap(new TmxMapLoader().load(KambojaMain.getMapName()));
		
		bitmap = new int[getTiledMap().getProperties().get("width", Integer.class)][getTiledMap().getProperties().get("height", Integer.class)];
		
		if(KambojaMain.getMapName().endsWith("island.tmx")){
			islandBackground = new IslandBackground();
		}
		else{
			islandBackground = null;
		}
		
		
		System.out.println(kambojaMap);
		
		
		endTimer = 0;

		world = new World(new Vector2(0, 0), false);

		world.setContactListener(new MyContactListener());
		b2dr = new Box2DDebugRenderer();
		
		tilesize = getTiledMap().getProperties().get("tilewidth", Integer.class);
		mapWidth = getTiledMap().getProperties().get("width", Integer.class);
		mapHeight = getTiledMap().getProperties().get("height", Integer.class);
		getCamera().position.set(mapWidth/2*tilesize/UNIT_SCALE, mapHeight/2*tilesize/UNIT_SCALE, 0);

		if(getPlayers() == null)
		setPlayers(new ArrayList<Player>());
		
		getPlayers().clear();
		
		if(layout == null)
		layout = new GlyphLayout();
		

		
		
		timer = 0;

		FreeTypeFontGenerator ftfg;
		FreeTypeFontParameter param;
		ftfg = new FreeTypeFontGenerator(Gdx.files.internal("fonts/outlander.ttf"));
		param = new FreeTypeFontParameter();
		param.size = (int) (100 * Gdx.graphics.getDensity());
		param.color = new Color(0.03f, 0.03f, 0.03f, 1);
		param.borderWidth = 2;
		param.borderColor = new Color(1, 0.9f, 0.9f, 1);
		param.shadowColor = new Color(0, 0, 0, 0.7f);
		param.shadowOffsetX = 3;
		param.shadowOffsetY = 3;
		font = ftfg.generateFont(param);
		param.size = (int) (50 * Gdx.graphics.getDensity());
		timeFont = ftfg.generateFont(param);
		
		ftfg.dispose();
		
		if(forRemoval == null)
		forRemoval = new ArrayList<Body>();
		
		forRemoval.clear();
		
		Texture[] bloods = new Texture[5];
		for(int i = 0; i < 5; i ++)
			bloods[i] = new Texture("particles/blood" + (i+1) + ".png");
		
		bloodEffect = new PersistentParticleEffect(bloods);
		bloodEffect.setMinVel(new Vector2(-0.05f, -0.05f));
		bloodEffect.setMaxVel(new Vector2(0.05f, 0.05f));
		bloodEffect.setMinLinDamp(10);
		bloodEffect.setMaxLinDamp(10);
		bloodEffect.setMinScale(1f/UNIT_SCALE * .3f);
		bloodEffect.setMaxScale(1f/UNIT_SCALE * .3f);
		
		bloodSpill = new PersistentParticleEffect(new Texture("particles/blood_spill.png"));
		bloodSpill.setMinScale(1f/UNIT_SCALE);
		bloodSpill.setMaxScale(1f/UNIT_SCALE);
		
		shellEffect = new PersistentParticleEffect(new Texture("imgs/weapons/shell.png"));
		shellEffect.setMinLinDamp(10);
		shellEffect.setMaxLinDamp(10);
		shellEffect.setMinScale(1f/UNIT_SCALE);
		shellEffect.setMaxScale(1f/UNIT_SCALE);
		
		Texture rocks[] = new Texture[4];
		
		for(int i = 0; i < 4; i ++)
			rocks[i] = new Texture("particles/rock" + (i+1) + ".png");
		
		rockEffect = new PersistentParticleEffect(rocks);
		rockEffect.setMinVel(new Vector2(-0.05f, -0.05f));
		rockEffect.setMaxVel(new Vector2(0.05f, 0.05f));
		rockEffect.setMinLinDamp(10);
		rockEffect.setMaxLinDamp(10);
		rockEffect.setMinScale(1f/UNIT_SCALE * .03f);
		rockEffect.setMaxScale(1f/UNIT_SCALE * .03f);
		
		skullEffect = new PersistentParticleEffect(new Texture("imgs/skull.png"));
		skullEffect.setMinScale(1f/UNIT_SCALE);
		skullEffect.setMaxScale(1f/UNIT_SCALE);
		
		explosion = new ParticleEffect();
		explosion.load(Gdx.files.internal("particles/explosion.par"), Gdx.files.internal("particles"));
		explosion.scaleEffect(1f/UNIT_SCALE / 6f);
		explosionPool = new ParticleEffectPool(explosion, 0, 5);
		explosionEffects = new ArrayList<PooledEffect>();		
		
		if(getBlocks() == null)
		setBlocks(new ArrayList<Block>());
		
		getBlocks().clear();
		
		
		
		TiledMapTileLayer bg = ((TiledMapTileLayer)getTiledMap().getLayers().get("breakable"));
		if(bg != null){
			for(int i = 0; i < mapWidth; i ++){
				for(int j = 0; j < mapHeight; j ++){
					//tiles background
					Cell c = bg.getCell(i, j);
					if(c != null){ 
						TextureRegion tr = c.getTile().getTextureRegion();
						Block block = new BreakableBlock(tr, i*tr.getRegionWidth() / UNIT_SCALE, j*tr.getRegionHeight() / UNIT_SCALE,
								tr.getRegionWidth() / UNIT_SCALE,
								tr.getRegionHeight() / UNIT_SCALE, world, this, c);
								
						getBlocks().add(block);
					}
					
				}
			}
		}
		else{
			System.out.println("no -breakable- layer found (you put the breakable blocks there)");
		}
		bg = ((TiledMapTileLayer)getTiledMap().getLayers().get("unbreakable"));
		if(bg != null){
			for(int i = 0; i < mapWidth; i ++){
				for(int j = 0; j < mapHeight; j ++){
					//tiles background
					Cell c = bg.getCell(i, j);
					if(c != null){ 
						TextureRegion tr = c.getTile().getTextureRegion();
						Block block = new UnbreakableBlock(tr, i*tr.getRegionWidth() / UNIT_SCALE, j*tr.getRegionHeight() / UNIT_SCALE,
								tr.getRegionWidth() / UNIT_SCALE,
								tr.getRegionHeight() / UNIT_SCALE, world, this, c);
								
						getBlocks().add(block);
					}
					
				}
			}
		}
		else{
			System.out.println("no -unbreakable- layer found (you put the unbreakable blocks there)");
		}
		bg = ((TiledMapTileLayer)getTiledMap().getLayers().get("fall"));
		if(bg != null){
			for(int i = 0; i < mapWidth; i ++){
				for(int j = 0; j < mapHeight; j ++){
					//tiles background
					Cell c = bg.getCell(i, j);
					if(c != null){ 
						TextureRegion tr = c.getTile().getTextureRegion();
						Block block = new HoleBlock(tr, i*tr.getRegionWidth() / UNIT_SCALE, j*tr.getRegionHeight() / UNIT_SCALE,
								tr.getRegionWidth() / UNIT_SCALE,
								tr.getRegionHeight() / UNIT_SCALE, world, this);
								
						getBlocks().add(block);
					}
					
				}
			}
		}
		else{
			System.out.println("no -fall- layer found (you put the blocks who players can fall there)");
		}
		bg = ((TiledMapTileLayer)getTiledMap().getLayers().get("water"));
		if(bg != null){
			for(int i = 0; i < mapWidth; i ++){
				for(int j = 0; j < mapHeight; j ++){
					//tiles background
					Cell c = bg.getCell(i, j);
					if(c != null){ 
						TextureRegion tr = c.getTile().getTextureRegion();
						Block block = new WaterBlock(tr, i*tr.getRegionWidth() / UNIT_SCALE, j*tr.getRegionHeight() / UNIT_SCALE,
								tr.getRegionWidth() / UNIT_SCALE,
								tr.getRegionHeight() / UNIT_SCALE, world, this, c);
								
						getBlocks().add(block);
					}
					
				}
			}
		}
		else{
			System.out.println("no -water- layer found (you put the blocks that the player can shoot through, but can not pass)");
		}

		for(int i = 0; i < KambojaMain.getControllers().size(); i ++){
			if(KambojaMain.getControllers().get(i) != null)
				createPlayer(i);
		}
		
		if(tiledMap.getProperties().get("mapClass") != null) {
			String mapClass = tiledMap.getProperties().get("mapClass").toString();
			
			if(mapClass != null) {
				try {
					kambojaMap = (KambojaMap)Class.forName("com.mygdx.game.objects.map." + mapClass).newInstance();
					kambojaMap.setGameState(this);
					kambojaMap.create();
				} catch (InstantiationException e1) {
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		}
		else {
			kambojaMap = null;
		}

		BodyDef def = new BodyDef();
		def.type = BodyType.StaticBody;
		def.position.set(mapWidth * tilesize / 2 / UNIT_SCALE, mapHeight * tilesize /2/ UNIT_SCALE);
		
		Body b = world.createBody(def);
		PolygonShape s = new PolygonShape();
		s.setAsBox((mapWidth*tilesize)/2f / UNIT_SCALE, tilesize / UNIT_SCALE, new Vector2(0, ((mapHeight * tilesize)/2f + tilesize)/UNIT_SCALE), 0);
		b.createFixture(s, 1);

		s.setAsBox((mapWidth*tilesize)/2f / UNIT_SCALE, tilesize / UNIT_SCALE, new Vector2(0, -((mapHeight * tilesize)/2f + tilesize)/UNIT_SCALE), 0);
		b.createFixture(s, 1);

		s.setAsBox(tilesize / UNIT_SCALE, (mapHeight*tilesize)/2f / UNIT_SCALE, new Vector2(((mapWidth * tilesize)/2f + tilesize)/UNIT_SCALE, 0), 0);
		b.createFixture(s, 1);

		s.setAsBox(tilesize / UNIT_SCALE, (mapHeight*tilesize)/2f / UNIT_SCALE, new Vector2(-((mapWidth * tilesize)/2f + tilesize)/UNIT_SCALE, 0), 0);
		b.createFixture(s, 1);
		
		s.dispose();
		
		b.setUserData("BLOCK");
		
		if(LIGHTS){
			handler = new RayHandler(world);
			handler.setCombinedMatrix(getCamera().combined);
	
			try{
				float ambientLight = Float.parseFloat(getTiledMap().getProperties().get("ambientLight", String.class));
				handler.setAmbientLight(0, 0, 0, ambientLight);
			}
			catch(Exception e ){
				System.out.println("[MUST] no -ambientLight- propertie found, add it to the map properties (0.0 to 1.0)");
				exitMap = true;
			}
		}
		
		if(LIGHTS){
			MapLayer ml = getTiledMap().getLayers().get("Light");
			if(ml != null){
				for(MapObject mo : ml.getObjects()){
					if(mo.getProperties().get("type").equals("light")){
						float x = mo.getProperties().get("x", Float.class);
						float y = mo.getProperties().get("y", Float.class);
						float width = mo.getProperties().get("width", Float.class);
						float height = mo.getProperties().get("height", Float.class);
						int rays = Integer.parseInt(mo.getProperties().get("rays", String.class));
						float distance = Float.parseFloat(mo.getProperties().get("distance", String.class));
						
						new box2dLight.PointLight(handler, rays, new Color(1, 1, 1, 0.3f), distance, (x+width/2) / UNIT_SCALE, (y+height/2) / UNIT_SCALE);
					}
				}
			}
			else{
				System.out.println("no -Light- layer found (you put your lights there)\n"
						+ "Lights should be objects with properties:\n"
						+ "\ttype: \"light\" (no quotes)"
						+ "\trays: amount of raycasts (default 50)\n"
						+ "\tdistance: distance the light can travel");
			}
		}
		
		if(exitMap){
			System.out.println("Fix the [MUST] errors and run again");
			System.exit(1);
		}
		
		MapLayer layer2d = tiledMap.getLayers().get("BOX2D");
		if(layer2d != null) {
			parser.load(world, layer2d);
			System.out.println("Loaded " + parser.getBodies().size + " BOX2D objects!");
		}
//		if(tiledMap.getProperties().get("music") != null)
//		musicName = tiledMap.getProperties().get("music").toString();
//		else
//			musicName = "the_league_of_mice";
		
	}
	
	public void setInSpace() {
		bloodEffect.setMinLinDamp(0);
		bloodEffect.setMaxLinDamp(0);
		
		bloodSpill.setMinLinDamp(0);
		bloodSpill.setMaxLinDamp(0);
		
		shellEffect.setMinLinDamp(0);
		shellEffect.setMaxLinDamp(0);
		
		rockEffect.setMinLinDamp(0);
		rockEffect.setMaxLinDamp(0);
		
		skullEffect.setMinLinDamp(0);
		skullEffect.setMaxLinDamp(0);
	}

	public void addItem(Vector2 pos, int id){
		BodyDef def = new BodyDef();
		def.position.set(pos);
		def.type = BodyType.StaticBody;
		
		Body body = world.createBody(def);
		
		CircleShape shape = new CircleShape();
		shape.setRadius(16 / UNIT_SCALE);

		Fixture f = body.createFixture(shape, 1);
		f.setSensor(true);
		shape.dispose();
		
		Item i = new Item(body, id);
		f.setUserData(i);
		body.setUserData(i);
		
		getItems().add(i);
		
	}

	public void createPlayer(int id){
		
		BodyDef def = new BodyDef();
		def.type = BodyType.DynamicBody;
		
		MapLayer ml = getTiledMap().getLayers().get("Player");
		if(ml != null){
			boolean found = false;
			for(MapObject mo : ml.getObjects()){
				Object val = mo.getProperties().get("type");
				if(val.equals("player"+id)){
						float x = mo.getProperties().get("x", Float.class);
						float y = mo.getProperties().get("y", Float.class);
						float width = mo.getProperties().get("width", Float.class);
						float height = mo.getProperties().get("height", Float.class);
						float angle = mo.getProperties().get("angle", Float.class);
						def.position.set(new Vector2((x+width/2) / UNIT_SCALE, (y+height/2) /UNIT_SCALE));
						def.angle = angle;
						found = true;
				}
			}
			if(!found){
				System.out.println("[MUST] no -Player " +id+ "- spawn found\n"
						+ "Object must be created at layer -Player- with properties:\n"
						+ "\ttype: \"player" + id);
				exitMap = true;
			}
		}
		else{
			System.out.println("[MUST] no -Player- layer found (you put the player spawns there)");
			exitMap = true;
		}

		Body body = world.createBody(def);
		
		CircleShape circle = new CircleShape();
		circle.setRadius(10 / UNIT_SCALE);
		Fixture f = body.createFixture(circle, 0.8f);
		
		circle.dispose();
		
		Player player = null;
		if(KambojaMain.getControllers().get(id) instanceof BotController){
			player = new BetterBot(body, id, this);
		}
		else{
			player = new Player(body, id, this);
		}
		
		player.setAngle(new Vector2((float)Math.sin(Math.toRadians(def.angle)), (float)Math.cos(Math.toRadians(def.angle))));
		
		body.setUserData(player);
		f.setUserData(player);
		getPlayers().add(player);
	

	}
	
	public void showBlood(Vector2 worldCenter) {
		bloodEffect.setMinPos(worldCenter);
		bloodEffect.setMaxPos(worldCenter);
		bloodEffect.addParticle();
	}
	
	public void showBloodSpill(Vector2 worldCenter, float scale){
		bloodSpill.setMinPos(worldCenter);
		bloodSpill.setMaxPos(worldCenter);
		bloodSpill.setMinScale(1f/UNIT_SCALE * scale);
		bloodSpill.setMaxScale(1f/UNIT_SCALE * scale);
		bloodSpill.addParticle();
	}
	
	public void showShell(Vector2 worldCenter, Vector2 direction) {
		shellEffect.setMinPos(worldCenter);
		shellEffect.setMaxPos(worldCenter);
		shellEffect.setMinVel(direction);
		shellEffect.setMaxVel(direction);
		shellEffect.addParticle();
	}
	
	public void showRock(Vector2 worldCenter){
		
		rockEffect.setMinPos(worldCenter);
		rockEffect.setMaxPos(worldCenter);
		rockEffect.addParticle();

	}
	
	public void showSkull(Vector2 worldCenter, float angle){
		skullEffect.setMinPos(worldCenter);
		skullEffect.setMaxPos(worldCenter);
		skullEffect.setMinAngle(angle);
		skullEffect.setMaxAngle(angle);
		skullEffect.addParticle();
	}
	
	public void showExplosion(Vector2 pos){
		ParticleEffect pe = explosionPool.obtain();
		pe.setPosition(pos.x, pos.y);
		pe.reset();
		
		if(!explosionEffects.contains(pe))
		explosionEffects.add((PooledEffect) pe);
	}

	//Render stuff
	
	public void render(SpriteBatch sb) {
				
		
		beforeBlood.begin();
		Gdx.gl.glClearColor(0.0f, 0.6f, 0.9f, 0.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		if(kambojaMap != null)
			kambojaMap.behindRender(sb, camera);
		drawBackgroundTiles(sb);
		Gdx.gl.glDisable(GL20.GL_BLEND);
		beforeBlood.end();

		drawPersistentParticles(sb);
		drawBlocks(sb);
		drawItems(sb);
		drawParticles(sb);
		drawPlayersAndLight(sb);
		if(kambojaMap != null)
		kambojaMap.render(sb, camera);
		drawCeilingTiles(sb);
		drawUI(sb);
		drawDebug(sb);
		drawPause(sb);
		
		
		
	}

	public void drawBackgroundTiles(SpriteBatch sb){
		
		if(islandBackground != null){
			islandBackground.render(sb, getCamera());
		}
		
		sb.setProjectionMatrix(getCamera().combined);

		
		sb.begin();
		
		if(LIGHTS)
		handler.setCombinedMatrix(getCamera().combined);
		

		sb.setColor(1, 1, 1, 1);
		
		int mapWidth = getTiledMap().getProperties().get("width", Integer.class);
		int mapHeight = getTiledMap().getProperties().get("height", Integer.class);
		TiledMapTileLayer bg = ((TiledMapTileLayer)getTiledMap().getLayers().get("floor"));
		if(bg != null){
			for(int i = 0; i < mapWidth; i ++){
				for(int j = 0; j < mapHeight; j ++){
					
					//tiles background
					Cell c = bg.getCell(i, j);
					if(c != null){ 
						TextureRegion tr = c.getTile().getTextureRegion();
						sb.draw(
							tr,
							i*tr.getRegionWidth() / UNIT_SCALE,
							j*tr.getRegionHeight() / UNIT_SCALE,
							tr.getRegionWidth() / UNIT_SCALE,
							tr.getRegionHeight() / UNIT_SCALE);
					}
					
				}
			}
		}
		bg = ((TiledMapTileLayer)getTiledMap().getLayers().get("floor2"));
		if(bg != null){
			for(int i = 0; i < mapWidth; i ++){
				for(int j = 0; j < mapHeight; j ++){
					
					//tiles background
					Cell c = bg.getCell(i, j);
					if(c != null){ 
						TextureRegion tr = c.getTile().getTextureRegion();
						sb.draw(
							tr,
							i*tr.getRegionWidth() / UNIT_SCALE,
							j*tr.getRegionHeight() / UNIT_SCALE,
							tr.getRegionWidth() / UNIT_SCALE,
							tr.getRegionHeight() / UNIT_SCALE);
					}
					
				}
			}
		}
		sb.end();
	}
	
	public void drawPersistentParticles(SpriteBatch sb){

		afterBlood.begin();
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		bloodSpill.render(sb);
		
		Gdx.gl.glDisable(GL20.GL_BLEND);
		afterBlood.end();
		
		renderBloodOverlay(sb);
		
		bloodEffect.render(sb);
		shellEffect.render(sb);
		rockEffect.render(sb);
		skullEffect.render(sb);
	}
	
	public void renderBloodOverlay(SpriteBatch sb){
		
		overlay.begin();
		sb.setShader(overlay);
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		Gdx.graphics.getGL20().glActiveTexture(GL20.GL_TEXTURE0);
		beforeBlood.getColorBufferTexture().bind(0);
		overlay.setUniformi("beforeBlood", 0);
		
		Gdx.graphics.getGL20().glActiveTexture(GL20.GL_TEXTURE1);
		afterBlood.getColorBufferTexture().bind(1);
		overlay.setUniformi("afterBlood", 1);
		
		sb.setProjectionMatrix(Util.getNormalProjection());
		
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
		
		sb.begin();
		sb.draw(beforeBlood.getColorBufferTexture(),
				0, 0,
				1920, 1080,
				0, 0,
				1920, 1080,
				false, true);
		sb.end();
		
		Gdx.gl.glDisable(GL20.GL_BLEND);
		
		overlay.end();
		sb.setShader(null);

		
		sb.setProjectionMatrix(camera.combined);
	}
	
	public void drawBlocks(SpriteBatch sb){
		
		sb.begin();

		for(int i = getBlocks().size() - 1; i >= 0; i --){
			Block b = getBlocks().get(i);

			if(! b.render(sb)){
				for(int j = 0; j < 5; j ++)
				showRock(b.getBody().getWorldCenter());
				
				removeBody(b.getBody());
				getBlocks().remove(b);
			}
		}
		sb.end();
		
	}
	
	public void drawItems(SpriteBatch sb){
		
		sb.begin();
		
		sb.setColor(1, 1, 1, 1);
		for(int i = getItems().size() - 1; i >= 0; i --){
			Item item = getItems().get(i);
			if(item.render(sb)){
				removeBody(item.getBody());
				getItems().remove(item);
				item.dispose();
			}
		}
		
		sb.end();
	}
	
	public void drawParticles(SpriteBatch sb){
		
		sb.begin();

		for(int i = explosionEffects.size() - 1; i >= 0; i --){
			ParticleEffect pe = explosionEffects.get(i);
			pe.draw(sb);
			if(pe.isComplete()){
				explosionPool.free((PooledEffect) pe);
				explosionEffects.remove(i);
			}
		}
		sb.end();
	}
	
	public void drawPlayersAndLight(SpriteBatch sb){

		for(Player p : getPlayers()){
			p.render(sb);
		}
		for(int i = getBullets().size() - 1; i >= 0; i --){
			Bullet b = getBullets().get(i);
			if(b.render(sb)){
				//removeBody(b.getBody());
				b.dispose();
				getBullets().remove(b);
			}
		}
		
		if(LIGHTS)
		handler.updateAndRender();

		
	}
	
	public void drawCeilingTiles(SpriteBatch sb){
		sb.setProjectionMatrix(getCamera().combined);
		sb.begin();
		TiledMapTileLayer bg = ((TiledMapTileLayer)getTiledMap().getLayers().get("ceiling"));
		if(bg != null){
			for(int i = 0; i < mapWidth; i ++){
				for(int j = 0; j < mapHeight; j ++){
					
					//tiles ceiling
					Cell c = bg.getCell(i, j);
					if(c != null){ 
						TextureRegion tr = c.getTile().getTextureRegion();
						sb.draw(
							tr,
							i*tr.getRegionWidth() / UNIT_SCALE,
							j*tr.getRegionHeight() / UNIT_SCALE,
							tr.getRegionWidth() / UNIT_SCALE,
							tr.getRegionHeight() / UNIT_SCALE);
					}
					
				}
			}
		}
		sb.end();
	}
	
	public void drawUI(SpriteBatch sb){
		sb.setProjectionMatrix(Util.getNormalProjection());
		sb.begin();
		
		layout.setText(font, "GAME!");
		font.draw(sb, "GAME!", (1920 - layout.width)/2f, 1080/2 - menuPos + 1080 * 6 + 100);
		
		layout.setText(font, "START!");
		font.draw(sb, "START!", (1920 - layout.width)/2f, 1080/2 - menuPos + 1080 * 4 + 100);
		
		layout.setText(font, "1");
		font.draw(sb, "1", (1920 - layout.width)/2f, 1080/2 - menuPos + 1080 * 3 + 100);
		
		layout.setText(font, "2");
		font.draw(sb, "2", (1920 - layout.width)/2f, 1080/2 - menuPos + 1080 * 2 + 100);
		
		layout.setText(font, "3");
		font.draw(sb, "3", (1920 - layout.width)/2f, 1080/2 - menuPos + 1080 + 100);
		
		int timeNum = (int) (KambojaMain.getGameTime() - timeCount);
		
		String time = (KambojaMain.getGameTime() == -1) ? "00:00" : timeNum/60 + ":" + ((timeNum % 60 >= 10) ? timeNum % 60 : "0" + timeNum % 60);
		
		layout.setText(timeFont, time);
		timeFont.draw(sb, time, (1920 - layout.width)/2f, 1080 - 30);
		
		sb.end();
	}
	
	public void drawDebug(SpriteBatch sb){
		if(DEBUG)
		b2dr.render(world, getCamera().combined);
	}
	
	public void drawPause(SpriteBatch sb){
		
		sb.begin();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			sr.begin(ShapeType.Filled);
			sr.setColor(0, 0, 0, opacity);
			sr.box(0, 0, 0, 1920, 1080, 0);
			sr.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		sb.end();
		
		if(isPause()){
			sb.setProjectionMatrix(Util.getNormalProjection());
			gamepause.render(sb);
		}
	}
	
	//Update
	
	public void update(float delta) {
		//GameMusic.fadeOut(GameMusic.MAIN_MENU);
				
		if(islandBackground != null){
			islandBackground.update(delta);
		}
		
		for(int i = 0; i < bitmap.length; i ++ ) {
			for(int j = 0; j < bitmap[0].length; j ++ ) {
				bitmap[i][j] = 0;
			}
		}
		for(int i = 0; i < bitmap.length; i ++ ) {
			for(int j = 0; j < bitmap[0].length; j ++ ) {
				final int x = i;
				final int y = j;
				world.QueryAABB(
						new QueryCallback() {
					public boolean reportFixture(Fixture fixture) {
						if(fixture.getBody().getUserData() instanceof Block) {
							bitmap[x][y] = 1;
							return false;
						}
						return true;
					}
				},	
				(16 + i * 32 - 1) / GameState.UNIT_SCALE , (16 + j * 32 - 1) / GameState.UNIT_SCALE,
				(16 + i * 32 + 1) / GameState.UNIT_SCALE, (16 + j * 32 + 1) / GameState.UNIT_SCALE);
				
			}
		}
		
		if(kambojaMap != null)
		kambojaMap.update(delta);
		
		if(Gdx.input.getX() < 0) Gdx.input.setCursorPosition(0, Gdx.input.getY());
		if(Gdx.input.getX() > 1920 - 45) Gdx.input.setCursorPosition(1920 - 45, Gdx.input.getY());
		if(Gdx.input.getY() < 0) Gdx.input.setCursorPosition(Gdx.input.getX(), 0);
		if(Gdx.input.getY() > 1080 - 45) Gdx.input.setCursorPosition( Gdx.input.getX(), 1080 - 45);
				
		for(int i = getFlameParticles().size() - 1; i >= 0; i --){
			Body b = getFlameParticles().get(i);
			if(b.getLinearVelocity().len() < 0.1f){
				GameState.removeBody(b);
				getFlameParticles().remove(b);
			}
		}
		
		if(!isPause()){
			bloodEffect.update(delta);
			shellEffect.update(delta);
			rockEffect.update(delta);
			skullEffect.update(delta);
			bloodSpill.update(delta);

			for(int i = explosionEffects.size() - 1; i >= 0; i --){
				ParticleEffect pe = explosionEffects.get(i);
				pe.update(delta*3);
				if(pe.isComplete()){
					explosionPool.free((PooledEffect) pe);
					explosionEffects.remove(i);
				}
			}
		}
		else{
			gamepause.update(delta);
		}
		
		
		if(isIntro()){
			opacity -= delta;
			if(opacity < 0){
				opacity = 0;
				setIntro(false);
			}
		}
		if(isOutro()){
			opacity += delta;
			if(opacity > 1){
				opacity = 1;
				manager.changeState(Manager.POST_GAME_STATE);
				//GameMusic.fadeOut(musicName);
				return;
			}
		}
		
		if(!isPause())
			timer += delta;
		
		if(!isPause())
		menuPos += (targetMenuPos - menuPos)/10.0f;
		
		if(timer > 1){
			targetMenuPos = 1080 * 1;
			if(!said_three){
				if(GameState.SFX)
				three.play(VOLUME);
				said_three = true;
			}
		}
		if(timer > 2){
			targetMenuPos = 1080 * 2;
			if(!said_two){
				if(GameState.SFX)
				two.play(VOLUME);
				said_two = true;
			}
		}
		if(timer > 3){
			targetMenuPos = 1080 * 3;
			if(!said_one){
				if(GameState.SFX)
				one.play(VOLUME);
				said_one = true;
			}
		}
		if(timer > 4){
			targetMenuPos = 1080 * 4;
			if(!said_start){
				if(GameState.SFX)
				start.play(VOLUME);
				said_start = true;
			}
		}
		if(timer > 5){
			targetMenuPos = 1080 * 5;
			if(!isPause())
			inputBlocked = false;
			else
			inputBlocked = true;
			
			//GameMusic.loop(musicName, 1);
		}
		
		for(int i = 0; i < getPlayers().size(); i ++){
			
			if(getPlayers().get(i).isDead() && !getPlayers().get(i).said_ko){
				int r = (int)(Math.random()*3f);
				if(GameState.SFX){
				ko[r].play(VOLUME);
				ko[r].play(VOLUME);
				ko[r].play(VOLUME);
				ko[r].play(VOLUME);
				}

				getPlayers().get(i).said_ko = true;
			}
			
			if(!getPlayers().get(i).isDead()){
				getPlayers().get(i).said_ko = false;
			}
			
			
		}
		
		if(KambojaMain.getGameTime() != -1){
			if((int) (KambojaMain.getGameTime() - timeCount) <= 0){
				targetMenuPos = 1080 * 6;
				inputBlocked = true;
				setEnd(true);
				//acabou o tempo
				
				for(Player p : getPlayers()){
					p.getWeapon().analog = 0;
				}
				
				if(KambojaMain.getPostGamePlayers() == null)
					KambojaMain.initializePostGamePlayers();
				KambojaMain.getPostGamePlayers().clear();
				KambojaMain.getPostGamePlayers().addAll(getPlayers());
			}
		}
		
		if(isEnd()){
			endTimer += delta;
			
			if(endTimer > 2){
				setOutro(true);
			}
		}
		
		if(!inputBlocked){
			timeCount += delta;
			itemTimer -= delta;
			
			if(itemTimer <= 0){
				if(KambojaMain.hasItems()){
					itemTimer = (float) (Math.random() * 10 + 10);
					addItem(new Vector2(
							(float)(Math.random() * mapWidth * 16) / UNIT_SCALE,
							(float)(Math.random() * mapHeight * 16) / UNIT_SCALE
							), (int)(Math.random() * (BETA_ITEMS ? 6 : 4)));
				}
			}
		}
		
		if(KambojaMain.getDeathsNumber() != -1){
			int inGame = 0;
			for(Player p : getPlayers()){
				if(p.getDeaths() < KambojaMain.getDeathsNumber() || !p.isDead()){
					inGame++;
				}
			}
			
			if(inGame <= 1){
				targetMenuPos = 1080 * 6;
				inputBlocked = true;
				if(!isEnd()){
					if(GameState.SFX)
					gameover[(int)(Math.random()*3f)].play();
				}
				setEnd(true);
				//só sobrou um player
				
				for(Player p : getPlayers()){
					p.getWeapon().analog = 0;
				}
				
				if(KambojaMain.getPostGamePlayers() != null)
					KambojaMain.getPostGamePlayers().clear();
				else 
					KambojaMain.initializePostGamePlayers();
				
				KambojaMain.getPostGamePlayers().addAll(getPlayers());
			}
		}
		

		world.getBodies(bodies);
		for(Body b : bodies){
			if(b.getUserData() instanceof Bullet){
				Bullet bullet = (Bullet) b.getUserData();
				if(bullet.canRemove()){
					removeBody(b);
				}
			}
		}
		
		if(isEnd() && inputBlocked){
			for(Player p : getPlayers()){
				p.endSound();
			}
		}
		
		if(!isPause() && !inputBlocked){
			for(Player p : getPlayers()){
				p.update(delta);
			}
		}
		
		for(Body b : forRemoval){
			try{world.destroyBody(b);} catch(NullPointerException e) {}
		}
		
		forRemoval.clear();
		
		if(!isPause())
		world.step(1/60f, 6, 2);
		
		float minx = getPlayers().get(0).getBody().getWorldCenter().x;
		float miny = getPlayers().get(0).getBody().getWorldCenter().y;
		float maxx = getPlayers().get(0).getBody().getWorldCenter().x;
		float maxy = getPlayers().get(0).getBody().getWorldCenter().y;
		
		for(Player p : getPlayers()){
			minx = Math.min(p.getBody().getWorldCenter().x, minx);
			miny = Math.min(p.getBody().getWorldCenter().y, miny);
			
			maxx = Math.max(p.getBody().getWorldCenter().x, maxx);
			maxy = Math.max(p.getBody().getWorldCenter().y, maxy);
		}
		
		minx -= 64 / UNIT_SCALE;
		miny -= 64 / UNIT_SCALE;
		maxx += 64 / UNIT_SCALE;
		maxy += 64 / UNIT_SCALE;
		
		bounds.setRect(minx, miny, maxx-minx, maxy-miny);
		
		med.set(minx + (maxx-minx)/2, miny + (maxy-miny)/2);
		
		float targetZoom = (float) (Math.max(bounds.getWidth()/1920, bounds.getHeight()/1080));
		getCamera().zoom += (targetZoom - getCamera().zoom)/10.0f;
		//camera.zoom = 1;
		
		getCamera().position.x += (med.x - getCamera().position.x)/10.0f;
		getCamera().position.y += (med.y - getCamera().position.y)/10.0f;
		
		getCamera().update();
		
		
	}

	//Input
	
	public void connected(Controller controller) {
		
	}

	public void disconnected(Controller controller) {
		
	}

	public boolean buttonDown(Controller controller, int buttonCode) {
		int id = Util.getControllerID(controller);
		if(id != -1){
		int start = 0;
		
		if(controller.getName().equals(Gamecube.getID())){
			start = Gamecube.START;
		}
		else if(controller.getName().toUpperCase().contains("XBOX") && controller.getName().contains("360")){
			start = XBox.BUTTON_START;
		}
		else if(controller.getName().toUpperCase().contains("SONY") || controller.getName().toUpperCase().contains("PLAYSTATION")){
			start = Playstation3.START;
		}
		else{
			start = GenericController.START;
		}
		
		if(isPause()){
			gamepause.buttonPressed(id, buttonCode, controller.getName());
		}
		
		if(id != -1){
			if(buttonCode == start){
				if(timer > 5 && !(isIntro() || isOutro())){
					pauseUnpause();
				}
			}
		}
		
		if(!inputBlocked){
			
			for(Player p : getPlayers()){
				if(id == p.getId()){
					if(!p.isFalling())
					p.buttonDown(controller, buttonCode);
				}
			}
		}
		}
		return false;
	}
	
	public void pauseUnpause(){
		if(isPause()){
			setPause(false);
			inputBlocked = false;
			opacity = 0;
		}
		else{
			gamepause.confirm = false;
			gamepause.offset1 = -1000;
			gamepause.offset2 = 1000;
			setPause(true);
			inputBlocked = true;
			opacity = 0.5f;
		}
	}

	public boolean buttonUp(Controller controller, int buttonCode) {
		if(!inputBlocked){
			int id = Util.getControllerID(controller);
			if(id != -1){
			for(Player p : getPlayers()){
				if(id == p.getId()){
					if(!p.isFalling())
					p.buttonUp(controller, buttonCode);
				}
			}
			}
			
		}
		return false;
	}

	public boolean axisMoved(Controller controller, int axisCode, float value) {
		if(!inputBlocked){
			int id = Util.getControllerID(controller);
			if(id != -1){
			for(Player p : getPlayers()){
				if(id == p.getId()){
					if(!p.isFalling())
					p.axisMoved(controller, axisCode, value);
				}
			}
			}
		}
		
		if(isPause()){
			gamepause.axisMoved(controller, axisCode, value);
		}
		
		return false;
	}

	public boolean povMoved(Controller controller, int povCode, PovDirection value) {
		if(!inputBlocked){
			int id = Util.getControllerID(controller);
			if(id != -1){
			for(Player p : getPlayers()){
				if(id == p.getId()){
					if(!p.isFalling())
					p.povMoved(controller, povCode, value);
				}
			}
			}
		}
		return false;
	}

	public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
		return false;
	}

	public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
		return false;
	}

	public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
		return false;
	}

	public void resize(int width, int height) {
		
	}

	public boolean isIntro() {
		return intro;
	}

	public void setIntro(boolean intro) {
		this.intro = intro;
	}

	public boolean isOutro() {
		return outro;
	}

	public void setOutro(boolean outro) {
		this.outro = outro;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}

	public boolean isPause() {
		return pause;
	}

	public void setPause(boolean pause) {
		this.pause = pause;
	}

	public ArrayList<Item> getItems() {
		return items;
	}

	public void setItems(ArrayList<Item> items) {
		this.items = items;
	}

	public boolean isEnd() {
		return end;
	}

	public void setEnd(boolean end) {
		this.end = end;
	}

	public ArrayList<Bullet> getBullets() {
		return bullets;
	}

	public void setBullets(ArrayList<Bullet> bullets) {
		this.bullets = bullets;
	}

	public ArrayList<Body> getFlameParticles() {
		return flameParticles;
	}

	public void setFlameParticles(ArrayList<Body> flameParticles) {
		this.flameParticles = flameParticles;
	}

	public OrthographicCamera getCamera() {
		return camera;
	}

	public void setCamera(OrthographicCamera camera) {
		this.camera = camera;
	}

	public TiledMap getTiledMap() {
		return tiledMap;
	}

	public void setTiledMap(TiledMap tiledMap) {
		this.tiledMap = tiledMap;
	}

	public ArrayList<Block> getBlocks() {
		return blocks;
	}

	public void setBlocks(ArrayList<Block> blocks) {
		this.blocks = blocks;
	}

	public float getTimer() {
		return timer;
	}

	public World getWorld() {
		return world;
	}

	public int[][] getBitmap() {
		return bitmap;
	}

	

}
