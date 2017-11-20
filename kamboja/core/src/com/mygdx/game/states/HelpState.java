package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
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

	public HelpState(Manager manager) {
		super(manager);
	}
	
	public void create() {
		super.create();
		background = new Texture("menu/help/fundo.jpg");
		base = new Texture("menu/help/base.png");
		
		shoot = new Texture("menu/help/placa shoot.png");
		sprint = new Texture("menu/help/placa sprint.png");
		
		back_cog = new Texture("menu/help/engrenagem.png");
		
		move = new Texture("menu/help/placa move.png");
		aim = new Texture("menu/help/placa aim.png");
		
		shootBody = createBox(
				new Vector2(Gdx.graphics.getWidth()/2 -700*factor, Gdx.graphics.getHeight()),
				new Vector2(shoot.getWidth()*factor/2f, shoot.getHeight()*factor/2f),
				BodyType.DynamicBody, 0.1f);
		
		sprintBody = createBox(
				new Vector2(Gdx.graphics.getWidth()/2 + 700*factor, Gdx.graphics.getHeight()),
				new Vector2(sprint.getWidth()*factor/2f, sprint.getHeight()*factor/2f),
				BodyType.DynamicBody, 0.1f);
		
		moveBody = createBox(
				new Vector2(Gdx.graphics.getWidth()/2 -700*factor, Gdx.graphics.getHeight()),
				new Vector2(move.getWidth()*factor/2f, move.getHeight()*factor/2f),
				BodyType.DynamicBody, 0.1f);
		
		aimBody = createBox(
				new Vector2(Gdx.graphics.getWidth()/2 + 700*factor, Gdx.graphics.getHeight()),
				new Vector2(aim.getWidth()*factor/2f, aim.getHeight()*factor/2f),
				BodyType.DynamicBody, 0.1f);
		
		buildRopeJoint(8, shootBody, -700*factor, 100);
		buildRopeJoint(10, sprintBody, 700*factor, 100);
		buildRopeJoint(18, moveBody, -700*factor, 50);
		buildRopeJoint(22, aimBody, 700*factor, 50);
		
		cog_angle = 0;
		cog_speed = 0;
	}

	public void insideRender(SpriteBatch sb) {
		sb.begin();
		
			sb.draw(base, (Gdx.graphics.getWidth() - base.getWidth()*factor)/2f, 0, base.getWidth()*factor, base.getHeight()*factor);
			sb.draw(back_cog,
					Gdx.graphics.getWidth()/2f - back_cog.getWidth()/2f*factor,
					-back_cog.getHeight()*factor/2f,
					back_cog.getWidth()/2f*factor,
					back_cog.getHeight()/2f*factor,
					back_cog.getWidth()*factor,
					back_cog.getHeight()*factor,
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
		manager.changeState(5);
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
