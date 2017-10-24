package com.mygdx.game.multiplayer.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import javax.swing.JTextArea;

import com.mygdx.game.multiplayer.DataIdentifier;
import com.mygdx.game.multiplayer.MultiplayerController;

public class ReceiveInfoLoop implements Runnable{


	
	int mode = DataIdentifier.PLAYER_SELECT;
	
	DatagramSocket server;
	JTextArea area;
	int port;
	
	public int getPort(){
		return port;
	}
	
	public ReceiveInfoLoop(JTextArea area, int port){
		this.area = area;
		this.port = port;
		try {
			server = new DatagramSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setMode(int mode){
		this.mode = mode;
	}
	
	public void run() {

		
			while(true){
				try{
					byte[] msg = new byte[256];
					DatagramPacket pack = new DatagramPacket(msg, msg.length);
					server.receive(pack);
	
					switch(pack.getData()[0]){
						case DataIdentifier.SERVER_CONNECT:
							String ip = pack.getAddress().getHostAddress();
							
							print("IP " + ip + " CONNECTED");
							
							break;
						case DataIdentifier.SERVER_DISCONNECT:
							ip = pack.getAddress().getHostAddress();
							
							print("IP " + ip + " DISCONNECTED");
							
							break;
							
						case DataIdentifier.PLAYER_CONNECTED:
							
							int skin = pack.getData()[1];
							int weapon = pack.getData()[2];
							byte[] name = new byte[pack.getData().length - 3];
							for(int i = 0; i < name.length; i ++){
								name[i] = pack.getData()[i + 3];
							}
						
							ServerWindow.mpc.add(new MultiplayerController(weapon, skin, new String(name).trim(), pack.getAddress()));
							print("Controller connected: Skin: " + skin + " Weapon: " + weapon + " Name: " + new String(name).trim());
							
							break;
						case DataIdentifier.PLAYER_DISCONNECTED:

							int index = pack.getData()[1];
							
							ServerWindow.mpc.remove(index);
							print("Controller disconected from IP: " + pack.getAddress().getHostAddress() + " at index " + index);
							
							break;
						case DataIdentifier.PLAYER_MAIN_MENU_INFO:
							
							skin = pack.getData()[1];
							weapon = pack.getData()[2];
							name = new byte[pack.getData().length - 3];
							for(int i = 0; i < name.length; i ++){
								name[i] = pack.getData()[i + 3];
							}
							
							MultiplayerController pl = getMultiplayerController(pack.getAddress(), skin);
							if(pl != null){
								pl.setPlayer(skin);
								pl.setWeapon(weapon);
								pl.setName(new String(name).trim());
							}
							
							break;
						default:
							print("Identifier unknown");
					}
				}catch(SocketException e){
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		
		
	}
	
	public MultiplayerController getMultiplayerController(InetAddress addr, int skin){
		for(MultiplayerController mc : ServerWindow.mpc){
			if(mc.getAddress().getHostAddress().equals(addr.getHostAddress())){
				if(mc.getPlayer() == skin){
					return mc;
				}

			}
		}
		
		return null;
	}
	
	public void dispose(){
		server.close();
	}
	
	public void print(String s){
		area.append("\n" + s);
	}

}
