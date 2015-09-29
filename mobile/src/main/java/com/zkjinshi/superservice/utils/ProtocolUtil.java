package com.zkjinshi.superservice.utils;

import com.zkjinshi.base.config.ConfigUtil;

/**
 * 协议接口工具类
 * 开发者：dujiande
 * 日期：2015/9/28
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ProtocolUtil {


    /**
     * 服务员登陆
     * @return
     */
    public static String getSempLoginUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"semp/login";
    }

    /**
     * 服务员修改资料
     * @return
     */
    public static String getSempupdateUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"semp/sempupdate";
    }

    /**
     * 服务员获取商家整个区域列表
     * @return
     */
    public static String getZonelistUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"semp/shoplocation";
    }

    /**
     * 服务员修改自己管辖的区域通知
     * @return
     */
    public static String getSemplocationupdateUrl(){
        return ConfigUtil.getInst().getHttpDomain()+"semp/semplocationupdate";
    }

}
