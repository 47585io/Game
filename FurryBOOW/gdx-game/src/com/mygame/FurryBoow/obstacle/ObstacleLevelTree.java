package com.mygame.FurryBoow.obstacle;
import android.graphics.*;

/* 按矩形物体的嵌套层次构建的树 */
public class ObstacleLevelTree implements ObstacleContainer
{
	public ObstacleLevelTree(ObstacleContainerFactory fa){
		mRoot = new TreeNode();
		mFactory = fa;
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
	
	private TreeNode mRoot;
	private ObstacleContainerFactory mFactory;
	
	private static class TreeNode
	{
		int mChildCount;
		TreeNode[] mChildren;
		ObstacleContainer container;
		
		private void addChild(){
			
		}
		private void removeChild(){
			// if container size == 0， then remove node
		}
		
		private boolean in(Obstacle obj){
			// if obj in node, then addChild(new Node(obj));
			return false;
		}
		private boolean op(Obstacle obj){
			// if obj op node， then container.add(obj)
			return false;
		}
		// if obj out node， then new Node() -> oldNode + new Node()
	}
}
