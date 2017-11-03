package com.mygdx.game.states;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
	private Texture[] texWep; //texture for each weapon
	
	int[] selection = new int[4];
	
	FrameBuffer playerBuffer[] = new FrameBuffer[4];
	FrameBuffer weaponBuffer[] = new FrameBuffer[4];
	float skinOffset[] = new float[4];
	float weaponOffset[] = new float[4];
		
	Body body_frames[] = new Body[4];
	Body body_subframes[] = new Body[4];
	
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
	
	OrthographicCamera camera;
	
	ArrayList<Body> chainBody;
	
	public PlayerSelectState(Manager manager) {
		super(manager);
	}

	@Override
	public void create() {
		outro = false;
		intro = true;
		alpha = 1;
		timer = 0;
		shaderIntensity = 0;
		intensityTarget = 0;
		globalTimer = 0;
		sr = new ShapeRenderer();
		
		selection_tex = new Texture("menu/player_select/selection.png");
		
		chainBody = new ArrayList<Body>();
		chain = new Texture("menu/player_select/chain.png");
		
		//texturas das armas
				if(texWep == null)
				texWep = new Texture[KambojaMain.getWeaponSize()];
				texWep[0] = new Texture("Weapons/Icon/Pistol.png");
				texWep[1] = new Texture("Weapons/Icon/PistolAkimbo.png");
				texWep[2] = new Texture("Weapons/Icon/minigun.png");
				texWep[3] = new Texture("Weapons/Icon/shotgun.png");
				texWep[4] = new Texture("Weapons/Icon/Mp5.png");
				texWep[5] = new Texture("Weapons/Icon/Flamethrower.png");
				texWep[6] = new Texture("Weapons/Icon/Bazook.png");
				texWep[7] = new Texture("Weapons/Icon/Laser.png");
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.zoom = 1/100f;
		camera.position.set(Gdx.graphics.getWidth()/2f / 100f, Gdx.graphics.getHeight() / 2f / 100f, 0);

		shader = new ShaderProgram(Gdx.files.internal("shaders/default.vs"),
				Gdx.files.internal("shaders/color_shift.fs"));
		ShaderProgram.pedantic = false;
		if(shader.getLog().length() > 0){
			System.out.println(shader.getLog());
		}
		shaderBuffer = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		
		background = new Texture("menu/player_select/fundo.jpg");
		
		factor = Gdx.graphics.getHeight() / 1080f;
		
		for(int i = 0; i < 4; i ++) {
			player_frames[i] = new Texture("menu/player_select/frame"+(i+1)+".png");
			player_glass[i] = new Texture("menu/player_select/glass"+(i+1)+".png");
			player_subframes[i] = new Texture("menu/player_select/caixa p"+(i+1)+".png");
			player_subglass[i] = new Texture("menu/player_select/subglass"+(i+1)+".png");
			playerBuffer[i] = new FrameBuffer(Format.RGBA8888, 181, 280, false);
			weaponBuffer[i] = new FrameBuffer(Format.RGBA8888, 181, 151, false);
			selection[i] = 3;
		}
		
		bufferProjectionPlayer = new Matrix4();
		bufferProjectionPlayer.setToOrtho2D(0, 0, 181, 280);
		bufferProjectionWeapon = new Matrix4();
		bufferProjectionWeapon.setToOrtho2D(0, 0, 181, 151);
		
		back_tex = new Texture("menu/player_select/back_btn.png");
		
		world = new World(new Vector2(0, -9.81f), false);
		b2dr = new Box2DDebugRenderer();
		
		
		float targetwidth = (452*3 + player_frames[0].getWidth()) * factor;
		float targetoffset = (Gdx.graphics.getWidth() - targetwidth)/2f;
		
	
		
		for(int i = 0; i < 4; i ++) {
			
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
					BodyType.StaticBody, 0f
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
					BodyType.DynamicBody, 0.03f
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
					Gdx.graphics.getWidth()/4f * i,
					Gdx.graphics.getHeight()/5f * 0,
					Gdx.graphics.getWidth()/4f,
					Gdx.graphics.getHeight()/5f
					);
			
			selection_bound_tween[i] = new Rectangle2D.Double(0, 0, 0, 0);
		}
		
		
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
						new Vector2(2.5f, 10), i == 0 ? BodyType.StaticBody : BodyType.DynamicBody, 1f);
				
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
		
		//controles ativos
				if(KambojaMain.getControllers() == null){
					KambojaMain.initializeControllers();
				}
		
	}

	@Override
	public void dispose() {
		sr.dispose();
	}
	
	public void setSpriteBatchColor(SpriteBatch sb, int id) {
		switch(id) {
		case 0:
			sb.setColor(0, 0, 1, 1);
			break;
		case 1:
			sb.setColor(1, 0, 0, 1);
			break;
		case 2:
			sb.setColor(0, 1, 0, 1);
			break;
		case 3:
			sb.setColor(1, 1, 0, 1);
			break;
		}
	}

	@Override
	public void render(SpriteBatch sb) {	
		
		float factor = Gdx.graphics.getHeight() / 1080f;
		
		for(int i = 0; i < 4; i ++) {
			
			if(KambojaMain.getControllers().size()-1 >= i){
				skinOffset[i] += (KambojaMain.getControllers().get(i).getPlayer() - skinOffset[i])/10.0f;
				weaponOffset[i] += (KambojaMain.getControllers().get(i).getWeapon() - weaponOffset[i])/10f;
				
				playerBuffer[i].begin();
				Gdx.gl.glClearColor(0, 0, 0, 0f);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
					for(int j = 0; j < KambojaMain.getPlayerSkinsSize(); j ++) {
						TextureRegion tex = Player.getTexture(j, 0);
							
						sb.begin();
						sb.setProjectionMatrix(bufferProjectionPlayer);
						sb.draw(tex,
								(181 - tex.getRegionWidth()*5) / 2f + j*181 - skinOffset[i]*181,
								(280 - tex.getRegionHeight()*5) / 2f,
								tex.getRegionWidth()*5 / 2f,
								tex.getRegionHeight()*5 / 2f,
								tex.getRegionWidth()*5,
								tex.getRegionHeight()*5,
								1,
								1,
								globalTimer*50);
							
						sb.end();
					}
				playerBuffer[i].end();
				
				weaponBuffer[i].begin();
				Gdx.gl.glClearColor(0, 0, 0, 0f);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
					for(int j = 0; j < KambojaMain.getWeaponSize(); j ++) {
						Texture tex = texWep[j];
							
						sb.begin();
						sb.setProjectionMatrix(bufferProjectionWeapon);
						
						float ratio = 181 / (tex.getWidth()*5f);
						
						sb.draw(tex,
								(181 - tex.getWidth()*5) / 2f + j*181 - weaponOffset[i]*181,
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
					weaponBuffer[i].end();
			}
		}
		
		sb.setProjectionMatrix(Util.getNormalProjection());
		shaderBuffer.begin();
		sb.begin();
		//DESENHA MENU
		
			sb.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

			for(int i = 0; i < 4; i ++) {
				sb.draw(
						player_frames[i],
						body_frames[i].getWorldCenter().x*100f - player_frames[0].getWidth()*factor/2f,
						body_frames[i].getWorldCenter().y*100f - player_frames[0].getHeight()*factor/2f,
						player_frames[i].getWidth() * factor,
						player_frames[i].getHeight() * factor);
				
				if(KambojaMain.getControllers().size()-1 >= i){
					//desenha a arma e o player selecionado
					Texture pt = playerBuffer[i].getColorBufferTexture();
					sb.draw(pt,
							body_frames[i].getWorldCenter().x*100f - pt.getWidth()*factor/2f,
							body_frames[i].getWorldCenter().y*100f - pt.getHeight()*factor/2f + 30*factor,
							pt.getWidth()*factor, pt.getHeight()*factor);
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
					//desenha a arma e o player selecionado
					Texture pt = weaponBuffer[i].getColorBufferTexture();
					
					
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
							false);
					

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
				
				setSpriteBatchColor(sb, i);
				
				
				if(KambojaMain.getControllers().size()-1 >= i){
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
					back_tex.getWidth()*factor,
					back_tex.getHeight() * factor);

		
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
		
		
		for(int i = 0; i < 4; i ++) {
			if(KambojaMain.getControllers().size()-1 >= 0){
				
				selection_bound_tween[i].setRect(
						selection_bound_tween[i].getX() + (selection_bounds[i][selection[i]].getX() - selection_bound_tween[i].getX())/10f,
						selection_bound_tween[i].getY() + (selection_bounds[i][selection[i]].getY() - selection_bound_tween[i].getY())/10f,
						selection_bound_tween[i].getWidth() + (selection_bounds[i][selection[i]].getWidth() - selection_bound_tween[i].getWidth())/10f,
						selection_bound_tween[i].getHeight() + (selection_bounds[i][selection[i]].getHeight() - selection_bound_tween[i].getHeight())/10f
						);
				
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
			if(pc.getPlayer() == player){
				return false;
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
		
		if(id != -1) {
		body_subframes[id].applyLinearImpulse(
				new Vector2((float)(Math.random() * 0.6 - 0.3), (float)(Math.random() * 0.6 - 0.3)),
				body_subframes[id].getWorldCenter(), true);
		}
		
		int select = 0;
		int start = 0;
		if(controller.getName().equals(Gamecube.getID())){
			select = Gamecube.A;
			start = Gamecube.START;

		}
		else if(controller.getName().toUpperCase().contains("XBOX") && controller.getName().contains("360")){
			select = XBox.BUTTON_A;
			start = XBox.BUTTON_START;
		}
		else{
			select = GenericController.X;
			start = GenericController.START;
		}
		
		if(buttonCode == start){
			if(Util.getControllerID(controller) == -1){
				if(KambojaMain.getControllers().size() < 4){
					PlayerController pc = new PlayerController(0, controller, firstPlayerAvailable(), "");
					KambojaMain.getControllers().add(pc);
				}
			}
		}
		
		return false;
	}



	@Override
	public boolean buttonUp(Controller controller, int buttonCode) {
		return false;
	}

	boolean xMoved = false;
	boolean yMoved = false;
	
	public void changePlayer(float value, int id) {
		if(value > 0) {
			int player = KambojaMain.getControllers().get(id).getPlayer();
			player = nextPlayer(player);
			KambojaMain.getControllers().get(id).setPlayer(player);
		}
		else {
			int player = KambojaMain.getControllers().get(id).getPlayer();
			player = previousPlayer(player);
			KambojaMain.getControllers().get(id).setPlayer(player);
		}
	}
	
	public void changeSelection(float value, int id) {
		System.out.println("Changed selection");
		if(value < 0) {
			
			if(selection[id] < 4)
			selection[id] ++;
			
		}
		else {
			if(selection[id] > 0)
			selection[id] --;
			
		}
	}
	
	public void changeWeapon(float value, int id) {
		
		if(value > 0)
			KambojaMain.getControllers().get(id).nextWeapon();
		else
			KambojaMain.getControllers().get(id).previousWeapon();	
	}
	
	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {
		int id = Util.getControllerID(controller);
		
		
		if(id != -1){
			if(controller.getName().equals(Gamecube.getID())){
				if(axisCode == Gamecube.MAIN_X) {
					if(Math.abs(value) > 0.5f) {
						if(!xMoved) {
							xMoved = true;
							
							if(selection[id] == 3)
							changePlayer(value, id);
							
							if(selection[id] == 1)
							changeWeapon(value, id);
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

							changeSelection(value, id);
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
							
							if(selection[id] == 3)
							changePlayer(value, id);
							
							if(selection[id] == 1)
							changeWeapon(value, id);
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

							changeSelection(value, id);
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
							
							if(selection[id] == 3)
							changePlayer(value, id);
							
							if(selection[id] == 1)
							changeWeapon(value, id);
							
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

							changeSelection(value, id);
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
