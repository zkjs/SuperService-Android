package com.zkjinshi.superservice.factory;

import android.content.ContentValues;
import android.database.Cursor;

import com.zkjinshi.superservice.bean.ClientBean;
import com.zkjinshi.superservice.vo.IsBill;

/**
 * 开发者：vincent
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientFactory {

    private static ClientFactory instance = null;

    private ClientFactory(){}

    public synchronized static ClientFactory getInstance(){
        if(instance == null){
            instance = new ClientFactory();
        }
        return instance;
    }

    /**
     * 构建新的客户对象键对值
     * @param client
     * @return
     */
    public ContentValues buildAddContentValues(ClientBean client) {
        ContentValues values = new ContentValues();
        String userId      = client.getUserid();
        int    id            = client.getId();
        String shopID        = client.getShopid();
        String salesID       = client.getSalesid();
        int   userLevel      = client.getUser_level();
        String levelDesc     = client.getLevel_desc();
        String cardNo        = client.getCard_no();
        String isSpecial     = client.getIs_special();
        String nationality   = client.getNationality();
        String likeDesc      = client.getLike_desc();
        String tabooDesc     = client.getTaboo_desc();
        String otherDesc     = client.getOther_desc();
        long  created       = client.getCreated();
        long  modified      = client.getModified();
        String username      = client.getUsername();
        String phone         = client.getPhone();
        String company       = client.getCompany();
        String position      = client.getPosition();
        int    isBill       = client.getIs_bill();

        values.put("userid", userId);
        values.put("id", id);
        values.put("shopid", shopID);
        values.put("salesid", salesID);
        values.put("user_level", userLevel);
        values.put("level_desc", levelDesc);
        values.put("card_no", cardNo);
        values.put("is_special", isSpecial);
        values.put("nationality", nationality);
        values.put("like_desc", likeDesc);
        values.put("taboo_desc", tabooDesc);
        values.put("other_desc", otherDesc);
        values.put("created", created);
        values.put("modified", modified);
        values.put("username", username);
        values.put("phone", phone);
        values.put("company", company);
        values.put("position", position);
        values.put("is_bill", isBill);
        return values;
    }

    /**
     * 根据游标构建客户对象
     * @param cursor
     * @return
     */
    public ClientBean buildclient(Cursor cursor) {
            ClientBean client = new ClientBean();
            client.setUserid(cursor.getString(0));
            client.setId(cursor.getInt(1));
            client.setShopid(cursor.getString(2));
            client.setSalesid(cursor.getString(3));
            client.setUser_level(cursor.getInt(4));
            client.setLevel_desc(cursor.getString(5));
            client.setCard_no(cursor.getString(6));
            client.setIs_special(cursor.getString(7));
            client.setNationality(cursor.getString(8));
            client.setLike_desc(cursor.getString(9));
            client.setTaboo_desc(cursor.getString(10));
            client.setOther_desc(cursor.getString(11));
            client.setCreated(cursor.getLong(12));
            client.setModified(cursor.getLong(13));
            client.setUsername(cursor.getString(14));
            client.setPhone(cursor.getString(15));
            client.setCompany(cursor.getString(16));
            client.setPosition(cursor.getString(17));
            client.setIs_bill(cursor.getInt(18));
        return  client;
    }

    /**
     * 获得是否挂账会员状态
     * @param isBill
     * @return
     */
    private IsBill isBill(int isBill){
        if(isBill == IsBill.ISONACCOUNT.getValue()){
            return IsBill.ISONACCOUNT;
        } else {
            return IsBill.NOTONACCOUNT;
        }
    }

}
