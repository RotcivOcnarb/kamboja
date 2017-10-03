package com.mygdx.game.objects.deprecated;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.objects.Bullet;
import com.mygdx.game.objects.Item;
import com.mygdx.game.objects.Player;
import com.mygdx.game.objects.map.Block;
import com.mygdx.game.objects.weapon.*;
import com.mygdx.game.states.GameState;

public class OldBotPlayer extends Player{

	public DNA dna;
	float rangeOfView = 2000f / GameState.UNIT_SCALE;
	
	public OldBotPlayer(Body body, int id, GameState state) {
		super(body, id, state);
	}
	
	public void setDNA(DNA dna){
		if(dna == null){
			this.dna = new DNA(-1);
		}
		else{
			this.dna = dna;
		}
		
	}
	
	Vector2 totalForce = new Vector2();
	float totalAngleForce = 0;
	
	public void render(SpriteBatch sb){
		super.render(sb);

	}
	
	public void update(float delta){	
		super.update(delta);
		
		totalForce.set(0, 0);
		totalAngleForce = 0;
		
		for(Player p : getState().getPlayers()){
			
			float distance = p.getPosition().cpy().sub(getPosition()).len();
			
			if(distance < rangeOfView && p != this){
			
			Vector2 targetVelocity = p.getPosition().cpy().sub(getPosition()).nor().scl(speed);
			Vector2 force = targetVelocity.sub(body.getLinearVelocity()).scl(10.0f);
			
			float targetAngle = p.getPosition().cpy().sub(getPosition()).angle();
			float angleForce = (targetAngle - angle.cpy().scl(1, -1).angle())/10.0f;
			
			if(p.getWeapon() instanceof Bazooka){
				force.scl(dna.getMovementBazooka() * (1f/(distance*distance)));
				angleForce *= dna.getAimBazooka();
			}
			if(p.getWeapon() instanceof DoublePistol){
				force.scl(dna.getMovementDoublePistol() * (1f/(distance*distance)));
				angleForce *= dna.getAimDoublePistol();
			}
			if(p.getWeapon() instanceof Flamethrower){
				force.scl(dna.getMovementFlamethrower() * (1f/(distance*distance)));
				angleForce *= dna.getAimFlamethrower();
			}
			if(p.getWeapon() instanceof Laser){
				force.scl(dna.getMovementLaser() * (1f/(distance*distance)));
				angleForce *= dna.getAimLaser();
			}
			if(p.getWeapon() instanceof Minigun){
				force.scl(dna.getMovementMinigun() * (1f/(distance*distance)));
				angleForce *= dna.getAimMinigun();
			}
			if(p.getWeapon() instanceof Mp5){
				force.scl(dna.getMovementMP5() * (1f/(distance*distance)));
				angleForce *= dna.getAimMP5();
			}
			if(p.getWeapon() instanceof Pistol){
				force.scl(dna.getMovementPistol() * (1f/(distance*distance)));
				angleForce *= dna.getAimPistol();
			}
			if(p.getWeapon() instanceof Shotgun){
				force.scl(dna.getMovementShotgun() * (1f/(distance*distance)));
				angleForce *= dna.getAimShotgun();
			}
			
			if(p.getBuff() == Item.DEFFENSE){
				force.scl(dna.getMovementPlayerWithDeffense() * (1f/(distance*distance)));
				angleForce *= dna.getAimPlayerWithDeffense();
			}
			if(p.getBuff() == Item.ATTACK){
				force.scl(dna.getMovementPlayerWithAttack() * (1f/(distance*distance)));	
				angleForce *= dna.getAimPlayerWithAttack();
			}
			if(p.getBuff() == Item.SPEED){
				force.scl(dna.getMovementPlayerWithSpeed() * (1f/(distance*distance)));
				angleForce *= dna.getAimPlayerWithSpeed();
			}
			if(p.getBuff() == Item.TURRET){
				force.scl(dna.getMovementPlayerWithTurret() * (1f/(distance*distance)));
				angleForce *= dna.getAimPlayerWithTurret();
			}
			if(p.getBuff() == Item.BARRIER){
				force.scl(dna.getMovementPlayerWithBarrier() * (1f/(distance*distance)));
				angleForce *= dna.getAimPlayerWithBarrier();
			}
			
			totalAngleForce += angleForce;
			totalForce.add(force);
			}
		}
		for(Block b : getState().getBlocks()){
			float distance = b.getPosition().cpy().sub(getPosition()).len();

			if(distance < rangeOfView){
			Vector2 targetVelocity = b.getPosition().cpy().sub(getPosition()).nor().scl(speed);
			Vector2 force = targetVelocity.sub(body.getLinearVelocity()).scl(10.0f);

			float targetAngle = b.getPosition().cpy().sub(getPosition()).angle();
			float angleForce = (targetAngle - angle.cpy().scl(1, -1).angle())/10.0f;
			
			force.scl(dna.getMovementBlock() * (1f/(distance*distance)));
			angleForce *= dna.getAimBlock();
			
			totalAngleForce += angleForce;
			totalForce.add(force);
			}
		}
		for(Item it : getState().getItems()){
			if(it.id != getId()){
				float distance = it.getPosition().cpy().sub(getPosition()).len();
	
				if(distance < rangeOfView){
					Vector2 targetVelocity = it.getPosition().cpy().sub(getPosition()).nor().scl(speed);
					Vector2 force = targetVelocity.sub(body.getLinearVelocity()).scl(10.0f);
					
					if(it.id == Item.LIFE){
						force.scl(dna.getMovementLifeItem() * (1- life/100f) * (1f/(distance*distance)));
					}
					if(it.id == Item.DEFFENSE){
						force.scl(dna.getMovementDeffenseItem() * (1f/(distance*distance)));			
					}
					if(it.id == Item.ATTACK){
						force.scl(dna.getMovementAttackItem() * (1f/(distance*distance)));
					}
					if(it.id == Item.SPEED){
						force.scl(dna.getMovementSpeedItem() * (1f/(distance*distance)));
					}
					if(it.id == Item.TURRET){
						force.scl(dna.getMovementTurretItem() * (1f/(distance*distance)));
					}
					if(it.id == Item.BARRIER){
						force.scl(dna.getMovementBarrierItem() * (1f/(distance*distance)));
					}
					
					totalForce.add(force);
				}
			}
		}
		for(Bullet b : getState().getBullets()){
			if(b.player != this){
				float distance = b.body.getWorldCenter().cpy().sub(getPosition()).len();
	
				if(distance < rangeOfView){
					Vector2 targetVelocity = b.body.getWorldCenter().cpy().sub(getPosition()).nor().scl(speed);
					Vector2 force = targetVelocity.sub(body.getLinearVelocity()).scl(10.0f);
		
					force.scl(dna.getMovementBullet() * (1f/(distance*distance)));
					
					totalForce.add(force);
				}
			}
		}
		for(Body b : getState().getFlameParticles()){
			if(((Flamethrower)b.getUserData()).getPlayer() != this){
				float distance = b.getWorldCenter().cpy().sub(getPosition()).len();
				
				if(distance < rangeOfView){
					Vector2 targetVelocity = b.getWorldCenter().cpy().sub(getPosition()).nor().scl(speed);
					Vector2 force = targetVelocity.sub(body.getLinearVelocity()).scl(10.0f);
		
					force.scl(dna.getMovementFlame() * (1f/(distance*distance)));
					
					totalForce.add(force);
				}
			}
		}
		//TODO: turret e barrier
		totalForce.clamp(0, speed);
		if(!isDead()){
			body.applyForceToCenter(totalForce, true);
			angle = angle.cpy().scl(1, -1).rotate(totalAngleForce).scl(1, -1);
		}
		
		if(!isDead())
			getWeapon().update(delta);
		
		getWeapon().botShoot();

	}

	public Vector2 getPosition() {
		return body.getWorldCenter();
	}

	public float getOrientation() {
		return body.getAngle();
	}

	public Vector2 getLinearVelocity() {
		return body.getLinearVelocity();
	}

	public float getAngularVelocity() {
		return body.getAngularVelocity();
	}

	public Vector2 newVector() {
		return new Vector2();
	}

	public float vectorToAngle(Vector2 vector) {
		return (float)Math.atan2(-vector.x, vector.y);
	}

	public Vector2 angleToVector(Vector2 outVector, float angle) {
		outVector.x -= (float)Math.sin(angle);
		outVector.y = (float)Math.cos(angle);
		return outVector;
	}

}
