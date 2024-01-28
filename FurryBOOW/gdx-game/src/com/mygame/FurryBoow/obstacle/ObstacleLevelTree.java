package com.mygame.FurryBoow.obstacle;
import android.graphics.*;

/* 按矩形物体的嵌套 */
public class ObstacleLevelTree implements ObstacleContainer
{
	public ObstacleLevelTree(){
		mRoot = new Node();
		mFactory = ObstacleContainerFactory.getInstance();
	}
	public void setContainerFactory(ObstacleContainerFactory factory){
		mFactory = factory;
	}
	
	@Override
	public void addObstacle(Obstacle obj, int xStart, int xEnd, int yStart, int yEnd)
	{
		// TODO: Implement this method
	}

	@Override
	public void removeObstacle(Obstacle obj)
	{
		// TODO: Implement this method
	}

	@Override
	public Obstacle[] getObstacles(int xStart, int xEnd, int yStart, int yEnd)
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public boolean getObstacleRect(Obstacle obj, Rect rect)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public int getObstacleCount(){
		return mRoot.mChildCount;
	}
	
	private Node mRoot;
	private ObstacleContainerFactory mFactory;
	
	private static class Node
	{
		int mChildCount;
		Node[] mChildren;
		ObstacleContainer container;
	}
}
