package com.mygdx.game.multiplayer.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class UDPTest {

	public static void main(String args[]){
		new UDPTest();
	}
	
	public UDPTest() {

		Scanner s = new Scanner(System.in);
		
		
		try {
			DatagramSocket ds = new DatagramSocket();
		System.out.println("Informe o IP do servidor: ");
		String ip = s.nextLine();
		InetAddress addr = InetAddress.getByName(ip);

		
		
		System.out.println("Informe a porta a ser conectada: ");
		int port = Integer.parseInt(s.nextLine());
		
		String msg = s.nextLine();
		while(!msg.equals("")){
			
			byte[] msgBytes = msg.getBytes();
			
			DatagramPacket pkg;
			pkg = new DatagramPacket(msgBytes, msgBytes.length, addr, port);
		
			ds.send(pkg);
			
			msg = s.nextLine();
		}
		System.out.println("Exiting");
		ds.close();
		
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		s.close();
		
	}

}
