package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Disposable;

public abstract class Obstacle implements IUpdatable, Disposable, ICollisionable {

	//private float x, y, width, height, angle;
	private Sprite[] sprites;
		
	protected Sprite[] getSprites(){ return sprites; }
	/*protected float getX(){ return x; }
	protected float getY(){ return y; }
	protected float getWidth(){ return width; }
	protected float getHeight(){ return height; }
	protected float getAngle(){ return angle; }*/
	
	/*protected void setX(float x){ this.x = x; }
	protected void setY(float y){ this.y = y; }
	protected void setWidth(float w) { this.width = w; }
	protected void setHeight(float h) { this.height = h; }
	protected void setAngle(float angle) { this.angle = angle; }*/
	protected void setSprites(Sprite[] s) { this.sprites = s; }
	protected void setSprite(Sprite s) { this.sprites = new Sprite[]{ s }; }
	
	protected Obstacle(){
		/*width = 1;
		height = 1;
		angle = 0;*/
	}
		
	
	@Override
	public boolean touches(float leftUpperX, float leftUpperY, float width, float height) {
		
		for(int i=0; i<sprites.length; i++){
			
			boolean collision = Util.rectangleCollision(
					leftUpperX, 
					leftUpperY, 
					width, 
					height, 
					sprites[i].getX(), 
					sprites[i].getY() - (sprites[i].getHeight() - 1), 
					sprites[i].getWidth(), 
					sprites[i].getHeight());			
			if(collision) return true;
		}
		
		return false;
	}

	@Override
	public void dispose() {		
		if(sprites != null){			
			for(int i=0; i<sprites.length; i++){			
				if(sprites[i] != null){
					sprites[i].getTexture().dispose();
				}
			}			
		}
	}

	@Override
	public void update() {
	}
	
}
