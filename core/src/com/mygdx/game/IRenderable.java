package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public interface IRenderable {

	void render(SpriteBatch batch);
	
	void render(ShapeRenderer shapes);
}
