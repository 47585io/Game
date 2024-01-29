package com.mygame.FurryBoow.obstacle;
import android.graphics.*;

public interface ObstacleContainer
{
	public void addObstacle(Obstacle obj, int xStart, int xEnd, int yStart, int yEnd);
	
	public void removeObstacle(Obstacle obj);
	
	public Obstacle[] getObstacles(int xStart, int xEnd, int yStart, int yEnd);
	
	public boolean getObstacleRect(Obstacle obj, Rect rect);
	
	public int getObstacleCount();
	
	public boolean containerRect(Rect rect);
	
	public ObstacleContainer subContainer(int xStart, int xEnd, int yStart, int yEnd);
	
	public void clear();
	
	public void setFilters(ObstacleRectFilter[] filter);
	
	
	public static interface ObstacleRectFilter
	{
		public Obstacle filter(Obstacle obj, Rect rect);
	}
	
	public static interface ObstacleContainerFactory
	{
		public ObstacleContainer newObstacleContainer();
	}
}
