package com.zkjinshi.superservice.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.zkjinshi.base.util.DeviceUtils;
import com.zkjinshi.base.util.ImageUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.common.contact.ContactsActivity;

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

    private void initView(){
        avatarIv = (ImageView)findViewById(R.id.avatar_iv);
        usernameTv = (TextView)findViewById(R.id.username_tv);
        shopnameTv = (TextView)findViewById(R.id.shop_name_tv);
        onlineCbx = (CheckBox)findViewById(R.id.online_cbx);
        avatarLayout = (RelativeLayout)findViewById(R.id.avatar_rlt);

        //设置在线或离线
        onlineCbx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

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
                finish();
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
        Bitmap displayBitmap = BitmapFactory.decodeFile(photoFilePath);
        if (displayBitmap != null) {
            int screenHeight = DeviceUtils.getScreenHeight(this);
            int screanWidth = DeviceUtils.getScreenWidth(this);
            int height = (int)(0.29*screenHeight);
            int width = (int)(0.71*screanWidth);

            displayBitmap = ImageUtil.cropBitmap(displayBitmap, width, height);
            //displayBitmap = ImageUtil.loadThumbBitmap(getActivity(), displayBitmap);
            avatarIv.setImageBitmap(displayBitmap);
        }
        // CacheUtil.getInstance().savePicPath(photoFilePath);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        setContentView(R.layout.activity_main);
        mainActivityController = new MainActivityController(this);
        mainActivityController.onCreate();
        initView();
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

}
