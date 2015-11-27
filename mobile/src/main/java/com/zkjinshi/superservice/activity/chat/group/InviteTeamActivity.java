package com.zkjinshi.superservice.activity.chat.group;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.List;

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
    private RelativeLayout mRlBack;
    private TextView mTvTitle;
    private RecyclerView mRcvTeamContacts;

    private LinearLayoutManager mLayoutManager;
    private InviteTeamAdapter mContactsAdapter;
    private List<ShopEmployeeVo>    mShopEmployeeVos;
    private RelativeLayout createGroupLayout;

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
        mTvTitle.setText("团队");
        mRcvTeamContacts.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRcvTeamContacts.setLayoutManager(mLayoutManager);
        mShopEmployeeVos = ShopEmployeeDBUtil.getInstance().queryAllByDeptIDAsc();
        mContactsAdapter = new InviteTeamAdapter(InviteTeamActivity.this, mShopEmployeeVos);
        mRcvTeamContacts.setAdapter(mContactsAdapter);
        createGroupLayout.setTag(mCheckedList);
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
                } else {
                    mCheckedList.add(empID);
                }
                createGroupLayout.setTag(mCheckedList);
            }
        });

        //创建群
        createGroupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mCheckedList = (ArrayList<String>) v.getTag();
                            if(null != mCheckedList && mCheckedList.size() >= 1){
                                String[] members = convertList2Array(mCheckedList);
                                EMGroup emGroup = EMGroupManager.getInstance().createPrivateGroup("中科金石", "内部私聊群", members, true);
                                Log.i(TAG,"群名id:"+emGroup.getGroupId());
                                Log.i(TAG,"群名称:"+emGroup.getGroupName());
                                if (null != emGroup) {
                                    showCreateGroupSuccDialog();
                                }
                            }else {
                                DialogUtil.getInstance().showCustomToast(InviteTeamActivity.this,"至少要选择一个联系人",Gravity.CENTER);
                            }

                        } catch (EaseMobException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
    }

    private String[] convertList2Array(List<String> shopEmployeeVos) {
        String[] members = new String[shopEmployeeVos.size()];
        for(int i=0; i<shopEmployeeVos.size(); i++){
            members[i] = shopEmployeeVos.get(i);
        }
        return members;
    }

    private void showCreateGroupSuccDialog(){
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

            }
        });
        customBuilder.create().show();
    }
}
