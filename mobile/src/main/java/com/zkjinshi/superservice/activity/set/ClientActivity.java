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
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.net.core.WebSocketManager;
import com.zkjinshi.base.net.observer.IMessageObserver;
import com.zkjinshi.base.net.observer.MessageSubject;
import com.zkjinshi.base.net.protocol.ProtocolMSG;
import com.zkjinshi.base.util.Constants;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.ContactsSortAdapter;
import com.zkjinshi.superservice.bean.ClientDetailBean;
import com.zkjinshi.superservice.entity.EmpStatusRecord;
import com.zkjinshi.superservice.entity.MsgEmpStatusRSP;
import com.zkjinshi.superservice.entity.MsgUserOnlineStatus;
import com.zkjinshi.superservice.entity.MsgUserOnlineStatusRSP;
import com.zkjinshi.superservice.entity.UserOnlineStatusRecord;
import com.zkjinshi.superservice.factory.ClientFactory;
import com.zkjinshi.superservice.factory.SortModelFactory;
import com.zkjinshi.superservice.listener.RecyclerItemClickListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestListener;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.sqlite.ClientDBUtil;
import com.zkjinshi.superservice.sqlite.ShopEmployeeDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.PinyinComparator;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.view.SideBar;
import com.zkjinshi.superservice.vo.ClientVo;
import com.zkjinshi.superservice.vo.ContactType;
import com.zkjinshi.superservice.vo.OnlineStatus;
import com.zkjinshi.superservice.vo.ShopEmployeeVo;
import com.zkjinshi.superservice.vo.SortModel;
import com.zkjinshi.superservice.vo.WorkStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 联系人列表显示， 提供字母快速进入和查找联系人功能
 * 开发者：vincent
 * 日期：2015/9/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientActivity extends AppCompatActivity implements IMessageObserver{

    private final static String TAG = ClientActivity.class.getSimpleName();

    private String      mUserID;
    private String      mToken;
    private String      mShopID;
    private Toolbar     mToolbar;
    private TextView    mTvCenterTitle;
    private SideBar     mSideBar;
    private TextView    mTvDialog;

    private RecyclerView        mRcvContacts;
    private LinearLayoutManager mLayoutManager;

    private List<SortModel>         mAllContactsList;
    private List<SortModel>         mLocalContacts;
    private Map<String, ClientVo>   mLocalClientMap;
    private PinyinComparator     pinyinComparator;
    private ContactsSortAdapter  mContactsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

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
        mTvCenterTitle.setText(getString(R.string.my_clients));

        mSideBar     = (SideBar)    findViewById(R.id.sb_sidebar);
        mTvDialog    = (TextView)   findViewById(R.id.tv_dialog);
        mRcvContacts = (RecyclerView) findViewById(R.id.rcv_contacts);
    }

    private void initData() {
        mUserID = CacheUtil.getInstance().getUserId();
        mToken  = CacheUtil.getInstance().getToken();
        mShopID = CacheUtil.getInstance().getShopID();

        /** 给ListView设置adapter **/
        mSideBar.setTextView(mTvDialog);

        mLocalClientMap  = new HashMap<>();
        mAllContactsList = new ArrayList<>();
        pinyinComparator = new PinyinComparator();
        Collections.sort(mAllContactsList, pinyinComparator);// 根据a-z进行排序源数据
        mContactsAdapter = new ContactsSortAdapter(this, mAllContactsList);
        mRcvContacts.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRcvContacts.setLayoutManager(mLayoutManager);
        mRcvContacts.setAdapter(mContactsAdapter);
    }

    private void initListener() {
        /** 后退界面 */
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientActivity.this.finish();
            }
        });

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(android.view.MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_client_search:
                        DialogUtil.getInstance().showToast(ClientActivity.this, "search");
                        break;

                    case R.id.menu_client_jia:
                        Intent clientSelect = new Intent(ClientActivity.this, ClientSelectActivity.class);
                        ClientActivity.this.startActivity(clientSelect);
                        break;
                }
                return true;
            }
        });

        //设置右侧[A-Z]快速导航栏触摸监听
        mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = mContactsAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mRcvContacts.scrollToPosition(position);
                }
            }
        });

        /** 我的客人条目点击事件 */
        mContactsAdapter.setOnItemClickListener(new RecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                SortModel sortModel = mAllContactsList.get(postion);
                if (sortModel.getContactType().getValue() == ContactType.NORMAL.getValue()) {
                    String phoneNumber = sortModel.getNumber();
                    Intent clientDetail = new Intent(ClientActivity.this, ClientDetailActivity.class);
                    clientDetail.putExtra("phone_number", phoneNumber);
                    ClientActivity.this.startActivity(clientDetail);
                } else {
                    DialogUtil.getInstance().showCustomToast(ClientActivity.this, "当前客户为本地联系人，无详细信息。", Gravity.CENTER);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_client, menu);
        return true;
    }

    @Override
    protected void onResume() {
        //TODO: 1.获得本地最近5位联系人列表的客户列表
        showLocalUnNormalClientList();
        showMyClientList(mUserID, mToken, mShopID);
        super.onResume();
    }

    /**
     * 获取我的客户列表
     * @param userID
     * @param token
     * @param shopID
     */
    public void showMyClientList(String userID, String token, String shopID) {
        NetRequest netRequest = new NetRequest(ProtocolUtil.getShopUserListUrl());
        HashMap<String,String> bizMap = new HashMap<>();
        bizMap.put("salesid", userID);
        bizMap.put("token", token);
        bizMap.put("shopid", shopID);
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(this, netRequest, NetResponse.class);
        netRequestTask.methodType     = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new NetRequestListener() {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                DialogUtil.getInstance().cancelProgressDialog();
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
                DialogUtil.getInstance().showToast(ClientActivity.this, "网络访问失败，稍候再试。");

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
                    //TODO: 获取客户列表失败请稍后再试
                    DialogUtil.getInstance().showToast(ClientActivity.this, "用户操作权限不够，请重新登录。");
                } else {
                    Gson gson = new Gson();
                    List<ClientDetailBean> clientDetailBeans = gson.fromJson(jsonResult,
                            new TypeToken<ArrayList<ClientDetailBean>>() {
                            }.getType());
                    if (null != clientDetailBeans && !clientDetailBeans.isEmpty()) {
                        List<ClientVo> clientVos = ClientFactory.getInstance().buildClientVosByClientBeans(clientDetailBeans);

                        for (ClientVo clientVo : clientVos) {
                            clientVo.setContactType(ContactType.NORMAL);
                            if (!ClientDBUtil.getInstance().isClientExistByUserID(clientVo.getUserid())) {
                                ClientDBUtil.getInstance().addClient(clientVo);
                            }

                            String userid = clientVo.getUserid();
                            if (mLocalClientMap.containsKey(userid)) {
                                mAllContactsList.remove(mLocalClientMap.get(userid));
                            }
                            mLocalClientMap.put(userid, clientVo);
                            SortModel sortModel = SortModelFactory.getInstance().buildSortModelByMyClientVo(clientVo);
                            mAllContactsList.add(sortModel);
                        }

                        ClientActivity.this.updateListView(mAllContactsList);

                        //获得需要查询是否在线的userID的集合
                        if (!mLocalClientMap.isEmpty()) {
                            List<String> keyList = new ArrayList<>(mLocalClientMap.keySet());

                            MsgUserOnlineStatus msgUserOnlineStatus = new MsgUserOnlineStatus();
                            msgUserOnlineStatus.setType(ProtocolMSG.MSG_UserOnlineStatus);
                            msgUserOnlineStatus.setTimestamp(System.currentTimeMillis());
                            msgUserOnlineStatus.setEmpid(mUserID);
                            msgUserOnlineStatus.setShopid(mShopID);
                            msgUserOnlineStatus.setClients(keyList);

                            if (null == gson)
                                gson = new Gson();

                            String jsonMsg = gson.toJson(msgUserOnlineStatus, MsgUserOnlineStatus.class);
                            WebSocketManager.getInstance().sendMessage(jsonMsg);
                        }
                    }
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

    /**
     * 获取本地非正式客人列表
     * @return
     */
    public void showLocalUnNormalClientList() {
        if(null != mAllContactsList && !mAllContactsList.isEmpty()){
            mAllContactsList.removeAll(mAllContactsList);
        }

        List<ClientVo> clientVos = ClientDBUtil.getInstance().queryUnNormalClient();
        for(ClientVo clientVo : clientVos){
            mLocalClientMap.put(clientVo.getUserid(), clientVo);
        }

        mLocalContacts =  SortModelFactory.getInstance().convertClientVos2SortModels(clientVos);
        if(null != mLocalContacts && !mLocalContacts.isEmpty()){
            mAllContactsList.addAll(mLocalContacts);
        }
        this.updateListView(mAllContactsList);
    }

    private void updateListView(List<SortModel> mAllContactsList) {
        if(null == mAllContactsList || mAllContactsList.isEmpty()){
            mTvDialog.setVisibility(View.VISIBLE);
            mTvDialog.setText(ClientActivity.this.getString(R.string.current_none));

        }else {
            // 根据a-z进行排序源数据
            mTvDialog.setVisibility(View.GONE);
            Collections.sort(mAllContactsList, pinyinComparator);
            mContactsAdapter.updateListView(mAllContactsList);
        }
    }

    private void addObservers() {
        MessageSubject.getInstance().addObserver(ClientActivity.this, ProtocolMSG.MSG_UserOnlineStatus_RSP);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MessageSubject.getInstance().removeObserver(this, ProtocolMSG.MSG_UserOnlineStatus_RSP);
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

            if (type == ProtocolMSG.MSG_UserOnlineStatus_RSP) {
                MsgUserOnlineStatusRSP userOnlineStatusRSP = gson.fromJson(message, MsgUserOnlineStatusRSP.class);
                List<UserOnlineStatusRecord> userOnlineStatusRecords =  userOnlineStatusRSP.getResult();
                if(null != userOnlineStatusRecords && !userOnlineStatusRecords.isEmpty()){
                    mAllContactsList.removeAll(mAllContactsList);
                    for(UserOnlineStatusRecord userOnlineStatusRecord : userOnlineStatusRecords){
                        String userID = userOnlineStatusRecord.getUserid();
                        if(mLocalClientMap.containsKey(userID)){
                            int onlineStatus = userOnlineStatusRecord.getOnlinestatus();
                            ClientVo clientVo = mLocalClientMap.get(userID);
                            clientVo.setIsOnline(OnlineStatus.OFFLINE.getValue() == onlineStatus ?
                                                      OnlineStatus.OFFLINE : OnlineStatus.ONLINE);
                            SortModel sortModel = SortModelFactory.getInstance().buildSortModelByMyClientVo(clientVo);
                            mAllContactsList.add(sortModel);
                        }
                    }

                    this.updateListView(mAllContactsList);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(Constants.ZKJINSHI_BASE_TAG, TAG + ".onNetReceiveSucceed()->message:" + message);
    }
}
