package com.zkjinshi.superservice.activity.notice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.zkjinshi.base.net.core.WebSocketManager;
import com.zkjinshi.base.net.observer.IMessageObserver;
import com.zkjinshi.base.net.observer.MessageSubject;
import com.zkjinshi.base.net.protocol.ProtocolMSG;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.entity.MsgEmpStatus;
import com.zkjinshi.superservice.entity.MsgEmpStatusRSP;
import com.zkjinshi.superservice.utils.CacheUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 员工在线人数列表
 * 开发者：JimmyZhang
 * 日期：2015/10/13
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OnlineListActivity extends AppCompatActivity implements IMessageObserver{

    private void initView(){

    }

    private void initData(){
        addObservers();
        //发送查询客户是否在线请求
        MsgEmpStatus msgEmpStatus = new MsgEmpStatus();
        msgEmpStatus.setType(ProtocolMSG.MSG_ShopEmpStatus);
        msgEmpStatus.setTimestamp(System.currentTimeMillis());
        msgEmpStatus.setShopid(CacheUtil.getInstance().getShopID());
        JSONArray empids = new JSONArray();
        msgEmpStatus.setEmps(empids);
        String jsonMsgEmpStatus = new Gson().toJson(msgEmpStatus);
        WebSocketManager.getInstance().sendMessage(jsonMsgEmpStatus);
    }

    private void initListeners(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_list);
        initView();
        initData();
        initListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeObservers();
    }

    private void addObservers() {
        MessageSubject.getInstance().addObserver(this, ProtocolMSG.MSG_ShopEmpStatus_RSP);
    }

    private void removeObservers(){
        MessageSubject.getInstance().removeObserver(this, ProtocolMSG.MSG_ShopEmpStatus_RSP);
    }

    @Override
    public void receive(String message) {
        if(TextUtils.isEmpty(message)){
            return ;
        }
        Gson gson = null;
        try {
            if(null == gson){
                gson = new Gson();
            }
            JSONObject messageObj = new JSONObject(message);
            int type = messageObj.getInt("type");
            if (type == ProtocolMSG.MSG_ShopEmpStatus_RSP) {
                MsgEmpStatusRSP msgEmpStatusRSP = gson.fromJson(message,MsgEmpStatusRSP.class);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
