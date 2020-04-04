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
			connection = new UDPConnection(12345);
			ip = InetAddress.getLocalHost();
			
			try {
				tcpSocket = new Socket(InetAddress.getByName(hostIP), 54321);
			}
			catch(Exception e) {
				e.printStackTrace();
				this.listener.connectionFailed("Could not connect to host");
				connected = false;
				return;
			}
			//Receive server TCP
			new Thread(() -> {
				while(KambojaMain.gameAlive) {
					try {
						if(!tcpSocket.isClosed()){
							ObjectInputStream ois = new ObjectInputStream(tcpSocket.getInputStream());
							this.listener.receiveTCP((KambojaPacket) ois.readObject());
						}
						else {
							this.listener.disconnected();
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
				while(KambojaMain.gameAlive) {
					System.out.println("Waiting for UDP Package");
					KambojaPacket kp = (KambojaPacket) connection.receive();
					System.out.println("KambojaPacket UDP Received " + kp.type);
					receivePackage(kp);
				}
				System.out.println("Finished waiting for UDP packages");
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
