package com.zkjinshi.superservice.activity.set;

import android.app.Activity;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.filechoser.activity.FileListActivity;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.ContactsAdapter;
import com.zkjinshi.superservice.bean.ImportSempBean;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.sqlite.ShopDepartmentDBUtil;
import com.zkjinshi.superservice.sqlite.ShopEmployeeDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.JxlUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.ContactLocalVo;
import com.zkjinshi.superservice.vo.DepartmentVo;
import com.zkjinshi.superservice.vo.EmployeeVo;

import android.content.Intent;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.TextUtils;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

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

    public static final int EXCEL_REQUEST_CODE = 4;
    public static final int HAND_REQUEST_CODE = 115;

    private TextView handText;
    private TextView importText;
    private ListView listView;
    private ContactsAdapter contactsAdapter;

    private List<EmployeeVo> employeeVoList; //团队联系人列表
    private ArrayList<ContactLocalVo> contactLocalList = new ArrayList<ContactLocalVo>(); //手机联系人
    private List<EmployeeVo> excelList = new ArrayList<EmployeeVo>(); //解析excel得到的员工资料
    private EmployeeVo handEmployeeVo = null; //手动输入的员工资料
    private ArrayList<DepartmentVo> deptList;

    private  List<EmployeeVo> allList = new ArrayList<EmployeeVo>(); // 汇总的要提交的新成员。

    private boolean hasGetDept = false;
    private String mShopID;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_add);
        mContext = this;
        initView();
        initData();
        initListener();
    }

    private void initView() {
        handText   = (TextView)findViewById(R.id.hand_input_tv);
        importText = (TextView)findViewById(R.id.import_name_tv);
        listView   = (ListView)findViewById(R.id.contact_listview);
    }

    /**得到手机通讯录联系人信息**/
    private void getPhoneContacts() {
        contactLocalList = new ArrayList<ContactLocalVo>();
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

                ContactLocalVo contactLocalVo = new ContactLocalVo();
                contactLocalVo.setContactid(contactid);
                contactLocalVo.setContactName(contactName);
                contactLocalVo.setPhoneNumber(phoneNumber);
                contactLocalVo.setPhotoid(photoid);
                if(!isExsitInEmployeeVoList(phoneNumber)){
                    contactLocalVo.setHasAdd(false);
                    contactLocalList.add(contactLocalVo);
                }

            }
            phoneCursor.close();
            CacheUtil.getInstance().saveListCache("contactLocalList", contactLocalList);
        }
    }

    private void initData() {

        mShopID = CacheUtil.getInstance().getShopID();

        contactsAdapter = new ContactsAdapter(this,contactLocalList);
        listView.setAdapter(contactsAdapter);
        listView.setEmptyView(findViewById(R.id.empty_tv));
        String listStr =  CacheUtil.getInstance().getListStrCache("contactLocalList");
        if(!TextUtils.isEmpty(listStr)){
            Type listType = new TypeToken<ArrayList<ContactLocalVo>>(){}.getType();
            Gson gson = new Gson();
            contactLocalList = gson.fromJson(listStr, listType);
            if (null != contactLocalList && !contactLocalList.isEmpty()) {
               contactsAdapter.refreshData(contactLocalList);
            }
        }
        LoadPhoneContactTask loadPhoneContactTask = new LoadPhoneContactTask();
        loadPhoneContactTask.execute();
    }

    private boolean isExsitInEmployeeVoList(String phone){
        for(EmployeeVo employeeVo : employeeVoList){
            if(employeeVo.getPhone() != null && employeeVo.getPhone().equals(phone)){
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
                if( excelList != null && excelList.size() > 0){
                    if(hasGetDept){
                        submitEmployees();
                    }else{
                        submitDepts();
                    }
                }else{
                    submitEmployees();
                }

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

    /**
     *  批量上传部门
     */
    private void submitDepts() {
        if(deptList != null && deptList.size() > 0){
            String url = ProtocolUtil.getBatchAddDeptUrl();
            Log.i(TAG, url);
            String deptJson = new Gson().toJson(deptList);
            NetRequest netRequest = new NetRequest(url);
            HashMap<String,String> bizMap = new HashMap<String,String>();
            bizMap.put("salesid", CacheUtil.getInstance().getUserId());
            bizMap.put("token", CacheUtil.getInstance().getToken());
            bizMap.put("shopid", CacheUtil.getInstance().getShopID());
            bizMap.put("dept", deptJson);
            bizMap.put("set","2");
            netRequest.setBizParamMap(bizMap);
            NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
            netRequestTask.methodType = MethodType.PUSH;
            netRequestTask.setNetRequestListener(new ExtNetRequestListener(this) {
                @Override
                public void onNetworkRequestError(int errorCode, String errorMessage) {
                    Log.i(TAG, "errorCode:" + errorCode);
                    Log.i(TAG, "errorMessage:" + errorMessage);
                }

                @Override
                public void onNetworkRequestCancelled() {}

                @Override
                public void onNetworkResponseSucceed(NetResponse result) {
                    super.onNetworkResponseSucceed(result);

                    Log.i(TAG, "result.rawResult:" + result.rawResult);
                    try {
                        ArrayList<DepartmentVo> dlist = new Gson().fromJson(result.rawResult, new TypeToken<ArrayList<DepartmentVo>>() {
                        }.getType());
                        if (dlist != null && deptList.size() > 0) {
                            deptList = dlist;
                            hasGetDept = true;
                            ShopDepartmentDBUtil.getInstance().batchAddShopDepartments(deptList);

                            for (EmployeeVo shopEmployeeVo : excelList) {
                                for (DepartmentVo departmentVo : deptList) {
                                    if (shopEmployeeVo.getRolename().equals(departmentVo.getDept_name())) {

                                        break;
                                    }
                                }
                            }
                            submitEmployees();
                        }

                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }

                }

                @Override
                public void beforeNetworkRequestStart() {
                }
            });
            netRequestTask.isShowLoadingDialog = true;
            netRequestTask.execute();
        }
    }

    //汇总要添加的成员
    private void summaryEmployees(){
        allList.clear();
        HashMap<String,String> map = new HashMap<String,String>();
        //汇总手动添加的新成员
        if(handEmployeeVo != null){
            if(!TextUtils.isEmpty(handEmployeeVo.getPhone()) && !map.containsKey(handEmployeeVo.getPhone())){
                allList.add(handEmployeeVo);
                map.put(handEmployeeVo.getPhone(),handEmployeeVo.getPhone());
            }
        }

        //汇总导入的Excel
        if( excelList != null && excelList.size() > 0){
            for(EmployeeVo shopEmployeeVo : excelList){
                if(!TextUtils.isEmpty(shopEmployeeVo.getPhone()) && !map.containsKey(shopEmployeeVo.getPhone())){
                    if(shopEmployeeVo.getRolename().equals("管理层")){
                        //shopEmployeeVo.setRoleid(1);
                    }else{
                        //shopEmployeeVo.setRoleid(2);
                    }
                    allList.add(shopEmployeeVo);
                    map.put(shopEmployeeVo.getPhone(),shopEmployeeVo.getPhone());
                }
            }
        }
        //汇总要上传的手机联系人
        for(ContactLocalVo contactLocalVo : contactLocalList){
            if(contactLocalVo.isHasAdd()){
                EmployeeVo shopEmployeeVo = new EmployeeVo();
                String phone = contactLocalVo.getPhoneNumber();
                phone = phone.replaceAll(" ","");
                shopEmployeeVo.setPhone(phone);
                shopEmployeeVo.setUsername(contactLocalVo.getContactName());
//                shopEmployeeVo.setRoleid(2);
//                shopEmployeeVo.setDept_id(0);
                if(!TextUtils.isEmpty(shopEmployeeVo.getPhone()) && !map.containsKey(shopEmployeeVo.getPhone())){
                    allList.add(shopEmployeeVo);
                    map.put(shopEmployeeVo.getPhone(),shopEmployeeVo.getPhone());
                }
            }
        }
        map = null;
    }

    // 批量上传新建的成员
    private void submitEmployees() {
        summaryEmployees();
        if(allList.size() <= 0){
            DialogUtil.getInstance().showToast(this,"至少添加一个成员");
            return;
        }
        try{

            JSONArray users = new JSONArray();
            for(int i=0;i<allList.size();i++){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("phone",allList.get(i).getPhone());
                jsonObject.put("username",allList.get(i).getUsername());
                users.put(jsonObject);
            }
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token",CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("users",users);
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.registerSSusers();
            client.post(mContext,url, stringEntity, "application/json", new JsonHttpResponseHandler(){
                public void onStart(){
                    super.onStart();
                    DialogUtil.getInstance().showAvatarProgressDialog(mContext,"");
                }

                public void onFinish(){
                    super.onFinish();
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                    super.onSuccess(statusCode,headers,response);
                    try {
                        if(response.getInt("res") == 0){
                            Toast.makeText(mContext,"成功添加",Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        }else{
                            Toast.makeText(mContext,response.getString("resDesc"),Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(mContext,"解析异常",Toast.LENGTH_SHORT).show();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse){
                    super.onFailure(statusCode,headers,throwable,errorResponse);
                    Toast.makeText(mContext,"API 错误："+statusCode,Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

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
                    handEmployeeVo = (EmployeeVo)data.getSerializableExtra(EmployeeHandAddActivity.CREATE_RESULT);
                    handText.setText(handEmployeeVo.getUsername()+","+handEmployeeVo.getPhone());
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
            getDeptList(excelList);
            Log.i(TAG,excelList.toString());
        }else{
            DialogUtil.getInstance().showToast(EmployeeAddActivity.this,"暂时只支持导入XLS 格式的文件");
        }
    }

    //获取部门列表
    private void getDeptList(List<EmployeeVo> excelList){
        deptList = new ArrayList<DepartmentVo>();
        HashMap<String,String> map = new HashMap<String,String>();
        for(EmployeeVo shopEmployeeVo : excelList){
            String deptname = shopEmployeeVo.getRolename();
            if(!TextUtils.isEmpty(deptname) && !map.containsKey(deptname)){
                map.put(deptname, deptname);
                DepartmentVo departmentVo = new DepartmentVo();
                departmentVo.setDept_name(deptname);
                deptList.add(departmentVo);
            }
        }
        map = null;
    }

    /**
     * 加载手机联系人
     */
    class LoadPhoneContactTask extends AsyncTask<Void,Void,Void>{

        protected void onPreExecute() {
            //第一个执行方法
            super.onPreExecute();
            DialogUtil.getInstance().showProgressDialog(EmployeeAddActivity.this);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //获取团队联系人列表
            employeeVoList = ShopEmployeeDBUtil.getInstance().queryAllExceptUser(CacheUtil.getInstance().getUserId());
            //得到手机通讯录联系人信息
            getPhoneContacts();
            return null;
        }

        protected void onPostExecute(Void voids) {

            contactsAdapter.refreshData(contactLocalList);
            DialogUtil.getInstance().cancelProgressDialog();
        }
    }


}
