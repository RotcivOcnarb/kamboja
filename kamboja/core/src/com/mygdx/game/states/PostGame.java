package com.mygdx.game.states;

import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
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
import com.mygdx.game.controllers.GenericController;
import com.mygdx.game.controllers.Playstation3;
import com.mygdx.game.controllers.XBox;
import com.mygdx.game.objects.Background;
import com.mygdx.game.objects.BotController;
import com.mygdx.game.objects.Player;
import com.mygdx.game.objects.Util;

public class PostGame extends State{
	
	private Background background;
	private float timer;
	private BitmapFont first;
	private BitmapFont other;
	private GlyphLayout layout;
	private float pos = 0;
	private float targetPos = 0;
	
	private Sound sound_select;

	public PostGame(Manager manager) {
		super(manager);

	}
	
	public void dispose(){
		background.dispose();
		first.dispose();
		other.dispose();
		sound_select.dispose();
	}

	public void create() {
		botTimer = 0;
		timer = -1;

		sound_select = Gdx.audio.newSound(Gdx.files.internal("audio/select.ogg"));
		
		background = new Background();
		
		pos = -1000;
		targetPos = 0;
		
		if(layout == null)
		layout = new GlyphLayout();
		
		FreeTypeFontGenerator ftfg;
		FreeTypeFontParameter param;
		ftfg = new FreeTypeFontGenerator(Gdx.files.internal("fonts/dot_to_dot.ttf"));
		param = new FreeTypeFontParameter();
		param.size = (int) (300 * Gdx.graphics.getDensity());
		param.color = new Color(255/255f, 201/255f, 14/255f, 1);
		param.borderWidth = 2;
		param.borderColor = new Color(1, 0.9f, 0.9f, 1);
		param.shadowColor = new Color(0, 0, 0, 0.7f);
		param.shadowOffsetX = 3;
		param.shadowOffsetY = 3;
		first = ftfg.generateFont(param);
		
		param.size = (int) (150 * Gdx.graphics.getDensity());
		param.color = new Color(0.03f, 0.03f, 0.03f, 1);
		other = ftfg.generateFont(param);
		
		ftfg.dispose();

		KambojaMain.getPostGamePlayers().sort(new Comparator<Player>(){
			public int compare(Player arg0, Player arg1) {
				return (int) (((float)arg1.getKills()/(float)arg1.getDeaths() - (float)arg0.getKills()/(float)arg1.getDeaths()));
			}
		});
		
	}
	
	float botTimer = 0;

	public void render(SpriteBatch sb) {
		background.render(sb);

			int botcont = 0;
			
			for(int i = 0; i < KambojaMain.getControllers().size(); i++){
				if(KambojaMain.getControllers().get(i) instanceof BotController){
					botcont ++;
				}
			}
			
			if(botcont >= 4 && botTimer >= 1){
				manager.changeState(1);
				if(GameState.SFX)
				sound_select.play();
				
				return;
			}
		
		
		sb.begin();

		
		sb.setColor(1, 1, 1, 1);
		sb.setProjectionMatrix(Util.getNormalProjection());
		
		for(int i = 0; i < KambojaMain.getPostGamePlayers().size(); i ++){
			Player p = KambojaMain.getPostGamePlayers().get(i);
			if(KambojaMain.getPostGamePlayers().get(0).equals(p)){
				String text = KambojaMain.getControllers().get(p.getID()).getName() + ": " + p.getKills() + "/" + p.getDeaths();
				layout.setText(first, text);
				first.draw(sb, text, (Gdx.graphics.getWidth() - layout.width)/2f - pos, Gdx.graphics.getHeight() - i * (Gdx.graphics.getHeight() /4f) - Gdx.graphics.getHeight() /8f);
			}
			else{
				String text = KambojaMain.getControllers().get(p.getID()).getName() + ": " + p.getKills() + "/" + p.getDeaths();
				layout.setText(other, text);
				other.draw(sb, text, (Gdx.graphics.getWidth() - layout.width)/2f - pos, Gdx.graphics.getHeight() - i * (Gdx.graphics.getHeight() /4f) - Gdx.graphics.getHeight() /8f);
			}
		}
		sb.end();
	}

	public void update(float delta) {
		timer += delta;
		botTimer += delta;
		pos += (targetPos - pos)/10.0f;
		
		background.update(delta, false);
		
		if(Gdx.input.isKeyJustPressed(Keys.ENTER)){
			manager.changeState(1);
			if(GameState.SFX)
			sound_select.play();
			
			return;
		}
	}

	public void connected(Controller controller) {
		
	}

	public void disconnected(Controller controller) {
		
	}

	public boolean buttonDown(Controller controller, int buttonCode) {
		int id = Util.getControllerID(controller);
		if(id != -1){
			if(Util.getControllerID(controller) != -1){
				int start = 0;
				if(controller.getName().equals(Gamecube.getID())){
					start = Gamecube.START;
				}
				else if(controller.getName().toUpperCase().contains("XBOX") && controller.getName().contains("360")){
					start = XBox.BUTTON_START;
				}
				else if(controller.getName().toUpperCase().contains("SONY") || controller.getName().toUpperCase().contains("PLAYSTATION")){
					start = Playstation3.START;
				}
				else{
					start = GenericController.START;
				}
				
				
				if(buttonCode == start){
					manager.changeState(1);
					if(GameState.SFX)
					sound_select.play();
						
					return false;
				}
				
			}
		}
		return false;
	}

	public boolean buttonUp(Controller controller, int buttonCode) {
		return false;
	}
	float laxis = 0;
	float raxis = 0;
	public boolean axisMoved(Controller controller, int axisCode, float value) {
		if(controller.getName().equals(Gamecube.getID())){
			if(axisCode == Gamecube.ANAL_L){
				laxis = value;
			}
			if(axisCode == Gamecube.ANAL_R){
				raxis = value;
			}
			if(laxis > 0.7f && raxis > 0.7f){
				
			}
		}
		else if(controller.getName().toUpperCase().contains("XBOX") && controller.getName().contains("360")){
			if(axisCode == XBox.AXIS_LEFT_TRIGGER){
				laxis = value;
			}
			if(axisCode == XBox.AXIS_RIGHT_TRIGGER){
				raxis = value;
			}
			if(laxis > 0.7f && raxis > 0.7f){
				
			}
		}
		else if(controller.getName().toUpperCase().contains("SONY") || controller.getName().toUpperCase().contains("PLAYSTATION")){
			if(axisCode == Playstation3.L2){
				laxis = value;
			}
			if(axisCode == Playstation3.R2){
				raxis = value;
			}
			if(laxis > 0.7f && raxis > 0.7f){
				
			}
		}
		
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
