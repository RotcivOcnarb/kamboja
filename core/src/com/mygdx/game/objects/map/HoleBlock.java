package com.mygdx.game.objects.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.objects.Bullet;
import com.mygdx.game.states.GameState;

public class HoleBlock extends Block{

	public HoleBlock(TextureRegion texture, float x, float y, float width, float height, World world, GameState state) {
		super(texture, x, y, width, height, world, state);
		body.getFixtureList().get(0).setSensor(true);
	}

	public void dispose() {
		
	}

	public void takeDamage(float amount, boolean flame) {
		
	}

	public boolean render(SpriteBatch sb) {
		return true;
	}

	public void bulletCollided(Contact contact, Bullet bullet) {
		contact.setEnabled(false);
	}
	

}
