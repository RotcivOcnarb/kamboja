package com.mygdx.game.objects.weapon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.objects.Bullet;
import com.mygdx.game.objects.Player;
import com.mygdx.game.states.GameState;

public class Shotgun extends Weapon{
	
	Texture pistol;
		
	float shootTimer = 0;
	Texture bullet;
	
	public static float DAMAGE;
	public static float PRECISION;
	public static float WEIGHT;
	
	//damage 5.2
	//precision 0
	
	static Sound shoot;
	
	static{
		shoot = Gdx.audio.newSound(Gdx.files.internal("audio/weapon/shotgun.ogg"));
	}

	public void dispose(){
		pistol.dispose();
		bullet.dispose();
	}

	public Shotgun(World world, Player player) {
		super(world, player);
		pistol = new Texture("Weapons/In-game/sss.png");
		bullet = new Texture("imgs/weapons/bullet.png");
	}
	
	public void render(SpriteBatch sb) {
		sb.begin();
		renderTexture(sb, pistol);
		
		sb.end();
	}

	public void update(float delta) {
		shootTimer += delta;
		
		if(analog > 0.7){
			if(shootTimer > 1f){
				if(GameState.SFX)
				shoot.play(0.7f);
				
				knockback(1.5f);
				screenshake(0.3f);
				
				player.getState().showShell(player.getPosition(), player.getAngleVector().cpy().scl(1, -1).scl(0.05f).rotate90(1));

				
				for(float i = -2; i < 3; i += 0.5f){
					float radius = 2;
					
					
					
					shootTimer = 0;
					BodyDef def = new BodyDef();
					def.bullet = true;
					def.type = BodyType.DynamicBody;
					def.linearDamping = 20f;
					def.position.set(
							getPlayer().getBody().getWorldCenter().x + (float)Math.cos(Math.toRadians(-getPlayer().getAngle())) * (5 / GameState.UNIT_SCALE),
							getPlayer().getBody().getWorldCenter().y + (float)Math.sin(Math.toRadians(-getPlayer().getAngle())) * (5 / GameState.UNIT_SCALE));
					
					float vel = (float) (50 + Math.random() * 5);
					float opening = (float) (10 + Math.random() * 3);
					float rnd = (float) (Math.random() - 0.5f);
					Vector2 direction = new Vector2((float)Math.sin(Math.toRadians(getPlayer().getShootingAngle() + 90 + i*opening + rnd*PRECISION + rnd*botPrecision)) * vel, (float)Math.cos(Math.toRadians(getPlayer().getShootingAngle() + 90 + i*opening + rnd*PRECISION + rnd*botPrecision)) * vel);
					def.linearVelocity.set(direction);
					if(!Float.isNaN(def.position.x) && !Float.isNaN(def.position.y)){
					Body bul = world.createBody(def);
					
					CircleShape shape = new CircleShape();
					shape.setRadius(radius/GameState.UNIT_SCALE);
					
					Fixture f = bul.createFixture(shape, 1);
					f.setSensor(true);
					
					shape.dispose();
					
					Bullet bullet = new Bullet(bul, getPlayer().getID(), DAMAGE * getPlayer().getAtk(), radius, getPlayer());
					bullet.setTexture(this.bullet);
					
					f.setUserData(bullet);
					bul.setUserData(bullet);
					
					player.getState().addBullet(bullet);
					}
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
