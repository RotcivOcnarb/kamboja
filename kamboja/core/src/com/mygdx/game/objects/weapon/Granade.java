package com.mygdx.game.objects.weapon;

import static com.mygdx.game.states.GameState.UNIT_SCALE;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.mygdx.game.KambojaMain;
import com.mygdx.game.objects.Player;
import com.mygdx.game.objects.map.Block;
import com.mygdx.game.states.GameState;

public class Granade {
	
	GameState gameState;
	public Player owner;
	
	Texture texture;
	public Body body;
	public boolean dead;
	
	public Granade(GameState gameState, Player owner) {
		this.gameState = gameState;
		this.owner = owner;
		dead = false;
		
		BodyDef bdef = new BodyDef();
		bdef.bullet = true;
		bdef.type = BodyType.DynamicBody;
		bdef.position.set(owner.getPosition());
		bdef.linearVelocity.set(owner.getAngleVector().cpy().nor().scl(30).scl(1, -1));
		bdef.linearDamping = 5f;
		
		body = gameState.getWorld().createBody(bdef);
		
		CircleShape circle = new CircleShape();
		circle.setRadius(5 / UNIT_SCALE);
		
		Fixture f = body.createFixture(circle, 0f);
		f.setSensor(true);
		
		body.setUserData(this);
		
		texture = KambojaMain.getTexture("Weapons/granada.png");
		
	}
	
	public boolean render(SpriteBatch sb) {
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		sb.setColor(Color.WHITE);
		
		sb.draw(texture,
				body.getWorldCenter().x - texture.getWidth()/2 / GameState.UNIT_SCALE,
				body.getWorldCenter().y - texture.getHeight()/2 / GameState.UNIT_SCALE,
				texture.getWidth()/2 /GameState.UNIT_SCALE,
				texture.getHeight()/2 /GameState.UNIT_SCALE,
				texture.getWidth() /GameState.UNIT_SCALE,
				texture.getHeight() /GameState.UNIT_SCALE,
				1, 1, body.getAngle(),
				0, 0,
				texture.getWidth(), texture.getHeight(),
				false, false);
		
		if(body.getLinearVelocity().len() < 0.1f) {
			System.out.println("Granada por falta de velocidade, owner da granada é " + owner.getName());
			explode();
		}
		
		return dead;
	}

	public void explode() {
		gameState.showExplosion(body.getWorldCenter());
		gameState.screenshake(.3f);
		
		for(int i = gameState.getPlayers().size() -1; i >=0; i --) {
			Player p = gameState.getPlayers().get(i);
			if(p.getPosition().cpy().sub(body.getWorldCenter()).len2() < 2) {
				p.takeDamage(50, owner, true);
			}
		}
		
		for(int i = gameState.getBlocks().size() -1; i >=0; i --) {
			Block block = gameState.getBlocks().get(i);
			if(block.getPosition().cpy().sub(body.getWorldCenter()).len2() < 2) {
				block.takeDamage(50, false);
			}
		}
		
		dead = true;
	}

	public void ricochet(Player fromWho) {
		owner = fromWho;
		System.out.println("RICOCHETE");
		body.setLinearVelocity(owner.getAngleVector().cpy().scl(1, -1).scl(30));
	}

}
