package com.zkjinshi.superservice.activity.set;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.zkjinshi.superservice.R;

/**
 * 开发者：vincent
 * 日期：2015/10/13
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class TeamEmployeeAddActivity extends AppCompatActivity{

    private Toolbar mToolbar;

    private Button mBtnAddManual;
    private Button mBtnBactchImport;
    private RecyclerView mRcvContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_employee_add);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.add_team_employee));
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setNavigationIcon(R.drawable.ic_fanhui);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBtnAddManual    = (Button)findViewById(R.id.btn_add_by_manual);
        mBtnBactchImport = (Button)findViewById(R.id.btn_batch_import_data);
        mRcvContacts     = (RecyclerView)findViewById(R.id.rcv_contacts);
    }

    private void initData() {
        
    }

    private void initListener() {
        mBtnAddManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mBtnBactchImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: 批量导入excel操作
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_team_emp_add, menu);
        return true;
    }

}
