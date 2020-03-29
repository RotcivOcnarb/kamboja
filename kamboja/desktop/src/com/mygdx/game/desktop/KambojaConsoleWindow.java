package com.mygdx.game.desktop;

import java.awt.GridLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.mygdx.game.MultiPrintStream;

public class KambojaConsoleWindow extends JPanel{
	private static final long serialVersionUID = 7540802965765622592L;

	public KambojaConsoleWindow() {
		setLayout(new GridLayout(1, 1));
		
		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		
		JScrollPane scroll = new JScrollPane(textArea);
		add(scroll);
		
		System.setOut(new MultiPrintStream(System.out, new ConsolePrintStream(textArea, scroll)));
	}
	
}

class ConsolePrintStream extends PrintStream{

	JTextArea area;
	JScrollPane scroll;
	
	public ConsolePrintStream(JTextArea area, JScrollPane scroll) {
		super(System.out);
		this.area = area;
		this.scroll = scroll;
	}
	
	@Override
	public void println(String s) {
		area.setText(area.getText() + s + "\n");
		JScrollBar vertical = scroll.getVerticalScrollBar();
		vertical.setValue( vertical.getMaximum() );
	}
	
	@Override
	public void print(String s) {
		area.setText(area.getText() + s);
		JScrollBar vertical = scroll.getVerticalScrollBar();
		vertical.setValue( vertical.getMaximum() );
	}
	
}
