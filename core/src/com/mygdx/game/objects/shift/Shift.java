package com.mygdx.game.objects.shift;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.objects.Player;

public abstract class Shift {
	
	public Player player;
	
	public Shift(Player player){
		this.player = player;
	}
	
	public abstract void render(SpriteBatch sb);
	public abstract void update(double delta);
	public abstract void fire();
	public abstract Texture getIcon();
	public abstract void dispose();
}
