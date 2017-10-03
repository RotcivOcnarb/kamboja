package com.mygdx.game.states;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
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
import com.mygdx.game.objects.KeyboardTyper;
import com.mygdx.game.objects.MenuCursors;
import com.mygdx.game.objects.Player;
import com.mygdx.game.objects.PlayerController;
import com.mygdx.game.objects.Util;

public class MainMenu extends State{

	private ShapeRenderer sr;// to draw controllers GUI (yellow things below)
	private Color tempC; //temporary color parameter instead of creating lots of instances of Color
	private float timer; //do the animation stuff
	private GlyphLayout layout; //to calculate the size of text to draw
	private BitmapFont menuFont; //to draw text
	
	private float[] menuPos; //positions of the buttons (X coord)
	private String[] options = new String[]{"Exit", "MONARK", "Start"};
	private FrameBuffer[] buffers; //so the font can be drawed at a buffer, that can be resized 
	//(fonts cant be resized after initialization)
	private Texture logoMain;
	private Background background; //desenha o fundo vermelho
	
	private Sound sound_select, new_player, change_sound;
	
	private float[] controlPos; //the positions of the controllers GUI (yellow things)
	private BitmapFont controllerFont; //the font used to draw the controllers gui text
	private int[] controllerSelecion; //wich player is selecting what (skin or weapon)
	//size 4 because we have at maximum 4 players
	
	private MenuCursors cursors; //handles the cursor movement and clicking
	private Texture[] texWep; //texture for each weapon
	
	private float[] arrowScale; //the little arrows size at the selection (skin or weapon)
	
	private Texture arrow;
	
	private int[] selection; //wich button is being hovered by the cursor (size 4 for 4 players)
	
	private Rectangle2D[] bounds; //the bounds of the buttons
	
	private Matrix4 m4Temp; //a matrix to draw the fonts at the frame buffers
	
	private boolean exiting; //if it is exiting the current state
	private boolean optionsE; //if it is exiting the current state and heading to the Options state
	
	private KeyboardTyper[] typer; //the little keyboard players can write its name
	
	public MainMenu(Manager manager) {
		super(manager);
	}
	
	public void dispose(){
		sr.dispose();
		menuFont.dispose();
		controllerFont.dispose();
		logoMain.dispose();
		for(Texture t : texWep){
			t.dispose();
		}
		cursors.dispose();
		for(FrameBuffer b : buffers){
			b.dispose();
		}
		arrow.dispose();
		for(KeyboardTyper kt : typer){
			kt.dispose();
		}
		sound_select.dispose();
		new_player.dispose();
		change_sound.dispose();
		background.dispose();
	}

	public void create() {
		timer = -1; //timer geral do estado
		exiting = false; //se tá saindo
		optionsE = false; //se tá indo pro menu de opçoes

		tempC = new Color();
		sr = new ShapeRenderer();
		sound_select = Gdx.audio.newSound(Gdx.files.internal("audio/select.ogg"));
		new_player = Gdx.audio.newSound(Gdx.files.internal("audio/new_player.ogg"));
		change_sound = Gdx.audio.newSound(Gdx.files.internal("audio/change.ogg"));
		background = new Background();
		
		//Creates the font
		FreeTypeFontGenerator ftfg;
		FreeTypeFontParameter param;
		ftfg = new FreeTypeFontGenerator(Gdx.files.internal("fonts/dot_to_dot.ttf"));
		param = new FreeTypeFontParameter();
		param.size = (int) (300 * Gdx.graphics.getDensity());
		param.color = new Color(0.03f, 0.03f, 0.03f, 1);
		param.borderWidth = 2;
		param.borderColor = new Color(1, 0.9f, 0.9f, 1);
		param.shadowColor = new Color(0, 0, 0, 0.7f);
		param.shadowOffsetX = 3;
		param.shadowOffsetY = 3;
		//font = ftfg.generateFont(param);
		//fonte de opções do menu
		param.size = (int) (200 * Gdx.graphics.getDensity());
		menuFont = ftfg.generateFont(param);
		//fonte do menu dos controles
		param.size = (int) (100 * Gdx.graphics.getDensity());
		controllerFont = ftfg.generateFont(param);
		ftfg.dispose();
		
		logoMain = new Texture("imgs/logoMain.png");
		
		//posições X das opçoes
		if(menuPos == null)
		menuPos = new float[3];
		menuPos[0] = -300;
		menuPos[1] = Gdx.graphics.getWidth() + 300;
		menuPos[2] = -300;
		
		if(layout == null)
		layout = new GlyphLayout(); //para calcular width e height de textos

		//posições dos menus dos controles
		if(controlPos == null)
		controlPos = new float[4];
		for(int i = 0; i < 4; i ++){
			controlPos[i] = -i * 200 - 200;
		}
		
		//controles ativos
		if(KambojaMain.getControllers() == null){
			KambojaMain.initializeControllers();
		}
		
		cursors = new MenuCursors();
		
		//texturas das armas
		if(texWep == null)
		texWep = new Texture[KambojaMain.getWeaponSize()];
		texWep[0] = new Texture("Weapons/Icon/Pistol.png");
		texWep[1] = new Texture("Weapons/Icon/PistolAkimbo.png");
		texWep[2] = new Texture("Weapons/Icon/minigun.png");
		texWep[3] = new Texture("Weapons/Icon/shotgun.png");
		texWep[4] = new Texture("Weapons/Icon/Mp5.png");
		texWep[5] = new Texture("Weapons/Icon/Flamethrower.png");
		texWep[6] = new Texture("Weapons/Icon/Bazook.png");
		texWep[7] = new Texture("Weapons/Icon/Laser.png");

		//qual opçao do menu está sendo hovered pelo ponteiro
		if(selection == null)
		selection = new int[4];
		for(int i = 0; i < 4; i ++){
			selection[i] = -1;
		}
		
		//canvas para desenhar os textos das opçoes de menu
		if(buffers == null)
		buffers = new FrameBuffer[options.length];
		for(int i = 0; i < buffers.length; i ++){
			layout.setText(menuFont, options[i]);
			buffers[i] = new FrameBuffer(Format.RGBA8888, (int)layout.width, (int)menuFont.getAscent()*2, false);
		}
		
		//retangulos de colisão das opçoes de menu
		if(bounds == null)
		bounds = new Rectangle2D[options.length];
		for(int i = 0; i < bounds.length; i ++){
			layout.setText(menuFont, options[i]);
			bounds[i] = new Rectangle2D.Double(3f/4f*Gdx.graphics.getWidth() - layout.width/2f, Gdx.graphics.getHeight()/2 + (i-1) * 100, (int)layout.width, (int)menuFont.getAscent()*2);
		}
		
		//matriz temporária
		if(m4Temp == null)
		m4Temp = new Matrix4();
		
		//qual item está sendo selecionado pelo player (arma ou player)
		if(controllerSelecion == null)
		controllerSelecion = new int[4];
		
		//textura de setinha
		arrow = new Texture("imgs/arrow.png");
		
		//tamanho da textura de cada setinha no menu
		if(arrowScale == null)
		arrowScale = new float[8];
		for(int i = 0; i < 8; i ++){
			arrowScale[i] = 1;
		}
		
		//menu para digitar textos
		if(typer == null)
		typer = new KeyboardTyper[4];
		for(int i = 0; i < 4; i ++){
			typer[i] = new KeyboardTyper(new Vector2(Gdx.graphics.getWidth()/4 * i + (Gdx.graphics.getWidth()/4 * 0.1f), 230), i);
		}
				
	}
	public void render(SpriteBatch sb) {
		background.render(sb);
		
	//Desenha o logo
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		sb.begin();	
		sb.setColor(1, 1, 1, Math.min(1, Math.max(0, timer-1)));
		sb.draw(logoMain, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		sb.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);

	//desenha as opçoes no framebuffer
		for(int i = 0; i < 3; i ++){
			m4Temp.setToOrtho2D(0, 0, buffers[i].getWidth(), buffers[i].getHeight()); 
			sb.setProjectionMatrix(m4Temp);
			
			buffers[i].begin();
				sb.begin();
					menuFont.draw(sb, options[i], 0, buffers[i].getHeight() - 10);
				sb.end();
			buffers[i].end();
		}
		m4Temp.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		sb.setProjectionMatrix(m4Temp);
		
	//desenha o framebuffer com o botão
		float amp = 0;
		for(int i = 0; i < 3; i ++){
			boolean selected = false;
			for(int j = 0; j < 4; j ++){
				if(selection[j] == i){
					selected = true;
					break;
				}
			}
			if(selected) amp = (float) (Math.sin(timer*2) * 7.5 + 15);
			else amp = 0;
			sb.begin();
				sb.draw(buffers[i].getColorBufferTexture(),
						menuPos[i] - amp,
						Gdx.graphics.getHeight()/2 + (i-1) * 100 + buffers[i].getHeight() + amp,
						buffers[i].getWidth() + amp*2,
						-(buffers[i].getHeight() + amp*2));
			sb.end();
		}
		
	
	//desenha o GUI de controles em baixo
		float tw = Gdx.graphics.getWidth()/4 * 0.8f;
		float margin = (Gdx.graphics.getWidth()/4 - tw)/2;
		for(int i = 0; i < 4; i ++){

			tempC.set(1, 1, 1, 1);
			sr.setColor(tempC);
			drawControllerGUI(Gdx.graphics.getWidth()/4 * i + margin - 4, controlPos[i] - 4 - 100, tw + 8, 228, 34, ShapeType.Filled);
			
			tempC.set(0, 0, 0, 1);
			sr.setColor(tempC);
			drawControllerGUI(Gdx.graphics.getWidth()/4 * i + margin - 2, controlPos[i] - 2 - 100, tw + 4, 224, 32, ShapeType.Filled);
			
			tempC.set(149/255f, 30/255f, 33/255f, 1);
			sr.setColor(tempC);
			drawControllerGUI(Gdx.graphics.getWidth()/4 * i + margin, controlPos[i] - 100, tw, 220, 30, ShapeType.Filled);

		}
		
	//desenha os textos no gui de controller
		sb.begin();

		for(int i = 0; i < 4; i ++){
			if(KambojaMain.getControllers().size()-1 >= i){
				//desenha o nome do player
					layout.setText(controllerFont, KambojaMain.getControllers().get(i).getName());
					controllerFont.draw(sb, KambojaMain.getControllers().get(i).getName(),
							Gdx.graphics.getWidth()/4 * i + margin + (tw - layout.width)/2f,
							 controlPos[i] + 110);
					
					float ratio = texWep[KambojaMain.getControllers().get(i).getWeapon()].getWidth() / (float)texWep[KambojaMain.getControllers().get(i).getWeapon()].getHeight();
					
					//desenha a arma e o player selecionado
					float targetHeight = (controlPos[i] + 90)*0.4f; //width do icone
					float wi = ratio * targetHeight;
					sb.draw(texWep[KambojaMain.getControllers().get(i).getWeapon()], Gdx.graphics.getWidth()/4 * i + margin + (tw - wi)/2f, controlPos[i] + 10, wi, targetHeight);
					sb.draw(Player.getTexture(KambojaMain.getControllers().get(i).getPlayer(), 0), Gdx.graphics.getWidth()/4 * i + margin + (tw - targetHeight)/2, controlPos[i] - 50, targetHeight, targetHeight);
					
					//desenha as setas
					switch(i){
					case 0:
						sb.setColor(0, 0, 1, 1);
						break;
					case 1:
						sb.setColor(1, 0, 0, 1);
						break;
					case 2:
						sb.setColor(0, 1, 0, 1);
						break;
					case 3:
						sb.setColor(1, 1, 0, 1);
					}
					
					float al = targetHeight*arrowScale[2*i];
					float ar = targetHeight*arrowScale[2*i + 1];
					
					sb.draw(arrow, Gdx.graphics.getWidth()/4 * i + margin + (tw - al)/2f - al,  controlPos[i] + 13 - (53)*controllerSelecion[i], //xy
							al/2, al/2,
							al, al,  //w, h
							1, 1,
							90, //rotation
							0, 0, arrow.getWidth(), arrow.getHeight(), false, false);
					
					sb.draw(arrow, Gdx.graphics.getWidth()/4 * i + margin + (tw - ar)/2f + ar,  controlPos[i] + 13 - (53)*controllerSelecion[i], //xy
							ar/2, ar/2,
							ar, ar,  //w, h
							1, 1,
							-90, //rotation
							0, 0, arrow.getWidth(), arrow.getHeight(), false, false);
					
					sb.setColor(1, 1, 1, 1);
					
					//desenha o texto de disconectar
					layout.setText(controllerFont, "Disconnect");
					controllerFont.draw(sb, "Disconnect",
							Gdx.graphics.getWidth()/4 * i + margin + (tw - layout.width)/2f,
							 controlPos[i] - 60);
				}
				else{
					//caso nao tenha controle ligado no momento
					layout.setText(controllerFont, "Press start!");
					controllerFont.draw(sb, "Press start!",
							Gdx.graphics.getWidth()/4 * i + margin + (tw - layout.width)/2f,
							 controlPos[i] + 90);
				}
		}
		
		sb.end();	
		
		//renderiza o digitador
		for(int i = 0; i < 4; i ++){
			typer[i].render(sb);
		}

		cursors.render(sb);
		
	
				
	}
	Rectangle2D recTemp = new Rectangle2D.Double();
	public void drawControllerGUI(float x, float y, float width, float height, float radius, ShapeType type){
		sr.begin(type);
			sr.box(x, y, 0, width, height - radius, 0);
			sr.box(x + radius, y, 0, width - (radius*2), height, 0);
			sr.circle(x + radius, y + height - radius, radius);
			sr.circle(x + width - radius, y + height - radius, radius);
		sr.end();
	}

	public void update(float delta) {
		
		background.update(delta, exiting);
		
		if(exiting){ //saindo do state, escondendo tudo
			timer -= delta;
			for(int i = 0; i < 4; i ++){
				controlPos[i] += (-200 - controlPos[i])/10.0f;
			}
			
			if(timer < -1){
				if(!optionsE)
				manager.changeState(1); //vai pra tela de seleção de mapa
				else
				manager.changeState(4);	//vai pras opções
				
				return;
			}
		}
		else{
			timer += delta;
		}
		
		//não deixa o cursor sair da tela
		if(Gdx.input.getX() < 0) Gdx.input.setCursorPosition(0, Gdx.input.getY());
		if(Gdx.input.getX() > Gdx.graphics.getWidth() - 32) Gdx.input.setCursorPosition(Gdx.graphics.getWidth() - 32, Gdx.input.getY());
		if(Gdx.input.getY() < 32) Gdx.input.setCursorPosition(Gdx.input.getX(), 32);
		if(Gdx.input.getY() > Gdx.graphics.getHeight()) Gdx.input.setCursorPosition( Gdx.input.getX(), Gdx.graphics.getHeight());
		
		cursors.update(delta);
		
		keyboardInput(delta);
		
		if(Gdx.input.justTouched()){
			buttonDown(null, Buttons.LEFT);
		}

		for(int i = 0; i < 4; i ++){
			typer[i].update(delta);
		}
		
		for(int i = 0; i < 8; i ++){
			arrowScale[i] += (1 - arrowScale[i])/10.0f;
		}
		
		if(timer > 2){
			layout.setText(menuFont, options[0]);
			menuPos[0] += (3f/4f*Gdx.graphics.getWidth() - layout.width/2f - menuPos[0])/10.0f;
		}
		
		if(timer > 2.5){
			layout.setText(menuFont, options[1]);
			menuPos[1] += (3f/4f*Gdx.graphics.getWidth() - layout.width/2f - menuPos[1])/10.0f;
		}
		
		if(timer > 3){
			layout.setText(menuFont, options[2]);
			menuPos[2] += (3f/4f*Gdx.graphics.getWidth() - layout.width/2f - menuPos[2])/10.0f;
		}
		
		if(timer > 4){ //mostra os gui de controle depois de 4 segundos
			for(int i = 0; i < 4; i ++){
				if(KambojaMain.getControllers().size()-1 >= i){
					controlPos[i] += (100 - controlPos[i])/10.0f;
				}
				else{
					controlPos[i] += (0 - controlPos[i])/10.0f;
				}
			}
		}
		
		
		//define a seleção do cursor
		for(int i = 0; i < 4; i ++){
			if(KambojaMain.getControllers().size()-1 >= i){
				Point2D p = new Point2D.Double(cursors.getPosition(i).x + 16, cursors.getPosition(i).y + 16);
				if(bounds[0].contains(p)){
					selection[i] = 0;
				}
				else if(bounds[1].contains(p)){
					selection[i] = 1;
				}
				else if(bounds[2].contains(p)){
					selection[i] = 2;
				}
				else{
					selection[i] = -1;
				}
			}
			else{
				selection[i] = -1;
			}
		}
		
	}
	
	public void keyboardInput(double delta){
		//Handle inputs from Keyboard player
				int cont = 0;
				for(PlayerController pc : KambojaMain.getControllers()){
					if(pc instanceof KeyboardController){
						
						if(Gdx.input.isKeyJustPressed(Keys.DOWN)){
								if(controllerSelecion[cont] == 0)
									controllerSelecion[cont] = 1;
								else
									controllerSelecion[cont] = 0;
								change_sound();
						}
						if(Gdx.input.isKeyJustPressed(Keys.UP)){
								if(controllerSelecion[cont] == 0)
									controllerSelecion[cont] = 1;
								else
									controllerSelecion[cont] = 0;
								change_sound();
						}
						if(Gdx.input.isKeyJustPressed(Keys.RIGHT)){
								if(controllerSelecion[cont] == 0){
									KambojaMain.getControllers().get(cont).nextWeapon();
								}
								else{
									int player = KambojaMain.getControllers().get(cont).getPlayer();
									player = nextPlayer(player);
									KambojaMain.getControllers().get(cont).setPlayer(player);
								}
								arrowScale[cont * 2 + 1] = 1.3f;
								change_sound();
						}
						if(Gdx.input.isKeyJustPressed(Keys.LEFT)){
								if(controllerSelecion[cont] == 0){
									KambojaMain.getControllers().get(cont).previousWeapon();
								}
								else{
									int player = KambojaMain.getControllers().get(cont).getPlayer();
									player = previousPlayer(player);
									KambojaMain.getControllers().get(cont).setPlayer(player);
								}
								
								arrowScale[cont * 2] = 1.3f;
								change_sound();
						}
						
						
						break;
					}
					cont++;
				}
	}

	public void connected(Controller controller) {
		System.out.println("Connected!");
	}

	public void disconnected(Controller controller) {
		
	}
	Point2D pointer = new Point2D.Double();
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
						
						if(typer[id].buttonDown(null, buttonCode, cursors.getPosition(id).x, cursors.getPosition(id).y)){
							return true;
						}
				
						float mg = Gdx.graphics.getWidth()/4 * 0.2f;
						pointer.setLocation(cursors.getPosition(id).x + 16, cursors.getPosition(id).y + 16);
						
						recTemp.setRect(Gdx.graphics.getWidth()/4 * id  + mg/2, controlPos[id] - 90, Gdx.graphics.getWidth()/4 - mg, 40);
							
						
						if(recTemp.contains(pointer)){
							KambojaMain.getControllers().remove(id);
							//cursorVelocity[id].set(0, 0);
							typer[id].hide();
							hasKeyboard = false;
							return true;
						}
						
						mg = Gdx.graphics.getWidth()/4 * 0.3f;
						recTemp.setRect(Gdx.graphics.getWidth()/4 * id  + mg/2, controlPos[id] + 80, Gdx.graphics.getWidth()/4 - mg, 40);
						if(recTemp.contains(pointer)){
							if(!typer[id].isShowing())
								typer[id].show();
							else
								typer[id].hide();
							return true;
						}
						
						
						if(selection[id] == 0){
							System.exit(0);
						}
						if(selection[id] == 1){
								if(!exiting){
									timer = 1;
									optionsE = true;
									exiting = true;
									sound_select();
								}
								
						}

						if(selection[id] == 2){
							if(KambojaMain.getControllers().size() >= 2){
								if(!exiting){
									timer = 1;
									exiting = true;
									sound_select();
								}
							}
						}
					
				
			}
			
			return false;
		}
		
		boolean handled = false;
		for(int i = 0; i < 4; i ++){
			handled = handled || typer[i].buttonDown(controller, buttonCode, cursors.getPosition(i).x, cursors.getPosition(i).y);
		}
		if(handled) return true;
		
		int select = 0;
		int start = 0;
		if(controller.getName().equals(Gamecube.getID())){
			select = Gamecube.A;
			start = Gamecube.START;
			if(buttonCode == Gamecube.PAD_DOWN){
				if(Util.getControllerID(controller) != -1){
					//KambojaMain.getControllers().remove(Util.getControllerID(controller));
					if(controllerSelecion[Util.getControllerID(controller)] == 0)
						controllerSelecion[Util.getControllerID(controller)] = 1;
					else
						controllerSelecion[Util.getControllerID(controller)] = 0;
				}
				change_sound();
			}
			if(buttonCode == Gamecube.PAD_UP){
				if(Util.getControllerID(controller) != -1){
					//KambojaMain.getControllers().remove(Util.getControllerID(controller));
					if(controllerSelecion[Util.getControllerID(controller)] == 0)
						controllerSelecion[Util.getControllerID(controller)] = 1;
					else
						controllerSelecion[Util.getControllerID(controller)] = 0;
				}
				change_sound();
			}
			if(Util.getControllerID(controller) != -1){
				if(buttonCode == Gamecube.PAD_RIGHT){
					if(controllerSelecion[Util.getControllerID(controller)] == 0){
						KambojaMain.getControllers().get(Util.getControllerID(controller)).nextWeapon();
					}
					else{
						int player = KambojaMain.getControllers().get(Util.getControllerID(controller)).getPlayer();
						player = nextPlayer(player);
						KambojaMain.getControllers().get(Util.getControllerID(controller)).setPlayer(player);
					}
					arrowScale[Util.getControllerID(controller) * 2 + 1] = 1.3f;
					change_sound();
				}
				if(buttonCode == Gamecube.PAD_LEFT){
					if(controllerSelecion[Util.getControllerID(controller)] == 0){
						KambojaMain.getControllers().get(Util.getControllerID(controller)).previousWeapon();
					}
					else{
						int player = KambojaMain.getControllers().get(Util.getControllerID(controller)).getPlayer();
						player = previousPlayer(player);
						KambojaMain.getControllers().get(Util.getControllerID(controller)).setPlayer(player);
					}
					
					arrowScale[Util.getControllerID(controller) * 2] = 1.3f;
					change_sound();
				}
			}
			
		}
		else if(controller.getName().toUpperCase().contains("XBOX") && controller.getName().contains("360")){
			select = XBox.BUTTON_A;
			start = XBox.BUTTON_START;
		}
		else if(controller.getName().toUpperCase().contains("SONY") || controller.getName().toUpperCase().contains("PLAYSTATION")){
			select = Playstation3.X;
			start = Playstation3.START;
			
			if(buttonCode == Playstation3.DOWN){
				if(Util.getControllerID(controller) != -1){
					if(controllerSelecion[Util.getControllerID(controller)] == 0)
						controllerSelecion[Util.getControllerID(controller)] = 1;
					else
						controllerSelecion[Util.getControllerID(controller)] = 0;
				}
				change_sound();
			}
			if(buttonCode == Playstation3.UP){
				if(Util.getControllerID(controller) != -1){
					if(controllerSelecion[Util.getControllerID(controller)] == 0)
						controllerSelecion[Util.getControllerID(controller)] = 1;
					else
						controllerSelecion[Util.getControllerID(controller)] = 0;
				}
				change_sound();
			}
			if(Util.getControllerID(controller) != -1){
				if(buttonCode == Playstation3.RIGHT){
					if(controllerSelecion[Util.getControllerID(controller)] == 0){
						KambojaMain.getControllers().get(Util.getControllerID(controller)).nextWeapon();
					}
					else{
						int player = KambojaMain.getControllers().get(Util.getControllerID(controller)).getPlayer();
						player = nextPlayer(player);
						KambojaMain.getControllers().get(Util.getControllerID(controller)).setPlayer(player);
					}
					arrowScale[Util.getControllerID(controller) * 2 + 1] = 1.3f;
					change_sound();
				}
				if(buttonCode == Playstation3.LEFT){
					if(controllerSelecion[Util.getControllerID(controller)] == 0){
						KambojaMain.getControllers().get(Util.getControllerID(controller)).previousWeapon();
					}
					else{
						int player = KambojaMain.getControllers().get(Util.getControllerID(controller)).getPlayer();
						player = previousPlayer(player);
						KambojaMain.getControllers().get(Util.getControllerID(controller)).setPlayer(player);
					}
					
					arrowScale[Util.getControllerID(controller) * 2] = 1.3f;
					change_sound();
				}
			}
		}
		
		if(buttonCode == select){
			if(Util.getControllerID(controller) != -1){
				float mg = Gdx.graphics.getWidth()/4 * 0.2f;
				pointer.setLocation(cursors.getPosition(Util.getControllerID(controller)).x + 16, cursors.getPosition(Util.getControllerID(controller)).y + 16);
				int id = Util.getControllerID(controller);
				recTemp.setRect(Gdx.graphics.getWidth()/4 * id  + mg/2, controlPos[id] - 90, Gdx.graphics.getWidth()/4 - mg, 40);
				if(recTemp.contains(pointer)){
					KambojaMain.getControllers().remove(Util.getControllerID(controller));
					//cursorVelocity[id].set(0, 0);
					typer[id].hide();
					return true;
				}
				
				mg = Gdx.graphics.getWidth()/4 * 0.3f;
				recTemp.setRect(Gdx.graphics.getWidth()/4 * id  + mg/2, controlPos[id] + 80, Gdx.graphics.getWidth()/4 - mg, 40);
				if(recTemp.contains(pointer)){
					if(!typer[id].isShowing())
						typer[id].show();
					else
						typer[id].hide();
					return true;
				}
				
				
				if(selection[Util.getControllerID(controller)] == 0){
					System.exit(0);
				}
				//selecionar opções
				if(selection[Util.getControllerID(controller)] == 1){
						if(!exiting){
							timer = 1;
							optionsE = true;
							exiting = true;
						}
				}
				if(selection[Util.getControllerID(controller)] == 2){
					if(KambojaMain.getControllers().size() >= 2){
						if(!exiting){
							timer = 1;
							exiting = true;
						}
						sound_select();
					}
				}
			}
		}
		if(buttonCode == start){
			if(timer > 4){
				if(Util.getControllerID(controller) == -1){
					if(KambojaMain.getControllers().size() < 4){
						PlayerController pc = new PlayerController(0, controller, firstPlayerAvailable(), "PLAYER " + (KambojaMain.getControllers().size()+1));
						KambojaMain.getControllers().add(pc);
						new_player();
					}
				}
			}
		}

		
		return true;
	}
	
	public void change_sound(){
		if(GameState.SFX)
		change_sound.play(0.7f, (float)Math.random()*0.1f + 0.95f, 0);
	}
	
	public void new_player(){
		if(GameState.SFX)
		new_player.play(1, (float)Math.random()*0.1f + 0.95f, 0);
	}
	
	public void sound_select(){
		if(GameState.SFX)
		sound_select.play(0.5f, (float)Math.random()*0.1f + 0.95f, 0);
	}
	
	public int nextPlayer(int player){
		int next = player+1;
		if(next == KambojaMain.getPlayerSkinsSize()) next = 0;
		
		while(!isAvailable(next)){
			next++;
			if(next == KambojaMain.getPlayerSkinsSize()) next = 0;
		}
		
		return next;
	}
	
	public int previousPlayer(int player){
		int prev = player-1;
		if(prev == -1) prev = KambojaMain.getPlayerSkinsSize()-1;
		
		while(!isAvailable(prev)){
			prev--;
			if(prev == -1) prev = KambojaMain.getPlayerSkinsSize()-1;
		}
		
		return prev;
	}
	
	public boolean isAvailable(int player){
		for(PlayerController pc : KambojaMain.getControllers()){
			if(pc.getPlayer() == player){
				return false;
			}
		}
		return true;
	}
	
	public int firstPlayerAvailable(){
		for(int i = 0; i < KambojaMain.getPlayerSkinsSize(); i ++){
			if(isAvailable(i))
			return i;
		}
		
		return -1;
	}

	public boolean buttonUp(Controller controller, int buttonCode) {
		return false;
	}

	public boolean axisMoved(Controller controller, int axisCode, float value) {
		cursors.axisMoved(controller, axisCode, value);

		return false;
	}

	public boolean povMoved(Controller controller, int povCode, PovDirection value) {
		int id = Util.getControllerID(controller);
		if(id != -1){
			
		
		if(controller.getName().toUpperCase().contains("XBOX") && controller.getName().contains("360")){
			if(value == XBox.BUTTON_DPAD_DOWN){
				if(Util.getControllerID(controller) != -1){
					//KambojaMain.getControllers().remove(Util.getControllerID(controller));
					if(controllerSelecion[Util.getControllerID(controller)] == 0)
						controllerSelecion[Util.getControllerID(controller)] = 1;
					else
						controllerSelecion[Util.getControllerID(controller)] = 0;
				}
				change_sound();
			}
			if(value == XBox.BUTTON_DPAD_UP){
				if(Util.getControllerID(controller) != -1){
					//KambojaMain.getControllers().remove(Util.getControllerID(controller));
					if(controllerSelecion[Util.getControllerID(controller)] == 0)
						controllerSelecion[Util.getControllerID(controller)] = 1;
					else
						controllerSelecion[Util.getControllerID(controller)] = 0;
				}
				change_sound();
			}
			
			if(Util.getControllerID(controller) != -1){
				if(value == XBox.BUTTON_DPAD_RIGHT){
					if(controllerSelecion[Util.getControllerID(controller)] == 0){
						KambojaMain.getControllers().get(Util.getControllerID(controller)).nextWeapon();
					}
					else{
						int player = KambojaMain.getControllers().get(Util.getControllerID(controller)).getPlayer();
						player = nextPlayer(player);
						KambojaMain.getControllers().get(Util.getControllerID(controller)).setPlayer(player);
					}
					arrowScale[Util.getControllerID(controller) * 2 + 1] = 1.3f;
					change_sound();
				}
				if(value == XBox.BUTTON_DPAD_LEFT){
					if(controllerSelecion[Util.getControllerID(controller)] == 0){
						KambojaMain.getControllers().get(Util.getControllerID(controller)).previousWeapon();
					}
					else{
						int player = KambojaMain.getControllers().get(Util.getControllerID(controller)).getPlayer();
						player = previousPlayer(player);
						KambojaMain.getControllers().get(Util.getControllerID(controller)).setPlayer(player);
					}
					
					arrowScale[Util.getControllerID(controller) * 2] = 1.3f;
					change_sound();
				}
			}
			
		}
		}
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

	public void resize(int width, int height) {}
	
	boolean hasKeyboard = false;
	
	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Keys.B){
			if(timer > 4){
				if(KambojaMain.getControllers().size() < 4){
					int pl = (int) (Math.random() * 5);
					while(!isAvailable(pl)){
						pl = (int) (Math.random() * 5);
					}
					
					BotController bc = new BotController(pl);
					KambojaMain.getControllers().add(bc);
					new_player();
				}
				else{
					exiting = true;
					timer = 1;
					return false;
				}
			}
		}
		if(keycode == Keys.N){
			if(KambojaMain.getControllers().size() >= 1){
				for(int i = KambojaMain.getControllers().size() - 1; i >= 0; i --){
					PlayerController pc = KambojaMain.getControllers().get(i);
					if(pc instanceof BotController){
						KambojaMain.getControllers().remove(pc);
						break;
					}
				}
			}
		}
		
		if(keycode == Keys.ENTER){
			if(KambojaMain.getControllers().size() < 4 && timer > 4 && !hasKeyboard){
				KeyboardController kc = new KeyboardController(0, firstPlayerAvailable(),  "PLAYER " + (KambojaMain.getControllers().size()+1));
				KambojaMain.getControllers().add(kc);
				hasKeyboard = true;
				new_player();
			}
		}
		
		return false;
	}
	@Override
	public boolean keyUp(int keycode) {
		return false;
	}


}
