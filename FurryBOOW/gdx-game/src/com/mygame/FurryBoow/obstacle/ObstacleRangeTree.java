package com.mygame.FurryBoow.obstacle;

import com.mygame.FurryBoow.array.*;
import com.mygame.FurryBoow.obstacle.*;
import java.util.*;
import android.graphics.*;

/* 用于存储和快速获取矩形物体的二维区间树 */
public class ObstacleRangeTree implements ObstacleContainer
{
	public ObstacleRangeTree(){
		mObstacleCount = 0;
	}
	public ObstacleRangeTree(Obstacle[] objs, int[] xStarts, int[] xEnds, int[] yStarts, int[] yEnds)
	{
		if(objs.length == 0){
			return;
		}
		mObstacleCount = objs.length;
		mRectOfObstacle = new IdentityHashMap<>(mObstacleCount * 2);
		for(int i = 0; i < mObstacleCount; ++i){
			mRectOfObstacle.put(objs[i], new Rect(xStarts[i], yStarts[i], xEnds[i], yEnds[i]));
		}
		mTree = new yTree(objs, xStarts, xEnds, yStarts, yEnds);
	}
	
	/* 向二维区间树中添加一个(被外接矩形包围的)物体 */
	public void addObstacle(Obstacle obj, int xStart, int xEnd, int yStart, int yEnd)
	{
		if(mRectOfObstacle == null){
			mTree = new yTree();
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
			mTree.removeObstacle(xStart, xEnd, yStart, yEnd, obj);
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
	private static final IdentityHashMap[] sCachedBuffer = new IdentityHashMap[6];
	
	//用一条扫描线从上往下扫，每遇到一个矩形的横边就将这条边的纵坐标记录下来
	//用这个方法将所有矩形横边的纵坐标投影到y轴上，用这些有序坐标点构建一个点数组
	//每两y点之间构成一个区间，区间内的数据存储在左边的点的下标处，有效区间数比点数少1，为了方便，将最后一个点置为空区间
	
	//每一个y轴上的区间中，有一个x轴的点区间列表(存储与此y轴区间重叠的所有矩形的竖边的横坐标)
	//同上面的方法一样，用一条扫描线把这些(与此y轴区间重叠的所有)矩形的竖边扫过，构建一个有序点数组
	//每两x点之间构成一个区间，每个区间中存储与此x区间重叠的矩形物体
	
	//由于这些点总是顺序排列，所以可以使用二分搜索法来夹逼区间
	//利用y轴上的点，可轻易夹逼出矩形所在的所有y区间，再用x轴上的点夹逼了所有y→x区间
	//这样就实现了局部搜索，只遍历与搜索范围重叠的区间，其它的区间被直接舍弃
	//另外，按扫描线(矩形的边)分隔的区间，其中的物体必然填充整个区间
	//因此搜索时仅需判断搜索范围是否与区间重叠，如果是则直接获取x区间内的所有物体
	
	private yTree mTree; //y轴区间树
	private int mObstacleCount; //容器内物体的个数
	private IdentityHashMap<Obstacle, Rect> mRectOfObstacle; //物体在容器中的矩形范围

	
	/* y轴区间树，实际上就是有序区间列表，也可以当成树遍历 */
	private static class yTree implements Cloneable
	{
		private int yPointCount; //y轴上的矩形横边的坐标点的个数
		private int[] yPoints;   //y轴上的矩形横边的坐标点
		private int[] yPointOverlap;  //y轴上横边的坐标点相同的矩形数
		private xTree[] yRangeNodes;  //与每一个y轴点区间重叠的所有矩形构成的x轴区间树
		
		public yTree(){
			yPointCount = 0;
			yPoints = EmptyArray.INT;
			yPointOverlap = EmptyArray.INT;
			yRangeNodes = EmptyArray.emptyArray(xTree.class);
		}
		/* 用许多矩形物体快速构建一颗y轴区间树 */
		public yTree(Obstacle[] objs, int[] xStarts, int[] xEnds, int[] yStarts, int[] yEnds)
		{
			//创建一个更大的数组来存储点
			int objSize = objs.length;
			int growSize = GrowingArrayUtils.growSize(objSize * 2);
			yPoints = new int[growSize];
			yPointOverlap = new int[growSize];
			yRangeNodes = new xTree[growSize];
			//用yStarts和yEnds的点填充yPoints
			System.arraycopy(yStarts, 0, yPoints, 0, objSize);
			System.arraycopy(yEnds, 0, yPoints, objSize, objSize);
			//用指定的点构建区间列表，并记录每个点的重复次数
			yPointCount = checkPoints(yPoints, yPointOverlap, objSize * 2);
			
			//统计与每个区间重叠的物体在原数组中的下标，以及它们的个数
			int[] countPoints = countPoints(yPoints, yPointCount, yStarts, yEnds, objSize);
			int[][] rowTable = makeTable(yPoints, countPoints, yPointCount, yStarts, yEnds, objSize);
			//遍历每个区间，截取处于区间中的物体，并用这些物体构建一颗x轴区间树
			for(int i = 0; i < yPointCount; ++i)
			{
				//根据区间内物体的个数创建数组
				int[] objIndexs = rowTable[i];
				int length = countPoints[i];
				Obstacle[] subxObjs = new Obstacle[length];
				int[] subxStarts = new int[length];
				int[] subxEnds = new int[length];
				//遍历与区间重叠的物体，填充数组后传递给xTree
				for(int j = 0; j < length; ++j){
					int index = objIndexs[j];
					subxObjs[j] = objs[index];
					subxStarts[j] = xStarts[index];
					subxEnds[j] = xEnds[index];
				}
				yRangeNodes[i] = new xTree(subxObjs, subxStarts, subxEnds);
			}
		}
		
		/* 为指定的物体在y轴区间列表中分配一个矩形范围 */
		public void addObstacle(int xStart, int xEnd, int yStart, int yEnd, Obstacle obj)
		{
			//将矩形的两横边的纵坐标插入到y轴点区间列表中
			//并遍历两边之间的区间，将矩形的两纵边的横坐标加到这些x轴区间列表中
			int yStartIndex = addPoint(yStart, 0);
			int yEndIndex = addPoint(yEnd, yStartIndex);
			for(;yStartIndex < yEndIndex; ++yStartIndex){
				yRangeNodes[yStartIndex].addObstacle(xStart, xEnd, obj);
			}
		}
		/* 在y轴的点区间列表中插入一个点，返回插入的点的位置 */
		private int addPoint(int y, int yStartIndex)
		{
			int yIndex = findIndex(yPoints, yStartIndex, yPointCount - 1, y);
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
		public void removeObstacle(int xStart, int xEnd, int yStart, int yEnd, Obstacle obj)
		{
			int yStartIndex = removePoint(yStart, 0);
			int yEndIndex = removePoint(yEnd, yStartIndex);
			for(;yStartIndex < yEndIndex; ++yStartIndex){
				yRangeNodes[yStartIndex].removeObstacle(xStart, xEnd, obj);
			}
		}
		/* 在y轴的点区间列表中移除一个点，返回移除的点的位置 */
		private int removePoint(int y, int yStartIndex)
		{
			//如果此点只有一个矩形的边的坐标与其重叠，则移除点
			//移除后，直接移除点对应的区间即可，因为此点是无关紧要的
			int yIndex = findIndex(yPoints, yStartIndex, yPointCount - 1, y);
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
			//按扫描线(矩形的边)分隔的区间，其中的物体必然填充整个区间
			//搜索时仅需判断搜索范围是否与区间重叠
			int yStartIndex = findIndex(yPoints, 0, yPointCount - 1, yStart);
			int yEndIndex = findIndex(yPoints, yStartIndex, yPointCount - 1, yEnd);
			if(yStartIndex > 0 && yStartIndex < yPointCount && yStart < yPoints[yStartIndex]){
				//包含左区间，但不包含右区间
				yStartIndex--;
			}
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
			for(int i = 0; i < yPointCount; ++i){
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

		public xTree(){
			xPointCount = 0;
			xPoints = EmptyArray.INT;
			xPointOverlap = EmptyArray.INT;
			xRangeObjs = EmptyArray.emptyArray(ObstacleLink.class);
		}
		/* 用许多矩形物体快速构建一颗x轴区间树 */
		public xTree(Obstacle[] objs, int[] xStarts, int[] xEnds)
		{
			int objSize = objs.length;
			int growSize = GrowingArrayUtils.growSize(objSize * 2);
			xPoints = new int[growSize];
			xPointOverlap = new int[growSize];
			xRangeObjs = new ObstacleLink[growSize];
			System.arraycopy(xStarts, 0, xPoints, 0, objSize);
			System.arraycopy(xEnds, 0, xPoints, objSize, objSize);
			xPointCount = checkPoints(xPoints, xPointOverlap, objSize * 2);

			int[] countPoints = countPoints(xPoints, xPointCount, xStarts, xEnds, objSize);
			int[][] colTable = makeTable(xPoints, countPoints, xPointCount, xStarts, xEnds, objSize);
			for(int i = 0; i < xPointCount; ++i)
			{
				int[] objIndexs = colTable[i];
				int length = countPoints[i];
				ObstacleLink link = null;
				for(int j = 0; j < length; ++j){
					int index = objIndexs[j];
					link = new ObstacleLink(objs[index], link);
				}
				xRangeObjs[i] = link;
			}
		}
		
		/* 为指定的物体在x轴点区间列表中分配一个范围 */
		public void addObstacle(int xStart, int xEnd, Obstacle obj)
		{
			//将矩形的两纵边的横坐标插入到这些x轴区间列表中
			//并遍历两边之间的区间，将矩形物体插入到与它重叠的区间中
			int xStartIndex = addPoint(xStart, 0);
			int xEndIndex = addPoint(xEnd, xStartIndex);
			for(;xStartIndex < xEndIndex; ++xStartIndex){
				xRangeObjs[xStartIndex] = new ObstacleLink(obj, xRangeObjs[xStartIndex]);
			}
		}
		/* 在x轴的点区间列表中插入一个点，返回插入的点的位置 */
		private int addPoint(int x, int xStartIndex)
		{
			int xIndex = findIndex(xPoints, xStartIndex, xPointCount - 1, x);
			if(xIndex < xPointCount && x == xPoints[xIndex]){
				xPointOverlap[xIndex]++;
			}
			else{
				GrowingArrayUtils.insert(xPointOverlap, xPointCount, xIndex, 1);
				GrowingArrayUtils.insert(xPoints, xPointCount, xIndex, x);
				ObstacleLink link = null;
				if(xIndex == 0 || xIndex == xPointCount){
					link = null;
				}else{
					link = xRangeObjs[xIndex - 1] != null ? xRangeObjs[xIndex - 1].clone() : null;
				}
				GrowingArrayUtils.insert(xRangeObjs, xPointCount, xIndex, link);
				xPointCount++;
			}
			return xIndex;
		}
		
		/* 移除x轴点区间列表中的指定范围的物体 */
		public void removeObstacle(int xStart, int xEnd, Obstacle obj)
		{
			int xStartIndex = removePoint(xStart,0);
			int xEndIndex = removePoint(xEnd, xStartIndex);
			for(;xStartIndex < xEndIndex; ++xStartIndex){
				xRangeObjs[xStartIndex] = ObstacleLink.removeLink(xRangeObjs[xStartIndex], obj);
			}
		}
		/* 在x轴的点区间列表中移除一个点，返回移除的点的位置 */
		private int removePoint(int x, int xStartIndex)
		{
			int xIndex = findIndex(xPoints, xStartIndex, xPointCount - 1, x);
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
			int xStartIndex = findIndex(xPoints, 0, xPointCount - 1, xStart);
			int xEndIndex = findIndex(xPoints, xStartIndex, xPointCount - 1, xEnd);
			if(xStartIndex > 0 && xStartIndex < xPointCount && xStart < xPoints[xStartIndex]){
				xStartIndex--;
			}
			for(;xStartIndex < xEndIndex; ++xStartIndex){
				ObstacleLink link = xRangeObjs[xStartIndex];
				for(;link != null; link = link.next){
					ret.put(link.obj, null);
				}
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
			for(int i = 0; i < xPointCount; ++i){
				if(xRangeObjs[i] != null){
					tree.xRangeObjs[i] = xRangeObjs[i].clone();
				}
			}
			return tree;
		} 
	}
	
	/* 用指定的点构建区间列表，返回区间列表的长度，并记录每个点的重复次数 */
	private static int checkPoints(int[] points, int[] pointOverlap, int pointCount)
	{
		//对points排序并去重，便构建了一个区间列表
		Arrays.sort(points, 0, pointCount);
		int remaining = pointCount;
		int prevHead = 1, prevTail = 1;
		pointOverlap[0] = 1;
		//首先，我们知道第一个元素必然重复1次
		//然后，prevHead总共可以再遍历(n - 1)次，所以刚好每个点都有一次机会(n次)
		for(;prevHead < pointCount;)
		{
			for(;points[prevHead] == points[prevHead - 1]; ++prevHead){
				//每有一个重复的元素，剩余元素个数减1，且上个点的重复个数加1
				//prevTail指向的是即将放入新元素的位置，因此只需要将旧元素(prevTail - 1)重复个数加1
				remaining--;
				++pointOverlap[prevTail - 1];
				if(prevHead == pointCount - 1){
					//这时，最后一次在此处执行
					return remaining;
				} 
			}
			//头找到一个不重复的元素，然后递给尾巴
			//不重复的元素被放入prevTail，因此该下标的点重复次数为1
			++pointOverlap[prevTail];
			points[prevTail++] = points[prevHead++];
		}
		return remaining;
	}

	/* 统计与每个区间重叠的元素个数，返回如下的内容
	 * [与区间1重叠的元素个数，与与区间2重叠的元素个数，与区间3重叠的元素个数，...]
	 *
	 * 最坏情况下，objSize = n，pointCount = 2n，单独来看
	 * 此函数搜索的时间复杂度为O(2n * log(2n))，此函数数的填充时间为O(2n²)
	 * 考虑到区间重叠，因此两者综合后的时间绝对远小于它们各自的最坏情况
	 * 如果每个物体范围较小，或区间数较少，则能够进一步提升速度
	 * 相比于直接行行比较的时间0(2n²)，此函数的平均时间最少比它快一倍甚至更多
	*/
	private static int[] countPoints(int[] points, int pointCount, int[] starts, int[] ends, int objSize)
	{
		//遍历每个物体，二分搜索与它的重叠点区间，并将这些区间的元素个数加1
		int[] countPoints = new int[pointCount];
		for(int i = 0; i < objSize; ++i)
		{
			int fromIndex = findIndex(points, 0, pointCount - 1, starts[i]);
			int toIndex = findIndex(points, fromIndex, pointCount - 1, ends[i]);
			for(;fromIndex < toIndex; ++fromIndex){
				countPoints[fromIndex]++;
			}
		}
		return countPoints;
	}

	/* 统计与每个区间重叠的的元素在数组中的下标，并保存在表中，表中内容如下:
	   [与区间1重叠的元素在starts中的下标: [下标1，下标2，下标3，...]，
	    与区间2重叠的元素在starts中的下标: [下标1，下标2，下标3，...]，
		与区间3重叠的元素在starts中的下标: [下标1，下标2，下标3，...]，...
	   ]
	*/
	private static int[][] makeTable(int[] points, int[] countPoints, int pointCount, int[] starts, int[] ends, int objSize)
	{
		//用countPoints中记录的个数创建每个区间的表
		int[][] table = new int[pointCount][];
		for(int i = 0; i < pointCount; ++i){
			table[i] = new int[countPoints[i]];
			countPoints[i] = 0;
		}
		//遍历每个物体，二分搜索与它的重叠点区间，并将元素(的下标)放入这些区间表中
		for(int i = 0; i < objSize; ++i)
		{
			int fromIndex = findIndex(points, 0, pointCount - 1, starts[i]);
			int toIndex = findIndex(points, fromIndex, pointCount - 1, ends[i]);
			for(;fromIndex < toIndex; ++fromIndex){
				table[fromIndex][countPoints[fromIndex]++] = i;
			}
		}
		return table;
	}

	/* 在有序数组中寻找一个点，如果找到了则返回它的位置，否则返回它应该插入的位置 */
	private static int findIndex(int[] points, int low, int high, int point)
	{
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
		public static ObstacleLink addLink(ObstacleLink link, Obstacle obj){
			return new ObstacleLink(obj, link);
		}
		/* 移除链表中的指定物体，返回头指针 */
		public static ObstacleLink removeLink(ObstacleLink link, Obstacle obj)
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
