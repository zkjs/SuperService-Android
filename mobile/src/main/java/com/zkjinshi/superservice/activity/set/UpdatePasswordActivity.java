package com.zkjinshi.superservice.activity.set;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.common.LoginController;
import com.zkjinshi.superservice.activity.common.MainActivity;
import com.zkjinshi.superservice.base.BaseActivity;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.MD5Util;
import com.zkjinshi.superservice.utils.StringUtil;
import com.zkjinshi.superservice.view.CustomImgDialog;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 密码修改
 * 开发者：dujiande
 * 更新者：JimmyZhang
 * 日期：2016/05/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class UpdatePasswordActivity extends BaseActivity {
    private final static String TAG = UpdatePasswordActivity.class.getSimpleName();

    private Context mContext;

    private EditText inputEt1;
    private EditText inputEt2;
    private Button confirmBtn;
    private LinearLayout imgllt1,imgllt2;
    private TextView nameTv;
    private CheckBox keepCb;

    private String orgPassword = "";
    private Handler handler;
    private Dialog dialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        mContext = this;

        initView();
        intData();
        initListener();
    }

    private void initView() {
        inputEt1 = (EditText)findViewById(R.id.psw_et1);
        inputEt2 = (EditText)findViewById(R.id.psw_et2);
        confirmBtn = (Button)findViewById(R.id.ok_btn);
        imgllt1 = (LinearLayout)findViewById(R.id.clear_llt1);
        imgllt2 = (LinearLayout)findViewById(R.id.clear_llt2);
        nameTv = (TextView)findViewById(R.id.nameTv);
        keepCb = (CheckBox)findViewById(R.id.keep_pwd_cb);
    }

    private void intData() {
        handler = new Handler();
        imgllt1.setVisibility(View.GONE);
        imgllt2.setVisibility(View.GONE);
        nameTv.setText(CacheUtil.getInstance().getUserName());
        orgPassword = getIntent().getStringExtra("passwrdMd5");
    }

    private void initListener() {
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePassword();
            }
        });

        inputEt1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }
            @Override
            public void afterTextChanged(Editable input) {
                //1.监听手机输入
                String inputStr = input.toString();
                if(!TextUtils.isEmpty(inputStr)){
                    imgllt1.setVisibility(View.VISIBLE);
                }else{
                    imgllt1.setVisibility(View.GONE);
                }
            }
        });

        inputEt2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }
            @Override
            public void afterTextChanged(Editable input) {
                //1.监听手机输入
                String inputStr = input.toString();
                if(!TextUtils.isEmpty(inputStr)){
                    imgllt2.setVisibility(View.VISIBLE);
                }else{
                    imgllt2.setVisibility(View.GONE);
                }
            }
        });

        imgllt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputEt1.setText("");
            }
        });
        imgllt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputEt2.setText("");
            }
        });

        keepCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    inputEt1.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    inputEt2.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }else {
                    inputEt1.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    inputEt2.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

    }

    private void updatePassword() {
        String inputStr1 = inputEt1.getText().toString();
        String inputStr2 = inputEt2.getText().toString();

        if(StringUtil.isEmpty(inputStr1) || StringUtil.isEmpty(inputStr2)){
            showDialogMsg("密码不能为空");
            return;
        }
        if(!inputStr1.equals(inputStr2)){
            showDialogMsg("两次填写的密码不一致");
            return;
        }

        String newpasswrd = MD5Util.MD5(inputStr1);
        if(newpasswrd.equals(orgPassword)){
            showDialogMsg("新密码和原密码不能重复");
            return;
        }

        if(inputStr1.length() >= 8 && isContainNumber(inputStr1) && isContainAlphabet(inputStr1)){
            LoginController.getInstance().updateLoginPassword(mContext, orgPassword, newpasswrd, new LoginController.CallBackExtListener() {
                @Override
                public void successCallback(JSONObject response) {
                    updateSuccessStatus();
                }
                @Override
                public void failCallback(JSONObject response) {
                    showDialogMsg("设置失败");
                }
            });

        }else{
            showDialogMsg("密码必须是8位以上的英文字母和数字的组合。");
        }


    }

    private void showDialogMsg(String msg){
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(mContext);
        customBuilder.setMessage(msg);
        customBuilder.setGravity(Gravity.CENTER);
        customBuilder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        customBuilder.create().show();
    }

    private void showSuccessDialog(){
        CustomImgDialog.Builder customBuilder = new CustomImgDialog.Builder(mContext);
        customBuilder.setImagePath(Uri.parse("res:///" + R.mipmap.ic_gou_blue_b));
        customBuilder.setPositiveButton("设置成功", new DialogInterface.OnClickListener() {
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
     * 必须含有一个数字
     * @param str
     * @return
     */
    public  boolean isContainNumber(String str) {
        Pattern p = Pattern.compile("[A-Za-z0-9]*[0-9]+[A-Za-z0-9]*");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 必须含有一个字母
     * @param str
     * @return
     */
    public  boolean isContainAlphabet(String str) {
        Pattern p = Pattern.compile("[A-Za-z0-9]*[A-Za-z]+[A-Za-z0-9]*");
        Matcher m = p.matcher(str);
        return m.matches();
    }

}
