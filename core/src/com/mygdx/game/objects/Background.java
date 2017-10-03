package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class Background {
	
	private ShapeRenderer sr;
	private Color tempC;
	private float tileWidth;
	private float tileHeight;
	private float timer;

	public Background() {
		sr = new ShapeRenderer();
		tempC = new Color();
		tileWidth = Gdx.graphics.getWidth()/32f; //tamanho do tile no efeito do começo
		tileHeight = Gdx.graphics.getHeight()/16f;
		timer = -1;
	}
	
	public void render(SpriteBatch sb){
		tempC.set(180/255f, 41/255f, 45/255f, 1);
		sr.setColor(tempC);

		//calcula tamanho dos tiles e desenha eles
		for(int i = 0; i < 32; i ++){
			for(int j = 0; j < 16; j ++){
				sr.begin(ShapeType.Filled);
					float w = tileWidth*(timer+(i+j)/50f);
					if(w < 0) w = 0;
					if(w > tileWidth) w = tileWidth;
					float h = tileHeight*(timer+(i+j)/50f);
					if(h < 0) h = 0;
					if(h > tileHeight) h = tileHeight;
					
					//float u = (i*tileWidth)/(float)Gdx.graphics.getWidth();
					//float u2 = (i*tileWidth + w)/(float)Gdx.graphics.getWidth();
					//float v = 1f - (j*tileHeight)/(float)Gdx.graphics.getHeight();
					//float v2 = 1f - (j*tileHeight + h)/(float)Gdx.graphics.getHeight();
										
					//sb.draw(logoMain, i*tileWidth, j*tileHeight, w, h, u, v, u2, v2);
					sr.box(i*tileWidth, j*tileHeight, 0, w, h, 0);
				sr.end();
			}
		}
		
	}
	
	public void update(double delta, boolean exiting){
		if(exiting){ //saindo do state, escondendo tudo
			timer -= delta;
			if(timer > 1) timer = 1;
		}
		else{
			timer += delta;
		}
	}
	
	public void dispose(){
		sr.dispose();
	}

}
