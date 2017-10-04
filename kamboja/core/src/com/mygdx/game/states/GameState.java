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
import com.badlogic.gdx.graphics.TextureData;
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
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.KambojaMain;
import com.mygdx.game.Manager;
import com.mygdx.game.State;
import com.mygdx.game.controllers.Gamecube;
import com.mygdx.game.controllers.Playstation3;
import com.mygdx.game.controllers.XBox;
import com.mygdx.game.objects.BotController;
import com.mygdx.game.objects.BotPlayer;
import com.mygdx.game.objects.Bullet;
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
import com.mygdx.game.objects.map.UnbreakableBlock;
import com.mygdx.game.objects.map.WaterBlock;

import box2dLight.RayHandler;

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
	
	private RayHandler handler;
	private BitmapFont font;
	private BitmapFont timeFont;
	
	private boolean inputBlocked;
	
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

	private boolean said_three, said_two, said_one, said_start;
	private IslandBackground islandBackground;
	
	//shader stuff
	
	SpriteBatch shaderBatch;
	ShaderProgram shader;
	FrameBuffer shaderBuffer;
	Texture binocularMask;
	Texture noiseTexture;
	
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
	
	public void create() {
		
		//shader stuff
		
		shaderBuffer = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		shader = new ShaderProgram(
				Gdx.files.internal("shaders/default.vs"),
				Gdx.files.internal("shaders/old_movie.fs"));
		ShaderProgram.pedantic = false;
		shaderBatch = new SpriteBatch(300, shader);
		
		binocularMask = new Texture("shaders/binoculars2.png");
		noiseTexture = new Texture("shaders/noisetex.jpg");
		
		if (shader.getLog().length()!=0)
			System.out.println(shader.getLog());
		
		System.out.println(shader.isCompiled());
		
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
		
		getCamera().setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
		
		System.out.println("Loading map " + KambojaMain.getMapName());
		setTiledMap(new TmxMapLoader().load(KambojaMain.getMapName()));
		
		if(KambojaMain.getMapName().endsWith("island.tmx")){
			islandBackground = new IslandBackground();
		}
		else{
			islandBackground = null;
		}
		
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
		ftfg = new FreeTypeFontGenerator(Gdx.files.internal("fonts/dot_to_dot.ttf"));
		param = new FreeTypeFontParameter();
		param.size = (int) (300 * Gdx.graphics.getDensity());
		param.color = new Color(0.03f, 0.03f, 0.03f, 1);
		param.borderWidth = 2;
		param.borderColor = new Color(1, 0.9f, 0.9f, 1);
		param.shadowColor = new Color(0, 0, 0, 0.7f);
		param.shadowOffsetX = 3;
		param.shadowOffsetY = 3;
		font = ftfg.generateFont(param);
		param.size = (int) (150 * Gdx.graphics.getDensity());
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
		
//		rock = new ParticleEffect();
//		rock.load(Gdx.files.internal("particles/rockExplosion.par"), Gdx.files.internal("particles"));
//		rock.scaleEffect(1f/UNIT_SCALE / 6f);
//		rockPool = new ParticleEffectPool(rock, 0, 10);
//		rockEffects = new ArrayList<PooledEffect>();
		
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
								tr.getRegionHeight() / UNIT_SCALE, world, this);
								
						getBlocks().add(block);
					}
					
				}
			}
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
								tr.getRegionHeight() / UNIT_SCALE, world, this);
								
						getBlocks().add(block);
					}
					
				}
			}
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
								tr.getRegionHeight() / UNIT_SCALE, world, this);
								
						getBlocks().add(block);
					}
					
				}
			}
		}

		for(int i = 0; i < KambojaMain.getControllers().size(); i ++){
				createPlayer(i);
		}
		
		if(LIGHTS){
			handler = new RayHandler(world);
			handler.setCombinedMatrix(getCamera().combined);
	
			float ambientLight = Float.parseFloat(getTiledMap().getProperties().get("ambientLight", String.class));
			
			handler.setAmbientLight(0, 0, 0, ambientLight);
		}
		
		if(LIGHTS){
			MapLayer ml = getTiledMap().getLayers().get("Light");
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
		for(MapObject mo : ml.getObjects()){
			if(mo.getProperties().get("type").equals("player"+id)){
				float x = mo.getProperties().get("x", Float.class);
				float y = mo.getProperties().get("y", Float.class);
				float width = mo.getProperties().get("width", Float.class);
				float height = mo.getProperties().get("height", Float.class);
				float angle = Float.parseFloat(mo.getProperties().get("angle", String.class));
				def.position.set(new Vector2((x+width/2) / UNIT_SCALE, (y+height/2) /UNIT_SCALE));
				def.angle = angle;
			}
		}

		Body body = world.createBody(def);
		
		CircleShape circle = new CircleShape();
		circle.setRadius(10 / UNIT_SCALE);
		Fixture f = body.createFixture(circle, 0.8f);
		
		circle.dispose();
		
		Player player = null;
		if(KambojaMain.getControllers().get(id) instanceof BotController){
			player = new BotPlayer(body, id, this);
		}
		else{
			player = new Player(body, id, this);
		}
		
		player.setAngle(new Vector2((float)Math.sin(Math.toRadians(def.angle)), (float)Math.cos(Math.toRadians(def.angle))));
		
		body.setUserData(player);
		f.setUserData(player);
		getPlayers().add(player);
		

		def = new BodyDef();
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

	}
	
	public void showBlood(Vector2 worldCenter) {
		bloodEffect.setMinPos(worldCenter);
		bloodEffect.setMaxPos(worldCenter);
		bloodEffect.addParticle();
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
	
	public void showExplosion(Vector2 pos){
		ParticleEffect pe = explosionPool.obtain();
		pe.setPosition(pos.x, pos.y);
		pe.reset();
		
		if(!explosionEffects.contains(pe))
		explosionEffects.add((PooledEffect) pe);
	}

	//Render stuff
	
	public void render(SpriteBatch sb) {

		shaderBuffer.begin();
		
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		sb.setShader(null);
		
		drawBackgroundTiles(sb);
		drawPersistentParticles(sb);
		drawBlocks(sb);
		drawItems(sb);
		drawParticles(sb);
		drawPlayersAndLight(sb);
		drawCeilingTiles(sb);
		drawUI(sb);
		drawDebug(sb);
		drawPause(sb);
		
		shaderBuffer.end();
		
		//shaderBuffer.getColorBufferTexture().bind(0);

		
		float amt = 0.5f;
		
		shader.begin();
		shader.setUniformf("time", timer);
		shader.setUniformf("flicker", 0.1f * amt);
		shader.setUniformf("lightvariance", 0.05f * amt);
		shader.setUniformf("blackandwhite", 1.0f * amt);
		shader.setUniformf("oversaturation", 0.3f * amt);
		shader.setUniformf("vignette", 1.0f * amt);
		shader.setUniformf("scratches", 0.5f * (float)Math.pow(amt, 4));
		shader.setUniformf("scratchsize", new Vector2(8, 200));
		shader.setUniformf("splotches", 200 * (float)Math.pow(amt, 8));
		
		shaderBatch.setShader(shader);
		
		shaderBatch.setProjectionMatrix(Util.getNormalProjection());
		shaderBatch.begin();
		
		shaderBatch.draw(shaderBuffer.getColorBufferTexture(),
					0, 0,
					Gdx.graphics.getWidth(),
					Gdx.graphics.getHeight(),
					0, 0,
					Gdx.graphics.getWidth(),
					Gdx.graphics.getHeight(),
					false, true);
		
		shaderBatch.end();
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
		bloodEffect.render(sb);
		shellEffect.render(sb);
		rockEffect.render(sb);
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
		
		for(Player p : getPlayers()){
			p.renderGUI(sb);
		}
		
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
		font.draw(sb, "GAME!", (Gdx.graphics.getWidth() - layout.width)/2f, Gdx.graphics.getHeight()/2 - menuPos + Gdx.graphics.getHeight() * 6 + 100);
		
		layout.setText(font, "START!");
		font.draw(sb, "START!", (Gdx.graphics.getWidth() - layout.width)/2f, Gdx.graphics.getHeight()/2 - menuPos + Gdx.graphics.getHeight() * 4 + 100);
		
		layout.setText(font, "1");
		font.draw(sb, "1", (Gdx.graphics.getWidth() - layout.width)/2f, Gdx.graphics.getHeight()/2 - menuPos + Gdx.graphics.getHeight() * 3 + 100);
		
		layout.setText(font, "2");
		font.draw(sb, "2", (Gdx.graphics.getWidth() - layout.width)/2f, Gdx.graphics.getHeight()/2 - menuPos + Gdx.graphics.getHeight() * 2 + 100);
		
		layout.setText(font, "3");
		font.draw(sb, "3", (Gdx.graphics.getWidth() - layout.width)/2f, Gdx.graphics.getHeight()/2 - menuPos + Gdx.graphics.getHeight() + 100);
		
		int timeNum = (int) (KambojaMain.getGameTime() - timeCount);
		
		String time = (KambojaMain.getGameTime() == -1) ? "00:00" : timeNum/60 + ":" + ((timeNum % 60 >= 10) ? timeNum % 60 : "0" + timeNum % 60);
		
		layout.setText(timeFont, time);
		timeFont.draw(sb, time, (Gdx.graphics.getWidth() - layout.width)/2f, Gdx.graphics.getHeight() - 30);
		
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
			sr.box(0, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0);
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
		Gdx.gl.glClearColor(0, 162/255f, 132/255f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if(islandBackground != null){
			islandBackground.update(delta);
		}
		
		if(Gdx.input.getX() < 0) Gdx.input.setCursorPosition(0, Gdx.input.getY());
		if(Gdx.input.getX() > Gdx.graphics.getWidth() - 45) Gdx.input.setCursorPosition(Gdx.graphics.getWidth() - 45, Gdx.input.getY());
		if(Gdx.input.getY() < 0) Gdx.input.setCursorPosition(Gdx.input.getX(), 0);
		if(Gdx.input.getY() > Gdx.graphics.getHeight() - 45) Gdx.input.setCursorPosition( Gdx.input.getX(), Gdx.graphics.getHeight() - 45);
				
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
			
//			for(int i = rockEffects.size() - 1; i >= 0; i --){
//				ParticleEffect pe = rockEffects.get(i);
//				pe.update(delta*2);
//				if(pe.isComplete()){
//					rockPool.free((PooledEffect) pe);
//					rockEffects.remove(i);
//				}
//			}
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
				manager.changeState(3);
				return;
			}
		}
		
		if(!isPause())
			timer += delta;
		
		if(!isPause())
		menuPos += (targetMenuPos - menuPos)/10.0f;
		
		if(timer > 1){
			targetMenuPos = Gdx.graphics.getHeight() * 1;
			if(!said_three){
				if(GameState.SFX)
				three.play();
				said_three = true;
			}
		}
		if(timer > 2){
			targetMenuPos = Gdx.graphics.getHeight() * 2;
			if(!said_two){
				if(GameState.SFX)
				two.play();
				said_two = true;
			}
		}
		if(timer > 3){
			targetMenuPos = Gdx.graphics.getHeight() * 3;
			if(!said_one){
				if(GameState.SFX)
				one.play();
				said_one = true;
			}
		}
		if(timer > 4){
			targetMenuPos = Gdx.graphics.getHeight() * 4;
			if(!said_start){
				if(GameState.SFX)
				start.play();
				said_start = true;
			}
		}
		if(timer > 5){
			targetMenuPos = Gdx.graphics.getHeight() * 5;
			if(!isPause())
			inputBlocked = false;
			else
			inputBlocked = true;
		}
		
		for(int i = 0; i < getPlayers().size(); i ++){
			
			if(getPlayers().get(i).isDead() && !getPlayers().get(i).said_ko){
				int r = (int)(Math.random()*3f);
				if(GameState.SFX){
				ko[r].play(1.0f);
				ko[r].play(1.0f);
				ko[r].play(1.0f);
				ko[r].play(1.0f);
				}

				getPlayers().get(i).said_ko = true;
			}
			
			if(!getPlayers().get(i).isDead()){
				getPlayers().get(i).said_ko = false;
			}
			
			
		}
		
		if(KambojaMain.getGameTime() != -1){
			if((int) (KambojaMain.getGameTime() - timeCount) <= 0){
				targetMenuPos = Gdx.graphics.getHeight() * 6;
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
				targetMenuPos = Gdx.graphics.getHeight() * 6;
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
		
		float targetZoom = (float) (Math.max(bounds.getWidth()/Gdx.graphics.getWidth(), bounds.getHeight()/Gdx.graphics.getHeight()));
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
				if(id == p.getID()){
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
				if(id == p.getID()){
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
				if(id == p.getID()){
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
				if(id == p.getID()){
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

	

}
