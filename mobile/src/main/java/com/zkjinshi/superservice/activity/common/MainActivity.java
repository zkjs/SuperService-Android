package com.zkjinshi.superservice.activity.common;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blueware.agent.android.BlueWare;
import com.facebook.drawee.view.SimpleDraweeView;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.pyxis.bluetooth.NetBeaconVo;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.set.ClientActivity;
import com.zkjinshi.superservice.activity.set.SettingActivity;
import com.zkjinshi.superservice.activity.set.TeamContactsActivity;
import com.zkjinshi.superservice.base.BaseAppCompatActivity;
import com.zkjinshi.superservice.blueTooth.BlueToothManager;
import com.zkjinshi.superservice.emchat.EasemobIMHelper;
import com.zkjinshi.superservice.ext.activity.facepay.CheckOutActivity;
import com.zkjinshi.superservice.manager.CheckoutInnerManager;
import com.zkjinshi.superservice.manager.SSOManager;
import com.zkjinshi.superservice.manager.YunBaSubscribeManager;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.sqlite.DBOpenHelper;
import com.zkjinshi.superservice.utils.AccessControlUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.view.CustomExtDialog;
import com.zkjinshi.superservice.vo.IdentityType;

import java.util.ArrayList;


/**
 * 主页面
 * 开发者：JimmyZhang
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MainActivity extends BaseAppCompatActivity {

    private MainActivityController mainActivityController;

    private final static String TAG = MainActivity.class.getSimpleName();

    private SimpleDraweeView avatarIv;
    private TextView        usernameTv;
    private TextView        shopnameTv;
    private RelativeLayout  avatarLayout;
    private ImageButton     setIbtn;
    private TextView checkOutTv,clientTv,serviceTagTv;

    private void initView(){
        avatarIv   = (SimpleDraweeView)findViewById(R.id.avatar_iv);
        usernameTv = (TextView)findViewById(R.id.username_tv);
        shopnameTv = (TextView)findViewById(R.id.shop_name_tv);
        avatarLayout = (RelativeLayout)findViewById(R.id.avatar_rlt);
        setIbtn = (ImageButton)findViewById(R.id.edit_avatar_ibtn);
        checkOutTv = (TextView)findViewById(R.id.amount_tv);
        clientTv = (TextView)findViewById(R.id.client_tv);
        serviceTagTv = (TextView)findViewById(R.id.service_tag_tv);
    }

    private void initData(){
        MainController.getInstance().init(this);
        MainController.getInstance().checkAppVersion();
        //String payInfo = CacheUtil.getInstance().getPayInfo();
        if(AccessControlUtil.isShowView(AccessControlUtil.CASHREGISTER)){
            checkOutTv.setVisibility(View.VISIBLE);
        }else {
            checkOutTv.setVisibility(View.GONE);
        }
        if(AccessControlUtil.isShowView(AccessControlUtil.MEMBER)){
            clientTv.setVisibility(View.VISIBLE);
        }else {
            clientTv.setVisibility(View.GONE);
        }
        if(AccessControlUtil.isShowView(AccessControlUtil.SERVICETAG)){
            serviceTagTv.setVisibility(View.VISIBLE);
        }else {
            serviceTagTv.setVisibility(View.GONE);
        }

        //打开蓝牙请求
        BlueToothManager.getInstance().openBluetooth();
        BlueToothManager.getInstance().startIBeaconService(new ArrayList<NetBeaconVo>());
    }

    private void initListeners(){

        //编辑头像按钮点击事件
        findViewById(R.id.edit_avatar_ibtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
            }
        });

        //团队联系人点击事件
        findViewById(R.id.team_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myTeamContacts = new Intent(MainActivity.this, TeamContactsActivity.class);
               // Intent myTeamContacts = new Intent(MainActivity.this, EmployeeAddActivity.class);
                MainActivity.this.startActivity(myTeamContacts);
                overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
            }
        });

        //邀请码点击
        findViewById(R.id.tv_invite_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inviteCode = new Intent(MainActivity.this, InviteCodesActivity.class);
                MainActivity.this.startActivity(inviteCode);
                overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
            }
        });

        //会员管理
        clientTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myClient = new Intent(MainActivity.this, ClientActivity.class);
                MainActivity.this.startActivity(myClient);
                overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
            }
        });

        //收款台
        checkOutTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CheckoutInnerManager.getInstance().isInnerCheckout()){
                    Intent intent = new Intent(MainActivity.this,CheckOutActivity.class);
                    startActivity(intent);
                }else {
                    DialogUtil.getInstance().showCustomToast(view.getContext(),"您不在收款区域,暂时无法收款!",Gravity.CENTER);
                }

            }
        });

        //服务标签
        serviceTagTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //设置点击事件
        findViewById(R.id.setting_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
            }
        });

        //退出点击事件
        findViewById(R.id.exit_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final CustomExtDialog.Builder customExtBuilder = new CustomExtDialog.Builder(MainActivity.this);
                customExtBuilder.setTitle(getString(R.string.exit));
                customExtBuilder.setMessage(getString(R.string.confirm_exit_the_current_account));
                customExtBuilder.setGravity(Gravity.CENTER);
                customExtBuilder.setNegativeButton(
                    getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }
                );

                customExtBuilder.setPositiveButton(
                    getString(R.string.confirm),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //移除云巴订阅推送
                            YunBaSubscribeManager.getInstance().unSubscribe(MainActivity.this,null);
                            //取消订阅别名
                            YunBaSubscribeManager.getInstance().cancelAlias(MainActivity.this);
                            //环信接口退出
                            EasemobIMHelper.getInstance().logout();
                            //修改登录状态
                            CacheUtil.getInstance().setLogin(false);
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        }
                    }
                );
                customExtBuilder.create().show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BlueWare.withApplicationToken("55C9EB081F38564DB9672705D06EF07955").start(this.getApplication());
        setTitle(R.string.app_name);

        setContentView(R.layout.activity_main);
        DBOpenHelper.DB_NAME = CacheUtil.getInstance().getUserId() + ".db";
        mainActivityController = new MainActivityController(this);
        mainActivityController.onCreate();

        initView();
        initData();
        initListeners();
    }

    /**
     * 设置tab右上角上的消息数量
     * @param postion
     * @param num
     */
    public void setMessageNum(int postion, int num){
        mainActivityController.setMessageNum(postion, num);
    }

    protected void onResume(){
        super.onResume();

        String userName = CacheUtil.getInstance().getUserName();
        usernameTv.setText(userName);
        String shopName = CacheUtil.getInstance().getShopFullName();
        shopnameTv.setText(shopName);
        TextView teamTv = (TextView)findViewById(R.id.team_tv);
        if(IdentityType.BUSINESS ==  CacheUtil.getInstance().getLoginIdentity()){
            teamTv.setText("团队管理");
            setIbtn.setVisibility(View.GONE);
        }else{
            teamTv.setText("团队联系人");
            avatarIv.setImageURI(Uri.parse(CacheUtil.getInstance().getUserPhotoUrl()));
            setIbtn.setVisibility(View.VISIBLE);
        }
    }

    protected void onDestroy(){
        super.onDestroy();
        Log.i(TAG, "protected void onDestroy");
        mainActivityController.clearImageChache();
        //退出环信
        EasemobIMHelper.getInstance().logout();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mainActivityController.onPostCreate();
    }

    @Override
    public void onBackPressed() {
        if(mainActivityController.isToggleOpen()){
            mainActivityController.closeToggle();
        }else {
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
