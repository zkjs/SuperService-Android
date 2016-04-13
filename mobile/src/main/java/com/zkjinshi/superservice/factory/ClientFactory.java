package com.zkjinshi.superservice.factory;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.zkjinshi.superservice.bean.ClientBaseBean;
import com.zkjinshi.superservice.bean.ClientDetailBean;
import com.zkjinshi.superservice.utils.PinyinHelper;
import com.zkjinshi.superservice.utils.RandomDrawbleUtil;
import com.zkjinshi.superservice.vo.ClientContactVo;
import com.zkjinshi.superservice.vo.ClientVo;
import com.zkjinshi.superservice.vo.ContactType;
import com.zkjinshi.superservice.vo.IsBill;
import com.zkjinshi.superservice.vo.OnlineStatus;
import com.zkjinshi.superservice.vo.SexType;

import java.util.ArrayList;
import java.util.List;

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
    public ContentValues buildAddContentValues(ClientVo client) {
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
        String created       = client.getCreated();
        long  modified      = client.getModified();
        String username      = client.getUsername();
        String phone         = client.getPhone();
        String company       = client.getCompany();
        String position      = client.getPosition();
        int    isBill       = client.getIs_bill();
        ContactType contactType = client.getContactType();
        SexType     setType     = client.getSex();
        int    orderCount  = client.getOrder_count();
        String tags        = client.getTags();

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
        if(null != contactType){
            values.put("contact_type", contactType.getValue());
        }

       if(null != setType){
           values.put("sex", setType.getVlaue());
       }
        values.put("order_count", orderCount);
        values.put("tags", tags);
        values.put("bg_drawable_res", RandomDrawbleUtil.getRandomDrawable());
        return values;
    }

    /**
     * 根据游标构建客户对象
     * @param cursor
     * @return
     */
    public ClientVo buildclient(Cursor cursor) {
            ClientVo client = new ClientVo();
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
            client.setCreated(cursor.getString(12));
            client.setModified(cursor.getLong(13));
            client.setUsername(cursor.getString(14));
            client.setPhone(cursor.getString(15));
            client.setCompany(cursor.getString(16));
            client.setPosition(cursor.getString(17));
            client.setIs_bill(cursor.getInt(18));
            client.setContactType(getContactType(cursor.getInt(19)));
            client.setSex(getSexType(cursor.getInt(20)));
            client.setOrder_count(cursor.getInt(21));
            client.setTags(cursor.getString(22));
            client.setIsOnline(getOnlineStatus(cursor.getInt(23)));
            client.setBgDrawableRes(cursor.getInt(24));
        return  client;
    }

    /**
     * 获得联系人类型
     * @param value
     * @return
     */
    private ContactType getContactType(int value) {
        if(value == ContactType.NORMAL.getValue()) {
            return ContactType.NORMAL;
        } else {
            return ContactType.UNNORMAL;
        }
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

    public List<ClientVo> buildClientVosByClientBeans(List<ClientDetailBean> clientDetailBeans) {
        List<ClientVo> clientVos = null;
        ClientVo       clientVo  = null;
        if(null != clientDetailBeans && !clientDetailBeans.isEmpty()){
            clientVos = new ArrayList<>();
            for(ClientDetailBean clientDetailBean : clientDetailBeans){
                clientVo = bulidClientVoByClientBean(clientDetailBean);
                clientVos.add(clientVo);
            }
        }
        return clientVos;
    }

    /**
     * @param clientDetailBean
     * @return
     */
    public ClientVo bulidClientVoByClientBean(ClientDetailBean clientDetailBean) {
        ClientVo clientVo = new ClientVo();
        clientVo.setUserid(clientDetailBean.getUserid());
        clientVo.setId(clientDetailBean.getId());
        clientVo.setShopid(clientDetailBean.getShopid());
        clientVo.setSalesid(clientDetailBean.getSalesid());
        clientVo.setUser_level(clientDetailBean.getUser_level());
        clientVo.setLevel_desc(clientDetailBean.getLevel_desc());
        clientVo.setCard_no(clientDetailBean.getCard_no());
        clientVo.setIs_special(clientDetailBean.getIs_special());
        clientVo.setNationality(clientDetailBean.getNationality());
        clientVo.setLike_desc(clientDetailBean.getLike_desc());
        clientVo.setTaboo_desc(clientDetailBean.getTaboo_desc());
        clientVo.setOther_desc(clientDetailBean.getOther_desc());
        clientVo.setCreated(clientDetailBean.getCreated());
        clientVo.setModified(clientDetailBean.getModified());
        clientVo.setUsername(clientDetailBean.getUsername());
        clientVo.setPhone(clientDetailBean.getPhone());
        clientVo.setCompany(clientDetailBean.getCompany());
        clientVo.setPosition(clientDetailBean.getPosition());
        clientVo.setIs_bill(clientDetailBean.getIs_bill());
        return clientVo;
    }

    public ContentValues buildUpdateContentValues(ClientVo client) {

        ContentValues values = new ContentValues();
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
        String created       = client.getCreated();
        long  modified      = client.getModified();
        String username      = client.getUsername();
        String phone         = client.getPhone();
        String company       = client.getCompany();
        String position      = client.getPosition();
        int    isBill       = client.getIs_bill();
        int    contactType  = client.getContactType().getValue();

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
        values.put("contact_type", contactType);
        values.put("sex", client.getSex().getVlaue());
        values.put("order_count", client.getOrder_count());
        values.put("tags", client.getTags());
        values.put("online_status", client.getIsOnline().getValue());
        return values;
    }

    private SexType getSexType(int sexType){
        return sexType == SexType.MALE.getVlaue()? SexType.MALE : SexType.FEMALE;
    }

    private OnlineStatus getOnlineStatus(int onlineStatus) {
        return onlineStatus == OnlineStatus.OFFLINE.getValue() ? OnlineStatus.OFFLINE : OnlineStatus.ONLINE;
    }

    /**
     * 构建排序对象
     * @param contactsList
     */
    public List<ClientContactVo> buildSortContactList(List<ClientContactVo> contactsList) {

        for(ClientContactVo contact : contactsList){
            String name = contact.getUsername();
            if(TextUtils.isEmpty(name)){
                name = contact.getUserid();
            }

            if(!TextUtils.isEmpty(name)){
                String sortKey = PinyinHelper.getInstance().getPinYin(name);
                contact.setSortKey(sortKey);
                String firstLetter = PinyinHelper.getInstance().getFirstLetter(sortKey);
                contact.setFirstLetter(firstLetter);
            }
        }
        return contactsList;
    }
}
