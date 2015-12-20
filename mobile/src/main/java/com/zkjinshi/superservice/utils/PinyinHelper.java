package com.zkjinshi.superservice.utils;

import java.util.ArrayList;

/**
 * 联系人帮助类, 用于本地数据库联系人
 * 开发者：WinkyQin
 * 日期：2015/12/16
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class PinyinHelper {

    private final static String TAG = PinyinHelper.class.getSimpleName();

    private static PinyinHelper instance = null;
    private PinyinHelper(){}

    public static synchronized PinyinHelper getInstance(){
        if(instance == null){
            instance = new PinyinHelper();
        }
        return instance;
    }

//    /**
//     * 获取本地联系人
//     * @return
//     */
//    public List<Contact> getContacts(Context context){
//        ContentResolver resolver = context.getContentResolver();
//        Cursor contactCursor = resolver.query(
//                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                        new String[] {
//                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
//                            ContactsContract.CommonDataKinds.Phone.NUMBER,
//                            ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY,
//                        },
//                        null,
//                        null,
//                        "sort_key COLLATE LOCALIZED ASC"
//        );
//
//        if (contactCursor == null || contactCursor.getCount() == 0) {
//            Toast.makeText(context,
//                           context.getString(R.string.no_permission_or_failed_get_the_contact_data),
//                           Toast.LENGTH_SHORT
//                           ).show();
//            return null;
//        }
//
//        int PHONES_NUMBER_INDEX = contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//        int PHONES_DISPLAY_NAME_INDEX = contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
//        int SORT_KEY_INDEX = contactCursor.getColumnIndex("sort_key");
//        List<Contact> contactList = null;
//        if (contactCursor.getCount() > 0) {
//            contactList = new ArrayList<>();
//            while (contactCursor.moveToNext()) {
//                //获得电话号码， 联系人名称，首字母等关键信息
//                String phoneNumber =  contactCursor.getString(PHONES_NUMBER_INDEX);
//                if (TextUtils.isEmpty(phoneNumber)){
//                    continue;
//                }
//                String contactName  = contactCursor.getString(PHONES_DISPLAY_NAME_INDEX);
//                //魅族等手机 sortkey有可能是中文
//                String sortKey      = contactCursor.getString(SORT_KEY_INDEX);
//                String pinYin       = this.getPinYin(sortKey);
//
//                //汉字转换成拼音
//                Contact contact = new Contact();
//                contact.setContactPhone(phoneNumber);
//                contact.setContactName(contactName);
//                contact.setSortKey(pinYin);
//                contact.setFirstLetter(getFirstLetter(pinYin));
//                contactList.add(contact);
//            }
//        }
//        System.out.println("contactList:" + contactList);
//        return contactList;
//    }

    /**
     * 汉字转换拼音，字母原样返回，都转换为小写
     *
     * @param input
     * @return
     */
    public String getPinYin(String input) {
        ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance().get(input);
        StringBuilder sb = new StringBuilder();
        if (tokens != null && tokens.size() > 0) {
            for (HanziToPinyin.Token token : tokens) {
                if (token.type == HanziToPinyin.Token.PINYIN) {
                    sb.append(token.target);
                } else {
                    sb.append(token.source);
                }
            }
        }
        return sb.toString().trim().toLowerCase();
    }

    /**
     * 获得联系人首字母
     * @param sortKey
     * @return
     */
    public String getFirstLetter(String sortKey){
        String first = sortKey.substring(0, 1);
        if (first.matches("[A-Za-z]")) {
            return first.toUpperCase();
        } else {
            return "#";
        }
    }
}
