package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Disposable;

public abstract class Obstacle implements IUpdatable, Disposable, ICollisionable {

	private Sprite[] sprites;		
	protected Sprite[] getSprites(){ return sprites; }
	protected void setSprites(Sprite[] s) { this.sprites = s; }
	protected void setSprite(Sprite s) { this.sprites = new Sprite[]{ s }; }
	
	protected Obstacle(){

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
