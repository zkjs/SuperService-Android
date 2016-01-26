package com.zkjinshi.superservice.activity.set;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.superservice.R;
//import com.zkjinshi.superservice.factory.UnRegClientFactory;
import com.zkjinshi.superservice.bean.ClientBaseBean;
import com.zkjinshi.superservice.bean.ClientBindBean;
import com.zkjinshi.superservice.factory.ClientFactory;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.sqlite.ClientDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.view.CircleImageView;
import com.zkjinshi.superservice.vo.ClientVo;
import com.zkjinshi.superservice.vo.ContactType;

import org.json.JSONException;
import org.json.JSONObject;

import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;

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
    private ImageButton mIbtnDianhua;
    private TextView    mTvMemberName;
    private TextView    mTvMemberPhone;
    private Button      mBtnConfirm;

    private CircleImageView  mCivMemberAvatar;
    private ClientBindBean   mClientBean;

    private String  mUserID;
    private String  mToken;

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
        mCivMemberAvatar = (CircleImageView) findViewById(R.id.civ_member_avatar);
        mTvMemberName    = (TextView)        findViewById(R.id.tv_member_name);
        mTvMemberPhone   = (TextView)        findViewById(R.id.tv_member_phone);
        mBtnConfirm      = (Button)         findViewById(R.id.btn_confirm);
    }

    private void initData() {
        mUserID = CacheUtil.getInstance().getUserId();
        mToken  = CacheUtil.getInstance().getToken();

        mClientBean = (ClientBindBean) getIntent().getSerializableExtra("client_bean");
        showClient(mClientBean);
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
        mTvMemberName.setText(client.getUsername());
        mTvMemberPhone.setText(client.getPhone());
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

}
