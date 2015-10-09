package com.zkjinshi.superservice.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.bean.OrderBean;
import com.zkjinshi.superservice.listener.RecyclerLoadMoreListener;
import com.zkjinshi.superservice.view.CircleImageView;
import com.zkjinshi.superservice.view.CircleStatusView;

import java.util.ArrayList;

/**
 * 开发者：dujiande
 * 日期：2015/10/7
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderAdapter extends RecyclerView.Adapter{

    private static final String TAG = OrderAdapter.class.getSimpleName();

    private ArrayList<OrderBean> dataList;
    private RecyclerLoadMoreListener loadMoreListener;
    private Context context;
    public OrderAdapter(ArrayList<OrderBean> dataList) {
        this.dataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        OrderViewHolder holder = (OrderViewHolder)viewHolder;
        holder.position = position;
        OrderBean orderBean = dataList.get(position);

        //默认0可取消订单 1已取消订单 2已确认订单 3已经完成的订单 4正在入住中 5已删除订单
//        String orderStatus = orderBean.getStatus();
//        String orderStatusStr = "";
//        if(orderStatus.equals("0")){
//            orderStatusStr = "已提交订单";
//        }else if(orderStatus.equals("1")){
//            orderStatusStr = "已取消订单";
//        }else if(orderStatus.equals("2")){
//            orderStatusStr = "已确认订单";
//        }else if(orderStatus.equals("3")){
//            orderStatusStr = "已完成订单";
//        }else if(orderStatus.equals("4")){
//            orderStatusStr = "正在入住中";
//        }else if(orderStatus.equals("5")){
//            orderStatusStr = "已删除订单";
//        }
//        holder.name.setText(orderBean.getGuest()+orderStatusStr);
        LinearLayout.LayoutParams contentLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if(position == 0){
            contentLayoutParams.setMargins(0, DisplayUtil.dip2px(context, 12),DisplayUtil.dip2px(context,8),DisplayUtil.dip2px(context,6));
        }else if(position == getItemCount()-1){
            contentLayoutParams.setMargins(0, DisplayUtil.dip2px(context, 6), DisplayUtil.dip2px(context, 8), DisplayUtil.dip2px(context, 6));
        }else{
            contentLayoutParams.setMargins(0, DisplayUtil.dip2px(context, 6), DisplayUtil.dip2px(context, 8), DisplayUtil.dip2px(context, 6));
        }
        holder.contentLayout.setLayoutParams(contentLayoutParams);
        if(null != loadMoreListener){
            if (position == dataList.size() - 1) {
                loadMoreListener.loadMore();
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class OrderViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{

        public int position;

        public CircleStatusView leftIcon;
        public CircleImageView avatar;
        public TextView  name;
        public TextView  order;
        public TextView  price;
        public TextView  payStatus;
        public TextView  time;
        public ImageView tel;
        public ImageView chat;
        public ImageView share;
        LinearLayout contentLayout;

        public OrderViewHolder(View itemView) {
            super(itemView);
            leftIcon = (CircleStatusView)itemView.findViewById(R.id.civ_left_icon);
            avatar = (CircleImageView)itemView.findViewById(R.id.civ_avatar);
            name = (TextView )itemView.findViewById(R.id.tv_name);
            order = (TextView )itemView.findViewById(R.id.tv_order);
            price = (TextView )itemView.findViewById(R.id.tv_price);
            payStatus = (TextView )itemView.findViewById(R.id.tv_pay_status);
            time = (TextView )itemView.findViewById(R.id.tv_time_info);
            tel = (ImageView)itemView.findViewById(R.id.iv_tel);
            chat = (ImageView)itemView.findViewById(R.id.iv_chat);
            share = (ImageView)itemView.findViewById(R.id.iv_share);
            contentLayout = (LinearLayout)itemView.findViewById(R.id.content_layout);

            order.setOnClickListener(this);
            tel.setOnClickListener(this);
            chat.setOnClickListener(this);
            share.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            OrderBean orderBean = dataList.get(position);

            switch (view.getId()){
                case R.id.tv_order:
                    break;
                case R.id.iv_tel:
                    break;
                case R.id.iv_chat:
                    break;
                case R.id.iv_share:
                    break;
            }

        }
    }

    public void setOnLoadMoreListener(RecyclerLoadMoreListener loadMoreListener){
        this.loadMoreListener = loadMoreListener;
    }

}
