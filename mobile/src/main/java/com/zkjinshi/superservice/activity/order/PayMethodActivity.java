package com.zkjinshi.superservice.activity.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.PayAdapter;
import com.zkjinshi.superservice.base.BaseAppCompatActivity;
import com.zkjinshi.superservice.bean.PayBean;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 开发者：WinkyQin
 * 日期：2015/11/28
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class PayMethodActivity  extends BaseAppCompatActivity implements AdapterView.OnItemClickListener{

    private final static String TAG = PayMethodActivity.class.getSimpleName();

    private String mUserID;
    private String mToken;
    private String mShopID;

    private Toolbar  mToolbar;
    private TextView mTvCenterTitle;

    private ArrayList<PayBean> mPayBeanList;
    private EditText   mEtRoomPayment;
    private ListView   mLvPayMethod;
    private PayAdapter mPayAdapter;
    private PayBean    mPayBean;
    private int        mSelelectId = 0;
    private double     mRoomPayment;//房间价格

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                                     WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_pay_method);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(R.mipmap.ic_fanhui);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTvCenterTitle = (TextView) findViewById(R.id.tv_center_title);
        mTvCenterTitle.setText(getString(R.string.choose_pay_method));

        mEtRoomPayment = (EditText) findViewById(R.id.et_room_amount);
        mLvPayMethod   = (ListView) findViewById(R.id.lv_pay_method);
    }

    private void initData() {

        mUserID = CacheUtil.getInstance().getUserId();
        mToken  = CacheUtil.getInstance().getToken();
        mShopID = CacheUtil.getInstance().getShopID();

        if(!TextUtils.isEmpty(mUserID) && !TextUtils.isEmpty(mToken)
                                       && !TextUtils.isEmpty(mShopID)){
            loadPayList(mUserID, mToken, mShopID);
        }
    }

    private void initListener() {
        //界面返回
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PayMethodActivity.this.finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        //菜单点击事件
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.menu_confirm_pay_method){

                    //传递房间价格和支付方式
                    String roomPayment = mEtRoomPayment.getText().toString();
                    Double doubleAmount = 0.00;
                    if (null!=mPayBean && !TextUtils.isEmpty(roomPayment)) {
                        doubleAmount = Double.parseDouble(roomPayment);
                        Intent data = new Intent();
                        data.putExtra("room_amount", doubleAmount);
                        data.putExtra("pay_bean", mPayBean);
                        setResult(RESULT_OK, data);
                        PayMethodActivity.this.finish();
                    } else {
                        DialogUtil.getInstance().showCustomToast(PayMethodActivity.this,
                        getString(R.string.please_choose_the_pay_method_and_room_amount),
                        Gravity.CENTER);
                    }
                }
                return true;
            }
        });
    }

    /**
     * 加载支付方式
     * @param salerID
     * @param token
     * @param shopID
     */
    private void loadPayList(String salerID, String token, String shopID){
        String url = ProtocolUtil.getSempPayListUrl();
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<>();
        bizMap.put("salesid", salerID);
        bizMap.put("token", token);
        bizMap.put("shopid", shopID);
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
                try {
                    mPayBeanList = new Gson().fromJson(result.rawResult, new TypeToken<ArrayList<PayBean>>()
                                                                                              {}.getType());
                    mPayBeanList = new Gson().fromJson(result.rawResult, new TypeToken<ArrayList<PayBean>>()
                                                                                              {}.getType());
                    mPayAdapter = new PayAdapter(PayMethodActivity.this, mPayBeanList, mSelelectId);
                    mLvPayMethod.setAdapter(mPayAdapter);
                    mLvPayMethod.setOnItemClickListener(PayMethodActivity.this);
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

    //获取支付方式名字
    public String getPayName(String id){
        String payName = "未设定";
        if(mPayBeanList != null){
            for(PayBean payBean : mPayBeanList){
                if(payBean.getPay_id() == Integer.parseInt(id)){
                    payName = payBean.getPay_name();
                    break;
                }
            }
        }
        return payName;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        mPayAdapter.setCheckidByPosition(position);
        mPayAdapter.notifyDataSetChanged();
        mPayBean    = mPayAdapter.gePayByPosition(position);
        mSelelectId = mPayBean.getPay_id();
        Log.i(TAG, "mSelelectId" + mSelelectId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pay_method, menu);
        return true;
    }

}