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
import com.zkjinshi.superservice.activity.label.GuestInfoActivity;
import com.zkjinshi.superservice.adapter.VipUserAdapter;
import com.zkjinshi.superservice.base.BaseAppCompatActivity;
import com.zkjinshi.superservice.listener.OnRefreshListener;
import com.zkjinshi.superservice.menu.vo.MenuItem;
import com.zkjinshi.superservice.response.BaseResponse;
import com.zkjinshi.superservice.response.WhiteUserListResponse;
import com.zkjinshi.superservice.utils.AccessControlUtil;
import com.zkjinshi.superservice.utils.AsyncHttpClientUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.view.SwipeRefreshListView;
import com.zkjinshi.superservice.vo.WhiteUserVo;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * 会员管理列表
 * 开发者：JimmyZhang
 * 日期：2016/05/23
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientActivity extends BaseAppCompatActivity {

    public static final int DELETE_MENU_ITEM = 0;
    public static int PAGE_NO = 0;
    public static final int PAGE_SIZE = 10;
    private boolean isLoadMoreAble = true;

    private Toolbar toolbar;
    private TextView centerTitleTv;
    private SwipeRefreshListView swipeRefreshListView;
    private ArrayList<WhiteUserVo> whiteUserList,requestWhiteUserList;
    private VipUserAdapter vipUserAdapter;
    private HashMap<String,Boolean> selectMap = new HashMap<String, Boolean>();
    private TextView noResultTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.mipmap.ic_fanhui);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        noResultTv = (TextView) findViewById(R.id.member_no_result);
        centerTitleTv = (TextView) findViewById(R.id.tv_center_title);
        centerTitleTv.setText(getString(R.string.my_member));
        swipeRefreshListView = (SwipeRefreshListView) findViewById(R.id.member_list_view);
    }

    private void initData() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                deleteItem.setWidth(DisplayUtil.dip2px(ClientActivity.this,90));
                deleteItem.setTitle("删除");
                deleteItem.setTitleSize(16);
                deleteItem.setTitleColor(Color.WHITE);
                if(AccessControlUtil.isShowView(AccessControlUtil.DELMEMBER)){
                    menu.addMenuItem(deleteItem);
                }
            }
        };
        swipeRefreshListView.setMenuCreator(creator);
        vipUserAdapter = new VipUserAdapter(this, whiteUserList);
        swipeRefreshListView.setAdapter(vipUserAdapter);
        vipUserAdapter.setSelectMap(selectMap);

    }

    private void initListener() {

        swipeRefreshListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case DELETE_MENU_ITEM://打开删除按钮

                        WhiteUserVo whiteUserVo = (WhiteUserVo) vipUserAdapter.getItem(position);
                        String userId = whiteUserVo.getUserid();
                        String phone = whiteUserVo.getPhone();
                        requestDeleteWhiteUserTask(userId,phone);

                        break;
                }
                return false;
            }
        });

        /**
         * 返回
         */
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientActivity.this.finish();
            }
        });

        /**
         * 菜单
         */
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(android.view.MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.menu_client_jia:
                        Intent clientSelect = new Intent(ClientActivity.this, AddWhiteUserActivity.class);
                        ClientActivity.this.startActivity(clientSelect);
                        break;
                }
                return true;
            }
        });

        /**
         * 下拉刷新、上拉加载、单选item
         */
        swipeRefreshListView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefreshing() {
                whiteUserList = new ArrayList<WhiteUserVo>();
                PAGE_NO = 0;
                requestWhiteUserListTask(true);
            }

            @Override
            public void onLoadingMore() {
                if(isLoadMoreAble){
                    isLoadMoreAble = false;
                    requestWhiteUserListTask(false);
                }
            }

            @Override
            public void implOnItemClickListener(AdapterView<?> parent, View view, int position, long id) {
                Boolean isSelect = true;
                WhiteUserVo userVo = (WhiteUserVo) vipUserAdapter.getItem(position-1);
                String userId = userVo.getUserid();
                /*if (selectMap != null
                        && selectMap.containsKey(userId)){
                    isSelect = !selectMap.get(userId);
                }
                selectMap.put(userId, isSelect);
                vipUserAdapter.setSelectMap(selectMap);
                if(null != whiteUserList && !whiteUserList.isEmpty() && position == whiteUserList.size()){
                    swipeRefreshListView.setSelection(parent.getCount()-1);

                }*/
                Intent intent = new Intent(ClientActivity.this,GuestInfoActivity.class);
                intent.putExtra("whiteUserVo",userVo);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_client, menu);
        for(int i = 0 ; i < menu.size() ; i++){
            if(AccessControlUtil.isShowView(AccessControlUtil.ADDMEMBER)){
                menu.getItem(i).setVisible(true);
            }else {
                menu.getItem(i).setVisible(false);
            }
        }
        return true;
    }

    /**
     * 删除白名单用户
     * @param userid
     * @param phone
     */
    private void requestDeleteWhiteUserTask(String userid,String phone){
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            if(CacheUtil.getInstance().isLogin()){
                client.addHeader("Token",CacheUtil.getInstance().getExtToken());
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userid", userid);
            jsonObject.put("phone", phone);
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            final String url = ProtocolUtil.getDeleteWhiteUserUrl();
            client.delete(ClientActivity.this,url, stringEntity, "application/json", new AsyncHttpResponseHandler(){
                public void onStart(){
                    super.onStart();
                    DialogUtil.getInstance().showAvatarProgressDialog(ClientActivity.this,"");
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
                                PAGE_NO = 0;
                                requestWhiteUserListTask(true);
                            } else {
                                String resultMsg = deleteWhiteUserResponse.getResDesc();
                                if (!TextUtils.isEmpty(resultMsg)) {
                                    DialogUtil.getInstance().showCustomToast(ClientActivity.this, resultMsg, Gravity.CENTER);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    AsyncHttpClientUtil.onFailure(ClientActivity.this,statusCode);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 获取白名单列表
     * @param isRefresh
     */
    private void requestWhiteUserListTask(final boolean isRefresh) {
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token",CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.getWhiteUserListUrl(""+PAGE_NO,""+PAGE_SIZE);
            client.get(ClientActivity.this,url, stringEntity, "application/json", new AsyncHttpResponseHandler(){
                public void onStart(){
                    super.onStart();
                    DialogUtil.getInstance().showAvatarProgressDialog(ClientActivity.this,"");
                }

                public void onFinish(){
                    super.onFinish();
                    DialogUtil.getInstance().cancelProgressDialog();
                    isLoadMoreAble = true;
                    if (null != swipeRefreshListView) {
                        swipeRefreshListView.refreshFinish();
                    }
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String response = new String(responseBody,"utf-8");
                        WhiteUserListResponse whiteUserListResponse = new Gson().fromJson(response,WhiteUserListResponse.class);
                        int resultFlag = whiteUserListResponse.getRes();
                        if(0 == resultFlag || 30001 == resultFlag){
                            requestWhiteUserList = whiteUserListResponse.getData();
                            if(isRefresh){
                                whiteUserList = requestWhiteUserList;
                            }else {
                                whiteUserList.addAll(requestWhiteUserList);
                            }
                            if(null != requestWhiteUserList && !requestWhiteUserList.isEmpty()){
                                PAGE_NO++;
                            }else {
                                if(!isRefresh){
                                    DialogUtil.getInstance().showCustomToast(ClientActivity.this,"再无更多数据", Gravity.CENTER);
                                }
                            }
                            vipUserAdapter.setVipUserList(whiteUserList);
                            if(30001 == resultFlag){
                                swipeRefreshListView.setEmptyView(noResultTv);
                            }
                        }else {
                            String errorMsg = whiteUserListResponse.getResDesc();
                            if(!TextUtils.isEmpty(errorMsg)){
                                DialogUtil.getInstance().showCustomToast(ClientActivity.this,errorMsg, Gravity.CENTER);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    isLoadMoreAble = true;
                    if (null != swipeRefreshListView) {
                        swipeRefreshListView.refreshFinish();
                    }
                    AsyncHttpClientUtil.onFailure(ClientActivity.this,statusCode);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PAGE_NO = 0;
        requestWhiteUserListTask(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
