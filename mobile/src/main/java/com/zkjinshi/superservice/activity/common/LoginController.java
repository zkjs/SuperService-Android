package com.zkjinshi.superservice.activity.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.notice.LocNoticeController;
import com.zkjinshi.superservice.activity.set.TeamContactsController;
import com.zkjinshi.superservice.bean.AdminLoginBean;
import com.zkjinshi.superservice.bean.SempLoginBean;
import com.zkjinshi.superservice.bean.TeamContactBean;
import com.zkjinshi.superservice.factory.ShopEmployeeFactory;
import com.zkjinshi.superservice.factory.UserFactory;
import com.zkjinshi.superservice.listener.GetTeamContactsListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestListener;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.sqlite.DBOpenHelper;
import com.zkjinshi.superservice.sqlite.ShopDepartmentDBUtil;
import com.zkjinshi.superservice.sqlite.ShopEmployeeDBUtil;
import com.zkjinshi.superservice.sqlite.UserDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.DepartmentVo;
import com.zkjinshi.superservice.vo.IdentityType;
import com.zkjinshi.superservice.vo.ShopEmployeeVo;
import com.zkjinshi.superservice.vo.UserVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    /**
     * 服务员请求登录
     * @param phone
     */
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
                    CacheUtil.getInstance().setShopFullName(sempLoginbean.getFullname());
                    CacheUtil.getInstance().setLoginIdentity(IdentityType.WAITER);

                    String userID = CacheUtil.getInstance().getUserId();
                    String token  = CacheUtil.getInstance().getToken();
                    String shopiD = CacheUtil.getInstance().getShopID();

                    DBOpenHelper.DB_NAME = sempLoginbean.getSalesid() + ".db";

                    getDeptList(userID, token, shopiD);//获取部门列表
                    TeamContactsController.getInstance().getTeamContacts(context, userID, token, shopiD, null);//获取团队列表
                    LocNoticeController.getInstance().init(context).requestLocTask();//获取区域信息

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
        netRequestTask.isShowLoadingDialog = false;
        netRequestTask.execute();
    }

    /**
     * 管理员请求登录
     * @param phone
     */
    public void requestAdminLogin(final String phone,final String password){
        String url = ProtocolUtil.getAdminLoginUrl();
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("phone",phone);
        bizMap.put("password", password);
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
                AdminLoginBean adminLoginBean = new Gson().fromJson(result.rawResult, AdminLoginBean.class);
                if (adminLoginBean.isSet()) {
                    //更新为最新的token和userid
                    CacheUtil.getInstance().setToken(adminLoginBean.getToken());
                    CacheUtil.getInstance().setUserId(adminLoginBean.getUserid());
                    CacheUtil.getInstance().setUserPhone(phone);
                    CacheUtil.getInstance().setUserName(adminLoginBean.getName());
                    CacheUtil.getInstance().setShopID(adminLoginBean.getShopid());
                    CacheUtil.getInstance().setShopFullName(adminLoginBean.getFullname());
                    CacheUtil.getInstance().setLoginIdentity(IdentityType.BUSINESS);
                    CacheUtil.getInstance().setPassword(password);
                    CacheUtil.getInstance().setLogin(true);
                    CacheUtil.getInstance().setAreaInfo(adminLoginBean.getLocid());

                    String userID = CacheUtil.getInstance().getUserId();
                    String token  = CacheUtil.getInstance().getToken();
                    String shopiD = CacheUtil.getInstance().getShopID();

                    DBOpenHelper.DB_NAME = adminLoginBean.getUserid() + ".db";

                    getDeptList(userID, token, shopiD);//获取部门列表
                    TeamContactsController.getInstance().getTeamContacts(context, userID, token, shopiD, null);//获取团队列表
                    LocNoticeController.getInstance().init(context).requestLocTask();//获取区域信息

                    UserVo userVo = UserFactory.getInstance().buildUserVo(adminLoginBean);
                    UserDBUtil.getInstance().addUser(userVo);
                    String avatarUrl = ProtocolUtil.getShopLogoUrl(adminLoginBean.getShopid());
                    CacheUtil.getInstance().saveUserPhotoUrl(avatarUrl);

                    Intent intent = new Intent(activity, MainActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                    activity.overridePendingTransition(R.anim.activity_new, R.anim.activity_out);

                } else {
                    DialogUtil.getInstance().showToast(activity, "密码或者手机号不对 ");
                    Intent intent = new Intent(activity, ShopLoginActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                    activity.overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
                }

            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = false;
        netRequestTask.execute();
    }

    /**
     * 获取团队联系人列表
     * @param userID
     * @param token
     * @param shopID
     */
    public void getDeptList(String userID, String token, final String shopID) {
        NetRequest netRequest = new NetRequest(ProtocolUtil.getDeptListUrl());
        HashMap<String,String> bizMap = new HashMap<>();
        bizMap.put("salesid", userID);
        bizMap.put("token", token);
        bizMap.put("shopid", shopID);
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(context, netRequest, NetResponse.class);
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
                String jsonResult = result.rawResult;
                if (result.rawResult.contains("set") || jsonResult.contains("err")) {
                    return ;
                } else {
                    Gson gson = new Gson();
                    List<DepartmentVo> departmentVos = gson.fromJson(jsonResult,
                            new TypeToken<ArrayList<DepartmentVo>>() {}.getType());
                    /** add to local db */
                    if(null != departmentVos && departmentVos.isEmpty()){
                        ShopDepartmentDBUtil.getInstance().batchAddShopDepartments(departmentVos);
                    }
                }
            }

            @Override
            public void beforeNetworkRequestStart()     {
                //网络请求前
            }
        });
        netRequestTask.isShowLoadingDialog = false;
        netRequestTask.execute();
    }

}
