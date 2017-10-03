package com.mygdx.game.objects.deprecated;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.objects.Player;
import com.mygdx.game.states.GameState;

public class MolotovPool {
	
	public Player player;
	public Body body;
	
	float timer;
	
	Texture pool;
	
	float opacity = 0f;
	
	ParticleEffect fire;

	float damage;
	
	public void dispose(){
		pool.dispose();
		fire.dispose();
	}
	
	public MolotovPool(Body body, Player player, float damage){
		this.player = player;
		this.body = body;
		this.damage = damage;
		pool = new Texture("imgs/weapons/pool.png");
		
		fire = new ParticleEffect();
		fire.load(Gdx.files.internal("particles/fire.par"), Gdx.files.internal("particles"));
		fire.scaleEffect(1f/GameState.UNIT_SCALE / 2f);
		fire.start();
		
	}
	
	public float getDamage(){
		return damage;
	}
	
	public Body getBody(){
		return body;
	}
	
	public boolean render(SpriteBatch sb){
		timer += Gdx.graphics.getDeltaTime();
		
		if(timer < 4){
			opacity += Gdx.graphics.getDeltaTime()*3;
			if(opacity > 0.7f) opacity = 0.7f;
		}
		else{
			opacity = (5 - timer) * 0.7f;
			if(opacity < 0) opacity = 0;
		}
		
		switch(player.getId()){
		case 0:
			sb.setColor(0, 0, 1, opacity);
			break;
		case 1:
			sb.setColor(1, 0, 0, opacity);
			break;
		case 2:
			sb.setColor(0, 1, 0, opacity);
			break;
		case 3:
			sb.setColor(1, 1, 0, opacity);
			break;
		}
		
		fire.setPosition(body.getWorldCenter().x,
				body.getWorldCenter().y);
		
		sb.draw(pool,
				body.getWorldCenter().x - 48 / GameState.UNIT_SCALE,
				body.getWorldCenter().y - 48 / GameState.UNIT_SCALE,
				48 /GameState.UNIT_SCALE,
				48 /GameState.UNIT_SCALE,
				96 /GameState.UNIT_SCALE,
				96 /GameState.UNIT_SCALE,
				1, 1, 0, 0, 0, pool.getWidth(), pool.getHeight(), false, false);
		
		fire.draw(sb);
		fire.update(Gdx.graphics.getDeltaTime());
		if(timer < 4){
			if(fire.isComplete()){
				fire.reset();
			}
		}
		
		if(timer > 5){
			return true;
		}
		
		return false;
		
	}

}
