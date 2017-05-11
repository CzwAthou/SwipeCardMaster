package com.athou.swipecarddemo;

import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.athou.swipecard.CardConfig;
import com.athou.swipecard.SwipeCallback;
import com.athou.swipecard.SwipeCardLayoutManager;
import com.bumptech.glide.Glide;

import java.util.List;

import static android.R.attr.defaultValue;

/**
 * 仿探探滑动效果
 * Created by athou on 2017/5/11.
 */

public class TanTanActivity extends AppCompatActivity {
    RecyclerView mRv;
    MyAdapter mAdapter;
    List<SwipeCardBean> mDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tantan);

        mDatas = SwipeCardBean.initDatas();

        mRv = (RecyclerView) findViewById(R.id.rv);
        mRv.setLayoutManager(new SwipeCardLayoutManager());
        mAdapter = new MyAdapter();
        mRv.setAdapter(mAdapter);

        //初始化配置
        ItemTouchHelper.Callback callback = new TantanCallback(mRv, mAdapter, mDatas);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mRv);
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(getLayoutInflater().inflate(R.layout.item_swipe_card, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            TextView tvName = (TextView) holder.itemView.findViewById(R.id.tvName);
            TextView tvPrecent = (TextView) holder.itemView.findViewById(R.id.tvPrecent);
            ImageView iv = (ImageView) holder.itemView.findViewById(R.id.iv);

            tvName.setText(mDatas.get(position).getName());
            tvPrecent.setText(mDatas.get(position).getPostition() + " /" + mDatas.size());
            Glide.with(TanTanActivity.this)
                    .load(mDatas.get(position).getUrl())
                    .into(iv);
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            public MyViewHolder(View itemView) {
                super(itemView);
            }

            public void setAlpha(int viewId, float alpha) {
                itemView.findViewById(viewId).setAlpha(alpha);
            }
        }
    }

    class TantanCallback extends SwipeCallback {

        //上下滑动卡片，不删除卡片,只有左右滑动才删除卡片
        //判断 此次滑动方向是否是竖直的 ，水平方向上的误差(阈值，默认我给了50dp),
        //水平方向位移超过这个值，则表示可以删除，否则，不进行删除
        int mHorizontalDeviation = 0;

        RecyclerView rv;

        public TantanCallback(RecyclerView rv, RecyclerView.Adapter adapter, List datas) {
            super(adapter, datas);
            this.rv = rv;
            mHorizontalDeviation = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
        }

        /**
         * TopView是否水平方向上居中的
         */
        public boolean isTopViewHorizontalCenter(View topView) {
            return Math.abs(rv.getWidth() / 2 - topView.getWidth() / 2 - topView.getX()) < mHorizontalDeviation;
        }

        @Override
        public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
            if (isTopViewHorizontalCenter(viewHolder.itemView)) {
                return Float.MAX_VALUE;
            }
            Log.i("TantanCallback", "getSwipeThreshold :" + defaultValue);
            return super.getSwipeThreshold(viewHolder);
        }

        //您可以增加此值，使其更难滑动或减少,以使其更容易滑动
        @Override
        public float getSwipeEscapeVelocity(float defaultValue) {
            View topView = rv.getChildAt(rv.getChildCount() - 1);
            if (isTopViewHorizontalCenter(topView)) {
                return Float.MAX_VALUE;
            }
            Log.i("TantanCallback", "getSwipeEscapeVelocity :" + defaultValue);
            return super.getSwipeEscapeVelocity(defaultValue);
        }

        //如果您增加值，用户将更容易对角滑动，如果减小该值，则用户需要进行相当直的手指移动才能触发滑动。
        @Override
        public float getSwipeVelocityThreshold(float defaultValue) {
            View topView = rv.getChildAt(rv.getChildCount() - 1);
            if (isTopViewHorizontalCenter(topView)) {
                return Float.MAX_VALUE;
            }
            Log.i("TantanCallback", "getSwipeVelocityThreshold:" + defaultValue);
            return super.getSwipeVelocityThreshold(defaultValue);
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            super.onSwiped(viewHolder, direction);
            //对rotate进行复位
            viewHolder.itemView.setRotation(0);
            //对移除的item的标记还原透明度0（不可见）
            if (viewHolder instanceof MyAdapter.MyViewHolder) {
                MyAdapter.MyViewHolder holder = (MyAdapter.MyViewHolder) viewHolder;
                holder.setAlpha(R.id.iv_love, 0);
                holder.setAlpha(R.id.iv_del, 0);
            }
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            double swipeValue = Math.sqrt(dX * dX + dY * dY);
            double faction = swipeValue / getThreshold(viewHolder.itemView);
            faction = Math.min(faction, 1);
            int childCount = recyclerView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = recyclerView.getChildAt(i);
                int level = childCount - i - 1;
                //level为0  表示最顶层view
                if (level > 0) {
                    //level值越大，越靠近底层，缩放量越大,底层向顶层依次递减，setScaleX的值为  1 - CardConfig.SCALE_GAP * level
                    //滑动距离越大，缩放量减小，setScaleX的值为 1 - CardConfig.SCALE_GAP * level + CardConfig.SCALE_GAP * faction
                    child.setScaleX((float) (1 - CardConfig.SCALE_GAP * level + CardConfig.SCALE_GAP * faction));
                    child.setScaleY((float) (1 - CardConfig.SCALE_GAP * level + CardConfig.SCALE_GAP * faction));
                    child.setTranslationY((float) (CardConfig.TRANS_Y_GAP * level - CardConfig.TRANS_Y_GAP * faction));
                } else { //最顶层，添加ratation和透明度
                    //X方向的比例因子
                    float xFaction = dX / getThreshold(viewHolder.itemView);
                    if (xFaction > 1) {
                        xFaction = 1;
                    } else if (xFaction < -1) {
                        xFaction = -1;
                    }
                    child.setRotation(xFaction * MAX_ROATATION);
                    if (viewHolder instanceof MyAdapter.MyViewHolder) {
                        MyAdapter.MyViewHolder holder = (MyAdapter.MyViewHolder) viewHolder;
                        if (dX > 0) { //右滑
                            holder.setAlpha(R.id.iv_love, xFaction);
                        } else if (dX < 0) {//左滑
                            holder.setAlpha(R.id.iv_del, -xFaction);
                        } else { //正中间
                            holder.setAlpha(R.id.iv_love, 0);
                            holder.setAlpha(R.id.iv_love, 0);
                        }
                    }
                }
            }
        }
    }

    private static final float MAX_ROATATION = 15f;
}
