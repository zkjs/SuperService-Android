package com.zkjinshi.superservice.utils;

import java.util.Set;

/**
 * 权限控制工具类
 * 开发者：JimmyZhang
 * 日期：2016/6/15
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */

public class AccessControlUtil {

    public static String MEMBERDETAIL = "MEMBERDETAIL";//会员详情页->拨打电话（点击电话按钮）
    public static String ADDMEMBER = "ADDMEMBER";//会员->新增会员
    public static String CASHREGISTER = "CASHREGISTER";//侧滑栏->收款台
    public static String CONTACTIMPORT = "CONTACTIMPORT";//团队管理->通讯录导入
    public static String DELMEMBER = "DELMEMBER";//会员->侧滑删除
    public static String BTNADDMEMBER = "BTNADDMEMBER";//团队管理->新建成员按钮
    public static String BATCHIMPORT = "BATCHIMPORT";//团队管理->批量导入Excel
    public static String BTNPOS = "BTNPOS";//收款台->收款记录按钮
    public static String MEMBER = "MEMBER";//侧滑栏->会员
    public static String DELEMPLOYEE = "DELEMPLOYEE";//团队管理->删除团队成员
    public static String SERVICETAG = "SERVICETAG";//服务标签

    /**
     * 视图是否可见
     * @param viewFeature
     * @return
     */
    public static boolean isShowView(String viewFeature){
        Set<String> featureSet = CacheUtil.getInstance().getFeatures();
        if(null != featureSet && !featureSet.isEmpty()){
            if(featureSet.contains(viewFeature)){
                return true;
            }
        }
        return false;
    }
}
