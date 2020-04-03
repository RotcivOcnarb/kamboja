package com.mygdx.game.multiplayer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;

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
			System.out.println("Creating Client connection to server");
			
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
			listener.connected();

			//Receive server TCP
			new Thread(() -> {
				while(true) {
					try {
						if(tcpSocket.getInputStream().read() != -1){
							ObjectInputStream ois = new ObjectInputStream(tcpSocket.getInputStream());
							listener.receiveTCP((KambojaPacket) ois.readObject());
						}
						else {
							listener.disconnected();
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}).start();
			
			//Receive server UDP
			new Thread(() -> {
				while(true) {						
					KambojaPacket kp = (KambojaPacket) connection.receive();
					System.out.println("Kamboja packed read successfully, forwarding");
					receivePackage(kp);
				}
			}).start();
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void receivePackage(KambojaPacket kp) {
		listener.receiveUDP(kp);
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
