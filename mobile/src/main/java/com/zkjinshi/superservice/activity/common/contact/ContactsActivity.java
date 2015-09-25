package com.zkjinshi.superservice.activity.common.contact;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.common.ClientAddActivity;
import com.zkjinshi.superservice.sqlite.ClientDBUtil;
import com.zkjinshi.superservice.vo.ClientVo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * 联系人列表显示， 提供字母快速进入和查找联系人功能
 * 开发者：vincent
 * 日期：2015/9/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ContactsActivity extends Activity{

    private final static String TAG = ContactsActivity.class.getSimpleName();

    private SideBar      mSideBar;
    private TextView     mTvDialog;
    private ImageView    mIvClearText;
    private EditText     mEtSearch;
    private ImageButton  mIbtnBack;
    private ImageButton  mIbtnAdd;
    private RecyclerView        mRcvContacts;
    private LinearLayoutManager mLayoutManager;

    private CharacterParser      characterParser;
    private List<SortModel>      mAllContactsList;
    private PinyinComparator     pinyinComparator;
    private ContactsSortAdapter  mContactsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        initView();
        initData();
        initListener();
        loadContacts();
    }

    private void initView() {
        mSideBar     = (SideBar)    findViewById(R.id.sb_sidebar);
        mTvDialog    = (TextView)   findViewById(R.id.tv_dialog);
        mIvClearText = (ImageView)  findViewById(R.id.iv_cleartext);
        mEtSearch    = (EditText)   findViewById(R.id.et_search);
        mRcvContacts = (RecyclerView) findViewById(R.id.rcv_contacts);
        mIbtnBack    = (ImageButton)  findViewById(R.id.ibtn_back);
        mIbtnAdd     = (ImageButton)  findViewById(R.id.ibtn_add);
    }

    private void initData() {
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

        //TODO: 1.服务器获得当前服务员关联的客户列表
        List<ClientVo> clientVos       = ClientDBUtil.getInstance().queryAll();
        if(!clientVos.isEmpty()){
            List<SortModel> sortModels = SortModelFactory.getInstance().convertClients2SortModels(clientVos);
            mAllContactsList = new ArrayList<>();
            mAllContactsList.addAll(sortModels);

            //TODO:2.获得服务器最近联系人列表， 进入排序
            List<SortModel> latestSortModels = SortModelFactory.getInstance().getLatestSortModel(clientVos);
            mAllContactsList.addAll(latestSortModels);
        }
    }

    private void initListener() {

        /** 后退界面 */
        mIbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactsActivity.this.finish();
            }
        });

        /** 进入新增客户界面 */
        mIbtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goAddClient = new Intent(ContactsActivity.this, ClientAddActivity.class);
                ContactsActivity.this.startActivity(goAddClient);
            }
        });

        /**清除输入字符**/
        mIvClearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEtSearch.setText("");
            }
        });

        mEtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable e) {
                String content = mEtSearch.getText().toString();
                if ("".equals(content)) {
                    mIvClearText.setVisibility(View.INVISIBLE);
                } else {
                    mIvClearText.setVisibility(View.VISIBLE);
                }
                if (content.length() > 0) {
                    ArrayList<SortModel> fileterList = (ArrayList<SortModel>) search(content);
                    mContactsAdapter.updateListView(fileterList);
                    //mAdapter.updateData(mContacts);
                } else {
                    mContactsAdapter.updateListView(mAllContactsList);
                }
                mRcvContacts.scrollToPosition(0);
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
        mContactsAdapter.setOnItemClickListener(new ContactsSortAdapter.OnContactItemClickListener() {
            @Override
            public void onItemClick(View view, SortModel sortModel) {
                Log.v(TAG, "sortModel:" + sortModel.getSortLetters());
            }
        });
    }

    /** 内容解析器获取联系人数据  */
    private void loadContacts() {
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
                            sortModel.setContactType(ContactType.LOCAL);//本地联系人类型
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
                        }
                    });
                } catch (Exception e) {
                    Log.e("xbc", e.getLocalizedMessage());
                }
            }
        }).start();
    }

    /**
     * 模糊查询
     * @param str
     * @return
     */
    public List<SortModel> search(String str) {
        List<SortModel> filterList = new ArrayList<SortModel>();// 过滤后的list
        //if (str.matches("^([0-9]|[/+])*$")) {// 正则表达式 匹配号码
        if (str.matches("^([0-9]|[/+]).*")) {// 正则表达式 匹配以数字或者加号开头的字符串(包括了带空格及-分割的号码)
            String simpleStr = str.replaceAll("\\-|\\s", "");
            for (SortModel contact : mAllContactsList) {
                if (contact.getNumber() != null && contact.getName() != null) {
                    if (contact.getSimpleNumber().contains(simpleStr) || contact.getName().contains(str)) {
                        if (!filterList.contains(contact)) {
                            filterList.add(contact);
                        }
                    }
                }
            }
        }else {
            for (SortModel contact : mAllContactsList) {
                if (contact.getNumber() != null && contact.getName() != null) {
                    //姓名全匹配,姓名首字母简拼匹配,姓名全字母匹配
                    if (contact.getName().toLowerCase(Locale.CHINESE).contains(str.toLowerCase(Locale.CHINESE))
                            || contact.getSortKey().toLowerCase(Locale.CHINESE).replace(" ", "").contains(str.toLowerCase(Locale.CHINESE))
                            || contact.getSortToken().simpleSpell.toLowerCase(Locale.CHINESE).contains(str.toLowerCase(Locale.CHINESE))
                            || contact.getSortToken().wholeSpell.toLowerCase(Locale.CHINESE).contains(str.toLowerCase(Locale.CHINESE))) {
                        if (!filterList.contains(contact)) {
                            filterList.add(contact);
                        }
                    }
                }
            }
        }
        return filterList;
    }

}
