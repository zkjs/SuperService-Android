package com.zkjinshi.superservice.activity.set;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.EventManagerAdapter;
import com.zkjinshi.superservice.base.BaseAppCompatActivity;
import com.zkjinshi.superservice.response.BaseResponse;
import com.zkjinshi.superservice.response.EventListResponse;
import com.zkjinshi.superservice.response.RoleListResponse;
import com.zkjinshi.superservice.utils.AccessControlUtil;
import com.zkjinshi.superservice.utils.AsyncHttpClientUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.EventVo;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * 活动管理Activity
 * 开发者：jimmyzhang
 * 日期：16/6/22
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class EventManagerActivity extends BaseAppCompatActivity {

    public static final int MENU_ITEM_EDIT = 0;
    public static final int MENU_ITEM_CANCEL = 1;

    private Toolbar toolbar;
    private TextView titleIv;
    private SwipeMenuListView eventListView;
    private EventManagerAdapter eventManagerAdapter;
    private ArrayList<EventVo> eventList;

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        titleIv = (TextView) findViewById(R.id.tv_center_title);
        eventListView = (SwipeMenuListView)findViewById(R.id.list_view_event_manager);
    }

    private void initData(){
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.mipmap.ic_fanhui);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        titleIv.setText("活动管理");
        eventManagerAdapter = new EventManagerAdapter(this,eventList);
        eventListView.setAdapter(eventManagerAdapter);
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem editItem = new SwipeMenuItem(
                        getApplicationContext());
                editItem.setBackground(new ColorDrawable(Color.rgb(0x03,
                        0xA9, 0xF4)));
                editItem.setWidth(DisplayUtil.dip2px(EventManagerActivity.this,90));
                editItem.setTitle("编辑");
                editItem.setTitleSize(16);
                editItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(editItem);

                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xE8,
                        0x4E, 0x40)));
                deleteItem.setWidth(DisplayUtil.dip2px(EventManagerActivity.this,90));
                deleteItem.setTitle("删除");
                deleteItem.setTitleSize(16);
                deleteItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(deleteItem);
            }
        };
        eventListView.setMenuCreator(creator);
    }

    private void initListeners(){

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(android.view.MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_event_manage_add://新增事件
                        Intent intent = new Intent(EventManagerActivity.this,CreateEventActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        break;
                }
                return true;
            }
        });

        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EventVo eventVo = (EventVo) parent.getAdapter().getItem(position);
                Intent intent = new Intent(EventManagerActivity.this,GuestListActivity.class);
                intent.putExtra("eventVo",eventVo);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });

        eventListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case MENU_ITEM_EDIT://编辑
                        EventVo eventVo = eventList.get(position);
                        Intent intent = new Intent(EventManagerActivity.this,EditEventActivity.class);
                        intent.putExtra("eventVo",eventVo);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        break;

                    case MENU_ITEM_CANCEL://取消
                        showSureCancelDialog(position);
                        break;
                }
                return false;
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_manager);
        initView();
        initData();
        initListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestEventListTask();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_manager, menu);
        return true;
    }

    /**
     * 显示取消活动对话框
     * @param position
     */
    private void showSureCancelDialog(final int position){
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
        customBuilder.setMessage("你确定要取消活动吗?");
        customBuilder.setGravity(Gravity.CENTER);
        customBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        customBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                EventVo eventVo = eventList.get(position);
                String actId = eventVo.getActid();
                requestDeleteEventTask(actId);
            }
        });
        customBuilder.create().show();
    }

    /**
     * 获取活动管理列表
     */
    private void requestEventListTask(){
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token", CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.getEventListUrl();
            client.get(EventManagerActivity.this,url, stringEntity, "application/json", new AsyncHttpResponseHandler(){
                public void onStart(){
                    super.onStart();
                    DialogUtil.getInstance().showAvatarProgressDialog(EventManagerActivity.this,"");
                }

                public void onFinish(){
                    super.onFinish();
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String response = new String(responseBody,"utf-8");
                        EventListResponse eventResponse = new Gson().fromJson(response,EventListResponse.class);
                        if(null != eventResponse){
                            if(eventResponse.getRes() == 0){
                                eventList = eventResponse.getData();
                                eventManagerAdapter.setEventList(eventList);
                            }else{
                                Toast.makeText(EventManagerActivity.this,eventResponse.getResDesc(),Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    AsyncHttpClientUtil.onFailure(EventManagerActivity.this,statusCode);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 取消活动请求
     *
     * @param actId
     */
    private void requestDeleteEventTask(String actId){
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            if(CacheUtil.getInstance().isLogin()){
                client.addHeader("Token",CacheUtil.getInstance().getExtToken());
            }
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            final String url = ProtocolUtil.getDeleteEventUrl(actId);
            client.delete(EventManagerActivity.this,url, stringEntity, "application/json", new AsyncHttpResponseHandler(){
                public void onStart(){
                    super.onStart();
                    DialogUtil.getInstance().showAvatarProgressDialog(EventManagerActivity.this,"");
                }

                public void onFinish(){
                    super.onFinish();
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String response = new String(responseBody,"utf-8");
                        BaseResponse deleteWhiteUserResponse = new Gson().fromJson(response,BaseResponse.class);
                        if(null != deleteWhiteUserResponse) {
                            int resultCode = deleteWhiteUserResponse.getRes();
                            if (0 == resultCode) {
                                requestEventListTask();
                            } else {
                                String resultMsg = deleteWhiteUserResponse.getResDesc();
                                if (!TextUtils.isEmpty(resultMsg)) {
                                    DialogUtil.getInstance().showCustomToast(EventManagerActivity.this, resultMsg, Gravity.CENTER);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    AsyncHttpClientUtil.onFailure(EventManagerActivity.this,statusCode);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
