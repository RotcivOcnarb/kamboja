package com.mygdx.game;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public abstract class State{
	
	//instance of list so it can change between states
	public Manager manager;

	public State(Manager manager){
		this.manager = manager;
	}
	
	public abstract void create();
	public abstract void dispose();
	public abstract void render(SpriteBatch sb);
	public abstract void update(float delta);
	public abstract void connected(Controller controller);
	public abstract void disconnected(Controller controller);
	public abstract boolean buttonDown(Controller controller, int buttonCode);
	public abstract boolean buttonUp(Controller controller, int buttonCode);
	public abstract boolean axisMoved(Controller controller, int axisCode, float value);
	public abstract boolean povMoved(Controller controller, int povCode, PovDirection value);
	public abstract boolean xSliderMoved(Controller controller, int sliderCode, boolean value);
	public abstract boolean ySliderMoved(Controller controller, int sliderCode, boolean value);
	public abstract boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value);

	public abstract void resize(int width, int height);
	
	public boolean keyDown(int keycode) {
		return false;
	}

	public boolean keyUp(int keycode) {
		return false;
	}


}
