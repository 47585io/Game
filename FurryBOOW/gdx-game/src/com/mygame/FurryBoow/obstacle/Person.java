package com.mygame.FurryBoow.obstacle;

import com.badlogic.gdx.InputProcessor;
import com.mygame.FurryBoow.buff.Buff;
import com.mygame.FurryBoow.obstacle.Bomb.BombFactory;
import com.mygame.FurryBoow.array.EmptyArray;

/* 人物类，拥有更强大的属性，并可以感知触摸事件来投炸弹 */
public class Person extends Entity implements InputProcessor
{
	private int pointerId;
	private int xOriginal, yOriginal;
	private BombFactory mBombFactory;
	
	public Person(Scenes map){
		super(map);
	}

	public boolean keyDown(int p1)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean keyUp(int p1)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean keyTyped(char p1)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int id, int p4)
	{
		xOriginal = x;
		yOriginal = y;
		pointerId = id;
		return true;
	}

	@Override
	public boolean touchUp(int x, int y, int id, int p4)
	{
		if(id == pointerId)
		{
			Scenes scenes = getScenes();
			Attributes attr = getAttributes();
			double angle = rotationAngle(x, y);
			Bomb bomb = mBombFactory.makeBomb(scenes, attr.attack, EmptyArray.emptyArray(Buff.class));
			bomb.setDirection(angle);
			bomb.setSpeed(5);
			scenes.addObstacle(bomb);
		}
		return true;
	}

	@Override
	public boolean touchDragged(int x, int y, int id)
	{
		if(id == pointerId){
			double angle = rotationAngle(x, y);
			
		}
		return true;
	}

	@Override
	public boolean mouseMoved(int p1, int p2)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean scrolled(int p1)
	{
		// TODO: Implement this method
		return false;
	}
	
	private double rotationAngle(int x, int y)
	{
		double dx = x - xOriginal;
		double dy = y - yOriginal;
		double angle = Math.toDegrees(Math.atan2(dy, dx));
		return y >= 0 ? angle : 360 - angle;
	}
}
