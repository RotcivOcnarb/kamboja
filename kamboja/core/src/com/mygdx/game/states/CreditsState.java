package com.mygdx.game.states;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Manager;

public class CreditsState extends GenericInterface{

	String credits_string;

	BitmapFont olivers_barney;
	BitmapFont olivers_barney_big;
	GlyphLayout layout;
	
	Texture sign;
	Texture cano_d, cano_e_b, cano_e_c, chain_big, chain_small;
	
	float credit_y = 0;
	Document doc;
	public CreditsState(Manager manager) {
		super(manager);
	}
	
	@Override
	public void create() {
		super.create();
		background = new Texture("menu/credits/fundo.jpg");
		credits_string = Gdx.files.internal("credits.txt").readString();
		
		sign = new Texture("menu/credits/placa.png");
		
		doc = Jsoup.parse(credits_string);
		
		credit_y = -3620*factor;
		
		cano_d = new Texture("menu/credits/cano_d.png");
		cano_e_b = new Texture("menu/credits/cano_e_b.png");
		cano_e_c = new Texture("menu/credits/cano_e_c.png");
		chain_big = new Texture("menu/credits/chain_big.png");
		chain_small = new Texture("menu/credits/chain_small.png");
		
		FreeTypeFontGenerator ftfg;
		FreeTypeFontParameter param;
		ftfg = new FreeTypeFontGenerator(Gdx.files.internal("fonts/olivers barney.ttf"));
		param = new FreeTypeFontParameter();
		param.size = (int) (50f * factor);
		param.color = new Color(171/255f, 205/255f, 230/255f, 1);
		olivers_barney = ftfg.generateFont(param);
		param.size = (int) (70 * factor);
		param.color = new Color(127/255f, 176/255f, 210/255f, 1);
		olivers_barney_big = ftfg.generateFont(param);
		ftfg.dispose();
	}

	public void insideRender(SpriteBatch sb) {
		
		sb.begin();
			float dy = 0;
			for(int i = doc.getAllElements().size() - 1; i >= 0; i --) {
				Element e = doc.getAllElements().get(i);
				dy += 60;
				if(e.nodeName().equals("title")) {
					dy += 40;
					olivers_barney_big.draw(sb, e.ownText(), 0, Gdx.graphics.getHeight() + credit_y + dy*factor, Gdx.graphics.getWidth(), 1, true);
				}
				else if(e.nodeName().equals("subtitle")) {
					dy += 80;
					olivers_barney_big.draw(sb, e.ownText(), 0, Gdx.graphics.getHeight() + credit_y + dy*factor, Gdx.graphics.getWidth(), 1, true);
				}
				else if(e.nodeName().equals("info")) {
					olivers_barney.draw(sb, e.ownText(), 0, Gdx.graphics.getHeight() + credit_y + dy*factor, Gdx.graphics.getWidth(), 1, true);
				}
			}
			
			sb.draw(sign, (Gdx.graphics.getWidth() - sign.getWidth()*factor)/2f, Gdx.graphics.getHeight() - sign.getHeight()*factor,
					sign.getWidth()*factor, sign.getHeight()*factor);
			

			sb.draw(cano_e_b, 0, Gdx.graphics.getHeight() - cano_e_b.getHeight()*factor,
					cano_e_b.getWidth()*factor, cano_e_b.getHeight()*factor);
			
			sb.draw(cano_e_c, 0, Gdx.graphics.getHeight() - cano_e_c.getHeight()*factor,
					cano_e_c.getWidth()*factor, cano_e_c.getHeight()*factor);
			
			sb.draw(cano_d, Gdx.graphics.getWidth() - cano_d.getWidth()*factor, Gdx.graphics.getHeight() - cano_d.getHeight()*factor,
					cano_d.getWidth()*factor, cano_d.getHeight()*factor);
			
		sb.end();
		
	}
	
	public void update(float delta) {
		super.update(delta);
		System.out.println(credit_y);
		credit_y += delta*100;
		
		
	}

	public void changeScreen() {
		
	}

	public void dispose() {
		
	}

	public void connected(Controller controller) {
		
	}

	public void disconnected(Controller controller) {
		
	}

	public boolean buttonDown(Controller controller, int buttonCode) {
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
