package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.objects.map.Block;
import com.mygdx.game.objects.shift.Barrier;
import com.mygdx.game.objects.shift.BarrierObject;
import com.mygdx.game.objects.shift.Turret;
import com.mygdx.game.objects.shift.TurretObject;
import com.mygdx.game.states.GameState;

public class BazookaBullet extends Bullet{
	
	ParticleEffect smoke;
	static Sound explosion;
	static{
		explosion = Gdx.audio.newSound(Gdx.files.internal("audio/weapon/explosion.ogg"));
	}

	public BazookaBullet(Body body, int id, float damage, float radius, Player player) {
		super(body, id, damage, radius, player);
		

		smoke = new ParticleEffect();
		smoke.load(Gdx.files.internal("particles/smoke.par"), Gdx.files.internal("particles"));
		smoke.scaleEffect(1f/GameState.UNIT_SCALE / 2f);
		smoke.start();

	}
	
	float expShake = 0;
	
	public void screenshake(float amount){
		player.getState().screenshake(amount);
	}
	
	public void dispose(){
		super.dispose();
		smoke.dispose();
	}
	
	public void explosionDamage(){
		if(GameState.SFX)
		explosion.play(0.5f);
		
		player.getState().showExplosion(body.getPosition());
		
		expShake = 1;
		
		float radius = 60 / GameState.UNIT_SCALE;
		
		for(Player p : player.getState().getPlayers()){
			if(!p.equals(player)){
				if(p.getPosition().cpy().sub(body.getPosition()).len2() < radius * radius){
					p.takeDamage(getDamage() * player.getAtk(), player);
				}
				
				if(p.getShift() instanceof Turret){
					for(TurretObject to : ((Turret)p.getShift()).turrets){
						if(to.body.getWorldCenter().cpy().sub(body.getPosition()).len2() < radius * radius){
							to.takeDamage(getDamage() * player.getAtk());
						}
					}
				}
				else if(p.getShift() instanceof Barrier){
					for(BarrierObject to : ((Barrier)p.getShift()).objects){
						if(to.body.getWorldCenter().cpy().sub(body.getPosition()).len2() < radius * radius){
							to.takeDamage(getDamage() * player.getAtk());
						}
					}
				}
			}
		}
		
		for(Block b : player.getState().getBlocks()){
			if(b.getBody().getWorldCenter().cpy().sub(body.getPosition()).len2() < radius * radius){
					b.takeDamage(getDamage() * player.getAtk(), false);
			}

		}
		
		//lista de blocos
		
		//dar dano aos blocos
	}
	
	public boolean render(SpriteBatch sb){
		
		body.applyForceToCenter(body.getLinearVelocity().cpy().nor().scl(0.2f), true);
		if(!disposed)
		trail.renderTrail(sb, !destroyed);
		
		globalTime += Gdx.graphics.getDeltaTime();
		
		Texture tex = bulletAnimation.getKeyFrame(globalTime).getTexture();
		
		expShake -= Gdx.graphics.getDeltaTime();
		if(expShake <= 0) expShake = 0;
		
		screenshake(expShake/5f);
		
		
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
					1, 1, body.getLinearVelocity().angle() - 90, 0, 0, tex.getWidth(), tex.getHeight(), false, false);
			
			sb.end();
		}
		
		smoke.setPosition(body.getWorldCenter().x, body.getWorldCenter().y);
		
		if(smoke.isComplete()){
			smoke.reset();
		}
		sb.begin();
		smoke.draw(sb);
		smoke.update(Gdx.graphics.getDeltaTime());
		sb.end();

		if(body.getLinearVelocity().len2() < 0.01f){
			canRemove = true;
		}
		
		if(destroyed){
			if(trail.finished()){
				canRemove = true;
			}
		}
		
		return canRemove;
	}

}
