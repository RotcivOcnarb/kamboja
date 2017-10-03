package com.mygdx.game.objects;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class TrailRenderer {
	
	Body body;
	float trailLength;
	float trailWidth;
	Color trailColor;
	
	ShapeRenderer sr;
	
	ArrayList<Vector2> pastPositions;
	Color trans;
	boolean disposed = false;
	
	public void dispose(){
		if(!disposed){
			sr.dispose();
			disposed = true;
		}
	}
	
	public TrailRenderer(Body body, float trailLength, float trailWidth){
		this.body = body;
		this.trailLength = trailLength;
		this.trailWidth = trailWidth;
		
		sr = new ShapeRenderer();
		
		pastPositions = new ArrayList<Vector2>();
	}
	
	public void setColor(Color c){
		trailColor = c;
		trans = new Color(trailColor.r, trailColor.g, trailColor.b, 0);
	}
	
	public void setColor(float r, float g, float b, float a){
		trailColor = new Color(r, g, b, a);
		trans = new Color(r, g, b, 0);
	}
	
	public void renderTrail(SpriteBatch sb, boolean exists){
		
		sr.setProjectionMatrix(sb.getProjectionMatrix());
			

			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			
				if(pastPositions.size() > trailLength){ //se tem mais items do q deveria
					for(int i = 0; i < pastPositions.size() - trailLength; i ++){//passa por todos os items "a mais"
						pastPositions.remove(pastPositions.size()-1); //e remove o ultimo
					}
				}
				
				if(exists)
				pastPositions.add(0, body.getWorldCenter().cpy());
				else{
					if(pastPositions.size() > 0)
					pastPositions.remove(pastPositions.size()-1); //remove o ultimo
				}
				sr.begin(ShapeType.Filled);
				for(int i = 1; i < pastPositions.size(); i ++){
					float lerp = (i+1) / (float)pastPositions.size();
					
					sr.setColor(trailColor.cpy().lerp(trans, lerp));
					
					sr.rectLine(pastPositions.get(i-1), pastPositions.get(i), lerp(trailWidth, 0, lerp));
						//sr.circle(pastPositions.get(i).x, pastPositions.get(i).y, trailWidth);
					
				}
				sr.end();
				Gdx.gl.glDisable(GL20.GL_BLEND);
		
	}
	
	public float lerp(float f1, float f2, float factor){
		return (1 - factor) * f1 + factor * f2;
	}
	
	public boolean finished(){
		if(pastPositions.size() > 0){
			Vector2 firstValue = pastPositions.get(0);
			
			for(int i = 0; i < pastPositions.size(); i ++){
				if(pastPositions.get(i).epsilonEquals(firstValue, 0.01f)){
					continue;
				}
				else{
					return false;
				}
			}
		}
		
		return true;

	}

}
