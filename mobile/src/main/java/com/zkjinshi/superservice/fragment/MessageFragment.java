package com.zkjinshi.superservice.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.MessageAdapter;
import com.zkjinshi.superservice.test.MessageBiz;
import com.zkjinshi.superservice.vo.MessageVo;

import java.util.ArrayList;

/**
 * 消息通知Fragment页面
 * 开发者：JimmyZhang
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MessageFragment extends Fragment{

    private RecyclerView messageRCV;
    private MessageAdapter messageAdapter;
    private ArrayList<MessageVo> messageList;
    private LinearLayoutManager linearLayoutManager;

    public static MessageFragment newInstance() {
        return new MessageFragment();
    }

    private void initView(View view){
        messageRCV = (RecyclerView)view.findViewById(R.id.rcv_message);
    }

    private void initData(){
        messageList = MessageBiz.getMessageList();
        messageAdapter = new MessageAdapter(getActivity(),messageList);
        messageRCV.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        messageRCV.setLayoutManager(linearLayoutManager);
        messageRCV.setAdapter(messageAdapter);
    }

    private void initListeners(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container,
                false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
