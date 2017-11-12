package com.mygdx.game.objects.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.objects.Player;
import com.mygdx.game.objects.Util;
import com.mygdx.game.states.GameState;

public class SpaceMap extends KambojaMap{
	
	Texture background;
	Texture stars;
	
	public void create() {
		background = new Texture("map_assets/space_background.png");
		stars = new Texture("map_assets/stars.png");
		stars.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
	}


	public void behindRender(SpriteBatch sb, OrthographicCamera camera) {
		
		sb.setProjectionMatrix(Util.getNormalProjection());
		sb.begin();
		sb.setColor(1, 1, 1, 1);
		sb.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		for(int i = 1; i <= 5; i ++) {
			sb.draw(stars,
					0, 0,
					(int)Gdx.graphics.getWidth(),
					(int)Gdx.graphics.getHeight(), 
					(int)(camera.position.x * GameState.UNIT_SCALE / (i*2f)),
					(int)(-camera.position.y * GameState.UNIT_SCALE / (i*2f)),
					(int)stars.getWidth(),
					(int)stars.getHeight(),
					false, false);
		}
		sb.end();

		sb.setProjectionMatrix(camera.combined);
		
	}

	public void update(float delta) {
		for(Player p : gameState.getPlayers()) {
			p.getBody().setLinearDamping(0);
			p.setInSpace(true);
		}
		gameState.setInSpace();
	}


	public void render(SpriteBatch sb, OrthographicCamera camera) {
		
	}

}
