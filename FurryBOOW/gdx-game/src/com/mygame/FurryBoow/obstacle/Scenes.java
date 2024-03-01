package com.mygame.FurryBoow.obstacle;

import com.badlogic.gdx.InputProcessor;
import android.graphics.Rect;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.Gdx;

public abstract class Scenes implements InputProcessor
{
	public static final int Touch_Bits = 31;
	
	public static final int Key_Bits = 30;
	
	//向场景中添加一个物体
	public abstract void addObstacle(Obstacle obj)
	
	//从场景中移除一个物体
	public abstract void removeObstacle(Obstacle obj)
	
	//重新调整物休在场景中的位置，仅添加至场景的物体才能调用
	protected abstract void requestLayout(Obstacle obj)
	
	//快速获取指定范围内的物体，这些物体的外包矩形与此范围重叠
	public abstract Obstacle[] getObstacles(int left, int top, int right, int bottom)
	
	//更新场景中所有物体的位置 
	public abstract void update()
	
	/* 在屏幕上绘制场景可见区域的内容 */
	public void draw(Batch batch)
	{
		Matrix4 matrix = batch.getTransformMatrix();
		matrix.translate(-xScroll, -yScroll, 0);
		Obstacle[] objs = getObstacles(xScroll, yScroll, xScroll + mDisplayWidth, yScroll + mDisplayHeight);
		for(int i = 0; i < objs.length; ++i){
			objs[i].draw(batch);
		}
		matrix.translate(xScroll, yScroll, 0);
	}

	/* 滚动场景的画布 */
	public void scrollTo(int sx, int sy){
		xScroll = calcScroll(sx, mContentWidth, mDisplayWidth);
		yScroll = calcScroll(sy, mContentHeight, mDisplayHeight);
	}
	public void scrollBy(int x, int y){
		scrollTo(xScroll + x, yScroll + y);
	}
	//计算滚动值，不允许滑出范围外
	private int calcScroll(int scroll, int content, int display){
		//如果显示宽度大于内容宽度，则只能滚动到0
		//如果滚动到的位置小于内容左边(0)，则只能滚动到内容左边(0)
		//如果要滚动到的位置加上显示宽度后，超出了内容的右边，则只能滚动到(content - display)
		return (display >= content || scroll <= 0) ? 0 :
		       (scroll + display > content ? content - display : scroll);
	}
	
	/* 设置场景的缩放 */
	public void setScale(float scale){
		xScale = yScale = scale;
	}

	/* 处理触摸事件 */
	public boolean touchDown(int x, int y, int id, int b)
	{
		Obstacle[] objs = getObstacles(x, y, x, y);
		for(int i = 0; i < objs.length; ++i)
		{
			if(objs[i] instanceof InputProcessor && ((objs[i].mPrivateFlags >> Touch_Bits) & 1) == 0){
				InputProcessor input = (InputProcessor) objs[i];
				if(input.touchDown(x, y, id, b)){
					mTouchTarget = TouchTarget.addPointer(mTouchTarget, input, id);
					return true;
				}
			}
		}
		return true;
	}
	public boolean touchUp(int x, int y, int id, int p4)
	{
		InputProcessor input = TouchTarget.findInputById(mTouchTarget, id);
		if(input != null){
			input.touchUp(x, y, id, p4);
			TouchTarget.removePointer(mTouchTarget, id);
		}
		return false;
	}
	public boolean touchDragged(int x, int y, int id)
	{
		InputProcessor input = TouchTarget.findInputById(mTouchTarget, id);
		if(input != null){
			if(input.touchDragged(x, y, id)){
				return true;
			}
			TouchTarget.removePointer(mTouchTarget, id);
		}
		return false;
	}

	/* 处理键事件 */
	public boolean keyDown(int p1)
	{
		if(mFocusObstacle != null){
			return mFocusObstacle.keyDown(p1);
		}
		return false;
	}
	public boolean keyUp(int p1)
	{
		if(mFocusObstacle != null){
			return mFocusObstacle.keyUp(p1);
		}
		return false;
	}
	public boolean keyTyped(char p1)
	{
		if(mFocusObstacle != null){
			return mFocusObstacle.keyTyped(p1);
		}
		return false;
	}

	/* 鼠标和滚轮，手机上不会触发 */
	public boolean mouseMoved(int p1, int p2){
		return false;
	}
	public boolean scrolled(int p1){
		return false;
	}
	
	private int mContentWidth, mContentHeight;
	private int mDisplayWidth, mDisplayHeight;
	private int xScroll, yScroll;
	private float xScale, yScale;
	
	private TouchTarget mTouchTarget;
	private InputProcessor mFocusObstacle;
	

	private static final class TouchTarget
	{
		private InputProcessor input;  //被触摸的Obstacle
		private int pointerIdBits;     //目标捕获的所有指针的 ID 的组合位掩码，用不同的位表示一个指针id
		private TouchTarget next;      //链表的下一个target

		public TouchTarget(InputProcessor input, int id, TouchTarget next){
			this.input = input;
			this.pointerIdBits = id;
			this.next = next;
		}
		public static TouchTarget addPointer(final TouchTarget touchTarget, InputProcessor input, int id)
		{
			TouchTarget target = touchTarget;
			for(;target != null; target = target.next){
				if(target.input == input){
					target.pointerIdBits |= 1 << id;
					return touchTarget;
				}
			}
			return new TouchTarget(input, id, touchTarget);
		}
		public static TouchTarget removePointer(final TouchTarget touchTarget, int id)
		{
			TouchTarget target = touchTarget;
			if(((target.pointerIdBits >> id) & 1) == 1)
			{
				target.pointerIdBits &= ~(1 << id);
				if(target.pointerIdBits == 0){
					target = target.next;
				}
				return target;
			}
			for(;target.next != null; target = target.next)
			{
				if(((target.next.pointerIdBits >> id) & 1) == 1){
					target.next.pointerIdBits &= ~(1 << id);
					if(target.next.pointerIdBits == 0){
						target.next = target.next.next;
					}
					break;
				}
			}
			return touchTarget;
		}
		public static InputProcessor findInputById(TouchTarget target, int id)
		{
			for(;target != null; target = target.next){
				if(((target.pointerIdBits >> id) & 1) == 1){
					return target.input;
				}
			}
			return null;
		}
	}
}
