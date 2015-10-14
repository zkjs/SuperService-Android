package com.zkjinshi.superservice.sqlite;

/**
 * 开发者：vincent
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class TableOpenHelper {

    /**
     * 创建messagetbl表sql语句
     */
    public static String MESSAGE_TBL_SQL =
            "create table if not exists "
                    + DBOpenHelper.MESSAGE_TBL
                    + "("
                    + " message_id text primary key , "
                    + " session_id text , "//聊天室唯一标识
                    + " shop_id text , "//聊点指定商家ID
                    + " contact_id text , "//发送者ID
                    + " contact_name text , "//发送者名称
                    + " content text , "//消息内容
                    + " send_time long , "//发送时间
                    + " title text , "//聊天室名称
                    + " voice_time text , "//语音时间
                    + " mime_type integer , "//消息类别
                    + " send_status integer ,  "//发送状态
                    + " is_read integer , "//是否已读
                    + " attach_id text , "//附件id
                    + " temp_id text , "//临时消息id
                    + " rule_type text , "//消息规则类型
                    + " file_name text , "//文件名称
                    + " file_path text , "//文件路径
                    + " url text , "//URl
                    + " scale_url text "//缩略图URL
                    + ")";

    /**
     * 创建chatroomtbl表sql语句
     */
    public static String CHAT_ROOM_TBL_SQL =
            "create table if not exists "
                + DBOpenHelper.CHAT_ROOM_TBL + "("
                + " chat_id text primary key, "//聊天室id
                + " shop_id text , "//商家id
                + " chat_type int, " // 对应chatType 枚举类型
                + " create_time long, "//创建聊天室时间
                + " create_id text, "//创建者id
                + " image_url text, "//聊天室头像链接
                + " title text, "//聊天室标题
                + " last_action long, "//资料更新时间戳	更新聊天室群成员资料凭证
                + " enabled int, " // 聊天室是否可用 true 1 false 0
                + " notice_count int "//消息未读数
                + ")";

    /**
     * 创建membertbl表sql语句
     */
    public static String MEMBER_TBL_SQL =
            "create table if not exists "
                    + DBOpenHelper.MEMBER_TBL
                    + "("
                    + " userid text primary key , "//用户ID
                    + " session_id text , "
                    + " logintype integer, "//用户类型 0:app用户  1:商家员工 默认为:0
                    + " shopid text, "//商家ID
                    + " empid text, "//员工ID
                    + " roleid text, "//角色ID
                    + " created long "//创建时间
                    + " )";

    public static String USER_TBL_SQL =
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
                    + " tags          text " //
                    + " ) ";

    /** 创建区域信息表 */
    public static String ZONE_TBL_SQL =
            " create table if not exists "
                    + DBOpenHelper.ZONE_TBL
                    + " ( "
                    + " loc_id  integer primary key, "
                    + " shop_id   text, "
                    + " sensor_id   integer, "
                    + " uuid      text, "
                    + " major     integer, "
                    + " minior   integer, "
                    + " locdesc   text, "
                    + " status     integer, "
                    + " remark      text "
                    + " ) ";

    /** 创建到店信息表 */
    public static String COMING_TBL_SQL =
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
                    + " empid         text primary key, " //员工ID
                    + " empcode       text, "//工号
                    + " name          text, "//用户名
                    + " roleid        int,  "//所属角色
                    + " email         text, "//邮箱
                    + " phone         text, "//手机
                    + " phone2        text, "//其它号码
                    + " fax           text, "//传真
                    + " created       long, "//增加时间
                    + " locationid    int,"//区域ID
                    + " role_name     text,"//角色名称
                    + " online_status int,"// 服务器在线状态
                    + " work_status   int,"//是否上班中
                    + " last_online_time  long, "//上一次在线时间
                    + " dept   text,"//部门
                    + " desc   text," //备注描述
                    + " shop_id   text" //商家ID
                    + " ) ";

//    /**
//     * 未注册的本地联系人数据表
//     */
//    public final static String UNREG_CLIENT_TBL_SQL =
//            " create table if not exists "
//                    + DBOpenHelper.UNREG_CLIENT_TBL
//                    + " ( "
//                    + " phone         text primary key, "
//                    + " username      text, "
//                    + " position      text, "
//                    + " company       text, "
//                    + " other_desc    text, "
//                    + " is_bill       int "
//                    + " ) ";


    /**
     * 获取数据库所有表名
     * @return
     */
    public static String[] getTableNames(){
        return new String[]{
                DBOpenHelper.MESSAGE_TBL,//消息表
                DBOpenHelper.CHAT_ROOM_TBL,//聊天室表
                DBOpenHelper.MEMBER_TBL,//成员表
                DBOpenHelper.USER_TBL,//app用户表
                DBOpenHelper.CLIENT_TBL,//客户表
                DBOpenHelper.ZONE_TBL,//个人区域信息表
                DBOpenHelper.COMING_TBL//到店信息表
        };
    }

}
