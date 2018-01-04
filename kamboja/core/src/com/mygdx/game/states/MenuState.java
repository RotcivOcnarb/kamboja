package com.mygdx.game.states;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
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
import com.mygdx.game.KambojaMain;
import com.mygdx.game.Manager;
import com.mygdx.game.State;
import com.mygdx.game.controllers.Gamecube;
import com.mygdx.game.controllers.GenericController;
import com.mygdx.game.controllers.XBox;
import com.mygdx.game.easing.Back;
import com.mygdx.game.objects.GameMusic;

public class MenuState extends State{
	
	Texture background;
	Texture bolinha;
	Texture explosao;
	Texture armas;
	Texture fumaca;
	Texture placa_letras;
	Texture sombra_letras;
	Texture fumaca_tras;
	Texture fumaca_frente;
	
	ShapeRenderer sr;
	
	Texture engrenagens[] = new Texture[6];
	Vector2 eng_pos[] = new Vector2[6];
	float eng_angle[] = new float[6];
	float eng_size[] = new float[6];
	Texture options[] = new Texture[6];
	

	boolean outro;
	boolean intro;
	float outro_vel;
	
	float timerWrong;
	
	int opt;
	
	Texture[] exps = new Texture[5];
	
	float timeStamp;
		
	float globalTimer;
	float timer;
	float alpha;
	float shaderIntensity;
	float intensityTarget;
	
	float SUM_ANGLE = 0;
	float LAST_ANGLE = 0;	
	float ENG_ANGLE = 0;	
	
	float x_value;
	
	ArrayList<ParticleEffect> explosions;
	ArrayList<ParticleEffect> bolinhas;
	
	ShaderProgram shader;
	FrameBuffer shaderBuffer;
		
	public MenuState(Manager manager) {
		super(manager);
		
		background = KambojaMain.getTexture("menu/main_menu/background.png");
		bolinha = KambojaMain.getTexture("menu/main_menu/bolinha.png");
		explosao = KambojaMain.getTexture("menu/main_menu/explosao.png");
		armas = KambojaMain.getTexture("menu/main_menu/armas.png");
		fumaca = KambojaMain.getTexture("menu/main_menu/fumaca.png");
		placa_letras = KambojaMain.getTexture("menu/main_menu/placa_letras.png");
		sombra_letras = KambojaMain.getTexture("menu/main_menu/sombra_letras.png");
		fumaca_tras = KambojaMain.getTexture("menu/main_menu/fumaca_back.png");
		fumaca_frente = KambojaMain.getTexture("menu/main_menu/fumaca_front.png");
		
		eng_pos[0] = new Vector2(-981, 1009);
		eng_pos[1] = new Vector2(-677, 969);
		eng_pos[2] = new Vector2(-424, 1158);
		eng_pos[3] = new Vector2(0, 1083);
		eng_pos[4] = new Vector2(443, 1083);
		eng_pos[5] = new Vector2(867, 1003);
		
		eng_size[0] = 169;
		eng_size[1] = 97;
		eng_size[2] = 171;
		eng_size[3] = 216;
		eng_size[4] = 171.5f;
		eng_size[5] = 217.5f;
		
		options[0] = KambojaMain.getTexture("menu/main_menu/VERSUS.png");
		options[1] = KambojaMain.getTexture("menu/main_menu/COOP_off.png");
		options[2] = KambojaMain.getTexture("menu/main_menu/ONLINE_off.png");
		options[3] = KambojaMain.getTexture("menu/main_menu/HELP.png");
		options[4] = KambojaMain.getTexture("menu/main_menu/OPTIONS.png");
		options[5] = KambojaMain.getTexture("menu/main_menu/CREDITS.png");
		
		for(int i = 0; i < 6; i ++){
			engrenagens[i] = KambojaMain.getTexture("menu/main_menu/E" + (i+1) + ".png");
		}
		
		explosions = new ArrayList<MenuState.ParticleEffect>();
		
		for(int i = 0; i < 5; i ++){
			exps[i] = KambojaMain.getTexture("menu/main_menu/exp" + (i+1) + ".png");
		}
		
		sr = new ShapeRenderer();
		
		bolinhas = new ArrayList<MenuState.ParticleEffect>();
		
		shader = new ShaderProgram(Gdx.files.internal("shaders/default.vs"),
				Gdx.files.internal("shaders/color_shift.fs"));
		
		ShaderProgram.pedantic = false;
		
		if(shader.getLog().length() > 0){
			System.out.println(shader.getLog());
		}
		
		GameMusic.startMenu();
		
	}

	public void create() {
		
		globalTimer = 0;
		outro = false;
		intro = true;
		outro_vel = 0;
		timerWrong = 0;
		opt = 0;
		timeStamp = 0;
		timer = 0;
		alpha = 1;
		shaderIntensity = 0;
		intensityTarget = 0;
		
		SUM_ANGLE = 0;
		LAST_ANGLE = 0;	
		ENG_ANGLE = 0;	
		
		x_value = 0;
		
		eng_angle[0] = 0;
		eng_angle[1] = 0;
		eng_angle[2] = 0;
		eng_angle[3] = 0;
		eng_angle[4] = 0;
		eng_angle[5] = 0;
		
		//GameMusic.loadMusic(GameMusic.MAIN_MENU);
		//GameMusic.loop(GameMusic.MAIN_MENU, 0);

		shaderBuffer = new FrameBuffer(Format.RGBA8888, 1920, 1080, false);
		
	}

	public void dispose() {
		
	}

	public void render(SpriteBatch sb) {
		//GameMusic.fadeIn(GameMusic.MAIN_MENU);
		
		
		
		//desenha menu no frameBuffer
		shaderBuffer.begin();

		sb.begin();
		
		sb.draw(background, 0, 0, 1920, 1080);
		
		for(int i = 0; i < explosions.size(); i ++){
			ParticleEffect pe = explosions.get(i);
			pe.render(sb);
		}
		
		for(int i = 0; i < bolinhas.size(); i ++){
			ParticleEffect pe = bolinhas.get(i);
			pe.render(sb);
		}
				
		sb.draw(sombra_letras,
				(1920 - sombra_letras.getWidth() )/2f, 0,
				sombra_letras.getWidth()/2,
				sombra_letras.getHeight() * (5f/8f),
				sombra_letras.getWidth(),
				sombra_letras.getHeight(),
				1, 1,
				(float)Math.sin(globalTimer)*2f,
				0, 0,
				sombra_letras.getWidth(),
				sombra_letras.getHeight(),
				false, false);	
		
		sb.draw(armas,
				(1920 - armas.getWidth() )/2f, 0,
				armas.getWidth()/2,
				armas.getHeight() * (5f/8f),
				armas.getWidth(),
				armas.getHeight(),
				1, 1,
				(float)Math.sin(globalTimer + 0.5f)*2f,
				0, 0,
				armas.getWidth(),
				armas.getHeight(),
				false, false);	
		
		sb.draw(placa_letras,
				(1920 - placa_letras.getWidth() )/2f, 0,
				placa_letras.getWidth()/2,
				placa_letras.getHeight() * (5f/8f),
				placa_letras.getWidth(),
				placa_letras.getHeight(),
				1, 1,
				(float)Math.sin(globalTimer)*2f,
				0, 0,
				placa_letras.getWidth(),
				placa_letras.getHeight(),
				false, false);	
		
		sb.draw(fumaca,
				(1920 - fumaca.getWidth() )/2f, 0,
				fumaca.getWidth()/2,
				fumaca.getHeight() * (5f/8f),
				fumaca.getWidth(),
				fumaca.getHeight(),
				1, 1,
				(float)Math.sin(globalTimer)*2f,
				0, 0,
				fumaca.getWidth(),
				fumaca.getHeight(),
				false, false);	
		
		sb.end();
		
		sb.begin();
		
		sb.draw(fumaca_tras,
				(1920 - fumaca_tras.getWidth() )/2f, -engrenagens[3].getHeight()/2,
				fumaca_tras.getWidth()/2,
				fumaca_tras.getHeight()/2,
				fumaca_tras.getWidth(),
				fumaca_tras.getHeight(),
				1, 1,
				0,
				0, 0,
				fumaca_tras.getWidth(),
				fumaca_tras.getHeight(),
				false, false);	
		
		
		
		for(int i = 0; i < 6; i ++){
			float fx = 0;
			if(i == 5) fx = 8;
			sb.draw(engrenagens[i],
					eng_pos[i].x - engrenagens[i].getWidth()/2 + 1920/2,
					1080 - eng_pos[i].y - engrenagens[i].getHeight()/2,
					engrenagens[i].getWidth()/2,
					engrenagens[i].getHeight()/2,
					engrenagens[i].getWidth(),
					engrenagens[i].getHeight(),
					1, 1,
					fx + eng_angle[i],
					0, 0,
					engrenagens[i].getWidth(),
					engrenagens[i].getHeight(),
					false, false);	
		}
		
		Texture t1 = options[opt];
		Texture t2 = options[(opt+2) % 6];
		
		if(opt % 2 == 0){
			t1 = options[opt]; 
			
			if(SUM_ANGLE < 0){
				t2 = options[(opt+1) % 6];
			}
			else{
				t2 = options[(opt-1 + 6) % 6];
			}
			
			
		}
		else{
			t2 = options[opt]; 
			if(SUM_ANGLE < 0){
				t1 = options[(opt+1) % 6];
			}
			else{
				t1 = options[(opt-1 + 6) % 6];
			}
		}
		
			sb.draw(t1,
				eng_pos[3].x - options[opt].getWidth()/2 + 1920/2,
				1080 - eng_pos[3].y - options[opt].getHeight()/2,
				options[opt].getWidth()/2,
				options[opt].getHeight()/2,
				options[opt].getWidth(),
				options[opt].getHeight(),
				1, 1,
				eng_angle[3],
				0, 0,
				options[opt].getWidth(),
				options[opt].getHeight(),
				false, false);
			
			sb.draw(t2,
					eng_pos[3].x - options[opt].getWidth()/2 + 1920/2,
					1080 - eng_pos[3].y - options[opt].getHeight()/2,
					options[opt].getWidth()/2,
					options[opt].getHeight()/2,
					options[opt].getWidth(),
					options[opt].getHeight(),
					1, 1,
					eng_angle[3] + 180,
					0, 0,
					options[opt].getWidth(),
					options[opt].getHeight(),
					false, false);
		
			sb.draw(fumaca_frente,
					(1920 - fumaca_frente.getWidth() )/2f, -engrenagens[3].getHeight()/2,
					fumaca_frente.getWidth()/2,
					fumaca_frente.getHeight()/2,
					fumaca_frente.getWidth(),
					fumaca_frente.getHeight(),
					1, 1,
					0,
					0, 0,
					fumaca_frente.getWidth(),
					fumaca_frente.getHeight(),
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
						1920,
						1080,
						0, 0,
						1920,
						1080,
						false, true);
			sb.end();	
		sb.setShader(null);
		shader.end();
		
		//desenha blackscreen
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		sr.begin(ShapeType.Filled);
		sr.setColor(0, 0, 0, alpha);
		sr.rect(0, 0, 1920, 1080);
		sr.end();
		
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

	public void update(float delta) {
		if(delta > 0.5) delta = 0;
		globalTimer += delta;
		timer -= delta;
		timerWrong -= delta;
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
				

				switch(opt){
				case 0:
					//Player select
					manager.changeState(Manager.PLAYER_SELECT_STATE);
					break;
				case 3:
					//Help
					manager.changeState(Manager.HELP_STATE);
					break;
				case 4:
					//Options
					manager.changeState(Manager.OPTIONS_STATE);
					break;
				case 5:
					//Credits
					manager.changeState(Manager.CREDITS_STATE);
					break;
				}
			}
			outro_vel += delta*20;
			eng_angle[3] += outro_vel;
			
		}
		
		checkMove();
		
		if(timerWrong > 0){
			if(timerWrong < 0.25f){
				eng_angle[3] += (ENG_ANGLE - eng_angle[3])/3.0f;
			}
			else{
				eng_angle[3] += (ENG_ANGLE + 10 - eng_angle[3])/3.0f;
			}
		}
		
		if(!outro && timerWrong <= 0)
		eng_angle[3] = Back.easeOut(Math.min(globalTimer - timeStamp, 0.5f), LAST_ANGLE, SUM_ANGLE, 0.5f);
		
		eng_angle[5] = -eng_angle[4] * (eng_size[4]/eng_size[5]);		
		eng_angle[4] = -eng_angle[3] * (eng_size[3]/eng_size[4]);		
		
		eng_angle[2] = -eng_angle[3] * (eng_size[3]/eng_size[2]);
		eng_angle[1] = -eng_angle[2] * (eng_size[2]/eng_size[1]);		
		eng_angle[0] = -eng_angle[1] * (eng_size[1]/eng_size[0]);		

		
		
		shaderIntensity += (intensityTarget - shaderIntensity) / 10.0f;
		
		if(timer < 0){
			timer = (float)Math.random() * 0.5f;
			intensityTarget = (float)(Math.random() * 0.3f) - 0.15f;
		}
		

			ParticleEffect bolinhaP = 
					new ParticleEffect(
							bolinha, new Vector2(1920/2, 1080 * (5f/8f)),
						new Vector2(0, 0), new Vector2((float)((Math.random() - 0.5) * 2) * 0.07f, (float)((Math.random() - 0.5) * 2) * 0.07f));

			bolinhaP.setScale(0);
			bolinhas.add(bolinhaP);

			
			
				ParticleEffect pep = new ParticleEffect(exps[(int)(Math.random()*5)],
						new Vector2(1920/2, 1080 * (5f/8f)),
						new Vector2(0, 0),
						new Vector2((float)((Math.random() - 0.5) * 2) * 0.07f, (float)((Math.random() - 0.5) * 2) * 0.07f));
				pep.setAlpha(0);
				pep.setScale(0.7f);
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
			pe.setScale(pe.getCounter() * pe.getRandom());
			pe.update(delta);
			
			if(pe.position.x < 0 - pe.tex.getWidth()/2 * pe.getScale()){
				bolinhas.remove(pe);
			}
			if(pe.position.x > 1920 + pe.tex.getWidth()/2 * pe.getScale()){
				bolinhas.remove(pe);
			}
			
			if(pe.position.y < 0 - pe.tex.getHeight()/2 * pe.getScale()){
				bolinhas.remove(pe);
			}
			if(pe.position.y> 1080 + pe.tex.getHeight()/2 * pe.getScale()){
				bolinhas.remove(pe);
			}
		}
	}
	
	boolean left;
	boolean right;
	
	public void checkMove(){
		if(x_value > 0.2){
			if(globalTimer - timeStamp > 0.5f){
				opt++;
				if(opt == 6) opt = 0;
				LAST_ANGLE = ENG_ANGLE;
				ENG_ANGLE += 180;
				SUM_ANGLE = 180;
				timeStamp = globalTimer;
			}
			
		}
		else if(x_value < -0.2){
			if(globalTimer - timeStamp > 0.5f){
				opt--;
				if(opt == -1) opt = 5;
				LAST_ANGLE = ENG_ANGLE;
				ENG_ANGLE -= 180;
				SUM_ANGLE = -180;
				timeStamp = globalTimer;
			}
			
		}
	}
	
	@Override
	public boolean keyDown(int keycode){
		
		if(keycode == Keys.RIGHT){
			if(!right){
				right = true;
				if(globalTimer - timeStamp > 0.5f){
					opt++;
					if(opt == 6) opt = 0;
					LAST_ANGLE = ENG_ANGLE;
					ENG_ANGLE += 180;
					SUM_ANGLE = 180;
					timeStamp = globalTimer;
				}
			}
		}
		if(keycode == Keys.LEFT){
			if(!left){
				left = true;
				if(globalTimer - timeStamp > 0.5f){
					opt--;
					if(opt == -1) opt = 5;
					LAST_ANGLE = ENG_ANGLE;
					ENG_ANGLE -= 180;
					SUM_ANGLE = -180;
					timeStamp = globalTimer;
				}
			}
		}
		
		if(keycode == Keys.ENTER){
			selectOption();
		}
		
		return false;
	}
	
	@Override
	public boolean keyUp(int keycode){
		if(keycode == Keys.LEFT){
			left = false;
		}
		if(keycode == Keys.RIGHT){
			right = false;
		}
		return false;
	}
	
	public void selectOption() {
		switch(opt){
		case 0:
			outro = true;
			intro = false;
			break;
		case 1:
			timerWrong = 0.5f;
			break;
		case 2:
			timerWrong = 0.5f;
			break;
		case 3:
			outro = true;
			intro = false;
			break;
		case 4:
			outro = true;
			intro = false;
			break;
		case 5:
			outro = true;
			intro = false;
			break;
		}
	}

	public void connected(Controller controller) {
		
	}

	public void disconnected(Controller controller) {
		
	}

	public boolean buttonDown(Controller controller, int buttonCode) {
		int A = 0;
		
		if(controller.getName().toUpperCase().contains("XBOX") && controller.getName().contains("360")){
			A = XBox.BUTTON_A;
		}
		else if(controller.getName().equals(Gamecube.getID())){
			A = Gamecube.A;
		}
		else{
			A = GenericController.X;
		}
		
		if(buttonCode == A){
			selectOption();
		}
		
		
		return false;
	}

	public boolean buttonUp(Controller controller, int buttonCode) {
		return false;
	}
	

	public boolean axisMoved(Controller controller, int axisCode, float value) {
		if(controller.getName().toUpperCase().contains("XBOX") && controller.getName().contains("360")){
			if(axisCode == XBox.AXIS_LEFT_X){
				x_value = value;
			}
		}
		else if(controller.getName().equals(Gamecube.getID())){
			if(axisCode == Gamecube.MAIN_X){
				x_value = value;
			}
		}
		else{
			if(axisCode == GenericController.LEFT_X){
				x_value = value;
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
