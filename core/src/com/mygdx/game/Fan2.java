package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;

public class Fan2 extends Obstacle {	
	
	private float speed;
	
	Polygon[] propellers;
	
	
	public Fan2(int centerX, int centerY, float propellerLength, float propellerWidth, float speed){
		
		this.speed = speed;
		
		this.speed = 2;

		
		Texture texture = new Texture("dot-blue.png");
		
		Sprite[] sprites = new Sprite[]{
				new Sprite(texture),
				new Sprite(texture),
				new Sprite(texture),
				new Sprite(texture)
		};
		
		for(int i=0; i<4; i++){
			sprites[i].setPosition(centerX+0.5f, centerY-0.5f);
			sprites[i].setOrigin(propellerWidth/2, propellerLength);
			sprites[i].rotate(90 * i);
			sprites[i].setSize(propellerWidth, propellerLength);
			sprites[i].setAlpha(0.4f);
		}

		
		setSprites(new Sprite[]{ sprites[0], sprites[1], sprites[2], sprites[3] });
		
		
		propellers = new Polygon[4];
		for(int i=0; i<4; i++){
			propellers[i] = new Polygon();
			
			//propellers[i].setOrigin(getSprites()[i].getOriginX(), getSprites()[i].getOriginY());
			propellers[i].setOrigin(getSprites()[i].getX(), getSprites()[i].getY()+1);
		}
		
		System.out.printf("%f %f\n", propellers[0].getOriginX(), propellers[0].getOriginY());
		

		float[] vertices = new float[] { 
				getSprites()[0].getX()  - propellerWidth/2, getSprites()[0].getY(), 
				getSprites()[0].getX()  - propellerWidth/2, getSprites()[0].getY() + propellerLength, 
				getSprites()[0].getX()  + propellerWidth/2, getSprites()[0].getY() + propellerLength, 
				getSprites()[0].getX()  + propellerWidth/2, getSprites()[0].getY()};

		for(int i=1; i<vertices.length; i+=2)
			vertices[i] += 1;
		
		
		propellers[0].setVertices(vertices);
		propellers[1].setVertices(vertices);
		propellers[2].setVertices(vertices);
		propellers[3].setVertices(vertices);
		
		
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
		
		Sprite s;
		
		for(int i=0; i<4; i++){
			s = getSprites()[i];
			s.setRotation(s.getRotation() - speed);
			if(s.getRotation() <= -360) s.setRotation(360f);			
		}
		
		s = getSprites()[0];
		
		propellers[0].setRotation(-s.getRotation());
		propellers[1].setRotation(-s.getRotation() + 90);
		propellers[2].setRotation(-s.getRotation() + 180);
		propellers[3].setRotation(-s.getRotation() + 270);				

	}
	

}
