package com.zkjinshi.superservice.activity.order;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.base.BaseActivity;
import com.zkjinshi.superservice.bean.OrderDetailBean;
import com.zkjinshi.superservice.emchat.EMConversationHelper;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.view.ItemCbxView;
import com.zkjinshi.superservice.view.ItemNumView;
import com.zkjinshi.superservice.view.ItemShowView;
import com.zkjinshi.superservice.vo.GoodInfoVo;
import com.zkjinshi.superservice.vo.OrderDetailForDisplay;

import org.jivesoftware.smack.util.Base64Encoder;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by dujiande on 2015/12/29.
 */
public class NormalDealActivity extends BaseActivity {

    private final static String TAG = NormalDealActivity.class.getSimpleName();

    private TextView titleTv,remarkTv;
    private ItemShowView dateTimeIsv;
    private ItemShowView orderNoIsv;
    private ItemNumView  peopleNumTnv;
    private ItemShowView contactTsv;
    private ItemShowView phoneTsv;
    private ItemShowView invoiceTsv;
    private ItemShowView privilegeTsv;

    private String orderNo;
    private Button confirmBtn;

    private OrderDetailForDisplay orderDetailForDisplay = null;
    private ArrayList<Calendar> calendarList = null;

    public static final int PEOPLE_REQUEST_CODE = 7;
    public static final int TICKET_REQUEST_CODE = 8;
    public static final int REMARK_REQUEST_CODE = 9;
    public static final int PHONE_REQUEST_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_deal);

        orderNo = getIntent().getStringExtra("orderNo");
        initView();
        initListener();
        loadOrderInfoByOrderNo();
    }

    private void initView() {
        titleTv = (TextView)findViewById(R.id.header_title_tv);
        remarkTv = (TextView)findViewById(R.id.tv_remark);

        orderNoIsv =  (ItemShowView)findViewById(R.id.ahb_no);
        dateTimeIsv = (ItemShowView)findViewById(R.id.ahb_date);
        contactTsv = (ItemShowView)findViewById(R.id.ahb_people);
        phoneTsv = (ItemShowView)findViewById(R.id.ahb_phone);
        peopleNumTnv = (ItemNumView)findViewById(R.id.ahb_num);
        invoiceTsv = (ItemShowView)findViewById(R.id.ahb_ticket);
        privilegeTsv = (ItemShowView)findViewById(R.id.ahb_privilege);

        confirmBtn = (Button)findViewById(R.id.btn_send_booking_order);

        Drawable drawable =  getResources().getDrawable(R.mipmap.list_ic_tequan_gary);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        privilegeTsv.titleTv.setCompoundDrawables(drawable,null,null,null);
        privilegeTsv.titleTv.setCompoundDrawablePadding(DisplayUtil.dip2px(this,10));
    }

    //根据订单号加载订单详细信息。
    private void loadOrderInfoByOrderNo() {
        String url =  ProtocolUtil.getOrderDetailUrl(orderNo);
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(this) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
                orderDetailForDisplay = null;
                DialogUtil.getInstance().showToast(NormalDealActivity.this,"获取订单详情失败");
                finish();
            }

            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {
                    orderDetailForDisplay = new Gson().fromJson( result.rawResult,OrderDetailForDisplay.class);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    orderDetailForDisplay = null;
                    DialogUtil.getInstance().showToast(NormalDealActivity.this,"获取订单详情失败");
                    finish();

                }
                if(orderDetailForDisplay != null){
                    initData();
                }

            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }

    private void unClickItems(){
        dateTimeIsv.setUnClick();
        contactTsv.setUnClick();
        phoneTsv.setUnClick();
        invoiceTsv.setUnClick();
        privilegeTsv.setUnClick();

    }

    private void initData() {
        titleTv.setText(orderDetailForDisplay.getShopname());
        String orderStatus = orderDetailForDisplay.getOrderstatus();
        //订单状态
        if(orderStatus.equals("待处理")||orderStatus.equals("待确认")||orderStatus.equals("待支付")){
            confirmBtn.setVisibility(View.VISIBLE);
            confirmBtn.setText("确认订单");
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    confirmOrder();
                }
            });
        }else{
            unClickItems();
            if(orderStatus.equals("已确认")){
                confirmBtn.setVisibility(View.VISIBLE);
                confirmBtn.setText("完成订单");
                confirmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finishOrder();
                    }
                });
            }else{
                confirmBtn.setVisibility(View.GONE);
            }
        }



        //订单号
        if(!TextUtils.isEmpty(orderDetailForDisplay.getOrderno())){
            orderNoIsv.setValue(orderDetailForDisplay.getOrderno());
        }

        //初始化时间
        if(!TextUtils.isEmpty(orderDetailForDisplay.getArrivaldate())){
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date aDate = sdf.parse(orderDetailForDisplay.getArrivaldate());
                setOrderDate(aDate);
            }catch (Exception e){
                Log.e(TAG,e.getMessage());
            }

        }else{
            dateTimeIsv.setValue("无设置");
        }

        //初始化人数
        if(orderDetailForDisplay.getPersoncount() > 0){
            peopleNumTnv.setValue(orderDetailForDisplay.getPersoncount()+"");
        }else{
            contactTsv.setValue("无设置");
        }
        //联系人
        if(!TextUtils.isEmpty(orderDetailForDisplay.getOrderedby())){
            contactTsv.setValue(orderDetailForDisplay.getOrderedby());
        }else{
            contactTsv.setValue(" ");
        }
        //联系人号码
        if(!TextUtils.isEmpty(orderDetailForDisplay.getTelephone())){
            phoneTsv.setValue(orderDetailForDisplay.getTelephone());
        }else{
            phoneTsv.setValue(" ");
        }
        //发票
        if(!TextUtils.isEmpty(orderDetailForDisplay.getCompany())){
            invoiceTsv.setValue(orderDetailForDisplay.getCompany());
        }else{
            invoiceTsv.setValue(" ");
        }

        //特权
        if(!TextUtils.isEmpty(orderDetailForDisplay.getPrivilegeName())){
            privilegeTsv.setValue(orderDetailForDisplay.getPrivilegeName());
        }else{
            privilegeTsv.setValue("暂无");
        }
        //备注
        if(!TextUtils.isEmpty(orderDetailForDisplay.getRemark())){
            remarkTv.setText(orderDetailForDisplay.getRemark());
        }else{
            remarkTv.setText("");
        }

    }



    private void initListener() {
        //返回
        findViewById(R.id.header_back_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });


        //日期
        dateTimeIsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dateTimeIsv.isShowIcon){
                    Intent intent = new Intent(NormalDealActivity.this, CalendarActivity.class);
                    if (calendarList != null) {
                        intent.putExtra("calendarList", calendarList);
                    }
                    startActivityForResult(intent, CalendarActivity.CALENDAR_REQUEST_CODE);
                    overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);
                }

            }
        });

        //发票
        invoiceTsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(invoiceTsv.isShowIcon){
                    Intent intent = new Intent(NormalDealActivity.this, AddRemarkActivity.class);
                    intent.putExtra("remark", invoiceTsv.getValue());
                    intent.putExtra("tips", "");
                    intent.putExtra("title", "添加发票信息");
                    intent.putExtra("hint", "请输入发票信息");
                    intent.putExtra("key", "invoice");
                    startActivityForResult(intent, TICKET_REQUEST_CODE);
                    overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);
                }

            }
        });
        //备注
        findViewById(R.id.llt_order_remark).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(invoiceTsv.isShowIcon){
                    Intent intent = new Intent(NormalDealActivity.this, AddRemarkActivity.class);
                    intent.putExtra("remark", remarkTv.getText());
                    intent.putExtra("tips", "如果有其他要求，请在此说明。");
                    intent.putExtra("title", "添加订单备注");
                    intent.putExtra("hint", "请输入订单备注");
                    intent.putExtra("key", "remark");
                    startActivityForResult(intent, REMARK_REQUEST_CODE);
                    overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);
                }

            }
        });
        //联系人
        contactTsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(contactTsv.isShowIcon){
                    Intent intent = new Intent(NormalDealActivity.this, AddRemarkActivity.class);
                    intent.putExtra("remark", contactTsv.getValue());
                    intent.putExtra("tips", "");
                    intent.putExtra("title", "编辑联系人");
                    intent.putExtra("hint", "请输入姓名");
                    intent.putExtra("key", "name");
                    startActivityForResult(intent, PEOPLE_REQUEST_CODE);
                    overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);
                }

            }
        });
        //手机号码
        phoneTsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(phoneTsv.isShowIcon){
                    Intent intent = new Intent(NormalDealActivity.this, AddRemarkActivity.class);
                    intent.putExtra("remark", phoneTsv.getValue());
                    intent.putExtra("tips", "");
                    intent.putExtra("title", "编辑联系方式");
                    intent.putExtra("hint", "请输入手机号");
                    intent.putExtra("key", "phone");
                    startActivityForResult(intent, PHONE_REQUEST_CODE);
                    overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
           if(TICKET_REQUEST_CODE == requestCode){
                if(null != data){
                    String remark = data.getStringExtra("remark");
                    invoiceTsv.setValue(remark);
                    orderDetailForDisplay.setIsinvoice(1);
                    orderDetailForDisplay.setCompany(remark);
                }
            }
            else if(REMARK_REQUEST_CODE == requestCode){
                if(null != data){
                    String remark = data.getStringExtra("remark");
                    remarkTv.setText(remark);
                    orderDetailForDisplay.setRemark(remark);
                }
            }
            else if(PEOPLE_REQUEST_CODE == requestCode){
                if(null != data){
                    String remark = data.getStringExtra("remark");
                    contactTsv.setValue(remark);
                    orderDetailForDisplay.setOrderedby(remark);
                }
            }
            else if(PHONE_REQUEST_CODE == requestCode){
                if(null != data){
                    String remark = data.getStringExtra("remark");
                    phoneTsv.setValue(remark);
                    orderDetailForDisplay.setTelephone(remark);
                }
            }
        }
    }

    //设置到达的日期
    private void setOrderDate(Date arriveDate) {

        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm");
        String arriveStr = sdf.format(arriveDate);
        dateTimeIsv.setValue(arriveStr);

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        orderDetailForDisplay.setArrivaldate(sdf2.format(arriveDate));
    }

    //确认订单
    private void confirmOrder() {
        String url = ProtocolUtil.getUpdateOrderUrl();
        Log.i(TAG,url);
        NetRequest netRequest = new NetRequest(url);
        if(null == orderDetailForDisplay){
            return ;
        }
        int num = Integer.parseInt(peopleNumTnv.getValue());
        orderDetailForDisplay.setPersoncount(num);
        orderDetailForDisplay.setOrderstatus("1");
        Gson gson = new GsonBuilder().serializeNulls().create();
        String jsonOrder = gson.toJson(orderDetailForDisplay, OrderDetailForDisplay.class);
        if(TextUtils.isEmpty(jsonOrder)){
            return ;
        }
        HashMap<String, String> bigMap = new HashMap<>();
        bigMap.put("data", Base64Encoder.getInstance().encode(jsonOrder));
        netRequest.setBizParamMap(bigMap);
        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.JSON;
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

                String jsonResult = result.rawResult;
                if(TextUtils.isEmpty(jsonResult)){
                    return ;
                }

                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(jsonResult);
                JsonObject object = element.getAsJsonObject();
                Boolean updateResult = object.get("result").getAsBoolean();
                if(updateResult){
                    String orderNO = object.get("data").getAsString();
                    EMConversationHelper.getInstance().sendOrderCmdMessage(
                            orderDetailForDisplay.getShopid(),
                            orderNO,
                            orderDetailForDisplay.getUserid(),
                            new EMCallBack() {

                                @Override
                                public void onSuccess() {
                                    Log.i(TAG, "发送订单确认信息成功");
                                }

                                @Override
                                public void onError(int i, String s) {
                                    Log.i(TAG, "errorMsg:" + s);
                                    Log.i(TAG, "errorCode" + i);
                                }

                                @Override
                                public void onProgress(int i, String s) {

                                }
                            });
                    DialogUtil.getInstance().showToast(NormalDealActivity.this,"订单修改成功");
                    finish();
                } else {
                    DialogUtil.getInstance().showToast(NormalDealActivity.this,"订单修改失败");
                }

            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }

    //完成订单
    public void finishOrder(){
        String url = ProtocolUtil.getConfirmOrderUrl();
        Log.i(TAG,url);
        NetRequest netRequest = new NetRequest(url);
        if(null == orderDetailForDisplay){
            return ;
        }

        HashMap<String, String> bigMap = new HashMap<>();
        bigMap.put("orderno", orderDetailForDisplay.getOrderno());
        bigMap.put("status","4");
        netRequest.setBizParamMap(bigMap);
        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.JSON;
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

                String jsonResult = result.rawResult;
                if(TextUtils.isEmpty(jsonResult)){
                    return ;
                }

                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(jsonResult);
                JsonObject object = element.getAsJsonObject();
                Boolean updateResult = object.get("result").getAsBoolean();
                if(updateResult){
                    String orderNO = object.get("data").getAsString();
                    DialogUtil.getInstance().showToast(NormalDealActivity.this,"订单修改成功");
                    finish();
                } else {
                    DialogUtil.getInstance().showToast(NormalDealActivity.this,"订单修改失败");
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
