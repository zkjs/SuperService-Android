package com.zkjinshi.superservice.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.base.util.IntentUtil;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.order.HotelDealActivity;
import com.zkjinshi.superservice.activity.order.KTVDealActivity;
import com.zkjinshi.superservice.activity.order.NormalDealActivity;
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

    private DisplayImageOptions options;

    private long lastTimeStamp = 0; //最后一条的时间戳
    private int pagedata = 5;     //每页多少条 默认10条

    public ArrayList<OrderBean> dataList;
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
        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)// 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.ic_launcher)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.ic_launcher)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build();
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
            lastTimeStamp = dataList.get(count-1).getCreated();
            lastTimeStamp = lastTimeStamp/1000;
        }
        notifyDataSetChanged();
    }

    public void loadMoreAction(ArrayList<OrderBean> dataList){
        int index = this.dataList.size();
        int count = dataList.size();
        if(count > 0){
            this.dataList.addAll(dataList);
            notifyItemRangeInserted(index, count);
            lastTimeStamp = dataList.get(count-1).getCreated();
            lastTimeStamp = lastTimeStamp/1000;
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

        viewHolder.setIsRecyclable(false);
        
        OrderViewHolder holder = (OrderViewHolder)viewHolder;
        holder.position = position;
        OrderBean orderBean = dataList.get(position);

        //默认0可取消订单 1已取消订单 2已确认订单 3已经完成的订单 4正在入住中 5已删除订单
        String orderStatus = orderBean.getOrderstatus();
        String userName = orderBean.getUsername();
        if(TextUtils.isEmpty(userName)){
            userName = orderBean.getUserid();
        }
        holder.name.setText(userName+"   "+orderStatus);

//        if(orderStatus == 0){
//            orderStatusStr = "已提交订单";
//            holder.leftIcon.setStatus(CircleStatusView.CircleStatus.STATUS_LOADING);
//            if(OrderUtil.isOrderTimeOut(new Date(orderBean.getArrivaldate()))){
//                orderStatusStr = "订单已失效";
//            }
//        }else if(orderStatus == 1){
//            holder.leftIcon.setStatus(CircleStatusView.CircleStatus.STATUS_FINISH);
//            orderStatusStr = "已取消订单";
//        }else if(orderStatus == 2){
//            holder.leftIcon.setStatus(CircleStatusView.CircleStatus.STATUS_FINISH);
//            orderStatusStr = "已确认订单";
//            if(OrderUtil.isOrderTimeOut(new Date(orderBean.getArrivaldate()))){
//                orderStatusStr = "订单已失效";
//            }
//        }else if(orderStatus == 3){
//            holder.leftIcon.setStatus(CircleStatusView.CircleStatus.STATUS_FINISH);
//            orderStatusStr = "已完成订单";
//        }else if(orderStatus == 4){
//            holder.leftIcon.setStatus(CircleStatusView.CircleStatus.STATUS_FINISH);
//            orderStatusStr = "正在入住中";
//        }else if(orderStatus == 5){
//            holder.leftIcon.setStatus(CircleStatusView.CircleStatus.STATUS_FINISH);
//            orderStatusStr = "已删除订单";
//        }

        try{
            SimpleDateFormat mSimpleFormat  = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat mChineseFormat = new SimpleDateFormat("MM/dd");
            Date arrivalDate = new Date(orderBean.getArrivaldate());
            Date departureDate = new Date(orderBean.getLeavedate());
            String arrivalStr = mChineseFormat.format(arrivalDate);
            String departureStr = mChineseFormat.format(departureDate);
            String roomType = orderBean.getRoomtype();
            int roomNum = orderBean.getRoomcount();
            int dayNum = TimeUtil.daysBetween(arrivalDate, departureDate);
            holder.order.setText(roomType + "×" + roomNum + "|" + dayNum + "晚|" + arrivalStr + "-" + departureStr);

        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
        holder.price.setText("￥"+orderBean.getRoomprice());

//        //支付状态 0未支付,1已支付,3支付一部分,4已退款, 5已挂账
//        String payStatus = orderBean.getPay_status();
//        String payStatusStr = "";
//        if(payStatus.equals("0")){
//            payStatusStr = "未支付";
//        }else if(payStatus.equals("1")){
//            payStatusStr = "已支付";
//        }else if(payStatus.equals("2")){
//            payStatusStr = "";
//        }else if(payStatus.equals("3")){
//            payStatusStr = "支付一部分";
//        }else if(payStatus.equals("4")){
//            payStatusStr = "已退款";
//        }else if(payStatus.equals("5")){
//            payStatusStr = "已挂账";
//        }
//        holder.payStatus.setText(payStatusStr);

        ImageLoader.getInstance().displayImage(ProtocolUtil.getAvatarUrl(orderBean.getUserid()), holder.avatar, this.options);
        String timeStr = TimeUtil.getChatTime(orderBean.getCreated());
        holder.time.setText(timeStr);

        LinearLayout.LayoutParams contentLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if(position == 0){
            holder.upCutLineView.setVisibility(View.INVISIBLE);
            contentLayoutParams.setMargins(0, DisplayUtil.dip2px(context, 12),DisplayUtil.dip2px(context,8),DisplayUtil.dip2px(context,6));
        }else if(position == getItemCount()-1){
            holder.upCutLineView.setVisibility(View.VISIBLE);
            contentLayoutParams.setMargins(0, DisplayUtil.dip2px(context, 6), DisplayUtil.dip2px(context, 8), DisplayUtil.dip2px(context, 6));
        }else{
            holder.upCutLineView.setVisibility(View.VISIBLE);
            contentLayoutParams.setMargins(0, DisplayUtil.dip2px(context, 6), DisplayUtil.dip2px(context, 8), DisplayUtil.dip2px(context, 6));
        }
        holder.contentLayout.setLayoutParams(contentLayoutParams);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class OrderViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{

        public int position;
        public View upCutLineView;
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

            upCutLineView = itemView.findViewById(R.id.time_axis_cut_line_up);
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

            tel.setOnClickListener(this);
            chat.setOnClickListener(this);
            share.setOnClickListener(this);
            contentLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            OrderBean orderBean = dataList.get(position);
            switch (view.getId()){
                case R.id.content_layout:
                    String orderNo = orderBean.getOrderno();
                    Intent intent = new Intent();
                    if(orderNo.startsWith("H")){
                        intent.setClass(context,HotelDealActivity.class);
                        intent.putExtra("orderNo",orderNo);
                    }else if(orderNo.startsWith("K")){
                        intent.setClass(context,KTVDealActivity.class);
                        intent.putExtra("orderNo",orderNo);
                    }
                    else if(orderNo.startsWith("O")){
                        intent.setClass(context,NormalDealActivity.class);
                        intent.putExtra("orderNo",orderNo);
                    }
                    context.startActivity(intent);
                    ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;

                case R.id.iv_tel:
                    if(TextUtils.isEmpty( orderBean.getTelephone())){
                        DialogUtil.getInstance().showToast(context,"联系号码为空");
                        return;
                    }
                    IntentUtil.callPhone(context, orderBean.getTelephone());
                    break;

                case R.id.iv_chat:
                    if(TextUtils.isEmpty( orderBean.getTelephone())){
                        DialogUtil.getInstance().showToast(context,"联系号码为空");
                        return;
                    }
                    IntentUtil.startSendMessage("",orderBean.getTelephone(),context);
                    break;

                case R.id.iv_share:
                    if(TextUtils.isEmpty( orderBean.getTelephone())){
                        DialogUtil.getInstance().showToast(context,"联系号码为空");
                        return;
                    }
                    IntentUtil.startSendMessage("",orderBean.getTelephone(),context);
                    break;
            }
        }
    }

}
