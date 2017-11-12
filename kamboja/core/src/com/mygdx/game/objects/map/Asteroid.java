package com.mygdx.game.objects.map;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.states.GameState;

public class Asteroid {
	
	int size;
	Body body;
	Texture tex;
	SpaceMap spaceMap;
	public Asteroid(SpaceMap spaceMap, World world, Vector2 position, Vector2 velocity, int size, Texture tex) {
		this.size = size;
		
		this.spaceMap = spaceMap;
		
		BodyDef def = new BodyDef();
		def.position.set(position);
		def.linearVelocity.set(velocity);
		def.linearDamping = 0;
		this.tex = tex;
		def.type = BodyType.DynamicBody;
		
		body = world.createBody(def);
		
		CircleShape cs = new CircleShape();
		cs.setRadius(8 * size / GameState.UNIT_SCALE);
		
		Fixture f = body.createFixture(cs, 1);
		
		f.setUserData(this);
		body.setUserData(this);
		
	}
	
	public void render(SpriteBatch sb) {
		sb.draw(tex,
				body.getWorldCenter().x - 16 * size/2f / GameState.UNIT_SCALE,
				body.getWorldCenter().y - 16 * size/2f / GameState.UNIT_SCALE,
				16 * size/2f / GameState.UNIT_SCALE,
				16 * size/2f / GameState.UNIT_SCALE,
				16 * size / GameState.UNIT_SCALE,
				16 * size / GameState.UNIT_SCALE,
				1, 1, 0,
				0, 0,
				tex.getWidth(),
				tex.getHeight(),
				false,
				false);
	}

	public void destroy() {
		spaceMap.remove(this);
		GameState.removeBody(body);
	}

}
