package com.mygame.FurryBoow;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.mygame.FurryBoow.obstacle.*;
import com.mygame.FurryBoow.scenes.*;
import com.mygame.FurryBoow.scenes.utils.*;
import android.graphics.Rect;


public class MyGdxGame implements ApplicationListener
{
	ShapeRenderer mShapeRenderer;
	ObstacleRegionTree mRegionTree;
	
	RectShaper[] mRectShapers;
	RectShaper mBox;
	
	@Override
	public void create()
	{ 
		mBox = new RectShaper(0, 0, 50, 50);
		mRectShapers = new RectShaper[]{
			new RectShaper(100, 100),
			new RectShaper(100, 200),
			new RectShaper(100, 300),
			new RectShaper(100, 400),
			new RectShaper(100, 500),
			new RectShaper(100, 600),
			new RectShaper(100, 700),
			new RectShaper(100, 800),
			new RectShaper(100, 900),
			new RectShaper(100, 1000),
			new RectShaper(100, 1100),
			new RectShaper(100, 1200),
			new RectShaper(100, 1300),
			new RectShaper(100, 1400),
			new RectShaper(100, 1500),
			new RectShaper(100, 1600),
			new RectShaper(100, 1700),
			new RectShaper(100, 1800),
			new RectShaper(100, 1900),
			new RectShaper(100, 2000),
			new RectShaper(100, 100),
			new RectShaper(100, 200),
			new RectShaper(100, 300),
			new RectShaper(100, 400),
			new RectShaper(100, 500),
			new RectShaper(100, 600),
			new RectShaper(100, 700),
			new RectShaper(100, 800),
			new RectShaper(100, 900),
			new RectShaper(100, 1000),
			new RectShaper(100, 1100),
			new RectShaper(100, 1200),
			new RectShaper(100, 1300),
			new RectShaper(100, 1400),
			new RectShaper(100, 1500),
			new RectShaper(100, 1600),
			new RectShaper(100, 1700),
			new RectShaper(100, 1800),
			new RectShaper(100, 1900),
			new RectShaper(100, 2000),
			new RectShaper(100, 100),
			new RectShaper(100, 200),
			new RectShaper(100, 300),
			new RectShaper(100, 400),
			new RectShaper(100, 500),
			new RectShaper(100, 600),
			new RectShaper(100, 700),
			new RectShaper(100, 800),
			new RectShaper(100, 900),
			new RectShaper(100, 1000),
			new RectShaper(100, 1100),
			new RectShaper(100, 1200),
			new RectShaper(100, 1300),
			new RectShaper(100, 1400),
			new RectShaper(100, 1500),
			new RectShaper(100, 1600),
			new RectShaper(100, 1700),
			new RectShaper(100, 1800),
			new RectShaper(100, 1900),
			new RectShaper(100, 2000),
			new RectShaper(100, 100),
			new RectShaper(100, 200),
			new RectShaper(100, 300),
			new RectShaper(100, 400),
			new RectShaper(100, 500),
			new RectShaper(100, 600),
			new RectShaper(100, 700),
			new RectShaper(100, 800),
			new RectShaper(100, 900),
			new RectShaper(100, 1000),
			new RectShaper(100, 1100),
			new RectShaper(100, 1200),
			new RectShaper(100, 1300),
			new RectShaper(100, 1400),
			new RectShaper(100, 1500),
			new RectShaper(100, 1600),
			new RectShaper(100, 1700),
			new RectShaper(100, 1800),
			new RectShaper(100, 1900),
			new RectShaper(100, 2000),
		};
		mShapeRenderer = new ShapeRenderer();
		
		
		Scenes map = new Scenes();
		map.initCube(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 
		             50, 100, 50, 100, 50, mRectShapers);
		mRegionTree = (ObstacleRegionTree) map.getContainer();
		
		Rect rect = new Rect();
		for(int i = 0; i < mRectShapers.length; ++i){
			RectShaper rs = mRectShapers[i];
			mRegionTree.getObstacleBounds(rs, rect);
			rs.set(rect);
		}
		
		/*
		mRegionTree = new ObstacleRegionTree();

		for(int i = 0; i < mRectShapers.length; ++i){
			RectShaper rs = mRectShapers[i];
			mRegionTree.addObstacle(rs, rs.x, rs.y, rs.x+rs.width, rs.y+rs.height);
		}*/
		
		mBox.color = Color.BLUE;
	}

	@Override
	public void render()
	{
		if(Gdx.input.isTouched()){
			int dx = Gdx.input.getDeltaX();
			int dy = Gdx.input.getDeltaY();
			mBox.move(dx, dy);
		}
		
	    Obstacle[] objs = mRegionTree.getObstacles(mBox.x, mBox.y, mBox.x+mBox.width, mBox.y+mBox.height);
		for(int i = 0; i < objs.length; ++i){
			((RectShaper)objs[i]).color = Color.GREEN;
		}
		mShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		for(int i = 0; i < mRectShapers.length; ++i){
			mRectShapers[i].draw(null, mShapeRenderer);
			mRectShapers[i].color = Color.RED;
		}
		mBox.draw(null, mShapeRenderer);
		mShapeRenderer.end();
	}

	@Override
	public void dispose(){
		mShapeRenderer.dispose();
	}

	@Override
	public void resize(int width, int height){}

	@Override
	public void pause(){}

	@Override
	public void resume(){}
	
	static class RectShaper extends Obstacle
	{
		public RectShaper(){
			super(null);
		}
		public RectShaper(int x, int y){
			super(null);
			this.x = x;
			this.y = y;
			width = 100;
			height = 100;
			color = Color.RED;
		}
		public RectShaper(int x, int y, int w, int h){
			super(null);
			this.x = x;
			this.y = y;
			width = w;
			height = h;
			color = Color.RED;
		}
		
		@Override
		public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer){
			shapeRenderer.setColor(color);
			shapeRenderer.rect(x, cast(y+height), width, height);
		}
		public void set(Rect rect){
			set(rect.left, rect.top, rect.right-rect.left, rect.bottom-rect.top);
		}
		public void set(int x, int y, int w, int h){
			this.x = x;
			this.y = y;
			width = w;
			height = h;
		}
		public void move(int dx, int dy){
			x += dx;
			y += dy;
		}
		
		Color color;
		int x, y, width, height;
	}
	
	public static final int cast(int y){
		return Gdx.graphics.getHeight() - y;
	}
}
