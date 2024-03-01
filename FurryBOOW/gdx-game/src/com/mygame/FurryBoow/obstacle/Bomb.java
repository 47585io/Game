package com.mygame.FurryBoow.obstacle;

import android.graphics.Rect;
import com.mygame.FurryBoow.buff.Buff;
import com.mygame.FurryBoow.scenes.utils.ObstacleContainer;
import com.mygame.FurryBoow.array.EmptyArray;

/* 炸弹类，对碰撞到的生命体造成伤害 */
public class Bomb extends Obstacle implements DynamicObject
{
	private int mDamage;        //炸弹的伤害
	private Buff[] mStates;     //炸弹附带的Buff
	
	private double mSpeed;      //炸弹移速(每次移动距离)
	private double mDirection;  //炸弹方向(旋转角度)
	private double unitDx, unitDy; //向着当前方向移动1个长度需要分别在x轴和y轴上移动的距离
	
	public Bomb(Scenes map){
		this(map, 0, EmptyArray.emptyArray(Buff.class));
	}
	public Bomb(Scenes map, int damage, Buff[] states){
		super(map);
		mDamage = damage;
		mStates = states;
	}
	
	public void setDirection(double degrees)
	{
		mDirection = degrees;
		//通俗地理解将角度转换为弧度
		//180º = πrad， 不妨将180º看作1个单位角度，它对应的单位弧度是π
		//对于任意角度angle，先看它有几个单位角度(angle / 180)，然后再乘以π
		double radians = degrees / 180 * Math.PI; 
		//sin和cos分别就是从原点向指定旋转角度移动1个单位长度需要分别在x轴和y轴上移动的距离
		unitDy = Math.sin(radians);
		unitDx = Math.cos(radians);
	}
	public void setSpeed(double speed){
		mSpeed = speed;
	}
	public double getDirection(){
		return mDirection;
	}
	public double getSpeed(){
		return mSpeed;
	}
	
	/* 向着指定方向飞行一段距离 */
	public void update(){
		int dx = (int)(mSpeed * unitDx);
		int dy = (int)(mSpeed * unitDy);
		move(dx, dy);
	}
	public void onCollision(Obstacle other)
	{
		if(other instanceof Entity){
			Entity entity = (Entity) other;
			entity.sendDamage(mDamage);
			entity.sendBuff(mStates);
		}
		else{
			other.onCollision(this);
		}
	}
	
	public static class BombFactory
	{
		public Bomb makeBomb(Scenes map, int damage, Buff[] states){
			return new Bomb(map, damage, states);
		}
	}
}
