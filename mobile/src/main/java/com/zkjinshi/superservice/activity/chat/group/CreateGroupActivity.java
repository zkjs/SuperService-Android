package com.zkjinshi.superservice.activity.chat.group;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.CreateGroupAdapter;
import com.zkjinshi.superservice.factory.EContactFactory;
import com.zkjinshi.superservice.listener.RecyclerItemClickListener;
import com.zkjinshi.superservice.sqlite.ClientDBUtil;
import com.zkjinshi.superservice.sqlite.ShopEmployeeDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.vo.ClientVo;
import com.zkjinshi.superservice.vo.EContactVo;
import com.zkjinshi.superservice.vo.EmployeeVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 创建群页面
 * 开发者：JimmyZhang
 * 日期：2015/11/25
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CreateGroupActivity extends Activity {

    public static final String TAG = CreateGroupActivity.class.getSimpleName();

    private List<String> selectList;
    private RelativeLayout mRlBack;
    private TextView mTvTitle;
    private RecyclerView mRcvTeamContacts;

    private LinearLayoutManager mLayoutManager;
    private CreateGroupAdapter mContactsAdapter;
    private List<EmployeeVo>    mEmployeeVos;
//    private RelativeLayout createGroupLayout;
    private ImageButton goIbtn;
    private EmployeeVo shopEmployeeVo;
    private ClientVo clientVo;
    private EContactVo contactVo;
    private String contactId;
    private String contanctName;
    private String shopEmployeeId;
    private Map<Integer, Boolean> selectMap = new HashMap<Integer, Boolean>();
    private Map<Integer, Boolean> enabledMap = new HashMap<Integer, Boolean>();
    private boolean addSucc;

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
        goIbtn = (ImageButton)findViewById(R.id.go_ibtn);
//        createGroupLayout = (RelativeLayout)findViewById(R.id.invite_create_group_layout);
    }

    private void initData() {
        mShopID = CacheUtil.getInstance().getShopID();

        selectList = new ArrayList<String>();
        if(null != getIntent() && null != getIntent().getStringExtra("userId")){
            contactId = getIntent().getStringExtra("userId");
            shopEmployeeVo = ShopEmployeeDBUtil.getInstance().queryEmployeeById(contactId);
            if(null != shopEmployeeVo){
                if(!selectList.contains(contactId)){
                    addSucc = selectList.add(contactId);
                }
            }
            clientVo = ClientDBUtil.getInstance().queryClientByClientID(contactId);
            if(null != clientVo && !addSucc){
                if(!selectList.contains(contactId)){
                    addSucc = selectList.add(contactId);
                }
            }
            if(!addSucc) {
                if(null != getIntent() && null != getIntent().getStringExtra("userName")){
                    addSucc = selectList.add(contactId);
                    contanctName = getIntent().getStringExtra("userName");
                }
            }
        }
        mTvTitle.setText("添加群聊对象");
        mRcvTeamContacts.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRcvTeamContacts.setLayoutManager(mLayoutManager);
        mEmployeeVos = ShopEmployeeDBUtil.getInstance().queryAllExceptUser(CacheUtil.getInstance().getUserId());
        if(null != mEmployeeVos && !mEmployeeVos.isEmpty()){
            for(int i= 0 ; i< mEmployeeVos.size(); i++){
                shopEmployeeVo = mEmployeeVos.get(i);
                if(null != shopEmployeeVo){
                    shopEmployeeId = shopEmployeeVo.getUserid();
                    if(!TextUtils.isEmpty(shopEmployeeId) && shopEmployeeId.equals(contactId)){
                        enabledMap.put(i,true);
                    }
                    if(!TextUtils.isEmpty(shopEmployeeId) && shopEmployeeId.equals(CacheUtil.getInstance().getUserId())){
                        enabledMap.put(i,true);
                    }
                }
            }
        }
        mContactsAdapter = new CreateGroupAdapter(CreateGroupActivity.this, mEmployeeVos);
        mRcvTeamContacts.setAdapter(mContactsAdapter);
        mContactsAdapter.setEnabledMap(enabledMap);
        mContactsAdapter.setSelectMap(selectMap);
    }

    private void initListener() {

        //返回
        mRlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateGroupActivity.this.finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        mContactsAdapter.setOnItemClickListener(new RecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                EmployeeVo shopEmployeeVo = mEmployeeVos.get(position);
                String empID = shopEmployeeVo.getUserid();
                if (selectList.contains(empID)) {
                    selectList.remove(empID);
                } else {
                    selectList.add(empID);
                }
            }
        });

        //创建群
        goIbtn.setOnClickListener(new View.OnClickListener() {
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
                DialogUtil.getInstance().showProgressDialog(CreateGroupActivity.this);
            }

            @Override
            protected EMGroup doInBackground(Void... params) {
                EMGroup emGroup = null;
                try {
                    if(null != selectList && selectList.size() >= 1){
                        String[] members = convertList2Array(selectList);
                        String title = convertList2String(selectList);
                        emGroup = EMGroupManager.getInstance().createPrivateGroup(title, CacheUtil.getInstance().getShopID(), members, true);
                    }else {
                        DialogUtil.getInstance().showCustomToast(CreateGroupActivity.this,"至少要选择一个联系人",Gravity.CENTER);
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
                    Intent intent = new Intent(CreateGroupActivity.this,ChatGroupActivity.class);
                    intent.putExtra("groupId",group.getGroupId());
                    startActivity(intent);
                    finish();
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
     * @param selectList
     * @return
     */
    private String convertList2String(List<String> selectList){
        StringBuilder teamTitle = new StringBuilder();
        for(String contactId : selectList){
            shopEmployeeVo = ShopEmployeeDBUtil.getInstance().queryEmployeeById( contactId);
            if(null != shopEmployeeVo){
                contactVo = EContactFactory.getInstance().buildEContactVo(shopEmployeeVo);
            }
            if(null == contactVo){
                clientVo = ClientDBUtil.getInstance().queryClientByClientID(contactId);
                if(null != clientVo){
                    contactVo = EContactFactory.getInstance().buildEContactVo(clientVo);
                }
            }
            if(null == contactVo){
                contactVo = EContactFactory.getInstance().buildDefaultContactVo(contactId,contanctName);
            }
            if(null != contactVo){
                String employeeName = contactVo.getContactName();
                teamTitle.append(employeeName).append("、");
            }
        }
        teamTitle.append(CacheUtil.getInstance().getUserName()).append("、");
        if(!TextUtils.isEmpty(teamTitle)){
            teamTitle.deleteCharAt(teamTitle.length()-1);
        }
        return teamTitle.toString();
    }
}
