package com.mygame.abbox.scenes.buff;

import com.badlogic.gdx.graphics.Color;
import com.mygame.abbox.scenes.widget.Person;

public interface Buff
{
	public int getDuration()
	
	public void onStart(Person person)

	public void onUpdate(Person person, int delta)

	public void beAttacked(Person person, int damage)
	
	public void onEnd(Person person)
	
	public boolean canImmunityBuff(Buff buff)

	public boolean isDebuff()
	
	public Color buffColor()
}
