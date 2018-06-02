package com.mygdx.game.objects;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.KambojaMain;
import com.mygdx.game.states.GameState;

public class Equipment {
	
	public static final float DRONE_DAMAGE = 5;
	public static final float BOMB_DAMAGE = 5;
	public static final float SPIKE_DAMAGE = 1;
	public static final float ACID_DAMAGE = 5;
	
	public static final int EQUIP_SIZE = 9;
	
	BitmapFont font;
	Player player;
	//ShapeRenderer sr;
	float globalTimer = 0;
	
	//Drone
	float drone_distance = 40 / GameState.UNIT_SCALE;
	float shootTimer = 0;
	int indexShooting = 0;
	float shootDuration = 1;
	ArrayList<Vector2> drone_positions;
	Texture drone_tex;
	
	//Bomba
	int bombs = 0;
	Texture bomb_tex;
	float bomb_timer = 0;
	
	//Espinho
	int spike_level = 0;
	Texture spikes_tex;
	
	//acid & glue
	public int acid = 0;
	public int glue = 0;
	float acidglueTimer = 0;
	
	//other
	int spd = 0;
	int life = 0;
	int atk = 0;
	int def = 0;
	
	public Equipment(Player player) {
		this.player = player;
		drone_tex = new Texture("Weapons/Drone.png");
		drone_positions = new ArrayList<Vector2>();
		bomb_tex = new Texture("Weapons/bomb.png");
		spikes_tex = new Texture("Weapons/spikes.png");
		
		font = KambojaMain.getFont("fonts/olivers barney.ttf");
	}
	
	public void addDrone() {
		drone_positions.add(player.getPosition().cpy());
	}
	
	public int getDrone() {
		return drone_positions.size();
	}
	
	public void addBomb() {
		bombs ++;
	}
	
	public int getBomb() {
		return bombs;
	}
	
	public void addSpikes() {
		spike_level ++;
	}
	
	public int getSpikes() {
		return spike_level;
	}
	
	public void addGlue() {
		glue++;
	}
	
	public int getGlue() {
		return glue;
	}
	
	public void addAcid() {
		acid++;
	}
	
	public int getAcid() {
		return acid;
	}
	
	public void addLife() {
		life++;
	}
	
	public int getLife() {
		return life;
	}
	
	public void addSpeed() {
		spd++;
	}
	
	public int getSpeed() {
		return spd;
	}
	
	public void addAtk() {
		atk++;
	}
	
	public int getAttack() {
		return atk;
	}
	
	public void addDef() {
		def++;
	}
	
	public int getDeffense() {
		return def;
	}
	
	public void renderUI(int position, SpriteBatch sb) {
		
		
		float xoff = 0;
		float y = 0;
		
		if(position == 1 || position == 2)
		xoff = 1920 - 32;
		
		switch(position) {
		case 0:
			y = 920;
			break;
		case 1:
			y = 920;
			break;
		case 2:
			y = 100;
			break;
		case 3:
			y = 100;
			break;
		}
		
		for(int e = 0; e < 9; e ++) {
			switch(e) {
			case 0: //Drone
				if(player.getEquipment().getDrone() > 0) {
					if(position == 0 || position == 3)
					xoff += 64;
					else
					xoff -= 64;
					
					sb.draw(KambojaMain.getTexture("Weapons/Drone.png"), xoff, y, 64, 64);
					font.draw(sb, player.getEquipment().getDrone() + "", xoff, y);
				}
				break;
			case 1: //Bomba
				if(player.getEquipment().getBomb() > 0) {
					if(position == 0 || position == 3)
						xoff += 64;
						else
						xoff -= 64;
					
					sb.draw(KambojaMain.getTexture("Weapons/bomb.png"), xoff, y, 64, 64);
					font.draw(sb, player.getEquipment().getBomb() + "", xoff, y);
				}
				break;
			case 2: //Espinho
				if(player.getEquipment().getSpikes() > 0) {
					if(position == 0 || position == 3)
						xoff += 64;
						else
						xoff -= 64;
					
					sb.draw(KambojaMain.getTexture("Weapons/Icon/spike_icon.png"), xoff, y, 64, 64);
					font.draw(sb, player.getEquipment().getSpikes() + "", xoff, y);
				}
				break;
			case 3: //Acid
				if(player.getEquipment().getAcid() > 0) {
					if(position == 0 || position == 3)
						xoff += 64;
						else
						xoff -= 64;
					
					sb.draw(KambojaMain.getTexture("Weapons/Icon/acid_icon.png"), xoff, y, 64, 64);
					font.draw(sb, player.getEquipment().getAcid() + "", xoff, y);
				}
				break;
			case 4: //Glue
				if(player.getEquipment().getGlue() > 0) {
					if(position == 0 || position == 3)
						xoff += 64;
						else
						xoff -= 64;
					
					sb.draw(KambojaMain.getTexture("Weapons/Icon/glue_icon.png"), xoff, y, 64, 64);
					font.draw(sb, player.getEquipment().getGlue() + "", xoff, y);
				}
				break;
			case 5: //Speed
				if(player.getEquipment().getSpeed() > 0) {
					if(position == 0 || position == 3)
						xoff += 64;
						else
						xoff -= 64;
					
					sb.draw(KambojaMain.getTexture("imgs/speed.png"), xoff, y, 64, 64);
					font.draw(sb, player.getEquipment().getSpeed() + "", xoff, y);
				}
				break;
			case 6: //Life
				if(player.getEquipment().getLife() > 0) {
					if(position == 0 || position == 3)
						xoff += 64;
						else
						xoff -= 64;
					
					sb.draw(KambojaMain.getTexture("imgs/heart.png"), xoff, y, 64, 64);
					font.draw(sb, player.getEquipment().getLife() + "", xoff, y);
				}
				break;
			case 7: //Attack
				if(player.getEquipment().getAttack() > 0) {
					if(position == 0 || position == 3)
						xoff += 64;
						else
						xoff -= 64;
					
					sb.draw(KambojaMain.getTexture("imgs/attack.png"), xoff, y, 64, 64);
					font.draw(sb, player.getEquipment().getAttack() + "", xoff, y);
				}
				break;
			case 8: //Deffense
				if(player.getEquipment().getDeffense() > 0) {
					if(position == 0 || position == 3)
						xoff += 64;
						else
						xoff -= 64;
					
					sb.draw(KambojaMain.getTexture("imgs/shield.png"), xoff, y, 64, 64);
					font.draw(sb, player.getEquipment().getDeffense() + "", xoff, y);
				}
				break;
			}
		}
	}
	
	private void throwBomb() {
		Vector2 direction = new Vector2((float)Math.sin(Math.toRadians(player.getShootingAngle() + 90)) * 40, (float)Math.cos(Math.toRadians(player.getShootingAngle() + 90)) * 40);
		
		BazookaBullet bullet = new BazookaBullet(player.getBody().getWorld(), player.getPosition(), direction, player.getId(), BOMB_DAMAGE * player.getAtk() * bombs, 5, player, 20, false);
		bullet.setTexture(bomb_tex);
		
		player.getState().addBullet(bullet);
	}
	
	public void render(SpriteBatch sb) {

		sb.begin();
		
		float angle = 0;
		for(int j = 0; j < drone_positions.size(); j ++) {
			int i = drone_positions.size() - j - 1;
			angle += 360/drone_positions.size();
			
			Vector2 d_pos = player.getPosition().cpy().add(
					(float)Math.cos(Math.toRadians(angle + globalTimer*100)) * drone_distance,
					(float)Math.sin(Math.toRadians(angle + globalTimer*100)) * drone_distance
					);
			
			drone_positions.get(i).add(d_pos.cpy().sub(drone_positions.get(i)).scl(1/15f));
			
			sb.draw(
					drone_tex,
					drone_positions.get(i).x - drone_tex.getWidth()/2f /GameState.UNIT_SCALE,
					drone_positions.get(i).y - drone_tex.getHeight()/2f /GameState.UNIT_SCALE,
					drone_tex.getWidth()/2f/GameState.UNIT_SCALE,
					drone_tex.getHeight()/2f/GameState.UNIT_SCALE,
					drone_tex.getWidth() /GameState.UNIT_SCALE,
					drone_tex.getHeight() /GameState.UNIT_SCALE,
					0.7f,
					0.7f,
					-globalTimer*50,
					0,
					0,
					drone_tex.getWidth(),
					drone_tex.getHeight(),
					false,
					false
					);
			
			
		}
		
		
		
		
		if(spike_level > 0) {
			sb.draw(
					spikes_tex,
					player.getPosition().x - spikes_tex.getWidth()/2f /GameState.UNIT_SCALE,
					player.getPosition().y - spikes_tex.getHeight()/2f /GameState.UNIT_SCALE,
					spikes_tex.getWidth()/2f/GameState.UNIT_SCALE,
					spikes_tex.getHeight()/2f/GameState.UNIT_SCALE,
					spikes_tex.getWidth() /GameState.UNIT_SCALE,
					spikes_tex.getHeight() /GameState.UNIT_SCALE,
					1,
					1,
					globalTimer*50*spike_level,
					0,
					0,
					spikes_tex.getWidth(),
					spikes_tex.getHeight(),
					false,
					false
					);
		}
		
		sb.end();
		
		
	}
	
	public void update(float delta) {
		globalTimer += delta;
		if(acid > 0 || glue > 0)
		acidglueTimer += delta;
		
		if(drone_positions.size() > 0)
		shootTimer += delta;
		
		if(bombs > 0 && player.getWeapon().analog > 0.4)
		bomb_timer += delta;
		
		if(bomb_timer > 3 * Math.pow(0.9f, bombs)) {
			bomb_timer -= 3 * Math.pow(0.9f, bombs);
			throwBomb();
		}
		
		if(acidglueTimer > 0.1f) {
			acidglueTimer -= 0.1f;
			
			player.getState().addAcidGlue(player);
		}
		
		if(shootTimer > shootDuration / drone_positions.size()) {
			shootTimer -= shootDuration / drone_positions.size();
			
			indexShooting ++;
			if(indexShooting == drone_positions.size()) indexShooting = 0;
			
			
			Player closest = null;
			for(Player p : player.getState().getPlayers()) {
				if(p != player) {
					
					if(closest == null && !p.isDead()) {
						closest = p;
					}
					else {
						if(closest == null) continue;
						if(player.getPosition().cpy().sub(p.getPosition()).len2() < player.getPosition().cpy().sub(closest.getPosition()).len2() && !p.isDead()) {
							closest = p;
						}
					}
					
				}
			}
			
			if(closest != null) {
				Bullet bullet = new Bullet(
					player.getBody().getWorld(),
					drone_positions.get(indexShooting),
					closest.getPosition().cpy().sub(player.getPosition()).nor().scl(30), player.getId(), DRONE_DAMAGE * player.getAtk(), 5, player);
				player.getState().addBullet(bullet);
			}
		}
		
	}

	
	
}
