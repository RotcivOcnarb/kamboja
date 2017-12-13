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

public class Mp5 extends Weapon{
	
	Texture pistol;
		
	float shootTimer = 0;
	
	float reload = 0;
	int bulletCount = 0;
	
	public static float DAMAGE;
	public static float PRECISION;
	public static float WEIGHT;
	
	static Sound shoot;
	
	static{
		shoot = Gdx.audio.newSound(Gdx.files.internal("audio/weapon/mp5.ogg"));
	}

	//precision 10;
	//damage 2;
	
	public void dispose(){
		pistol.dispose();
	}
	
	public Mp5(World world, Player player) {
		super(world, player);
		pistol = new Texture("Weapons/In-game/MP5.png");		

	}
	float globalTimer = 0;
	public void render(SpriteBatch sb) {
		sb.begin();
		renderTexture(sb, pistol);
		
		sb.end();
	}

	public void update(float delta) {
		shootTimer += delta;
		globalTimer += delta*3;
		reload += delta;
		if(analog > 0.7){
			if(shootTimer > 0.1f && reload > 0){
				float radius = 2;
				if(GameState.SFX)
				shoot.play(0.7f * GameState.VOLUME);
				
				knockback(0.3f);
				
				player.getState().showShell(player.getPosition(), player.getAngleVector().cpy().scl(1, -1).scl(0.05f).rotate90(1));

				
				shootTimer = 0;
				Vector2 position = new Vector2(
						getPlayer().getBody().getWorldCenter().x + (float)Math.cos(Math.toRadians(-getPlayer().getAngle())) * (5 / GameState.UNIT_SCALE),
						getPlayer().getBody().getWorldCenter().y + (float)Math.sin(Math.toRadians(-getPlayer().getAngle())) * (5 / GameState.UNIT_SCALE));
				float rnd = (float) Math.random() - 0.5f;
				float vel = 30;
				Vector2 direction = new Vector2((float)Math.sin(Math.toRadians(getPlayer().getShootingAngle() + 90 + rnd*PRECISION + Math.sin(globalTimer)*botPrecision)) * vel, (float)Math.cos(Math.toRadians(getPlayer().getShootingAngle() + 90 + rnd*PRECISION + Math.sin(globalTimer)*botPrecision)) * vel);

				Bullet bullet = new Bullet(world, position, direction, getPlayer().getId(), DAMAGE * getPlayer().getAtk(), radius, getPlayer());

				player.getState().addBullet(bullet);
					
				bulletCount ++;
				if(bulletCount > 40){
					bulletCount = 0;
					reload = -1f;
				}

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
