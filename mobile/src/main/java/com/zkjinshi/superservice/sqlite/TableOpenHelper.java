package com.zkjinshi.superservice.sqlite;

/**
 * 开发者：vincent
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class TableOpenHelper {

    public static final String USER_TBL_SQL =
            "create table if not exists "
                    + DBOpenHelper.USER_TBL
                    + "("
                    + " user_id text primary key, "//用户ID
                    + " user_name text, "//用户姓名
                    + " shop_id text, "//商家id
                    + " cellphone text, "//手机
                    + " photo_url text, "//用户头像
                    + " shop_name text , "//商家全称
                    + " token text, "//token
                    + " role_id text, "//角色id
                    + " sex  integer " //性别
                    + " )";

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

    /** 创建到店信息表 */
    public static final String COMING_TBL_SQL =
            " create table if not exists "
                    + DBOpenHelper.COMING_TBL
                    + " ( "
                    + "  _id integer primary key autoincrement , "
                    + " user_id text , "
                    + " loc_id text, "
                    + " vip text, "
                    + " user_name text, "
                    + " location text, "
                    + " phone_num text, "
                    + " room_type text, "
                    + " check_in_date text, "
                    + " check_out_date text, "
                    + " stay_days integer, "
                    + " arrive_time long, "
                    + " order_status integer "
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
                DBOpenHelper.USER_TBL,//app用户表
                DBOpenHelper.CLIENT_TBL,//客户表
                DBOpenHelper.COMING_TBL,//到店信息表
                DBOpenHelper.SHOP_EMPLOYEE_TBL,//商家员工表
                DBOpenHelper.SHOP_DEPARTMENT_TBL//商家部门表
        };
    }

}
