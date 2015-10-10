package com.zkjinshi.superservice.factory;

import android.content.ContentValues;
import android.database.Cursor;

import com.zkjinshi.superservice.bean.ClientDetailBean;
import com.zkjinshi.superservice.vo.UnRegClientVo;

/**
 * 开发者：vincent
 * 日期：2015/10/10
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class UnRegClientFactory {

    private static UnRegClientFactory instance = null;

    private UnRegClientFactory(){}

    public synchronized static UnRegClientFactory getInstance(){
        if(instance == null){
            instance = new UnRegClientFactory();
        }
        return instance;
    }

    /**
     * 构建键对值
     * @param unRegClient
     * @return
     */
    public ContentValues buildAddContentValues(UnRegClientVo unRegClient) {
        ContentValues values = new ContentValues();
        values.put("phone", unRegClient.getPhone());
        values.put("username", unRegClient.getUsername());
        values.put("position", unRegClient.getCompany());
        values.put("company", unRegClient.getPosition());
        values.put("other_desc", unRegClient.getOther_desc());
        values.put("is_bill", unRegClient.getIs_bill());
        return values;
    }

    /**
     * 根据游标构建客户对象
     * @param cursor
     * @return
     */
    public UnRegClientVo buildUnRegClient(Cursor cursor) {
        UnRegClientVo unRegClientVo = new UnRegClientVo();
        unRegClientVo.setPhone(cursor.getString(0));
        unRegClientVo.setUsername(cursor.getString(1));
        unRegClientVo.setPosition(cursor.getString(2));
        unRegClientVo.setCompany(cursor.getString(3));
        unRegClientVo.setOther_desc(cursor.getString(4));
        unRegClientVo.setIs_bill(cursor.getInt(5));
        return unRegClientVo;
    }

    public ContentValues buildUpdateContentValues(UnRegClientVo unRegClient) {
        ContentValues values = new ContentValues();
        values.put("username", unRegClient.getUsername());
        values.put("position", unRegClient.getCompany());
        values.put("company", unRegClient.getPosition());
        values.put("other_desc", unRegClient.getOther_desc());
        values.put("is_bill", unRegClient.getIs_bill());
        return values;
    }

    public UnRegClientVo buildUnRegClientByClientDetailBean(ClientDetailBean mClient) {
        UnRegClientVo unRegClientVo = new UnRegClientVo();
        unRegClientVo.setPhone(mClient.getPhone());
        unRegClientVo.setUsername(mClient.getUsername());
        unRegClientVo.setPosition(mClient.getPosition());
        unRegClientVo.setCompany(mClient.getCompany());
        //TODO: tianji
        unRegClientVo.setOther_desc(mClient.getTags().toString());
        unRegClientVo.setIs_bill(mClient.getIs_bill());
        return unRegClientVo;
    }
}
