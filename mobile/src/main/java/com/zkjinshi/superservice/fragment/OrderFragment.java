package com.zkjinshi.superservice.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.common.LoginActivity;
import com.zkjinshi.superservice.adapter.OrderAdapter;
import com.zkjinshi.superservice.adapter.OrderMoreAdapter;
import com.zkjinshi.superservice.bean.BaseBean;
import com.zkjinshi.superservice.bean.OrderBean;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestListener;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.sqlite.UserDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.view.CircleStatusView;
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
    private RecyclerView rcyOrderMore;
    private UserVo userVo;
    private CircleStatusView moreStatsuView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private OrderAdapter orderAdapter;
    private OrderMoreAdapter orderMoreAdapter;
    private TextView timeTips;


    public static OrderFragment newInstance() {
        return new OrderFragment();
    }

    private void initView(View view){
        rcyOrder = (RecyclerView) view.findViewById(R.id.rcv_order);
        rcyOrderMore = (RecyclerView)view.findViewById(R.id.rcv_order_done);

        moreStatsuView = (CircleStatusView)view.findViewById(R.id.csv_more);
        timeTips = (TextView)view.findViewById(R.id.tv_time_info);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.srl_order);

        rcyOrder.setHasFixedSize(true);
        rcyOrder.setLayoutManager(new LinearLayoutManager(getActivity()));
        orderAdapter = new OrderAdapter(new ArrayList<OrderBean>());
        rcyOrder.setAdapter(orderAdapter);

        rcyOrderMore.setHasFixedSize(true);
        rcyOrderMore.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        orderMoreAdapter = new OrderMoreAdapter(new ArrayList<OrderBean>());
        rcyOrderMore.setAdapter(orderMoreAdapter);

        moreStatsuView.setStatus(CircleStatusView.CircleStatus.STATUS_MORE);
        moreStatsuView.invalidate();

        swipeRefreshLayout.setRefreshing(true);
        loadOrderList(0);
        initListeners();

    }

    private void initListeners(){

        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadOrderList(0);
            }
        });

        //自动加载更多
        rcyOrder.setOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean isSlidingToLast = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    int lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount = linearLayoutManager.getItemCount();
                    if (lastVisibleItem == (totalItemCount -1) && isSlidingToLast) {
                        //加载更多功能的代码
                        swipeRefreshLayout.setRefreshing(true);
                        loadOrderList(orderAdapter.getLastTimeStamp());
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

    }


    private void loadOrderList(final long lastTimeStamp) {
        String url = ProtocolUtil.getSempOrderUrl();
        Log.i(TAG,url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("salesid",CacheUtil.getInstance().getUserId());
        bizMap.put("token",CacheUtil.getInstance().getToken());
        bizMap.put("shopid",CacheUtil.getInstance().getShopID());
        bizMap.put("status","0,1,2,3,4,5");
        if(lastTimeStamp == 0){
            bizMap.put("page","1");
        }else{
            bizMap.put("pagetime",""+lastTimeStamp);
        }

        bizMap.put("pagedata",""+orderAdapter.getPagedata()*2);
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(getActivity(),netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(getActivity()) {
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

                    if(lastTimeStamp == 0){
                        ArrayList<OrderBean> upList = getUpList(orderList);
                        orderAdapter.refreshingAction(upList);

                        ArrayList<OrderBean> downList = getDownList(orderList);
                        orderMoreAdapter.refreshingAction(downList);
                        timeTips.setText(orderMoreAdapter.getTimeTips());

                        swipeRefreshLayout.setRefreshing(false);

                    }else{
                        ArrayList<OrderBean> upList = getUpList(orderList);
                        orderAdapter.loadMoreAction(upList);

                        ArrayList<OrderBean> downList = getDownList(orderList);
                        orderMoreAdapter.refreshingAction(downList);
                        timeTips.setText(orderMoreAdapter.getTimeTips());

                        swipeRefreshLayout.setRefreshing(false);
                    }
                }catch (Exception e){
                    Log.e(TAG,e.getMessage());
                    BaseBean baseBean = new Gson().fromJson(result.rawResult,BaseBean.class);
                    if(baseBean!= null && baseBean.isSet() && baseBean.getErr().equals("400")){
                        DialogUtil.getInstance().showToast(getActivity(),"Token 失效，请重新登录");
                        CacheUtil.getInstance().setLogin(false);
                        Intent intent = new Intent(getActivity(),LoginActivity.class);
                        getActivity().startActivity(intent);
                        getActivity().finish();

                    }
                }

            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = false;
        netRequestTask.execute();
    }

    private ArrayList<OrderBean> getDownList(ArrayList<OrderBean> orderList) {
        ArrayList<OrderBean> listData = new ArrayList<OrderBean>();
        if(orderList.size() > orderAdapter.getPagedata()){
            for(int i=orderAdapter.getPagedata();i < orderList.size() ;i++){
                listData.add(orderList.get(i));
            }
        }
        return listData;
    }

    private ArrayList<OrderBean> getUpList(ArrayList<OrderBean> orderList) {
        ArrayList<OrderBean> listData = new ArrayList<OrderBean>();
        if(orderList.size() > orderAdapter.getPagedata()){
            for(int i=0;i<orderAdapter.getPagedata();i++){
                listData.add(orderList.get(i));
            }
        }else{
            listData = orderList;
        }

        return listData;
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
