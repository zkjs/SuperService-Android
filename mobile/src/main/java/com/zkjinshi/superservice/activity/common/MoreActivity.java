package com.zkjinshi.superservice.activity.common;

import android.app.Activity;
import android.content.Intent;
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
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.mine.MineNetController;
import com.zkjinshi.superservice.activity.mine.MineUiController;
import com.zkjinshi.superservice.base.BaseFragmentActivity;
import com.zkjinshi.superservice.utils.AsyncHttpClientUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.FileUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.utils.task.ImgAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //屏蔽输入法自动弹出
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_more);
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

}
