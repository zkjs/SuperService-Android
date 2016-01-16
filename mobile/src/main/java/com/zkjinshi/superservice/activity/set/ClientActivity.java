package com.zkjinshi.superservice.activity.set;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.chat.single.ChatActivity;
import com.zkjinshi.superservice.adapter.ContactsSortAdapter;
import com.zkjinshi.superservice.factory.ClientFactory;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ClientComparator;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.view.CustomExtDialog;
import com.zkjinshi.superservice.view.SideBar;
import com.zkjinshi.superservice.vo.ClientContactVo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 联系人列表显示
 * 开发者：vincent
 * 日期：2015/9/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientActivity extends AppCompatActivity{

    private final static String TAG = ClientActivity.class.getSimpleName();

    private boolean     mChooseOrderPerson;
    private String      mUserID;
    private String      mToken;
    private String      mShopID;
    private Toolbar     mToolbar;
    private TextView    mTvCenterTitle;
    private SideBar     mSideBar;
    private TextView    mTvDialog;
    private ListView    mRcvContacts;

    private List<ClientContactVo>   mAllContactsList;
//    private Map<String, ContactVo>  mLocalClientMap;
    private ClientComparator        mClientCompatator;
    private ContactsSortAdapter     mContactsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
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
        mTvCenterTitle.setText(getString(R.string.my_clients));

        mSideBar     = (SideBar)  findViewById(R.id.sb_sidebar);
        mTvDialog    = (TextView) findViewById(R.id.tv_dialog);
        mRcvContacts = (ListView) findViewById(R.id.rcv_contacts);
    }

    private void initData() {

        mChooseOrderPerson = getIntent().getBooleanExtra("choose_order_person", false);

        mUserID = CacheUtil.getInstance().getUserId();
        mToken  = CacheUtil.getInstance().getToken();
        mShopID = CacheUtil.getInstance().getShopID();

        /** 给ListView设置adapter **/
        mSideBar.setTextView(mTvDialog);

//        mLocalClientMap  = new HashMap<>();
        mAllContactsList  = new ArrayList<>();
        mClientCompatator = new ClientComparator();
        mContactsAdapter  = new ContactsSortAdapter(this, mAllContactsList);

        mRcvContacts.setAdapter(mContactsAdapter);
        showMyClientList(mUserID, mToken, mShopID);
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
                    mRcvContacts.setSelection(position);
                }
            }
        });

        /** 我的客人条目点击事件 */
        mRcvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ClientContactVo contact = mAllContactsList.get(position);
                final String clientId = contact.getUserid();
                final String clientName = contact.getUsername();
                final String clientPhone = contact.getPhone();

                if (mChooseOrderPerson) {
                    final CustomExtDialog.Builder customExtBuilder = new CustomExtDialog.Builder(ClientActivity.this);
                    customExtBuilder.setTitle(getString(R.string.add_order_person));
                    customExtBuilder.setMessage(getString(R.string.add_client) + clientName + getString(R.string.order_person) + "?");
                    customExtBuilder.setGravity(Gravity.CENTER);
                    customExtBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    customExtBuilder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent data = new Intent();
                            data.putExtra("client_id", clientId);
                            data.putExtra("client_name", clientName);
                            data.putExtra("client_phone", clientPhone);

                            dialog.dismiss();
                            ClientActivity.this.setResult(RESULT_OK, data);
                            ClientActivity.this.finish();
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        }
                    });
                    customExtBuilder.create().show();
                } else {
                    Intent intent = new Intent(ClientActivity.this, ChatActivity.class);
                    intent.putExtra(Constants.EXTRA_USER_ID, clientId);
                    if (!TextUtils.isEmpty(mShopID)) {
                        intent.putExtra(Constants.EXTRA_SHOP_ID, mShopID);
                    }
                    intent.putExtra(Constants.EXTRA_SHOP_NAME, CacheUtil.getInstance().getShopFullName());
                    if (!TextUtils.isEmpty(clientName)) {
                        intent.putExtra(Constants.EXTRA_TO_NAME, clientName);
                    }
                    intent.putExtra(Constants.EXTRA_FROM_NAME, CacheUtil.getInstance().getUserName());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_client, menu);
        return true;
    }

    /**
     * 获取我的客户列表
     * @param userID
     * @param token
     * @param shopID
     */
    public void showMyClientList(String userID, String token, String shopID) {
        //  2  获取本地客户联系人列表
//        if(null != mAllContactsList && !mAllContactsList.isEmpty()){
//            mAllContactsList.removeAll(mAllContactsList);
//        }
//        List<ClientVo> clientVos = ClientDBUtil.getInstance().queryUnNormalClient();
//        if(null != clientVos && !clientVos.isEmpty()){
//            List<ContactVo> contactVos = new ArrayList<>();
//            for(ClientVo clientVo : clientVos){
//                ContactVo contact =  ContactFactory.getInstance().buildContactVoByMyClientVo(clientVo);
//                contact.setIsOnLine(OnlineStatus.OFFLINE);
//                mLocalClientMap.put(contact.getClientID(), contact);
//                contactVos.add(contact);
//            }
//            Collections.sort(contactVos, pinyinComparator);
//            mAllContactsList.addAll(contactVos);
//        }

        // 1. 服务器获取联系人
        ClientController.getInstance().getShopClients(ClientActivity.this, userID, token, shopID,
            new ExtNetRequestListener(this) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                DialogUtil.getInstance().cancelProgressDialog();

                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
            }

            @Override
            public void onNetworkRequestCancelled() {
                DialogUtil.getInstance().cancelProgressDialog();
            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                DialogUtil.getInstance().cancelProgressDialog();
                try {
                    Log.i(TAG, "result.rawResult:" + result.rawResult);
                    String jsonResult = result.rawResult;
                    Gson gson = new Gson();
                    mAllContactsList = gson.fromJson(jsonResult,
                    new TypeToken<ArrayList<ClientContactVo>>() {}.getType());
                    if (null != mAllContactsList && !mAllContactsList.isEmpty()) {
                        mTvDialog.setVisibility(View.GONE);
                        mAllContactsList = ClientFactory.getInstance().
                                buildSortContactList(mAllContactsList);
                        Collections.sort(mAllContactsList, mClientCompatator);
                        mContactsAdapter.setData(mAllContactsList);
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void beforeNetworkRequestStart() {
                //网络请求前
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        showMyClientList(mUserID, mToken, mShopID);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
