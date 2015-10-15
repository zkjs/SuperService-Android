package com.zkjinshi.superservice.activity.set;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.net.core.WebSocketManager;
import com.zkjinshi.base.net.observer.IMessageObserver;
import com.zkjinshi.base.net.observer.MessageSubject;
import com.zkjinshi.base.net.protocol.ProtocolMSG;
import com.zkjinshi.base.util.Constants;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.TeamContactsAdapter;
import com.zkjinshi.superservice.bean.TeamContactBean;
import com.zkjinshi.superservice.entity.EmpStatusRecord;
import com.zkjinshi.superservice.entity.MsgEmpStatus;
import com.zkjinshi.superservice.entity.MsgEmpStatusRSP;
import com.zkjinshi.superservice.factory.ShopEmployeeFactory;
import com.zkjinshi.superservice.listener.GetTeamContactsListener;
import com.zkjinshi.superservice.listener.RecyclerItemClickListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestListener;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.sqlite.ShopEmployeeDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.view.AutoSideBar;
import com.zkjinshi.superservice.vo.IdentityType;
import com.zkjinshi.superservice.vo.OnlineStatus;
import com.zkjinshi.superservice.vo.ShopEmployeeVo;
import com.zkjinshi.superservice.vo.WorkStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 团队联系人显示界面
 * 开发者：vincent
 * 日期：2015/10/9
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class TeamContactsActivity extends AppCompatActivity implements IMessageObserver{

    private final static String TAG = TeamContactsActivity.class.getSimpleName();

    private Toolbar         mToolbar;
    private TextView        mTvCenterTitle;
    private RecyclerView    mRvTeamContacts;
    private RelativeLayout  mRlSideBar;
    private TextView        mTvDialog;
    private AutoSideBar     mAutoSideBar;

    private LinearLayoutManager     mLayoutManager;
    private TeamContactsAdapter     mTeamContactAdapter;

    private List<ShopEmployeeVo>   mShopEmployeeVos;
    private ShopEmployeeVo         mFirstShopEmployee;

    private String mUserID;
    private String mShopID;
    private String mToken;
    private IdentityType mUserType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_contacts);

        addObservers();
        initView();
        initData();
        initListener();
    }

    private void initView() {

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(R.drawable.ic_fanhui);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTvCenterTitle = (TextView) findViewById(R.id.tv_center_title);
        mTvCenterTitle.setText(getString(R.string.team));

        mRvTeamContacts = (RecyclerView)     findViewById(R.id.rcv_team_contacts);
        mRlSideBar      = (RelativeLayout)   findViewById(R.id.rl_side_bar);
        mTvDialog       = (TextView) findViewById(R.id.tv_dialog);
        mAutoSideBar    = new AutoSideBar(TeamContactsActivity.this);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                DisplayUtil.dip2px(TeamContactsActivity.this, 30),
                ViewGroup.LayoutParams.MATCH_PARENT);
        mAutoSideBar.setTextView(mTvDialog);
        mAutoSideBar.setLayoutParams(layoutParams);

    }

    private void initData() {
        mUserID     = CacheUtil.getInstance().getUserId();
        mToken      = CacheUtil.getInstance().getToken();
        mShopID     = CacheUtil.getInstance().getShopID();
        mUserType   = CacheUtil.getInstance().getLoginIdentity();

        // 创建商店排序对象
        mFirstShopEmployee = new ShopEmployeeVo();
        String shopName    = CacheUtil.getInstance().getShopFullName();
        mFirstShopEmployee.setName(shopName);
        mFirstShopEmployee.setDept_name(shopName);
        mFirstShopEmployee.setEmpid(System.currentTimeMillis() + "");

        mShopEmployeeVos = new ArrayList<ShopEmployeeVo>();
        mShopEmployeeVos.add(0, mFirstShopEmployee);

        mRvTeamContacts.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvTeamContacts.setLayoutManager(mLayoutManager);

        mTeamContactAdapter = new TeamContactsAdapter(TeamContactsActivity.this, mShopEmployeeVos);
        mRvTeamContacts.setAdapter(mTeamContactAdapter);

        //TODO: 1.服务器获得最近 5位联系人列表的客户列表

    }

    private void initListener() {
        //get team list
        TeamContactsController.getInstance().getTeamContacts(
                TeamContactsActivity.this,
                mUserID,
                mToken,
                mShopID,
                new GetTeamContactsListener() {
                    @Override
                    public void getContactsDone(List<TeamContactBean> teamContacts) {
                        List<ShopEmployeeVo> shopEmployeeVos = ShopEmployeeFactory.getInstance().
                                                                buildShopEmployees(teamContacts);
                        System.out.print("shopEmployeeVos:"+shopEmployeeVos);
                        List<String> strLetters = null;//首字母显示数组
                        List<String> empids     = null;//员工ID数组
                        if (null != shopEmployeeVos && !shopEmployeeVos.isEmpty()) {

                            strLetters = new ArrayList<String>();
                            empids = new ArrayList<>();
                            for (ShopEmployeeVo shopEmployeeVo : shopEmployeeVos) {
                                shopEmployeeVo.setShop_id(mShopID);
                                ShopEmployeeDBUtil.getInstance().addShopEmployee(shopEmployeeVo);
                                mShopEmployeeVos.add(shopEmployeeVo);

                                empids.add(shopEmployeeVo.getEmpid());
                                String deptID   = shopEmployeeVo.getDept_id()+"";
                                String deptName = shopEmployeeVo.getDept_name();
                                String sortLetter = null;
                                if(!TextUtils.isEmpty(deptName)){
                                    sortLetter = deptName.substring(0, 1);
                                }else {
                                    sortLetter = deptID.substring(0, 1);
                                }

                                //部门分类并消除相同部门
                                if (!TextUtils.isEmpty(sortLetter) && !strLetters.contains(sortLetter)) {
                                    strLetters.add(sortLetter);
                                } else {
                                    continue;
                                }
                            }

                            String[] sortArray = strLetters.toArray(new String[strLetters.size()]);
                            if (sortArray.length > 0) {
                                mAutoSideBar.setSortArray(sortArray);
                                mRlSideBar.addView(mAutoSideBar);
                            }

                            mTeamContactAdapter.updateListView(mShopEmployeeVos);
                        }

                        //发送查询客户是否在线请求
                        MsgEmpStatus msgEmpStatus = new MsgEmpStatus();
                        msgEmpStatus.setType(ProtocolMSG.MSG_ShopEmpStatus);
                        msgEmpStatus.setTimestamp(System.currentTimeMillis());
                        msgEmpStatus.setShopid(mShopID);
                        msgEmpStatus.setEmps(empids);

                        Gson gson = new Gson();
                        String jsonMsgEmpStatus = gson.toJson(msgEmpStatus, MsgEmpStatus.class);
                        LogUtil.getInstance().info(LogLevel.INFO, "jsonMsgEmpStatus:"+jsonMsgEmpStatus);
                        WebSocketManager.getInstance().sendMessage(jsonMsgEmpStatus);
                    }
                });

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeamContactsActivity.this.finish();
            }
        });

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(android.view.MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_team_search:
                        DialogUtil.getInstance().showToast(TeamContactsActivity.this, "search");
                        break;

                    case R.id.menu_team_jia:
                        Intent intent = new Intent(TeamContactsActivity.this, EmployeeAddActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
                        break;

                    case R.id.menu_team_edit:
                        Intent teamEdit = new Intent(TeamContactsActivity.this, TeamEditActivity.class);
                        teamEdit.putExtra("shop_id", mShopID);
                        TeamContactsActivity.this.startActivity(teamEdit);
                        break;
                }
                return true;
            }
        });

        mAutoSideBar.setOnTouchingLetterChangedListener(new AutoSideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = mTeamContactAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mRvTeamContacts.scrollToPosition(position);
                }
            }
        });

        mTeamContactAdapter.setOnItemClickListener(new RecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                DialogUtil.getInstance().showCustomToast(TeamContactsActivity.this, "TODO:进入客户聊天界面", Gravity.CENTER);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(mUserType == IdentityType.BUSINESS){
            getMenuInflater().inflate(R.menu.menu_team_for_business, menu);
        }else {
            getMenuInflater().inflate(R.menu.menu_team_for_waiter, menu);
        }
        return true;
    }

    private void addObservers() {
        MessageSubject.getInstance().addObserver(this, ProtocolMSG.MSG_ShopEmpStatus_RSP);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MessageSubject.getInstance().removeObserver(this, ProtocolMSG.MSG_ShopEmpStatus_RSP);
    }

    @Override
    public void receive(String message) {

        System.out.print("message:" + message);
        if(TextUtils.isEmpty(message)){
            return ;
        }

        Gson gson = null;
        if(null == gson )
            gson = new Gson();

        try {
            JSONObject messageObj = new JSONObject(message);
            int type = messageObj.getInt("type");

            if (type == ProtocolMSG.MSG_ShopEmpStatus_RSP) {
                MsgEmpStatusRSP msgEmpStatusRSP = gson.fromJson(message, MsgEmpStatusRSP.class);
                if(null != msgEmpStatusRSP && mShopID.equals(msgEmpStatusRSP.getShopid())){
                    //TODO:获取在线状态后更新界面
                    List<EmpStatusRecord> empStatusRecords = msgEmpStatusRSP.getResult();
                    Map<String, EmpStatusRecord> empStatusRecordMap = new HashMap<>();

                    //接收服务器返回在线员工数组
                    for(EmpStatusRecord empStatusRecord : empStatusRecords){
                        empStatusRecordMap.put(empStatusRecord.getEmpid(), empStatusRecord);
                    }

                    //接收服务器返回在线员工数组
                    for(int i=0; i< mShopEmployeeVos.size(); i++){
                        ShopEmployeeVo shopEmployeeVo = mShopEmployeeVos.get(i);
                        String empID = shopEmployeeVo.getEmpid();
                        if(empStatusRecordMap.containsKey(empID)){
                            EmpStatusRecord empStatusRecord = empStatusRecordMap.get(empID);

                            //获得员工是否服务器在线
                            if(empStatusRecord.getOnlinestatus() == OnlineStatus.ONLINE.getValue()){
                                shopEmployeeVo.setOnline_status(OnlineStatus.ONLINE);

                                //获得登录时间
                                Long lastLoginTime = empStatusRecord.getLogintimestamp();
                                shopEmployeeVo.setLastOnLineTime(lastLoginTime);

                            } else {
                                shopEmployeeVo.setOnline_status(OnlineStatus.OFFLINE);
                            }

                            //获得员工是否工作中
                            if(empStatusRecord.getWorkstatus() == WorkStatus.ONWORK.getValue()){
                                shopEmployeeVo.setWork_status(WorkStatus.ONWORK);
                            } else {
                                shopEmployeeVo.setWork_status(WorkStatus.OFFWORK);
                            }
                            //更新数据库
                            ShopEmployeeDBUtil.getInstance().addShopEmployee(shopEmployeeVo);
                            mShopEmployeeVos.remove(i);
                            mShopEmployeeVos.add(i, shopEmployeeVo);
                        }
                    }
                    mTeamContactAdapter.updateListView(mShopEmployeeVos);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(Constants.ZKJINSHI_BASE_TAG, TAG + ".onNetReceiveSucceed()->message:" + message);
    }

}
