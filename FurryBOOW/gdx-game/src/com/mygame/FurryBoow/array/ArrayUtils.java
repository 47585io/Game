package com.mygame.FurryBoow.array;

import java.lang.reflect.Array;
import java.util.Objects;

public final class ArrayUtils
{
	private static final int ALIGNMENT_AMOUNT = 8;

	/* 创建一个新的指定元素个数的数组，但这会充分利用空闲的空间 
	 * 如果size == 0，则返回一个已有的空数组对象
	 * 否则返回一个内存对齐后的数组对象
	 */
	public static<T> T[] newUnpaddedArray(Class<T> kind, int size){
		return size == 0 ? EmptyArray.emptyArray(kind) :
		       (T[])Array.newInstance(kind,getAlignmentMinimumSize(kind, size));
	}
	public static boolean[] newUnpaddedBooleanArray(int size){
		return size == 0 ? EmptyArray.BOOLEAN :
		       new boolean[getAlignmentMinimumSize(boolean.class,size)];
	}
	public static byte[] newUnpaddedByteArray(int size){
		return size == 0 ? EmptyArray.BYTE :
		       new byte[getAlignmentMinimumSize(byte.class,size)];
	}
	public static char[] newUnpaddedCharArray(int size){
		return size == 0 ? EmptyArray.CHAR :
		       new char[getAlignmentMinimumSize(char.class,size)];
	}
	public static int[] newUnpaddedIntArray(int size){
		return size == 0 ? EmptyArray.INT :
		       new int[getAlignmentMinimumSize(int.class,size)];
	}
	public static float[] newUnpaddedFloatArray(int size){
		return size == 0 ? EmptyArray.FLOAT :
		       new float[getAlignmentMinimumSize(float.class,size)];
	}
	public static long[] newUnpaddedLongArray(int size){
		return size == 0 ? EmptyArray.LONG :
		       new long[getAlignmentMinimumSize(long.class,size)];
	}
	public static double[] newUnpaddedDoubleArray(int size){
		return size == 0 ? EmptyArray.DOUBLE :
		       new double[getAlignmentMinimumSize(double.class,size)];
	}

	//计算指定元素类型且指定元素个数的数组的内存对齐后的最小元素个数
	private static int getAlignmentMinimumSize(Class kind, int size)
	{
		if(kind==boolean.class || kind==byte.class){
			size = getAlignmentMinimumSize(1,size);
		}
		else if(kind==char.class){
			size = getAlignmentMinimumSize(2,size);
		}
		else if(kind==int.class || kind==float.class){
			size = getAlignmentMinimumSize(4,size);
		}
		else if(kind==long.class || kind==double.class){
			size = getAlignmentMinimumSize(8,size);
		}
		else{
			size = getAlignmentMinimumSize(4,size);
		}
		return size;
	}

	//数组要占用的内存长度没有对齐，就将其对齐到下个位置，计算对齐后的元素个数
	private static int getAlignmentMinimumSize(int kind, int size)
	{
		int len = size * kind;
		int over = len % ALIGNMENT_AMOUNT;
		if(over != 0){
			len += ALIGNMENT_AMOUNT - over;
			size = len / kind;
		}
		return size;
	}
	
	/* 在数组中向后寻找指定元素，找到了返回它的下标，从index开始 */
	public static <T> int indexOf(T[] array, T value, int index)
	{
        if (array == null || index<0) return -1;
        for (; index < array.length; index++) {
            if (Objects.equals(array[index], value)) return index;
        }
        return -1;
    }
	/* 在数组中向前寻找指定元素，找到了返回它的下标，从index开始 */
	public static <T> int lastIndexOf(T[] array, T value, int index)
	{
        if (array == null || index>=array.length) return -1;
        for (;index>=0; index--) {
            if (Objects.equals(array[index], value)) return index;
        }
        return -1;
    }

	public static int indexOf(char[] array, char value, int index, int end)
	{
        if (array == null || index<0) return -1;
        for (; index < end; index++) {
            if (array[index]==value) return index;
        }
        return -1;
    }
	public static int lastIndexOf(char[] array, char value, int index, int begin)
	{
        if (array == null || index>=array.length) return -1;
        for (;index >= begin; index--) {
            if (array[index]==value) return index;
        }
        return -1;
    }

	public static int indexOf(int[] array, int value, int index)
	{
        if (array == null || index<0) return -1;
        for (; index < array.length; index++) {
            if (array[index]==value) return index;
        }
        return -1;
    }
	public static int lastIndexOf(int[] array, int value, int index)
	{
        if (array == null || index>=array.length) return -1;
        for (;index>=0; index--) {
            if (array[index]==value) return index;
        }
        return -1;
    }
}
