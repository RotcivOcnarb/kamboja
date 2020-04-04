package com.mygdx.game.objects.weapon;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.objects.BazookaBullet;
import com.mygdx.game.objects.players.Player;
import com.mygdx.game.states.GameState;

public class Bazooka extends Weapon{
	
	Texture pistol;

	static Texture bulletBazooka;
		
	float globalTimer = 0;
	
	public static float DAMAGE;
	public static float PRECISION;
	public static float WEIGHT;
	
	static Sound torpedo[] = new Sound[2];
	static{
		torpedo[0] = Gdx.audio.newSound(Gdx.files.internal("audio/weapon/torpedo1.ogg"));
		torpedo[1] = Gdx.audio.newSound(Gdx.files.internal("audio/weapon/torpedo2.ogg"));
	}
	float shootTimer = 0;
	//damage 13
	//precision 3
	
	public void dispose(){
		pistol.dispose();
		
	}
	
	public Bazooka(World world, Player player) {
		super(world, player);
		pistol = new Texture("Weapons/In-game/Bazooka.png");

		
		if(bulletBazooka == null){
			bulletBazooka = new Texture("Weapons/baz.png");
		}
	}

	public void render(SpriteBatch sb) {
		sb.begin();
		renderTexture(sb, pistol);
		
		sb.end();
	}

	public void update(float delta) {
		shootTimer += delta;
		globalTimer += delta;
		
		if(analog > 0.7){
			if(shootTimer > 1.2f){
				float radius = 2;
				knockback(0.7f);
				screenshake(0.4f);
				
				if(GameState.SFX)
				torpedo[(int)(Math.random()*2f)].play(0.5f * GameState.VOLUME);
								
				shootTimer = 0;
				Vector2 position = new Vector2(
						getPlayer().getBody().getWorldCenter().x + (float)Math.cos(Math.toRadians(-getPlayer().getAngle())) * (20 / GameState.UNIT_SCALE),
						getPlayer().getBody().getWorldCenter().y + (float)Math.sin(Math.toRadians(-getPlayer().getAngle())) * (20 / GameState.UNIT_SCALE));
				float rnd = (float) Math.random() - 0.5f;
				float vel =  1;
				Vector2 direction = new Vector2((float)Math.sin(Math.toRadians(getPlayer().getShootingAngle() + 90 + rnd*PRECISION + Math.sin(globalTimer)*botPrecision)) * vel, (float)Math.cos(Math.toRadians(getPlayer().getShootingAngle() + 90 + rnd*PRECISION + Math.sin(globalTimer)*botPrecision)) * vel);
				
				BazookaBullet bullet = new BazookaBullet(world, position, direction.scl(15), getPlayer().getId(), DAMAGE * getPlayer().getAtk(), radius, getPlayer(), 0, true);
				bullet.setTexture(bulletBazooka);
				
				player.getState().addBullet(bullet);

			}
		}
	}

	public void botShoot() {
		analog = 0.8f;
		botPrecision = 0;
	}

	@Override
	public void endSound() {
		// TODO Auto-generated method stub
		
	}

}
