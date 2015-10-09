package com.zkjinshi.superservice.activity.set;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.bean.TeamContactBean;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestListener;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 团队联系人显示界面
 * 开发者：vincent
 * 日期：2015/10/9
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class TeamContactsActivity extends Activity{

    private final static String TAG = TeamContactsActivity.class.getSimpleName();

    private ImageButton mIbtnBack;

    private String mUserID;
    private String mShopID;
    private String mToken;
    private List<TeamContactBean> teamList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_contacts);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mIbtnBack        = (ImageButton)     findViewById(R.id.ibtn_back);
    }

    private void initData() {
        mUserID     = CacheUtil.getInstance().getUserId();
        mToken      = CacheUtil.getInstance().getToken();
        mShopID     = CacheUtil.getInstance().getShopID();
        DialogUtil.getInstance().showProgressDialog(this);
        getTeamList(mUserID, mToken, mShopID);
    }

    private void initListener() {
        //返回上一页
        mIbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeamContactsActivity.this.finish();
            }
        });
    }

    /**
     * 获取团队联系人列表
     * @param userID
     * @param token
     * @param shopID
     */
    public void getTeamList(String userID, String token, String shopID) {
        NetRequest netRequest = new NetRequest(ProtocolUtil.getTeamListUrl());
        HashMap<String,String> bizMap = new HashMap<>();
        bizMap.put("salesid", userID);
        bizMap.put("token", token);
        bizMap.put("shopid", shopID);
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(this, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.POST;
        netRequestTask.setNetRequestListener(new NetRequestListener() {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                DialogUtil.getInstance().cancelProgressDialog();
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
                DialogUtil.getInstance().showToast(TeamContactsActivity.this, "网络访问失败，稍候再试。");
            }

            @Override
            public void onNetworkRequestCancelled() {
                DialogUtil.getInstance().cancelProgressDialog();
            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                DialogUtil.getInstance().cancelProgressDialog();
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                String jsonResult = result.rawResult;
                if (result.rawResult.contains("set") || jsonResult.contains("err")) {

                } else {
                    Gson gson = new Gson();
                    List<TeamContactBean> teamContactBeans = gson.fromJson(jsonResult,
                            new TypeToken<ArrayList<TeamContactBean>>() {}.getType());
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
