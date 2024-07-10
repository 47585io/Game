package com.mygame.abbox.scenes;
import com.badlogic.gdx.*;
import com.mygame.abbox.obstacle.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.scenes.scene2d.*;
import java.util.*;
import com.mygame.abbox.scenes.widget.*;

public class Scenes extends InputAdapter
{
	public Scenes(int width, int height, int displayw, int displayh)
	{
		mObstacleGroup = new ObstacleGroup(width, height, displayw, displayh);
		mShapeRenderer = new ShapeRenderer();
		mGameManger = new GameManger(this);
		mInputEvent = new InputEvent();
	}
	
	public void init(){
		mGameManger.init();
		mObstacleGroup.setCollisionCallback(mGameManger);
	}
	public ObstacleGroup getObstacleGroup(){
		return mObstacleGroup;
	}
	public ShapeRenderer getShapeRender(){
		return mShapeRenderer;
	}
	
	public void draw(){
		mShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		mObstacleGroup.draw(mShapeRenderer);
		mShapeRenderer.end();
	}
	public void update(){
		mObstacleGroup.update();
	}

	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		setTouchEvent(screenX, screenY, pointer, button, InputEvent.Type.touchDown);
		int scrollX = mObstacleGroup.getScrollX();
		int scrollY = mObstacleGroup.getScrollY();
		return mObstacleGroup.touchDown(mInputEvent, screenX + scrollX, screenY + scrollY, pointer, button);
	}

	public boolean touchDragged(int screenX, int screenY, int pointer)
	{
		setTouchEvent(screenX, screenY, pointer, 0, InputEvent.Type.touchDragged);
		int scrollX = mObstacleGroup.getScrollX();
		int scrollY = mObstacleGroup.getScrollY();
		mObstacleGroup.touchDragged(mInputEvent, screenX + scrollX, screenY + scrollY, pointer);
		return true;
	}

	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		setTouchEvent(screenX, screenY, pointer, button, InputEvent.Type.touchUp);
		int scrollX = mObstacleGroup.getScrollX();
		int scrollY = mObstacleGroup.getScrollY();
		mObstacleGroup.touchUp(mInputEvent, screenX + scrollX, screenY + scrollY, pointer, button);
		return true;
	}
	
	private void setTouchEvent(int screenX, int screenY, int pointer, int button, InputEvent.Type type)
	{
		mInputEvent.reset();
		mInputEvent.setStageX(screenX);
		mInputEvent.setStageY(screenY);
		mInputEvent.setPointer(pointer);
		mInputEvent.setButton(button);
		mInputEvent.setType(type);
	}
	
	private ArrayList<Box> mBoxes;
	private ArrayList<Person> mPersons;
	
	private ShapeRenderer mShapeRenderer;
	private ObstacleGroup mObstacleGroup;
	private GameManger mGameManger;
	
	private InputEvent mInputEvent;
}
