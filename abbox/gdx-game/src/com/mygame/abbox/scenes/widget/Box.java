package com.mygame.abbox.scenes.widget;

import android.graphics.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.mygame.abbox.obstacle.*;
import com.mygame.abbox.obstacle.shape.*;
import com.mygame.abbox.scenes.buff.*;
import java.util.*;

public class Box extends Obstacle
{
	private Buff mState;
	private static final Random mRand = new Random();
	
	public Box(){
		this(0, 0, 0, 0);
	}
	public Box(int left, int top, int right, int bottom){
		setShape(new RectShape(left, top, right, bottom));
		//mState = randBuff();
	}
	public RectShape getShape(){
		return (RectShape)super.getShape();
	}
	public Buff popBuff(){
		return mState;
	}

	public void draw(ShapeRenderer render){
		//render.setColor(mState.buffColor());
		Rect rect = getShape().getBounds();
		render.rect(rect.left, rect.top, rect.width(), rect.height());
	}
	
	private static Buff randBuff()
	{
		int id = mRand.nextInt() % 2;
		Buff buff = null;
		switch(id){
			case 1:
				buff = new HealthyBuff(50);
				break;
		}
		return buff;
	}
}

