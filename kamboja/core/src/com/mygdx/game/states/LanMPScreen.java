package com.mygdx.game.states;

import java.awt.geom.Rectangle2D;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
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
import com.mygdx.game.KambojaMain;
import com.mygdx.game.Manager;
import com.mygdx.game.multiplayer.KambojaConnectionListener;
import com.mygdx.game.multiplayer.KambojaPacket;
import com.mygdx.game.objects.Util;

public class LanMPScreen extends GenericInterface implements KambojaConnectionListener{

	BitmapFont olivers_barney;
	BitmapFont olivers_barney_big;
	GlyphLayout layout;
	
	Texture screenContainer;
	Texture ipHoldContainer;
	Texture back_cog;
	Texture selection_tex;
	Texture host_cog;
	Texture connect_cog;
	
	float back_cog_angle;
	float back_cog_speed;
	
	float host_cog_angle;
	float host_cog_speed;
	
	float connect_cog_angle;
	float connect_cog_speed;
	
	Rectangle2D[] selection_bounds;
	Rectangle2D selection_bound_tween;
	int selectedRect = 1;
	
	String rawIP;
			
	public LanMPScreen(Manager manager) {
		super(manager);
		
		background = KambojaMain.getTexture("menu/credits/fundo.jpg");
		
		FreeTypeFontGenerator ftfg;
		FreeTypeFontParameter param;
		ftfg = new FreeTypeFontGenerator(Gdx.files.internal("fonts/olivers barney.ttf"));
		param = new FreeTypeFontParameter();
		param.size = (int) (80f);
		param.color = new Color(171/255f, 205/255f, 230/255f, 1);
		olivers_barney = ftfg.generateFont(param);
		param.size = (int) (70);
		param.color = new Color(127/255f, 176/255f, 210/255f, 1);
		olivers_barney_big = ftfg.generateFont(param);
		ftfg.dispose();
		
		layout = new GlyphLayout();
		
		screenContainer = KambojaMain.getTexture("menu/main_menu/mp_container.png");
		ipHoldContainer = KambojaMain.getTexture("menu/main_menu/mp_ip_container.png");
		back_cog = KambojaMain.getTexture("menu/help/engrenagem.png");
		selection_tex = KambojaMain.getTexture("menu/player_select/selection.png");
		host_cog = KambojaMain.getTexture("menu/main_menu/host_cog.png");
		connect_cog = KambojaMain.getTexture("menu/main_menu/connect_cog.png");

		selection_bounds = new Rectangle2D[3];
		
		selection_bounds[0] = new Rectangle2D.Float(80, -200, back_cog.getWidth() + 40, 400);
		selection_bounds[1] = new Rectangle2D.Float(1920 / 2 - connect_cog.getWidth()/2f - 20, -100, connect_cog.getWidth() + 40, 400);
		selection_bounds[2] = new Rectangle2D.Float(1920 - host_cog.getWidth() - 100 + 20, -170, host_cog.getWidth()*0.8f + 40, 400);
		 
		
		selection_bound_tween = (Rectangle2D) selection_bounds[selectedRect].clone();
		
		rawIP = "";
	}
	
	@Override
	public void create() {
		super.create();
		back_cog_angle = 0;
		back_cog_speed = 0;
		
		host_cog_angle = 0;
		host_cog_speed = 0;
		
		connect_cog_angle = 0;
		connect_cog_speed = 0;
	}
	
	public void insideRender(SpriteBatch sb) {
		
		sb.setProjectionMatrix(Util.getNormalProjection());
		sb.begin();
		
		sb.draw(screenContainer, 0, 0, 1920, 1080);
		sb.draw(ipHoldContainer, 0, 0, 1920, 1080);
		layout.setText(olivers_barney, rawIP);
		olivers_barney.draw(sb, rawIP, 1920/2 - layout.width / 2, 1080 - 200);
		
		sb.draw(back_cog,
				100,
				-back_cog.getHeight()/2f,
				back_cog.getWidth()/2f,
				back_cog.getHeight()/2f,
				back_cog.getWidth(),
				back_cog.getHeight(),
				1, 1,
				back_cog_angle,
				0, 0,
				back_cog.getWidth(),
				back_cog.getHeight(),
				false, false);
		
		sb.draw(connect_cog,
				1920 / 2 - connect_cog.getWidth()/2f,
				-connect_cog.getHeight()/2f,
				connect_cog.getWidth()/2f,
				connect_cog.getHeight()/2f,
				connect_cog.getWidth(),
				connect_cog.getHeight(),
				1, 1,
				connect_cog_angle,
				0, 0,
				connect_cog.getWidth(),
				connect_cog.getHeight(),
				false, false);
		
		sb.draw(host_cog,
				1920 - host_cog.getWidth() - 100,
				-host_cog.getHeight()/2f,
				host_cog.getWidth()/2f,
				host_cog.getHeight()/2f,
				host_cog.getWidth(),
				host_cog.getHeight(),
				.8f, .8f,
				host_cog_angle,
				0, 0,
				host_cog.getWidth(),
				host_cog.getHeight(),
				false, false);
				
		Rectangle2D boundingBox = selection_bound_tween;
		
		//UPPER LEFT
		sb.draw(
				selection_tex,
				(float)boundingBox.getX(),
				(float)(boundingBox.getY() + boundingBox.getHeight()) - selection_tex.getHeight(),
				(float)boundingBox.getWidth()/2f,
				-(float)boundingBox.getHeight()/2f,
				selection_tex.getWidth(),
				selection_tex.getHeight(),
				1,
				1,
				0,
				0,
				0,
				selection_tex.getWidth(),
				selection_tex.getHeight(),
				true,
				false);
		
		//UPPER RIGHT
		sb.draw(
				selection_tex,
				(float)(boundingBox.getX() + boundingBox.getWidth()) - selection_tex.getWidth(),
				(float)(boundingBox.getY() + boundingBox.getHeight()) - selection_tex.getHeight(),
				-(float)boundingBox.getWidth()/2f,
				-(float)boundingBox.getHeight()/2f,
				selection_tex.getWidth(),
				selection_tex.getHeight(),
				1,
				1,
				0,
				0,
				0,
				selection_tex.getWidth(),
				selection_tex.getHeight(),
				false,
				false);
		
		//BOTTOM LEFT
		sb.draw(
				selection_tex,
				(float)boundingBox.getX(),
				(float)boundingBox.getY(),
				(float)boundingBox.getWidth()/2f,
				(float)boundingBox.getHeight()/2f,
				selection_tex.getWidth(),
				selection_tex.getHeight(),
				1,
				1,
				0,
				0,
				0,
				selection_tex.getWidth(),
				selection_tex.getHeight(),
				true,
				true);
		
		//BOTTOM RIGHT
		sb.draw(
				selection_tex,
				(float)(boundingBox.getX() + boundingBox.getWidth()) - selection_tex.getWidth(),
				(float)boundingBox.getY(),
				-(float)boundingBox.getWidth()/2f,
				(float)boundingBox.getHeight()/2f,
				selection_tex.getWidth(),
				selection_tex.getHeight(),
				1,
				1,
				0,
				0,
				0,
				selection_tex.getWidth(),
				selection_tex.getHeight(),
				false,
				true);
		
		sb.end();
		
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		back_cog_angle += back_cog_speed;
		host_cog_angle += host_cog_speed;
		connect_cog_angle += connect_cog_speed;
		
		if(outro) {
			switch(selectedRect) {
			case 0:
				back_cog_speed += delta*10f;
				break;
			case 1:
				connect_cog_speed += delta*10f;
				break;
			case 2:
				host_cog_speed += delta*10f;
				break;
			}
		}
		
		selection_bound_tween.setRect(
				selection_bound_tween.getX() + (selection_bounds[selectedRect].getX() - selection_bound_tween.getX()) / 5f,
				selection_bound_tween.getY() + (selection_bounds[selectedRect].getY() - selection_bound_tween.getY()) / 5f,
				selection_bound_tween.getWidth() + (selection_bounds[selectedRect].getWidth() - selection_bound_tween.getWidth()) / 5f,
				selection_bound_tween.getHeight() + (selection_bounds[selectedRect].getHeight() - selection_bound_tween.getHeight()) / 5f
			);
	}

	public void changeScreen() {
		switch (selectedRect) {
		case 0:
			manager.changeState(Manager.MENU_STATE);
			break;
		case 1:
			KambojaMain.getInstance().createClientConnection(rawIP, this);
			break;
		case 2:
			
			KambojaMain.getInstance().createHostConnection(this);
			
			manager.changeState(Manager.PLAYER_SELECT_STATE);
			
			break;
		}
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
	
	public void doSelection() {
		outro = true;
		intro = false;
	}
	
	@Override
	public boolean keyDown(int keyCode) {
		
		if(keyCode >= Keys.NUM_0 && keyCode <= Keys.NUM_9) {
			int number = keyCode - 7;
			rawIP += number + "";
		}
		if(keyCode == Keys.PERIOD) {
			rawIP += ".";
		}
		if(keyCode == Keys.BACKSPACE) {
			if(rawIP.length() > 0)
				rawIP = rawIP.substring(0, rawIP.length()-1);
		}
		
		if(rawIP.length() > 15) {
			rawIP = rawIP.substring(0, 15);
		}
		
		if(keyCode == Keys.RIGHT) {
			switch(selectedRect) {
			case 0:
				selectedRect = 1;
				break;
			case 1:
				selectedRect = 2;
				break;
			case 2:
				break;
			}
		}
		if(keyCode == Keys.LEFT) {
			switch(selectedRect) {
			case 1:
				selectedRect = 0;
				break;
			case 2:
				selectedRect = 1;
				break;
			}
		}
		
		if(keyCode == Keys.ENTER) {
			doSelection();
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

	@Override
	public void receiveUDP(KambojaPacket data) {
		System.out.println("I just received an UDP package!");
	}

	@Override
	public void receiveTCP(KambojaPacket data) {
		System.out.println("I just received a TCP package! type: " + data.type);
	}
	
	@Override
	public void connected() {
		System.out.println("I have connected successfully!");
		manager.changeState(Manager.PLAYER_SELECT_STATE);
	}

	@Override
	public void disconnected() {
		System.out.println("I have been disconnected");
	}

	@Override
	public void connectionFailed(String message) {
		System.out.println(message);
	}

	

}
