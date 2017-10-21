package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.states.GameState;

public class Item implements Steerable<Vector2>{
	
	public static final int ATTACK = 0;
	public static final int DEFFENSE = 1;
	public static final int SPEED = 2;
	public static final int LIFE = 3;
	
	public static final int TURRET = 4;
	public static final int BARRIER = 5;
	
	Body body;
	public int id;
	
	Texture texture;
	
	float timer = 0;
	
	boolean canRemove = false;
	
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
		texture.dispose();
	}
	
	public Item(Body body, int id){
		this.body = body;
		this.id = id;
		
		if(GameState.SFX)
		spawn[(int)(Math.random()*4)].play(GameState.VOLUME);
		
		switch(id){
		case ATTACK:
			texture = new Texture("imgs/attack.png");
			break;
		case DEFFENSE:
			texture = new Texture("imgs/shield.png");
			break;
		case SPEED:
			texture = new Texture("imgs/speed.png");
			break;
		case LIFE:
			texture = new Texture("imgs/heart.png");
			break;
			
		case TURRET:
			texture = new Texture("imgs/shift/turret.png");
			break;
		case BARRIER:
			texture = new Texture("imgs/shift/barrier.png");
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
	
	public boolean render(SpriteBatch sb){
		
		timer += Gdx.graphics.getDeltaTime();
		
		sb.draw(texture,
				body.getWorldCenter().x - (8 + (float)Math.sin(timer) * 2) / GameState.UNIT_SCALE,
				body.getWorldCenter().y - (8 + (float)Math.sin(timer) * 2) / GameState.UNIT_SCALE,
				(8 + (float)Math.sin(timer) * 2) /GameState.UNIT_SCALE,
				(8 + (float)Math.sin(timer) * 2) /GameState.UNIT_SCALE,
				(16 + (float)Math.sin(timer) * 4) /GameState.UNIT_SCALE,
				(16 + (float)Math.sin(timer) * 4) /GameState.UNIT_SCALE,
				1, 1, 0, 0, 0, texture.getWidth(), texture.getHeight(), false, false);
		
		return canRemove;
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
