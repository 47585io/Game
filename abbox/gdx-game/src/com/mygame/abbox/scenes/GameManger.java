package com.mygame.abbox.scenes;

import android.graphics.*;
import com.mygame.abbox.obstacle.*;
import com.mygame.abbox.scenes.widget.*;
import java.util.*;
import com.badlogic.gdx.scenes.scene2d.*;

public class GameManger implements ObstacleGroup.CollisionCallback
{
	public GameManger(Scenes scenes){
		mScenes = scenes;
	}
	
	public void init(){
		initEdge();
		//initPersons();
		initBomb();
		//initBox(200);
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

		Box left = new Box(0, 0, 0 + spacing, height);
		Box top = new Box(0, 0, width, 0 + spacing);
		Box right = new Box(width - spacing, 0, width, height);
		Box bottom = new Box(0, height - spacing, width, height);

		group.addObstacle(left);
		group.addObstacle(top);
		group.addObstacle(right);
		group.addObstacle(bottom);
	}

	private void initPersons(){
		Person person = new Person(100, 500, 160, 700);
		mScenes.getObstacleGroup().addObstacle(person);
	}
	
	private void initBomb(){
		Bomb b1 = new Bomb(50, 50, 30, 0, 4, 4);
		Bomb b2 = new Bomb(500, 500, 30, 0, 4, 4);
		Bomb b3 = new Bomb(250, 250, 30, 0, 4, 4);
		mScenes.getObstacleGroup().addObstacle(b1);
		mScenes.getObstacleGroup().addObstacle(b2);
		mScenes.getObstacleGroup().addObstacle(b3);
		b1.requestFocus();
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
		return new Box(rect.left, rect.top, rect.right, rect.bottom);
	}
	
	private static int rand(int a, int b){
		return mRand.nextInt(b - a + 1) + a;
	}

	public void onCollision(Obstacle target, Obstacle other){
		target.onCollision(other);
		//物体可以处理的东西自己处理
		//不能处理在这里处理，比如全局buff
		
		InputEvent v;
	}
	
	private Person self;
	private Bomb selfBomb;
	
	private Scenes mScenes;
	private static final Random mRand = new Random();
}
