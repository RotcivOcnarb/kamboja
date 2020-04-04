package com.mygdx.game.objects.shift;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.objects.Bullet;
import com.mygdx.game.objects.players.Player;
import com.mygdx.game.states.GameState;

public class TurretObject {
	
	public Turret turret;

	public Body body;
	Texture turretTex;
	
	float hitTimer;
	
	ShapeRenderer sr;
	
	float shootTimer = 0;
		
	float radius;
	float targetRadius = Turret.RANGE;
	
	float life = Turret.LIFE;
	
	Sound hit, destroy, shoot;
	
	public void dispose(){
		hit.dispose();
		destroy.dispose();
		shoot.dispose();
		sr.dispose();
		turretTex.dispose();
	}
	
	public TurretObject(Turret turret, World world, Vector2 position) {
		this.turret = turret;
		
		hit = Gdx.audio.newSound(Gdx.files.internal("audio/shift/metal_hit.ogg"));
		destroy = Gdx.audio.newSound(Gdx.files.internal("audio/shift/turret_destroy.ogg"));
		shoot = Gdx.audio.newSound(Gdx.files.internal("audio/weapon/pistol.ogg"));
		
		BodyDef def = new BodyDef();
		def.position.set(position);
		def.type = BodyType.StaticBody;
		
		body = world.createBody(def);
		body.setUserData(this);
		
		CircleShape s = new CircleShape();
		s.setRadius(10f / GameState.UNIT_SCALE);
		
		Fixture f = body.createFixture(s, 1);
		f.setUserData(this);
		
		s.dispose();
		
		turretTex = new Texture("imgs/shift/turret.png");
		
		sr = new ShapeRenderer();
		
	}
	
	boolean canRemove = false;
	
	public void takeDamage(float damage){
		hitTimer = 1;
		life -= damage;
		
		if(life <= 0){
			canRemove = true;
			if(GameState.SFX)
			destroy.play(0.5f * GameState.VOLUME);
		}
		else{
			if(GameState.SFX)
			hit.play(0.5f * GameState.VOLUME, (float)Math.random()*0.1f + 0.95f, 0);
		}
	}

	public boolean render(SpriteBatch sb) {
		
		sb.setProjectionMatrix(turret.player.getState().getCamera().combined);

		Gdx.gl20.glEnable(GL20.GL_BLEND);
		
		sr.setProjectionMatrix(turret.player.getState().getCamera().combined);
		sr.begin(ShapeType.Filled);
			sr.setColor(1, 0, 0, 0.3f);
			sr.circle(body.getWorldCenter().x, body.getWorldCenter().y, radius / GameState.UNIT_SCALE, 20);
		sr.end();
		
		Gdx.gl20.glDisable(GL20.GL_BLEND);
		
		sb.begin();
		sb.setColor(1, 1 - hitTimer, 1- hitTimer, Math.min(1, radius));
		sb.draw(turretTex,
				body.getWorldCenter().x - turretTex.getWidth()/2 / GameState.UNIT_SCALE,
				body.getWorldCenter().y - turretTex.getHeight()/2 / GameState.UNIT_SCALE,
				turretTex.getWidth()/2 /GameState.UNIT_SCALE,
				turretTex.getHeight()/2 /GameState.UNIT_SCALE,
				turretTex.getWidth() /GameState.UNIT_SCALE,
				turretTex.getHeight() /GameState.UNIT_SCALE,
				1,
				1, 
				270 - body.getAngle(),
				0,
				0,
				turretTex.getWidth(),
				turretTex.getHeight(),
				false,
				false);
		
		sb.end();
		sb.setColor(1, 1, 1, 1);
		
		if(canRemove){
			targetRadius = 0;
			
			if(radius < 0.01){
				return true;
			}
			return false;
		}
		return false;
	}

	public void update(double delta) {
		
		hitTimer -= delta;
		
		if(hitTimer < 0) hitTimer = 0;
		
		Player closest = null;
		
		for(Player p : turret.player.getState().getPlayers()){
			if(!p.equals(turret.player)){
				if(closest == null){
					if(p.getPosition().cpy().sub(body.getWorldCenter()).len() < targetRadius / GameState.UNIT_SCALE){
						closest = p;
					}
				}
				else{
					if(p.getPosition().cpy().sub(body.getWorldCenter()).len() < closest.getPosition().cpy().sub(body.getWorldCenter()).len()){
						if(p.getPosition().cpy().sub(body.getWorldCenter()).len() < targetRadius / GameState.UNIT_SCALE){
							closest = p;
						}
					}
					
				}
			}
		}
		
		if(closest != null){
			body.setTransform(body.getWorldCenter(), 270 - closest.getPosition().cpy().sub(body.getWorldCenter()).angle() + 90 + (float)Math.random()*10f - 5);
		}
		
		radius += (targetRadius - radius)/5.0f;
		
		shootTimer += delta;
		
			if(shootTimer > 1f && closest != null && !canRemove){
				
				float dif = 0.05f;
				if(GameState.SFX)
				shoot.play(0.6f, (float)Math.random()*dif + (1f - dif/2f), 0);
				
				float radius = 4;
				
				shootTimer = 0;
				Vector2 position = new Vector2(
						body.getWorldCenter().x + (float)Math.cos(Math.toRadians(-body.getAngle())) * (20 / GameState.UNIT_SCALE),
						body.getWorldCenter().y + (float)Math.sin(Math.toRadians(-body.getAngle())) * (20 / GameState.UNIT_SCALE));
				Vector2 direction = new Vector2((float)Math.sin(Math.toRadians(body.getAngle() + 90)) * 30, (float)Math.cos(Math.toRadians(body.getAngle() + 90)) * 30);
				
				Bullet bullet = new Bullet(body.getWorld(), position, direction, turret.player.getId(), Turret.DAMAGE * turret.player.getAtk(), radius, turret.player);

				turret.player.getState().addBullet(bullet);
			}
		
	}

}
