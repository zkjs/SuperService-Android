package com.zkjinshi.superservice.activity.set;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.TeamEditContactsAdapter;
import com.zkjinshi.superservice.sqlite.ShopEmployeeDBUtil;
import com.zkjinshi.superservice.vo.ShopEmployeeVo;

import java.util.List;

/**
 * 团队编辑页面
 * 开发者：vincent
 * 日期：2015/10/12
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class TeamEditActivity extends Activity{

    private String        mShopID;
    private ImageButton   mIbtnBack;
    private TextView      mTvTitle;
    private RecyclerView  mRcvTeamContacts;
    private LinearLayoutManager mLayoutManager;
    private TeamEditContactsAdapter mContactsAdapter;

    public TeamEditActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_edit);
        
        initView();
        initData();
        initListener();
    }

    private void initView() {
        mIbtnBack = (ImageButton) findViewById(R.id.ibtn_back);
        mTvTitle  = (TextView)    findViewById(R.id.tv_title);
        mRcvTeamContacts  = (RecyclerView) findViewById(R.id.rcv_team_contacts);
    }

    private void initData() {
        mShopID = getIntent().getStringExtra("shop_id");
        mTvTitle.setText(getString(R.string.team_edit));

        mRcvTeamContacts.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRcvTeamContacts.setLayoutManager(mLayoutManager);
        List<ShopEmployeeVo> shopEmployeeVos = ShopEmployeeDBUtil.getInstance().queryAll();
        if(null != shopEmployeeVos && !shopEmployeeVos.isEmpty()){
            mContactsAdapter = new TeamEditContactsAdapter(TeamEditActivity.this, shopEmployeeVos);
            mRcvTeamContacts.setAdapter(mContactsAdapter);
        }

    }

    private void initListener() {
        mIbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeamEditActivity.this.finish();
            }
        });
    }

}
