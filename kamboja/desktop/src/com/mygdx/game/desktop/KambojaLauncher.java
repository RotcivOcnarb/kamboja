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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.mygdx.game.KambojaMain;


public class KambojaLauncher{
	public static void main (String[] arg) {
		new KambojaLauncher();
	}

	int width;
	int height;
	boolean fullscreen;
	boolean debug;
	
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
					
					if(configs.get("DebugMode") == null) {
						debug = false;
					}
					else {
						debug = Boolean.parseBoolean(configs.get("DebugMode"));
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
		
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Kamboja");
				
			config.setWindowedMode(width, height);
			config.setIdleFPS(60);
			config.setResizable(false);
			config.setWindowIcon("icon.png");
		
		if(debug)
			openConsoleWindow();
			
		new Lwjgl3Application(new KambojaMain(), config);
		Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		
	}
	
	static void openConsoleWindow() {
		
		JFrame frame = new JFrame("Kamboja Console");
		frame.setContentPane(new KambojaConsoleWindow());
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
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