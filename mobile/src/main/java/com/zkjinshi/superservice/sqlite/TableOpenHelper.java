package com.zkjinshi.superservice.sqlite;

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
                    + " client_id       text primary key, " //主键
                    + " client_name     text, " //客户姓名
                    + " client_phone    text, " //客户手机
                    + " avatar_name     text, " //图片名称
                    + " avatar_url      text，" //图片url
                    +" client_company   text, " //客户所在公司
                    + " client_position text, " //客户职位
                    + " on_account      integer "   //是否挂账会员 0:挂账会员 1 非挂账会员
                    + " ) ";

    /**
     * 获取数据库所有表名
     * @return
     */
    public static String[] getTableNames(){
        return new String[]{
                DBOpenHelper.CLIENT_TBL,     //客户信息表
        };
    }
}
