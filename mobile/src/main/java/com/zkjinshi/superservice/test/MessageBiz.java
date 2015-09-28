package com.zkjinshi.superservice.test;

import com.zkjinshi.superservice.vo.MessageVo;
import com.zkjinshi.superservice.vo.MimeType;

import java.util.ArrayList;

/**
 * 开发者：JimmyZhang
 * 日期：2015/9/28
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MessageBiz {

    public static ArrayList<MessageVo> getMessageList(){

        ArrayList<MessageVo> messageList = new ArrayList<MessageVo>();

        MessageVo messageVo = new MessageVo();
        messageVo.setMessageId(""+0);
        messageVo.setTitle("周杰伦");
        messageVo.setSendTime(System.currentTimeMillis());
        messageVo.setIsRead(false);
        messageVo.setContent("现在还有双人床吗?");
        messageVo.setMimeType(MimeType.TEXT);
        messageList.add(messageVo);

        messageVo = new MessageVo();
        messageVo.setMessageId(""+1);
        messageVo.setTitle("古天乐");
        messageVo.setSendTime(System.currentTimeMillis());
        messageVo.setIsRead(false);
        messageVo.setContent("您好，我要申请退房!");
        messageVo.setMimeType(MimeType.TEXT);
        messageList.add(messageVo);

        messageVo = new MessageVo();
        messageVo.setMessageId(""+2);
        messageVo.setTitle("黄晓明");
        messageVo.setSendTime(System.currentTimeMillis());
        messageVo.setIsRead(false);
        messageVo.setContent("我要一个豪华大床房!");
        messageVo.setMimeType(MimeType.TEXT);
        messageList.add(messageVo);

        messageVo = new MessageVo();
        messageVo.setMessageId(""+2);
        messageVo.setTitle("徐峥");
        messageVo.setSendTime(System.currentTimeMillis());
        messageVo.setIsRead(false);
        messageVo.setContent("港囧要热映，你知道吗?");
        messageVo.setMimeType(MimeType.TEXT);
        messageList.add(messageVo);

        return messageList;
    }
}
