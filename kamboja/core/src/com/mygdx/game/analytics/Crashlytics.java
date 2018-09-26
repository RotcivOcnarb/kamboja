package com.mygdx.game.analytics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import com.mygdx.game.KambojaMain;

public class Crashlytics extends PrintStream{

	public Crashlytics() throws FileNotFoundException {
		super(System.out);
	}
	
	public void println(String s) {
	      if(s.startsWith("Exception in thread")) {
	    	  
	    	  EventData data = new EventData("exception");
	    	  data.put("error", s);
	    	  KambojaMain.event(data);
	      }
	}

}
