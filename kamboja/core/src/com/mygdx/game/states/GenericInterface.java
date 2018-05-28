package com.mygdx.game.states;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Manager;
import com.mygdx.game.State;
import com.mygdx.game.objects.FrameBufferStack;
import com.mygdx.game.objects.Util;

public abstract class GenericInterface extends State{

	Texture background;
	Texture chain;
	ParticleEffect fogo;
	ParticleEffect bolinha;
	ParticleEffect fumaca_cano;
	ParticleEffectPool cano_pool;
	boolean intro;
	boolean outro;
	
	float timer;
	float globalTimer;
	FrameBuffer shaderBuffer;
	ShaderProgram shader;
	private float shaderIntensity;
	private float intensityTarget;
	ShapeRenderer sr;
	World world;
	Box2DDebugRenderer b2dr;
	OrthographicCamera camera;
	float alpha;
	
	ArrayList<Body> chainBody;
	
	public GenericInterface(Manager manager) {
		super(manager);
		chainBody = new ArrayList<Body>();
		chain = new Texture("menu/player_select/chain.png");
		
		sr = new ShapeRenderer();
		
		fogo = new ParticleEffect();
		fogo.load(Gdx.files.internal("particles/fogo.par"), Gdx.files.internal("particles"));
		fogo.setPosition(1920/2f, -32);
		fogo.scaleEffect(10);
		
		bolinha = new ParticleEffect();
		bolinha.load(Gdx.files.internal("particles/bolinha.par"), Gdx.files.internal("particles"));
		bolinha.setPosition(1920/2f, 1080/2f);
		
		fumaca_cano = new ParticleEffect();
		fumaca_cano.load(Gdx.files.internal("particles/cano.par"), Gdx.files.internal("particles"));
		fumaca_cano.scaleEffect(2);
		
		cano_pool = new ParticleEffectPool(fumaca_cano, 1, 5);

		shader = new ShaderProgram(Gdx.files.internal("shaders/default.vs"),
				Gdx.files.internal("shaders/color_shift.fs"));
		ShaderProgram.pedantic = false;
		if(shader.getLog().length() > 0){
			System.out.println(shader.getLog());
		}
	}
	
	public void renderImageInBody(SpriteBatch sb, Texture tex, Body body) {
		sb.draw(tex,
				body.getWorldCenter().x * 100f - tex.getWidth()/2f,
				body.getWorldCenter().y * 100f - tex.getHeight()/2f,
				tex.getWidth()/2f,
				tex.getHeight()/2f,
				tex.getWidth(),
				tex.getHeight(),
				1, 1,
				(float)Math.toDegrees(body.getAngle()),
				0, 0,
				tex.getWidth(),
				tex.getHeight(),
				false, false);
	}
	
	public void renderImageInBody(SpriteBatch sb, Texture tex, Body body, boolean flipY) {
		sb.draw(tex,
				body.getWorldCenter().x * 100f - tex.getWidth()/2f,
				body.getWorldCenter().y * 100f - tex.getHeight()/2f,
				tex.getWidth()/2f,
				tex.getHeight()/2f,
				tex.getWidth(),
				tex.getHeight(),
				1, 1,
				(float)Math.toDegrees(body.getAngle()),
				0, 0,
				tex.getWidth(),
				tex.getHeight(),
				false, flipY);
	}
	
	@Override
	public void create() {
		
		chainBody.clear();
		
		shaderIntensity = 0;
		intensityTarget = 0;
		
		intro = true;
		outro = false;
		alpha = 1;
		
		timer = 0;
		globalTimer = 0;
		
		world = new World(new Vector2(0, -9.81f), false);
		b2dr = new Box2DDebugRenderer();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1920, 1080);
		camera.position.set(1920/2/100f, 1080/2f/100f, 0);
		camera.zoom = 1/100f;
		
		
		shaderBuffer = new FrameBuffer(Format.RGBA8888, 1920, 1080, false);
		
		
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
	
	public void buildRopeJoint(int numChains, Body body, float position_x, float position_y, float spacing) {
		
		for(int k = -1; k <= 1; k += 2) {
			Array<Body> bodies = new Array<Body>();
			
			for(int i = 0; i < numChains; i ++) {
				Body b = createBox(
						new Vector2(1920/2f + k*spacing + position_x, 1080-(30*i) + 50),
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
			def.bodyB = body;
			def.localAnchorA.set(0, -7.5f/100f);
			def.localAnchorB.set(k*spacing /100f,
					position_y / 100f); //(413/2f - 100) / 100f);
			
			world.createJoint(def);
			
			bodies.get(1).applyLinearImpulse(new Vector2((float)Math.random()*0.1f, 0), bodies.get(1).getWorldCenter(), true);
			
		}
		
	}
	
	public void drawChains(SpriteBatch sb) {
		for(int i = chainBody.size() - 1; i >= 0; i --) {
			Body bd = chainBody.get(i);
			sb.draw(
					chain,
					bd.getWorldCenter().x*100f - chain.getWidth()/2f,
					bd.getWorldCenter().y*100f - chain.getHeight()/2f,
					chain.getWidth()/2f,
					chain.getHeight()/2f,
					chain.getWidth(),
					chain.getHeight(),
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
	}

	public void render(SpriteBatch sb) {
		FrameBufferStack.begin(shaderBuffer);
		sb.setProjectionMatrix(Util.getNormalProjection());
		sb.begin();
		sb.draw(background, 0, 0, 1920, 1080);
		fogo.draw(sb);
		bolinha.draw(sb);
		sb.end();
		insideRender(sb);
		
		FrameBufferStack.end();
		
		shader.begin();
		shader.setUniformf("intensity", shaderIntensity);
		
		sb.setShader(shader);
			sb.begin();
				sb.draw(FrameBufferStack.getTexture(),
						0, 0,
						1920,
						1080,
						0, 0,
						1920,
						1080,
						false, true);
			sb.end();	
		sb.setShader(null);
		shader.end();
		
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		sr.setProjectionMatrix(Util.getNormalProjection());
		sr.begin(ShapeType.Filled);
		sr.setColor(0, 0, 0, alpha);
		sr.rect(0, 0, 1920, 1080);
		sr.end();
		
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}
	
	public abstract void insideRender(SpriteBatch sb);

	@Override
	public void update(float delta) {
		if(delta > 1) delta = 0;
		timer -= delta;
		
		globalTimer += delta;
		
		bolinha.update(delta);
		fogo.update(delta);
		fumaca_cano.update(delta);
		
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
				changeScreen();
			}
		}
		
		shaderIntensity += (intensityTarget - shaderIntensity) / 10.0f;
		
		if(timer < 0){
			timer = (float)Math.random() * 0.5f;
			intensityTarget = (float)(Math.random() * 0.3f) - 0.15f;
		}
	}
	
	public abstract void changeScreen();
	
}
