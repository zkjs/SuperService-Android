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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.chat.single.ChatActivity;
import com.zkjinshi.superservice.bean.ClientBindBean;
import com.zkjinshi.superservice.emchat.EMConversationHelper;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.view.CircleImageView;
import com.zkjinshi.superservice.vo.SexType;

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
    private LinearLayout   mLlPhoneCall;
    private TextView    mTvTitle;
    private TextView    mTvEmail;
    private TextView    mTvUserLevel;
    private TextView    mTvPoints;
    private TextView    mTvMemberPhone;
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
        mCivMemberAvatar = (CircleImageView) findViewById(R.id.civ_member_avatar);
        mTvMemberPhone   = (TextView)        findViewById(R.id.tv_member_phone);
        mTvEmail         = (TextView)        findViewById(R.id.tv_email);
        mTvUserLevel     = (TextView)        findViewById(R.id.tv_user_level);
        mTvPoints        = (TextView)        findViewById(R.id.tv_accumulate_points);
        mLlPhoneCall     = (LinearLayout)    findViewById(R.id.ll_phone_call);
        mTvMemberPhone   = (TextView)        findViewById(R.id.tv_member_phone);
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

        mLlPhoneCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mClientBean.getPhone())) {
                    //打电话
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mClientBean.getPhone()));
                    ClientBindActivity.this.startActivity(intent);
                }
            }
        });

//        mIbtnDuihua.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ClientBindActivity.this, ChatActivity.class);
//                String clientID = mClientBean.getUserid();
//                if(!TextUtils.isEmpty(clientID)){
//                    intent.putExtra(Constants.EXTRA_USER_ID, clientID);
//                }
//
//                if (!TextUtils.isEmpty(mShopID)) {
//                    intent.putExtra(Constants.EXTRA_SHOP_ID, mShopID);
//                }
//
//                intent.putExtra(Constants.EXTRA_SHOP_NAME, CacheUtil.getInstance().getShopFullName());
//
//                String clientName = mClientBean.getUsername();
//                if (!TextUtils.isEmpty(clientName)) {
//                    intent.putExtra(Constants.EXTRA_TO_NAME, clientName);
//                }
//
//                intent.putExtra(Constants.EXTRA_FROM_NAME, CacheUtil.getInstance().getUserName());
//                startActivity(intent);
//            }
//        });

        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != mClientBean){

                    //带绑定客户ID
                    final String fuid = mClientBean.getUserid();
                    final String clientName = mClientBean.getUsername();

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
                                            EMConversationHelper.getInstance().sendClientBindedTextMsg(
                                                fuid,
                                                clientName,
                                                CacheUtil.getInstance().getUserName(),
                                                CacheUtil.getInstance().getShopID(),
                                                CacheUtil.getInstance().getShopFullName(),
                                                true,
                                                new EMCallBack() {
                                                    @Override
                                                    public void onSuccess() {
                                                        LogUtil.getInstance().info(LogLevel.INFO, TAG + "文本信息发送成功");
                                                    }

                                                    @Override
                                                    public void onError(int i, String s) {
                                                        LogUtil.getInstance().info(LogLevel.INFO, TAG + "文本信息发送错误");
                                                    }

                                                    @Override
                                                    public void onProgress(int i, String s) {
                                                    }
                                                });

                                            //客人添加成功，发送透传消息
                                            EMConversationHelper.getInstance().sendClientBindedNotification(
                                                fuid,
                                                new EMCallBack() {
                                                    @Override
                                                    public void onSuccess() {
                                                        LogUtil.getInstance().info(LogLevel.INFO, TAG + "信息发送成功");
                                                    }

                                                    @Override
                                                    public void onError(int i, String s) {
                                                        LogUtil.getInstance().info(LogLevel.INFO, TAG + "信息发送失败");
                                                    }

                                                    @Override
                                                    public void onProgress(int i, String s) {

                                                    }
                                                }
                                            );

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
                    .showImageOnLoading(R.mipmap.ic_launcher)
                    .showImageForEmptyUri(R.mipmap.ic_launcher)
                    .showImageOnFail(R.mipmap.ic_launcher)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();
            ImageLoader.getInstance().displayImage(imageUrl, mCivMemberAvatar, options);
        }

        String username = client.getUsername();
        if(!TextUtils.isEmpty(username)){
            mTvTitle.setText(username);
        } else {
            mTvTitle.setText(userID);
        }

        String phone = client.getPhone();
        if(!TextUtils.isEmpty(phone)){
            mTvMemberPhone.setText(phone);
        }
        mTvEmail.setText(getString(R.string.current_none));
        mTvUserLevel.setText(getString(R.string.current_none));
        mTvPoints.setText(getString(R.string.current_none));
    }

}
