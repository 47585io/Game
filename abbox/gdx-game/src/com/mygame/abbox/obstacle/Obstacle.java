package com.mygame.abbox.obstacle;

import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.mygame.abbox.obstacle.shape.*;

public class Obstacle extends InputListener
{
	protected Shape mShape;
	ObstacleGroup mObstacleGroup;
	InputListener mInputListener;
	protected int mDuration;
	
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
	
	public void setInputListener(InputListener listener){
		mInputListener = listener;
	}
	public InputListener getInputListener(){
		return mInputListener;
	}
	public void setInputDuration(int duration){
		mDuration = duration;
	}
	public int getInputDuration(){
		return mDuration;
	}
	
	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
	{
		if(mInputListener != null && mDuration <= 0){
			return mInputListener.touchDown(event, x, y, pointer, button);
		}
		return false;
	}
	
	public void touchDragged(InputEvent event, float x, float y, int pointer)
	{
		if(mInputListener != null && mDuration <= 0){
			mInputListener.touchDragged(event, x, y, pointer);
		}
	}

	public void touchUp(InputEvent event, float x, float y, int pointer, int button)
	{
		if(mInputListener != null && mDuration <= 0){
			mInputListener.touchUp(event, x, y, pointer, button);
		}
	}

	public boolean keyDown(InputEvent event, int keycode)
	{
		if(mInputListener != null && mDuration <= 0){
			return mInputListener.keyDown(event, keycode);
		}
		return false;
	}

	public boolean keyUp(InputEvent event, int keycode)
	{
		if(mInputListener != null && mDuration <= 0){
			return mInputListener.keyUp(event, keycode);
		}
		return false;
	}

	public boolean keyTyped(InputEvent event, char character)
	{
		if(mInputListener != null && mDuration <= 0){
			return mInputListener.keyTyped(event, character);
		}
		return false;
	}
	
	public void draw(ShapeRenderer render){}
	
	public void onCollision(Obstacle other){}
}
