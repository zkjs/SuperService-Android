package com.zkjinshi.superservice.activity.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.set.ClientActivity;
import com.zkjinshi.superservice.base.BaseActivity;
import com.zkjinshi.superservice.bean.AddOrderBean;
import com.zkjinshi.superservice.bean.PayBean;
import com.zkjinshi.superservice.emchat.EMConversationHelper;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.view.AmountInDecreaseView;
import com.zkjinshi.superservice.vo.GoodInfoVo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;

/**
 * 订单新增界面
 * 开发者：WinkyQin
 * 日期：2015/11/26
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderNewActivity extends BaseActivity {

    private final static String TAG = OrderNewActivity.class.getSimpleName();

    private String mUserID;
    private String mToken;
    private String mShopID;
    private String mShopName;

    private String mClientID;
    private String mClientName;
    private String mClientPhone;

    private int    mRoomCount;//房间数量

    private DisplayImageOptions mOptions;

    private TextView mTvRoomType;
    private TextView mTvRoomRate;
    private TextView mTvArriveDate;
    private TextView mTvLeaveDate;
    private TextView mTvDateTips;

    private ImageButton mBtnBack;
    private ImageView mIvRoomImg;
    private LinearLayout mLlOrderPerson;
//    private RelativeLayout mRlOrderDate;
    private LinearLayout mLlOrderDate;
    private LinearLayout mLltYuan;
//    private LinearLayout mLlLivePerson;
//    private LinearLayout nLlPersons;
    private TextView mTvOrderPerson;
    private TextView mTvOrderStatus;
    private LinearLayout mLlPayMethod;
    private TextView mTvPayMethod;

    private RelativeLayout mRlRoomType;
    private Button mBtnSendOrder;
    private Button mBtnCancelOrder;

    private AmountInDecreaseView mAivRoomCount;
    private TextView mTvPayTips; //支付提示语句

    private SimpleDateFormat mSimpleFormat;
    private SimpleDateFormat mChineseFormat;
    private ArrayList<Calendar> calendarList = null;

    private int     dayNum;
    private Double  mRoomAmount;//房间价格
    private PayBean mPayBean;//支付方式对象

    private GoodInfoVo mGoodInfoVo;//选中房型对象

    private ArrayList<String> mLiveNames;
    public static final int REQUEST_ORDER_PERSON = 10;
    public static final int REQUEST_ROOM_TYPE = 11;
    public static final int REQUEST_LIVE_PERSON = 12;
    public static final int REQUEST_PAY_METHOD = 13;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_new);

        initView();
        initData();
        initListener();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void initView() {
        mBtnBack = (ImageButton) findViewById(R.id.back_btn);
        mIvRoomImg = (ImageView) findViewById(R.id.iv_room_img);
        mLlOrderPerson = (LinearLayout) findViewById(R.id.ll_order_person);
//        mRlOrderDate = (RelativeLayout) findViewById(R.id.rl_order_date);
        mLlOrderDate = (LinearLayout) findViewById(R.id.ll_order_date);
//        mLlLivePerson = (LinearLayout) findViewById(R.id.ll_live_person);
//        nLlPersons = (LinearLayout) findViewById(R.id.ll_persons);
        mTvOrderPerson = (TextView) findViewById(R.id.tv_order_person);

        mLlPayMethod = (LinearLayout) findViewById(R.id.ll_pay_method);
        mTvPayMethod = (TextView) findViewById(R.id.tv_pay_method);

        mBtnSendOrder = (Button) findViewById(R.id.btn_send_booking_order);
        mBtnCancelOrder = (Button) findViewById(R.id.btn_cancel_order);
        mRlRoomType = (RelativeLayout) findViewById(R.id.rl_room_type);
        mTvRoomType = (TextView) findViewById(R.id.tv_room_type);
        mTvRoomRate = (TextView) findViewById(R.id.tv_payment);
        mLltYuan = (LinearLayout) findViewById(R.id.rl_yuan);
        mTvOrderStatus = (TextView) findViewById(R.id.tv_order_status);

        mTvArriveDate = (TextView) findViewById(R.id.tv_arrive_date);
        mTvLeaveDate = (TextView) findViewById(R.id.tv_leave_date);
        mTvDateTips = (TextView) findViewById(R.id.tv_date_tips);

        mAivRoomCount = (AmountInDecreaseView) findViewById(R.id.aiv_room_count);
        mTvPayTips    = (TextView) findViewById(R.id.tv_pay_tips);
    }

    private void initData() {

        mUserID = CacheUtil.getInstance().getUserId();
        mToken = CacheUtil.getInstance().getToken();
        mShopID = CacheUtil.getInstance().getShopID();
        mShopName = CacheUtil.getInstance().getShopFullName();

        this.mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_room_pic_default)
                .showImageForEmptyUri(R.mipmap.ic_room_pic_default)
                .showImageOnFail(R.mipmap.ic_room_pic_default)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        //初始化入住时间
        initArrivalDate();
    }

    private void initListener() {

        //退出界面
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderNewActivity.this.finish();
            }
        });

        //房型选择
        mRlRoomType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chooseRoomType = new Intent(OrderNewActivity.this, GoodListActivity.class);
                startActivityForResult(chooseRoomType, REQUEST_ROOM_TYPE);
            }
        });

        //点击进入预定人
        mLlOrderPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseClient = new Intent(OrderNewActivity.this, ClientActivity.class);
                chooseClient.putExtra("choose_order_person", true);
                startActivityForResult(chooseClient, REQUEST_ORDER_PERSON);
            }
        });

        //点击进入入住人
//        mLlLivePerson.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(OrderNewActivity.this, AddLivePersonActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putStringArrayList("live_persons", mLiveNames);
//                intent.putExtras(bundle);
//                startActivityForResult(intent, REQUEST_LIVE_PERSON);
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//            }
//        });

//        //选择入住时间
//        mRlOrderDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(OrderNewActivity.this, CalendarActivity.class);
//                if (calendarList != null) {
//                    intent.putExtra("calendarList", calendarList);
//                }
//                startActivityForResult(intent, CalendarActivity.CALENDAR_REQUEST_CODE);
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//            }
//        });

        //选择入住时间
        mLlOrderDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderNewActivity.this, CalendarActivity.class);
                if (calendarList != null) {
                    intent.putExtra("calendarList", calendarList);
                }
                startActivityForResult(intent, CalendarActivity.CALENDAR_REQUEST_CODE);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        mLlPayMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getPayMethod = new Intent(OrderNewActivity.this, PayMethodActivity.class);
                startActivityForResult(getPayMethod, REQUEST_PAY_METHOD);
            }
        });

        //创建订单
        mBtnSendOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addOrderByServer();
            }
        });

        //取消订单
        mBtnCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderNewActivity.this.finish();
            }
        });
    }

    //初始化入住时间
    private void initArrivalDate() {
        calendarList = new ArrayList<>();
        mSimpleFormat = new SimpleDateFormat("yyyy-MM-dd");
        mChineseFormat = new SimpleDateFormat("MM月dd日");

        Calendar today = Calendar.getInstance();
        today.setTime(new Date()); //当天
        calendarList.add(0, today);

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.setTime(new Date());
        tomorrow.add(Calendar.DAY_OF_YEAR, 1); //下一天
        calendarList.add(1, tomorrow);
        setOrderDate(calendarList);
    }

    //设置离开和到达的日期
    private void setOrderDate(ArrayList<Calendar> calendarList) {
        mChineseFormat = new SimpleDateFormat("MM月dd日");
        Date arrivalDate = calendarList.get(0).getTime();
        Date leaveDate = calendarList.get(1).getTime();

        mTvArriveDate.setText(mChineseFormat.format(arrivalDate));
        mTvLeaveDate.setText(mChineseFormat.format(leaveDate));
        try {
            dayNum = TimeUtil.daysBetween(arrivalDate, leaveDate);
            mTvDateTips.setText("共" + dayNum + "晚，在" + mTvLeaveDate.getText() + "13点前退房");
        } catch (Exception e) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            if (CalendarActivity.CALENDAR_REQUEST_CODE == requestCode) {
                if (null != data) {
                    calendarList = (ArrayList<Calendar>) data.getSerializableExtra("calendarList");
                    if (null != calendarList && !calendarList.isEmpty()) {
                        setOrderDate(calendarList);
                    }
                }
            }
            //请求选择预订人
            else if (REQUEST_ORDER_PERSON == requestCode) {
                if (null != data) {
                    mClientID    = data.getStringExtra("client_id");
                    mClientName  = data.getStringExtra("client_name");
                    mClientPhone = data.getStringExtra("client_phone");
                    if (!TextUtils.isEmpty(mClientName)) {
                        mTvOrderPerson.setText(mClientName);
                    }
                }
            }
            //请求选择房型
            else if (REQUEST_ROOM_TYPE == requestCode) {
                if (null != data) {
                    mGoodInfoVo = (GoodInfoVo) data.getSerializableExtra("room_type");
                    if (null != mGoodInfoVo) {

                        String room = mGoodInfoVo.getName();
                        String price = mGoodInfoVo.getPrice()+"";
                        String imgUrl = mGoodInfoVo.getImgurl();

                        if (!TextUtils.isEmpty(room)) {
                            mTvRoomType.setText(room);
                        }

                        if (!TextUtils.isEmpty(price)) {
                            mTvRoomRate.setText(price);
                        }

                        if (!TextUtils.isEmpty(imgUrl)) {
                            ImageLoader.getInstance().displayImage(ProtocolUtil.getHostImgUrl(imgUrl), mIvRoomImg, mOptions);
                        }
                    }
                }
            }
            //请求选择入住人
//            else if (REQUEST_LIVE_PERSON == requestCode) {
//                if (null != data) {
//                    Bundle bundle = data.getExtras();
//                    mLiveNames = (ArrayList<String>) bundle.get("live_persons");
//                    for (String liveName : mLiveNames) {
//                        nLlPersons.addView(getItemLivePersonView(getString(R.string.live_person) +
//                                mLiveNames.indexOf(liveName) + 1, liveName));
//                    }
//                }
//            }
            //请求选择支付方式和金额
            else if (REQUEST_PAY_METHOD == requestCode) {
                if (null != data) {
                    mRoomAmount = data.getDoubleExtra("room_amount", 0.00);
                    mPayBean = (PayBean) data.getSerializableExtra("pay_bean");
                    if(null != mPayBean){
                        String payName = mPayBean.getPay_name();
                        mTvPayMethod.setText(payName);
                    }
                    mTvPayTips.setText("待支付金额：" + mRoomAmount + getString(R.string.yuan));
                }
            }
        }
    }

    /**
     * 获得居住人View
     *
     * @param title
     * @param content
     * @return
     */
    public View getItemLivePersonView(String title, String content) {

        View view = View.inflate(OrderNewActivity.this, R.layout.item_live_person, null);
        TextView titleView = (TextView) view.findViewById(R.id.tv_text_title);
        TextView contentView = (TextView) view.findViewById(R.id.tv_text_content);
        if (!TextUtils.isEmpty(title)) {
            titleView.setText(title);
        }

        if (!TextUtils.isEmpty(title)) {
            contentView.setText(content);
        }
        return view;
    }

    //添加订单
    private void addOrderByServer() {

        //判断预订人是为空
        if(TextUtils.isEmpty(mClientName)){
            DialogUtil.getInstance().showCustomToast(OrderNewActivity.this, getString(R.string.please_add_the_order_person), Gravity.CENTER);
            return ;
        }

        //判断房型是否为空
        if(null == mGoodInfoVo || TextUtils.isEmpty(mGoodInfoVo.getName())){
            DialogUtil.getInstance().showCustomToast(OrderNewActivity.this, getString(R.string.choose_room_type), Gravity.CENTER);
            return ;
        }

        //判断支付方式是否选中
        if(null == mPayBean || TextUtils.isEmpty(mPayBean.getPay_name())){
            DialogUtil.getInstance().showCustomToast(OrderNewActivity.this, getString(R.string.choose_pay_method), Gravity.CENTER);
            return ;
        }

        String url = ProtocolUtil.getAddOrderUrl();
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String, String> bizMap = new HashMap<>();
        bizMap.put("salesid", mUserID);//销售员ID
        bizMap.put("token", mToken);
        bizMap.put("shopid", mShopID);
        bizMap.put("fullname", mShopName);//酒店全称

        bizMap.put("userid", mClientID);//预订人ID
        bizMap.put("guesttel", mClientPhone);//预订人电话
        bizMap.put("guest", mClientName);//预订人姓名

        bizMap.put("roomid", mGoodInfoVo.getId());//房型ID
        bizMap.put("room_type", mGoodInfoVo.getName());//房型
        bizMap.put("imgurl", mGoodInfoVo.getImgurl());//图片路径

        mRoomCount = mAivRoomCount.getNumber();
        bizMap.put("rooms", mRoomCount+"");//房间数量
        bizMap.put("arrival_date",  mSimpleFormat.format(calendarList.get(0).getTime()));//到达日期
        bizMap.put("departure_date", mSimpleFormat.format(calendarList.get(1).getTime()));//离开日期
        bizMap.put("room_rate", mRoomAmount + "");//房价
        bizMap.put("payment", mPayBean.getPay_id() + "");//支付方式
        bizMap.put("status", "0");

        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(this, netRequest, NetResponse.class);
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
                    AddOrderBean addOrderBean = new Gson().fromJson(result.rawResult, AddOrderBean.class);
                    if (addOrderBean.isSet()) {
                        DialogUtil.getInstance().showToast(OrderNewActivity.this,
                                                "订单添加成功。\n订单号是：" +
                                                addOrderBean.getReservation_no());

                        EMConversationHelper.getInstance().sendOrderCmdMessage(
                            mShopID,
                            addOrderBean.getReservation_no(),
                            mClientID,
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
                        finish();
                    } else {
                        DialogUtil.getInstance().showToast(OrderNewActivity.this, "订单添加失败");
                        Log.e(TAG, addOrderBean.getErr());
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
}
