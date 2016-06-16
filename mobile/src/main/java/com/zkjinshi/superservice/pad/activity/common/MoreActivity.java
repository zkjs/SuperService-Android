package com.zkjinshi.superservice.pad.activity.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.pad.manager.SSOManager;
import com.zkjinshi.superservice.pad.utils.CacheUtil;
import com.zkjinshi.superservice.pad.utils.Constants;
import com.zkjinshi.superservice.pad.utils.ProtocolUtil;
import com.zkjinshi.superservice.pad.R;
import com.zkjinshi.superservice.pad.activity.mine.MineUiController;
import com.zkjinshi.superservice.pad.base.BaseFragmentActivity;
import com.zkjinshi.superservice.pad.utils.AsyncHttpClientUtil;
import com.zkjinshi.superservice.pad.vo.PayloadVo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Set;

import cz.msebera.android.httpclient.Header;

/**
 * 客服注册完善信息页面
 * 开发者：dujiande
 * 日期：2015/9/21
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MoreActivity extends BaseFragmentActivity{

    private SimpleDraweeView avatarCiv;
    private TextView nameTv;
    private EditText inputNameEt;
    private CheckBox sexCbx;
    private ImageButton backIBtn;
    private String picPath = null;
    private View contentRlt;
    private float offsetY;

    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //屏蔽输入法自动弹出
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_more);

        mContext = this;
        initView();
        initData();
        initListener();
    }

    private void initView() {
        backIBtn = (ImageButton)findViewById(R.id.header_bar_btn_back);
        avatarCiv = (SimpleDraweeView)findViewById(R.id.avatar);
        nameTv = (TextView)findViewById(R.id.org_username_tv);
        inputNameEt = (EditText)findViewById(R.id.new_username_et);
        sexCbx = (CheckBox)findViewById(R.id.sex_cbx);
        contentRlt = findViewById(R.id.content_rlt);
    }

    private void initData() {
        MineUiController.getInstance().init(this);
        if(getIntent().getBooleanExtra("from_setting",false)) {
            backIBtn.setVisibility(View.VISIBLE);
            nameTv.setText(CacheUtil.getInstance().getUserName());
            if(CacheUtil.getInstance().getSex().equals("0")){
                sexCbx.setChecked(false);
            }else{
                sexCbx.setChecked(true);
            }
            String avatarUrl = CacheUtil.getInstance().getUserPhotoUrl();
            avatarCiv.setImageURI(Uri.parse(avatarUrl));
        }
        offsetY = getResources().getDimension(R.dimen.key_up_more_height);

    }

    private void initListener() {

        //返回
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }
        });

        findViewById(R.id.go_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //完善资料api接口
                sempupdate();
            }
        });

        avatarCiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MineUiController.getInstance().showChoosePhotoDialog();
            }
        });

        //添加layout大小发生改变监听器
        findViewById(R.id.scrollView).addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right,
                                       int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if(oldBottom != 0 && bottom != 0 &&(oldBottom - bottom > 0)){
                    //Toast.makeText(mContext, "监听到软键盘弹起...", Toast.LENGTH_SHORT).show();
                    moveUp();
                }else if(oldBottom != 0 && bottom != 0 &&(bottom - oldBottom > 0)){
                    //Toast.makeText(mContext, "监听到软件盘关闭...", Toast.LENGTH_SHORT).show();
                    moveDown();
                }
            }
        });
    }

    private void moveUp(){
        ViewHelper.setTranslationY(contentRlt,0);
        long time = 300;
        ViewPropertyAnimator.animate(contentRlt).translationYBy(-offsetY).setDuration(time);
    }

    private void moveDown(){
        long time = 300;
        ViewHelper.setTranslationY(contentRlt,-offsetY);
        ViewPropertyAnimator.animate(contentRlt).translationYBy(offsetY).setDuration(time);
    }

    /*
    * 资料更新
    * */
    private void sempupdate() {
        if(!getIntent().getBooleanExtra("from_setting",false) && picPath == null) {
            DialogUtil.getInstance().showToast(this,"请上传头像");
            return;
        }
        String input = inputNameEt.getText().toString();
        final String name = TextUtils.isEmpty(input)? nameTv.getText().toString() : input;
        final String sex = sexCbx.isChecked() ? "1" : "0";
        if(TextUtils.isEmpty(name)){
            DialogUtil.getInstance().showToast(this,"请输入昵称。");
            return;
        }
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Token",CacheUtil.getInstance().getExtToken());
            String url = ProtocolUtil.registerUpdateUserInfo();
            if(getIntent().getBooleanExtra("from_setting",false)){
                url = ProtocolUtil.updateUserInfo();
            }
            RequestParams params = new RequestParams();
            params.put("sex",sex);
            params.put("username",name);
            params.put("realname",name);
            if(picPath != null){
                params.put("image",new File(picPath));
            }
            client.post(url, params, new JsonHttpResponseHandler(){
                public void onStart(){
                    super.onStart();
                    DialogUtil.getInstance().showAvatarProgressDialog(MoreActivity.this,"");
                }

                public void onFinish(){
                    super.onFinish();
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                    super.onSuccess(statusCode,headers,response);
                    try {
                        if(response.getInt("res") == 0){
                            CacheUtil.getInstance().setUserName(name);
                            CacheUtil.getInstance().setSex(sex);
                            if(!getIntent().getBooleanExtra("from_setting",false)){
                                String token = response.getString("token");
                                CacheUtil.getInstance().setExtToken(token);
                                PayloadVo payloadVo = SSOManager.getInstance().decodeToken(token);
                                if(null != payloadVo){
                                    Set<String> featureSet = payloadVo.getFeatures();
                                    if(null != featureSet){
                                        CacheUtil.getInstance().setFeatures(featureSet);
                                    }
                                }
                                getUserInfo();
                            }else{
                                if(picPath != null){
                                    JSONObject dataJson = response.getJSONObject("data");
                                    String imgurl = dataJson.getString("userimage");
                                    imgurl = ProtocolUtil.getHostImgUrl(imgurl);
                                    CacheUtil.getInstance().saveUserPhotoUrl(imgurl);
                                }
                                finish();
                                overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
                            }

                        }else{
                            Toast.makeText(MoreActivity.this,response.getString("resDesc"),Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse){
                    super.onFailure(statusCode,headers,throwable,errorResponse);
                    AsyncHttpClientUtil.onFailure(MoreActivity.this,statusCode);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取详细信息
     */
    private void getUserInfo() {
        LoginController.getInstance().getUserInfo(this, CacheUtil.getInstance().getUserId(), new LoginController.CallBackListener() {
            @Override
            public void successCallback(JSONObject response) {
                try{
                    Intent mainIntent = new Intent(MoreActivity.this, ZoneActivity.class);
                    startActivity(mainIntent);
                    finish();
                    overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MineUiController.getInstance().onActivityResult(requestCode,resultCode,data,avatarCiv);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.FLAG_MODIFY_FINISH:// 修改完成
                    picPath = CacheUtil.getInstance().getPicPath();
                    break;

                default:
                    break;
            }
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        {
            moveDown();
        }
        else{

        }
        super.onConfigurationChanged(newConfig);
    }

}
