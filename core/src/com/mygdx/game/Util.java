package com.mygdx.game;

public class Util {
	
	public static boolean rectangleCollision(float x1, float y1, float width1, float height1, float x2, float y2, float width2, float height2){
		
		System.out.printf("(%f, %f, %f, %f) en (%f, %f, %f, %f)\n", x1, y1, width1, height1, x2, y2, width2, height2);
		
		if(rectangleInsideRectangle(x1, y1, width1, height1, x2, y2, width2, height2)) return true;
		if(rectangleInsideRectangle(x2, y2, width2, height2, x1, y1, width1, height1)) return true;		
		return false;
	}
	
	private static boolean pointInsideRectangle(float x1, float y1, float width, float height, float x2, float y2){		
		if(x2 < x1) return false;
		if(x1 + width < x2) return false;		
		if(y2 < y1) return false;
		if(y1 + height < y2) return false;		
		return true;		
	}
	
	private static boolean rectangleInsideRectangle(float x1, float y1, float width1, float height1, float x2, float y2, float width2, float height2){
		if(pointInsideRectangle(x1, x2, width1, height1, x2, y2)) return true;
		if(pointInsideRectangle(x1, x2, width1, height1, x2 + width2, y2)) return true;
		if(pointInsideRectangle(x1, x2, width1, height1, x2, y2 + height2)) return true;
		if(pointInsideRectangle(x1, x2, width1, height1, x2 + width2, y2 + height2)) return true;
		return false;
	}
}
