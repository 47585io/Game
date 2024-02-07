package com.mygame.FurryBoow.obstacle;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.*;
import android.graphics.*;
import com.mygame.FurryBoow.scenes.*;

public class Obstacle 
{
	private int mPrivateFlags;
	private Scenes mScenes; //物体所在的场景
	private Rect mBounds;   //物体的外包矩形
	private Region mRegion; //物体的区域
	private int mOffsetx, mOffsety; //物体的位置相较于原来的位置偏移
	
	public Obstacle(Scenes map){
		mScenes = map;
	}
	public Scenes getScenes(){
		return mScenes;
	}
	
	/* 物体的外包矩形，用于快速判断 */
	public void getRect(Rect rect){
		rect.set(mBounds);
	}
	/* 物体的图形区域，用于精确判断 */
	public Region getRegion(){
		return mRegion;
	}
	/* 物体既可以绘制图形，也可以绘制图片，
	 * 但其本身都有一个区域Region用于判断碰撞
	 */
	public void onCollision(Obstacle other){}
	
	public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer){}
	
	public void dispose(){}
	
	public boolean onTouchEvent(){
		//Gdx.gl是GPU层的API，很可怕
		return false;
	}
	public boolean onKeyEvent(){
		return false;
	}
	
	SpriteCache b;
	//现在，我想知道是否可以缓存背景。
	//因为它保持不变，所以不需要每次都计算它。
	//我通过 Google 搜索找到了 SpriteCache
	Sprite s;
	//现在，只绘制部分内容，可以使用精灵图
	//PixelMap是保存在内存中的点数据，而Texture则是在GPU层的纹理
	//且Texture可用PixelMap创建
}
