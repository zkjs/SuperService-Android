package com.zkjinshi.superservice.activity.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.InviteCodeFragmentAdapter;
import com.zkjinshi.superservice.fragment.UnusedInviteCodeFragment;
import com.zkjinshi.superservice.fragment.UsedInviteCodeFragment;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.response.AddInviteCodeResponse;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.GoodInfoVo;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private ImageView   mIvTabLine;

    private TextView     mTvUnusedCount;
    private TextView     mTvUsedCount;
    private LinearLayout mLlUnusedCodes;
    private LinearLayout mLlUsedCodes;

    private ViewPager       mVpInviteCode;
    private FragmentManager mFragmentManager;
    private List<Fragment>  mFragmentLists;

    private InviteCodeFragmentAdapter mPagerAdapter;
    private InviteCodeReceiver        mReceiver;

    private int mCurrentIndex ;
    private int mScreenWidth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_codes);

        mReceiver = new InviteCodeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.zkjinshi.invite_code");
        registerReceiver(mReceiver, filter);

        initView();
        initData();
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != mReceiver){
            unregisterReceiver(mReceiver);
        }
    }

    private void initView() {

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(R.drawable.header_ibtn_back);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTvCenterTitle = (TextView) findViewById(R.id.tv_center_title);
        mTvCenterTitle.setText(getString(R.string.invite_code));

        mVpInviteCode  = (ViewPager) findViewById(R.id.vp_invite_codes);
        mIvTabLine     = (ImageView) findViewById(R.id.iv_tab_line);

        mLlUnusedCodes = (LinearLayout) findViewById(R.id.ll_unused_invite_code);
        mLlUsedCodes   = (LinearLayout) findViewById(R.id.ll_used_invite_code);
        mTvUnusedCount = (TextView)  findViewById(R.id.tv_unused_count);
        mTvUsedCount   = (TextView)  findViewById(R.id.tv_used_count);

        initTabLineWidth();
    }

    private void initData() {

        mFragmentLists      = new ArrayList<>();
        mFragmentLists.add(new UnusedInviteCodeFragment());
        mFragmentLists.add(new UsedInviteCodeFragment());

        mFragmentManager = getSupportFragmentManager();
        mPagerAdapter = new InviteCodeFragmentAdapter(mFragmentManager, mFragmentLists);
        mVpInviteCode.setAdapter(mPagerAdapter);
        mVpInviteCode.setCurrentItem(0);
    }

    private void initListener() {

        //未使用的邀请码
        mLlUnusedCodes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentIndex != 0){
                    mVpInviteCode.setCurrentItem(0);
                }
            }
        });

        //已使用邀请码
        mLlUsedCodes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentIndex != 1){
                    mVpInviteCode.setCurrentItem(1);
                }
            }
        });

        //返回
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InviteCodesActivity.this.finish();
            }
        });

        //页面滑动切换
        mVpInviteCode.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float offset, int i1) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mIvTabLine.getLayoutParams();
                if (mCurrentIndex == 0 && position == 0){
                    lp.leftMargin = (int) (offset * (mScreenWidth * 1.0 / 2)
                                           + mCurrentIndex * (mScreenWidth / 2));
                } else if (mCurrentIndex == 1 && position == 0){
                    lp.leftMargin = (int) ( - (1 - offset) * (mScreenWidth * 1.0 / 2)
                                            + mCurrentIndex * (mScreenWidth / 2));
                }
                mIvTabLine.setLayoutParams(lp);
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();
        if(itemID == R.id.item_add_invite_code){
            createNewInviteCode();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_invite_codes, menu);
        return true;
    }

    /**
     * 设置滑动条的宽度为屏幕的1/2(根据Tab的个数而定)
     */
    private void initTabLineWidth() {
        DisplayMetrics dpMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dpMetrics);
        mScreenWidth = dpMetrics.widthPixels;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mIvTabLine.getLayoutParams();
        lp.width = mScreenWidth / 2;
        mIvTabLine.setLayoutParams(lp);
    }

    /**
     * 更新未使用邀请码数量
     * @param count
     */
    public void udpateUnusedCodeCount(int count) {
        mTvUnusedCount.setText(count+"");
    }

    /**
     * 更新已经使用邀请码数量
     * @param count
     */
    public void updateUsedCodeCount(int count) {
        mTvUsedCount.setText(count+"");
    }

    /**
     * 创建邀请码
     */
    private void createNewInviteCode() {
        String requestUrl = ProtocolUtil.getSaleCodeUrl();
        NetRequest netRequest = new NetRequest(requestUrl);
        HashMap<String,String> bizMap = new HashMap<String, String>();
        bizMap.put("rmk", CacheUtil.getInstance().getUserName()+"生成邀请码");
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(this, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.JSON;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(this) {
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
                AddInviteCodeResponse addInviteCodeResponse = new Gson().fromJson(result.rawResult,AddInviteCodeResponse.class);
                if(null != addInviteCodeResponse){
                    int resultCode = addInviteCodeResponse.getRes();
                    if(0 == resultCode){
                        mFragmentLists.get(0).onResume();
                    }else {
                        String resultMsg = addInviteCodeResponse.getResDesc();
                        if(!TextUtils.isEmpty(resultMsg)){
                            DialogUtil.getInstance().showCustomToast(InviteCodesActivity.this,resultMsg, Gravity.CENTER);
                        }
                    }
                }
            }

            @Override
            public void beforeNetworkRequestStart() {
            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();

    }

    /**
     * 收到邀请码使用信息，刷新当前界面
     */
    public class InviteCodeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            for(int i=0; i<mFragmentLists.size(); i++){
                mFragmentLists.get(i).onResume();
            }
        }
    }

}
