package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.states.GameState;

public class Bullet {
	
	int id;
	public Body body;
	float damage;
	
	static Texture defaultBulletTex;
	static Texture muzzleTex;
	
	Texture bulletTex;
	
	Animation<TextureRegion> bulletAnimation;
	float radius;
	protected boolean canRemove = false;
	public Player player;
	
	TrailRenderer trail;
	
	static {
		defaultBulletTex = new Texture("imgs/weapons/bullet.png");
		muzzleTex = new Texture("imgs/weapons/muzzle.png");
	}
	
	boolean destroyed = false;
	boolean disposed = false;
	
	float globalTime = 0;
	
	public boolean canRemove(){
		return canRemove;
	}
	
	public void dispose(){
		//not disposing bullet texture because its not created, but passed as reference
		//tex.dispose();
		trail.dispose();
		disposed = true;
	}
	
	public Bullet(Body body, int id, float damage, float radius, Player player){
		this.body = body;
		this.id = id;
		this.radius = radius;
		this.damage = damage;
		this.player = player;
		
		bulletTex = defaultBulletTex;
		
		bulletAnimation = new Animation<TextureRegion>(1/50f, new TextureRegion(muzzleTex), new TextureRegion(bulletTex));

		trail = new TrailRenderer(body, 100, 3 / GameState.UNIT_SCALE);
		switch(id){
		case 0:
			trail.setColor(0, 0, 1, 0.5f);
			break;
		case 1:
			trail.setColor(1, 0, 0, 0.5f);
			break;
		case 2:
			trail.setColor(0, 1, 0, 0.5f);
			break;
		case 3:
			trail.setColor(1, 1, 0, 0.5f);
		}
		
		destroyed = false;
	}
	
	public void setTexture(Texture tex){
		bulletTex = tex;
		bulletAnimation = new Animation<TextureRegion>(1/10f, new TextureRegion(muzzleTex), new TextureRegion(bulletTex));
	}

	public void remove(){
		destroyed = true;
	}
	
	
	
	public boolean render(SpriteBatch sb){
		if(!disposed){
		trail.renderTrail(sb, !destroyed);
		
		globalTime += Gdx.graphics.getDeltaTime();
		
		Texture tex = bulletAnimation.getKeyFrame(globalTime).getTexture();
		
		if(!destroyed){
			sb.begin();
			sb.setProjectionMatrix(player.getState().getCamera().combined);
			sb.draw(tex,
					body.getWorldCenter().x - tex.getWidth()/2 / GameState.UNIT_SCALE,
					body.getWorldCenter().y - tex.getHeight()/2 / GameState.UNIT_SCALE,
					tex.getWidth()/2 /GameState.UNIT_SCALE,
					tex.getHeight()/2 /GameState.UNIT_SCALE,
					tex.getWidth() /GameState.UNIT_SCALE,
					tex.getHeight() /GameState.UNIT_SCALE,
					0.5f, 0.5f, body.getLinearVelocity().angle() - 90, 0, 0, tex.getWidth(), tex.getHeight(), false, false);
			sb.end();
		}
		
		if(body.getLinearVelocity().len2() < 0.01f){
			canRemove = true;
		}
		
		if(destroyed){
			if(trail.finished()){
				canRemove = true;
			}
		}
		}
		return canRemove;
	}
	
	public int getID(){
		return id;
	}
	
	public float getDamage(){
		return damage;
	}

	public Player getPlayer() {
		return player;
	}

	public Body getBody() {
		return body;
	}

}
