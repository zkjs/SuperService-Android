package com.zkjinshi.superservice.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.CallServiceAdapter;
import com.zkjinshi.superservice.listener.RecyclerItemClickListener;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.response.NoticeResponse;
import com.zkjinshi.superservice.response.ServiceTaskListResponse;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.NoticeVo;
import com.zkjinshi.superservice.vo.ServiceTaskVo;

import java.util.ArrayList;

/**
 * 呼叫服务Fragment页面
 * 开发者：JimmyZhang
 * 日期：2016/6/21
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class CallServiceFragment extends Fragment implements CallServiceAdapter.ServiceOptionListener{

    public static int PAGE_NO = 0;
    public static final int PAGE_SIZE = 10;

    private RelativeLayout emptyLayout;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private CallServiceAdapter callServiceAdapter;
    private TextView emptyTips;
    private ArrayList<ServiceTaskVo> serviceList = new ArrayList<ServiceTaskVo>();
    private ArrayList<ServiceTaskVo> requestServiceList;
    private boolean isLoadMoreAble = true;

    private void initView(View view){
        emptyLayout = (RelativeLayout)view.findViewById(R.id.call_empty_layout);
        refreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.call_swipe_refresh_layout);
        recyclerView = (RecyclerView)view.findViewById(R.id.call_recycler_view);
        emptyTips = (TextView) view.findViewById(R.id.empty_tips);
    }

    private void initData(){
        emptyTips.setText("暂无呼叫通知");
        callServiceAdapter = new CallServiceAdapter(getActivity(),serviceList);
        recyclerView.setAdapter(callServiceAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void initListeners(){

        //下拉刷新
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                PAGE_NO = 0;
                requestCallServiceTask(true);
            }
        });

        //自动加载更多
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            boolean isSlidingToLast = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    int lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount = linearLayoutManager.getItemCount();
                    if (lastVisibleItem == (totalItemCount -1) && isSlidingToLast) {
                        if(isLoadMoreAble){
                            isLoadMoreAble = false;
                            refreshLayout.setRefreshing(true);
                            requestCallServiceTask(false);
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //dx用来判断横向滑动方向，dy用来判断纵向滑动方向
                if(dy > 0){
                    //大于0表示，正在向右滚动
                    isSlidingToLast = true;
                }else{
                    //小于等于0 表示停止或向左滚动
                    isSlidingToLast = false;
                }
            }
        });

        //单选列表
        callServiceAdapter.setOnItemClickListener(new RecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });
    }

    public static CallServiceFragment newInstance() {
        return new CallServiceFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_call_service, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
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

    @Override
    public void executeCancel(ServiceTaskVo taskVo) {//取消呼叫服务

    }

    @Override
    public void executeFinish(ServiceTaskVo taskVo) {//完成呼叫服务

    }

    @Override
    public void executeAppoint(ServiceTaskVo taskVo) {//指派呼叫服务

    }

    @Override
    public void executeReady(ServiceTaskVo taskVo) {//就绪呼叫服务

    }

    /**
     * 获取呼叫服务列表
     * @param isRefresh
     */
    private void requestCallServiceTask(final boolean isRefresh){
        String noticesUrl = ProtocolUtil.getServiceListUrl(""+PAGE_NO,""+PAGE_SIZE);
        NetRequest netRequest = new NetRequest(noticesUrl);
        NetRequestTask netRequestTask = new NetRequestTask(getActivity(), netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(getActivity()) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
                if (null != refreshLayout) {
                    refreshLayout.setRefreshing(false);
                }
                isLoadMoreAble = true;
            }

            @Override
            public void onNetworkRequestCancelled() {
                isLoadMoreAble = true;
            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                try {
                    Log.i(TAG, "result.rawResult:" + result.rawResult);
                    isLoadMoreAble = true;
                    if (null != refreshLayout) {
                        refreshLayout.setRefreshing(false);
                    }
                    ServiceTaskListResponse serviceTaskListResponse = new Gson().fromJson(result.rawResult,ServiceTaskListResponse.class);
                    if(null != serviceTaskListResponse){
                        int resultCode = serviceTaskListResponse.getRes();
                        if(0 == resultCode){
                            requestServiceList = serviceTaskListResponse.getData();
                            if(isRefresh){
                                serviceList = requestServiceList;
                            }else {
                                serviceList.addAll(requestServiceList);
                            }
                            if(null != requestServiceList && !requestServiceList.isEmpty()){
                                PAGE_NO++;
                            }else {
                                DialogUtil.getInstance().showCustomToast(getActivity(),"再无更多数据", Gravity.CENTER);
                            }
                            callServiceAdapter.setServiceList(serviceList);
                        }else {
                            String resultMsg = serviceTaskListResponse.getResDesc();
                            if(!TextUtils.isEmpty(resultMsg)){
                                DialogUtil.getInstance().showCustomToast(getActivity(),resultMsg,Gravity.CENTER);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = false;
        netRequestTask.execute();
    }
}
