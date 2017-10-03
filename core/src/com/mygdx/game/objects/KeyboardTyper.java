package com.mygdx.game.objects;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.KambojaMain;
import com.mygdx.game.controllers.Gamecube;
import com.mygdx.game.controllers.Playstation3;
import com.mygdx.game.controllers.XBox;

public class KeyboardTyper {
	
	Vector2 position;
	
	String text = "";
	
	BitmapFont font;
	ShapeRenderer sr;
	GlyphLayout layout;
	
	float buttonSize[];
	
	String str[] = new String[]{
			"pqrs",	"ghi",	".,?!",	"tuv",
			"jkl",	"abc",	"wxyz",	"mno",
			"def",	"Ok!",	"",		"<-"};
	
	int rows = 3;
	int cols = 4;
	
	String lastLetter = "";
	FreeTypeFontGenerator ftfg;
	
	float width = Gdx.graphics.getWidth()/4 * 0.8f;
	float height = 120;
	
	int id;
	
	boolean show = false;
	
	public void dispose(){
		font.dispose();
		sr.dispose();
		ftfg.dispose();
	}
	
	public KeyboardTyper(Vector2 position, int id){
		this.position = position;
		this.id = id;
		
		lastLetter = "" + id;
		
		sr = new ShapeRenderer();
		
		ftfg = new FreeTypeFontGenerator(Gdx.files.internal("fonts/dot_to_dot.ttf"));
		FreeTypeFontParameter param = new FreeTypeFontParameter();
		param.size = (int) (50 * Gdx.graphics.getDensity());
		param.color = new Color(0.03f, 0.03f, 0.03f, 1);
		param.borderWidth = 2;
		param.borderColor = new Color(1, 0.9f, 0.9f, 1);
		param.shadowColor = new Color(0, 0, 0, 0.7f);
		param.shadowOffsetX = 3;
		param.shadowOffsetY = 3;
		font = ftfg.generateFont(param);
		
		buttonSize = new float[rows * cols];
		for(int i = 0; i < rows * cols ; i ++){
			buttonSize[i] = 0;
		}
		
		layout = new GlyphLayout();
	}
	
	public void show(){
		show = true;
	}
	
	public boolean isShowing(){
		return show;
	}
	
	public void hide(){
		show = false;
	}
	
	float quickTimer = 0;
	
	public void render(SpriteBatch sb){
		sb.begin();
		
			sr.begin(ShapeType.Filled);
				sr.setColor(0, 0, 1, 1);
				int i = 0; 
				for(int x = 0; x < cols; x ++){
					for(int y = 0; y < rows; y ++){
					sr.rect(position.x + x*(width/cols)  + width/cols/2 - (width/cols * buttonSize[i])/2, position.y + y*(height/rows) + height/rows/2 - (height/rows * buttonSize[i])/2, (width/cols * buttonSize[i]), (height/rows * buttonSize[i]));
					i++;
					}
				}
			sr.end();
			sr.begin(ShapeType.Line);
				sr.setColor(1, 1, 1, 1);
				i = 0; 
				for(int x = 0; x < cols; x ++){
					for(int y = 0; y < rows; y ++){
					sr.rect(position.x + x*(width/cols) + width/cols/2 - (width/cols * buttonSize[i])/2, position.y + y*(height/rows) + height/rows/2 - (height/rows * buttonSize[i])/2, (width/cols * buttonSize[i]), (height/rows * buttonSize[i]));
					i++;
					}
				}
			sr.end();
		sb.end();
		if(show){
			sb.begin();
				i = 0; 
				for(int x = 0; x < cols; x ++){
					for(int y = 0; y < rows; y ++){
					layout.setText(font, str[i]);
					font.draw(sb, str[i],
							position.x + x*(width/cols) + width/cols/2 - layout.width/2, 
							position.y + y*(height/rows) + height/rows/2 + layout.height/2);
					i++;
					}
				}
			sb.end();
		}
		
	}
	
	public void update(float delta){
		quickTimer -= delta;
		
		for(int i = 0; i < cols*rows; i ++){
			if(show){
				buttonSize[i] += (1-buttonSize[i])/10.0f;
			}
			else{
				buttonSize[i] += (-buttonSize[i])/10.0f;
			}
		}
	}
	
	public String addLetter(String letters, String name){
		if(quickTimer > 0){
			boolean found = false;
			for(int i = 0; i < letters.length(); i ++){
				if(lastLetter.equals(letters.substring(i, i+1))){
					name = name.substring(0, name.length() - 1);
					if(i+2 > letters.length()){
						name += letters.substring(0, 1);
						lastLetter = letters.substring(0, 1);
						found = true;
						quickTimer = 1f;
						break;
					}
					else{
						name += letters.substring(i+1, i+2);
						lastLetter = letters.substring(i+1, i+2);
						found = true;
						quickTimer = 1f;
						break;
					}
				}
			}
			if(!found){
				name += letters.substring(0, 1);
				lastLetter = letters.substring(0, 1);
				quickTimer = 1f;
			}
		}
		else{
			name += letters.substring(0, 1);
			lastLetter = letters.substring(0, 1);
			quickTimer = 1f;
		}
		return name;
	}
	
	public void executeButton(int id){
		String currentName = KambojaMain.getControllers().get(this.id).name;
		switch(id){
		case 0: //PQRS
			KambojaMain.getControllers().get(this.id).name =	addLetter("PQRS", currentName);
			break;
		case 1: //GHI
			KambojaMain.getControllers().get(this.id).name =	addLetter("GHI", currentName);
			break;
		case 2: //.,?!
			KambojaMain.getControllers().get(this.id).name =	addLetter(".,?!", currentName);
			break;
		case 3: // TUV
			KambojaMain.getControllers().get(this.id).name =	addLetter("TUV", currentName);
			break;
		case 4: //JKL
			KambojaMain.getControllers().get(this.id).name =	addLetter("JKL", currentName);
			break;
		case 5: //ABC
			KambojaMain.getControllers().get(this.id).name =	addLetter("ABC", currentName);
			break;
		case 6: //WXYZ
			KambojaMain.getControllers().get(this.id).name =	addLetter("WXYZ", currentName);
			break;
		case 7: //MNO
			KambojaMain.getControllers().get(this.id).name =	addLetter("MNO", currentName);
			break;
		case 8: //DEF
			KambojaMain.getControllers().get(this.id).name =	addLetter("DEF", currentName);
			break;
		case 9: //Ok!
			hide();
			break;
		case 10: //Space
			KambojaMain.getControllers().get(this.id).name += " ";
			break;
		case 11: //Apagar
			if(KambojaMain.getControllers().get(this.id).name.length() > 0){
				KambojaMain.getControllers().get(this.id).name = KambojaMain.getControllers().get(this.id).name.substring(0, KambojaMain.getControllers().get(this.id).name.length()-1);
			}
			break;
		}
		
		if(KambojaMain.getControllers().get(this.id).name.length() > 10){
			KambojaMain.getControllers().get(this.id).name = KambojaMain.getControllers().get(this.id).name.substring(0, 10);
		}
	}
	
	Point2D pointer = new Point2D.Double();
	Rectangle2D rect = new Rectangle2D.Double();
	public boolean buttonDown(Controller controller, int buttonCode, float x, float y) {	
		if(show){
			
			if(controller == null){
				
				int id = 0;
				for(PlayerController pc : KambojaMain.getControllers()){
					if(pc instanceof KeyboardController){
						break;
					}
					id++;
				}
				
				if(this.id == id){
					pointer.setLocation(x + 16, y + 16);
					
					int i = 0; 
					for(int xc = 0; xc < cols; xc ++){
						for(int yc = 0; yc < rows; yc ++){
							rect.setRect(position.x + xc*width/cols, position.y + yc*height/rows, width/cols, height/cols);
		
							if(rect.contains(pointer)){
								buttonSize[i] = 1.4f;
								
								executeButton(i);
								
								return true;
							}
							i++;
						}
					}
				}
				
				return false;
			}
			
			int select = 0;
			if(controller.getName().equals(Gamecube.getID())){
				select = Gamecube.A;
			}
			else if(controller.getName().toUpperCase().contains("XBOX") && controller.getName().contains("360")){
				select = XBox.BUTTON_A;
			}
			else if(controller.getName().toUpperCase().contains("SONY") || controller.getName().toUpperCase().contains("PLAYSTATION")){
				select = Playstation3.X;
			}
			
			if(buttonCode == select){
				int id = Util.getControllerID(controller);
				if(this.id == id){
					pointer.setLocation(x + 16, y + 16);
					
					int i = 0; 
					for(int xc = 0; xc < cols; xc ++){
						for(int yc = 0; yc < rows; yc ++){
							rect.setRect(position.x + xc*width/cols, position.y + yc*height/rows, width/cols, height/cols);
		
							if(rect.contains(pointer)){
								buttonSize[i] = 1.4f;
								
								executeButton(i);
								
								return true;
							}
							i++;
						}
					}
				}
			}
		}
		return false;
	}
	

}
