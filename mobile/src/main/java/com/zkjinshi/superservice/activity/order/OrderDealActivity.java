package com.zkjinshi.superservice.activity.order;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.view.ItemUserSettingView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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

    private TagView mRoomTagView;
    private TagView mServiceTagView;

    private ItemUserSettingView mIusvPlayType;
    private ItemUserSettingView mIusvRoomNumber;
    private TextView mTvTicket;
    private ArrayList<ItemUserSettingView> customerList;

    private TextView        mTvArriveDate;
    private TextView        mTvLeaveDate;
    private TextView        mTvDateTips;
    private LinearLayout    mLltDateContainer;

    private SimpleDateFormat mSimpleFormat;
    private SimpleDateFormat mChineseFormat;

    private ArrayList<Calendar> calendarList = null;

    private int dayNum;
    private int roomNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_deal);

        initView();
        initData();
        initListener();
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

    }

    private void addRightIcon(ItemUserSettingView item){
        Drawable drawable= getResources().getDrawable(R.mipmap.ic_get_into_w);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        item.getmTextContent2().setCompoundDrawables(null, null, drawable, null);
    }

    private void initData() {
        initCalendar();
        initRoomTags();
        initServiceTags();
    }

    private void initListener() {
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mLltDateContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderDealActivity.this, CalendarActivity.class);
                if (calendarList != null) {
                    intent.putExtra("calendarList", calendarList);
                }
                startActivityForResult(intent, CalendarActivity.CALENDAR_REQUEST_CODE);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            if(CalendarActivity.CALENDAR_REQUEST_CODE == requestCode){
                if(null != data){
                    calendarList = (ArrayList<Calendar>)data.getSerializableExtra("calendarList");
                    setOrderDate(calendarList);
                }
            }
        }
    }

    //初始化日期
    private void initCalendar() {
        calendarList = new ArrayList<Calendar>();

        mSimpleFormat  = new SimpleDateFormat("yyyy-MM-dd");
        mChineseFormat = new SimpleDateFormat("MM月dd日");

        Calendar today = Calendar.getInstance();
        today.setTime(new Date()); //当天
        calendarList.add(today);

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.setTime(new Date());
        tomorrow.add(Calendar.DAY_OF_YEAR, 1); //下一天
        calendarList.add(tomorrow);
        setOrderDate(calendarList);
    }



    //设置离开和到达的日期
    private void setOrderDate(ArrayList<Calendar> calendarList){
        mChineseFormat = new SimpleDateFormat("MM月dd日");
        Date arrivalDate = calendarList.get(0).getTime();
        Date leaveDate   = calendarList.get(1).getTime();

        mTvArriveDate.setText(mChineseFormat.format(arrivalDate));
        mTvLeaveDate.setText(mChineseFormat.format(leaveDate));

        try{
            dayNum = TimeUtil.daysBetween(arrivalDate, leaveDate);
            mTvDateTips.setText("共"+dayNum+"晚，在"+mTvLeaveDate.getText()+"13点前退房");
        }catch (Exception e){

        }

    }

    //初始化房间选项标签
    private void initRoomTags() {
        String tags[] = {"单早","双早"};
        for(int i=0;i<2;i++){
            mRoomTagView.addTag(createTag(i,tags[i],false));
        }
    }

    //初始化其他服务标签
    private void initServiceTags() {

        String tags[] = {"免前台","更多开发中"};
        for(int i=0;i<2;i++){
            mServiceTagView.addTag(createTag(i,tags[i],false));
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
