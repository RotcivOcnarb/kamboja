package com.mygdx.game.objects;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class PersistentParticleEffect {
	
	ArrayList<PersistentParticle> particles;
	Texture[] textures;
	
	Vector2 minPos, maxPos;
	Vector2 minVel, maxVel;
	float minLinDamp, maxLinDamp;
	float minScale, maxScale;
	float minAngle, maxAngle;
	
	public PersistentParticleEffect(Texture... tex) {
		particles = new ArrayList<PersistentParticle>();
		textures = tex;
		
		minPos = new Vector2();
		maxPos = new Vector2();
		minVel = new Vector2();
		maxVel = new Vector2();
		minLinDamp = 0;
		maxLinDamp = 0;
		minScale = 1;
		maxScale = 1;
		minAngle = 0;
		maxAngle = 360;
	}
	
	public float random(float min, float max) {
		return (float) (Math.random() * (max - min) + min);
	}
	
	public void render(SpriteBatch sb) {
		sb.begin();
		for(int i = 0; i < particles.size(); i ++) {
			PersistentParticle pp = particles.get(i);
			pp.render(sb);
		}
		sb.end();
	}
	
	public void update(float delta) {
		for(int i = particles.size() - 1; i >= 0; i --) {
			PersistentParticle pp = particles.get(i);
			pp.update(delta);
		}
	}
	
	public void addParticle() {
		particles.add(new PersistentParticle(
				textures[(int)random(0, textures.length)],
				new Vector2(random(minPos.x, maxPos.x), random(minPos.y, maxPos.y)),
				new Vector2(random(minVel.x, maxVel.x), random(minVel.y, maxVel.y)),
				random(minLinDamp, maxLinDamp), random(minScale, maxScale),
				random(minAngle, maxAngle)));
	}

	public Vector2 getMinPos() {
		return minPos;
	}

	public void setMinPos(Vector2 minPos) {
		this.minPos = minPos;
	}

	public Vector2 getMaxPos() {
		return maxPos;
	}

	public void setMaxPos(Vector2 maxPos) {
		this.maxPos = maxPos;
	}

	public Vector2 getMinVel() {
		return minVel;
	}

	public void setMinVel(Vector2 minVel) {
		this.minVel = minVel;
	}

	public Vector2 getMaxVel() {
		return maxVel;
	}

	public void setMaxVel(Vector2 maxVel) {
		this.maxVel = maxVel;
	}

	public float getMinLinDamp() {
		return minLinDamp;
	}

	public void setMinLinDamp(float minLinDamp) {
		this.minLinDamp = minLinDamp;
	}

	public float getMaxLinDamp() {
		return maxLinDamp;
	}

	public void setMaxLinDamp(float maxLinDamp) {
		this.maxLinDamp = maxLinDamp;
	}

	public float getMinScale() {
		return minScale;
	}

	public void setMinScale(float minScale) {
		this.minScale = minScale;
	}

	public float getMaxScale() {
		return maxScale;
	}

	public void setMaxScale(float maxScale) {
		this.maxScale = maxScale;
	}

	public float getMinAngle() {
		return minAngle;
	}

	public void setMinAngle(float minAngle) {
		this.minAngle = minAngle;
	}

	public float getMaxAngle() {
		return maxAngle;
	}

	public void setMaxAngle(float maxAngle) {
		this.maxAngle = maxAngle;
	}

}
