package com.mygdx.game.analytics;

import java.util.HashMap;

public class HitData {
		
	HashMap<String, String> data;
	
	public HitData() {
		data = new HashMap<String, String>();
	}
	
	public void put(String key, String value) {
		data.put(key, value);
	}
	
	public HashMap<String, String> getData(){
		return data;
	}
	
	public String payload() {
		String payload = "";
		for(String s : data.keySet()) {
			if(!s.equals("v"))
			payload += "&" + s + "=" + data.get(s);
		}
		
		return "v=1" + payload;
	}

}
