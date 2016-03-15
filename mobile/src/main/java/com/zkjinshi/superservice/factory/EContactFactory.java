package com.zkjinshi.superservice.factory;

import android.text.TextUtils;

import com.zkjinshi.superservice.bean.MemberBean;
import com.zkjinshi.superservice.vo.ClientVo;
import com.zkjinshi.superservice.vo.EContactVo;
import com.zkjinshi.superservice.vo.EmployeeVo;

import java.util.ArrayList;

/**
 * 开发者：JimmyZhang
 * 日期：2015/11/30
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class EContactFactory {

    private EContactFactory (){}

    private static EContactFactory instance;

    public synchronized static EContactFactory getInstance(){
        if(null == instance){
            instance = new EContactFactory();
        }
        return instance;
    }

    public EContactVo buildEContactVo(MemberBean memberBean){
        EContactVo contactVo = new EContactVo();
        if(null != memberBean){
            contactVo.setContactName(memberBean.getUsername());
            contactVo.setContactId(memberBean.getUserid());
        }
        return contactVo;
    }

    public EContactVo buildEContactVo(EmployeeVo shopEmployeeVo){
        EContactVo contactVo = new EContactVo();
        if(null != shopEmployeeVo){
            String contactId = shopEmployeeVo.getUserid();
            if(!TextUtils.isEmpty(contactId)){
                contactVo.setContactId(contactId);
            }
            String contactName = shopEmployeeVo.getUsername();
            if(!TextUtils.isEmpty(contactName)){
                contactVo.setContactName(contactName);
            }
        }
        return  contactVo;
    }

    public EContactVo buildEContactVo(ClientVo clientVo){
        EContactVo contactVo = new EContactVo();
        if(null != clientVo){
            String contactId = clientVo.getUserid();
            if(!TextUtils.isEmpty(contactId)){
                contactVo.setContactId(contactId);
            }
            String contactName = clientVo.getUsername();
            if(!TextUtils.isEmpty(contactName)){
                contactVo.setContactName(contactName);
            }
        }
        return contactVo;
    }

    public EContactVo buildDefaultContactVo(String userId,String userName){
        EContactVo contactVo = new EContactVo();
        if(!TextUtils.isEmpty(userId)){
            contactVo.setContactId(userId);
        }
        if(!TextUtils.isEmpty(userName)){
            contactVo.setContactName(userName);
        }
        return contactVo;
    }

}
