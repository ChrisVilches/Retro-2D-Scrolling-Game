package com.mygdx.game;

import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Snow extends Weather {
	
	private float[] x;
	private float[] y;
	private float[] speedX;
	private float[] speedY;
	private int quantity;
	private float rangeWidth, rangeHeight;
	private Random rand;
	private float strength;
	private Texture snowflakeTexture;
	private Sprite snowflakeSprite;
	
	public Snow(int quantity, float rangeWidthAbsolute, float rangeHeightAbsolute, float strength){
		
		snowflakeTexture = new Texture("snowflake.png");
		snowflakeSprite = new Sprite(snowflakeTexture);
		
		snowflakeSprite.setColor(1, 1, 1, 0.4f);
		snowflakeSprite.setScale(0.6f);
		
		this.strength = strength;
		
		if(this.strength < 1){
			this.strength = 1;
		}
		
		if(this.strength > 10){
			this.strength = 10;
		}
		
		this.rangeWidth = rangeWidthAbsolute;
		this.rangeHeight = rangeHeightAbsolute;
		this.quantity = quantity;
		x = new float[quantity];
		y = new float[quantity];
		speedX = new float[quantity];
		speedY = new float[quantity];
		
		rand = new Random();
		
		for(int i=0; i<quantity; i++){
			resetSnowFlake(i);
		}
		
	}

	@Override
	public void update(){		
		for(int i=0; i<quantity; i++){
			x[i] += speedX[i] * strength;
			y[i] += speedY[i] * strength;
			
			if(x[i] < 0 || x[i] > rangeWidth){
				resetSnowFlake(i, true);
			} else if(y[i] < 0 || y[i] > rangeHeight){
				resetSnowFlake(i, true);
			}
		}		
	}
	
	private void resetSnowFlake(int i, boolean spawnAtBorder){
		
		if(spawnAtBorder){
			x[i] = rangeWidth;
		} else {
			x[i] = rand.nextFloat() * rangeWidth;
		}

		y[i] = rand.nextFloat() * rangeHeight;		
		speedX[i] = -rand.nextFloat() * 0.5f;
		speedY[i] = (rand.nextFloat() - 0.5f) * 0.5f;
	}
	
	private void resetSnowFlake(int i){
		resetSnowFlake(i, false);
	}
	
	@Override
	public void render(SpriteBatch batch){

		for(int i=0; i<quantity; i++){
			
			// This can be improved... (it's stupid)
			snowflakeSprite.setCenterX(x[i]);
			snowflakeSprite.setCenterY(y[i]);
			snowflakeSprite.draw(batch);
	    }	    
	}

	@Override
	public void dispose(){
		snowflakeTexture.dispose();
	}

}
