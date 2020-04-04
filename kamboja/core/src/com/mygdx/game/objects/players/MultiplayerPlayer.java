package com.mygdx.game.objects.players;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.states.GameState;

public class MultiplayerPlayer extends Player{

	public String ip;
	
	Vector2 targetPosition;
	Vector2 targetAngle;
	
	public MultiplayerPlayer(Body body, int id, GameState state, String name, String ip) {
		super(body, id, state, name);
		this.ip = ip;
	}

	@Override
	public void update(float delta) {
		nonInputUpdate(delta);
		
		if(targetPosition != null)
			body.applyForceToCenter(targetPosition.cpy().sub(body.getWorldCenter()).scl(100f), true);
		
		if(targetAngle != null)
			nextAngle = targetAngle;
	}
	
	@Override
	public void updateTransform(Vector2 position, Vector2 angle, float weaponAnalog) {
		this.targetPosition = position;
		this.targetAngle = angle;
		getWeapon().analog = weaponAnalog;
	}
	
}

