package com.mygdx.game;

public class TransitionFactory {
	
	public TransitionFactory(){
		
	}
	
	public Transition getTransition(String name){
		
		name = name.trim();
		name = name.toLowerCase();
		
		if(name.equals("bars")){
			return new BarsTransition();
		}
		
		if(name.equals("circle")){
			return new CircleTransition();
		}
		
		return null;
		
	}

}
