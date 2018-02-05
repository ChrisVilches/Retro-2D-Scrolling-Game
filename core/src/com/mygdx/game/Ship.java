package com.mygdx.game;

import com.badlogic.gdx.Gdx;

public class Ship implements IUpdatable {
	
	float x, y;
	
	boolean moving = false;
	
	float collisionPorcentageWidth = 0.8f;
	float collisionPorcentageHeight = 0.8f;
	
	public Ship(float absoluteX, float absoluteY){
		
		this.x = absoluteX;
		this.y = absoluteY;
		
	}
	
	public void update(){
		
		if(!moving) return;
		
		float xMouse = Gdx.input.getX();
		float yMouse = Gdx.graphics.getHeight()-Gdx.input.getY();
		
		x = x - ((x - xMouse) * 0.1f);
		y = y - ((y - yMouse) * 0.1f);		
	}

}
