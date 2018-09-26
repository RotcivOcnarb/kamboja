package com.mygdx.game.analytics;

import java.util.HashMap;

public class EventData {
	
	String name;
	
	HashMap<String, Object> data;
	
	public EventData(String name) {
		this.name = name;
		data = new HashMap<String, Object>();
	}
	
	public void put(String key, Object value) {
		data.put(key, value);
	}
	
	public HashMap<String, Object> getData(){
		return data;
	}

	public String getName() {
		return name;
	}

}
