package com.mygame.FurryBoow;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.mygame.FurryBoow.obstacle.*;
import com.mygame.FurryBoow.scenes.*;
import com.mygame.FurryBoow.scenes.utils.*;
import android.graphics.Rect;
import com.badlogic.gdx.scenes.scene2d.*;


public class MyGdxGame implements ApplicationListener
{
	@Override
	public void create()
	{ 
		
	}

	@Override
	public void render()
	{
	
	}

	@Override
	public void dispose(){
		
	}

	@Override
	public void resize(int width, int height){}

	@Override
	public void pause(){}

	@Override
	public void resume(){}

	
	public static final int cast(int y){
		return Gdx.graphics.getHeight() - y;
	}
}
