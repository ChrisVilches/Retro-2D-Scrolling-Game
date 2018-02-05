package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;

public abstract class Weather implements IRenderable, IUpdatable, Disposable{
	
	public abstract void update();
	
	public void render(SpriteBatch batch){
		
	}
	
	public void render(ShapeRenderer shapes){
		
	}
	
	public void dispose(){
		
	}
}
