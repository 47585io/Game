package com.mygame.abbox.share.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ColorShapeDrawable implements ShapeDrawable
{
	private Color mColor;
	
	public ColorShapeDrawable(Color color){
		mColor = color;
	}
	public void setColor(Color color){
		mColor = color;
	}
	public Color getColor(){
		return mColor;
	}

	public void draw(ShapeRenderer render)
	{
		Color saveColor = render.getColor();
		render.setColor(mColor);
		onDraw(render);
		render.setColor(saveColor);
	}
	public void onDraw(ShapeRenderer render){}
}
