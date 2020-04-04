package com.mygdx.game.multiplayer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.mygdx.game.KambojaMain;

public class UDPConnection {

	//UDP
	DatagramSocket receiveSocket;
	DatagramSocket sendSocket;
	byte[] receiveByte = new byte[65535];
	int udpport, tcpport;
	final DatagramPacket receivePacket = new DatagramPacket(receiveByte, 65535);
	
	public UDPConnection(int udpport, int tcpport) {
		this.udpport = udpport;
		try {
			receiveSocket = new DatagramSocket(udpport);
			sendSocket = new DatagramSocket();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public Object receive() {
		try {
			receiveSocket.receive(receivePacket);
			
			ByteArrayInputStream bis = new ByteArrayInputStream(receiveByte);
			ObjectInputStream ois = new ObjectInputStream(bis);
			return ois.readObject();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void send(Object data, InetAddress ip) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(data);
			oos.flush();
			
			DatagramPacket dp = new DatagramPacket(bos.toByteArray(), bos.size(), ip, udpport);
			sendSocket.send(dp);
			
			System.out.println("udp packet successfully sent to ip " + ip.getHostAddress());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void dispose() {
		receiveSocket.close();
		sendSocket.close();
	}
	
}


