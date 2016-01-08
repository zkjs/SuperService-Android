package com.zkjinshi.superservice.activity.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;



import android.util.Log;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.GoodAdapter;

import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.GoodInfoVo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品列表Activity
 * 开发者：JimmyZhang
 * 日期：2015/7/27
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class GoodListActivity extends Activity {

    private final static String TAG = GoodListActivity.class.getSimpleName();

    private ListView listView;

    private ArrayList<GoodInfoVo> goodInfoList;
    private GoodAdapter goodAdapter;
    private GoodInfoVo goodInfoVo = null;
    private String shopid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_list);
        if(getIntent().getSerializableExtra("GoodInfoVo") != null){
            goodInfoVo = (GoodInfoVo)getIntent().getSerializableExtra("GoodInfoVo");
        }
        shopid = getIntent().getStringExtra("shopid");
        initView();
        initData();
        initListeners();
    }

    private void initView() {
        listView =  (ListView)findViewById(R.id.good_list_list_view);
    }

    private void initData() {
        TextView tips  = (TextView)findViewById(R.id.empty_tips);
        tips.setText("暂无房型可选");
        listView.setEmptyView(findViewById(R.id.empty_linearlayout));
        findViewById(R.id.empty_linearlayout).setVisibility(View.INVISIBLE);
        //获取房型列表
        showGoodInfoList();
    }

    private void initListeners() {

        //返回
       findViewById(R.id.header_back_iv).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               finish();
           }
       });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                goodInfoVo = (GoodInfoVo)goodAdapter.getItem(position);
                Intent intent = new Intent();
                intent.putExtra("GoodInfoVo", goodInfoVo);
                intent.putExtra("shopid",shopid);
                setResult(RESULT_OK, intent);
                finish();
            }
        });




    }

    /**
     * 获取商品（房型列表）
     * @return
     */
    public void showGoodInfoList() {
        String url = ProtocolUtil.getGoodListUrl(shopid);
        Log.i(TAG, url);
        NetRequest     netRequest     = new NetRequest(url);
        NetRequestTask netRequestTask = new NetRequestTask(this, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
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
                try {
                    Type listType = new TypeToken<ArrayList<GoodInfoVo>>() {
                    }.getType();
                    Gson gson = new Gson();
                    goodInfoList = gson.fromJson(result.rawResult, listType);
                    if (null != goodInfoList && !goodInfoList.isEmpty()) {
                        setResponseData(goodInfoList);
                    }else{
                        findViewById(R.id.empty_linearlayout).setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }

            @Override
            public void beforeNetworkRequestStart() {
            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }

    private void setResponseData(ArrayList<GoodInfoVo> goodInfoList){
        goodAdapter = new GoodAdapter(this,goodInfoList);
        listView.setAdapter(goodAdapter);
        if(goodInfoVo != null){
            goodAdapter.selectGood(goodInfoVo.getId());
        }
    }

}
