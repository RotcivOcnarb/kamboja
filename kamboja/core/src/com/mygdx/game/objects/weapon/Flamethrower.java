package com.mygdx.game.objects.weapon;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.mygdx.game.objects.Player;
import com.mygdx.game.states.GameState;

public class Flamethrower extends Weapon{
	
	Texture pistol;
	Texture flameParticle;
	
	//ParticleEffect flames;
		
	public static float DAMAGE;
	public static float PRECISION;
	public static float WEIGHT;
	
	// damage 0.5
	//precision useless
	
	ArrayList<FlameParticle> particles;
	
	static Music flamethrower;
	
	
	float particleTimer = 0;
	
	public class FlameParticle{
		Body body;
		public Flamethrower ft;
		float maxVel = 0;
		public FlameParticle(Flamethrower ft){
			this.ft = ft;
			float rand = (float) Math.sin(Math.random()*Math.PI*2) * 30f/2f;
			
			BodyDef def = new BodyDef();
			def.type = BodyType.DynamicBody;
			def.linearDamping = 5;
			def.position.set(player.getPosition().cpy().add(
					(float)Math.sin(Math.toRadians(player.getAngle() + 90)) * (30 / GameState.UNIT_SCALE),
					(float)Math.cos(Math.toRadians(player.getAngle() + 90)) * (30 / GameState.UNIT_SCALE)
					));
			def.linearVelocity.set(
					(float)Math.sin(Math.toRadians(player.getAngle() + 90 + rand)) * (float)(Math.random() * 5 + 5),
					(float)Math.cos(Math.toRadians(player.getAngle() + 90 + rand)) * (float)(Math.random() * 5 + 5)
					);
			maxVel = def.linearVelocity.len();
			body = world.createBody(def);
			
			CircleShape cs = new CircleShape();
			cs.setRadius(3 / GameState.UNIT_SCALE);
			
			Fixture f = body.createFixture(cs, 1);
			//f.setSensor(true);
			
			cs.dispose();
			
			f.setUserData(this);
			body.setUserData(this);
		}
		
		boolean canRemove = false;
		
		public boolean render(SpriteBatch sb, Texture tex){
			sb.setColor(1, 1, 1, body.getLinearVelocity().len()/maxVel);
			sb.draw(tex,
					body.getWorldCenter().x - 8f/GameState.UNIT_SCALE,
					body.getWorldCenter().y - 8f/GameState.UNIT_SCALE,
					16f/GameState.UNIT_SCALE, 16f/GameState.UNIT_SCALE);
			
			if(body.getLinearVelocity().len2() < 0.01){
				canRemove = true;
			}
			
			return canRemove;
		}
		
		public void remove(){
			
			canRemove = true;
			
		}
	}
	
	public void dispose(){
		pistol.dispose();
		//flames.dispose();
		flamethrower.dispose();
	}

	public Flamethrower(World world, Player player) {
		super(world, player);
		pistol = new Texture("Weapons/In-game/flahme.png");
		
		flameParticle = new Texture("particles/fire.png");
		flamethrower = Gdx.audio.newMusic(Gdx.files.internal("audio/weapon/flamethrower.ogg"));
		flamethrower.setLooping(true);
		
		particles = new ArrayList<Flamethrower.FlameParticle>();
	}

	
	public void createParticle(){
		if(particleTimer < 0){
			particleTimer = 0.01f;
			particles.add(new FlameParticle(this));
		}
	}
	
	public float getDamage(){
		return DAMAGE * getPlayer().getAtk();
	}

	public void render(SpriteBatch sb) {
		sb.begin();
		renderTexture(sb, pistol);		
		sb.end();
		
		sb.begin();
		for(int i = particles.size() -1; i >= 0; i --){
			if(particles.get(i).render(sb, flameParticle)){
				GameState.removeBody(particles.get(i).body);
				particles.remove(i);
			}
		}
		sb.end();
		
		player.setSprintCooldown(9);
		
		
	}


	public void update(float delta) {

		particleTimer -= delta;
		
		if(analog > 0.7){
			
			createParticle();
			
			try{
			if(GameState.SFX)
			flamethrower.play();
			}
			catch(GdxRuntimeException e){
			}

		}
		else{
			flamethrower.stop();
		}
				
		if(player.getState().isEnd()){
			flamethrower.stop();
			analog = 0;
		}
	}

	public void botShoot() {
		analog = 0.8f;
	}

	@Override
	public void endSound() {
		flamethrower.stop();
	}

}
