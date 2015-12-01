package com.zkjinshi.superservice.activity.chat.group;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.zkjinshi.superservice.factory.EContactFactory;
import com.zkjinshi.superservice.listener.RecyclerItemClickListener;
import com.zkjinshi.superservice.sqlite.ClientDBUtil;
import com.zkjinshi.superservice.sqlite.ShopEmployeeDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.vo.ClientVo;
import com.zkjinshi.superservice.vo.EContactVo;
import com.zkjinshi.superservice.vo.ShopEmployeeVo;

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
    private List<EContactVo> selectContactList;
    private RelativeLayout mRlBack;
    private TextView mTvTitle;
    private RecyclerView mRcvTeamContacts;

    private LinearLayoutManager mLayoutManager;
    private InviteTeamAdapter mContactsAdapter;
    private List<ShopEmployeeVo>    mShopEmployeeVos;
    private RelativeLayout createGroupLayout;
    private ShopEmployeeVo shopEmployeeVo;
    private ClientVo clientVo;
    private EContactVo contactVo;
    private String contactId;
    private String shopEmployeeId;
    private Map<Integer, Boolean> checkedMap = new HashMap<Integer, Boolean>();
    private boolean addSucc;

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
        selectList = new ArrayList<String>();
        selectContactList = new ArrayList<EContactVo>();
        if(null != getIntent() && null != getIntent().getStringExtra("userId")){
            contactId = getIntent().getStringExtra("userId");
            shopEmployeeVo = ShopEmployeeDBUtil.getInstance().queryEmployeeById(contactId);
            if(null != shopEmployeeVo){
                contactVo = EContactFactory.getInstance().buildEContactVo(shopEmployeeVo);
                if(!selectContactList.contains(contactVo)){
                    addSucc = selectContactList.add(contactVo);
                    selectList.add(contactId);
                }
            }
            clientVo = ClientDBUtil.getInstance().queryClientByClientID(contactId);
            if(null != clientVo && !addSucc){
                contactVo = EContactFactory.getInstance().buildEContactVo(clientVo);
                if(!selectContactList.contains(contactVo)){
                    selectContactList.add(contactVo);
                    selectList.add(contactId);
                }
            }
        }
        mTvTitle.setText("创建团队");
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
                    if(!TextUtils.isEmpty(shopEmployeeId) && shopEmployeeId.equals(contactId)){
                        checkedMap.put(i,true);
                    }
                }
            }
        }
        mContactsAdapter = new InviteTeamAdapter(CreateGroupActivity.this, mShopEmployeeVos);
        mRcvTeamContacts.setAdapter(mContactsAdapter);
        mContactsAdapter.setCheckedMap(checkedMap);
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
            public void onItemClick(View view, int postion) {
                ShopEmployeeVo shopEmployeeVo = mShopEmployeeVos.get(postion);
                String empID = shopEmployeeVo.getEmpid();
                contactVo = EContactFactory.getInstance().buildEContactVo(shopEmployeeVo);
                if (selectList.contains(empID)) {
                    selectList.remove(empID);
                    selectContactList.remove(contactVo);
                } else {
                    selectList.add(empID);
                    selectContactList.add(contactVo);
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
                DialogUtil.getInstance().showProgressDialog(CreateGroupActivity.this);
            }

            @Override
            protected EMGroup doInBackground(Void... params) {
                EMGroup emGroup = null;
                try {
                    if(null != selectList && selectList.size() >= 1){
                        String[] members = convertList2Array(selectList);
                        String title = convertList2String(selectContactList);
                        emGroup = EMGroupManager.getInstance().createPrivateGroup(title, "内部私聊群", members, true);
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
     * @param contactList
     * @return
     */
    private String convertList2String(List<EContactVo> contactList){
        StringBuilder teamTitle = new StringBuilder();
        for(EContactVo contactVo : contactList){
            String employeeName = contactVo.getContactName();
            teamTitle.append(employeeName).append("、");
        }
        teamTitle.append(CacheUtil.getInstance().getUserName()).append("、");
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
                Intent intent = new Intent(CreateGroupActivity.this,ChatGroupActivity.class);
                intent.putExtra("groupId",groupId);
                startActivity(intent);
                finish();

            }
        });
        customBuilder.create().show();
    }
}
