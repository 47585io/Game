package com.mygame.abbox.scenes.buff;

import com.mygame.abbox.scenes.widget.Person;
import com.mygame.abbox.share.utils.IdentityArrayList;

public class BuffPool
{
	private IdentityArrayList<Buff> mBuffs;
	private IdentityArrayList<Integer> mBuffTime;
	
	public BuffPool(){
		mBuffs = new IdentityArrayList<>();
		mBuffTime = new IdentityArrayList<>();
	}
	
	public void addBuff(Person person, Buff buff)
	{
		if(canImmunityBuff(buff)){
			return;
		}
		buff.onStart(person);
		if(buff.getDuration() > 0){
			mBuffs.add(buff);
			mBuffTime.add(buff.getDuration());
		}
	}
	
	public void removeBuff(Person person, Buff buff)
	{
		buff.onEnd(person);
		int i = mBuffs.indexOf(buff);
		if(i > -1){
			mBuffs.remove(i);
			mBuffTime.remove(i);
		}
	}
	
	private void removeBuff(Person person, int i)
	{
		mBuffs.get(i).onEnd(person);
		mBuffs.remove(i);
		mBuffTime.remove(i);
	}
	
	public boolean hasBuff(Buff buff){
		return mBuffs.contains(buff);
	}
	
	public void clearBuffs()
	{
		mBuffs.clear();
		mBuffTime.clear();
	}

	public Buff[] toArray()
	{
		Buff[] buffs = new Buff[mBuffs.size()];
		mBuffs.toArray(buffs);
		return buffs;
	}
	
	public void updateBuffs(Person person, int delta)
	{
		for(int i = mBuffs.size() - 1; i > -1; i--)
		{
			int duration = mBuffTime.get(i);
			if(duration <= 0){
				removeBuff(person, i);
				continue;
			}
			mBuffs.get(i).onUpdate(person, delta);
			mBuffTime.set(i, duration - delta);
		}
	}
	
	public void beAttacked(Person person, int damage)
	{
		for(int i = mBuffs.size() - 1; i > -1; i--){
			mBuffs.get(i).beAttacked(person, damage);
		}
	}
	
	public boolean canImmunityBuff(Buff buff)
	{
		for(int i = mBuffs.size() - 1; i > -1; i--){
			if(mBuffs.get(i).canImmunityBuff(buff)){
				return true;
			}
		}
		return false;
	}
}
