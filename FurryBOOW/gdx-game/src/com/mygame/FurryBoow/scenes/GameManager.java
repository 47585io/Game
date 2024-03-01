package com.mygame.FurryBoow.scenes;
import java.util.*;
import android.graphics.*;
import com.mygame.FurryBoow.obstacle.*;

/* 管理游戏逻辑 */
public class GameManager
{
	private Scenes mScenes;
	
	public GameManager(Scenes map){
		mScenes = map;
	}
	
	public void initCube(int wStart, int wEnd, int hStart, int hEnd, int spacing, Obstacle[] objs)
	{/*
		mStaticObstacles.clear();
		mBounds.set(left, top, right, bottom);
		mCubeSize.set(wStart, wEnd, hStart, hEnd, spacing);
		Rect rect = new Rect();
		for(int i = 0; i < objs.length; ++i){
			nextRandCube(rect);
			if(!rect.isEmpty())
			    mStaticObstacles.addObstacle(objs[i], rect.left, rect.top, rect.right, rect.bottom);
		}*/
	}
	private void nextRandCube(Rect rect)
	{/*
		Obstacle[] objs;
		int tryCount = 0;
		int spacing = mCubeSize.spacing;
		do{
		    rect.left = rand(mBounds.left, mBounds.right);
		    rect.top = rand(mBounds.top, mBounds.bottom);
		    rect.right = rect.left + rand(mCubeSize.wStart, mCubeSize.wEnd);
		    rect.bottom = rect.top + rand(mCubeSize.hStart, mCubeSize.hEnd);
		    objs = mStaticObstacles.getObstacles(rect.left-spacing, rect.top-spacing, rect.right+spacing, rect.bottom+spacing);
			tryCount++;
		}
		while(tryCount < MAX_COUNT && objs.length > 0);
		if(objs.length > 0){
			rect.setEmpty();
		}*/
	} 
	private static int rand(int a, int b){
		return mRand.nextInt(b - a + 1) + a;
	}
	
	private static class CubeSize
	{
		private int wStart, wEnd;
		private int hStart, hEnd;
		private int spacing;

		public CubeSize(){}
		public CubeSize(int ws, int we, int hs, int he, int sp){
			set(ws, we, hs, he, sp);
		}
		public void set(int ws, int we, int hs, int he, int sp){
			wStart = ws;
			wEnd = we;
			hStart = hs;
			hEnd = he;
			spacing = sp;
		}
	}
	
	private Rect mBounds;
	private Rect mCubeBounds;
	private CubeSize mCubeSize;
	
	private static final int MAX_COUNT = 10;
	private static final Random mRand = new Random();
	
}
