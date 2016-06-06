package com.zkjinshi.superservice.pad.ext.activity.facepay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.pad.ext.response.AmountDetailResponse;
import com.zkjinshi.superservice.pad.ext.util.MathUtil;
import com.zkjinshi.superservice.pad.ext.vo.AmountStatusVo;
import com.zkjinshi.superservice.pad.net.ExtNetRequestListener;
import com.zkjinshi.superservice.pad.net.MethodType;
import com.zkjinshi.superservice.pad.net.NetRequest;
import com.zkjinshi.superservice.pad.net.NetRequestTask;
import com.zkjinshi.superservice.pad.net.NetResponse;
import com.zkjinshi.superservice.pad.R;

import java.util.HashMap;

/**
 * 收款详情页(完成)
 * 开发者：JimmyZhang
 * 日期：2016/3/7
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class AmountDetailActivity extends Activity {

    public static final String TAG = AmountDetailActivity.class.getSimpleName();

    private ImageButton backIBtn;
    private TextView titleTv;
    private LinearLayout amountFailLayout,amountSuccLayout,waitSureLayout;
    private TextView amountFailUserNameTv,amountFailAmountPriceTv,amountFailResendRequestTv,amountFailOrderNoTv,amountFailAmountTimeTv;
    private TextView amountSuccUserNameTv,amountSuccAmountPriceTv,amountSuccOrderNoTv,amountSuccAmountTimeTv,amountSuccAmountFinishTv;
    private TextView amountWaitUserNameTv,amountWaitAmountPriceTv,amountWaitResendRequestTv,amountWaitOrderNoTv,amountWaitAmountTimeTv;

    private AmountStatusVo amountStatusVo;
    private int amountStatus;

    private boolean isSuccess;

    private void initView(){
        amountFailLayout = (LinearLayout)findViewById(R.id.layout_amount_fail);
        amountSuccLayout = (LinearLayout)findViewById(R.id.layout_amount_succ);
        waitSureLayout = (LinearLayout)findViewById(R.id.layout_amount_wait_sure);
        amountFailUserNameTv = (TextView)findViewById(R.id.amount_fail_user_name);
        amountFailAmountPriceTv = (TextView)findViewById(R.id.amount_fail_amount_price);
        amountFailResendRequestTv = (TextView)findViewById(R.id.amount_fail_resend_amount_request);
        amountFailOrderNoTv = (TextView)findViewById(R.id.amount_fail_order_no);
        amountFailAmountTimeTv = (TextView)findViewById(R.id.amount_fail_amount_time);
        amountSuccUserNameTv = (TextView)findViewById(R.id.amount_succ_user_name);
        amountSuccAmountPriceTv = (TextView)findViewById(R.id.amount_succ_amount_price);
        amountSuccOrderNoTv = (TextView)findViewById(R.id.amount_succ_order_no);
        amountSuccAmountTimeTv = (TextView)findViewById(R.id.amount_succ_amount_time);
        amountSuccAmountFinishTv = (TextView)findViewById(R.id.amount_succ_finish_time);
        amountWaitUserNameTv = (TextView)findViewById(R.id.amount_wait_sure_user_name);
        amountWaitAmountPriceTv = (TextView)findViewById(R.id.amount_wait_sure_amount_price);
        amountWaitResendRequestTv = (TextView)findViewById(R.id.amount_wait_sure_resend_request);
        amountWaitOrderNoTv = (TextView)findViewById(R.id.amount_wait_sure_order_no);
        amountWaitAmountTimeTv = (TextView)findViewById(R.id.amount_wait_sure_amount_time);
        backIBtn = (ImageButton)findViewById(R.id.header_bar_btn_back);
        titleTv = (TextView)findViewById(R.id.header_bar_tv_title);
    }

    private void initData(){
        isSuccess = getIntent().getBooleanExtra("isSuccess",false);
        backIBtn.setVisibility(View.VISIBLE);
        titleTv.setText("详情");
        if(null != getIntent() && null != getIntent().getSerializableExtra("amountStatusVo")){
            amountStatusVo = (AmountStatusVo) getIntent().getSerializableExtra("amountStatusVo");
        }
        if(null != amountStatusVo){
            amountStatus = amountStatusVo.getStatus();
            //订单状态码  0-待确认, 1-已拒绝, 2-已确认
            if(0 == amountStatus){
                //amountFailLayout,amountSuccLayout,waitSureLayout
                amountFailLayout.setVisibility(View.GONE);
                amountSuccLayout.setVisibility(View.GONE);
                waitSureLayout.setVisibility(View.VISIBLE);

            }else if(1 == amountStatus){
                amountFailLayout.setVisibility(View.VISIBLE);
                amountSuccLayout.setVisibility(View.GONE);
                waitSureLayout.setVisibility(View.GONE);

            }else {
                amountFailLayout.setVisibility(View.GONE);
                amountSuccLayout.setVisibility(View.VISIBLE);
                waitSureLayout.setVisibility(View.GONE);

            }
            String userNameStr = amountStatusVo.getUsername();
            if(!TextUtils.isEmpty(userNameStr)){
                amountFailUserNameTv.setText(userNameStr);
                amountSuccUserNameTv.setText(userNameStr);
                amountWaitUserNameTv.setText(userNameStr);
            }
            double totalPrice = amountStatusVo.getAmount();
            if(totalPrice > 0){
                String amountPriceStr = ""+ MathUtil.convertStr(totalPrice);
                if(!TextUtils.isEmpty(amountPriceStr)){
                    amountFailAmountPriceTv.setText(amountPriceStr);
                    amountSuccAmountPriceTv.setText(amountPriceStr);
                    amountWaitAmountPriceTv.setText(amountPriceStr);
                }
            }
            String orderNoStr = amountStatusVo.getPaymentno();
            if(!TextUtils.isEmpty(orderNoStr)){
                amountFailOrderNoTv.setText(orderNoStr);
                amountSuccOrderNoTv.setText(orderNoStr);
                amountWaitOrderNoTv.setText(orderNoStr);
            }
            String amountTime = amountStatusVo.getCreatetime();
            if(!TextUtils.isEmpty(amountTime)){
                amountFailAmountTimeTv.setText(amountTime);
                amountSuccAmountTimeTv.setText(amountTime);
                amountWaitAmountTimeTv.setText(amountTime);
            }
            String finishTimeStr = amountStatusVo.getConfirmtime();
            if(!TextUtils.isEmpty(finishTimeStr)){
                amountSuccAmountFinishTv.setText(finishTimeStr);
            }
        }
    }

    private void initListeners(){

        //重发发送收款请求
        amountFailResendRequestTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestResendAmountTask(amountStatusVo);
            }
        });

        //重发发送收款请求
        amountWaitResendRequestTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestResendAmountTask(amountStatusVo);
            }
        });

        //返回
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSuccess){
                    Intent intent = new Intent(AmountDetailActivity.this,AmountRecordActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amount_detail);
        initView();
        initData();
        initListeners();
    }

    /**
     * 重新发送扣款请求
     */
    private void requestResendAmountTask(AmountStatusVo amountStatusVo){
        String url = ConfigUtil.getInst().getForDomain()+"res/v1/payment";
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,Object> bizMap = new HashMap<String,Object>();
        double amount = amountStatusVo.getAmount();
        String target = amountStatusVo.getUserid();
        String orderNo = amountStatusVo.getOrderno();
        bizMap.put("amount", amount);
        bizMap.put("target",target);
        bizMap.put("orderno",orderNo);
        netRequest.setObjectParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.JSONPOST;
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
                    AmountDetailResponse amountResponse = new Gson().fromJson(result.rawResult,AmountDetailResponse.class);
                    if(null != amountResponse){
                        int resultFlag = amountResponse.getRes();
                        if(0 == resultFlag){
                            DialogUtil.getInstance().showCustomToast(AmountDetailActivity.this,"重新发送扣款成功", Gravity.CENTER);
                        }else {
                            String errorMsg = amountResponse.getResDesc();
                            if(!TextUtils.isEmpty(errorMsg)){
                                DialogUtil.getInstance().showCustomToast(AmountDetailActivity.this,errorMsg, Gravity.CENTER);
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
