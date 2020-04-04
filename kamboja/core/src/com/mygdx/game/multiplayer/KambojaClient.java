package com.mygdx.game.multiplayer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import com.mygdx.game.KambojaMain;

public class KambojaClient {
	
	UDPConnection connection;
	InetAddress ip;
	String hostIP;	
	Socket tcpSocket;
	KambojaConnectionListener listener;
	public boolean connected = true;
	
	public KambojaClient(String hostIP, KambojaConnectionListener listener) {
		this.listener = listener;
		this.hostIP = hostIP;
		try {			
			connection = new UDPConnection(12345, 54321);
			ip = InetAddress.getLocalHost();
			
			try {
				tcpSocket = new Socket(InetAddress.getByName(hostIP), 54321);
			}
			catch(Exception e) {
				e.printStackTrace();
				listener.connectionFailed("Could not connect to host");
				connected = false;
				return;
			}
			//Receive server TCP
			new Thread(() -> {
				while(KambojaMain.gameAlive) {
					try {
						if(!tcpSocket.isClosed()){
							ObjectInputStream ois = new ObjectInputStream(tcpSocket.getInputStream());
							listener.receiveTCP((KambojaPacket) ois.readObject());
						}
						else {
							listener.disconnected();
							break;
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
				
			}).start();
			
			//Receive server UDP
			new Thread(() -> {
				while(true) {						
					KambojaPacket kp = (KambojaPacket) connection.receive();
					receivePackage(kp);
				}
			}).start();
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void connect() {
		if(connected)
			listener.connected();
	}
	
	public void setConnectionListener(KambojaConnectionListener listener) {
		this.listener = listener;
	}
	
	public void receivePackage(KambojaPacket kp) {
		listener.receiveUDP(kp);
	}
	
	public void sendTCPPackage(KambojaPacket kp) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(tcpSocket.getOutputStream());
			oos.flush();
			oos.writeObject(kp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendPackage(KambojaPacket kp) {
		try {
			connection.send(kp, InetAddress.getByName(hostIP));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
