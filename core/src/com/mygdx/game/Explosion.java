package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Explosion {
	
	float x, y;
	
	Animation<TextureRegion> animation;
	
	public float elapsed = 0;
	
	public void incrementElapsed(float n){
		elapsed += n;
	}
	
	public Explosion(float x, float y){
		this.x = x;
		this.y = y;
		animation = GifDecoder.loadGIFAnimation(Animation.PlayMode.LOOP, Gdx.files.internal("explosion.gif").read());
	}
		
	public Explosion(){
		this(0, 0);
	}
	
	public void initialize(){
		elapsed = 0;
	}
	

}
