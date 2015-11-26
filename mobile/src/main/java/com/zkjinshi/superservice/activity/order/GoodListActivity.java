package com.zkjinshi.superservice.activity.order;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.GoodAdapter;
import com.zkjinshi.superservice.adapter.GoodInfoAdapter;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.utils.CacheUtil;
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

    private String        mShopID;
    private ImageButton   mIbtnBack;
    private RecyclerView  mRvRoomList;
    private LinearLayoutManager mLayoutManager;
    private List<GoodInfoVo>    mGoodInfoVoList;
    private GoodInfoAdapter     mGoodInfoAdatper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_list);

        initView();
        initData();
        initListeners();
    }

    private void initView() {
        mIbtnBack   = (ImageButton)  findViewById(R.id.ibtn_back);
        mRvRoomList = (RecyclerView) findViewById(R.id.rv_room_list);
    }

    private void initData() {
        mShopID = CacheUtil.getInstance().getShopID();
        mGoodInfoVoList = new ArrayList<>();

        mRvRoomList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvRoomList.setLayoutManager(mLayoutManager);

        mGoodInfoAdatper = new GoodInfoAdapter(mGoodInfoVoList, GoodListActivity.this);
        mRvRoomList.setAdapter(mGoodInfoAdatper);
        //获取房型列表
        getGoodInfoList();
    }

    private void initListeners() {
        //返回
        mIbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoodListActivity.this.finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    /**
     * 获取商品（房型列表）
     * @return
     */
    public void getGoodInfoList() {
        String url = ProtocolUtil.getGoodListUrl(mShopID);
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
                    Type listType = new TypeToken<List<GoodInfoVo>>() {}.getType();
                    Gson gson = new Gson();
                    mGoodInfoVoList = gson.fromJson(result.rawResult, listType);
                    if (null != mGoodInfoVoList && !mGoodInfoVoList.isEmpty()) {
                        //显示获取到的房型列表
                        mGoodInfoAdatper.clear();
                        mGoodInfoAdatper.updateListView(mGoodInfoVoList);
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

}
