package com.zkjinshi.superservice.pad.utils;

import android.util.Log;

import com.zkjinshi.base.util.TimeUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 开发者：dujiande
 * 日期：2015/11/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderUtil {

    /**
     * 判读订单是否失效
     * @return
     */
    public static boolean isOrderTimeOut(String arrivedataStr){
        try{
            Calendar todayC = Calendar.getInstance();
            Date today = todayC.getTime();
            SimpleDateFormat mSimpleFormat  = new SimpleDateFormat("yyyy-MM-dd");
            Date arrivelDay = mSimpleFormat.parse(arrivedataStr);
            int offsetDay = TimeUtil.daysBetween(today, arrivelDay);
            if(offsetDay < 0 ){
                return  true;
            }
        }catch (Exception e){
            Log.e("OrderUtil",e.getMessage());
        }

        return false;
    }

    /**
     * 判读订单是否失效
     * @return
     */
    public static boolean isOrderTimeOut(Date arrivedate){
        Calendar todayC = Calendar.getInstance();
        Date today = todayC.getTime();
        try {
            int offsetDay = TimeUtil.daysBetween(today, arrivedate);
            if(offsetDay < 0 ){
                return  true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

}
