package com.zkjinshi.superservice.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zkjinshi.base.net.core.WebSocketManager;
import com.zkjinshi.base.util.DeviceUtils;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.ImageUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.set.ContactsActivity;
import com.zkjinshi.superservice.activity.set.TeamContactsActivity;
import com.zkjinshi.superservice.bean.BaseBean;
import com.zkjinshi.superservice.listener.MessageListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestListener;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.sqlite.DBOpenHelper;
import com.zkjinshi.superservice.sqlite.UserDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.utils.task.ImgAsyncTask;
import com.zkjinshi.superservice.vo.SexType;
import com.zkjinshi.superservice.vo.UserVo;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 主页面
 * 开发者：JimmyZhang
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MainActivity extends AppCompatActivity {

    private MainActivityController mainActivityController;

    private final static String TAG = MainActivity.class.getSimpleName();
    public static int REQUEST_IMAGE = 1;

    private ImageView avatarIv;
    private TextView usernameTv;
    private TextView shopnameTv;
    private CheckBox onlineCbx;
    private RelativeLayout avatarLayout;
    private MessageListener messageListener;
    private UserVo userVo;



    private void initView(){
        avatarIv = (ImageView)findViewById(R.id.avatar_iv);
        usernameTv = (TextView)findViewById(R.id.username_tv);
        shopnameTv = (TextView)findViewById(R.id.shop_name_tv);
        onlineCbx = (CheckBox)findViewById(R.id.online_cbx);
        avatarLayout = (RelativeLayout)findViewById(R.id.avatar_rlt);
    }

    private void initData(){
        messageListener = new MessageListener();
        initService(messageListener);
        userVo = UserDBUtil.getInstance().queryUserById(CacheUtil.getInstance().getUserId());
        mainActivityController.setUserPhoto(CacheUtil.getInstance().getUserPhotoUrl(), avatarIv);
        if(null != userVo){
            String userName = userVo.getUserName();
            if(!TextUtils.isEmpty(userName)){
                usernameTv.setText(userName);
            }
            String shopName = userVo.getShopName();
            if(!TextUtils.isEmpty(shopName)){
                shopnameTv.setText(shopName);
            }
        }
        onlineCbx.setChecked(CacheUtil.getInstance().getOnline());


    }

    private void initListeners(){
        //设置在线或离线
        onlineCbx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                CacheUtil.getInstance().setOnline(b);
            }
        });

        //编辑头像按钮点击事件
        findViewById(R.id.edit_avatar_ibtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MultiImageSelectorActivity.class);
                // 是否显示调用相机拍照
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
                // 最大图片选择数量
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
                // 设置模式 (支持 单选/MultiImageSelectorActivity.MODE_SINGLE 或者 多选/MultiImageSelectorActivity.MODE_MULTI)
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
                startActivityForResult(intent, REQUEST_IMAGE);
            }
        });

        //团队联系人点击事件
        findViewById(R.id.team_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myTeamContacts = new Intent(MainActivity.this, TeamContactsActivity.class);
                MainActivity.this.startActivity(myTeamContacts);
            }
        });

        //我的客人点击事件
        findViewById(R.id.client_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myClient = new Intent(MainActivity.this, ContactsActivity.class);
                MainActivity.this.startActivity(myClient);
            }
        });

        //设置点击事件
        findViewById(R.id.setting_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //退出点击事件
        findViewById(R.id.exit_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        //关于我们点击事件
        findViewById(R.id.about_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE){
            if(resultCode == Activity.RESULT_OK){
                // 获取返回的图片列表
                List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                Log.e(TAG, path.toString());
                String photoFilePath = path.get(0);
                setAvatar(photoFilePath);
            }
        }
    }

    private void setAvatar(String photoFilePath){
        ImgAsyncTask imgAsyncTask = new ImgAsyncTask(this,photoFilePath,avatarIv,
                new ImgAsyncTask.CallBack() {
                    @Override
                    public void getNewPath(String path) {
                        submitAvatar(path);
                    }
                });
        imgAsyncTask.execute();
    }

    private void submitAvatar(final String path){
        NetRequest netRequest = new NetRequest(ProtocolUtil.getSempupdateUrl());
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("salesid", CacheUtil.getInstance().getUserId());
        bizMap.put("token",CacheUtil.getInstance().getToken());
        netRequest.setBizParamMap(bizMap);

        HashMap<String,File> fileMap = new HashMap<String, File>();
        fileMap.put("file", new File(path));
        netRequest.setFileMap(fileMap);

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
                BaseBean baseBean = new Gson().fromJson(result.rawResult, BaseBean.class);
                if (baseBean.isSet()) {
                    DialogUtil.getInstance().showToast(MainActivity.this, "头像上传成功");
                } else {
                    DialogUtil.getInstance().showToast(MainActivity.this, baseBean.getErr());
                }

            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
         setContentView(R.layout.activity_main);
        DBOpenHelper.DB_NAME = CacheUtil.getInstance().getUserId() + ".db";
        mainActivityController = new MainActivityController(this);
        mainActivityController.onCreate();
        initView();
        initData();
        initListeners();
    }

    protected void onDestroy(){
        super.onDestroy();
        Log.i(TAG,"protected void onDestroy");
        mainActivityController.clearImageChache();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mainActivityController.onPostCreate();
    }

    @Override
    public void onBackPressed() {
        if(mainActivityController.onBackPressed()){
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mainActivityController.toggleDrawer();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * 初始化socket
     */
    private void initService(MessageListener messageListener) {
        WebSocketManager.getInstance().initService(this).setMessageListener(messageListener);
    }

}
