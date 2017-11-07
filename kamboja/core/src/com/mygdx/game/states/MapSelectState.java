package com.mygdx.game.states;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
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
import com.mygdx.game.Manager;
import com.mygdx.game.State;
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
	
	ParticleEffect fogo;
	ParticleEffect bolinha;
	
	float options_x;

	ShapeRenderer sr;
	
	float factor;
	
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
	
	public void render(SpriteBatch sb) {
		
		shaderBuffer.begin();
		sb.begin();
		
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
				map_frame.getWidth()*factor,
				1, 1,
				(float)Math.toDegrees(mapBody.getAngle()),
				0, 0,
				map_frame.getWidth(),
				map_frame.getWidth(),
				false, false);
		
		sb.draw(map_name,
				mapNameBody.getWorldCenter().x * 100f - map_name.getWidth()/2f*factor,
				mapNameBody.getWorldCenter().y * 100f - map_name.getHeight()/2f*factor,
				map_name.getWidth()/2f*factor,
				map_name.getHeight()/2f*factor,
				map_name.getWidth()*factor,
				map_name.getWidth()*factor,
				1, 1,
				(float)Math.toDegrees(mapNameBody.getAngle()),
				0, 0,
				map_name.getWidth(),
				map_name.getWidth(),
				false, false);
		
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
		
		sb.end();
		
		b2dr.render(world, camera.combined);
		
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
			}
		}
		
		if(timer < 0){
			timer = (float)Math.random() * 0.5f;
			intensityTarget = (float)(Math.random() * 0.3f) - 0.15f;
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
		return false;
	}

	public boolean buttonUp(Controller controller, int buttonCode) {
		return false;
	}

	public boolean axisMoved(Controller controller, int axisCode, float value) {
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

	public void resize(int width, int height) {
		
	}

}
