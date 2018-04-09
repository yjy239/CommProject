package com.example.easyioclibrary.CommonRecycleAdapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by yjy on 2018/4/6.
 */

public class WrapRecyclerView extends RecyclerView {

    private WrapRecyclerViewAdpater mAdapter;

    public WrapRecyclerView(Context context) {
        super(context);
    }

    public WrapRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter){
        mAdapter = new WrapRecyclerViewAdpater(adapter);
        super.setAdapter(mAdapter);
        mAdapter.attachToRecyclerView(this);
    }

    public void setAdapter(RecyclerCommonAdapter adapter){
        mAdapter = new WrapRecyclerViewAdpater(adapter);
        super.setAdapter(mAdapter);
        mAdapter.attachToRecyclerView(this);
    }

    //添加头部
    public void addHeaderView(View view){
        if(mAdapter != null){
            mAdapter.addHeaderView(view);
        }
    }


    //添加底部
    public void addFooterView(View view){
        if(mAdapter != null){
            mAdapter.addFooterView(view);
        }
    }

    //移除头部
    public void removeHeaderView(View view){
        if(mAdapter != null){
            mAdapter.removeHeaderView(view);
        }
    }

    //移除底部
    public void removeFooterView(View view){
        if(mAdapter != null){
            mAdapter.removeFooterView(view);
        }
    }

    public void setTouchHelperOpen(boolean isTouch){
        mAdapter.setTouchHelperOpen(isTouch);
    }

    public void setTouchHelperListener(ItemTouchHelperListener listener){
        mAdapter.setTouchHelperListener(listener);
    }



}
