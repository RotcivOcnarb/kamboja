package com.mygdx.game.desktop;

import java.awt.DisplayMode;
import java.awt.FlowLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.KambojaMain;


public class KambojaLauncher{
	public static void main (String[] arg) {
		new KambojaLauncher();
	}

	int width;
	int height;
	boolean fullscreen;
	
	public void readConfig() {
		//Loads the config.ini file and sets all the parameters
				try {
					File conf = new File("config.ini");
					
					BufferedReader br = new BufferedReader(new FileReader(conf));
					
					HashMap<String, String> configs = new HashMap<String, String>();
					String s;
					while((s = br.readLine()) != null){
						try{
							configs.put(s.split("=")[0], s.split("=")[1]);
						}
						catch(ArrayIndexOutOfBoundsException e){
							
						}
						//System.out.println(s);
					}
					
					br.close();
					if (configs.get("Width") == null) {
						GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
						GraphicsDevice dev = devices[0];
						DisplayMode[] md = dev.getDisplayModes();
						
						width = md[md.length - 1].getWidth();
						configs.put("Width", width + "");
					}
					else {
						width = Integer.parseInt(configs.get("Width"));
					}
					
					if (configs.get("Height") == null) {
						GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
						GraphicsDevice dev = devices[0];
						DisplayMode[] md = dev.getDisplayModes();

						height = md[md.length - 1].getHeight();
						configs.put("Height", height + "");
					}
					else {
						height = Integer.parseInt(configs.get("Height"));
					}
					
					if(configs.get("Fullscreen") == null) {
						fullscreen = true;
						configs.put("Fullscreen", fullscreen + "");
					}
					else {
						fullscreen = Boolean.parseBoolean(configs.get("Fullscreen"));
						
					}
					
					BufferedWriter bw = new BufferedWriter(new FileWriter(conf));
					
					for(String key : configs.keySet()) {
						bw.write(key + "=" + configs.get(key) + "\n");
					}
					
					bw.close();
					
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					System.exit(0);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
	
	public KambojaLauncher() {
		
		readConfig();
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Kamboja";
		
			config.width = width;
			config.height = height;
			config.fullscreen = fullscreen;
			config.foregroundFPS = 60;
			config.backgroundFPS = 60;
			config.resizable = false;
			config.addIcon("icon.png", FileType.Internal);
			
		new LwjglApplication(new KambojaMain(), config);
		
	}
	
	public void KaaambojaLauncher() {
		boolean debug = false;
		PrintStream logTxt;
		try {
			logTxt = new PrintStream(new File ("log.txt"));
			System.setErr(logTxt);
			//System.setOut(logTxt);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		JPanel panel = new JPanel();
		
		JCheckBox chk_full = new JCheckBox("Fullscreen");
		chk_full.setSelected(!debug);
		
		JLabel lbl_res = new JLabel("Resolution: ");
		JComboBox<ScreenSize> combo = new JComboBox<ScreenSize>();
		
		GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		GraphicsDevice dev = devices[0];
		DisplayMode[] md = dev.getDisplayModes();
		
		int cont = 0;
		for(DisplayMode mode : md){
			ScreenSize d = new ScreenSize(mode.getWidth(), mode.getHeight());			
			if(!contains(combo, d)) {// && Math.abs(d.width / (float)d.height - (16/9f)) < 0.1f){
				combo.addItem(d);
				
				if(Toolkit.getDefaultToolkit().getScreenSize().getWidth() == d.getWidth() &&
						Toolkit.getDefaultToolkit().getScreenSize().getHeight() == d.getHeight()){
					combo.setSelectedIndex(cont);
				}
				
				cont++;
				
			}
		}
		
		readConfig();
		
		if(debug) combo.setSelectedIndex(7);
	
		panel.setLayout(new FlowLayout());

		panel.add(chk_full);
		panel.add(lbl_res);
		panel.add(combo);
				
		if(JOptionPane.showConfirmDialog(null, panel, "Kamboja", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION){
				
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Kamboja";
		
			config.width = ((ScreenSize)combo.getSelectedItem()).getWidth();
			config.height = ((ScreenSize)combo.getSelectedItem()).getHeight();
			config.fullscreen = chk_full.isSelected();
			config.foregroundFPS = 60;
			config.backgroundFPS = 60;
			config.resizable = false;
			config.addIcon("icon.png", FileType.Internal);
			
		new LwjglApplication(new KambojaMain(), config);
		}
	}
	
	static class ScreenSize{
		
		int width;
		int height;
		
		public ScreenSize(int width, int height){
			this.width = width;
			this.height = height;
		}
		
		public int getWidth(){
			return width;
		}
		
		public int getHeight(){
			return height;
		}
		
		public String toString(){
			return width + "x" + height;
		}
		
		public boolean equals(ScreenSize s){
			if(s.width == width && s.height == height){
				return true;
			}
			return false;
		}
		
	}
	
	public static boolean contains(JComboBox<ScreenSize> combo, ScreenSize d){
		for(int i = 0; i < combo.getItemCount(); i ++){
			if(combo.getItemAt(i).equals(d)){
				return true;
			}
		}
		return false;
	}

}