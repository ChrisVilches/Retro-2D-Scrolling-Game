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
		
	
	float shipWidth = 30;//60;
	float shipHeight = 20;//40;
	
	float cursorSize = 20;
	
	float tileSize = 50;
	
	Level level;
	
	ArrayList<IUpdatable> updatables;
	
	ShapeRenderer shapeRenderer;
	
	ArrayList<Weather> weathers;
	
	ArrayList<Obstacle> obstacles;
	
	Explosion explosion;
	
	Transition transition;


	@Override
	public void create() {
		shapes = new ShapeRenderer();
		shapeRenderer = new ShapeRenderer();
		batch = new SpriteBatch();
		shipTexture = new Texture("ship.png");		
		tile = new Texture("map-tile.jpg");
		cursor = Gdx.graphics.newCursor(new Pixmap(Gdx.files.internal("cursor.png")), 3, 3);
		Gdx.graphics.setCursor(cursor);

		level = new Level("level1.txt");
		
		ship = new Ship((level.startX * tileSize) - tileSize/2, Gdx.graphics.getHeight() - ((level.startY + 1) * tileSize) + tileSize/2);
		
		updatables = new ArrayList<IUpdatable>();
		weathers = new ArrayList<Weather>();
		obstacles = new ArrayList<Obstacle>();
		
		updatables.add(level);
		updatables.add(ship);
		
		weathers.add(new Snow(60, 700, 530, 5));
		
		obstacles.add(new MovingBlock(5, 3, 13, 3));
		obstacles.add(new MovingBlock(20, 18, 25, 18));
		
		obstacles.add(new MovingBlock(30, 27, 30, 28));
		obstacles.add(new MovingBlock(33, 28, 33, 27));
		
		obstacles.add(new MovingBlock(4, 4, 4, 5));
		
		obstacles.add(new Fan(6, 5, 4, 0.4f));
		obstacles.add(new Fan(9, 6, 2, -1.2f));
		obstacles.add(new Fan(11, 5, 3, 0.7f));

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
		
		for(int i=0; i<obstacles.size(); i++){
			if(obstacles.get(i).touches(x, y, width, height)){
				return true;
			}
		}
			
		return level.touches(x, y, width, height);
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
				
				s.setSize(o.getWidth() * tileSize, o.getHeight() * tileSize);
				s.setRotation(o.getAngle());
				s.setOrigin(s.getWidth()/2, s.getHeight()/2);			
				
				s.setPosition(
						(o.getX() + level.shiftX) * tileSize, 
						Gdx.graphics.getHeight() - (o.getY() + level.shiftY + 1) * tileSize
						);
				
				s.draw(batch);
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
		
		
		for(int i=0; i<weathers.size(); i++){
			weathers.get(i).render(batch);
			weathers.get(i).render(shapes);
		}
	    

	    batch.end();
	    Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
	    
	    
	    
	    
	    
	    
	    
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
	    }	    
	    
	    
	    shapes.end();
	    
	}
	
	@Override
	public void dispose () {
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
