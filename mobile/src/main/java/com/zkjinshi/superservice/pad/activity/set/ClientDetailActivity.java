package com.zkjinshi.superservice.pad.activity.set;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.pad.base.BaseActivity;
import com.zkjinshi.superservice.pad.utils.CacheUtil;
import com.zkjinshi.superservice.pad.utils.Constants;
import com.zkjinshi.superservice.pad.utils.ProtocolUtil;
import com.zkjinshi.superservice.pad.vo.ClientContactVo;
import com.zkjinshi.superservice.pad.R;
import com.zkjinshi.superservice.pad.activity.chat.single.ChatActivity;

/**
 * 客人详细信息
 * 开发者：vincent
 * 日期：2015/10/5
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientDetailActivity extends BaseActivity {

    private final static String TAG = ClientDetailActivity.class.getSimpleName();

    private ClientContactVo clientContactVo;

    private ScrollView     mSvClientDetail;
    private RelativeLayout mRlBack;
    private TextView    mTvTitle;
    private ImageButton mIbtnDianhua;
    private ImageButton mIbtnDuiHua;
    private TextView    mTvMemberName;
    private TextView    mTvMemberPhone;


    private SimpleDraweeView mCivMemberAvatar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_detail);

        initView();
        initData();
        initListener();
    }

    private void initView() {

        mSvClientDetail  = (ScrollView)      findViewById(R.id.sv_client_detail);
        mRlBack          = (RelativeLayout)  findViewById(R.id.rl_back);
        mTvTitle         = (TextView)        findViewById(R.id.tv_title);

        mTvTitle.setText(getString(R.string.client_detail));

        mCivMemberAvatar = (SimpleDraweeView) findViewById(R.id.civ_member_avatar);
        mTvMemberName    = (TextView)        findViewById(R.id.tv_member_name);
        mTvMemberPhone   = (TextView)        findViewById(R.id.tv_member_phone);
        mIbtnDianhua     = (ImageButton)     findViewById(R.id.ibtn_dianhua);
        mIbtnDuiHua      = (ImageButton)     findViewById(R.id.ibtn_duihua);


        //将scrollView滚动置顶
        mSvClientDetail.smoothScrollTo(0, 0);
        mSvClientDetail.setSmoothScrollingEnabled(true);
    }

    private void initData() {
        mSvClientDetail.fullScroll(ScrollView.FOCUS_UP);
        clientContactVo = (ClientContactVo)getIntent().getSerializableExtra("contact");


        //非空判断
        if(clientContactVo != null){
            showClient(clientContactVo);
        }else{
            DialogUtil.getInstance().showToast(this,"信息为空！");
            finish();
        }

    }


    /**
     * 显示会员信息在界面上
     * @param clientInfo
     */
    private void showClient(ClientContactVo clientInfo) {
        String avatarUrl = ProtocolUtil.getHostImgUrl(clientInfo.getUserimage());
        mCivMemberAvatar.setImageURI(Uri.parse(avatarUrl));
        mTvMemberName.setText(clientInfo.getUsername());
        mTvMemberPhone.setText(clientInfo.getPhone());
    }

    private void initListener() {
        mRlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientDetailActivity.this.finish();
            }
        });

        mIbtnDianhua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = clientContactVo.getPhone();
                if(!TextUtils.isEmpty(phoneNumber)){
                    //打电话
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
                    ClientDetailActivity.this.startActivity(intent);
                }
            }
        });

        mIbtnDuiHua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClientDetailActivity.this, ChatActivity.class);
                intent.putExtra(Constants.EXTRA_USER_ID, clientContactVo.getUserid());
//                if (!TextUtils.isEmpty(clientContactVo.get)) {
//                    intent.putExtra(Constants.EXTRA_SHOP_ID,mShopID);
//                }
//                if (!TextUtils.isEmpty(shopName)) {
//                    intent.putExtra(Constants.EXTRA_SHOP_NAME,shopName);
//                }
                if(!TextUtils.isEmpty(clientContactVo.getUsername())){
                    intent.putExtra(Constants.EXTRA_TO_NAME, clientContactVo.getUsername());
                }
                intent.putExtra(Constants.EXTRA_FROM_NAME, CacheUtil.getInstance().getUserName());
                startActivity(intent);
            }
        });

    }

}
