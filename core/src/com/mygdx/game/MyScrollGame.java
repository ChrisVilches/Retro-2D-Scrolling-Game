package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.Disposable;

public class MyScrollGame extends ApplicationAdapter {
	
	enum State {
		PLAYING,
		STOPPED,
		DYING,
		FADEOUT,
		FADEIN
	};	
	
	SpriteBatch batch;
	Texture shipTexture;
	Texture tile;	
	Cursor cursor;
	Ship ship;
	State state;
	ShapeRenderer shapes;
		
	
	float shipWidth = 30 * 1.5f;
	float shipHeight = 20 * 1.5f;
	
	float cursorSize = 20;
	
	float tileSize = 50;
	
	LevelMap level;
	
	List<IUpdatable> updatables;
	
	ShapeRenderer shapeRenderer;
	
	List<Weather> weathers;
	
	List<Obstacle> obstacles;
	
	List<ICollisionable> collisionables;
	
	Explosion explosion;

	List<IDebuggable> debuggables;
	
	List<Disposable> disposables;


	@Override
	public void create() {
		shapes = new ShapeRenderer();
		shapeRenderer = new ShapeRenderer();
		batch = new SpriteBatch();
		shipTexture = new Texture("ship-red.png");		
		tile = new Texture("map-tile.jpg");
		cursor = Gdx.graphics.newCursor(new Pixmap(Gdx.files.internal("cursor.png")), 3, 3);
		//Gdx.graphics.setCursor(cursor);

		level = new LevelMap("level1.json");
		
		ship = new Ship((level.startX * tileSize) - tileSize/2, Gdx.graphics.getHeight() - ((level.startY + 1) * tileSize) + tileSize/2);
		
		explosion = new Explosion();
		
		updatables = new ArrayList<IUpdatable>();
		weathers = new ArrayList<Weather>();
		obstacles = new ArrayList<Obstacle>();
		collisionables = new ArrayList<ICollisionable>();		
		disposables = new ArrayList<Disposable>();		
		debuggables = new ArrayList<IDebuggable>();	

		
		addToAppropiateList(level);
		addToAppropiateList(ship);
		addToAppropiateList(new Snow(60, 700, 530, 5));
		
		for(Object o : level.getGameObjects()){			
			addToAppropiateList(o);
		}		
		
		initialize();
		state = State.STOPPED;
		
	}
	
	private void addToAppropiateList(Object o){
		if(o instanceof IDebuggable){
			debuggables.add((IDebuggable) o);
		} 
		if(o instanceof Disposable){
			disposables.add((Disposable) o);
		} 
		if(o instanceof ICollisionable){
			collisionables.add((ICollisionable) o);
		} 
		if(o instanceof IUpdatable){
			updatables.add((IUpdatable) o);
		} 
		if(o instanceof Obstacle){
			obstacles.add((Obstacle) o);
		} 
		if(o instanceof Weather){
			weathers.add((Weather) o);
		}
	}
	
	public void initialize(){
		ship.initialize();
		level.initialize();
		explosion.initialize();		
	}
	
	
	private void update() {
		
		switch(state){
		case STOPPED:
			
			float mx = Gdx.input.getX();
			float my = Gdx.input.getY();
			
			level.pause(true);
			ship.pause(true);
						
			if(Gdx.input.justTouched()){				
				if(Util.pointInsideRectangle(ship.x - (shipWidth/2), Gdx.graphics.getHeight() - ship.y - (shipHeight/2), shipWidth, shipHeight, mx, my)){
					state = State.PLAYING;
				}				
			}			
			break;	
			
		case PLAYING:
			
			level.pause(false);
			ship.pause(false);

			if(checkCollision()){
				state = State.DYING;
				explosion.x = ship.x;
				explosion.y = ship.y;
			}
			
			break;
			
		case DYING:
			
			level.pause(true);
			ship.pause(true);

			explosion.incrementElapsed(Gdx.graphics.getDeltaTime());
			
			if(explosion.animation.isAnimationFinished(explosion.elapsed)){
				level.getTransition().initialize();
				state = State.FADEOUT;
			}
			
			break;
			
		case FADEOUT:
			
			level.pause(true);
			ship.pause(true);
			
			if(level.getTransition().fadeOut()){
				initialize();
				state = State.FADEIN;
			}
			break;
			
		case FADEIN:
			
			level.pause(true);
			ship.pause(true);
			
			if(level.getTransition().fadeIn()){
				level.getTransition().initialize();
				initialize();
				state = State.STOPPED;
			}			
			break;
			
		}
		
	
		for(int i=0; i<updatables.size(); i++){			
			IUpdatable u = updatables.get(i);			
			if(u.isPaused()) continue;
			u.update();
		}	
		
	}
	
	private boolean checkCollision(){
		
		// Suboptimal, most of these calculations are constant (no need to do them every frame)
		
		float x = (ship.x / tileSize) - level.shiftX;
		float y = ((Gdx.graphics.getHeight()-ship.y) / tileSize) - level.shiftY;
		
		x -= (shipWidth / 2) / tileSize;
		y -= (shipHeight / 2) / tileSize;
		
		float collisionBoxRemovalWidth = (shipWidth * (1-ship.collisionPorcentageWidth)) / tileSize;
		float collisionBoxRemovalHeight = (shipHeight * (1-ship.collisionPorcentageHeight)) / tileSize;
		
		x += collisionBoxRemovalWidth;
		y += collisionBoxRemovalHeight;
		
		float width = (shipWidth/tileSize) - (collisionBoxRemovalWidth*2);
		float height = (shipHeight/tileSize) - (collisionBoxRemovalHeight*2);
		
		for(int i=0; i<collisionables.size(); i++){
			if(collisionables.get(i).touches(x, y, width, height)){
				return true;
			}
		}
			
		return false;
	}
	
	
	private void renderTiles(){
		int fromY = -(int) level.shiftY;		
		int fromX = -(int) level.shiftX;		
	
		for(int i=fromY; i<fromY+15 && i<level.mapRows; i++){
			for(int j=fromX; j<fromX+15 && j<level.mapCols; j++){
				if(level.walls[i][j] == 0) continue;
				batch.draw(tile, (j + level.shiftX) * tileSize, Gdx.graphics.getHeight()-(i + level.shiftY + 1) * tileSize, tileSize, tileSize);
			}
		}
	}
	
	
	private void renderObstacles(){
		for(int i=0; i<obstacles.size(); i++){
			Obstacle o = obstacles.get(i);
			
			Sprite[] sprites = o.getSprites();
			
			for(int j=0; j<sprites.length; j++){
				
				Sprite s = sprites[j];
				
				float x = s.getX();
				float y = s.getY();	
				
				s.setScale(tileSize);
				s.setPosition(
						(s.getX() + level.shiftX) * tileSize, 
						Gdx.graphics.getHeight() - (s.getY() + level.shiftY + 1) * tileSize
						);
				
				s.draw(batch);
				s.setPosition(x, y);
				
			}
		}
	}

	
	private void renderFadeMasking(){
		switch(state){
		case FADEIN:
		case FADEOUT:
			
			Gdx.gl.glClearDepthf(1.0f);
		    Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
		    Gdx.gl.glColorMask(false, false, false, false);
		    Gdx.gl.glDepthFunc(GL20.GL_LESS);
		    Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		    Gdx.gl.glDepthMask(true);
		    
		    shapes.begin(ShapeRenderer.ShapeType.Filled);
		    shapes.setColor(1f, 1f, 1f, 0.5f);
		    level.getTransition().render(shapes);
		    shapes.end();

		    Gdx.gl.glColorMask(true, true, true, true);
		    Gdx.gl.glDepthMask(true);
		    Gdx.gl.glDepthFunc(GL20.GL_EQUAL);
			
			break;
		default:
			break;
		}	
	}
	
	private void renderWeather(){
		for(int i=0; i<weathers.size(); i++){
			weathers.get(i).render(batch);
			weathers.get(i).render(shapes);
		}
	}

	
	@Override
	public void render () {
		
		update();
		
		renderFadeMasking();
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	    
	    batch.begin();	       
    
	    renderTiles();

		renderObstacles();
		
		switch(state){
		
		case FADEIN:
		case STOPPED:
		case PLAYING:
			
			batch.draw(shipTexture, ship.x - (shipWidth/2), ship.y - (shipHeight/2), shipWidth, shipHeight);
			break;
		
		case DYING:
			batch.draw(explosion.animation.getKeyFrame(explosion.elapsed), explosion.x - 60, explosion.y - 60, 120, 120);			
			break;
		default:
			break;
		}		
		
		renderWeather();	    

	    batch.end();
	    Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
	    
	    
	    
	    
	    //debug();
	    

	}
	
	public void debug(){		
	
	    shapes.begin(ShapeRenderer.ShapeType.Line);
	    
	    float t = tileSize;
	    
	    for(IDebuggable d : debuggables){
	    	
	    	shapes.setColor(1f, 0, 0, 1f);
	    	
	    	Polygon[] polygons = d.getDebugPolygons();
	    	
	    	for(int p=0; p<polygons.length; p++){	  
	    		
	    		float[] vertices = polygons[p].getTransformedVertices();
	    		
	    		for(int v=0; v<vertices.length/2; v++){	    			
	    			
	    			float x1 = vertices[v*2];
	    			float y1 = vertices[(v*2)+1];
	    			float x2 = vertices[((v*2) + 2) % vertices.length];
	    			float y2 = vertices[((v*2) + 3) % vertices.length];
	    			
	    			shapes.line(
		    				(x1 + level.shiftX) * t, Gdx.graphics.getHeight() - (y1 + level.shiftY) * t, 
		    				(x2 + level.shiftX) * t, Gdx.graphics.getHeight() - (y2 + level.shiftY) * t
		    				);    			

	    		}	    		
	    	}
	    }    

	    shapes.end();
	}
	
	@Override
	public void dispose() {
		
		for(int i=0; i<disposables.size(); i++){
			disposables.get(i).dispose();
		}

	}
}
