package com.mygame.FurryBoow.obstacle;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.*;
import android.graphics.*;

public class Obstacle 
{
	private Scenes mScenes;
	private int left, top, right, bottom;
	
	public Obstacle(Scenes map){
		mScenes = map;
		//mScenes.addObstacle(this);
	}
	public Scenes getScenes(){
		return mScenes;
	}

	public void onCollision(Obstacle other){
		
	}
	public void layout(int left, int top, int right, int bottom)
	{
		//mScenes.update();
	}
	public void getRect(Rect rect){
		rect.left = left;
		rect.top = top;
		rect.right = right;
		rect.bottom = bottom;
	}
	public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer)
	{
		SpriteCache b;
		//现在，我想知道是否可以缓存背景。
		//因为它保持不变，所以不需要每次都计算它。
		//我通过 Google 搜索找到了 SpriteCache
		Sprite s;
		//现在，只绘制部分内容，可以使用精灵图
		//PixelMap是保存在内存中的点数据，而Texture则是在GPU层的纹理
		//且Texture可用PixelMap创建
	}
	public void dispose(){
		//mScenes.removeObstacle(this);
	}
	
	public boolean onTouchEvent(){
		//Gdx.gl是GPU层的API，很可怕
		return false;
	}
	public boolean onKeyEvent(){
		return false;
	}
	
}
