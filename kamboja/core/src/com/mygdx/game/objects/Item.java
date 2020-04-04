package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.KambojaMain;
import com.mygdx.game.states.GameState;

public class Item implements Steerable<Vector2>{
	
	public static final int ATTACK = 0;
	public static final int DEFFENSE = 1;
	public static final int SPEED = 2;
	public static final int LIFE = 3;
	
	public static final int TURRET = 4;
	public static final int BARRIER = 5;
	
	public static final int DRONE = 6;
	public static final int BOMB = 7;
	public static final int SPIKE = 8;
	public static final int GLUE = 9;
	public static final int ACID = 10;
	
	Body body;
	public int id;
	
	Texture texture;
	Texture itemPoint;
	
	float timer = 0;
	
	public boolean canRemove = false;
	
	ParticleEffect showing;
	
	static Sound blow, fire, deffense, life, spawn[] = new Sound[4];
	static{
		blow = Gdx.audio.newSound(Gdx.files.internal("audio/blow.ogg"));
		fire = Gdx.audio.newSound(Gdx.files.internal("audio/catch_fire.ogg"));
		deffense = Gdx.audio.newSound(Gdx.files.internal("audio/deffense.ogg"));
		life = Gdx.audio.newSound(Gdx.files.internal("audio/bubbles.ogg"));
		
		for(int i = 0; i < 4; i ++){
			spawn[i] = Gdx.audio.newSound(Gdx.files.internal("audio/spawn"+(i+1)+".ogg"));
		}

	}
	
	public void dispose(){
		//texture.dispose();
	}
	
	public Item(Body body, int id){
		this.body = body;
		this.id = id;
		
		itemPoint = KambojaMain.getTexture("Weapons/ItemPoint.png");
		
		showing = new ParticleEffect();
		showing.load(Gdx.files.internal("particles/item.par"), Gdx.files.internal("particles"));
		showing.scaleEffect(1f/GameState.UNIT_SCALE / 2f);
		
		if(GameState.SFX)
		spawn[(int)(Math.random()*4)].play(GameState.VOLUME);
		
		switch(id){
		case ATTACK:
			texture = KambojaMain.getTexture("imgs/attack.png");
			break;
		case DEFFENSE:
			texture = KambojaMain.getTexture("imgs/shield.png");
			break;
		case SPEED:
			texture = KambojaMain.getTexture("imgs/speed.png");
			break;
		case LIFE:
			texture = KambojaMain.getTexture("imgs/heart.png");
			break;
			
		case TURRET:
			texture = KambojaMain.getTexture("imgs/shift/turret.png");
			break;
		case BARRIER:
			texture = KambojaMain.getTexture("imgs/shift/barrier.png");
			break;
			
		case DRONE:
			texture = KambojaMain.getTexture("Weapons/Drone.png");
			break;
		case BOMB:
			texture = KambojaMain.getTexture("Weapons/bomb.png");
			break;
		case SPIKE:
			texture = KambojaMain.getTexture("Weapons/Icon/spike_icon.png");
			break;
		case GLUE:
			texture = KambojaMain.getTexture("Weapons/Icon/glue_icon.png");
			break;	
		case ACID:
			texture = KambojaMain.getTexture("Weapons/Icon/acid_icon.png");
			break;
		}
	}
	
	public void remove(){
		canRemove = true;
		switch(id){
		case ATTACK:
			if(GameState.SFX)
			fire.play(GameState.VOLUME);
			break;
		case DEFFENSE:
			if(GameState.SFX)
			deffense.play(GameState.VOLUME);
			break;
		case SPEED:
			if(GameState.SFX)
			blow.play(GameState.VOLUME);
			break;
		case LIFE:
			if(GameState.SFX)
			life.play(GameState.VOLUME);
			break;
			
		}
	}
	
	public boolean render(SpriteBatch sb, Camera camera){
		
		timer += Gdx.graphics.getDeltaTime();

		if(!canRemove) {
			if(showing.isComplete()){
				showing.reset();
			}
		}
		else {
			showing.allowCompletion();
		}
		
		showing.update(Gdx.graphics.getDeltaTime());
		showing.setPosition(body.getWorldCenter().x, body.getWorldCenter().y);
		
		showing.draw(sb);
		
		float clampedX = clamp(body.getWorldCenter().x, camera.unproject(new Vector3(0, 0, 0)).x, camera.unproject(new Vector3(1920, 0, 0)).x);
		float clampedY = clamp(body.getWorldCenter().y, camera.unproject(new Vector3(0, 1080, 0)).y, camera.unproject(new Vector3(0, 0, 0)).y);

		camera.frustum.pointInFrustum(new Vector3(body.getWorldCenter(), 0));
		
		sb.draw(texture,
				clampedX - (16 + (float)Math.sin(timer) * 2) / GameState.UNIT_SCALE,
				clampedY - (16 + (float)Math.sin(timer) * 2) / GameState.UNIT_SCALE,
				(16 + (float)Math.sin(timer) * 2) /GameState.UNIT_SCALE,
				(16 + (float)Math.sin(timer) * 2) /GameState.UNIT_SCALE,
				(32 + (float)Math.sin(timer) * 4) /GameState.UNIT_SCALE,
				(32 + (float)Math.sin(timer) * 4) /GameState.UNIT_SCALE,
				1, 1, 0, 0, 0, texture.getWidth(), texture.getHeight(), false, false);
		
		renderArrow(sb, clampedX, clampedY);
		
		return canRemove;
	}
	
	public void renderArrow(SpriteBatch sb, float clampedX, float clampedY) {
		
		int cx = 0;
		int cy = 0;
		
		float angle = 0;
		
		if(clampedX > body.getWorldCenter().x) {
			cx = -1;
		}
		else if(clampedX < body.getWorldCenter().x) {
			cx = 1;
		}

		if(clampedY > body.getWorldCenter().y) {
			cy = -1;
		}
		else if(clampedY < body.getWorldCenter().y) {
			cy = 1;
		}
		
		
		if(cx == 0 && cy == 1) {
			angle = 270;
		}
		else if(cx == 0 && cy == -1) {
			angle = 90;
		}
		else if(cx == 1 && cy == 0) {
			angle = 180;
		}
		else if(cx == 1 && cy == 1) {
			angle = 225;
		}
		else if(cx == 1 && cy == -1) {
			angle = 135;
		}
		else if(cx == -1 && cy == 0) {
			angle = 0;
		}
		else if(cx == -1 && cy == 1) {
			angle = 315;
		}
		else if(cx == -1 && cy == -1) {
			angle = 45;
		}
		
		//if(cx != 0 || cy != 0) {
			sb.draw(itemPoint,
					clampedX - (32 + (float)Math.sin(timer) * 2) / GameState.UNIT_SCALE,
					clampedY - (32 + (float)Math.sin(timer) * 2) / GameState.UNIT_SCALE,
					(32 + (float)Math.sin(timer) * 2) /GameState.UNIT_SCALE,
					(32 + (float)Math.sin(timer) * 2) /GameState.UNIT_SCALE,
					(64 + (float)Math.sin(timer) * 4) /GameState.UNIT_SCALE,
					(64 + (float)Math.sin(timer) * 4) /GameState.UNIT_SCALE,
					1, 1, angle, 0, 0, itemPoint.getWidth(), itemPoint.getHeight(), false, false);
		//}
		
	}
	
	public float clamp(float x, float min, float max) {
		return Math.max(min, Math.min(max, x));
		
	}
	public Body getBody() {
		return body;
	}

	@Override
	public float getMaxLinearSpeed() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setMaxLinearSpeed(float maxLinearSpeed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getMaxLinearAcceleration() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setMaxLinearAcceleration(float maxLinearAcceleration) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getMaxAngularSpeed() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setMaxAngularSpeed(float maxAngularSpeed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getMaxAngularAcceleration() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setMaxAngularAcceleration(float maxAngularAcceleration) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Vector2 getPosition() {
		return body.getWorldCenter();
	}

	@Override
	public float getOrientation() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Vector2 getLinearVelocity() {
		return body.getLinearVelocity();
	}

	@Override
	public float getAngularVelocity() {
		return body.getAngularVelocity();
	}

	@Override
	public float getBoundingRadius() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isTagged() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setTagged(boolean tagged) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Vector2 newVector() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float vectorToAngle(Vector2 vector) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Vector2 angleToVector(Vector2 outVector, float angle) {
		// TODO Auto-generated method stub
		return null;
	}

}
