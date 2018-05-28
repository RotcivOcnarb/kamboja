package com.mygdx.game.objects;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
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
	
	ShapeRenderer sr;
	
	public boolean confirm = false;
	
	public float offset1, offset2;
	FreeTypeFontGenerator ftfg;
	GlyphLayout layout;
	
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
		
		layout = new GlyphLayout();
		
		offset1 = -1000;
		offset2 = 1000;
		
		sr = new ShapeRenderer();
		
		exit = new Rectangle2D.Float((1920 - 130)/2.0f + offset1, 300, 130, 50);
		resume = new Rectangle2D.Float((1920 - 210)/2.0f + offset2, 400, 210, 50);
		
		yes = new Rectangle2D.Float((1920 - 100)/2.0f + offset1, 300, 100, 50);
		no = new Rectangle2D.Float((1920 - 60)/2.0f + offset2, 400, 60, 50);
		
		cursors = new MenuCursors();
		
	}
	
	public void render(SpriteBatch sb){
		
		sb.setProjectionMatrix(Util.getNormalProjection());
		
		offset1 += (-offset1)/20.0f;
		offset2 += (-offset2)/20.0f;
		
		exit.setRect((1920 - 130)/2.0f + offset1, 300, 130, 50);
		resume.setRect((1920 - 210)/2.0f + offset2, 400, 210, 50);
		
		yes.setRect((1920 - 100)/2.0f + offset1, 300, 100, 50);
		no.setRect((1920 - 60)/2.0f + offset2, 400, 60, 50);

		sb.begin();
		if(!confirm){
			layout.setText(font, "Resume");
			font.draw(sb, "Resume", (float)resume.getX() + 10, (float)resume.getY() + layout.height);
			
			layout.setText(font, "Exit");
			font.draw(sb, "Exit", (float)exit.getX() + 10, (float)exit.getY() + layout.height);
		}
		else{
			layout.setText(font, "Really want to exit?");
			font.draw(sb, "Really want to exit?", (1920 - layout.width)/2f, 1920/2f);
			
			layout.setText(font, "Yes");
			font.draw(sb, "Yes", (float)yes.getX() + 10, (float)yes.getY() + layout.height);
			
			layout.setText(font, "No");
			font.draw(sb, "No", (float)no.getX() + 10, (float)no.getY() + layout.height);
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
			Rectangle2D curs = new Rectangle2D.Double(cursors.getPosition(id).x - 16, cursors.getPosition(id).y - 16, 32, 32);
			
			if(!confirm){
				if(exit.intersects(curs) || exit.contains(curs)){
					confirm = true;
					offset1 = -1000;
					offset2 = 1000;
				}
				else if(resume.intersects(curs) ||resume.contains(curs)){
					state.pauseUnpause();
				}
			}
			else{
				if(yes.intersects(curs) || yes.contains(curs)){
					state.manager.changeState(Manager.PLAYER_SELECT_STATE);
					return;
				}
				else if(no.intersects(curs) || no.contains(curs)){
					confirm = false;
					offset1 = -1000;
					offset2 = 1000;
				}
			}
		}
		
	}

}
