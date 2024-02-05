package com.mygame.FurryBoow.scenes;

import com.mygame.FurryBoow.obstacle.*;
import com.mygame.FurryBoow.scenes.utils.ObstacleRegionTree;

public class Scenes
{
	public Scenes(){
		
	}
	
	public Obstacle[] getObstacles(int left, int top, int right, int bottom){
		return null;
	}
	
	private int mObstacleCount;
	private ObstacleRegionTree mStaticTree;
	private Obstacle[] mCharacters;
	private Link<Obstacle> mDynamicObstacles;
	
	private static class Link<T>{
		public T obj;
		public Link<T> next;
	}
}
