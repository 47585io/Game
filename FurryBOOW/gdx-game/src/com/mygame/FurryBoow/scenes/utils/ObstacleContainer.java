package com.mygame.FurryBoow.scenes.utils;

import android.graphics.Rect;
import com.mygame.FurryBoow.obstacle.Obstacle;

public interface ObstacleContainer
{
	public void addObstacle(Obstacle obj, int left, int top, int right, int bottom);

	public void removeObstacle(Obstacle obj);

	public Obstacle[] getObstacles(int left, int top, int right, int bottom);

	public boolean getObstacleBounds(Obstacle obj, Rect bounds);

	public int getObstacleCount();

	public void containerBounds(Rect bounds);

	public ObstacleContainer subContainer(int left, int top, int right, int bottom);

	public void clear();

	public void setFilters(ObstacleBoundsFilter[] filter);

	public ObstacleBoundsFilter[] getFilters();


	public static interface ObstacleBoundsFilter
	{
		public void filter(Obstacle obj, Rect bounds);
	}

	public static interface ObstacleContainerFactory
	{
		public ObstacleContainer newObstacleContainer();
	}
}
