package com.mygdx.game.objects.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.objects.Bullet;
import com.mygdx.game.states.GameState;

public class UnbreakableBlock extends Block{

	public UnbreakableBlock(TextureRegion texture, float x, float y, float width, float height, World world, GameState state) {
		super(texture, x, y, width, height, world, state);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean render(SpriteBatch sb) {
	        sb.draw(texture, x, y, width, height);
	       return true;
	       	
	}

	public void bulletCollided(Contact contact, Bullet bullet) {
		bullet.remove();
		GameState.removeBody(bullet.getBody());
	}

	@Override
	public void takeDamage(float amount, boolean flame) {
		// TODO Auto-generated method stub
		
	}

}
