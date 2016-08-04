package com.zkjinshi.superservice.pad.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.superservice.pad.R;
import com.zkjinshi.superservice.pad.bean.OrderBean;
import com.zkjinshi.superservice.pad.utils.Constants;
import com.zkjinshi.superservice.pad.utils.ProtocolUtil;
import com.zkjinshi.superservice.pad.view.CircleImageView;

import java.util.ArrayList;

/**
 * 开发者：dujiande
 * 日期：2015/10/12
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderMoreAdapter extends RecyclerView.Adapter {

    private static final String TAG = OrderMoreAdapter.class.getSimpleName();

    private DisplayImageOptions options;
    private ArrayList<OrderBean> dataList = new ArrayList<OrderBean>();
    private Context context;

   public String getTimeTips(){
       if(dataList.size() > 0){
           return TimeUtil.getChatTime(dataList.get(0).getCreated());
       }

       return "";
   }

    public OrderMoreAdapter(ArrayList<OrderBean> dataList) {
        this.dataList = dataList;
        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)// 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.ic_launcher)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.ic_launcher)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build();
    }

    public void refreshingAction( ArrayList<OrderBean> dataList){
        this.dataList = dataList;
        int count = dataList.size();
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_more_list, null);
        return new OrderMoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        viewHolder.setIsRecyclable(false);

        OrderMoreViewHolder holder = (OrderMoreViewHolder)viewHolder;
        holder.position = position;
        OrderBean orderBean = dataList.get(position);

        String userId = orderBean.getUserid();
        if(!TextUtils.isEmpty(userId)){
            String imageUrl =  ProtocolUtil.getAvatarUrl(userId);
            if(!TextUtils.isEmpty(imageUrl)){
                ImageLoader.getInstance().displayImage(imageUrl, holder.photoImageView,options);
            }
        }
        if(position == 0){
            holder.moreLayout.setPadding(DisplayUtil.dip2px(context, 15),DisplayUtil.dip2px(context,5),DisplayUtil.dip2px(context,5),DisplayUtil.dip2px(context,5));
        }else if(position == getItemCount()-1){
            holder.moreLayout.setPadding(DisplayUtil.dip2px(context,5),DisplayUtil.dip2px(context,5),DisplayUtil.dip2px(context,15),DisplayUtil.dip2px(context,5));
        }else{
            holder.moreLayout.setPadding(DisplayUtil.dip2px(context,5),DisplayUtil.dip2px(context,5), DisplayUtil.dip2px(context, 5),DisplayUtil.dip2px(context,5));
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class OrderMoreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public int position;
        private CircleImageView photoImageView;
        private LinearLayout moreLayout;

        public OrderMoreViewHolder(View itemView) {
            super(itemView);
            photoImageView = (CircleImageView) itemView.findViewById(R.id.order_more_cv_photo);
            moreLayout = (LinearLayout)itemView.findViewById(R.id.more_layout);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            OrderBean orderBean = dataList.get(position);

        }
    }
}
