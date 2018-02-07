package com.mygdx.game;

import java.util.List;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

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

public class LevelMap implements IUpdatable, ICollisionable {
		
	int[][] walls;
	int startX;
	int startY;
	
	int mapRows, mapCols;
	
	float speed = 0.03f;
	
	float shiftX, shiftY;
	
	private boolean paused = false;
	
	private List<Object> gameObjects;
	
	private Transition transition;
	
	private String levelName;
	
	ArrayList<Direction> directions;
	int currentDirection = 0;
	float currentDirectionShift = 0;	
	
	@Override
	public void update(){
		changeDirection();
		move();

	}
	
	public Transition getTransition(){
		return transition;
	}
	
	private void readJSON(String jsonFileName){
		FileHandle jsonFile = Gdx.files.internal(jsonFileName);
		String jsonString = jsonFile.readString();		
		
		try {
			
			JSONObject map = new JSONObject(jsonString);
			
			levelName = (String) map.get("levelName");
			
			String transitionName = (String) map.get("transition");
			
			TransitionFactory tf = new TransitionFactory();
			
			transition = tf.getTransition(transitionName);
			
			// Read directions
			
			JSONArray directionsArray = map.getJSONArray("directions");
			
			for(int d=0; d<directionsArray.length(); d++){
				JSONObject dir = directionsArray.getJSONObject(d);
				String arrow = dir.getString("arrow");
				int steps = dir.getInt("steps");
				
				directions.add(new Direction(arrow, steps));
			}
			
			// Read matrix
			
			JSONArray matrixRows = map.getJSONArray("matrixRows");
			
			this.mapRows = matrixRows.length();
			this.mapCols = matrixRows.getString(0).length();
			walls = new int[mapRows][mapCols];			
		
			for(int i=0; i<matrixRows.length(); i++){
				String row = matrixRows.getString(i);
				for(int j=0; j<row.length(); j++){
		    		char c = row.charAt(j);
		    		if(c == 'x'){
		    			walls[i][j] = 1;
		    		} else if(c == 'o'){
		    			startX = j;
		    			startY = i;
		    		}
		    	}
			}
			
			
			// Read game objects
			
			JSONArray gameObjectsArray = map.getJSONArray("gameObjects");
			
			for(int o=0; o<gameObjectsArray.length(); o++){
				
				JSONObject gameObject = gameObjectsArray.getJSONObject(o);
				String objectClass = gameObject.getString("className");
				JSONObject params = gameObject.getJSONObject("params");
				
				Iterator<?> iterator = params.keys();
				
				Map<String, Object> paramsMap = new HashMap<String, Object>();

				while(iterator.hasNext()){					
					String key = (String) iterator.next();					
					paramsMap.put(key, params.get(key));
				}
				
				Class<?> clazz = Class.forName(objectClass);
				Constructor<?>[] constructors = clazz.getConstructors();
				Constructor<?> constructor = null;
				
				// Find annotated constructor
				
				for(Constructor<?> c : constructors){
					for(Annotation a : c.getAnnotations()){
						if(a.annotationType().equals(MapConstructor.class)){
							constructor = c;
							break;
						}
					}
					if(constructor == null) break;
				}
				
				
				Object generatedObj = constructor.newInstance(paramsMap);
				
				gameObjects.add(generatedObj);
			}
						
		} catch(Exception e){
			e.printStackTrace();
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
	
	public String getLevelName(){
		return levelName;
	}

	
	public LevelMap(String fileName){
		
		gameObjects = new ArrayList<Object>();
		directions = new ArrayList<Direction>();
		
		readJSON(fileName);

		initialize();

	}
	
	public List<Object> getGameObjects(){
		return gameObjects;
	}
	
	public void initialize(){
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
	
	@Override
	public void pause(boolean b){
		paused = b;
	}
	
	@Override
	public boolean isPaused(){
		return paused;
	}

}
