package com.zkjinshi.superservice.activity.common;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.InviteCodeFragmentAdapter;
import com.zkjinshi.superservice.fragment.UnusedInviteCodeFragment;
import com.zkjinshi.superservice.fragment.UsedInviteCodeFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 邀请码管理界面
 * 开发者：WinkyQin
 * 日期：2015/11/4
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class InviteCodesActivity extends AppCompatActivity {

    private final static String TAG = InviteCodesActivity.class.getSimpleName();

    private Toolbar     mToolbar;
    private TextView    mTvCenterTitle;

    private ViewPager   mVpInviteCode;
    private InviteCodeFragmentAdapter mPagerAdapter;
    private FragmentManager      mFragmentManager;
    private List<Fragment>       mFragmentLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_codes);

        initView();
        initData();
        initListener();
    }

    private void initView() {

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(R.drawable.header_ibtn_back);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTvCenterTitle = (TextView) findViewById(R.id.tv_center_title);
        mTvCenterTitle.setText(getString(R.string.invite_code));

        mVpInviteCode = (ViewPager) findViewById(R.id.vp_invite_codes);
    }

    private void initData() {

        mFragmentLists = new ArrayList<>();
        mFragmentLists.add(new UnusedInviteCodeFragment());
        mFragmentLists.add(new UsedInviteCodeFragment());

        mFragmentManager = getSupportFragmentManager();
        mPagerAdapter = new InviteCodeFragmentAdapter(mFragmentManager, mFragmentLists);
        mVpInviteCode.setAdapter(mPagerAdapter);
        mVpInviteCode.setCurrentItem(0);
    }

    private void initListener() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InviteCodesActivity.this.finish();
            }
        });

        mVpInviteCode.addOnPageChangeListener(
                new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int i, float v, int i1) {
                    }

                    @Override
                    public void onPageSelected(int i) {
                    }

                    @Override
                    public void onPageScrollStateChanged(int i) {
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_invite_codes, menu);
        return true;
    }

//    /**
//     * 获取服务员邀请码
//     *
//     * @param saiesID
//     * @param token
//     */
//    private void getEmpInviteCode(String saiesID, String token) {
//
//        NetRequest netRequest = new NetRequest(ProtocolUtil.getEmpInviteCodeUrl());
//        HashMap<String, String> bizMap = new HashMap<>();
//            bizMap.put("salesid", saiesID);
//        bizMap.put("token", token);
//
////        bizMap.put("page", saiesID);
////        bizMap.put("pagedata", token);
////        page 默认第一页
////        pagedata 默认10条
//
//        netRequest.setBizParamMap(bizMap);
//        NetRequestTask netRequestTask = new NetRequestTask(mContext, netRequest, NetResponse.class);
//        netRequestTask.methodType = MethodType.PUSH;
//        netRequestTask.setNetRequestListener(new ExtNetRequestListener(mContext) {
//            @Override
//            public void onNetworkRequestError(int errorCode, String errorMessage) {
//                Log.i(TAG, "errorCode:" + errorCode);
//                Log.i(TAG, "errorMessage:" + errorMessage);
//            }
//
//            @Override
//            public void onNetworkRequestCancelled() {
//            }
//
//            @Override
//            public void onNetworkResponseSucceed(NetResponse result) {
//                super.onNetworkResponseSucceed(result);
//
//                Log.i(TAG, "result.rawResult:" + result.rawResult);
//                String jsonResult = result.rawResult;
//                Gson gson = new Gson();
//                SempCodeReturnBean sempCodeReturnBean = gson.fromJson(jsonResult,
//                                                        SempCodeReturnBean.class);
//                final Head             head  = sempCodeReturnBean.getHead();
//                final List<InviteCode> codes = sempCodeReturnBean.getCode_data();
//                if (head.isSet() && ( null != codes && !codes.isEmpty())) {
//                    //TODO:查询用户邀请码列表成功 并且邀请码不为空, 显示邀请码列表
//                    String inviteCode = codes.get(0).getSalecode();
//                } else {
//                    //TODO: 邀请码不存在，为当前服务员生成邀请码
//                    newInviteCodeForSaler(mShopID, mSalerID, mToken);
//
//                }
//            }
//
//            @Override
//            public void beforeNetworkRequestStart() {
//                //网络请求前
//            }
//        });
//        netRequestTask.isShowLoadingDialog = true;
//        netRequestTask.execute();
//    }
//
//    /**
//     * 为当前销售服务员生成邀请码
//     */
//    private void newInviteCodeForSaler(String shopID, String salesID, String token) {
//
//        NetRequest netRequest = new NetRequest(ProtocolUtil.getNewRandomInviteCodeUrl());
//        HashMap<String, String> bizMap = new HashMap<>();
//
//        if(TextUtils.isEmpty(shopID)){
//            DialogUtil.getInstance().showCustomToast(InviteCodesActivity.this,
//                                         "当前shopID不能为空", Gravity.CENTER);
//            return ;
//        }
//        bizMap.put("shopid", shopID);
//        bizMap.put("salesid", salesID);
//        bizMap.put("token", token);
//
//        netRequest.setBizParamMap(bizMap);
//        NetRequestTask netRequestTask = new NetRequestTask(mContext, netRequest, NetResponse.class);
//        netRequestTask.methodType = MethodType.PUSH;
//        netRequestTask.setNetRequestListener(new ExtNetRequestListener(mContext) {
//            @Override
//            public void onNetworkRequestError(int errorCode, String errorMessage) {
//                Log.i(TAG, "errorCode:" + errorCode);
//                Log.i(TAG, "errorMessage:" + errorMessage);
//            }
//
//            @Override
//            public void onNetworkRequestCancelled() {
//            }
//
//            @Override
//            public void onNetworkResponseSucceed(NetResponse result) {
//                super.onNetworkResponseSucceed(result);
//                Log.i(TAG, "result.rawResult:" + result.rawResult);
//                String jsonResult = result.rawResult;
//                try {
//                    JSONObject resultObj = new JSONObject(jsonResult);
//                    Boolean isSuccess = resultObj.getBoolean("set");
//                    if (isSuccess) {
//
//                        String inviteCode = resultObj.getString("code");
//                        if (!TextUtils.isEmpty(inviteCode)) {
//                        }
//                    } else {
//
//                        int errCode = resultObj.getInt("err");
//                        if (errCode == 400) {
//                            DialogUtil.getInstance().showCustomToast(InviteCodesActivity.this,
//                                    "身份信息已经过期，请重新登录", Gravity.CENTER);
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void beforeNetworkRequestStart() {
//                //网络请求前
//            }
//        });
//        netRequestTask.isShowLoadingDialog = true;
//        netRequestTask.execute();
//    }

}
