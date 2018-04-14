package com.example.easyioclibrary.CommonLayoutManager;

import android.graphics.Rect;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yjy on 2018/4/10.
 */

public abstract class RecyclerCommmLayoutManager extends RecyclerView.LayoutManager {

    private int mVerticalOffset;//竖直偏移量 每次换行时，要根据这个offset判断
    private int mHorizontalOffset;//横向偏移量 每次换行时，要根据这个offset判断
    /** 用于保存item的位置信息 */
    private SparseArray<Rect> mItemRects = new SparseArray<>();
    private int mFirstVisiPos = 0;
    private int mLastVisiPos = 0;
    /** 用于保存item是否处于回收状态的信息 */
    private SparseBooleanArray mItemDetachStates = new SparseBooleanArray();

    //控制滑动的变量
    private boolean isVerticalScroll = false;
    private boolean isHorizontalScroll = false;
    private boolean isAutoScroll = true;
    private int totalHeight = 0;
    private int totalWidth = 0;

    //显示子view的数目
    private int mShownCount = 0;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if(getItemCount() == 0){
            detachAndScrapAttachedViews(recycler);
            return;
        }

        //没有布局好就不需要摆布局，因为onMeasure也是要走一次
        if(getChildCount() == 0&&state.isPreLayout()){
            return;
        }

        //解绑所有的view
        detachAndScrapAttachedViews(recycler);
        mFirstVisiPos = 0;
        mLastVisiPos = getItemCount();

        fill(recycler, state);
    }

    //填充子view方法
    private void fill(RecyclerView.Recycler recycler, RecyclerView.State state) {
        fill(recycler, state, 0);
    }

    private int fill(RecyclerView.Recycler recycler, RecyclerView.State state, int offset) {
        //还需要计算顶部的padding,左侧的padding
        int toppadding = getPaddingTop();
        int leftpadding = getPaddingLeft();

        //回收views
        recyclerViews(recycler,offset);

        //做好了回收，开始布局
        //这边要做view的布局处理，默认竖直方向滑动
        //超出界面 超出控件最大width的，允许左右滑动
        //超出控件最大height的，允许上下滑动
        //能够设置显示多少个数目，注意两者只能允许其中一个方向滑动
        //允许不滑动
        if(offset >= 0){
            calcuateShown(recycler,offset);

            //添加好之后，判断有没有空位
//            View lastChild = getChildAt(getChildCount() - 1);
//            if(getPosition(lastChild) == getItemCount()-1){
//                int gap = getHeight() - getPaddingBottom() - getDecoratedBottom(lastChild);
//                if(gap>0){
//                    offset -= gap;
//                }
//            }

        }else {
            fillViewsFromUp(recycler,offset);
        }
        Log.d("TAG", "count= [" + getChildCount() + "]" + ",[recycler.getScrapList().size():" + recycler.getScrapList().size() + ", offset:" + offset + ",  mHorizontalOffset" + mHorizontalOffset + ", ");


        return offset;
    }




    //向下滑动，填充逆序过去的view
    private void fillViewsFromUp(RecyclerView.Recycler recycler, int offset){
        int maxpos = getItemCount()-1;
        mFirstVisiPos = 0;
        if(getChildCount() > 0){
            View firstView = getChildAt(0);
            maxpos = getPosition(firstView)-1;
        }


        for(int i = maxpos;i>=mFirstVisiPos;i--){
            Rect rect = mItemRects.get(i);
            //越界则第一个加一
            if(rect!=null){
                //这里只考虑向下滑动的问题
                View child = recycler.getViewForPosition(i);

                if(rect.bottom - mVerticalOffset - offset<getPaddingTop()){
                    mFirstVisiPos = i+1;
                    break;
                }else if(rect.right-mHorizontalOffset -offset<getPaddingLeft()){
//                    Log.e("fillViewsFromUp "+i,"offset "+offset+" rect.left："+rect.left+" rect.right："+rect.right
//                    +"mHorizontalOffset "+mHorizontalOffset);
                    mFirstVisiPos = i+1;
                    break;
                }else{
//                    Log.e("add "+i,"offset "+offset+" rect.left："+rect.left+" rect.right："
//                            +rect.right+"mHorizontalOffset "+mHorizontalOffset);
//                        View child = recycler.getViewForPosition(i);
//                        if(!itemStates.get(i)){
                    addView(child,0);
                    measureChildWithMargins(child, 0, 0);
                    layoutDecoratedWithMargins(child, rect.left-mHorizontalOffset, rect.top - mVerticalOffset,
                                rect.right- mHorizontalOffset, rect.bottom - mVerticalOffset);
                    mItemDetachStates.put(i,false);

                }
            }

        }
    }


    //首次布局，计算哪个方向可以滑动，当两处都能滑动时候，默认是竖直滑动
    //当dy为0的时候可以初次布局，最好加个判断位，标志视图是否处于附着状态
    private void calcuateShown(RecyclerView.Recycler recycler, int offset){
        //先假设它能够全部铺满，先铺面再判断，超过的部分全部回收掉
        int minpos = mFirstVisiPos;
        mLastVisiPos = getItemCount() - 1;
        if(getChildCount()>0){
            //本来已经添加的
            View lastview = getChildAt(getChildCount()-1);
            minpos = getPosition(lastview) + 1;//从最后一个View+1开始开始添加
        }


        for(int i=minpos;i<mLastVisiPos;i++){
            View child = recycler.getViewForPosition(i);
            ChildViewState viewState = new ChildViewState();
//            viewState.setView(v);
            viewState.setWidth(getDecoratedMeasuredWidth(child));
            viewState.setHeight(getDecoratedMeasuredHeight(child));
            viewState = getChildViewState(i,child,viewState);

            addView(child);

            //测量
            measureChildWithMargins(child,0,0);
            //开始添加判断能够滑动的方向,先不断的累加高度和宽度
//            totalWidth = totalWidth + getDecoratedMeasurementHorizontal(child);
//            totalHeight = totalHeight + getDecoratedMeasurementVertical(child);
            //判断每个view中最大的处于最大的高度
            totalHeight = Math.max(viewState.getTop()+getDecoratedMeasurementVertical(child),totalHeight);
            totalWidth = Math.max(viewState.getLeft()+getDecoratedMeasurementHorizontal(child),totalWidth);
            if(totalHeight > getVerticalSpace()&&!isHorizontalScroll&&!isVerticalScroll){
                isVerticalScroll = true;
                mHorizontalOffset= 0;
            }else if(totalWidth > getHorizontalSpace()&&!isHorizontalScroll&&!isVerticalScroll){
                isHorizontalScroll = true;
                mVerticalOffset = 0;
            }

//            Log.e("isScroll","isHorizontalScroll"+isHorizontalScroll+ " isVerticalScroll "+isVerticalScroll);

            //每一次布局好了再一次检测一次,默认是铺满布局加1
            //如果默认显示数目为0按照平铺标准，不然只显示显示数量
            if(mShownCount ==0){
                if(isHorizontalScroll){
                    if(viewState.getLeft() -mHorizontalOffset- offset>getWidth()-getPaddingRight()){
//                        Log.e("mHorizontalOffset"+i,"mLastVisiPos "+mLastVisiPos);
                        removeAndRecycleView(child,recycler);
                        mItemDetachStates.put(i,true);
                        mLastVisiPos = i-1;
                        continue;
                    }
                }else{
                    if(viewState.getTop() -mVerticalOffset- offset>getHeight()-getPaddingBottom()){
                        removeAndRecycleView(child,recycler);
                        mItemDetachStates.put(i,true);
                        mLastVisiPos = i-1;
                        continue;
                    }
                }
            }

            //允许自定义view的大小
            int width = viewState.getWidth() == 0? getDecoratedMeasurementHorizontal(child) : viewState.getWidth();
            int height = viewState.getHeight() == 0?getDecoratedMeasurementVertical(child) : viewState.getHeight();

            //判断好之后开始布局
            layoutDecoratedWithMargins(child,viewState.getLeft()-mHorizontalOffset,viewState.getTop()-mVerticalOffset,
                    viewState.getLeft()+width-mHorizontalOffset,
                    viewState.getTop()+height-mVerticalOffset);

            Rect rect = new Rect(viewState.getLeft(),
                    viewState.getTop()+mVerticalOffset,
                    viewState.getLeft()+width,
                    viewState.getTop()+height+mVerticalOffset);
            mItemRects.put(i,rect);
            mItemDetachStates.put(i,false);


            //排序好之后，我们可以对每个item添加动画
            setViewAination(i,child);

        }

    }




    private void recyclerViews(RecyclerView.Recycler recycler, int offset){
        //现在情况更为复杂，一个我们需要判断dy和dx,dy代表竖直方向滑动，dx代表横向移动
        //分为2种情况，一种已经加载好view了，这个时候说明已经加载一边，要做的事情是要把对应的view回收
        if(getChildCount() > 0){
            //方法判断一下每一个view的当前的位置,逆序检测
            for(int i = getChildCount() - 1;i>=0;i--){
                View child = getChildAt(i);
                if(isVerticalScroll){
                    if(offset>0){
                        //说明正在向上滑动，回收掉超过上边界的view
                        if(getDecoratedBottom(child) -offset< getPaddingTop()){
                            removeAndRecycleView(child,recycler);
                            mItemDetachStates.put(i,false);
                            mFirstVisiPos++;
                            continue;
                        }
                    }else if(offset<0){
                        //回收下边界，计算好底部
                        if(getDecoratedTop(child)-offset>getHeight()-getPaddingBottom()){
//                            Log.e(" recycler down","recycler： "+i+" "+(getDecoratedTop(child) - offset > getHeight() - getPaddingBottom()));
                            removeAndRecycleView(child,recycler);
                            mItemDetachStates.put(i,false);
                            mLastVisiPos--;
                            continue;
                        }
                    }
                }else if(isHorizontalScroll){
                    //判断横向
                    if(offset>0){
                        //向右侧滑动
                        if(getDecoratedRight(child) - offset < getPaddingLeft()){
                            removeAndRecycleView(child,recycler);
                            mItemDetachStates.put(i,false);
                            mFirstVisiPos++;
                            continue;
                        }
                    }else if(offset<0){
                        if(getDecoratedLeft(child) - offset > getWidth()-getPaddingRight()){
                            removeAndRecycleView(child,recycler);
                            mLastVisiPos--;
                            mItemDetachStates.put(i,false);
                            continue;

                        }
                    }
                }

                //判断当前状态进行回收
//                if(!mItemDetachStates.get(i)){
//                    removeAndRecycleView(child,recycler);
//                    if(offset>0){
//                        mFirstVisiPos++;
//                    }else if(offset<0){
//                        mLastVisiPos++;
//                    }
//                    mItemDetachStates.put(i,true);
//                }
            }
        }
    }


    @Override
    public boolean canScrollVertically() {
        return isVerticalScroll;
    }

    @Override
    public boolean canScrollHorizontally() {
        return isHorizontalScroll;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        //竖直方向我们需要进行填充
        if(dy == 0 || getChildCount() == 0||!isVerticalScroll){
            return 0;
        }

        int realoffset = dy;

        if(mVerticalOffset + realoffset < 0){
//            //上边界 向上滑
            int gap = getPaddingTop();
            View first = getChildAt(0);
            if(getPosition(first) == 0){
//                Log.e("realoffset"," realoffset: "+realoffset+" getDecoratedTop(first)"+getDecoratedTop(first));
                realoffset = getDecoratedTop(first) - realoffset;

//                realoffset = fill(recycler,state,-realoffset);
//                offsetChildrenVertical(-realoffset);
                return 0;
            }else {
                realoffset = -mVerticalOffset;
            }

            //获取第一个view的位置，禁止滑动

        }else if(realoffset > 0){
            //下边界 向下滑
            View lastChild = getChildAt(getChildCount() - 1);
            //如果刚好在下边界
            if(getPosition(lastChild) == getItemCount()-1){
                //当没有充满屏幕的时候
                if(getDecoratedBottom(lastChild) < getHeight()-getPaddingBottom()){
                    return 0;
                }
                //在最底部不允许继续向下滑动,给个回弹
                int gap = getHeight()-getPaddingBottom()-getDecoratedBottom(lastChild);
//                Log.e("gap",""+gap+" realoffset: "+realoffset);
                if(gap > 0){
                    realoffset = -gap;
                } else if (gap == 0) {
                    realoffset = 0;
                } else {
                    realoffset = Math.min(realoffset, -gap);
                }
            }
        }

        //先填充再移动
        realoffset = fill(recycler,state,dy);

        mVerticalOffset += realoffset;

        offsetChildrenVertical(-realoffset);


        return realoffset;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if(dx == 0 || getChildCount() == 0||!isHorizontalScroll){
            return 0;
        }

        int realoffset = dx;

        if(mHorizontalOffset + realoffset < 0){
//            //上边界 向上滑
            int gap = getPaddingTop();
            View first = getChildAt(0);
            if(getPosition(first) == 0){
//                Log.e("realoffset"," realoffset: "+realoffset+" getDecoratedTop(first)"+getDecoratedTop(first));

//                realoffset = fill(recycler,state,-realoffset);
//                offsetChildrenVertical(-realoffset);
                return 0;
            }else {
                realoffset = -mHorizontalOffset;
            }

            //获取第一个view的位置，禁止滑动

        }else if(realoffset > 0){
            //下边界 向下滑
            View lastChild = getChildAt(getChildCount() - 1);
            //如果刚好在下边界
            if(getPosition(lastChild) == getItemCount()-1){
                //当没有充满屏幕的时候
                if(getDecoratedRight(lastChild) < getWidth()-getPaddingRight()){
                    return 0;
                }
                //在最底部不允许继续向下滑动,给个回弹
                int gap = getWidth()-getPaddingRight()-getDecoratedRight(lastChild);
//                Log.e("gap",""+gap+" realoffset: "+realoffset);
                if(gap > 0){
                    realoffset = -gap;
                } else if (gap == 0) {
                    realoffset = 0;
                } else {
                    realoffset = Math.min(realoffset, -gap);
                }
            }
        }

        //先填充再移动
        realoffset = fill(recycler,state,dx);

        mHorizontalOffset += realoffset;

        offsetChildrenHorizontal(-realoffset);

        return realoffset;
    }


    private int getVerticalSpace() {
        //计算RecyclerView的可用高度，除去上下Padding值
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }

    public int getHorizontalSpace() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    /**
     * 获取某个childView在水平方向所占的空间
     *
     * @param view
     * @return
     */
    public int getDecoratedMeasurementHorizontal(View view) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                view.getLayoutParams();
        return getDecoratedMeasuredWidth(view) + params.leftMargin
                + params.rightMargin;
    }

    /**
     * 获取某个childView在竖直方向所占的空间
     *
     * @param view
     * @return
     */
    public int getDecoratedMeasurementVertical(View view) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                view.getLayoutParams();
        return getDecoratedMeasuredHeight(view) + params.topMargin
                + params.bottomMargin;
    }


    public abstract ChildViewState getChildViewState(int position, View child, ChildViewState state);

    public abstract void setViewAination(int position,View child);



    @Override
    public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(view, recycler);
        removeAndRecycleAllViews(recycler);
        recycler.clear();
    }
}
