package com.zkjinshi.superservice.activity.set;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.ContactsSortAdapter;
import com.zkjinshi.superservice.bean.ClientDetailBean;
import com.zkjinshi.superservice.factory.ClientFactory;
import com.zkjinshi.superservice.factory.SortModelFactory;
import com.zkjinshi.superservice.listener.RecyclerItemClickListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestListener;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.sqlite.ClientDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.CharacterParser;
import com.zkjinshi.superservice.utils.PinyinComparator;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.utils.SortKeyUtil;
import com.zkjinshi.superservice.view.SideBar;
import com.zkjinshi.superservice.vo.ClientVo;
import com.zkjinshi.superservice.vo.ContactType;
import com.zkjinshi.superservice.vo.SortModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * 联系人列表显示， 提供字母快速进入和查找联系人功能
 * 开发者：vincent
 * 日期：2015/9/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientActivity extends AppCompatActivity{

    private final static String TAG = ClientActivity.class.getSimpleName();

    private final static int REFRESH_SORTMODELS = 0X01;//刷新界面显示

    private String      mUserID;
    private String      mToken;
    private String      mShopID;
    private Toolbar     mToolbar;
    private TextView    mTvCenterTitle;
    private SideBar     mSideBar;
    private TextView    mTvDialog;

    private RecyclerView        mRcvContacts;
    private LinearLayoutManager mLayoutManager;

    private CharacterParser      characterParser;
    private List<SortModel>      mAllContactsList;
    private List<ClientVo>       mLocalContacts;
    private PinyinComparator     pinyinComparator;
    private ContactsSortAdapter  mContactsAdapter;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case REFRESH_SORTMODELS:
                    mContactsAdapter.updateListView(mAllContactsList);
                    break;

            }
        }
    };

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
        characterParser  = CharacterParser.getInstance();

        mAllContactsList = new ArrayList<>();
        pinyinComparator = new PinyinComparator();
        Collections.sort(mAllContactsList, pinyinComparator);// 根据a-z进行排序源数据
        mContactsAdapter = new ContactsSortAdapter(this, mAllContactsList);
        mRcvContacts.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRcvContacts.setLayoutManager(mLayoutManager);
        mRcvContacts.setAdapter(mContactsAdapter);

        //TODO: 1.获得本地最近5位联系人列表的客户列表
        //TODO:查询我的本地客户
        mLocalContacts = ClientDBUtil.getInstance().queryUnNormalClient();
        if(!mLocalContacts.isEmpty()){
            List<SortModel> sortModels = SortModelFactory.getInstance().convertClientVos2SortModels(mLocalContacts);
            mAllContactsList.addAll(sortModels);
        }

        getMyClientList(mUserID, mToken, mShopID);
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

//        mEtSearch.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable e) {
//                String content = mEtSearch.getText().toString();
//                if ("".equals(content)) {
//                    mIvClearText.setVisibility(View.INVISIBLE);
//                } else {
//                    mIvClearText.setVisibility(View.VISIBLE);
//                }
//                if (content.length() > 0) {
//                    ArrayList<SortModel> fileterList = (ArrayList<SortModel>) search(content);
//                    mContactsAdapter.updateListView(fileterList);
//                    //mAdapter.updateData(mContacts);
//                } else {
//                    mContactsAdapter.updateListView(mAllContactsList);
//                }
//                mRcvContacts.scrollToPosition(0);
//            }
//        });

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_client, menu);
        return true;
    }

//    /**
//     * 模糊查询
//     * @param str
//     * @return
//     */
//    public List<SortModel> search(String str) {
//        List<SortModel> filterList = new ArrayList<SortModel>();// 过滤后的list
//        //if (str.matches("^([0-9]|[/+])*$")) {// 正则表达式 匹配号码
//        if (str.matches("^([0-9]|[/+]).*")) {// 正则表达式 匹配以数字或者加号开头的字符串(包括了带空格及-分割的号码)
//            String simpleStr = str.replaceAll("\\-|\\s", "");
//            for (SortModel contact : mAllContactsList) {
//                if (contact.getNumber() != null && contact.getName() != null) {
//                    if (contact.getSimpleNumber().contains(simpleStr) || contact.getName().contains(str)) {
//                        if (!filterList.contains(contact)) {
//                            filterList.add(contact);
//                        }
//                    }
//                }
//            }
//        }else {
//            for (SortModel contact : mAllContactsList) {
//                if (contact.getNumber() != null && contact.getName() != null) {
//                    //姓名全匹配,姓名首字母简拼匹配,姓名全字母匹配
//                    if (contact.getName().toLowerCase(Locale.CHINESE).contains(str.toLowerCase(Locale.CHINESE))
//                            || contact.getSortKey().toLowerCase(Locale.CHINESE).replace(" ", "").contains(str.toLowerCase(Locale.CHINESE))
//                            || contact.getSortToken().simpleSpell.toLowerCase(Locale.CHINESE).contains(str.toLowerCase(Locale.CHINESE))
//                            || contact.getSortToken().wholeSpell.toLowerCase(Locale.CHINESE).contains(str.toLowerCase(Locale.CHINESE))) {
//                        if (!filterList.contains(contact)) {
//                            filterList.add(contact);
//                        }
//                    }
//                }
//            }
//        }
//        return filterList;
//    }

    /**
     * 获取我的客户列表
     * @param userID
     * @param token
     * @param shopID
     */
    public void getMyClientList(String userID, String token, String shopID) {
        DialogUtil.getInstance().showProgressDialog(ClientActivity.this);
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
                Message msg = Message.obtain();
                msg.what    = REFRESH_SORTMODELS;
                handler.sendMessage(msg);
            }

            @Override
            public void onNetworkRequestCancelled() {
                DialogUtil.getInstance().cancelProgressDialog();
                Message msg = Message.obtain();
                msg.what    = REFRESH_SORTMODELS;
                handler.sendMessage(msg);
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
                            new TypeToken<ArrayList<ClientDetailBean>>() {}.getType());

                    if(null != clientDetailBeans && !clientDetailBeans.isEmpty()){

                        List<ClientVo> clientVos = ClientFactory.getInstance().buildClientVosByClientBeans(clientDetailBeans);
                        for(ClientVo clientVo : clientVos){
                            if(!ClientDBUtil.getInstance().isClientExistByUserID(clientVo.getUserid())){
                                clientVo.setContactType(ContactType.NORMAL);
                                ClientDBUtil.getInstance().addClients(clientVo);
                            }
                        }

                        List<SortModel> sortModels = SortModelFactory.getInstance().convertClientVos2SortModels(clientVos);
                        mAllContactsList.addAll(sortModels);

                        Message msg = Message.obtain();
                        msg.what    = REFRESH_SORTMODELS;
                        handler.sendMessage(msg);
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
     * 获取本地联系人数据
     */
    public void getLocalContacts() {
        DialogUtil.getInstance().showProgressDialog(ClientActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ContentResolver resolver = getApplicationContext().getContentResolver();
                    Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            new String[]
                                    {
                                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                                            "sort_key"
                                    },
                            null, null, "sort_key");

                    if (phoneCursor == null || phoneCursor.getCount() == 0) {
                        Toast.makeText(getApplicationContext(), "未获得读取联系人权限 或 未获得联系人数据", Toast.LENGTH_SHORT).show();
                        return ;
                    }
                    int PHONES_CONTEACT_ID_INDEX  = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
                    int PHONES_NUMBER_INDEX       = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    int PHONES_DISPLAY_NAME_INDEX = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    int SORT_KEY_INDEX            = phoneCursor.getColumnIndex("sort_key");
                    if (phoneCursor.getCount() > 0) {
                        if(null == mAllContactsList){
                            mAllContactsList = new ArrayList<>();
                        }
                        while (phoneCursor.moveToNext()) {
                            String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                            if (TextUtils.isEmpty(phoneNumber))
                                continue;
                            long   contactID   = phoneCursor.getLong(PHONES_CONTEACT_ID_INDEX);
                            String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
                            String sortKey     = phoneCursor.getString(SORT_KEY_INDEX);
                            System.out.println(sortKey);

                            SortModel sortModel = new SortModel();
                            sortModel.setContactType(ContactType.UNNORMAL);//本地联系人类型
                            sortModel.setContactID(contactID);
                            sortModel.setName(contactName);
                            sortModel.setNumber(phoneNumber);
                            sortModel.setSortKey(sortKey);

                            //优先使用系统sortkey取, 取不到再使用工具取
                            String sortLetters = SortKeyUtil.getSortLetterBySortKey(sortKey, characterParser);
                            if (sortLetters == null) {
                                sortLetters = SortKeyUtil.getSortLetter(contactName, characterParser);
                            }
                            sortModel.setSortLetters(sortLetters);
                            sortModel.setSortToken(SortKeyUtil.parseSortKey(sortKey));
                            mAllContactsList.add(sortModel);
                        }
                    }
                    phoneCursor.close();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Collections.sort(mAllContactsList, pinyinComparator);
                            mContactsAdapter.updateListView(mAllContactsList);
                            DialogUtil.getInstance().cancelProgressDialog();
                        }
                    });
                } catch (Exception e) {
                    Log.e("xbc", e.getLocalizedMessage());
                }
            }
        }).start();
    }
}
