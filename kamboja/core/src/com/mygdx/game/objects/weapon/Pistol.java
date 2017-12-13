package com.mygdx.game.objects.weapon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.objects.Bullet;
import com.mygdx.game.objects.Player;
import com.mygdx.game.states.GameState;

public class Pistol extends Weapon{
	
	Texture pistol;
	
	float shootTimer = 0;
	
	public static float DAMAGE;
	public static float PRECISION;
	public static float WEIGHT;
	
	static Sound shoot;
	static{
		shoot = Gdx.audio.newSound(Gdx.files.internal("audio/weapon/pistol.ogg"));
	}

	//precision 0
	//damage 13;
	
	public void dispose(){
		pistol.dispose();
	}

	public Pistol(World world, Player player) {
		super(world, player);
		pistol = new Texture("Weapons/In-game/Taurus.png");
	}
	
	public void render(SpriteBatch sb) {
		sb.begin();
		renderTexture(sb, pistol);
		
		sb.end();
	}

	public void update(float delta) {
		shootTimer += delta;
		
		if(analog > 0.7){
			if(shootTimer > 0.5f){
				
				knockback(0.3f);
				screenshake(0.07f);
				
				player.getState().showShell(player.getPosition(), player.getAngleVector().cpy().scl(1, -1).scl(0.05f).rotate90(1));
				
				float dif = 0.05f;
				if(GameState.SFX)
				shoot.play(0.6f * GameState.VOLUME, (float)Math.random()*dif + (1f - dif/2f), 0);
				
				float radius = 5;
				
				shootTimer = 0;
				Vector2 position = new Vector2(
					getPlayer().getBody().getWorldCenter().x + (float)Math.cos(Math.toRadians(-getPlayer().getAngle())) * (5 / GameState.UNIT_SCALE),
						getPlayer().getBody().getWorldCenter().y + (float)Math.sin(Math.toRadians(-getPlayer().getAngle())) * (5 / GameState.UNIT_SCALE));
				float rnd = (float) Math.random() - 0.5f;
				Vector2 direction = new Vector2((float)Math.sin(Math.toRadians(getPlayer().getShootingAngle() + 90 + rnd*PRECISION + rnd*botPrecision)) * 30, (float)Math.cos(Math.toRadians(getPlayer().getShootingAngle() + 90 + rnd*PRECISION + rnd*botPrecision)) * 30);

				Bullet bullet = new Bullet(world, position, direction, getPlayer().getId(), DAMAGE * getPlayer().getAtk(), radius, getPlayer());

				player.getState().addBullet(bullet);

			}
		}
	}


	@Override
	public void botShoot() {
		analog = 0.8f;
		botPrecision = 0;
	}

	@Override
	public void endSound() {
		// TODO Auto-generated method stub
		
	}

}
