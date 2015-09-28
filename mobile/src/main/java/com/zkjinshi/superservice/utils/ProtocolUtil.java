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

}
