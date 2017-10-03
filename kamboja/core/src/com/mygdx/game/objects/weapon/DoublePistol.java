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
				shoot.play(0.6f, (float)Math.random()*dif + (1f - dif/2f), 0);
				shootTimer = 0;
				BodyDef def = new BodyDef();
				def.bullet = true;
				def.type = BodyType.DynamicBody;
				def.linearDamping = 0;
				if(side == 1){
					//TODO: tirar instancia nova a cada frame
					def.position.set(
							getPlayer().getBody().getWorldCenter().x + (float)Math.cos(Math.toRadians(-getPlayer().getAngle() + 18)) * (5 / GameState.UNIT_SCALE),
							getPlayer().getBody().getWorldCenter().y + (float)Math.sin(Math.toRadians(-getPlayer().getAngle() + 18)) * (5 / GameState.UNIT_SCALE));
				}
				else{
					def.position.set(
							getPlayer().getBody().getWorldCenter().x + (float)Math.cos(Math.toRadians(-getPlayer().getAngle() - 10)) * (5 / GameState.UNIT_SCALE),
							getPlayer().getBody().getWorldCenter().y + (float)Math.sin(Math.toRadians(-getPlayer().getAngle() - 10)) * (5 / GameState.UNIT_SCALE));
				}
				if(!Float.isNaN(def.position.x) && !Float.isNaN(def.position.y)){
				float vel = 30;
				float rnd = (float) Math.random() - 0.5f;
				Vector2 direction = new Vector2((float)Math.sin(Math.toRadians(getPlayer().getShootingAngle() + 90 + rnd*PRECISION + rnd*botPrecision)) * vel, (float)Math.cos(Math.toRadians(getPlayer().getShootingAngle() + 90 + rnd*PRECISION + rnd*botPrecision)) * vel);
				def.linearVelocity.set(direction);
				
				Body bul = world.createBody(def);
				
				CircleShape shape = new CircleShape();
				shape.setRadius(radius/GameState.UNIT_SCALE);
				
				Fixture f = bul.createFixture(shape, 1);
				f.setSensor(true);
				
				shape.dispose();
				
				Bullet bullet = new Bullet(bul, getPlayer().getID(), DAMAGE * getPlayer().getAtk(), radius, getPlayer());
				
				f.setUserData(bullet);
				bul.setUserData(bullet);
				
				player.getState().addBullet(bullet);
				
				side *= -1;
				}
				else{
					System.out.println(getPlayer().getBody().getWorldCenter() + ", " + getClass().getSimpleName());
				}
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
