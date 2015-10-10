package com.zkjinshi.superservice.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.order.OrderDealActivity;
import com.zkjinshi.superservice.bean.OrderBean;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.view.CircleImageView;
import com.zkjinshi.superservice.view.CircleStatusView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 开发者：dujiande
 * 日期：2015/10/7
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderAdapter extends RecyclerView.Adapter {

    private static final String TAG = OrderAdapter.class.getSimpleName();

    private long lastTimeStamp = 0; //最后一条的时间戳
    private int pagedata = 4;     //每页多少条 默认10条

    private ArrayList<OrderBean> dataList;
    private Context context;

    public int getPagedata() {
        return pagedata;
    }

    public void setPagedata(int pagedata) {
        this.pagedata = pagedata;
    }

    public long getLastTimeStamp() {
        return lastTimeStamp;
    }

    public void setLastTimeStamp(long lastTimeStamp) {
        this.lastTimeStamp = lastTimeStamp;
    }

    public OrderAdapter(ArrayList<OrderBean> dataList) {
        this.dataList = dataList;
    }

    public ArrayList<OrderBean> getDataList() {
        return dataList;
    }

    public void setDataList(ArrayList<OrderBean> dataList) {
        this.dataList = dataList;
    }

    public void refreshingAction( ArrayList<OrderBean> dataList){
        this.dataList = dataList;
        int count = dataList.size();
        if(count > 0){
            lastTimeStamp = TimeUtil.timeStrToTimeStamp(dataList.get(count-1).getCreated());
        }
        notifyDataSetChanged();

    }

    public void loadMoreAction(ArrayList<OrderBean> dataList){
        int index = this.dataList.size();
        int count = dataList.size();
        if(count > 0){
            this.dataList.addAll(dataList);
            notifyItemRangeInserted(index, count);
            lastTimeStamp = TimeUtil.timeStrToTimeStamp(dataList.get(count-1).getCreated());
        }
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
        String orderStatus = orderBean.getStatus();
        String orderStatusStr = "";
        if(orderStatus.equals("0")){
            orderStatusStr = "已提交订单";
        }else if(orderStatus.equals("1")){
            holder.leftIcon.setStatus(CircleStatusView.CircleStatus.STATUS_LOADING);
            holder.leftIcon.invalidate();
            orderStatusStr = "已取消订单";
        }else if(orderStatus.equals("2")){
            holder.leftIcon.setStatus(CircleStatusView.CircleStatus.STATUS_FINISH);
            holder.leftIcon.invalidate();
            orderStatusStr = "已确认订单";
        }else if(orderStatus.equals("3")){
            holder.leftIcon.setStatus(CircleStatusView.CircleStatus.STATUS_FINISH);
            holder.leftIcon.invalidate();
            orderStatusStr = "已完成订单";
        }else if(orderStatus.equals("4")){
            holder.leftIcon.setStatus(CircleStatusView.CircleStatus.STATUS_FINISH);
            holder.leftIcon.invalidate();
            orderStatusStr = "正在入住中";
        }else if(orderStatus.equals("5")){
            orderStatusStr = "已删除订单";
        }
        holder.name.setText(orderBean.getGuest()+"   "+orderStatusStr);

        try{
            SimpleDateFormat mSimpleFormat  = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat mChineseFormat = new SimpleDateFormat("MM/dd");
            Date arrivalDate =  mSimpleFormat.parse(orderBean.getArrival_date());
            Date departureDate =  mSimpleFormat.parse(orderBean.getDeparture_date());
            String arrivalStr = mChineseFormat.format(arrivalDate);
            String departureStr = mChineseFormat.format(departureDate);
            String roomType = orderBean.getRoom_type();
            int roomNum = orderBean.getRooms();
            int dayNum = TimeUtil.daysBetween(arrivalDate, departureDate);
            holder.order.setText(roomType + "×" + roomNum + "|" + dayNum + "晚|" + arrivalStr + "-" + departureStr);

        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
        holder.price.setText("￥"+orderBean.getRoom_rate());

        //支付状态 0未支付,1已支付,3支付一部分,4已退款, 5已挂账
        String payStatus = orderBean.getPay_status();
        String payStatusStr = "";
        if(payStatus.equals("0")){
            payStatusStr = "未支付";
        }else if(payStatus.equals("1")){
            payStatusStr = "已支付";
        }else if(payStatus.equals("2")){
            payStatusStr = "";
        }else if(payStatus.equals("3")){
            payStatusStr = "支付一部分";
        }else if(payStatus.equals("4")){
            payStatusStr = "已退款";
        }else if(payStatus.equals("5")){
            payStatusStr = "已挂账";
        }
        holder.payStatus.setText(payStatusStr);
        ImageLoader.getInstance().displayImage(ProtocolUtil.getAvatarUrl(orderBean.getUserid()), holder.avatar);


        LinearLayout.LayoutParams contentLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if(position == 0){
            contentLayoutParams.setMargins(0, DisplayUtil.dip2px(context, 12),DisplayUtil.dip2px(context,8),DisplayUtil.dip2px(context,6));
        }else if(position == getItemCount()-1){
            contentLayoutParams.setMargins(0, DisplayUtil.dip2px(context, 6), DisplayUtil.dip2px(context, 8), DisplayUtil.dip2px(context, 6));
        }else{
            contentLayoutParams.setMargins(0, DisplayUtil.dip2px(context, 6), DisplayUtil.dip2px(context, 8), DisplayUtil.dip2px(context, 6));
        }
        holder.contentLayout.setLayoutParams(contentLayoutParams);
        String timeStr = TimeUtil.getChatTime(orderBean.getCreated());
        holder.time.setText(timeStr);


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
                    Intent intent = new Intent(context, OrderDealActivity.class);
                    intent.putExtra("reservation_no", orderBean.getReservation_no());
                    context.startActivity(intent);
                    ((Activity)context).overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);
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



}
