package com.zkjinshi.superservice.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.ZoneAdapter;

import com.zkjinshi.superservice.bean.BaseBean;
import com.zkjinshi.superservice.bean.SempLoginBean;
import com.zkjinshi.superservice.bean.ZoneBean;

import com.zkjinshi.superservice.factory.UserFactory;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestListener;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;

import com.zkjinshi.superservice.sqlite.DBOpenHelper;
import com.zkjinshi.superservice.sqlite.UserDBUtil;

import com.zkjinshi.superservice.sqlite.ZoneDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.view.RefreshLayout;
import com.zkjinshi.superservice.vo.IdentityType;
import com.zkjinshi.superservice.vo.UserVo;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * 开发者：dujiande
 * 日期：2015/9/21
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ZoneActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener,RefreshLayout.OnLoadListener {

    private final static String TAG = ZoneActivity.class.getSimpleName();

    private RefreshLayout swipeLayout;
    private ListView zoneLv;
    private ZoneAdapter zoneAdapter;
    private View header;
    private UserVo userVo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone);
        String userid = CacheUtil.getInstance().getUserId();
        userVo = UserDBUtil.getInstance().queryUserById(userid);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        header = getLayoutInflater().inflate(R.layout.header, null);
        swipeLayout = (RefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);


        zoneLv = (ListView)findViewById(R.id.zone_listview);

    }

    private void initData() {
        NetRequest netRequest = new NetRequest(ProtocolUtil.getZonelistUrl());
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("salesid",userVo.getUserId());
        bizMap.put("token",userVo.getToken());
        bizMap.put("shopid",userVo.getShopId());
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
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
                ArrayList<ZoneBean> zoneList = new Gson().fromJson(result.rawResult, new TypeToken< ArrayList<ZoneBean>>(){}.getType());
                zoneAdapter = new ZoneAdapter(ZoneActivity.this, zoneList);
                zoneLv.setAdapter(zoneAdapter);
            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }

    private void initListener() {
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setOnLoadListener(this);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //CacheUtil.getInstance().setLoginIdentity(IdentityType.BUSINESS);
                if(CacheUtil.getInstance().getLoginIdentity() == IdentityType.BUSINESS){
                    startActivity(new Intent(ZoneActivity.this,ShopLoginActivity.class));
                    finish();
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                }else{
                    startActivity(new Intent(ZoneActivity.this,MoreActivity.class));
                    finish();
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                }

            }
        });

        findViewById(R.id.go_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                semplocationupdate();
            }
        });
    }

/*
* 服务员修改自己管辖的区域通知
* */
    public void semplocationupdate(){
        String locid = zoneAdapter.getCheckedIds();
        NetRequest netRequest = new NetRequest(ProtocolUtil.getSemplocationupdateUrl());
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("salesid",userVo.getUserId());
        bizMap.put("token",userVo.getToken());
        bizMap.put("shopid",userVo.getShopId());
        bizMap.put("locid",locid);
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
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
                BaseBean baseBean = new Gson().fromJson(result.rawResult, BaseBean.class);
                if (baseBean.isSet()) {
                    CacheUtil.getInstance().setLogin(true);
                    CacheUtil.getInstance().setAreaInfo(zoneAdapter.getCheckedIds());
                    startActivity(new Intent(ZoneActivity.this, MainActivity.class));
                    finish();
                    overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
                } else {
                    DialogUtil.getInstance().showToast(ZoneActivity.this,baseBean.getErr());
                }

            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }


    @Override
    public void onRefresh() {
        swipeLayout.postDelayed(new Runnable() {

            @Override
            public void run() {
                // 更新数据
                // 更新完后调用该方法结束刷新
                swipeLayout.setRefreshing(false);
            }
        }, 1000);

    }

    @Override
    public void onLoad() {
        swipeLayout.postDelayed(new Runnable() {

            @Override
            public void run() {
                // 更新数据
                // 更新完后调用该方法结束刷新
                swipeLayout.setLoading(false);
            }
        }, 1000);
    }
}
