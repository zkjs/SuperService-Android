package com.zkjinshi.superservice.activity.set;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.chat.single.ChatActivity;
import com.zkjinshi.superservice.bean.ClientBindBean;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.view.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 客人绑定服务员操作
 * 开发者：vincent
 * 日期：2015/10/5
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientBindActivity extends Activity {

    private final static String TAG = ClientBindActivity.class.getSimpleName();

    private RelativeLayout mRlBack;
    private TextView    mTvTitle;
    private TextView    mTvSalerName;//专属客服姓名
    private TextView    mTvMemberName;
    private TextView    mTvMemberPhone;
    private TextView    mTvMemberLevel;
    private ImageButton mIbtnDianhua;
    private ImageButton mIbtnDuihua;
    private Button      mBtnConfirm;

    private CircleImageView  mCivMemberAvatar;
    private ClientBindBean   mClientBean;

    private String mShopID;
    private String mUserID;
    private String mToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_bind);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mRlBack          = (RelativeLayout)  findViewById(R.id.rl_back);
        mTvTitle         = (TextView)        findViewById(R.id.tv_title);
        mTvTitle.setText(getString(R.string.member_bind));
        mTvTitle.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        mCivMemberAvatar = (CircleImageView) findViewById(R.id.civ_member_avatar);
        mTvSalerName     = (TextView)        findViewById(R.id.tv_saler_name);
        mTvMemberName    = (TextView)        findViewById(R.id.tv_member_name);
        mTvMemberPhone   = (TextView)        findViewById(R.id.tv_member_phone);
        mTvMemberLevel   = (TextView)        findViewById(R.id.tv_member_level);
        mIbtnDianhua     = (ImageButton)     findViewById(R.id.ibtn_dianhua);
        mIbtnDuihua      = (ImageButton)     findViewById(R.id.ibtn_duihua);
        mBtnConfirm      = (Button)          findViewById(R.id.btn_confirm);
    }

    private void initData() {

        mShopID = CacheUtil.getInstance().getShopID();
        mUserID = CacheUtil.getInstance().getUserId();
        mToken  = CacheUtil.getInstance().getToken();

        mClientBean = (ClientBindBean) getIntent().getSerializableExtra("client_bean");
        showClient(mClientBean);
    }

    private void initListener() {

        mRlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientBindActivity.this.finish();
            }
        });

        mIbtnDianhua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mClientBean.getPhone())) {
                    //打电话
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mClientBean.getPhone()));
                    ClientBindActivity.this.startActivity(intent);
                }
            }
        });

        mIbtnDuihua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClientBindActivity.this, ChatActivity.class);
                String clientID = mClientBean.getUserid();
                if(!TextUtils.isEmpty(clientID)){
                    intent.putExtra(Constants.EXTRA_USER_ID, clientID);
                }

                if (!TextUtils.isEmpty(mShopID)) {
                    intent.putExtra(Constants.EXTRA_SHOP_ID, mShopID);
                }

                intent.putExtra(Constants.EXTRA_SHOP_NAME, CacheUtil.getInstance().getShopFullName());

                String clientName = mClientBean.getUsername();
                if (!TextUtils.isEmpty(clientName)) {
                    intent.putExtra(Constants.EXTRA_TO_NAME, clientName);
                }

                intent.putExtra(Constants.EXTRA_FROM_NAME, CacheUtil.getInstance().getUserName());
                startActivity(intent);
            }
        });

        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != mClientBean){
                    String fuid = mClientBean.getUserid();
                    if(TextUtils.isEmpty(fuid)){
                        return ;
                    }

                    //销售绑定用户
                    ClientController.getInstance().addFuser(
                            ClientBindActivity.this,
                            mShopID,
                            fuid,
                            mUserID,
                            mToken,
                            new ExtNetRequestListener(ClientBindActivity.this) {
                                @Override
                                public void onNetworkRequestError(int errorCode, String errorMessage) {
                                    super.onNetworkRequestError(errorCode, errorMessage);
                                }

                                @Override
                                public void onNetworkRequestCancelled() {
                                    super.onNetworkRequestCancelled();
                                }

                                @Override
                                public void onNetworkResponseSucceed(NetResponse result) {
                                    super.onNetworkResponseSucceed(result);
                                    String jsonResult = result.rawResult;

                                    try {
                                        if(!TextUtils.isEmpty(jsonResult)){
                                            JSONObject objResult = new JSONObject(jsonResult) ;
                                            boolean isSet = objResult.getBoolean("set");

                                            if(isSet){
                                                Intent intent = new Intent(ClientBindActivity.this, ClientActivity.class);
                                                startActivity(intent);
                                                ClientBindActivity.this.finish();
                                            } else {
                                                DialogUtil.getInstance().showCustomToast(ClientBindActivity.this,
                                                                                     "添加失败", Gravity.CENTER);
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void beforeNetworkRequestStart() {
                                    super.beforeNetworkRequestStart();
                                }
                            }
                    );
                }
            }
        });
    }

    /**
     * 显示会员信息在界面上
     * @param client
     */
    private void showClient(ClientBindBean client) {

        String userID = client.getUserid();
        if(!TextUtils.isEmpty(userID)) {
            String imageUrl =  ProtocolUtil.getAvatarUrl(userID);
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.mipmap.img_hotel_zhanwei)
                    .showImageForEmptyUri(R.mipmap.img_hotel_zhanwei)
                    .showImageOnFail(R.mipmap.img_hotel_zhanwei)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();
            ImageLoader.getInstance().displayImage(imageUrl, mCivMemberAvatar, options);
        }
        String salerID = client.getSalesid();
        if(!TextUtils.isEmpty(salerID)){
            String salerName = client.getSalesname();
            if (!TextUtils.isEmpty(salerName)) {
                mTvSalerName.setText(salerName);
            } else {
                mTvSalerName.setText(salerID);
            }
        } else {
            mTvSalerName.setText(getString(R.string.not_choose_yet));
        }

        String username = client.getUsername();
        if(!TextUtils.isEmpty(username)){
            mTvMemberName.setText(username);
        }

        String phone = client.getPhone();
        if(!TextUtils.isEmpty(phone)){
            mTvMemberPhone.setText(phone);
        }

    }

}
