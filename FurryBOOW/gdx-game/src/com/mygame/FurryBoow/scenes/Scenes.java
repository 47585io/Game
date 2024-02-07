package com.mygame.FurryBoow.scenes;

import com.mygame.FurryBoow.obstacle.*;
import com.mygame.FurryBoow.scenes.utils.ObstacleRegionTree;
import android.graphics.Rect;
import java.util.*;
import com.mygame.FurryBoow.array.*;
import com.mygame.FurryBoow.scenes.utils.*;
import com.mygame.FurryBoow.scenes.utils.ObstacleContainer.ObstacleBoundsFilter;

public class Scenes
{
	public Scenes(){
		mBounds = new Rect();
		mCubeSize = new CubeSize();
		mStaticObstacles = new ObstacleRegionTree();
		mStaticObstacles.setFilters(new ObstacleBoundsFilter[]{new StaticBounds()});
	}
	
	public void initCube(int left, int top, int right, int bottom,
	                      int wStart, int wEnd, int hStart, int hEnd, int spacing, Obstacle[] objs)
	{
		mStaticObstacles.clear();
		mBounds.set(left, top, right, bottom);
		mCubeSize.set(wStart, wEnd, hStart, hEnd, spacing);
		Rect rect = new Rect();
		for(int i = 0; i < objs.length; ++i){
			nextRandCube(rect);
			mStaticObstacles.addObstacle(objs[i], rect.left, rect.top, rect.right, rect.bottom);
		}
	}
	private void nextRandCube(Rect rect)
	{
		Obstacle[] objs;
		final int spacing = mCubeSize.spacing;
		do{
		    rect.left = rand(mBounds.left, mBounds.right);
		    rect.top = rand(mBounds.top, mBounds.bottom);
		    rect.right = rect.left + rand(mCubeSize.wStart, mCubeSize.wEnd);
		    rect.bottom = rect.top + rand(mCubeSize.hStart, mCubeSize.hEnd);
		    objs = mStaticObstacles.getObstacles(rect.left-spacing, rect.top-spacing, rect.right+spacing, rect.bottom+spacing);
		}
		while(objs.length > 0);
	} 
	private static int rand(int a, int b){
		return mRand.nextInt(b - a + 1) + a;
	}
	
	public ObstacleContainer getContainer(){
		return mStaticObstacles;
	}
	public void add(int f){}
	public void remove(){}
	
	
	public void draw(){
		
	}
	public void update()
	{
		Obstacle a = null;
		Obstacle b = null;
		a.getRegion().quickReject(b.getRegion());
	}
	
	/* 先快速获取指定范围内的物体，使用物体的外包矩形快递判断，之后可以再精确判断获取到的物体 */
	public Obstacle[] getObstacles(int left, int top, int right, int bottom)
	{
		Rect rect = new Rect();
		List<Obstacle> list = new ArrayList<>();
		//先获取范围内的静态物体
		Obstacle[] sObjs = mStaticObstacles.getObstacles(left, top, right, bottom);
		//再快速判断移动的物体
		for(int i = 0; i < mMoveObstacles.length; ++i){
			mMoveObstacles[i].getRect(rect);
			if(rect.intersects(left, top, right, bottom)){
				list.add(mMoveObstacles[i]);
			}
		}
		//最后快速判断动态的物体
		Link<Obstacle> link = mDynamicObstacles;
		for(;link != null; link = link.next){
			link.obj.getRect(rect);
			if(rect.intersects(left, top, right, bottom)){
				list.add(link.obj);
			}
		}
		//合并所有在范围内的物体
		if(sObjs.length + list.size() == 0){
			return EmptyArray.emptyArray(Obstacle.class);
		}
		Obstacle[] retObjs = new Obstacle[sObjs.length + list.size()];
		if(list.size() != 0){
			list.toArray(retObjs);
		}
		if(sObjs.length != 0){
			System.arraycopy(sObjs, 0, retObjs, list.size(), sObjs.length);
		}
		return retObjs;
	}
	
	private int mObstacleCount;
	private ObstacleRegionTree mStaticObstacles; //位置静态固定且不常增删的物体集合
	private Obstacle[] mMoveObstacles;           //不会增删但会移动的物体集合
	private Link<Obstacle> mDynamicObstacles;    //位置会移动并且还经常增删的物体集合
	
	private Rect mBounds;
	private CubeSize mCubeSize;
	
	public static final int StaticObstacles = 0;
	public static final int DynamicObstacles = 1;
	private static final Random mRand = new Random();
	
	private static class Link<T>{
		public T obj;
		public Link<T> next;
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
	
	private class StaticBounds implements ObstacleBoundsFilter
	{
		public void filter(Obstacle obj, Rect bounds){
			bounds.intersect(mBounds);
		}
	}
}
