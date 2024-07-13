package com.mygame.abbox.scenes;

import android.graphics.*;
import com.mygame.abbox.obstacle.*;
import com.mygame.abbox.obstacle.ObstacleGroup.*;
import com.mygame.abbox.scenes.widget.*;
import java.util.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.mygame.abbox.scenes.buff.*;

public class GameManger implements CollisionCallback, ObstacleWatcher
{
	public GameManger(Scenes scenes){
		mScenes = scenes;
	}
	
	public void init(){
		initEdge();
		initPersons();
		//initBomb();
		initBox(200);
	}
	
	private void initBox(int count)
	{
		ObstacleGroup group = mScenes.getObstacleGroup();
		for(int i = 0; i < count; ++i)
		{
			Box box = nextRandBox();
			if(box != null){
				group.addObstacle(box);
			}
		}
	}
	private void initEdge()
	{
		ObstacleGroup group = mScenes.getObstacleGroup();
		int width = group.getWidth();
		int height = group.getHeight();
		int spacing = 10;

		Box left = new Box(0, 0, 0 + spacing, height, null);
		Box top = new Box(0, 0, width, 0 + spacing, null);
		Box right = new Box(width - spacing, 0, width, height, null);
		Box bottom = new Box(0, height - spacing, width, height, null);

		group.addObstacle(left);
		group.addObstacle(top);
		group.addObstacle(right);
		group.addObstacle(bottom);
	}

	private void initPersons(){
		self = new Person(1000, 1500, 1060, 1700);
		mScenes.getObstacleGroup().addObstacle(self);
	}
	
	private void initBomb(){
		Buff[] buffs = new Buff[0];
		selfBomb = self.makeBomb(30, 4, 4);
		//Bomb b2 = new Bomb(500, 500, 30, 0, buffs, 4, 4);
		//Bomb b3 = new Bomb(250, 250, 30, 0, buffs, 4, 4);
		mScenes.getObstacleGroup().addObstacle(selfBomb);
		//mScenes.getObstacleGroup().addObstacle(b2);
		//mScenes.getObstacleGroup().addObstacle(b3);
		selfBomb.requestFocus();
	}

	private Box nextRandBox()
	{
		ObstacleGroup group = mScenes.getObstacleGroup();
		Rect rect = new Rect();
		int width = group.getWidth();
		int height = group.getHeight();
		int boxWidth = 100;
		int boxHeight = 100;
		int spacing = 100;
		int tryCount = 10;

		ArrayList<Obstacle> objs = new ArrayList<>();
		do{
			objs.clear();
		    rect.left = rand(0, width - boxWidth);
		    rect.top = rand(0, height - boxHeight);                                                                                           
		    rect.right = rect.left + boxWidth;
		    rect.bottom = rect.top + boxHeight;
		    group.getObstacles(rect.left-spacing, rect.top-spacing, rect.right+spacing, rect.bottom+spacing, objs);
			tryCount--;
		}
		while(tryCount > 0 && objs.size() > 0);
		if(objs.size() > 0){
			return null;
		}
		return new Box(rect.left, rect.top, rect.right, rect.bottom, null);
	}
	
	private static int rand(int a, int b){
		return mRand.nextInt(b - a + 1) + a;
	}
	
	/* 到自己发射炸弹的回合 */
	private void onSelfRound(){
		self.requestFocus();
		self.setInputDuration(0);
		self.updateBuffs(1);
	}

	public void onCollision(Obstacle target, Obstacle other)
	{
		target.onCollision(other);
		//物体可以处理的东西自己处理
		//不能处理在这里处理，比如全局buff
	}

	public void onObstacleAdded(Obstacle obj)
	{
		if(obj instanceof Bomb){
			//当添加了一个炸弹时，查看是不是自己添加的
			Bomb bomb = (Bomb) obj;
			if(bomb.getTarget() == self){
				selfBomb = bomb;
			}
		}
	}

	public void onObstacleRemoved(Obstacle obj)
	{
		if(obj == selfBomb){
			//当移除物体时，查看移除的是不是自己的炸弹
			onSelfRound();
		}
	}
	
	private Person self;
	private Bomb selfBomb;
	
	private Scenes mScenes;
	private static final Random mRand = new Random();
}
