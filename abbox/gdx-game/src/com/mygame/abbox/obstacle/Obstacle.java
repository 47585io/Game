package com.mygame.abbox.obstacle;

import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygame.abbox.obstacle.shape.Shape;
import com.mygame.abbox.share.graphics.ShapeDrawable;

public class Obstacle extends InputListener
{
	private Shape mShape;
	private ShapeDrawable mShapeDrawable;
	private int mInputDuration;
	private InputListener mInputListener;
	ObstacleGroup mObstacleGroup;
	
	public ObstacleGroup getMyGroup(){
		return mObstacleGroup;
	}
	public void requestFocus(){
		if(mObstacleGroup != null){
			mObstacleGroup.mFocusObstacle = this;
		}
	}
	
	protected void setShape(Shape shape){
		mShape = shape;
	}
	public Shape getShape(){
		return mShape;
	}
	public void setObstacleDrawable(ShapeDrawable drawable){
		mShapeDrawable = drawable;
	}
	public ShapeDrawable getObstacleDrawable(){
		return mShapeDrawable;
	}
	
	public void setInputDuration(int duration){
		mInputDuration = duration;
	}
	public int getInputDuration(){
		return mInputDuration;
	}
	public void setInputListener(InputListener listener){
		mInputListener = listener;
	}
	public InputListener getInputListener(){
		return mInputListener;
	}
	
	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
	{
		if(mInputListener != null){
			return mInputListener.touchDown(event, x, y, pointer, button);
		}
		return false;
	}

	public void touchDragged(InputEvent event, float x, float y, int pointer)
	{
		if(mInputListener != null){
			mInputListener.touchDragged(event, x, y, pointer);
		}
	}

	public void touchUp(InputEvent event, float x, float y, int pointer, int button)
	{
		if(mInputListener != null){
			mInputListener.touchUp(event, x, y, pointer, button);
		}
	}

	public boolean keyDown(InputEvent event, int keycode)
	{
		if(mInputListener != null){
			return mInputListener.keyDown(event, keycode);
		}
		return false;
	}

	public boolean keyUp(InputEvent event, int keycode)
	{
		if(mInputListener != null){
			return mInputListener.keyUp(event, keycode);
		}
		return false;
	}

	public boolean keyTyped(InputEvent event, char character)
	{
		if(mInputListener != null){
			return mInputListener.keyTyped(event, character);
		}
		return false;
	}
	
	public void draw(ShapeRenderer render)
	{
		if(mShapeDrawable != null){
			mShapeDrawable.draw(render);
		}
	}
	
	public void onCollision(Obstacle other){}
}
