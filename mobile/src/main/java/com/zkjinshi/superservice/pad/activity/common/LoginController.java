package com.zkjinshi.superservice.pad.activity.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.pad.emchat.EasemobIMManager;
import com.zkjinshi.superservice.pad.manager.SSOManager;
import com.zkjinshi.superservice.pad.manager.YunBaSubscribeManager;
import com.zkjinshi.superservice.pad.net.ExtNetRequestListener;
import com.zkjinshi.superservice.pad.net.MethodType;
import com.zkjinshi.superservice.pad.net.NetRequest;
import com.zkjinshi.superservice.pad.net.NetRequestTask;
import com.zkjinshi.superservice.pad.net.NetResponse;
import com.zkjinshi.superservice.pad.sqlite.DBOpenHelper;
import com.zkjinshi.superservice.pad.sqlite.ShopDepartmentDBUtil;
import com.zkjinshi.superservice.pad.utils.AsyncHttpClientUtil;
import com.zkjinshi.superservice.pad.utils.CacheUtil;
import com.zkjinshi.superservice.pad.utils.Constants;
import com.zkjinshi.superservice.pad.utils.ProtocolUtil;
import com.zkjinshi.superservice.pad.vo.DepartmentVo;
import com.zkjinshi.superservice.pad.vo.IdentityType;
import com.zkjinshi.superservice.pad.vo.PayloadVo;
import com.zkjinshi.superservice.pad.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

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
     * 获取部门列表
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
        netRequestTask.setNetRequestListener(
            new ExtNetRequestListener(activity) {
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
                super.onNetworkResponseSucceed(result);
                try {
                    Log.i(TAG, "result.rawResult:" + result.rawResult);
                    String jsonResult = result.rawResult;
                    Gson gson = new Gson();
                    List<DepartmentVo> departmentVos = gson.fromJson(jsonResult,
                            new TypeToken<ArrayList<DepartmentVo>>() {}.getType());
                    /** add to local db */
                    if(null != departmentVos && departmentVos.isEmpty()){
                        ShopDepartmentDBUtil.getInstance().batchAddShopDepartments(departmentVos);
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
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


    public interface CallBackListener{
        public void successCallback(JSONObject response);
    }

    public interface CallBackExtListener{
        public void successCallback(JSONObject response);
        public void failCallback(JSONObject response);
    }

    /**
     * 通过验证码获取token
     * @param mContext
     * @param phone
     * @param code
     * @param callBackListener
     */
    public void getTokenByCode(final Context mContext,final String phone,final String code,final CallBackListener callBackListener){
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("phone",phone);
            jsonObject.put("code",code);
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.ssoToken();
            client.post(mContext,url, stringEntity, "application/json", new JsonHttpResponseHandler(){
                public void onStart(){
                    super.onStart();
                    DialogUtil.getInstance().showAvatarProgressDialog(mContext,"");
                }

                public void onFinish(){
                    super.onFinish();
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                    super.onSuccess(statusCode,headers,response);
                    try {
                        if(response.getInt("res") == 0){
                            if(callBackListener != null){
                                callBackListener.successCallback(response);
                            }
                        }else if(response.getInt("res") == 11){//资料不全
                            String token = response.getString("token");
                            CacheUtil.getInstance().setExtToken(token);
                            PayloadVo payloadVo = SSOManager.getInstance().decodeToken(token);
                            CacheUtil.getInstance().setUserId(payloadVo.getSub());
                            CacheUtil.getInstance().setShopID(payloadVo.getShopid());
                            CacheUtil.getInstance().setLoginIdentity(IdentityType.WAITER);
                            DBOpenHelper.DB_NAME = payloadVo.getSub() + ".db";
                            Intent intent;
                            intent = new Intent(mContext, MoreActivity.class);
                            mContext.startActivity(intent);
                            if(mContext instanceof Activity){
                                Activity activity = (Activity)mContext;
                                activity.finish();
                                activity.overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
                            }

                        }
                        else{
                            Toast.makeText(mContext,response.getString("resDesc"),Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse){
                    super.onFailure(statusCode,headers,throwable,errorResponse);
                    AsyncHttpClientUtil.onFailure(mContext,statusCode);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 使用用户名密码创建Token
     * @param mContext
     * @param name
     * @param password
     * @param callBackListener
     */
    public void getTokenByPassword(final Context mContext,final String name,final String password,final CallBackListener callBackListener){
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username",name);
            jsonObject.put("password",password);
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.ssoPasswordGetToken();
            client.post(mContext,url, stringEntity, "application/json", new JsonHttpResponseHandler(){
                public void onStart(){
                    super.onStart();
                    DialogUtil.getInstance().showAvatarProgressDialog(mContext,"");
                }

                public void onFinish(){
                    super.onFinish();
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                    super.onSuccess(statusCode,headers,response);
                    try {
                        if(response.getInt("res") == 0){
                            if(callBackListener != null){
                                callBackListener.successCallback(response);
                            }
                        }else{
                            Toast.makeText(mContext,response.getString("resDesc"),Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse){
                    super.onFailure(statusCode,headers,throwable,errorResponse);
                    AsyncHttpClientUtil.onFailure(mContext,statusCode);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 验证原始密码是否正确
     * @param mContext
     * @param originalpassword
     * @param callBackExtListener
     */
    public void vertifyLoginPassword(final Context mContext,final String originalpassword,final CallBackExtListener callBackExtListener){
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            if(CacheUtil.getInstance().isLogin()){
                client.addHeader("Token",CacheUtil.getInstance().getExtToken());
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("originalpassword",originalpassword);
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.verifyLoginpassword();
            client.post(mContext,url, stringEntity, "application/json", new JsonHttpResponseHandler(){
                public void onStart(){
                    super.onStart();
                    DialogUtil.getInstance().showAvatarProgressDialog(mContext,"");
                }

                public void onFinish(){
                    super.onFinish();
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                    super.onSuccess(statusCode,headers,response);
                    try {
                        if(response.getInt("res") == 0){
                            if(callBackExtListener != null){
                                callBackExtListener.successCallback(response);
                            }
                        }else{
                            callBackExtListener.failCallback(response);
                            //Toast.makeText(mContext,response.getString("resDesc"),Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse){
                    super.onFailure(statusCode,headers,throwable,errorResponse);
                    try {
                        JSONObject response = new JSONObject();
                        response.put("statusCode",statusCode);
                        //callBackExtListener.failCallback(response);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    AsyncHttpClientUtil.onFailure(mContext,statusCode);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 修改密码接口
     * @param mContext
     * @param oldpassword
     * @param newpassword
     * @param callBackExtListener
     */
    public void updateLoginPassword(final Context mContext,final String oldpassword,final String newpassword,final CallBackExtListener callBackExtListener){
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            if(CacheUtil.getInstance().isLogin()){
                client.addHeader("Token",CacheUtil.getInstance().getExtToken());
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("oldpassword",oldpassword);
            jsonObject.put("newpassword",newpassword);
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.updateLoginpassword();
            client.post(mContext,url, stringEntity, "application/json", new JsonHttpResponseHandler(){
                public void onStart(){
                    super.onStart();
                    DialogUtil.getInstance().showAvatarProgressDialog(mContext,"");
                }

                public void onFinish(){
                    super.onFinish();
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                    super.onSuccess(statusCode,headers,response);
                    try {
                        if(response.getInt("res") == 0){
                            if(callBackExtListener != null){
                                callBackExtListener.successCallback(response);
                            }
                        }else{
                            callBackExtListener.failCallback(response);
                            //Toast.makeText(mContext,response.getString("resDesc"),Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse){
                    super.onFailure(statusCode,headers,throwable,errorResponse);
                    try {
                        JSONObject response = new JSONObject();
                        response.put("statusCode",statusCode);
                        callBackExtListener.failCallback(response);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    AsyncHttpClientUtil.onFailure(mContext,statusCode);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 获取用户资料-批量，所有用户
     * @param mContext
     * @param userids
     * @param callBackListener
     */
    public void getUserInfo(final Context mContext,final String userids,final CallBackListener callBackListener){
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token",CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userids",userids);
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.getUserInfoAll();
            client.get(mContext,url, stringEntity, "application/json", new JsonHttpResponseHandler(){
                public void onStart(){
                    super.onStart();
                    DialogUtil.getInstance().showAvatarProgressDialog(mContext,"");
                }

                public void onFinish(){
                    super.onFinish();
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                    super.onSuccess(statusCode,headers,response);
                    try {
                        if(response.getInt("res") == 0){
                            JSONArray dataArr = response.getJSONArray("data");
                            if(dataArr != null && dataArr.length() > 0){
                                JSONObject dataJson = dataArr.getJSONObject(0);
                                DBOpenHelper.DB_NAME = dataJson.getString("userid") + ".db";
                                CacheUtil.getInstance().setUserId(dataJson.getString("userid"));
                                CacheUtil.getInstance().setShopID(dataJson.getString("shopid"));
                                CacheUtil.getInstance().setUserPhone(dataJson.getString("phone"));
                                CacheUtil.getInstance().setUserName(dataJson.getString("username"));
                                CacheUtil.getInstance().setShopFullName(dataJson.getString("fullname"));
                                String imgurl = dataJson.getString("userimage");
                                imgurl = ProtocolUtil.getHostImgUrl(imgurl);
                                CacheUtil.getInstance().saveUserPhotoUrl(imgurl);
                                CacheUtil.getInstance().setSex(dataJson.getString("sex"));

                                EasemobIMManager.getInstance().loginHxUser();
                                YunBaSubscribeManager.getInstance().setAlias(context);
                                YunBaSubscribeManager.getInstance().subscribe();

                                CacheUtil.getInstance().setLogin(true);
                                if(callBackListener != null){
                                    callBackListener.successCallback(dataJson);
                                }
                            }

                        }else{
                            Toast.makeText(mContext,response.getString("resDesc"),Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse){
                    super.onFailure(statusCode,headers,throwable,errorResponse);
                    AsyncHttpClientUtil.onFailure(mContext,statusCode);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }



}
