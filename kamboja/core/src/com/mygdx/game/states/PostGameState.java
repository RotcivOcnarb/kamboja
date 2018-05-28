package com.mygdx.game.states;

import java.io.File;
import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.mygdx.game.KambojaMain;
import com.mygdx.game.Manager;
import com.mygdx.game.objects.GameMusic;
import com.mygdx.game.objects.Player;
import com.mygdx.game.objects.Util;

public class PostGameState extends GenericInterface{

	Texture[] main_frame = new Texture[4];
	Texture[] name_frame = new Texture[4];
	Texture[] player_frame = new Texture[4];
	Texture crown, crown_light;
	Texture kill_icon, death_icon;
	Texture unlockedText;
	Texture unlockedImage;
	
	Texture capsule, capsule_light, progress_case, progress_bar;
		
	Body[] frame_body = new Body[4];
	
	FrameBuffer main_buffer[] = new FrameBuffer[4];
	Matrix4 bufferProjection;

	OrthographicCamera bufferCamera;
	
	BitmapFont oliver;
	BitmapFont oliverFrag, oliverScore;
	ShaderProgram waveShader;
	Texture[] inGameWep;
	GlyphLayout layout;

	boolean increasingEXP = true;
	int unlockable_frame_selection = -1;
	Unlockable nextUnlockable;
	float textTween;
	float imageTween;
	
	enum Unlockable{
		Island("Island", "Map", "maps/thumb_Disland.png"),
		Iceland("Iceland", "Map", "maps/thumb_Eiceland.png"),
		Volcan("Volcan", "Map", "maps/thumb_Fvolcan.png"),
		Space("Space", "Map", "maps/thumb_Gspace.png"),
		MP5("MP5", "Weapon", "Weapons/Icon/Mp5.png"),
		Shotgun("Shotgun", "Weapon", "Weapons/Icon/shotgun.png"),
		Flamethrower("Flamethrower", "Weapon", "Weapons/Icon/Flamethrower.png"),
		Minigun("Minigun", "Weapon", "Weapons/Icon/minigun.png"),
		Bazooka("Bazooka", "Weapon", "Weapons/Icon/Bazook.png"),
		Laser("Laser", "Weapon", "Weapons/Icon/Laser.png");
		
		public String nome;
		public String tipo;
		public Texture imagem;
		
		private Unlockable(String nome, String tipo, String imagem) {
			this.nome = nome;
			this.tipo = tipo;
			
			if(tipo.equals("Map")) {
				this.imagem = new Texture(new FileHandle(new File(imagem)));
			}
			else {
				this.imagem = KambojaMain.getTexture(imagem);
			}

		}

	}
	
	public void unlock() {
		if(nextUnlockable != null) {
			switch (nextUnlockable){
			case Island:
				KambojaMain.mapUnlocked[3] = true;
				break;
			case Iceland:
				KambojaMain.mapUnlocked[4] = true;
				break;
			case Volcan:
				KambojaMain.mapUnlocked[5] = true;
				break;
			case Space:
				KambojaMain.mapUnlocked[6] = true;
				break;
			case MP5:
				KambojaMain.weaponUnlocked[4] = true;
				break;
			case Shotgun:
				KambojaMain.weaponUnlocked[3] = true;
				break;
			case Flamethrower:
				KambojaMain.weaponUnlocked[5] = true;
				break;
			case Minigun:
				KambojaMain.weaponUnlocked[2] = true;
				break;
			case Bazooka:
				KambojaMain.weaponUnlocked[6] = true;
				break;
			case Laser:
				KambojaMain.weaponUnlocked[7] = true;
				break;
			}
		}
	}
	
	public PostGameState(Manager manager) {
		super(manager);
		
		background = KambojaMain.getTexture("menu/postgame/fundo.jpg");
		
		oliver = KambojaMain.getInstance().getAssets().get("fonts/olivers barney.ttf", BitmapFont.class);
		
		unlockedText = KambojaMain.getTexture("menu/map_select/map_name.png");
		unlockedImage = KambojaMain.getTexture("menu/map_select/frame_map.png");
		
		FreeTypeFontGenerator ftfg;
		FreeTypeFontParameter param;
		ftfg = new FreeTypeFontGenerator(Gdx.files.internal("fonts/olivers barney.ttf"));
		param = new FreeTypeFontParameter();
		param.size = 50;
		param.color = new Color(255/255f, 38/255f, 66/255f, 1);
		oliverFrag = ftfg.generateFont(param);
		param.size = 40;
		param.color = new Color(212/255f, 231/255f, 247/255f, 1);
		oliverScore = ftfg.generateFont(param);
		ftfg.dispose();	
		
		waveShader = new ShaderProgram(Gdx.files.internal("shaders/default.vs"), Gdx.files.internal("shaders/wave.fs"));
		if(waveShader.getLog().length() > 0) {
			System.out.println(waveShader.getLog());
		}
		
		inGameWep = new Texture[KambojaMain.getWeaponSize()];
		inGameWep[0] = KambojaMain.getTexture("Weapons/In-game/Taurus.png");
		inGameWep[1] = KambojaMain.getTexture("Weapons/In-game/Taurus Akimbo.png");
		inGameWep[2] = KambojaMain.getTexture("Weapons/In-game/Minigun.png");
		inGameWep[3] = KambojaMain.getTexture("Weapons/In-game/sss.png");
		inGameWep[4] = KambojaMain.getTexture("Weapons/In-game/MP5.png");
		inGameWep[5] = KambojaMain.getTexture("Weapons/In-game/flahme.png");
		inGameWep[6] = KambojaMain.getTexture("Weapons/In-game/Bazooka.png");
		inGameWep[7] = KambojaMain.getTexture("Weapons/In-game/Laser.png");
		
		kill_icon = KambojaMain.getTexture("menu/postgame/icon_weapon.png");
		death_icon = KambojaMain.getTexture("menu/postgame/icon_skull.png");

		capsule = KambojaMain.getTexture("menu/postgame/lvl_capsule.png");
		capsule_light = KambojaMain.getTexture("menu/postgame/lvl_light.png");
		progress_case = KambojaMain.getTexture("menu/postgame/bar_back.png");
		progress_bar = KambojaMain.getTexture("menu/postgame/bar_front.png");
		
		
		for(int i = 0; i < 4; i ++) {
			main_frame[i] = KambojaMain.getTexture("menu/postgame/f" + (i+1) + ".png");
			name_frame[i] = KambojaMain.getTexture("menu/postgame/n" + (i+1) + ".png");
			player_frame[i] = KambojaMain.getTexture("menu/postgame/p" + (i+1) + ".png");
			main_buffer[i] = new FrameBuffer(Format.RGBA8888, (int)(350), (int)(500), false);
			
		}
		
		bufferProjection = new Matrix4();
		bufferProjection.setToOrtho2D(0, 0, (int)(350), (int)(500));
		
		bufferCamera = new OrthographicCamera(main_buffer[0].getWidth(), main_buffer[0].getHeight());
		bufferCamera.position.set(main_buffer[0].getWidth()/2f, main_buffer[0].getHeight()/2f, 0);
		bufferCamera.update();
		
		crown = KambojaMain.getTexture("menu/postgame/coroa.png");
		crown_light = KambojaMain.getTexture("menu/postgame/crown_light.png");

		nextUnlockable = getNextUnlockable();
		GameMusic.playMenuSong();

	}
	
	public Unlockable getNextUnlockable() {

		switch(KambojaMain.level) {
		case 1:
			return Unlockable.MP5;
		case 2:
			return Unlockable.Island;
		case 3:
			return Unlockable.Shotgun;
		case 4:
			return Unlockable.Iceland;
		case 5:
			return Unlockable.Flamethrower;
		case 6:
			return Unlockable.Volcan;
		case 7:
			return Unlockable.Minigun;
		case 8:
			return Unlockable.Space;
		case 9:
			return Unlockable.Bazooka;
		case 10:
			return Unlockable.Laser;
		default:
				return null;
		}
		
	}
	
	public void create() {
		super.create();
		
		layout = new GlyphLayout();

		for(int i = 0; i < KambojaMain.getPostGamePlayers().size(); i ++) {
			float margin = (1920 - 4*main_frame[i].getWidth())/5f;
			frame_body[i] = createBox(new Vector2(
					margin + i*(main_frame[i].getWidth() + margin) + main_frame[i].getWidth()/2f,
					1080),
					new Vector2(350/2f, 500/2f), BodyType.DynamicBody, 0.01f);
			buildRopeJoint(13,
					frame_body[i],
					margin + i*(main_frame[i].getWidth() + margin) + main_frame[i].getWidth()/2f - 1920/2f,
					(413/2f),
					160);			
		}
		
		KambojaMain.getPostGamePlayers().sort(new Comparator<Player>(){
			public int compare(Player arg0, Player arg1) {
				if(arg1.getKills() - arg0.getKills() == 0) {
					return (int)(arg1.getScore() - arg0.getScore());
				}
				return  arg1.getKills() - arg0.getKills();
			}
		});
		
	}
	
	public void render(SpriteBatch sb) {
		for(int i = 0; i < KambojaMain.getPostGamePlayers().size(); i ++) {
			main_buffer[i].begin();
			
			Gdx.gl.glClearColor(1, 1, 1, 0);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			
			
			sb.begin();
			sb.setProjectionMatrix(bufferProjection);
			sb.draw(name_frame[i],
					(main_buffer[i].getWidth() - name_frame[i].getWidth())/2f, main_buffer[i].getHeight() - name_frame[i].getHeight() - 10,
					name_frame[i].getWidth(), name_frame[i].getHeight());
			
			oliver.draw(sb,
					KambojaMain.getControllers().get(KambojaMain.getPostGamePlayers().get(i).getId()).getName(),
					(main_buffer[i].getWidth() - name_frame[i].getWidth())/2f,
					main_buffer[i].getHeight() - name_frame[i].getHeight() + 40,
					name_frame[i].getWidth(), 1, false);
			
			sb.draw(player_frame[i],
					(main_buffer[i].getWidth() - player_frame[i].getWidth())/2f, main_buffer[i].getHeight() - player_frame[i].getHeight() - 100,
					player_frame[i].getWidth(), player_frame[i].getHeight());
			
			TextureRegion playerTexture = 
					Player.getTexture(
							KambojaMain.getControllers().get(KambojaMain.getPostGamePlayers().get(i).getId()).getPlayer(),
							Player.getSkinPositionByWeapon(KambojaMain.getControllers().get(KambojaMain.getPostGamePlayers().get(i).getId()).getWeapon()));
			Texture wep = inGameWep[KambojaMain.getControllers().get(KambojaMain.getPostGamePlayers().get(i).getId()).getWeapon()];
			int size = i == 0 ? 200 : 150;
			sb.draw(playerTexture, (main_buffer[i].getWidth() - size)/2f, 147 + (240 - size)/2f, size/2f, size/2f, size, size, 1, 1, globalTimer*size);
			float factor = i == 0 ? 200 / (float)playerTexture.getRegionWidth() : 150 / (float)playerTexture.getRegionWidth();
			sb.draw(wep,
					(main_buffer[i].getWidth() - wep.getWidth())/2f,
					147 + (240 - wep.getHeight())/2f,
					wep.getWidth()/2f,
					wep.getHeight()/2f,
					wep.getWidth(), wep.getHeight(),
					factor, factor, globalTimer*size, 0, 0,
					wep.getWidth(), wep.getHeight(), false, false);
			
			sb.setColor(1, 1, 1, 0.3f);
			sb.draw(player_frame[i],
					(main_buffer[i].getWidth() - player_frame[i].getWidth())/2f, main_buffer[i].getHeight() - player_frame[i].getHeight() - 100,
					player_frame[i].getWidth(), player_frame[i].getHeight());
			sb.setColor(1, 1, 1, 1);

			sb.draw(kill_icon, 10, 10);
			sb.draw(death_icon, 350 - 10 - death_icon.getWidth(), 10);
			
			oliverFrag.draw(sb,
					KambojaMain.getPostGamePlayers().get(i).getKills() + " / " + KambojaMain.getPostGamePlayers().get(i).getDeaths(),
					(main_buffer[i].getWidth() - name_frame[i].getWidth())/2f,
					10 + 50,
					name_frame[i].getWidth(), 1, false);
			
			oliverScore.draw(sb,
					"Score: " + (int)KambojaMain.getPostGamePlayers().get(i).getScore(),
					(main_buffer[i].getWidth() - name_frame[i].getWidth())/2f,
					10 + 110,
					name_frame[i].getWidth(), 1, false);
			
			sb.end();
			
			main_buffer[i].end();
		}
		sb.setProjectionMatrix(Util.getNormalProjection());
		
		super.render(sb);
	}

	public void insideRender(SpriteBatch sb) {
		
		float margin = (1920 - 4*main_frame[0].getWidth())/5f;
		
		sb.begin();
		for(int i = 0; i < KambojaMain.getPostGamePlayers().size(); i++) {
			renderImageInBody(sb, main_frame[i], frame_body[i]);
			renderImageInBody(sb, main_buffer[i].getColorBufferTexture(), frame_body[i], true);
		}

		drawChains(sb);
		
		sb.setColor(1, 1, 1, (float)((Math.sin(globalTimer*3) + 1)/2f * 0.5f + 0.5f));
		sb.draw(crown_light, margin + (main_frame[0].getWidth() - crown_light.getWidth())/2f,
				1080 - (1080 - main_frame[0].getHeight())/2f);
		sb.setColor(1, 1, 1, 1);
		
		sb.draw(crown, margin + (main_frame[0].getWidth() - crown.getWidth())/2f,
				1080 - (1080 - main_frame[0].getHeight())/2f);

		sb.draw(progress_case, 200, 50 + (capsule.getHeight() - progress_case.getHeight())/2f);
		sb.end();
		
		sb.begin();
		waveShader.begin();
		waveShader.setUniformf("time", globalTimer*2);
		waveShader.setUniformf("squish", 10f);
		waveShader.setUniformf("intensity", 0.003f);
		waveShader.setUniformf("percent_x", KambojaMain.experience / (float)KambojaMain.maxExperience);
		
		sb.setShader(waveShader);
		sb.draw(progress_bar, 200, 50 + (capsule.getHeight() - progress_case.getHeight())/2f, 0, 0,
				progress_bar.getWidth(), progress_bar.getHeight());
		sb.end();
		
		waveShader.end();
		sb.setShader(null);
		
		sb.begin();
		sb.draw(capsule, 50, 50);
		oliverFrag.draw(sb, "LVL " + KambojaMain.level, 50, 180, capsule.getWidth(), 1, false);
		sb.draw(capsule_light, 50, 50);
				
		if(nextUnlockable != null) {
		
		sb.setColor(1, 1, 1, textTween);
		sb.draw(unlockedText,
				(1920 - unlockedText.getWidth()*textTween*2)/2f,
				(1080 - unlockedText.getHeight()*textTween*2)/2f,
				unlockedText.getWidth()*textTween*2,
				unlockedText.getHeight()*textTween*2
				);		
		
		oliverFrag.setColor(1, 1, 1, textTween);
		oliverFrag.draw(sb, "Unlocked new " + nextUnlockable.tipo + "!", 0,
				(1080 - unlockedText.getHeight()*textTween*2)/2f + 150,
				1920, 1, false);
		
		
		sb.setColor(1, 1, 1, imageTween);
		sb.draw(unlockedImage,
				(1920 - unlockedImage.getWidth()*imageTween)/2f,
				(1080 - unlockedImage.getHeight()*imageTween)/2f,
				unlockedImage.getWidth()*imageTween,
				unlockedImage.getWidth()*imageTween
				);
		
		
			
		float imageWidth = nextUnlockable.tipo.equals("Map") ? 300 : nextUnlockable.imagem.getWidth()*4;
		float imageHeight = nextUnlockable.tipo.equals("Map") ? 300 : nextUnlockable.imagem.getHeight()*4;
		
		sb.draw(nextUnlockable.imagem,
				(1920 - imageWidth*imageTween)/2f,
				(1080 - imageHeight*imageTween)/2f,
				imageWidth*imageTween,
				imageHeight*imageTween);
		
		oliverFrag.setColor(1, 1, 1, imageTween);
		oliverFrag.draw(sb, nextUnlockable.nome, 0, (1080 - unlockedImage.getHeight()*imageTween)/2f + 150,
				1920, 1, false);
		
		}
		
		oliverFrag.setColor(1, 1, 1, 1);
		sb.setColor(1, 1, 1, 1);

		//TODO:
		/*
		 * Fazer física em cima dos frames [OK]
		 * Desenhar frame de nome e frame de aparencia[OK-semi] concertar arma pra fora do canvas
		 * desenhar barra de progresso[OK]
		 * desenhar botão de back
		 * fazer seleção
		 * colocar score nos players[OK]
		 * depois de um certo tempo o score dos players descem pra zero e o progress aumenta[OK]
		 * desenhar o nível atual[OK]
		 * quando upar de nivel, mostrar um frame caindo do céu mostrando o q a pessoa liberou
		 * refazer o sort de player, o primeiro é quem matou mais, se empatar com alguem filtra pelo score
		 * 
		 */
		
		sb.flush();
		
		//b2dr.render(world, camera.combined);
		
		sb.end();
		
	}
	
	boolean ended = false;
	
	public void update(float delta) {
		super.update(delta);
		
		if(globalTimer > 2) {
			ended = true;
			if(increasingEXP) {
				for(int i = 0; i < KambojaMain.getPostGamePlayers().size(); i ++) {
					if(KambojaMain.getPostGamePlayers().get(i).getScore() > 10){
					KambojaMain.experience += 40;
					KambojaMain.getPostGamePlayers().get(i).reduceScore(10);
					ended = false;
					}
					else if(KambojaMain.getPostGamePlayers().get(i).getScore() > 0){
					KambojaMain.experience += KambojaMain.getPostGamePlayers().get(i).getScore();
					KambojaMain.getPostGamePlayers().get(i).reduceScore(KambojaMain.getPostGamePlayers().get(i).getScore());
					ended = false;
					}
				}
				if(KambojaMain.experience > KambojaMain.maxExperience) {
					KambojaMain.experience -= KambojaMain.maxExperience;
					KambojaMain.level ++;
					if(getNextUnlockable() == null) {
						unlockable_frame_selection = -1;
					}
					else {
						unlockable_frame_selection = 0;
						increasingEXP = false;
					}
					
					
				}
			}
		}
		
		if(unlockable_frame_selection == 0) {
			textTween += (1 - textTween)/10.0f;
		}
		else {
			textTween += (0 - textTween)/10.0f;
		}
		
		if(unlockable_frame_selection == 1) {
			imageTween += (1 - imageTween)/10.0f;
		}
		else {
			imageTween += (0 - imageTween)/10.0f;
		}
	}

	public void changeScreen() {
		manager.changeState(Manager.PLAYER_SELECT_STATE);
	}

	public void dispose() {
	}

	public void connected(Controller controller) {
		
	}

	public void disconnected(Controller controller) {
		
	}
	
	@Override
	public boolean keyDown(int keycode) {
		
		if(unlockable_frame_selection == 0) {
			unlockable_frame_selection = 1;
			return false;
		}
		
		if(unlockable_frame_selection == 1) {
			unlockable_frame_selection = -1;
			unlock();
			nextUnlockable = getNextUnlockable();
			increasingEXP = true;
			return false;
		}
		
		if(ended) {
			if(unlockable_frame_selection == -1) {
				intro = false;
				outro = true;
			}
		}
		else {
			while(!ended) {
				ended = true;
				for(int i = 0; i < KambojaMain.getPostGamePlayers().size(); i ++) {
					if(KambojaMain.getPostGamePlayers().get(i).getScore() > 10){
					KambojaMain.experience += 40;
					KambojaMain.getPostGamePlayers().get(i).reduceScore(10);
					ended = false;
					}
					else if(KambojaMain.getPostGamePlayers().get(i).getScore() > 0){
					KambojaMain.experience += KambojaMain.getPostGamePlayers().get(i).getScore();
					KambojaMain.getPostGamePlayers().get(i).reduceScore(KambojaMain.getPostGamePlayers().get(i).getScore());
					ended = false;
					}
				}
				if(KambojaMain.experience > KambojaMain.maxExperience) {
					KambojaMain.experience -= KambojaMain.maxExperience;
					KambojaMain.level ++;
					unlockable_frame_selection = 0;
					ended = false;
					break;
					
				}
			}
		}
		
		return false;
	}

	public boolean buttonDown(Controller controller, int buttonCode) {
		
		if(unlockable_frame_selection == 0) {
			unlockable_frame_selection = 1;
			return false;
		}
		
		if(unlockable_frame_selection == 1) {
			unlockable_frame_selection = -1;
			unlock();
			nextUnlockable = getNextUnlockable();
			increasingEXP = true;
			return false;
		}
		
		if(ended) {
			if(unlockable_frame_selection == -1) {
				intro = false;
				outro = true;
			}
		}
		else {
			while(!ended) {
				ended = true;
				for(int i = 0; i < KambojaMain.getPostGamePlayers().size(); i ++) {
					if(KambojaMain.getPostGamePlayers().get(i).getScore() > 10){
					KambojaMain.experience += 10;
					KambojaMain.getPostGamePlayers().get(i).reduceScore(10);
					ended = false;
					}
					else if(KambojaMain.getPostGamePlayers().get(i).getScore() > 0){
					KambojaMain.experience += KambojaMain.getPostGamePlayers().get(i).getScore();
					KambojaMain.getPostGamePlayers().get(i).reduceScore(KambojaMain.getPostGamePlayers().get(i).getScore());
					ended = false;
					}
				}
				if(KambojaMain.experience > KambojaMain.maxExperience) {
					KambojaMain.experience -= KambojaMain.maxExperience;
					KambojaMain.level ++;
					unlockable_frame_selection = 0;
					ended = false;
					break;
					
				}
			}
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
