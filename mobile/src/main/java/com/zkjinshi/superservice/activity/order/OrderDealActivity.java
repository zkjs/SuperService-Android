package com.zkjinshi.superservice.activity.order;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.OrderAdapter;
import com.zkjinshi.superservice.bean.AddOrderBean;
import com.zkjinshi.superservice.bean.GoodBean;
import com.zkjinshi.superservice.bean.OrderBean;
import com.zkjinshi.superservice.bean.OrderDetailBean;
import com.zkjinshi.superservice.bean.OrderInvoiceBean;
import com.zkjinshi.superservice.bean.OrderPrivilegeBean;
import com.zkjinshi.superservice.bean.OrderRoomTagBean;
import com.zkjinshi.superservice.bean.OrderUsersBean;
import com.zkjinshi.superservice.bean.PayBean;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestListener;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.sqlite.DBOpenHelper;
import com.zkjinshi.superservice.sqlite.UserDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.utils.StringUtil;
import com.zkjinshi.superservice.view.ItemUserSettingView;
import com.zkjinshi.superservice.vo.UserVo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;

/**
 * 订单处理页面
 * 开发者：dujiande
 * 日期：2015/9/28
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderDealActivity extends Activity {

    private static final String TAG = OrderDealActivity.class.getSimpleName();

    public static final int GOOD_REQUEST_CODE = 6;
    public static final int PAY_REQUEST_CODE = 7;
    public static final int TICKET_REQUEST_CODE = 8;
    public static final int REMARK_REQUEST_CODE = 9;

    private TagView mRoomTagView;
    private TagView mServiceTagView;

    private ItemUserSettingView mIusvPlayType;
    private ItemUserSettingView mIusvRoomNumber;
    private TextView mTvTicket;
    private TextView mTvRemark;
    private Button finishBtn;
    private ArrayList<ItemUserSettingView> customerList;

    private TextView        mTvArriveDate;
    private TextView        mTvLeaveDate;
    private TextView        mTvDateTips;
    private LinearLayout    mLltDateContainer;

    private SimpleDateFormat mSimpleFormat;
    private SimpleDateFormat mChineseFormat;

    private ArrayList<Calendar> calendarList = null;

    private int dayNum;
    private OrderDetailBean orderDetailBean;
    private boolean isBooking = false;
    private UserVo userVo;
    private String reservationNo;
    private ArrayList<PayBean> payBeans = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_deal);
        DBOpenHelper.DB_NAME = CacheUtil.getInstance().getUserId() + ".db";
        userVo = UserDBUtil.getInstance().queryUserById(CacheUtil.getInstance().getUserId());

        initView();
        Serializable serializable = getIntent().getSerializableExtra("book_order");
        if(serializable == null){
            isBooking = false;
            reservationNo = getIntent().getStringExtra("reservation_no");
            if(!TextUtils.isEmpty(reservationNo)){
                loadPayList();
            }

        }else{
            isBooking = true;
            orderDetailBean = (OrderDetailBean)serializable;
            initData();
        }
        initListener();
    }

    /**
     * 加载支付方式
     */
    private void loadPayList(){
        String url = ProtocolUtil.getSempPayListUrl();
        Log.i(TAG,url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("salesid", CacheUtil.getInstance().getUserId());
        bizMap.put("token", CacheUtil.getInstance().getToken());
        bizMap.put("shopid", userVo.getShopId());
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
                    payBeans = new Gson().fromJson(result.rawResult, new TypeToken<ArrayList<PayBean>>() {
                    }.getType());
                    loadOrder();
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
    /*
    加载订单
     */
    private void loadOrder() {
        String url = ProtocolUtil.getSempShowUrl();
        Log.i(TAG,url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("salesid", CacheUtil.getInstance().getUserId());
        bizMap.put("token",CacheUtil.getInstance().getToken());
        bizMap.put("reservation_no", reservationNo);
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
                    orderDetailBean = new Gson().fromJson(result.rawResult, OrderDetailBean.class);
                    initData();
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

    private void initView() {
        mRoomTagView = (TagView)findViewById(R.id.tagview_room_tags);
        mServiceTagView = (TagView)findViewById(R.id.tagview_service_tags);

        mTvArriveDate = (TextView)findViewById(R.id.tv_arrive_date);
        mTvLeaveDate  = (TextView)findViewById(R.id.tv_leave_date);
        mTvDateTips   = (TextView)findViewById(R.id.tv_date_tips);
        mLltDateContainer = (LinearLayout)findViewById(R.id.llt_date_container);

        mIusvRoomNumber = (ItemUserSettingView)findViewById(R.id.aod_room_number);
        addRightIcon(mIusvRoomNumber);

        mIusvPlayType  = (ItemUserSettingView)findViewById(R.id.pay_type);
        addRightIcon(mIusvPlayType);
        customerList = new ArrayList<ItemUserSettingView>();
        int[] customerIds = {R.id.aod_customer1,R.id.aod_customer2,R.id.aod_customer3};
        for(int i=0;i<customerIds.length;i++){
            ItemUserSettingView item = (ItemUserSettingView) findViewById(customerIds[i]);
            customerList.add((ItemUserSettingView) findViewById(customerIds[i]));
        }

        mTvTicket  = (TextView)findViewById(R.id.tv_ticket);
        mTvRemark = (TextView)findViewById(R.id.tv_remark);
        finishBtn = (Button)findViewById(R.id.btn_finish);

    }

    private void addRightIcon(ItemUserSettingView item){
        Drawable drawable= getResources().getDrawable(R.mipmap.ic_get_into_w);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        item.getmTextContent2().setCompoundDrawables(null, null, drawable, null);
    }

    private void initData() {
        if(orderDetailBean == null){
            return;
        }
        //初始化订单状态的显示
        initOrderStatus();

        initCalendar();
        initRoomTags();
        initServiceTags();

        //初始化入住人信息
        initPeople();
        //初始化发票
        initTicket();
        //初始化订单备注
        initRemark();
        //初始化支付方式
        initPayType();
    }

    //初始化支付方式
    private void initPayType() {
        String rate = orderDetailBean.getRoom().getRoom_rate();
        if(TextUtils.isEmpty(rate)){
            rate = "未设定";
        }else{
            rate = "￥"+rate;
        }
        mIusvPlayType.setTextTitle(rate);

        String payType = orderDetailBean.getRoom().getPayment();
        if(TextUtils.isEmpty(payType)){
            payType = "未设定";
        }else{
            payType = getPayName(payType);
        }
        mIusvPlayType.setTextContent2(payType);
    }

    //获取支付方式名字
    public String getPayName(String id){
        String payName = "未设定";
        if(payBeans != null){
            for(PayBean payBean : payBeans){
                if(payBean.getPay_id() == Integer.parseInt(id)){
                    payName = payBean.getPay_name();
                    break;
                }
            }
        }

        return payName;
    }

    //初始化订单状态的显示
    private void initOrderStatus() {
        //订单状态 默认0可取消订单 1已取消订单 2已确认订单 3已经完成的订单 4正在入住中 5已删除订单
        //支付状态 0未支付,1已支付,3支付一部分,4已退款, 5已挂账

        final String orderStatus = orderDetailBean.getRoom().getStatus();
        String payStatus = orderDetailBean.getRoom().getPay_status();
    }

    //初始化订单备注
    private void initRemark() {
        if(!StringUtil.isEmpty(orderDetailBean.getRoom().getRemark())){
            mTvRemark.setText(orderDetailBean.getRoom().getRemark());
        }
    }

    //初始化发票
    private void initTicket() {
        OrderInvoiceBean orderInvoiceBean = orderDetailBean.getInvoice();
        if(orderInvoiceBean != null && orderInvoiceBean.getInvoice_title() != null){
            mTvTicket.setText(orderInvoiceBean.getInvoice_title());
        }
        else{
            mTvTicket.setText("");
        }

    }

    //初始化入住人信息
    private void initPeople(){
        notifyRoomNumberChange();

//        for(int i=0;i<customerList.size();i++){
//            Drawable d= getResources().getDrawable(R.mipmap.ic_get_into_w);
//            d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
//            customerList.get(i).getmTextContent2().setCompoundDrawables(null, null, d, null);
//        }

        ArrayList<OrderUsersBean> users = orderDetailBean.getUsers();
        for(int i=0;i< orderDetailBean.getRoom().getRooms();i++){
            if(i<customerList.size() && users != null && users.size() > 0 && i<users.size()){
                OrderUsersBean user = users.get(i);
                customerList.get(i).getmTextContent2().setText(user.getRealname());
            }

        }
    }

    private void initListener() {
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(orderDetailBean == null){
                    return;
                }
                addOrder();
            }
        });

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mLltDateContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(orderDetailBean == null){
                    return;
                }
                Intent intent = new Intent(OrderDealActivity.this, CalendarActivity.class);
                if (calendarList != null) {
                    intent.putExtra("calendarList", calendarList);
                }
                startActivityForResult(intent, CalendarActivity.CALENDAR_REQUEST_CODE);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });

        mIusvRoomNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(orderDetailBean == null){
                    return;
                }
                Intent intent = new Intent(OrderDealActivity.this, OrderGoodsActivity.class);
                intent.putExtra("roomNum", orderDetailBean.getRoom().getRooms());
                intent.putExtra("selelectId", orderDetailBean.getRoom().getRoom_typeid());
                startActivityForResult(intent, GOOD_REQUEST_CODE);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        findViewById(R.id.pay_type).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(orderDetailBean == null){
                    return;
                }
                Intent intent = new Intent(OrderDealActivity.this, OrderPayActivity.class);
                intent.putExtra("payment", orderDetailBean.getRoom().getPayment());
                startActivityForResult(intent, PAY_REQUEST_CODE);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    //添加订单
    private void addOrder() {
        String url = ProtocolUtil.getAddOrderUrl();
        Log.i(TAG,url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("empid",CacheUtil.getInstance().getUserId());
        bizMap.put("token",CacheUtil.getInstance().getToken());
        bizMap.put("shopid",orderDetailBean.getRoom().getShopid());
        bizMap.put("userid",orderDetailBean.getRoom().getUserid());

        bizMap.put("guest",orderDetailBean.getRoom().getGuest());
        bizMap.put("fullname",orderDetailBean.getRoom().getFullname());
        bizMap.put("roomid",orderDetailBean.getRoom().getRoom_typeid()+"");
        bizMap.put("room_type",orderDetailBean.getRoom().getRoom_type());
        bizMap.put("imgurl",orderDetailBean.getRoom().getImgurl());
        bizMap.put("rooms",orderDetailBean.getRoom().getRooms()+"");
        bizMap.put("arrival_date",orderDetailBean.getRoom().getArrival_date());
        bizMap.put("departure_date",orderDetailBean.getRoom().getDeparture_date());
        bizMap.put("room_rate",orderDetailBean.getRoom().getRoom_rate());
        bizMap.put("status","0");
        if(!TextUtils.isEmpty(orderDetailBean.getRoom().getPayment()))
        bizMap.put("payment",orderDetailBean.getRoom().getPayment());

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
                    AddOrderBean addOrderBean = new Gson().fromJson(result.rawResult, AddOrderBean.class);
                    if(addOrderBean.isSet()){
                        DialogUtil.getInstance().showToast(OrderDealActivity.this,"订单添加成功。订单号是："+addOrderBean.getReservation_no());
                        finish();
                    }else{
                        DialogUtil.getInstance().showToast(OrderDealActivity.this,"订单添加失败");
                        Log.e(TAG,addOrderBean.getErr());
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            if(CalendarActivity.CALENDAR_REQUEST_CODE == requestCode){
                if(null != data){
                    calendarList = (ArrayList<Calendar>)data.getSerializableExtra("calendarList");
                    setOrderDate(calendarList);
                }
            }else if(GOOD_REQUEST_CODE == requestCode){
                if(null != data){
                    int roomNum = data.getIntExtra("roomNum",0);
                    orderDetailBean.getRoom().setRooms(roomNum);
                    GoodBean goodBean = (GoodBean)data.getSerializableExtra("selectGood");
                    int selelectId = goodBean.getId();
                    orderDetailBean.getRoom().setRoom_typeid(selelectId);
                    orderDetailBean.getRoom().setRoom_type(goodBean.getRoom() + goodBean.getType());
                    notifyRoomNumberChange();
                }
            }
        }
    }

    //房间数量已经变 通知UI做调整
    private void notifyRoomNumberChange(){
        mIusvRoomNumber.setTextContent2(orderDetailBean.getRoom().getRooms() + "间");
        mIusvRoomNumber.setTextTitle(orderDetailBean.getRoom().getRoom_type());
        for(int i=0;i<customerList.size();i++){
            if(i < orderDetailBean.getRoom().getRooms()){
                customerList.get(i).setVisibility(View.VISIBLE);
            }else{
                customerList.get(i).setVisibility(View.GONE);
            }
        }
    }

    //初始化日期
    private void initCalendar() {
        calendarList = new ArrayList<Calendar>();

        mSimpleFormat  = new SimpleDateFormat("yyyy-MM-dd");
        mChineseFormat = new SimpleDateFormat("MM月dd日");

//        Calendar today = Calendar.getInstance();
//        today.setTime(new Date()); //当天
//        calendarList.add(today);
//
//        Calendar tomorrow = Calendar.getInstance();
//        tomorrow.setTime(new Date());
//        tomorrow.add(Calendar.DAY_OF_YEAR, 1); //下一天
//        calendarList.add(tomorrow);

        try{
            Calendar arrivalDate = Calendar.getInstance();
            arrivalDate.setTime(mSimpleFormat.parse(orderDetailBean.getRoom().getArrival_date()));
            calendarList.add(arrivalDate);

            Calendar departureDate = Calendar.getInstance();
            departureDate.setTime(mSimpleFormat.parse(orderDetailBean.getRoom().getDeparture_date()));
            calendarList.add(departureDate);
            setOrderDate(calendarList);
        }catch ( Exception e){
            Log.e(TAG,e.getMessage() );
        }

    }



    //设置离开和到达的日期
    private void setOrderDate(ArrayList<Calendar> calendarList){

        Date arrivalDate = calendarList.get(0).getTime();
        Date leaveDate   = calendarList.get(1).getTime();

        mChineseFormat = new SimpleDateFormat("MM月dd日");
        mTvArriveDate.setText(mChineseFormat.format(arrivalDate));
        mTvLeaveDate.setText(mChineseFormat.format(leaveDate));

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        orderDetailBean.getRoom().setArrival_date(sdf.format(arrivalDate));
        orderDetailBean.getRoom().setDeparture_date(sdf.format(leaveDate));

        try{
            dayNum = TimeUtil.daysBetween(arrivalDate, leaveDate);
            mTvDateTips.setText("共"+dayNum+"晚，在"+mTvLeaveDate.getText()+"13点前退房");
        }catch (Exception e){
            Log.e(TAG,e.getMessage() );
        }

    }

    //初始化房间选项标签
    private void initRoomTags() {
//        String tags[] = {"单早","双早"};
//        for(int i=0;i<2;i++){
//            mRoomTagView.addTag(createTag(i,tags[i],false));
//        }

        ArrayList<OrderRoomTagBean> roomTagBeans = orderDetailBean.getRoom_tag();
        for(OrderRoomTagBean roomTagBean : roomTagBeans){
            mRoomTagView.addTag(createTag(roomTagBean.getId(),roomTagBean.getContent(),false));
        }
    }

    //初始化其他服务标签
    private void initServiceTags() {
//        String tags[] = {"免前台","更多开发中"};
//        for(int i=0;i<2;i++){
//            mServiceTagView.addTag(createTag(i,tags[i],false));
//        }
        ArrayList<OrderPrivilegeBean> orderPrivilegeBeans = orderDetailBean.getPrivilege();
        for(OrderPrivilegeBean orderPrivilegeBean : orderPrivilegeBeans){
            mServiceTagView.addTag(createTag(orderPrivilegeBean.getId(),orderPrivilegeBean.getPrivilege_name(),false));
        }
    }

    private Tag createTag(int id,String tagstr,boolean isChecked){
        Tag tag = new Tag(id,tagstr);
        tag.tagTextColor = Color.parseColor("#000000");
        tag.layoutColor =  Color.parseColor("#ffffff");
        tag.layoutColorPress = Color.parseColor("#DDDDDD");
        //or tag.background = this.getResources().getDrawable(R.drawable.custom_bg);
        tag.radius = 40f;
        tag.tagTextSize = 18f;
        tag.layoutBorderSize = 1f;
        tag.layoutBorderColor = Color.parseColor("#000000");
        tag.deleteIndicatorColor =  Color.parseColor("#ff0000");
        tag.deleteIndicatorSize =  18f;
        tag.isDeletable = false;
//        if(isChecked){
//            tag.deleteIcon = "√";
//        }else{
//            tag.deleteIcon = "";
//        }

        return tag;
    }


}
