package com.zkjinshi.superservice.activity.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.bean.SempLoginBean;
import com.zkjinshi.superservice.factory.UserFactory;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestListener;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.sqlite.DBOpenHelper;
import com.zkjinshi.superservice.sqlite.UserDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.UserVo;

import java.util.HashMap;

/**
 * 开发者：dujiande
 * 日期：2015/9/29
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class LoginController {

    private final static String TAG = LoginController.class.getSimpleName();

    private LoginController(){}
    private static LoginController instance;
    private Context context;
    private Activity activity;

    public static synchronized LoginController getInstance(){
        if(null ==  instance){
            instance = new LoginController();
        }
        return instance;
    }

    public void init(Context context){
        this.context = context;
        this.activity = (Activity)context;

    }

    /*
* 请求登录
* */
    public void requestLogin(final String phone){
        NetRequest netRequest = new NetRequest(ProtocolUtil.getSempLoginUrl());
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("phone",phone);
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(activity,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new NetRequestListener() {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
            }

            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                SempLoginBean sempLoginbean = new Gson().fromJson(result.rawResult, SempLoginBean.class);
                if (sempLoginbean.isSet()) {
                    //更新为最新的token和userid
                    CacheUtil.getInstance().setToken(sempLoginbean.getToken());
                    CacheUtil.getInstance().setUserId(sempLoginbean.getSalesid());
                    CacheUtil.getInstance().setUserPhone(phone);
                    CacheUtil.getInstance().setUserName(sempLoginbean.getName());
                    CacheUtil.getInstance().setShopID(sempLoginbean.getShopid());

                    DBOpenHelper.DB_NAME = sempLoginbean.getSalesid() + ".db";
                    UserVo userVo = UserFactory.getInstance().buildUserVo(sempLoginbean);
                    UserDBUtil.getInstance().addUser(userVo);
                    String avatarUrl = Constants.AVATAR_PRE_URL+userVo.getUserId()+".jpg";
                    CacheUtil.getInstance().saveUserPhotoUrl(avatarUrl);
                    if (CacheUtil.getInstance().isLogin()) {
                        Intent intent = new Intent(activity, MainActivity.class);
                        activity.startActivity(intent);
                        activity.finish();
                        activity.overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
                    } else {
                        Intent intent = new Intent(activity, MoreActivity.class);
                        activity.startActivity(intent);
                        activity.finish();
                        activity.overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
                    }

                } else {
                    DialogUtil.getInstance().showToast(activity, "手机号还不是服务员 ");
                }

            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }
}
