package com.zkjinshi.superservice.activity.set;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.TaskHistoryAdapter;
import com.zkjinshi.superservice.base.BaseAppCompatActivity;
import com.zkjinshi.superservice.response.TaskDetailResponse;
import com.zkjinshi.superservice.utils.AsyncHttpClientUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.ListViewUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.ServiceHistoryVo;
import com.zkjinshi.superservice.vo.TaskDetailVo;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * 任务详情Activity
 * 开发者：jimmyzhang
 * 日期：16/6/22
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class DetailTaskActivity extends BaseAppCompatActivity {

    private Toolbar toolbar;
    private TextView titleIv;
    private String taskId;
    private SimpleDraweeView userPhotoSdv;
    private TextView userNameTv;
    private TextView serviceNameTv;
    private ListView historyListView;
    private TaskHistoryAdapter taskHistoryAdapter;
    private ArrayList<ServiceHistoryVo> historyList;

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        titleIv = (TextView) findViewById(R.id.tv_center_title);
    }

    private void initData(){
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.mipmap.ic_fanhui);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        titleIv.setText("任务追踪");
        if(null != getIntent()){
            if(null != getIntent().getStringExtra("taskId")){
                taskId = getIntent().getStringExtra("taskId");
            }
        }
        taskHistoryAdapter = new TaskHistoryAdapter(this,historyList);
        historyListView.setAdapter(taskHistoryAdapter);
        requestTaskDetailTask(taskId);
    }

    private void initListeners(){

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }
        });
    }

    private void updateData(TaskDetailVo taskDetailVo){
        if(null != taskDetailVo){
            String userNameStr = taskDetailVo.getUsername();
            String locDesc = taskDetailVo.getLocdesc();
            if(!TextUtils.isEmpty(userNameStr) && !TextUtils.isEmpty(locDesc)){
                userNameTv.setText(userNameStr+" (" + locDesc + ")");
            }
            String serviceNameStr = taskDetailVo.getSrvname();
            if(!TextUtils.isEmpty(serviceNameStr)){
                serviceNameTv.setText(serviceNameStr);
            }
            historyList = taskDetailVo.getHistory();
            taskHistoryAdapter.setHistoryList(historyList);
            ListViewUtil.setListViewHeightBasedOnChildren(historyListView);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_service_detail);
        initView();
        initData();
        initListeners();
    }

    /**
     * 获取任务追踪明细
     *
     */
    private void requestTaskDetailTask(String taskId){
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token",CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.getTaskDetailUrl(taskId);
            client.get(DetailTaskActivity.this,url, stringEntity, "application/json", new AsyncHttpResponseHandler(){
                public void onStart(){
                    DialogUtil.getInstance().showAvatarProgressDialog(DetailTaskActivity.this,"");
                }

                public void onFinish(){
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String response = new String(responseBody,"utf-8");
                        TaskDetailResponse taskDetialResponse = new Gson().fromJson(response,TaskDetailResponse.class);
                        if(null != taskDetialResponse && taskDetialResponse.getRes() == 0){
                            TaskDetailVo taskDetailVo = taskDetialResponse.getData();
                            updateData(taskDetailVo);
                        }else{
                            Toast.makeText(DetailTaskActivity.this,taskDetialResponse.getResDesc(),Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    AsyncHttpClientUtil.onFailure(DetailTaskActivity.this,statusCode);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
