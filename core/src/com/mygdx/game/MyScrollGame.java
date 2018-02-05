package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MyScrollGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture shipTexture;
	Texture tile;
	Cursor cursor;
	Ship ship;
	
	float shipWidth = 80;
	float shipHeight = 60;
	
	float cursorSize = 20;
	
	float tileSize = 50;
	
	Level level;
	
	ArrayList<IUpdatable> updatables;
	

	@Override
	public void create () {
		batch = new SpriteBatch();
		shipTexture = new Texture("ship.png");
		tile = new Texture("map-tile.jpg");
		cursor = Gdx.graphics.newCursor(new Pixmap(Gdx.files.internal("cursor.png")), 3, 3);
		Gdx.graphics.setCursor(cursor);

		level = new Level("level1.txt");
		
		ship = new Ship((level.startX * tileSize) - tileSize/2, Gdx.graphics.getHeight() - ((level.startY + 1) * tileSize) + tileSize/2);
		
		updatables = new ArrayList<IUpdatable>();
		
		updatables.add(level);
		updatables.add(ship);
		
	}
	
	private void update() {	
		
		if(Gdx.input.justTouched()) {
			//level.moving = true;
			ship.moving = true;
		}
		
		
		for(int i=0; i<updatables.size(); i++){
			updatables.get(i).update();
		}
	}
	
	private boolean checkCollision(){
		
		float x = (ship.x / tileSize) - level.shiftX;
		float y = ((Gdx.graphics.getHeight()-ship.y) / tileSize) - level.shiftY;
		
		x -= (shipWidth / 2) / tileSize;
		y -= (shipHeight / 2) / tileSize;
		
		float collisionBoxRemovalWidth = (shipWidth * (1-ship.collisionPorcentageWidth)) / tileSize;
		float collisionBoxRemovalHeight = (shipHeight * (1-ship.collisionPorcentageHeight)) / tileSize;
			
		return level.collision(x + collisionBoxRemovalWidth, 
				y + collisionBoxRemovalHeight, 
				(shipWidth/tileSize) - (collisionBoxRemovalWidth*2), 
				(shipHeight/tileSize) - (collisionBoxRemovalHeight*2));
	}
	

	@Override
	public void render () {
		update();
		
		if(checkCollision()){
			Gdx.gl.glClearColor(1, 0, 0, 1);
		} else {
			Gdx.gl.glClearColor(0, 0, 0, 1);
		}

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(shipTexture, ship.x - (shipWidth/2), ship.y - (shipHeight/2), shipWidth, shipHeight);
		
		for(int i=0; i<level.mapRows; i++){
			for(int j=0; j<level.mapCols; j++){
				if(level.walls[i][j] == 0) continue;
				batch.draw(tile, (j + level.shiftX) * tileSize, Gdx.graphics.getHeight()-(i + level.shiftY + 1) * tileSize, tileSize, tileSize);
			}
		}		

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		tile.dispose();
		shipTexture.dispose();
		cursor.dispose();
	}
}
