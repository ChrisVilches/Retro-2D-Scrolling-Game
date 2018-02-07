package com.mygdx.game;

import java.util.Map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;

public class Fan2 extends Obstacle implements IDebuggable {	
	
	private float speed;
	
	private Polygon[] propellers;
	
	private float collisionAreaUpperLeftX;
	private float collisionAreaUpperLeftY;
	private float collisionAreaWidth;
	private float collisionAreaHeight;
	
	@MapConstructor
	public Fan2(Map<String, Object> map){

		this((Integer)map.get("centerX"), 
				(Integer)map.get("centerY"), 
				Util.numberToFloat(map.get("propellerLength")), 
				Util.numberToFloat(map.get("propellerWidth")), 
				Util.numberToFloat(map.get("speed")));
		
	}
	
	
	public Fan2(int centerX, int centerY, float propellerLength, float propellerWidth, float speed){
		
		this.speed = speed;
		
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
		}

		
		setSprites(new Sprite[]{ sprites[0], sprites[1], sprites[2], sprites[3] });		
		
		propellers = new Polygon[4];
		for(int i=0; i<4; i++){
			propellers[i] = new Polygon();
			propellers[i].setOrigin(getSprites()[i].getX(), getSprites()[i].getY()+1);
		}

		float[] vertices = new float[] { 
				getSprites()[0].getX() - propellerWidth/2, getSprites()[0].getY() + 1, 
				getSprites()[0].getX() - propellerWidth/2, getSprites()[0].getY() + 1 + propellerLength, 
				getSprites()[0].getX() + propellerWidth/2, getSprites()[0].getY() + 1 + propellerLength, 
				getSprites()[0].getX() + propellerWidth/2, getSprites()[0].getY() + 1 };

		for(int i=0; i<4; i++){
			propellers[i].setVertices(vertices);
		}
		
		
		setCollisionAreaSquare();

	}
	
	
	private void setCollisionAreaSquare(){
		
		update(); // Required so that the propellers are rotated at least once.
		
		float minX = 1000000000, maxX = -1000000000, minY = 1000000000, maxY = -1000000000;
		
		for(int i=0; i<4; i++){
			
			float[] v = propellers[i].getTransformedVertices();
			
			for(int j=0; j<4; j++){
				minX = Math.min(minX, v[j*2]);
				maxX = Math.max(maxX, v[j*2]);
				minY = Math.min(minY, v[j*2 + 1]);
				maxY = Math.max(maxY, v[j*2 + 1]);
			}
		}
		
		collisionAreaUpperLeftX = minX;
		collisionAreaUpperLeftY = minY;
		collisionAreaWidth = maxX - minX;
		collisionAreaHeight = maxY - minY;		
	}
	
	@Override
	public boolean touches(float leftUpperX, float leftUpperY, float width, float height) {
		
		if(!Util.rectangleCollision(
				leftUpperX, leftUpperY, 
				width, height, 
				collisionAreaUpperLeftX, collisionAreaUpperLeftY, 
				collisionAreaWidth, collisionAreaHeight)){
			return false;
		}
		
		for(int i=0; i<4; i++){
			if(rectangleIntersectionRotated(propellers[i], leftUpperX, leftUpperY, width, height)){ 
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
	
	
	Polygon square = null;
	
	@Override
	public Polygon[] getDebugPolygons(){
		
		Polygon[] polys = new Polygon[propellers.length + 1];
		
		polys[0] = new Polygon();
		
		polys[0].setVertices(new float[]{
				collisionAreaUpperLeftX, collisionAreaUpperLeftY,
				collisionAreaUpperLeftX, collisionAreaUpperLeftY + collisionAreaHeight,
				collisionAreaUpperLeftX + collisionAreaWidth, collisionAreaUpperLeftY + collisionAreaHeight,
				collisionAreaUpperLeftX + collisionAreaWidth, collisionAreaUpperLeftY
		});
		
		for(int i=0; i<4; i++){
			polys[i+1] = propellers[i];
		}
				
		return polys;
	}
	
	
	@Override
	public void update() {		
		
		Sprite s;
		
		for(int i=0; i<4; i++){
			s = getSprites()[i];			
			float newRot = s.getRotation() - speed;			
			s.setRotation(newRot);
			propellers[i].setRotation(-newRot);
		}		
		
		for(int i=0; i<4; i++){
			s = getSprites()[i];
			if(s.getRotation() <= -360) s.setRotation(360f);			
		}
	}
	
	@Override
	public void pause(boolean b){
		
	}
	
	@Override
	public boolean isPaused(){
		return false;
	}

}
