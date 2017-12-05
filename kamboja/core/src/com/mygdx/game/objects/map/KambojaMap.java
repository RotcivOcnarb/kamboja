package com.mygdx.game.objects.map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.states.GameState;

public abstract class KambojaMap {
	
	GameState gameState;
	
	public abstract void create();
	public abstract void behindRender(SpriteBatch sb, OrthographicCamera camera);
	public abstract void render(SpriteBatch sb, OrthographicCamera camera);
	public abstract void update(float delta);
	
	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

}
