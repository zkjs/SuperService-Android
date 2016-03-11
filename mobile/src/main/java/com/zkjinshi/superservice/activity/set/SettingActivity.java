package com.zkjinshi.superservice.activity.set;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.common.InviteCodesActivity;
import com.zkjinshi.superservice.activity.common.MoreActivity;
import com.zkjinshi.superservice.activity.common.WebViewActivity;
import com.zkjinshi.superservice.activity.common.ZoneActivity;
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
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.view.CircleImageView;
import com.zkjinshi.superservice.view.ItemUserSettingView;
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
public class SettingActivity extends Activity  {

    private final static String TAG = SettingActivity.class.getSimpleName();



    private TextView usernameTv;
    private SimpleDraweeView iconCiv;
    private ItemUserSettingView shopNameIusv;
    private ItemUserSettingView zoneIusv;
    private ItemUserSettingView phoneIusv;
    private ItemUserSettingView authIusv;
    private ItemUserSettingView jobIusv;



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
        authIusv = (ItemUserSettingView)findViewById(R.id.autho_iusv);
        jobIusv = (ItemUserSettingView)findViewById(R.id.job_iusv);

        addRightIcon(zoneIusv);


    }

    private void initData() {

        usernameTv.setText(CacheUtil.getInstance().getUserName());
        shopNameIusv.setTextContent2(CacheUtil.getInstance().getShopFullName());
        phoneIusv.setTextContent2(CacheUtil.getInstance().getUserPhone());
        String avatarUrl = CacheUtil.getInstance().getUserPhotoUrl();
        iconCiv.setImageURI(Uri.parse(avatarUrl));
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
        findViewById(R.id.about_iusv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, WebViewActivity.class);
                intent.putExtra("webview_url","http://zkjinshi.com/about_us/");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });

    }

}
