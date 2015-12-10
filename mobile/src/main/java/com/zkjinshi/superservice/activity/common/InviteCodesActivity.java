package com.zkjinshi.superservice.activity.common;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.bean.Head;
import com.zkjinshi.superservice.bean.InviteCode;
import com.zkjinshi.superservice.bean.SempCodeReturnBean;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * 开发者：WinkyQin
 * 日期：2015/11/4
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class InviteCodesActivity extends Activity {

    private final static String TAG = InviteCodesActivity.class.getSimpleName();

    private Context      mContext;
    private ImageView    mIvAvatar;
    private RelativeLayout mRlBack;
    private TextView     mTvInviteCode;
    private ImageButton  mIbtnShare;
    private ImageButton  mIbtnCopy;
    private TextView     mTvEmpName;
    private RecyclerView mRcvInviteCodes;

    private String mShopID;
    private String mSalerID;
    private String mToken;
    private String mEmpName;

    private DisplayImageOptions mOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_codes);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mIvAvatar       = (ImageView)      findViewById(R.id.iv_avatar);
        mRlBack         = (RelativeLayout) findViewById(R.id.rl_back);
        mTvInviteCode   = (TextView)       findViewById(R.id.tv_invite_code);
        mIbtnShare      = (ImageButton)    findViewById(R.id.ibtn_share_invite_code);
        mIbtnCopy       = (ImageButton)    findViewById(R.id.ibtn_copy_invite_code);
        mTvEmpName      = (TextView)       findViewById(R.id.tv_emp_name);
        mRcvInviteCodes = (RecyclerView)   findViewById(R.id.rcv_invite_codes);
    }

    private void initData() {

        mContext = InviteCodesActivity.this;
        mShopID  = CacheUtil.getInstance().getShopID();
        mSalerID = CacheUtil.getInstance().getUserId();
        mToken   = CacheUtil.getInstance().getToken();

        mEmpName = CacheUtil.getInstance().getUserName();

        mOptions = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.img_morentu)
            .showImageForEmptyUri(R.drawable.img_morentu)
            .showImageOnFail(R.drawable.img_morentu)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .build();

        String avatarUrl = ProtocolUtil.getAvatarUrl(mSalerID);
        ImageLoader.getInstance().displayImage(avatarUrl, mIvAvatar, mOptions);

        if(!TextUtils.isEmpty(mEmpName)){
            mTvEmpName.setText(mEmpName);
        }

        String userID = CacheUtil.getInstance().getUserId();
        String token  = CacheUtil.getInstance().getToken();
        getEmpInviteCode(userID, token);
    }

    private void initListener() {

        mRlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InviteCodesActivity.this.finish();
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
        NetRequestTask netRequestTask = new NetRequestTask(mContext, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(mContext) {
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
                final Head             head  = sempCodeReturnBean.getHead();
                final List<InviteCode> codes = sempCodeReturnBean.getCode_data();
                if (head.isSet() && ( null != codes && !codes.isEmpty())) {
                    //TODO:查询用户邀请码列表成功 并且邀请码不为空, 显示邀请码列表
                    String inviteCode = codes.get(0).getSalecode();
                    mTvInviteCode.setText(inviteCode);
                } else {
                    //TODO: 邀请码不存在，为当前服务员生成邀请码
                    newInviteCodeForSaler(mShopID, mSalerID, mToken);

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
            DialogUtil.getInstance().showCustomToast(InviteCodesActivity.this,
                                         "当前shopID不能为空", Gravity.CENTER);
            return ;
        }
        bizMap.put("shopid", shopID);
        bizMap.put("salesid", salesID);
        bizMap.put("token", token);

        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(mContext, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(mContext) {
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
                try {
                    JSONObject resultObj = new JSONObject(jsonResult);
                    Boolean isSuccess = resultObj.getBoolean("set");
                    if (isSuccess) {

                        String inviteCode = resultObj.getString("code");
                        if (!TextUtils.isEmpty(inviteCode)) {
                            mTvInviteCode.setText(inviteCode);
                        }
                    } else {

                        int errCode = resultObj.getInt("err");
                        if (errCode == 400) {
                            DialogUtil.getInstance().showCustomToast(InviteCodesActivity.this,
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
