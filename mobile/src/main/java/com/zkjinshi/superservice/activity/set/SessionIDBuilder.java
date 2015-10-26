package com.zkjinshi.superservice.activity.set;

import android.app.Activity;
import android.content.Intent;

import com.zkjinshi.superservice.activity.chat.ChatActivity;

/**
 * 开发者：vincent
 * 日期：2015/10/26
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SessionIDBuilder {

    private final static String TAG = SessionIDBuilder.class.getSimpleName();

    private static SessionIDBuilder instance = null;

    private SessionIDBuilder(){
    }

    public synchronized static SessionIDBuilder getInstance(){
        if(instance == null){
            instance = new SessionIDBuilder();
        }
        return instance;
    }

    /**
     * 构建内部单聊sessionID
     * @param shopID
     * @param mUserID
     * @param empID
     * @return
     */
    public String buildSingleSessionID(String shopID, String mUserID, String empID) {
        if(mUserID.compareTo(empID) < 0){
            return "single_" + shopID + "_" + mUserID + "_" + empID;
        }else if(mUserID.compareTo(empID) > 0){
            return "single_" + shopID + "_" + empID + "_" + mUserID;
        } else {
            return null;
        }
    }

    /**
     * 进入聊天界面
     * @param mActivity
     * @param mShopID
     * @param sessionID
     * @param sessionName
     */
    public void goSession(Activity mActivity, String mShopID, String sessionID, String sessionName) {
        //开启单聊界面
        Intent goSession = new Intent(mActivity, ChatActivity.class);
        goSession.putExtra("shop_id", mShopID);
        goSession.putExtra("session_id", sessionID);
        goSession.putExtra("session_name", sessionName);
        mActivity.startActivity(goSession);
    }

}
