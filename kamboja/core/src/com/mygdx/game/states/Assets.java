package com.mygdx.game.states;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.KambojaMain;

public class Assets {
	
	public static void LOADSHIT(AssetManager assets) {
		menuState(assets);
		optionsState(assets);
		mapSelectState(assets);
		playerSelectState(assets);
		helpState(assets);
		creditsState(assets);
	}
	
	private static void menuState(AssetManager assets) {
		assets.load("menu/background.png", Texture.class);
		assets.load("menu/bolinha.png", Texture.class);
		assets.load("menu/explosao.png", Texture.class);
		assets.load("menu/armas.png", Texture.class);
		assets.load("menu/fumaca.png", Texture.class);
		assets.load("menu/placa_letras.png", Texture.class);
		assets.load("menu/sombra_letras.png", Texture.class);
		assets.load("menu/fumaca_back.png", Texture.class);
		assets.load("menu/fumaca_front.png", Texture.class);
		
		assets.load("menu/VERSUS.png", Texture.class);
		assets.load("menu/COOP_off.png", Texture.class);
		assets.load("menu/ONLINE_off.png", Texture.class);
		assets.load("menu/HELP.png", Texture.class);
		assets.load("menu/OPTIONS.png", Texture.class);
		assets.load("menu/CREDITS.png", Texture.class);
		
		for(int i = 0; i < 6; i ++)
		assets.load("menu/E" + (i+1) + ".png", Texture.class);
		
		for(int i = 0; i < 5; i ++)
		assets.load("menu/exp" + (i+1) + ".png", Texture.class);
	}
	
	private static void optionsState(AssetManager assets) {
		assets.load("menu/options/fundo.jpg", Texture.class);
		assets.load("menu/options/main_sign.png", Texture.class);
		assets.load("menu/options/above_bar.png", Texture.class);
		assets.load("menu/options/options_sign.png", Texture.class);
		
		assets.load("menu/options/seta esquerda.png", Texture.class);
		assets.load("menu/options/seta direita.png", Texture.class);
		assets.load("menu/options/on.png", Texture.class);
		assets.load("menu/options/off.png", Texture.class);
		
		assets.load("menu/options/barra.png", Texture.class);
		assets.load("menu/options/small_cog.png", Texture.class);
		
		assets.load("menu/player_select/selection.png", Texture.class);
		
		assets.load("menu/options/light.png", Texture.class);
		assets.load("menu/options/debug_mode.png", Texture.class);
		
		assets.load("menu/options/engrenagem.png", Texture.class);
	}
	
	private static void mapSelectState(AssetManager assets) {
		assets.load("menu/player_select/chain.png", Texture.class);
		assets.load("menu/map_select/map_name.png", Texture.class);
		
		assets.load("menu/map_select/gear_start.png", Texture.class);
		assets.load("menu/map_select/gear_back.png", Texture.class);
		assets.load("menu/map_select/map_containers.png", Texture.class);
		
		assets.load("menu/player_select/selection.png", Texture.class);
		
		assets.load("menu/map_select/frame_map.png", Texture.class);
		
		assets.load("menu/map_select/fundo.jpg", Texture.class);
		assets.load("menu/map_select/frame_options.png", Texture.class);

	}
	
	private static void playerSelectState(AssetManager assets) {
		assets.load("menu/player_select/fundo.jpg", Texture.class);
		assets.load("menu/player_select/selection.png", Texture.class);
		assets.load("menu/player_select/chain.png", Texture.class);
		
		assets.load("Weapons/Icon/lock.png", Texture.class);
		
		assets.load("menu/player_select/press start.png", Texture.class);
		
		assets.load("Weapons/Icon/Pistol.png", Texture.class);
		assets.load("Weapons/Icon/PistolAkimbo.png", Texture.class);
		assets.load("Weapons/Icon/minigun.png", Texture.class);
		assets.load("Weapons/Icon/shotgun.png", Texture.class);
		assets.load("Weapons/Icon/Mp5.png", Texture.class);
		assets.load("Weapons/Icon/Flamethrower.png", Texture.class);
		assets.load("Weapons/Icon/Bazook.png", Texture.class);
		assets.load("Weapons/Icon/Laser.png", Texture.class);
		
		assets.load("Weapons/In-game/Taurus.png", Texture.class);
		assets.load("Weapons/In-game/Taurus Akimbo.png", Texture.class);
		assets.load("Weapons/In-game/Minigun.png", Texture.class);
		assets.load("Weapons/In-game/sss.png", Texture.class);
		assets.load("Weapons/In-game/MP5.png", Texture.class);
		assets.load("Weapons/In-game/flahme.png", Texture.class);
		assets.load("Weapons/In-game/Bazooka.png", Texture.class);
		assets.load("Weapons/In-game/Laser.png", Texture.class);
		
		assets.load("menu/player_select/ok.png", Texture.class);
		
		for(int i = 0; i < 4; i ++) {
			assets.load("menu/player_select/frame"+(i+1)+".png", Texture.class);
			assets.load("menu/player_select/glass"+(i+1)+".png", Texture.class);
			assets.load("menu/player_select/caixa p"+(i+1)+".png", Texture.class);
			assets.load("menu/player_select/subglass"+(i+1)+".png", Texture.class);
			assets.load("menu/player_select/gear"+(i+1)+".png", Texture.class);
		}
		
		assets.load("menu/player_select/back_btn.png", Texture.class);
	}
	
	private static void helpState(AssetManager assets) {
		assets.load("menu/help/fundo.jpg", Texture.class);
		assets.load("menu/help/base.png", Texture.class);
		
		assets.load("menu/help/placa shoot.png", Texture.class);
		assets.load("menu/help/placa sprint.png", Texture.class);
		
		assets.load("menu/help/engrenagem.png", Texture.class);
		
		assets.load("menu/help/placa move.png", Texture.class);
		assets.load("menu/help/placa aim.png", Texture.class);
	}
	
	private static void creditsState(AssetManager assets) {
		assets.load("menu/credits/fundo.jpg", Texture.class);
		assets.load("menu/credits/placa.png", Texture.class);
		assets.load("menu/credits/cano_d.png", Texture.class);
		assets.load("menu/credits/cano_e_b.png", Texture.class);
		assets.load("menu/credits/cano_e_c.png", Texture.class);
		assets.load("menu/credits/chain_big.png", Texture.class);
		assets.load("menu/credits/chain_small.png", Texture.class);
	}

}
