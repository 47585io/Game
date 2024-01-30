package com.mygame.FurryBoow.array;
import java.lang.reflect.*;

public class RecyleArrayPool
{
	private static final int ARRAY_SIZE = 6;
	
	private static final char[][] sCharArrays = new char[ARRAY_SIZE][0];
	
	private static final int[][] sIntArrays = new int[ARRAY_SIZE][0];
	
	private static final float[][] sFloatArrays = new float[ARRAY_SIZE][0];
	
	private static final int CACHE_SIZE = 73;
	
	private static final Object[] sCachedArray = new Object[CACHE_SIZE];
	
	
	public static char[] obtainCharArray(int size)
	{
		synchronized(sCharArrays)
		{
			for(int i = 0; i < sCharArrays.length; ++i)
			{
				char[] array = sCharArrays[i];
				if (array != null && array.length >= size) {
					sCharArrays[i] = null;
					return array;
				}
			}
		}
		return ArrayUtils.newUnpaddedCharArray(GrowingArrayUtils.growSize(size));
	}
	public static void recyleCharArray(char[] array)
	{
		synchronized (sCharArrays)
		{
			for (int i = 0; i<sCharArrays.length; i++) 
			{
				if (sCharArrays[i] == null || array.length > sCharArrays[i].length) {
					sCharArrays[i] = array;
					break;
				}
			}
		}
	}
	public static int[] obtainIntArray(int size)
	{
		synchronized(sIntArrays)
		{
			for(int i = 0; i < sIntArrays.length; ++i)
			{
				int[] array = sIntArrays[i];
				if (array != null && array.length >= size) {
					sIntArrays[i] = null;
					return array;
				}
			}
		}
		return ArrayUtils.newUnpaddedIntArray(GrowingArrayUtils.growSize(size));
	}
	public static void recyleIntArray(int[] array)
	{
		synchronized (sIntArrays)
		{
			for (int i = 0; i < sIntArrays.length; i++) 
			{
				if (sIntArrays[i] == null || array.length > sIntArrays[i].length) {
					sIntArrays[i] = array;
					break;
				}
			}
		}
	}
	public static float[] obtainFloatArray(int size)
	{
		synchronized(sFloatArrays)
		{
			for(int i = 0; i < sFloatArrays.length; ++i)
			{
				float[] array = sFloatArrays[i];
				if (array != null && array.length >= size) {
					sFloatArrays[i] = null;
					return array;
				}
			}
		}
		return ArrayUtils.newUnpaddedFloatArray(GrowingArrayUtils.growSize(size));
	}
	public static void recyleFloatArray(float[] array)
	{
		synchronized (sFloatArrays)
		{
			for (int i = 0; i < sFloatArrays.length; i++) 
			{
				if (sFloatArrays[i] == null || array.length > sFloatArrays[i].length) {
					sFloatArrays[i] = array;
					break;
				}
			}
		}
	}
	
	public static<T> T[] obtainObjectArray(Class<T> kind, int size)
	{
		synchronized (sCachedArray)
		{
			//计算用于存放(T[])的数组处于sObjectArray中的下标
			int bucket = (kind.hashCode() & 0x7FFFFFFF) % CACHE_SIZE;
			Object cache = sCachedArray[bucket];
			if (cache == null || cache.getClass().getComponentType().getComponentType() != kind) {
				return ArrayUtils.newUnpaddedArray(kind, GrowingArrayUtils.growSize(size));
			}
			T[][] cacheArray = (T[][]) cache;
			for(int i = 0; i < cacheArray.length; ++i)
			{
				T[] array = cacheArray[i];
				if (array != null && array.length >= size) {
					cacheArray[i] = null;
					return array;
				}
			}
		}
		return ArrayUtils.newUnpaddedArray(kind, GrowingArrayUtils.growSize(size));
	}
	public static<T> void recyleObjectArray(T[] array)
	{
		synchronized (sCachedArray)
		{
			Class<T> kind = (Class<T>) array.getClass().getComponentType();
			int bucket = (kind.hashCode() & 0x7FFFFFFF) % CACHE_SIZE;
			Object cache = sCachedArray[bucket];
			if (cache == null || cache.getClass().getComponentType().getComponentType() != kind) {
				//第一次获取，或者键计算冲突，则新创建一个
				cache = Array.newInstance(kind, 6, 0);
				sCachedArray[bucket] = cache;
			}
			T[][] cacheArray = (T[][]) cache;
			for (int i = 0;i < cacheArray.length;i++) 
			{
				if (cacheArray[i] == null || array.length > cacheArray[i].length) {
					cacheArray[i] = array;
					break;
				}
			}
		}
	}
	
}
