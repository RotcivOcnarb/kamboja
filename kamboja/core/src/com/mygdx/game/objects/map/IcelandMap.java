package com.mygdx.game.objects.map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.objects.Player;

public class IcelandMap extends KambojaMap{

	public void create() {
		
	}

	public void behindRender(SpriteBatch sb, OrthographicCamera camera) {
		
	}

	public void render(SpriteBatch sb, OrthographicCamera camera) {
		
	}

	public void update(float delta) {
		for(Player c : gameState.getPlayers()) {
			c.getBody().setLinearDamping(0);
			c.setInSpace(true, false);
		}
	}

}
