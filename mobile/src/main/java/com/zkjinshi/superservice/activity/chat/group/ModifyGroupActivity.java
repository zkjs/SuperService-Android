package com.zkjinshi.superservice.activity.chat.group;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.ChatDetailAdapter;
import com.zkjinshi.superservice.utils.CacheUtil;

/**
 * 修改群名称
 * 开发者：JimmyZhang
 * 日期：2015/12/2
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ModifyGroupActivity extends Activity {

    private TextView titleTv;
    private ImageButton backIBtn;
    private String groupId;
    private String groupName;
    private EMGroup group;
    private EditText inputGroupNameEtv;
    private Button modifyGroupNameBtn;

    private void initView(){
        titleTv = (TextView) findViewById(R.id.header_bar_tv_title);
        backIBtn = (ImageButton)findViewById(R.id.header_bar_btn_back);
        inputGroupNameEtv = (EditText)findViewById(R.id.modify_group_etv_group_name);
        modifyGroupNameBtn = (Button)findViewById(R.id.modify_group_btn_commit);
    }

    private void initData(){
        titleTv.setText("修改团队名称");
        backIBtn.setVisibility(View.VISIBLE);
        if(null != getIntent() && null != getIntent().getStringExtra("groupId")){
            groupId = getIntent().getStringExtra("groupId");
            if(!TextUtils.isEmpty(groupId)){
                group = EMGroupManager.getInstance().getGroup(groupId);
                if(null != group){
                    groupName = group.getGroupName();
                    if(!TextUtils.isEmpty(groupName)){
                        inputGroupNameEtv.setText(groupName);
                    }
                }
            }
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

        //修改群名称
        modifyGroupNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String changedGroupName = inputGroupNameEtv.getText().toString();
                if(!TextUtils.isEmpty(changedGroupName)){
                    requestModifyGroupNameTask(groupId,changedGroupName);
                }else {
                    DialogUtil.getInstance().showCustomToast(ModifyGroupActivity.this,"团队名称不能为空", Gravity.CENTER);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_group);
        initView();
        initData();
        initListeners();
    }

    /**
     * 修改群名称
     * @param groupId
     * @param changedGroupName
     */
    private void requestModifyGroupNameTask(final String groupId, final String changedGroupName){

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                DialogUtil.getInstance().showProgressDialog(ModifyGroupActivity.this);
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    EMGroupManager.getInstance().changeGroupName(groupId,changedGroupName);
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                DialogUtil.getInstance().cancelProgressDialog();
                Intent intent = getIntent();
                intent.putExtra("groupName",changedGroupName);
                setResult(RESULT_OK,intent);
                finish();
            }
        }.execute();
    }

}
