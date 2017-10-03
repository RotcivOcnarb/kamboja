package com.mygdx.game.objects;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.states.GameState;

public class BotPlayer extends Player
{
  boolean tagged;
  float boundingRadius;
  float maxLinearSpeed;
  float maxLinearAcceleration;
  float maxAngularSpeed;
  float maxAngularAcceleration;
  SteeringBehavior<Vector2> behavior;
  SteeringAcceleration<Vector2> steeringOutput;
  Arrive<Vector2> arrive;

  Steerable<Vector2> target;

  float sideTimer = 0.0F;
  int side = 1;
  
  Vector2 targetAim = new Vector2(0, 0);
  float difficulty = 0.1f;
  
  public enum BotState{
	  ATTACK_NEAREST,
	  ATTACK_WEAKEST,
	  HIDING,
	  GET_LIFE,
	  NEAREST_ITEM
	  
  }
  
  BotPlayer.BotState botState = BotPlayer.BotState.ATTACK_NEAREST;
  
  public float variancia()
  {
    float media = 0.0F;
    
    for (Player p : getState().getPlayers()) {
      if ((p != this) && (!isDead())) {
        media += p.getPosition().cpy().sub(getPosition()).len();
      }
    }
    
    media /= (getState().getPlayers().size() - 1);
    
    float variancia = 0.0F;
    
    for (Player p : getState().getPlayers()) {
      if ((p != this) && (!isDead())) {
        variancia = (float)(variancia + Math.pow(p.getPosition().cpy().sub(getPosition()).len() - media, 2.0D));
      }
    }
    
    variancia /= (getState().getPlayers().size() - 1);
    
    return variancia;
  }
  

  public BotPlayer(Body body, int id, GameState state)
  {
    super(body, id, state);
    
    switch(GameState.DIFFICULTY){
    case 0:
    	difficulty = 0.01f;
    	break;
    case 1:
    	difficulty = 0.05f;
    	break;
    case 2:
    	difficulty = 0.1f;
    	break;
    case 3:
    	difficulty = 0.5f;
    	break;
    case 4:
    	difficulty = 1f;
    	break;
    	
    }
    
    boundingRadius = 10.0F;
    
    maxLinearSpeed = (speed * 5.0F);
    maxLinearAcceleration = 5000.0F;
    maxAngularSpeed = 30.0F;
    maxAngularAcceleration = 5.0F;
    
    steeringOutput = new SteeringAcceleration<Vector2>(new Vector2());
    
    arrive = new Arrive<Vector2>(this, null)
      .setArrivalTolerance(1.5F)
      .setDecelerationRadius(2.0F);

  }
  
  public void update(float delta) {
    super.update(delta);
    
    sideTimer -= delta;
    
    if (sideTimer <= 0.0F) {
      side *= -1;
      sideTimer = ((float)(Math.random() * 3000.0D + 2000.0D));
    }
    
    if(target != null)
    targetAim.add(target.getPosition().cpy().sub(targetAim).scl(difficulty));
    

    if (!isDead()) {
      switch (botState) {
      case ATTACK_NEAREST: 
        followNearestPlayer(delta);
        break;
      case ATTACK_WEAKEST: 
        followWeakestPlayer(delta);
        break;
      case HIDING: 
    	 hide(delta);
        break;
      case GET_LIFE: 
    	  followLifeItem(delta);
        break;
      case NEAREST_ITEM: 
        followNearestItem(delta);
      }
      
    }
  }
  

  public void applySteeringReversed(float delta)
  {
    boolean anyAccelerations = false;
    
    if (!((Vector2)steeringOutput.linear).isZero()) {
      Vector2 force = ((Vector2)steeringOutput.linear).scl(delta);
      body.applyForceToCenter(force.scl(-1.0F), true);
      anyAccelerations = true;
    }
    
    float ag = (targetAim).cpy().sub(getPosition()).angle();
    angle = new Vector2(
      (float)Math.sin(Math.toRadians(ag + 90.0F)), 
      (float)Math.cos(Math.toRadians(ag + 90.0F)));
    

    if (anyAccelerations) {
      Vector2 velocity = body.getLinearVelocity();
      float currentSpeedSquared = velocity.len2();
      
      if (currentSpeedSquared > maxLinearSpeed * maxLinearSpeed) {
        body.setLinearVelocity(
          velocity.scl(
          maxLinearSpeed / (float)Math.sqrt(currentSpeedSquared)));
      }
      



      if (body.getAngularVelocity() > maxAngularSpeed) {
        body.setAngularVelocity(maxAngularSpeed);
      }
    }
  }
  

  public void applySteering(float delta)
  {
    boolean anyAccelerations = false;
    
    if (!((Vector2)steeringOutput.linear).isZero()) {
      Vector2 force = ((Vector2)steeringOutput.linear).scl(delta);
      body.applyForceToCenter(force, true);
      anyAccelerations = true;
    }
    


    float ag = (targetAim).cpy().sub(getPosition()).angle();
    angle = new Vector2(
      (float)Math.sin(Math.toRadians(ag + 90.0F)), 
      (float)Math.cos(Math.toRadians(ag + 90.0F)));
    

    if (anyAccelerations) {
      Vector2 velocity = body.getLinearVelocity();
      float currentSpeedSquared = velocity.len2();
      
      if (currentSpeedSquared > maxLinearSpeed * maxLinearSpeed) {
        body.setLinearVelocity(velocity.scl(maxLinearSpeed / (float)Math.sqrt(currentSpeedSquared)));
      }
      
      if (body.getAngularVelocity() > maxAngularSpeed) {
        body.setAngularVelocity(maxAngularSpeed);
      }
    }
    Vector2 a = new Vector2();
    body.applyForceToCenter(angleToVector(a, (float)Math.toRadians(angle.angle() + 90 * side)), true);
  }
  
  public Player getNearestPlayer()
  {
    Player nearest = (Player)getState().getPlayers().get(0);
    for (Player p : getState().getPlayers()) {
      if (p != this)
      {

        if ((nearest != p) && (isDead())) {
          nearest = p;

        }
        else if ((nearest == this) && (p != this)) {
          nearest = p;

        }
        else if ((p.getPosition().cpy().sub(getPosition()).len2() < nearest.getPosition().cpy().sub(getPosition()).len2()) && 
          (!isDead()))
          nearest = p;
      }
    }
    return nearest;
  }
  
  public Player getWeakestPlayer() {
    Player weakest = (Player)getState().getPlayers().get(0);
    for (Player p : getState().getPlayers()) {
      if (p != this)
      {

        if ((weakest != p) && (isDead())) {
          weakest = p;

        }
        else if ((weakest == this) && (p != this)) {
          weakest = p;

        }
        else if ((p.getLife() < weakest.getLife()) && 
          (!isDead()))
          weakest = p;
      }
    }
    return weakest;
  }

  public Item getNearestItem()
  {
    if (getState().getItems().size() > 0) {
      Item nearest = (Item)getState().getItems().get(0);
      for (Item p : getState().getItems()) {
        if ((!p.canRemove) && 
          (p.getPosition().cpy().sub(getPosition()).len2() < nearest.getPosition().cpy().sub(getPosition()).len2())) {
          nearest = p;
        }
      }
      
      return nearest;
    }
    return null;
  }
  
  public Item getNearestLife() {
    if (getState().getItems().size() > 0) {
      Item nearest = (Item)getState().getItems().get(0);
      for (Item p : getState().getItems()) {
        if ((getId() != 3) && (getId() == 3)) {
          nearest = p;

        }
        else if ((!p.canRemove) && (getId() == 3) && 
          (p.getPosition().cpy().sub(getPosition()).len2() < nearest.getPosition().cpy().sub(getPosition()).len2())) {
          nearest = p;
        }
      }
      


      return getId() == 3 ? nearest : null;
    }
    return null;
  }
  
  public void followNearestPlayer(float delta)
  {
    arrive.setArrivalTolerance(1.5F).setDecelerationRadius(2.0F);
    getWeapon().botShoot();
    target = getNearestPlayer();
    arrive.setTarget(target);
    behavior = arrive;
    
    if ((behavior != null) && (target != null)) {
      behavior.calculateSteering(steeringOutput);
      applySteering(delta);
    }
    
    if ((life > 50.0F) && (variancia() < 0.5F)) {
      botState = BotPlayer.BotState.ATTACK_WEAKEST;
    }
    
    if ((life < 30.0F) && (getNearestLife() != null)) {
      botState = BotPlayer.BotState.GET_LIFE;
    }
    
    if ((life < 30.0F) && (getNearestLife() == null)) {
      botState = BotPlayer.BotState.HIDING;
    }
    
    if ((life > 50.0F) && (getNearestItem() != null) && 
      (((Vector2)target.getPosition()).cpy().sub(getPosition()).len() > getNearestItem().getPosition().cpy().sub(getPosition()).len())) {
      botState = BotPlayer.BotState.NEAREST_ITEM;
    }
  }
  
  public void followWeakestPlayer(float delta)
  {
    arrive.setArrivalTolerance(1.5F).setDecelerationRadius(2.0F);
    getWeapon().botShoot();
    target = getWeakestPlayer();
    arrive.setTarget(target);
    behavior = arrive;
    
    if ((behavior != null) && (target != null)) {
      behavior.calculateSteering(steeringOutput);
      applySteering(delta);
    }
    
    if ((getNearestPlayer().getPosition().cpy().sub(getPosition()).len() < 30.0F) && 
      (variancia() > 0.5D)) {
      botState = BotPlayer.BotState.ATTACK_NEAREST;
    }
    

    if ((life < 30.0F) && (getNearestLife() != null)) {
      botState = BotPlayer.BotState.GET_LIFE;
    }
    
    if ((life < 30.0F) && (getNearestLife() == null)) {
      botState = BotPlayer.BotState.HIDING;
    }
    
    if ((life > 50.0F) && (getNearestItem() != null) && 
      (((Vector2)target.getPosition()).cpy().sub(getPosition()).len() > getNearestItem().getPosition().cpy().sub(getPosition()).len())) {
      botState = BotPlayer.BotState.NEAREST_ITEM;
    }
  }
  


  public void hide(float delta)
  {
    arrive.setArrivalTolerance(1.5F).setDecelerationRadius(2.0F);
    getWeapon().botShoot();
    target = getNearestPlayer();
    arrive.setTarget(target);
    behavior = arrive;
    
    if ((behavior != null) && (target != null)) {
      behavior.calculateSteering(steeringOutput);
      applySteeringReversed(delta);
    }
    
    if ((life > 50.0F) && (getNearestItem() != null)) {
      botState = BotPlayer.BotState.NEAREST_ITEM;
    }
    
    if ((life < 30.0F) && (getNearestLife() != null)) {
      botState = BotPlayer.BotState.GET_LIFE;
    }
    
    if ((life > 50.0F) && (getNearestPlayer().getPosition().cpy().sub(getPosition()).len() < 30.0F) && 
      (variancia() > 0.5D)) {
      botState = BotPlayer.BotState.ATTACK_NEAREST;
    }
    

    if ((life > 50.0F) && (variancia() < 0.5F)) {
      botState = BotPlayer.BotState.ATTACK_WEAKEST;
    }
  }
  
  public void followLifeItem(float delta)
  {
    arrive.setArrivalTolerance(0.0F).setDecelerationRadius(0.0F);
    getWeapon().botShoot();
    target = getNearestLife();
    arrive.setTarget(target);
    behavior = arrive;
    
    if ((behavior != null) && (target != null)) {
      behavior.calculateSteering(steeringOutput);
      applySteering(delta);
    }
    
    if ((life > 50.0F) && (getNearestItem() != null)) {
      botState = BotPlayer.BotState.NEAREST_ITEM;
    }
    
    if ((life < 30.0F) && (getNearestLife() == null)) {
      botState = BotPlayer.BotState.HIDING;
    }
    
    if ((getNearestPlayer().getPosition().cpy().sub(getPosition()).len() < 30.0F) && 
      (variancia() > 0.5D)) {
      botState = BotPlayer.BotState.ATTACK_NEAREST;
    }
    

    if ((life > 50.0F) && (variancia() < 0.5F)) {
      botState = BotPlayer.BotState.ATTACK_WEAKEST;
    }
  }
  
  public void followNearestItem(float delta)
  {
    arrive.setArrivalTolerance(0.0F).setDecelerationRadius(0.0F);
    getWeapon().botShoot();
    target = getNearestItem();
    arrive.setTarget(target);
    behavior = arrive;
    
    if ((behavior != null) && (target != null)) {
      behavior.calculateSteering(steeringOutput);
      applySteering(delta);
    }
    

    if ((life < 30.0F) && (getNearestLife() == null)) {
      botState = BotPlayer.BotState.HIDING;
    }
    
    if ((life < 30.0F) && (getNearestLife() != null)) {
      botState = BotPlayer.BotState.GET_LIFE;
    }
    
    if ((life > 50.0F) && (getNearestPlayer().getPosition().cpy().sub(getPosition()).len() < 30.0F) && 
      (variancia() > 0.5D)) {
      botState = BotPlayer.BotState.ATTACK_NEAREST;
    }
    

    if ((life > 50.0F) && (variancia() < 0.5F) && (target != null) && 
      (getWeakestPlayer().getPosition().cpy().sub(getPosition()).len() < ((Vector2)target.getPosition()).cpy().sub(getPosition()).len())) {
      botState = BotPlayer.BotState.ATTACK_WEAKEST;
    }
  }
  
  public float getMaxLinearSpeed() {
    return maxLinearSpeed;
  }
  
  public void setMaxLinearSpeed(float maxLinearSpeed) {
    this.maxLinearSpeed = maxLinearSpeed;
  }
  
  public float getMaxLinearAcceleration()
  {
    return maxLinearAcceleration;
  }
  
  public void setMaxLinearAcceleration(float maxLinearAcceleration) {
    this.maxLinearAcceleration = maxLinearAcceleration;
  }
  
  public float getMaxAngularSpeed() {
    return maxAngularSpeed;
  }
  
  public void setMaxAngularSpeed(float maxAngularSpeed) {
    this.maxAngularSpeed = maxAngularSpeed;
  }
  
  public float getMaxAngularAcceleration() {
    return maxAngularAcceleration;
  }
  
  public void setMaxAngularAcceleration(float maxAngularAcceleration) {
    this.maxAngularAcceleration = maxAngularAcceleration;
  }
  
  public Vector2 getPosition() {
    return body.getWorldCenter();
  }
  
  public float getOrientation() {
    return body.getAngle();
  }
  
  public Vector2 getLinearVelocity() {
    return body.getLinearVelocity();
  }
  
  public float getAngularVelocity() {
    return body.getAngularVelocity();
  }
  
  public float getBoundingRadius() {
    return boundingRadius;
  }
  
  public boolean isTagged() {
    return tagged;
  }
  
  public void setTagged(boolean tagged) {
    this.tagged = tagged;
  }
  
  public Vector2 newVector() {
    return new Vector2();
  }
  
  public float vectorToAngle(Vector2 vector) {
    return (float)Math.atan2(-vector.x, vector.y);
  }
  
  public Vector2 angleToVector(Vector2 outVector, float angle) {
    outVector.x -= (float)Math.sin(angle);
    outVector.y = ((float)Math.cos(angle));
    return outVector;
  }
  
  public void setBehavior(SteeringBehavior<Vector2> behavior) {
    this.behavior = behavior;
  }
  
  public SteeringBehavior<Vector2> getBehavior() {
    return behavior;
  }
}