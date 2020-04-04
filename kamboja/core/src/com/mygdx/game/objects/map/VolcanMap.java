package com.mygdx.game.objects.map;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.objects.players.Player;
import com.mygdx.game.states.GameState;

public class VolcanMap extends KambojaMap{
	
	float timer = 10;
	
	float rockLaunch = 0.5f;
	int numRocks = 0;
	
	Texture[] lava_rocks = new Texture[3];
	Texture shadow;
	
	ArrayList<LavaRock> rocks;

	public void create() {
		for(int i = 0; i < 3; i ++) {
			lava_rocks[i] = new Texture("map_assets/lava_rock_" + (i+1) + ".png");
		}
		rocks = new ArrayList<VolcanMap.LavaRock>();
		shadow = new Texture("map_assets/shadow.png");
	}
	
	public void render(SpriteBatch sb, OrthographicCamera camera) {
		sb.setProjectionMatrix(camera.combined);
		sb.begin();
		
		for(int i = rocks.size() - 1; i >= 0; i  --) {
			LavaRock lr = rocks.get(i);
			if(!lr.ascending) {
				sb.setColor(1, 1, 1, 1);
				sb.draw(shadow,
						lr.position.x - shadow.getWidth() / GameState.UNIT_SCALE/2f,
						lr.position.y - shadow.getHeight() / GameState.UNIT_SCALE/2f,
						shadow.getWidth() / GameState.UNIT_SCALE,
						shadow.getHeight() / GameState.UNIT_SCALE);
				sb.setColor(1, 1, 1, 1);
			}
			lr.render(sb);
		}
		
		sb.end();
	}

	public void update(float delta) {
		timer -= delta;
		
		if(timer <= 0) {
			rockLaunch -= delta;
			if(rockLaunch <= 0) {
				rockLaunch = 0.5f;
				numRocks ++;
				rocks.add(new LavaRock(lava_rocks[(int)(Math.random() * 3)], new Vector2(17.5f, 13.6f), rocks));
				if(numRocks == 3) {
					timer = (float)(Math.random() * 10) + 10;
					rockLaunch = 0.5f;
					numRocks = 0;
				}
			}
		}
		
		for(int i = rocks.size() - 1; i >= 0; i  --) {
			LavaRock lr = rocks.get(i);
			if(lr.update(delta)) {
				for(Player p : gameState.getPlayers()) {
					float dist = p.getPosition().cpy().sub(lr.position.cpy()).len();
					if(dist < 1) {
						p.takeDamage(1000, null, true, true);
					}
					
				}
			}
		}
	}
	
	class LavaRock{
		
		boolean ascending;
		
		Texture tex;
		
		float scale = 1;
		float alpha = 1;
		float angle = 0;
		float speedAngle;
		
		Vector2 position;
		
		float delayTimer = 0;
		boolean alreadyHit = false;
		
		ArrayList<LavaRock> rocks;
		
		public LavaRock(Texture tex, Vector2 position, ArrayList<LavaRock> rocks) {
			ascending = true;
			this.position = position;
			this.tex = tex;
			this.rocks = rocks;
			speedAngle = (float)(((Math.random() * 30) + 40) * (Math.random() > 0.5 ? 1 : -1));
		}
		
		public boolean update(float delta) {
			boolean hit = false;
			
			if(ascending) {
				scale += delta*5f * scale;
				alpha -= delta;
				angle += delta*speedAngle;
				if(alpha <= 0) {
					alpha = 0;
					ascending = false;
					float randMag = (float)(Math.random() * 3) + 3;
					position.add((float)Math.cos(Math.random() * Math.PI*2) * randMag, (float)Math.sin(Math.random() * Math.PI*2) * randMag);
				}
			}
			else {
				delayTimer += delta;
				if(delayTimer > 3) {
					scale -= delta*5f * scale;
					if(scale <= 1) {
						if(!alreadyHit) {
							hit = true;
							alreadyHit = true;
						}
						scale = 1;
						alpha -= delta;
						if(alpha <= 0) {
							alpha = 0;
							rocks.remove(this);
						}
					}
					else {
						alpha += delta;
						angle += delta*speedAngle;
					}
					
					
					if(alpha >= 1) alpha = 1;
				}
			}
			
			
			return hit;
		}
		
		public void render(SpriteBatch sb) {
			sb.setColor(1, 1, 1, alpha);
			sb.draw(tex,
					position.x - tex.getWidth()/2f / GameState.UNIT_SCALE,
					position.y - tex.getHeight()/2f / GameState.UNIT_SCALE,
					tex.getWidth()/2f / GameState.UNIT_SCALE,
					tex.getHeight()/2f / GameState.UNIT_SCALE,
					tex.getWidth() / GameState.UNIT_SCALE,
					tex.getHeight() / GameState.UNIT_SCALE,
					scale,
					scale,
					angle,
					0,
					0,
					tex.getWidth(),
					tex.getHeight(),
					false,
					false);
			sb.setColor(1, 1, 1, 1);
		}
		
	}

	@Override
	public void behindRender(SpriteBatch sb, OrthographicCamera camera) {
		// TODO Auto-generated method stub
		
	}
	
}
