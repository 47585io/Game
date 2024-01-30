package com.mygame.FurryBoow.obstacle;
import android.graphics.*;

public interface ObstacleContainer
{
	public void addObstacle(Obstacle obj, int xStart, int xEnd, int yStart, int yEnd);
	
	public void removeObstacle(Obstacle obj);
	
	public Obstacle[] getObstacles(int xStart, int xEnd, int yStart, int yEnd);
	
	public boolean getObstacleBounds(Obstacle obj, Rect bounds);
	
	public int getObstacleCount();
	
	public boolean containerBounds(Rect bounds);
	
	public ObstacleContainer subContainer(int xStart, int xEnd, int yStart, int yEnd);
	
	public void clear();
	
	public void setFilters(ObstacleBoundsFilter[] filter);
	
	public ObstacleBoundsFilter[] getFilters();
	
	
	public static interface ObstacleBoundsFilter
	{
		public Obstacle filter(Obstacle obj, Rect bounds);
	}
	
	public static interface ObstacleContainerFactory
	{
		public ObstacleContainer newObstacleContainer();
	}
}
