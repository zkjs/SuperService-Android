package com.zkjinshi.superservice.pad.activity.set;

import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.superservice.pad.response.BaseResponse;
import com.zkjinshi.superservice.pad.utils.CacheUtil;
import com.zkjinshi.superservice.pad.utils.Constants;
import com.zkjinshi.superservice.pad.R;
import com.zkjinshi.superservice.pad.base.BaseActivity;
import com.zkjinshi.superservice.pad.utils.ProtocolUtil;
import com.zkjinshi.superservice.pad.utils.StringUtil;
import com.zkjinshi.superservice.pad.view.CustomImgDialog;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * 开发者：JimmyZhang
 * 日期：2016/5/24
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */

public class AddWhiteUserActivity extends BaseActivity {

    private EditText inputUserNameEtv,inputPhoneEtv,remarkEtv;
    private Button saveBtn;
    private ImageButton backIBtn;
    private Handler handler;
    private Dialog dialog;

    private void initView(){
        inputUserNameEtv = (EditText)findViewById(R.id.et_input_name);
        inputPhoneEtv = (EditText)findViewById(R.id.et_input_phone);
        remarkEtv = (EditText)findViewById(R.id.et_remark);
        saveBtn = (Button)findViewById(R.id.btn_save);
        backIBtn = (ImageButton)findViewById(R.id.header_back_btn);
    }

    private void initData(){
        handler = new Handler();
    }

    private void initListeners(){

        //返回
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //保存
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userNameStr = inputUserNameEtv.getText().toString().trim();
                String phoneStr = inputPhoneEtv.getText().toString().trim();
                String remarkStr = remarkEtv.getText().toString().trim();
                if(TextUtils.isEmpty(userNameStr)){
                    showResultDialog("会员名称不能为空！");
                    return;
                }
                if(TextUtils.isEmpty(phoneStr)){
                    showResultDialog("手机号码不能为空！");
                    return;
                }
                if(!StringUtil.isPhoneNumber(phoneStr)){
                    showResultDialog("会员手机号码不正确！");
                    return;
                }
                requestAddWhiteTask(userNameStr,phoneStr,remarkStr);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_white_user);
        initView();
        initData();
        initListeners();
    }

    /**
     * 新增白名单
     * @param username
     * @param phone
     * @param rmk
     */
    private void requestAddWhiteTask(String username,String phone,String rmk){
        try {
            AsyncHttpClient httpClient = new AsyncHttpClient();
            httpClient.setTimeout(Constants.OVERTIMEOUT);
            httpClient.addHeader("Content-Type","application/json; charset=UTF-8");
            httpClient.addHeader("Token", CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username",username);
            jsonObject.put("phone",phone);
            jsonObject.put("rmk",rmk);
            StringEntity stringEntity = new StringEntity(jsonObject.toString(),"UTF-8");
            String requestUrl = ProtocolUtil.getAddWhiteUserUrl();
            httpClient.post(AddWhiteUserActivity.this,requestUrl,stringEntity,"application/json",new JsonHttpResponseHandler(){

                @Override
                public void onStart() {
                    super.onStart();
                    DialogUtil.getInstance().showAvatarProgressDialog(AddWhiteUserActivity.this,"");
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    if(null != response){
                        BaseResponse addWhiteResponse = new Gson().fromJson(response.toString(),BaseResponse.class);
                        if(null != addWhiteResponse){
                            int resultCode = addWhiteResponse.getRes();
                            if(0 == resultCode){
                                updateSuccessStatus();
                            }else {
                                String resultMsg = addWhiteResponse.getResDesc();
                                if(!TextUtils.isEmpty(resultMsg)){
                                    showResultDialog(resultMsg);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     *  检查提示框
     * @param message
     */
    private void showResultDialog(String message){
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(AddWhiteUserActivity.this);
        customBuilder.setMessage(message);
        customBuilder.setGravity(Gravity.CENTER);
        customBuilder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        customBuilder.create().show();
    }

    private void updateSuccessStatus(){
        showSuccessDialog();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                finish();
            }
        },1000);
    }

    /**
     * 显示添加成功对话框
     */
    private void showSuccessDialog(){
        CustomImgDialog.Builder customBuilder = new CustomImgDialog.Builder(this);
        customBuilder.setImagePath(Uri.parse("res:///" + R.mipmap.ic_gou_blue_b));
        customBuilder.setPositiveButton("添加成功", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }
        });
        dialog = customBuilder.create();
        dialog.show();
    }

}
