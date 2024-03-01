package com.mygame.FurryBoow.obstacle;

import android.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;

/* 所有物体的基类，声明了物体的区域，并可以绘制出来 */
public class Obstacle 
{
	Scenes mScenes;    //物体所在的场景
	int mPrivateFlags; //物体在场景的信息
	
	private Rect mBounds;   //物体的外包矩形(用于快速判断碰撞)
	private Path mPath;     //物体的轮廓(用于修改和变换图形)
	private Region mRegion; //物体的区域(用于判断碰撞)
	
	public Obstacle(Scenes map){
		mScenes = map;
	}
	public Scenes getScenes(){
		return mScenes;
	}
	public int getFlags(){
		return mPrivateFlags;
	}
	
	public Rect getRect(){
		return mBounds;
	}
	public Path getPath(){
		return mPath;
	}
	public Region getRegion(){
		return mRegion;
	}
	
	/* 物体可以进行任意绘制
	 * 但其本身都有一个区域Region用于判断碰撞
	 */
	public void onCollision(Obstacle other){}
	
	public void draw(Batch batch){}
	
	public void setFlags(int flags){
		mPrivateFlags = flags;
	}
	/* 为物体设置一个轮廓，并同时设置区域和外包矩形 
	 * 且不论你单独修改了物体的Path，Region，使用此方法将它们重新同步
	 */
	public void setPath(Path path)
	{
		if(path == null){
			throw new NullPointerException("Obstacle Region is null");
		}
		mPath = path;
		if(mRegion == null){
			mRegion = new Region();
		}
		mRegion.setPath(path, null);
		if(mBounds == null){
			mBounds = new Rect();
		}
		mRegion.getBounds(mBounds);
		requestLayout();
	}
	/* 将物体移动一段距离 */
	public void move(int dx, int dy)
	{
		if(mRegion == null){
			throw new NullPointerException("Obstacle Region is null");
		}
		mBounds.offset(dx, dy);
		mRegion.translate(dx, dy);
		requestLayout();
	}
	/* 将物体绕中点旋转一定角度 */
	public void rotate(float angle)
	{
		if(mRegion == null){
			throw new NullPointerException("Obstacle Region is null");
		}
		Matrix matrix = new Matrix();
		float px = mBounds.centerX();
		float py = mBounds.centerY();

		matrix.setRotate(angle, px, py);
		mPath.transform(matrix);
		mRegion.getBounds(mBounds);
		requestLayout();
	}
	/* 物体在场景中的位置变化了，需要更新它的信息 */
	public void requestLayout(){
		if(mScenes != null)
		    mScenes.requestLayout(this);
	}
	
	/* 判断两个物体是否发生碰撞 */
	public static boolean checkCollision(Obstacle o1, Obstacle o2)
	{
		//首先判断两个物体的外包矩形是否重叠
		if(Rect.intersects(o1.mBounds, o2.mBounds)){
			//再判断它们的区域是否重叠，重叠则返回true
			if(o1.mRegion.quickReject(o2.mRegion)){
				return true;
			}
		}
		return false;
	}
	
	/* 判断外包矩形是否重叠 */
    public static boolean checkBoundsCollision(Obstacle o1, Obstacle o2) {
        return Rect.intersects(o1.mBounds, o2.mBounds);
    }
    /* 判断具体区域是否重叠 */
    public static boolean checkRegionCollision(Obstacle o1, Obstacle o2) {
        return o1.mRegion.quickReject(o2.mRegion);
    }
}
