package com.zkjinshi.superservice.activity.set;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.ServiceTagAdapter;
import com.zkjinshi.superservice.base.BaseAppCompatActivity;
import com.zkjinshi.superservice.response.BaseResponse;
import com.zkjinshi.superservice.response.ServiceTagListResponse;
import com.zkjinshi.superservice.utils.AccessControlUtil;
import com.zkjinshi.superservice.utils.AsyncHttpClientUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.ServiceTagVo;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * 服务标签列表Activity
 * 开发者：jimmyzhang
 * 日期：16/6/22
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ServiceTagListActivity extends BaseAppCompatActivity {

    public static final int DELETE_MENU_ITEM = 0;

    private Toolbar toolbar;
    private TextView titleIv;
    private TextView emptyTv;
    private RelativeLayout emptyLayout;
    private SwipeMenuListView tagListView;
    private ArrayList<ServiceTagVo> serviceTagList;
    private ServiceTagAdapter serviceTagAdapter;

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        titleIv = (TextView) findViewById(R.id.tv_center_title);
        tagListView = (SwipeMenuListView)findViewById(R.id.tag_list_view);
        emptyTv = (TextView)findViewById(R.id.empty_tips);
        emptyLayout = (RelativeLayout)findViewById(R.id.tag_list_empty_layout);
    }

    private void initData(){
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.mipmap.ic_fanhui);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        titleIv.setText("服务标签");
        emptyTv.setText("暂无服务标签");
        SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                deleteItem.setWidth(DisplayUtil.dip2px(ServiceTagListActivity.this,90));
                deleteItem.setTitle("删除");
                deleteItem.setTitleSize(16);
                deleteItem.setTitleColor(Color.WHITE);
                if(AccessControlUtil.isShowView(AccessControlUtil.DELMEMBER)){
                    menu.addMenuItem(deleteItem);
                }
            }
        };
        tagListView.setMenuCreator(swipeMenuCreator);
        serviceTagAdapter = new ServiceTagAdapter(this,serviceTagList);
        tagListView.setAdapter(serviceTagAdapter);
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
                    case R.id.menu_add_service_tag://添加服务标签
                        Intent intent = new Intent(ServiceTagListActivity.this,AddServiceTagActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        break;
                }
                return true;
            }
        });

        tagListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case DELETE_MENU_ITEM://打开删除按钮
                        ServiceTagVo serviceTagVo = serviceTagList.get(position);
                        String firstTagId = serviceTagVo.getFirstSrvTagId();
                        requestDeleteFirstTagTask(firstTagId);
                        break;
                }
                return false;
            }
        });

        tagListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ServiceTagVo serviceTagVo = serviceTagList.get(position);
                Intent intent = new Intent(ServiceTagListActivity.this,ServiceSecondTagListActivity.class);
                intent.putExtra("serviceTagVo",serviceTagVo);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_tag_list);
        initView();
        initData();
        initListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestServiceTagListTask();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_service_tag_list, menu);
        return true;
    }

    /**
     * 获取标签列表
     */
    private void requestServiceTagListTask(){
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token", CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.getServiceTagListUrl();
            client.get(ServiceTagListActivity.this,url, stringEntity, "application/json", new AsyncHttpResponseHandler(){
                public void onStart(){
                    super.onStart();
                    DialogUtil.getInstance().showAvatarProgressDialog(ServiceTagListActivity.this,"");
                }

                public void onFinish(){
                    super.onFinish();
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String response = new String(responseBody,"utf-8");
                        ServiceTagListResponse serviceTagListResponse = new Gson().fromJson(response,ServiceTagListResponse.class);
                        int resultFlag = serviceTagListResponse.getRes();
                        if(0 == resultFlag || 30001 == resultFlag){
                            serviceTagList = serviceTagListResponse.getData();
                            serviceTagAdapter.setFirstTagList(serviceTagList);
                            if(30001 == resultFlag){
                                tagListView.setEmptyView(emptyLayout);
                            }
                        }else {
                            String errorMsg = serviceTagListResponse.getResDesc();
                            if(!TextUtils.isEmpty(errorMsg)){
                                DialogUtil.getInstance().showCustomToast(ServiceTagListActivity.this,errorMsg, Gravity.CENTER);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    AsyncHttpClientUtil.onFailure(ServiceTagListActivity.this,statusCode);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void requestDeleteFirstTagTask(String firstTagId){
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            if(CacheUtil.getInstance().isLogin()){
                client.addHeader("Token",CacheUtil.getInstance().getExtToken());
            }
            final String url = ProtocolUtil.getDeleteFirstTagUrl(firstTagId);
            client.delete(ServiceTagListActivity.this,url, new AsyncHttpResponseHandler(){
                public void onStart(){
                    super.onStart();
                    DialogUtil.getInstance().showAvatarProgressDialog(ServiceTagListActivity.this,"");
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
                                requestServiceTagListTask();
                            } else {
                                String resultMsg = deleteWhiteUserResponse.getResDesc();
                                if (!TextUtils.isEmpty(resultMsg)) {
                                    DialogUtil.getInstance().showCustomToast(ServiceTagListActivity.this, resultMsg, Gravity.CENTER);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    AsyncHttpClientUtil.onFailure(ServiceTagListActivity.this,statusCode);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
