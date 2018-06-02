package com.mygdx.game;

import java.io.PrintStream;

public class MultiPrintStream extends PrintStream {

	private PrintStream[] streams;
 
    public MultiPrintStream (PrintStream... streams) {
    	super(streams[0]);
 
        this.streams = streams;
    }
 
    public void println(String s) {
        for(PrintStream ps : this.streams)
            ps.println(s);
    }
 
}