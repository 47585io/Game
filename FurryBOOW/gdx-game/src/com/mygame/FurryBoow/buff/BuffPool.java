package com.mygame.FurryBoow.buff;

import com.mygame.FurryBoow.obstacle.Entity;
import com.badlogic.gdx.graphics.g2d.Batch;

public class BuffPool
{
	private long mImmunityBuffFlags;
	
	public int attack(int damage){
		return damage;
	}
	public int hurt(int damage){
		return damage;
	}
	public void addBuff(Entity en, Buff buff)
	{
		buff.onStart(en);
		if(buff.isActive() && !canImmunityBuff(buff)){
			addPreBuff();
		}
	}
	private void addPreBuff(){
		
	}
	private boolean canImmunityBuff(Buff buff){
		int code = buff.hashCode();
		return ((mImmunityBuffFlags >> code) & 1) == 1;
	}
	public void removeBuff(Entity en, Buff buff){
		
	}
	public void applyBuffs(Entity en){
		
	}
	public void updateBuffs(Entity en, float deltaTime){}
	
	public void clearBuffs(){}
	
	public void draw(Batch batch, Entity en){}
}
