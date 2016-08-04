package com.zkjinshi.superservice.pad.activity.set;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.pad.R;
import com.zkjinshi.superservice.pad.adapter.TeamMemberAdapter;
import com.zkjinshi.superservice.pad.base.BaseAppCompatActivity;
import com.zkjinshi.superservice.pad.response.GetEmployeesResponse;
import com.zkjinshi.superservice.pad.sqlite.ShopEmployeeDBUtil;
import com.zkjinshi.superservice.pad.utils.AsyncHttpClientUtil;
import com.zkjinshi.superservice.pad.utils.CacheUtil;
import com.zkjinshi.superservice.pad.utils.Constants;
import com.zkjinshi.superservice.pad.utils.ListViewUtil;
import com.zkjinshi.superservice.pad.utils.ProtocolUtil;
import com.zkjinshi.superservice.pad.vo.EmployeeVo;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * 添加服务标签Activity
 * 开发者：jimmyzhang
 * 日期：16/6/22
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class AddServiceTagActivity extends BaseAppCompatActivity {

    private Toolbar toolbar;
    private TextView titleIv;
    private EditText inputSeviceTagNameEtv;
    private ListView memberslistView;
    private TeamMemberAdapter teamMemberAdapter;
    private ArrayList<EmployeeVo> teamMemberList;
    private ArrayList<String> selectMemberList;
    private Menu menu;
    private Map<String, Boolean> selectMap;

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        titleIv = (TextView) findViewById(R.id.tv_center_title);
        inputSeviceTagNameEtv = (EditText)findViewById(R.id.add_service_tag_etv);
        memberslistView = (ListView)findViewById(R.id.list_view_team_members);
    }

    private void initData(){
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.mipmap.ic_fanhui);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        titleIv.setText("新增服务标签");
        teamMemberAdapter = new TeamMemberAdapter(this,teamMemberList);
        selectMap = new HashMap<String,Boolean>();
        selectMemberList = new ArrayList<String>();
        memberslistView.setAdapter(teamMemberAdapter);
        requestTeamMemberListTask();
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
                    case R.id.menu_add_service_tag_next://下一步
                        String firstTagName = inputSeviceTagNameEtv.getText().toString().trim();
                        Intent intent = new Intent(AddServiceTagActivity.this,ChooseCoverageActivity.class);
                        intent.putExtra("firstTagName",firstTagName);
                        intent.putExtra("selectMemberList",selectMemberList);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        break;
                }
                return true;
            }
        });

        memberslistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EmployeeVo employeeVo = (EmployeeVo) parent.getAdapter().getItem(position);
                String empId = employeeVo.getUserid();
                if (selectMap != null
                        && selectMap.containsKey(empId)
                        && selectMap.get(empId)) {
                    selectMap.put(empId, false);
                    selectMemberList.remove(empId);
                } else {
                    selectMap.put(empId, true);
                    selectMemberList.add(empId);
                }
                teamMemberAdapter.setSelectMap(selectMap);
                String firstTagNameStr = inputSeviceTagNameEtv.getText().toString();
                if(!TextUtils.isEmpty(firstTagNameStr)&& null != selectMemberList && !selectMemberList.isEmpty()){
                    showMenu(menu);
                }else {
                    hiddenMenu(menu);
                }
            }
        });

        inputSeviceTagNameEtv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0 && null != selectMemberList && !selectMemberList.isEmpty()) {//显示menu
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
        setContentView(R.layout.activity_add_service_tag);
        initView();
        initData();
        initListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_service_tag, menu);
        this.menu = menu;
        return true;
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

    /**
     * 获取团队成员列表
     */
    private void requestTeamMemberListTask(){
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token", CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.getEmpployeeList();
            client.get(AddServiceTagActivity.this,url, stringEntity, "application/json", new AsyncHttpResponseHandler(){
                public void onStart(){
                    super.onStart();
                    DialogUtil.getInstance().showAvatarProgressDialog(AddServiceTagActivity.this,"");
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
                            teamMemberList = getEmployeesResponse.getData();
                            if (null != teamMemberList && !teamMemberList.isEmpty()) {
                                ShopEmployeeDBUtil.getInstance().deleteAllShopEmployee();
                                ShopEmployeeDBUtil.getInstance().batchAddShopEmployees(teamMemberList);
                                teamMemberList = ShopEmployeeDBUtil.getInstance().queryAllExceptUser();
                                List<String> strLetters = new ArrayList<>();//首字母显示数组

                                //获取部门首字母进行排序
                                for (EmployeeVo employeeVo : teamMemberList) {
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
                                teamMemberAdapter.setData(teamMemberList);
                                ListViewUtil.setListViewHeightBasedOnChildren(memberslistView);
                            }
                        }else{
                            Toast.makeText(AddServiceTagActivity.this,getEmployeesResponse.getResDesc(),Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    AsyncHttpClientUtil.onFailure(AddServiceTagActivity.this,statusCode);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
