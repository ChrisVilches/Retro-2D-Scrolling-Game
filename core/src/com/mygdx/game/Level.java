package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

class Direction {
	enum Dirs {
		UP, LEFT, DOWN, RIGHT
	};
	
	Dirs dir;
	int steps;
	String dirString;
	
	
	public Direction(String d, int steps){
		dirString = d;
		if(d.equals("Å®")){ dir = Dirs.RIGHT; }
		else if(d.equals("Å´")){ dir = Dirs.DOWN; }
		else if(d.equals("Å©")){ dir = Dirs.LEFT; }
		else if(d.equals("Å™")){ dir = Dirs.UP; }
	
		this.steps = steps;
	}	
	
	@Override
	public String toString(){
		return dirString + ", " + steps;
	}
}

public class Level implements IUpdatable, ICollisionable {
		
	int[][] walls;
	int startX;
	int startY;
	
	boolean moving;
	
	int mapRows, mapCols;
	
	float speed = 0.05f;
	
	float shiftX, shiftY;	
	
	ArrayList<Direction> directions;
	int currentDirection = 0;
	float currentDirectionShift = 0;	
	
	@Override
	public void update(){
		if(moving){
			changeDirection();
			move();
		}		
	}
	
	private void changeDirection(){		
		if(currentDirection >= directions.size()) return;
		currentDirectionShift += speed;		
		if(currentDirectionShift >= directions.get(currentDirection).steps){
			currentDirection++;
			currentDirectionShift = 0;
		}
	}
	
	
	private void move(){
		
		if(currentDirection >= directions.size()) return;
		
		Direction currentDir = directions.get(currentDirection);		
	
		switch(currentDir.dir){		
		case UP: shiftY += speed; break;
		case LEFT: shiftX += speed; break;
		case DOWN: shiftY -= speed; break;
		case RIGHT: shiftX -= speed; break;	
		}
		
	}
	
	
	private void setDimensions(String[] lines){
		int cols = lines[0].length();
		int rows = 0;
		for(int i=0; i<lines.length; i++){			
			if(lines[i].length() == cols){
				rows++;
			}
			
			if(lines[i] == "-") break;
		}
		
		this.mapRows = rows;
		this.mapCols = cols;
	}
	
	private void setDirections(String[] lines){
		int i;
		for(i=0; i<lines.length; i++){			
			if(lines[i].equals("-")) break;
		}
		
		i++;
		
		for(; i<lines.length; i++){
			
			String[] split = lines[i].split(" ");
			directions.add(new Direction(split[0], Integer.parseInt(split[1])));
			
		}
	}
	
	private void setWallMatrix(String[] lines){
		walls = new int[mapRows][mapCols];	    
	    
	    for(int i=0; i<mapRows; i++){
	    	String line = lines[i];
	    	
	    	for(int j=0; j<line.length(); j++){
	    		char c = line.charAt(j);
	    		if(c == 'x'){
	    			walls[i][j] = 1;
	    		} else if(c == 'o'){
	    			startX = j;
	    			startY = i;
	    		}
	    	}
	    }
	}
	
	
	public Level(String fileName){

		directions = new ArrayList<Direction>();		
	
		FileHandle file = Gdx.files.internal(fileName);
		String text = file.readString();
		
		String[] lines = text.split("\\r?\\n");
		
		setDimensions(lines);
		setDirections(lines);
		setWallMatrix(lines);
		initialize();

	}
	
	public void initialize(){
		moving = false;
		shiftX = 0;
		shiftY = 0;
		currentDirection = 0;
		currentDirectionShift = 0;	
	}

	@Override
	public boolean touches(float leftUpperX, float leftUpperY, float width, float height) {
		int a = (int) leftUpperX;
		int b = (int) leftUpperY;
		int c = (int) (leftUpperX + width);
		int d = (int) (leftUpperY + height);
		
		for(int i=b; i<=d; i++){
			for(int j=a; j<=c; j++){
				if(walls[i][j] == 1){
					return true;
				}
			}
		}
	
		return false;
	}

}
