package com.mygdx.game.desktop;

import java.awt.DisplayMode;
import java.awt.FlowLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;

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

	public KambojaLauncher() {
		JPanel panel = new JPanel();
		
		JCheckBox chk_full = new JCheckBox("Fullscreen");
		chk_full.setSelected(true);
		
		JLabel lbl_res = new JLabel("Resolution: ");
		JComboBox<ScreenSize> combo = new JComboBox<ScreenSize>();
		
		GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		GraphicsDevice dev = devices[0];
		DisplayMode[] md = dev.getDisplayModes();
		
		int cont = 0;
		for(DisplayMode mode : md){
			ScreenSize d = new ScreenSize(mode.getWidth(), mode.getHeight());
			if(!contains(combo, d)){
				combo.addItem(d);
				
				if(Toolkit.getDefaultToolkit().getScreenSize().getWidth() == d.getWidth() &&
						Toolkit.getDefaultToolkit().getScreenSize().getHeight() == d.getHeight()){
					combo.setSelectedIndex(cont);
				}
				
				cont++;
				
			}
		}
	
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