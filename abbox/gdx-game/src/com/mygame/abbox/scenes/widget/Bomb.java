package com.mygame.abbox.scenes.widget;

import java.util.Arrays;
import android.graphics.Rect;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.mygame.abbox.obstacle.*;
import com.mygame.abbox.obstacle.shape.CircleShape;
import com.mygame.abbox.scenes.buff.Buff;
import com.mygame.abbox.share.graphics.ShapeCollision;
import com.mygame.abbox.share.graphics.ColorShapeDrawable;
import com.mygame.abbox.share.math.Vector2D;
import com.mygame.abbox.share.input.onTouchMove;

public class Bomb extends Obstacle implements DynamicObject
{
	private static final int MAX_STATE_COUNT = 5;
	private int mDamage;         //炸弹的伤害
	private Buff[] mStates;      //炸弹的buff
	private int mStateCount;     //炸弹的buff数量
	private Vector2D mVelocity;  //炸弹的向量
	Person mTarget;              //谁发射的炸弹
	
	public Bomb(){
		this(0, 0, 0);
	}
	public Bomb(int cx, int cy, int radius){
		this(cx, cy, radius, 0, new Buff[0], 0, 0);
	}
	public Bomb(int cx, int cy, int radius, int damage, Buff[] buffs, int dx, int dy)
	{
		mDamage = damage;
		mStates = new Buff[MAX_STATE_COUNT];
		mStateCount = buffs.length > MAX_STATE_COUNT ? MAX_STATE_COUNT : buffs.length;
		System.arraycopy(buffs, 0, mStates, 0, mStateCount);
		mVelocity = new Vector2D(dx, dy);
		
		setShape(new CircleShape(cx, cy, radius));
		setObstacleDrawable(new BombDrawable(Color.WHITE));
		setInputListener(new MoveBomb());
	}

	public int getDamage(){
		return mDamage;
	}
	public Buff[] getBuffs(){
		return Arrays.copyOf(mStates, mStateCount);
	}
	public Vector2D getVelocity(){
		return mVelocity;
	}
	public Person getTarget(){
		return mTarget;
	}
	public CircleShape getShape(){
		return (CircleShape)super.getShape();
	}

	public void update(){
		//向着指定方向移动一段距离，并且限制触摸的帧数减1
		getShape().offset((int)mVelocity.x, (int)mVelocity.y);
		setInputDuration(getInputDuration() - 1);
	}

	public void onCollision(Obstacle other)
	{
		if(other instanceof Box){
			onCollisionBox((Box)other);
		}
		else if(other instanceof Person){
			onCollisionPerson((Person)other);
		}
		else if(other instanceof Bomb){
			onCollisionBomb((Bomb)other);
		}
		setInputDuration(10); //碰撞后一段时间内无法手动改变方向
	}

	/* 炸弹碰到方块了 */
	private void onCollisionBox(Box box)
	{
		antimissileVelocityIfBombCollisionBox(this, box);
		//收集方块附带的Buff
		Buff buff = box.popBuff();
		if(buff != null)
		{
			if(buff.isDebuff()){
				//如果是负面Buff，则让它附着于炸弹上
				if(mStateCount < mStates.length){
					mStates[mStateCount++] = buff;
				}
			}else{
				//如果是正面Buff，则直接给自己
				mTarget.getBuffPool().addBuff(mTarget, buff);
			}
		}
	}
	
	/* 当球和方块碰撞时，反弹球的速度向量 */
	private static void antimissileVelocityIfBombCollisionBox(Bomb bomb, Box box)
	{
		//球和方块碰撞了，具体碰撞到了哪条边或者是四个角呢？
		//我们将球的圆心和方块中心连一条线，这条线会穿过方块的边缘
		//如果这条线与方块的一条边相交，则球碰到该边
		//如果这条线与方块的两条边相交，则球碰到这两边的顶点
		Rect boxBounds = box.getShape().getBounds();
		int left = boxBounds.left;
		int top = boxBounds.top;
		int right = boxBounds.right;
		int bottom = boxBounds.bottom;

		CircleShape bombShape = bomb.getShape();
		int circleX = bombShape.getCircleX();
		int circleY = bombShape.getCircleY();
		int boxcenterX = boxBounds.centerX();
		int boxcenterY = boxBounds.centerY();

		//分别判断方块的四条边与球的圆心和方块中心的连线是否相交
		boolean collisionLeft = ShapeCollision.line_line(left, top, left, bottom, circleX, circleY, boxcenterX, boxcenterY);
		boolean collisionTop = ShapeCollision.line_line(left, top, right, top, circleX, circleY, boxcenterX, boxcenterY);
		boolean collisionRight = ShapeCollision.line_line(right, top, right, bottom, circleX, circleY, boxcenterX, boxcenterY);
		boolean collisionBottom = ShapeCollision.line_line(left, bottom, right, bottom, circleX, circleY, boxcenterX, boxcenterY);

		//碰到一边则反弹一个速度，碰到顶点则反弹两个速度
		Vector2D velocity = bomb.getVelocity();
		if(collisionLeft || collisionRight){
			//以当前速度移动时，如果球的圆心会更靠近方块中心(与其距离更近)，则反弹速度
			if(Math.abs(boxcenterX - circleX) < Math.abs(boxcenterX - (circleX - velocity.x))){
				velocity.x *= -1;
			}
		}
		if(collisionTop || collisionBottom){
			if(Math.abs(boxcenterY - circleY) < Math.abs(boxcenterY - (circleY - velocity.y))){
				velocity.y *= -1;
			}
		}
	}

	/* 炸弹碰到炸弹了 */
	private void onCollisionBomb(Bomb bomb){
		antimissileVelocityIfBombCollisionBomb(this, bomb);
	}
	
	/* 当两个球碰撞时，反弹它们各自的速度向量 */
	private static void antimissileVelocityIfBombCollisionBomb(Bomb bomb1, Bomb bomb2)
	{
		Vector2D pos1 = new Vector2D(bomb1.getShape().getCircleX(), bomb1.getShape().getCircleY());
		Vector2D vel1 = bomb1.getVelocity();
		Vector2D pos2 = new Vector2D(bomb2.getShape().getCircleX(), bomb2.getShape().getCircleY());
		Vector2D vel2 = bomb2.getVelocity();
		
		// 计算碰撞后的新速度向量
		Vector2D v1f = calculateCollisionVelocity(pos1, vel1, pos2, vel2);
        Vector2D v2f = calculateCollisionVelocity(pos2, vel2, pos1, vel1);
        // 更新球的速度向量
        vel1.set(v1f);
        vel2.set(v2f);
	}

	/* 当两个球碰撞时，计算并返回第一个球反弹后的速度向量 */
    private static Vector2D calculateCollisionVelocity(Vector2D pos1, Vector2D vel1,
													   Vector2D pos2, Vector2D vel2) {
        // 计算相对速度
        Vector2D relativeVelocity = vel2.subtract(vel1);
        // 计算碰撞方向（单位向量）
        Vector2D collisionDirection = pos2.subtract(pos1).normalize();
        // 计算相对速度在碰撞方向上的投影
        double projection = relativeVelocity.dotProduct(collisionDirection);
        // 计算碰撞后的速度向量
        Vector2D newVelocity = collisionDirection.multiply(projection);
		// 保持速度大小不变，只改变方向
		double originalSpeed = vel1.length(); 
		return newVelocity.normalize().multiply(originalSpeed);
    }
	
	/* 炸弹碰到人了 */
	private void onCollisionPerson(Person person)
	{
		//将炸弹的伤害和附着在炸弹上的负面Buff施加到人身上，然后销毁自己
		Buff[] buffs = getBuffs();
		person.sendDamage(mDamage);
		person.sendBuff(buffs);
		getMyGroup().removeObstacle(this);
	}
	
	/* 绘制炸弹的Drawable */
	public class BombDrawable extends ColorShapeDrawable
	{
		public BombDrawable(Color color){
			super(color);
		}
		public void onDraw(ShapeRenderer render)
		{
			//绘制球本身
			CircleShape shape = getShape();
			int circleX = shape.getCircleX();
			int circleY = shape.getCircleY();
			int circleRadius = shape.getCircleRadius();
			render.circle(circleX, circleY, circleRadius);
		}
	}
	
	/* 当触摸炸弹时，改变炸弹方向 */
	public class MoveBomb extends onTouchMove
	{
		public void move(InputEvent event, int dx, int dy, int orginDx, int orginDy){
			mVelocity.set(orginDx, orginDy);
		}
	}
	
	/* 炸弹工厂 */
	public static class BombFactory
	{
		public Bomb makeBomb(int cx, int cy, int radius, int damage, Buff[] buffs, int dx, int dy){
			return new Bomb(cx, cy, radius, damage, buffs, dx, dy);
		}
	}
}
