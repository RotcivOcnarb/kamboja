package com.mygdx.game.multiplayer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.mygdx.game.multiplayer.KambojaPacket.PacketType;

public class KambojaClient {
	
	DatagramSocket receiveSocket;
	DatagramSocket sendSocket;
	InetAddress ip;
	byte[] receive = new byte[65535]; 
	String hostIP;
	KambojaPacketCallback confirmCallback;
	
	public KambojaClient() {
		try {
			
			receiveSocket = new DatagramSocket(12345);
			sendSocket = new DatagramSocket();
			ip = InetAddress.getLocalHost();
			
			final DatagramPacket receivePacket = new DatagramPacket(receive, receive.length);
			
			System.out.println("Creating listening thread");
			
			new Thread(new Runnable() {
				public void run() {
					try {
						
						receiveSocket.receive(receivePacket);
						
						System.out.println("Packet received, decoding");
						
						ByteArrayInputStream bis = new ByteArrayInputStream(receive);
						ObjectInputStream ois = new ObjectInputStream(bis);
						
						KambojaPacket kp = (KambojaPacket) ois.readObject();
						System.out.println("Kamboja packed read successfully, forwarding");
						receivePackage(kp);
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void receivePackage(KambojaPacket kp) {
		if(kp.type == PacketType.CONNECTION_CONFIRM) {
			confirmCallback.callback(kp);
		}
		
		//Trata o q tem q tratar
		
	}
	
	public void sendPackage(KambojaPacket kp) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(kp);
			oos.flush();
			
			DatagramPacket dp = new DatagramPacket(bos.toByteArray(), bos.size(), InetAddress.getByName(hostIP), 12345);
			sendSocket.send(dp);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void connectToHost(String ip, KambojaPacketCallback callback) throws UnknownHostException {
		hostIP = ip;
		KambojaPacket kp = new KambojaPacket(PacketType.CONNECT_TO_SERVER, InetAddress.getLocalHost());
		sendPackage(kp);
		confirmCallback = callback;
	}
	

}
