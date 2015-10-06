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
                    + DBOpenHelper.CHAT_ROOM_TBL
                    + "("
                    + " shop_id text primary key, "
                    + " shop_name text, "//聊天室名称
                    + " session_id text, "
                    + " remark text, "//备注
                    + " created long, "//创建时间
                    + " end_time long, "//会话结束时间
                    + " end_user_id text, "//会话结束人
                    + " client_id text, "//会话创建人
                    + " client_name text , "//会话创建姓名
                    + " is_visible integer"//是否显示此聊天室 //0:隐藏 //1:显示
                    + " )";

    /**
     * 创建membertbl表sql语句
     */
    public static String MEMBER_TBL_SQL =
            "create table if not exists "
                    + DBOpenHelper.MEMBER_TBL
                    + "("
                    + " session_id text , "
                    + " userid text, "//用户ID
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
                    + " client_id       text primary key, " //主键
                    + " client_name     text, " //客户姓名
                    + " client_phone    text, " //客户手机
                    + " avatar_name     text, " //图片名称
                    + " avatar_url      text, " //图片url
                    + " client_company  text, " //客户公司
                    + " client_position text, " //客户职位
                    + " on_account      integer "   //是否挂账会员 0:挂账会员 1 非挂账会员
                    + " ) ";

    /** 创建到店用户信息表 */
    public static String CLIENT_LATEST_TBL_SQL =
            " create table if not exists "
                    + DBOpenHelper.CLIENT_LATEST_TBL
                    + " ( "
                    + " user_id     text, "
                    + " user_name   text, "
                    + " timestamp   long, "
                    + " shop_id     text, "
                    + " loc_id      text "
                    + " ) ";

    /**
     * 获取数据库所有表名
     * @return
     */
    public static String[] getTableNames(){
        return new String[]{
                DBOpenHelper.MESSAGE_TBL,//消息表
                DBOpenHelper.CHAT_ROOM_TBL,//聊天室表
                DBOpenHelper.MEMBER_TBL,//成员表
                DBOpenHelper.USER_TBL,//客户表
                DBOpenHelper.CLIENT_TBL,//客户表
                DBOpenHelper.CLIENT_LATEST_TBL,//到店通知客户表
        };
    }

}
