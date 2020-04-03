package com.mygdx.game.multiplayer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class KambojaHost {

	DatagramSocket datagramSocket;
	InetAddress ip;
	byte[] receive = new byte[65535]; 
	
	public KambojaHost() {
		try {
			System.out.println("Creating host...");
			datagramSocket = new DatagramSocket();
			ip = InetAddress.getLocalHost();
			
			DatagramPacket receivePacket = new DatagramPacket(receive, receive.length);
			
			new Thread(new Runnable() {
				public void run() {
					try {
						
						datagramSocket.receive(receivePacket);
						
						ByteArrayInputStream bis = new ByteArrayInputStream(receive);
						ObjectInputStream ois = new ObjectInputStream(bis);
						
						KambojaPacket kp = (KambojaPacket) ois.readObject();
						System.out.println("Packet of type " + kp.type + " received!");
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
		
		//Trata o q tem q tratar
		
	}
	
	public void sendPackage(KambojaPacket kp, String clientIP) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(kp);
			oos.flush();
			
			DatagramPacket dp = new DatagramPacket(bos.toByteArray(), bos.size(), InetAddress.getByName(clientIP), 3224);
			datagramSocket.send(dp);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
