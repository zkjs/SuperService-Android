package com.zkjinshi.superservice.pad.utils;

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

    public static final String EXTRA_CHAT_TYPE = "chatType";
    public static final String EXTRA_USER_ID = "userId";
    public static final String EXTRA_FROM_NAME = "fromName";
    public static final String EXTRA_TO_NAME = "toName";
    public static final String EXTRA_SHOP_ID = "shopId";
    public static final String EXTRA_SHOP_NAME = "shopName";
    public static final String EXTRA_TXT_MSG_CONTENT = "txtMsgContent";
    public static final String MSG_TXT_EXT_TYPE= "extType";

    public static final String EXTRA_VOICE_REPLY = "extra_voice_reply";//语音回复

    public static final String ACTION_VOICE_RELAY = "com.zkjinshi.superservice.pad.intent.action.RELAY";

    public static final String ACTION_NOTICE = "com.zkjinshi.superservice.pad.intent.action.NOTICE";

    public static final String WECHAT_APP_ID = "wx55cd1d05f22990a0";

    public static final int OVERTIMEOUT = 5000;
}
