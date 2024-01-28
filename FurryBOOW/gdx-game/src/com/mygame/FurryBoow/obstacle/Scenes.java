package com.mygame.FurryBoow.obstacle;
import java.util.*;
import com.mygame.FurryBoow.array.*;

public class Scenes
{
	public Scenes(){
		yPointCount = 0;
		yPoints = EmptyArray.INT;
		yPointOverlap = EmptyArray.INT;
		yRanges = EmptyArray.emptyArray(yRangeNode.class);
	}
	public Scenes(Obstacle[] objs){
		
	}
	/* 用指定的点构建区间列表(points长度必须比点数多1)，返回区间列表的长度 */
	private static int checkPoints(int[] points)
	{
		//对points排序后去重，便构建了一个区间列表
		Arrays.sort(points, 0, points.length - 1);
		int length = points.length - 1;
		int remainingLength = length;
		int prevHead = 1, prevTail = 1;
		points[length] = Integer.MIN_VALUE; //聪明一点，设置一个小的数
		for(;prevHead < length;)
		{
			for(;points[prevHead] == points[prevHead-1]; ++prevHead){
				remainingLength--;
			}
			//头找到一个不重复的元素，然后递给尾巴
			points[prevTail++] = points[prevHead++];
		}
		return remainingLength;
	}
	
	/* 向二维区间树中添加一个矩形 */
	public void addObstacle(Obstacle obj)
	{
		//int xStart = obj.left;
		//int xEnd = obj.right;
		//int yStart = obj.top;
		//int yEnd = obj.bottom;
		//addyRange(xStart, xEnd, yStart, yEnd, obj);
	}
	/* 将矩形的两横边的纵坐标插入到y轴点区间列表中，并遍历两边之间的区间，将矩形的两纵边的横坐标加到这些x轴区间列表中 */
	private void addyRange(int xStart, int xEnd, int yStart, int yEnd, Obstacle obj)
	{
		int yStartIndex = addyPoint(yStart);
		int yEndIndex = addyPoint(yEnd);
		for(;yStartIndex < yEndIndex; ++yStartIndex){
			addxRange(yRanges[yStartIndex], xStart, xEnd, obj);
		}
	}
	/* 将矩形的两纵边的横坐标插入到这些x轴区间列表中，并遍历两边之间的区间，将矩形插入到与它重叠的区间中 */
	private static void addxRange(yRangeNode node, int xStart, int xEnd, Obstacle obj)
	{
		int xStartIndex = addxPoint(node, xStart);
		int xEndIndex = addxPoint(node, xEnd);
		ObstacleLink[] xRangeObjs = node.xRangeObjs;
		for(;xStartIndex < xEndIndex; ++xStartIndex){
			xRangeObjs[xStartIndex] = new ObstacleLink(obj, xRangeObjs[xStartIndex]);
		}
	}
	/* 在y轴的点区间列表中插入一个点，返回插入的点的位置 */
	private int addyPoint(int y)
	{
		//如果已有相同的点就无需再次插入
		int yIndex = findIndex(yPoints, yPointCount, y);
		if(!(yIndex < yPointCount && y == yPoints[yIndex]))
		{
			//插入新的点，还需要把其所在区间的内容拷贝一份(除了在区间之外)
			GrowingArrayUtils.insert(yPoints, yPointCount, yIndex, y);
			yRangeNode node = null;
			if(yIndex == 0 || yIndex == yPointCount){
				node = new yRangeNode();
			}else{
				node = yRanges[yIndex - 1].clone();
			}
			GrowingArrayUtils.insert(yRanges, yPointCount - 1, yIndex == yPointCount ? yPointCount - 1 : yIndex, node);
			yPointCount++;
		}
		return yIndex;
	}
	/* 在x轴的点区间列表中插入一个点，返回插入的点的位置 */
	private static int addxPoint(yRangeNode node, int x)
	{
		int[] xPoints = node.xPoints;
		int xPointCount = node.xPointCount;
		int xIndex = findIndex(xPoints, xPointCount, x);
		if(!(xIndex < xPointCount && x == xPoints[xIndex]))
		{
			GrowingArrayUtils.insert(xPoints, xPointCount, xIndex, x);
			ObstacleLink link = null;
			if(xIndex == 0 || xIndex == xPointCount){
				link = new ObstacleLink(null, null);
			}else{
				link = node.xRangeObjs[xIndex - 1].clone();
			}
			GrowingArrayUtils.insert(node.xRangeObjs, xPointCount - 1, xIndex == xPointCount ? xPointCount - 1 : xIndex, link);
			node.xPointCount++;
		}
		return xIndex;
	}
	
	/* 在二维区间树中移除一个矩形 */
	public void removeObstacle(Obstacle obj)
	{
		//int xStart = obj.left;
		//int xEnd = obj.right;
		//int yStart = obj.top;
		//int yEnd = obj.bottom;
		//removeyRange(xStart, xEnd, yStart, yEnd, obj);
	}
	private void removeyRange(int xStart, int xEnd, int yStart, int yEnd, Obstacle obj)
	{
		int yStartIndex = removeyPoint(yStart);
		int yEndIndex = removeyPoint(yEnd);
		for(;yStartIndex < yEndIndex; ++yStartIndex){
			removexRange(yRanges[yStartIndex], xStart, xEnd, obj);
		}
	}
	private void removexRange(yRangeNode node, int xStart, int xEnd, Obstacle obj)
	{
		int xStartIndex = removexPoint(node, xStart);
		int xEndIndex = removexPoint(node, xEnd) - 1;
		ObstacleLink[] xRangeObjs = node.xRangeObjs;
		for(;xStartIndex < xEndIndex; ++xStartIndex)
		{
			ObstacleLink link = xRangeObjs[xStartIndex];
			if(link.obj == obj){
				link = link.next;
			}
			for(;link.next != null; link = link.next){
				if(link.next.obj == obj){
					link.next = link.next.next;
				}
			}
		}
	}
	private int removeyPoint(int y)
	{
		//如果此点只有一个矩形的边的坐标与其重叠，则移除点
		//移除后，直接移除点对应的区间即可，因为此点是无关紧要的
		
		return 0;
	}
	private int removexPoint(yRangeNode node, int x)
	{
		//在x轴点
		return 0;
	}
	
	/* 为即将插入的数在有序数组中寻找一个位置 */
	private static int findIndex(int[] array, int size, int value)
	{
		int low = 0;   
		int high = size - 1;   
		while (low <= high)
		{   
			int middle = (low + high) >> 1;  
			if (value == array[middle])
				return middle;
			if (value < array[middle])
				high = middle - 1;   
			else 
				low = middle + 1;
		}  
		return low;
	}
	
	/* 获取在二维区间树中与指定的矩形查找范围重叠的所有矩形 */
	public Obstacle[] getObstacles(int xStart, int xEnd, int yStart, int yEnd)
	{
		if(yPointCount == 0){
			return EmptyArray.emptyArray(Obstacle.class);
		}
		IdentityHashMap<Obstacle, Object> ret = obtain();
		jumpToyRange(xStart, xEnd, yStart, yEnd, treeRoot(yPointCount), ret);
		
		if(ret.size() == 0){
			recyle(ret);
			return EmptyArray.emptyArray(Obstacle.class);
		}
		
		Obstacle[] objs = new Obstacle[ret.size()];
		ret.keySet().toArray(objs);
		recyle(ret);
		return objs;
	}
	/* 在y轴区间树中的节点i开始遍历其子节点，搜索与指定范围重叠的所有矩形 */
	private void jumpToyRange(int xStart, int xEnd, int yStart, int yEnd, int i, IdentityHashMap<Obstacle, Object> ret)
	{
		//按扫描线(矩形的边)分隔的区间，其中的物体必然填充整个区间
		//搜索时仅需判断搜索范围是否与区间重叠
		//搜索区间为(0 ~ yPointCount - 1)，但不包含yPointCount - 1
		if((i & 1) != 0){
			//若节点i不是叶子节点，且搜索范围与左区间重叠，遍历左区间
			//(超出yPointCount的节点实际上是上一层i的右子节点，且让我必须找到一个在i之后的节点)
			if(i >= yPointCount || yStart <= yPoints[i]){
				jumpToyRange(xStart, xEnd, yStart, yEnd, leftChild(i), ret);
			}
		}
		if(i < yPointCount - 1){
			//若i在有效节点范围内，且搜索范围与当前区间重叠，则看看自己是否与搜索范围重叠
			//如果节点i的y区间与搜索范围重叠，那么跳至这个节点的x区间去搜索矩形
			if(!(yPoints[i] > yEnd || yPoints[i+1] < yStart)){
				jumpToxRange(xStart, xEnd, yRanges[i], treeRoot(yRanges[i].xPointCount), ret);
			}
			if((i & 1) != 0){
				//若节点i不是叶子节点，且搜索范围与右区间重叠，遍历右区间
				//如果i已经是yPointCount - 2，其实在之前已经将最后一个区间遍历了，不用向右走
				if(i < yPointCount - 2 && yEnd >= yPoints[i+1]){
					jumpToyRange(xStart, xEnd, yStart, yEnd, rightChild(i), ret);
				}
			}
		} 
	}
	/* 在指定的x轴区间树中的节点i开始遍历其子节点，搜索与指定范围重叠的所有矩形 */
	private static void jumpToxRange(int xStart, int xEnd, yRangeNode node, int i, IdentityHashMap<Obstacle, Object> ret)
	{
		//搜索区间为(0 ~ xPointCount - 1)，但不包含xPointCount - 1
		int xPointCount = node.xPointCount;
		int[] xPoints = node.xPoints;
		if((i & 1) != 0){
			//若节点i不是叶子节点，且搜索范围与左区间重叠，遍历左区间
			if(i >= xPointCount || xStart <= xPoints[i]){
				jumpToxRange(xStart, xEnd, node, leftChild(i), ret);
			}
		}
		if(i < xPointCount - 1){
			//如果节点i的x区间与搜索范围重叠，那么获取这个x区间的所有矩形
			if(!(xPoints[i] > xEnd || xPoints[i+1] < xStart)){
				copyObstacles(node, i, ret);
			}
			if((i & 1) != 0){
				//若节点i不是叶子节点，且搜索范围与右区间重叠，遍历右区间
				if(i < xPointCount - 2 && xEnd >= xPoints[i+1]){
					jumpToxRange(xStart, xEnd, node, rightChild(i), ret);
				}
			}
		} 
	}
	/* 复制指定的y轴区间节点(x轴区间树)中的与指定下标的区间重叠的矩形至指定的容器中 */
	private static void copyObstacles(yRangeNode node, int i, IdentityHashMap<Obstacle, Object> ret)
	{
		ObstacleLink link = node.xRangeObjs[i];
		for(;link != null; link = link.next){
			ret.put(link.obj, null);
		}
	}
	
	/* 获取临时矩形容器 */
	private static IdentityHashMap obtain()
	{
		synchronized(sCachedBuffer)
		{
			for(int i = sCachedBuffer.length-1; i >= 0; --i){
				IdentityHashMap buffer = sCachedBuffer[i];
				if(buffer != null){
					sCachedBuffer[i] = null;
					return buffer;
				}
			}
		}
		return new IdentityHashMap();
	}
	/* 回收临时矩形容器 */
	private static void recyle(IdentityHashMap buffer)
	{
		buffer.clear();
		synchronized(sCachedBuffer)
		{
			for(int i = sCachedBuffer.length-1; i >= 0; --i){
				if(sCachedBuffer[i] == null){
					sCachedBuffer[i] = buffer;
					break;
				}
			}
		}
	}
	private static final IdentityHashMap[] sCachedBuffer = new IdentityHashMap[6];

	//获取能容纳i个节点的最小完美二叉树的根节点下标
	private static int treeRoot(int i) {   
        return Integer.highestOneBit(i) - 1;
    }
    //获取下标为i的节点的左子节点在数组中的下标
    private static int leftChild(int i) {
        return i - (((i + 1) & ~i) >> 1);
    }
    //获取下标为i的节点的右子节点在数组中的下标
    private static int rightChild(int i) {
        return i + (((i + 1) & ~i) >> 1);
    }
	
	private int yPointCount; //y轴上的矩形横边的坐标点的个数
	private int[] yPoints;   //y轴上的矩形横边的坐标点
	private int[] yPointOverlap;  //y轴上横边的坐标点相同的矩形数
	private yRangeNode[] yRanges; //与每一个y轴点区间重叠的所有矩形
	
	private static class yRangeNode implements Cloneable
	{
		private int xPointCount; //x轴上的矩形纵边的坐标点的个数
		private int[] xPoints;   //x轴上的矩形纵边的坐标点
		private int[] xPointOverlap;  //x轴上纵边的坐标点相同的矩形数
		private ObstacleLink[] xRangeObjs;//与每一个x轴点区间重叠的所有矩形

		private yRangeNode(){
			xPointCount = 0;
			xPoints = EmptyArray.INT;
			xPointOverlap = EmptyArray.INT;
			xRangeObjs = EmptyArray.emptyArray(ObstacleLink.class);
		}
		@Override
		public yRangeNode clone() 
		{
			yRangeNode copyNode = new yRangeNode();
			copyNode.xPointCount = xPointCount;
			copyNode.xPoints = xPoints.clone();
			copyNode.xRangeObjs = new ObstacleLink[xRangeObjs.length];
			for(int i = 0; i < xPointCount - 1; ++i){
				copyNode.xRangeObjs[i] = xRangeObjs[i].clone();
			}
			return copyNode;
		} 
	}
	
	public static class ObstacleLink implements Cloneable
	{
		public ObstacleLink(Obstacle o, ObstacleLink n){
			obj = o;
			next = n;
		}
		@Override
		public ObstacleLink clone()
		{
			ObstacleLink link = this;
			ObstacleLink copyLink = null;
			for(;link != null; link = link.next){
				copyLink = new ObstacleLink(link.obj, copyLink);
			}
			return copyLink;
		}
		public Obstacle obj;
		public ObstacleLink next;
	}
	
}
