package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class BarsTransition extends Transition {

	private int time;
	private final int MAX_TIME = 100;
	
	private final float SPACE = 30;
	
	private final float screenHeight;
	
	private final int TRANSITION_SPEED = 2;
	
	public BarsTransition(){
		screenHeight = Gdx.graphics.getHeight();
	}
	

	@Override
	public void render(ShapeRenderer shapes) {
		
		float h = screenHeight * ((float)time/(float)MAX_TIME);
		
		for(int i=0; i<20; i++){
			shapes.rect(i * SPACE*2, 0, SPACE, h);
			shapes.rect((i * SPACE*2) + SPACE, screenHeight, SPACE, -h);
		}
	}

	@Override
	public void initialize() {
		time = MAX_TIME;
	}

	@Override
	public boolean fadeOut() {
		time -= TRANSITION_SPEED;
		return time <= 0;
	}

	@Override
	public boolean fadeIn() {
		time += TRANSITION_SPEED;
		return time >= MAX_TIME;
	}

}
