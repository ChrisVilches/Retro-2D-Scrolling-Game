package com.mygdx.game;

import com.badlogic.gdx.Gdx;

public class Ship implements IUpdatable {
	
	float x, y;
	
	private float absX, absY;
	
	float collisionPorcentageWidth = 0.8f;
	float collisionPorcentageHeight = 0.8f;
	
	private boolean paused = false;
	
	public Ship(float absoluteX, float absoluteY){		
		absX = absoluteX;
		absY = absoluteY;
	}
	
	public void initialize(){
		this.x = absX;
		this.y = absY;
	}	
	
	
	public void update(){
		
		float xMouse = Gdx.input.getX();
		float yMouse = Gdx.graphics.getHeight()-Gdx.input.getY();
		
		x = x - ((x - xMouse) * 0.1f);
		y = y - ((y - yMouse) * 0.1f);		
	}
	
	@Override
	public boolean isPaused(){
		return paused;
	}
	
	@Override
	public void pause(boolean b){
		paused = b;
	}

}
