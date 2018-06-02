package com.mygdx.game.objects;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.states.GameState;

public class AcidGlue {

	Player player;
	int acid_level;
	int glue_level;
	float waitTime = 5f;
	float rotation;
	Body body;
	Texture tex;

	ArrayList<AcidGlue> list;
	
	public AcidGlue(ArrayList<AcidGlue> list, World world, Player player, int acid, int glue) {
		this.player = player;
		this.list = list;
		this.acid_level = acid;
		this.glue_level = glue;
		rotation = (float) (Math.random() * 360);
		tex = new Texture("Weapons/glue.png");
		
		BodyDef def = new BodyDef();
		def.position.set(player.getPosition().cpy());
		def.type = BodyType.StaticBody;
		body = world.createBody(def);
		
		CircleShape cs = new CircleShape();
		cs.setRadius(14 / GameState.UNIT_SCALE);
		Fixture fs = body.createFixture(cs, 0);
		fs.setSensor(true);
		fs.setUserData(this);
		body.setUserData(this);
		
	}
	
	public void render(SpriteBatch sb) {
		waitTime -= Gdx.graphics.getDeltaTime();
		
		if(waitTime < 0) {
			list.remove(this);
			GameState.removeBody(body);
		}
	
		float alpha = 0;
		if(glue_level != 0) {
			alpha = (float)Math.pow(2, -(acid_level/glue_level));
		}
		
		sb.setColor(new Color(0, 0.5f, 0, Math.min(1, waitTime)).cpy().lerp(new Color(1, 1, 1, Math.min(1, waitTime)).cpy(), alpha));
		
		sb.draw(tex,
				body.getWorldCenter().x - tex.getWidth()/2f / GameState.UNIT_SCALE,
				body.getWorldCenter().y - tex.getHeight()/2f / GameState.UNIT_SCALE,
				tex.getWidth()/2f / GameState.UNIT_SCALE,
				tex.getHeight()/2f / GameState.UNIT_SCALE,
				tex.getWidth() / GameState.UNIT_SCALE,
				tex.getHeight() / GameState.UNIT_SCALE,
				1, 1, rotation,
				0, 0,
				tex.getWidth(),
				tex.getHeight(),
				false, false);
		
		sb.setColor(1, 1, 1, 1);
		
	
		
	}
	
}
