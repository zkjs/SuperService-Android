package com.zkjinshi.superservice.activity.set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.common.MainActivity;
import com.zkjinshi.superservice.activity.common.MoreActivity;
import com.zkjinshi.superservice.activity.common.TagAddActivity;
import com.zkjinshi.superservice.bean.ClientDetailBean;
import com.zkjinshi.superservice.bean.SempLoginBean;
import com.zkjinshi.superservice.factory.UserFactory;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestListener;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.net.RequestUtil;
import com.zkjinshi.superservice.sqlite.ClientDBUtil;
import com.zkjinshi.superservice.sqlite.DBOpenHelper;
import com.zkjinshi.superservice.sqlite.UserDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.view.CircleImageView;
import com.zkjinshi.superservice.vo.UserVo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private String      mPhoneNumber;

    private ImageButton mIbtnBack;
    private ImageButton mIbtnDianhua;
    private ImageButton mIbtnDuiHua;
    private TextView    mTvMemberName;
    private TextView    mTvMemberPhone;
    private TextView    mTvMemberLevel;
    private TextView    mTvMemberType;
    private TextView    mTvRecordTimes;
    private TagView     mTvTagPreference;
    private TagView     mTvTagPrivilege;
    private TagView     mTvTagClient;
    private EditText    mEtRemark;

    private CircleImageView mCivMemberAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_detail);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mIbtnBack        = (ImageButton)     findViewById(R.id.ibtn_back);
        mCivMemberAvatar = (CircleImageView) findViewById(R.id.civ_member_avatar);
        mTvMemberName    = (TextView)        findViewById(R.id.tv_member_name);
        mTvMemberPhone   = (TextView)        findViewById(R.id.tv_member_phone);
        mIbtnDianhua     = (ImageButton)     findViewById(R.id.ibtn_dianhua);
        mTvMemberLevel   = (TextView)        findViewById(R.id.tv_member_level);
        mIbtnDuiHua      = (ImageButton)     findViewById(R.id.ibtn_duihua);
        mTvMemberType    = (TextView)        findViewById(R.id.tv_member_type);
        mTvRecordTimes   = (TextView)        findViewById(R.id.tv_record_times);
        mTvTagPreference = (TagView)         findViewById(R.id.tv_preference_tag);
        mTvTagPrivilege  = (TagView)         findViewById(R.id.tv_privilege_tag);
        mTvTagClient     = (TagView)         findViewById(R.id.tv_client_tag);
        mEtRemark        = (EditText)        findViewById(R.id.et_remark);
    }

    private void initData() {
        mPhoneNumber  = getIntent().getStringExtra("phone_number");
        DialogUtil.getInstance().showProgressDialog(ClientDetailActivity.this);
        getClientDetail(mPhoneNumber);
    }

    private void getClientDetail(String mPhoneNumber) {
        String userID = CacheUtil.getInstance().getUserId();
        String token  = CacheUtil.getInstance().getToken();

        NetRequest netRequest = new NetRequest(ProtocolUtil.getClientDetailUrl());
        HashMap<String,String> bizMap = new HashMap<>();
        bizMap.put("empid", userID);
        bizMap.put("token", token);
        bizMap.put("phone", mPhoneNumber);
        bizMap.put("set", "9");
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(this, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.POST;
        netRequestTask.setNetRequestListener(new NetRequestListener() {
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
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                DialogUtil.getInstance().cancelProgressDialog();
                String jsonResult = result.rawResult;
                if (jsonResult.contains("false") || jsonResult.trim().contains("err")) {
                    DialogUtil.getInstance().showToast(ClientDetailActivity.this, "验证不通过，请退出后重新登录。");
                } else {
                    Gson gson = new Gson();
                    ClientDetailBean client = gson.fromJson(jsonResult, ClientDetailBean.class);
                    showClient(client);
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
     * 邀请对话框
     */
    private void showInviteDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage("此用户尚未成为会员？邀请当前用户加入?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //TODO: 发送短信邀请
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 显示会员信息在界面上
     * @param client
     */
    private void showClient(ClientDetailBean client) {
        String userID = client.getUserid();
        if(!TextUtils.isEmpty(userID)) {
            String imageUrl = Constants.GET_USER_AVATAR + userID + ".jpg";
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.img_hotel_zhanwei)
                .showImageForEmptyUri(R.drawable.img_hotel_zhanwei)
                .showImageOnFail(R.drawable.img_hotel_zhanwei)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
            ImageLoader.getInstance().displayImage(imageUrl, mCivMemberAvatar, options);
        }
        mTvMemberName.setText(client.getUsername());
        mTvMemberPhone.setText(client.getPhone());
        if(client.getIs_bill() == 1) {
            mTvMemberType.setText(getString(R.string.debt_member));
        } else {
            mTvMemberType.setText(getString(R.string.not_debt_member));
        }
        mTvRecordTimes.setText(client.getOrder_count() + "");

        //TODO: 1.客户偏好标签处理
        //TODO: 2.特权标签处理

        //3. 客户信息标签处理
        final List<ClientDetailBean.ClientTag> tags = client.getTags();
        if(null != tags && !tags.isEmpty()){
            for(ClientDetailBean.ClientTag clientTag : tags){
                mTvTagClient.addTag(createTag(clientTag.tagid, clientTag.tag, null));
            }
            mTvTagClient.addTag(createTag("     +     "));
        }
    }

    private void initListener() {
        mIbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientDetailActivity.this.finish();
            }
        });

        mIbtnDianhua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(mPhoneNumber)){
                    //打电话
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mPhoneNumber));
                    ClientDetailActivity.this.startActivity(intent);
                }
            }
        });

        mIbtnDuiHua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: 发起聊天
                DialogUtil.getInstance().showToast(ClientDetailActivity.this, "会话聊天");
            }
        });

        mTvTagClient.setOnTagClickListener(new OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag, int position) {
                if(!TextUtils.isEmpty(tag.text) && tag.text.trim().equals("+")) {
                    //进入添加标签界面
                    Intent addNewTag = new Intent(ClientDetailActivity.this, TagAddActivity.class);
                    addNewTag.putExtra("phone_number", mPhoneNumber);
                    ClientDetailActivity.this.startActivity(addNewTag);
                }
            }
        });
    }

    /**
     * 创建新Tag
     * @param tagstr
     * @return
     */
    private Tag createTag(String tagstr){
        return createTag(tagstr, null);
    }

    private Tag createTag(String tagstr, Drawable background){
        return createTag(0, tagstr, background);
    }

    private Tag createTag(int id, String tagstr, Drawable background){
        Tag tag = new Tag(id, tagstr);
        tag.tagTextColor         = Color.BLACK;
        tag.layoutColor          = Color.WHITE;
        tag.layoutColorPress     = Color.parseColor("#DDDDDD");
        tag.background           = background;
        tag.radius               = 40f;
        tag.tagTextSize          = 18f;
        tag.layoutBorderSize     = 1f;
        tag.layoutBorderColor    = Color.parseColor("#bbbbbb");
        tag.deleteIndicatorColor = Color.parseColor("#ff0000");
        tag.deleteIndicatorSize  = 18f;
        tag.isDeletable          = false;
        return tag;
    }
}
