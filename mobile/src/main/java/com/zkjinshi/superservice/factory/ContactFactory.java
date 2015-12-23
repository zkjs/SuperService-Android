package com.zkjinshi.superservice.factory;

import android.text.TextUtils;

import com.zkjinshi.superservice.utils.PinyinHelper;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.ClientVo;
import com.zkjinshi.superservice.vo.ContactVo;

import org.w3c.dom.Text;

/**
 * 构建联系人工厂类
 * 开发者：vincent
 * 日期：2015/9/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ContactFactory {

    private static ContactFactory instance = null;

    private ContactFactory(){}

    public synchronized static ContactFactory getInstance(){
        if(instance == null){
            instance = new ContactFactory();
        }
        return instance;
    }

    /**
     * 根据我的客户对象生成排序对象
     * @param clientVo
     * @return
     */
    public ContactVo buildContactVoByMyClientVo(ClientVo clientVo) {

        String clientID = clientVo.getUserid();
        String userName = clientVo.getUsername();

        ContactVo contact = new ContactVo();
        contact.setContactType(clientVo.getContactType());
        contact.setName(userName);
        if(!TextUtils.isEmpty(clientID)){
            contact.setAvatarUrl(ProtocolUtil.getAvatarUrl(clientID));
        }
        contact.setNumber(clientVo.getPhone());
        if(!TextUtils.isEmpty(userName)){
            contact.setSortKey(userName);
            String pinyin = PinyinHelper.getInstance().getPinYin(userName);
            //获得首字母
            String firstLetter = PinyinHelper.getInstance().getFirstLetter(pinyin);
            contact.setFirstLetter(firstLetter);
        }else {
            contact.setSortKey(clientID);
            String pinyin = PinyinHelper.getInstance().getPinYin(userName);
            //获得首字母
            String firstLetter = PinyinHelper.getInstance().getFirstLetter(pinyin);
            contact.setFirstLetter(firstLetter);
        }
        contact.setClientID(clientID);
        contact.setIsOnLine(clientVo.getIsOnline());
        contact.setBgDrawableRes(clientVo.getBgDrawableRes());
        return contact;

        //获得电话号码， 联系人名称，首字母等关键信息
//                String phoneNumber =  contactCursor.getString(PHONES_NUMBER_INDEX);
//                if (TextUtils.isEmpty(phoneNumber)){
//                    continue;
//                }
//                String contactName  = contactCursor.getString(PHONES_DISPLAY_NAME_INDEX);
//                //魅族等手机 sortkey有可能是中文
//                String sortKey      = contactCursor.getString(SORT_KEY_INDEX);
//
//
//                //汉字转换成拼音
//                Contact contact = new Contact();
//                contact.setContactPhone(phoneNumber);
//                contact.setContactName(contactName);
//                contact.setSortKey(pinYin);
//                contact.setFirstLetter(getFirstLetter(pinYin));
//                contactList.add(contact);
    }
}
