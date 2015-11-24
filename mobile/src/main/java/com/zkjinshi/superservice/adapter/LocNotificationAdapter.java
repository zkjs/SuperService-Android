package com.zkjinshi.superservice.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.base.util.IntentUtil;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.listener.RecyclerItemClickListener;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.view.CircleStatusView;
import com.zkjinshi.superservice.vo.ComingVo;
import com.zkjinshi.superservice.view.CircleImageView;

import java.util.ArrayList;

/**
 * 开发者：vincent
 * 日期：2015/9/25
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class LocNotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    private  ArrayList<ComingVo> comingList;
    private DisplayImageOptions     options;
    private RecyclerItemClickListener itemClickListener;

    public void setComingList(ArrayList<ComingVo> comingList) {
        if(null ==  comingList){
            this.comingList = new ArrayList<ComingVo>();
        }else{
            this.comingList = comingList;
        }
        notifyDataSetChanged();
    }

    public LocNotificationAdapter(Activity activity, ArrayList<ComingVo> comingList) {

        this.context = activity;
        this.setComingList(comingList);
        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.img_hotel_zhanwei)// 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.img_hotel_zhanwei)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.img_hotel_zhanwei)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_loc_notification, null);
        NoticeViewHolder localHolder = new NoticeViewHolder(view);
        return localHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ComingVo comingVo = comingList.get(position);

        String userId = comingVo.getUserId();
        String imageUrl = Constants.GET_USER_AVATAR + userId + ".jpg";
        if(!TextUtils.isEmpty(imageUrl)){
            ImageLoader.getInstance().displayImage(imageUrl, ((NoticeViewHolder) holder).civClientAvatar, options);
        }
        String vip = comingVo.getVip();
        if(!TextUtils.isEmpty(vip)){
            ((NoticeViewHolder) holder).tvVip.setText("VIP"+vip);
        }
        String userName = comingVo.getUserName();
        if(!TextUtils.isEmpty(userName)){
            ((NoticeViewHolder) holder).tvClientName.setText(userName);
        }
        String location = comingVo.getLocation();
        if(!TextUtils.isEmpty(location)){
            ((NoticeViewHolder) holder).tvClientInfo.setText("到达"+location);
        }
        String roomType = comingVo.getRoomType();
        int stayDays = comingVo.getStayDays();
        String checkInDate = comingVo.getCheckInDate();
        if(!TextUtils.isEmpty(roomType)){
            ((NoticeViewHolder) holder).tvClientNotice.setText("需要办理入住手续");
            ((NoticeViewHolder) holder).tvOrderInfo.setText(roomType+"|"+stayDays+"晚|"+checkInDate+"入住");
        }else{
            ((NoticeViewHolder) holder).tvOrderInfo.setText("无订单信息");
        }
        ((NoticeViewHolder) holder).tvTodo.setText("请做好迎接");
        ((NoticeViewHolder) holder).tvTimeInfo.setText(TimeUtil.getChatTime(comingVo.getArriveTime()));
        final String phoneNum = comingVo.getPhoneNum();
        ((NoticeViewHolder) holder).ivDianHua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.callPhone(context,phoneNum);
            }
        });
        ((NoticeViewHolder) holder).ivDuiHua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.startSendMessage("",phoneNum,context);
            }
        });

        ((NoticeViewHolder) holder).ivFenXiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.startSendMessage("", phoneNum, context);
            }
        });
        LinearLayout.LayoutParams contentLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if(position == 0){
            ((NoticeViewHolder) holder).upCutLineView.setVisibility(View.INVISIBLE);
            contentLayoutParams.setMargins(0,DisplayUtil.dip2px(context,12),DisplayUtil.dip2px(context,8),DisplayUtil.dip2px(context,6));
        }else if(position == getItemCount()-1){
            contentLayoutParams.setMargins(0, DisplayUtil.dip2px(context, 6), DisplayUtil.dip2px(context, 8), DisplayUtil.dip2px(context, 6));
            ((NoticeViewHolder) holder).upCutLineView.setVisibility(View.VISIBLE);
        }else{
            contentLayoutParams.setMargins(0, DisplayUtil.dip2px(context, 6), DisplayUtil.dip2px(context, 8), DisplayUtil.dip2px(context, 6));
            ((NoticeViewHolder) holder).upCutLineView.setVisibility(View.VISIBLE);
        }
        ((NoticeViewHolder) holder).contentLayout.setLayoutParams(contentLayoutParams);
    }

    @Override
    public int getItemCount() {
        return comingList.size();
    }

    public class NoticeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        View upCutLineView;
        CircleImageView civClientAvatar;
        TextView  tvVip;
        TextView  tvClientName;
        TextView  tvClientInfo;
        TextView  tvClientNotice ;
        TextView  tvTodo;
        TextView  tvOrderInfo;
        ImageView ivClock;
        TextView  tvTimeInfo;
        ImageView ivDianHua;
        ImageView ivDuiHua;
        ImageView ivFenXiang;
        LinearLayout contentLayout;

        public NoticeViewHolder(View view) {
            super(view);
            upCutLineView = (View)view.findViewById(R.id.time_axis_cut_line_up);
            civClientAvatar = (CircleImageView) view.findViewById(R.id.civ_client_avatar);
            tvVip           = (TextView)  view.findViewById(R.id.tv_vip);
            tvClientName    = (TextView)  view.findViewById(R.id.tv_client_name);
            tvClientInfo    = (TextView)  view.findViewById(R.id.tv_location_info);
            tvClientNotice  = (TextView)  view.findViewById(R.id.tv_client_notice);
            tvTodo          = (TextView)  view.findViewById(R.id.tv_to_do);
            tvOrderInfo     = (TextView)  view.findViewById(R.id.tv_order_info);
            ivClock         = (ImageView) view.findViewById(R.id.iv_clock);
            tvTimeInfo      = (TextView)  view.findViewById(R.id.tv_time_info);
            ivDianHua       = (ImageView) view.findViewById(R.id.iv_dianhua);
            ivDuiHua        = (ImageView) view.findViewById(R.id.iv_duihua);
            ivFenXiang      = (ImageView) view.findViewById(R.id.iv_fenxiang);
            contentLayout = (LinearLayout)view.findViewById(R.id.content_layout);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(null != itemClickListener){
                itemClickListener.onItemClick(v,getAdapterPosition());
            }
        }
    }

    public void setOnItemClickListener(RecyclerItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

}
