package com.zkjinshi.superservice.activity.chat.group;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.InviteTeamAdapter;
import com.zkjinshi.superservice.listener.RecyclerItemClickListener;
import com.zkjinshi.superservice.sqlite.ShopEmployeeDBUtil;
import com.zkjinshi.superservice.vo.ShopEmployeeVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 邀请群成员
 * 开发者：JimmyZhang
 * 日期：2015/11/25
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class InviteTeamActivity extends Activity {

    public static final String TAG = InviteTeamActivity.class.getSimpleName();

    private List<String> mCheckedList;
    private List<ShopEmployeeVo> mCheckedEmployeeList;
    private RelativeLayout mRlBack;
    private TextView mTvTitle;
    private RecyclerView mRcvTeamContacts;

    private LinearLayoutManager mLayoutManager;
    private InviteTeamAdapter mContactsAdapter;
    private List<ShopEmployeeVo>    mShopEmployeeVos;
    private RelativeLayout createGroupLayout;
    private ShopEmployeeVo shopEmployeeVo;
    private String empId;
    private String shopEmployeeId;
    private Map<Integer, Boolean> checkedMap = new HashMap<Integer, Boolean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_team);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        mRlBack   = (RelativeLayout) findViewById(R.id.rl_back);
        mTvTitle  = (TextView)    findViewById(R.id.tv_title);
        mRcvTeamContacts     = (RecyclerView) findViewById(R.id.rcv_team_contacts);
        createGroupLayout = (RelativeLayout)findViewById(R.id.invite_create_group_layout);
    }

    private void initData() {
        mCheckedList = new ArrayList<String>();
        mCheckedEmployeeList = new ArrayList<ShopEmployeeVo>();
        if(null != getIntent() && null != getIntent().getSerializableExtra("shopEmployeeVo")){
            shopEmployeeVo = (ShopEmployeeVo) getIntent().getSerializableExtra("shopEmployeeVo");
            if(null != shopEmployeeVo){
                empId = shopEmployeeVo.getEmpid();
                if(!TextUtils.isEmpty(empId)){
                    mCheckedList.add(empId);
                    mCheckedEmployeeList.add(shopEmployeeVo);
                }
            }
        }
        mTvTitle.setText("团队");
        mRcvTeamContacts.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRcvTeamContacts.setLayoutManager(mLayoutManager);
        mShopEmployeeVos = ShopEmployeeDBUtil.getInstance().queryAllByDeptIDAsc();
        if(null != mShopEmployeeVos && !mShopEmployeeVos.isEmpty()){
            for(int i= 0 ; i< mShopEmployeeVos.size(); i++){
                shopEmployeeVo = mShopEmployeeVos.get(i);
                if(null != shopEmployeeVo){
                    shopEmployeeId = shopEmployeeVo.getEmpid();
                    if(!TextUtils.isEmpty(shopEmployeeId) && shopEmployeeId.equals(empId)){
                        checkedMap.put(i,true);
                    }
                }
            }
        }
        mContactsAdapter = new InviteTeamAdapter(InviteTeamActivity.this, mShopEmployeeVos);
        mRcvTeamContacts.setAdapter(mContactsAdapter);
        mContactsAdapter.setCheckedMap(checkedMap);
    }

    private void initListener() {

        //返回
        mRlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InviteTeamActivity.this.finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        mContactsAdapter.setOnItemClickListener(new RecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                ShopEmployeeVo shopEmployeeVo = mShopEmployeeVos.get(postion);
                String empID = shopEmployeeVo.getEmpid();
                if (mCheckedList.contains(empID)) {
                    mCheckedList.remove(empID);
                    mCheckedEmployeeList.remove(shopEmployeeVo);
                } else {
                    mCheckedList.add(empID);
                    mCheckedEmployeeList.add(shopEmployeeVo);
                }
            }
        });

        //创建群
        createGroupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                requestCreateGroupTask();
            }
        });
    }

    /**
     * 创建团队
     */
    private void requestCreateGroupTask(){
        new AsyncTask<Void,Void,EMGroup>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                DialogUtil.getInstance().showProgressDialog(InviteTeamActivity.this);
            }

            @Override
            protected EMGroup doInBackground(Void... params) {
                EMGroup emGroup = null;
                try {
                    if(null != mCheckedList && mCheckedList.size() >= 1){
                        String[] members = convertList2Array(mCheckedList);
                        String title = convertList2String(mCheckedEmployeeList);
                        emGroup = EMGroupManager.getInstance().createPrivateGroup(title, "内部私聊群", members, true);
                    }else {
                        DialogUtil.getInstance().showCustomToast(InviteTeamActivity.this,"至少要选择一个联系人",Gravity.CENTER);
                    }
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
                return emGroup;
            }

            @Override
            protected void onPostExecute(EMGroup group) {
                super.onPostExecute(group);
                DialogUtil.getInstance().cancelProgressDialog();
                if (null != group) {
                    showCreateGroupSuccDialog(group.getGroupId());
                }
            }
        }.execute();
    }

    /**
     * 获得群成员
     * @param shopEmployeeVos
     * @return
     */
    private String[] convertList2Array(List<String> shopEmployeeVos) {
        String[] members = new String[shopEmployeeVos.size()];
        for(int i=0; i<shopEmployeeVos.size(); i++){
            members[i] = shopEmployeeVos.get(i);
        }
        return members;
    }

    /**
     * 获取群标题
     * @param shopEmployeeList
     * @return
     */
    private String convertList2String(List<ShopEmployeeVo> shopEmployeeList){
        StringBuilder teamTitle = new StringBuilder();
        for(ShopEmployeeVo shopEmployee : shopEmployeeList){
            String employeeName = shopEmployee.getName();
            teamTitle.append(employeeName).append("、");
        }
        if(!TextUtils.isEmpty(teamTitle)){
            teamTitle.deleteCharAt(teamTitle.length()-1);
        }
        return teamTitle.toString();
    }

    private void showCreateGroupSuccDialog(final String groupId){
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
        customBuilder.setTitle("提示");
        customBuilder.setMessage("创建群成功，是否打开?");
        customBuilder.setGravity(Gravity.CENTER);
        customBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        customBuilder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(InviteTeamActivity.this,ChatGroupActivity.class);
                intent.putExtra("groupId",groupId);
                startActivity(intent);

            }
        });
        customBuilder.create().show();
    }
}
