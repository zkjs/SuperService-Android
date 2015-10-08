package com.zkjinshi.superservice.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.listener.RecyclerItemClickListener;
import com.zkjinshi.superservice.sqlite.ChatRoomDBUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.view.CircleImageView;
import com.zkjinshi.superservice.vo.ChatRoomVo;
import com.zkjinshi.superservice.vo.LatestClientVo;
import com.zkjinshi.superservice.vo.MessageVo;
import com.zkjinshi.superservice.vo.MimeType;

import java.util.ArrayList;

/**
 * 到店通知底部更多适配器
 * 开发者：JimmyZhang
 * 日期：2015/9/28
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class LocMoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<LatestClientVo> latestClientList;
    private Context context;
    private LayoutInflater inflater;
    private DisplayImageOptions options;
    private RecyclerItemClickListener itemClickListener;

    public void setLatestClientList(ArrayList<LatestClientVo> latestClientList) {
        if(null == latestClientList){
            this.latestClientList = new ArrayList<LatestClientVo>();
        }else{
            this.latestClientList = latestClientList;
        }
        notifyDataSetChanged();
    }

    public LocMoreAdapter(Context context,ArrayList<LatestClientVo> messageList){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.setLatestClientList(messageList);
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
        View view = inflater.inflate(R.layout.item_notify_more_list, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        LatestClientVo messageVo = latestClientList.get(position);
        String userId = messageVo.getUserID();
        if(!TextUtils.isEmpty(userId)){
            String imageUrl = Constants.GET_USER_AVATAR + userId + ".jpg";
            if(!TextUtils.isEmpty(imageUrl)){
                ImageLoader.getInstance().displayImage(imageUrl, ((ViewHolder)holder).photoImageView,options);
            }
        }
        if(position == 0){
            ((ViewHolder)holder).moreLayout.setPadding(DisplayUtil.dip2px(context,15),DisplayUtil.dip2px(context,5),DisplayUtil.dip2px(context,5),DisplayUtil.dip2px(context,5));
        }else if(position == getItemCount()-1){
            ((ViewHolder)holder).moreLayout.setPadding(DisplayUtil.dip2px(context,5),DisplayUtil.dip2px(context,5),DisplayUtil.dip2px(context,15),DisplayUtil.dip2px(context,5));
        }else{
            ((ViewHolder)holder).moreLayout.setPadding(DisplayUtil.dip2px(context,5),DisplayUtil.dip2px(context,5), DisplayUtil.dip2px(context, 5),DisplayUtil.dip2px(context,5));
        }
    }

    @Override
    public int getItemCount() {
        return latestClientList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private CircleImageView photoImageView;
        private LinearLayout moreLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            photoImageView = (CircleImageView) itemView.findViewById(R.id.notify_more_cv_photo);
            moreLayout = (LinearLayout)itemView.findViewById(R.id.more_layout);
            itemView.setOnClickListener(this);
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
