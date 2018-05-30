package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.objects.map.Block;
import com.mygdx.game.objects.shift.Barrier;
import com.mygdx.game.objects.shift.BarrierObject;
import com.mygdx.game.objects.shift.Turret;
import com.mygdx.game.objects.shift.TurretObject;
import com.mygdx.game.states.GameState;

public class BazookaBullet extends Bullet{
	
	ParticleEffect smoke;
	boolean follow = false;
	static Sound explosion;
	static{
		explosion = Gdx.audio.newSound(Gdx.files.internal("audio/weapon/explosion.ogg"));
	}

	public BazookaBullet(World world, Vector2 position, Vector2 direction, int id, float damage, float radius, Player player, float linearDamping, boolean follow) {
		super(world, position, direction, id, damage, radius, player, linearDamping);
		this.follow = follow;
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
		explosion.play(0.5f * GameState.VOLUME);
		
		player.getState().showExplosion(body.getPosition());
		
		expShake = 1;
		
		float radius = 60 / GameState.UNIT_SCALE;
		
		for(Player p : player.getState().getPlayers()){
			if(!p.equals(player)){
				if(p.getPosition().cpy().sub(body.getPosition()).len2() < radius * radius){
					p.takeDamage(getDamage() * player.getAtk(), player, true);
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
	
	float force = 0;
	
	public boolean render(SpriteBatch sb){
		if(body.getLinearVelocity().len2() < 0.01f && !canRemove){
			canRemove = true;
			explosionDamage();
		}
		
		super.render(sb);

		if(follow) {
		Player closestP = null;
		float closestD = 1000;
		
		for(int i = getPlayer().getState().getPlayers().size() - 1; i >= 0; i --) {
			if(!getPlayer().getState().getPlayers().get(i).equals(getPlayer())) {
				float dist = getPlayer().getState().getPlayers().get(i).getPosition().cpy().sub(body.getWorldCenter()).len();
				if(dist < closestD) {
					if(!getPlayer().getState().getPlayers().get(i).isDead()) {
						closestD = dist;
						closestP = getPlayer().getState().getPlayers().get(i);
					}
				}
			}
		}
		
		if(closestP != null) {
			System.out.println(closestP.getPosition().cpy().sub(body.getWorldCenter().cpy()));
			body.applyForceToCenter(
							closestP.getPosition().cpy().sub(
									body.getWorldCenter().cpy()
									).nor().scl(force), true
							
					);
		}
		
		}
		
		force += 0.001f;
		
		screenshake(expShake/5f);
		
		expShake -= Gdx.graphics.getDeltaTime();
		if(expShake <= 0) expShake = 0;

		smoke.setPosition(body.getWorldCenter().x, body.getWorldCenter().y);
		
		if(smoke.isComplete()){
			smoke.reset();
		}
		sb.begin();
		smoke.draw(sb);
		smoke.update(Gdx.graphics.getDeltaTime());
		sb.end();

		return canRemove;
	}

}
