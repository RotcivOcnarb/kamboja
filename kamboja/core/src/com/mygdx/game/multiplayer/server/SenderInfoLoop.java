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
	JTextArea area;
	
	public int getPort(){
		return port;
	}
	
	public SenderInfoLoop(JTextArea area, int port) {
		this.port = port;
		this.area = area;
		try {
			server = new DatagramSocket();
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
						
						ServerWindow.getInstance().addSent();
						
						byte[] bytes = new byte[9 + mc.getName().getBytes().length];
						bytes[0] = DataIdentifier.CLIENT_PLAYER_MAIN_MENU_INFO;
						bytes[1] = (byte)mc.getPlayer();
						bytes[2] = (byte)mc.getWeapon();
						bytes[3] = (byte)ServerWindow.mpc.indexOf(mc);
						for(int i = 0; i < 5; i ++){
							bytes[4 + i] = mc.getIdentifier()[i];
						}
						for(int i = 0; i < mc.getName().getBytes().length; i ++){
							bytes[9 + i] = mc.getName().getBytes()[i];
						}
						
						DatagramPacket pkg;
						pkg = new DatagramPacket(bytes, bytes.length, target.getAddress(), port);
					
						ds.send(pkg);
						
						
					}
				}
				ds.close();
				
				break;
			}
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			
		}
		
	}
	
	public void print(String s){
		area.append("\n" + s);
	}

}
