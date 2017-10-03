package com.mygdx.game.objects.weapon;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.objects.Player;
import com.mygdx.game.states.GameState;

public abstract class Weapon {

	protected World world;
	protected Player player;
	public float analog;
	Vector2 lerpPosition;
	
	protected float botPrecision = 0;
	
	public Weapon(World world, Player player){
		this.world = world;
		this.player = player;
		lerpPosition = player.getBody().getWorldCenter().cpy();
	}
	
	public abstract void render(SpriteBatch sb);
	public abstract void update(float delta);
	public abstract void dispose();
	
	public void renderTexture(SpriteBatch sb, Texture tex){
		sb.draw(tex,
				lerpPosition.x - tex.getWidth()/2 / GameState.UNIT_SCALE,
				lerpPosition.y - tex.getHeight()/2 / GameState.UNIT_SCALE,
				tex.getWidth()/2 /GameState.UNIT_SCALE,
				tex.getHeight()/2 /GameState.UNIT_SCALE,
				tex.getWidth() /GameState.UNIT_SCALE,
				tex.getHeight() /GameState.UNIT_SCALE,
				player.isFalling() ? Math.max(0.3f, player.getFallingTimer()) : 1,
				player.isFalling() ? Math.max(0.3f, player.getFallingTimer()) : 1,
				270 - getPlayer().getAngle(), 0, 0, tex.getWidth(), tex.getHeight(), false, false);
		
		lerpPosition.add(player.getBody().getWorldCenter().cpy().sub(lerpPosition).scl(1/1.5f));
	}

	public abstract void botShoot();

	public Player getPlayer() {
		return player;
	}
	
	public void knockback(float amount){
		player.getBody().applyLinearImpulse(player.getAngleVector().cpy().scl(-1, 1).scl(amount), player.getPosition(), true);
	}
	
	public void screenshake(float amount){
		player.getState().screenshake(amount);
	}

	public abstract void endSound();
}
