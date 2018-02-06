package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

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
	
	Level level;
	
	ArrayList<IUpdatable> updatables;
	
	ShapeRenderer shapeRenderer;
	
	ArrayList<Weather> weathers;
	
	ArrayList<Obstacle> obstacles;
	
	ArrayList<ICollisionable> collisionables;
	
	Explosion explosion;
	
	Transition transition;


	@Override
	public void create() {
		shapes = new ShapeRenderer();
		shapeRenderer = new ShapeRenderer();
		batch = new SpriteBatch();
		shipTexture = new Texture("ship-red.png");		
		tile = new Texture("map-tile.jpg");
		//cursor = Gdx.graphics.newCursor(new Pixmap(Gdx.files.internal("cursor.png")), 3, 3);
		//Gdx.graphics.setCursor(cursor);

		level = new Level("level1.txt");
		
		ship = new Ship((level.startX * tileSize) - tileSize/2, Gdx.graphics.getHeight() - ((level.startY + 1) * tileSize) + tileSize/2);
		
		updatables = new ArrayList<IUpdatable>();
		weathers = new ArrayList<Weather>();
		obstacles = new ArrayList<Obstacle>();
		collisionables = new ArrayList<ICollisionable>();
		
		updatables.add(level);
		updatables.add(ship);
		
		weathers.add(new Snow(60, 700, 530, 5));
		
		obstacles.add(new MovingBlock(5, 3, 6, 3));
		obstacles.add(new MovingBlock(20, 18, 25, 18));
		
		obstacles.add(new MovingBlock(30, 27, 30, 28));
		obstacles.add(new MovingBlock(33, 28, 33, 27));
		obstacles.add(new MovingBlock(34, 27, 34, 28));
		obstacles.add(new MovingBlock(35, 28, 35, 27));
		obstacles.add(new MovingBlock(36, 27, 36, 28));
		obstacles.add(new MovingBlock(37, 28, 37, 27));
		
		obstacles.add(new MovingBlock(4, 4, 4, 5));
		
		/*obstacles.add(new Fan(6, 5, 6, 0.4f));
		obstacles.add(new Fan(9, 6, 2, -1.2f));
		obstacles.add(new Fan(20, 19, 3, 0.7f));*/
		
		obstacles.add(new Fan2(5, 5, 4, 0.3f, 0.4f));
		obstacles.add(new Fan2(8, 8, 2, 0.6f, -1.2f));
		obstacles.add(new Fan2(13, 3, 5f, 0.2f, -1.2f));
		obstacles.add(new Fan2(14, 5, 5, 0.2f, -0.9f));
		obstacles.add(new Fan2(19, 5, 5, 0.2f, -0.9f));
		obstacles.add(new Fan2(5, 19, 3, 0.1f, 0.7f));
		
		obstacles.add(new Fan2(15, 18, 3, 0.1f, 0.7f));
		obstacles.add(new Fan2(17, 18, 4, 0.1f, -0.9f));
		
		
		collisionables.add(level);
		
		for(Obstacle o : obstacles){
			collisionables.add(o);
		}

		explosion = new Explosion();
		
		transition = new CircleTransition();
		
		initialize();
		state = State.STOPPED;
		
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
						
			if(Gdx.input.justTouched()){				
				if(Util.pointInsideRectangle(ship.x - (shipWidth/2), Gdx.graphics.getHeight() - ship.y - (shipHeight/2), shipWidth, shipHeight, mx, my)){
					state = State.PLAYING;
					level.moving = true;
					ship.moving = true;
				}				
			}			
			break;	
			
		case PLAYING:
			
			for(int i=0; i<updatables.size(); i++){				
				updatables.get(i).update();
			}
			
			if(checkCollision()){
				state = State.DYING;
				explosion.x = ship.x;
				explosion.y = ship.y;
			}
			
			break;
			
		case DYING:

			explosion.incrementElapsed(Gdx.graphics.getDeltaTime());
			
			if(explosion.animation.isAnimationFinished(explosion.elapsed)){
				transition.initialize();
				state = State.FADEOUT;
			}
			
			break;
			
		case FADEOUT:
			
			if(transition.fadeOut()){
				initialize();
				state = State.FADEIN;
			}
			break;
			
		case FADEIN:
			
			if(transition.fadeIn()){
				transition.initialize();
				initialize();
				state = State.STOPPED;
			}			
			break;
			
		}
		
	
		for(int i=0; i<obstacles.size(); i++){
			obstacles.get(i).update();
		}
		
		for(int i=0; i<weathers.size(); i++){
			weathers.get(i).update();
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
		    transition.render(shapes);
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
		
		/****************** DEBUG ****************************/
		
	    shapes.begin(ShapeRenderer.ShapeType.Line);
	    shapes.setColor(1f, 0f, 0f, 1f);
	    float t = tileSize;
	    Fan fan = null;
	    for(Obstacle o : obstacles){
	    	if(o instanceof Fan){
	    		fan = (Fan)o;
	    		for(int i=0; i<4; i++){	    	    	
	    	    	float[] v = fan.getPropellers()[i].getTransformedVertices();	    	    	
	    	    	shapes.line(v[0] * t, Gdx.graphics.getHeight() - v[1] * t, v[2] * t, Gdx.graphics.getHeight() - v[3] * t);
	    	    	shapes.line(v[2] * t, Gdx.graphics.getHeight() - v[3] * t, v[4] * t, Gdx.graphics.getHeight() - v[5] * t);
	    	    	shapes.line(v[4] * t, Gdx.graphics.getHeight() - v[5] * t, v[6] * t, Gdx.graphics.getHeight() - v[7] * t);
	    	    	shapes.line(v[6] * t, Gdx.graphics.getHeight() - v[7] * t, v[0] * t, Gdx.graphics.getHeight() - v[1] * t);
	    	    }
	    	}
	    	
	    	if(o instanceof Fan2){
	    		
	    		Fan2 fan2 = (Fan2)o;
	    		for(int i=0; i<4; i++){	    	    	
	    	    	float[] v = fan2.getPropellers()[i].getTransformedVertices();
	    	    	
	    	    	for(int x=0; x<4; x++){	    	    		
	    	    		shapes.line(
	    	    				(v[x*2] + level.shiftX) * t, 
	    	    				Gdx.graphics.getHeight() - (v[(x*2)+1] + level.shiftY) * t, 
	    	    				(v[((x*2) + 2)%8] + level.shiftX) * t, 
	    	    				Gdx.graphics.getHeight() - (v[((x*2) + 3)%8] + level.shiftY) * t
	    	    				);
	    	    	}	    	    	
	    		}
	    	}
	    }  

	    shapes.end();
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		tile.dispose();
		shipTexture.dispose();
		cursor.dispose();
		
		for(int i=0; i<weathers.size(); i++){
			weathers.get(i).dispose();
		}
		
		for(int i=0; i<obstacles.size(); i++){
			obstacles.get(i).dispose();
		}
	}
}
