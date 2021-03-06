package com.athou.swipecard;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import java.util.List;

/**
 * Created by athou on 2017/5/8.
 */

public class SwipeCallback extends ItemTouchHelper.SimpleCallback {

    RecyclerView.Adapter mAdapter;
    List mDatas;

    public SwipeCallback(RecyclerView.Adapter adapter, List datas) {
        this(0, ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        mAdapter = adapter;
        mDatas = datas;
    }

    public SwipeCallback(int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        Log.d("swipecard", "onSwiped() called with: viewHolder = [" + viewHolder + "], direction = [" + direction + "]");
        Object remove = mDatas.remove(viewHolder.getLayoutPosition());
        mDatas.add(0, remove);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        Log.d("swipecard", "onChildDraw()  post = [" + viewHolder.getLayoutPosition() + "], dX = [" + dX + "], dY = [" + dY + "], actionState = [" + actionState + "], isCurrentlyActive = [" + isCurrentlyActive + "]");
        //先根据滑动的dxdy 算出现在动画的比例系数fraction
        //获取滑到的距离
        double swipeValue = Math.sqrt(dX * dX + dY * dY);
        double fraction = swipeValue / getThreshold(viewHolder.itemView);
        //边界修正 最大为1
        if (fraction > 1) {
            fraction = 1;
        }
        //对每个ChildView进行缩放 位移
        int childCount = recyclerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            int level = childCount - i - 1;
            //最顶层不需要缩放和位移
            Log.d("swipecard", "onChildDraw()  fraction = [" + fraction + "], level = [" + level + "]");
            if (level > 0) {
                View child = recyclerView.getChildAt(i);
                //level值越大，越靠近底层，缩放量越大,底层向顶层依次递减，setScaleX的值为  1 - CardConfig.SCALE_GAP * level
                //滑动距离越大，缩放量减小，setScaleX的值为 1 - CardConfig.SCALE_GAP * level + CardConfig.SCALE_GAP * faction
                child.setScaleX((float) (1 - CardConfig.SCALE_GAP * level + CardConfig.SCALE_GAP * fraction));
                child.setScaleY((float) (1 - CardConfig.SCALE_GAP * level + CardConfig.SCALE_GAP * fraction));
                //level值越大，越靠近底层,Y偏移越大:CardConfig.TRANS_Y_GAP * level
                //滑动距离越大，Y减小: CardConfig.TRANS_Y_GAP * level - CardConfig.TRANS_Y_GAP * fraction
                child.setTranslationY((float) (CardConfig.TRANS_Y_GAP * level - CardConfig.TRANS_Y_GAP * fraction));
            }
        }
    }

    /**
     * 水平方向是否可以被回收掉的阈值
     */
    protected float getThreshold(View view) {
        return view.getWidth();
    }
}
