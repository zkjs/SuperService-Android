package com.zkjinshi.superservice.utils;

import com.zkjinshi.base.config.ConfigUtil;

/**
 * 常量值工具类
 * 开发者：vincent
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class Constants {

    //选择图片
    public static final int FLAG_CHOOSE_IMG = 5;

    //拍照
    public static final int FLAG_CHOOSE_PHOTO = 6;

    //修改完成
    public static final int FLAG_MODIFY_FINISH = 7;

    public final static int POST_SUCCESS = 1;
    public final static int POST_FAILED  = 0;

    public static final int CHATTYPE_SINGLE = 1;
    public static final int CHATTYPE_GROUP = 2;
    public static final int CHATTYPE_CHATROOM = 3;

    public static final String EXTRA_CHAT_TYPE = "chatType";
    public static final String EXTRA_USER_ID = "userId";
    public static final String MSG_TXT_EXT_TYPE= "extType";

    //协议包返回结果
    public final static int PROTOCAL_SUCCESS = 0;
    public final static int PROTOCAL_FAILED  = 1;

    public final static String HTTP_URL = ConfigUtil.getInst().getHttpDomain();

    //头像前缀
    public static final String AVATAR_PRE_URL = "http://api.zkjinshi.com/uploads/users/";

    public final static String GET_USER_AVATAR    = HTTP_URL + "uploads/users/";

    public static final String EXTRA_VOICE_REPLY = "extra_voice_reply";//语音回复

    public static final String ACTION_VOICE_RELAY = "com.zkjinshi.superservice.intent.action.RELAY";

}
