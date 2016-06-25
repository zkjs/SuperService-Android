package com.zkjinshi.superservice.fragment;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.response.BaseResponse;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.ProtocolUtil;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * 开发者：jimmyzhang
 * 日期：16/6/25
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class CallServiceNetController {

    private CallServiceNetController(){}

    private static CallServiceNetController instance;

    public synchronized static CallServiceNetController getInstance(){
        if(null == instance){
            instance = new CallServiceNetController();
        }
        return instance;
    }

    /**
     * 呼叫服务状态变更
     * @param taskid
     * @param taskaction 指派(2), 就绪(3), 取消(4), 完成(5), 评价(6)
     * @param target
     * @param context
     * @param callBack
     */
    public void requestUpdateServiceTask(String taskid, int taskaction, String target, final Context context, final NetCallBack callBack){

        try {
            AsyncHttpClient httpClient = new AsyncHttpClient();
            httpClient.setTimeout(Constants.OVERTIMEOUT);
            httpClient.addHeader("Content-Type","application/json; charset=UTF-8");
            httpClient.addHeader("Token", CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("taskid",taskid);
            jsonObject.put("taskaction",taskaction);
            if(!TextUtils.isEmpty(target)){
                jsonObject.put("target",target);
            }
            StringEntity stringEntity = new StringEntity(jsonObject.toString(),"UTF-8");
            String requestUrl = ProtocolUtil.getUpdateServiceUrl();
            httpClient.put(context,requestUrl,stringEntity,"application/json",new JsonHttpResponseHandler(){

                @Override
                public void onStart() {
                    super.onStart();
                    DialogUtil.getInstance().showAvatarProgressDialog(context,"");
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    if(null != response){
                        BaseResponse baseResponse = new Gson().fromJson(response.toString(),BaseResponse.class);
                        if(null != baseResponse){
                            int resultCode = baseResponse.getRes();
                            if(0 == resultCode){
                                if(null != callBack){
                                    callBack.onSuccess();
                                }
                            }else {
                                String resultMsg = baseResponse.getResDesc();
                                if(!TextUtils.isEmpty(resultMsg)){
                                    DialogUtil.getInstance().showCustomToast(context,""+resultMsg, Gravity.CENTER);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface NetCallBack{
        void onSuccess();
    }
}
