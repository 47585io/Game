package com.mygame.abbox.scenes.widget;
import com.mygame.abbox.obstacle.*;
import com.mygame.abbox.obstacle.shape.*;
import com.mygame.abbox.scenes.buff.*;
import com.badlogic.gdx.graphics.glutils.*;
import android.graphics.Rect;
import com.badlogic.gdx.graphics.*;

public class Person extends Obstacle
{
	private Attributes mAttributes;
	private BuffPool mBuffPool;

	public Person(){
		this(0, 0, 0, 0);
	}
	public Person(int left, int top, int right, int bottom){
		setShape(new RectShape(left, top, right, bottom));
		mAttributes = new Attributes();
		mBuffPool = new BuffPool();
	}

	public Attributes getAttributes(){
		return mAttributes;
	}
	public BuffPool getBuffPool(){
		return mBuffPool;
	}
	public RectShape getShape(){
		return (RectShape)super.getShape();
	}
	
	public void sendDamage(int damage){
		mAttributes.hurt(damage);
		mBuffPool.beAttacked(this, damage);
	}
	public void sendBuff(Buff[] buffs){
		for(int i = 0; i < buffs.length; i++){
			mBuffPool.addBuff(this, buffs[i]);
		}
	}
	public void updateBuffs(int delta){
		mBuffPool.updateBuffs(this, delta);
	}
	
	public void draw(ShapeRenderer render)
	{
		//根据外包矩形的大小，绘制小人
		Rect bounds = getShape().getBounds();
		float width = bounds.width();
		float height = bounds.height();
		
		//用两个椭圆充当小人的头部和身体
		float headHeight = height * 0.3f;
		render.ellipse(bounds.left, bounds.top, width, headHeight);
		float bodyHeight = height * 0.7f;
		render.ellipse(bounds.left, bounds.top + headHeight, width, bodyHeight);
	}
	
	public static class Attributes
	{
		public int healthy;
		public int attack;
		public int defense;

		public int attck(int damage){
			return damage + attack;
		}
		public void hurt(int damage){
			healthy -= damage - defense;
		}
	}
}
