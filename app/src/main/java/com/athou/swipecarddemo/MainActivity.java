package com.athou.swipecarddemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.athou.swipecard.CardConfig;
import com.athou.swipecard.SwipeCallback;
import com.athou.swipecard.SwipeCardLayoutManager;
import com.bumptech.glide.Glide;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRv;
    MyAdapter mAdapter;
    List<SwipeCardBean> mDatas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatas = SwipeCardBean.initDatas();

        mRv = (RecyclerView) findViewById(R.id.rv);
        mRv.setLayoutManager(new SwipeCardLayoutManager());
        mAdapter = new MyAdapter();
        mRv.setAdapter(mAdapter);

        //初始化配置
        CardConfig.init(this);
        ItemTouchHelper.Callback callback = new SwipeCallback(mAdapter, mDatas);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mRv);

        findViewById(R.id.btnRefresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatas.add(new SwipeCardBean(mDatas.size() + 1, "http://news.k618.cn/tech/201604/W020160407281077548026.jpg", "增加的"));
                mAdapter.notifyDataSetChanged();
            }
        });
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
            Glide.with(MainActivity.this)
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
        }
    }
}
