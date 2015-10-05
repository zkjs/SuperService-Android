package com.zkjinshi.superservice.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.listener.RecyclerItemClickListener;
import com.zkjinshi.superservice.vo.MessageVo;
import com.zkjinshi.superservice.vo.MimeType;

import java.util.ArrayList;

/**
 * 消息通知适配器
 * 开发者：JimmyZhang
 * 日期：2015/9/28
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<MessageVo> messageList;
    private Context context;
    private LayoutInflater inflater;
    private DisplayImageOptions options;
    private RecyclerItemClickListener itemClickListener;

    public void setMessageList(ArrayList<MessageVo> messageList) {
        if(null ==  messageList){
            this.messageList = new ArrayList<MessageVo>();
        }else{
            this.messageList = messageList;
        }
    }

    public MessageAdapter(Context context,ArrayList<MessageVo> messageList){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.setMessageList(messageList);
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
        View view = inflater.inflate(R.layout.item_message_center, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MessageVo messageVo = messageList.get(position);
        long sendTime = messageVo.getSendTime();
        String content = messageVo.getContent();
        MimeType mimeType = messageVo.getMimeType();
        String title = messageVo.getTitle();
        String imageUrl = messageVo.getUrl();
        if(!TextUtils.isEmpty(title)){
            ((ViewHolder)holder).titleTv.setText(title);
        }
        if(MimeType.TEXT == mimeType){
            if(!TextUtils.isEmpty(content)){
                ((ViewHolder)holder).contentTv.setText(content);
            }
        }else if(MimeType.IMAGE == mimeType){
            ((ViewHolder)holder).contentTv.setText("[图片]");
        }else if(MimeType.VIDEO == mimeType){
            ((ViewHolder)holder).contentTv.setText("[视频]");
        }else if(MimeType.AUDIO == mimeType){
            ((ViewHolder)holder).contentTv.setText("[语音]");
        }else if(MimeType.APPLICATION == mimeType){
            ((ViewHolder)holder).contentTv.setText("[文件]");
        }else if(MimeType.CARD == mimeType){
            ((ViewHolder)holder).contentTv.setText("[订单信息]");
        }
        ((ViewHolder)holder).sendTimeTv.setText(TimeUtil.getChatTime(sendTime));
        if(!TextUtils.isEmpty(imageUrl)){
            ImageLoader.getInstance().displayImage(imageUrl, ((ViewHolder)holder).photoImageView,options);
        }
        int notifyCount = 0;
        if(notifyCount <= 0){
            ((ViewHolder)holder).noticeCountTv.setVisibility(View.GONE);
        }else if(notifyCount > 99) {
            ((ViewHolder)holder).noticeCountTv.setVisibility(View.VISIBLE);
            ((ViewHolder)holder).noticeCountTv.setText("99+");
        } else {
            ((ViewHolder)holder).noticeCountTv.setVisibility(View.VISIBLE);
            ((ViewHolder)holder).noticeCountTv.setText(notifyCount+"");
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView photoImageView;
        private TextView titleTv,contentTv,sendTimeTv,noticeCountTv;

        public ViewHolder(View itemView) {
            super(itemView);
            photoImageView = (ImageView) itemView.findViewById(R.id.message_notice_photo_iv);
            titleTv = (TextView) itemView.findViewById(R.id.message_notice_title);
            contentTv = (TextView) itemView.findViewById(R.id.message_notice_content);
            sendTimeTv = (TextView) itemView.findViewById(R.id.message_notice_send_time);
            noticeCountTv = (TextView) itemView.findViewById(R.id.message_notice_notice_count);
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
