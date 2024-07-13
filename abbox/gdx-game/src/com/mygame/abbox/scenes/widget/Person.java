package com.mygame.abbox.scenes.widget;

import android.graphics.Rect;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.mygame.abbox.obstacle.Obstacle;
import com.mygame.abbox.obstacle.shape.RectShape;
import com.mygame.abbox.scenes.buff.*;
import com.mygame.abbox.scenes.widget.Bomb.BombFactory;
import com.mygame.abbox.share.graphics.ColorShapeDrawable;
import com.mygame.abbox.share.input.*;
import com.mygame.abbox.share.math.Vector2D;

public class Person extends Obstacle
{
	private Attributes mAttributes;  //人物的属性
	private BuffPool mBuffPool;      //人物的buff
	
	private VelocityDrawable mVelocityDrawable; //绘制拉线箭头
	private BombFactory mBombFactory;  //允许你自定义创建炸弹的样式
	private Buff[] mCachedBombBuffs;   //缓存的炸弹的buff，准备附加给下个创建炸弹
	
	public Person(){
		this(0, 0, 0, 0);
	}
	public Person(int left, int top, int right, int bottom)
	{
		mAttributes = new Attributes(100, 10, 0);
		mBuffPool = new BuffPool();
		
		setShape(new RectShape(left, top, right, bottom));
		setObstacleDrawable(new PersonDrawable(Color.WHITE));
		setVelocityDrawable(new VelocityDrawable(Color.WHITE));
		setInputListener(new BombLauncher());
		setBombFactory(new BombFactory());
		setCachedBombBuffs(new Buff[0]);
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
	public VelocityDrawable getVelocityDrawable(){
		return mVelocityDrawable;
	}
	public void setVelocityDrawable(VelocityDrawable drawable){
		mVelocityDrawable = drawable;
	}
	public void setBombFactory(BombFactory factory){
		mBombFactory = factory;
	}
	public void setCachedBombBuffs(Buff[] buffs){
		mCachedBombBuffs = buffs;
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
	
	public Bomb makeBomb(int radius, double dx, double dy)
	{
		Bomb bomb = null;
		Rect bounds = getShape().getBounds();
		int cx = bounds.centerX();
		int cy = bounds.centerY();
		int damage = mAttributes.attck(0);
		Buff[] buffs = mCachedBombBuffs;
		
		if(mBombFactory != null){
			bomb = mBombFactory.makeBomb(cx, cy, radius, damage, buffs, dx, dy);
		}else{
			bomb = new Bomb(cx, cy, radius, damage, buffs, dx, dy);
		}
		bomb.mTarget = this;
		return bomb;
	}

	public void draw(ShapeRenderer render)
	{
		super.draw(render);
		//如果正在被触摸，则绘制发射炸弹的拉线向量箭头
		if(mVelocityDrawable != null && getInputListener() != null && getInputListener() instanceof onTouchMove)
		{
			InputRecording recording = ((onTouchMove)getInputListener()).getInputRecording();
			if(recording != null && recording.isTouching())
			{
				Rect bounds = getShape().getBounds();
				int startX = bounds.centerX();
				int startY = bounds.centerY();
				int dx = recording.nowX - startX;
				int dy = recording.nowY - startY;	
				//发射方向应与拖拉方向相反
				mVelocityDrawable.setVelocityWidth(10);
				mVelocityDrawable.setVelocityHeadLength(30);
				mVelocityDrawable.setVelocity(startX, startY, -dx, -dy);
				mVelocityDrawable.draw(render);
			}
		}
	}
	
	/* 绘制人物的Drawable */
	public class PersonDrawable extends ColorShapeDrawable
	{
		public PersonDrawable(Color color){
			super(color);
		}
		public void onDraw(ShapeRenderer render)
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
			
			//在人物下方绘制血条
			int healthyWidth = (int)(width * mAttributes.healthyBili());
			int healthyHeight  = (int)(height / 10);
			render.rect(bounds.left, bounds.bottom + healthyHeight, healthyWidth, healthyHeight);
		}
	}
	
	/* 绘制向量箭头的Drawable */
	public static class VelocityDrawable extends ColorShapeDrawable
	{
		private int velocityWidth;
		private int velocityHeadLength;
		private int startX, startY, dx, dy;
		
		public VelocityDrawable(Color color){
			super(color);
		}
		public void setVelocity(int startX, int startY, int dx, int dy){
			this.startX = startX;
			this.startY = startY;
			this.dx = dx;
			this.dy = dy;
		}
		public void setVelocityWidth(int width){
			velocityWidth = width;
		}
		public void setVelocityHeadLength(int length){
			velocityHeadLength = length;
		}
		public void onDraw(ShapeRenderer render)
		{
			//绘制箭头的直线
			Vector2D velocity = new Vector2D(dx, dy);
			int xVertex = startX + dx;
			int yVertex = startY + dy;
			render.rectLine(startX, startY, xVertex, yVertex, velocityWidth);

			//绘制箭头的顶角
			Vector2D direction = velocity.normalize().negate();
			Vector2D perpendicular = direction.perpendicular();
			double sx = Math.cos(0.3);
			double sy = Math.sin(0.3);

			Vector2D line1 = direction.multiply(sx).add(perpendicular.multiply(sy)).multiply(velocityHeadLength);
			Vector2D line2 = direction.multiply(sx).add(perpendicular.negate().multiply(sy)).multiply(velocityHeadLength);
			render.rectLine(xVertex, yVertex, (int)(xVertex + line1.x), (int)(yVertex + line1.y), velocityWidth);
			render.rectLine(xVertex, yVertex, (int)(xVertex + line2.x), (int)(yVertex + line2.y), velocityWidth);
		}
	}
	
	/* 感知触摸并发射炸弹 */
	public class BombLauncher extends onTouchMove
	{
		public void touchUp(InputEvent event, float x, float y, int pointer, int button)
		{
			super.touchUp(event, x, y, pointer, button);
			//计算炸弹方向
			Rect bounds = getShape().getBounds();
			int dx = (int)x - bounds.centerX();
			int dy = (int)y - bounds.centerY();
			Vector2D velocity = new Vector2D(-dx, -dy).normalize().multiply(6);
			//创建一个炸弹，并添加到组中
			Bomb bomb = makeBomb(30, velocity.x, velocity.y);
			getMyGroup().addObstacle(bomb);
		}
	}
	
	/* 人物的属性 */
	public static class Attributes
	{
		public int maxHealthy;
		public int healthy;
		public int attack;
		public int defense;

		public Attributes(int healthy, int attack, int defense){
			maxHealthy = this.healthy = healthy;
			this.attack = attack;
			this.defense = defense;
		}
		
		public float healthyBili(){
			return (float)healthy / maxHealthy;
		}
		public int attck(int damage){
			return damage + attack;
		}
		public void hurt(int damage){
			healthy -= damage - defense;
		}
	}
}
