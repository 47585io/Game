package com.mygame.abbox.obstacle;

import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygame.abbox.obstacle.shape.Shape;

public class Obstacle extends InputListener
{
	private Shape mShape;
	private int mInputDuration;
	ObstacleGroup mObstacleGroup;
	
	protected void setShape(Shape shape){
		mShape = shape;
	}
	public Shape getShape(){
		return mShape;
	}
	
	public ObstacleGroup getMyGroup(){
		return mObstacleGroup;
	}
	public void requestFocus(){
		if(mObstacleGroup != null){
			mObstacleGroup.mFocusObstacle = this;
		}
	}
	
	public void setInputDuration(int duration){
		mInputDuration = duration;
	}
	public int getInputDuration(){
		return mInputDuration;
	}
	
	public void draw(ShapeRenderer render){}
	
	public void onCollision(Obstacle other){}
}
