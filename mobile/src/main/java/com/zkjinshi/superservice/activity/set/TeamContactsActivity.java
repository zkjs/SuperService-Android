package com.zkjinshi.superservice.activity.set;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import com.google.gson.JsonArray;
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
import com.zkjinshi.superservice.ServiceApplication;
import com.zkjinshi.superservice.adapter.TeamContactsSortAdapter;
import com.zkjinshi.superservice.bean.TeamContactBean;
import com.zkjinshi.superservice.entity.MsgCustomerServiceImgChat;
import com.zkjinshi.superservice.entity.MsgCustomerServiceMediaChat;
import com.zkjinshi.superservice.entity.MsgCustomerServiceTextChat;
import com.zkjinshi.superservice.entity.MsgEmpStatus;
import com.zkjinshi.superservice.entity.MsgPushTriggerLocNotificationM2S;
import com.zkjinshi.superservice.factory.MessageFactory;
import com.zkjinshi.superservice.factory.ShopEmployeeFactory;
import com.zkjinshi.superservice.factory.SortModelFactory;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestListener;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.notification.NotificationHelper;
import com.zkjinshi.superservice.sqlite.ChatRoomDBUtil;
import com.zkjinshi.superservice.sqlite.MessageDBUtil;
import com.zkjinshi.superservice.sqlite.ShopEmployeeDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.CharacterParser;
import com.zkjinshi.superservice.utils.FileUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.utils.SortKeyUtil;
import com.zkjinshi.superservice.view.AutoSideBar;
import com.zkjinshi.superservice.vo.ContactType;
import com.zkjinshi.superservice.vo.MessageVo;
import com.zkjinshi.superservice.vo.ShopEmployeeVo;
import com.zkjinshi.superservice.vo.SortModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private RecyclerView    mRvTeamContacts;
    private RelativeLayout  mRlSideBar;
    private TextView        mTvDialog;
    private AutoSideBar     mAutoSideBar;

    private LinearLayoutManager     mLayoutManager;
    private TeamContactsSortAdapter mTeamContactAdapter;

    private SortModel               mShopSortModel;
    private List<SortModel>         mTeamSortModels;

    private String mUserID;
    private String mShopID;
    private String mToken;


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
        mToolbar.setTitle("团队管理");
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setNavigationIcon(R.drawable.ic_fanhui);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        // 创建商店排序对象
        mShopSortModel = new SortModel();
        mShopSortModel.setContactType(ContactType.UNNORMAL);
        mShopSortModel.setName(CacheUtil.getInstance().getShopFullName());
        if(!TextUtils.isEmpty(mShopID)){
            mShopSortModel.setAvatarUrl(ProtocolUtil.getShopLogoUrl(mShopID));
        }
        String sortLetter = SortKeyUtil.getSortLetter(CacheUtil.getInstance().getShopFullName(), CharacterParser.getInstance());
        mShopSortModel.setSortKey(sortLetter);
        mShopSortModel.setSortLetters(sortLetter); //此处为角色部门全称

        mTeamSortModels = new ArrayList<SortModel>();
        mTeamSortModels.add(0, mShopSortModel);

        mRvTeamContacts.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvTeamContacts.setLayoutManager(mLayoutManager);

        //TODO: 1.服务器获得最近 5位联系人列表的客户列表
        getTeamList(mUserID, mToken, mShopID);
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
                        DialogUtil.getInstance().showToast(TeamContactsActivity.this, "search");
                        break;

                    case R.id.menu_team_jia:

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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_team, menu);
        return true;
    }

    /**
     * 获取团队联系人列表
     * @param userID
     * @param token
     * @param shopID
     */
    public void getTeamList(String userID, String token, String shopID) {
        DialogUtil.getInstance().showProgressDialog(this);
        NetRequest netRequest = new NetRequest(ProtocolUtil.getTeamListUrl());
        HashMap<String,String> bizMap = new HashMap<>();
        bizMap.put("salesid", userID);
        bizMap.put("token", token);
        bizMap.put("shopid", shopID);
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(this, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new NetRequestListener() {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                DialogUtil.getInstance().cancelProgressDialog();
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
                DialogUtil.getInstance().showToast(TeamContactsActivity.this, "网络访问失败，稍候再试。");
            }

            @Override
            public void onNetworkRequestCancelled() {
                DialogUtil.getInstance().cancelProgressDialog();
            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                DialogUtil.getInstance().cancelProgressDialog();
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                String jsonResult = result.rawResult;
                if (result.rawResult.contains("set") || jsonResult.contains("err")) {
                    DialogUtil.getInstance().showToast(TeamContactsActivity.this, "获取团队联系人失败");

                } else {
                    Gson gson = new Gson();
                    List<TeamContactBean> teamContactBeans = gson.fromJson(jsonResult,
                            new TypeToken<ArrayList<TeamContactBean>>() {}.getType());

                    /** add to local db */
                    List<ShopEmployeeVo> shopEmployeeVos = ShopEmployeeFactory.getInstance().buildShopEmployees(teamContactBeans);
//                    if(!shopEmployeeVos.isEmpty()){
//                        ShopEmployeeDBUtil.getInstance().batchAddShopEmployees(shopEmployeeVos);
//                    }
                    JSONArray empids = null;
                    for(ShopEmployeeVo shopEmployeeVo : shopEmployeeVos){
                        ShopEmployeeDBUtil.getInstance().addShopEmployee(shopEmployeeVo);
                        if(null == empids){
                            empids = new JSONArray();
                        }
                        empids.put(shopEmployeeVo.getEmpid());
                    }

                    if (null != mTeamContactAdapter) {
                        mTeamContactAdapter = null;
                    }
                    List<SortModel> sortModels = SortModelFactory.getInstance().convertTeamContacts2SortModels(teamContactBeans);
                    if (null != sortModels && !sortModels.isEmpty()) {
                        mTeamSortModels.addAll(sortModels);
                        List<String> strLetters = new ArrayList<String>();
                        for (SortModel sortModel : mTeamSortModels) {
                            String sortLetter = sortModel.getSortLetters().substring(0, 1);
                            //商家本身
                            if (sortModel == mTeamSortModels.get(0)) {
                                continue;
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
                    }

                    mTeamContactAdapter = new TeamContactsSortAdapter(TeamContactsActivity.this, mTeamSortModels);
                    mRvTeamContacts.setAdapter(mTeamContactAdapter);
                    //发送查询客户是否在线请求
                    MsgEmpStatus msgEmpStatus = new MsgEmpStatus();
                    msgEmpStatus.setType(ProtocolMSG.MSG_ShopEmpStatus);
                    msgEmpStatus.setTimestamp(System.currentTimeMillis());
                    msgEmpStatus.setShopid(mShopID);
                    msgEmpStatus.setEmps(empids);

                    String jsonMsgEmpStatus = gson.toJson(msgEmpStatus);
                    WebSocketManager.getInstance().sendMessage(jsonMsgEmpStatus);

                }
            }

            @Override
            public void beforeNetworkRequestStart() {
                //网络请求前
            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }

    private void addObservers() {
        MessageSubject.getInstance().addObserver(this, ProtocolMSG.MSG_ShopEmpStatus_RSP);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MessageSubject.getInstance().addObserver(this, ProtocolMSG.MSG_ShopEmpStatus_RSP);
    }

    @Override
    public void receive(String message) {

        System.out.print("message:"+message);
        if(TextUtils.isEmpty(message)){
            return ;
        }
        Gson gson = null;
        try {
            JSONObject messageObj = new JSONObject(message);
            int type = messageObj.getInt("type");

            if (type == ProtocolMSG.MSG_ClientLogin_RSP) {
                return;
            }

            if (type == ProtocolMSG.MSG_ShopEmpStatus_RSP) {//重复登录
                System.out.print("message:"+message);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(Constants.ZKJINSHI_BASE_TAG, TAG + ".onNetReceiveSucceed()->message:" + message);

    }

}
