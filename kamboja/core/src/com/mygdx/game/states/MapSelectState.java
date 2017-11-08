package com.mygdx.game.states;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.files.FileHandle;
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
import com.mygdx.game.objects.KeyboardController;
import com.mygdx.game.objects.PlayerController;
import com.mygdx.game.objects.Util;

public class MapSelectState extends State{
	
	boolean intro;
	boolean outro;

	float alpha;
	
	float timer;
	
	Texture background;
	Texture options_frame;
	Texture map_frame;
	Texture chain;
	Texture map_name;
	Texture gear_start;
	Texture gear_back;
	Texture map_containers;
	Texture selection_tex;
	TextureRegion[] map_container = new TextureRegion[16];
	
	Rectangle2D selection_bounds[] = new Rectangle2D[21];
	Rectangle2D selection_bound_tween[] = new Rectangle2D[4];
	
	ParticleEffect fogo;
	ParticleEffect bolinha;
	
	BitmapFont outlander;
	BitmapFont oliver_barney;
	GlyphLayout layout;
	
	float options_x;

	ShapeRenderer sr;
	
	int selected_map = 0;
	
	int selection[] = new int[4];
	
	float factor;
	
	private ArrayList<Texture> thumbs;
	private ArrayList<String> mapNames;
	private ArrayList<String> mapTitles;
	
	FrameBuffer shaderBuffer;
	ShaderProgram shader;
	private float shaderIntensity;
	private float intensityTarget;
	
	World world;
	Box2DDebugRenderer b2dr;
	OrthographicCamera camera;
	
	Body mapBody;
	Body mapNameBody;
	
	ArrayList<Body> chainBody;
	
	FrameBuffer mapNameBuffer;
	Matrix4 nameBufferProjection;
	private boolean goingBack;

	
	public MapSelectState(Manager manager) {
		super(manager);
	}

	public void create() {
		
		intro = true;
		outro = false;
		alpha = 1;
		
		timer = 0;
		
		chainBody = new ArrayList<Body>();
		chain = new Texture("menu/player_select/chain.png");
		map_name = new Texture("menu/map_select/map_name.png");
		
		mapNameBuffer = new FrameBuffer(Format.RGBA8888, map_name.getWidth(), map_name.getHeight(), false);
		nameBufferProjection = new Matrix4();
		nameBufferProjection.setToOrtho2D(0, 0, map_name.getWidth(), map_name.getHeight());
		
		gear_start = new Texture("menu/map_select/gear_start.png");
		gear_back = new Texture("menu/map_select/gear_back.png");
		map_containers = new Texture("menu/map_select/map_containers.png");
		
		for(int i = 0; i < 4; i ++) {
			for(int j = 0; j < 4; j ++) {
				map_container[i + j*4] = new TextureRegion(map_containers, i*217, j*199, 217, 199);
			}
		}
		
		selection_tex = new Texture("menu/player_select/selection.png");
		

		world = new World(new Vector2(0, -9.81f), false);
		b2dr = new Box2DDebugRenderer();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(Gdx.graphics.getWidth()/2/100f, Gdx.graphics.getHeight()/2f/100f, 0);
		camera.zoom = 1/100f;
		
		options_x = Gdx.graphics.getWidth()/2f;
		
		map_frame = new Texture("menu/map_select/frame_map.png");

		shaderIntensity = 0;
		intensityTarget = 0;
		
		sr = new ShapeRenderer();
		
		background = new Texture("menu/map_select/fundo.jpg");
		options_frame = new Texture("menu/map_select/frame_options.png");
		
		factor = Gdx.graphics.getHeight() / 1080f;
		

		FreeTypeFontGenerator ftfg;
		FreeTypeFontParameter param;
		ftfg = new FreeTypeFontGenerator(Gdx.files.internal("fonts/outlander.ttf"));
		param = new FreeTypeFontParameter();
		param.size = (int) (100f * factor);
		param.color = new Color(199/255f, 224/255f, 243/255f, 1f).mul(0.9f);
		outlander = ftfg.generateFont(param);
		ftfg = new FreeTypeFontGenerator(Gdx.files.internal("fonts/olivers barney.ttf"));
		param = new FreeTypeFontParameter();
		param.size = (int) (60 * factor);
		param.color = new Color(255/255f, 48/255f, 77/255f, 1f);
		oliver_barney = ftfg.generateFont(param);
		ftfg.dispose();
		
		layout = new GlyphLayout();
		
		fogo = new ParticleEffect();
		fogo.load(Gdx.files.internal("particles/fogo.par"), Gdx.files.internal("particles"));
		fogo.setPosition(Gdx.graphics.getWidth()/2f, -32*factor);
		fogo.scaleEffect(10*factor);
		
		bolinha = new ParticleEffect();
		bolinha.load(Gdx.files.internal("particles/bolinha.par"), Gdx.files.internal("particles"));
		bolinha.setPosition(Gdx.graphics.getWidth()/2f, Gdx.graphics.getHeight()/2f);
		bolinha.scaleEffect(factor);
		
		shader = new ShaderProgram(Gdx.files.internal("shaders/default.vs"),
				Gdx.files.internal("shaders/color_shift.fs"));
		ShaderProgram.pedantic = false;
		if(shader.getLog().length() > 0){
			System.out.println(shader.getLog());
		}
		shaderBuffer = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		
		
		mapBody = createBox(
				new Vector2(Gdx.graphics.getWidth() * (3/4f), Gdx.graphics.getHeight() * (3/4f)),
				new Vector2(444/2f*factor, 413/2f*factor), BodyType.DynamicBody, 0.1f);
		
		mapNameBody = createBox(
				new Vector2(Gdx.graphics.getWidth() * (3/4f), Gdx.graphics.getHeight()/2f),
				new Vector2(402/2f*factor, 88/2f*factor), BodyType.DynamicBody, 0.1f);
		
		buildRopeJoint((int)(10 * factor));
		buildRopeJoint2((int)(3 * factor));
		
		thumbs = new ArrayList<Texture>();
		mapTitles = new ArrayList<String>();
		mapNames = new ArrayList<String>();
		File folder = new File("maps");
		
		for(File f : folder.listFiles()){
			if(f.getName().endsWith(".tmx")){
				
				System.out.println("path " + f.getPath());
				mapNames.add(f.getPath());
				
				String thumbnailPath = "maps/thumb_" + f.getName().split("\\.")[0] + ".png";
				System.out.println(thumbnailPath);
				thumbs.add(new Texture(new FileHandle(new File(thumbnailPath))));
				
				String mapTitle = f.getName().replaceFirst(f.getName().substring(0, 1), f.getName().substring(0, 1).toUpperCase());
				System.out.println(mapTitle.split("\\.")[0]);
				mapTitles.add(mapTitle.split("\\.")[0]);
			}
		}
		mapNames.add("");
		thumbs.add(new Texture(new FileHandle(new File("maps/thumb_random.png"))));
		mapTitles.add("Random");
		
		for(int i = 0; i < 16; i ++) {
			int x = i % 4;
			int y = i / 4;
			selection_bounds[i] = new Rectangle2D.Double(
					x*250*factor - 217*factor/2f + 190*factor,
					Gdx.graphics.getHeight() - (y*250*factor - 199*factor/2f) - 360*factor,
					237*factor,
					219*factor
					);

		}
		
		selection_bounds[16] = new Rectangle2D.Double(
				Gdx.graphics.getWidth()* (1/4f) - gear_back.getWidth()/2f*factor,
				-30*factor - gear_back.getHeight()/2f*factor,
				gear_back.getWidth()*factor,
				gear_back.getHeight()*factor
				);
		
		selection_bounds[17] = new Rectangle2D.Double(
				Gdx.graphics.getWidth()/2f - gear_start.getWidth()/2f*factor,
				-30*factor - gear_start.getHeight()/2f*factor,
				gear_start.getWidth()*factor,
				gear_start.getHeight()*factor
				);
		
		
		String tm = "" + (KambojaMain.getGameTime() == -1 ? "Inf." :
			KambojaMain.getGameTime()/60 + ":" + ((KambojaMain.getGameTime() % 60 >= 10) ? KambojaMain.getGameTime() % 60 : "0" + KambojaMain.getGameTime() % 60));
	
		layout.setText(oliver_barney, tm);
		selection_bounds[18] = new Rectangle2D.Double(Gdx.graphics.getWidth() - 650*factor,
				190* factor, 550*factor, 120*factor);

		tm = "" + (KambojaMain.getDeathsNumber() == -1  ? "Inf." : KambojaMain.getDeathsNumber());

		layout.setText(oliver_barney, tm);
		selection_bounds[19] = new Rectangle2D.Double(Gdx.graphics.getWidth() - 650*factor,
				120* factor, 550*factor, 120*factor);

		tm = "" + (KambojaMain.hasItems()  ? "on" : "off");
		
		layout.setText(oliver_barney, tm);
		selection_bounds[20] = new Rectangle2D.Double(Gdx.graphics.getWidth() - 650*factor,
				50* factor, 550*factor, 120*factor);

		
		for(int i = 0; i < 4; i ++) {
			selection[i] = i;
			selection_bound_tween[i] = (Rectangle2D) selection_bounds[i].clone();
		}
	}

	public void dispose() {
		
	}
	
	public Body createBox(Vector2 pos, Vector2 size, BodyType type, float density) {
		BodyDef def = new BodyDef();
		def.type = type;
		def.linearDamping = 0.2f;
		def.position.set(pos.cpy().scl(1/100f));
		
		Body b = world.createBody(def);
		
		PolygonShape s = new PolygonShape();
		s.setAsBox(size.x / 100f, size.y / 100f);
		
		b.createFixture(s, density);
		
		return b;
	}
	
	public void buildRopeJoint(int numChains) {
		
		for(int k = -1; k <= 1; k += 2) {
			Array<Body> bodies = new Array<Body>();
			
			for(int i = 0; i < numChains; i ++) {
				Body b = createBox(
						new Vector2(Gdx.graphics.getWidth()*(3/4f) + k*100, Gdx.graphics.getHeight()-(30*i) + 50*factor),
						new Vector2(5f, 20), i == 0 ? BodyType.StaticBody : BodyType.DynamicBody, 1f);
				
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
			def.bodyB = mapBody;
			def.localAnchorA.set(0, -7.5f/100f);
			def.localAnchorB.set((Gdx.graphics.getWidth()*(3/4f) + k*100) /100f - mapBody.getWorldCenter().x,
					(413/2f*factor - 50*factor) / 100f);
			
			world.createJoint(def);
			
			bodies.get(1).applyLinearImpulse(new Vector2((float)Math.random()*0.1f, 0), bodies.get(1).getWorldCenter(), true);
			
		}
		
	}
	
	public void buildRopeJoint2(int numChains) {
		
		for(int k = -1; k <= 1; k += 2) {
			Array<Body> bodies = new Array<Body>();
			
			for(int i = 0; i < numChains; i ++) {
				Body b = createBox(
						new Vector2(Gdx.graphics.getWidth()*(3/4f) + k*100, Gdx.graphics.getHeight()-(30*i) - 600*factor),
						new Vector2(5f, 20), BodyType.DynamicBody, 1f);
				
				chainBody.add(b);
				bodies.add(b);
			}
			
			RevoluteJointDef def = new RevoluteJointDef();
			def.bodyA = mapBody;
			def.bodyB = bodies.get(0);
			def.localAnchorA.set((Gdx.graphics.getWidth()*(3/4f) + k*100) /100f - mapBody.getWorldCenter().x,
					-(413/2f*factor - 50*factor) / 100f);
			def.localAnchorB.set(0, 15f/100f);
			
			world.createJoint(def);
			
			for(int i = 1; i < numChains; i ++) {
				def = new RevoluteJointDef();
				def.bodyA = bodies.get(i-1);
				def.bodyB = bodies.get(i);
				def.localAnchorA.set(0, -15f/100f);
				def.localAnchorB.set(0, 15f/100f);
				
				world.createJoint(def);
			}
			
			def = new RevoluteJointDef();
			def.bodyA = bodies.get(bodies.size - 1);
			def.bodyB = mapNameBody;
			def.localAnchorA.set(0, -7.5f/100f);
			def.localAnchorB.set((Gdx.graphics.getWidth()*(3/4f) + k*100) /100f - mapBody.getWorldCenter().x,
					(200/2f*factor - 50*factor) / 100f);
			
			world.createJoint(def);
			
			bodies.get(1).applyLinearImpulse(new Vector2((float)Math.random()*0.1f, 0), bodies.get(1).getWorldCenter(), true);
			
		}
		
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
	
	public void render(SpriteBatch sb) {
		
		mapNameBuffer.begin();
		Gdx.gl.glClearColor(1, 1, 1, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			sb.setProjectionMatrix(nameBufferProjection);
			sb.begin();
			
			layout.setText(outlander, mapTitles.get(selected_map));
			outlander.draw(sb, mapTitles.get(selected_map),
					(mapNameBuffer.getWidth() - layout.width)/2f,
					(mapNameBuffer.getHeight() - layout.height)/2f + layout.height);
			
			sb.end();
		
		mapNameBuffer.end();
		
		shaderBuffer.begin();
		sb.begin();
		sb.setProjectionMatrix(Util.getNormalProjection());
		
		sb.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	
		bolinha.draw(sb);
		fogo.draw(sb);
	
		sb.draw(options_frame,
				options_x,
				-100*factor,
				options_frame.getWidth() * factor,
				options_frame.getHeight() * factor);
		
		sb.draw(map_frame,
				mapBody.getWorldCenter().x * 100f - map_frame.getWidth()/2f*factor,
				mapBody.getWorldCenter().y * 100f - map_frame.getHeight()/2f*factor,
				map_frame.getWidth()/2f*factor,
				map_frame.getHeight()/2f*factor,
				map_frame.getWidth()*factor,
				map_frame.getHeight()*factor,
				1, 1,
				(float)Math.toDegrees(mapBody.getAngle()),
				0, 0,
				map_frame.getWidth(),
				map_frame.getHeight(),
				false, false);
		
		sb.draw(thumbs.get(selected_map),
				mapBody.getWorldCenter().x * 100f - map_frame.getWidth()/2f*factor * 0.6f,
				mapBody.getWorldCenter().y * 100f - map_frame.getHeight()/2f*factor * 0.6f - 20*factor,
				map_frame.getWidth()/2f*factor * 0.6f,
				map_frame.getHeight()/2f*factor * 0.6f,
				map_frame.getWidth()*factor * 0.6f,
				map_frame.getHeight()*factor * 0.6f,
				1, 1,
				(float)Math.toDegrees(mapBody.getAngle()),
				0, 0,
				thumbs.get(selected_map).getWidth(),
				thumbs.get(selected_map).getHeight(),
				false, false);
		
		sb.draw(map_name,
				mapNameBody.getWorldCenter().x * 100f - map_name.getWidth()/2f*factor,
				mapNameBody.getWorldCenter().y * 100f - map_name.getHeight()/2f*factor,
				map_name.getWidth()/2f*factor,
				map_name.getHeight()/2f*factor,
				map_name.getWidth()*factor,
				map_name.getHeight()*factor,
				1, 1,
				(float)Math.toDegrees(mapNameBody.getAngle()),
				0, 0,
				map_name.getWidth(),
				map_name.getHeight(),
				false, false);
		
		sb.draw(mapNameBuffer.getColorBufferTexture(),
				mapNameBody.getWorldCenter().x * 100f - map_name.getWidth()/2f*factor,
				mapNameBody.getWorldCenter().y * 100f - map_name.getHeight()/2f*factor,
				map_name.getWidth()/2f*factor,
				map_name.getHeight()/2f*factor,
				map_name.getWidth()*factor,
				map_name.getHeight()*factor,
				1, 1,
				(float)Math.toDegrees(mapNameBody.getAngle()),
				0, 0,
				map_name.getWidth(),
				map_name.getHeight(),
				false, true);
		
		sb.draw(gear_start,
				Gdx.graphics.getWidth()/2f - gear_start.getWidth()/2f*factor,
				-30*factor - gear_start.getHeight()/2f*factor,
				gear_start.getWidth()/2f*factor,
				gear_start.getHeight()/2f*factor,
				gear_start.getWidth()*factor,
				gear_start.getHeight()*factor,
				1, 1,
				0,
				0, 0,
				gear_start.getWidth(),
				gear_start.getHeight(),
				false, false);
		
		sb.draw(gear_back,
				Gdx.graphics.getWidth()* (1/4f) - gear_back.getWidth()/2f*factor,
				-30*factor - gear_back.getHeight()/2f*factor,
				gear_back.getWidth()/2f*factor,
				gear_back.getHeight()/2f*factor,
				gear_back.getWidth()*factor,
				gear_back.getHeight()*factor,
				1, 1,
				0,
				0, 0,
				gear_back.getWidth(),
				gear_back.getHeight(),
				false, false);
		
		String tm = "" + (KambojaMain.getGameTime() == -1 ? "Inf." :
			KambojaMain.getGameTime()/60 + ":" + ((KambojaMain.getGameTime() % 60 >= 10) ? KambojaMain.getGameTime() % 60 : "0" + KambojaMain.getGameTime() % 60));
	
		layout.setText(oliver_barney, tm);
		oliver_barney.draw(sb, tm,
				Gdx.graphics.getWidth() - 400*factor + options_x - layout.width/2f,
				275* factor);
		
		tm = "" + (KambojaMain.getDeathsNumber() == -1  ? "Inf." : KambojaMain.getDeathsNumber());

		layout.setText(oliver_barney, tm);
		oliver_barney.draw(sb, tm,
				Gdx.graphics.getWidth() - 400*factor + options_x - layout.width/2f,
				210* factor);
		
		tm = "" + (KambojaMain.hasItems()  ? "on" : "off");
		
		layout.setText(oliver_barney, tm);
		oliver_barney.draw(sb, tm,
				Gdx.graphics.getWidth() - 400*factor + options_x - layout.width/2f,
				140* factor);
				
		for(int i = 0; i < mapNames.size(); i ++){
			int x = i % 4;
			int y = i / 4;
			if(i < thumbs.size()) {
				
				sb.draw(map_container[i],
						x*250*factor - 217*factor/2f + 200*factor,
						Gdx.graphics.getHeight() - (y*250*factor - 199*factor/2f) - 350*factor,
						217*factor,
						199*factor
						);
				
				sb.draw(thumbs.get(i),
						x*250*factor - 192*factor/2f + 200*factor,
						Gdx.graphics.getHeight() - (y*250*factor - 174*factor/2f) - 325*factor,
						192*factor,
						174*factor
						);
				
				if(KambojaMain.mapUnlocked[i])
				sb.setColor(1, 1, 1, 0.3f);
				else
				sb.setColor(0.3f, 0.3f, 0.3f, 1f);
				
				sb.draw(map_container[i],
						x*250*factor - 217*factor/2f + 200*factor,
						Gdx.graphics.getHeight() - (y*250*factor - 199*factor/2f) - 350*factor,
						217*factor,
						199*factor
						);
				
				sb.setColor(1, 1, 1, 1);
			}
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
					2.4f,
					2.4f,
					(float)Math.toDegrees(bd.getAngle()),
					0,
					0,
					chain.getWidth(),
					chain.getHeight(),
					false,
					false);
		}
		
		for(int i = 0; i < 4; i ++) {
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
		}
		
		sb.setColor(1, 1, 1, 1);
		
		sb.end();
		
		//b2dr.render(world, camera.combined);
		
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

	public void update(float delta) {
		
		timer -= delta;
		
		options_x += (0 - options_x)/10.0f;
		
		camera.update();
		world.step(1/60f, 6, 2);
		
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
					manager.changeState(6);
				}
				else {
					manager.changeState(2);
				}
			}
		}
		
		if(timer < 0){
			timer = (float)Math.random() * 0.5f;
			intensityTarget = (float)(Math.random() * 0.3f) - 0.15f;
		}
		
		for(int i = 0; i < 4; i ++) {
			if(KambojaMain.getControllers().size()-1 >= i){
				if(KambojaMain.getControllers().get(i) != null) {
					selection_bound_tween[i].setRect(
							selection_bound_tween[i].getX() + (selection_bounds[selection[i]].getX() - selection_bound_tween[i].getX())/10f,
							selection_bound_tween[i].getY() + (selection_bounds[selection[i]].getY() - selection_bound_tween[i].getY())/10f,
							selection_bound_tween[i].getWidth() + (selection_bounds[selection[i]].getWidth() - selection_bound_tween[i].getWidth())/10f,
							selection_bound_tween[i].getHeight() + (selection_bounds[selection[i]].getHeight() - selection_bound_tween[i].getHeight())/10f
							);
				}
			}
		}
			
		
		shaderIntensity += (intensityTarget - shaderIntensity) / 10.0f;

		bolinha.update(delta);
		fogo.update(delta);
		
	}

	public void connected(Controller controller) {
		
	}

	public void disconnected(Controller controller) {
		
	}

	public boolean buttonDown(Controller controller, int buttonCode) {
		int id = Util.getControllerID(controller);
		if(id != -1){
			int select = 0;
			int backbtn = 0;
			int startbtn = 0;
			
			if(controller.getName().equals(Gamecube.getID())){
				select = Gamecube.A;
				backbtn = Gamecube.B;
				startbtn = Gamecube.START;
			}
			else if(controller.getName().toUpperCase().contains("XBOX") && controller.getName().contains("360")){
				select = XBox.BUTTON_A;
				backbtn = XBox.BUTTON_B;
				startbtn = XBox.BUTTON_START;
			}
			else{
				select = GenericController.X;
				backbtn = GenericController.TRIANGLE;
				startbtn = GenericController.START;
			}
			
			if(buttonCode == select) {
				doSelection(id);
			}
			if(buttonCode == backbtn) {
				selection[id] = 16;
			}
			if(buttonCode == startbtn) {
				selection[id] = 17;
			}
		}
		
		return false;
	}

	public boolean buttonUp(Controller controller, int buttonCode) {
		return false;
	}
	
	boolean xMoved = false;
	boolean yMoved = false;
	
	public void changeSelectionX(int id, float value) {
		if(id != -1) {
			if(value > 0) {
				if(selection[id] < 15) {
					if((selection[id]+1) % 4 != 0) {
						if(selection[id] + 1 < mapNames.size()) {
							selection[id] ++;
						}
						else {
							selection[id] = 18;
						}
					}
					else {
						selection[id] = 18;
					}
				}
				else {
					if(selection[id] == 15) {
						selection[id] = 18;
					}
					else {
						if(selection[id] == 16) selection[id] = 17;
						else if(selection[id] == 17) selection[id] = 20;
					}
				}
			}
			else {
				if(selection[id] < 16) {
					if(selection[id] % 4 != 0) {
						selection[id] --;
					}
				}
				else {
					if(selection[id] == 17) selection[id] = 16;
					else if(selection[id] > 17) selection[id] = 17;
				}
			}
		}
	}
	
	public void changeSelectionY(int id, float value) {
		if(value < 0) {
			if(selection[id] > 3) {
				if(selection[id] < 16) {
					selection[id] -= 4;
				}
				else {
					if(selection[id] <= 17) {
						selection[id] = 15;
						while(selection[id] >= mapNames.size()) {
							selection[id] --;
						}
					}
					else if(selection[id] > 18) selection[id] --;
					
					
				}
			}
		}
		else {
			if(selection[id] < 12) {
				if(selection[id] + 4 < mapNames.size()) {
					selection[id] += 4;
				}
				else {
					selection[id] = 16;
				}
			}
			else {
				if(selection[id] < 16) {
					selection[id] = 16;
				}
				else {
					if(selection[id] >= 18 && selection[id] != 20) {
						selection[id]++;
					}
				}
			}
		}
	}
	
	public boolean axisMoved(Controller controller, int axisCode, float value) {
	int id = Util.getControllerID(controller);
		
		
		if(id != -1){
			if(controller.getName().equals(Gamecube.getID())){
				if(axisCode == Gamecube.MAIN_X) {
					if(Math.abs(value) > 0.5f) {
						if(!xMoved) {
							xMoved = true;
							
							changeSelectionX(id, value);
							
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
							changeSelectionY(id, value);
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
							changeSelectionX(id, value);
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
							changeSelectionY(id, value);
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
							changeSelectionX(id, value);
							
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
							changeSelectionY(id, value);
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

	public boolean povMoved(Controller controller, int povCode, PovDirection value) {
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
	
	public KeyboardController getKeyboardController() {
			
			for(PlayerController pc : KambojaMain.getControllers()) {
				if(pc instanceof KeyboardController) {
					return (KeyboardController)pc;
				}
			}
			
			return null;
		}
	
	public void doSelection(int id) {

		switch(selection[id]) {
		case 16:
			outro = true;
			goingBack = true;
			break;
		case 17:
			outro = true;
			break;
		case 18:
			KambojaMain.setGameTime(KambojaMain.getGameTime() + 60);
			if(KambojaMain.getGameTime() == 11*60){
				if(KambojaMain.getDeathsNumber() != -1){
					KambojaMain.setGameTime(-1);
				}
				else{
					KambojaMain.setGameTime(60);
				}
			}
			if(KambojaMain.getGameTime() == 59){
				KambojaMain.setGameTime(60);
			}
			break;
		case 19:
			KambojaMain.setDeathsNumber(KambojaMain.getDeathsNumber() + 1);
			if(KambojaMain.getDeathsNumber() == 16){
				if(KambojaMain.getGameTime() != -1){
					KambojaMain.setDeathsNumber(-1);
				}
				else{
					KambojaMain.setDeathsNumber(1);
				}
			}
			if(KambojaMain.getDeathsNumber() == 0){
				KambojaMain.setDeathsNumber(1);
			}
			break;
		case 20:
			KambojaMain.setItems(!KambojaMain.hasItems());
			break;
		default:
			
			if(KambojaMain.mapUnlocked[selection[id]]) {
				selected_map = selection[id];
				if(selected_map == mapNames.size() - 1){
					selected_map = (int) (Math.random() * 4);
				}
				KambojaMain.setMapName(mapNames.get(selected_map));
			}
			
			break;
		}
	}
	
	public boolean keyDown(int keycode) {
		int id = Util.getControllerID(getKeyboardController());
		
		if(id != -1) {
			if(keycode == Keys.ENTER) {
				doSelection(id);
			}
			if(keycode == Keys.DOWN || keycode == Keys.S) {
				changeSelectionY(id, 1);
			}
			if(keycode == Keys.UP || keycode == Keys.W) {
				changeSelectionY(id, -1);
			}
			if(keycode == Keys.LEFT || keycode == Keys.A) {
				changeSelectionX(id, -1);
			}
			if(keycode == Keys.RIGHT || keycode == Keys.D) {
				changeSelectionX(id, 1);
			}
		}
		return false;
	}

	public void resize(int width, int height) {
		
	}

}
