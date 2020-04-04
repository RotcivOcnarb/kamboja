package com.mygdx.game.objects.players;

import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.mygdx.game.objects.Pathfinding;
import com.mygdx.game.objects.map.BreakableBlock;
import com.mygdx.game.objects.map.UnbreakableBlock;
import com.mygdx.game.objects.weapon.Flamethrower;
import com.mygdx.game.objects.weapon.Shotgun;
import com.mygdx.game.states.GameState;

public class BetterBot extends Player{
	
	ArrayList<Vector2> path;
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
	
	public BetterBot(Body body, int id, GameState state, String name) {
		super(body, id, state, name);
		path = new ArrayList<Vector2>();

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
					if(fixture.getBody().getUserData() instanceof UnbreakableBlock || fixture.getBody().getUserData() instanceof BreakableBlock){
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
	
	void IA(float delta) {
		target = null;
		
		for(Player p : getState().getPlayers()) {
			if(p != this) {
				if(target == null) {
					if(!p.isDead()) {
						target = p.getPosition();
					}
				}
				else if(p.getPosition().cpy().sub(getPosition()).len() < target.cpy().sub(getPosition()).len()) {
					if(!p.isDead())
					target = p.getPosition();
				}
			}
		}
	
	if(target != null && !isDead()) {
	Vector2 aimingTarget = target.cpy().sub(getPosition().cpy()).nor();
	angleAiming.add(aimingTarget.cpy().sub(angleAiming.cpy()).scl(difficulty));
	setAngle(angleAiming.cpy().scl(1, -1));
	
	if(target.cpy().sub(getPosition().cpy()).len2() > 0) {
		getState().getWorld().rayCast(raycast, getPosition().cpy(), target.cpy());
	}
	else {
		System.out.println("Ha! This should have given an error (i think)");
	}
	
		pf.setSx((int)(getPosition().x / (32 / GameState.UNIT_SCALE)));
		pf.setSy((int)(getPosition().y / (32 / GameState.UNIT_SCALE)));
		pf.setEx((int)(target.x / (32 / GameState.UNIT_SCALE)));
		pf.setEy((int)(target.y / (32 / GameState.UNIT_SCALE)));
		pf.setMap(getState().getBitmap());
		path = pf.getPath();
	
	}
	
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
			else {
				body.applyForceToCenter(angleAiming.cpy().rotate(90).scl(5, -5), true);
			}
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
	}
	
	public void update(float delta) {
		super.update(delta);
				
		//IA();
		
	}

}
