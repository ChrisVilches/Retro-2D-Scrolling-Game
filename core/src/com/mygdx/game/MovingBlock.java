package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;

public class MovingBlock extends Obstacle {
	
	private float x1, y1, x2, y2;
	
	private boolean state = true; // forward or backwards
	
	private float t = 0;
	
	public MovingBlock(int x1, int y1, int x2, int y2){
		
		this.x1 = (float) x1;
		this.y1 = (float) y1;
		this.x2 = (float) x2;
		this.y2 = (float) y2;
		
		x = this.x1;
		y = this.y1;
		
		texture = new Texture("map-tile.jpg");
		
	}
	
	@Override
	public boolean touches(float leftUpperX, float leftUpperY, float width, float height) {
		
		return Util.rectangleCollision(
				leftUpperX, 
				leftUpperY, 
				width, 
				height, 
				this.x, 
				this.y, 
				this.width, 
				this.height);
	}


	@Override
	public void update() {		
		
		t += state? 1 : -1;
		
		//x = x1 + ((x2 - x1) * t / 100);
		//y = y1 + ((y2 - y1) * t / 100);		
		
		if(t == 100 || t == 0){
			state = !state;
		}		
	}
	
	
	
}
