package com.zkjinshi.superservice.ext.activity.facepay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.IntentUtil;
import com.zkjinshi.base.util.SoftInputUtil;
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
import com.zkjinshi.superservice.utils.AccessControlUtil;
import com.zkjinshi.superservice.utils.CacheUtil;

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

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
    private ImageButton moreIBtn;
    private ImageButton backIBtn;
    private GridView nearbyUserGv;
    private ArrayList<NearbyUserVo> nearbyUserList;
    private NearbyAdapter nearbyAdapter;
    private EditText inputPhoneNumEtv;
    private ImageButton clearIBtn,startSearchIBtn;
    private TextView noResultTv;

    private void initView(){
        titleTv = (TextView)findViewById(R.id.header_bar_tv_title);
        moreIBtn = (ImageButton)findViewById(R.id.header_bar_btn_record);
        backIBtn = (ImageButton) findViewById(R.id.header_bar_btn_back);
        nearbyUserGv = (GridView)findViewById(R.id.nearby_layout);
        inputPhoneNumEtv = (EditText)findViewById(R.id.search_etv_phone_num);
        clearIBtn = (ImageButton)findViewById(R.id.search_btn_clear);
        startSearchIBtn = (ImageButton)findViewById(R.id.search_btn_search);
        noResultTv = (TextView)findViewById(R.id.no_result);
    }

    private void initData(){

        backIBtn.setVisibility(View.VISIBLE);
        if(AccessControlUtil.isShowView(AccessControlUtil.BTNPOS)){
            moreIBtn.setVisibility(View.VISIBLE);
        }else {
            moreIBtn.setVisibility(View.GONE);
        }
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

        //附近用户列表单击操作
        nearbyUserGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                NearbyUserVo nearbyUserVo = (NearbyUserVo)nearbyAdapter.getItem(position);
                Intent intent = new Intent(CheckOutActivity.this,PayRequestActivity.class);
                intent.putExtra("nearbyUserVo",nearbyUserVo);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_bottom,
                        R.anim.slide_out_top);
            }
        });

        //手机号码输入框
        inputPhoneNumEtv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    clearIBtn.setVisibility(VISIBLE);
                } else {
                    clearIBtn.setVisibility(GONE);
                    requestNearbyUserListTask();
                }
            }
        });

        //清空手机号码
        clearIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputPhoneNumEtv.setText("");
            }
        });

        //虚拟盘搜索
        inputPhoneNumEtv.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    String phoneNumStr = inputPhoneNumEtv.getText().toString();
                    if (TextUtils.isEmpty(phoneNumStr)) {
                        DialogUtil.getInstance().showCustomToast(CheckOutActivity.this, "输入的手机号码不能为空", Gravity.CENTER);
                        return true;
                    }
                    if(!IntentUtil.isMobileNO(phoneNumStr)){
                        DialogUtil.getInstance().showCustomToast(CheckOutActivity.this, "请输入正确格式的手机号码", Gravity.CENTER);
                        return true;
                    }
                    SoftInputUtil.hideSoftInputMode(CheckOutActivity.this,inputPhoneNumEtv);
                    requestNearbyByPhoneNumTask(phoneNumStr);
                    return true;
                }
                return false;
            }
        });

        //搜索按钮
        startSearchIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumStr = inputPhoneNumEtv.getText().toString();
                if (TextUtils.isEmpty(phoneNumStr)) {
                    DialogUtil.getInstance().showCustomToast(CheckOutActivity.this, "输入的手机号码不能为空", Gravity.CENTER);
                    return;
                }
                if(!IntentUtil.isMobileNO(phoneNumStr)){
                    DialogUtil.getInstance().showCustomToast(CheckOutActivity.this, "请输入正确格式的手机号码", Gravity.CENTER);
                    return;
                }
                SoftInputUtil.hideSoftInputMode(CheckOutActivity.this,inputPhoneNumEtv);
                requestNearbyByPhoneNumTask(phoneNumStr);
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
        String url = ConfigUtil.getInst().getPyxDomain()+"lbs/v1/loc/beacon/"+shopId+"/"+CacheUtil.getInstance().getPayInfo()+"?page=0&page_size=20";
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
                        if(0 == resultFlag || 30001 == resultFlag){
                            nearbyUserGv.setNumColumns(2);
                            nearbyUserList =  nearbyUserResponse.getData();
                            if(null != nearbyUserList && !nearbyUserList.isEmpty()){
                                nearbyAdapter.setNearbyUserList(nearbyUserList);
                            }
                            if( 30001 == resultFlag){
                                nearbyUserGv.setEmptyView(noResultTv);
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

    /**
     * 根据手机号码获取用户名
     *
     */
    private void requestNearbyByPhoneNumTask(String phoneNum){
        String url = ConfigUtil.getInst().getForDomain()+"res/v1/query/si/all?phone="+phoneNum;
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
                        if(0 == resultFlag || 30001 == resultFlag){
                            nearbyUserGv.setNumColumns(1);
                            nearbyUserList =  nearbyUserResponse.getData();
                            nearbyAdapter.setNearbyUserList(nearbyUserList);
                            if( 30001 == resultFlag){
                                nearbyUserGv.setEmptyView(noResultTv);
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
