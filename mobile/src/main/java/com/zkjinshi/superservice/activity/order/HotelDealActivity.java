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
import android.widget.Toast;

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
import org.w3c.dom.Text;

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
public class HotelDealActivity extends Activity {

    private final static String TAG = HotelDealActivity.class.getSimpleName();

    private TextView titleTv,remarkTv;
    private ItemShowView dateTimeIsv;
    private ItemShowView orderNoIsv;
    private ItemShowView roomTypeTsv;
    private ItemNumView roomNumTnv;
    private ItemShowView contactTsv;
    private ItemShowView phoneTsv;
    private ItemShowView payTypeTsv;
    private ItemShowView invoiceTsv;
    private ItemShowView privilegeTsv;

    private ItemCbxView breakfastTcv;
    private ItemCbxView noSmokeTcv;

    private String orderNo;
    int payType;

    private Button confirmBtn;
    private DisplayImageOptions options;
    private OrderDetailForDisplay orderDetailForDisplay = null;
    private ArrayList<Calendar> calendarList = null;
    private GoodInfoVo lastGoodInfoVo = null;

    public static final int GOOD_REQUEST_CODE = 6;
    public static final int PEOPLE_REQUEST_CODE = 7;
    public static final int TICKET_REQUEST_CODE = 8;
    public static final int REMARK_REQUEST_CODE = 9;
    public static final int PHONE_REQUEST_CODE = 10;
    public static final int PAY_REQUEST_CODE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_deal);

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
        roomTypeTsv = (ItemShowView)findViewById(R.id.ahb_type);
        roomNumTnv = (ItemNumView)findViewById(R.id.ahb_num);
        contactTsv = (ItemShowView)findViewById(R.id.ahb_people);
        phoneTsv = (ItemShowView)findViewById(R.id.ahb_phone);
        payTypeTsv = (ItemShowView)findViewById(R.id.ahb_pay);
        invoiceTsv = (ItemShowView)findViewById(R.id.ahb_ticket);
        privilegeTsv = (ItemShowView)findViewById(R.id.ahb_privilege);

        breakfastTcv = (ItemCbxView)findViewById(R.id.ahb_breakfast);
        noSmokeTcv = (ItemCbxView)findViewById(R.id.ahb_nosmoking);

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
                DialogUtil.getInstance().showToast(HotelDealActivity.this,"获取订单详情失败");
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
                    DialogUtil.getInstance().showToast(HotelDealActivity.this,"获取订单详情失败");
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
        roomTypeTsv.setUnClick();
        roomNumTnv.setUnClick();
        contactTsv.setUnClick();
        phoneTsv.setUnClick();
        invoiceTsv.setUnClick();
        privilegeTsv.setUnClick();

        breakfastTcv.valueCbx.setEnabled(false);
        noSmokeTcv.valueCbx.setEnabled(false);
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
                    if(orderDetailForDisplay.getPaytype()== 0){
                        DialogUtil.getInstance().showToast(HotelDealActivity.this,"请设置支付方式");
                        return;
                    }else if(orderDetailForDisplay.getPaytype()== 1 && orderDetailForDisplay.getRoomprice().doubleValue() <= 0.0 ){
                        DialogUtil.getInstance().showToast(HotelDealActivity.this,"请设置价格");
                        return;
                    }
                    confirmOrder();
                }
            });
        }else{
            unClickItems();
            if(orderStatus.equals("已确认")){
                confirmBtn.setVisibility(View.VISIBLE);
                confirmBtn.setText("订单完成");
                confirmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finishOrder();
                    }
                });
            }else if(orderStatus.equals("已完成")){
                confirmBtn.setVisibility(View.VISIBLE);
                confirmBtn.setText("订单已完成");
                confirmBtn.setEnabled(false);
            }else{
                confirmBtn.setVisibility(View.GONE);
            }
        }

        if( orderDetailForDisplay.getDoublebreakfeast() == 1){
            breakfastTcv.valueCbx.setChecked(true);

        }else{
            breakfastTcv.valueCbx.setChecked(false);
        }

        if(orderDetailForDisplay.getNosmoking() == 1){
            noSmokeTcv.valueCbx.setChecked(true);
        }else{
            noSmokeTcv.valueCbx.setChecked(false);
        }

        //订单号
        if(!TextUtils.isEmpty(orderDetailForDisplay.getOrderno())){
            orderNoIsv.setValue(orderDetailForDisplay.getOrderno());
        }
        //房型
        if(!TextUtils.isEmpty(orderDetailForDisplay.getRoomtype())){
            roomTypeTsv.setValue(orderDetailForDisplay.getRoomtype());
        }
        //房间数量
        roomNumTnv.setValue(orderDetailForDisplay.getRoomcount()+"");
        //初始化时间
        setOrderDate();
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
        if(!TextUtils.isEmpty(orderDetailForDisplay.getPriviledgename())){
            privilegeTsv.setValue(orderDetailForDisplay.getPriviledgename());
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

        //支付方式
        payTypeTsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(payTypeTsv.isShowIcon){
                    Intent intent = new Intent(HotelDealActivity.this, OrderPayActivity.class);
                    intent.putExtra("orderDetailForDisplay", orderDetailForDisplay);
                    startActivityForResult(intent, PAY_REQUEST_CODE);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });

        //日期
        dateTimeIsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dateTimeIsv.isShowIcon){
                    Intent intent = new Intent(HotelDealActivity.this, CalendarActivity.class);
                    if (calendarList != null) {
                        intent.putExtra("calendarList", calendarList);
                    }
                    startActivityForResult(intent, CalendarActivity.CALENDAR_REQUEST_CODE);
                    overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);
                }

            }
        });
        //房型
        roomTypeTsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(roomTypeTsv.isShowIcon){
                    Intent intent = new Intent(HotelDealActivity.this, GoodListActivity.class);
                    intent.putExtra("GoodInfoVo", lastGoodInfoVo);
                    intent.putExtra("shopid",orderDetailForDisplay.getShopid());
                    startActivityForResult(intent, GOOD_REQUEST_CODE);
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
                    Intent intent = new Intent(HotelDealActivity.this, AddRemarkActivity.class);
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
                    Intent intent = new Intent(HotelDealActivity.this, AddRemarkActivity.class);
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
                    Intent intent = new Intent(HotelDealActivity.this, AddRemarkActivity.class);
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
                    Intent intent = new Intent(HotelDealActivity.this, AddRemarkActivity.class);
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

        //双早
        breakfastTcv.valueCbx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    orderDetailForDisplay.setDoublebreakfeast(1);
                }else{
                    orderDetailForDisplay.setDoublebreakfeast(0);
                }

            }
        });

        //无烟
        noSmokeTcv.valueCbx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    orderDetailForDisplay.setNosmoking(1);
                }else{
                    orderDetailForDisplay.setNosmoking(0);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            if (CalendarActivity.CALENDAR_REQUEST_CODE == requestCode) {
                if (null != data) {
                    calendarList = (ArrayList<Calendar>) data.getSerializableExtra("calendarList");
                    changeOrderDate(calendarList);
                }
            } else if (GOOD_REQUEST_CODE == requestCode) {
                if (null != data) {
                    lastGoodInfoVo = (GoodInfoVo) data.getSerializableExtra("GoodInfoVo");
                    setOrderRoomType(lastGoodInfoVo);
                }
            } else if(TICKET_REQUEST_CODE == requestCode){
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
            else if(PAY_REQUEST_CODE == requestCode){
                if(null != data){
                    String room_rate = data.getStringExtra("room_rate");
                    String payment = data.getStringExtra("payment");
                    String payment_name = data.getStringExtra("payment_name");
                    //String reason = data.getStringExtra("reason");
                    //String guest = data.getStringExtra("guest");

                    payTypeTsv.setValue("¥"+room_rate + "  " + payment_name);
                    if(!TextUtils.isEmpty(payment)){
                        orderDetailForDisplay.setPaytype(Integer.parseInt(payment));
                    }
                    if(!TextUtils.isEmpty(room_rate)){
                        BigDecimal rate = new BigDecimal(room_rate);
                        orderDetailForDisplay.setRoomprice(rate);
                    }

                }
            }
        }
    }

    //设置离开和到达的日期
    private void setOrderDate() {
        try {
             SimpleDateFormat sdf1 = new SimpleDateFormat("MM月dd日");
             SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
             Date arrivalDate = sdf2.parse(orderDetailForDisplay.getArrivaldate());
             Date leaveDate = sdf2.parse(orderDetailForDisplay.getLeavedate());

             calendarList = new ArrayList<Calendar>();

            Calendar arrivalCalendar = Calendar.getInstance();
            arrivalCalendar.setTime(arrivalDate);
            calendarList.add(arrivalCalendar);

            Calendar leaveCalendar = Calendar.getInstance();
            leaveCalendar.setTime(leaveDate);
            calendarList.add(leaveCalendar);

             String arriveStr = sdf1.format(arrivalDate);
             String leaveStr = sdf1.format(leaveDate);

             int dayNum = TimeUtil.daysBetween(arrivalDate, leaveDate);
             dateTimeIsv.setValue(arriveStr+"-"+leaveStr+","+dayNum+"晚");

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    //修改离开和到达的日期
    private void changeOrderDate(ArrayList<Calendar> calendarList) {
        SimpleDateFormat mChineseFormat = new SimpleDateFormat("MM月dd日");
        Date arrivalDate = calendarList.get(0).getTime();
        Date leaveDate = calendarList.get(1).getTime();

        String arriveStr = mChineseFormat.format(arrivalDate);
        String leaveStr = mChineseFormat.format(leaveDate);
        try {
            int dayNum = TimeUtil.daysBetween(arrivalDate, leaveDate);
            dateTimeIsv.setValue(arriveStr+"-"+leaveStr+","+dayNum+"晚");
        } catch (Exception e) {
            e.printStackTrace();
        }

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        orderDetailForDisplay.setArrivaldate(sdf2.format(arrivalDate));
        orderDetailForDisplay.setLeavedate(sdf2.format(leaveDate));
    }

    //设置房型图片
    private void setOrderRoomType(GoodInfoVo goodInfoVo) {
        lastGoodInfoVo = goodInfoVo;
        String imageUrl = goodInfoVo.getImgurl();
        if (!TextUtils.isEmpty(imageUrl)) {
            orderDetailForDisplay.setImgurl(imageUrl);
        }
        orderDetailForDisplay.setProductid(goodInfoVo.getId());
        orderDetailForDisplay.setRoomtype(goodInfoVo.getRoom());
        roomTypeTsv.setValue(goodInfoVo.getRoom());
    }

    //确认订单
    private void confirmOrder() {
        String url = ProtocolUtil.getUpdateOrderUrl();
        Log.i(TAG,url);
        NetRequest netRequest = new NetRequest(url);
        if(null == orderDetailForDisplay){
            return ;
        }
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
                    DialogUtil.getInstance().showToast(HotelDealActivity.this,"订单修改成功");
                    finish();
                } else {
                    DialogUtil.getInstance().showToast(HotelDealActivity.this,"订单修改失败");
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
                    DialogUtil.getInstance().showToast(HotelDealActivity.this,"订单修改成功");
                    finish();
                } else {
                    DialogUtil.getInstance().showToast(HotelDealActivity.this,"订单修改失败");
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
