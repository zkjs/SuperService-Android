package com.zkjinshi.superservice.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatRoom;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.facebook.drawee.view.SimpleDraweeView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.listener.RecyclerItemClickListener;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.EmotionType;
import com.zkjinshi.superservice.utils.EmotionUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.view.CircleImageView;
import com.zkjinshi.superservice.vo.MemberVo;
import com.zkjinshi.superservice.vo.TxtExtType;

import java.util.ArrayList;

/**
 * 消息通知适配器
 * 开发者：JimmyZhang
 * 日期：2015/9/28
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<EMConversation> conversationList;
    private ArrayList<MemberVo> memberList;
    private Context context;
    private LayoutInflater inflater;
    private RecyclerItemClickListener itemClickListener;

    public void setConversationList(ArrayList<EMConversation> conversationList) {
        if(null == conversationList){
            this.conversationList = new ArrayList<EMConversation>();
        }else {
            this.conversationList = conversationList;
        }
        notifyDataSetChanged();
    }

    public void setMemberList(ArrayList<MemberVo> memberList) {
        if(null == memberList){
            this.memberList = new ArrayList<MemberVo>();
        }else {
            this.memberList = memberList;
        }
        notifyDataSetChanged();
    }

    public MessageAdapter(Context context, ArrayList<EMConversation> conversationList){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.setConversationList(conversationList);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_message_center, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        //holder.setIsRecyclable(false);

        EMConversation conversation = conversationList.get(position);
        EMConversation.EMConversationType chatType = conversation.getType();
        String username = conversation.getUserName();
        if (conversation.getMsgCount() != 0) {
            EMMessage message = conversation.getLastMessage();
            long sendTime = message.getMsgTime();
            EMMessage.Type msgType = message.getType();
            if (msgType == EMMessage.Type.IMAGE) {
                ((ViewHolder)holder).contentTv.setText("[图片]");
            }else if (msgType ==  EMMessage.Type.VOICE) {
                ((ViewHolder)holder).contentTv.setText("[语音]");
            }else if(msgType == EMMessage.Type.TXT){
                try {
                    int extType = message.getIntAttribute(Constants.MSG_TXT_EXT_TYPE);
                    if(TxtExtType.CARD.getVlaue() == extType){
                        ((ViewHolder)holder).contentTv.setText("[订单]");
                    }else{
                        TextMessageBody txtBody = (TextMessageBody) message.getBody();
                        String content = txtBody.getMessage();
                        CharSequence txt = EmotionUtil.getInstance()
                                .convertStringToSpannable(context,
                                        content,
                                        EmotionType.CHAT_LIST);
                        ((ViewHolder)holder).contentTv.setText(txt);
                    }
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
            }
            String userId = message.getUserName();
            if(chatType == EMConversation.EMConversationType.Chat){
                if(!TextUtils.isEmpty(userId)){
                    String userImg = getUserImg(userId);
                    if(!TextUtils.isEmpty(userImg)){
                        int width = (int)context.getResources().getDimension(R.dimen.item_message_avatar_size);
                        String imageUrl = ProtocolUtil.getImageUrlByWidth(context,userImg,width);
                        ((ViewHolder)holder).photoImageView.setImageURI(Uri.parse(imageUrl));
                    }
                }
            }
            ((ViewHolder)holder).sendTimeTv.setText(TimeUtil.getChatTime(sendTime));
            if (conversation.getType() == EMConversation.EMConversationType.GroupChat) {
                EMGroup group = EMGroupManager.getInstance().getGroup(username);
                ((ViewHolder) holder).titleTv.setText(group != null ? group.getGroupName() : username);
            } else if(conversation.getType() == EMConversation.EMConversationType.ChatRoom){
                EMChatRoom room = EMChatManager.getInstance().getChatRoom(username);
                ((ViewHolder) holder).titleTv.setText(room != null && !TextUtils.isEmpty(room.getName()) ? room.getName() : username);
            }else {
                ((ViewHolder) holder).titleTv.setText(username);
                try {
                    String fromName = message.getStringAttribute("fromName");
                    String toName = message.getStringAttribute("toName");
                    if(!TextUtils.isEmpty(fromName) && !fromName.equals(CacheUtil.getInstance().getUserName())){
                        ((ViewHolder) holder).titleTv.setText(fromName);
                    }else{
                        if(!TextUtils.isEmpty(toName)){
                            ((ViewHolder) holder).titleTv.setText(toName);
                        }
                    }
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
            }
            //设置消息未读条数
            long notifyCount = conversation.getUnreadMsgCount();
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
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private SimpleDraweeView photoImageView;
        private TextView titleTv,contentTv,sendTimeTv,noticeCountTv;

        public ViewHolder(View itemView) {
            super(itemView);
            photoImageView = (SimpleDraweeView) itemView.findViewById(R.id.message_notice_photo_iv);
            titleTv = (TextView) itemView.findViewById(R.id.message_notice_title);
            contentTv = (TextView) itemView.findViewById(R.id.message_notice_content);
            sendTimeTv = (TextView) itemView.findViewById(R.id.message_notice_send_time);
            noticeCountTv = (TextView) itemView.findViewById(R.id.message_notice_notice_count);
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

    private String getUserImg(String userId){
        String userImg = null;
        if(null != memberList && !memberList.isEmpty()){
            String mUserId = null;
            for(MemberVo memberVo : memberList){
                mUserId = memberVo.getUserid();
                if(userId.equals(mUserId)){
                    userImg = memberVo.getUserimage();
                }
            }
        }
        return userImg;
    }
}
