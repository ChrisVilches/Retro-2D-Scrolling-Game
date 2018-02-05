package com.mygdx.game;

public interface ICollisionable {
	
	// Model coordinates (not the ones relative to the screen)
	public boolean touches(float leftUpperX, float leftUpperY, float width, float height);
}
