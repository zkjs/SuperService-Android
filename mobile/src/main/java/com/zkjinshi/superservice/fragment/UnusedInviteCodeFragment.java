package com.zkjinshi.superservice.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.common.InviteCodeController;
import com.zkjinshi.superservice.activity.common.InviteCodeOperater;
import com.zkjinshi.superservice.activity.common.InviteCodesActivity;
import com.zkjinshi.superservice.adapter.InviteCodeAdapter;
import com.zkjinshi.superservice.bean.Head;
import com.zkjinshi.superservice.bean.InviteCode;
import com.zkjinshi.superservice.bean.InviteCodeData;
import com.zkjinshi.superservice.listener.RecyclerItemClickListener;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.ComingVo;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 开发者：WinkyQin
 * 日期：2015/12/10
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class UnusedInviteCodeFragment extends Fragment {

    private Activity           mActivity;
    private RelativeLayout     mEmptyLayout;
    private SwipeRefreshLayout mSrlContainer;
    private RecyclerView       mRvUnusedCodes;
    private InviteCodeAdapter    mInviteCodeAdapter;
    private LinearLayoutManager  mLayoutManager;
    private List<InviteCode>     mInviteCodes;
    private int mPage;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_unused_invite_code, container, false);
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
        mPage = 1;
        getInviteCode(mPage);
    }

    private void initView(View view){
        mEmptyLayout   = (RelativeLayout) view.findViewById(R.id.empty_layout);
        mSrlContainer  = (SwipeRefreshLayout) view.findViewById(R.id.srl_container);
        mRvUnusedCodes = (RecyclerView) view.findViewById(R.id.rcv_unused_invite_code);
    }

    private void initData(){
        mActivity          = this.getActivity();
        mInviteCodes       = new ArrayList<>();
        mInviteCodeAdapter = new InviteCodeAdapter(mActivity, mInviteCodes);
        mRvUnusedCodes.setAdapter(mInviteCodeAdapter);
        mRvUnusedCodes.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mActivity);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvUnusedCodes.setLayoutManager(mLayoutManager);
    }

    private void initListeners(){

        //下拉加载数据
        mSrlContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getInviteCode(mPage);
            }
        });

        //上拉加载数据
        mRvUnusedCodes.setOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        getInviteCode(mPage);
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

        mInviteCodeAdapter.setOnItemClickListener(
            new RecyclerItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    InviteCode inviteCode = mInviteCodes.get(position);
                    String inviteCodeStr  = inviteCode.getSalecode();
                    InviteCodeOperater.getInstance().showOperationDialog(mActivity, inviteCodeStr);
                }
            }
        );
    }

    /**
     * 获取我的邀请码列表
     * @param page
     */
    private void getInviteCode(int page) {
        InviteCodeController.getInstance().getInviteCodes(
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
                    LogUtil.getInstance().info(LogLevel.INFO, result.rawResult);

                    Gson gson = new Gson();
                    InviteCodeData inviteCodeData = gson.fromJson(result.rawResult, InviteCodeData.class);
                    if(null != inviteCodeData){
                        Head head = inviteCodeData.getHead();
                        if(head.isSet()){
                            mEmptyLayout.setVisibility(View.GONE);
                            int count = head.getCount();
                            ((InviteCodesActivity)mActivity).udpateUnusedCodeCount(count);

                            mPage++;
                            List<InviteCode> inviteCodes = inviteCodeData.getCode_data();
                            if(null!=inviteCodes && !inviteCodes.isEmpty()){
                                mInviteCodes.addAll(inviteCodes);
                                mInviteCodeAdapter.notifyDataSetChanged();
                            } else {
//                                DialogUtil.getInstance().showCustomToast(mActivity, mActivity.getString(
//                                                                         R.string.no_more_data),
//                                                                         Gravity.CENTER);
                            }
                        }
                    }

                    if(null==mInviteCodes || mInviteCodes.isEmpty()){
                        mEmptyLayout.setVisibility(View.VISIBLE);
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