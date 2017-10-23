package com.mygdx.game.multiplayer.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Scanner;

public class UDPTest {

	public static void main(String args[]){
		new UDPTest();
	}
	
	public UDPTest() {

		Scanner s = new Scanner(System.in);
		
		try {
			System.out.println("Broadcast: " + getBroadcast().getHostAddress());
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
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

	
	public InetAddress getBroadcast() throws SocketException{
		Enumeration<NetworkInterface> interfaces =
			    NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
			  NetworkInterface networkInterface = interfaces.nextElement();
			  if (networkInterface.isLoopback())
			    continue;    // Don't want to broadcast to the loopback interface
			  for (InterfaceAddress interfaceAddress :
			           networkInterface.getInterfaceAddresses()) {
			    InetAddress broadcast = interfaceAddress.getBroadcast();
			    if (broadcast == null)
			      continue;
			    
			    return broadcast;
			  }
			}
			return null;

	}
	
}
