package com.mygame.abbox.share.input;
import com.badlogic.gdx.scenes.scene2d.*;

public class onTouchMove extends InputListener
{
	private InputRecording mInputRecording;
	
	public InputRecording getInputRecording(){
		return mInputRecording;
	}
	
	public void move(InputEvent event, int dx, int dy, int orginDx, int orginDy){}
	
	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
	{
		if(mInputRecording == null){
			mInputRecording = new InputRecording();
		}
		//多指触摸的情况下，只接受第一根手指
		if(!mInputRecording.isTouching()){
			mInputRecording.touchDown((int)x, (int)y, pointer);
			return true;
		}
		return false;
	}

	public void touchDragged(InputEvent event, float x, float y, int pointer)
	{
		//继续追踪第一根手指的位置，计算偏移距离
		if(pointer == mInputRecording.pointer){
			mInputRecording.setNowPos((int)x, (int)y);
			move(event, mInputRecording.getDx(), mInputRecording.getDy(), mInputRecording.getOrginDx(), mInputRecording.getOrginDy());
			mInputRecording.setLastPos((int)x, (int)y);
		}
	}
	
	public void touchUp(InputEvent event, float x, float y, int pointer, int button)
	{
		if(pointer == mInputRecording.pointer){
			mInputRecording.setNowPos((int)x, (int)y);
			move(event, mInputRecording.getDx(), mInputRecording.getDy(), mInputRecording.getOrginDx(), mInputRecording.getOrginDy());
			mInputRecording.setLastPos((int)x, (int)y);
			mInputRecording.touchUp();
		}
	}
}
