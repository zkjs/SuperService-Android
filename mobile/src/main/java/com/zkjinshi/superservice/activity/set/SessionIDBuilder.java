package com.zkjinshi.superservice.activity.set;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.zkjinshi.superservice.activity.chat.ChatActivity;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;

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
     * @param userId
     * @param toName
     * @param shopId
     * @param shopName
     */
    public void goSession(Activity mActivity, String userId,String toName,String shopId,String shopName) {
        //开启单聊界面
        Intent intent = new Intent(mActivity, ChatActivity.class);
        intent.putExtra(Constants.EXTRA_USER_ID, userId);
        intent.putExtra(Constants.EXTRA_FROM_NAME, CacheUtil.getInstance().getUserName());
        intent.putExtra(Constants.EXTRA_TO_NAME,toName);
        if (!TextUtils.isEmpty(shopId)) {
            intent.putExtra(Constants.EXTRA_SHOP_ID,shopId);
        }
        if (!TextUtils.isEmpty(shopName)) {
            intent.putExtra(Constants.EXTRA_SHOP_NAME,shopName);
        }
        mActivity.startActivity(intent);
    }

}
