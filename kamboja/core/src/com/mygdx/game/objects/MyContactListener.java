package com.mygdx.game.objects;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.game.objects.Player.PlayerFall;
import com.mygdx.game.objects.map.Asteroid;
import com.mygdx.game.objects.map.Block;
import com.mygdx.game.objects.map.BreakableBlock;
import com.mygdx.game.objects.map.HoleBlock;
import com.mygdx.game.objects.map.UnbreakableBlock;
import com.mygdx.game.objects.shift.BarrierObject;
import com.mygdx.game.objects.shift.TurretObject;
import com.mygdx.game.objects.weapon.Flamethrower;
import com.mygdx.game.objects.weapon.Laser;
import com.mygdx.game.states.GameState;
import com.mygdx.game.states.GameState.PlayerSpike;

public class MyContactListener implements ContactListener{
	
	public void beginContact(Contact contact) {
		
		//Player com acidGlue
		if(contact.getFixtureA().getBody().getUserData() instanceof Player &&
				contact.getFixtureB().getUserData() instanceof AcidGlue) {
			
			AcidGlue ag = (AcidGlue) contact.getFixtureB().getUserData();
			Player p = (Player) contact.getFixtureA().getBody().getUserData();
			
			if(!ag.player.equals(p) && !p.stepping.contains(ag)) {
				p.stepping.add(ag);
			}
			
		}
		if(contact.getFixtureB().getBody().getUserData() instanceof Player &&
				contact.getFixtureA().getUserData() instanceof AcidGlue) {
			
			AcidGlue ag = (AcidGlue) contact.getFixtureA().getUserData();
			Player p = (Player) contact.getFixtureB().getBody().getUserData();
			
			if(!ag.player.equals(p) && !p.stepping.contains(ag)) {
				p.stepping.add(ag);
			}
			
		}
		
		//Player com espínho
		if(
				contact.getFixtureA().getUserData() instanceof PlayerSpike &&
				!contact.getFixtureB().isSensor() &&
				contact.getFixtureB().getBody().getUserData() instanceof Player &&
				!contact.getFixtureA().getBody().equals(contact.getFixtureB().getBody())) {
			
			Player p1 = (Player)contact.getFixtureA().getBody().getUserData();
			Player p2 = (Player)contact.getFixtureB().getBody().getUserData();
			if(p1.getEquipment().spike_level > 0)
			p2.takeDamage(Equipment.SPIKE_DAMAGE * p1.getEquipment().spike_level, p1, true);
			
		}
		if(
				contact.getFixtureB().getUserData() instanceof PlayerSpike &&
				!contact.getFixtureA().isSensor() &&
				contact.getFixtureA().getBody().getUserData() instanceof Player &&
				!contact.getFixtureB().getBody().equals(contact.getFixtureA().getBody())) {
			
			Player p1 = (Player)contact.getFixtureB().getBody().getUserData();
			Player p2 = (Player)contact.getFixtureA().getBody().getUserData();
			if(p1.getEquipment().spike_level > 0)
			p2.takeDamage(Equipment.SPIKE_DAMAGE * p1.getEquipment().spike_level, p1, true);
		}

		//player com buraco
		if(contact.getFixtureA().getUserData() instanceof PlayerFall && contact.getFixtureB().getUserData() instanceof HoleBlock){
			PlayerFall p = (PlayerFall) contact.getFixtureA().getUserData();
			p.player.setFalling();
		}
		if(contact.getFixtureB().getUserData() instanceof PlayerFall && contact.getFixtureA().getUserData() instanceof HoleBlock){
			PlayerFall p = (PlayerFall) contact.getFixtureB().getUserData();
			
			p.player.setFalling();
		}

		//qualquer objeto com range do laser
		if(contact.getFixtureA().getUserData() instanceof Laser){
			Laser l = (Laser) contact.getFixtureA().getUserData();
			if(!(contact.getFixtureB().getUserData() instanceof Laser))
			l.addRange(contact.getFixtureB().getBody());
		}
		if(contact.getFixtureB().getUserData() instanceof Laser){
			Laser l = (Laser) contact.getFixtureB().getUserData();
			if(!(contact.getFixtureA().getUserData() instanceof Laser))
			l.addRange(contact.getFixtureA().getBody());
		}
		
		//tiro com turret
		if(contact.getFixtureA().getUserData() instanceof Bullet && contact.getFixtureB().getUserData() instanceof TurretObject){
			
			Bullet bullet = (Bullet)contact.getFixtureA().getUserData();
			TurretObject to = (TurretObject) contact.getFixtureB().getUserData();
			
			if(!bullet.player.equals(to.turret.player)){
				
				if(bullet instanceof BazookaBullet){
					((BazookaBullet)bullet).explosionDamage();
				}
				
				to.takeDamage(bullet.getDamage());
				bullet.remove();
				GameState.removeBody(contact.getFixtureA().getBody());
			}
			else{
				contact.setEnabled(false);
			}
			
			
			
		}
		if(contact.getFixtureB().getUserData() instanceof Bullet && contact.getFixtureA().getUserData() instanceof TurretObject){
			
			Bullet bullet = (Bullet)contact.getFixtureB().getUserData();
			TurretObject to = (TurretObject) contact.getFixtureA().getUserData();
			
			if(!bullet.player.equals(to.turret.player)){
				
				if(bullet instanceof BazookaBullet){
					((BazookaBullet)bullet).explosionDamage();
				}
				
				to.takeDamage(bullet.getDamage());
				bullet.remove();
				GameState.removeBody(contact.getFixtureB().getBody());
			}
			else{
				contact.setEnabled(false);
			}
			
			
			
		}
		
		//tiro com barrier
				if(contact.getFixtureA().getUserData() instanceof Bullet && contact.getFixtureB().getUserData() instanceof BarrierObject){
					
					Bullet bullet = (Bullet)contact.getFixtureA().getUserData();
					BarrierObject to = (BarrierObject) contact.getFixtureB().getUserData();
					
					if(!bullet.player.equals(to.barrier.player)){
						if(bullet instanceof BazookaBullet){
							((BazookaBullet)bullet).explosionDamage();
						}
						to.takeDamage(bullet.getDamage());
						bullet.remove();
						GameState.removeBody(contact.getFixtureA().getBody());
					}
					else{
						contact.setEnabled(false);
					}
					
					
					
				}
				if(contact.getFixtureB().getUserData() instanceof Bullet && contact.getFixtureA().getUserData() instanceof BarrierObject){
					
					Bullet bullet = (Bullet)contact.getFixtureB().getUserData();
					BarrierObject to = (BarrierObject) contact.getFixtureA().getUserData();
					
					if(!bullet.player.equals(to.barrier.player)){
						if(bullet instanceof BazookaBullet){
							((BazookaBullet)bullet).explosionDamage();
						}
						to.takeDamage(bullet.getDamage());
						bullet.remove();
						GameState.removeBody(contact.getFixtureB().getBody());
					}
					else{
						contact.setEnabled(false);
					}
					
					
					
				}
		
		
		//tiro com parede
		if(contact.getFixtureA().getUserData() instanceof Bullet && contact.getFixtureB().getBody().getUserData() != null &&
				contact.getFixtureB().getBody().getUserData().equals("BLOCK")){
			Bullet b = (Bullet) contact.getFixtureA().getUserData();
			
			if(b instanceof BazookaBullet){
				((BazookaBullet)b).explosionDamage();
			}
			
			b.remove();
			GameState.removeBody(contact.getFixtureB().getBody());
		}
		
		if(contact.getFixtureB().getUserData() instanceof Bullet &&
				contact.getFixtureA().getBody().getUserData() != null && contact.getFixtureA().getBody().getUserData().equals("BLOCK")){
			Bullet b = (Bullet) contact.getFixtureB().getUserData();
			
			if(b instanceof BazookaBullet){
				((BazookaBullet)b).explosionDamage();
			}
			
			b.remove();
			GameState.removeBody(contact.getFixtureB().getBody());
		}
		
		//player com item
		if(contact.getFixtureA().getUserData() instanceof Player && contact.getFixtureB().getUserData() instanceof Item){
			Item it = (Item) contact.getFixtureB().getUserData();
			Player pl = (Player) contact.getFixtureA().getBody().getUserData();
			
			pl.setBuff(it.id);
			it.remove();
		}
		
		if(contact.getFixtureB().getUserData() instanceof Player && contact.getFixtureA().getUserData() instanceof Item){
			Item it = (Item) contact.getFixtureA().getUserData();
			Player pl = (Player) contact.getFixtureB().getBody().getUserData();
			
			pl.setBuff(it.id);
			it.remove();
		}
		
		
		//bala atinge inimigo
		if(contact.getFixtureA().getUserData() instanceof Player && contact.getFixtureB().getUserData() instanceof Bullet){
			Player player = (Player) contact.getFixtureA().getUserData();
			Bullet b = (Bullet) contact.getFixtureB().getUserData();
			
			if(b.getID() != player.getId()){
				if(!player.isDead()){
					player.takeDamage(b.getDamage(), b.getPlayer(), true);
					
					if(b instanceof BazookaBullet){
						((BazookaBullet)b).explosionDamage();
					}
					b.remove();
					GameState.removeBody(contact.getFixtureB().getBody());
				}
				else{
					contact.setEnabled(false);
					return;
				}
				
				
			}
			
		}
		if(contact.getFixtureB().getUserData() instanceof Player && contact.getFixtureA().getUserData() instanceof Bullet){
			Player player = (Player) contact.getFixtureB().getUserData();
			Bullet b = (Bullet) contact.getFixtureA().getUserData();
			
			if(b.getID() != player.getId()){
				if(!player.isDead()){
					player.takeDamage(b.getDamage(), b.getPlayer(), true);
					
					if(b instanceof BazookaBullet){
						((BazookaBullet)b).explosionDamage();
					}

					b.remove();
					GameState.removeBody(contact.getFixtureA().getBody());
				}
				else{
					contact.setEnabled(false);
					return;
				}
				
			}
		}
		
		//bala atinge parede
		if(contact.getFixtureA().getUserData() instanceof Block && contact.getFixtureB().getUserData() instanceof Bullet){
			Block block = (Block) contact.getFixtureA().getUserData();
			Bullet bullet = (Bullet) contact.getFixtureB().getUserData();
			
			if(bullet instanceof BazookaBullet && (block instanceof BreakableBlock || block instanceof UnbreakableBlock)){
				((BazookaBullet)bullet).explosionDamage();
			}
			
			block.bulletCollided(contact, bullet);
			
		}
		if(contact.getFixtureB().getUserData() instanceof Block && contact.getFixtureA().getUserData() instanceof Bullet){
			Block block = (Block) contact.getFixtureB().getUserData();
			Bullet bullet = (Bullet) contact.getFixtureA().getUserData();
			
			if(bullet instanceof BazookaBullet && (block instanceof BreakableBlock || block instanceof UnbreakableBlock)){
				((BazookaBullet)bullet).explosionDamage();
			}
			
			block.bulletCollided(contact, bullet);
			
		}
		if(contact.getFixtureB().getBody().getUserData() instanceof Asteroid) {
			if(contact.getFixtureA().getUserData() instanceof Bullet) {
				Asteroid a = (Asteroid)contact.getFixtureB().getBody().getUserData();
				Bullet bullet = (Bullet) contact.getFixtureA().getUserData();
				
				a.destroy();
				
				bullet.remove();
				GameState.removeBody(bullet.getBody());
			}
		}
		
		if(contact.getFixtureA().getBody().getUserData() instanceof Asteroid) {
			if(contact.getFixtureB().getUserData() instanceof Bullet) {
				Asteroid a = (Asteroid)contact.getFixtureA().getBody().getUserData();
				Bullet bullet = (Bullet) contact.getFixtureB().getUserData();
				
				a.destroy();
				
				bullet.remove();
				GameState.removeBody(bullet.getBody());
			}
		}
	}

	public void endContact(Contact contact) {
		
		//qualquer objeto com range do laser
				if(contact.getFixtureA().getUserData() instanceof Laser){
					Laser l = (Laser) contact.getFixtureA().getUserData();
					l.removeRange(contact.getFixtureB().getBody());
				}
				if(contact.getFixtureB().getUserData() instanceof Laser){
					Laser l = (Laser) contact.getFixtureB().getUserData();
					l.removeRange(contact.getFixtureA().getBody());
				}
		
		//Player com acidGlue
				if(contact.getFixtureA().getBody().getUserData() instanceof Player &&
						contact.getFixtureB().getUserData() instanceof AcidGlue) {
					
					AcidGlue ag = (AcidGlue) contact.getFixtureB().getUserData();
					Player p = (Player) contact.getFixtureA().getBody().getUserData();
					
					if(!ag.player.equals(p) && p.stepping.contains(ag)) {
						p.stepping.remove(ag);
					}
					
				}
				if(contact.getFixtureB().getBody().getUserData() instanceof Player &&
						contact.getFixtureA().getUserData() instanceof AcidGlue) {
					
					AcidGlue ag = (AcidGlue) contact.getFixtureA().getUserData();
					Player p = (Player) contact.getFixtureB().getBody().getUserData();
					
					if(!ag.player.equals(p) && p.stepping.contains(ag)) {
						p.stepping.remove(ag);
					}
					
				}
				
				
	}

	public void preSolve(Contact contact, Manifold oldManifold) {
		if(contact.getFixtureA().getBody().getUserData() instanceof Asteroid) {
			if(!(contact.getFixtureB().getBody().getUserData() instanceof Player)) {
				contact.setEnabled(false);
			}
		}
		if(contact.getFixtureB().getBody().getUserData() instanceof Asteroid) {
			if(!(contact.getFixtureA().getBody().getUserData() instanceof Player)) {
				contact.setEnabled(false);
			}

		}
		
		//flamethrower atinge algo
		if(contact.getFixtureA().getUserData() instanceof Flamethrower.FlameParticle){
			contact.setEnabled(false);
			
			if(contact.getFixtureB().getUserData() instanceof Player){
				Flamethrower.FlameParticle flame = (Flamethrower.FlameParticle) contact.getFixtureA().getUserData();
				Player pl = (Player) contact.getFixtureB().getUserData();
				
				if(flame.ft.getPlayer().getId() != pl.getId()){
					pl.applyFlame(flame.ft.getPlayer().getAtk(), flame.ft.getPlayer());
				}
				
			}
			
			if(contact.getFixtureB().getUserData() instanceof Block){
				Flamethrower.FlameParticle flame = (Flamethrower.FlameParticle) contact.getFixtureA().getUserData();
				Block pl = (Block) contact.getFixtureB().getUserData();
				
				if(pl instanceof BreakableBlock){
					((BreakableBlock)pl).takeDamage(flame.ft.getDamage(), true);
				}
				
				if(pl instanceof UnbreakableBlock)
				flame.remove();
				
			}
			
			if(contact.getFixtureB().getUserData() instanceof TurretObject){
				Flamethrower.FlameParticle flame = (Flamethrower.FlameParticle) contact.getFixtureA().getUserData();
				TurretObject pl = (TurretObject) contact.getFixtureB().getUserData();
				
				if(flame.ft.getPlayer().getId() != pl.turret.player.getId()){
					pl.takeDamage(flame.ft.getDamage());
				}
			}
			
			if(contact.getFixtureB().getUserData() instanceof BarrierObject){
				Flamethrower.FlameParticle flame = (Flamethrower.FlameParticle) contact.getFixtureA().getUserData();
				BarrierObject pl = (BarrierObject) contact.getFixtureB().getUserData();
				
				if(flame.ft.getPlayer().getId() != pl.barrier.player.getId()){
					pl.takeDamage(flame.ft.getDamage());
				}
				
				flame.remove();
			}
			
		}
		if(contact.getFixtureB().getUserData() instanceof Flamethrower.FlameParticle){
			contact.setEnabled(false);
			
			if(contact.getFixtureA().getUserData() instanceof Player){
				Flamethrower.FlameParticle flame = (Flamethrower.FlameParticle) contact.getFixtureB().getUserData();
				Player pl = (Player) contact.getFixtureA().getUserData();
				
				if(flame.ft.getPlayer().getId() != pl.getId()){
					pl.applyFlame(flame.ft.getPlayer().getAtk(), flame.ft.getPlayer());
				}
			
			}
			
			if(contact.getFixtureA().getUserData() instanceof Block){
				Flamethrower.FlameParticle flame = (Flamethrower.FlameParticle) contact.getFixtureB().getUserData();
				Block pl = (Block) contact.getFixtureA().getUserData();
				
				if(pl instanceof BreakableBlock){
					((BreakableBlock)pl).takeDamage(flame.ft.getDamage(), true);
				}
				
				if(pl instanceof UnbreakableBlock)
				flame.remove();
			}
			
			if(contact.getFixtureA().getUserData() instanceof TurretObject){
				Flamethrower.FlameParticle flame = (Flamethrower.FlameParticle) contact.getFixtureB().getUserData();
				TurretObject pl = (TurretObject) contact.getFixtureA().getUserData();
				
				if(flame.ft.getPlayer().getId() != pl.turret.player.getId()){
					pl.takeDamage(flame.ft.getDamage());
				}
			}
			
			if(contact.getFixtureA().getUserData() instanceof BarrierObject){
				Flamethrower.FlameParticle flame = (Flamethrower.FlameParticle) contact.getFixtureB().getUserData();
				BarrierObject pl = (BarrierObject) contact.getFixtureA().getUserData();
				
				if(flame.ft.getPlayer().getId() != pl.barrier.player.getId()){
					pl.takeDamage(flame.ft.getDamage());
				}
				
				flame.remove();
			}
		}

	}

	public void postSolve(Contact contact, ContactImpulse impulse) {
		
	}

}
