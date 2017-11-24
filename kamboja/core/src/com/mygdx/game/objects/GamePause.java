package com.mygdx.game.objects;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.mygdx.game.KambojaMain;
import com.mygdx.game.Manager;
import com.mygdx.game.controllers.Gamecube;
import com.mygdx.game.controllers.GenericController;
import com.mygdx.game.controllers.Playstation3;
import com.mygdx.game.controllers.XBox;
import com.mygdx.game.states.GameState;

public class GamePause {
	
	GameState state;
	
	MenuCursors cursors;

	BitmapFont font;
	
	Rectangle2D exit, resume, yes, no;
	
	public boolean confirm = false;
	
	public float offset1, offset2;
	FreeTypeFontGenerator ftfg;
	
	public void dispose(){
		ftfg.dispose();
		font.dispose();
		cursors.dispose();
	}
	
	public GamePause(GameState state){
		this.state = state;
				
		ftfg = new FreeTypeFontGenerator(Gdx.files.internal("fonts/outlander.ttf"));
		FreeTypeFontParameter param = new FreeTypeFontParameter();
		param.size = 50;
		param.color = new Color(0.03f, 0.03f, 0.03f, 1);
		param.borderWidth = 2;
		param.borderColor = new Color(1, 0.9f, 0.9f, 1);
		param.shadowColor = new Color(0, 0, 0, 0.7f);
		param.shadowOffsetX = 3;
		param.shadowOffsetY = 3;
		font = ftfg.generateFont(param);
		
		offset1 = -1000;
		offset2 = 1000;
		
		exit = new Rectangle2D.Float((1920 - 130)/2.0f + offset1, 300, 130, 50);
		resume = new Rectangle2D.Float((1920 - 210)/2.0f + offset2, 400, 210, 50);
		
		yes = new Rectangle2D.Float((1920 - 100)/2.0f + offset1, 300, 100, 50);
		no = new Rectangle2D.Float((1920 - 60)/2.0f + offset2, 400, 60, 50);
		
		cursors = new MenuCursors();
		
	}
	
	public void render(SpriteBatch sb){
		//desenha os cursores
		
		offset1 += (-offset1)/20.0f;
		offset2 += (-offset2)/20.0f;
		
		exit.setRect((1920 - 130)/2.0f + offset1, 300, 130, 50);
		resume.setRect((1920 - 210)/2.0f + offset2, 400, 210, 50);
		
		yes.setRect((1920 - 100)/2.0f + offset1, 300, 100, 50);
		no.setRect((1920 - 60)/2.0f + offset2, 400, 60, 50);

		sb.begin();
		if(!confirm){
			font.draw(sb, "Resume", (float)resume.getX(), (float)resume.getY() + font.getLineHeight()/2);
			font.draw(sb, "Exit", (float)exit.getX(), (float)exit.getY() + font.getLineHeight()/2);
		}
		else{
			font.draw(sb, "Yes", (float)yes.getX(), (float)yes.getY() + font.getLineHeight()/2);
			font.draw(sb, "No", (float)no.getX(), (float)no.getY() + font.getLineHeight()/2);
		}
		sb.end();

		cursors.render(sb);
	}
	
	public void update(double delta){
		cursors.update((float) delta);
		
		int cont = 0;
		for(PlayerController pc : KambojaMain.getControllers()){
			if(pc instanceof KeyboardController){
				break;
			}
			cont++;
		}
		
		if(Gdx.input.justTouched()){
			buttonPressed(cont, Buttons.LEFT, "Keyboard");
		}
	}
	
	public void axisMoved(Controller controller, int axisCode, float value){
		cursors.axisMoved(controller, axisCode, value);
	}
	
	public void buttonPressed(int id, int button, String controllerName){
		
		int select = 0;
		
		if(controllerName.equals(Gamecube.getID())){
			select = Gamecube.A;
		}
		else if(controllerName.toUpperCase().contains("XBOX") && controllerName.contains("360")){
			select = XBox.BUTTON_X;
		}
		else if(controllerName.toUpperCase().contains("SONY") || controllerName.toUpperCase().contains("PLAYSTATION")){
			select = Playstation3.X;
		}
		else if(controllerName.toUpperCase().equals("KEYBOARD")){
			select = Buttons.LEFT;
		}
		else{
			select = GenericController.X;
		}
		
		if(button == select){
			Point2D p = new Point2D.Double(cursors.getPosition(id).x, cursors.getPosition(id).y);
			
			if(!confirm){
				if(exit.contains(p)){
					confirm = true;
					offset1 = -1000;
					offset2 = 1000;
				}
				else if(resume.contains(p)){
					state.pauseUnpause();
				}
			}
			else{
				if(yes.contains(p)){
					state.manager.changeState(Manager.PLAYER_SELECT_STATE);
					return;
				}
				else if(no.contains(p)){
					confirm = false;
					offset1 = -1000;
					offset2 = 1000;
				}
			}
		}
		
	}

}
