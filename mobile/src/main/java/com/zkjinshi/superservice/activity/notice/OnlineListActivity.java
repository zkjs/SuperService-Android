package com.zkjinshi.superservice.activity.notice;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zkjinshi.base.net.core.WebSocketManager;
import com.zkjinshi.base.net.observer.IMessageObserver;
import com.zkjinshi.base.net.observer.MessageSubject;
import com.zkjinshi.base.net.protocol.ProtocolMSG;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.OnlineAdapter;
import com.zkjinshi.superservice.entity.EmpStatusRecord;
import com.zkjinshi.superservice.entity.MsgEmpStatus;
import com.zkjinshi.superservice.entity.MsgEmpStatusRSP;
import com.zkjinshi.superservice.factory.EmpStatusFactory;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.vo.EmpStatusVo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 员工在线人数列表
 * 开发者：JimmyZhang
 * 日期：2015/10/13
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OnlineListActivity extends AppCompatActivity implements IMessageObserver{

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private TextView currentEmpTv,totalEmpTv;
    private ArrayList<EmpStatusVo> empStatusList;
    private OnlineAdapter onlineAdapter;
    private ImageButton backIBtn;
    private TextView titleTv;
    private LinearLayoutManager linearLayoutManager;

    private void initView(){
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.srl_online);
        recyclerView = (RecyclerView)findViewById(R.id.rcv_online);
        currentEmpTv = (TextView)findViewById(R.id.notice_tv_current_online_employee);
        totalEmpTv = (TextView)findViewById(R.id.notice_tv_total_employee);
        backIBtn = (ImageButton)findViewById(R.id.header_bar_btn_back);
        titleTv = (TextView)findViewById(R.id.header_bar_tv_title);
    }

    private void initData(){
       // recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        onlineAdapter = new OnlineAdapter(this,empStatusList);
        recyclerView.setAdapter(onlineAdapter);
        addObservers();
        requestOnlineTask();
        titleTv.setText("员工状态");
        backIBtn.setVisibility(View.VISIBLE);
    }

    private void initListeners(){

        //刷新页面
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestOnlineTask();
            }
        });

        //返回
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
                if(null != msgEmpStatusRSP){
                    ArrayList<EmpStatusRecord> empStatusRecordList =  (ArrayList<EmpStatusRecord>)msgEmpStatusRSP.getResult();
                    if(null != empStatusRecordList && !empStatusRecordList.isEmpty()){
                        empStatusList = EmpStatusFactory.getInstance().buildEmpStatusList(empStatusRecordList);
                        onlineAdapter.setEmpStatusList(empStatusList);
                    }
                }
                if(null != swipeRefreshLayout){
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送查询客户是否在线请求
     */
    private void requestOnlineTask(){
        MsgEmpStatus msgEmpStatus = new MsgEmpStatus();
        msgEmpStatus.setType(ProtocolMSG.MSG_ShopEmpStatus);
        msgEmpStatus.setTimestamp(System.currentTimeMillis());
        msgEmpStatus.setShopid(CacheUtil.getInstance().getShopID());
        ArrayList<String> empids = new ArrayList<String>();
        msgEmpStatus.setEmps(empids);
        String jsonMsgEmpStatus = new Gson().toJson(msgEmpStatus);
        WebSocketManager.getInstance().sendMessage(jsonMsgEmpStatus);
    }

}
