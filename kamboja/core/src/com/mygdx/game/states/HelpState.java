package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.mygdx.game.KambojaMain;
import com.mygdx.game.Manager;
import com.mygdx.game.controllers.Gamecube;
import com.mygdx.game.controllers.GenericController;
import com.mygdx.game.controllers.XBox;

public class HelpState extends GenericInterface{
	
	Texture base;
	Texture shoot, sprint;
	Texture move, aim;
	Texture back_cog;
	
	Body shootBody;
	Body sprintBody;
	Body moveBody;
	Body aimBody;
	
	float cog_angle;
	float cog_speed;
	
	PooledEffect fumaca[] = new PooledEffect[2];

	public HelpState(Manager manager) {
		super(manager);
		
		background = KambojaMain.getTexture("menu/help/fundo.jpg");
		base = KambojaMain.getTexture("menu/help/base.png");
		
		shoot = KambojaMain.getTexture("menu/help/placa shoot.png");
		sprint = KambojaMain.getTexture("menu/help/placa sprint.png");
		
		back_cog = KambojaMain.getTexture("menu/help/engrenagem.png");
		
		move = KambojaMain.getTexture("menu/help/placa move.png");
		aim = KambojaMain.getTexture("menu/help/placa aim.png");
		
		for(int i = 0; i < 2; i ++) {
			fumaca[i] = cano_pool.obtain();
		}
		
		fumaca[1].getEmitters().get(0).getAngle().setHighMin(-50);
		fumaca[1].getEmitters().get(0).getAngle().setHighMax(-40);
		
		fumaca[1].getEmitters().get(0).getAngle().setLowMin(0);
		fumaca[1].getEmitters().get(0).getAngle().setLowMax(0);
	}
	
	//TODO: particula de fumaça
	
	public void create() {
		super.create();
		shootBody = createBox(
				new Vector2(1920/2 -700, 1080),
				new Vector2(shoot.getWidth()/2f, shoot.getHeight()/2f),
				BodyType.DynamicBody, 0.1f);
		
		sprintBody = createBox(
				new Vector2(1920/2 + 700, 1080),
				new Vector2(sprint.getWidth()/2f, sprint.getHeight()/2f),
				BodyType.DynamicBody, 0.1f);
		
		moveBody = createBox(
				new Vector2(1920/2 -700, 1080),
				new Vector2(move.getWidth()/2f, move.getHeight()/2f),
				BodyType.DynamicBody, 0.1f);
		
		aimBody = createBox(
				new Vector2(1920/2 + 700, 1080),
				new Vector2(aim.getWidth()/2f, aim.getHeight()/2f),
				BodyType.DynamicBody, 0.1f);
		
		buildRopeJoint(8, shootBody, -700, (413/2f - 100), 100);
		buildRopeJoint(10, sprintBody, 700, (413/2f - 100), 100);
		buildRopeJoint(18, moveBody, -700, (413/2f - 100), 50);
		buildRopeJoint(22, aimBody, 700, (413/2f - 100), 50);
		
		cog_angle = 0;
		cog_speed = 0;
	}

	public void insideRender(SpriteBatch sb) {
		sb.begin();
		
			sb.draw(base, (1920 - base.getWidth())/2f, 0, base.getWidth(), base.getHeight());
			sb.draw(back_cog,
					1920/2f - back_cog.getWidth()/2f,
					-back_cog.getHeight()/2f,
					back_cog.getWidth()/2f,
					back_cog.getHeight()/2f,
					back_cog.getWidth(),
					back_cog.getHeight(),
					1, 1,
					cog_angle,
					0, 0,
					back_cog.getWidth(),
					back_cog.getHeight(),
					false, false);
			
			drawChains(sb);
			
			renderImageInBody(sb, shoot, shootBody);
			renderImageInBody(sb, sprint, sprintBody);
			renderImageInBody(sb, move, moveBody);
			renderImageInBody(sb, aim, aimBody);
			
			 fumaca[0].setPosition(680, 160);
			 fumaca[0].draw(sb, Gdx.graphics.getDeltaTime());
			
			 fumaca[1].setPosition(1330, 90);
			 fumaca[1].draw(sb, Gdx.graphics.getDeltaTime());
			
			sb.flush();
			//b2dr.render(world, camera.combined);
		sb.end();
	}
	
	public void update(float delta) {
		super.update(delta);
		
		cog_angle += cog_speed;
		
		if(outro) {
			cog_speed += delta*10f;
		}
	}

	public void changeScreen() {
		manager.changeState(Manager.MENU_STATE);
	}

	public void dispose() {
		
	}

	public void connected(Controller controller) {
		
	}

	public void disconnected(Controller controller) {
		
	}

	public boolean buttonDown(Controller controller, int buttonCode) {
		int A = 0;
		
		if(controller.getName().toUpperCase().contains("XBOX") && controller.getName().contains("360")){
			A = XBox.BUTTON_START;
		}
		else if(controller.getName().equals(Gamecube.getID())){
			A = Gamecube.START;
		}
		else{
			A = GenericController.START;
		}
		
		if(buttonCode == A){
			intro = false;
			outro = true;
		}
		return false;
	}
	
	@Override
	public boolean keyDown(int keyCode) {
		if(keyCode == Keys.ENTER) {
			intro = false;
			outro = true;
		}
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
