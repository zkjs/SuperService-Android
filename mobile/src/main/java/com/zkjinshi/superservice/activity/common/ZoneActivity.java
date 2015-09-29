package com.zkjinshi.superservice.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import com.google.gson.Gson;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.ZoneAdapter;
import com.zkjinshi.superservice.bean.SempLoginBean;
import com.zkjinshi.superservice.factory.UserFactory;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestListener;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.sqlite.DBOpenHelper;
import com.zkjinshi.superservice.sqlite.UserDBUtil;
import com.zkjinshi.superservice.test.ZoneBiz;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.view.RefreshLayout;
import com.zkjinshi.superservice.vo.UserVo;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        header = getLayoutInflater().inflate(R.layout.header, null);
        swipeLayout = (RefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);


        zoneLv = (ListView)findViewById(R.id.zone_listview);
        zoneAdapter = new ZoneAdapter(this, ZoneBiz.getZoneList());
        zoneLv.setAdapter(zoneAdapter);
    }

    private void initData() {
//        NetRequest netRequest = new NetRequest(ProtocolUtil.getSempLoginUrl());
//        HashMap<String,String> bizMap = new HashMap<String,String>();
//        bizMap.put("phone",phone);
//        netRequest.setBizParamMap(bizMap);
//        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
//        netRequestTask.methodType = MethodType.POST;
//        netRequestTask.setNetRequestListener(new NetRequestListener() {
//            @Override
//            public void onNetworkRequestError(int errorCode, String errorMessage) {
//                Log.i(TAG, "errorCode:" + errorCode);
//                Log.i(TAG, "errorMessage:" + errorMessage);
//            }
//
//            @Override
//            public void onNetworkRequestCancelled() {
//
//            }
//
//            @Override
//            public void onNetworkResponseSucceed(NetResponse result) {
//                Log.i(TAG, "result.rawResult:" + result.rawResult);
//                SempLoginBean sempLoginbean = new Gson().fromJson(result.rawResult, SempLoginBean.class);
//                if (sempLoginbean.isSet()) {
//                    //更新为最新的token和userid
//                    CacheUtil.getInstance().setToken(sempLoginbean.getToken());
//                    CacheUtil.getInstance().setUserId(sempLoginbean.getSalesid());
//                    CacheUtil.getInstance().setUserPhone(inputEt.getText().toString());
//                    CacheUtil.getInstance().setUserName(sempLoginbean.getName());
//                    CacheUtil.getInstance().setLogin(true);
//                    DBOpenHelper.DB_NAME = sempLoginbean.getSalesid() + ".db";
//                    UserVo userVo = UserFactory.getInstance().buildUserVo(sempLoginbean);
//                    UserDBUtil.getInstance().addUser(userVo);
//
//                    Intent intent = new Intent(LoginActivity.this, MoreActivity.class);
//                    startActivity(intent);
//                    finish();
//                    overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
//                } else {
//                    DialogUtil.getInstance().showToast(LoginActivity.this, "手机号还不是服务员 ");
//                }
//
//            }
//
//            @Override
//            public void beforeNetworkRequestStart() {
//
//            }
//        });
//        netRequestTask.isShowLoadingDialog = true;
//        netRequestTask.execute();
    }

    private void initListener() {
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setOnLoadListener(this);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ZoneActivity.this,MoreActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }
        });

        findViewById(R.id.go_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ZoneActivity.this,MainActivity.class));
                finish();
                overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
            }
        });
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
