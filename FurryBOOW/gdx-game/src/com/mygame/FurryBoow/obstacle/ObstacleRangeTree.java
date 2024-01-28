package com.mygame.FurryBoow.obstacle;
import com.mygame.FurryBoow.array.*;
import com.mygame.FurryBoow.obstacle.*;
import java.util.*;
import android.graphics.*;

public class ObstacleRangeTree implements ObstacleContainer
{
	public ObstacleRangeTree(){
		mObstacleCount = 0;
	}
	public ObstacleRangeTree(Obstacle[] objs){

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

	/* 向二维区间树中添加一个(被外接矩形包围的)物体 */
	public void addObstacle(Obstacle obj, int xStart, int xEnd, int yStart, int yEnd)
	{
		if(mRectOfObstacle == null){
			mRectOfObstacle = new IdentityHashMap<>();
		}
		Rect rect = mRectOfObstacle.get(obj);
		if(rect == null){
			//如果没有这个矩形物体就添加一个
			mObstacleCount++;
			mRectOfObstacle.put(obj, new Rect(xStart, yStart, xEnd, yEnd));
		}else{
			//否则重新设置物体的矩形范围
			rect.set(xStart, yStart, xEnd, yEnd);
			mTree.removeObstacle(xStart, yStart, xEnd, yEnd, obj);
		}
		mTree.addObstacle(xStart, xEnd, yStart, yEnd, obj);
	}
	
	/* 在二维区间树中移除一个矩形物体 */
	public void removeObstacle(Obstacle obj)
	{
		if(mRectOfObstacle == null){
			return;
		} 
		Rect rect = mRectOfObstacle.remove(obj);
		if(rect != null){
			//如果存在此矩形物体则移除它
			int xStart = rect.left;
			int xEnd = rect.right;
			int yStart = rect.top;
			int yEnd = rect.bottom;
			mTree.removeObstacle(xStart, xEnd, yStart, yEnd, obj);
			mObstacleCount--;
		}
	}

	/* 获取指定物体的矩形范围，成功获取则返回true */
	public boolean getObstacleRect(Obstacle obj ,Rect rect)
	{
		if(mRectOfObstacle != null)
		{
			Rect self = mRectOfObstacle.get(obj);
			if(self != null){
				rect.set(self);
				return true;
			}
		}
		return false;
	}
	/* 获取二维区间树中的物体的个数 */
	public int getObstacleCount(){
		return mObstacleCount;
	}

	/* 获取在二维区间树中与指定的矩形查找范围重叠的所有矩形物体 */
	public Obstacle[] getObstacles(int xStart, int xEnd, int yStart, int yEnd)
	{
		if(mObstacleCount == 0){
			return EmptyArray.emptyArray(Obstacle.class);
		}
		IdentityHashMap<Obstacle, Object> ret = obtain();
		mTree.getObstacles(xStart, xEnd, yStart, yEnd, ret);

		if(ret.size() == 0){
			recyle(ret);
			return EmptyArray.emptyArray(Obstacle.class);
		}

		Obstacle[] objs = new Obstacle[ret.size()];
		ret.keySet().toArray(objs);
		recyle(ret);
		return objs;
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
	/* 复制链表中的矩形物体至指定的容器中 */
	private static void copyObstacles(ObstacleLink link, IdentityHashMap<Obstacle, Object> ret)
	{
		for(;link != null; link = link.next){
			ret.put(link.obj, null);
		}
	}
	private static final IdentityHashMap[] sCachedBuffer = new IdentityHashMap[6];
	
	private yTree mTree; //y轴区间树
	private int mObstacleCount; //容器内物体的个数
	private IdentityHashMap<Obstacle, Rect> mRectOfObstacle; //物体在容器中的矩形范围

	/* 在有序数组中寻找一个点，如果找到了则返回它的位置，否则返回它应该插入的位置 */
	private static int findIndex(int[] points, int pointCount, int point)
	{
		int low = 0;   
		int high = pointCount - 1;   
		while (low <= high)
		{   
			int middle = (low + high) >> 1;  
			if (point == points[middle])
				return middle;
			if (point < points[middle])
				high = middle - 1;   
			else 
				low = middle + 1;
		}  
		return low;
	}
	
	/* y轴区间树，实际上就是有序区间列表，也可以当成树遍历 */
	private static class yTree implements Cloneable
	{
		private int yPointCount; //y轴上的矩形横边的坐标点的个数
		private int[] yPoints;   //y轴上的矩形横边的坐标点
		private int[] yPointOverlap;  //y轴上横边的坐标点相同的矩形数
		private xTree[] yRangeNodes;  //与每一个y轴点区间重叠的所有矩形构成的x轴区间树
		
		private yTree(){
			yPointCount = 0;
			yPoints = EmptyArray.INT;
			yPointOverlap = EmptyArray.INT;
			yRangeNodes = EmptyArray.emptyArray(xTree.class);
		}
		
		/* 为指定的物体在y轴区间列表中分配一个矩形范围 */
		public void addObstacle(int xStart, int xEnd, int yStart, int yEnd, Obstacle obj)
		{
			//将矩形的两横边的纵坐标插入到y轴点区间列表中
			//并遍历两边之间的区间，将矩形的两纵边的横坐标加到这些x轴区间列表中
			int yStartIndex = addPoint(yStart);
			int yEndIndex = addPoint(yEnd);
			for(;yStartIndex < yEndIndex; ++yStartIndex){
				yRangeNodes[yStartIndex].addObstacle(xStart, xEnd, obj);
			}
		}
		/* 在y轴的点区间列表中插入一个点，返回插入的点的位置 */
		private int addPoint(int y)
		{
			int yIndex = findIndex(yPoints, yPointCount, y);
			if(yIndex < yPointCount && y == yPoints[yIndex]){
				//如果已有相同的点就无需再次插入，直接共享此点
				yPointOverlap[yIndex]++;
			}
			else{
				//插入新的点，还需要把其所在区间的内容截为两段(也就是复制区间的xTree)
				//如果插入的点在边界处，那么构建一个空区间(创建一个空xTree)
				GrowingArrayUtils.insert(yPointOverlap, yPointCount, yIndex, 1);
				GrowingArrayUtils.insert(yPoints, yPointCount, yIndex, y);
				xTree node = null;
				if(yIndex == 0 || yIndex == yPointCount){
					node = new xTree();
				}else{
					node = yRangeNodes[yIndex - 1].clone();
				}
				GrowingArrayUtils.insert(yRangeNodes, yPointCount, yIndex, node);
				yPointCount++;
			}
			return yIndex;
		}
		
		/* 移除y轴区间列表中的指定矩形范围的物体 */
		private void removeObstacle(int xStart, int xEnd, int yStart, int yEnd, Obstacle obj)
		{
			int yStartIndex = removePoint(yStart);
			int yEndIndex = removePoint(yEnd);
			for(;yStartIndex < yEndIndex; ++yStartIndex){
				yRangeNodes[yStartIndex].removeObstacle(xStart, xEnd, obj);
			}
		}
		/* 在y轴的点区间列表中移除一个点，返回移除的点的位置 */
		private int removePoint(int y)
		{
			//如果此点只有一个矩形的边的坐标与其重叠，则移除点
			//移除后，直接移除点对应的区间即可，因为此点是无关紧要的
			int yIndex = findIndex(yPoints, yPointCount, y);
			if(--yPointOverlap[yIndex] == 0)
			{
				GrowingArrayUtils.remove(yPoints, yPointCount, yIndex);
				GrowingArrayUtils.remove(yPointOverlap, yPointCount, yIndex);
				GrowingArrayUtils.remove(yRangeNodes, yPointCount, yIndex);
				yPointCount--;
			}
			return yIndex;
		}
		
		/* 获取在y轴区间树中指定矩形范围内的所有物体 */
		public void getObstacles(int xStart, int xEnd, int yStart, int yEnd, IdentityHashMap<Obstacle, Object> ret)
		{
			int yStartIndex = findIndex(yPoints, yPointCount, yStart);
			int yEndIndex = findIndex(yPoints, yPointCount, yEnd);
			for(;yStartIndex < yEndIndex; ++yStartIndex){
				yRangeNodes[yStartIndex].getObstacles(xStart, xEnd, ret);
			}
		}
		
		/* 克隆一个新的区间树(深拷贝) */
		public yTree clone()
		{
			yTree tree = new yTree();
			tree.yPointCount = yPointCount;
			tree.yPoints = yPoints.clone();
			tree.yPointOverlap = yPointOverlap.clone();
			tree.yRangeNodes = new xTree[yRangeNodes.length];
			for(int i = 0; i < yPointCount - 1; ++i){
				tree.yRangeNodes[i] = yRangeNodes[i].clone();
			}
			return tree;
		}
	}
	
	/* x轴区间树，实际上就是有序区间列表，也可以当成树遍历 */
	private static class xTree implements Cloneable
	{
		private int xPointCount; //x轴上的矩形纵边的坐标点的个数
		private int[] xPoints;   //x轴上的矩形纵边的坐标点
		private int[] xPointOverlap;  //x轴上纵边的坐标点相同的矩形数
		private ObstacleLink[] xRangeObjs;//与每一个x轴点区间重叠的所有矩形物体

		private xTree(){
			xPointCount = 0;
			xPoints = EmptyArray.INT;
			xPointOverlap = EmptyArray.INT;
			xRangeObjs = EmptyArray.emptyArray(ObstacleLink.class);
		}
		/* 为指定的物体在x轴点区间列表中分配一个范围 */
		public void addObstacle(int xStart, int xEnd, Obstacle obj)
		{
			//将矩形的两纵边的横坐标插入到这些x轴区间列表中
			//并遍历两边之间的区间，将矩形物体插入到与它重叠的区间中
			int xStartIndex = addPoint(xStart);
			int xEndIndex = addPoint(xEnd);
			for(;xStartIndex < xEndIndex; ++xStartIndex){
				xRangeObjs[xStartIndex] = new ObstacleLink(obj, xRangeObjs[xStartIndex]);
			}
		}
		/* 在x轴的点区间列表中插入一个点，返回插入的点的位置 */
		private int addPoint(int x)
		{
			int xIndex = findIndex(xPoints, xPointCount, x);
			if(xIndex < xPointCount && x == xPoints[xIndex]){
				xPointOverlap[xIndex]++;
			}
			else{
				GrowingArrayUtils.insert(xPointOverlap, xPointCount, xIndex, 1);
				GrowingArrayUtils.insert(xPoints, xPointCount, xIndex, x);
				ObstacleLink link = null;
				if(xIndex == 0 || xIndex == xPointCount){
					link = new ObstacleLink(null, null);
				}else{
					link = xRangeObjs[xIndex - 1].clone();
				}
				GrowingArrayUtils.insert(xRangeObjs, xPointCount, xIndex, link);
				xPointCount++;
			}
			return xIndex;
		}
		
		/* 移除x轴点区间列表中的指定范围的物体 */
		private void removeObstacle(int xStart, int xEnd, Obstacle obj)
		{
			int xStartIndex = removePoint(xStart);
			int xEndIndex = removePoint(xEnd);
			for(;xStartIndex < xEndIndex; ++xStartIndex)
			{
				ObstacleLink link = xRangeObjs[xStartIndex];
				if(link.obj == obj){
					link = link.next;
					continue;
				}
				for(;link.next != null; link = link.next){
					if(link.next.obj == obj){
						link.next = link.next.next;
						break;
					}
				}
			}
		}
		/* 在x轴的点区间列表中移除一个点，返回移除的点的位置 */
		private int removePoint(int x)
		{
			int xIndex = findIndex(xPoints, xPointCount, x);
			if(--xPointOverlap[xIndex] == 0){
				GrowingArrayUtils.remove(xPoints, xPointCount, xIndex);
				GrowingArrayUtils.remove(xPointOverlap, xPointCount, xIndex);
				GrowingArrayUtils.remove(xRangeObjs, xPointCount, xIndex);
				xPointCount--;
			}
			return xIndex;
		}
		
		/* 获取在x轴区间树中指定范围内的所有物体 */
		public void getObstacles(int xStart, int xEnd, IdentityHashMap<Obstacle, Object> ret)
		{
			int xStartIndex = findIndex(xPoints, xPointCount, xStart);
			int xEndIndex = findIndex(xPoints, xPointCount, xEnd);
			for(;xStartIndex < xEndIndex; ++xStartIndex){
				ObstacleLink link = xRangeObjs[xStartIndex];
				copyObstacles(link, ret);
			}
		}
		
		/* 克隆一个新的区间树(深拷贝) */
		public xTree clone() 
		{
			xTree tree = new xTree();
			tree.xPointCount = xPointCount;
			tree.xPoints = xPoints.clone();
			tree.xPointOverlap = xPointOverlap.clone();
			tree.xRangeObjs = new ObstacleLink[xRangeObjs.length];
			for(int i = 0; i < xPointCount - 1; ++i){
				tree.xRangeObjs[i] = xRangeObjs[i].clone();
			}
			return tree;
		} 
	}

	/* 物体单链表，无序快速增删 */
	public static class ObstacleLink implements Cloneable
	{
		public ObstacleLink(Obstacle o, ObstacleLink n){
			obj = o;
			next = n;
		}
		/* 克隆一个新的单链表(深拷贝) */
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
		
		/* 在链表头部添加一个物体，返回头指针 */
		public static ObstacleLink add(ObstacleLink link, Obstacle obj){
			return new ObstacleLink(obj, link);
		}
		/* 移除链表中的指定物体，返回头指针 */
		public static ObstacleLink remove(ObstacleLink link, Obstacle obj)
		{
			if(link == null){
				return null;
			}
			if(link.obj == obj){
				return link.next;
			}
			ObstacleLink head = link;
			for(;link.next != null; link = link.next){
				if(link.next.obj == obj){
					link.next = link.next.next;
					break;
				}
			}
			return head;
		}
	}
	
}
