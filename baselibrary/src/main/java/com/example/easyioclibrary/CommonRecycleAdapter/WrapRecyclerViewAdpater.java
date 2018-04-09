package com.example.easyioclibrary.CommonRecycleAdapter;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by yjy on 2018/4/6.
 * 使他支持头部底部添加
 */

public class WrapRecyclerViewAdpater extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<View> mHeaderViews;

    private ArrayList<View> mFooterViews;

    private RecyclerView.Adapter mRealAdapter;

    private RecyclerCommonAdapter mRealCommAdapter;

    private ItemTouchHelperListener mTouchHelperListener;

    private WrapRecyclerViewAdpater.ItemTouchCallback callback;

    private ItemTouchHelper helper;

    private  int MARK = 1 << 31; //10 00 00 01


    public WrapRecyclerViewAdpater(RecyclerView.Adapter realAdapter){
        this.mRealAdapter = realAdapter;
        this.mRealCommAdapter = null;
        mRealAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                notifyItemRangeRemoved(positionStart+mHeaderViews.size(), itemCount);
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                onItemRangeMoved(fromPosition, toPosition,itemCount);
            }
        });
        mHeaderViews = new ArrayList<>();
        mFooterViews = new ArrayList<>();
    }

    public WrapRecyclerViewAdpater(RecyclerCommonAdapter realAdapter){
        this.mRealCommAdapter = realAdapter;
        this.mRealAdapter = null;
        mRealCommAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                notifyItemRangeRemoved(positionStart+mHeaderViews.size(), itemCount);
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                onItemRangeMoved(fromPosition, toPosition,itemCount);
            }
        });
        mHeaderViews = new ArrayList<>();
        mFooterViews = new ArrayList<>();
        callback = new WrapRecyclerViewAdpater.ItemTouchCallback(mRealCommAdapter,false);
        helper = new ItemTouchHelper(callback);

    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int mark) {

        //根据位置来判断是哪个位置
        //头部返回头部的viewholder

        int position = getPosition(mark);
        int numHeaders = getHeadersCount();
        if (position < numHeaders) {
            return createHeaderFooterViewHolder(mHeaderViews.get(position));
        }

        // Adapter
        //真实的返回真实的viewholder
        final int adjPosition = position - numHeaders;
        int adapterCount = 0;
        if (mRealAdapter != null) {
            adapterCount = mRealAdapter.getItemCount();
            if (adjPosition < adapterCount) {
                return mRealAdapter.onCreateViewHolder(parent,mRealAdapter.getItemViewType(adjPosition));
            }
        }

        //底部就返回底部的viewholder
        // Footer (off-limits positions will throw an IndexOutOfBoundsException)
        return createHeaderFooterViewHolder(mFooterViews.get(adjPosition - adapterCount));
    }

    private RecyclerView.ViewHolder createHeaderFooterViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        //根据位置返回
        return getType(position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int mark) {
        //只有真实adapter才处理，头部和底部不处理

        int position = getPosition(mark);
        int numHeaders = getHeadersCount();
        if (position < numHeaders) {
            return;
        }

        // Adapter
        //真实的返回真实的viewholder
        final int adjPosition = position - numHeaders;
        int adapterCount = 0;
        if (mRealAdapter != null) {
            adapterCount = mRealAdapter.getItemCount();
            if (adjPosition < adapterCount) {
                mRealAdapter.onBindViewHolder(holder,adjPosition);
            }
        }


    }

    @Override
    public int getItemCount() {
        //头部+底部+真实的条数
        return mHeaderViews.size()+mFooterViews.size()+mRealAdapter.getItemCount();
    }

    //获取位置，去高2位为标志位分别为 01 头 00 正文 10 尾部
    public int getType(int position){
        int flag = 0;
        if(position < getHeadersCount()){
            flag = (MARK ^ 3 << 30) | position;
        }else if(position - getHeadersCount()<getRealCount()){
            flag = position;
//            Log.e("postyp",""+flag+"  "+position);
        }else if(position > getHeadersCount() + getRealCount()-1){
            flag = position | MARK;

        }
        return flag;
    }

    public int getPosition(int mark){
        int pos = 0;
        pos = (mark << 2)>>2;
        return pos;
    }

    public int getRealCount(){
        if(mRealCommAdapter != null){
            return mRealCommAdapter.getItemCount();
        }else if((mRealAdapter != null)){
            return mRealAdapter.getItemCount();
        }else {
            return 0;
        }
    }

    public int getHeadersCount() {
        return mHeaderViews.size();
    }

    public int getFootersCount() {
        return mFooterViews.size();
    }

    //添加头部
    public void addHeaderView(View view){
        if(!mHeaderViews.contains(view)){
            mHeaderViews.add(view);
            notifyDataSetChanged();
        }
    }


    //添加底部
    public void addFooterView(View view){
        if(!mFooterViews.contains(view)){
            mFooterViews.add(view);
            notifyDataSetChanged();
        }
    }

    //移除头部
    public void removeHeaderView(View view){
        if(mHeaderViews.contains(view)){
            mHeaderViews.remove(view);
            notifyDataSetChanged();
        }
    }

    //移除底部
    public void removeFooterView(View view){
        if(mFooterViews.contains(view)){
            mFooterViews.remove(view);
            notifyDataSetChanged();
        }
    }

    public void attachToRecyclerView(RecyclerView view){
        if(helper != null){
            helper.attachToRecyclerView(view);
        }

    }

    public void setTouchHelperOpen(boolean isTouch){
        callback.setTouchHelperOpen(isTouch);
    }

    public void setTouchHelperListener(ItemTouchHelperListener listener){
        this.mTouchHelperListener = listener;
    }

    public class ItemTouchCallback extends ItemTouchHelper.Callback{

        private RecyclerCommonAdapter adapter;

        private boolean isTouch = false;

        public  ItemTouchCallback(RecyclerCommonAdapter adapter,boolean isTouch){
            this.adapter = adapter;
            this.isTouch = isTouch;
        }

        public void setTouchHelperOpen(boolean isTouch){
            this.isTouch = isTouch;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int curposition = viewHolder.getAdapterPosition();
            if(curposition < getHeadersCount() || curposition >adapter.getList().size()+getHeadersCount()-1 || !isTouch){
                return 0;
            }
            int dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            return makeMovementFlags(dragFlag,swipeFlag);
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return true;
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            if(mTouchHelperListener != null){
                mTouchHelperListener.onChildDraw(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive);
            }
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            if(mTouchHelperListener != null){
                mTouchHelperListener.clearView(recyclerView,viewHolder);
            }
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            if(adapter!=null && isTouch){
                int curposition = viewHolder.getAdapterPosition();
                if(curposition < getHeadersCount()){
                    return false;
                }
                int movepos = curposition - getHeadersCount();
//                Log.e("onMove","cur:"+curposition+" move: "+movepos+"target : "+target.getAdapterPosition());
                Collections.swap(adapter.getList(),movepos,target.getAdapterPosition()-getHeadersCount());
                notifyItemMoved(curposition,target.getAdapterPosition());
            }


            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            if(adapter != null){
                List list = adapter.getList();
                int curposition = viewHolder.getAdapterPosition();
                if(curposition < getHeadersCount()){
                    return;
                }
//                Log.e("onSwiped","size:"+list.size());

                int deletepos = curposition - getHeadersCount();
//                Log.e("onSwiped","cur:"+curposition+" delete: "+deletepos);
                list.remove(deletepos);
                notifyItemRemoved(curposition);
                notifyDataSetChanged();
//                Log.e("onSwiped","size:"+list.size());
            }
        }
    }


}
