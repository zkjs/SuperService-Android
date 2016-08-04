package com.zkjinshi.superservice.pad.sqlite;

/**
 * 开发者：vincent
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class TableOpenHelper {

    public final static String CLIENT_TBL_SQL =
            " create table if not exists "
                    + DBOpenHelper.CLIENT_TBL
                    + " ( "
                    + " userid        text primary key, "
                    + " id            int, "
                    + " shopid        text, "
                    + " salesid       text, "
                    + " user_level    int, "
                    + " level_desc    text, "
                    + " card_no       text, "
                    + " is_special    text, "
                    + " nationality   text, "
                    + " like_desc     text, "
                    + " taboo_desc    text, "
                    + " other_desc    text, "
                    + " created       text, "
                    + " modified      text, "
                    + " username      text, "
                    + " phone         text, "
                    + " company       text, "
                    + " position      text, "
                    + " is_bill       int, "
                    + " contact_type  int, "
                    + " sex           int, "
                    + " order_count   int, "
                    + " tags          text, "
                    + " online_status int, "
                    + " bg_drawable_res  int " //背景颜色资源值
                    + " ) ";

    /** 商家员工信息表 */
    public final static String SHOP_EMPLOYEE_TBL_SQL =
            " create table if not exists "
                    + DBOpenHelper.SHOP_EMPLOYEE_TBL
                    + " ( "
                    + " userid         text primary key, " //员工ID
                    + " email           text, "//
                    + " phone          text, "
                    + " realname        text,  "
                    + " rolecode         text, "
                    + " roledesc         text, "
                    + " roleid        text, "
                    + " rolename      text, "
                    + " sex       int, "
                    + " userimage    text,"
                    + " username     text"
                    + " ) ";

    /** 商家部门表 */
    public final static String SHOP_DEPARTMENT_TBL_SQL =
            " create table if not exists "
                    + DBOpenHelper.SHOP_DEPARTMENT_TBL
                    + " ( "
                    + " deptid        int primary key, " //部门ID
                    + " shopid        text, "//商家ID
                    + " dept_code     text, "//部门编号
                    + " dept_name     int,  "//部门名称
                    + " description   text "//描述
                    + " ) ";

    /**
     * 获取数据库所有表名
     * @return
     */
    public static String[] getTableNames(){
        return new String[]{
                DBOpenHelper.CLIENT_TBL,//客户表
                DBOpenHelper.SHOP_EMPLOYEE_TBL,//商家员工表
                DBOpenHelper.SHOP_DEPARTMENT_TBL//商家部门表
        };
    }

}
