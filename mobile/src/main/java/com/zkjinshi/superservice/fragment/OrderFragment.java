package com.zkjinshi.superservice.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.OrderAdapter;
import com.zkjinshi.superservice.adapter.OrderMoreAdapter;
import com.zkjinshi.superservice.bean.OrderBean;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.view.CircleStatusView;
import com.zkjinshi.superservice.vo.IdentityType;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 订单处理Fragment页面
 * 开发者：杜健德
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderFragment extends BaseFragment{

    private static final String TAG = OrderFragment.class.getSimpleName();

    private RecyclerView rcyOrder;
    private RecyclerView rcyOrderMore;
    private CircleStatusView moreStatsuView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private OrderAdapter orderAdapter;
    private OrderMoreAdapter orderMoreAdapter;
    private TextView timeTips,emptyTips;
    private View emptyLayout,moreLayout;

    private ArrayList<OrderBean> mOrderList;

    private String mShopID;
    private String mUserID;

    private int mPage;
    private int mPageSize = 5;

    @Override
    protected View initView() {

        View view = View.inflate(mActivity, R.layout.fragment_order, null);
        emptyLayout = view.findViewById(R.id.empty_layout);
        moreLayout  = view.findViewById(R.id.more_layout);
        emptyTips   = (TextView) view.findViewById(R.id.empty_tips);
        emptyTips.setText("暂无订单");

        rcyOrder = (RecyclerView) view.findViewById(R.id.rcv_order);
        rcyOrderMore = (RecyclerView)view.findViewById(R.id.rcv_order_done);

        moreStatsuView = (CircleStatusView)view.findViewById(R.id.csv_more);
        timeTips = (TextView)view.findViewById(R.id.tv_time_info);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.srl_order);

        rcyOrder.setNestedScrollingEnabled(false);
        rcyOrder.setHasFixedSize(true);
        rcyOrder.setLayoutManager(new LinearLayoutManager(getActivity()));

        //设置 适配器
        mOrderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(mOrderList);
        rcyOrder.setAdapter(orderAdapter);

        rcyOrderMore.setNestedScrollingEnabled(false);
        rcyOrderMore.setHasFixedSize(true);
        rcyOrderMore.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        orderMoreAdapter = new OrderMoreAdapter(new ArrayList<OrderBean>());
        rcyOrderMore.setAdapter(orderMoreAdapter);

        moreStatsuView.setStatus(CircleStatusView.CircleStatus.STATUS_MORE);
        moreStatsuView.invalidate();
        return view;
    }

    public void onResume(){
        super.onResume();
        swipeRefreshLayout.setRefreshing(true);
        mPage = 1;
        getOrderList(mShopID, mUserID, mPage, mPageSize);
    }

    @Override
    protected void initData() {
        super.initData();

        mShopID = CacheUtil.getInstance().getShopID();
        mUserID = CacheUtil.getInstance().getUserId();

//        loadOrderList(0);
    }

    @Override
    protected void initListener() {
        super.initListener();

        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                loadOrderList(0);
                mPage = 1;
                getOrderList(mShopID, mUserID, mPage, mPageSize);
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
                        getOrderList(mShopID, mUserID, mPage, mPageSize);
//                        loadOrderList(orderAdapter.getLastTimeStamp());
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

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(null != savedInstanceState){
            mPage     = savedInstanceState.getInt("page");
            mOrderList = (ArrayList<OrderBean>) savedInstanceState.getSerializable("order_list");
            if(null != mOrderList){
                orderAdapter.setDataList(mOrderList);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(null != mOrderList && !mOrderList.isEmpty()){
            outState.putInt("page", mPage);
            outState.putSerializable("order_list", mOrderList);
        }
    }

    /**
     * 商家获取订单列表
     * @param shopID
     * @param userID
     * @param page
     * @param pageSize
     */
    private void getOrderList(String shopID, String userID, int page, int pageSize) {


        String url = ProtocolUtil.getOrderListUrl(shopID, userID, page, pageSize);
        if(IdentityType.BUSINESS ==  CacheUtil.getInstance().getLoginIdentity()){
            url = ConfigUtil.getInst().getJavaDomain()+"order/list/shop/"+shopID+"/"+page+"/"+pageSize;
        }
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        NetRequestTask netRequestTask = new NetRequestTask(getActivity(),netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(getActivity()) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                swipeRefreshLayout.setRefreshing(false);
                try{
                    ArrayList<OrderBean> orderList = new Gson().fromJson(result.rawResult,
                            new TypeToken<ArrayList<OrderBean>>(){}.getType());

                    if(null != orderList && !orderList.isEmpty()){
                        if(mPage == 1){
                            mOrderList = orderList;
                        } else {
                            mOrderList.addAll(orderList);
                        }
                        orderAdapter.setDataList(mOrderList);
                        orderAdapter.notifyDataSetChanged();
                        mPage++;

                    }

                    if(null != orderList && !orderList.isEmpty()){
                        emptyLayout.setVisibility(View.GONE);
                        moreLayout.setVisibility(View.GONE);
                    }else{
                        emptyLayout.setVisibility(View.VISIBLE);
                        moreLayout.setVisibility(View.GONE);
                    }

                }catch (Exception e){
                    Log.e(TAG,e.getMessage());
                    emptyLayout.setVisibility(View.VISIBLE);
                    moreLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = false;
        netRequestTask.execute();
    }

    private void loadOrderList(final long lastTimeStamp) {
        String url = ProtocolUtil.getSempOrderUrl();
        Log.i(TAG,url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<>();
        bizMap.put("salesid",CacheUtil.getInstance().getUserId());
        bizMap.put("token",CacheUtil.getInstance().getToken());
        bizMap.put("shopid",CacheUtil.getInstance().getShopID());
        bizMap.put("status","0,1,2,3,4,5");
        if(CacheUtil.getInstance().getLoginIdentity()== IdentityType.WAITER){
            //bizMap.put("status","0,1,2,3,4,5");
        }else{
            // bizMap.put("status","0,1,2,3,4,5");
        }
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
                super.onNetworkResponseSucceed(result);

                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try{
                    ArrayList<OrderBean> orderList = new Gson().fromJson(result.rawResult, new TypeToken< ArrayList<OrderBean>>(){}.getType());
                    if(lastTimeStamp == 0){
                        ArrayList<OrderBean> upList = getUpList(orderList);
                        orderAdapter.refreshingAction(upList);
                        if(orderAdapter.dataList.size() > 0){
                            emptyLayout.setVisibility(View.GONE);
                            moreLayout.setVisibility(View.GONE);
                        }else{
                            emptyLayout.setVisibility(View.VISIBLE);
                            moreLayout.setVisibility(View.GONE);
                        }

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
                    emptyLayout.setVisibility(View.VISIBLE);
                    moreLayout.setVisibility(View.GONE);
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
        ArrayList<OrderBean> listData = new ArrayList<>();
        if(orderList.size() > orderAdapter.getPagedata()){
            for(int i=orderAdapter.getPagedata();i < orderList.size() ;i++){
                listData.add(orderList.get(i));
            }
        }
        return listData;
    }

    private ArrayList<OrderBean> getUpList(ArrayList<OrderBean> orderList) {
        ArrayList<OrderBean> listData = new ArrayList<>();
        if(orderList.size() > orderAdapter.getPagedata()){
            for(int i=0;i<orderAdapter.getPagedata();i++){
                listData.add(orderList.get(i));
            }
        }else{
            listData = orderList;
        }
        return listData;
    }

}
