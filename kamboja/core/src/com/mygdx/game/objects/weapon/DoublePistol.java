package com.mygdx.game.objects.weapon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.objects.Bullet;
import com.mygdx.game.objects.players.Player;
import com.mygdx.game.states.GameState;

public class DoublePistol extends Weapon{
	
	Texture pistol;
		
	float shootTimer = 0;
	
	int side = 1;
	
	public static float DAMAGE;
	public static float PRECISION;
	public static float WEIGHT;
	
	static Sound shoot;
	static{
		shoot = Gdx.audio.newSound(Gdx.files.internal("audio/weapon/pistol.ogg"));
	}
	//damage 6.5
	
	public void dispose(){
		pistol.dispose();
	}
	
	public DoublePistol(World world, Player player) {
		super(world, player);
		pistol = new Texture("Weapons/In-game/Taurus Akimbo.png");
	}
	
	public void render(SpriteBatch sb) {
		sb.begin();
		renderTexture(sb, pistol);		
		sb.end();
		
	}
	
	float timer;

	public void update(float delta) {
		shootTimer += delta;
		timer += delta*50;
		
		if(analog > 0.7){
			if(shootTimer > 0.25f){
				
				knockback(0.3f);
				screenshake(0.07f);
				
				float radius = 2;
				float dif = 0.05f;
				if(GameState.SFX)
				shoot.play(0.6f * GameState.VOLUME, (float)Math.random()*dif + (1f - dif/2f), 0);
				shootTimer = 0;
				
				Vector2 bulletPos = new Vector2();
				
				if(side == 1){
					player.getState().showShell(player.getPosition(), player.getAngleVector().cpy().scl(1, -1).scl(0.05f).rotate90(1));
					bulletPos.set(
							getPlayer().getBody().getWorldCenter().x + (float)Math.cos(Math.toRadians(-getPlayer().getAngle() + 18)) * (5 / GameState.UNIT_SCALE),
							getPlayer().getBody().getWorldCenter().y + (float)Math.sin(Math.toRadians(-getPlayer().getAngle() + 18)) * (5 / GameState.UNIT_SCALE));
				}
				else{
					player.getState().showShell(player.getPosition(), player.getAngleVector().cpy().scl(1, -1).scl(0.05f).rotate90(-1));

					bulletPos.set(
							getPlayer().getBody().getWorldCenter().x + (float)Math.cos(Math.toRadians(-getPlayer().getAngle() - 10)) * (5 / GameState.UNIT_SCALE),
							getPlayer().getBody().getWorldCenter().y + (float)Math.sin(Math.toRadians(-getPlayer().getAngle() - 10)) * (5 / GameState.UNIT_SCALE));
				}
				
				float vel = 30;
				float rnd = (float) Math.random() - 0.5f;
				Vector2 direction = new Vector2((float)Math.sin(Math.toRadians(getPlayer().getShootingAngle() + 90 + rnd*PRECISION + rnd*botPrecision)) * vel, (float)Math.cos(Math.toRadians(getPlayer().getShootingAngle() + 90 + rnd*PRECISION + rnd*botPrecision)) * vel);
				
				Bullet bullet = new Bullet(world, bulletPos, direction, getPlayer().getId(), DAMAGE * getPlayer().getAtk(), radius, getPlayer());
				
				player.getState().addBullet(bullet);
				
				side *= -1;

			}
		}
	}

	@Override
	public void botShoot() {
		analog = 0.8f;
		botPrecision = 0f;
	}

	@Override
	public void endSound() {
		// TODO Auto-generated method stub
		
	}

}
