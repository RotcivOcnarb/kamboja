package com.mygdx.game.multiplayer.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.mygdx.game.KambojaMain;
import com.mygdx.game.multiplayer.DataIdentifier;
import com.mygdx.game.multiplayer.MultiplayerController;
import com.mygdx.game.objects.PlayerController;
import com.mygdx.game.states.MainMenu;

public class MainMenuSender implements Runnable{

	boolean stop = false;
		
	MainMenu mainMenu;
	DatagramSocket ds;
	InetAddress addr;
	
	public void stop(){
		stop = true;
	}
	
	public MainMenuSender(MainMenu mainMenu) {
		this.mainMenu = mainMenu;
		try {
			
			ds = new DatagramSocket();
			addr = InetAddress.getByName(KambojaMain.HOST_IP);

		
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public void connectPlayer(PlayerController pc){
		try {
			
			byte[] msgBytes = new byte[3 + pc.getName().getBytes().length];
			msgBytes[0] = DataIdentifier.PLAYER_CONNECTED;
			msgBytes[1] = (byte)pc.getPlayer();
			msgBytes[2] = (byte)pc.getWeapon();
			for(int i = 0; i < pc.getName().getBytes().length; i ++){
				msgBytes[3 + i] = pc.getName().getBytes()[i];
			}
			DatagramPacket pkg;
			pkg = new DatagramPacket(msgBytes, msgBytes.length, addr, KambojaMain.PORT);
			ds.send(pkg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void disconnectPlayer(int playerid){
		try {
			
			byte[] msgBytes = new byte[2];
			msgBytes[0] = DataIdentifier.PLAYER_DISCONNECTED;
			msgBytes[1] = (byte)playerid;
			
			DatagramPacket pkg;
			pkg = new DatagramPacket(msgBytes, msgBytes.length, addr, KambojaMain.PORT);
			ds.send(pkg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		
		while(!stop){
			
			
			//TODO: definir mensagem
			
			//Mandar para cada não-multiplayer-controller
			//-Nome
			//-Skin
			//-Arma
			if(KambojaMain.getControllers() != null) {
				for(PlayerController controller : KambojaMain.getControllers()){
					if(!(controller instanceof MultiplayerController)){
						try{
							byte[] msgBytes = new byte[3 + controller.getName().getBytes().length];
							msgBytes[0] = DataIdentifier.PLAYER_MAIN_MENU_INFO;
							msgBytes[1] = (byte)controller.getPlayer();
							msgBytes[2] = (byte)controller.getWeapon();
							for(int i = 0; i < controller.getName().getBytes().length; i ++){
								msgBytes[3 + i] = controller.getName().getBytes()[i];
							}
							
							DatagramPacket pkg;
							pkg = new DatagramPacket(msgBytes, msgBytes.length, addr, KambojaMain.PORT);
						
							ds.send(pkg);
						}
						catch(Exception e){
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

}
