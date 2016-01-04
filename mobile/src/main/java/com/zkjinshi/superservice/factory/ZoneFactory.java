package com.zkjinshi.superservice.factory;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.superservice.bean.ZoneBean;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.vo.ContactLocalVo;
import com.zkjinshi.superservice.vo.ZoneVo;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * 开发者：JimmyZhang
 * 日期：2015/10/8
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ZoneFactory {

    private static ZoneFactory instance;

    private ZoneFactory(){}

    public synchronized static ZoneFactory getInstance(){
        if(null == instance){
            instance = new ZoneFactory();
        }
        return instance;
    }

    public ContentValues buildContentValues(ZoneBean zoneBean){
        ContentValues contentValues = new ContentValues();
        int locId = zoneBean.getLocid();
        String shopId = zoneBean.getShopid()+"";
        int sensorId = zoneBean.getSensorid();
        String uuid = zoneBean.getUuid();
        int major = zoneBean.getMajor();
        int minior = zoneBean.getMinior();
        String locDesc = zoneBean.getLocdesc();
        int status = zoneBean.getStatus();
        String remark = zoneBean.getRemark();
        contentValues.put("loc_id",locId);
        contentValues.put("shop_id",shopId);
        contentValues.put("sensor_id",sensorId);
        if(!TextUtils.isEmpty(uuid)){
            contentValues.put("uuid",uuid);
        }
        contentValues.put("major",major);
        if(!TextUtils.isEmpty(locDesc)){
            contentValues.put("locdesc",locDesc);
        }
        contentValues.put("status",status);
        if(!TextUtils.isEmpty(remark)){
            contentValues.put("remark",remark);
        }
        return contentValues;
    }

    public ZoneVo buildZone(Cursor cursor){
        ZoneVo zoneVo = new ZoneVo();
        zoneVo.setLocId(cursor.getInt(0));
        zoneVo.setShopId(cursor.getString(1));
        zoneVo.setSensorId(cursor.getInt(2));
        zoneVo.setUuid(cursor.getString(3));
        zoneVo.setMajor(cursor.getInt(4));
        zoneVo.setMinior(cursor.getInt(5));
        zoneVo.setLocDesc(cursor.getString(6));
        zoneVo.setStatus(cursor.getInt(7));
        zoneVo.setRemark(cursor.getString(8));
        return zoneVo;
    }

    /**
     * 构建订阅数组
     * @return
     */
    public String[] buildZoneArray(){
        String[] zoneArray = null;
        ArrayList<ZoneBean> zoneList = getZoneBeanList();
        if(null != zoneList && !zoneList.isEmpty()){
            int length = zoneList.size();
            zoneArray = new String[length];
            for(int i = 0; i < length ; i++){
                zoneArray[i] = ""+zoneList.get(i).getLocid();
            }
        }
        return zoneArray;
    }

    /**
     * 构建选择区域
     * @return
     */
    public ArrayList<ZoneBean> getZoneBeanList(){
        ArrayList<ZoneBean> zoneList = new ArrayList<ZoneBean>();
        String listStr =  CacheUtil.getInstance().getListStrCache("zoneBeanList");
        if(!TextUtils.isEmpty(listStr)) {
            Type listType = new TypeToken<ArrayList<ZoneBean>>() {
            }.getType();
            Gson gson = new Gson();
            zoneList = gson.fromJson(listStr, listType);
        }
        return zoneList;
    }

}
