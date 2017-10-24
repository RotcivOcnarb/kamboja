package com.mygdx.game.states;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Manager;
import com.mygdx.game.State;
import com.mygdx.game.controllers.Gamecube;

public class MenuState extends State{
	
	Texture background;
	Texture bolinha;
	Texture explosao;
	Texture armas;
	Texture fumaca;
	Texture placa_letras;
	Texture sombra_letras;
	
	ShapeRenderer sr;
	
	Texture[] exps = new Texture[5];
		
	float globalTimer;
	float timer = 0;
	
	float alpha = 1;
	float shaderIntensity = 0;
	float intensityTarget = 0;
	
	ArrayList<ParticleEffect> explosions;
	ArrayList<ParticleEffect> bolinhas;
	
	ShaderProgram shader;
	FrameBuffer shaderBuffer;
	
	Music music;
	
	public MenuState(Manager manager) {
		super(manager);
	}

	public void create() {
		
		background = new Texture("menu/background.png");
		bolinha = new Texture("menu/bolinha.png");
		explosao = new Texture("menu/explos�o.png");
		armas = new Texture("menu/armas.png");
		fumaca = new Texture("menu/fumaca.png");
		placa_letras = new Texture("menu/placa_letras.png");
		sombra_letras = new Texture("menu/sombra_letras.png");
		
		music = Gdx.audio.newMusic(Gdx.files.internal("music/sylvan_spring.ogg"));
		music.setLooping(true);
		//music.play();
		
		explosions = new ArrayList<MenuState.ParticleEffect>();
		
		for(int i = 0; i < 5; i ++){
			exps[i] = new Texture("menu/exp" + (i+1) + ".png");
		}
		
		bolinhas = new ArrayList<MenuState.ParticleEffect>();
		
		sr = new ShapeRenderer();
		
		shader = new ShaderProgram(Gdx.files.internal("shaders/default.vs"),
				Gdx.files.internal("shaders/color_shift.fs"));
		
		ShaderProgram.pedantic = false;
		
		if(shader.getLog().length() > 0){
			System.out.println(shader.getLog());
		}
		
		shaderBuffer = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		
	}

	public void dispose() {
		
	}

	public void render(SpriteBatch sb) {
					
		
		//desenha menu no frameBuffer
		shaderBuffer.begin();

		sb.begin();
		
		sb.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		float factor = Gdx.graphics.getHeight() / 1080f;
		//sb.draw(explosao,(Gdx.graphics.getWidth() - explosao.getWidth()*factor )/2f, 0, explosao.getWidth() * factor, explosao.getHeight() * factor);
		
		for(int i = 0; i < explosions.size(); i ++){
			ParticleEffect pe = explosions.get(i);
			pe.render(sb);
		}
		
		for(int i = 0; i < bolinhas.size(); i ++){
			ParticleEffect pe = bolinhas.get(i);
			pe.render(sb);
		}
				
		sb.draw(sombra_letras,
				(Gdx.graphics.getWidth() - sombra_letras.getWidth()*factor )/2f, 0,
				sombra_letras.getWidth()*factor/2,
				sombra_letras.getHeight()*factor * (5f/8f),
				sombra_letras.getWidth() * factor,
				sombra_letras.getHeight() * factor,
				1, 1,
				(float)Math.sin(globalTimer)*2f,
				0, 0,
				sombra_letras.getWidth(),
				sombra_letras.getHeight(),
				false, false);	
		
		sb.draw(armas,
				(Gdx.graphics.getWidth() - armas.getWidth()*factor )/2f, 0,
				armas.getWidth()*factor/2,
				armas.getHeight()*factor * (5f/8f),
				armas.getWidth() * factor,
				armas.getHeight() * factor,
				1, 1,
				(float)Math.sin(globalTimer + 0.5f)*2f,
				0, 0,
				armas.getWidth(),
				armas.getHeight(),
				false, false);	
		
		sb.draw(placa_letras,
				(Gdx.graphics.getWidth() - placa_letras.getWidth()*factor )/2f, 0,
				placa_letras.getWidth()*factor/2,
				placa_letras.getHeight()*factor * (5f/8f),
				placa_letras.getWidth() * factor,
				placa_letras.getHeight() * factor,
				1, 1,
				(float)Math.sin(globalTimer)*2f,
				0, 0,
				placa_letras.getWidth(),
				placa_letras.getHeight(),
				false, false);	
		
		sb.draw(fumaca,
				(Gdx.graphics.getWidth() - fumaca.getWidth()*factor )/2f, 0,
				fumaca.getWidth()*factor/2,
				fumaca.getHeight()*factor * (5f/8f),
				fumaca.getWidth() * factor,
				fumaca.getHeight() * factor,
				1, 1,
				(float)Math.sin(globalTimer)*2f,
				0, 0,
				fumaca.getWidth(),
				fumaca.getHeight(),
				false, false);	
		
		sb.end();
		
		//desenha frame buffer com shader aplicado
		
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
		
		//desenha blackscreen
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		sr.begin(ShapeType.Filled);
		sr.setColor(0, 0, 0, alpha);
		sr.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		sr.end();
		
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

	public void update(float delta) {
		globalTimer += delta;
		timer -= delta;
		alpha -= delta;
		
		if(alpha <= 0) alpha = 0;
		
		shaderIntensity += (intensityTarget - shaderIntensity) / 10.0f;
		
		if(timer < 0){
			timer = (float)Math.random() * 0.5f;
			intensityTarget = (float)(Math.random() * 0.3f) - 0.15f;
		}
		
		float factor = Gdx.graphics.getHeight() / 1080f;
		
			
			ParticleEffect bolinhaP = 
					new ParticleEffect(
							bolinha, new Vector2(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight() * (5f/8f)),
						new Vector2(0, 0), new Vector2((float)((Math.random() - 0.5) * 2) * 0.07f * factor, (float)((Math.random() - 0.5) * 2) * 0.07f * factor));

			bolinhaP.setScale(0);
			bolinhas.add(bolinhaP);

			
			
				ParticleEffect pep = new ParticleEffect(exps[(int)(Math.random()*5)],
						new Vector2(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight() * (5f/8f)),
						new Vector2(0, 0),
						new Vector2((float)((Math.random() - 0.5) * 2) * 0.07f * factor, (float)((Math.random() - 0.5) * 2) * 0.07f * factor));
				pep.setAlpha(0);
				pep.setScale(0.7f * factor);
				explosions.add(pep);

		
		for(int i = explosions.size() - 1; i >= 0; i --){
			ParticleEffect pe = explosions.get(i);
			pe.setAlpha((float)Math.sin(pe.getCounter()*1.5f));
			pe.update(delta);
			
			if(pe.getCounter()*1.5f > Math.PI){
				explosions.remove(pe);
			}
		}
		
		for(int i = bolinhas.size() - 1; i >= 0; i --){
			ParticleEffect pe = bolinhas.get(i);
			pe.setScale(pe.getCounter() * pe.getRandom() * factor);
			pe.update(delta);
			
			if(pe.position.x < 0 - pe.tex.getWidth()/2 * pe.getScale()){
				bolinhas.remove(pe);
			}
			if(pe.position.x > Gdx.graphics.getWidth() + pe.tex.getWidth()/2 * pe.getScale()){
				bolinhas.remove(pe);
			}
			
			if(pe.position.y < 0 - pe.tex.getHeight()/2 * pe.getScale()){
				bolinhas.remove(pe);
			}
			if(pe.position.y> Gdx.graphics.getHeight() + pe.tex.getHeight()/2 * pe.getScale()){
				bolinhas.remove(pe);
			}
		}
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
	
	public class ParticleEffect{
		
		Texture tex;
		Vector2 position, velocity, gravity;
		float scale = 1;
		float angle = 0;
		float alpha = 1;
		float counter = 0;
		float random;
		
		public ParticleEffect(Texture tex, Vector2 position, Vector2 velocity, Vector2 gravity){
			this.tex = tex;
			this.position = position;
			this.velocity = velocity;
			this.gravity = gravity;
			
			random = (float)Math.random();
		}
		
		public float getRandom(){
			return random;
		}
		
		public void setAlpha(float alpha){
			this.alpha = alpha;
		}
		
		public float getAlpha(){
			return alpha;
		}
		
		public void setAngle(float angle){
			this.angle = angle;
		}
		
		public float getAngle(){
			return angle;
		}
		
		public void setScale(float scale){
			this.scale = scale;
		}
		
		public float getScale(){
			return scale;
		}
		
		public float getCounter(){
			return counter;
		}
		
		public void render(SpriteBatch sb){
			
			sb.setColor(1, 1, 1, alpha);
				sb.draw(tex,
						position.x - tex.getWidth()*scale/2,
						position.y - tex.getHeight()*scale/2,
						tex.getWidth()*scale/2,
						tex.getHeight()*scale/2,
						tex.getWidth()*scale,
						tex.getHeight()*scale,
						1, 1,
						angle,
						0, 0,
						tex.getWidth(),
						tex.getHeight(),
						false, false);			
			sb.setColor(1, 1, 1, 1);
			
		}
		
		public void update(float delta){
			velocity.add(gravity);
			position.add(velocity);
			
			counter += delta;
		}
	
	}

}