package com.mygame.FurryBoow;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.mygame.FurryBoow.obstacle.*;
import com.mygame.FurryBoow.scenes.*;
import com.mygame.FurryBoow.scenes.utils.*;


public class MyGdxGame implements ApplicationListener
{
	ShapeRenderer mShapeRenderer;
	ObstacleRegionTree mRegionTree;
	
	RectShaper[] mRectShapers;
	RectShaper mBox;
	
	@Override
	public void create()
	{ 
		mBox = new RectShaper(null, 0,0,100,100);
		mRectShapers = new RectShaper[]{};
		mShapeRenderer = new ShapeRenderer();
		mRegionTree = new ObstacleRegionTree();
		
		for(int i = 0; i < mRectShapers.length; ++i){
			RectShaper rs = mRectShapers[i];
			//mRegionTree.addObstacle(rs, rs.left, rs.top, rs.right, rs.bottom);
		}
		mBox.shapeType = ShapeRenderer.ShapeType.Line;
	}

	@Override
	public void render()
	{
		if(Gdx.input.isTouched()){
			int dx = Gdx.input.getDeltaX();
			int dy = Gdx.input.getDeltaY();
			mBox.move(dx, dy);
		}
		/*
	    Obstacle[] objs = mRegionTree.getObstacles(mBox.left, mBox.right, mBox.top, mBox.bottom);
		for(int i = 0; i < objs.length; ++i){
			((RectShaper)objs[i]).shapeType = ShapeRenderer.ShapeType.Line;
		}*/
		mShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		for(int i = 0; i < mRectShapers.length; ++i){
			mRectShapers[i].draw(null, mShapeRenderer);
			mRectShapers[i].shapeType = ShapeRenderer.ShapeType.Filled;
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
		public RectShaper(Scenes map, int l, int t, int r, int b){
			super(map);
			left = l;
			top = t;
			right = r;
			bottom = b;
		}

		@Override
		public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer){
			//shapeRenderer.set(shapeType);
			//shapeRenderer.setColor(Color.YELLOW);
			shapeRenderer.rect(left, cast(top), right-left, bottom-top);
		}
		
		public void move(int dx, int dy){
			left += dx;
			right += dx;
			top += dy;
			bottom += dy;
		}
		
		int left, top, right, bottom;
		ShapeRenderer.ShapeType shapeType;
	}
	
	public static int cast(int y){
		return Gdx.graphics.getHeight() - y;
	}
}
