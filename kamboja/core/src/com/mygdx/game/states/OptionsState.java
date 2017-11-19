package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Manager;
import com.mygdx.game.State;

public class OptionsState extends State{
	
	Texture background;
	ParticleEffect fogo;
	ParticleEffect bolinha;
	
	float factor;

	public OptionsState(Manager manager) {
		super(manager);
	}

	public void create() {
		
		background = new Texture("menu/map_select/fundo.jpg");
		
		factor = Gdx.graphics.getHeight() / 1080f;
		
		fogo = new ParticleEffect();
		fogo.load(Gdx.files.internal("particles/fogo.par"), Gdx.files.internal("particles"));
		fogo.setPosition(Gdx.graphics.getWidth()/2f, -32*factor);
		fogo.scaleEffect(10*factor);
		
		bolinha = new ParticleEffect();
		bolinha.load(Gdx.files.internal("particles/bolinha.par"), Gdx.files.internal("particles"));
		bolinha.setPosition(Gdx.graphics.getWidth()/2f, Gdx.graphics.getHeight()/2f);
		bolinha.scaleEffect(factor);
	}

	public void dispose() {
		
	}

	public void render(SpriteBatch sb) {
		
	}

	public void update(float delta) {
		
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
