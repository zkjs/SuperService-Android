package com.zkjinshi.superservice.factory;

import android.content.ContentValues;
import android.text.TextUtils;

import com.zkjinshi.superservice.vo.ClientVo;
import com.zkjinshi.superservice.vo.OnAccountStatus;

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
     * @param clientVo
     * @return
     */
    public ContentValues
    buildAddContentValues(ClientVo clientVo) {

        ContentValues values = new ContentValues();
        String clientID = clientVo.getClientID();
        String clientName = clientVo.getClientName();
        String avatarName = clientVo.getAvatarName();
        String avatarUrl  = clientVo.getAvatarUrl();
        String clientPhone = clientVo.getClientPhone();
        String clientCompany = clientVo.getClientCompany();
        String clientPosition = clientVo.getClientPosition();
        OnAccountStatus isOnAccount = clientVo.isOnAccount();

        values.put("client_id", clientID);
        values.put("client_name", clientName);
        values.put("client_phone", clientPhone);
        values.put("on_account", isOnAccount.getValue());

        if(!TextUtils.isEmpty(avatarName)){
            values.put("avatar_name", avatarName);
        }

        if(!TextUtils.isEmpty(avatarUrl)){
            values.put("avatar_url", avatarUrl);
        }

        if(!TextUtils.isEmpty(clientCompany)){
            values.put("client_company", clientCompany);
        }

        if(!TextUtils.isEmpty(clientPosition)){
            values.put("client_position", clientPosition);
        }
        return values;
    }

}
