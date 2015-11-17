package com.zkjinshi.superservice.activity.set;

import android.content.Context;

import com.zkjinshi.superservice.ServiceApplication;
import com.zkjinshi.superservice.bean.ClientBaseBean;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;

import java.util.HashMap;

/**
 * 新增客户联系人控制器
 * 开发者：WinkyQin
 * 日期：2015/11/17
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientBindController {

    private Context mContext;
    private String  mUserID;
    private String  mToken;
    private String  mShopID;

    private static ClientBindController instance = null;

    private ClientBindController(){
        init(ServiceApplication.getContext());
    }

    public static synchronized ClientBindController getInstance(){
        if(null == instance){
            instance = new ClientBindController();
        }
        return instance;
    }

    private void init(Context context) {
        this.mContext = context;
        mUserID = CacheUtil.getInstance().getUserId();
        mToken  = CacheUtil.getInstance().getToken();
        mShopID = CacheUtil.getInstance().getShopID();
    }

    /**
    * 为服务员绑定客户
    * @param mClientBean
    * @param netRequestListener
    */
    public void bindClient(ClientBaseBean mClientBean, ExtNetRequestListener netRequestListener) {

        NetRequest netRequest = new NetRequest(ProtocolUtil.getAddUserUrl());
        HashMap<String,String> bizMap = new HashMap<>();
        bizMap.put("salesid", mUserID);
        bizMap.put("token", mToken);
        bizMap.put("shopid", mShopID);
        bizMap.put("userid", mClientBean.getUserid());
        bizMap.put("phone", mClientBean.getPhone());
        bizMap.put("username", mClientBean.getUsername());
        bizMap.put("position", mClientBean.getPosition());
        bizMap.put("company", mClientBean.getCompany());
        bizMap.put("other_desc", "");
        bizMap.put("is_bill", mClientBean.getIs_bill() + "");
        netRequest.setBizParamMap(bizMap);

        NetRequestTask netRequestTask = new NetRequestTask(mContext, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(netRequestListener);
        netRequestTask.isShowLoadingDialog = false;
        netRequestTask.execute();
    }

}
