package com.zkjinshi.superservice.activity.set;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.chat.single.ChatActivity;
import com.zkjinshi.superservice.bean.ClientDetailBean;
import com.zkjinshi.superservice.bean.ClientInfoBean;
import com.zkjinshi.superservice.bean.ClientTag;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.NetRequestListener;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.view.CircleImageView;

import java.util.List;

import me.kaede.tagview.OnTagClickListener;
import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;

/**
 * 客人详细信息
 * 开发者：vincent
 * 日期：2015/10/5
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientDetailActivity extends Activity {

    private final static String TAG = ClientDetailActivity.class.getSimpleName();

    private String      mClientID;
    private String      mClientName;
    private String      mClientPhone;
    private String      mUserID;
    private String      mToken;
    private String      mShopID;
    private String      shopName;

    private ScrollView     mSvClientDetail;
    private RelativeLayout mRlBack;
    private TextView    mTvTitle;
    private ImageButton mIbtnDianhua;
    private ImageButton mIbtnDuiHua;
    private TextView    mTvMemberName;
    private TextView    mTvMemberPhone;
//    private TextView    mTvMemberLevel;
//    private TextView    mTvMemberType;
//    private TextView    mTvRecordTimes;
//    private TagView     mTvTagPreference;
//    private TagView     mTvTagPrivilege;
//    private TagView     mTvTagClient;
//    private EditText    mEtRemark;
//    private TextView    mTvExclusiceServer;

    private CircleImageView  mCivMemberAvatar;
    private ClientInfoBean   mClientInfo;

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

        mCivMemberAvatar = (CircleImageView) findViewById(R.id.civ_member_avatar);
        mTvMemberName    = (TextView)        findViewById(R.id.tv_member_name);
        mTvMemberPhone   = (TextView)        findViewById(R.id.tv_member_phone);
        mIbtnDianhua     = (ImageButton)     findViewById(R.id.ibtn_dianhua);
        mIbtnDuiHua      = (ImageButton)     findViewById(R.id.ibtn_duihua);
//        mTvMemberLevel   = (TextView)        findViewById(R.id.tv_member_level);
//        mTvMemberType    = (TextView)        findViewById(R.id.tv_member_type);
//        mTvRecordTimes   = (TextView)        findViewById(R.id.tv_record_times);
//        mTvTagPreference = (TagView)         findViewById(R.id.tv_preference_tag);
//        mTvTagPrivilege  = (TagView)         findViewById(R.id.tv_privilege_tag);
//        mTvTagClient     = (TagView)         findViewById(R.id.tv_client_tag);
//        mEtRemark        = (EditText)        findViewById(R.id.et_remark);
//        mTvExclusiceServer = (TextView)      findViewById(R.id.tv_exclusive_server);

        //将scrollView滚动置顶
        mSvClientDetail.smoothScrollTo(0, 0);
        mSvClientDetail.setSmoothScrollingEnabled(true);
    }

    private void initData() {
        mSvClientDetail.fullScroll(ScrollView.FOCUS_UP);
        mClientID = getIntent().getStringExtra("user_id");

        mUserID = CacheUtil.getInstance().getUserId();
        mToken  = CacheUtil.getInstance().getToken();
        mShopID = CacheUtil.getInstance().getShopID();
        shopName = CacheUtil.getInstance().getShopFullName();

        //非空判断
        if(!TextUtils.isEmpty(mClientID)){
            getClientInfo(mUserID, mToken, mClientID);
        }

    }

    private void getClientInfo(String userID, String token, String clientID) {
        ClientController.getInstance().getClientInfo(
            ClientDetailActivity.this,
            userID,
            token,
            clientID,
            new ExtNetRequestListener(this) {
                @Override
                public void onNetworkRequestError(int errorCode, String errorMessage) {
                    Log.i(TAG, "errorCode:" + errorCode);
                    Log.i(TAG, "errorMessage:" + errorMessage);

                    //网络请求异常
                    DialogUtil.getInstance().cancelProgressDialog();
                    ClientDetailActivity.this.finish();
                    DialogUtil.getInstance().showToast(ClientDetailActivity.this, "网络异常");
                }

                @Override
                public void onNetworkRequestCancelled() {
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                @Override
                public void onNetworkResponseSucceed(NetResponse result) {
                    super.onNetworkResponseSucceed(result);

                    Log.i(TAG, "result.rawResult:" + result.rawResult);
                    DialogUtil.getInstance().cancelProgressDialog();
                    String jsonResult = result.rawResult;
                    if (jsonResult.contains("false") || jsonResult.trim().contains("err")) {
                        DialogUtil.getInstance().showToast(ClientDetailActivity.this, "验证不通过，请退出后重新登录。");
                    } else {
                        Gson gson = new Gson();
                        mClientInfo = gson.fromJson(jsonResult, ClientInfoBean.class);
                        showClient(mClientInfo);
                    }
                }

                @Override
                public void beforeNetworkRequestStart() {
                    //网络请求前
                }
            });
    }

    /**
     * 显示会员信息在界面上
     * @param clientInfo
     */
    private void showClient(ClientInfoBean clientInfo) {
        mClientID    = clientInfo.getUserid();
        mClientName  = clientInfo.getUsername();
        mClientPhone = clientInfo.getPhone();
        if(!TextUtils.isEmpty(mClientID)) {
            String imageUrl = ProtocolUtil.getAvatarUrl(mClientID);
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
            ImageLoader.getInstance().displayImage(imageUrl, mCivMemberAvatar, options);
        }
        mTvMemberName.setText(mClientName);
        mTvMemberPhone.setText(mClientPhone);

//        mTvMemberLevel.setText("VIP" + clientInfo.getUser_level());
//        if(clientInfo.getIs_bill() == 1) {
//            mTvMemberType.setText(getString(R.string.debt_member));
//        } else {
//            mTvMemberType.setText(getString(R.string.not_debt_member));
//        }
//        mTvRecordTimes.setText(clientInfo.getOrder_count() + "");
//        String salesName = clientInfo.getSalesname();
//        mTvExclusiceServer.setText(TextUtils.isEmpty(salesName) ? salesName : getString(R.string.not_choose_yet));
//
//        //1.客户偏好标签处理
//        String   likeDesc   = clientInfo.getLike_desc();
//        if(!TextUtils.isEmpty(likeDesc)){
//            String[] clientLike = likeDesc.split(",");
//
//            //清空编号标签
//            List<Tag> lieDescList = mTvTagPreference.getTags();
//            if(null != lieDescList && !lieDescList.isEmpty()){
//                mTvTagPreference.removeAllTags();
//            }
//
//            if(clientLike.length > 0){
//                for(int i=0; i<clientLike.length; i++){
//                    String preference = clientLike[i];
//                    if(TextUtils.isEmpty(preference)){
//                        continue;
//                    }else {
//                        mTvTagPreference.addTag(createTag(preference, null));
//                    }
//                }
//            }
//        }
//
//        //TODO: 2.特权标签处理
//
//        //3. 客户信息标签处理
//        List<Tag> tagList = mTvTagClient.getTags();
//        if(null != tagList && !tagList.isEmpty()){
//            mTvTagClient.removeAllTags();
//        }
//
//        final List<ClientTag> tags = client.getTags();
//        if(null != tags && !tags.isEmpty()){
//            for(ClientTag clientTag : tags){
//                mTvTagClient.addTag(createTag(clientTag.tagid, clientTag.tag, null));
//            }
//        }
//        mTvTagClient.addTag(createTag("   +   "));

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
                String phoneNumber = mClientInfo.getPhone();
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
                intent.putExtra(Constants.EXTRA_USER_ID, mClientID);
                if (!TextUtils.isEmpty(mShopID)) {
                    intent.putExtra(Constants.EXTRA_SHOP_ID,mShopID);
                }
                if (!TextUtils.isEmpty(shopName)) {
                    intent.putExtra(Constants.EXTRA_SHOP_NAME,shopName);
                }
                if(!TextUtils.isEmpty(mClientName)){
                    intent.putExtra(Constants.EXTRA_TO_NAME, mClientName);
                }
                intent.putExtra(Constants.EXTRA_FROM_NAME, CacheUtil.getInstance().getUserName());
                startActivity(intent);
            }
        });

//        mTvTagClient.setOnTagClickListener(new OnTagClickListener() {
//            @Override
//            public void onTagClick(Tag tag, int position) {
//                //TODO: 判断此服务员是否有权限添加标签
//                if (mClientInfo != null) {
//                    if (!TextUtils.isEmpty(tag.text) && tag.text.trim().equals("+")) {
//                        //进入添加标签界面
//                        Intent addNewTag = new Intent(ClientDetailActivity.this, TagAddActivity.class);
//                        addNewTag.putExtra("phone_number", mClientPhone);
//                        ClientDetailActivity.this.startActivity(addNewTag);
//                    }
//                }
//            }
//        });
    }

//    /**
//     * 创建新Tag
//     * @param tagstr
//     * @return
//     */
//    private Tag createTag(String tagstr){
//        return createTag(tagstr, null);
//    }
//
//    private Tag createTag(String tagstr, Drawable background){
//        return createTag(0, tagstr, background);
//    }
//
//    private Tag createTag(int id, String tagstr, Drawable background){
//        Tag tag = new Tag(id, tagstr);
//        tag.tagTextColor         = Color.BLACK;
//        tag.layoutColor          = Color.WHITE;
//        tag.layoutColorPress     = Color.parseColor("#DDDDDD");
//        tag.background           = background;
//        tag.radius               = 40f;
//        tag.tagTextSize          = 18f;
//        tag.layoutBorderSize     = 1f;
//        tag.layoutBorderColor    = Color.parseColor("#bbbbbb");
//        tag.deleteIndicatorColor = Color.parseColor("#ff0000");
//        tag.deleteIndicatorSize  = 18f;
//        tag.isDeletable          = false;
//        return tag;
//    }
}
