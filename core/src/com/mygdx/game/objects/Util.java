package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.math.Matrix4;
import com.mygdx.game.KambojaMain;

public class Util {
	
	static Matrix4 normalProjection;
	
	public static Matrix4 getNormalProjection(){
		if(normalProjection == null){
			normalProjection = new Matrix4();
			normalProjection.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}
		
		return normalProjection;
	}
	
	public static int getControllerID(Controller controller){
		for(int i = 0; i < KambojaMain.getControllers().size(); i ++){
			if(controller.equals(KambojaMain.getControllers().get(i).controller)){
				return i;
			}
		}
		return -1;
	}

}
