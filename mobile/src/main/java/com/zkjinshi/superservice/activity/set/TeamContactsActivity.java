package com.zkjinshi.superservice.activity.set;

import android.content.Context;
import android.content.Intent;
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

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.chat.single.ChatActivity;
import com.zkjinshi.superservice.adapter.TeamContactsAdapter;

import com.zkjinshi.superservice.manager.SSOManager;
import com.zkjinshi.superservice.response.GetEmployeesResponse;
import com.zkjinshi.superservice.sqlite.ShopEmployeeDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.view.AutoSideBar;
import com.zkjinshi.superservice.vo.EmployeeVo;
import com.zkjinshi.superservice.vo.IdentityType;
import com.zkjinshi.superservice.vo.PayloadVo;

import org.json.JSONObject;

import java.util.ArrayList;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * 团队联系人显示界面
 * 开发者：vincent
 * 日期：2015/10/9
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class TeamContactsActivity extends AppCompatActivity{

    public static final int ADD_REQUEST_CODE = 1;

    private Toolbar         mToolbar;
    private TextView        mTvCenterTitle;
    private ListView        mRvTeamContacts;
    private RelativeLayout  mRlSideBar;
    private TextView        mTvDialog;
    private AutoSideBar     mAutoSideBar;

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

        mRvTeamContacts = (ListView)     findViewById(R.id.rcv_team_contacts);
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
                        Toast.makeText(mContext,"解析异常",Toast.LENGTH_SHORT).show();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    Toast.makeText(mContext,"API 错误："+statusCode,Toast.LENGTH_SHORT).show();
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
                intent.putExtra(com.zkjinshi.superservice.utils.Constants.EXTRA_USER_ID, userId);
                PayloadVo payloadVo = SSOManager.getInstance().decodeToken(CacheUtil.getInstance().getExtToken());
                if (!TextUtils.isEmpty(payloadVo.getShopid())) {
                    intent.putExtra(com.zkjinshi.superservice.utils.Constants.EXTRA_SHOP_ID,payloadVo.getShopid());
                }
                intent.putExtra(com.zkjinshi.superservice.utils.Constants.EXTRA_SHOP_NAME, shopName);
                if(!TextUtils.isEmpty(toName)){
                    intent.putExtra(com.zkjinshi.superservice.utils.Constants.EXTRA_TO_NAME, toName);
                }
                intent.putExtra(com.zkjinshi.superservice.utils.Constants.EXTRA_FROM_NAME, CacheUtil.getInstance().getUserName());
                startActivity(intent);
            }
        });
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
        if(mUserType == IdentityType.BUSINESS){
            getMenuInflater().inflate(R.menu.menu_team_for_business, menu);
        }else {
            //getMenuInflater().inflate(R.menu.menu_team_for_waiter, menu);
        }
        return true;
    }

}
