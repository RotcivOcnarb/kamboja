package com.mygdx.game.states;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.KambojaMain;
import com.mygdx.game.Manager;
import com.mygdx.game.State;
import com.mygdx.game.controllers.Gamecube;
import com.mygdx.game.controllers.GenericController;
import com.mygdx.game.controllers.XBox;
import com.mygdx.game.objects.GameMusic;
import com.mygdx.game.objects.KeyboardController;
import com.mygdx.game.objects.Player;
import com.mygdx.game.objects.PlayerController;
import com.mygdx.game.objects.Util;

public class PlayerSelectState extends State{
	
	boolean outro;
	boolean intro;
	float alpha;
	float shaderIntensity;
	float globalTimer;
	
	Texture background;
	Texture player_frames[] = new Texture[4];
	Texture player_glass[] = new Texture[4];
	Texture player_subframes[] = new Texture[4];
	Texture player_subglass[] = new Texture[4];
	Texture back_tex;
	Texture chain;
	Texture selection_tex;
	Texture ok;
	Texture pressStart;
	
	float okAlpha[] = new float[4];
	float okScale[] = new float[4];
	float[] okAngle = new float[4];
	
	boolean playerReady[] = new boolean[4];
	
	private Texture[] texWep; //texture for each weapon
	Texture inGameWep[];
	Texture select_gear[] = new Texture[4];
	float gear_angle[] = new float[4];
	
	int[] selection = new int[4];
	boolean[] typing = new boolean[4];
	
	int positionPlayerOffset[] = new int[4];
	int positionWeaponOffset[] = new int[4];
	
	FrameBuffer playerBuffer[] = new FrameBuffer[4];
	FrameBuffer weaponBuffer[] = new FrameBuffer[4];
	FrameBuffer keyboardBuffer[] = new FrameBuffer[4];
	
	int key_x[] = new int[4];
	int key_y[] = new int[4];
	float tween_key_x[] = new float[4];
	float tween_key_y[] = new float[4];
	
	String keys[][] = new String[10][4];
	
	float skinOffset[] = new float[4];
	float weaponOffset[] = new float[4];
		
	Body body_frames[] = new Body[4];
	Body body_subframes[] = new Body[4];
	Body pressStartBody;
	
	ShapeRenderer sr;
	FrameBuffer shaderBuffer;
	ShaderProgram shader;
	
	Matrix4 bufferProjectionPlayer;
	Matrix4 bufferProjectionWeapon;
	
	Rectangle2D selection_bounds[][] = new Rectangle2D[4][5];
	Rectangle2D selection_bound_tween[] = new Rectangle2D[4];
	
	float timer;
	float intensityTarget;
	float factor;
	
	World world;
	Box2DDebugRenderer b2dr;
	
	ParticleEffect fogo;
	ParticleEffect bolinha;
	
	OrthographicCamera camera;
	
	ArrayList<Body> chainBody;
	
	BitmapFont outlander[] = new BitmapFont[4];
	BitmapFont outlanderBig[] = new BitmapFont[4];
	BitmapFont olivers_barney[];
	GlyphLayout layout;
	private float back_angle;
	
	boolean goingBack;
	boolean allReady;
	boolean hasFallen;

	
	//TODO: particulas de fumaça
	//correntes
	//mayber logo imagem no titulo
	
	public PlayerSelectState(Manager manager) {
		super(manager);
		
		factor = Gdx.graphics.getHeight() / 1080f;
		
		sr = new ShapeRenderer();
		selection_tex = KambojaMain.getTexture("menu/player_select/selection.png");
		chainBody = new ArrayList<Body>();
		chain = KambojaMain.getTexture("menu/player_select/chain.png");
		
		pressStart = KambojaMain.getTexture("menu/player_select/press start.png");
		
		if(KambojaMain.getControllers() == null)
			KambojaMain.initializeControllers();
		
			texWep = new Texture[KambojaMain.getWeaponSize()];
			texWep[0] = KambojaMain.getTexture("Weapons/Icon/Pistol.png");
			texWep[1] = KambojaMain.getTexture("Weapons/Icon/PistolAkimbo.png");
			texWep[2] = KambojaMain.getTexture("Weapons/Icon/minigun.png");
			texWep[3] = KambojaMain.getTexture("Weapons/Icon/shotgun.png");
			texWep[4] = KambojaMain.getTexture("Weapons/Icon/Mp5.png");
			texWep[5] = KambojaMain.getTexture("Weapons/Icon/Flamethrower.png");
			texWep[6] = KambojaMain.getTexture("Weapons/Icon/Bazook.png");
			texWep[7] = KambojaMain.getTexture("Weapons/Icon/Laser.png");
			
			inGameWep = new Texture[KambojaMain.getWeaponSize()];
			inGameWep[0] = KambojaMain.getTexture("Weapons/In-game/Taurus.png");
			inGameWep[1] = KambojaMain.getTexture("Weapons/In-game/Taurus Akimbo.png");
			inGameWep[2] = KambojaMain.getTexture("Weapons/In-game/Minigun.png");
			inGameWep[3] = KambojaMain.getTexture("Weapons/In-game/sss.png");
			inGameWep[4] = KambojaMain.getTexture("Weapons/In-game/MP5.png");
			inGameWep[5] = KambojaMain.getTexture("Weapons/In-game/flahme.png");
			inGameWep[6] = KambojaMain.getTexture("Weapons/In-game/Bazooka.png");
			inGameWep[7] = KambojaMain.getTexture("Weapons/In-game/Laser.png");
			
			ok = KambojaMain.getTexture("menu/player_select/ok.png");
			
			camera = new OrthographicCamera();
			camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			camera.zoom = 1/100f;
			camera.position.set(Gdx.graphics.getWidth()/2f / 100f, Gdx.graphics.getHeight() / 2f / 100f, 0);
			
			setKeys();
			
			shader = new ShaderProgram(Gdx.files.internal("shaders/default.vs"),
					Gdx.files.internal("shaders/color_shift.fs"));
			ShaderProgram.pedantic = false;
			if(shader.getLog().length() > 0){
				System.out.println(shader.getLog());
			}
			
			background = KambojaMain.getTexture("menu/player_select/fundo.jpg");
			
			for(int i = 0; i < 4; i ++) {
				//Creates the font
				FreeTypeFontGenerator ftfg;
				FreeTypeFontParameter param;
				ftfg = new FreeTypeFontGenerator(Gdx.files.internal("fonts/outlander.ttf"));
				param = new FreeTypeFontParameter();
				param.size = (int) (50f * factor);
				param.color = new Color(0.5f, 0.5f, 0.5f, 1).mul(getPlayerColor(i));
				param.borderColor = new Color(0.7f, 0.7f,0.7f, 1).add(getPlayerColor(i).mul(0.3f));
				param.borderWidth = 2*factor;
				outlander[i] = ftfg.generateFont(param);
				param.size = (int) (200f * factor);
				outlanderBig[i] = ftfg.generateFont(param);
				ftfg.dispose();	
			}
			
			layout = new GlyphLayout();
			
			
			fogo = new ParticleEffect();
			fogo.load(Gdx.files.internal("particles/fogo.par"), Gdx.files.internal("particles"));
			fogo.setPosition(Gdx.graphics.getWidth()/2f, -32*factor);
			fogo.scaleEffect(10*factor);
			
			bolinha = new ParticleEffect();
			bolinha.load(Gdx.files.internal("particles/bolinha.par"), Gdx.files.internal("particles"));
			bolinha.setPosition(Gdx.graphics.getWidth()/2f, Gdx.graphics.getHeight()/2f);
			bolinha.scaleEffect(factor);
			
			bufferProjectionPlayer = new Matrix4();
			bufferProjectionPlayer.setToOrtho2D(0, 0, 181, 280);
			bufferProjectionWeapon = new Matrix4();
			bufferProjectionWeapon.setToOrtho2D(0, 0, 181, 151);
			
			for(int i = 0; i < 4; i ++) {
				player_frames[i] = KambojaMain.getTexture("menu/player_select/frame"+(i+1)+".png");
				player_glass[i] = KambojaMain.getTexture("menu/player_select/glass"+(i+1)+".png");
				player_subframes[i] = KambojaMain.getTexture("menu/player_select/caixa p"+(i+1)+".png");
				player_subglass[i] = KambojaMain.getTexture("menu/player_select/subglass"+(i+1)+".png");
				select_gear[i] = KambojaMain.getTexture("menu/player_select/gear"+(i+1)+".png");
				playerBuffer[i] = new FrameBuffer(Format.RGBA8888, 181, 280, false);
				weaponBuffer[i] = new FrameBuffer(Format.RGBA8888, 181, 151, false);
				keyboardBuffer[i] = new FrameBuffer(Format.RGBA8888, 181, 151, false);
				
			}
			
			back_tex = KambojaMain.getTexture("menu/player_select/back_btn.png");
	}

	@Override
	public void create() {
		outro = false;
		intro = true;
		alpha = 1;
		timer = 0;
		shaderIntensity = 0;
		intensityTarget = 0;
		hasFallen = false;
		globalTimer = 0;
		
		
		goingBack = false;
		allReady = false;
		back_angle = 0;
		
		GameMusic.loadMusic(GameMusic.MAIN_MENU);
		GameMusic.loop(GameMusic.MAIN_MENU, 0);

		chainBody.clear();


		
		shaderBuffer = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		

		for(int i = 0; i < 4; i ++) {
			okAlpha[i] = 0;
			okScale[i] = 2;
			okAngle[i] = 30;
			key_x[i] = 0;
			key_y[i] = 2;
		}

		for(int i = 0; i < 4; i ++) {
			selection[i] = 3;
		}
		
		
		
	
		
		world = new World(new Vector2(0, -9.81f), false);
		b2dr = new Box2DDebugRenderer();
		
		pressStartBody = createBox(
				new Vector2(Gdx.graphics.getWidth()/2f, Gdx.graphics.getHeight()*2),
				new Vector2(772*factor, 181*factor),
				BodyType.DynamicBody, 0.01f, true);
		
		buildStartRopeJoint((int)(20 * factor));
		
		
		float targetwidth = (452*3 + player_frames[0].getWidth()) * factor;
		float targetoffset = (Gdx.graphics.getWidth() - targetwidth)/2f;
		
	
		
		for(int i = 0; i < 4; i ++) {
			
			playerReady[i] = false;
			typing[i] = false;
			
			targetwidth = (452*3 + player_frames[0].getWidth()) * factor;
			targetoffset = (Gdx.graphics.getWidth() - targetwidth)/2f;
			
			body_frames[i] = createBox(
					new Vector2(
							targetoffset + (i * 452)*factor 
							+ (player_frames[i].getWidth()*0.9f * factor / 2f),
							Gdx.graphics.getHeight() - factor*(138 + player_frames[i].getHeight()*0.9f)
							+ (player_frames[i].getHeight()*0.9f * factor)/2f
					), 
					new Vector2(
							player_frames[i].getWidth()*0.9f * factor / 2f,
							player_frames[i].getHeight()*0.9f * factor / 2f
							),
					BodyType.StaticBody, 0f, true
					);		
			
			targetwidth = (452*3 + player_subframes[0].getWidth()) * factor;
			targetoffset = (Gdx.graphics.getWidth() - targetwidth)/2f;
			
			body_subframes[i] = createBox(
					new Vector2(
							targetoffset + (i * 452)*factor 
							+ player_subframes[i].getWidth()*0.7f * factor / 2f,
							Gdx.graphics.getHeight() - factor*(700 + player_subframes[i].getHeight()*0.7f)
							+ (player_subframes[i].getHeight()*0.7f * factor)/2f
					), 
					new Vector2(
							player_subframes[i].getWidth()*0.7f * factor / 2f,
							player_subframes[i].getHeight()*0.7f * factor / 2f
							),
					BodyType.DynamicBody, 0.03f, false
					);	
			
			//buildRopeJoint(body_frames[i], body_subframes[i]);
			buildRopeJoint(i);
		}
		
		
		for(int i = 0; i < 4; i ++) {
			
			selection_bounds[i][4] = new Rectangle2D.Double(
					(Gdx.graphics.getWidth() - back_tex.getWidth()*factor)/2f,
					Gdx.graphics.getHeight() - back_tex.getHeight()*(1/3f) * factor - 15*factor,
					back_tex.getWidth()*factor,
					back_tex.getHeight() * factor - 10*factor
					);
			
			selection_bounds[i][3] = new Rectangle2D.Double(
					body_frames[i].getWorldCenter().x*100f - 181*factor/2f - 25*factor,
					body_frames[i].getWorldCenter().y*100f - 280*factor/2f + 5*factor,
					231*factor, 330*factor
					);
			
			selection_bounds[i][2] = new Rectangle2D.Double(
					body_frames[i].getWorldCenter().x*100f - 181*factor/2f - 60*factor,
					body_frames[i].getWorldCenter().y*100f - 280*factor/2f - 90*factor,
					301*factor, 100*factor
					);
			
			selection_bounds[i][1] = new Rectangle2D.Double(
					Gdx.graphics.getWidth()/4f * i,
					Gdx.graphics.getHeight()/5f * 1,
					Gdx.graphics.getWidth()/4f,
					Gdx.graphics.getHeight()/5f
					);
			
			selection_bounds[i][0] = new Rectangle2D.Double(
					Gdx.graphics.getWidth()/4f * i + 130*factor,
					- 120*factor,
					Gdx.graphics.getWidth()/4f - 200*factor,
					Gdx.graphics.getHeight()/5f
					);
			
			selection_bound_tween[i] = (Rectangle2D) selection_bounds[i][3].clone();
		}

	}
	
	private void setKeys() {
		
		keys[0][0] = "1";
		keys[1][0] = "2";
		keys[2][0] = "3";
		keys[3][0] = "4";
		keys[4][0] = "5";
		keys[5][0] = "6";
		keys[6][0] = "7";
		keys[7][0] = "8";
		keys[8][0] = "9";
		keys[9][0] = "0";
		
		keys[0][1] = "A";
		keys[1][1] = "B";
		keys[2][1] = "C";
		keys[3][1] = "D";
		keys[4][1] = "E";
		keys[5][1] = "F";
		keys[6][1] = "G";
		keys[7][1] = "H";
		keys[8][1] = "I";
		keys[9][1] = "";
		
		keys[0][2] = "J";
		keys[1][2] = "K";
		keys[2][2] = "L";
		keys[3][2] = "M";
		keys[4][2] = "N";
		keys[5][2] = "O";
		keys[6][2] = "P";
		keys[7][2] = "Q";
		keys[8][2] = "R";
		keys[9][2] = "";
		
		keys[0][3] = "S";
		keys[1][3] = "T";
		keys[2][3] = "U";
		keys[3][3] = "V";
		keys[4][3] = "W";
		keys[5][3] = "X";
		keys[6][3] = "Y";
		keys[7][3] = "Z";
		keys[8][3] = "";
		keys[9][3] = "";
	}

	public Body createBox(Vector2 pos, Vector2 size, BodyType type, float density, boolean sensor) {
		BodyDef def = new BodyDef();
		def.type = type;
		def.linearDamping = 0.2f;
		def.position.set(pos.cpy().scl(1/100f));
		
		Body b = world.createBody(def);
		
		PolygonShape s = new PolygonShape();
		s.setAsBox(size.x / 100f, size.y / 100f);
		
		Fixture f = b.createFixture(s, density);
		f.setSensor(sensor);
		
		return b;
	}
	
	public void buildRopeJoint(int p) {
		
		for(int k = -1; k <= 1; k += 2) {
			Array<Body> bodies = new Array<Body>();
			
			float targetwidth = (452*3 + player_frames[0].getWidth()) * factor;
			float targetoffset = (Gdx.graphics.getWidth() - targetwidth)/2f;
			
			for(int i = 0; i < 5; i ++) {
				Body b = createBox(
						new Vector2(targetoffset + (p * 452)*factor 
								+ player_frames[p].getWidth()*0.9f * factor / 2f + k*(player_frames[p].getWidth()*0.9f * factor / 2f)/2f,
								Gdx.graphics.getHeight() - factor*(138 + player_frames[p].getHeight()*0.9f)
								+ (player_frames[p].getHeight()*0.9f * factor)/2f - player_frames[p].getHeight()*0.9f * factor / 2f
								- (15*i)),
						new Vector2(2.5f, 10), i == 0 ? BodyType.StaticBody : BodyType.DynamicBody, 1f, false);
				
				chainBody.add(b);
				bodies.add(b);
			}
			
			for(int i = 1; i < 5; i ++) {
				RevoluteJointDef def = new RevoluteJointDef();
				def.bodyA = bodies.get(i-1);
				def.bodyB = bodies.get(i);
				def.localAnchorA.set(0, -7.5f/100f);
				def.localAnchorB.set(0, 7.5f/100f);
				
				world.createJoint(def);
			}
			
			RevoluteJointDef def = new RevoluteJointDef();
			def.bodyA = bodies.get(bodies.size - 1);
			def.bodyB = body_subframes[p];
			def.localAnchorA.set(0, -7.5f/100f);
			def.localAnchorB.set(
					(k*(player_frames[p].getWidth()*0.9f * factor / 2f)/2f) / 100f,
					((player_subframes[p].getHeight()*0.7f * factor / 2f) - 7.5f) / 100f
					);
			
			world.createJoint(def);
			
			bodies.get(1).applyLinearImpulse(new Vector2((float)Math.random()*0.1f, 0), bodies.get(1).getWorldCenter(), true);
			
		}

	}
	
	public void buildStartRopeJoint(int numChains) {
		
		for(int k = -1; k <= 1; k += 2) {
			Array<Body> bodies = new Array<Body>();
			
			for(int i = 0; i < numChains; i ++) {
				Body b = createBox(
						new Vector2(Gdx.graphics.getWidth()/2 + k*(Gdx.graphics.getWidth()/4f), Gdx.graphics.getHeight()-(30*i) + 250*factor),
						new Vector2(5f, 20), i == 0 ? BodyType.StaticBody : BodyType.DynamicBody, 1f, true);
				
				chainBody.add(b);
				bodies.add(b);
			}
			
			for(int i = 1; i < numChains; i ++) {
				RevoluteJointDef def = new RevoluteJointDef();
				def.bodyA = bodies.get(i-1);
				def.bodyB = bodies.get(i);
				def.localAnchorA.set(0, -15f/100f);
				def.localAnchorB.set(0, 15f/100f);
				
				world.createJoint(def);
			}
			
			RevoluteJointDef def = new RevoluteJointDef();
			def.bodyA = bodies.get(bodies.size - 1);
			def.bodyB = pressStartBody;
			def.localAnchorA.set(0, -7.5f/100f);
			def.localAnchorB.set((k*Gdx.graphics.getWidth()/4f) /100f,
					(413/2f*factor - 50*factor) / 100f);
			
			world.createJoint(def);
			
			bodies.get(1).applyLinearImpulse(new Vector2((float)Math.random()*0.1f, 0), bodies.get(1).getWorldCenter(), true);
			
		}
		
	}

	public void dispose() {
		
	}
	
	public Color getPlayerColor(int id) {
		switch(id) {
		case 0:
			return new Color(0, 0, 1, 1);
		case 1:
			return new Color(1, 0, 0, 1);
		case 2:
			return new Color(0, 1, 0, 1);
		case 3:
			return new Color(1, 1, 0, 1);
		}
		return new Color(1, 1, 1, 1);
	}
	
	public void setSpriteBatchColor(SpriteBatch sb, int id) {
		sb.setColor(getPlayerColor(id));
	}
	
	public void setFontColor(BitmapFont font, int id) {
		font.setColor(getPlayerColor(id));
	}
	
	public int getSkinPositionByWeapon(int weapon) {
		switch (weapon) {
		case 0:
			return 2;
		case 1:
			return 0;
		default:
			return 1;
		}
	}

	@Override
	public void render(SpriteBatch sb) {	
		
		float factor = Gdx.graphics.getHeight() / 1080f;
		
		for(int i = 0; i < 4; i ++) {
			
			if(KambojaMain.getControllers().size()-1 >= i){
				if(KambojaMain.getControllers().get(i) != null) {
				skinOffset[i] += (positionPlayerOffset[i] - skinOffset[i])/10.0f;
				weaponOffset[i] += (positionWeaponOffset[i] - weaponOffset[i])/10f;
				
				playerBuffer[i].begin();
				Gdx.gl.glClearColor(0, 0, 0, 0f);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				int shift = positionPlayerOffset[i] / KambojaMain.getPlayerSkinsSize();
				for(int k = shift - 1; k <= shift + 1; k ++) {
					for(int j = 0; j < KambojaMain.getPlayerSkinsSize(); j ++) {
						TextureRegion tex = Player.getTexture(j, getSkinPositionByWeapon(
								KambojaMain.getControllers().get(i).getWeapon()
								));
							
						sb.begin();
						sb.setProjectionMatrix(bufferProjectionPlayer);
						sb.draw(tex,
								(181 - tex.getRegionWidth()*5) / 2f + j*181 - skinOffset[i]*181 + 
								k * (181*KambojaMain.getPlayerSkinsSize()),
								(280 - tex.getRegionHeight()*5) / 2f,
								tex.getRegionWidth()*5 / 2f,
								tex.getRegionHeight()*5 / 2f,
								tex.getRegionWidth()*5,
								tex.getRegionHeight()*5,
								1,
								1,
								globalTimer*50);
						
						Texture wep = inGameWep[KambojaMain.getControllers().get(i).getWeapon()];
						
						sb.draw(wep,
								(181 - wep.getWidth()*5) / 2f + j*181 - skinOffset[i]*181 + 
								k * (181*KambojaMain.getPlayerSkinsSize()),
								(280 - wep.getHeight()*5) / 2f,
								wep.getWidth()*5 / 2f,
								wep.getHeight()*5 / 2f,
								wep.getWidth()*5,
								wep.getHeight()*5,
								1,
								1,
								globalTimer*50,
								0,
								0,
								wep.getWidth(),
								wep.getHeight(),
								false, false);
							
						sb.end();
					}
				}
				playerBuffer[i].end();
				
				weaponBuffer[i].begin();
				Gdx.gl.glClearColor(0, 0, 0, 0f);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				shift = positionWeaponOffset[i] / KambojaMain.getWeaponSize();
				for(int k = shift - 1; k <= shift + 1; k ++) {
					for(int j = 0; j < KambojaMain.getWeaponSize(); j ++) {
						Texture tex = texWep[j];
							
						sb.begin();
						sb.setProjectionMatrix(bufferProjectionWeapon);
						
						float ratio = 181 / (tex.getWidth()*5f);
						
						sb.draw(tex,
								(181 - tex.getWidth()*5) / 2f + j*181 - weaponOffset[i]*181 +
								k * (181*KambojaMain.getWeaponSize()),
								(151 - tex.getHeight()*5) / 2f,
								tex.getWidth()*5 / 2f,
								tex.getHeight()*5 / 2f,
								tex.getWidth()*5,
								tex.getHeight()*5,
								ratio,
								ratio,
								0, 
								0,
								0,
								tex.getWidth(),
								tex.getHeight(),
								false,
								true);
							
						sb.end();
					}
				}
				weaponBuffer[i].end();
				
				keyboardBuffer[i].begin();
				Gdx.gl.glClearColor(0, 0, 0, 0f);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				
					for(int x = 0; x < 10; x ++) {
						for(int y = 0; y < 4; y ++) {
							BitmapFont f = (x == key_x[i] && y == 3-key_y[i]) ? outlanderBig[i] : outlander[i];
							
							layout.setText(f, keys[x][y]);
							sb.begin();
							f.draw(
									sb,
									keys[x][y],
									x * 70 + (181 - layout.width)/2f - tween_key_x[i] * 70,
									(3-y) * 70 + (151 - layout.height)/2f + layout.height - tween_key_y[i] * 70);
							sb.end();
						}
					}
				
				keyboardBuffer[i].end();
			}
			}
		}
		
		sb.setProjectionMatrix(Util.getNormalProjection());
		shaderBuffer.begin();
		sb.begin();
		//DESENHA MENU
		
			sb.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			
			bolinha.draw(sb);
			fogo.draw(sb);
		
			for(int i = 0; i < 4; i ++) {
				
				sb.draw(
						player_frames[i],
						body_frames[i].getWorldCenter().x*100f - player_frames[0].getWidth()*factor/2f,
						body_frames[i].getWorldCenter().y*100f - player_frames[0].getHeight()*factor/2f,
						player_frames[i].getWidth() * factor,
						player_frames[i].getHeight() * factor);
				
				if(KambojaMain.getControllers().size()-1 >= i){
					if(KambojaMain.getControllers().get(i) != null) {
					//desenha a arma e o player selecionado
					Texture pt = playerBuffer[i].getColorBufferTexture();
					sb.draw(pt,
							body_frames[i].getWorldCenter().x*100f - pt.getWidth()*factor/2f,
							body_frames[i].getWorldCenter().y*100f - pt.getHeight()*factor/2f + 30*factor,
							pt.getWidth()*factor, pt.getHeight()*factor);
					
					layout.setText(outlander[i], KambojaMain.getControllers().get(i).getName());
					
					outlander[i].draw(sb, KambojaMain.getControllers().get(i).getName(),
							body_frames[i].getWorldCenter().x*100f - layout.width/2f,
							body_frames[i].getWorldCenter().y*100f - 160*factor
							);
					sb.setColor(1, 1, 1, 1);
					}
				}
				

				sb.draw(
						player_glass[i],
						body_frames[i].getWorldCenter().x*100f - player_glass[0].getWidth()*factor/2f,
						body_frames[i].getWorldCenter().y*100f - player_glass[0].getHeight()*factor/2f,
						player_glass[i].getWidth() * factor,
						player_glass[i].getHeight() * factor);
				
				sb.draw(
						player_subframes[i],
						body_subframes[i].getWorldCenter().x*100f - player_subframes[0].getWidth()*factor/2f,
						body_subframes[i].getWorldCenter().y*100f - player_subframes[0].getHeight()*factor/2f,
						player_subframes[i].getWidth()*factor/2f,
						player_subframes[i].getHeight()*factor/2f,
						player_subframes[i].getWidth() * factor,
						player_subframes[i].getHeight() * factor,
						1,
						1,
						(float)Math.toDegrees(body_subframes[i].getAngle()),
						0,
						0,
						player_subframes[i].getWidth(),
						player_subframes[i].getHeight(),
						false,
						false);
				
				if(KambojaMain.getControllers().size()-1 >= i){
					if(KambojaMain.getControllers().get(i) != null) {
					
					//desenha a arma e o player selecionado
					
					Texture pt;
					if(!typing[i]) {
						pt = weaponBuffer[i].getColorBufferTexture();
					}
					else {
						pt = keyboardBuffer[i].getColorBufferTexture();
					}
					
					sb.draw(
							pt,
							body_subframes[i].getWorldCenter().x*100f - pt.getWidth()*factor/2f - 7*factor,
							body_subframes[i].getWorldCenter().y*100f - pt.getHeight()*factor/2f - 10*factor,
							pt.getWidth()*factor/2f,
							pt.getHeight()*factor/2f,
							pt.getWidth()*factor,
							pt.getHeight()*factor,
							1,
							1,
							(float)Math.toDegrees(body_subframes[i].getAngle()),
							0,
							0,
							pt.getWidth(),
							pt.getHeight(),
							false,
							typing[i]);
					
					
					}
				}
				
				sb.draw(
						player_subglass[i],
						body_subframes[i].getWorldCenter().x*100f - player_subglass[0].getWidth()*factor/2f,
						body_subframes[i].getWorldCenter().y*100f - player_subglass[0].getHeight()*factor/2f,
						player_subglass[i].getWidth()*factor/2f,
						player_subglass[i].getHeight()*factor/2f,
						player_subglass[i].getWidth() * factor,
						player_subglass[i].getHeight() * factor,
						1,
						1,
						(float)Math.toDegrees(body_subframes[i].getAngle()),
						0,
						0,
						player_subglass[i].getWidth(),
						player_subglass[i].getHeight(),
						false,
						false);
				
				sb.draw(
						select_gear[i],
						body_frames[i].getWorldCenter().x*100f - select_gear[i].getWidth()*factor/2f,
						-select_gear[i].getHeight()*factor*(2f/3f),
						select_gear[i].getWidth()*factor/2f,
						select_gear[i].getHeight()*factor/2f,
						select_gear[i].getWidth() * factor,
						select_gear[i].getHeight() * factor,
						1,
						1,
						gear_angle[i],
						0,
						0,
						select_gear[i].getWidth(),
						select_gear[i].getHeight(),
						false,
						false);

				
				if(!playerReady[i]) {
				setSpriteBatchColor(sb, i);
				
				if(KambojaMain.getControllers().size()-1 >= i){
					if(KambojaMain.getControllers().get(i) != null) {
				Rectangle2D boundingBox = selection_bound_tween[i];
				
				//UPPER LEFT
				sb.draw(
						selection_tex,
						(float)boundingBox.getX(),
						(float)(boundingBox.getY() + boundingBox.getHeight()) - selection_tex.getHeight()*factor,
						(float)boundingBox.getWidth()/2f,
						-(float)boundingBox.getHeight()/2f,
						selection_tex.getWidth()*factor,
						selection_tex.getHeight()*factor,
						1,
						1,
						0,
						0,
						0,
						selection_tex.getWidth(),
						selection_tex.getHeight(),
						true,
						false);
				
				//UPPER RIGHT
				sb.draw(
						selection_tex,
						(float)(boundingBox.getX() + boundingBox.getWidth()) - selection_tex.getWidth()*factor,
						(float)(boundingBox.getY() + boundingBox.getHeight()) - selection_tex.getHeight()*factor,
						-(float)boundingBox.getWidth()/2f,
						-(float)boundingBox.getHeight()/2f,
						selection_tex.getWidth()*factor,
						selection_tex.getHeight()*factor,
						1,
						1,
						0,
						0,
						0,
						selection_tex.getWidth(),
						selection_tex.getHeight(),
						false,
						false);
				
				//BOTTOM LEFT
				sb.draw(
						selection_tex,
						(float)boundingBox.getX(),
						(float)boundingBox.getY(),
						(float)boundingBox.getWidth()/2f,
						(float)boundingBox.getHeight()/2f,
						selection_tex.getWidth()*factor,
						selection_tex.getHeight()*factor,
						1,
						1,
						0,
						0,
						0,
						selection_tex.getWidth(),
						selection_tex.getHeight(),
						true,
						true);
				
				//BOTTOM RIGHT
				sb.draw(
						selection_tex,
						(float)(boundingBox.getX() + boundingBox.getWidth()) - selection_tex.getWidth()*factor,
						(float)boundingBox.getY(),
						-(float)boundingBox.getWidth()/2f,
						(float)boundingBox.getHeight()/2f,
						selection_tex.getWidth()*factor,
						selection_tex.getHeight()*factor,
						1,
						1,
						0,
						0,
						0,
						selection_tex.getWidth(),
						selection_tex.getHeight(),
						false,
						true);
				}
				}
				
				sb.setColor(1, 1, 1, 1);
				}
				
				
			
				
			}
			
			
			for(int i = 0; i < 4; i ++) {
				
				sb.setColor(1, 1, 1, okAlpha[i]);
					sb.draw(ok,
							body_frames[i].getWorldCenter().x*100f - ok.getWidth()*factor/2f,
							body_frames[i].getWorldCenter().y*100f - ok.getHeight()*factor/2f,
							ok.getWidth() * factor/2f,
							ok.getHeight() * factor/2f,
							ok.getWidth() * factor,
							ok.getHeight() * factor,
							okScale[i],
							okScale[i],
							okAngle[i],
							0,
							0,
							ok.getWidth(),
							ok.getHeight(),
							false,
							false);
					
					sb.setColor(1, 1, 1, 1);
			}
			
			for(int i = chainBody.size() - 1; i >= 0; i --) {
				Body bd = chainBody.get(i);
				sb.draw(
						chain,
						bd.getWorldCenter().x*100f - chain.getWidth()*factor/2f,
						bd.getWorldCenter().y*100f - chain.getHeight()*factor/2f,
						chain.getWidth()*factor/2f,
						chain.getHeight()*factor/2f,
						chain.getWidth() * factor,
						chain.getHeight() * factor,
						1.7f,
						1.7f,
						(float)Math.toDegrees(bd.getAngle()),
						0,
						0,
						chain.getWidth(),
						chain.getHeight(),
						false,
						false);
			}
			
			sb.end();
			
			sb.begin();
			
			sb.draw(back_tex,
					(Gdx.graphics.getWidth() - back_tex.getWidth()*factor)/2f,
					Gdx.graphics.getHeight() - back_tex.getHeight()*(1/3f) * factor,
					back_tex.getWidth()*factor/2f,
					back_tex.getHeight() * factor/2f,
					back_tex.getWidth()*factor,
					back_tex.getHeight() * factor,
					1,
					1,
					back_angle,
					0,
					0,
					back_tex.getWidth(),
					back_tex.getHeight(),
					false,
					false);
			

			sb.draw(
					pressStart,
					pressStartBody.getWorldCenter().x*100f - pressStart.getWidth()*factor/2f,
					pressStartBody.getWorldCenter().y*100f - pressStart.getHeight()*factor/2f,
					pressStart.getWidth()*factor/2f,
					pressStart.getHeight()*factor/2f,
					pressStart.getWidth() * factor,
					pressStart.getHeight() * factor,
					1,
					1,
					(float)Math.toDegrees(pressStartBody.getAngle()),
					0,
					0,
					pressStart.getWidth(),
					pressStart.getHeight(),
					false,
					false);

		
			//b2dr.render(world, camera.combined);
			
		//FIM DESENHO MENU
		sb.end();
		shaderBuffer.end();
		
		shader.begin();
		shader.setUniformf("intensity", shaderIntensity);
		
		sb.setShader(shader);
			sb.begin();
				sb.draw(shaderBuffer.getColorBufferTexture(),
						0, 0,
						Gdx.graphics.getWidth(),
						Gdx.graphics.getHeight(),
						0, 0,
						Gdx.graphics.getWidth(),
						Gdx.graphics.getHeight(),
						false, true);
			sb.end();	
		sb.setShader(null);
		shader.end();
		
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		sr.setProjectionMatrix(Util.getNormalProjection());
		sr.begin(ShapeType.Filled);
		sr.setColor(0, 0, 0, alpha);
		sr.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		sr.end();
		
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

	@Override
	public void update(float delta) {
		
		world.step(1/60f, 6, 2);
		camera.update();
		globalTimer += delta;
		
		timer -= delta;

		if(outro && goingBack) {
			back_angle += delta*100;
		}
		
		if(allReady) {
			if(!hasFallen) {
				pressStartBody.setTransform(new Vector2(Gdx.graphics.getWidth()/2f / 100f,  Gdx.graphics.getHeight()*2 / 100f), 0);
				pressStartBody.setLinearVelocity(0, 0);
				hasFallen = true;
			}
		}
		else {
			hasFallen = false;
			pressStartBody.applyForceToCenter(new Vector2(0, 20), true);
		}
		
		allReady = true;
		for(int i = 0; i < 4; i ++) {
			if(KambojaMain.getControllers().size()-1 >= i){
				if(KambojaMain.getControllers().get(i) != null) {
					if(!playerReady[i]) {
						allReady = false;
						break;
					}
				}
			}
			else {
				if(i <= 1) allReady = false;
			}
		}
		
		
		
		fogo.update(delta/2f);
		bolinha.update(delta);

		if(intro){
			alpha -= delta;
			if(alpha <= 0){
				intro = false;
				alpha = 0;
				
			}
			
		}
		if(outro){
			alpha += delta;
			if(alpha >= 1){
				outro = false;
				alpha = 1;
				if(goingBack) {
					goingBack = false;
					manager.changeState(5);
				}
				else {
					manager.changeState(7);
				}
				
			}
		}
		
		
		for(int i = 0; i < 4; i ++) {
			if(KambojaMain.getControllers().size()-1 >= i){
				if(KambojaMain.getControllers().get(i) != null) {
					
					
					if(playerReady[i]) {
						okAlpha[i] += (1 - okAlpha[i])/10.0f;
						okScale[i] += (1 - okScale[i])/10.0f;
						okAngle[i] += (0 - okAngle[i])/10.0f;
						gear_angle[i] += (180 - gear_angle[i])/10.0f;
					}
					else {
						okAlpha[i] += (0 - okAlpha[i])/10.0f;
						okScale[i] += (2 - okScale[i])/10.0f;
						okAngle[i] += (30 - okAngle[i])/10.0f;
						gear_angle[i] += (0 - gear_angle[i])/10.0f;
					}
					
					selection_bound_tween[i].setRect(
							selection_bound_tween[i].getX() + (selection_bounds[i][selection[i]].getX() - selection_bound_tween[i].getX())/10f,
							selection_bound_tween[i].getY() + (selection_bounds[i][selection[i]].getY() - selection_bound_tween[i].getY())/10f,
							selection_bound_tween[i].getWidth() + (selection_bounds[i][selection[i]].getWidth() - selection_bound_tween[i].getWidth())/10f,
							selection_bound_tween[i].getHeight() + (selection_bounds[i][selection[i]].getHeight() - selection_bound_tween[i].getHeight())/10f
							);
					
					tween_key_x[i] += (key_x[i] - tween_key_x[i])/10.0f;
					tween_key_y[i] += (key_y[i] - tween_key_y[i])/10.0f;

				}
			}
			
			selection_bounds[i][1].setRect(
					body_subframes[i].getWorldCenter().x*100f - player_subframes[0].getWidth()*factor/2f,
					body_subframes[i].getWorldCenter().y*100f - player_subframes[0].getHeight()*factor/2f,
					player_subframes[i].getWidth() * factor,
					player_subframes[i].getHeight() * factor
					);
		}
		
		shaderIntensity += (intensityTarget - shaderIntensity) / 10.0f;
		
		if(timer < 0){
			timer = (float)Math.random() * 0.5f;
			intensityTarget = (float)(Math.random() * 0.3f) - 0.15f;
		}
		
	}
	
	
	public int firstPlayerAvailable(){
		for(int i = 0; i < KambojaMain.getPlayerSkinsSize(); i ++){
			if(isAvailable(i))
			return i;
		}
		
		return -1;
	}
	
	public boolean isAvailable(int player){
		for(PlayerController pc : KambojaMain.getControllers()){
			if(pc != null) {
				if(pc.getPlayer() == player){
					return false;
				}
			}
		}
		return true;
	}
	
	public int nextPlayer(int player){
		int next = player+1;
		if(next == KambojaMain.getPlayerSkinsSize()) next = 0;
		
		while(!isAvailable(next)){
			next++;
			if(next == KambojaMain.getPlayerSkinsSize()) next = 0;
		}
		
		return next;
	}
	
	public int previousPlayer(int player){
		int prev = player-1;
		if(prev == -1) prev = KambojaMain.getPlayerSkinsSize()-1;
		
		while(!isAvailable(prev)){
			prev--;
			if(prev == -1) prev = KambojaMain.getPlayerSkinsSize()-1;
		}
		
		return prev;
	}

	@Override
	public void connected(Controller controller) {
		
	}

	@Override
	public void disconnected(Controller controller) {
		
	}

	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {
		
		int id = Util.getControllerID(controller);
		
		int select = 0;
		int start = 0;
		int back = 0;
		int disc = 0;
		
		if(controller.getName().equals(Gamecube.getID())){
			select = Gamecube.A;
			start = Gamecube.START;
			back = Gamecube.B;
			disc = Gamecube.Y;

		}
		else if(controller.getName().toUpperCase().contains("XBOX") && controller.getName().contains("360")){
			select = XBox.BUTTON_A;
			start = XBox.BUTTON_START;
			back = XBox.BUTTON_B;
			disc = XBox.BUTTON_Y;
		}
		else{
			select = GenericController.X;
			start = GenericController.START;
			back = GenericController.CIRCLE;
			disc = GenericController.TRIANGLE;
		}
		
		if(id != -1) {
		body_subframes[id].applyLinearImpulse(
				new Vector2((float)(Math.random() * 0.6 - 0.3), (float)(Math.random() * 0.6 - 0.3)),
				body_subframes[id].getWorldCenter(), true);

		if(buttonCode == select) {
			if(id != -1){
				if(!typing[id]) {
					switch(selection[id]) {
						case 2:
							typing[id] = true;
							selection[id] = 1;
							break;
						case 0:
							playerReady[id] = true;
							break;
						case 4:
							outro = true;
							goingBack = true;
							break;
					}
				}
				else {
					KambojaMain.getControllers().get(id).addLetterToName(keys[key_x[id]][3-key_y[id]]);
				}

			}
		}
		
		
		if(buttonCode == back) {
			if(!typing[id]) {
				if(!playerReady[id]) {
					selection[id] = 4;
				}
				else {
					playerReady[id] = false;
				}
			}
			else {
				KambojaMain.getControllers().get(id).removeLetterFromName();
			}
		}
		
		if(buttonCode == disc) {
			if(!typing[id] && !playerReady[id])
			KambojaMain.getControllers().set(id, null);
		}
		
		}
		
		if(buttonCode == start){
			if(id == -1){
				
				int put_id = Util.getFirstAvailableID();
				if(put_id != -1) {
					PlayerController pc = new PlayerController(0, controller, firstPlayerAvailable(), "Player " + (put_id+1));
					KambojaMain.getControllers().remove(put_id);
					KambojaMain.getControllers().add(put_id, pc);
				}
				else {
					if(KambojaMain.getControllers().size() < 4) {
						PlayerController pc = new PlayerController(0, controller, firstPlayerAvailable(), "Player " + (KambojaMain.getControllers().size()+1));
						KambojaMain.getControllers().add(pc);
						
						positionPlayerOffset[KambojaMain.getControllers().size() - 1] = pc.getPlayer();
						positionWeaponOffset[KambojaMain.getControllers().size() - 1] = 0;
					}
				}
			}
			else {
				if(typing[id]) {
					typing[id] = false;
					selection[id] = 2;
				}
				else if(!allReady && !playerReady[id]) {
					selection[id] = 0;
				}
				else if(allReady) {
					outro = true;
				}
			}
		}
		
		
		return false;
	}



	@Override
	public boolean buttonUp(Controller controller, int buttonCode) {
		return false;
	}

	
	public void changePlayer(float value, int id) {
		if(value > 0) {
			int player = KambojaMain.getControllers().get(id).getPlayer();
			
			int dif = nextPlayer(player) - player;
			if(dif > 0) {
				positionPlayerOffset[id] += dif;
			}
			else {
				positionPlayerOffset[id] += KambojaMain.getPlayerSkinsSize() + dif;
			}
			
			player = nextPlayer(player);
			KambojaMain.getControllers().get(id).setPlayer(player);
		}
		else {
			int player = KambojaMain.getControllers().get(id).getPlayer();
			
			int dif = player - previousPlayer(player);
			if(dif > 0) {
				positionPlayerOffset[id] -= dif;
			}
			else {
				positionPlayerOffset[id] -= KambojaMain.getPlayerSkinsSize() + dif;
			}
			
			player = previousPlayer(player);
			KambojaMain.getControllers().get(id).setPlayer(player);
			
		}
	}
	
	public void changeSelection(float value, int id) {
		if(!typing[id] && !playerReady[id]) {
			if(value < 0) {
				
				if(selection[id] < 4)
				selection[id] ++;
				
			}
			else {
				if(selection[id] > 0)
				selection[id] --;
				
			}
		}
	}
	
	public void changeLetterX(float value, int id) {
		if(value > 0) {
			if(key_x[id] + 1 < keys.length) {
				if(!keys[key_x[id]+1][3-key_y[id]].equals("")) {
					key_x[id]++;
				}
			}
		}
		else {
			if(key_x[id] - 1 >= 0) {
				if(!keys[key_x[id]-1][3-key_y[id]].equals("")) {
					key_x[id]--;
				}
			}
		}
	}	
	
	public void changeLetterY(float value, int id) {
		if(value < 0) {
			if(key_y[id] + 1 < keys[0].length) {
				if(!keys[key_x[id]][3-(key_y[id]+1)].equals("")) {
					key_y[id]++;
				}
			}
		}
		else {
			if(key_y[id] - 1 >= 0) {
				if(!keys[key_x[id]][3-(key_y[id]-1)].equals("")) {
					key_y[id]--;
				}
			}
		}
	}
	
	public void changeWeapon(float value, int id) {
		
		if(value > 0) {
			KambojaMain.getControllers().get(id).nextWeapon();
			positionWeaponOffset[id] ++;
		}
		else {
			KambojaMain.getControllers().get(id).previousWeapon();	
			positionWeaponOffset[id] --;
		}
	}

	boolean xMoved = false;
	boolean yMoved = false;
	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {
		int id = Util.getControllerID(controller);
		
		
		if(id != -1){
			if(controller.getName().equals(Gamecube.getID())){
				if(axisCode == Gamecube.MAIN_X) {
					if(Math.abs(value) > 0.5f) {
						if(!xMoved) {
							xMoved = true;
							
							if(!typing[id]) {
								if(selection[id] == 3)
								changePlayer(value, id);
								
								if(selection[id] == 1)
								changeWeapon(value, id);
							}
							else {
								changeLetterX(value, id);
							}
						}
					}
					else {
						xMoved = false;
					}
				}
				if(axisCode == Gamecube.MAIN_Y) {
					if(Math.abs(value) > 0.5f) {
						if(!yMoved) {
							yMoved = true;
							if(!typing[id]) {
								changeSelection(value, id);
							}
							else {
								changeLetterY(value, id);
							}
						}
					}
					else {
						yMoved = false;
					}
				}
			}
			else if(controller.getName().toUpperCase().contains("XBOX") && controller.getName().contains("360")){
				if(axisCode == XBox.AXIS_LEFT_X) {
					if(Math.abs(value) > 0.5f) {
						if(!xMoved) {
							xMoved = true;
							
							if(!typing[id]) {
								if(selection[id] == 3)
								changePlayer(value, id);
								
								if(selection[id] == 1)
								changeWeapon(value, id);
							}
							else {
								changeLetterX(value, id);
							}
						}
					}
					else {
						xMoved = false;
					}
				}
				if(axisCode == Gamecube.MAIN_Y) {
					if(Math.abs(value) > 0.5f) {
						if(!yMoved) {
							yMoved = true;

							if(!typing[id]) {
								changeSelection(value, id);
							}
							else {
								changeLetterY(value, id);
							}
						}
					}
					else {
						yMoved = false;
					}
				}
			}
			else {
				if(axisCode == GenericController.LEFT_X) {
					if(Math.abs(value) > 0.5f) {
						if(!xMoved) {
							xMoved = true;
							
							if(!typing[id]) {
								if(selection[id] == 3)
								changePlayer(value, id);
								
								if(selection[id] == 1)
								changeWeapon(value, id);
							}
							else {
								changeLetterX(value, id);
							}
							
						}
					}
					else {
						xMoved = false;
					}
				}
				if(axisCode == Gamecube.MAIN_Y) {
					if(Math.abs(value) > 0.5f) {
						if(!yMoved) {
							yMoved = true;

							if(!typing[id]) {
								changeSelection(value, id);
							}
							else {
								changeLetterY(value, id);
							}
						}
					}
					else {
						yMoved = false;
					}
				}
			}
		}
		return false;
	}
	
	public KeyboardController getKeyboardController() {
		
		for(PlayerController pc : KambojaMain.getControllers()) {
			if(pc instanceof KeyboardController) {
				return (KeyboardController)pc;
			}
		}
		
		return null;
	}
	
	public boolean keyDown(int keycode) {
		
		int id = Util.getControllerID(getKeyboardController());
		
		if(keycode == Keys.ENTER) {
			if(id == -1) {
				int put_id = Util.getFirstAvailableID();
				if(put_id != -1) {
					KeyboardController pc = new KeyboardController(0, firstPlayerAvailable(), "Player " + (put_id+1));
					KambojaMain.getControllers().remove(put_id);
					KambojaMain.getControllers().add(put_id, pc);
				}
				else {
					if(KambojaMain.getControllers().size() < 4) {
						KeyboardController pc = new KeyboardController(0, firstPlayerAvailable(), "Player " + (KambojaMain.getControllers().size()+1));
						KambojaMain.getControllers().add(pc);
						
						positionPlayerOffset[KambojaMain.getControllers().size() - 1] = pc.getPlayer();
						positionWeaponOffset[KambojaMain.getControllers().size() - 1] = 0;
					}
				}
			}
			else {
				if(!typing[id]) {
					if(playerReady[id]) {
						if(allReady) {
							outro = true;
						}
					}
					else {
						switch(selection[id]) {
							case 2:
								typing[id] = true;
								selection[id] = 1;
								break;
							case 0:
								playerReady[id] = true;
								break;
							case 4:
								outro = true;
								goingBack = true;
								break;
						}
					}
				}
				else {
					typing[id] = false;
					selection[id] = 2;
				}

			}
		}
		if(keycode == Keys.DOWN || keycode == Keys.S) {
			if(id != -1) {
				if(!typing[id]) {
					changeSelection(1, id);
				}
				else {
					changeLetterY(1, id);
				}
			}
		}
		if(keycode == Keys.UP || keycode == Keys.W) {
			if(id != -1) {
				if(!typing[id]) {
					changeSelection(-1, id);
				}
				else {
					changeLetterY(-1, id);
				}
			}
		}
		if(keycode == Keys.LEFT || keycode == Keys.A) {
			if(id != -1) {
				if(!typing[id]) {
					if(selection[id] == 3)
					changePlayer(-1, id);
					
					if(selection[id] == 1)
					changeWeapon(-1, id);
				}
				else {
					changeLetterX(-1, id);
				}
			}
		}
		if(keycode == Keys.RIGHT || keycode == Keys.D) {
			if(id != -1) {
				if(!typing[id]) {
					if(selection[id] == 3)
					changePlayer(1, id);
					
					if(selection[id] == 1)
					changeWeapon(1, id);
				}
				else {
					changeLetterX(1, id);
				}
			}
		}
		if(keycode == Keys.ESCAPE) {
			if(id != -1) {
				if(!typing[id] && !playerReady[id])
					KambojaMain.getControllers().set(id, null);
			}
		}
		
		if(keycode == Keys.BACKSPACE || keycode == Keys.DEL || keycode == Keys.FORWARD_DEL) {
			if(!typing[id]) {
				if(!playerReady[id]) {
					selection[id] = 4;
				}
				else {
					playerReady[id] = false;
				}
			}
			else {
				KambojaMain.getControllers().get(id).removeLetterFromName();
			}
		}
		
		if(id != -1) {
			if(typing[id]) {
				if(keycode >= 29 && keycode <= 54) {
					KambojaMain.getControllers().get(id).addLetterToName(getLetterFromKeycode(keycode));
				}
			}
		}
		
		return false;
	}
	
	public String getLetterFromKeycode(int keycode) {
		return Input.Keys.toString(keycode);
	}

	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean povMoved(Controller controller, int povCode, PovDirection value) {
		
		return false;
	}

	@Override
	public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
		return false;
	}

	@Override
	public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
		return false;
	}

	@Override
	public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
		return false;
	}

	@Override
	public void resize(int width, int height) {
		
	}

}
