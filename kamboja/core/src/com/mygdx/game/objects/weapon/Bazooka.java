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
import com.mygdx.game.objects.BazookaBullet;
import com.mygdx.game.objects.Player;
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
			if(shootTimer > 1.5f){
				float radius = 2;
				
				knockback(0.7f);
				screenshake(0.4f);
				
				if(GameState.SFX)
				torpedo[(int)(Math.random()*2f)].play(0.5f * GameState.VOLUME);
				
				shootTimer = 0;
				BodyDef def = new BodyDef();
				def.bullet = true;
				def.type = BodyType.DynamicBody;
				def.linearDamping = 0;
				def.position.set(
						getPlayer().getBody().getWorldCenter().x + (float)Math.cos(Math.toRadians(-getPlayer().getAngle())) * (20 / GameState.UNIT_SCALE),
						getPlayer().getBody().getWorldCenter().y + (float)Math.sin(Math.toRadians(-getPlayer().getAngle())) * (20 / GameState.UNIT_SCALE));
				float rnd = (float) Math.random() - 0.5f;
				float vel =  1;
				Vector2 direction = new Vector2((float)Math.sin(Math.toRadians(getPlayer().getShootingAngle() + 90 + rnd*PRECISION + Math.sin(globalTimer)*botPrecision)) * vel, (float)Math.cos(Math.toRadians(getPlayer().getShootingAngle() + 90 + rnd*PRECISION + Math.sin(globalTimer)*botPrecision)) * vel);
				def.linearVelocity.set(direction);
				if(!Float.isNaN(def.position.x) && !Float.isNaN(def.position.y)){
				Body bul = world.createBody(def);
				
				CircleShape shape = new CircleShape();
				shape.setRadius(radius/GameState.UNIT_SCALE);
				
				Fixture f = bul.createFixture(shape, 1);
				f.setSensor(true);
				
				shape.dispose();
				
				BazookaBullet bullet = new BazookaBullet(bul, getPlayer().getId(), DAMAGE * getPlayer().getAtk(), radius, getPlayer());
				bullet.setTexture(bulletBazooka);
				
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

	public void botShoot() {
		analog = 0.8f;
		botPrecision = 0;
	}

	@Override
	public void endSound() {
		// TODO Auto-generated method stub
		
	}

}
