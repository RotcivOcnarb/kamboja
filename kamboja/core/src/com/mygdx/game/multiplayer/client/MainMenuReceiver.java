package com.mygdx.game.multiplayer.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Comparator;

import com.mygdx.game.KambojaMain;
import com.mygdx.game.multiplayer.DataIdentifier;
import com.mygdx.game.multiplayer.MultiplayerController;
import com.mygdx.game.objects.PlayerController;
import com.mygdx.game.objects.Util;
import com.mygdx.game.states.MainMenu;

public class MainMenuReceiver implements Runnable{


	boolean stop = false;
		
	MainMenu mainMenu;
	DatagramSocket server;
	InetAddress addr;
	
	public void stop(){
		stop = true;
	}
	
	public MainMenuReceiver(MainMenu mainMenu) {
		this.mainMenu = mainMenu;
		try {
			
			server = new DatagramSocket();
			addr = InetAddress.getByName(KambojaMain.HOST_IP);

		
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void run() {

		while(!stop){
			System.out.println("");
			
			try{
				byte[] msg = new byte[256];
				DatagramPacket pack = new DatagramPacket(msg, msg.length);
				server.receive(pack);
				
					switch(pack.getData()[0]){
					
					case DataIdentifier.PLAYER_MAIN_MENU_INFO:
						
						int skin = pack.getData()[1];
						int weapon = pack.getData()[2];
						int index = pack.getData()[3];
						byte[] identifier = new byte[5];
						for(int i = 0; i < 5; i ++){
							identifier[i] = pack.getData()[i + 4];
						}
						byte[] name = new byte[pack.getData().length - 9];
						for(int i = 0; i < name.length; i ++){
							name[i] = pack.getData()[i + 9];
						}
						
						MultiplayerController mc = null;
						for(PlayerController pc : KambojaMain.getControllers()){
							if(Util.compareID(identifier, pc.getIdentifier())){
								if(pc instanceof MultiplayerController)
								mc = (MultiplayerController) pc;
							}
						}
						if(mc == null){
							KambojaMain.getControllers().add(new MultiplayerController(weapon, skin, new String(name).trim(), pack.getAddress(), identifier, index));
							mainMenu.new_player();
							System.out.println("Player detected");
						}
						else{
							mc.setPlayer(skin);
							mc.setWeapon(weapon);
							mc.setName(new String(name).trim());
						}
						Collections.sort(KambojaMain.getControllers(), new Comparator<PlayerController>(){
							public int compare(PlayerController arg0, PlayerController arg1) {
								return arg1.getIndex() - arg0.getIndex();
							}
						});
						
						break;
					
					}
	
			} catch (IOException e) {
				e.printStackTrace();
			}

			
		}
		
	}

}
