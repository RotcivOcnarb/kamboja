package com.mygdx.game.multiplayer.server;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.mygdx.game.multiplayer.MultiplayerController;

public class ServerWindow extends JFrame implements WindowListener{
	private static final long serialVersionUID = 1L;

	public static volatile ArrayList<MultiplayerController> mpc;
	
	JLabel lbl[] = new JLabel[4];
	
	public static void main(String args[]){
		JFrame janela = new ServerWindow();
		janela.setSize(1280, 600);
		janela.setLocationRelativeTo(null);
		janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		janela.setVisible(true);

	}
	
	Thread thread;

	public ServerWindow() {
		
		mpc = new ArrayList<MultiplayerController>();
		
		setLayout(null);
		addWindowListener(this);

		JTextArea area = new JTextArea();
		area.setLocation(0, 0);
		area.setSize(600, 600);
		area.setEditable(false);
		
		JScrollPane sc = new JScrollPane(area);
		sc.setLayout(null);
		sc.setLocation(0, 0);
		sc.setSize(600, 600);
		
		for(int i = 0; i < 4; i ++){
			
			lbl[i] = new JLabel();
			
			lbl[i].setLocation(600, i * 30);
			lbl[i].setSize(1280 - 600, 30);
			lbl[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
			
			add(lbl[i]);
		}
		
		startServer(area);
		sc.add(area);
		add(sc);
	}
	
	@Override
	public void paint(Graphics g){
		super.paint(g);
		
		for(int i = 0; i < 4; i ++){
			if(i < mpc.size()){
				String ID = "";
				
				for(int j = 0; j < 5; j ++){
					ID += mpc.get(i).getIdentifier()[j] + "";
				}
				
			lbl[i].setText(
					"Nome: " + mpc.get(i).getName() + " - " +
					"Skin: " + mpc.get(i).getPlayer() + " - " +		
					"Weapon: " + mpc.get(i).getWeapon() + " - " +
					"ID: " + ID + " - " +
					"Host: " + mpc.get(i).getAddress().getHostAddress()
					);
			}
			else{
				lbl[i].setText("");
			}
		}
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
