package com.zkjinshi.superservice.activity.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.GoodInfoAdapter;
import com.zkjinshi.superservice.listener.RecyclerItemClickListener;
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
public class GoodListActivity extends AppCompatActivity {

    private final static String TAG = GoodListActivity.class.getSimpleName();

    private String              mShopID;
    private Toolbar             mToolbar;
    private TextView            mTvCenterTitle;
    private SwipeRefreshLayout  mSrlContainer;
    private RecyclerView        mRvRoomList;
    private LinearLayoutManager mLayoutManager;
    private List<GoodInfoVo>    mGoodInfoVoList;
    private GoodInfoAdapter     mGoodInfoAdatper;
    
    private int                 mCheckedPosition = -1;//默认选中位置为空 -1

    private RelativeLayout      mRlConfirm;
    private RelativeLayout      mRlCancel;

    private GoodInfoVo          mGoodInfoVo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_list);

        initView();
        initData();
        initListeners();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(R.mipmap.ic_fanhui);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTvCenterTitle = (TextView) findViewById(R.id.tv_center_title);
        mTvCenterTitle.setText(getString(R.string.choose_room_type));

        mSrlContainer = (SwipeRefreshLayout) findViewById(R.id.srl_container);
        mRvRoomList   = (RecyclerView) findViewById(R.id.rv_room_list);
        mRlConfirm    = (RelativeLayout) findViewById(R.id.rl_confirm);
        mRlCancel     = (RelativeLayout) findViewById(R.id.rl_cancel);
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
        showGoodInfoList();
    }

    private void initListeners() {

        //返回
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoodListActivity.this.finish();
            }
        });

        //设置界面数据刷新
        mSrlContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mGoodInfoAdatper.clear();
                showGoodInfoList();
            }
        });

        //设置条目点击事件
        mGoodInfoAdatper.setOnItemClickListener(new RecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                mCheckedPosition = mGoodInfoAdatper.getCheckedPosition();
                mGoodInfoVo = mGoodInfoVoList.get(mCheckedPosition);
            }
        });

        //确认选中房型
        mRlConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mGoodInfoVo != null){
                    Intent roomData = new Intent();
                    roomData.putExtra("room_type", mGoodInfoVo);
                    setResult(RESULT_OK, roomData);
                    GoodListActivity.this.finish();
                } else {
                    DialogUtil.getInstance().showCustomToast(GoodListActivity.this, "尚未选择房型!", Gravity.CENTER);
                }
            }
        });

        //取消
        mRlCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoodListActivity.this.finish();
            }
        });

    }

    /**
     * 获取商品（房型列表）
     * @return
     */
    public void showGoodInfoList() {
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
                        mGoodInfoAdatper.updateListView(mGoodInfoVoList);
                    }
                    mSrlContainer.setRefreshing(false);
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
