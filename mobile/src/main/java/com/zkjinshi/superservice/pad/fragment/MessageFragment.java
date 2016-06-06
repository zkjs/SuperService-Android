package com.zkjinshi.superservice.pad.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.zkjinshi.superservice.pad.activity.chat.group.ChatGroupActivity;
import com.zkjinshi.superservice.pad.adapter.MessageAdapter;
import com.zkjinshi.superservice.pad.emchat.EMConversationHelper;
import com.zkjinshi.superservice.pad.listener.RecyclerItemClickListener;
import com.zkjinshi.superservice.pad.net.ExtNetRequestListener;
import com.zkjinshi.superservice.pad.net.NetResponse;
import com.zkjinshi.superservice.pad.response.MembersResponse;
import com.zkjinshi.superservice.pad.utils.CacheUtil;
import com.zkjinshi.superservice.pad.utils.Constants;
import com.zkjinshi.superservice.pad.vo.MemberVo;
import com.zkjinshi.superservice.pad.R;
import com.zkjinshi.superservice.pad.activity.chat.single.ChatActivity;
import com.zkjinshi.superservice.pad.activity.chat.single.controller.ChatMemberController;
import com.zkjinshi.superservice.pad.activity.common.MainActivity;
import com.zkjinshi.superservice.pad.emchat.observer.EMessageSubject;
import com.zkjinshi.superservice.pad.emchat.observer.IEMessageObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息通知Fragment页面
 * 开发者：JimmyZhang
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MessageFragment extends Fragment implements IEMessageObserver {

    private RecyclerView messageRCV;
    private MessageAdapter messageAdapter;
    private ArrayList<EMConversation> conversationList;
    private LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View emptyLayout;
    private TextView emptyTips;
    private ArrayList<MemberVo> memberList;

    public static MessageFragment newInstance() {
        return new MessageFragment();
    }

    private void initView(View view){
        emptyLayout = view.findViewById(R.id.empty_layout);
        emptyTips = (TextView) view.findViewById(R.id.empty_tips);
        emptyTips.setText("暂无消息通知");
        messageRCV = (RecyclerView)view.findViewById(R.id.rcv_message);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.srl_message);
    }

    private void initData(){
        messageAdapter = new MessageAdapter(getActivity(),conversationList);
        messageRCV.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        messageRCV.setNestedScrollingEnabled(false);
        messageRCV.setLayoutManager(linearLayoutManager);
        messageRCV.setAdapter(messageAdapter);
    }

    private void initListeners() {

        messageAdapter.setOnItemClickListener(new RecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                EMConversation conversation = conversationList.get(position);
                EMMessage message = conversation.getLastMessage();
                String username = conversation.getUserName();
                Intent intent = new Intent();
                if (conversation.isGroup()) {
                    intent.setClass(getActivity(), ChatGroupActivity.class);
                    intent.putExtra("groupId",username);
                }else{
                    intent.setClass(getActivity(), ChatActivity.class);
                    intent.putExtra(Constants.EXTRA_USER_ID, username);
                    if (null != message) {
                        try {
                            intent.putExtra(Constants.EXTRA_FROM_NAME, CacheUtil.getInstance().getUserName());
                            String shopId = CacheUtil.getInstance().getShopID();
                            String shopName = CacheUtil.getInstance().getShopFullName();
                            String fromName = message.getStringAttribute("fromName");
                            String toName = message.getStringAttribute("toName");
                            if (!TextUtils.isEmpty(shopId)) {
                                intent.putExtra(Constants.EXTRA_SHOP_ID,shopId);
                            }
                            if (!TextUtils.isEmpty(shopName)) {
                                intent.putExtra(Constants.EXTRA_SHOP_NAME,shopName);
                            }
                            if(!toName.equals(CacheUtil.getInstance().getUserName())){
                                intent.putExtra(Constants.EXTRA_TO_NAME, toName);
                            }else{
                                intent.putExtra(Constants.EXTRA_TO_NAME, fromName);
                            }
                        } catch (EaseMobException e) {
                            e.printStackTrace();
                        }
                    }
                }
                startActivity(intent);
            }
        });

        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });
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
        addAllObserver();
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
        EMConversationHelper.getInstance().requestGroupListTask();
        conversationList = (ArrayList<EMConversation>) EMConversationHelper.getInstance().loadConversationList();
        messageAdapter.setConversationList(conversationList);
        if(conversationList.size() > 0){
            requestUserListTask(conversationList);
            emptyLayout.setVisibility(View.GONE);
        }else{
            emptyLayout.setVisibility(View.VISIBLE);
        }
        ((MainActivity)getActivity()).setMessageNum(1,unreadTotalCount(conversationList));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeAllObserver();
    }

    /**
     * 获取未读消息总个数
     * @param conversationList
     * @return
     */
    private int unreadTotalCount(List<EMConversation> conversationList){
        int totalCount = 0;
        if(null != conversationList && !conversationList.isEmpty()){
            for(EMConversation conversation : conversationList){
                totalCount = totalCount+ conversation.getUnreadMsgCount();
            }
        }
        return  totalCount;
    }

    /**
     * 刷新页面
     */
    public void refresh() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                conversationList.clear();
                conversationList.addAll(EMConversationHelper.getInstance().loadConversationList());
                if(conversationList.size() > 0){
                    emptyLayout.setVisibility(View.GONE);
                }else{
                    emptyLayout.setVisibility(View.VISIBLE);
                }
                messageAdapter.setConversationList(conversationList);
                ((MainActivity)getActivity()).setMessageNum(1,unreadTotalCount(conversationList));
            }
        });
    }

    /**
     * 添加观察者
     */
    private void addAllObserver(){
        EMessageSubject.getInstance().addObserver(this,EMNotifierEvent.Event.EventNewMessage);
        EMessageSubject.getInstance().addObserver(this,EMNotifierEvent.Event.EventOfflineMessage);
        EMessageSubject.getInstance().addObserver(this,EMNotifierEvent.Event.EventConversationListChanged);
    }

    /**
     * 移除观察者
     */
    private void removeAllObserver(){
        EMessageSubject.getInstance().removeObserver(this, EMNotifierEvent.Event.EventNewMessage);
        EMessageSubject.getInstance().removeObserver(this, EMNotifierEvent.Event.EventOfflineMessage);
        EMessageSubject.getInstance().removeObserver(this, EMNotifierEvent.Event.EventConversationListChanged);
    }

    @Override
    public void receive(EMNotifierEvent event) {
        switch (event.getEvent()) {
            case EventNewMessage:
            case EventOfflineMessage:
            case EventConversationListChanged:
                refresh();
                break;
            default:
                break;
        }
    }

    private void requestUserListTask(ArrayList<EMConversation> conversationList){

        ChatMemberController.getInstance().requestChatMembersTask(getMemberIds(conversationList), new ExtNetRequestListener(getActivity()) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                super.onNetworkRequestError(errorCode, errorMessage);
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
            };

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                if(null != result && !TextUtils.isEmpty(result.rawResult)){
                    try {
                        Log.i(TAG, "result:" + result.rawResult);
                        MembersResponse membersResponse = new Gson().fromJson(result.rawResult,MembersResponse.class);
                        if(null != membersResponse) {
                            int resultCode = membersResponse.getRes();
                            if (0 == resultCode) {
                                memberList = membersResponse.getData();
                                messageAdapter.setMemberList(memberList);
                            }else {
                                String resultMsg = membersResponse.getResDesc();
                                if(!TextUtils.isEmpty(resultMsg)){
                                   Log.i(com.zkjinshi.base.util.Constants.ZKJINSHI_BASE_TAG,"errorMsg:"+resultMsg);
                                }
                            }
                        }

                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
        },getActivity());
    }

    public String getMemberIds(ArrayList<EMConversation> conversationList){
        String memberStr = null;
        StringBuffer memberSb = new StringBuffer();
        if(null != conversationList && !conversationList.isEmpty()){
            for(int i=0;i<conversationList.size();i++){
                EMConversation conversation = conversationList.get(i);
                memberSb.append(conversation.getUserName()).append(",");
            }
            memberStr = memberSb.substring(0,memberSb.length()-1);
        }
        return  memberStr;
    }
}
