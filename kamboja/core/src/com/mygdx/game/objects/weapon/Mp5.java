package com.mygdx.game.objects.weapon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
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
				shoot.play(0.7f);
				
				knockback(0.3f);
				
				shootTimer = 0;
				BodyDef def = new BodyDef();
				def.bullet = true;
				def.type = BodyType.DynamicBody;
				def.linearDamping = 0;
				def.position.set(
						getPlayer().getBody().getWorldCenter().x + (float)Math.cos(Math.toRadians(-getPlayer().getAngle())) * (5 / GameState.UNIT_SCALE),
						getPlayer().getBody().getWorldCenter().y + (float)Math.sin(Math.toRadians(-getPlayer().getAngle())) * (5 / GameState.UNIT_SCALE));
				float rnd = (float) Math.random() - 0.5f;
				float vel = 30;
				Vector2 direction = new Vector2((float)Math.sin(Math.toRadians(getPlayer().getShootingAngle() + 90 + rnd*PRECISION + Math.sin(globalTimer)*botPrecision)) * vel, (float)Math.cos(Math.toRadians(getPlayer().getShootingAngle() + 90 + rnd*PRECISION + Math.sin(globalTimer)*botPrecision)) * vel);
				def.linearVelocity.set(direction);
				if(!Float.isNaN(def.position.x) && !Float.isNaN(def.position.y)){
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
					
					bulletCount ++;
					if(bulletCount > 40){
						bulletCount = 0;
						reload = -1f;
					}
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
		botPrecision = 0;
	}

	@Override
	public void endSound() {
		// TODO Auto-generated method stub
		
	}

}
