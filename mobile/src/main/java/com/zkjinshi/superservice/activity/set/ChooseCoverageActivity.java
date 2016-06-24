package com.zkjinshi.superservice.activity.set;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.CoverageAdapter;
import com.zkjinshi.superservice.base.BaseAppCompatActivity;
import com.zkjinshi.superservice.bean.ZoneBean;
import com.zkjinshi.superservice.response.GetZoneListResponse;
import com.zkjinshi.superservice.response.ServiceTagListResponse;
import com.zkjinshi.superservice.utils.AsyncHttpClientUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.ProtocolUtil;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * 选择服务区域Activity
 * 开发者：jimmyzhang
 * 日期：16/6/22
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ChooseCoverageActivity extends BaseAppCompatActivity {

    private Toolbar toolbar;
    private TextView titleIv;
    private ListView coveragelistView;
    private Menu menu;
    private String firstTagName;
    private ArrayList<String> selectMemberList;
    private ArrayList<String> selectCoverageList;
    private ArrayList<ZoneBean> zoneList;
    private CoverageAdapter coverageAdapter;
    private Map<String, Boolean> selectMap;

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        titleIv = (TextView) findViewById(R.id.tv_center_title);
        coveragelistView = (ListView)findViewById(R.id.list_view_coverage);
    }

    private void initData(){
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.mipmap.ic_fanhui);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        titleIv.setText("选择服务区域");
        if(null != getIntent()){
            if(null != getIntent().getStringExtra("firstTagName")){
                firstTagName = getIntent().getStringExtra("firstTagName");
            }
            if(null != getIntent().getStringArrayListExtra("selectMemberList")){
                selectMemberList = getIntent().getStringArrayListExtra("selectMemberList");
            }
        }
        selectCoverageList = new ArrayList<String>();
        selectMap = new HashMap<String, Boolean>();
        coverageAdapter = new CoverageAdapter(this,zoneList);
        coveragelistView.setAdapter(coverageAdapter);
        requestCoverageListTask();
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
                    case R.id.menu_choose_coverage_next://下一步
                        Intent intent = new Intent(ChooseCoverageActivity.this,ChooseAgencyActivity.class);
                        intent.putExtra("firstTagName",firstTagName);
                        intent.putExtra("selectMemberList",selectMemberList);
                        intent.putExtra("selectCoverageList",selectCoverageList);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        break;
                }
                return true;
            }
        });

        coveragelistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ZoneBean zoneBean = (ZoneBean) parent.getAdapter().getItem(position);
                String locId = zoneBean.getLocid();
                if (selectMap != null
                        && selectMap.containsKey(locId)
                        && selectMap.get(locId)) {
                    selectMap.put(locId, false);
                    selectCoverageList.remove(locId);
                } else {
                    selectMap.put(locId, true);
                    selectCoverageList.add(locId);
                }
                coverageAdapter.setSelectMap(selectMap);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_coverage);
        initView();
        initData();
        initListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_choose_coverage, menu);
        this.menu = menu;
        return true;
    }

    /**
     * 获取服务区域
     *
     */
    private void requestCoverageListTask(){
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token",CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.getZoneList();
            client.get(ChooseCoverageActivity.this,url, stringEntity, "application/json", new AsyncHttpResponseHandler(){
                public void onStart(){
                    DialogUtil.getInstance().showAvatarProgressDialog(ChooseCoverageActivity.this,"");
                }

                public void onFinish(){
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String response = new String(responseBody,"utf-8");
                        GetZoneListResponse getZoneListResponse = new Gson().fromJson(response,GetZoneListResponse.class);
                        if(null != getZoneListResponse && getZoneListResponse.getRes() == 0){
                            zoneList= getZoneListResponse.getData();
                            coverageAdapter.setZoneList(zoneList);
                        }else{
                            Toast.makeText(ChooseCoverageActivity.this,getZoneListResponse.getResDesc(),Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    AsyncHttpClientUtil.onFailure(ChooseCoverageActivity.this,statusCode);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
