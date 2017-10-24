package com.mygdx.game.multiplayer.server;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.Inet4Address;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class ServerWindow extends JFrame implements WindowListener{
	private static final long serialVersionUID = 1L;

	public static void main(String args[]){
		JFrame janela = new ServerWindow();
		janela.setSize(800, 600);
		janela.setLocationRelativeTo(null);
		janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		janela.setVisible(true);
	}
	
	Thread thread;

	public ServerWindow() {
		setLayout(null);
		addWindowListener(this);
		
		JTextArea area = new JTextArea();
		area.setLocation(0, 0);
		area.setSize(600, 600);
		area.setEditable(false);
		
		startServer(area);
		
		add(area);
	}
	ReceiveInfoLoop loop;
	public void startServer(JTextArea area){
		
		loop = new ReceiveInfoLoop(area, 3224);
		
		thread = new Thread(loop);
		thread.start();
		
		try {
			area.append("Server started at IP " + Inet4Address.getLocalHost().getHostAddress() + " and port " + loop.getPort());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
	}

	public void windowActivated(WindowEvent arg0) {
		
	}

	public void windowClosed(WindowEvent arg0) {
		
	}

	public void windowClosing(WindowEvent arg0) {
		loop.dispose();
	}

	public void windowDeactivated(WindowEvent arg0) {
		
	}

	public void windowDeiconified(WindowEvent arg0) {
		
	}

	public void windowIconified(WindowEvent arg0) {
		
	}

	public void windowOpened(WindowEvent arg0) {
		
	}

}
