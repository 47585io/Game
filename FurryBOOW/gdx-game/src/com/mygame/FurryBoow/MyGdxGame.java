package com.mygame.FurryBoow;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.mygame.FurryBoow.obstacle.*;


public class MyGdxGame implements ApplicationListener
{
	ShapeRenderer mShapeRenderer;
	ObstacleRangeTree mRangeTree;
	
	RectShaper[] mRectShapers;
	RectShaper mBox;
	
	@Override
	public void create()
	{
		mBox = new RectShaper();
		mRectShapers = new RectShaper[]{};
		
		int length = mRectShapers.length;
		Obstacle[] objs = new Obstacle[length];
		int[] xStarts = new int[length];
		int[] xEnds = new int[length];
		int[] yStarts = new int[length];
		int[] yEnds = new int[length];
		for(int i = 0; i < length; ++i){
			RectShaper rect = mRectShapers[i];
			objs[i] = rect;
			xStarts[i] = rect.left;
			xEnds[i] = rect.right;
			yStarts[i] = rect.top;
			yEnds[i] = rect.bottom;
		}
		
		mShapeRenderer = new ShapeRenderer();
		mRangeTree = new ObstacleRangeTree(objs, xStarts, xEnds, yStarts, yEnds);
	}

	@Override
	public void render()
	{
		if(Gdx.input.isTouched()){
			int x = Gdx.input.getX();
			int y = Gdx.input.getY();
		}
		
	    Obstacle[] objs = mRangeTree.getObstacles(mBox.left, mBox.right, mBox.top, mBox.bottom);
		for(int i = 0; i < objs.length; ++i){
			((RectShaper)objs[i]).shapeType = ShapeRenderer.ShapeType.Line;
		}
		mShapeRenderer.begin();
		for(int i = 0; i < mRectShapers.length; ++i){
			mRectShapers[i].draw(null, mShapeRenderer);
		}
		mShapeRenderer.end();
	}

	@Override
	public void dispose()
	{
	}

	@Override
	public void resize(int width, int height)
	{
	}

	@Override
	public void pause()
	{
	}

	@Override
	public void resume()
	{
	}
	
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
			shapeRenderer.set(shapeType);
			shapeRenderer.setColor(Color.YELLOW);
			shapeRenderer.rect(left, top, right-left, bottom-top);
			shapeType = ShapeRenderer.ShapeType.Filled;
		}
		
		int left, top, right, bottom;
		ShapeRenderer.ShapeType shapeType;
	}
}
