package com.mygdx.game.multiplayer.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.SocketException;

import javax.swing.JTextArea;

import com.mygdx.game.multiplayer.DataIdentifier;
import com.mygdx.game.multiplayer.MultiplayerController;
import com.mygdx.game.objects.Util;

public class ReceiveInfoLoop implements Runnable{


	
	int mode = DataIdentifier.PLAYER_SELECT;
	
	MulticastSocket server;
	JTextArea area;
	int port;
	
	public int getPort(){
		return port;
	}
	
	public ReceiveInfoLoop(JTextArea area, int port){
		this.area = area;
		this.port = port;
		try {
			server = new MulticastSocket(port);
			server.setReuseAddress(true);
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
							
							for(int i = ServerWindow.mpc.size() - 1; i >= 0; i --){
								MultiplayerController mc = ServerWindow.mpc.get(i);
								if(mc.getAddress().getHostAddress().equals(pack.getAddress().getHostAddress())){
									ServerWindow.mpc.remove(mc);
								}
							}
							
							break;
						case DataIdentifier.SERVER_DISCONNECT:
							ip = pack.getAddress().getHostAddress();
							
							print("IP " + ip + " DISCONNECTED");
							
							break;
							
						case DataIdentifier.PLAYER_CONNECTED:
							
							int skin = pack.getData()[1];
							int weapon = pack.getData()[2];
							byte[] identifier = new byte[5];
							for(int i = 0; i < 5; i ++){
								identifier[i] = pack.getData()[i + 3];
							}
							byte[] name = new byte[pack.getData().length - 8];
							for(int i = 0; i < name.length; i ++){
								name[i] = pack.getData()[i + 8];
							}
							MultiplayerController mc = new MultiplayerController(weapon, skin, new String(name).trim(), pack.getAddress(), identifier, ServerWindow.mpc.size());
							
							ServerWindow.mpc.add(mc);
							print("Controller connected: Skin: " + skin + " Weapon: " + weapon + " Name: " + new String(name).trim());
							
							break;
						case DataIdentifier.PLAYER_DISCONNECTED:

							identifier = new byte[5];
							
							for(int i = 0; i < 5; i ++){
								identifier[i] = pack.getData()[1 + i];
							}
							mc = getMultiplayerController(identifier);
							
							if(mc != null){
								ServerWindow.mpc.remove(mc);
								print("Controller disconected from IP: " + pack.getAddress().getHostAddress());
							}
							
							break;
						case DataIdentifier.SERVER_PLAYER_MAIN_MENU_INFO:
							
							skin = pack.getData()[1];
							weapon = pack.getData()[2];
							identifier = new byte[5];
							for(int i = 0; i < 5; i ++){
								identifier[i] = pack.getData()[i + 3];
							}
							name = new byte[pack.getData().length - 8];
							for(int i = 0; i < name.length; i ++){
								name[i] = pack.getData()[i + 8];
							}
							
							MultiplayerController pl = getMultiplayerController(identifier);
							if(pl != null){
								
								if(pl.getPlayer() != skin || pl.getWeapon() != weapon || !pl.getName().equals(new String(name).trim())) {
									print("Player updated to:\n\tSkin: " + skin + "\n\tWeapon: " + weapon + "\n\tName: " + new String(name).trim());
								}
								
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
	
	public MultiplayerController getMultiplayerController(byte[] identifier){
		for(MultiplayerController mc : ServerWindow.mpc){
			if(Util.compareID(mc.getIdentifier(), identifier)){
				return mc;
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
