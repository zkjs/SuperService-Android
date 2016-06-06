package com.zkjinshi.superservice.pad.activity.set;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.superservice.pad.utils.CacheUtil;
import com.zkjinshi.superservice.pad.utils.Constants;
import com.zkjinshi.superservice.pad.R;
import com.zkjinshi.superservice.pad.activity.chat.single.ChatActivity;
import com.zkjinshi.superservice.pad.base.BaseActivity;
import com.zkjinshi.superservice.pad.utils.ProtocolUtil;
import com.zkjinshi.superservice.pad.vo.EmployeeVo;

/**
 * 开发者：vincent
 * 日期：2015/10/26
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class EmployeeInfoActivity extends BaseActivity {

    private String          mShopID;
    private String          mUserID;

    private EmployeeVo      mEmployee;
    private DisplayImageOptions mOptions;

    private ImageView       mIvAvatar;
    private RelativeLayout  mRlBack;
    private TextView        mTvPhoneNumber;
    private TextView        mTvEmpName;
    private TextView        mTvShopName;
    private TextView        mTvLatestOnline;
    private RelativeLayout  mRlDuiHua;
    private RelativeLayout  mRlDianHua;
    private Button          mBtnChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_info);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mIvAvatar       = (ImageView)      findViewById(R.id.iv_avatar);
        mRlBack         = (RelativeLayout) findViewById(R.id.rl_back);
        mTvPhoneNumber  = (TextView)       findViewById(R.id.tv_phone_number);
        mTvEmpName      = (TextView)       findViewById(R.id.tv_emp_name);
        mTvShopName     = (TextView)       findViewById(R.id.tv_shop_name);
        mTvLatestOnline = (TextView)       findViewById(R.id.tv_latest_online);

        mRlDuiHua       = (RelativeLayout) findViewById(R.id.id_rl_duihua);
        mRlDianHua      = (RelativeLayout) findViewById(R.id.id_rl_dianhua);
        mBtnChat        = (Button)         findViewById(R.id.btn_chat);
    }

    private void initData() {

        mShopID = CacheUtil.getInstance().getShopID();
        mUserID = CacheUtil.getInstance().getUserId();

        mEmployee      = (EmployeeVo) getIntent().getSerializableExtra("shop_employee");
        this.mOptions  = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.img_morentu)
                .showImageForEmptyUri(R.drawable.img_morentu)
                .showImageOnFail(R.drawable.img_morentu)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        if(null != mEmployee){
            String avatarUrl = ProtocolUtil.getHostImgUrl(mEmployee.getUserimage());
            mIvAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ImageLoader.getInstance().displayImage(avatarUrl, mIvAvatar, mOptions);

            String phoneNumber = mEmployee.getPhone();
            if(!TextUtils.isEmpty(phoneNumber)){
                mTvPhoneNumber.setText(phoneNumber);
            }

            String empName = mEmployee.getUsername();
            if(!TextUtils.isEmpty(empName)){
                mTvEmpName.setText(empName);
            }

            String shopName = CacheUtil.getInstance().getShopFullName();
            if(!TextUtils.isEmpty(shopName)){
                mTvShopName.setText(shopName);
            }

//            long latestOnlineTime = ShopEmployeeDBUtil.getInstance().queryLatestOnlineByEmpID(empID);
//            if(latestOnlineTime > 0){
//                String onlineTime = TimeUtil.getChatTime(latestOnlineTime * 10);
//                mTvLatestOnline.setText(getString(R.string.latest) + onlineTime + getString(R.string.online));
//            } else {
//                mTvLatestOnline.setText(getString(R.string.offline));
//            }
        }
    }

    private void initListener() {
        mRlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmployeeInfoActivity.this.finish();
            }
        });

        mRlDuiHua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId    = mEmployee.getUserid();
                String toName  = mEmployee.getUsername();
                String shopName = CacheUtil.getInstance().getShopFullName();
                Intent intent = new Intent(EmployeeInfoActivity.this, ChatActivity.class);
                intent.putExtra(Constants.EXTRA_USER_ID, userId);
                if (!TextUtils.isEmpty(mShopID)) {
                    intent.putExtra(Constants.EXTRA_SHOP_ID,mShopID);
                }
                intent.putExtra(Constants.EXTRA_SHOP_NAME,shopName);
                if(!TextUtils.isEmpty(toName)){
                    intent.putExtra(Constants.EXTRA_TO_NAME, toName);
                }
                intent.putExtra(Constants.EXTRA_FROM_NAME, CacheUtil.getInstance().getUserName());
                startActivity(intent);
            }
        });

        mRlDianHua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = mTvPhoneNumber.getText().toString().trim();
                if(!TextUtils.isEmpty(phoneNumber)){
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
                    EmployeeInfoActivity.this.startActivity(intent);
                }
            }
        });

        mBtnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId    = mEmployee.getUserid();
                String toName  = mEmployee.getUsername();
                String shopName = CacheUtil.getInstance().getShopFullName();
                Intent intent = new Intent(EmployeeInfoActivity.this, ChatActivity.class);
                intent.putExtra(Constants.EXTRA_USER_ID, userId);
                if (!TextUtils.isEmpty(mShopID)) {
                    intent.putExtra(Constants.EXTRA_SHOP_ID,mShopID);
                }
                intent.putExtra(Constants.EXTRA_SHOP_NAME,shopName);
                if(!TextUtils.isEmpty(toName)){
                    intent.putExtra(Constants.EXTRA_TO_NAME, toName);
                }
                intent.putExtra(Constants.EXTRA_FROM_NAME, CacheUtil.getInstance().getUserName());
                startActivity(intent);
            }
        });
    }
}
