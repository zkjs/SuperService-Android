package com.zkjinshi.superservice.pad.activity.set;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.base.util.IntentUtil;
import com.zkjinshi.superservice.pad.R;
import com.zkjinshi.superservice.pad.adapter.GuestAdapter;
import com.zkjinshi.superservice.pad.base.BaseAppCompatActivity;
import com.zkjinshi.superservice.pad.vo.EventUserVo;
import com.zkjinshi.superservice.pad.vo.EventVo;

import java.util.ArrayList;

/**
 * 邀请名单页面
 * 开发者：jimmyzhang
 * 日期：16/6/28
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class GuestListActivity extends BaseAppCompatActivity {

    public static final int MENU_ITEM_CALL = 0;

    private Toolbar toolbar;
    private TextView titleIv;
    private SwipeMenuListView guestListView;
    private EventVo eventVo;
    private ArrayList<EventUserVo> eventUserList;
    private GuestAdapter guestAdapter;
    private TextView inviteCountTv,sureCountTv;

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        titleIv = (TextView) findViewById(R.id.tv_center_title);
        guestListView = (SwipeMenuListView)findViewById(R.id.list_view_guest);
        inviteCountTv = (TextView)findViewById(R.id.total_invit_count_tv);
        sureCountTv = (TextView)findViewById(R.id.total_sure_count_tv);
    }

    private void initData(){
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.mipmap.ic_fanhui);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        titleIv.setText("邀请名单");
        if(null != getIntent() && null != getIntent().getSerializableExtra("eventVo")){
            eventVo = (EventVo) getIntent().getSerializableExtra("eventVo");
            if(null != eventVo){
                eventUserList = eventVo.getInviteperson();
            }
            int inviteCount = eventVo.getInvitedpersoncnt();
            inviteCountTv.setText("已邀请"+inviteCount+"人");
            int sureCount = eventVo.getConfirmpersoncnt();
            sureCountTv.setText("已确认"+sureCount+"人");
        }
        guestAdapter = new GuestAdapter(this,eventUserList);
        guestListView.setAdapter(guestAdapter);
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem callItem = new SwipeMenuItem(
                        getApplicationContext());//1ab6ff
                callItem.setBackground(new ColorDrawable(Color.rgb(0x1A,
                        0xB6, 0xFF)));
                callItem.setWidth(DisplayUtil.dip2px(GuestListActivity.this,90));
                callItem.setTitle("呼叫");
                callItem.setTitleSize(14);
                callItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(callItem);
            }
        };
        guestListView.setMenuCreator(creator);
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
                    case R.id.menu_guest_list_add://新增邀请人
                        Intent intent = new Intent(GuestListActivity.this,AddInviteActivity.class);
                        intent.putExtra("actId",eventVo.getActid());
                        intent.putExtra("portable",eventVo.getPortable());
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        break;
                }
                return true;
            }
        });

        guestListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case MENU_ITEM_CALL://呼叫
                        EventUserVo eventVo = eventUserList.get(position);
                        String phone = eventVo.getPhone();
                        if(!TextUtils.isEmpty(phone)){
                            IntentUtil.callPhone(GuestListActivity.this,phone);
                        }
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_list);
        initView();
        initData();
        initListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_guest_list, menu);
        return true;
    }
}
