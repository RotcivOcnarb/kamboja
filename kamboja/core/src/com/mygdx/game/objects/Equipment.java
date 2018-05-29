package com.mygdx.game.objects;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.states.GameState;

public class Equipment {
	
	public static final float DRONE_DAMAGE = 5;

	Player player;
	
	float drone_distance = 40 / GameState.UNIT_SCALE;
	float globalTimer = 0;
	float shootTimer = 0;
	int indexShooting = 0;
	float shootDuration = 1;
	
	ShapeRenderer sr;
	
	ArrayList<Vector2> drone_positions;
	
	public Equipment(Player player) {
		this.player = player;
		sr = new ShapeRenderer();
		drone_positions = new ArrayList<Vector2>();
	}
	
	public void addDrone() {
		drone_positions.add(player.getPosition().cpy());
	}
	
	public void render(SpriteBatch sb) {
	
		
		
		sr.begin(ShapeType.Filled);
		sr.setProjectionMatrix(sb.getProjectionMatrix());
		sr.setColor(Color.RED);
		
		float angle = 0;
		for(int i = 0; i < drone_positions.size(); i ++) {
			
			angle += 360/drone_positions.size();
			
			Vector2 d_pos = player.getPosition().cpy().add(
					(float)Math.cos(Math.toRadians(angle + globalTimer*100)) * drone_distance,
					(float)Math.sin(Math.toRadians(angle + globalTimer*100)) * drone_distance
					);
			
			drone_positions.get(i).add(d_pos.cpy().sub(drone_positions.get(i)).scl(1/15f));
			
			sr.circle(drone_positions.get(i).x, drone_positions.get(i).y, 5 / GameState.UNIT_SCALE, 20);
			
		}
		
		sr.end();
		
	}
	
	public void update(float delta) {
		globalTimer += delta;
		if(drone_positions.size() > 0)
		shootTimer += delta;
		
		if(shootTimer > shootDuration / drone_positions.size()) {
			shootTimer -= shootDuration / drone_positions.size();
			
			indexShooting ++;
			if(indexShooting == drone_positions.size()) indexShooting = 0;
			
			
			Player closest = null;
			for(Player p : player.getState().getPlayers()) {
				if(p != player) {
					
					if(closest == null) {
						closest = p;
					}
					else {
						if(player.getPosition().cpy().sub(p.getPosition()).len2() < player.getPosition().cpy().sub(closest.getPosition()).len2()) {
							closest = p;
						}
					}
					
				}
			}
			
			
			Bullet bullet = new Bullet(
					player.getBody().getWorld(),
					drone_positions.get(indexShooting),
					closest.getPosition().cpy().sub(player.getPosition()).nor().scl(30), player.getId(), DRONE_DAMAGE * player.getAtk(), 5, player);
			player.getState().addBullet(bullet);
		}
		
	}
	
}
