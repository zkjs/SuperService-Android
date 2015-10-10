package com.zkjinshi.superservice.activity.set;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.bean.ClientDetailBean;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestListener;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 新增客户联系人
 * 开发者：vincent
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientSelectActivity extends Activity {

    private final static String TAG = ClientSelectActivity.class.getSimpleName();

    private String          mUserID;
    private String          mToken;
    private EditText        mEtClientPhone;

    private ClientDetailBean        mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_select);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mEtClientPhone      = (EditText) findViewById(R.id.et_client_phone);
    }

    private void initData() {
        mUserID = CacheUtil.getInstance().getUserId();
        mToken  = CacheUtil.getInstance().getToken();
    }

    private void initListener() {
        mEtClientPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String phone = mEtClientPhone.getText().toString().trim();
                    if(StringUtil.isPhoneNumber(phone)){
                        //TODO:执行网络获取具体客户信息
                        getClientDetail(phone);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void getClientDetail(final String phoneNumber) {
        NetRequest netRequest = new NetRequest(ProtocolUtil.getClientDetailUrl());
        HashMap<String,String> bizMap = new HashMap<>();
        bizMap.put("empid", mUserID);
        bizMap.put("token", mToken);
        bizMap.put("phone", phoneNumber);
        bizMap.put("set", "9");
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(this, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new NetRequestListener() {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
                //网络请求异常
                DialogUtil.getInstance().cancelProgressDialog();
                ClientSelectActivity.this.finish();
                DialogUtil.getInstance().showToast(ClientSelectActivity.this, "网络异常");
            }

            @Override
            public void onNetworkRequestCancelled() {
                DialogUtil.getInstance().cancelProgressDialog();
            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                DialogUtil.getInstance().cancelProgressDialog();
                String jsonResult = result.rawResult;
                if (jsonResult.contains("set") && jsonResult.contains("err") && jsonResult.trim().contains("err")) {
                    try {
                        JSONObject jsonObject = new JSONObject(jsonResult);
                        int errCode = jsonObject.getInt("err");
                        //验证没通过
                        if (400 == errCode) {
                            DialogUtil.getInstance().showToast(ClientSelectActivity.this,
                                    "验证不通过，请退出后重新查询。");
                        }

                        //用户不存在
                        if (407 == errCode) {
                            Intent addClient = new Intent(ClientSelectActivity.this, ClientAddActivity.class);
                            addClient.putExtra("phone_number", phoneNumber);
                            ClientSelectActivity.this.startActivity(addClient);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Gson gson = new Gson();
                    mClient   = gson.fromJson(jsonResult, ClientDetailBean.class);
                    Intent clienBind = new Intent(ClientSelectActivity.this, ClientBindActivity.class);
                    clienBind.putExtra("client", mClient);
                    ClientSelectActivity.this.startActivity(clienBind);
                    ClientSelectActivity.this.finish();
                }
            }

            @Override
            public void beforeNetworkRequestStart() {
                //网络请求前
            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }

}
