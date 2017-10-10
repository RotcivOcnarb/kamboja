package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.KambojaMain;
import com.mygdx.game.controllers.Gamecube;
import com.mygdx.game.controllers.GenericController;
import com.mygdx.game.controllers.Playstation3;
import com.mygdx.game.controllers.XBox;

public class MenuCursors {
	
	private static Texture[] cursors;
	private static Vector2[] cursorPosition;
	private static Vector2[] cursorVelocity;

	public MenuCursors() {
		//texturas dos cursores
		if(cursors == null){
			cursors = new Texture[4];
			cursors[0] = new Texture("imgs/cursor_blue.png");
			cursors[1] = new Texture("imgs/cursor_red.png");
			cursors[2] = new Texture("imgs/cursor_green.png");
			cursors[3] = new Texture("imgs/cursor_yellow.png");
		}
		
		//posições dos cursores
		if(cursorPosition == null){
			cursorPosition = new Vector2[4];
			for(int i = 0; i < 4; i ++){
				cursorPosition[i] = new Vector2(Gdx.graphics.getWidth()/2f, Gdx.graphics.getHeight()/2f);
			}
		}
		
		//velocidades dos cursores
		if(cursorVelocity == null){
			cursorVelocity = new Vector2[4];
			for(int i = 0; i < 4; i ++){
				cursorVelocity[i] = new Vector2(0, 0);
			}
		}
		
	}
	
	public void render(SpriteBatch sb){

		sb.begin();
		
		//desenha os cursores
		sb.setColor(1, 1, 1, 1);
		for(int i = 0; i < KambojaMain.getControllers().size(); i ++){
			sb.draw(cursors[i], cursorPosition[i].x, cursorPosition[i].y, 32, 32);
		}
		
		sb.end();
	}
	
	public void update(float delta){
		int cont = 0;
		for(PlayerController pc : KambojaMain.getControllers()){
			if(pc instanceof KeyboardController){
				cursorPosition[cont].x = Gdx.input.getX();
				cursorPosition[cont].y = Gdx.graphics.getHeight() - Gdx.input.getY();
			}
			cont++;
		}
		
		for(int i = 0; i < 4; i ++){
			cursorPosition[i].add(cursorVelocity[i].cpy().scl(delta*70));
			if(cursorPosition[i].x < 0) cursorPosition[i].x = 0;
			if(cursorPosition[i].x > Gdx.graphics.getWidth() - 32) cursorPosition[i].x = Gdx.graphics.getWidth() - 32;
			if(cursorPosition[i].y < 0) cursorPosition[i].y = 0;
			if(cursorPosition[i].y > Gdx.graphics.getHeight() - 32) cursorPosition[i].y = Gdx.graphics.getHeight() - 32;
		}
	}
	
	public Vector2 getPosition(int index){
		return cursorPosition[index];
	}
	
	public void dispose(){
		
	}
	
	public void axisMoved(Controller controller, int axisCode, float value){
		int id = Util.getControllerID(controller);
		if(id != -1){
			int ax = 0;
			int ay = 0;
			
			if(controller.getName().equals(Gamecube.getID())){
				ax = Gamecube.MAIN_X;
				ay = Gamecube.MAIN_Y;
		
			}
			else if(controller.getName().toUpperCase().contains("XBOX") && controller.getName().contains("360")){
				ax = XBox.AXIS_LEFT_X;
				ay = XBox.AXIS_LEFT_Y;
			}
			else if(controller.getName().toUpperCase().contains("SONY") || controller.getName().toUpperCase().contains("PLAYSTATION")){
				ax = Playstation3.LEFT_X;
				ay = Playstation3.LEFT_Y;
			}
			else{
				ax = GenericController.LEFT_X;
				ay = GenericController.LEFT_Y;
			}
	
			if(axisCode == ax){ //X
				if(Math.abs(value) > 0.2)
				cursorVelocity[id].x = value * 10;
				else
				cursorVelocity[id].x = 0;
				
			}
			else if(axisCode == ay){ //Y
				if(Math.abs(value) > 0.2)
				cursorVelocity[id].y = -value * 10;
				else
				cursorVelocity[id].y = 0;
			}
		}
	}

}
