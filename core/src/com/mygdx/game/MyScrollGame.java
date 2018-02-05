package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class MyScrollGame extends ApplicationAdapter {
	
	enum State {
		PLAYING,
		STOPPED,
		DYING
	};
	
	
	SpriteBatch batch;
	Texture shipTexture;
	Texture tile;	
	Cursor cursor;
	Ship ship;
	State state;
	
	float shipWidth = 60;
	float shipHeight = 40;
	
	float cursorSize = 20;
	
	float tileSize = 50;
	
	Level level;
	
	ArrayList<IUpdatable> updatables;
	
	ShapeRenderer shapeRenderer;
	
	ArrayList<Weather> weathers;
	
	ArrayList<Obstacle> obstacles;
	
	Explosion explosion;


	@Override
	public void create() {
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
		
		obstacles.add(new MovingBlock(2, 3, 8, 3));
		obstacles.add(new MovingBlock(20, 18, 25, 18));
		
		explosion = new Explosion();
		
		initialize();
		
	}
	
	public void initialize(){
		ship.initialize();
		level.initialize();
		explosion.initialize();
		state = State.STOPPED;		
	}
	
	
	private void update() {
		
		switch(state){
		case STOPPED:			
			if(Gdx.input.isTouched()){
				state = State.PLAYING;
				level.moving = true;
				ship.moving = true;
			}			
			break;	
			
		case PLAYING:
			
			for(int i=0; i<updatables.size(); i++){				
				updatables.get(i).update();
			}
			
			if(checkCollision()){
				Gdx.gl.glClearColor(1, 0, 0, 1);
				state = State.DYING;
			}
			
			break;
			
		case DYING:
			
			explosion.x = ship.x;
			explosion.y = ship.y;
			
			explosion.incrementElapsed(Gdx.graphics.getDeltaTime());
			
			if(explosion.animation.isAnimationFinished(explosion.elapsed)){
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
	

	@Override
	public void render () {
		update();			

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		
		int fromY = -(int) level.shiftY;		
		int fromX = -(int) level.shiftX;		
	
		for(int i=fromY; i<fromY+15 && i<level.mapRows; i++){
			for(int j=fromX; j<fromX+15 && j<level.mapCols; j++){
				if(level.walls[i][j] == 0) continue;
				batch.draw(tile, (j + level.shiftX) * tileSize, Gdx.graphics.getHeight()-(i + level.shiftY + 1) * tileSize, tileSize, tileSize);
			}
		}
		
		for(int i=0; i<obstacles.size(); i++){
			Obstacle o = obstacles.get(i);
			batch.draw(o.getTexture(), (o.getX() + level.shiftX) * tileSize, Gdx.graphics.getHeight()-(o.getY() + level.shiftY + 1) * tileSize, tileSize * o.getWidth(), tileSize * o.getHeight());
		}
		
		batch.draw(shipTexture, ship.x - (shipWidth/2), ship.y - (shipHeight/2), shipWidth, shipHeight);
		
		
		switch(state){
		case STOPPED:
			break;
		case PLAYING:
			break;
		case DYING:
			batch.draw(explosion.animation.getKeyFrame(explosion.elapsed), explosion.x - 60, explosion.y - 60, 120, 120);			
			break;
		}		
		
		
		for(int i=0; i<weathers.size(); i++){
			weathers.get(i).render(batch);
		}
		
		batch.end();
		
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
