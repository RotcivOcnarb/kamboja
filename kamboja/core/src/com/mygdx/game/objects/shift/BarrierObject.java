package com.mygdx.game.objects.shift;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.states.GameState;

public class BarrierObject {
	
	public Barrier barrier;
	
	public Body body;
	Texture barrierTex;
	
	float life = Barrier.LIFE;
	
	Sound hit, destroy;
	
	float hitTimer = 0;
	
	float angle;
	
	public void dispose(){
		hit.dispose();
		destroy.dispose();
		barrierTex.dispose();
	}

	public BarrierObject(Barrier barrier, World world, Vector2 position) {
		this.barrier = barrier;
		
		hit = Gdx.audio.newSound(Gdx.files.internal("audio/shift/metal_hit.ogg"));
		destroy = Gdx.audio.newSound(Gdx.files.internal("audio/shift/turret_destroy.ogg"));

		BodyDef def = new BodyDef();
		def.position.set(position);
		def.type = BodyType.StaticBody;
		
		body = world.createBody(def);
		body.setUserData(this);
		
		angle = (float)Math.toRadians(270 - barrier.player.getAngle());
		
		PolygonShape ps = new PolygonShape();
		ps.setAsBox(15 / GameState.UNIT_SCALE, 5 / GameState.UNIT_SCALE, new Vector2(0, 0), angle);
		
		Fixture f = body.createFixture(ps, 1);
		f.setUserData(this);
		
		ps.dispose();
		
		barrierTex = new Texture("imgs/shift/barrier.png");
	}
	
	boolean canRemove = false;
	
	public void takeDamage(float damage){
		hitTimer = 1;
		life -= damage;
		
		if(life <= 0){
			canRemove = true;
			if(GameState.SFX)
			destroy.play(0.5f);
		}
		else{
			if(GameState.SFX)
			hit.play(0.5f, (float)Math.random()*0.1f + 0.95f, 0);
		}
	}
	
	public boolean render(SpriteBatch sb){
		sb.setProjectionMatrix(barrier.player.getState().getCamera().combined);
		
		sb.begin();
		sb.setColor(1, 1 - hitTimer, 1- hitTimer, 1);
		sb.draw(barrierTex,
				body.getWorldCenter().x - barrierTex.getWidth()/2 / GameState.UNIT_SCALE,
				body.getWorldCenter().y - barrierTex.getHeight()/2 / GameState.UNIT_SCALE,
				barrierTex.getWidth()/2 /GameState.UNIT_SCALE,
				barrierTex.getHeight()/2 /GameState.UNIT_SCALE,
				barrierTex.getWidth() /GameState.UNIT_SCALE,
				barrierTex.getHeight() /GameState.UNIT_SCALE,
				1,
				1, 
				(float)Math.toDegrees(angle),
				0,
				0,
				barrierTex.getWidth(),
				barrierTex.getHeight(),
				false,
				false);
		
		sb.end();
		sb.setColor(1, 1, 1, 1);
		
		return canRemove;
	}
	
	public void update(double delta){
		
		hitTimer -= delta;
		if(hitTimer < 0) hitTimer = 0;
		
	}

}
