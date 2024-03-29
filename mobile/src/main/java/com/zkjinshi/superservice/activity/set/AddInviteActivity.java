package com.zkjinshi.superservice.activity.set;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.EventAddInviteAdapter;
import com.zkjinshi.superservice.adapter.EventManagerAdapter;
import com.zkjinshi.superservice.base.BaseAppCompatActivity;
import com.zkjinshi.superservice.base.BaseApplication;
import com.zkjinshi.superservice.manager.SSOManager;
import com.zkjinshi.superservice.response.BaseResponse;
import com.zkjinshi.superservice.response.GuestListResponse;
import com.zkjinshi.superservice.response.RoleListResponse;
import com.zkjinshi.superservice.utils.AsyncHttpClientUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.EventVo;
import com.zkjinshi.superservice.vo.GuestVo;
import com.zkjinshi.superservice.vo.MemberVo;
import com.zkjinshi.superservice.vo.PayloadVo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * 添加 邀请Activity
 * 开发者：jimmyzhang
 * 日期：16/6/22
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class AddInviteActivity extends BaseAppCompatActivity {

    public static final int CONTACT_REQUEST_CODE = 0x0011;

    private Toolbar toolbar;
    private TextView titleIv;
    private ListView guestListView;
    private EventAddInviteAdapter eventAddInviteAdapter;
    private ArrayList<GuestVo> guestList;
    private HashMap<String,ArrayList<MemberVo>> chooseMemberMap = new HashMap<String, ArrayList<MemberVo>>();
    private String actName;
    private String actContent;
    private String startDate;
    private String endDate;
    private int maxTake;
    private int portable;
    private String actUrl;
    private String actId;
    private String actImage;
    private Map<String, Boolean> selectMap;

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        titleIv = (TextView) findViewById(R.id.tv_center_title);
        guestListView = (ListView)findViewById(R.id.list_view_add_invite);
    }

    private void initData(){
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.mipmap.ic_fanhui);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        titleIv.setText("增加邀请");
        selectMap = new HashMap<String, Boolean>();
        eventAddInviteAdapter = new EventAddInviteAdapter(this,guestList);
        eventAddInviteAdapter.setSelectMap(selectMap);
        eventAddInviteAdapter.setChooseMemberMap(chooseMemberMap);
        guestListView.setAdapter(eventAddInviteAdapter);
        if(null != getIntent()){
            if(null != getIntent().getStringExtra("actName")){
                actName = getIntent().getStringExtra("actName");
            }
            if(null != getIntent().getStringExtra("actContent")){
                actContent = getIntent().getStringExtra("actContent");
            }
            if(null != getIntent().getStringExtra("startDate")){
                startDate = getIntent().getStringExtra("startDate");
            }
            if(null != getIntent().getStringExtra("endDate")){
                endDate = getIntent().getStringExtra("endDate");
            }
            maxTake = getIntent().getIntExtra("maxTake",0);
            portable = getIntent().getIntExtra("portable",0);
            if(null != getIntent().getStringExtra("actUrl")){
                actUrl = getIntent().getStringExtra("actUrl");
            }
            if(null != getIntent().getStringExtra("actImage")){
                actImage = getIntent().getStringExtra("actImage");
            }
            if(null != getIntent().getStringExtra("actId")){
                actId = getIntent().getStringExtra("actId");
            }
        }
        requestGuestListTask();
    }

    private void initListeners(){

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(android.view.MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_event_add_invite://确定
                        showPublishEventDialog();
                        break;
                }
                return true;
            }
        });

        guestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GuestVo guestVo = (GuestVo) parent.getAdapter().getItem(position);
                if(null != guestVo){
                    selectMap = ((EventAddInviteAdapter)parent.getAdapter()).getSelectMap();
                    chooseMemberMap = ((EventAddInviteAdapter) parent.getAdapter()).getChooseMemberMap();
                    String roleId = guestVo.getRoleid();
                    boolean isAll = false;
                    if(null != selectMap && selectMap.containsKey(roleId)){
                        isAll = selectMap.get(roleId);
                    }
                    ArrayList<MemberVo> chooseMemberList = chooseMemberMap.get(roleId);
                    Intent intent = new Intent(AddInviteActivity.this,ContactActivity.class);
                    intent.putExtra("guestVo",guestVo);
                    intent.putExtra("chooseMemberList",chooseMemberList);
                    intent.putExtra("isAll",isAll);
                    startActivityForResult(intent,CONTACT_REQUEST_CODE);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_invite);
        initView();
        initData();
        initListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_invite, menu);
        return true;
    }

    /**
     * 获取邀请列表
     * @return
     */
    private HashMap<String,ArrayList<MemberVo>>  getChooseMemberMap(){
        Iterator iterator = selectMap.entrySet().iterator();
        ArrayList<MemberVo> memberList;
        String roleId = null;
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            boolean value = (Boolean) entry.getValue();
            if(value){
                String key = (String) entry.getKey();
                for (GuestVo guestVo: guestList){
                    roleId = guestVo.getRoleid();
                    if(roleId.equals(key)){
                        memberList = guestVo.getMember();
                        chooseMemberMap.put(key,memberList);
                    }
                }
            }
        }
        return chooseMemberMap;
    }

    /**
     * 获取邀请人字符串
     * @return
     */
    private String getChooseMembers(){
        chooseMemberMap = getChooseMemberMap();
        Iterator iterator = chooseMemberMap.entrySet().iterator();
        StringBuffer memberBuffer = new StringBuffer();
        ArrayList<MemberVo> memberList;
        String memberId;
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            memberList = (ArrayList<MemberVo>) entry.getValue();
            for (MemberVo memberVo : memberList){
                memberId = memberVo.getUserid();
                if(!TextUtils.isEmpty(memberId)){
                    memberBuffer.append(memberId).append(",");
                }
            }
        }
        return memberBuffer.length() > 0 ? memberBuffer.substring(0,memberBuffer.length()-1) : memberBuffer.toString();
    }

    /**
     * 获取邀请名单
     */
    private void requestGuestListTask(){
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token", CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = null;
            if(TextUtils.isEmpty(actId)){
                url = ProtocolUtil.getGuestListUrl();
            }else {
                url = ProtocolUtil.getGuestListUl(actId);
            }
            client.get(AddInviteActivity.this,url, stringEntity, "application/json", new AsyncHttpResponseHandler(){
                public void onStart(){
                    super.onStart();
                    DialogUtil.getInstance().showAvatarProgressDialog(AddInviteActivity.this,"");
                }

                public void onFinish(){
                    super.onFinish();
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String response = new String(responseBody,"utf-8");
                        GuestListResponse guestListResponse = new Gson().fromJson(response,GuestListResponse.class);
                        if(null != guestListResponse){
                            if(guestListResponse.getRes() == 0){
                                guestList = guestListResponse.getData();
                                eventAddInviteAdapter.setGuestList(guestList);
                            }else{
                                Toast.makeText(AddInviteActivity.this,guestListResponse.getResDesc(),Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    AsyncHttpClientUtil.onFailure(AddInviteActivity.this,statusCode);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 创建活动
     * @param actName
     * @param actContent
     * @param startDate
     * @param endDate
     * @param actImage
     * @param maxTake
     * @param actUrl
     * @param portable
     * @param invites
     */
    private void requestCreateEventTask(String actName,String actContent,String startDate,String endDate,String actImage,int maxTake,String actUrl,int portable,String invites){
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(50000);
            if(CacheUtil.getInstance().isLogin()){
                client.addHeader("Token",CacheUtil.getInstance().getExtToken());
            }
            String url = null;
            if(TextUtils.isEmpty(actId)){
                url  = ProtocolUtil.getCreateEventUrl();
            }else {
                url = ProtocolUtil.getEditEventUrl(actId);
            }
            RequestParams params = new RequestParams();
            if(!TextUtils.isEmpty(actName)){
                params.put("actname",actName);
            }
            if(!TextUtils.isEmpty(actContent)){
                params.put("actcontent",actContent);
            }
            if(!TextUtils.isEmpty(startDate)){
                params.put("startdate",startDate);
            }
            if(!TextUtils.isEmpty(endDate)){
                params.put("enddate",endDate);
            }
            if(!TextUtils.isEmpty(actImage)){
                params.put("actimage",new File(actImage));
            }
            if(maxTake > 0){
                params.put("maxtake",maxTake);
            }
            if(!TextUtils.isEmpty(actUrl)){
                params.put("acturl",actUrl);
            }
            params.put("portable",portable);
            params.put("invitesi",invites);
            client.post(url, params, new JsonHttpResponseHandler(){
                public void onStart(){
                    super.onStart();
                    DialogUtil.getInstance().showAvatarProgressDialog(AddInviteActivity.this,"");
                }

                public void onFinish(){
                    super.onFinish();
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                    super.onSuccess(statusCode,headers,response);
                    BaseResponse baseResponse = new Gson().fromJson(response.toString(),BaseResponse.class);
                    if(null != baseResponse){
                        int resultCode = baseResponse.getRes();
                        if(0 == resultCode){
                            DialogUtil.getInstance().showCustomToast(AddInviteActivity.this,"发布活动成功",Gravity.CENTER);
                            BaseApplication.getInst().clearLeaveTop();
                        }else {
                            String resultMsg = baseResponse.getResDesc();
                            if(!TextUtils.isEmpty(resultMsg)){
                                DialogUtil.getInstance().showCustomToast(AddInviteActivity.this,""+resultMsg,Gravity.CENTER);
                            }
                        }
                    }
                }

                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse){
                    super.onFailure(statusCode,headers,throwable,errorResponse);
                    AsyncHttpClientUtil.onFailure(AddInviteActivity.this,statusCode);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 显示发布活动对话框
     */
    private void showPublishEventDialog(){
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
        if(TextUtils.isEmpty(actId)){
            customBuilder.setMessage("您确定发布活动邀请吗?");
        }else {
            customBuilder.setMessage("您确定编辑活动邀请吗?");
        }
        customBuilder.setGravity(Gravity.CENTER);
        customBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        customBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String members = getChooseMembers();
                requestCreateEventTask( actName, actContent, startDate, endDate, actImage, maxTake, actUrl, portable, members);
            }
        });
        customBuilder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CONTACT_REQUEST_CODE && resultCode == RESULT_OK){
            if(null != data){
                boolean isAll = data.getBooleanExtra("isAll",false);
                GuestVo guestVo = (GuestVo) data.getSerializableExtra("guestVo");
                ArrayList<MemberVo> memberList = guestVo.getMember();
                String roleId = guestVo.getRoleid();
                String roleid = null;
                if(isAll){
                    selectMap.put(roleId, true);
                }else {
                    selectMap.put(roleId,false);
                }
                chooseMemberMap.put(roleId,memberList);
                for (GuestVo guest : guestList){
                    roleid = guest.getRoleid();
                    if(roleId.equals(roleid)){
                        guest.setCount(memberList == null ? 0: memberList.size());
                    }
                }
                eventAddInviteAdapter.setSelectMap(selectMap);
            }
        }
    }
}
