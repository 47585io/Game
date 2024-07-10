package com.mygame.abbox.scenes.buff;
import com.mygame.abbox.scenes.widget.*;
import com.badlogic.gdx.graphics.*;

public class HealthyBuff implements Buff
{
	private int mHealthy;
	
	public HealthyBuff(int healthy){
		mHealthy = healthy;
	}
	public int getDuration(){
		return 0;
	}
	public void onStart(Person person){
		person.getAttributes().healthy += mHealthy;
	}

	public void onUpdate(Person person, int delta){}

	public void beAttacked(Person person, int damage){}
	
	public void onEnd(Person person){}

	public boolean canImmunityBuff(Buff buff){
		return false;
	}
	public boolean isDebuff(){
		return false;
	}
	public Color buffColor(){
		return Color.RED;
	}
}
