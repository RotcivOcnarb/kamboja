package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.states.CreditsState;
import com.mygdx.game.states.GameState;
import com.mygdx.game.states.HelpState;
import com.mygdx.game.states.MainMenu;
import com.mygdx.game.states.MapSelect;
import com.mygdx.game.states.MapSelectState;
import com.mygdx.game.states.MenuState;
import com.mygdx.game.states.Options;
import com.mygdx.game.states.OptionsState;
import com.mygdx.game.states.PlayerSelectState;
import com.mygdx.game.states.PostGame;

public class Manager implements ControllerListener, InputProcessor{
	
	private ArrayList<State> states;
	private int currentState = 10;
	private boolean disposed = false; //so it doesnt dispose the same state twice
	
	public Manager(){
		states = new ArrayList<State>();
		states.add(new MainMenu(this));
		states.add(new MapSelect(this));
		states.add(new GameState(this));
		states.add(new PostGame(this));
		states.add(new Options(this));
		states.add(new MenuState(this));
		states.add(new PlayerSelectState(this));
		states.add(new MapSelectState(this));
		states.add(new OptionsState(this));
		states.add(new HelpState(this));
		states.add(new CreditsState(this));
		
		Controllers.addListener(this);
		Gdx.input.setInputProcessor(this);
	}

	public void reAddControllers(){
		Controllers.clearListeners();
		Controllers.addListener(this);
	}
	
	public void create(){
		states.get(currentState).create();
	}
	
	public void render(SpriteBatch sb){
		if(!disposed){
		states.get(currentState).render(sb);
		}
		else{
			disposed = false;
		}
	}
	
	public void update(float delta){
		if(!disposed)
		states.get(currentState).update(delta);
	}

	public void connected(Controller controller) {
		states.get(currentState).connected(controller);
	}

	public void disconnected(Controller controller) {
		states.get(currentState).disconnected(controller);
	}

	public boolean buttonDown(Controller controller, int buttonCode) {
		return states.get(currentState).buttonDown(controller, buttonCode);
	}

	public boolean buttonUp(Controller controller, int buttonCode) {
		return states.get(currentState).buttonUp(controller, buttonCode);
	}

	public boolean axisMoved(Controller controller, int axisCode, float value) {
		return states.get(currentState).axisMoved(controller, axisCode, value);
	}

	public boolean povMoved(Controller controller, int povCode, PovDirection value) {
		return states.get(currentState).povMoved(controller, povCode, value);
	}

	public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
		return states.get(currentState).xSliderMoved(controller, sliderCode, value);
	}

	public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
		return states.get(currentState).ySliderMoved(controller, sliderCode, value);
	}

	public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
		return states.get(currentState).accelerometerMoved(controller, accelerometerCode, value);
	}
	public void dispose(){
		states.get(currentState).dispose();
	}
	
	public void changeState(int state){
		dispose();
		currentState = state;
		create();
		disposed = true;
	}

	public void resize(int width, int height) {
		states.get(currentState).resize(width, height);
	}

	@Override
	public boolean keyDown(int keycode) {
		return states.get(currentState).keyDown(keycode);
	}

	@Override
	public boolean keyUp(int keycode) {
		return states.get(currentState).keyUp(keycode);
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {

		return false;
	}

}
