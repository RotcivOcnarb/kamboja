package com.mygdx.game.multiplayer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import com.mygdx.game.KambojaMain;

public class KambojaHost {

	UDPConnection connection;
	InetAddress ip;
	
	ServerSocket server;
	
	public HashMap<String, Socket> connectedClients;
	
	KambojaConnectionListener listener;
	
	public KambojaHost(KambojaConnectionListener listener) {
		this.listener = listener;
		try {
			connectedClients = new HashMap<String, Socket>();
			
			System.out.println("Creating host at IP " + InetAddress.getLocalHost().getHostAddress());
			connection = new UDPConnection(12345);
			ip = InetAddress.getLocalHost();
			
			//Recebimento de conexão TCP
			server = new ServerSocket(54321);
			new Thread(() -> {
				while(KambojaMain.gameAlive) {
					try {
						final Socket client = server.accept();
						final String clientIP = client.getInetAddress().getHostAddress();
						System.out.println("Um cliente acabou de se conectar [" + client.getInetAddress().getHostAddress() + "]");
						if(!connectedClients.containsKey(clientIP)) {
							
							if(listener.clientTriesToConnect()) {
								connectedClients.put(clientIP, client);
								
								new Thread(() -> {
									while(KambojaMain.gameAlive) {
										try {
											if(!client.isClosed()) {
												try {
													ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
													receiveTCPPackage(client, ois.readObject());
												}
												catch(Exception e) {
													e.printStackTrace();
													System.out.println("Client " + clientIP + " disconnected");
													connectedClients.remove(clientIP);
													break;
												}
												
											}
											else {
												//Client disconnected
												System.out.println("Client " + clientIP + " disconnected");
												connectedClients.remove(clientIP);
												break;
											}
										}
										catch(Exception e) {
											e.printStackTrace();
										}
									}
								}).start();
							}
							else {
								client.close();
							}
						}
						else {
							//Opa, alquem que já tá conectado tá tentando conectar de novo
						}						
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
			
			
			//Recebimento de pacotes UDP
			new Thread(() -> {
				System.out.println("Thread start");
				
				while(KambojaMain.gameAlive) {
					KambojaPacket kp = (KambojaPacket) connection.receive();
					receivePackage(kp);
				}
			}).start();
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setConnectionListener(KambojaConnectionListener listener) {
		this.listener = listener;
	}
	
	public void receiveTCPPackage(Socket client, Object data) {
		listener.receiveTCP((KambojaPacket)data);
	}
	
	public void receivePackage(KambojaPacket kp) {
		listener.receiveUDP(kp);
	}
	
	public void sendTCPPackage(KambojaPacket kp, String ip) {
		if(connectedClients.containsKey(ip)) {
			try {
				Socket client = connectedClients.get(ip);
				ObjectOutputStream saida = new ObjectOutputStream(client.getOutputStream());
		        saida.flush();
		        saida.writeObject(kp);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void sendPackage(KambojaPacket kp, InetAddress ip) {
		connection.send(kp, ip);
		System.out.println("udp packet successfully sent to ip " + ip.getHostAddress());
	}
	
}
