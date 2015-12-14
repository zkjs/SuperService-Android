package com.zkjinshi.superservice.activity.common;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.InviteCodeFragmentAdapter;
import com.zkjinshi.superservice.fragment.UnusedInviteCodeFragment;
import com.zkjinshi.superservice.fragment.UsedInviteCodeFragment;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.NetResponse;

import org.json.JSONException;
import org.json.JSONObject;

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
    private ImageView   mIvTabLine;

    private TextView    mTvUnusedCount;
    private TextView    mTvUsedCount;
    private LinearLayout mLlUnusedCodes;
    private LinearLayout mLlUsedCodes;

    private ViewPager       mVpInviteCode;
    private FragmentManager mFragmentManager;
    private List<Fragment>  mFragmentLists;

    private InviteCodeFragmentAdapter mPagerAdapter;

    private int mCurrentIndex ;
    private int mScreenWidth ;

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
        mLlUnusedCodes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentIndex != 0){
                    mVpInviteCode.setCurrentItem(0);
                }
            }
        });

        mLlUsedCodes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentIndex != 1){
                    mVpInviteCode.setCurrentItem(1);
                }
            }
        });

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InviteCodesActivity.this.finish();
            }
        });

        mVpInviteCode.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            /**
             * position :当前页面，及你点击滑动的页面 offset:当前页面偏移的百分比
             * offsetPixels:当前页面偏移的像素位置
             */
            @Override
            public void onPageScrolled(int position, float offset, int i1) {

                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mIvTabLine.getLayoutParams();
                Log.e("offset:", offset + "");
                /**
                 * 利用currentIndex(当前所在页面)和position(下一个页面)以及offset来
                 * 设置mTabLineIv的左边距 滑动场景：
                 * 从左到右分别为0,1  0->1; 1->2; 2->1; 1->0
                 */
                // 0->1
                if (mCurrentIndex == 0 && position == 0){
                    lp.leftMargin = (int) (offset * (mScreenWidth * 1.0 / 2)
                                           + mCurrentIndex * (mScreenWidth / 2));
                // 1->0
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

            /**
             * state滑动中的状态
             * 有三种状态（0，1，2） 1：正在滑动 2：滑动完毕 0：什么都没做。
             */
            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();
        if(itemID == R.id.item_add_invite_code){
            // showAddInviteCodeDialog();
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

    private void createNewInviteCode() {
        //服务员新增邀请码使用
        InviteCodeController.getInstance().newInviteCode(
            InviteCodesActivity.this,
            new ExtNetRequestListener(InviteCodesActivity.this) {
                @Override
                public void onNetworkRequestError(int errorCode, String errorMessage) {
                    super.onNetworkRequestError(errorCode, errorMessage);

                    Log.i(TAG, "errorCode:" + errorCode);
                    Log.i(TAG, "errorMessage:" + errorMessage);
                }

                @Override
                public void onNetworkRequestCancelled() {
                    super.onNetworkRequestCancelled();
                }

                @Override
                public void onNetworkResponseSucceed(NetResponse result) {
                    super.onNetworkResponseSucceed(result);
                    super.onNetworkResponseSucceed(result);
                    Log.i(TAG, "result.rawResult:" + result.rawResult);
                    String jsonResult = result.rawResult;
                try {
                    JSONObject resultObj = new JSONObject(jsonResult);
                    Boolean    isSuccess = resultObj.getBoolean("set");
                    if (isSuccess) {
                        mFragmentLists.get(0).onResume();
                    } else {
                        //TODO: 提示生成邀请码失败
                        mFragmentLists.get(0).onResume();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void beforeNetworkRequestStart() {
                super.beforeNetworkRequestStart();
            }
        });
    }

//    /**
//     * 弹出新增邀请码提示框
//     */
//    private void showAddInviteCodeDialog() {
//        CustomDialog.Builder builder = new CustomDialog.Builder(this);
//        builder.setMessage(getString(R.string.confirm_to_create_new_invite_code));
//        //设置确定按钮
//        builder.setPositiveButton(
//            getString((R.string.confirm)),
//            new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                    createNewInviteCode();
//                }
//        });
//        //设置取消按钮
//        builder.setNegativeButton(
//            getString((R.string.cancel)),
//            new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//            }
//        });
//        builder.create().show();
//    }
}
