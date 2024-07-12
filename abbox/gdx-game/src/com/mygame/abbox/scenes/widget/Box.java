package com.mygame.abbox.scenes.widget;

import android.graphics.Rect;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygame.abbox.obstacle.Obstacle;
import com.mygame.abbox.obstacle.shape.RectShape;
import com.mygame.abbox.scenes.buff.Buff;
import com.mygame.abbox.share.graphics.ColorShapeDrawable;

public class Box extends Obstacle
{
	private Buff mState;
	
	public Box(){
		this(0, 0, 0, 0, null);
	}
	public Box(int left, int top, int right, int bottom, Buff buff){
		mState = buff;
		setShape(new RectShape(left, top, right, bottom));
		setObstacleDrawable(new BoxDrawable(mState != null ? mState.buffColor() : Color.WHITE));
	}
	public RectShape getShape(){
		return (RectShape)super.getShape();
	}
	public Buff popBuff(){
		return mState;
	}

	/* 绘制方块的Drawable */
	public class BoxDrawable extends ColorShapeDrawable
	{
		public BoxDrawable(Color color){
			super(color);
		}
		public void onDraw(ShapeRenderer render){
			//绘制方块矩形
			Rect rect = getShape().getBounds();
			render.rect(rect.left, rect.top, rect.width(), rect.height());
		}
	}
}
