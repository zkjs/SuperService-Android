package com.zkjinshi.superservice.emchat;

import android.util.Log;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.superservice.emchat.observer.EMessageListener;
import com.zkjinshi.superservice.utils.CacheUtil;

/**
 * 开发者：JimmyZhang
 * 日期：2016/3/15
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class EasemobIMManager {

    public static final String TAG = EasemobIMManager.class.getSimpleName();

    private EasemobIMManager(){}

    private static EasemobIMManager instance;

    public synchronized static EasemobIMManager getInstance(){
        if(null == instance){
            instance = new EasemobIMManager();
        }
        return instance;
    }

    /**
     * 登录环信IM
     */
    public void loginHxUser(){
        EasemobIMHelper.getInstance().loginUser(CacheUtil.getInstance().getUserId(), "123456", new EMCallBack() {
            @Override
            public void onSuccess() {
                // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                EMGroupManager.getInstance().loadAllGroups();
                EMChatManager.getInstance().loadAllConversations();
                EMessageListener.getInstance().registerEventListener();
                EMConversationHelper.getInstance().requestGroupListTask();
                EMChatManager.getInstance().updateCurrentUserNick(CacheUtil.getInstance().getUserName());
            }

            @Override
            public void onError(int i, String s) {
                Log.i(TAG, "环信登录失败-errorCode:" + i);
                Log.i(TAG, "环信登录失败-errorMessage:" + s);
                LogUtil.getInstance().info(LogLevel.ERROR,"环信登录失败-errorCode:" + i);
                LogUtil.getInstance().info(LogLevel.ERROR,"环信登录失败-errorMessage:" + s);
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }
}
