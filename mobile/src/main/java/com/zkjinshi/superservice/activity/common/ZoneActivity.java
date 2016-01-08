package com.zkjinshi.superservice.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.ZoneAdapter;

import com.zkjinshi.superservice.bean.BaseBean;
import com.zkjinshi.superservice.bean.ZoneBean;

import com.zkjinshi.superservice.manager.YunBaSubscribeManager;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;

import com.zkjinshi.superservice.sqlite.UserDBUtil;

import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.IdentityType;
import com.zkjinshi.superservice.vo.UserVo;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.HashMap;

import io.yunba.android.manager.YunBaManager;


/**
 * 开发者：dujiande
 * 日期：2015/9/21
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ZoneActivity extends Activity {

    private final static String TAG = ZoneActivity.class.getSimpleName();


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
        zoneLv = (ListView)findViewById(R.id.zone_listview);

        findViewById(R.id.back_btn).setVisibility(View.GONE);
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
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(this) {
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

                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try{
                    ArrayList<ZoneBean> zoneList = new Gson().fromJson(result.rawResult, new TypeToken< ArrayList<ZoneBean>>(){}.getType());
                    zoneAdapter = new ZoneAdapter(ZoneActivity.this, zoneList);
                    zoneLv.setAdapter(zoneAdapter);
                    unsubscribeLocs();
                    getMyZone();
                }catch (Exception e){
                    Log.e(TAG,e.getMessage());
                }

            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }

    //服务员获取自己的通知区域
    private void getMyZone(){
        NetRequest netRequest = new NetRequest(ProtocolUtil.getMySemplocationUrl());
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("salesid",userVo.getUserId());
        bizMap.put("token",userVo.getToken());
        bizMap.put("shopid",userVo.getShopId());
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(this) {
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

                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try{
                    ArrayList<ZoneBean> zoneList = new Gson().fromJson(result.rawResult, new TypeToken< ArrayList<ZoneBean>>(){}.getType());
                   if(zoneList.size() > 0){
                       zoneAdapter.setCheckedZone(zoneList);
                       zoneAdapter.notifyDataSetChanged();
                   }
                }catch (Exception e){
                    Log.e(TAG,e.getMessage());
                }

            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }


    private void initListener() {


        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CacheUtil.getInstance().getLoginIdentity() == IdentityType.BUSINESS){
                    if(!getIntent().getBooleanExtra("from_setting",false)){
                        startActivity(new Intent(ZoneActivity.this,ShopLoginActivity.class));
                    }
                    finish();
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                }else{
                    if(!getIntent().getBooleanExtra("from_setting",false)){
                        startActivity(new Intent(ZoneActivity.this,MoreActivity.class));
                    }
                    finish();
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                }

            }
        });

        findViewById(R.id.go_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CacheUtil.getInstance().saveListCache("zoneBeanList",zoneAdapter.getSelectZoneBeanList());
                subscribeLocs();//订阅云巴消息
                semplocationupdate();
            }
        });

        zoneLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                boolean b =  zoneAdapter.getZoneList().get(i).isHasAdd();
                zoneAdapter.getZoneList().get(i).setHasAdd(!b);
                zoneAdapter.notifyDataSetChanged();
            }
        });


    }

    /**
     * 取消订阅区域
     */
    public void unsubscribeLocs(){
        YunBaManager.unsubscribe(getApplicationContext(),zoneAdapter.getAllLocIds(),
                new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.i(TAG,"取消订阅云巴成功");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        if (exception instanceof MqttException) {
                            MqttException ex = (MqttException)exception;
                            String msg =  "Subscribe failed with error code : " + ex.getReasonCode();
                            Log.i(TAG,"取消订阅云巴失败:"+msg);
                        }
                    }
                }
        );
    }

    /**
     * 订阅区域
     */
    public void subscribeLocs(){
        YunBaSubscribeManager.getInstance().subscribe(zoneAdapter.getLocIds());
    }

    /*
     * 服务员修改自己管辖的区域通知
     *
     */
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
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(this) {
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

                Log.i(TAG, "result.rawResult:" + result.rawResult);
                BaseBean baseBean = new Gson().fromJson(result.rawResult, BaseBean.class);
                if (baseBean.isSet()) {
                    CacheUtil.getInstance().setLogin(true);
                    CacheUtil.getInstance().setAreaInfo(zoneAdapter.getCheckedIds());
                    if (!getIntent().getBooleanExtra("from_setting", false)) {
                        startActivity(new Intent(ZoneActivity.this, MainActivity.class));
                    }
                    finish();
                    overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
                } else {
                    DialogUtil.getInstance().showToast(ZoneActivity.this, baseBean.getErr());
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
