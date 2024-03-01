package com.mygame.FurryBoow.scenes;

import android.graphics.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.mygame.FurryBoow.array.*;
import com.mygame.FurryBoow.obstacle.*;
import com.mygame.FurryBoow.scenes.utils.*;
import java.util.*;
import com.badlogic.gdx.input.*;

public class BaseScenes extends Scenes
{
	public BaseScenes(){
		mStaticObstacles = new ObstacleRegionTree();
		mGameManager = new GameManager(this);
	}
	public GameManager getGameManager(){
		return mGameManager;
	}

	//向场景中添加一个物体
	public void addObstacle(Obstacle obj)
	{
		if(obj instanceof DynamicObject){
			mDynamicObstacles.next = new Link<Obstacle>(obj, mDynamicObstacles);
		}else{
			Rect rect = obj.getRect();
			mStaticObstacles.addObstacle(obj, rect.left, rect.top, rect.right, rect.bottom);
		}
	}
	//从场景中移除一个物体
	public void removeObstacle(Obstacle obj)
	{
		if(obj instanceof DynamicObject){
			Link.remove(mDynamicObstacles, obj);
		}else{
			mStaticObstacles.removeObstacle(obj);
		}
	}
	//重新调整物休在场景中的位置
	protected void requestLayout(Obstacle obj)
	{
		if(!(obj instanceof DynamicObject)){
			//静态物体位置改变，需要重新插入到树中
			addObstacle(obj);
		}
	}
	
	/* 更新场景中所有动态物体的位置 */
	public void update()
	{
		Link<Obstacle> link;
		for(link = mDynamicObstacles; link != null; link = link.next){
			((DynamicObject)link.obj).update();
		}
		checkCollision();
	}
	
	/* 动态物体在移动后，是否与其它物体碰撞 */
	private void checkCollision()
	{
		Link<Obstacle> link;
		for(link = mDynamicObstacles; link != null; link = link.next)
		{
			Rect rect = link.obj.getRect();
			Obstacle[] objs = mStaticObstacles.getObstacles(rect.left, rect.top, rect.right, rect.bottom);
			for(int i = 0; i < objs.length; ++i){
				if(Obstacle.checkRegionCollision(link.obj, objs[i])){
					link.obj.onCollision(objs[i]);
				}
			}
			Link<Obstacle> next;
			for(next = link.next; next != null; next = next.next){
				if(Obstacle.checkCollision(link.obj, next.obj)){
					link.obj.onCollision(next.obj);
				}
			}
		}
	}
	
	/* 快速获取指定范围内的物体 */
	public Obstacle[] getObstacles(int left, int top, int right, int bottom)
	{
		List<Obstacle> list = new ArrayList<>();
		//先获取范围内的静态物体
		Obstacle[] sObjs = mStaticObstacles.getObstacles(left, top, right, bottom);
		//再获取范围内的动态物体
		Link<Obstacle> link = mDynamicObstacles;
		for(;link != null; link = link.next){
			Rect rect = link.obj.getRect();
			if(rect.intersects(left, top, right, bottom)){
				list.add(link.obj);
			}
		}
		//合并所有在范围内的物体
		if(sObjs.length + list.size() == 0){
			return EmptyArray.emptyArray(Obstacle.class);
		}
		Obstacle[] retObjs = new Obstacle[sObjs.length + list.size()];
		list.toArray(retObjs);
		System.arraycopy(sObjs, 0, retObjs, list.size(), sObjs.length);
		return retObjs;
	}

	private ObstacleRegionTree mStaticObstacles; //位置静态固定且不常增删的物体集合
	private Link<Obstacle> mDynamicObstacles;    //位置会移动并且还经常增删的物体集合
	private GameManager mGameManager;
	
	private static final class Link<T>
	{
		public T obj;
		public Link<T> next;
		
		public Link(T o, Link<T> n){
			obj = o;
			next = n;
		}
		public static<T> Link<T> remove(Link<T> link, T obj)
		{
			if(link == null){
				return null;
			}
			if(link.obj == obj){
				return link.next;
			}
			Link<T> copy = link;
			for(;copy.next != null; copy = copy.next){
				if(copy.next.obj == obj){
					copy.next = copy.next.next;
					break;
				}
			}
			return link;
		}
	}
}
