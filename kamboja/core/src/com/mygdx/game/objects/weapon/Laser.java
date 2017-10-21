package com.mygdx.game.objects.weapon;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.objects.Player;
import com.mygdx.game.objects.map.Block;
import com.mygdx.game.objects.map.BreakableBlock;
import com.mygdx.game.objects.map.HoleBlock;
import com.mygdx.game.objects.map.UnbreakableBlock;
import com.mygdx.game.objects.map.WaterBlock;
import com.mygdx.game.objects.shift.BarrierObject;
import com.mygdx.game.objects.shift.TurretObject;
import com.mygdx.game.states.GameState;

public class Laser extends Weapon{
	
	Texture pistol;
	
	RayCastCallback callback;
	QueryCallback qc;
	
	ShapeRenderer sr;
	
	float shootTimer = 0;
	
	float closestFraction = 1;
	Fixture closestFixture;
	Vector2 p1, p2, collision;
	
	static Sound laser;
	static{
		laser = Gdx.audio.newSound(Gdx.files.internal("audio/weapon/laser.ogg"));

	}
	
	public static float DAMAGE;
	public static float PRECISION;
	public static float WEIGHT;
	//damage 10
	//precision useless
	
	ArrayList<Body> inRange;
	ArrayList<Vector2> points;
	
	public void dispose(){
		pistol.dispose();
		sr.dispose();
	}
	
	public void addRange(Body b){
		if(!inRange.contains(b)){
			inRange.add(b);
		}
	}
	
	public void removeRange(Body b){
		if(inRange.contains(b)){
			inRange.remove(b);
		}
	}

	public Laser(World world, final Player player) {
		super(world, player);
		pistol = new Texture("Weapons/In-game/Laser.png");
		
		p1 = new Vector2();
		p2 = new Vector2();
		collision = new Vector2();
		
		inRange = new ArrayList<Body>();
		points = new ArrayList<Vector2>();
		
		sr = new ShapeRenderer();
		
		float distance = 50;
		float range = 1;
		
		PolygonShape s = new PolygonShape();
		s.setAsBox(distance/2f, range/2f, new Vector2(distance/2, 0), 0);
		
		Fixture laseraim = player.getBody().createFixture(s, 0);
		laseraim.setSensor(true);
		laseraim.setUserData(this);
		
		qc = new QueryCallback() {
			
			public boolean reportFixture(Fixture fixture) {
				if(fixture.getUserData() instanceof BreakableBlock ||
						fixture.getUserData() instanceof UnbreakableBlock){
					stop = fixture;
					return false;
				}
				
				return true;
			}
		};

		
		callback = new RayCastCallback() {
			
			public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
				
				if(fixture.getUserData() instanceof Player){
					Player pl = (Player) fixture.getUserData();
					if(pl.getID() != player.getID()){
						if(fraction < closestFraction){
							closestFraction = fraction;
							closestFixture = fixture;
							collision.set(point.cpy());
						}
						return 1;
					}
					else{
						return -1;
					}
					
				}
				else if(fixture.getUserData() instanceof Block){
					if(fixture.getUserData() instanceof HoleBlock || fixture.getUserData() instanceof WaterBlock){
						return -1;
					}
					else{
						if(fraction < closestFraction){
							closestFraction = fraction;
							closestFixture = fixture;
							collision.set(point.cpy());
						}
						return 1;
					}
					
				}
				else if(fixture.getBody().getUserData() != null && fixture.getBody().getUserData().equals("BLOCK")){
					if(fraction < closestFraction){
						closestFraction = fraction;
						closestFixture = fixture;
						collision.set(point.cpy());
					}
					return 1;
				}
				else if(fixture.getUserData() instanceof BarrierObject){
					if(fraction < closestFraction){
						closestFraction = fraction;
						closestFixture = fixture;
						collision.set(point.cpy());
					}
					return 1;
				}
				else if(fixture.getUserData() instanceof TurretObject){
					if(fraction < closestFraction){
						closestFraction = fraction;
						closestFixture = fixture;
						collision.set(point.cpy());
					}
					return 1;
				}
				
				else{
					return -1;
				}
				}

			
		};
	}

	private float getDamage() {
		return DAMAGE;
	}
	
	float opacity = 0;
	
	ArrayList<Vector2> laserPoints = new ArrayList<Vector2>();
	
	public void render(SpriteBatch sb) {
		sb.begin();
		renderTexture(sb, pistol);
		sb.end();
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		sr.setProjectionMatrix(sb.getProjectionMatrix());
		
		if(!player.isDead()){
		sr.begin(ShapeType.Filled);
		
		laserPoints.clear();
		
		for(int i = 0; i < points.size(); i ++){
			laserPoints.add(points.get(i).cpy().add(new Vector2(
					(float)(Math.sin(Math.random() * Math.PI*2) * (Math.random()*10 / GameState.UNIT_SCALE)),
					(float)(Math.cos(Math.random() * Math.PI*2) * (Math.random()*10 / GameState.UNIT_SCALE))
					)));
		}
		switch(player.getID()){
		case 0:
			sr.setColor(0, 0, 1, opacity);
			break;
		case 1:
			sr.setColor(1, 0, 0, opacity);
			break;
		case 2:
			sr.setColor(0, 1, 0, opacity);
			break;
		case 3:
			sr.setColor(1, 1, 0, opacity);
		}
		for(int i = 0; i < laserPoints.size()-1; i ++){
			sr.rectLine(laserPoints.get(i), laserPoints.get(i+1), (float)(Math.random()*3)/GameState.UNIT_SCALE);
		}

		sr.end();
		}
		Gdx.gl.glDisable(GL20.GL_BLEND);
		

	}

	Fixture stop;
	
	public void update(float delta) {
		shootTimer += delta;
		opacity -= delta;
		if(opacity < 0) opacity = 0;
		
		if(analog > 0.7){
			if(shootTimer > 1f){
				if(GameState.SFX)
				laser.play(GameState.VOLUME);
				opacity = 1;
				
				knockback(0.7f);
				screenshake(0.3f);
				
				points.clear();
				
				Body bodyplayer = null;
				//caso exista algum player em range, acha o mais próximo
				for(int i = 0; i < inRange.size(); i ++){
					if(inRange.get(i).getUserData() instanceof Player && inRange.get(i).getUserData() != this.getPlayer()){
						Player p = (Player) inRange.get(i).getUserData();
						if(bodyplayer == null) {
							if(!p.isDead())
							bodyplayer = inRange.get(i);
						}
						else{
							if(!p.isDead()) {
								float lastDist = bodyplayer.getWorldCenter().cpy().sub(this.getPlayer().getPosition().cpy()).len2();
								float currentDist = inRange.get(i).getWorldCenter().cpy().sub(this.getPlayer().getPosition().cpy()).len2();
								
								if(currentDist < lastDist){
									bodyplayer = inRange.get(i);
								}
							}
						}
					}
				}
				//caso não exista nenhum player no range, calcula o raycast normal
				if(bodyplayer == null){
					float rnd = (float) Math.random() - 0.5f;
					p1.set(getPlayer().getPosition().cpy().add(
							new Vector2(
									(float)Math.cos(Math.toRadians(-getPlayer().getAngle()))*(25/GameState.UNIT_SCALE),
									(float)Math.sin(Math.toRadians(-getPlayer().getAngle()))*(25/GameState.UNIT_SCALE)
									)
							)
							);
					p2.set(getPlayer().getPosition().cpy().add(
											new Vector2(
													(float)Math.cos(Math.toRadians(-getPlayer().getAngle() + rnd*PRECISION))*(1000),
													(float)Math.sin(Math.toRadians(-getPlayer().getAngle() + rnd*PRECISION))*(1000)
													)
											)
							);
					closestFraction = 1;
					
					world.rayCast(callback, p1.cpy(), p2.cpy());
					if(closestFixture != null){
						if(closestFixture.getUserData() instanceof Player){
							Player pl = (Player) closestFixture.getUserData();
							if(pl.getID() != player.getID()){
								pl.takeDamage(getDamage() * player.getAtk(), player, true);
							}
						}
						else if(closestFixture.getUserData() instanceof Block){
							Block bl = (Block)closestFixture.getUserData();
							bl.takeDamage(getDamage()*player.getAtk(), false);	
						}
						else if(closestFixture.getUserData() instanceof BarrierObject){
							BarrierObject bl = (BarrierObject)closestFixture.getUserData();
							bl.takeDamage(getDamage()*player.getAtk());	
						}
						else if(closestFixture.getUserData() instanceof TurretObject){
							TurretObject bl = (TurretObject)closestFixture.getUserData();
							bl.takeDamage(getDamage()*player.getAtk());	
						}
					}
					
					Vector2 startPoint = p1.cpy();
					float step = 2f / GameState.UNIT_SCALE;
					float distance = collision.cpy().sub(p1).len();
					Vector2 direction = collision.cpy().sub(p1).nor();
					points.clear();
					for(int i = 0; i < (distance/step) + 1; i ++){
						points.add(startPoint.cpy());
						startPoint.add(direction.cpy().scl(step));
					}
				}
				else{
					//caso exista algum player, calcula os pontos que se curvam em direção à esse player
					p1.set(getPlayer().getPosition().cpy().add(
							new Vector2(
									(float)Math.cos(Math.toRadians(-getPlayer().getAngle()))*(25/GameState.UNIT_SCALE),
									(float)Math.sin(Math.toRadians(-getPlayer().getAngle()))*(25/GameState.UNIT_SCALE)
									)
							)
							);
					
					Vector2 startPoint = p1.cpy();
					float step = 2f / GameState.UNIT_SCALE;
					float distance = bodyplayer.getPosition().cpy().sub(p1).len();
					Vector2 direction = bodyplayer.getPosition().cpy().sub(p1).nor();
					Vector2 currentFacing = player.getAngleVector().cpy().scl(1,  -1);
					points.clear();
					
					float dist = 10;

					int i = 0;
					while(dist > 0.1){
					//for(int i = 0; i < (distance/step) + 1; i ++){
						points.add(startPoint.cpy());
						stop = null;
						//ve se não tem nenhum bloco no caminhp
						world.QueryAABB(qc, startPoint.x, startPoint.y, startPoint.x, startPoint.y);
						
						//se não tiver, continua curvando
						if(stop == null){
						direction = bodyplayer.getPosition().cpy().sub(startPoint.cpy()).nor();
												
						if(i > (distance/step)/2)
						currentFacing.add(direction.cpy().sub(currentFacing.cpy()).scl(1/50f));
						else
						currentFacing.sub(direction.cpy().sub(currentFacing.cpy()).scl(1/300f));	
						
						startPoint.add(currentFacing.x * step, currentFacing.y * step);
						}
						else{
							//se tiver para aqui
							break;
						}
						dist = bodyplayer.getPosition().cpy().sub(startPoint).len();
						i++;
					}
					
					if(stop == null){//se não tiver nada no caminho da dano pro player
						Player p = (Player) bodyplayer.getUserData();
						p.takeDamage(getDamage() * player.getAtk(), player, true);
					}
					else{ //se tiver dá dano no bloco q está no caminho
						if(stop.getUserData() instanceof BreakableBlock){
							BreakableBlock bb = (BreakableBlock) stop.getUserData();
							bb.takeDamage(player.getAtk()* DAMAGE, false);
						}
					}
					
				}

				shootTimer = 0;
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
