package com.mygdx.game.objects.shift;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.objects.players.Player;
import com.mygdx.game.states.GameState;

public class Barrier extends Shift{
	
	public ArrayList<BarrierObject> objects;
	float putTimer = 0;
	
	public static float LIFE = 50;
	
	Texture icon;
	
	public void dispose(){
		icon.dispose();
	}
	
	public Barrier(Player player) {
		super(player);
		
		objects = new ArrayList<BarrierObject>();
		
		icon = new Texture("imgs/shift/barrier.png");
	}

	public void render(SpriteBatch sb) {
		
		for(int i = objects.size() - 1; i >= 0; i --){
			BarrierObject bo = objects.get(i);
			if(bo.render(sb)){
				GameState.removeBody(bo.body);
				objects.remove(bo);
			}
		}
		
	}

	public void update(double delta) {
		for(int i = objects.size() - 1; i >= 0; i --){
			BarrierObject to = objects.get(i);
			to.update(delta);
		}
		
		putTimer -= delta;
	}

	Vector2 temp = new Vector2();
	
	public void fire() {
		if(player.getMana() >= 10 & putTimer < 0){
			player.decreaseMana(10);
			putTimer = 1f;
			temp.set(
					(float)Math.cos(Math.toRadians(player.getAngle())),
					-(float)Math.sin(Math.toRadians(player.getAngle()))	
					);
			
			objects.add(new BarrierObject(this, player.getBody().getWorld(), player.getPosition().cpy().add(temp)));
		}
	}

	@Override
	public Texture getIcon() {
		return icon;
	}

}
