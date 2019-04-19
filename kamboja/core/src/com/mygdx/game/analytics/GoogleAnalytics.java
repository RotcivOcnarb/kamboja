package com.mygdx.game.analytics;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Stack;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.KambojaMain;

public class GoogleAnalytics implements Runnable{
	
	String userID;
	String sessionID;
	String UA;
	
	String user_agent;
	
	Thread thread;
	
	Stack<HitData> queue;
	
	public GoogleAnalytics(String userID, String UA) {
		
		this.userID = userID;
		this.sessionID = UUID.randomUUID().toString();
		this.UA = UA;
		
		user_agent = System.getProperty("os.name") + " - " + System.getProperty("sun.cpu.isalist");
		
		queue = new Stack<HitData>();
		
		thread = new Thread(this);
		thread.start();
	}
	
	@Override
	public void run() {
		while(KambojaMain.gameAlive) {
			while(!queue.empty()) {
				HitData data = queue.pop();
				sendHit(data);
			}
		}
		
	}
	
	public HitData event(String category, String action, String label) {
		return event(category, action, label, null);
	}
	
	public HitData event(String category, String action, String label, HashMap<String, String> customs) {
		
		HitData data = constructBaseHit("event");
		data.put("ec", category);
		data.put("ea", action);
		data.put("el", label);
		
		if(customs != null) {
			for(String s : customs.keySet()) {
				data.put(s, customs.get(s));
			}
		}
		
		return data;
	}
	
	public void sendHit(HitData data) {
		try {
			
			String url = "https://www.google-analytics.com/collect";
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			//add request header
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", user_agent);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			
			String urlParameters = data.payload();
			
			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + urlParameters);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();


		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void sessionStart() {
		HitData data = constructBaseHit("screenview");
		data.put("sc", "start");
		data.put("cd", "Kamboja");
		sendHit(data);
	}
	
	public void sessionEnd() {
		HitData data = constructBaseHit("screenview");
		data.put("sc", "end");
		data.put("cd", "Kamboja");
		sendHit(data);
	}
	
	public HitData constructBaseHit(String hitType) {
		HitData data = new HitData();
		
		try {
		data.put("tid", UA);
		data.put("ds", "game");
		data.put("cid", sessionID);
		data.put("uid", userID);
		data.put("an", "Kamboja");
		data.put("sr", Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight());
		data.put("t", hitType);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	

}
