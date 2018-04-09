package com.example.easyioclibrary.CommonRecycleAdapter;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;

/**
 * Created by yjy on 2018/4/9.
 */

public interface ItemTouchHelperListener {

    void onChildDraw(Canvas c, RecyclerView recyclerView,
                     RecyclerView.ViewHolder viewHolder, float dX, float dY,
                     int actionState, boolean isCurrentlyActive);

    void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder);
}
