package com.zkjinshi.superservice.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.common.InviteCodeController;
import com.zkjinshi.superservice.activity.common.InviteCodesActivity;
import com.zkjinshi.superservice.adapter.InviteCodeUserAdapter;
import com.zkjinshi.superservice.bean.InviteCode;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.response.InviteCodeResponse;
import com.zkjinshi.superservice.vo.InviteCodeListVo;

import java.util.ArrayList;
import java.util.List;

/**
 * 已经使用邀请码页面
 * 开发者：WinkyQin
 * 日期：2015/12/10
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class UsedInviteCodeFragment extends Fragment {

    private Activity        mActivity;
    private RelativeLayout  mEmptyLayout;
    private SwipeRefreshLayout    mSrlContainer;
    private RecyclerView          mRvUnusedCodes;
    private InviteCodeUserAdapter mInviteCodeAdapter;
    private LinearLayoutManager   mLayoutManager;
    private List<InviteCode>  mInviteCodeUsers;
    private int mPage;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_used_invite_code, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initData();
        initListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(null != mInviteCodeAdapter){
            mInviteCodeAdapter.clear();
        }
        mPage = 0;
        getHistoryInviteCodeUsers(mPage,true);
    }

    private void initView(View view){
        mEmptyLayout   = (RelativeLayout) view.findViewById(R.id.empty_layout);
        mSrlContainer  = (SwipeRefreshLayout) view.findViewById(R.id.srl_container);
        mRvUnusedCodes = (RecyclerView) view.findViewById(R.id.rcv_used_invite_code);
    }

    private void initData(){
        mActivity = this.getActivity();
        mInviteCodeUsers   = new ArrayList<InviteCode>();
        mInviteCodeAdapter = new InviteCodeUserAdapter(mActivity, mInviteCodeUsers);
        mRvUnusedCodes.setAdapter(mInviteCodeAdapter);
        mRvUnusedCodes.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mActivity);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvUnusedCodes.setLayoutManager(mLayoutManager);
    }

    private void initListeners(){

        //刷新数据
        mSrlContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 0;
                getHistoryInviteCodeUsers(mPage,true);
            }
        });

        //滑动监听
        mRvUnusedCodes.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean isSlidingToLast = false;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount  = linearLayoutManager.getItemCount();
                    if (lastVisibleItem == (totalItemCount - 1) && isSlidingToLast) {
                        //加载更多功能的代码
                        getHistoryInviteCodeUsers(mPage,false);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //dx用来判断横向滑动方向，dy用来判断纵向滑动方向
                if (dy > 0) {
                    //大于0表示，正在向下滚动
                    isSlidingToLast = true;
                } else {
                    //小于等于0 表示停止或向下滚动
                    isSlidingToLast = false;
                }
            }
        });
    }

    /**
     * 获取邀请使用记录
     * @param page
     */
    private void getHistoryInviteCodeUsers(int page,final boolean isRefresh) {
        InviteCodeController.getInstance().getHistoryInviteCodes(
            page,
            mActivity,
            new ExtNetRequestListener(mActivity) {
                @Override
                public void onNetworkRequestError(int errorCode, String errorMessage) {
                    super.onNetworkRequestError(errorCode, errorMessage);
                    mSrlContainer.setRefreshing(false);
                }

                @Override
                public void onNetworkRequestCancelled() {
                    super.onNetworkRequestCancelled();
                    mSrlContainer.setRefreshing(false);
                }

                @Override
                public void onNetworkResponseSucceed(NetResponse result) {
                    super.onNetworkResponseSucceed(result);
                    Gson gson = new Gson();
                    InviteCodeResponse historyInviteCodeResponse = gson.fromJson(result.rawResult, InviteCodeResponse.class);
                    if(null != historyInviteCodeResponse){
                        int resultCode = historyInviteCodeResponse.getRes();
                        if(0 == resultCode){
                            InviteCodeListVo inviteCodeListVo = historyInviteCodeResponse.getData();
                            if(null != inviteCodeListVo){
                                int count = inviteCodeListVo.getTotal();
                                ((InviteCodesActivity)mActivity).updateUsedCodeCount(count);
                                ArrayList<InviteCode> inviteCodeUsers = inviteCodeListVo.getSalecodes();
                                if (null != inviteCodeUsers && !inviteCodeUsers.isEmpty()) {
                                    mPage++;
                                    if(isRefresh){
                                        mInviteCodeUsers =inviteCodeUsers;
                                    }else {
                                        mInviteCodeUsers.addAll(inviteCodeUsers);
                                    }
                                    mInviteCodeAdapter.setData(mInviteCodeUsers);
                                }
                            }

                        }else {
                            String resultMsg = historyInviteCodeResponse.getResDesc();
                            if(!TextUtils.isEmpty(resultMsg)){
                                DialogUtil.getInstance().showCustomToast(getActivity(),resultMsg, Gravity.CENTER);
                            }
                        }
                    }

                    //控制空白页面提示
                    if(null != mEmptyLayout){
                        if(null == mInviteCodeUsers || mInviteCodeUsers.isEmpty()){
                            mEmptyLayout.setVisibility(View.VISIBLE);
                        } else {
                            mEmptyLayout.setVisibility(View.GONE);
                        }
                    }

                    mSrlContainer.setRefreshing(false);
                }

                @Override
                public void beforeNetworkRequestStart() {
                    super.beforeNetworkRequestStart();
                }
            });
    }
}
