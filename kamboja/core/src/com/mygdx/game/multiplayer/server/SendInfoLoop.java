package com.mygdx.game.multiplayer.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.JTextArea;

public class SendInfoLoop implements Runnable{

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
	
	public SendInfoLoop(JTextArea area, int port){
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

		try{
			while(true){
				byte[] msg = new byte[256];
				DatagramPacket pack = new DatagramPacket(msg, msg.length);
				server.receive(pack);
				
				print("\""  + pack.getData() + "\" from client " + pack.getAddress().getHostAddress());
			}
		}catch(SocketException e){
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void dispose(){
		server.close();
	}
	
	public void print(String s){
		area.append("\n" + s);
	}

}
