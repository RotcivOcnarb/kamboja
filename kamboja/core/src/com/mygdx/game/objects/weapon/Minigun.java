package com.mygdx.game.objects.weapon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.objects.Bullet;
import com.mygdx.game.objects.Player;
import com.mygdx.game.states.GameState;

public class Minigun extends Weapon{
	
	Texture pistol;
		
	float shootTimer = 0;
	
	float reload = 0;
	int bulletCount = 0;
	
	public static float DAMAGE;
	public static float PRECISION;
	public static float WEIGHT;
	
	//precision 3
	//damage 2
	
	static Music minigun;
	static Sound overheat;
	
	static{
		minigun = Gdx.audio.newMusic(Gdx.files.internal("audio/weapon/minigun.ogg"));
		overheat = Gdx.audio.newSound(Gdx.files.internal("audio/weapon/overheat.ogg"));
		minigun.setLooping(true);
		minigun.setVolume(0.2f);
	}

	public void dispose(){
		pistol.dispose();
	}

	public Minigun(World world, Player player) {
		super(world, player);
		pistol = new Texture("Weapons/In-game/Minigun.png");
		
		
	}
	
	public void render(SpriteBatch sb) {
		sb.begin();
		renderTexture(sb, pistol);
		
		sb.end();
	}
	
	boolean overheat_flag = false;
	float globalTimer = 0;
	
	public void update(float delta) {
		shootTimer += delta;
		globalTimer += delta*3;
		reload += delta;
		if(analog > 0.7){
			if(shootTimer > 0.05f && reload > 0){
				
				knockback(0.2f);
				screenshake(0.05f);
				
				player.getState().showShell(player.getPosition(), player.getAngleVector().cpy().scl(1, -1).scl(0.05f).rotate90(1));

				
				overheat_flag = false;
				try{
				if(GameState.SFX)
					minigun.setVolume(GameState.VOLUME * 0.2f);
				minigun.play();
				}
				catch(GdxRuntimeException e){
				}
				
				float radius = 2;
				
				shootTimer = 0;
				Vector2 position = new Vector2(
						getPlayer().getBody().getWorldCenter().x + (float)Math.cos(Math.toRadians(-getPlayer().getAngle())) * (15 / GameState.UNIT_SCALE),
						getPlayer().getBody().getWorldCenter().y + (float)Math.sin(Math.toRadians(-getPlayer().getAngle())) * (15 / GameState.UNIT_SCALE));
				float rnd = (float) Math.random() - 0.5f;
				float vel = 30;
				Vector2 direction = new Vector2((float)Math.sin(Math.toRadians(getPlayer().getShootingAngle() + 90 + rnd*PRECISION + Math.sin(globalTimer)*botPrecision)) * vel, (float)Math.cos(Math.toRadians(getPlayer().getShootingAngle() + 90 + rnd*PRECISION + Math.sin(globalTimer)*botPrecision)) * vel);
				Bullet bullet = new Bullet(world, position, direction, getPlayer().getId(), DAMAGE * getPlayer().getAtk(), radius, getPlayer());
				
				player.getState().addBullet(bullet);
					
				bulletCount ++;

				if(bulletCount > 15){
					bulletCount = 0;
					reload = -1f;
				}
			}
			else{
				if(reload < 0){
					if(!overheat_flag){
						if(GameState.SFX)
						overheat.play(GameState.VOLUME);
						overheat_flag = true;
					}
					minigun.stop();
				}
				
			}
		}
		else{
			minigun.stop();
		}
		
		if(player.getState().isEnd()){
			minigun.stop();
			analog = 0;
		}
	}

	@Override
	public void botShoot() {
		analog = 0.8f;
		botPrecision = 0;
	}

	@Override
	public void endSound() {
		minigun.stop();
		overheat.stop();
		
	}

}
