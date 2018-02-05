package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;

public class Fan extends Obstacle {	
	
	private float speed;
	
	private Polygon[] propellers;
	
	private float propellerWidth;
	private float propellerLength;
	
	public Fan(int centerX, int centerY, float size, float speed){
		
		this.speed = speed;
		
		setX((float) centerX);
		setY((float) centerY);		
		
		Texture texture = new Texture("x.png");

		setSprite(new Sprite(texture));

		
		setWidth(size);
		setHeight(size);
		
		propellerLength = size / 2;
		propellerWidth = size * 0.038f;		
		
		setAngle(90f);
		
		propellers = new Polygon[4];
		
		for(int i=0; i<4; i++){
			propellers[i] = new Polygon();
			propellers[i].setOrigin(getX() + 0.5f, getY() + 0.5f);
		}
		
		float[] vertices = new float[] { 
				getX() + 0.5f - propellerWidth, getY() + 0.5f, 
				getX() + 0.5f - propellerWidth, getY() + 0.5f + propellerLength, 
				getX() + 0.5f + propellerWidth, getY() + 0.5f + propellerLength, 
				getX() + 0.5f + propellerWidth, getY() + 0.5f };
		
		propellers[0].setVertices(vertices);
		propellers[1].setVertices(vertices);
		propellers[2].setVertices(vertices);
		propellers[3].setVertices(vertices);		
			
		setX(getX() - ((getWidth() - 1)/2));
		setY(getY() + ((getHeight() - 1)/2));		
	
	}
	
	@Override
	public boolean touches(float leftUpperX, float leftUpperY, float width, float height) {
		
		for(int i=0; i<4; i++){
			if(rectangleIntersectionRotated(propellers[i], leftUpperX, leftUpperY, width, height)){ 
				return true; 
			}
		}
		
		return false;
	}
	
	public Polygon[] getPropellers(){
		return propellers;
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
		propellers[0].setRotation(-getAngle());
		propellers[1].setRotation(-getAngle() + 90);
		propellers[2].setRotation(-getAngle() + 180);
		propellers[3].setRotation(-getAngle() + 270);

	}


}
