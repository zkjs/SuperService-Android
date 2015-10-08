package com.zkjinshi.superservice.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.view.CircleStatusView;
import com.zkjinshi.superservice.vo.LatestClientVo;
import com.zkjinshi.superservice.view.CircleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * 开发者：vincent
 * 日期：2015/9/25
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class LocNotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mActivity;

    private List<LatestClientVo>  mList;
    private DisplayImageOptions     options;
    private TypedArray              locNoticeColors;

    public LocNotificationAdapter(Activity activity, List<LatestClientVo> list) {

        this.mActivity = activity;
        this.mList     = list;
        locNoticeColors = activity.getResources().obtainTypedArray(R.array.loc_notice_colors);
        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.img_hotel_zhanwei)// 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.img_hotel_zhanwei)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.img_hotel_zhanwei)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_loc_notification, null);
        NoticeViewHolder localHolder = new NoticeViewHolder(view);
        return localHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        LatestClientVo latestClientVo = mList.get(position);

        String userId = latestClientVo.getUserID();
        String imageUrl = Constants.GET_USER_AVATAR + userId + ".jpg";
        if(!TextUtils.isEmpty(imageUrl)){
            ImageLoader.getInstance().displayImage(imageUrl, ((NoticeViewHolder) holder).civClientAvatar, options);
        }
        //((NoticeViewHolder) holder).tvVip.setText("VIP1");
        ((NoticeViewHolder) holder).tvClientName.setText(latestClientVo.getUserName());
        //((NoticeViewHolder) holder).tvClientInfo.setText(latestClientVo.getUserID());
        //((NoticeViewHolder) holder).tvClientNotice.setText("VIP#");
        //((NoticeViewHolder) holder).tvTodo.setText("VIP#");
        //((NoticeViewHolder) holder).tvOrderInfo.setText("VIP#");
        ((NoticeViewHolder) holder).tvTimeInfo.setText(TimeUtil.getChatTime(latestClientVo.getTimeStamp()));

        ((NoticeViewHolder) holder).ivDianHua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.getInstance().showToast(mActivity, "ivDianHua onClick");
            }
        });

        ((NoticeViewHolder) holder).ivDuiHua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.getInstance().showToast(mActivity, "ivDuiHua onClick");
            }
        });

        ((NoticeViewHolder) holder).ivFenXiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.getInstance().showToast(mActivity, "ivFenXiang onClick");
            }
        });
        ((NoticeViewHolder) holder).ibtnOrderStatus.setStatus(CircleStatusView.CircleStatus.STATUS_FINISH);
        ((NoticeViewHolder) holder).ibtnOrderStatus.invalidate();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class NoticeViewHolder extends RecyclerView.ViewHolder{

        CircleStatusView ibtnOrderStatus;
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

        public NoticeViewHolder(View view) {
            super(view);
            ibtnOrderStatus = (CircleStatusView) view.findViewById(R.id.ibtn_order_status);
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
        }
    }

    public void setData(List<LatestClientVo> latestClientBeans) {
        if (null == latestClientBeans) {
            this.mList = new ArrayList<>();
        } else {
            this.mList = latestClientBeans;
        }
        notifyDataSetChanged();
    }
}
