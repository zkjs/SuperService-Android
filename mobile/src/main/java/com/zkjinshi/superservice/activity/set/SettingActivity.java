package com.zkjinshi.superservice.activity.set;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.common.InviteCodesActivity;
import com.zkjinshi.superservice.activity.common.MoreActivity;
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

    public static int REQUEST_IMAGE = 1;

    private TextView usernameTv;
    private CircleImageView iconCiv;
    private ItemUserSettingView shopNameIusv;
    private ItemUserSettingView zoneIusv;
    private ItemUserSettingView phoneIusv;
    private ItemUserSettingView codeIusv;
    private ItemUserSettingView authIusv;
    private ItemUserSettingView jobIusv;

    private UserVo userVo;
    private DisplayImageOptions mOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
        iconCiv = (CircleImageView)findViewById(R.id.civ_user_icon);
        shopNameIusv = (ItemUserSettingView)findViewById(R.id.shopname_iusv);
        zoneIusv = (ItemUserSettingView)findViewById(R.id.zone_iusv);
        phoneIusv = (ItemUserSettingView)findViewById(R.id.phone_iusv);
        codeIusv = (ItemUserSettingView)findViewById(R.id.code_iusv);
        authIusv = (ItemUserSettingView)findViewById(R.id.autho_iusv);
        jobIusv = (ItemUserSettingView)findViewById(R.id.job_iusv);

        addRightIcon(codeIusv);
        addRightIcon(zoneIusv);

        mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.img_morentu)
                .showImageForEmptyUri(R.drawable.img_morentu)
                .showImageOnFail(R.drawable.img_morentu)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        getEmpInviteCode(CacheUtil.getInstance().getUserId(),CacheUtil.getInstance().getToken());

    }

    private void initData() {
        userVo = UserDBUtil.getInstance().queryUserById(CacheUtil.getInstance().getUserId());
        if(null != userVo){
            String userName = userVo.getUserName();
            if(!TextUtils.isEmpty(userName)){
                usernameTv.setText(userName);
            }
            String shopName = userVo.getShopName();
            if(!TextUtils.isEmpty(shopName)){
                shopNameIusv.setTextContent2(shopName);
            }
            if(!TextUtils.isEmpty(CacheUtil.getInstance().getUserPhone())){
                phoneIusv.setTextContent2(CacheUtil.getInstance().getUserPhone());
            }
        }

        String avatarUrl = ProtocolUtil.getAvatarUrl(CacheUtil.getInstance().getUserId());
        ImageLoader.getInstance().displayImage(avatarUrl, iconCiv, mOptions);
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
        //查看邀请码
        codeIusv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, InviteCodesActivity.class);
                intent.putExtra("from_setting", true);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
            }
        });

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }
        });

    }

    /**
     * 获取服务员邀请码
     *
     * @param saiesID
     * @param token
     */
    private void getEmpInviteCode(String saiesID, String token) {

        NetRequest netRequest = new NetRequest(ProtocolUtil.getEmpInviteCodeUrl());
        HashMap<String, String> bizMap = new HashMap<>();
        bizMap.put("salesid", saiesID);
        bizMap.put("token", token);

//        bizMap.put("page", saiesID);
//        bizMap.put("pagedata", token);
//        page 默认第一页
//        pagedata 默认10条

        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(SettingActivity.this, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(SettingActivity.this) {
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
                super.onNetworkResponseSucceed(result);
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                String jsonResult = result.rawResult;
                Gson gson = new Gson();
                SempCodeReturnBean sempCodeReturnBean = gson.fromJson(jsonResult,
                        SempCodeReturnBean.class);
                final Head head  = sempCodeReturnBean.getHead();
                final List<InviteCode> codes = sempCodeReturnBean.getCode_data();
                if (head.isSet() && ( null != codes && !codes.isEmpty())) {
                    //TODO:查询用户邀请码列表成功 并且邀请码不为空, 显示邀请码列表
                    String inviteCode = codes.get(0).getSalecode();
                    codeIusv.setTextContent2(inviteCode);
                } else {
                    //TODO: 邀请码不存在，为当前服务员生成邀请码
                    newInviteCodeForSaler(CacheUtil.getInstance().getShopID(), CacheUtil.getInstance().getUserId(), CacheUtil.getInstance().getToken());

                }
            }

            @Override
            public void beforeNetworkRequestStart() {
                //网络请求前
            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }

    /**
     * 为当前销售服务员生成邀请码
     */
    private void newInviteCodeForSaler(String shopID, String salesID, String token) {

        NetRequest netRequest = new NetRequest(ProtocolUtil.getNewRandomInviteCodeUrl());
        HashMap<String, String> bizMap = new HashMap<>();

        if(TextUtils.isEmpty(shopID)){
            DialogUtil.getInstance().showCustomToast(SettingActivity.this,
                    "当前shopID不能为空", Gravity.CENTER);
            return ;
        }
        bizMap.put("shopid", shopID);
        bizMap.put("salesid", salesID);
        bizMap.put("token", token);

        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(SettingActivity.this, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(SettingActivity.this) {
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
                String jsonResult = result.rawResult;
                try {
                    JSONObject resultObj = new JSONObject(jsonResult);
                    Boolean isSuccess = resultObj.getBoolean("set");
                    if (isSuccess) {

                        String inviteCode = resultObj.getString("code");
                        if (!TextUtils.isEmpty(inviteCode)) {
                            codeIusv.setTextContent2(inviteCode);
                        }
                    } else {

                        int errCode = resultObj.getInt("err");
                        if (errCode == 400) {
                            DialogUtil.getInstance().showCustomToast(SettingActivity.this,
                                    "身份信息已经过期，请重新登录", Gravity.CENTER);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void beforeNetworkRequestStart() {
                //网络请求前
            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }
}
