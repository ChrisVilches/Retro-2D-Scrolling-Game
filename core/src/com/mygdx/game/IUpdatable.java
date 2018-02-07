package com.mygdx.game;

public interface IUpdatable {
	void update();
	
	void pause(boolean b);
	
	public boolean isPaused();
}
