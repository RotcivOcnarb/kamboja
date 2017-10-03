package com.mygdx.game.objects.shift;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.objects.Player;
import com.mygdx.game.states.GameState;

public class Turret extends Shift{
	
	public ArrayList<TurretObject> turrets;
	
	float putTimer = 0;
	
	public static float RANGE = 60;
	public static float DAMAGE = 13;
	public static float LIFE = 20;
	
	Texture icon;
	
	public void dispose(){
		icon.dispose();
	}
	
	public Turret(Player player) {
		super(player);
		turrets = new ArrayList<TurretObject>();
		
		icon = new Texture("imgs/shift/turret.png");
	}

	public void render(SpriteBatch sb) {
		for(int i = turrets.size() - 1; i >= 0; i --){
			TurretObject to = turrets.get(i);
			if(to.render(sb)){
				GameState.removeBody(to.body);
				turrets.remove(to);
			}
		}
	}

	public void update(double delta) {
		for(int i = turrets.size() - 1; i >= 0; i --){
			TurretObject to = turrets.get(i);
			to.update(delta);
		}
		
		putTimer -= delta;
	}

	Vector2 temp = new Vector2();
	
	public void fire() {
		if(player.getMana() >= 30 & putTimer < 0){
			player.decreaseMana(30);
			putTimer = 1;
			temp.set(
					(float)Math.cos(Math.toRadians(player.getAngle()))*(10/GameState.UNIT_SCALE),
					(float)Math.sin(Math.toRadians(player.getAngle()))*(10/GameState.UNIT_SCALE)	
					);
			turrets.add(new TurretObject(this, player.getBody().getWorld(), player.getPosition().cpy().add(temp)));
		}
	}

	@Override
	public Texture getIcon() {
		return icon;
	}

}
