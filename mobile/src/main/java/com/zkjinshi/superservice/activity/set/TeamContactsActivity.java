package com.zkjinshi.superservice.activity.set;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
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
import com.zkjinshi.superservice.activity.chat.ChatActivity;
import com.zkjinshi.superservice.adapter.TeamContactsAdapter;
import com.zkjinshi.superservice.bean.TeamContactBean;
import com.zkjinshi.superservice.entity.EmpStatusRecord;
import com.zkjinshi.superservice.entity.MsgBuildSession;
import com.zkjinshi.superservice.entity.MsgBuildSessionRSP;
import com.zkjinshi.superservice.entity.MsgEmpStatus;
import com.zkjinshi.superservice.entity.MsgEmpStatusRSP;
import com.zkjinshi.superservice.entity.MsgIMSessionUser;
import com.zkjinshi.superservice.entity.MsgSessionMemberInfo;
import com.zkjinshi.superservice.entity.MsgShopSessionSearch;
import com.zkjinshi.superservice.entity.MsgShopSessionSearchRSP;
import com.zkjinshi.superservice.factory.ShopEmployeeFactory;
import com.zkjinshi.superservice.listener.GetTeamContactsListener;
import com.zkjinshi.superservice.listener.RecyclerItemClickListener;
import com.zkjinshi.superservice.sqlite.MessageDBUtil;
import com.zkjinshi.superservice.sqlite.ShopEmployeeDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.view.AutoSideBar;
import com.zkjinshi.superservice.vo.IdentityType;
import com.zkjinshi.superservice.vo.MsgAdmin;
import com.zkjinshi.superservice.vo.OnlineStatus;
import com.zkjinshi.superservice.vo.ShopEmployeeVo;
import com.zkjinshi.superservice.vo.WorkStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

    public static final int ADD_REQUEST_CODE = 1;
    public static final int GET_CONTACTS_ONLINE_STATUS = 0X11;//获取团队成员是否在线状态

    private Toolbar         mToolbar;
    private TextView        mTvCenterTitle;
    private RecyclerView    mRvTeamContacts;
    private RelativeLayout  mRlSideBar;
    private TextView        mTvDialog;
    private AutoSideBar     mAutoSideBar;

    private LinearLayoutManager     mLayoutManager;
    private TeamContactsAdapter     mTeamContactAdapter;
    private List<ShopEmployeeVo>    mShopEmployeeVos;
    private ShopEmployeeVo          mFirstShopEmployee;

    private String mUserID;
    private String mShopID;
    private String mToken;
    private String mUserName;

    private IdentityType mUserType;

    private MsgIMSessionUser mFromUser;
    private MsgIMSessionUser mToUser;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_CONTACTS_ONLINE_STATUS:
                    String jsonMsg = (String) msg.obj;
                    WebSocketManager.getInstance().sendMessage(jsonMsg);
                    break;
            }
        }
    };

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
        mToolbar.setNavigationIcon(R.mipmap.ic_fanhui);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTvCenterTitle = (TextView) findViewById(R.id.tv_center_title);
        mTvCenterTitle.setText(getString(R.string.team));

        mRvTeamContacts = (RecyclerView)     findViewById(R.id.rcv_team_contacts);
        mRlSideBar      = (RelativeLayout)   findViewById(R.id.rl_side_bar);
        mTvDialog       = (TextView)         findViewById(R.id.tv_dialog);
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
        mUserName   = CacheUtil.getInstance().getUserName();
        mUserType   = CacheUtil.getInstance().getLoginIdentity();

        mRvTeamContacts.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvTeamContacts.setLayoutManager(mLayoutManager);

        // 创建商店排序对象
        mFirstShopEmployee = new ShopEmployeeVo();
        String shopName    = CacheUtil.getInstance().getShopFullName();
        mFirstShopEmployee.setName(shopName);
        mFirstShopEmployee.setDept_name(shopName);
        mFirstShopEmployee.setEmpid(System.currentTimeMillis() + "");

        mShopEmployeeVos = new ArrayList<>();
        mTeamContactAdapter = new TeamContactsAdapter(TeamContactsActivity.this,
                                                               mShopEmployeeVos);
        mRvTeamContacts.setAdapter(mTeamContactAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO: 1.服务器获得最近 5位联系人列表的客户列表
        showDataList();
    }

    /**
     * 初始化待显示数据并展示
     */
    private void showDataList() {
        //获取团队列表
        DialogUtil.getInstance().showProgressDialog(TeamContactsActivity.this);
        TeamContactsController.getInstance().getTeamContacts(
                TeamContactsActivity.this,
                mUserID, mToken, mShopID, new GetTeamContactsListener() {
                    @Override
                    public void getContactsDone(List<TeamContactBean> teamContacts) {
                        List<ShopEmployeeVo> shopEmployeeVos = ShopEmployeeDBUtil.getInstance().queryTeamByShopID(mShopID);
                        List<String> strLetters = new ArrayList<>();//首字母显示数组
                        List<String> empids = new ArrayList<>();//员工ID数组

                        if (null != shopEmployeeVos && !shopEmployeeVos.isEmpty()) {
                            Iterator<ShopEmployeeVo> shopEmployeeVoIterator = shopEmployeeVos.iterator();
                            while (shopEmployeeVoIterator.hasNext()) {
                                ShopEmployeeVo shopEmployeeVo = shopEmployeeVoIterator.next();
                                String empID = shopEmployeeVo.getEmpid();
                                if (empID.equals(mUserID)) {
                                    shopEmployeeVoIterator.remove();
                                } else {
                                    shopEmployeeVo.setOnline_status(OnlineStatus.OFFLINE);
                                    continue;
                                }
                            }

                            if(!mShopEmployeeVos.contains(mFirstShopEmployee)){
                                mShopEmployeeVos.add(0, mFirstShopEmployee);
                            }
                            mShopEmployeeVos.addAll(shopEmployeeVos);

                            for (ShopEmployeeVo shopEmployeeVo : shopEmployeeVos) {
                                empids.add(shopEmployeeVo.getEmpid());
                                String deptID   = shopEmployeeVo.getDept_id() + "";
                                String deptName = shopEmployeeVo.getDept_name();
                                String sortLetter = null;
                                if (!TextUtils.isEmpty(deptName)) {
                                    sortLetter = deptName.substring(0, 1);
                                } else {
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
                                //移除之前设置
                                mRlSideBar.removeAllViews();
                                mRlSideBar.addView(mAutoSideBar);
                            }

                            DialogUtil.getInstance().cancelProgressDialog();
                            mTeamContactAdapter.updateListView(mShopEmployeeVos);
                        }

                        //发送查询团队是否在线请求
                        MsgEmpStatus msgEmpStatus = new MsgEmpStatus();
                        msgEmpStatus.setType(ProtocolMSG.MSG_ShopEmpStatus);
                        msgEmpStatus.setTimestamp(System.currentTimeMillis());
                        msgEmpStatus.setShopid(mShopID);
                        msgEmpStatus.setEmps(empids);

                        Gson gson = new Gson();
                        String jsonMsgEmpStatus = gson.toJson(msgEmpStatus, MsgEmpStatus.class);
                        Message getOnlineMsg = Message.obtain();
                        getOnlineMsg.what = GET_CONTACTS_ONLINE_STATUS;
                        getOnlineMsg.obj  = jsonMsgEmpStatus;
                        handler.sendMessageDelayed(getOnlineMsg, 2000);
                        //更新数据库数据
                        ShopEmployeeDBUtil.getInstance().batchAddShopEmployees(shopEmployeeVos);
                    }

                    @Override
                    public void getContactsFailed() {
                        //获取在线数据失败更新
                        List<ShopEmployeeVo> shopEmployeeVos = ShopEmployeeDBUtil.getInstance().queryTeamByShopID(mShopID);
                        if(shopEmployeeVos != null && !shopEmployeeVos.isEmpty()){
                            mShopEmployeeVos.addAll(shopEmployeeVos);
                        }
                        if(!mShopEmployeeVos.contains(mFirstShopEmployee)){
                            mShopEmployeeVos.add(0, mFirstShopEmployee);
                        }
                        mTeamContactAdapter.updateListView(mShopEmployeeVos);
                        DialogUtil.getInstance().cancelProgressDialog();
                    }
                }
        );
    }

    private void initListener() {
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
                        //TODO: search 团队成员检索
                        break;

                    case R.id.menu_team_jia:
                        Intent intent = new Intent(TeamContactsActivity.this, EmployeeAddActivity.class);
                        startActivityForResult(intent, ADD_REQUEST_CODE);
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
                ShopEmployeeVo shopEmployeeVo = mShopEmployeeVos.get(postion);
                String userId    = shopEmployeeVo.getEmpid();
                String toName  = shopEmployeeVo.getName();
                String shopName = CacheUtil.getInstance().getShopFullName();
                Intent intent = new Intent(TeamContactsActivity.this, ChatActivity.class);
                intent.putExtra(com.zkjinshi.superservice.utils.Constants.EXTRA_USER_ID, userId);
                if (!TextUtils.isEmpty(mShopID)) {
                    intent.putExtra(com.zkjinshi.superservice.utils.Constants.EXTRA_SHOP_ID,mShopID);
                }
                intent.putExtra(com.zkjinshi.superservice.utils.Constants.EXTRA_SHOP_NAME, shopName);
                if(!TextUtils.isEmpty(toName)){
                    intent.putExtra(com.zkjinshi.superservice.utils.Constants.EXTRA_TO_NAME, toName);
                }
                intent.putExtra(com.zkjinshi.superservice.utils.Constants.EXTRA_FROM_NAME, CacheUtil.getInstance().getUserName());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            if (ADD_REQUEST_CODE == requestCode) {
                showDataList();
            }
        }
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
        MessageSubject.getInstance().addObserver(this, ProtocolMSG.MSG_ShopSessionSearch_RSP);
        MessageSubject.getInstance().addObserver(this, ProtocolMSG.MSG_BuildSession_RSP);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MessageSubject.getInstance().removeObserver(this, ProtocolMSG.MSG_ShopEmpStatus_RSP);
        MessageSubject.getInstance().removeObserver(this, ProtocolMSG.MSG_ShopSessionSearch_RSP);
        MessageSubject.getInstance().removeObserver(this, ProtocolMSG.MSG_BuildSession_RSP);
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
                                Long LatestOnlineTime = empStatusRecord.getLogintimestamp();
                                shopEmployeeVo.setLastOnLineTime(LatestOnlineTime);

                            } else {
                                shopEmployeeVo.setOnline_status(OnlineStatus.OFFLINE);
                                Long LatestOnlineTime = ShopEmployeeDBUtil.getInstance().queryLatestOnlineByEmpID(empID);
                                shopEmployeeVo.setLastOnLineTime(LatestOnlineTime);
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

            //会话查询协议
            if (type == ProtocolMSG.MSG_ShopSessionSearch_RSP) {
                //TODO: 判断服务器此sessionID是否存在
                MsgShopSessionSearchRSP msgShopSessionSearchRSP = gson.fromJson(message,
                        MsgShopSessionSearchRSP.class);
                int result = msgShopSessionSearchRSP.getResult();
                List<MsgSessionMemberInfo> msgSessionMemberInfos = msgShopSessionSearchRSP.getDetail();

                //成功
                if(result <= 0 && null != msgSessionMemberInfos && !msgSessionMemberInfos.isEmpty()){
                    //开启单聊界面
                    String sessionID = msgShopSessionSearchRSP.getSessionid();
                    String shopID    = msgShopSessionSearchRSP.getShopid();
                   /* SessionIDBuilder.getInstance().goSession(TeamContactsActivity.this,
                                             mShopID, sessionID, mToUser.getUsername());*/
                }else {
                    if(null == mFromUser){
                        mFromUser = new MsgIMSessionUser();
                        mFromUser.setShopid(mShopID);
                        mFromUser.setUserid(mUserID);
                        mFromUser.setUsername(mUserName);
                        mFromUser.setIsadmin(MsgAdmin.ISADMIN.getValue());
                    }
                    if(null != mFromUser && null != mToUser){
                        String sessionName = mFromUser.getUsername() + mToUser.getUsername() + "会话";
                        String fromID = mFromUser.getUserid();
                        String toID   = mToUser.getUserid();

                        String sessionID = SessionIDBuilder.getInstance().buildSingleSessionID(mShopID, fromID, toID);
                        sendBuildNewSessionMsg(mShopID, sessionID, sessionName, mFromUser, mToUser);
                    }
                }
            }

            //建立会话协议回复
            if (type == ProtocolMSG.MSG_BuildSession_RSP) {
                MsgBuildSessionRSP msgBuildSessionRSP = gson.fromJson(message, MsgBuildSessionRSP.class);
                //创建会话成功 进入聊天界面
                if(msgBuildSessionRSP.getResult() <= 0){
                    String sessionID = msgBuildSessionRSP.getSessionid();
                  /*  SessionIDBuilder.getInstance().goSession(TeamContactsActivity.this, mShopID, sessionID, mToUser.getUsername());*/
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(Constants.ZKJINSHI_BASE_TAG, TAG + ".onNetReceiveSucceed()->message:" + message);
    }

    /**
     * 发送创建新会话请求
     * @param shopID
     * @param sessionID
     * @param sessionName
     * @param fromUser
     * @param toUser
     */
    private void sendBuildNewSessionMsg(String shopID, String sessionID, String sessionName,
                                   MsgIMSessionUser fromUser, MsgIMSessionUser toUser) {

        List<MsgIMSessionUser> msgIMSessionUsers = new ArrayList<>();
        MsgBuildSession msgBuildSession = new MsgBuildSession();
        msgBuildSession.setType(ProtocolMSG.MSG_BuildSession);
        msgBuildSession.setTimestamp(System.currentTimeMillis());
        msgBuildSession.setSessionid(sessionID);
        msgBuildSession.setSessionname(sessionName);
        msgBuildSession.setShopid(shopID);
        msgBuildSession.setUserid(fromUser.getUserid());

        msgIMSessionUsers.add(fromUser);
        msgIMSessionUsers.add(toUser);
        msgBuildSession.setDetail(msgIMSessionUsers);

        Gson gson = new Gson();
        String msgBuildSessionJson = gson.toJson(msgBuildSession, MsgBuildSession.class);
        WebSocketManager.getInstance().sendMessage(msgBuildSessionJson);

    }

}
