package com.mygame.FurryBoow.obstacle;

import com.mygame.FurryBoow.buff.*;

/* 拥有生命的物体，除了拥有位置，还拥有属性，并可以附带Buff */
public class Entity extends Obstacle
{
	private BuffPool mBuffPool;
	private Attributes mAttributes;
	
	public Entity(Scenes map){
		super(map);
	}
	public void setAttributes(Attributes attr){
		mAttributes = attr;
	}
	public Attributes getAttributes(){
		return mAttributes;
	}
	public BuffPool getBuffPool(){
		return mBuffPool;
	}
	
	public void sendDamage(int damage){

	}
	public void sendBuff(Buff[] buffs){

	}
	
	public static class Attributes
	{
		public int healthy;
		public int attack;
		public int defense;
		
		public int attck(int damage){
			return damage + attack;
		}
		public void hurt(int damage){
			healthy -= damage - defense;
		}
	}
}
