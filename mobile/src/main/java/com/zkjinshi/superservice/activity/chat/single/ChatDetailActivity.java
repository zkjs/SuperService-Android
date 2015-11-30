package com.zkjinshi.superservice.activity.chat.single;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.chat.group.InviteTeamActivity;
import com.zkjinshi.superservice.activity.set.EmployeeInfoActivity;
import com.zkjinshi.superservice.adapter.ChatDetailAdapter;
import com.zkjinshi.superservice.factory.EContactFactory;
import com.zkjinshi.superservice.sqlite.ClientDBUtil;
import com.zkjinshi.superservice.sqlite.ShopEmployeeDBUtil;
import com.zkjinshi.superservice.vo.ClientVo;
import com.zkjinshi.superservice.vo.EContactVo;
import com.zkjinshi.superservice.vo.ShopEmployeeVo;

import java.util.ArrayList;

/**
 * 单聊详情页
 * 开发者：JimmyZhang
 * 日期：2015/11/25
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ChatDetailActivity extends Activity{

    private TextView titleTv;
    private ImageButton backIBtn;
    private ChatDetailAdapter chatDetailAdapter;
    private ArrayList<EContactVo> contactList = new ArrayList<EContactVo>();
    private String userId;
    private ShopEmployeeVo shopEmployeeVo;
    private EContactVo contactVo;
    private ClientVo clientVo;
    private GridView shopEmpGv;

    private void initView(){
        titleTv = (TextView)findViewById(R.id.header_bar_tv_title);
        backIBtn = (ImageButton)findViewById(R.id.header_bar_btn_back);
        shopEmpGv = (GridView)findViewById(R.id.chat_detail_gv_contacts);
    }

    private void initData(){
        titleTv.setText("聊天详情");
        backIBtn.setVisibility(View.VISIBLE);
        if(null != getIntent() && null != getIntent().getStringExtra("userId")){
            userId = getIntent().getStringExtra("userId");
            shopEmployeeVo = ShopEmployeeDBUtil.getInstance().queryEmployeeById(userId);
            if(null != shopEmployeeVo){
                contactVo = EContactFactory.getInstance().buildEContactVo(shopEmployeeVo);
                contactList.add(contactVo);
            }
            clientVo = ClientDBUtil.getInstance().queryClientByClientID(userId);
            if(null != clientVo){
                contactVo = EContactFactory.getInstance().buildEContactVo(clientVo);
                contactList.add(contactVo);
            }
        }
        chatDetailAdapter = new ChatDetailAdapter(this, contactList);
        shopEmpGv.setAdapter(chatDetailAdapter);
        setGridViewHeightBasedOnChildren(shopEmpGv);
    }

    private void initListeners(){

        //返回
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        shopEmpGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (contactList.size() < 12 && contactList.size() == position || position == 11) { //点击加号
                    Intent intent = new Intent(ChatDetailActivity.this, InviteTeamActivity.class);
                    if (contactList != null && contactList.size() > 0){}
                        intent.putExtra("userId", contactList.get(0).getContactId());
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_bottom,
                            R.anim.slide_out_top);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);
        initView();
        initData();
        initListeners();
    }

    /**
     * 设置GridView高度
     * @param gridView
     */
    private void setGridViewHeightBasedOnChildren(GridView gridView) {
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null || listAdapter.getCount() == 0) {
            return;
        }
        int totalHeight = 0;
        int count = listAdapter.getCount() / 4;
        int row = listAdapter.getCount() % 4 == 0 ? count : count + 1;
        for (int i = 0; i < row; i++) {
            View listItem = listAdapter.getView(i, null, gridView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight + DisplayUtil.dip2px(this, 20);
        gridView.setLayoutParams(params);
    }

}
