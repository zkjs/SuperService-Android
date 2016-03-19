package com.zkjinshi.superservice.ext.activity.facepay;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blueware.agent.android.B;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.ext.response.AmountDetailResponse;
import com.zkjinshi.superservice.ext.util.MathUtil;
import com.zkjinshi.superservice.ext.vo.AmountStatusVo;
import com.zkjinshi.superservice.ext.vo.NearbyUserVo;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.utils.ProtocolUtil;

import java.util.HashMap;

/**
 * 收款页面
 * 开发者：JimmyZhang
 * 日期：2016/3/7
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class PayRequestActivity extends Activity {

    public static final String TAG = PayRequestActivity.class.getSimpleName();

    private ImageButton backIBtn;
    private TextView titleTv;
    private LinearLayout payRequestLayout,payRequestSuccLayout;
    private SimpleDraweeView userPhotoDv;
    private TextView userNameTv,finishUserNameTv,finishAmount;
    private EditText inputPriceEtv;
    private Button confirmBtn,finishConfirmBtn;
    private NearbyUserVo nearbyUserVo;

    private void initView(){
        backIBtn = (ImageButton)findViewById(R.id.header_bar_btn_back);
        titleTv = (TextView)findViewById(R.id.header_bar_tv_title);
        payRequestLayout = (LinearLayout)findViewById(R.id.start_request_pay_layout);
        payRequestSuccLayout = (LinearLayout)findViewById(R.id.request_pay_succ_layout);
        userPhotoDv = (SimpleDraweeView)findViewById(R.id.user_photo_dv);
        userNameTv = (TextView)findViewById(R.id.user_name_tv);
        inputPriceEtv = (EditText)findViewById(R.id.input_price_etv);
        confirmBtn = (Button)findViewById(R.id.btn_confirm);
        finishUserNameTv = (TextView)findViewById(R.id.finish_user_name_tv);
        finishAmount = (TextView)findViewById(R.id.finish_amount_tv);
        finishConfirmBtn = (Button)findViewById(R.id.btn_finish_confirm);
    }

    private void initData(){
        backIBtn.setVisibility(View.VISIBLE);
        titleTv.setText("开始收款");
        payRequestLayout.setVisibility(View.VISIBLE);
        payRequestSuccLayout.setVisibility(View.GONE);
        if(null != getIntent() && null != getIntent().getSerializableExtra("nearbyUserVo")){
            nearbyUserVo = (NearbyUserVo)getIntent().getSerializableExtra("nearbyUserVo");
        }
        if(null != nearbyUserVo){
            String userNameStr = nearbyUserVo.getUsername();
            if(!TextUtils.isEmpty(userNameStr)){
                userNameTv.setText(userNameStr);
                finishUserNameTv.setText(userNameStr);
            }
            String userimage = nearbyUserVo.getUserimage();
            if(!TextUtils.isEmpty(userimage)){
                String path = ConfigUtil.getInst().getImgDomain()+userimage;
                if(!TextUtils.isEmpty(path)){
                    userPhotoDv.setImageURI(Uri.parse(path));
                }
            }
        }
    }

    private void initListeners(){

        //返回
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //提交收款请求
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount = inputPriceEtv.getText().toString();
                if(MathUtil.isPriceOk(amount)){
                    if(!TextUtils.isEmpty(amount)){
                        finishAmount.setText(amount);
                        long amountL = MathUtil.parsePrice(amount);
                        if(amountL > 0){
                            requestChargeTask(nearbyUserVo,amountL);
                        }else {
                            DialogUtil.getInstance().showCustomToast(PayRequestActivity.this,"请输入正确金额!",Gravity.CENTER);
                        }
                    }else {
                        DialogUtil.getInstance().showCustomToast(PayRequestActivity.this,"输入金额不能为空!",Gravity.CENTER);
                    }
                }else {
                    DialogUtil.getInstance().showCustomToast(PayRequestActivity.this,"请输入正确金额!",Gravity.CENTER);
                }
            }
        });

        //完成
        finishConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_request);
        initView();
        initData();
        initListeners();
    }

    /**
     * 请求扣款
     */
    private void requestChargeTask(final NearbyUserVo nearbyUserVo,final long amount){
        String url = ConfigUtil.getInst().getForDomain()+"res/v1/payment";
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,Object> bizMap = new HashMap<String,Object>();
        String target = nearbyUserVo.getUserid();
        bizMap.put("amount", amount);
        bizMap.put("target",target);
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
                            if(amount <= 10000){
                                AmountStatusVo amountStatusVo = amountResponse.getData();
                                Intent intent = new Intent(PayRequestActivity.this,AmountDetailActivity.class);
                                intent.putExtra("amountStatusVo",amountStatusVo);
                                startActivity(intent);
                            }else {
                                String userNameStr = nearbyUserVo.getUsername();
                                String msgTxt = "发送成功，等待"+userNameStr+"确认";
                                DialogUtil.getInstance().showCustomToast(PayRequestActivity.this,msgTxt, Gravity.CENTER);
                                payRequestLayout.setVisibility(View.GONE);
                                payRequestSuccLayout.setVisibility(View.VISIBLE);
                            }
                        }else {
                            String errorMsg = amountResponse.getResDesc();
                            if(!TextUtils.isEmpty(errorMsg)){
                                DialogUtil.getInstance().showCustomToast(PayRequestActivity.this,errorMsg, Gravity.CENTER);
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
