package com.zkjinshi.superservice.factory;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.zkjinshi.superservice.bean.ZoneBean;
import com.zkjinshi.superservice.vo.ComingVo;
import com.zkjinshi.superservice.vo.ZoneVo;

/**
 * 到店通知工厂类
 * 开发者：JimmyZhang
 * 日期：2015/10/8
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ComingFactory {

    private static ComingFactory instance;

    private ComingFactory(){}

    public synchronized static ComingFactory getInstance(){
        if(null == instance){
            instance = new ComingFactory();
        }
        return instance;
    }

    public ContentValues buildContentValues(ComingVo comingVo){
        ContentValues contentValues = new ContentValues();
        String userId = comingVo.getUserId();
        long arriveTime = comingVo.getArriveTime();
        String checkInDate = comingVo.getCheckInDate();
        String checkOutDate = comingVo.getCheckOutDate();
        String locId = comingVo.getLocId();
        String location = comingVo.getLocation();
        int orderStatus = comingVo.getOrderStatus();
        String phoneNum = comingVo.getPhoneNum();
        String roomType = comingVo.getRoomType();
        int stayDays = comingVo.getStayDays();
        String vip = comingVo.getVip();
        String userName = comingVo.getUserName();
        if(!TextUtils.isEmpty(userId)){
            contentValues.put("user_id",userId);
        }
        contentValues.put("arrive_time",arriveTime);
        if(!TextUtils.isEmpty(checkInDate)){
            contentValues.put("check_in_date",checkInDate);
        }
        if(!TextUtils.isEmpty(checkOutDate)){
            contentValues.put("check_out_date",checkOutDate);
        }
        if(!TextUtils.isEmpty(locId)){
            contentValues.put("loc_id",locId);
        }
        if(!TextUtils.isEmpty(location)){
            contentValues.put("location",location);
        }
        contentValues.put("order_status",orderStatus);
        if(!TextUtils.isEmpty(phoneNum)){
            contentValues.put("phone_num",phoneNum);
        }
        if(!TextUtils.isEmpty(roomType)){
            contentValues.put("room_type",roomType);
        }
        contentValues.put("stay_days",stayDays);
        if(!TextUtils.isEmpty(vip)){
            contentValues.put("vip",vip);
        }
        if(!TextUtils.isEmpty(userName)){
            contentValues.put("user_name",userName);
        }
        return contentValues;
    }

    public ComingVo buildComing(Cursor cursor){
        ComingVo comingVo = new ComingVo();
        comingVo.setUserId(cursor.getString(1));
        comingVo.setLocId(cursor.getString(2));
        comingVo.setVip(cursor.getString(3));
        comingVo.setUserName(cursor.getString(4));
        comingVo.setLocation(cursor.getString(5));
        comingVo.setPhoneNum(cursor.getString(6));
        comingVo.setRoomType(cursor.getString(7));
        comingVo.setCheckInDate(cursor.getString(8));
        comingVo.setCheckOutDate(cursor.getString(9));
        comingVo.setStayDays(cursor.getInt(10));
        comingVo.setArriveTime(cursor.getLong(11));
        comingVo.setOrderStatus(cursor.getInt(12));
        return comingVo;
    }
}
