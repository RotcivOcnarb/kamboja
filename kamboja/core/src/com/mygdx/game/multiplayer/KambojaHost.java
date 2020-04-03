package com.mygdx.game.multiplayer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.mygdx.game.multiplayer.KambojaPacket.PacketType;

public class KambojaHost {

	DatagramSocket receiveSocket;
	DatagramSocket sendSocket;
	InetAddress ip;
	byte[] receive = new byte[65535]; 
	
	public KambojaHost() {
		try {
			System.out.println("Creating host...");
			receiveSocket = new DatagramSocket(12345);
			sendSocket = new DatagramSocket();
			ip = InetAddress.getLocalHost();
			
			final DatagramPacket receivePacket = new DatagramPacket(receive, receive.length);
			
			new Thread(new Runnable() {
				public void run() {
					System.out.println("Thread start");
					try {
						
						receiveSocket.receive(receivePacket);
						System.out.println("package received");
						
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
	
	public void receivePackage(KambojaPacket kp) throws UnknownHostException {
		
		if(kp.type == PacketType.CONNECT_TO_SERVER) {
			sendPackage(new KambojaPacket(PacketType.CONNECTION_CONFIRM, InetAddress.getLocalHost()), kp.ipOrigin);
		}
		
		
		//Trata o q tem q tratar
	}
	
	public void sendPackage(KambojaPacket kp, InetAddress ip) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(kp);
			oos.flush();
			
			DatagramPacket dp = new DatagramPacket(bos.toByteArray(), bos.size(), ip, 12345);
			sendSocket.send(dp);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
