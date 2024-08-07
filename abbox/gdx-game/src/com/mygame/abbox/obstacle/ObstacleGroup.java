package com.mygame.abbox.obstacle;

import java.util.*;
import android.graphics.Rect;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygame.abbox.obstacle.shape.*;
import com.mygame.abbox.obstacle.ObstacleList.ObstacleListIterator;
import com.mygame.abbox.share.input.onTouchMove;
import com.mygame.abbox.share.utils.IdentityArrayList;

/* 用于存储物体，并高效管理一组物体的碰撞，绘制和事件的类 */
public class ObstacleGroup extends onTouchMove implements ObstacleContainer
{
	/* 创建指定大小的物体组 */
	public ObstacleGroup(int width, int height, int displayw, int displayh)
	{
		mContentWidth = width;
		mContentHeight = height;
		mDisplayWidth = displayw;
		mDisplayHeight = displayh;
		mScrollX = mScrollY = 0;
		
		mStaticObstacles = new ObstacleRegionTree();
		mDynamicObstacles = new ObstacleList();
		onForeachDynamicObstacles = false;
	}

	/* 添加一个物体到组中 */
	public void addObstacle(Obstacle obj)
	{
		int oldSize = getObstacleCount();
		//动态物体添加到动态物体容器中，静态物体添加到静态物体容器中
		if(obj instanceof DynamicObject)
		{
			if(onForeachDynamicObstacles){
				//在遍历时，使用iterator修改容器，同步下标
				modifyIterator.add(obj);
			}else{
				mDynamicObstacles.addObstacle(obj);
			}
		}
		else{
			mStaticObstacles.addObstacle(obj);
		}
		
		int newSize = getObstacleCount();
		if(oldSize < newSize){
			//如果添加成功，则让物体建立与该组的绑定
			obj.mObstacleGroup = this;
			if(mObstacleWatcher != null){
				mObstacleWatcher.onObstacleAdded(obj);
			}
		}
	}

	/* 从组中移除一个物体 */
	public void removeObstacle(Obstacle obj)
	{
		int oldSize = getObstacleCount();
		//动态物体从动态物体容器中移除，静态物体从静态物体容器中移除
		if(obj instanceof DynamicObject)
		{
			if(onForeachDynamicObstacles){
				//在遍历时，使用iterator修改容器，同步下标
				modifyIterator.remove(obj);
			}else{
				mDynamicObstacles.removeObstacle(obj);
			}	
		}
		else{
			mStaticObstacles.removeObstacle(obj);
		}
		
		int newSize = getObstacleCount();
		if(oldSize > newSize){
			//如果移除成功，则在发送事件后移除物体与该组的绑定
			if(mObstacleWatcher != null){
				mObstacleWatcher.onObstacleRemoved(obj);
			}
			obj.mObstacleGroup = null;
		}
	}

	/* 获取与指定矩形区域重叠的所有物体，获取到指定的集合中 */
	public void getObstacles(int left, int top, int right, int bottom, Collection<Obstacle> ret){
		mStaticObstacles.getObstacles(left, top, right, bottom, ret);
		mDynamicObstacles.getObstacles(left, top, right, bottom, ret);
	}
	/* 获取与指定矩形区域重叠的所有物体，返回这些物体的列表 */
	public List<Obstacle> getObstacles(int left, int top, int right, int bottom)
	{
		if(getObstacleCount() == 0){
			return Collections.emptyList();
		}
		ArrayList<Obstacle> ret = new ArrayList<>();
		getObstacles(left, top, right, bottom, ret);
		return ret;
	}

	/* 获取组中的物体个数 */
	public int getObstacleCount(){
		return mStaticObstacles.getObstacleCount() + mDynamicObstacles.getObstacleCount();
	}
	/* 组中是否包含该物体 */
	public boolean contains(Obstacle obj){
		return mStaticObstacles.contains(obj) || mDynamicObstacles.contains(obj);
	}

	/* 清空组中的物体 */
	public void clear()
	{
		//获取所有物体并一个个移除，在removeObstacle中处理各种情况
		List<Obstacle> objs = getObstacles(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
		for(Obstacle obj : objs){
			removeObstacle(obj);
		}
	}

	/* 设置物体碰撞回调，并启用碰撞检测 */
	public void setCollisionCallback(CollisionCallback callback){
		mCollisionCallback = callback;
	}
	/* 设置物体增删监视器 */
	public void setObstacleWatcher(ObstacleWatcher watcher){
		mObstacleWatcher = watcher;
	}
	
	public int getWidth(){
		return mContentWidth;
	}
	public int getHeight(){
		return mContentHeight;
	}
	public int getScrollX(){
		return mScrollX;
	}
	public int getScrollY(){
		return mScrollY;
	}
	public int getDisplayWidth(){
		return mDisplayWidth;
	}
	public int getDisplayHeight(){
		return mDisplayHeight;
	}

	/* 滚动场景的画布 */
	public void scrollTo(int sx, int sy){
		mScrollX = calcScroll(sx, mContentWidth, mDisplayWidth);
		mScrollY = calcScroll(sy, mContentHeight, mDisplayHeight);
	}
	public void scrollBy(int x, int y){
		scrollTo(mScrollX + x, mScrollY + y);
	}
	//计算滚动值，不允许滑出范围外
	private int calcScroll(int scroll, int content, int display){
		//如果显示宽度大于内容宽度，则只能滚动到0
		//如果滚动到的位置小于内容左边(0)，则只能滚动到内容左边(0)
		//如果要滚动到的位置加上显示宽度后，超出了内容的右边，则只能滚动到(content - display)
		return (display >= content || scroll <= 0) ? 0 :
			(scroll + display > content ? content - display : scroll);
	}

	/* 绘制可视范围内的所有物体 */
	public void draw(ShapeRenderer render)
	{
		//绘制前先设置平移矩阵，在之后绘制时将所有物体偏移到屏幕区域
		Matrix4 matrix = render.getTransformMatrix();
		matrix.translate(-mScrollX, -mScrollY, 0);
		render.setTransformMatrix(matrix);
		IdentityArrayList<Obstacle> visibleObstacles = obtain();
		//获取可视范围内的所有物体，一个个绘制在它们的位置上
		getObstacles(mScrollX, mScrollY, mScrollX + mDisplayWidth, mScrollY + mDisplayHeight, visibleObstacles);
		int count = visibleObstacles.size();
		for(int i = 0; i < count; i++){
			visibleObstacles.get(i).draw(render);
		}
		recyle(visibleObstacles);
		//绘制后将矩阵还原
		matrix.translate(mScrollX, mScrollY, 0);
		render.setTransformMatrix(matrix);
	}

	/* 更新场景内物体的位置，并检查物体的碰撞 */
	public void update()
	{
		onForeachDynamicObstacles = true; 
		//使用迭代器安全地遍历物体，在回调中允许使用迭代器修改容器
		modifyIterator = mDynamicObstacles.listIerator();
		while(modifyIterator.hasNext()){
			((DynamicObject)modifyIterator.next()).update();
		}
		if(mCollisionCallback != null){
			checkCollision();
		}
		onForeachDynamicObstacles = false;
		
		//跟踪拥有焦点的物体
		if(mFocusObstacle != null){
			Rect bounds = mFocusObstacle.getShape().getBounds();
			int x = bounds.centerX();
			int y = bounds.centerY();
			scrollTo(x - mDisplayWidth / 2, y - mDisplayHeight / 2);
		}
	}

	/* 检查物体的碰撞 */
	private void checkCollision()
	{
		if(mCollisionedObstaclePairs == null){
			mCollisionedObstaclePairs = new IdentityHashMap<>();
		}
		
		IdentityArrayList<Obstacle> overlappingObstacles = obtain();
		//遍历每个动态物体，检查它与场景内的物体碰撞
		modifyIterator = mDynamicObstacles.listIerator();
		while(modifyIterator.hasNext())
		{
			Obstacle target = modifyIterator.next();
			Rect rect = target.getShape().getBounds();
			//获取与该物体矩形区域重叠的所有物体
			getObstacles(rect.left, rect.top, rect.right, rect.bottom, overlappingObstacles);
			int count = overlappingObstacles.size();
			for(int k = 0; k < count; ++k)
			{
				Obstacle other = overlappingObstacles.get(k);
				//将每对碰撞的物体传给mCollisionCallback操作，但不包含自己与自己的碰撞
				if(target != other && hasCollision(target, other))
				{
					if(other instanceof DynamicObject){
						//两个动态物体碰撞在一起了，先检查它们是否已经碰撞过一次
						if(!isCollisioned(target, other)){
							//如果已经碰撞过一次则不再碰撞，否则需要记录下来，因为下次它们可能重复碰撞
							mCollisionCallback.onCollision(target, other);
							addCollisionedObstacle(target, other);
						}
					}
					else{
						mCollisionCallback.onCollision(target, other);
					}
				}
			}
			//清空重叠物体列表
			overlappingObstacles.clear();
		}
		
		recyle(overlappingObstacles);
		clearCollisionedObstaclePairs(); //清空本轮碰撞记录
	}
	
	/* 再次检查真实形状是否发生碰撞 */
	private boolean hasCollision(Obstacle o1, Obstacle o2)
	{
		Shape s1 = o1.getShape();
		Shape s2 = o2.getShape();
		if(s1 instanceof CircleShape && s2 instanceof CircleShape){
			return ShapeCollisionChecker.circle_circle((CircleShape)s1, (CircleShape)s2);
		}
		if(s1 instanceof CircleShape && s2 instanceof RectShape){
			return ShapeCollisionChecker.rect_circle((RectShape)s2, (CircleShape)s1);
		}
		if(s1 instanceof RectShape && s2 instanceof CircleShape){
			return ShapeCollisionChecker.rect_circle((RectShape)s1, (CircleShape)s2);
		}
		return true; //rect_rect
	}
	
	/* 检查两个物体在本轮碰撞检测中是否已经碰撞过一次 */
	private boolean isCollisioned(Obstacle target, Obstacle other)
	{
		//检测到target和other碰撞了，但是other和target以前是否碰撞过一次呢
		IdentityArrayList<Obstacle> collisionedObstaclelist = mCollisionedObstaclePairs.get(other);
		if(collisionedObstaclelist == null){
			//other还没有进行碰撞检测
			return false;
		}
		//如果在other的已碰撞物体列表中找到了target，则它们已经发生过一次碰撞
		return collisionedObstaclelist.contains(target);
	}
	
	/* 向mCollisionedObstaclePairs中添加一对碰撞的物体 */
	private void addCollisionedObstacle(Obstacle target, Obstacle other)
	{
		//获取target的已碰撞物体列表，将other添加到target的已碰撞物体列表中
		IdentityArrayList<Obstacle> collisionedObstaclelist = mCollisionedObstaclePairs.get(target);
		if(collisionedObstaclelist == null){
			//如果列表没有就创建一个，并让target与列表建立绑定
			collisionedObstaclelist = obtain();
			mCollisionedObstaclePairs.put(target, collisionedObstaclelist);
		}
		collisionedObstaclelist.add(other);
	}
	
	/* 清空本轮碰撞记录，并回收临时物体列表 */
	private void clearCollisionedObstaclePairs()
	{
		for(IdentityArrayList<Obstacle> collisionedObstaclelist : mCollisionedObstaclePairs.values()){
			recyle(collisionedObstaclelist);
		}
		mCollisionedObstaclePairs.clear();
	}
	
	/* 获取临时物体容器 */
	private static IdentityArrayList<Obstacle> obtain()
	{
		synchronized(sCachedBuffer)
		{
			if(sCachedBufferCount > 0){
				IdentityArrayList<Obstacle> buffer = sCachedBuffer[--sCachedBufferCount];
				sCachedBuffer[sCachedBufferCount] = null;
				return buffer;
			}
		}
		return new IdentityArrayList<Obstacle>();
	}
	/* 回收临时物体容器 */
	private static void recyle(IdentityArrayList<Obstacle> buffer)
	{
		buffer.clear();
		synchronized(sCachedBuffer)
		{
			if(sCachedBufferCount < sCachedBuffer.length){
				sCachedBuffer[sCachedBufferCount++] = buffer;
			}
		}
	}

	private int mContentWidth, mContentHeight;    //内容大小
	private int mDisplayWidth, mDisplayHeight;    //显示大小
	private int mScrollX, mScrollY;               //内容滚到的位置

	private ObstacleRegionTree mStaticObstacles;  //位置静态不变的物体用树存储，提升获取效率
	private ObstacleList mDynamicObstacles;       //位置动态改变的物体用列表存储，不需频繁调整
	private ObstacleWatcher mObstacleWatcher;     //物体监视器

	private boolean onForeachDynamicObstacles;    //是否正在遍历DynamicObstacles物体容器
	private ObstacleListIterator modifyIterator;  //在遍历DynamicObstacles时修改要用iterator，在同步数据的同时安全遍历
	
	private CollisionCallback mCollisionCallback; //碰撞检测回调器
	private IdentityHashMap<Obstacle, IdentityArrayList<Obstacle>> mCollisionedObstaclePairs; //在每次碰撞检测中，存储每一对已经碰撞的物体
	
	private static int sCachedBufferCount = 0;    //临时物体容器个数
	private static final IdentityArrayList<Obstacle>[] sCachedBuffer = new IdentityArrayList[50]; //存储临时物体容器
	
	private TouchTarget mTouchTarget;       //记录所有被触摸的物体
	Obstacle mFocusObstacle;                //要跟踪的物体，键分配给的物体
	
	/* 处理触摸事件 */
	public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button)
	{
		//手指触摸到内容的位置需要加上滚动
		int contentX = (int) screenX + mScrollX;
		int contentY = (int) screenY + mScrollY;
		IdentityArrayList<Obstacle> containsPointObstacles = obtain();
		//获取包含该点的所有物体，一个个遍历
		getObstacles(contentX, contentY, contentX, contentY, containsPointObstacles);
		int count = containsPointObstacles.size();
		for(int i = 0; i < count; ++i)
		{
			Obstacle obj = containsPointObstacles.get(i);
			if(obj.getInputDuration() <= 0 && obj.getShape().contains(contentX, contentY) && obj.touchDown(event, contentX, contentY, pointer, button)){
				//如果物体可以接收事件，并且它的真实形状包含此点则将事件传给它，如果它消耗了事件则记录它并返回true
				mTouchTarget = TouchTarget.addPointer(mTouchTarget, obj, pointer);
				recyle(containsPointObstacles);
				return true;
			}
		}
		recyle(containsPointObstacles);
		//没有物体被触摸或消耗事件，则自己消耗事件
		if(mFocusObstacle != null){
			//如果正在跟踪焦点物体，则不滚动
			return false;
		}
		//在滚动时，需要使用的是手指落在ObstacleGroup上的坐标
		return super.touchDown(event, screenX, screenY, pointer, button);
	}
	
	public void touchDragged(InputEvent event, float screenX, float screenY, int pointer)
	{
		int contentX = (int) screenX + mScrollX;
		int contentY = (int) screenY + mScrollY;
		//从记录的被触摸物体中寻找一个消耗该手指事件的物体
		Obstacle input = TouchTarget.findInputById(mTouchTarget, pointer);
		if(input != null){
			//如果找到了则传递事件，并让它继续消耗事件
			if(input.getInputDuration() <= 0){
				input.touchDragged(event, contentX, contentY, pointer);
			}
			return ;
		}
		//没有物体消耗事件，则看自己能不能消耗事件
		super.touchDragged(event, screenX, screenY, pointer);
	}
	
	public void touchUp(InputEvent event, float screenX, float screenY, int pointer, int button)
	{
		int contentX = (int) screenX + mScrollX;
		int contentY = (int) screenY + mScrollY;
		//从记录的被触摸物体中寻找一个消耗该手指事件的物体
		Obstacle input = TouchTarget.findInputById(mTouchTarget, pointer);
		if(input != null){
			//如果找到了则传递事件，在touchUp时必须移除该物体的该手指
			if(input.getInputDuration() <= 0){
				input.touchUp(event, contentX, contentY, pointer, button);
			}
			TouchTarget.removePointer(mTouchTarget, pointer);
			return ;
		}
		//没有物体消耗事件，则看自己能不能消耗事件
		super.touchUp(event, screenX, screenY, pointer, button);
	}

	/* 当手指在空白处移动，滚动自己的内容 */
	public void move(InputEvent event, int dx, int dy, int orginDx, int orginDy){
		scrollBy(-dx, -dy);
	}

	/* 处理键事件 */
	public boolean keyDown(InputEvent event, int key)
	{
		if(mFocusObstacle != null && mFocusObstacle.getInputDuration() <= 0){
			return mFocusObstacle.keyDown(event, key);
		}
		return false;
	}
	public boolean keyUp(InputEvent event, int key)
	{
		if(mFocusObstacle != null && mFocusObstacle.getInputDuration() <= 0){
			return mFocusObstacle.keyUp(event, key);
		}
		return false;
	}
	public boolean keyTyped(InputEvent event, char charcter)
	{
		if(mFocusObstacle != null && mFocusObstacle.getInputDuration() <= 0){
			return mFocusObstacle.keyTyped(event, charcter);
		}
		return false;
	}
	
	/* 被触摸物体的链表节点 */
	private static final class TouchTarget
	{
		private Obstacle input;        //被触摸的Obstacle
		private int pointerIdBits;     //目标捕获的所有指针的 ID 的组合位掩码，用不同的位表示一个指针id
		private TouchTarget next;      //链表的下一个target

		public TouchTarget(Obstacle input, int id, TouchTarget next)
		{
			this.input = input;
			this.pointerIdBits = id;
			this.next = next;
		}
		
		//为指定的物体添加一个手指，如果没有该物体则将它添加到链表中
		public static TouchTarget addPointer(final TouchTarget touchTarget, Obstacle input, int id)
		{
			TouchTarget target = touchTarget;
			for(;target != null; target = target.next){
				if(target.input == input){
					target.pointerIdBits |= 1 << id;
					return touchTarget;
				}
			}
			return new TouchTarget(input, 1 << id, touchTarget); 
		}
		
		//从链表中寻找与该手指绑定的物体，移除物体与该手指的绑定
		public static TouchTarget removePointer(final TouchTarget touchTarget, int id)
		{
			TouchTarget target = touchTarget;
			if(((target.pointerIdBits >> id) & 1) == 1)
			{
				target.pointerIdBits &= ~(1 << id);
				if(target.pointerIdBits == 0){
					target = target.next;
				}
				return target;
			}
			for(;target.next != null; target = target.next)
			{
				if(((target.next.pointerIdBits >> id) & 1) == 1){
					target.next.pointerIdBits &= ~(1 << id);
					if(target.next.pointerIdBits == 0){
						target.next = target.next.next;
					}
					break;
				}
			}
			return touchTarget;
		}
		
		//寻找与指定手指绑定的物体
		public static Obstacle findInputById(TouchTarget target, int id)
		{
			for(;target != null; target = target.next){
				if(((target.pointerIdBits >> id) & 1) == 1){
					return target.input;
				}
			}
			return null;
		}
	}
	
	/* 碰撞检测回调 */
	public static interface CollisionCallback
	{
		public void onCollision(Obstacle target, Obstacle other)
	}
	
	/* 物体添加移除监视器 */
	public static interface ObstacleWatcher
	{
		public void onObstacleAdded(Obstacle obj)
		
		public void onObstacleRemoved(Obstacle obj)
	}
}
