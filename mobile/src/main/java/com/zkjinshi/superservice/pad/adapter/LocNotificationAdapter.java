package com.zkjinshi.superservice.pad.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.base.util.IntentUtil;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.superservice.pad.listener.RecyclerItemClickListener;
import com.zkjinshi.superservice.pad.utils.CacheUtil;
import com.zkjinshi.superservice.pad.utils.Constants;
import com.zkjinshi.superservice.pad.utils.ProtocolUtil;
import com.zkjinshi.superservice.pad.vo.OrderVo;
import com.zkjinshi.superservice.pad.R;
import com.zkjinshi.superservice.pad.activity.chat.single.ChatActivity;
import com.zkjinshi.superservice.pad.vo.NoticeVo;

import java.util.ArrayList;

/**
 * 开发者：WinkyQin
 * 日期：2015/9/25
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class LocNotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    private ArrayList<NoticeVo> noticeList;
    private RecyclerItemClickListener itemClickListener;

    public void setNoticeList(ArrayList<NoticeVo> noticeList) {
        if(null == noticeList){
            this.noticeList = new ArrayList<>();
        }else{
            this.noticeList = noticeList;
        }
        notifyDataSetChanged();
    }

    public LocNotificationAdapter(Activity activity, ArrayList<NoticeVo> comingList) {

        this.context = activity;
        this.setNoticeList(comingList);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_loc_notification, null);
        NoticeViewHolder localHolder = new NoticeViewHolder(view);
        return localHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        NoticeVo noticeVo = noticeList.get(position);
        final String userId   = noticeVo.getUserid();
        final String userName = noticeVo.getUsername();
        String userImage = noticeVo.getUserimage();
        int width = (int)context.getResources().getDimension(R.dimen.normal_avatar_size);
        String imageUrl = ProtocolUtil.getImageUrlByWidth(context,userImage,width);
        if(!TextUtils.isEmpty(imageUrl)){
            ((NoticeViewHolder) holder).civClientAvatar.setImageURI(Uri.parse(imageUrl));
            if(!TextUtils.isEmpty(userName)){
                ((NoticeViewHolder) holder).tvClientName.setText(userName);
            } else {
                ((NoticeViewHolder) holder).tvClientName.setText(userId);
            }
        }

        String locid = noticeVo.getLocid();
        if(!TextUtils.isEmpty(locid)){
            String locName = noticeVo.getLocdesc();
            if(TextUtils.isEmpty(locName)){
                locName = context.getString(R.string.location) + locid;
            }
            ((NoticeViewHolder) holder).tvLocationInfo.setText(context.getString(R.string.arrive) + locName);
        }

        //获取订单信息并显示基本信息
        ArrayList<OrderVo> orderList = noticeVo.getOrders();
        if(null != orderList && !orderList.isEmpty()){
            final OrderVo orderVo = orderList.get(0);
            if(null != orderVo){
                String roomType = orderVo.getRoom();
                String inDate = orderVo.getIndate();
                long checkInDate = TimeUtil.timeStrToTimeStamp(inDate);
                if(!TextUtils.isEmpty(roomType)){
                    String orderStr = roomType+" | "+TimeUtil.getMonthTime(checkInDate)+"入住";
                    ((NoticeViewHolder) holder).tvOrderInfo.setText(orderStr);
                }else{
                    ((NoticeViewHolder) holder).tvOrderInfo.setVisibility(View.GONE);
                    ((NoticeViewHolder) holder).ivOrderInfo.setVisibility(View.GONE);
                }
            }
        } else {
            ((NoticeViewHolder) holder).tvOrderInfo.setVisibility(View.GONE);
            ((NoticeViewHolder) holder).ivOrderInfo.setVisibility(View.GONE);
        }

        String timeCreated = noticeVo.getArrivetime();
        if(!TextUtils.isEmpty(timeCreated)){
            ((NoticeViewHolder) holder).tvTimeInfo.setText(TimeUtil.getNoticeTime(timeCreated));
        }

        final String phoneNum = noticeVo.getPhone();
        ((NoticeViewHolder) holder).llPhoneCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.callPhone(context, phoneNum);
            }
        });

        ((NoticeViewHolder) holder).llChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                if(!TextUtils.isEmpty(userId)){
                    intent.putExtra(Constants.EXTRA_USER_ID, userId);
                }
                String shopID = CacheUtil.getInstance().getShopID();
                if (!TextUtils.isEmpty(shopID)) {
                    intent.putExtra(Constants.EXTRA_SHOP_ID, shopID);
                }

                intent.putExtra(Constants.EXTRA_SHOP_NAME, CacheUtil.getInstance().getShopFullName());

                if (!TextUtils.isEmpty(userName)) {
                    intent.putExtra(Constants.EXTRA_TO_NAME, userName);
                }
                intent.putExtra(Constants.EXTRA_FROM_NAME, CacheUtil.getInstance().getUserName());
                context.startActivity(intent);
            }
        });

        LinearLayout.LayoutParams contentLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        int rightDp = DisplayUtil.dip2px(context, context.getResources().getDimension(R.dimen.item_notice_right_padding_size));
        int topdp   = DisplayUtil.dip2px(context, context.getResources().getDimension(R.dimen.item_notice_top_padding_size));

        if(position == 0){
            ((NoticeViewHolder) holder).upCutLineView.setVisibility(View.INVISIBLE);
            contentLayoutParams.setMargins(0, 2*topdp,rightDp, topdp);
        }else if(position == getItemCount()-1){
            contentLayoutParams.setMargins(0, topdp, rightDp, topdp);
            ((NoticeViewHolder) holder).upCutLineView.setVisibility(View.VISIBLE);
        }else{
            contentLayoutParams.setMargins(0, topdp, rightDp, topdp);
            ((NoticeViewHolder) holder).upCutLineView.setVisibility(View.VISIBLE);
        }
        ((NoticeViewHolder) holder).contentLayout.setLayoutParams(contentLayoutParams);
    }

    @Override
    public int getItemCount() {
        return noticeList.size();
    }

    public class NoticeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        View            upCutLineView;
        SimpleDraweeView civClientAvatar;
        TextView        tvClientName;
        TextView        tvLocationInfo;
        TextView        tvOrderInfo;
        ImageView       ivOrderInfo;
        TextView        tvTimeInfo;
        LinearLayout    llChat;
        LinearLayout    llPhoneCall;
        LinearLayout    contentLayout;

        public NoticeViewHolder(View view) {
            super(view);
            upCutLineView   = view.findViewById(R.id.time_axis_cut_line_up);
            civClientAvatar = (SimpleDraweeView) view.findViewById(R.id.civ_client_avatar);
            tvClientName    = (TextView)  view.findViewById(R.id.tv_client_name);
            tvLocationInfo  = (TextView)  view.findViewById(R.id.tv_location_info);
            tvOrderInfo     = (TextView)  view.findViewById(R.id.tv_order_info);
            ivOrderInfo     = (ImageView) view.findViewById(R.id.iv_order_info);
            tvTimeInfo      = (TextView)  view.findViewById(R.id.tv_time_info);
            llPhoneCall     = (LinearLayout)view.findViewById(R.id.ll_phone_call);
            llChat          = (LinearLayout)view.findViewById(R.id.ll_chat);
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