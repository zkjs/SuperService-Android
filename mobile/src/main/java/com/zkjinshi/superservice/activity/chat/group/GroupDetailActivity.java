package com.zkjinshi.superservice.activity.chat.group;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.common.MainActivity;
import com.zkjinshi.superservice.adapter.ChatDetailAdapter;
import com.zkjinshi.superservice.factory.EContactFactory;
import com.zkjinshi.superservice.sqlite.ClientDBUtil;
import com.zkjinshi.superservice.sqlite.ShopEmployeeDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.vo.ClientVo;
import com.zkjinshi.superservice.vo.EContactVo;
import com.zkjinshi.superservice.vo.ShopEmployeeVo;

import java.util.ArrayList;
import java.util.List;

/**
 * 群聊详情页
 * 开发者：JimmyZhang
 * 日期：2015/11/25
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class GroupDetailActivity extends Activity{

    public static final int MODIFY_GROUP_NAME_REQUEST_CODE = 1;

    private TextView titleTv;
    private ImageButton backIBtn;
    private TextView groupNameTv;
    private ChatDetailAdapter chatDetailAdapter;
    private ArrayList<EContactVo> contactList = new ArrayList<EContactVo>();
    private EContactVo contactVo;
    private GridView shopEmpGv;
    private String groupId;
    private EMGroup group;
    private List<String> memberList;
    private ClientVo clientVo;
    private ShopEmployeeVo shopEmployeeVo;
    private CheckBox blockMessageCb;
    private RelativeLayout blockMessageLayout,clearHistoryLayout,groupNameLayout;
    private Button dissolveBtn,quitBtn;

    private void initView(){
        titleTv = (TextView)findViewById(R.id.header_bar_tv_title);
        backIBtn = (ImageButton)findViewById(R.id.header_bar_btn_back);
        shopEmpGv = (GridView)findViewById(R.id.chat_group_detail_gv_contacts);
        groupNameTv = (TextView)findViewById(R.id.chat_group_detail_tv_group_name);
        blockMessageCb = (CheckBox)findViewById(R.id.chat_group_detail_cb_block_group_message);
        blockMessageLayout = (RelativeLayout)findViewById(R.id.chat_group_detail_layout_block_group_message);
        clearHistoryLayout = (RelativeLayout)findViewById(R.id.chat_group_detail_layout_clear_history);
        groupNameLayout = (RelativeLayout)findViewById(R.id.chat_group_detail_layout_group_name);
        dissolveBtn = (Button)findViewById(R.id.chat_group_detail_btn_dissolve_group);
        quitBtn = (Button)findViewById(R.id.chat_group_detail_btn_quit_group);
    }

    private void initData(){
        titleTv.setText("团队详情");
        backIBtn.setVisibility(View.VISIBLE);
        if(null != getIntent() && null != getIntent().getStringExtra("groupId")){
            groupId = getIntent().getStringExtra("groupId");
            if(!TextUtils.isEmpty(groupId)){
                requestGroupTask(groupId,shopEmpGv,groupNameTv);
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

        shopEmpGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (contactList.size() < 12 && contactList.size() == position || position == 11) { //点击加号
                    Intent intent = new Intent(GroupDetailActivity.this, InviteMembersActivity.class);
                    intent.putExtra("groupId", groupId);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_bottom,
                            R.anim.slide_out_top);
                }
            }
        });

        //屏蔽或者开启消息
        blockMessageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = blockMessageCb.isChecked();
                if(isChecked){
                    //执行取消
                    requestUnblockGroupMessageTask(groupId);
                }else{
                    //执行屏蔽
                    requestBlockGroupMessageTask(groupId);
                }
            }
        });

        //清空和某个user的聊天记录(包括本地)，不删除conversation这个会话对象
        clearHistoryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClearHistoryDialog(groupId);
            }
        });

        //解散该团退
        dissolveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showExitFromGroupDialog(groupId);
            }
        });

        //退出该团队
        quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitAndDeleteDialog(groupId);
            }
        });

        //修改群名称
        groupNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupDetailActivity.this,ModifyGroupActivity.class);
                intent.putExtra("groupId",groupId);
                startActivityForResult(intent,MODIFY_GROUP_NAME_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_detail);
        initView();
        initData();
        initListeners();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(RESULT_OK == resultCode){
            if(requestCode == MODIFY_GROUP_NAME_REQUEST_CODE){
                if(null != data){
                    String groupName =  data.getStringExtra("groupName");
                    if(!TextUtils.isEmpty(groupName)){
                        groupNameTv.setText(groupName);
                    }
                }
            }
        }
    }

    /**
     * 设置联系人集合
     * @param memberList
     */
    private ArrayList<EContactVo> setContactList(List<String> memberList){
        boolean isSucc = false;
        for (String memberId : memberList){
            isSucc = false;
            shopEmployeeVo = ShopEmployeeDBUtil.getInstance().queryEmployeeById(memberId);
            if(null != shopEmployeeVo){
                contactVo = EContactFactory.getInstance().buildEContactVo(shopEmployeeVo);
                if(null != contactVo &&!contactList.contains(contactVo)){
                    isSucc = contactList.add(contactVo);
                }
            }
            if(!isSucc){
                clientVo = ClientDBUtil.getInstance().queryClientByClientID(memberId);
                if(null != clientVo){
                    contactVo = EContactFactory.getInstance().buildEContactVo(clientVo);
                    if(null != contactVo && !contactList.contains(contactVo)){
                        contactList.add(contactVo);
                    }
                }
            }
        }
        return contactList;
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
     * 获取群详情
     * @param groupId
     * @param gridView
     * @param groupNameTv
     */
    private void requestGroupTask(final String groupId,final GridView gridView,final TextView groupNameTv){

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                DialogUtil.getInstance().showProgressDialog(GroupDetailActivity.this);
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
                    if(null != memberList && memberList.size() > 0){
                        contactList = setContactList(memberList);
                    }
                }
                chatDetailAdapter = new ChatDetailAdapter(GroupDetailActivity.this, contactList);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                DialogUtil.getInstance().cancelProgressDialog();
                gridView.setAdapter(chatDetailAdapter);
                setGridViewHeightBasedOnChildren(gridView);
                if(null != group){
                    String groupName = group.getGroupName();
                    if(!TextUtils.isEmpty(groupName)){
                        groupNameTv.setText(groupName);
                    }
                    boolean msgBlocked = group.isMsgBlocked();
                    blockMessageCb.setChecked(msgBlocked);
                    String ownerId = group.getOwner();
                    if(!TextUtils.isEmpty(ownerId) && ownerId.equals(CacheUtil.getInstance().getUserId())){
                        dissolveBtn.setVisibility(View.VISIBLE);
                        quitBtn.setVisibility(View.GONE);
                    }else{
                        dissolveBtn.setVisibility(View.GONE);
                        quitBtn.setVisibility(View.VISIBLE);
                    }
                }
            }
        }.execute();
    }

    /**
     * 屏蔽群消息后，就不能接收到此群的消息 （群创建者不能屏蔽群消息）（还是群里面的成员，但不再接收消息）
     * @param groupId
     */
    private void requestBlockGroupMessageTask(final String groupId){

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                DialogUtil.getInstance().showProgressDialog(GroupDetailActivity.this);
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    EMGroupManager.getInstance().blockGroupMessage(groupId);//需异步处理
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                DialogUtil.getInstance().cancelProgressDialog();
                blockMessageCb.setChecked(true);
            }
        }.execute();
    }

    /**
     * 取消屏蔽群消息,就可以正常收到群的所有消息
     * @param groupId
     */
    private void requestUnblockGroupMessageTask(final String groupId){

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                DialogUtil.getInstance().showProgressDialog(GroupDetailActivity.this);
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    EMGroupManager.getInstance().unblockGroupMessage(groupId);//需异步处理
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                DialogUtil.getInstance().cancelProgressDialog();
                blockMessageCb.setChecked(false);
            }
        }.execute();
    }

    /**
     * 退出该团队
     * @param groupId
     */
    private void requestExitFromGroupTask(final String groupId){

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                DialogUtil.getInstance().showProgressDialog(GroupDetailActivity.this);
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    EMGroupManager.getInstance().exitFromGroup(groupId);
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                DialogUtil.getInstance().cancelProgressDialog();
                Intent intent = new Intent(GroupDetailActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("currentItem",1);
                startActivity(intent);
                finish();
            }
        }.execute();
    }

    /**
     * 解散该团队
     * @param groupId
     */
    private void requestExitAndDeleteGroupTask(final String groupId){

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                DialogUtil.getInstance().showProgressDialog(GroupDetailActivity.this);
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    EMGroupManager.getInstance().exitAndDeleteGroup(groupId);
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                DialogUtil.getInstance().cancelProgressDialog();
                Intent intent = new Intent(GroupDetailActivity.this, MainActivity.class);
                intent.putExtra("currentItem",1);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }.execute();
    }

    /**
     * 退出该团队
     * @param groupId
     */
    private void showExitFromGroupDialog(final String groupId){
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(GroupDetailActivity.this);
        customBuilder.setTitle("提示");
        customBuilder.setMessage("确认退出该团队？");
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
                requestExitFromGroupTask(groupId);
            }
        });
        customBuilder.create().show();
    }

    /**
     * 解散该团队
     * @param groupId
     */
    private void showExitAndDeleteDialog(final String groupId){
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(GroupDetailActivity.this);
        customBuilder.setTitle("提示");
        customBuilder.setMessage("确认解散该团队？");
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
               requestExitAndDeleteGroupTask(groupId);
            }
        });
        customBuilder.create().show();
    }

    /**
     * 清空聊天记录
     * @param groupId
     */
    private void showClearHistoryDialog(final String groupId){
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(GroupDetailActivity.this);
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
                EMChatManager.getInstance().clearConversation(groupId);
                Intent intent = new Intent(GroupDetailActivity.this, MainActivity.class);
                intent.putExtra("currentItem",1);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        customBuilder.create().show();
    }
}
