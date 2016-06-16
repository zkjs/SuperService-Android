package com.zkjinshi.superservice.manager;

import android.text.TextUtils;

import com.blueware.com.google.gson.internal.T;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.pyxis.bluetooth.IBeaconVo;
import com.zkjinshi.superservice.bean.ZoneBean;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.vo.BeaconVo;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * 收款台员工区域判断类
 * 开发者：jimmyzhang
 * 日期：16/6/16
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class CheckoutInnerManager {

    private static CheckoutInnerManager ourInstance;

    private ArrayList<BeaconVo> beaconList;

    public static CheckoutInnerManager getInstance() {
        if(null  == ourInstance){
           ourInstance  = new CheckoutInnerManager();
        }
        return ourInstance;
    }

    private CheckoutInnerManager() {
    }

    /**
     * 获取收款台beacon信息
     * @return
     */
    public ArrayList<BeaconVo> getBeaconList(){
        String beaconsStr = CacheUtil.getInstance().getListStrCache("beacons");
        if(!TextUtils.isEmpty(beaconsStr)) {
            Type listType = new TypeToken<ArrayList<BeaconVo>>() {
            }.getType();
            Gson gson = new Gson();
            beaconList = gson.fromJson(beaconsStr, listType);
        }
        return beaconList;
    }

    /**
     * 是否在收款台区域
     * @return
     */
    public boolean isInnerCheckout(){
        beaconList = getBeaconList();
        if(null != beaconList && !beaconList.isEmpty()){
            for (BeaconVo beaconVo : beaconList){
                if(beaconVo.isInner()){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 保存收款台beacon信息
     * @param beaconList
     */
    public void saveBeaconCache(ArrayList<BeaconVo> beaconList){
        CacheUtil.getInstance().saveListCache("beacons",beaconList);
    }

    /**
     * 保存收款台beacon信息
     * @param isInner 进入区域:true 离开区域:false
     * @param beacon
     */
    public void saveBeaconCache(boolean isInner, IBeaconVo beacon){
        beaconList = getBeaconList();
        String minor = null;
        boolean saveFlag = false;
        for(BeaconVo beaconVo :  beaconList){
            minor = beaconVo.getMinor();
            //minor为空时, 表示该区域内可能有多个beacon, 扫描时只需要根据uuid和major做模糊匹配即可
            if(TextUtils.isEmpty(minor)){
                if(getMateKey(beacon).equals(getMateKey(beaconVo))){
                    beaconVo.setInner(isInner);
                    saveFlag = true;
                }
            }else {
                if(getFullKey(beacon).equals(getFullKey(beaconVo))){
                    beaconVo.setInner(isInner);
                    saveFlag = true;
                }
            }
        }
        if(saveFlag){
            saveBeaconCache(beaconList);
        }
    }

    private String getFullKey(IBeaconVo beacon){
        return beacon.getProximityUuid()+beacon.getMajor()+beacon.getMinor();
    }

    private String getFullKey(BeaconVo beacon){
        return beacon.getUuid()+beacon.getMajor()+beacon.getMinor();
    }

    private String getMateKey(IBeaconVo beacon){
        return beacon.getProximityUuid()+beacon.getMajor();
    }

    private String getMateKey(BeaconVo beacon){
        return beacon.getUuid()+beacon.getMajor();
    }
}
