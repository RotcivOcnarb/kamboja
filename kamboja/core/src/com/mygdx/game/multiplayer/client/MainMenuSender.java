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
			
			byte[] msgBytes = new byte[8 + pc.getName().getBytes().length];
			msgBytes[0] = DataIdentifier.PLAYER_CONNECTED;
			msgBytes[1] = (byte)pc.getPlayer();
			msgBytes[2] = (byte)pc.getWeapon();
			for(int i = 0; i < 5; i ++){
				msgBytes[3 + i] = pc.getIdentifier()[i];
			}
			for(int i = 0; i < pc.getName().getBytes().length; i ++){
				msgBytes[8 + i] = pc.getName().getBytes()[i];
			}
			DatagramPacket pkg;
			pkg = new DatagramPacket(msgBytes, msgBytes.length, addr, KambojaMain.PORT);
			ds.send(pkg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void disconnectPlayer(byte[] identifier){
		try {
			
			byte[] msgBytes = new byte[6];
			msgBytes[0] = DataIdentifier.PLAYER_DISCONNECTED;
			for(int i = 0; i < 5; i ++){
				msgBytes[1 + i] = identifier[i];
			}
			
			DatagramPacket pkg;
			pkg = new DatagramPacket(msgBytes, msgBytes.length, addr, KambojaMain.PORT);
			for(int i = 0; i < 10; i ++)
			ds.send(pkg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		
		while(!stop){
			
			//Mandar para cada não-multiplayer-controller
			//-Nome
			//-Skin
			//-Arma
			
			if(KambojaMain.getControllers() != null) {
				//TODO: esse carinha aqui não tá enviando os pacotes quando o cliente e o server estão na mesma máquina
				for(int j = KambojaMain.getControllers().size() - 1; j >= 0; j --){
					PlayerController controller = KambojaMain.getControllers().get(j);
					if(!(controller instanceof MultiplayerController)){
						try{
							
							byte[] msgBytes = new byte[8 + controller.getName().getBytes().length];
							msgBytes[0] = DataIdentifier.SERVER_PLAYER_MAIN_MENU_INFO;
							msgBytes[1] = (byte)controller.getPlayer();
							msgBytes[2] = (byte)controller.getWeapon();
							for(int i = 0; i < 5; i ++){
								msgBytes[3 + i] = controller.getIdentifier()[i];
							}
							for(int i = 0; i < controller.getName().getBytes().length; i ++){
								msgBytes[8 + i] = controller.getName().getBytes()[i];
							}
							
							DatagramPacket pkg;
							pkg = new DatagramPacket(msgBytes, msgBytes.length, addr, KambojaMain.PORT);
						
							ds.send(pkg);
						}
						catch(ArrayIndexOutOfBoundsException e){
							
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
