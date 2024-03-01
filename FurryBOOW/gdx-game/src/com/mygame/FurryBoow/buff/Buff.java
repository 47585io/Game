package com.mygame.FurryBoow.buff;

import com.mygame.FurryBoow.obstacle.Entity;
import com.badlogic.gdx.graphics.g2d.Batch;

public interface Buff
{
	public long getDuration();
	
	public boolean isActive();
	
	public void onStart(Entity en);
	
	public void onUpdate(Entity en, long delta);
	
	public void onEnd(Entity en);

	public int hashCode();
	
	public CharSequence description();
	
	public void draw(Batch batch, Entity en);
}
