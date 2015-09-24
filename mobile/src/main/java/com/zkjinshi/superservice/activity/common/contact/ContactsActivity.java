package com.zkjinshi.superservice.activity.common.contact;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.common.ClientAddActivity;
import com.zkjinshi.superservice.sqlite.ClientDBUtil;
import com.zkjinshi.superservice.vo.ClientVo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 联系人列表显示， 提供字母快速进入和查找联系人功能
 * 开发者：vincent
 * 日期：2015/9/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ContactsActivity extends Activity{

    private final static String TAG = ContactsActivity.class.getSimpleName();

    private SideBar   mSideBar;
    private TextView  mTvDialog;
    private ImageView mIvClearText;
    private EditText  mEtSearch;
    private ListView  mLvContacts;
    private ImageButton mIbtnBack;
    private ImageButton mIbtnAdd;

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
        mSideBar     = (SideBar) findViewById(R.id.sb_sidebar);
        mTvDialog    = (TextView) findViewById(R.id.tv_dialog);
        mIvClearText = (ImageView) findViewById(R.id.iv_cleartext);
        mEtSearch    = (EditText)  findViewById(R.id.et_search);
        mLvContacts  = (ListView)  findViewById(R.id.lv_contacts);
        mIbtnBack    = (ImageButton)  findViewById(R.id.ibtn_back);
        mIbtnAdd     = (ImageButton)  findViewById(R.id.ibtn_add);
    }

    private void initData() {
        /** 给ListView设置adapter **/
        mSideBar.setTextView(mTvDialog);
        characterParser  = CharacterParser.getInstance();

        //TODO: 服务器获得当前服务员关联的客户列表
        List<ClientVo> clientVos = ClientDBUtil.getInstance().queryAll();
        DialogUtil.getInstance().showToast(this, "clientVos 的条数："+clientVos.size());

        if(!clientVos.isEmpty()){
//            SortModelFactory.getInstance().convertClients2SortModels();
        }

        mAllContactsList = new ArrayList<>();
        pinyinComparator = new PinyinComparator();
        Collections.sort(mAllContactsList, pinyinComparator);// 根据a-z进行排序源数据
        mContactsAdapter = new ContactsSortAdapter(this, mAllContactsList);
        mLvContacts.setAdapter(mContactsAdapter);
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
                mLvContacts.setSelection(0);
            }
        });

        //设置右侧[A-Z]快速导航栏触摸监听
        mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = mContactsAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mLvContacts.setSelection(position);
                }
            }
        });

        mLvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
                ContactsSortAdapter.ViewHolder viewHolder = (ContactsSortAdapter.ViewHolder) view.getTag();
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
                        mAllContactsList = new ArrayList<>();
                        while (phoneCursor.moveToNext()) {
                            String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                            if (TextUtils.isEmpty(phoneNumber))
                                continue;
                            long   contactID   = phoneCursor.getLong(PHONES_CONTEACT_ID_INDEX);
                            System.out.println("contactID:" + contactID);
                            String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
                            String sortKey     = phoneCursor.getString(SORT_KEY_INDEX);
                            System.out.println(sortKey);
                            SortModel sortModel = new SortModel(contactID, contactName, phoneNumber, sortKey);

                            //优先使用系统sortkey取, 取不到再使用工具取
                            String sortLetters = getSortLetterBySortKey(sortKey);
//                            System.out.println("sortLetters:" + sortLetters);
                            if (sortLetters == null) {
                                sortLetters = getSortLetter(contactName);
                            }
                            sortModel.sortLetters = sortLetters;
                            sortModel.sortToken   = parseSortKey(sortKey);
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
     * 名字转拼音,取首字母
     * @param name
     * @return
     */
    private String getSortLetter(String name) {
        String letter = "#";
        if (name == null) {
            return letter;
        }

        //汉字转换成拼音
        String pinyin     = characterParser.getSelling(name);
        String sortString = pinyin.substring(0, 1).toUpperCase(Locale.CHINESE);

        // 正则表达式，判断首字母是否是英文字母
        if (sortString.matches("[A-Z]")) {
            letter = sortString.toUpperCase(Locale.CHINESE);
        }
        return letter;
    }

    /**
     * 取sort_key的首字母
     * @param sortKey
     * @return
     */
    private String getSortLetterBySortKey(String sortKey) {
        if (sortKey == null || "".equals(sortKey.trim())) {
            return null;
        }

        String letter = "#";
        //汉字转换成拼音
        String sortString = sortKey.trim().substring(0, 1).toUpperCase(Locale.CHINESE);
        System.out.print("sortString:"+ sortString);
        if (sortString.length() > 0 && isChinese(sortString)) {
            letter = characterParser.getSelling(sortString).trim().substring(0, 1).toUpperCase(Locale.CHINA);
            System.out.print("letter:"+ letter);
        } else if (sortString.matches("[A-Z]")) {
            // 正则表达式，判断首字母是否是英文字母
            letter = sortString.toUpperCase(Locale.CHINESE);
        }
        return letter;
    }

    /**
     * 判断字符串是否为中文
     * @param str
     * @return
     */
    private boolean isChinese(String str) {
        String regEx = "[\u4e00-\u9fa5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        while (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 模糊查询
     * @param str
     * @return
     */
    private List<SortModel> search(String str) {
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
                            || contact.sortToken.simpleSpell.toLowerCase(Locale.CHINESE).contains(str.toLowerCase(Locale.CHINESE))
                            || contact.sortToken.wholeSpell.toLowerCase(Locale.CHINESE).contains(str.toLowerCase(Locale.CHINESE))) {
                        if (!filterList.contains(contact)) {
                            filterList.add(contact);
                        }
                    }
                }
            }
        }
        return filterList;
    }

    String chReg = "[\\u4E00-\\u9FA5]+";//中文字符串匹配
    //String chReg="[^\\u4E00-\\u9FA5]";//除中文外的字符匹配
    /**
     * 解析sort_key,封装简拼,全拼
     * @param sortKey
     * @return
     */
    public SortToken parseSortKey(String sortKey) {
        SortToken token = new SortToken();
        if (sortKey != null && sortKey.length() > 0) {
            //其中包含的中文字符
            String[] enStrs = sortKey.replace(" ", "").split(chReg);
            for (int i = 0, length = enStrs.length; i < length; i++) {
                if (enStrs[i].length() > 0) {
                    //拼接简拼
                    token.simpleSpell += enStrs[i].charAt(0);
                    token.wholeSpell += enStrs[i];
                }
            }
        }
        return token;
    }

}
