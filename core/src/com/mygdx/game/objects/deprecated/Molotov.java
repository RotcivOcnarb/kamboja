package com.mygdx.game.objects.deprecated;

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
import com.mygdx.game.objects.weapon.Weapon;
import com.mygdx.game.states.GameState;

public class Molotov extends Weapon{
	
	Texture pistol;
		
	float shootTimer = 0;
	
	Texture bul;
	
	public static float DAMAGE;
	public static float PRECISION;
	public static float WEIGHT;

	//damage 10
	// precision 0;
	

	public void dispose(){
		pistol.dispose();
		bul.dispose();
	}
	

	public Molotov(World world, Player player) {
		super(world, player);
		pistol = new Texture("Weapons/In-game/molotov.png");
		bul = new Texture("Weapons/Icon/Molotov_bullet.png");
	}
	
	public void render(SpriteBatch sb) {
		sb.begin();
		renderTexture(sb, pistol);
		
		
		sb.end();
	}

	public void update(float delta) {
		shootTimer += delta;
		
		if(analog > 0.7){
			if(shootTimer > 0.8f){
				float radius = 4;
				
				shootTimer = 0;
				BodyDef def = new BodyDef();
				def.bullet = true;
				def.type = BodyType.DynamicBody;
				def.linearDamping = 8f;
				def.position.set(
						getPlayer().getBody().getWorldCenter().x + (float)Math.cos(Math.toRadians(-getPlayer().getAngle())) * (20 / GameState.UNIT_SCALE),
						getPlayer().getBody().getWorldCenter().y + (float)Math.sin(Math.toRadians(-getPlayer().getAngle())) * (20 / GameState.UNIT_SCALE));
				
				float rnd = (float) (Math.random() - 0.5f);
				Vector2 direction = new Vector2((float)Math.sin(Math.toRadians(getPlayer().getShootingAngle() + 90 + rnd*PRECISION + rnd*botPrecision)) * 15,
						(float)Math.cos(Math.toRadians(getPlayer().getShootingAngle() + 90 + rnd*PRECISION + rnd*botPrecision)) * 15);
				def.linearVelocity.set(direction);
				if(!Float.isNaN(def.position.x) && !Float.isNaN(def.position.y)){
				Body bul = world.createBody(def);
				
				CircleShape shape = new CircleShape();
				shape.setRadius(radius/GameState.UNIT_SCALE);
				
				Fixture f = bul.createFixture(shape, 1);
				f.setSensor(true);
				
				shape.dispose();
				
				Bullet bullet = new Bullet(bul, getPlayer().getID(), DAMAGE * getPlayer().getAtk(), radius, getPlayer());
				bullet.setTexture(this.bul);
				
				f.setUserData(bullet);
				bul.setUserData(bullet);
				
				player.getState().addBullet(bullet);
				}
				else{
					System.out.println(getPlayer().getBody().getWorldCenter() + ", " + getClass().getSimpleName());
				}
			}
		}
	}

	public void shoot() {
		
		
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
