package com.mygdx.game.states;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.KambojaMain;
import com.mygdx.game.Manager;
import com.mygdx.game.controllers.Gamecube;
import com.mygdx.game.controllers.GenericController;
import com.mygdx.game.controllers.XBox;

public class CreditsState extends GenericInterface{

	String credits_string;

	BitmapFont olivers_barney;
	BitmapFont olivers_barney_big;
	GlyphLayout layout;
	
	Texture sign;
	Texture cano_d, cano_e_b, cano_e_c, chain_big, chain_small;
	
	PooledEffect fumaca[] = new PooledEffect[3];
	
	float credit_y = 0;
	Document doc;
	public CreditsState(Manager manager) {
		super(manager);
		
		background = KambojaMain.getTexture("menu/credits/fundo.jpg");
		credits_string = Gdx.files.internal("credits.txt").readString();

		sign = KambojaMain.getTexture("menu/credits/placa.png");
		
		doc = Jsoup.parse(credits_string);
		
		cano_d = KambojaMain.getTexture("menu/credits/cano_d.png");
		cano_e_b = KambojaMain.getTexture("menu/credits/cano_e_b.png");
		cano_e_c = KambojaMain.getTexture("menu/credits/cano_e_c.png");
		chain_big = KambojaMain.getTexture("menu/credits/chain_big.png");
		chain_small = KambojaMain.getTexture("menu/credits/chain_small.png");
		
		FreeTypeFontGenerator ftfg;
		FreeTypeFontParameter param;
		ftfg = new FreeTypeFontGenerator(Gdx.files.internal("fonts/olivers barney.ttf"));
		param = new FreeTypeFontParameter();
		param.size = (int) (50f);
		param.color = new Color(171/255f, 205/255f, 230/255f, 1);
		olivers_barney = ftfg.generateFont(param);
		param.size = (int) (70);
		param.color = new Color(127/255f, 176/255f, 210/255f, 1);
		olivers_barney_big = ftfg.generateFont(param);
		ftfg.dispose();
		
		for(int i = 0; i < 3; i ++) {
			fumaca[i] = cano_pool.obtain();
		}
		
		fumaca[1].getEmitters().get(0).getAngle().setHighMin(20);
		fumaca[1].getEmitters().get(0).getAngle().setHighMax(30);
		
		fumaca[2].getEmitters().get(0).getAngle().setHighMin(130);
		fumaca[2].getEmitters().get(0).getAngle().setHighMax(140);
	}
	
	@Override
	public void create() {
		super.create();
		credit_y = -3620;
		KambojaMain.screenView(this);
	}

	public void insideRender(SpriteBatch sb) {
		
		sb.begin();
			float dy = 0;
			for(int i = doc.getAllElements().size() - 1; i >= 0; i --) {
				Element e = doc.getAllElements().get(i);
				dy += 60;
				if(e.nodeName().equals("title")) {
					dy += 40;
					olivers_barney_big.draw(sb, e.ownText(), 0, 1080 + credit_y + dy, 1920, 1, true);
				}
				else if(e.nodeName().equals("subtitle")) {
					dy += 80;
					olivers_barney_big.draw(sb, e.ownText(), 0, 1080 + credit_y + dy, 1920, 1, true);
				}
				else if(e.nodeName().equals("info")) {
					olivers_barney.draw(sb, e.ownText(), 0, 1080 + credit_y + dy, 1920, 1, true);
				}
			}
			
			sb.draw(sign, (1920 - sign.getWidth())/2f, 1080 - sign.getHeight(),
					sign.getWidth(), sign.getHeight());
			

			sb.draw(cano_e_b, 0, 1080 - cano_e_b.getHeight(),
					cano_e_b.getWidth(), cano_e_b.getHeight());
			
			sb.draw(cano_e_c, 0, 1080 - cano_e_c.getHeight(),
					cano_e_c.getWidth(), cano_e_c.getHeight());
			
			sb.draw(cano_d, 1920 - cano_d.getWidth(), 1080 - cano_d.getHeight(),
					cano_d.getWidth(), cano_d.getHeight());
			
			sb.flush();
			
			 fumaca[0].setPosition(240, 360);
			 fumaca[0].draw(sb, Gdx.graphics.getDeltaTime());
			
			 fumaca[1].setPosition(340, 230);
			 fumaca[1].draw(sb, Gdx.graphics.getDeltaTime());
		
			 fumaca[2].setPosition(1770, 390);
			 fumaca[2].draw(sb, Gdx.graphics.getDeltaTime());
			
		sb.end();
		
	}
	
	public void update(float delta) {
		super.update(delta);
		credit_y += delta*100;
		
		
	}

	public void changeScreen() {
		manager.changeState(Manager.MENU_STATE);
	}

	public void dispose() {
		
	}

	public void connected(Controller controller) {
		
	}

	public void disconnected(Controller controller) {
		
	}

	public boolean buttonDown(Controller controller, int buttonCode) {
		int A = 0;
		
		if(controller.getName().toUpperCase().contains("XBOX") && controller.getName().contains("360")){
			A = XBox.BUTTON_START;
		}
		else if(controller.getName().equals(Gamecube.getID())){
			A = Gamecube.START;
		}
		else{
			A = GenericController.START;
		}
		
		if(buttonCode == A){
			intro = false;
			outro = true;
		}
		return false;
	}
	
	@Override
	public boolean keyDown(int keyCode) {
		if(keyCode == Keys.ENTER) {
			intro = false;
			outro = true;
		}
		return false;
	}

	public boolean buttonUp(Controller controller, int buttonCode) {
		return false;
	}

	public boolean axisMoved(Controller controller, int axisCode, float value) {
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
		
	}

}
