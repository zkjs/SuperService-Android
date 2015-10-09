package com.zkjinshi.superservice.activity.order;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.PayAdapter;
import com.zkjinshi.superservice.bean.NoticeBean;
import com.zkjinshi.superservice.bean.OrderDetailBean;
import com.zkjinshi.superservice.bean.PayBean;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestListener;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.view.CircleImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


/**
 * 账单确定
 * 开发者：dujiande
 * 日期：2015/10/6
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderPayActivity  extends Activity implements AdapterView.OnItemClickListener{
    private final static String TAG = OrderPayActivity.class.getSimpleName();

    private OrderDetailBean orderDetailBean;

    private TextView usernameTv;
    private TextView userInfoTv;
    private TextView orderInfoTv;
    private CircleImageView avatarCiv;
    private EditText priceEt;
    private EditText priceReasonEt;
    private ListView payListView;
    private ArrayList<PayBean> payBeanList;
    private PayAdapter payAdapter;
    private PayBean selectPay;
    private int selelectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        //屏蔽输入法自动弹出
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_order_pay);
        orderDetailBean = (OrderDetailBean)getIntent().getSerializableExtra("orderDetailBean");

        if(orderDetailBean != null){
            initView();
            initData();
            initListener();
        }

    }

    private void initView() {
        usernameTv = (TextView)findViewById(R.id.tv_username);
        userInfoTv = (TextView)findViewById(R.id.tv_user_info);
        orderInfoTv = (TextView)findViewById(R.id.order_text);
        avatarCiv = (CircleImageView)findViewById(R.id.avatar);
        priceEt = (EditText)findViewById(R.id.order_rate);
        priceReasonEt = (EditText)findViewById(R.id.price_remark);
        payListView = (ListView)findViewById(R.id.pay_listview);
    }

    private void initData() {
        if(!TextUtils.isEmpty(orderDetailBean.getRoom().getPayment())){
            selelectId = Integer.parseInt(orderDetailBean.getRoom().getPayment());
        }else{
            selelectId = 0;
        }
        usernameTv.setText(orderDetailBean.getRoom().getGuest());
        ImageLoader.getInstance().displayImage(ProtocolUtil.getAvatarUrl(orderDetailBean.getRoom().getUserid()), avatarCiv);

        try{
            SimpleDateFormat mSimpleFormat  = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat mChineseFormat = new SimpleDateFormat("MM/dd");
            Date arrivalDate =  mSimpleFormat.parse(orderDetailBean.getRoom().getArrival_date());
            Date departureDate =  mSimpleFormat.parse(orderDetailBean.getRoom().getDeparture_date());
            String arrivalStr = mChineseFormat.format(arrivalDate);
            String departureStr = mChineseFormat.format(departureDate);
            String roomType = orderDetailBean.getRoom().getRoom_type();
            int roomNum = orderDetailBean.getRoom().getRooms();
            int dayNum = TimeUtil.daysBetween(arrivalDate, departureDate);
            orderInfoTv.setText(roomType+"×"+roomNum+"|"+dayNum+"晚|"+arrivalStr+"-"+departureStr);

        }catch (Exception e){
            Log.e(TAG,e.getMessage());
        }

        if(!TextUtils.isEmpty(orderDetailBean.getRoom().getRoom_rate())){
            priceEt.setText(orderDetailBean.getRoom().getRoom_rate());
        }
        userInfoTv.setText("");
        loadPayList();
        loadUserInfo();

    }


    private void initListener() {
        findViewById(R.id.back_btn_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        findViewById(R.id.go_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(selelectId == 0){
                   DialogUtil.getInstance().showToast(OrderPayActivity.this,"请选择支付方式。");
                   return;
               }

            }
        });
    }

    /**
     *获取用户资料
     */
    private void loadUserInfo() {
        String url = ProtocolUtil.getSempNoticeUrl();
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("salesid", CacheUtil.getInstance().getUserId());
        bizMap.put("token", CacheUtil.getInstance().getToken());
        bizMap.put("uid", orderDetailBean.getRoom().getUserid());
        bizMap.put("shopid", orderDetailBean.getRoom().getShopid());
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.POST;
        netRequestTask.setNetRequestListener(new NetRequestListener() {
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
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {
                    NoticeBean noticeBean = new Gson().fromJson(result.rawResult,NoticeBean.class);
                    if(noticeBean.isSet()){
                        usernameTv.setText(noticeBean.getUsername());
                        userInfoTv.setText(noticeBean.getOrder_count()+"次订单");
                    }else{
                        Log.e(TAG, noticeBean.getErr());
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


    /**
     * 加载支付方式
     */
    private void loadPayList(){
        String url = ProtocolUtil.getSempPayListUrl();
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("salesid", CacheUtil.getInstance().getUserId());
        bizMap.put("token", CacheUtil.getInstance().getToken());
        bizMap.put("shopid", orderDetailBean.getRoom().getShopid());
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.POST;
        netRequestTask.setNetRequestListener(new NetRequestListener() {
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
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {
                    payBeanList = new Gson().fromJson(result.rawResult, new TypeToken<ArrayList<PayBean>>() {}.getType());
                    payAdapter = new PayAdapter(OrderPayActivity.this,payBeanList,selelectId);
                    payListView.setAdapter(payAdapter);
                    payListView.setOnItemClickListener(OrderPayActivity.this);

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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Log.e(TAG, "positoin" + position);
        payAdapter.setCheckidByPosition(position);
        payAdapter.notifyDataSetChanged();
        selectPay = payAdapter.gePayByPosition(position);
        selelectId = selectPay.getPay_id();
    }
}
