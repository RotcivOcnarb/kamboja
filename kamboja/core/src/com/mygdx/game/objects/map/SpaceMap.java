package com.mygdx.game.objects.map;

import java.util.ArrayList;

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
	
	float asteroid_timer = 10;
	Texture asteroid_tex;
	
	ArrayList<Asteroid> asteroids;
	ArrayList<AsteroidData> toBeCreated;
	
	public void create() {
		background = new Texture("map_assets/space_background.png");
		stars = new Texture("map_assets/stars.png");
		stars.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		asteroids = new ArrayList<Asteroid>();
		toBeCreated = new ArrayList<SpaceMap.AsteroidData>();
		asteroid_tex = new Texture("map_assets/asteroid.png");
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
			p.setInSpace(true, true);
		}
		gameState.setInSpace();
		
		asteroid_timer -= delta;
		
		if(asteroid_timer <= 0) {
			asteroid_timer = 10;
			
			boolean vert = Math.random() < 0.5f;
			Vector2 randomPosition = new Vector2(
					7, 7
					);
			if(vert) {
				randomPosition.x = 7 + 7 * (Math.random() < 0.5f ? 1 :- 1);
				randomPosition.y = (float)(Math.random() *  14f);
			}
			else {
				randomPosition.y = 7 + 7 * (Math.random() < 0.5f ? 1 :- 1);
				randomPosition.x = (float)(Math.random() *  14f);
			}

			addAsteroid(randomPosition, 4, true);
		}
		
		for(AsteroidData ad : toBeCreated) {
			asteroids.add(new Asteroid(this, gameState.getWorld(), ad.position, ad.velocity, ad.size, asteroid_tex));
		}
		toBeCreated.clear();
	}
	
	public void addAsteroid(Vector2 position, int size, boolean towardsCenter) {
		
		Vector2 velocity;
		
		if(towardsCenter) {
		velocity = new Vector2(7, 7).sub(position.cpy()).nor().scl((float)Math.random() * 4);
		}
		else {
		velocity = new Vector2(
				(float)(Math.cos(Math.PI*2 * Math.random()) * Math.random() * 5),
				(float)(Math.sin(Math.PI*2 * Math.random()) * Math.random() * 5)
				);
		}
		
		toBeCreated.add(new AsteroidData(position, velocity, size));
	}


	public void render(SpriteBatch sb, OrthographicCamera camera) {
		sb.setProjectionMatrix(camera.combined);
		sb.begin();
		for(int i = asteroids.size() - 1; i >= 0; i --) {
			Asteroid a = asteroids.get(i);
			a.render(sb);
		}
		sb.end();
		
	}


	public void remove(Asteroid asteroid) {
		if(asteroids.contains(asteroid)) {
			asteroids.remove(asteroid);
			if(asteroid.size > 1) {
				for(int i = 0; i < 4; i ++) {
					addAsteroid(asteroid.body.getWorldCenter(), asteroid.size - 1, false);
				}
			}
		}
	}
	
	class AsteroidData{
		Vector2 position, velocity;
		int size;

		public AsteroidData(Vector2 position, Vector2 velocity, int size) {
			super();
			this.position = position;
			this.velocity = velocity;
			this.size = size;
		}
		
	}

}
