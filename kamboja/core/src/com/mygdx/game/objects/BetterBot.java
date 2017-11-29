package com.mygdx.game.objects;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.mygdx.game.objects.map.Block;
import com.mygdx.game.objects.map.UnbreakableBlock;
import com.mygdx.game.objects.weapon.Flamethrower;
import com.mygdx.game.objects.weapon.Shotgun;
import com.mygdx.game.states.GameState;

public class BetterBot extends Player{
	
	ArrayList<Vector2> path;
		
	int bitmap[][];
	
	Vector2 target;

	Pathfinding pf;
	
	Vector2 angleAiming;
	
	float difficulty;
	
	boolean canHit = false;
	
	RayCastCallback raycast;
	
	public void dispose() {
		super.dispose();
		pf.end();
	}
	
	public BetterBot(Body body, int id, GameState state) {
		super(body, id, state);
		path = new ArrayList<Vector2>();
				
		bitmap = new int[getState().getTiledMap().getProperties().get("width", Integer.class)][getState().getTiledMap().getProperties().get("height", Integer.class)];

		target = new Vector2();
	
		pf = new Pathfinding();
		
		angleAiming = new Vector2();
		
		switch(GameState.DIFFICULTY){
	    case 0:
	    	difficulty = 0.01f;
	    	break;
	    case 1:
	    	difficulty = 0.05f;
	    	break;
	    case 2:
	    	difficulty = 0.1f;
	    	break;
	    case 3:
	    	difficulty = 0.5f;
	    	break;
	    case 4:
	    	difficulty = 1f;
	    	break;
	    	
	    }
		
		raycast = new RayCastCallback() {
						
			public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
				if(fixture.getBody().getUserData() instanceof Player && !fixture.getBody().getUserData().equals(this)) {
					canHit = true;
					return -1;
				}
				else {
					if(fixture.getBody().getUserData() instanceof UnbreakableBlock){
						canHit = false;
						return 0;
					}
				}
				return -1;
				
			}
		};
		
	}
	
	public void render(SpriteBatch sb){
		super.render(sb);
		
	}
	
	public void update(float delta) {
		long startTime = System.nanoTime();
		super.update(delta);
		
		for(Player p : getState().getPlayers()) {
			if(p.getPosition().cpy().sub(getPosition()).len() < target.cpy().sub(getPosition()).len()) {
				if(p != this)
				target = p.getPosition();
			}
		}

		if(target != null && !isDead()) {
		Vector2 aimingTarget = target.cpy().sub(getPosition().cpy()).nor();
		angleAiming.add(aimingTarget.cpy().sub(angleAiming.cpy()).scl(difficulty));
		setAngle(angleAiming.cpy().scl(1, -1));
		
		getState().getWorld().rayCast(raycast, getPosition().cpy(), target.cpy());
		
		}
		
		pf.setSx((int)(getPosition().x / (32 / GameState.UNIT_SCALE)));
		pf.setSy((int)(getPosition().y / (32 / GameState.UNIT_SCALE)));
		pf.setEx((int)(target.x / (32 / GameState.UNIT_SCALE)));
		pf.setEy((int)(target.y / (32 / GameState.UNIT_SCALE)));
		pf.setMap(bitmap);
		
		path = pf.getPath();
		
		if(path != null) {
			for(Vector2 p : path) {
				p.scl(32 / GameState.UNIT_SCALE);
				p.add(16 / GameState.UNIT_SCALE, 16 / GameState.UNIT_SCALE);
			}
			Collections.reverse(path);
			
			if(!path.isEmpty())
				path.remove(0);
		}
		
		if(path.size() > 0 && !isDead()) {
			if(getWeapon() instanceof Flamethrower || getWeapon() instanceof Shotgun) {
				body.applyForceToCenter(path.get(0).cpy().sub(body.getWorldCenter().cpy()).nor().scl(speed * spd), true);
			}
			else {
				if(!canHit)
				body.applyForceToCenter(path.get(0).cpy().sub(body.getWorldCenter().cpy()).nor().scl(speed * spd), true);
			}
		}
		
		if(!isDead()) {
			if(getWeapon() instanceof Flamethrower || getWeapon() instanceof Shotgun) {
				getWeapon().analog = 1;
			}
			else {
			
				if(canHit) {
					getWeapon().analog = 1;
				}
				else {
					getWeapon().analog = 0;
				}
				
			}
		}
		
		for(int i = 0; i < bitmap.length; i ++ ) {
			for(int j = 0; j < bitmap[0].length; j ++ ) {
				bitmap[i][j] = 0;
			}
		}
		for(int i = 0; i < bitmap.length; i ++ ) {
			for(int j = 0; j < bitmap[0].length; j ++ ) {
				final int x = i;
				final int y = j;
				body.getWorld().QueryAABB(
						new QueryCallback() {
					public boolean reportFixture(Fixture fixture) {
						if(fixture.getBody().getUserData() instanceof Block) {
							bitmap[x][y] = 1;
							return false;
						}
						return true;
					}
				},	
				(16 + i * 32 - 1) / GameState.UNIT_SCALE , (16 + j * 32 - 1) / GameState.UNIT_SCALE,
				(16 + i * 32 + 1) / GameState.UNIT_SCALE, (16 + j * 32 + 1) / GameState.UNIT_SCALE);
				
			}
		}
		

		int elapsed = (int)((System.nanoTime() - startTime) / 1000000f);
		float percent = (elapsed / (1000/60f))*100;
		System.out.println("rendered player in: " + percent + "% of total frame time");
		startTime = System.nanoTime();
	}

}
