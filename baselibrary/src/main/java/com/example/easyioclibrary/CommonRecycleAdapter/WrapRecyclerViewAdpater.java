package com.example.easyioclibrary.CommonRecycleAdapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by yjy on 2018/4/6.
 * 使他支持头部底部添加
 */

public class WrapRecyclerViewAdpater extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final RecyclerView.Adapter mRealAdapter;

    private ArrayList<View> mHeaderViews;

    private ArrayList<View> mFooterViews;


    public WrapRecyclerViewAdpater(RecyclerView.Adapter realAdapter){
        this.mRealAdapter = realAdapter;
        mRealAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                notifyDataSetChanged();
            }
        });
        mHeaderViews = new ArrayList<>();
        mFooterViews = new ArrayList<>();
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {

        //根据位置来判断是哪个位置
        //头部返回头部的viewholder

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
        return position;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //只有真实adapter才处理，头部和底部不处理


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


}
