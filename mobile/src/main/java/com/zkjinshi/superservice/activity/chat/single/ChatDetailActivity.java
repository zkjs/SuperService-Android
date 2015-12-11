package com.zkjinshi.superservice.activity.chat.single;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.easemob.chat.EMChatManager;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.chat.group.CreateGroupActivity;
import com.zkjinshi.superservice.activity.common.MainActivity;
import com.zkjinshi.superservice.adapter.ChatDetailAdapter;
import com.zkjinshi.superservice.factory.EContactFactory;
import com.zkjinshi.superservice.sqlite.ClientDBUtil;
import com.zkjinshi.superservice.sqlite.ShopEmployeeDBUtil;
import com.zkjinshi.superservice.vo.ClientVo;
import com.zkjinshi.superservice.vo.EContactVo;
import com.zkjinshi.superservice.vo.ShopEmployeeVo;

import java.util.ArrayList;
import java.util.List;

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
    private String userName;
    private ShopEmployeeVo shopEmployeeVo;
    private EContactVo contactVo;
    private ClientVo clientVo;
    private String clientId;
    private GridView shopEmpGv;
    private boolean addSucc;
    private RelativeLayout clearHistoryLayout;

    private void initView(){
        titleTv = (TextView)findViewById(R.id.header_bar_tv_title);
        backIBtn = (ImageButton)findViewById(R.id.header_bar_btn_back);
        shopEmpGv = (GridView)findViewById(R.id.chat_detail_gv_contacts);
        clearHistoryLayout = (RelativeLayout)findViewById(R.id.chat_detail_layout_clear_history);
    }

    private void initData(){
        titleTv.setText("聊天详情");
        backIBtn.setVisibility(View.VISIBLE);
        if(null != getIntent() && null != getIntent().getStringExtra("userId")){
            userId = getIntent().getStringExtra("userId");
            shopEmployeeVo = ShopEmployeeDBUtil.getInstance().queryEmployeeById(userId);
            if(null != shopEmployeeVo){
                contactVo = EContactFactory.getInstance().buildEContactVo(shopEmployeeVo);
                if(!contactList.contains(contactVo)){
                    addSucc = contactList.add(contactVo);
                }
            }
            if(!addSucc){
                clientVo = ClientDBUtil.getInstance().queryClientByClientID(userId);
                if(null != clientVo){
                    contactVo = EContactFactory.getInstance().buildEContactVo(clientVo);
                    if(!contactList.contains(contactVo)){
                        addSucc = contactList.add(contactVo);
                    }
                }
            }
            if(!addSucc) {//网络获取用户私聊信息
                if(null != getIntent() && null != getIntent().getStringExtra("userName")){
                    userName = getIntent().getStringExtra("userName");
                    contactVo = EContactFactory.getInstance().buildDefaultContactVo(userId,userName);
                    if(!contactList.contains(contactVo)){
                        addSucc = contactList.add(contactVo);
                    }
                }
            }
            chatDetailAdapter = new ChatDetailAdapter(this, contactList);
            shopEmpGv.setAdapter(chatDetailAdapter);
            setGridViewHeightBasedOnChildren(shopEmpGv);
        }
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
                if (null != contactList && !contactList.isEmpty() && contactList.size() < 12 && contactList.size() == position || position == 11) { //点击加号
                    if (contactList != null && !contactList.isEmpty()){
                        Intent intent = new Intent(ChatDetailActivity.this, CreateGroupActivity.class);
                        contactVo = contactList.get(0);
                        String userId = contactVo.getContactId();
                        String userName = contactVo.getContactName();
                        intent.putExtra("userId", userId);
                        intent.putExtra("userName", userName);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_bottom,
                                R.anim.slide_out_top);
                    }
                }
            }
        });

        //清空记录
        clearHistoryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClearHistoryDialog(userId);
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

    /**
     * 清空聊天记录
     * @param userId
     */
    private void showClearHistoryDialog(final String userId){
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(ChatDetailActivity.this);
        customBuilder.setTitle("提示");
        customBuilder.setMessage("确认清空聊天记录？");
        customBuilder.setGravity(Gravity.CENTER);
        customBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        customBuilder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                EMChatManager.getInstance().clearConversation(userId);
                Intent intent = new Intent(ChatDetailActivity.this, MainActivity.class);
                intent.putExtra("currentItem",1);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        customBuilder.create().show();
    }

}
