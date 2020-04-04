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
	int port;
	
	public UDPConnection(int port) {
		this.port = port;
		try {
			receiveSocket = new DatagramSocket(port);
			sendSocket = new DatagramSocket();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public Object receive() {
		try {
			byte[] receiveByte = new byte[65535];
			DatagramPacket receivePacket = new DatagramPacket(receiveByte, 65535);
			receiveSocket.receive(receivePacket);
			
			ByteArrayInputStream bis = new ByteArrayInputStream(receivePacket.getData());
			ObjectInputStream ois = new ObjectInputStream(bis);
			Object o = ois.readObject();
			ois.close();
			bis.close();
			
			return o;
			
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
			
			DatagramPacket dp = new DatagramPacket(bos.toByteArray(), bos.size(), ip, port);
			sendSocket.send(dp);
			
			oos.close();
			bos.close();
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


