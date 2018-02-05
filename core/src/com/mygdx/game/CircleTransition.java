package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class CircleTransition extends Transition {

	private float fadeRadius = 0;
	
	private final float MAX_RADIUS = 480;
	
	@Override
	public void initialize(){
		fadeRadius = MAX_RADIUS;
	}
	
	@Override
	public boolean fadeOut(){
		fadeRadius -= 10;
		return fadeRadius <= 0;
	}
	
	@Override
	public boolean fadeIn(){
		fadeRadius += 10;
		return fadeRadius >= MAX_RADIUS;
	}	

	@Override
	public void render(ShapeRenderer shapes) {
		shapes.circle(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2, fadeRadius);
	}
	
}
