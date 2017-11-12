package com.mygdx.game.objects.map;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.objects.Bullet;
import com.mygdx.game.states.GameState;

public abstract class Block {
	
	TextureRegion texture;
	float x;
	float y;
	float width;
	float height;
	Cell cell;
	Body body;

	GameState state;
	
	public Block(TextureRegion texture, float x, float y, float width, float height, World world, GameState state, Cell cell){
		this.texture = texture;
		this.state = state;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.cell = cell;
				
		BodyDef def = new BodyDef();
		def.position.set(x, y);
		
		def.type = BodyType.StaticBody;
		
		body = world.createBody(def);
		PolygonShape ps = new PolygonShape();
		ps.setAsBox(width/2, height/2, new Vector2(width/2, height/2), 0);
		
		Fixture f = body.createFixture(ps, 1);
		ps.dispose();
		
		f.setUserData(this);
		body.setUserData(this);

	}
	public abstract void dispose();
	public Body getBody(){
		return body;
	}
	public abstract void takeDamage(float amount, boolean flame);
	public abstract boolean render(SpriteBatch sb);

	public Vector2 getPosition() {
		return body.getWorldCenter();
	}
	
	public abstract void bulletCollided(Contact contact, Bullet bullet);

}
