package com.mygdx.game.objects.map;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.objects.Util;

public class IslandBackground {
	
	Texture background;
	
	ArrayList<CloudObject> clouds;
	OrthographicCamera parallaxCamera;
	Texture[] cloud_tex;
	
	double timer = 0;

	public IslandBackground() {
		background = new Texture("maps/assets/island_background.png");
		background.setWrap(TextureWrap.MirroredRepeat, TextureWrap.MirroredRepeat);
		clouds = new ArrayList<IslandBackground.CloudObject>();
		parallaxCamera = new OrthographicCamera(1920, 1080);
		cloud_tex = new Texture[4];
		for(int i = 0; i < 4; i ++){
			cloud_tex[i] = new Texture("maps/assets/nuvem" + (i+1) + ".png");
		}
		
		for(int i = 0; i < Math.random() * 10; i ++){
			Texture tex = cloud_tex[(int)(Math.random() * 4)];
			boolean left = Math.random() < 0.5;
			clouds.add(new CloudObject(
					new Vector2(
							(float) (Math.random() * 1920),
							(float) (Math.random() * 1080)
					),
					left ? -1 : 1,
					tex));
		}
	}
	
	public void render(SpriteBatch sb, OrthographicCamera camera){
		parallaxCamera.position.set(camera.position.cpy());
		parallaxCamera.zoom = 0.5f;
		
		parallaxCamera.update();
				
		sb.setProjectionMatrix(Util.getNormalProjection());
		sb.setColor(1, 1, 1, 1);
		sb.begin();
		sb.draw(background, 0, 0, 1920, 1080);
		
		for(int i = clouds.size() - 1; i >= 0; i --){
			CloudObject co = clouds.get(i);
			co.render(sb);
		}
		
		sb.end();
	}
	
	public void update(double delta){
		timer -= delta;
		
		if(timer < 0){
			timer = Math.random() * 2 + 2;
			Texture tex = cloud_tex[(int)(Math.random() * 4)];
			boolean left = Math.random() < 0.5;
			clouds.add(new CloudObject(
					new Vector2(
							(left ? 1920 : -tex.getWidth()),
							(float) (Math.random() * 1080)
					),
					left ? -1 : 1,
					tex));
			
			System.out.println(clouds.size());
		}
		
		for(int i = clouds.size() - 1; i >= 0; i --){
			CloudObject co = clouds.get(i);
			if(co.update(delta)){
				clouds.remove(co);
			}
		}
	}
	
	public void dispose(){
		
	}
	
	class CloudObject{
		
		Vector2 position;
		int direction;
		Texture tex;
		
		public CloudObject(Vector2 position, int direction, Texture tex){
			this.position = position;
			this.direction = direction;
			this.tex = tex;
		}
		
		public void render(SpriteBatch sb){
			
			sb.draw(tex, position.x, position.y);
			
		}
		
		public boolean update(double delta){
			
			switch(direction){
			case -1:
				position.x -= delta*30;
				
				if(position.x < -tex.getWidth()){
					return true;
				}
				
				break;
			case 1:
				position.x += delta*30;
				if(position.x > 1920){
					return true;
				}
				break;
			}
			
			return false;
		}
		
	}

}
