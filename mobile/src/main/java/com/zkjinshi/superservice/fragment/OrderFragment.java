package com.zkjinshi.superservice.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.OrderAdapter;
import com.zkjinshi.superservice.adapter.ZoneAdapter;
import com.zkjinshi.superservice.bean.OrderBean;
import com.zkjinshi.superservice.bean.ZoneBean;
import com.zkjinshi.superservice.listener.RecyclerLoadMoreListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestListener;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.sqlite.UserDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.view.CircleStatusView;
import com.zkjinshi.superservice.view.zoomview.ImageViewTouchBase;
import com.zkjinshi.superservice.vo.UserVo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 订单处理Fragment页面
 * 开发者：杜健德
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderFragment extends Fragment{

    private static final String TAG = OrderFragment.class.getSimpleName();

    private RecyclerView rcyOrder;
    private RecyclerView rcyOrderDone;
    private UserVo userVo;
    private CircleStatusView moreStatsuView;
    private SwipeRefreshLayout swipeRefreshLayout;
    OrderAdapter orderAdapter;

    public static OrderFragment newInstance() {
        return new OrderFragment();
    }

    private void initView(View view){
        rcyOrder = (RecyclerView) view.findViewById(R.id.rcv_order);
        rcyOrderDone = (RecyclerView)view.findViewById(R.id.rcv_order_done);

        moreStatsuView = (CircleStatusView)view.findViewById(R.id.csv_more);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.srl_order);

        rcyOrder.setHasFixedSize(true);
        rcyOrder.setLayoutManager(new LinearLayoutManager(getActivity()));
        orderAdapter = new OrderAdapter(new ArrayList<OrderBean>());
        rcyOrder.setAdapter(orderAdapter);
        initListeners();

        rcyOrderDone.setHasFixedSize(true);
        rcyOrderDone.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        moreStatsuView.setStatus(CircleStatusView.CircleStatus.STATUS_MORE);
        moreStatsuView.invalidate();

        loadOrderList(1);
    }

    private void initListeners(){

        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadOrderList(1);
            }
        });

        //自动加载更多
        orderAdapter.setOnLoadMoreListener(new RecyclerLoadMoreListener() {
            @Override
            public void loadMore() {
                orderAdapter.isLoadingMore = true;
                swipeRefreshLayout.setRefreshing(true);
                loadOrderList(orderAdapter.getCurrentPage()+1);
            }
        });


    }


    private void loadOrderList(final int page) {
        userVo = UserDBUtil.getInstance().queryUserById(CacheUtil.getInstance().getUserId());
        String url = ProtocolUtil.getSempOrderUrl();
        Log.i(TAG,url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("salesid",userVo.getUserId());
        bizMap.put("token",userVo.getToken());
        bizMap.put("shopid",userVo.getShopId());
        bizMap.put("status","0,1,2,3,4,5");
        bizMap.put("page",page+"");
        bizMap.put("pagedata","10");
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(getActivity(),netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new NetRequestListener() {
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
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try{
                    ArrayList<OrderBean> orderList = new Gson().fromJson(result.rawResult, new TypeToken< ArrayList<OrderBean>>(){}.getType());
                    if(page == 1){
                        orderAdapter.refreshingAction(orderList);
                        swipeRefreshLayout.setRefreshing(false);
                    }else{
                        orderAdapter.loadMoreAction(orderList);
                        swipeRefreshLayout.setRefreshing(false);
                        orderAdapter.isLoadingMore = false;
                    }
                }catch (Exception e){
                    Log.e(TAG,e.getMessage());
                }

            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container,
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
