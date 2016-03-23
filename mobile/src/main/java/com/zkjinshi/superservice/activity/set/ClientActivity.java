package com.zkjinshi.superservice.activity.set;

import android.content.Context;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.chat.single.ChatActivity;
import com.zkjinshi.superservice.adapter.ContactsSortAdapter;
import com.zkjinshi.superservice.base.BaseAppCompatActivity;
import com.zkjinshi.superservice.factory.ClientFactory;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.net.RequestUtil;
import com.zkjinshi.superservice.response.GetClientsResponse;
import com.zkjinshi.superservice.utils.AsyncHttpClientUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ClientComparator;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.view.CustomExtDialog;
import com.zkjinshi.superservice.view.SideBar;
import com.zkjinshi.superservice.vo.ClientContactVo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * 联系人列表显示
 * 开发者：vincent
 * 日期：2015/9/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientActivity extends BaseAppCompatActivity {

    private final static String TAG = ClientActivity.class.getSimpleName();

    private boolean     mChooseOrderPerson;
    private String      mShopID;
    private Toolbar     mToolbar;
    private TextView    mTvCenterTitle;
    private SideBar     mSideBar;
    private TextView    mTvDialog;
    private ListView    mRcvContacts;
    private Context mContext;

    private List<ClientContactVo>   mAllContactsList;
//    private Map<String, ContactVo>  mLocalClientMap;
    private ClientComparator        mClientCompatator;
    private ContactsSortAdapter     mContactsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        mContext = this;
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
        mShopID = CacheUtil.getInstance().getShopID();

        /** 给ListView设置adapter **/
        mSideBar.setTextView(mTvDialog);

//        mLocalClientMap  = new HashMap<>();
        mAllContactsList  = new ArrayList<>();
        mClientCompatator = new ClientComparator();
        mContactsAdapter  = new ContactsSortAdapter(this, mAllContactsList);

        mRcvContacts.setAdapter(mContactsAdapter);
        showMyClientList();
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
        getMenuInflater().inflate(R.menu.menu_client, menu);
        return true;
    }

    /**
     * 获取我的客户列表
     */
    public void showMyClientList() {

        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token",CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.getClientList();
            client.get(mContext,url, stringEntity, "application/json", new AsyncHttpResponseHandler(){
                public void onStart(){
                    super.onStart();
                    DialogUtil.getInstance().showAvatarProgressDialog(mContext,"");
                }

                public void onFinish(){
                    super.onFinish();
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String response = new String(responseBody,"utf-8");
                        GetClientsResponse getClientsResponse = new Gson().fromJson(response,GetClientsResponse.class);
                        if(getClientsResponse == null){
                            return;
                        }
                        if(getClientsResponse.getRes() == 0){
                            mAllContactsList = getClientsResponse.getData();
                            if (null != mAllContactsList && !mAllContactsList.isEmpty()) {
                                mTvDialog.setVisibility(View.GONE);
                                mAllContactsList = ClientFactory.getInstance().
                                        buildSortContactList(mAllContactsList);
                                Collections.sort(mAllContactsList, mClientCompatator);
                                mContactsAdapter.setData(mAllContactsList);
                            }
                        }else{
                            Toast.makeText(mContext,getClientsResponse.getResDesc(),Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    AsyncHttpClientUtil.onFailure(mContext,statusCode);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showMyClientList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
