package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

public class Obstacle implements IUpdatable, Disposable, ICollisionable {

	protected float x, y, width = 1, height = 1;
	protected Texture texture;
	
	public Texture getTexture(){ return texture; }
	public float getX(){ return x; }
	public float getY(){ return y; }
	public float getWidth(){ return width; }
	public float getHeight(){ return height; }
		
	
	@Override
	public boolean touches(float leftUpperX, float leftUpperY, float width, float height) {
		return Util.rectangleCollision(
				leftUpperX, 
				leftUpperY, 
				width, 
				height, 
				this.x, 
				this.y - (this.height - 1), 
				this.width, 
				this.height);
	}

	@Override
	public void dispose() {
		texture.dispose();
	}

	@Override
	public void update() {
	}
	
}
