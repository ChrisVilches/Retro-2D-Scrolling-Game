package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class Transition implements IRenderable {
	
	public Transition(){
		initialize();
	}
	
	public abstract void initialize();
	
	public abstract boolean fadeOut();
	
	public abstract boolean fadeIn();

	@Override
	public void render(SpriteBatch batch) {
	}
	
	@Override
	public void render(ShapeRenderer shapes) {
	}
	

}
