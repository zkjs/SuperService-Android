package com.zkjinshi.superservice.activity.chat.group;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.InviteTeamAdapter;
import com.zkjinshi.superservice.factory.EContactFactory;
import com.zkjinshi.superservice.listener.RecyclerItemClickListener;
import com.zkjinshi.superservice.sqlite.ShopEmployeeDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.vo.EContactVo;
import com.zkjinshi.superservice.vo.EmployeeVo;


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
public class InviteMembersActivity extends Activity {

    public static final String TAG = InviteMembersActivity.class.getSimpleName();

    private List<String> selectList;
    private RelativeLayout mRlBack;
    private TextView mTvTitle;
    private RecyclerView mRcvTeamContacts;

    private LinearLayoutManager mLayoutManager;
    private InviteTeamAdapter mContactsAdapter;
    private List<EmployeeVo> shopEmployeeList;
    private RelativeLayout createGroupLayout;
    private EContactVo contactVo;
    private Map<Integer, Boolean> selectMap = new HashMap<Integer, Boolean>();
    private Map<Integer, Boolean> enabledMap = new HashMap<Integer, Boolean>();
    private String groupId;
    private EmployeeVo shopEmployeeVo;
    private EMGroup group;
    private List<String> memberList;
    private String empid;
    private String mShopID;

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

        mShopID = CacheUtil.getInstance().getShopID();

        selectList = new ArrayList<String>();
        mTvTitle.setText("邀请好友");
        mRcvTeamContacts.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRcvTeamContacts.setLayoutManager(mLayoutManager);
        shopEmployeeList = ShopEmployeeDBUtil.getInstance().queryAllExceptUser(CacheUtil.getInstance().getUserId());
        mContactsAdapter = new InviteTeamAdapter(InviteMembersActivity.this, shopEmployeeList);
        mRcvTeamContacts.setAdapter(mContactsAdapter);
        mContactsAdapter.setSelectMap(selectMap);
        mContactsAdapter.setEnabledMap(enabledMap);
        if(null != getIntent() && null != getIntent().getStringExtra("groupId")){
            groupId = getIntent().getStringExtra("groupId");
            if(!TextUtils.isEmpty(groupId)){
                requestGroupTask(groupId,shopEmployeeList,mRcvTeamContacts);
            }
        }
    }

    private void initListener() {

        //返回
        mRlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InviteMembersActivity.this.finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        mContactsAdapter.setOnItemClickListener(new RecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (enabledMap != null && enabledMap.containsKey(position)
                        && enabledMap.get(position))
                    return;
                EmployeeVo shopEmployeeVo = shopEmployeeList.get(position);
                String empID = shopEmployeeVo.getUserid();
                contactVo = EContactFactory.getInstance().buildEContactVo(shopEmployeeVo);
                if (selectList.contains(empID)) {
                    selectList.remove(empID);
                } else {
                    selectList.add(empID);;
                }
            }
        });

        //邀请好友
        createGroupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                requestInviteUserTask(groupId,selectList);
            }
        });
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
     * 邀请团队成员
     * @param groupId
     * @param memberList
     */
    private void requestInviteUserTask(final String groupId, final List<String> memberList){
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                DialogUtil.getInstance().showProgressDialog(InviteMembersActivity.this);
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    group =  EMGroupManager.getInstance().getGroupFromServer(groupId);
                    if(null != group){
                        String ownerId = group.getOwner();
                        if(null != memberList && !memberList.isEmpty()){
                            String[] memberArray = convertList2Array(memberList);
                            if(!TextUtils.isEmpty(ownerId) && ownerId.equals(CacheUtil.getInstance().getUserId())){//群主
                                EMGroupManager.getInstance().addUsersToGroup(groupId, memberArray);
                            }else {//普通成员
                                EMGroupManager.getInstance().inviteUser(groupId, memberArray, null);
                            }
                        }
                    }
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                DialogUtil.getInstance().cancelProgressDialog();
                if (null != group) {
                    Intent intent = new Intent(InviteMembersActivity.this,ChatGroupActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("groupId",group.getGroupId());
                    startActivity(intent);
                    finish();
                }

            }
        }.execute();
    }

    /**
     * 获取群详情
     * @param groupId
     * @param recyclerView
     */
    private void requestGroupTask(final String groupId, final List<EmployeeVo> shopEmployeeList,final RecyclerView recyclerView){

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                DialogUtil.getInstance().showProgressDialog(InviteMembersActivity.this);
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    group =  EMGroupManager.getInstance().getGroupFromServer(groupId);
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
                if(null != group){
                    memberList = group.getMembers();
                    if(null != memberList && !memberList.isEmpty()){
                        if(null != shopEmployeeList && !shopEmployeeList.isEmpty()){
                            for(int i = 0 ; i< shopEmployeeList.size(); i++){
                                shopEmployeeVo = shopEmployeeList.get(i);
                                empid = shopEmployeeVo.getUserid();
                                if(!TextUtils.isEmpty(empid)){
                                    if(memberList.contains(empid)){
                                        enabledMap.put(i,true);
                                    }
                                }
                            }
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                DialogUtil.getInstance().cancelProgressDialog();
                mContactsAdapter.setEnabledMap(enabledMap);
            }
        }.execute();
    }
}
