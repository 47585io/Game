package com.mygame.abbox.share.input;

public class InputRecording
{
	public int nowX, nowY;
	public int lastX, lastY;
	public int firstX, firstY;
	public int pointer;
	public int key;
	
	public InputRecording(){
		pointer = -1;
	}
	
	public void setNowPos(int x, int y){
		nowX = x;
		nowY = y;
	}
	public void setLastPos(int x, int y){
		lastX = x;
		lastY = y;
	}
	public void setFirstPos(int x, int y){
		firstX = x;
		firstY = y;
	}
	
	public int getDx(){
		return nowX - lastX;
	}
	public int getDy(){
		return nowY - lastY;
	}
	public int getOrginDx(){
		return nowX - firstX;
	}
	public int getOrginDy(){
		return nowY - firstY;
	}
	
	public void touchDown(int x, int y, int id)
	{
		firstX = lastX = nowX = x;
		firstY = lastY = nowY = y;
		pointer = id;
	}
	public void touchUp(){
		pointer = -1;
	}
	public boolean isTouching(){
		return pointer != -1;
	}
}
