package com.zkjinshi.superservice.ext.activity.facepay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.ext.adapter.AmountAdapter;
import com.zkjinshi.superservice.ext.response.AmountDetailResponse;
import com.zkjinshi.superservice.ext.response.AmountRecordResponse;
import com.zkjinshi.superservice.ext.vo.AmountStatusVo;
import com.zkjinshi.superservice.listener.OnRefreshListener;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.view.RefreshListView;
import com.zkjinshi.superservice.vo.NoticeVo;

import java.util.ArrayList;

/**
 * 支付记录页面（完成）
 * 开发者：JimmyZhang
 * 日期：2016/3/7
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class AmountRecordActivity extends Activity {

    public static final String TAG = AmountRecordActivity.class.getSimpleName();

    private ImageButton backIBtn;
    private TextView titleTv;
    private RefreshListView amountRecordListView;
    private TextView amountRecordNoResultTv;
    private ArrayList<AmountStatusVo> amountStatusList;
    private AmountAdapter amountAdapter;
    public static int PAGE_NO = 0;
    public static final int PAGE_SIZE = 10;

    private void initView(){
        backIBtn = (ImageButton) findViewById(R.id.header_bar_btn_back);
        titleTv = (TextView)findViewById(R.id.header_bar_tv_title);
        amountRecordListView = (RefreshListView)findViewById(R.id.amount_record_list_view);
        amountRecordNoResultTv = (TextView)findViewById(R.id.amount_record_no_result);
    }

    private void initData(){
        backIBtn.setVisibility(View.VISIBLE);
        titleTv.setText("收款记录");
        amountAdapter = new AmountAdapter(this,amountStatusList);
        amountRecordListView.setAdapter(amountAdapter);
        PAGE_NO = 0;
        requestAmountRecordListTask(true);
    }

    private void initListeners(){

        //返回
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        amountRecordListView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefreshing() {
                amountStatusList = new ArrayList<AmountStatusVo>();
                PAGE_NO = 0;
                requestAmountRecordListTask(true);
            }

            @Override
            public void onLoadingMore() {
                requestAmountRecordListTask(false);
            }

            @Override
            public void implOnItemClickListener(AdapterView<?> parent, View view, int position, long id) {
                int realPostion = position - 1;
                AmountStatusVo amountStatusVo = (AmountStatusVo) amountAdapter.getItem(realPostion) ;
                Intent intent = new Intent(AmountRecordActivity.this,AmountDetailActivity.class);
                intent.putExtra("amountStatusVo",amountStatusVo);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amount_record);
        initView();
        initData();
        initListeners();
    }

    /**
     * 获取收款记录列表
     */
    private void requestAmountRecordListTask(final boolean isRefresh){
        String url = ConfigUtil.getInst().getForDomain()+"res/v1/payment/ss?page="+PAGE_NO+"&page_size="+PAGE_SIZE;
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
                amountRecordListView.refreshFinish();
                if(null != result && !TextUtils.isEmpty(result.rawResult)){
                    AmountRecordResponse amountResponse = new Gson().fromJson(result.rawResult,AmountRecordResponse.class);
                    if(null != amountResponse){
                        int resultFlag = amountResponse.getRes();
                        if(0 == resultFlag || 30001 == resultFlag){
                            if(isRefresh){
                                amountStatusList = amountResponse.getData();
                            }else {
                                ArrayList<AmountStatusVo>  requestNoticeList = amountResponse.getData();
                                amountStatusList.addAll(requestNoticeList);
                            }
                            PAGE_NO++;
                            amountAdapter.setAmountStatusList(amountStatusList);
                            if(30001 == resultFlag){
                                amountRecordListView.setEmptyView(amountRecordNoResultTv);
                            }
                        }else {
                            String errorMsg = amountResponse.getResDesc();
                            if(!TextUtils.isEmpty(errorMsg)){
                                DialogUtil.getInstance().showCustomToast(AmountRecordActivity.this,errorMsg, Gravity.CENTER);
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
