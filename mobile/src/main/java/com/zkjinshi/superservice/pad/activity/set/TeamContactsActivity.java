package com.zkjinshi.superservice.pad.activity.set;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.zkjinshi.superservice.pad.response.BaseFornaxResponse;
import com.zkjinshi.superservice.pad.utils.AccessControlUtil;
import com.zkjinshi.superservice.pad.view.AutoSideBar;
import com.zkjinshi.superservice.pad.R;
import com.zkjinshi.superservice.pad.activity.chat.single.ChatActivity;
import com.zkjinshi.superservice.pad.adapter.TeamContactsAdapter;

import com.zkjinshi.superservice.pad.base.BaseAppCompatActivity;
import com.zkjinshi.superservice.pad.manager.SSOManager;
import com.zkjinshi.superservice.pad.response.GetEmployeesResponse;
import com.zkjinshi.superservice.pad.sqlite.ShopEmployeeDBUtil;
import com.zkjinshi.superservice.pad.utils.AsyncHttpClientUtil;
import com.zkjinshi.superservice.pad.utils.CacheUtil;
import com.zkjinshi.superservice.pad.utils.Constants;
import com.zkjinshi.superservice.pad.utils.ProtocolUtil;
import com.zkjinshi.superservice.pad.vo.EmployeeVo;
import com.zkjinshi.superservice.pad.vo.IdentityType;
import com.zkjinshi.superservice.pad.vo.PayloadVo;

import org.json.JSONObject;

import java.util.ArrayList;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import static com.zkjinshi.superservice.pad.activity.set.ClientActivity.DELETE_MENU_ITEM;

/**
 * 团队联系人显示界面
 * 开发者：vincent
 * 日期：2015/10/9
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class TeamContactsActivity extends BaseAppCompatActivity {

    public static final int ADD_REQUEST_CODE = 1;

    private Toolbar         mToolbar;
    private TextView        mTvCenterTitle;
    private SwipeMenuListView mRvTeamContacts;
    private RelativeLayout  mRlSideBar;
    private TextView        mTvDialog;
    private AutoSideBar mAutoSideBar;

    private TeamContactsAdapter mTeamContactAdapter;
    private IdentityType mUserType;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_contacts);
        mContext = this;

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(R.mipmap.ic_fanhui);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTvCenterTitle = (TextView) findViewById(R.id.tv_center_title);
        mTvCenterTitle.setText(getString(R.string.team));

        mRvTeamContacts = (SwipeMenuListView)     findViewById(R.id.rcv_team_contacts);
        mRlSideBar      = (RelativeLayout)   findViewById(R.id.rl_side_bar);
        mTvDialog       = (TextView)         findViewById(R.id.tv_dialog);
        mAutoSideBar    = new AutoSideBar(TeamContactsActivity.this);

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                DisplayUtil.dip2px(TeamContactsActivity.this, 30),
                ViewGroup.LayoutParams.MATCH_PARENT);
        mAutoSideBar.setTextView(mTvDialog);
        mAutoSideBar.setLayoutParams(layoutParams);
    }

    private void initData() {
        mUserType = CacheUtil.getInstance().getLoginIdentity();

        if(AccessControlUtil.isShowView(AccessControlUtil.DELEMPLOYEE)){
            SwipeMenuCreator creator = new SwipeMenuCreator() {

                @Override
                public void create(SwipeMenu menu) {
                    SwipeMenuItem openItem = new SwipeMenuItem(
                            getApplicationContext());
                    openItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                            0x3F, 0x25)));
                    openItem.setWidth(DisplayUtil.dip2px(TeamContactsActivity.this,90));
                    openItem.setTitle("删除");
                    openItem.setTitleSize(16);
                    openItem.setTitleColor(Color.WHITE);
                    menu.addMenuItem(openItem);
                }
            };
            mRvTeamContacts.setMenuCreator(creator);
        }
        mTeamContactAdapter = new TeamContactsAdapter(TeamContactsActivity.this,new ArrayList<EmployeeVo>());
        mRvTeamContacts.setAdapter(mTeamContactAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getEmployeesList();
    }




    private void getEmployeesList(){
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token",CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.getEmpployeeList();
            client.get(mContext,url, stringEntity, "application/json", new AsyncHttpResponseHandler(){
                public void onStart(){
                    super.onStart();
                    DialogUtil.getInstance().showAvatarProgressDialog(mContext,"");
                }

                public void onFinish(){
                    super.onFinish();
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String response = new String(responseBody,"utf-8");
                        GetEmployeesResponse getEmployeesResponse = new Gson().fromJson(response,GetEmployeesResponse.class);
                        if(getEmployeesResponse == null){
                            return;
                        }
                        if(getEmployeesResponse.getRes() == 0){
                            ArrayList<EmployeeVo> employeeVos = getEmployeesResponse.getData();
                            if (null != employeeVos && !employeeVos.isEmpty()) {
                                ShopEmployeeDBUtil.getInstance().deleteAllShopEmployee();
                                ShopEmployeeDBUtil.getInstance().batchAddShopEmployees(employeeVos);
                                employeeVos = ShopEmployeeDBUtil.getInstance().queryAllExceptUser(CacheUtil.getInstance().getUserId());
                                List<String> strLetters = new ArrayList<>();//首字母显示数组

                                //获取部门首字母进行排序
                                for (EmployeeVo employeeVo : employeeVos) {
                                    String roleName = employeeVo.getRolename();
                                    String sortLetter = null;

                                    if (TextUtils.isEmpty(roleName)) {
                                        sortLetter = "#";
                                        employeeVo.setRolename(sortLetter);
                                    } else {
                                        sortLetter = roleName.substring(0, 1);
                                    }

                                    //部门分类并消除相同部门
                                    if (!TextUtils.isEmpty(sortLetter) && !strLetters.contains(sortLetter)) {
                                        strLetters.add(sortLetter);
                                    }

                                }

                                String[] sortArray = strLetters.toArray(new String[strLetters.size()]);
                                if (sortArray.length > 0) {
                                    mAutoSideBar.setSortArray(sortArray);
                                    //移除之前设置
                                    mRlSideBar.removeAllViews();
                                    mRlSideBar.addView(mAutoSideBar);
                                }
                                mTeamContactAdapter.setData(employeeVos);
                            }
                        }else{
                            Toast.makeText(mContext,getEmployeesResponse.getResDesc(),Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    AsyncHttpClientUtil.onFailure(mContext,statusCode);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initListener() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeamContactsActivity.this.finish();
            }
        });

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(android.view.MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_team_jia:
                        Intent intent = new Intent(TeamContactsActivity.this, EmployeeAddActivity.class);
                        startActivityForResult(intent, ADD_REQUEST_CODE);
                        overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
                        break;

                    case R.id.menu_team_edit:
                        Intent teamEdit = new Intent(TeamContactsActivity.this, TeamEditActivity.class);
                        TeamContactsActivity.this.startActivity(teamEdit);
                        break;
                }
                return true;
            }
        });

        mAutoSideBar.setOnTouchingLetterChangedListener(new AutoSideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = mTeamContactAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mRvTeamContacts.setSelection(position);
                }
            }
        });

        mRvTeamContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EmployeeVo employeeVo = mTeamContactAdapter.mDatas.get(position);
                String userId = employeeVo.getUserid();
                String toName = employeeVo.getUsername();
                String shopName = CacheUtil.getInstance().getShopFullName();
                Intent intent = new Intent(TeamContactsActivity.this, ChatActivity.class);
                intent.putExtra(com.zkjinshi.superservice.pad.utils.Constants.EXTRA_USER_ID, userId);
                PayloadVo payloadVo = SSOManager.getInstance().decodeToken(CacheUtil.getInstance().getExtToken());
                if (!TextUtils.isEmpty(payloadVo.getShopid())) {
                    intent.putExtra(com.zkjinshi.superservice.pad.utils.Constants.EXTRA_SHOP_ID,payloadVo.getShopid());
                }
                intent.putExtra(com.zkjinshi.superservice.pad.utils.Constants.EXTRA_SHOP_NAME, shopName);
                if(!TextUtils.isEmpty(toName)){
                    intent.putExtra(com.zkjinshi.superservice.pad.utils.Constants.EXTRA_TO_NAME, toName);
                }
                intent.putExtra(com.zkjinshi.superservice.pad.utils.Constants.EXTRA_FROM_NAME, CacheUtil.getInstance().getUserName());
                startActivity(intent);
            }
        });

        mRvTeamContacts.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case DELETE_MENU_ITEM://打开删除按钮
                        EmployeeVo employeeVo = mTeamContactAdapter.mDatas.get(position);
                        requestDeleteEmployeeTask(employeeVo);
                        break;
                }
                return false;
            }
        });

        if(AccessControlUtil.isShowView(AccessControlUtil.DELEMPLOYEE)){
            mRvTeamContacts.setOnMenuStateChangeListener(new SwipeMenuListView.OnMenuStateChangeListener() {
                @Override
                public void onMenuOpen(int position) {
                    //Toast.makeText(TeamContactsActivity.this,"onMenuOpen",Toast.LENGTH_SHORT).show();
                    mAutoSideBar.setVisibility(View.GONE);
                }

                @Override
                public void onMenuClose(int position) {
                    //Toast.makeText(TeamContactsActivity.this,"onMenuClose",Toast.LENGTH_SHORT).show();
                    mAutoSideBar.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    //删除员工
    private void requestDeleteEmployeeTask(EmployeeVo employeeVo) {
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token", CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString(),"UTF-8");
            String url = ProtocolUtil.deleteEmployee(employeeVo.getUserid());
            client.delete(mContext,url, stringEntity, "application/json", new AsyncHttpResponseHandler(){
                public void onStart(){
                    DialogUtil.getInstance().showAvatarProgressDialog(mContext,"");
                }

                public void onFinish(){
                    DialogUtil.getInstance().cancelProgressDialog();
                    mAutoSideBar.setVisibility(View.VISIBLE);
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String response = new String(responseBody,"utf-8");
                        BaseFornaxResponse baseFornaxResponse = new Gson().fromJson(response,BaseFornaxResponse.class);
                        if(baseFornaxResponse.getRes() == 0){
                            getEmployeesList();
                        }else{
                            Toast.makeText(mContext,baseFornaxResponse.getResDesc(),Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    Toast.makeText(mContext,"API 错误："+statusCode,Toast.LENGTH_SHORT).show();
                    AsyncHttpClientUtil.onFailure(mContext,statusCode);
                }
            });
        }catch (Exception e){
            Toast.makeText(mContext,"json解析错误",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            if (ADD_REQUEST_CODE == requestCode) {

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(AccessControlUtil.isShowView(AccessControlUtil.BTNADDMEMBER)){
            getMenuInflater().inflate(R.menu.menu_team_for_business, menu);
        }else {
            //getMenuInflater().inflate(R.menu.menu_team_for_waiter, menu);
        }
        return true;
    }

}
