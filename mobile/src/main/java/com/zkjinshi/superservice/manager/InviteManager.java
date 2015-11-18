package com.zkjinshi.superservice.manager;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;

/**
 * 邀请加入管理器
 * (暂时处理订单确认和订单取消)
 * 开发者：JimmyZhang
 * 日期：2015/11/16
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class InviteManager {

    private InviteManager(){}

    private static InviteManager instance;

    private EMMessage message;

    public synchronized static InviteManager getInstance(){
        if(null == instance){
            instance = new InviteManager();
        }
        return instance;
    }

    public synchronized void receiveInviteCmdMessage(EMNotifierEvent event,final Context context){
        switch (event.getEvent()) {
            case EventNewCMDMessage:{//接收透传消息
                try {
                    message = (EMMessage) event.getData();
                    CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
                    String aciton = cmdMsgBody.action;
                    if(!TextUtils.isEmpty(aciton) && "inviteAdd".equals(aciton)){
                        String userId = message.getStringAttribute("userId");
                        String userName = message.getStringAttribute("userName");
                        String mobileNo = message.getStringAttribute("mobileNo");
                        String date = message.getStringAttribute("date");
                        Intent intent = new Intent();
                        intent.setAction("com.zkjinshi.svip.ACTION_INVITE");
                        intent.putExtra("userId", userId);
                        intent.putExtra("userName", userName);
                        intent.putExtra("mobileNo", mobileNo);
                        if(!TextUtils.isEmpty(date)){
                            intent.putExtra("date",Long.parseLong(date));
                        }
                        context.sendBroadcast(intent);
                    }
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
