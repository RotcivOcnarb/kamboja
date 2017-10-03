package com.mygdx.game.states;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.KambojaMain;
import com.mygdx.game.Manager;
import com.mygdx.game.State;
import com.mygdx.game.controllers.Gamecube;
import com.mygdx.game.controllers.Playstation3;
import com.mygdx.game.controllers.XBox;
import com.mygdx.game.objects.Background;
import com.mygdx.game.objects.BotController;
import com.mygdx.game.objects.KeyboardController;
import com.mygdx.game.objects.MenuCursors;
import com.mygdx.game.objects.PlayerController;
import com.mygdx.game.objects.Util;

public class MapSelect extends State{
	
	private ShapeRenderer sr;
	private Color tempC;
	private float timer;
	private Texture thumbs[];
	private String mapNames[];
	private String mapTitles[];
	private Background background;
		
	private float size[];
	private boolean selected[];
	private int mapSelected;
	
	private MenuCursors cursors;
	
	private BitmapFont font;
	private GlyphLayout layout;
	private float titleX;
	private float timeX;
	private float deathX;
	private float itemsX;
	private Rectangle2D timeR, deathR, back, itemsR;
	
	private float startPos;
	private float targetPos;
	
	private Sound sound_select, change_sound;

	private float backPos;
	
	private boolean exiting;
	private boolean goingBack;
	
	public void dispose(){
		for(Texture t : thumbs){
			t.dispose();
		}
		cursors.dispose();
		font.dispose();
		sound_select.dispose();
		change_sound.dispose();
		background.dispose();
		sr.dispose();
	}
	
	public MapSelect(Manager manager) {
		super(manager);
	}

	public void create() {

		timer = -1;
		exiting = false;
		
		sr = new ShapeRenderer();
		tempC = new Color();
		
		background = new Background();

		sound_select = Gdx.audio.newSound(Gdx.files.internal("audio/select.ogg"));
		change_sound = Gdx.audio.newSound(Gdx.files.internal("audio/change.ogg"));
		
		backPos = -300;
		
		mapSelected = -1;
		
		for(PlayerController p : KambojaMain.getControllers()){
			if(p instanceof BotController){
				int rand = (int)(Math.random() * 9f);
				//p.setWeapon(rand);
				while(rand == 1 || rand == 2 || rand == 5 || rand == 7){
					rand = (int)(Math.random() * 9f);
				}
				
			}
		}
		
		if(size == null)
		size = new float[6];
		
		selected = new boolean[size.length];
		
		if(thumbs == null)
		thumbs = new Texture[size.length];
		thumbs[0] = new Texture("imgs/thumb_mansion.png");
		thumbs[1] = new Texture("imgs/thumb_grass.png");
		thumbs[2] = new Texture("imgs/thumb_cross.png");
		thumbs[3] = new Texture("imgs/thumb_island.png");
		thumbs[4] = new Texture("imgs/thumb_library.png");
		thumbs[5] = new Texture("imgs/thumb_random.png");
		
		if(mapNames == null)
		mapNames = new String[size.length];
		mapNames[0] = "maps/mansion.tmx";
		mapNames[1] = "maps/grass.tmx";
		mapNames[2] = "maps/cross.tmx";
		mapNames[3] = "maps/island.tmx";
		mapNames[4] = "maps/library.tmx";
		mapNames[5] = "";
		
		if(mapTitles == null){
			mapTitles = new String[size.length];
			mapTitles[0] = "Mansion";
			mapTitles[1] = "Grass";
			mapTitles[2] = "Cross";
			mapTitles[3] = "Island";
			mapTitles[4] = "Library";
			mapTitles[5] = "Random";
		}
		
		cursors = new MenuCursors();
		
		goingBack = false;
		
		FreeTypeFontGenerator ftfg;
		FreeTypeFontParameter param;
		ftfg = new FreeTypeFontGenerator(Gdx.files.internal("fonts/dot_to_dot.ttf"));
		param = new FreeTypeFontParameter();
		param.size = (int) (200 * Gdx.graphics.getDensity());
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
		
		titleX = Gdx.graphics.getWidth() + 200;
		timeX = titleX*2;
		deathX = timeX*2;
		itemsX = deathX*2;
		
		String tm = "Time: " + (KambojaMain.getGameTime() == -1 ? "Inf." :
			KambojaMain.getGameTime()/60 + ":" + ((KambojaMain.getGameTime() % 60 >= 10) ? KambojaMain.getGameTime() % 60 : "0" + KambojaMain.getGameTime() % 60));
		
		layout.setText(font, tm);
		timeR = new Rectangle2D.Double(Gdx.graphics.getWidth()/4 * 3 - layout.width/2, Gdx.graphics.getHeight() - 300 - layout.height, layout.width, layout.height);
		
		if(back == null);
		back = new Rectangle2D.Double(0, 0, 400, 50);
		
		back.setRect(0, 0, 400, 50);
		
		tm = "Deaths: " + (KambojaMain.getDeathsNumber() == -1  ? "Inf." :KambojaMain.getDeathsNumber());
		layout.setText(font, tm);
		deathR = new Rectangle2D.Double(Gdx.graphics.getWidth()/4 * 3 - layout.width/2, Gdx.graphics.getHeight() - 400 - layout.height, layout.width, layout.height);

		tm = "Items: " + (KambojaMain.hasItems()  ? "on" : "off");
		layout.setText(font, tm);
		itemsR = new Rectangle2D.Double(Gdx.graphics.getWidth()/4 * 3 - layout.width/2, Gdx.graphics.getHeight() - 500 - layout.height, layout.width, layout.height);

		
		startPos = -200;
		targetPos = -100;
	}
	
	public void change_sound(){
		if(GameState.SFX)
		change_sound.play(0.7f, (float)Math.random()*0.1f + 0.95f, 0);
	}
	
	public void sound_select(){
		if(GameState.SFX)
		sound_select.play(0.5f, (float)Math.random()*0.1f + 0.95f, 0);
	}

	public void render(SpriteBatch sb) {
		background.render(sb);
		
		if(!exiting){
			int botcont = 0;
			
			for(int i = 0; i < KambojaMain.getControllers().size(); i++){
				if(KambojaMain.getControllers().get(i) instanceof BotController){
					botcont ++;
				}
			}
			
			if(botcont >= 4){
				mapSelected = (int)(Math.random() * (size.length-1));
				KambojaMain.setMapName(mapNames[mapSelected]);
				targetPos = -100;
				startPos = 100;
				exiting = true;
				timer = 1;
				sound_select();
			}
		}

		backPos += (-backPos)/10.0f;


		float th = Gdx.graphics.getWidth()*0.5f;

		sb.setProjectionMatrix(Util.getNormalProjection());
		tempC.set(1, 1, 1, 1);
		sb.setColor(tempC);
		sb.begin();
			for(int i = 0; i < size.length; i ++){
				int x = i % 3;
				int y = i / 3;
				sb.draw(thumbs[i], 50 + th/6 + th/3*x - size[i]/2, Gdx.graphics.getHeight()/2 - (th/3f)*(y-1) - size[i]/2, size[i], size[i]);
			}

			sb.end();

			sb.begin();
			layout.setText(font, "Select level");
			font.draw(sb, "Select level", Gdx.graphics.getWidth()/4 * 3 - layout.width/2 + titleX, Gdx.graphics.getHeight() - 100);
			
			if(mapSelected >= 0){
				layout.setText(font, mapTitles[mapSelected]);
				font.draw(sb, mapTitles[mapSelected], Gdx.graphics.getWidth()/4 * 3 - layout.width/2 + titleX, Gdx.graphics.getHeight() - 200);
			}
			
			
			font.draw(sb, "Back", backPos, 55);
			
			String tm = "Time: " + (KambojaMain.getGameTime() == -1 ? "Inf." :
					KambojaMain.getGameTime()/60 + ":" + ((KambojaMain.getGameTime() % 60 >= 10) ? KambojaMain.getGameTime() % 60 : "0" + KambojaMain.getGameTime() % 60));
			
			layout.setText(font, tm);
			font.draw(sb, tm, Gdx.graphics.getWidth()/4 * 3 - layout.width/2 + timeX, Gdx.graphics.getHeight() - 300);
			
			tm = "Deaths: " + (KambojaMain.getDeathsNumber() == -1  ? "Inf." : KambojaMain.getDeathsNumber());
			layout.setText(font, tm);
			font.draw(sb, tm, Gdx.graphics.getWidth()/4 * 3 - layout.width/2 + deathX, Gdx.graphics.getHeight() - 400);
			
			tm = "Items: " + (KambojaMain.hasItems()  ? "on" : "off");
			layout.setText(font, tm);
			font.draw(sb, tm, Gdx.graphics.getWidth()/4 * 3 - layout.width/2 + itemsX, Gdx.graphics.getHeight() - 500);
			
			sb.end();
			sb.begin();
			
			//desenha botão de start
			tempC.set(1, 1, 1, 1);
			sr.setColor(tempC);
			drawStartButton(Gdx.graphics.getWidth()/4 * 3 - 150 - 4, startPos -4 - 100, 308, 208, 30);
			
			tempC.set(0, 0, 0, 1);
			sr.setColor(tempC);
			drawStartButton(Gdx.graphics.getWidth()/4 * 3 - 150 - 2, startPos -2 - 100, 304, 204, 30);
			
			tempC.set(255/255f, 201/255f, 14/255f, 1);
			sr.setColor(tempC);
			drawStartButton(Gdx.graphics.getWidth()/4 * 3 - 150, startPos - 100, 300, 200, 30);
			
			sb.end();
			sb.begin();
			
			layout.setText(font, "START");
			font.draw(sb, "START", Gdx.graphics.getWidth()/4 * 3 - layout.width/2, startPos + 70);
			
			sb.end();
			cursors.render(sb);
		
	}
	Point2D p = new Point2D.Double();
	public void drawStartButton(float x, float y, float width, float height, float radius){
		sr.begin(ShapeType.Filled);
			sr.box(x, y, 0, width, height - radius, 0);
			sr.box(x + radius, y, 0, width - (radius*2), height, 0);
			sr.circle(x + radius, y + height - radius, radius);
			sr.circle(x + width - radius, y + height - radius, radius);
		sr.end();
	}

	
	public void update(float delta) {
		background.update(delta, exiting);
		if(exiting){
			timer -= delta;
			startPos += (targetPos - startPos)/10.0f;
			titleX += (Gdx.graphics.getWidth() + 100 -titleX)/10.0f;
			timeX += (Gdx.graphics.getWidth() + 100 -timeX)/10.0f;
			deathX += (Gdx.graphics.getWidth() + 100 -deathX)/10.0f;
			itemsX+= (Gdx.graphics.getWidth() + 100 -itemsX)/10.0f;
					
			for(int i = 0; i < size.length; i ++){
				size[i] += (-size[i])/10.0f;
			}
			
			if(timer < -1){
				if(goingBack){
					manager.changeState(0);
				}
				else
				manager.changeState(2);
				
				return;
			}
		}
		else{
			timer += delta;
		}
		
		if(Gdx.input.getX() < 0) Gdx.input.setCursorPosition(0, Gdx.input.getY());
		if(Gdx.input.getX() > Gdx.graphics.getWidth() - 32) Gdx.input.setCursorPosition(Gdx.graphics.getWidth() - 32, Gdx.input.getY());
		if(Gdx.input.getY() < 32) Gdx.input.setCursorPosition(Gdx.input.getX(), 32);
		if(Gdx.input.getY() > Gdx.graphics.getHeight()) Gdx.input.setCursorPosition( Gdx.input.getX(), Gdx.graphics.getHeight());

		cursors.update(delta);
		
		if(Gdx.input.justTouched()){
			buttonDown(null, Buttons.LEFT);
		}
		
		float th = Gdx.graphics.getWidth()*0.5f;
		
		if(timer > 1.5){
			titleX += -titleX/10.0f;
			timeX += -timeX/10.0f;
			deathX += -deathX/10.0f;
			itemsX += -itemsX/10.0f;
			startPos += (targetPos - startPos)/10.0f;
		}
		
		for(int i = 0; i < selected.length; i ++){
			selected[i] = false;
		}
		
		for(int j = 0; j < 4; j ++){
			if(Controllers.getControllers().size-1 >= j){
				p.setLocation(cursors.getPosition(j).x + 16, cursors.getPosition(j).y + 16);
				for(int i = 0; i < size.length; i ++){
					
					int x = i % 3;
					int y = i / 3;
					Rectangle2D rect = new Rectangle2D.Double(50 + th/6 + th/3*x - size[i]/2, Gdx.graphics.getHeight()/2 - (th/3f)*(y-1) - size[i]/2, size[i], size[i]);
				
					if(rect.contains(p)){
						selected[i] = true;
					}
				}
			}
		}
		
		for(int i = 0; i < size.length; i ++){
			if(timer > i*0.1f + 0.5){
				if(mapSelected != i){
					if(!selected[i])
						size[i] += (th/3f * 0.8f - size[i])/10.0f;
					else
						size[i] += (th/3f * 1f - size[i])/10.0f;
				}
				else{
					size[i] += (th/3f * 0.5f - size[i])/10.0f;
				}
			}
		}

		
	}
	
	

	public void connected(Controller controller) {
		
	}

	public void disconnected(Controller controller) {
		
	}
	
	Rectangle2D tempRect = new Rectangle2D.Double();
	
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
							
							//change time
							if(timeR.contains(p)){
								timeX = 100;
								KambojaMain.setGameTime(KambojaMain.getGameTime() + 60);
								if(KambojaMain.getGameTime() == 11*60){
									if(KambojaMain.getDeathsNumber() != -1){
										KambojaMain.setGameTime(-1);
									}
									else{
										KambojaMain.setGameTime(60);
									}
								}
								if(KambojaMain.getGameTime() == 59){
									KambojaMain.setGameTime(60);
								}
								change_sound();
							}
							//change death
							if(deathR.contains(p)){
								deathX = 100;
								KambojaMain.setDeathsNumber(KambojaMain.getDeathsNumber() + 1);
								if(KambojaMain.getDeathsNumber() == 16){
									if(KambojaMain.getGameTime() != -1){
										KambojaMain.setDeathsNumber(-1);
									}
									else{
										KambojaMain.setDeathsNumber(1);
									}
									return false;
								}
								if(KambojaMain.getDeathsNumber() == 0){
									KambojaMain.setDeathsNumber(1);
								}
								change_sound();
							}
							//change items
							if(itemsR.contains(p)){
								itemsX = 100;
								KambojaMain.setItems(!KambojaMain.hasItems());
								change_sound();
							}
							if(back.contains(p)){
								backPos = 100;
								exiting = true;
								goingBack = true;
								timer = 1;
								sound_select();
							}
							
							//select map
							if(id != -1){
								float th = Gdx.graphics.getWidth()*0.5f;

								for(int i = 0; i < size.length; i ++){

									int x = i % 3;
									int y = i / 3;
									tempRect.setFrame(50 + th/6 + th/3*x - size[i]/2, Gdx.graphics.getHeight()/2 - (th/3f)*(y-1) - size[i]/2, size[i], size[i]);
								
									if(tempRect.contains(p)){
										mapSelected = i;
										KambojaMain.setMapName(mapNames[i]);
										size[i] = (float) (th/3f*1.2);
										targetPos = 0;
										change_sound();
									}
								}
							}
							
							if(mapSelected != -1){
								tempRect.setFrame(Gdx.graphics.getWidth()/4 * 3 - 150, startPos, 300, 100);
								
								if(tempRect.contains(p)){
									if(mapSelected == 5){
										mapSelected = (int) (Math.random() * 4);
										KambojaMain.setMapName(mapNames[mapSelected]);
									}
									targetPos = -100;
									startPos = 100;
									exiting = true;
									timer = 1;
									sound_select();
								}
							}
						}
				
			}
			
			return false;
		}
		
		int id = Util.getControllerID(controller);
		if(id != -1){
		if(Util.getControllerID(controller) != -1){
			int select = 0;
			int backbtn = 0;
			int startbtn = 0;
			
			if(controller.getName().equals(Gamecube.getID())){
				select = Gamecube.A;
				backbtn = Gamecube.B;
				startbtn = Gamecube.START;
			}
			else if(controller.getName().toUpperCase().contains("XBOX") && controller.getName().contains("360")){
				select = XBox.BUTTON_A;
				backbtn = XBox.BUTTON_B;
				startbtn = XBox.BUTTON_START;
			}
			else if(controller.getName().toUpperCase().contains("SONY") || controller.getName().toUpperCase().contains("PLAYSTATION")){
				select = Playstation3.X;
				backbtn = Playstation3.CIRCLE;
				startbtn = Playstation3.START;
			}
			
			if(buttonCode == select){
				if(timer > 1.5f){
					p.setLocation(cursors.getPosition(id).x + 16, cursors.getPosition(id).y + 16);
					
					//change time
					if(timeR.contains(p)){
						timeX = 100;
						KambojaMain.setGameTime(KambojaMain.getGameTime() + 60);
						if(KambojaMain.getGameTime() == 11*60){
							if(KambojaMain.getDeathsNumber() != -1){
								KambojaMain.setGameTime(-1);
							}
							else{
								KambojaMain.setGameTime(60);
							}
						}
						if(KambojaMain.getGameTime() == 59){
							KambojaMain.setGameTime(60);
						}
						change_sound();
					}
					//change death
					if(deathR.contains(p)){
						deathX = 100;
						KambojaMain.setDeathsNumber(KambojaMain.getDeathsNumber() + 1);
						if(KambojaMain.getDeathsNumber() == 16){
							if(KambojaMain.getGameTime() != -1){
								KambojaMain.setDeathsNumber(-1);
							}
							else{
								KambojaMain.setDeathsNumber(1);
							}
							return false;
						}
						if(KambojaMain.getDeathsNumber() == 0){
							KambojaMain.setDeathsNumber(1);
						}
						change_sound();
					}
					//change items
					if(itemsR.contains(p)){
						itemsX = 100;
						KambojaMain.setItems(!KambojaMain.hasItems());
						change_sound();
						change_sound();
					}
					if(back.contains(p)){
						backPos = 100;
						exiting = true;
						goingBack = true;
						timer = 1;
						sound_select();
					}
					
					//select map
					if(Util.getControllerID(controller) != -1){
						float th = Gdx.graphics.getWidth()*0.5f;

						for(int i = 0; i < size.length; i ++){

							int x = i % 3;
							int y = i / 3;
							tempRect.setFrame(50 + th/6 + th/3*x - size[i]/2, Gdx.graphics.getHeight()/2 - (th/3f)*(y-1) - size[i]/2, size[i], size[i]);
						
							if(tempRect.contains(p)){
								mapSelected = i;
								KambojaMain.setMapName(mapNames[i]);
								size[i] = (float) (th/3f*1.2);
								targetPos = 0;
								change_sound();
							}
						}
					}
					
					if(mapSelected != -1){
						tempRect.setFrame(Gdx.graphics.getWidth()/4 * 3 - 150, startPos, 300, 100);
						
						if(tempRect.contains(p)){
							if(mapSelected == 5){
								mapSelected = (int) (Math.random() * 4);
								KambojaMain.setMapName(mapNames[mapSelected]);
							}
							
							targetPos = -100;
							startPos = 100;
							exiting = true;
							timer = 1;
							sound_select();
						}
					}
				}
			}
			if(buttonCode == backbtn){
				if(timer > 2){
					backPos = 100;
					exiting = true;
					goingBack = true;
					timer = 1;
					sound_select();
				}
			}
			if(buttonCode == startbtn){
				if(mapSelected != -1){
					
					tempRect.setFrame(Gdx.graphics.getWidth()/4 * 3 - 150, startPos, 300, 100);
					if(mapSelected == 5){
						mapSelected = (int) (Math.random() * 4);
						KambojaMain.setMapName(mapNames[mapSelected]);
					}
					targetPos = -100;
					startPos = 100;
					exiting = true;
					timer = 1;
					sound_select();
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

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

}
