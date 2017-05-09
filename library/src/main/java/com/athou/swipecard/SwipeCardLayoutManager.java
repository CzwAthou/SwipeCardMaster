package com.athou.swipecard;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by athou on 2017/5/8.
 */

public class SwipeCardLayoutManager extends RecyclerView.LayoutManager {
    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);

        int itemCount = getItemCount();
        //没有child，直接return
        if (itemCount < 1) {
            return;
        }

        //top 3 view 的最底部pos
        int bottomPosition = 0;
        if (itemCount <= CardConfig.MAX_SHOW_COUNT) {
            bottomPosition = 0;
        } else {
            bottomPosition = itemCount - CardConfig.MAX_SHOW_COUNT;
        }

        //从最底部的view开始布局
        for (int pos = bottomPosition; pos < itemCount; pos++) {
            View view = recycler.getViewForPosition(pos);
            addView(view); //添加view
            measureChildWithMargins(view, 0, 0); //测量view,以便获取宽高等参数

            int childWidth = getDecoratedMeasuredWidth(view); //获取子view的宽
            int childHeight = getDecoratedMeasuredHeight(view); //获取子view的高
            int widthSpace = getWidth() - childWidth; //获取宽间距
            int heightSpace = getHeight() - childHeight; //获取高间距
            //将child居中处理
            layoutDecoratedWithMargins(view, widthSpace / 2, heightSpace / 2, widthSpace / 2 + childWidth, heightSpace / 2 + childHeight);

            //最顶层不需要缩放和位移， level越大，缩放位移越多
            int level = itemCount - pos - 1;
            if (level > 0) {
                //每层x方向缩小
                view.setScaleX(1 - CardConfig.SCALE_GAP * level);
                view.setScaleY(1 - CardConfig.SCALE_GAP * level);
                view.setTranslationY(CardConfig.TRANS_Y_GAP * level);
            }
        }
    }
}
