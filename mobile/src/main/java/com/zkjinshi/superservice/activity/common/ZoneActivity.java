package com.zkjinshi.superservice.activity.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.ZoneAdapter;

import com.zkjinshi.superservice.base.BaseActivity;
import com.zkjinshi.superservice.bean.ZoneBean;

import com.zkjinshi.superservice.net.RequestUtil;
import com.zkjinshi.superservice.response.GetZoneListResponse;
import com.zkjinshi.superservice.utils.AsyncHttpClientUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.ProtocolUtil;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;


import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * 开发者：dujiande
 * 日期：2015/9/21
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ZoneActivity extends BaseActivity {

    private final static String TAG = ZoneActivity.class.getSimpleName();
    private String ZONE_CACHE_KEY = "zoneBeanList"+CacheUtil.getInstance().getUserId();
    private ListView zoneLv;
    private ZoneAdapter zoneAdapter;
    private Context mContext;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone);
        mContext = this;

        initView();
        initData();
        initListener();
    }

    private void initView() {
        zoneLv = (ListView)findViewById(R.id.zone_listview);
    }

    private void initData() {

        zoneAdapter = new ZoneAdapter(ZoneActivity.this, new ArrayList<ZoneBean>());
        zoneLv.setAdapter(zoneAdapter);
        getZoneList();

    }

    private void getZoneList(){
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token",CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.getZoneList();
            client.get(mContext,url, stringEntity, "application/json", new AsyncHttpResponseHandler(){
                public void onStart(){
                    DialogUtil.getInstance().showAvatarProgressDialog(mContext,"");
                }

                public void onFinish(){
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String response = new String(responseBody,"utf-8");
                        GetZoneListResponse getZoneListResponse = new Gson().fromJson(response,GetZoneListResponse.class);
                        if (getZoneListResponse == null){
                            return;
                        }
                        if(getZoneListResponse.getRes() == 0){
                            ArrayList<ZoneBean> zoneListAll = getZoneListResponse.getData();
                            if(!zoneListAll.isEmpty()){
                                zoneAdapter.setZoneList(zoneListAll);
                                String listStr = CacheUtil.getInstance().getListStrCache(ZONE_CACHE_KEY);
                                if(!TextUtils.isEmpty(listStr)){
                                    Type listType = new TypeToken<ArrayList<ZoneBean>>(){}.getType();
                                    Gson gson = new Gson();
                                    ArrayList<ZoneBean> myZoneList = gson.fromJson(listStr, listType);
                                    if (null != myZoneList && !myZoneList.isEmpty()) {
                                        zoneAdapter.setCheckedZone(myZoneList);
                                    }
                                }
                                zoneAdapter.notifyDataSetChanged();
                            }


                        }else{
                            Toast.makeText(mContext,getZoneListResponse.getResDesc(),Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    AsyncHttpClientUtil.onFailure(mContext,statusCode);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initListener() {

        findViewById(R.id.go_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CacheUtil.getInstance().saveListCache(ZONE_CACHE_KEY, zoneAdapter.getSelectZoneBeanList());
                CacheUtil.getInstance().setAreaInfo(zoneAdapter.getCheckedIds());

                if (!getIntent().getBooleanExtra("from_setting", false)) {
                    startActivity(new Intent(ZoneActivity.this, MainActivity.class));
                }
                finish();
                overridePendingTransition(R.anim.activity_new, R.anim.activity_out);

            }
        });

        zoneLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int added =  zoneAdapter.getZoneList().get(i).getSubscribed();
                if(added == 0){
                    zoneAdapter.getZoneList().get(i).setSubscribed(1);
                }else{
                    zoneAdapter.getZoneList().get(i).setSubscribed(0);
                }
                zoneAdapter.notifyDataSetChanged();
            }
        });


    }

}
