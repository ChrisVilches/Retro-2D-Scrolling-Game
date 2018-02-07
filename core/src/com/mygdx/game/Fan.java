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
	
	public Fan(int tileX, int tileY, float size, float speed){
		
		this.speed = speed;
		
		Texture texture = new Texture("x.png");
		Sprite s = new Sprite(texture);		

		s.setX((float) tileX);
		s.setY((float) tileY);	
		
		s.setSize(size, size);
		
		propellerLength = size / 2;
		propellerWidth = size * 0.038f;		
		
		s.setRotation(90f);
		
		propellers = new Polygon[4];
		
		for(int i=0; i<4; i++){
			propellers[i] = new Polygon();
			propellers[i].setOrigin(s.getX() + 0.5f, s.getY() + 0.5f);
		}
		
		float[] vertices = new float[] { 
				s.getX() + 0.5f - propellerWidth, s.getY() + 0.5f, 
				s.getX() + 0.5f - propellerWidth, s.getY() + 0.5f + propellerLength, 
				s.getX() + 0.5f + propellerWidth, s.getY() + 0.5f + propellerLength, 
				s.getX() + 0.5f + propellerWidth, s.getY() + 0.5f };
		
		propellers[0].setVertices(vertices);
		propellers[1].setVertices(vertices);
		propellers[2].setVertices(vertices);
		propellers[3].setVertices(vertices);		
			
		//s.setX(s.getX() - ((s.getWidth() - 1)/2));
		//s.setY(s.getY() + ((s.getHeight() - 1)/2));	
		
		s.setX(s.getX() + 0.5f);
		s.setY(s.getY() - 0.5f);
		
		s.setOrigin(s.getWidth()/2, s.getHeight()/2);
		
		
		setSprite(s);
	
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
		
		Sprite s = getSprites()[0];
		
		s.setRotation(s.getRotation() - speed);		
		if(s.getRotation() <= 0) s.setRotation(90f);
		propellers[0].setRotation(-s.getRotation());
		propellers[1].setRotation(-s.getRotation() + 90);
		propellers[2].setRotation(-s.getRotation() + 180);
		propellers[3].setRotation(-s.getRotation() + 270);

	}

	@Override
	public void pause(boolean b){
		
	}
	
	@Override
	public boolean isPaused(){
		return false;
	}
	
}
