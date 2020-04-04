package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.states.GameState;

public class Ghost {

	private Vector2 target;
	private Vector2 position;
	private Vector2 velocity;
	
	Animation<TextureRegion> anim;
	
	float timer = 0;
	
	private int color;
	
	public Ghost(int color, Vector2 position) {
		target = new Vector2();
		velocity = new Vector2((float)Math.random() * 0.1f, (float)Math.random() * 0.1f);
		this.position = position.cpy();
		this.setColor(color);
		
		Texture sheet = new Texture("imgs/ghost.png");
		
		TextureRegion tr[] = new TextureRegion[4];
		
		for(int i = 0; i < 4; i ++){
			tr[i] = new TextureRegion(sheet, i*32, 0, 32, 32);
		}
		
		anim = new Animation<TextureRegion>(1/10f, tr);
		anim.setPlayMode(PlayMode.LOOP);
		
	}
	
	public void setTargetPosition(Vector2 pos){
		this.target = pos.cpy().add((float)Math.cos(timer*2) * 0.7f, (float)Math.sin(timer*2) * 0.7f);
	}
	
	public void render(SpriteBatch sb){
		
		TextureRegion tr = anim.getKeyFrame(timer);
		
		sb.draw(tr,
				position.x - (tr.getRegionWidth() * 1/GameState.UNIT_SCALE * 0.5f)/2,
				position.y - (tr.getRegionHeight() * 1/GameState.UNIT_SCALE * 0.5f)/2,
				0, 0,
				tr.getRegionWidth(),
				tr.getRegionHeight(), 1/GameState.UNIT_SCALE * 0.5f, 1/GameState.UNIT_SCALE * 0.5f, 0);
		
	}
	
	public void update(float delta){
				
		timer += delta;
		
		velocity.add(target.cpy().sub(position.cpy()).scl(1/2f));		
		velocity.scl(0.9f);
		
		position.add(velocity.cpy().scl(delta));
		
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

}
