package com.mygdx.game.multiplayer.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import javax.swing.JTextArea;

import com.mygdx.game.multiplayer.DataIdentifier;
import com.mygdx.game.multiplayer.MultiplayerController;

public class SenderInfoLoop implements Runnable{

	int mode = DataIdentifier.PLAYER_SELECT;
	
	DatagramSocket server;
	int port;
	
	public int getPort(){
		return port;
	}
	
	public SenderInfoLoop(int port) {
		this.port = port;
		try {
			server = new DatagramSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		
		while(true){
			try{
			switch(mode){
			case DataIdentifier.PLAYER_SELECT:
				
				DatagramSocket ds = new DatagramSocket();
				//manda as informações de todos os manos pra todos os ips da sala
				for(MultiplayerController target : ServerWindow.mpc){
					for(MultiplayerController mc : ServerWindow.mpc){
						
						byte[] bytes = new byte[4 + mc.getName().getBytes().length];
						bytes[0] = DataIdentifier.PLAYER_MAIN_MENU_INFO;
						bytes[1] = (byte)mc.getPlayer();
						bytes[2] = (byte)mc.getWeapon();
						bytes[3] = (byte)ServerWindow.mpc.indexOf(mc);
						for(int i = 0; i < mc.getName().getBytes().length; i ++){
							bytes[4 + i] = mc.getName().getBytes()[i];
						}
						
						DatagramPacket pkg;
						pkg = new DatagramPacket(bytes, bytes.length, target.getAddress(), port);
					
						ds.send(pkg);
						
					}
				}
				
				
				break;
			}
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			
		}
		
	}

}
