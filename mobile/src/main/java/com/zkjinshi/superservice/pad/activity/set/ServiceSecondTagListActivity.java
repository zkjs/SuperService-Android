package com.zkjinshi.superservice.pad.activity.set;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.superservice.pad.R;
import com.zkjinshi.superservice.pad.adapter.ServiceSecondTagAdapter;
import com.zkjinshi.superservice.pad.base.BaseAppCompatActivity;
import com.zkjinshi.superservice.pad.response.AddSecondTagResponse;
import com.zkjinshi.superservice.pad.response.BaseResponse;
import com.zkjinshi.superservice.pad.utils.AccessControlUtil;
import com.zkjinshi.superservice.pad.utils.AsyncHttpClientUtil;
import com.zkjinshi.superservice.pad.utils.CacheUtil;
import com.zkjinshi.superservice.pad.utils.Constants;
import com.zkjinshi.superservice.pad.utils.ProtocolUtil;
import com.zkjinshi.superservice.pad.vo.SecondServiceTagVo;
import com.zkjinshi.superservice.pad.vo.ServiceTagVo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * 服务标签列表Activity
 * 开发者：jimmyzhang
 * 日期：16/6/22
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ServiceSecondTagListActivity extends BaseAppCompatActivity {

    public static final int DELETE_MENU_ITEM = 0;

    private Toolbar toolbar;
    private TextView titleIv;
    private TextView emptyTv;
    private RelativeLayout emptyLayout;
    private SwipeMenuListView tagListView;
    private ArrayList<SecondServiceTagVo> secondServiceTagList = new ArrayList<SecondServiceTagVo>();
    private ServiceTagVo serviceTagVo;
    private String firstTagId;
    private ServiceSecondTagAdapter serviceSecondTagAdapter;
    private EditText inputSecondTagNameEtv;
    private Menu menu;

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        titleIv = (TextView) findViewById(R.id.tv_center_title);
        tagListView = (SwipeMenuListView)findViewById(R.id.second_tag_list_view);
        emptyTv = (TextView)findViewById(R.id.empty_tips);
        emptyLayout = (RelativeLayout)findViewById(R.id.second_tag_list_empty_layout);
        inputSecondTagNameEtv = (EditText)findViewById(R.id.add_second_tag_etv);
    }

    private void initData(){
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.mipmap.ic_fanhui);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                deleteItem.setWidth(DisplayUtil.dip2px(ServiceSecondTagListActivity.this,90));
                deleteItem.setTitle("删除");
                deleteItem.setTitleSize(16);
                deleteItem.setTitleColor(Color.WHITE);
                if(AccessControlUtil.isShowView(AccessControlUtil.DELMEMBER)){
                    menu.addMenuItem(deleteItem);
                }
            }
        };
        tagListView.setMenuCreator(swipeMenuCreator);
        if(null != getIntent() && null != getIntent().getSerializableExtra("serviceTagVo")){
            serviceTagVo = (ServiceTagVo) getIntent().getSerializableExtra("serviceTagVo");
            secondServiceTagList = serviceTagVo.getSecondSrvTag();
        }
        serviceSecondTagAdapter = new ServiceSecondTagAdapter(this,secondServiceTagList);
        tagListView.setAdapter(serviceSecondTagAdapter);
        if(null != serviceTagVo){
            String titleNameStr =  serviceTagVo.getFirstSrvTagName();
            if(TextUtils.isEmpty(titleNameStr)){
                titleIv.setText(titleNameStr);
            }
            firstTagId = serviceTagVo.getFirstSrvTagId();
        }
        emptyTv.setText("暂无二级服务标签");
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
                    case R.id.menu_finish_add_second_service_tag://添加二级标签
                        String secondTagNameStr = inputSecondTagNameEtv.getText().toString().trim();
                        if(!TextUtils.isEmpty(secondTagNameStr)){
                            requestAddSecondTagTask(firstTagId,secondTagNameStr);
                        }
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
                        SecondServiceTagVo serviceTagVo = secondServiceTagList.get(position);
                        String secondTagId = serviceTagVo.getSecondSrvTagId();
                        requestDeleteSecondTagTask(secondTagId);
                        break;
                }
                return false;
            }
        });

        tagListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        inputSecondTagNameEtv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {//显示menu
                    showMenu(menu);
                } else {//隐藏menu
                    hiddenMenu(menu);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_second_tag_list);
        initView();
        initData();
        initListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_service_second_tag_list, menu);
        this.menu = menu;
        return true;
    }

    /**
     * 本地删除二级服务标签
     * @param secondTagId
     */
    private void deleteSecondTag(String secondTagId){
        if(null != secondServiceTagList && !secondServiceTagList.isEmpty()){
            Iterator<SecondServiceTagVo> iterator = secondServiceTagList.iterator();
            while(iterator.hasNext()){
                SecondServiceTagVo secondServiceTagVo = iterator.next();
                if(secondServiceTagVo.getSecondSrvTagId().equals(secondTagId)){
                    iterator.remove();
                }
            }
        }

        serviceSecondTagAdapter.setSecondTagList(secondServiceTagList);
    }

    /**
     * 本地添加二级服务标签
     * @param serviceTagVo
     */
    private void addSecondTag(SecondServiceTagVo serviceTagVo){
        if(null == secondServiceTagList){
            secondServiceTagList = new ArrayList<SecondServiceTagVo>();
        }
        secondServiceTagList.add(0,serviceTagVo);
        serviceSecondTagAdapter.setSecondTagList(secondServiceTagList);
    }

    /**
     * 添加二级标签
     */
    private void requestAddSecondTagTask(String firstTagId,String secondTagName){
        try {
            AsyncHttpClient httpClient = new AsyncHttpClient();
            httpClient.setTimeout(Constants.OVERTIMEOUT);
            httpClient.addHeader("Content-Type","application/json; charset=UTF-8");
            httpClient.addHeader("Token", CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("firstSrvTagId",firstTagId);
            jsonObject.put("secondSrvTagName",secondTagName);
            StringEntity stringEntity = new StringEntity(jsonObject.toString(),"UTF-8");
            String requestUrl = ProtocolUtil.getAddSecondTagUrl();
            httpClient.post(ServiceSecondTagListActivity.this,requestUrl,stringEntity,"application/json",new JsonHttpResponseHandler(){

                @Override
                public void onStart() {
                    super.onStart();
                    DialogUtil.getInstance().showAvatarProgressDialog(ServiceSecondTagListActivity.this,"");
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    if(null != response){
                        AddSecondTagResponse addSecondTagResponse = new Gson().fromJson(response.toString(),AddSecondTagResponse.class);
                        if(null != addSecondTagResponse){
                            int resultCode = addSecondTagResponse.getRes();
                            if(0 == resultCode){
                                SecondServiceTagVo secondTagVo = addSecondTagResponse.getData();
                                if(null != secondTagVo){
                                    addSecondTag(secondTagVo);
                                }
                            }else {
                                String resultMsg = addSecondTagResponse.getResDesc();
                                if(!TextUtils.isEmpty(resultMsg)){
                                    DialogUtil.getInstance().showCustomToast(ServiceSecondTagListActivity.this,""+resultMsg,Gravity.CENTER);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除二级标签
     * @param secondTagId
     */
    private void requestDeleteSecondTagTask(final String secondTagId){
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            if(CacheUtil.getInstance().isLogin()){
                client.addHeader("Token",CacheUtil.getInstance().getExtToken());
            }
            final String url = ProtocolUtil.getDeleteSecondTagUrl(secondTagId);
            client.delete(ServiceSecondTagListActivity.this,url, new AsyncHttpResponseHandler(){
                public void onStart(){
                    super.onStart();
                    DialogUtil.getInstance().showAvatarProgressDialog(ServiceSecondTagListActivity.this,"");
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
                            if (0 == resultCode) {//删除服务标签,更新列表
                                deleteSecondTag(secondTagId);
                            } else {
                                String resultMsg = deleteWhiteUserResponse.getResDesc();
                                if (!TextUtils.isEmpty(resultMsg)) {
                                    DialogUtil.getInstance().showCustomToast(ServiceSecondTagListActivity.this, resultMsg, Gravity.CENTER);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    AsyncHttpClientUtil.onFailure(ServiceSecondTagListActivity.this,statusCode);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 显示菜单
     * @param menu
     */
    private void showMenu(Menu menu){
        if(null != menu){
            for(int i = 0 ; i < menu.size() ; i++){
                menu.getItem(i).setVisible(true);
            }
        }
    }

    /**
     * 隐藏菜单
     * @param menu
     */
    private void hiddenMenu(Menu menu){
        if(null != menu){
            for(int i = 0 ; i < menu.size() ; i++){
                menu.getItem(i).setVisible(false);
            }
        }
    }
}
