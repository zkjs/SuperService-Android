package com.zkjinshi.superservice.activity.set;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.base.BaseAppCompatActivity;

/**
 * 选中服务区域Activity
 * 开发者：jimmyzhang
 * 日期：16/6/22
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ChooseCoverageActivity extends BaseAppCompatActivity {

    private Toolbar toolbar;
    private TextView titleIv;
    private EditText inputSeviceTagNameEtv;
    private ListView memberslistView;
    private Menu menu;

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

                        break;
                }
                return true;
            }
        });

        memberslistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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
}
