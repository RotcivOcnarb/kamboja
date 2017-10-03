package com.mygdx.game.states;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.KambojaMain;
import com.mygdx.game.Manager;
import com.mygdx.game.State;
import com.mygdx.game.controllers.Gamecube;
import com.mygdx.game.controllers.XBox;
import com.mygdx.game.objects.Background;
import com.mygdx.game.objects.KeyboardController;
import com.mygdx.game.objects.MenuCursors;
import com.mygdx.game.objects.PlayerController;
import com.mygdx.game.objects.Util;

public class Options extends State{

	private float timer;
	private Background background;
	private MenuCursors cursors;
	
	
	private float[] button_x = new float[6];
	private Rectangle2D[] btn_bounds = new Rectangle2D[6];
	private String[] button_text = new String[6];
	
	private BitmapFont font;
	private GlyphLayout layout;
	
	private boolean exiting;
	
	private int numButtons = 7;
	

	
	public Options(Manager manager) {
		super(manager);
	}

	SpriteBatch sb;
	
	public void dispose(){
		cursors.dispose();
		font.dispose();
		background.dispose();
	}
	
	public void create() {		

		timer = -1;
		exiting = false;
		
		button_text[0] = "Lights: " + GameState.LIGHTS;
		button_text[1] = "Debug: " + GameState.DEBUG;
		button_text[2] = "Beta Items: " + GameState.BETA_ITEMS;
		button_text[3] = "SFX: " + GameState.SFX;
		button_text[4] = "Bot Difficulty: " + difText(GameState.DIFFICULTY);
		button_text[5] = "Back";
		
		cursors = new MenuCursors();
		background = new Background();
		
		for(int i = 0; i < button_x.length; i ++){
			button_x[i] = (i % 2 == 0 ? 1 : -1 ) * Gdx.graphics.getWidth() + 200;
		}

		FreeTypeFontGenerator ftfg;
		FreeTypeFontParameter param;
		ftfg = new FreeTypeFontGenerator(Gdx.files.internal("fonts/dot_to_dot.ttf"));
		param = new FreeTypeFontParameter();
		param.size = (int) (150 * Gdx.graphics.getDensity());
		param.color = new Color(0.03f, 0.03f, 0.03f, 1);
		param.borderWidth = 2;
		param.borderColor = new Color(1, 0.9f, 0.9f, 1);
		param.shadowColor = new Color(0, 0, 0, 0.7f);
		param.shadowOffsetX = 3;
		param.shadowOffsetY = 3;
		font = ftfg.generateFont(param);
		ftfg.dispose();
		
		if(layout == null)
		layout = new GlyphLayout();
	
		for(int i = 0; i < btn_bounds.length; i ++){
			layout.setText(font, button_text[i]);
			btn_bounds[i] = new Rectangle2D.Double(Gdx.graphics.getWidth()/2 - layout.width/2, Gdx.graphics.getHeight() - (Gdx.graphics.getHeight() / numButtons)*(i+1) - layout.height - 10, layout.width, layout.height);
		}

	}

	public void render(SpriteBatch sb) {
		this.sb = sb;
		background.render(sb);
		sb.setColor(1, 1, 1, 1);
		
		
		sb.begin();
		
		layout.setText(font, "Options");
		font.draw(sb, "Options", Gdx.graphics.getWidth()/2 - layout.width/2, Gdx.graphics.getHeight() - (Gdx.graphics.getHeight() / numButtons)*0 - 10);
		
		button_text[0] = "Lights: " + GameState.LIGHTS;
		button_text[1] = "Debug: " + GameState.DEBUG;
		button_text[2] = "Beta Items: " + GameState.BETA_ITEMS;
		button_text[3] = "SFX: " + GameState.SFX;
		button_text[4] = "Bot Difficulty: " + difText(GameState.DIFFICULTY);
		button_text[5] = "Back";
								
		
		for(int i = 0; i < btn_bounds.length; i ++){
			String tm = button_text[i];
			layout.setText(font, tm);
			font.draw(sb, tm, Gdx.graphics.getWidth()/2 - layout.width/2 + button_x[i], Gdx.graphics.getHeight() - (Gdx.graphics.getHeight() / numButtons)*(i+1) - 10);
			
		}

		sb.end();

		cursors.render(sb);

	}
	
	public String difText(int diff){
		switch(diff){
		 case 0:
		    	return "Super easy";
		    case 1:
		    	return "Easy";
		    case 2:
		    	return "Normal";
		    case 3:
		    	return "Hard";
		    case 4:
		    	return "Super Hard";
		}
		return "";
	}

	public void update(float delta) {
		if(exiting){
			timer -= delta;
			
			for(int i = 0; i < btn_bounds.length; i ++){
				button_x[i] += (Gdx.graphics.getWidth() + 400 -button_x[i])/30.0f;
			}

			if(timer < -1){
				manager.changeState(0);
				return;
			}
		}
		else{
			timer += delta;
		}
		
		background.update(delta, exiting);
		
		if(timer > 1.5){
			for(int i = 0; i < btn_bounds.length; i ++){
				button_x[i] += -button_x[i]/10.0f;
			}

		}
		
		cursors.update(delta);
		
		if(Gdx.input.getX() < 0) Gdx.input.setCursorPosition(0, Gdx.input.getY());
		if(Gdx.input.getX() > Gdx.graphics.getWidth() - 32) Gdx.input.setCursorPosition(Gdx.graphics.getWidth() - 32, Gdx.input.getY());
		if(Gdx.input.getY() < 32) Gdx.input.setCursorPosition(Gdx.input.getX(), 32);
		if(Gdx.input.getY() > Gdx.graphics.getHeight()) Gdx.input.setCursorPosition( Gdx.input.getX(), Gdx.graphics.getHeight());
		
		
		if(Gdx.input.justTouched()){
			buttonDown(null, Buttons.LEFT);
		}
		
	}

	public void connected(Controller controller) {
		
	}

	public void disconnected(Controller controller) {
		
	}
	Point2D p = new Point2D.Double();
	public boolean buttonDown(Controller controller, int buttonCode) {
		if(controller == null){	
			if(buttonCode == Buttons.LEFT){
				
				int id = 0;
				for(PlayerController pc : KambojaMain.getControllers()){
					if(pc instanceof KeyboardController){
						break;
					}
					id++;
				}
				
				if(timer > 1.5f){
					p.setLocation(cursors.getPosition(id).x + 16, cursors.getPosition(id).y + 16);
					
					if(btn_bounds[0].contains(p)){
						button_x[0] = 100;
						GameState.LIGHTS = !GameState.LIGHTS;
					}
					if(btn_bounds[1].contains(p)){
						button_x[1] = 100;
						GameState.DEBUG = !GameState.DEBUG;
					}
					if(btn_bounds[2].contains(p)){
						button_x[2] = 100;
						GameState.BETA_ITEMS = !GameState.BETA_ITEMS;
					}
					if(btn_bounds[3].contains(p)){
						button_x[3] = 100;
						GameState.SFX = !GameState.SFX;
					}
					if(btn_bounds[4].contains(p)){
						button_x[4] = 100;
						GameState.DIFFICULTY ++;
						if(GameState.DIFFICULTY == 5) GameState.DIFFICULTY = 0;
					}
					if(btn_bounds[5].contains(p)){
						button_x[5] = 100;
						exiting = true;
						timer = 1.5f;
					}


				}
				
			}
		}
		else{
			int id = Util.getControllerID(controller);
			if(id != -1){
			if(Util.getControllerID(controller) != -1){
				int select = 0;
				int backbtn = 0;
				
				if(controller.getName().equals(Gamecube.getID())){
					select = Gamecube.A;
					backbtn = Gamecube.B;
				}
				if(controller.getName().equals(XBox.getID())){
					select = XBox.BUTTON_A;
					backbtn = XBox.BUTTON_B;
				}
				
				if(buttonCode == select){
					if(timer > 1.5f){
						p.setLocation(cursors.getPosition(id).x + 16, cursors.getPosition(id).y + 16);
						
						if(btn_bounds[0].contains(p)){
							button_x[0] = 100;
							GameState.LIGHTS = !GameState.LIGHTS;
						}
						if(btn_bounds[1].contains(p)){
							button_x[1] = 100;
							GameState.DEBUG = !GameState.DEBUG;
						}
						if(btn_bounds[2].contains(p)){
							button_x[2] = 100;
							GameState.BETA_ITEMS = !GameState.BETA_ITEMS;
						}
						if(btn_bounds[3].contains(p)){
							button_x[3] = 100;
							GameState.SFX = !GameState.SFX;
						}
						if(btn_bounds[4].contains(p)){
							button_x[4] = 100;
							GameState.DIFFICULTY ++;
							if(GameState.DIFFICULTY == 5) GameState.DIFFICULTY = 0;
						}
						if(btn_bounds[5].contains(p)){
							button_x[5] = 100;
							exiting = true;
							timer = 1.5f;
						}
	
	
					}
				}
				if(buttonCode == backbtn){
					if(timer > 2){
						button_x[5] = 100;
						exiting = true;
						timer = 1.5f;
					}
				}
			}
			}
		}
		return false;
	}
	
	public boolean buttonUp(Controller controller, int buttonCode) {
		return false;
	}

	public boolean axisMoved(Controller controller, int axisCode, float value) {
		cursors.axisMoved(controller, axisCode, value);
		return false;
	}

	public boolean povMoved(Controller controller, int povCode, PovDirection value) {
		return false;
	}

	public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
		return false;
	}

	public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
		return false;
	}

	public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
		return false;
	}

	public void resize(int width, int height) {
		sb.setProjectionMatrix(Util.getNormalProjection());

	}

}
