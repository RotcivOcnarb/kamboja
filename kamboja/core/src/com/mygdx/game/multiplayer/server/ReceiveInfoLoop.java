package com.mygdx.game.multiplayer.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.JTextArea;

import com.mygdx.game.multiplayer.DataIdentifier;

public class ReceiveInfoLoop implements Runnable{

	public static final int PLAYER_SELECT = 0;
	public static final int MAP_SELECT = 1;
	public static final int GAME_STATE = 2;
	
	int mode = PLAYER_SELECT;
	
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
							
							byte[] addr = new byte[pack.getData().length - 1];
							for(int i = 0; i < addr.length; i ++){
								addr[i] = pack.getData()[i+1];
							}
		
							
							String ip = InetAddress.getByAddress(addr).getHostAddress();
							
							print("IP " + ip + " CONNECTED");
							
							break;
							
						case DataIdentifier.PLAYER_CONNECTED:
							
							int skin = pack.getData()[1];
							int weapon = pack.getData()[2];
							byte[] name = new byte[pack.getData().length - 3];
							for(int i = 0; i < name.length; i ++){
								name[i] = pack.getData()[i + 3];
							}
						
							print("Controller connected: Skin: " + skin + " Weapon: " + weapon + " Name: " + new String(name));
							
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
	
	public void dispose(){
		server.close();
	}
	
	public void print(String s){
		area.append("\n" + s);
	}

}
