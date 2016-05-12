package com.zkjinshi.superservice.activity.set;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.common.InviteCodesActivity;
import com.zkjinshi.superservice.activity.common.LoginController;
import com.zkjinshi.superservice.activity.common.MainActivity;
import com.zkjinshi.superservice.activity.common.MoreActivity;
import com.zkjinshi.superservice.activity.common.WebViewActivity;
import com.zkjinshi.superservice.activity.common.ZoneActivity;
import com.zkjinshi.superservice.activity.mine.MineNetController;
import com.zkjinshi.superservice.activity.mine.MineUiController;
import com.zkjinshi.superservice.base.BaseActivity;
import com.zkjinshi.superservice.bean.Head;
import com.zkjinshi.superservice.bean.InviteCode;
import com.zkjinshi.superservice.bean.SempCodeReturnBean;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.sqlite.UserDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.MD5Util;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.utils.StringUtil;
import com.zkjinshi.superservice.view.CircleImageView;
import com.zkjinshi.superservice.view.CustomInputDialog;
import com.zkjinshi.superservice.view.ItemUserSettingView;
import com.zkjinshi.superservice.vo.IdentityType;
import com.zkjinshi.superservice.vo.UserVo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * 开发者：dujiande
 * 日期：2015/11/25
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SettingActivity extends BaseActivity {

    private final static String TAG = SettingActivity.class.getSimpleName();

    private TextView usernameTv;
    private SimpleDraweeView iconCiv;
    private ItemUserSettingView shopNameIusv;
    private ItemUserSettingView zoneIusv;
    private ItemUserSettingView phoneIusv;
    private ItemUserSettingView passwordIusv;
    private ItemUserSettingView aboutusIusv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();
        initListener();
    }

    protected void onResume(){
        super.onResume();
        initData();
    }

    private void addRightIcon(ItemUserSettingView item){
        Drawable drawable= getResources().getDrawable(R.mipmap.ic_get_into_w);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        item.getmTextContent2().setCompoundDrawables(null, null, drawable, null);
    }

    private void initView() {
        usernameTv = (TextView)findViewById(R.id.username_tv);
        iconCiv = (SimpleDraweeView)findViewById(R.id.civ_user_icon);
        shopNameIusv = (ItemUserSettingView)findViewById(R.id.shopname_iusv);
        zoneIusv = (ItemUserSettingView)findViewById(R.id.zone_iusv);
        phoneIusv = (ItemUserSettingView)findViewById(R.id.phone_iusv);
        passwordIusv = (ItemUserSettingView)findViewById(R.id.password_iusv);
        aboutusIusv = (ItemUserSettingView)findViewById(R.id.about_iusv);
        addRightIcon(zoneIusv);
        addRightIcon(passwordIusv);
        addRightIcon(aboutusIusv);
    }

    private void initData() {
        usernameTv.setText(CacheUtil.getInstance().getUserName());
        shopNameIusv.setTextContent2(CacheUtil.getInstance().getShopFullName());
        phoneIusv.setTextContent2(CacheUtil.getInstance().getUserPhone());
        String avatarUrl = CacheUtil.getInstance().getUserPhotoUrl();
        iconCiv.setImageURI(Uri.parse(avatarUrl));

        if(IdentityType.BUSINESS ==  CacheUtil.getInstance().getLoginIdentity()){
            passwordIusv.setVisibility(View.VISIBLE);
            findViewById(R.id.border_psw_line).setVisibility(View.VISIBLE);
        }else{
            passwordIusv.setVisibility(View.GONE);
            findViewById(R.id.border_psw_line).setVisibility(View.GONE);
        }
    }

    private void initListener() {

        //设置头像
        findViewById(R.id.rl_user_icon_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, MoreActivity.class);
                intent.putExtra("from_setting",true);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
            }
        });
        //设置区域
        zoneIusv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, ZoneActivity.class);
                intent.putExtra("from_setting", true);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
            }
        });
        // 返回
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }
        });
        //关于我们
        aboutusIusv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, WebViewActivity.class);
                intent.putExtra("webview_url","http://zkjinshi.com/about_us/");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });

        //修改密码
        passwordIusv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputDialog();
            }
        });

    }

    private void showInputDialog() {
        final CustomInputDialog.Builder customBuilder = new CustomInputDialog.Builder(this);
        customBuilder.setTitle("验证原密码");
        customBuilder.setMessage("为保障您的数据安全，修改密码请填写原密码。");
        customBuilder.setGravity(Gravity.CENTER);
        customBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        customBuilder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                EditText inputEt = customBuilder.inputEt;
                String inputStr = inputEt.getText().toString();
                vertifyPassword(inputStr);
            }
        });
        customBuilder.create().show();
    }

    private void vertifyPassword(String inputStr) {
        if(StringUtil.isEmpty(inputStr)){
            showDialogMsg("密码错误，请重新输入。");
            return;
        }

        final String passwrdMd5 =  MD5Util.MD5(inputStr);
        LoginController.getInstance().vertifyLoginPassword(this, passwrdMd5, new LoginController.CallBackExtListener() {
            @Override
            public void successCallback(JSONObject response) {
                Intent intent = new Intent(SettingActivity.this, UpdatePasswordActivity.class);
                intent.putExtra("passwrdMd5",passwrdMd5);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
            }

            @Override
            public void failCallback(JSONObject response) {
                showDialogMsg("密码错误，请重新输入。");
            }
        });
    }

    private void showDialogMsg(String msg){
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
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

}
