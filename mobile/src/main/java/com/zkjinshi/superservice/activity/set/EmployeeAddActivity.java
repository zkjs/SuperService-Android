package com.zkjinshi.superservice.activity.set;

import android.app.Activity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.filechoser.activity.FileListActivity;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.order.CalendarActivity;
import com.zkjinshi.superservice.adapter.ContactsAdapter;
import com.zkjinshi.superservice.bean.BaseBean;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestListener;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.sqlite.ShopEmployeeDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.JxlUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.ContactLocalVo;
import com.zkjinshi.superservice.vo.DepartmentVo;
import com.zkjinshi.superservice.vo.ShopEmployeeVo;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.TextUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * 开发者：dujiande
 * 日期：2015/10/13
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class EmployeeAddActivity extends Activity {

    private final static String TAG = EmployeeAddActivity.class.getSimpleName();
    /**获取库Phon表字段**/
    private static final String[] PHONES_PROJECTION = new String[] {
            Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID };

    /**联系人显示名称**/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;

    /**电话号码**/
    private static final int PHONES_NUMBER_INDEX = 1;

    /**头像ID**/
    private static final int PHONES_PHOTO_ID_INDEX = 2;

    /**联系人的ID**/
    private static final int PHONES_CONTACT_ID_INDEX = 3;

    //
    public static final int EXCEL_REQUEST_CODE = 4;
    public static final int HAND_REQUEST_CODE = 5;

    private TextView handText;
    private TextView importText;
    private ListView listView;
    private ContactsAdapter contactsAdapter;

    private List<ShopEmployeeVo> employeeVoList; //团队联系人列表
    private ArrayList<ContactLocalVo> contactLocalList = new ArrayList<ContactLocalVo>(); //手机联系人
    private List<ShopEmployeeVo> excelList = new ArrayList<ShopEmployeeVo>(); //解析excel得到的员工资料
    private ShopEmployeeVo handEmployeeVo = null; //手动输入的员工资料

    private  List<ShopEmployeeVo> allList = new ArrayList<ShopEmployeeVo>(); // 汇总的要提交的新成员。



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_add);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        handText = (TextView)findViewById(R.id.hand_input_tv);
        importText = (TextView)findViewById(R.id.import_name_tv);
        listView = (ListView)findViewById(R.id.contact_listview);

    }



    /**得到手机通讯录联系人信息**/
    private void getPhoneContacts() {
        ContentResolver resolver = getContentResolver();
        // 获取手机联系人
        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                //得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                //当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                //得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
                //得到联系人ID
                Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);
                //得到联系人头像ID
                Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);
                //得到联系人头像Bitamp
                Bitmap contactPhoto = null;
                //photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
                if(photoid > 0 ) {
                    Uri uri =ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,contactid);
                    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
                    contactPhoto = BitmapFactory.decodeStream(input);
                }else {
                    contactPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.img_hotel_zhanwei);
                }

                ContactLocalVo contactLocalVo = new ContactLocalVo();
                contactLocalVo.setContactid(contactid);
                contactLocalVo.setContactName(contactName);
                contactLocalVo.setContactPhoto(contactPhoto);
                contactLocalVo.setPhoneNumber(phoneNumber);
                contactLocalVo.setPhotoid(photoid);
                contactLocalVo.setHasAdd(isExsitInEmployeeVoList(phoneNumber));
                contactLocalList.add(contactLocalVo);
            }
            phoneCursor.close();
        }
    }

    private void initData() {
        //获取团队联系人列表
        employeeVoList = ShopEmployeeDBUtil.getInstance().queryAll();
        //得到手机通讯录联系人信息
        getPhoneContacts();

        contactsAdapter = new ContactsAdapter(this,contactLocalList);
        listView.setAdapter(contactsAdapter);

    }

    private boolean isExsitInEmployeeVoList(String phone){
        for(ShopEmployeeVo employeeVo : employeeVoList){
            if(employeeVo.getPhone() != null && employeeVo.getPhone().equals(phone)){
                return true;
            }
            else if(employeeVo.getPhone2() != null && employeeVo.getPhone2().equals(phone)){
                return true;
            }
        }
        return  false;
    }

    private void initListener() {
        //返回按钮
        findViewById(R.id.header_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        //确认按钮
        findViewById(R.id.header_confirm_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitEmployees();
            }
        });
        //手动添加
        findViewById(R.id.hand_listener).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmployeeAddActivity.this, EmployeeHandAddActivity.class);
                startActivityForResult(intent,HAND_REQUEST_CODE);
            }
        });

        //批量导入Excel
        findViewById(R.id.import_listener).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmployeeAddActivity.this, FileListActivity.class);
                startActivityForResult(intent,EXCEL_REQUEST_CODE);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                boolean b =  contactLocalList.get(i).isHasAdd();
                contactLocalList.get(i).setHasAdd(!b);
                contactsAdapter.setContactLocalList(contactLocalList);
                contactsAdapter.notifyDataSetChanged();
            }
        });
    }

    // 上传新建的成员
    private void submitEmployees() {
        allList.clear();
        //汇总要上传的手机联系人
        for(ContactLocalVo contactLocalVo : contactLocalList){
            if(contactLocalVo.isHasAdd()){
                ShopEmployeeVo shopEmployeeVo = new ShopEmployeeVo();
                shopEmployeeVo.setPhone(contactLocalVo.getPhoneNumber());
                shopEmployeeVo.setName(contactLocalVo.getContactName());
                allList.add(shopEmployeeVo);
            }
        }
        //汇总手动添加的新成员
        if(handEmployeeVo != null){
            allList.add(handEmployeeVo);
        }
        //汇总导入的Excel
        if( excelList != null && excelList.size() > 0){
            allList.addAll(excelList);
        }
        if(allList.size() <= 0){
            DialogUtil.getInstance().showToast(this,"至少添加一个成员");
            return;
        }

        String deptsJson = new Gson().toJson(allList);


        //调用批量上传API
        String url = ProtocolUtil.getBatchAddClientUrl();
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("salesid", CacheUtil.getInstance().getUserId());
        bizMap.put("token", CacheUtil.getInstance().getToken());
        bizMap.put("shopid", CacheUtil.getInstance().getShopID());
        bizMap.put("deptsJson", deptsJson);
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new NetRequestListener() {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
            }

            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try{
                    BaseBean baseBean = new Gson().fromJson(result.rawResult,BaseBean.class);
                    if(baseBean.isSet()){
                        DialogUtil.getInstance().showToast(EmployeeAddActivity.this,"添加成员成功");
                        finish();
                    }else{
                        Log.e(TAG,baseBean.getErr());
                    }
                }catch (Exception e){
                    Log.e(TAG,e.getMessage());
                }

            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            if (EXCEL_REQUEST_CODE == requestCode) {
                if (null != data) {
                    String filePath = data.getStringExtra(FileListActivity.EXTRA_RESULT);
                    importText.setText(filePath);
                    decodeExcel(filePath);
                }
            }else if (HAND_REQUEST_CODE == requestCode) {
                if (null != data) {
                    handEmployeeVo = (ShopEmployeeVo)data.getSerializableExtra(EmployeeHandAddActivity.CREATE_RESULT);
                    handText.setText(handEmployeeVo.getName());
                }
            }
        }
    }

    //解析excel
    private void decodeExcel(String filePath) {
        int index =  filePath.lastIndexOf(".");
        String hz = filePath.substring(index+1,filePath.length());
        if( hz.toUpperCase().equals("XLS")){
            excelList = JxlUtil.decodeXLS(filePath);
            Log.i(TAG,excelList.toString());
        }else{
            DialogUtil.getInstance().showToast(EmployeeAddActivity.this,"暂时只支持导入XLS 格式的文件");
        }
    }

}
