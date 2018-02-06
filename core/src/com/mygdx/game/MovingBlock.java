package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class MovingBlock extends Obstacle {
	
	private float x1, y1, x2, y2;
	
	private boolean state = true; // forward or backwards
	
	private float t = 0;
	
	public MovingBlock(int x1, int y1, int x2, int y2){
		
		this.x1 = (float) x1;
		this.y1 = (float) y1;
		this.x2 = (float) x2;
		this.y2 = (float) y2;		
		
		Texture texture = new Texture("map-tile.jpg");
		
		Sprite s = new Sprite(texture);
		s.setX(this.x1);
		s.setY(this.y1);
		s.setSize(1, 1);
		s.setOrigin(0, 0);
		
		setSprite(s);
		
	}
	

	@Override
	public void update() {
		
		t += state? 1 : -1;
		
		getSprites()[0].setX(x1 + ((x2 - x1) * t / 100));
		getSprites()[0].setY(y1 + ((y2 - y1) * t / 100));
		
		if(t == 100 || t == 0){
			state = !state;
		}		
	}
	
}
