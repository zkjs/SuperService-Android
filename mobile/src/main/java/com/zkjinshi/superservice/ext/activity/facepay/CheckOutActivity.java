package com.zkjinshi.superservice.ext.activity.facepay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.chat.single.ChatDetailActivity;
import com.zkjinshi.superservice.ext.adapter.NearbyAdapter;
import com.zkjinshi.superservice.ext.response.AmountRecordResponse;
import com.zkjinshi.superservice.ext.response.NearbyUserResponse;
import com.zkjinshi.superservice.ext.vo.NearbyUserVo;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.utils.CacheUtil;

import java.util.ArrayList;

/**
 * 收款台主页面(完成)
 * 开发者：JimmyZhang
 * 日期：2016/3/7
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class CheckOutActivity extends Activity {

    public static final String TAG = CheckOutActivity.class.getSimpleName();

    private TextView titleTv;
    private ImageButton moreIBtn,backIBtn;
    private LinearLayout searchLayout;
    private GridView nearbyUserGv;
    private ArrayList<NearbyUserVo> nearbyUserList;
    private NearbyAdapter nearbyAdapter;

    private void initView(){
        titleTv = (TextView)findViewById(R.id.header_bar_tv_title);
        moreIBtn = (ImageButton)findViewById(R.id.header_bar_btn_record);
        backIBtn = (ImageButton)findViewById(R.id.header_bar_btn_back);
        searchLayout = (LinearLayout)findViewById(R.id.search_layout);
        nearbyUserGv = (GridView)findViewById(R.id.nearby_layout);
    }

    private void initData(){
        moreIBtn.setVisibility(View.VISIBLE);
        backIBtn.setVisibility(View.VISIBLE);
        titleTv.setText("收款台");
        nearbyAdapter = new NearbyAdapter(this,nearbyUserList);
        nearbyUserGv.setAdapter(nearbyAdapter);
        requestNearbyUserListTask();
    }

    private void initListeners(){

        //返回
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //账单
        moreIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CheckOutActivity.this,AmountRecordActivity.class);
                startActivity(intent);
            }
        });

        //搜索
        searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CheckOutActivity.this,PayeeSearchActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_bottom,
                        R.anim.slide_out_top);
            }
        });

        //附近用户列表单击操作
        nearbyUserGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                NearbyUserVo nearbyUserVo = (NearbyUserVo)nearbyAdapter.getItem(position);
                Intent intent = new Intent(CheckOutActivity.this,PayRequestActivity.class);
                intent.putExtra("nearbyUserVo",nearbyUserVo);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        initView();
        initData();
        initListeners();
    }

    /**
     * 请求附近用户列表
     */
    private void requestNearbyUserListTask(){
        String shopId = CacheUtil.getInstance().getShopID();
        String url = ConfigUtil.getInst().getPyxDomain()+"lbs/v1/loc/beacon/"+shopId+"/1000?page=0&page_size=20";
        NetRequest netRequest = new NetRequest(url);
        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
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
                if(null != result && !TextUtils.isEmpty(result.rawResult)){
                    NearbyUserResponse nearbyUserResponse = new Gson().fromJson(result.rawResult,NearbyUserResponse.class);
                    if(null != nearbyUserResponse){
                        int resultFlag = nearbyUserResponse.getRes();
                        if(0 == resultFlag){
                            nearbyUserList =  nearbyUserResponse.getData();
                            if(null != nearbyUserList && !nearbyUserList.isEmpty()){
                                nearbyAdapter.setNearbyUserList(nearbyUserList);
                            }
                        }else {
                            String errorMsg = nearbyUserResponse.getResDesc();
                            if(!TextUtils.isEmpty(errorMsg)){
                                DialogUtil.getInstance().showCustomToast(CheckOutActivity.this,errorMsg, Gravity.CENTER);
                            }
                        }
                    }
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
