package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class PersistentParticle {
	
	Texture tex;
	Vector2 position;
	Vector2 velocity;
	float linearDamping;
	float scale;
	float angle;
	
	public PersistentParticle(
			Texture tex, Vector2 position, Vector2 velocity,
			float linearDamping, float scale, float angle) {
		this.tex = tex;
		this.position = position;
		this.velocity = velocity;
		this.linearDamping = linearDamping;
		this.scale = scale;
		this.angle = angle;
	}
	
	public void render(SpriteBatch sb) {
		
		sb.draw(tex,
				position.x,
				position.y,
				tex.getWidth()/2 * scale,
				tex.getHeight()/2 * scale,
				tex.getWidth(),
				tex.getHeight(),
				scale,
				scale,
				angle,
				0,
				0,
				tex.getWidth(),
				tex.getHeight(),
				false,
				false
				);
		
	}
	
	public void update(double delta) {
				
		velocity.scl((float)Math.pow(1.01, -linearDamping));
		position.add(velocity);
		
	}

}
