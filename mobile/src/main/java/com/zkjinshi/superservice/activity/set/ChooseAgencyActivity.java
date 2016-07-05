package com.zkjinshi.superservice.activity.set;

import android.app.Activity;
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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.common.MoreActivity;
import com.zkjinshi.superservice.adapter.AgencyAdapter;
import com.zkjinshi.superservice.base.BaseAppCompatActivity;
import com.zkjinshi.superservice.manager.SSOManager;
import com.zkjinshi.superservice.response.BaseResponse;
import com.zkjinshi.superservice.response.GetEmployeesResponse;
import com.zkjinshi.superservice.response.RoleListResponse;
import com.zkjinshi.superservice.sqlite.DBOpenHelper;
import com.zkjinshi.superservice.sqlite.ShopEmployeeDBUtil;
import com.zkjinshi.superservice.utils.AsyncHttpClientUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.ListViewUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.AgencyVo;
import com.zkjinshi.superservice.vo.EmployeeVo;
import com.zkjinshi.superservice.vo.IdentityType;
import com.zkjinshi.superservice.vo.PayloadVo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * 选择执行部门Activity
 * 开发者：jimmyzhang
 * 日期：16/6/22
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ChooseAgencyActivity extends BaseAppCompatActivity {

    private Toolbar toolbar;
    private TextView titleIv;
    private ListView agencylistView;
    private Menu menu;
    private ArrayList<AgencyVo> agencyList;

    private ArrayList<String> selectAgencyList;
    private String firstTagName;
    private ArrayList<String> selectMemberList;
    private ArrayList<String> selectCoverageList;
    private AgencyAdapter agencyAdapter;
    private Map<String, Boolean> selectMap;

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        titleIv = (TextView) findViewById(R.id.tv_center_title);
        agencylistView = (ListView)findViewById(R.id.list_view_agency);
    }

    private void initData(){
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.mipmap.ic_fanhui);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        titleIv.setText("任务执行部门");
        agencyAdapter = new AgencyAdapter(this,agencyList);
        agencylistView.setAdapter(agencyAdapter);
        selectMap = new HashMap<String,Boolean>();
        selectAgencyList = new ArrayList<String>();
        if(null != getIntent()){
            if(null != getIntent().getStringExtra("firstTagName")){
                firstTagName = getIntent().getStringExtra("firstTagName");
            }
            if(null != getIntent().getStringArrayListExtra("selectMemberList")){
                selectMemberList = getIntent().getStringArrayListExtra("selectMemberList");
            }
            if(null != getIntent().getStringArrayListExtra("selectCoverageList")){
                selectCoverageList = getIntent().getStringArrayListExtra("selectCoverageList");
            }
        }
        requestRoleListTask();
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
                    case R.id.menu_choose_agency_finish://完成
                        requestAddServiceTagTask(firstTagName,selectMemberList,selectCoverageList,selectAgencyList);
                        break;
                }
                return true;
            }
        });

        agencylistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AgencyVo agencyVo = (AgencyVo) parent.getAdapter().getItem(position);
                String roleId = agencyVo.getRoleid();
                if (selectMap != null
                        && selectMap.containsKey(roleId)
                        && selectMap.get(roleId)) {
                    selectMap.put(roleId, false);
                    selectAgencyList.remove(roleId);
                } else {
                    selectMap.put(roleId, true);
                    selectAgencyList.add(roleId);
                }
                agencyAdapter.setSelectMap(selectMap);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_agency);
        initView();
        initData();
        initListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_choose_agency, menu);
        this.menu = menu;
        return true;
    }

    /**
     * 获取部分列表
     */
    private void requestRoleListTask(){
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token", CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.getRoleListUrl();
            client.get(ChooseAgencyActivity.this,url, stringEntity, "application/json", new AsyncHttpResponseHandler(){
                public void onStart(){
                    super.onStart();
                    DialogUtil.getInstance().showAvatarProgressDialog(ChooseAgencyActivity.this,"");
                }

                public void onFinish(){
                    super.onFinish();
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String response = new String(responseBody,"utf-8");
                        RoleListResponse roleListResponse = new Gson().fromJson(response,RoleListResponse.class);
                        if(null != roleListResponse){
                            if(roleListResponse.getRes() == 0){
                                agencyList = roleListResponse.getData();
                                agencyAdapter.setAgencyList(agencyList);
                            }else{
                                Toast.makeText(ChooseAgencyActivity.this,roleListResponse.getResDesc(),Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    AsyncHttpClientUtil.onFailure(ChooseAgencyActivity.this,statusCode);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 新增一级服务标签
     * @param firstTagName
     * @param selectMemberList
     * @param selectCoverageList
     * @param selectAgencyList
     */
    private void requestAddServiceTagTask(String firstTagName,ArrayList<String> selectMemberList,
                                          ArrayList<String> selectCoverageList,ArrayList<String> selectAgencyList){
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            if(CacheUtil.getInstance().isLogin()){
                client.addHeader("Token",CacheUtil.getInstance().getExtToken());
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("firstSrvTagName",firstTagName);
            JSONArray agencyArray = new JSONArray();
            for (String agencyId : selectAgencyList){
                agencyArray.put(agencyId);
            }
            jsonObject.put("roleids",agencyArray);
            JSONArray memberArray = new JSONArray();
            for (String memberId : selectMemberList){
                memberArray.put(memberId);
            }
            jsonObject.put("ownerids",memberArray);
            JSONArray coverageArray = new JSONArray();
            for (String coverageId : selectCoverageList){
                coverageArray.put(coverageId);
            }
            jsonObject.put("locids",coverageArray);
            StringEntity stringEntity = new StringEntity(jsonObject.toString(),"UTF-8");
            String url = ProtocolUtil.getAddFirstTagUrl();
            client.post(ChooseAgencyActivity.this,url, stringEntity, "application/json", new AsyncHttpResponseHandler(){
                public void onStart(){
                    super.onStart();
                    DialogUtil.getInstance().showAvatarProgressDialog(ChooseAgencyActivity.this,"");
                }

                public void onFinish(){
                    super.onFinish();
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String response = new String(responseBody,"utf-8");
                        BaseResponse baseResponse = new Gson().fromJson(response,BaseResponse.class);
                        if(null != baseResponse){
                            if(baseResponse.getRes() == 0){
                                DialogUtil.getInstance().showCustomToast(ChooseAgencyActivity.this,"添加一级标签成功", Gravity.CENTER);
                                finish();
                            }else{
                                Toast.makeText(ChooseAgencyActivity.this,baseResponse.getResDesc(),Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    AsyncHttpClientUtil.onFailure(ChooseAgencyActivity.this,statusCode);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
