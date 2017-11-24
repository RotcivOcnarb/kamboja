package com.mygdx.game.states;

import java.util.ArrayList;
import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.mygdx.game.KambojaMain;
import com.mygdx.game.Manager;
import com.mygdx.game.objects.Player;
import com.mygdx.game.objects.PlayerController;
import com.mygdx.game.objects.Util;

public class PostGameState extends GenericInterface{

	Texture[] main_frame = new Texture[4];
	Texture[] name_frame = new Texture[4];
	Texture[] player_frame = new Texture[4];
	Texture crown, crown_light;
	
	Texture capsule, capsule_light, progress_case, progress_bar;
	
	float timer;
	
	Body[] frame_body = new Body[4];
	
	FrameBuffer main_buffer[] = new FrameBuffer[4];
	Matrix4 bufferProjection;

	OrthographicCamera bufferCamera;
	
	BitmapFont oliver;
	
	ArrayList<PlayerController> players;
	
	public PostGameState(Manager manager) {
		super(manager);
	}
	
	public void create() {
		super.create();
		background = KambojaMain.getTexture("menu/postgame/fundo.jpg");
	
		oliver = KambojaMain.getInstance().getAssets().get("fonts/olivers barney.ttf", BitmapFont.class);
		
		capsule = KambojaMain.getTexture("menu/postgame/lvl_capsule.png");
		capsule_light = KambojaMain.getTexture("menu/postgame/lvl_light.png");
		progress_case = KambojaMain.getTexture("menu/postgame/bar_back.png");
		progress_bar = KambojaMain.getTexture("menu/postgame/bar_front.png");
		
		for(int i = 0; i < 4; i ++) {
			main_frame[i] = KambojaMain.getTexture("menu/postgame/f" + (i+1) + ".png");
			name_frame[i] = KambojaMain.getTexture("menu/postgame/n" + (i+1) + ".png");
			player_frame[i] = KambojaMain.getTexture("menu/postgame/p" + (i+1) + ".png");
			
			float margin = (Gdx.graphics.getWidth() - 4*main_frame[i].getWidth()*factor)/5f;
			frame_body[i] = createBox(new Vector2(
					margin + i*(main_frame[i].getWidth()*factor + margin) + main_frame[i].getWidth()*factor/2f,
					Gdx.graphics.getHeight()),
					new Vector2(350*factor/2f, 500*factor/2f), BodyType.DynamicBody, 0.01f);
			buildRopeJoint(13,
					frame_body[i],
					margin + i*(main_frame[i].getWidth()*factor + margin) + main_frame[i].getWidth()*factor/2f - Gdx.graphics.getWidth()/2f,
					(413/2f*factor),
					100*factor);
			main_buffer[i] = new FrameBuffer(Format.RGBA8888, (int)(350), (int)(500), false);
			
		}
		bufferProjection = new Matrix4();
		bufferProjection.setToOrtho2D(0, 0, (int)(350), (int)(500));
		
		bufferCamera = new OrthographicCamera(main_buffer[0].getWidth(), main_buffer[0].getHeight());
		bufferCamera.position.set(main_buffer[0].getWidth()/2f, main_buffer[0].getHeight()/2f, 0);
		bufferCamera.update();
		
		crown = KambojaMain.getTexture("menu/postgame/coroa.png");
		crown_light = KambojaMain.getTexture("menu/postgame/crown_light.png");
		
		players = new ArrayList<PlayerController>();
		
		players.add(new PlayerController(
				(int)(Math.random() * KambojaMain.getWeaponSize()),
				null, (int)(Math.random() * KambojaMain.getPlayerSkinsSize()), "Monark"));
		
//		KambojaMain.getPostGamePlayers().sort(new Comparator<Player>(){
//			public int compare(Player arg0, Player arg1) {
//				if(arg1.getKills() - arg0.getKills() == 0) {
//					return (int)(arg1.getScore() - arg0.getScore());
//				}
//				return  arg1.getKills() - arg0.getKills();
//			}
//		});
		
	}
	
	public void render(SpriteBatch sb) {
		for(int i = 0; i < 4; i ++) {
			main_buffer[i].begin();

			sb.begin();
			sb.setProjectionMatrix(bufferProjection);
			sb.draw(name_frame[i],
					(main_buffer[i].getWidth() - name_frame[i].getWidth())/2f, main_buffer[i].getHeight() - name_frame[i].getHeight() - 10,
					name_frame[i].getWidth(), name_frame[i].getHeight());
			
			oliver.draw(sb,
					"Custom Name",
					(main_buffer[i].getWidth() - name_frame[i].getWidth())/2f,
					main_buffer[i].getHeight() - name_frame[i].getHeight() + 40,
					name_frame[i].getWidth(), 1, false);
			
			sb.draw(player_frame[i],
					(main_buffer[i].getWidth() - player_frame[i].getWidth())/2f, main_buffer[i].getHeight() - player_frame[i].getHeight() - 100,
					player_frame[i].getWidth(), player_frame[i].getHeight());
			
			
			sb.end();
			main_buffer[i].end();
		}
		sb.setProjectionMatrix(Util.getNormalProjection());
		
		super.render(sb);
	}

	public void insideRender(SpriteBatch sb) {
		
		float margin = (Gdx.graphics.getWidth() - 4*main_frame[0].getWidth()*factor)/5f;
		
		sb.begin();
		for(int i = 0; i < 4; i++) {
			renderImageInBody(sb, main_frame[i], frame_body[i]);
			renderImageInBody(sb, main_buffer[i].getColorBufferTexture(), frame_body[i], true);
		}
		
		drawChains(sb);
		
		sb.setColor(1, 1, 1, (float)((Math.sin(timer*3) + 1)/2f * 0.5f + 0.5f));
		sb.draw(crown_light, margin + (main_frame[0].getWidth()*factor - crown_light.getWidth()*factor)/2f,
				Gdx.graphics.getHeight() - (Gdx.graphics.getHeight() - main_frame[0].getHeight()*factor)/2f,
				crown_light.getWidth()*factor, crown_light.getHeight()*factor);
		sb.setColor(1, 1, 1, 1);
		
		sb.draw(crown, margin + (main_frame[0].getWidth()*factor - crown.getWidth()*factor)/2f,
				Gdx.graphics.getHeight() - (Gdx.graphics.getHeight() - main_frame[0].getHeight()*factor)/2f,
				crown.getWidth()*factor, crown.getHeight()*factor);
		sb.draw(capsule, 50*factor, 50*factor, capsule.getWidth()*factor, capsule.getHeight()*factor);

		//TODO:
		/*
		 * Fazer física em cima dos frames [OK]
		 * Desenhar frame de nome e frame de aparencia
		 * desenhar barra de progresso
		 * desenhar botão de back
		 * fazer seleção
		 * colocar score nos players
		 * depois de um certo tempo o score dos players descem pra zero e o progress aumenta
		 * desenhar o nível atual
		 * quando upar de nivel, mostrar um frame caindo do céu mostrando o q a pessoa liberou
		 * refazer o sort de player, o primeiro é quem matou mais, se empatar com alguem filtra pelo score
		 * 
		 */
		
		sb.flush();
		
		b2dr.render(world, camera.combined);
		
		sb.end();
		
	}
	
	public void update(float delta) {
		super.update(delta);
		timer += delta;
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
