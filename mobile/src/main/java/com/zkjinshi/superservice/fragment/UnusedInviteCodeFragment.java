package com.zkjinshi.superservice.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.InviteCodeAdapter;

import java.util.ArrayList;
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
    private InviteCodeAdapter  mInviteCodeAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<String>        mInviteCodes;

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
    }

    private void initView(View view){
        mEmptyLayout   = (RelativeLayout) view.findViewById(R.id.empty_layout);
        mSrlContainer  = (SwipeRefreshLayout) view.findViewById(R.id.srl_container);
        mRvUnusedCodes = (RecyclerView) view.findViewById(R.id.rcv_unused_invite_code);
    }

    private void initData(){
        mActivity = this.getActivity();
        mInviteCodes = new ArrayList<>();
        mInviteCodes.add("invite_code");
        mInviteCodeAdapter = new InviteCodeAdapter(mActivity, mInviteCodes);
        mRvUnusedCodes.setAdapter(mInviteCodeAdapter);
        mRvUnusedCodes.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mActivity);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvUnusedCodes.setLayoutManager(mLayoutManager);
    }

    private void initListeners(){

    }

}