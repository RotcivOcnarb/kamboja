package com.mygdx.game.objects.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.objects.Bullet;
import com.mygdx.game.states.GameState;

public class BreakableBlock extends Block{
	
	float life = 1;
	float hitTimer = 0;
	
	public BreakableBlock(TextureRegion texture, float x, float y, float width, float height, World world, GameState state) {
		super(texture, x, y, width, height, world, state);
		
	}

	public void dispose() {

	}

	@Override
	public boolean render(SpriteBatch sb) {
		hitTimer -= Gdx.graphics.getDeltaTime();
		if(hitTimer < 0) hitTimer = 0;

        sb.draw(texture, x, y, width, height);

		if(life <= 0){
			//if(GameState.SFX)
			//broke.play(0.5f);
			return false;
		}
		else return true;
	}
	
	public void takeDamage(float amount, boolean flame){
		hitTimer = 1f;
		life -= amount*2.5;
		
		//if(!flame)
			//if(GameState.SFX)
		//hit.play(0.5f);
	}

	public void bulletCollided(Contact contact, Bullet bullet) {
		takeDamage(bullet.getDamage(), false);
		bullet.remove();
		GameState.removeBody(bullet.getBody());
		
	}

}
