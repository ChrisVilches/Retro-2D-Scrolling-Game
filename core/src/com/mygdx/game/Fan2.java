package com.mygdx.game;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;

public class Fan2 extends Obstacle {
	
	
	private float speed;
	
	Polygon[] propeller;
	
	private float propellerWidth = 0.1f;
	private float propellerLength = 100;
	
	public Fan2(int centerX, int centerY, float propellerLength, float propellerWidth, float speed){
		
		this.speed = speed;
		
		setX((float) centerX);
		setY((float) centerY);		
		
		Texture texture = new Texture("x.png");
		
		setSprite(new Sprite(texture));
	
		
		setWidth(size);
		setHeight(size);
		
		propellerLength = size/1.6f;
		propellerWidth = size * 0.038f;
		
		
		setAngle(90f);
		
		propeller = new Polygon[4];
		
		for(int i=0; i<4; i++){
			propeller[i] = new Polygon();	
			
			propeller[i].setOrigin(getX() + 0.5f, getY() + 0.5f);
		}
		
		float[] vertices = new float[] { 
				getX() + 0.5f - propellerWidth, getY() + 0.5f, 
				getX() + 0.5f - propellerWidth, getY() + 0.5f + propellerLength, 
				getX() + 0.5f + propellerWidth, getY() + 0.5f + propellerLength, 
				getX() + 0.5f + propellerWidth, getY() + 0.5f };
		
		propeller[0].setVertices(vertices);
		propeller[1].setVertices(vertices);
		propeller[2].setVertices(vertices);
		propeller[3].setVertices(vertices);
		
		
		setX(getX() - (getWidth() - 1)/2);
		setY(getY() - (getHeight() - 1)/2);
		
	}
	
	@Override
	public boolean touches(float leftUpperX, float leftUpperY, float width, float height) {
		
		for(int i=0; i<4; i++){
			if(rectangleIntersectionRotated(propeller[i], leftUpperX, leftUpperY, width, height)){ 
				return true; 
			}
		}
		
		return false;
	}
	
	private boolean rectangleIntersectionRotated(Polygon p, float leftUpperX, float leftUpperY, float width, float height) {
	    
	    Polygon rPoly = new Polygon(new float[] { 
	    		leftUpperX, leftUpperY, 
	    		leftUpperX, leftUpperY + height, 
	    		leftUpperX + width, leftUpperY + height, 
	    		leftUpperX + width, leftUpperY });
	    
	    return Intersector.overlapConvexPolygons(rPoly, p);
	
	}
	
	
	@Override
	public void update() {		
		setAngle(getAngle() - speed);		
		if(getAngle() <= 0) setAngle(90f);
		propeller[0].setRotation(-getAngle() + 45);
		propeller[1].setRotation(-getAngle() + 90 + 45);
		propeller[2].setRotation(-getAngle() + 180 + 45);
		propeller[3].setRotation(-getAngle() + 270 + 45);
	
	}
	

}
